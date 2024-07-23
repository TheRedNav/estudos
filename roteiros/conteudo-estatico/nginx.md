> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/conteudo-estatico/nginx.md&amp;action_name=conteudo-estatico/nginx.md)
# Utilizando o Nginx como servidor web

## Contexto e motivação
Para prover conteúdo estático na Cloud BB, o Nginx é a melhor alternativa. Conteúdo estático pode ser um site ou o resultado do *build* de uma aplicação de frontend que usa frameworks como Angular ou React.

## Passo a passo
### Construção
Essa fase só se aplica a aplicações de frontend que têm uma fase de build. Nesse caso, a esteira Jenkins usada deve ser compatível com a tecnologia do projeto, e gerar os artefatos estáticos a serem servidos. 

Por exemplo, para uma aplicação Create React App(CRA), deve-se usar a [esteira JavaScript](https://fontes.intranet.bb.com.br/aic/publico/atendimento/-/wikis/Pipelines/Javascript). A esteira instalará as dependências e, após o `npm build`, vai contruir os estáticos (no CRA, por padrão na pasta /build)

#### Jenkinsfile

Exemplo de Jenkinsfile para JavaScript:

```
@Library(['aic-jenkins-sharedlib']) _

jsBuildPipeline {
    nomePod                         = 'node16' // habilita a troca da versão do compilador (Ex: jdk8,jdk11,node8,node10,node12)
    habilitarValidacaoEstatica      = true // habilita a validação estática do código fonte
    habilitarConstrucao             = true // habilita a construção da aplicação
    habilitarTestesUnidade          = true // habilita a execução dos testes de unidade
    habilitarTestesIntegracao       = false // habilita a execução dos testes de integração
    habilitarSonar                  = true // habilita a execução do SonarQube Scanner
    habilitarEmpacotamento          = true // habilita o empacotamento da aplicação
    habilitarEmpacotamentoDocker    = true // habilita o build e publicação da imagem docker
    habilitarPublicacao             = false // habilita a publicação do pacote no repositório corporativo
    habilitarDebug                  = false // habilita o debug
}
```

Note que dessa forma, mesmo servindo conteúdo estático, o projeto pode lançar mão dos benefícios da esteira, como medição de cobertura de código.

Se o projeto é apenas um site estático e não envolve construção, a pipeline utilizada deve ser a de DockerImage. Segue exemplo de Jenkinsfile:

```
@Library(['aic-jenkins-sharedlib']) _

dockerBuildPipeline {
    habilitarValidacaoEstatica  = true // habilita a validação estática do código fonte
    habilitarValidacaoSeguranca = false // habilita a validação de segurança do código fonte
    habilitarConstrucao         = true // habilita a construção da aplicação
    habilitarTestesUnidade      = false // habilita a execução dos testes de unidade
    habilitarTestesIntegracao   = false // habilita a execução dos testes de integração
    habilitarEmpacotamento      = true // habilita o empacotamento da aplicação
    habilitarPublicacao         = true // habilita a publicação do pacote no repositório corporativo
    habilitarDebug              = false // habilita debug
}
```


### Servindo os arquivos
Essa fase se aplica a todos os projetos.

Para servir os arquivos, usaremos uma imagem de nginx que tenha suporte da Red Hat. No Dockerfile, é necessário copiar os arquivos estáticos do site ou resultantes do build da aplicação para a pasta do nginx. No exemplo do Create React App, os arquivos da pasta /build. Além disso, para que funcione no OpenShift, também é necessário que qualquer usuário seja capaz de executar a imagem, por causa das [políticas de segurança](https://developers.redhat.com/blog/2020/10/26/adapting-docker-and-kubernetes-containers-to-run-on-red-hat-openshift-container-platform). Dessa forma, é necessário dar permissão nos arquivos protegidos e usar uma porta com número acima de 1024, o que nossa imagem base já faz, utilizando a 8080.

Segue o **Dockerfile** para um aplicativo Create React App:

```Dockerfile
FROM docker.binarios.intranet.bb.com.br/ubi9/nginx-122:1-12

USER 0

COPY docker-entrypoint.sh "${HOME}"/nginx-start/

RUN chmod 777 ./nginx-start/docker-entrypoint.sh && mkdir "${NGINX_CONFIGURATION_PATH}"/templates

COPY nginx/default.conf.template "${NGINX_CONFIGURATION_PATH}"/templates

RUN chmod 777 -R /etc/nginx/conf.d/

#Apenas para projetos AngularIO
#COPY dist/nome-do-projeto/ "${HOME}"/

#Apenas para projetos React
COPY build/ "${HOME}"/
COPY public "${HOME}"/public
RUN chmod 777 -R "${HOME}"/public

#Comando para executar o script
ENTRYPOINT ["/opt/app-root/src/nginx-start/docker-entrypoint.sh"]

USER 1001

CMD ["/bin/bash", "-c", "nginx -g 'daemon off;'"]
```
Esse exemplo deve ser ajustado para outras tecnologias, que podem ter outras pastas como destino dos arquivos após a construção. Por exemplo, no caso do Angular o diretório dos arquivos pós build é o `dist/nome-do-projeto`.

É uma boa prática verificar se há versões novas da imagem em https://catalog.redhat.com/software/containers/ubi9/nginx-122/63f7653b9b0ca19f84f7e9a1

Esse Dockerfile copia as pastas `build` e `public` (ou `dist/nome-do-projeto`, caso Angular) para a pasta de conteúdo do nginx e copia o template de configuração para uma pasta de templates. Esse template tem as variáveis de ambiente substituídas no script `docker-entrypoint.sh`, fazendo o arquivo de configuração tomar a versão final. Além disso, para que esse arquivo seja modificado, é necessário dar permissão a todos os usuários na pasta de configuração do nginx.

Conteúdo de exemplo do `docker-entrypoint.sh`, que no Dockefile de exemplo está **na raiz do projeto**:

```sh
#!/usr/bin/env sh
set -eu

envsubst '${API_HOST} ${OUTRA_ENV}' < "${NGINX_CONFIGURATION_PATH}"/templates/default.conf.template >  "${NGINX_DEFAULT_CONF_PATH}"/default.conf

exec "$@"

```

Nesse exemplo, a variável `API_HOST` usada no arquivo de configuração do nginx vai ser substituída na subida da imagem pelo valor definido nas environments do conteiner, de acordo com a configuração `values.yml`.

Se for necessário utilizar outras variáveis, basta informar à frente da variável anterior, conforme demonstrado com a variável `${OUTRA_ENV}`.

### Variáveis de ambiente / proxy reverso

A substituição das variáveis de ambiente mencionada na seção anterior é feita para que seja possível ter comportamentos diferentes a partir dos mesmos arquivos estáticos, e para que não seja necessário fazer um `build` para cada ambiente, respeitando os princípios do ciclo build-release-run.

Com essa prática é possível implantar o mesmo código em desenvolvimento, homologação e produção, e fazer com que o mesmo site estático faça requests para os endereços correspondentes da API em cada ambiente. Para o exemplo dado da variável `API_HOST`, o arquivo de configuração do nginx (que deverá estar localizado no diretorio do projeto com nome `default.conf.template` dentro de diretorio chamado `nginx`) ficaria assim:

```
    #access_log /dev/stdout main;
    
    location /health/live {
      stub_status on;
    }

    location /health/ready {
      stub_status on;
    }

    location /api {
        rewrite /api/(.*)$ /$1 break;
        proxy_ssl_verify off;
        proxy_pass ${API_HOST}; 
    }

    #error_page  404              /404.html;

    # redirect server error pages to the static page /50x.html
    #
    error_page   500 502 503 504  /50x.html;
    location = /50x.html {
        root   /usr/share/nginx/html;
    }
```

Nesse exemplo, os requests para `/api` são redirecionados para o endereço especificado em `API_HOST`. Ou seja, o código servido no frontend faz requests sempre para `/api`, independente do ambiente, mas o proxy reverso do nginx, que roda do lado servidor no mesmo endereço do frontend, conhece o endereço para o qual deve redirecionar essas requisições em cada um dos ambientes. Isso também elimina a necessidade de se configurar CORS na API.

#### Observações Importantes

1. Este arquivo não deve conter as definições de servidor (`server`, `listen`, `server_name`), nem o mapeamento de `'location /'`, uma vez que o mesmo já está configurado em arquivo `/etc/nginx/nginx.conf` contido na imagem docker que será utilizada.

2. Os mapeamentos `'location /health/live'` e `'location /health/ready'` foram informados para utilização de configuração do `livenessProbe` e `readinessProbe` no `values.yaml`. Se desejar, pode remover-los e utiliza o valor `'/'` no atributo `path` de cada configuração.

3. Por padrão esta imagem do docker está com o nginx configurado para apresentar os logs (access.log, error.log) dentro dos diretórios `/var/log/nginx`. Se desejar que este seja apresentado no terminal, é necessário remover o `#` do trecho `#access_log /dev/stdout main;`.

### Configuração do values

Como o nginx dessa imagem serve na porta 8080, deve ser criado um service nesse formato:

```
  service:
    enable: true
    type: "ClusterIP"
    ports:
      - name: "http"
        port: 80
        targetPort: 8080
```

No liveness e readiness, pode ser usado o caminho raiz:

```
      livenessProbe:
        httpGet:
          path: "/"
          #path: "/health/live" #pode ser utilizado, caso configurado no arquivo 'default.conf.template'
          port: 8080
        initialDelaySeconds: 90
        periodSeconds: 10
        timeoutSeconds: 60
        failureThreshold: 5
        successThreshold: 1
      readinessProbe:
        httpGet:
          path: "/"
          #path: "/health/ready" #pode ser utilizado, caso configurado no arquivo 'default.conf.template'
          port: 8080
        initialDelaySeconds: 90
        periodSeconds: 10
        timeoutSeconds: 60
        failureThreshold: 5
        successThreshold: 1
```

E a seção de paths do Ingress fica assim:

```
    paths: []
```
Esse valores foram ajustados para atender a todas as versões do bb-aplic. Mas declarar um nome no service e declarar o path `/` na seção `paths` do Ingress também funciona.

# Fim

Esse roteiro é experimental e traz a configuração de uma tecnologia ainda não totalmente suportada pelo time dev, responsável pelo atendimento.

Ajude a melhorar nossos roteiros propondo mudanças em merge requests e solicitando a avaliação através de issue.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/conteudo-estatico/nginx.md&internalidade=conteudo-estatico/nginx)