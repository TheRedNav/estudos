> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Imagens base Java
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/arquivados/imagens-java.md&amp;action_name=java/arquivados/imagens-java)

Para a execução e compilação de aplicações baseadas em JVM, o time Duke Nuvem oferece e recomenda imagens base baseadas nas imagens oficiais, homologadas e com suporte da Red Hat. Elas são baseadas na [Red Hat UBI](https://www.redhat.com/en/blog/introducing-red-hat-universal-base-image).

## Imagens oferecidas

* Java 11 (Runtime): (bb/dev/dev-java:11.0.17)[https://fontes.intranet.bb.com.br/dev/imagens-docker/java/dev-java-11]
* Java 11 (Com JDK e Maven): (bb/dev/dev-java-jdk:11.0.17)[https://fontes.intranet.bb.com.br/dev/imagens-docker/java/dev-java-jdk-11]
* Java 17 (Runtime): (bb/dev/dev-java:17.0.5)[https://fontes.intranet.bb.com.br/dev/imagens-docker/java/dev-java-17]
* Java 17 (Com JDK e Maven): (bb/dev/dev-java-jdk:17.0.5)[https://fontes.intranet.bb.com.br/dev/imagens-docker/java/dev-java-jdk-17]

Consultar as últimas versões sempre que necessário, para isso acesso um dos links citados acima e acesse o menu na opção `Repository -> Tags`.

Repositório das imagens: https://fontes.intranet.bb.com.br/dev/imagens-docker/java

Exemplo:
`FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.0.5`

O uso de cada uma das imagens deve ser baseado nos requisitos de cada aplicação. Um microsserviço comum provavelmente não precisará da versão com JDK e Maven. A imagem que contém apenas JRE ocupa menos espaço em disco e é mais apropriada.

É importante destacar que aplicações compiladas em versões inferiores do Java podem ser executadas numa versão superior sem impedimentos.

IMPORTANTE: Sempre verifique qual é a versão mais atualizada da imagem.

## Funcionalidades

### Certificados

Todas as imagens possuem os certificados do Banco do Brasil instalados e configurados na JVM. Isso evita problemas e adiciona segurança na hora de fazer requisições https para outras aplicações.

Caso precise adicionar um certificado novo, tanto no cacerts quanto no Sistema Operacional da imagem utilize o script de atualização de certificados da imagem base.
Inclua de preferencia no comeco da configuração da imagem os seguintes passos, primeiro mudando para o usuario root da imagem, depois realizando a copia do certificado, seguido da execução do script e por fim voltando ao usuario default (185).

No exemplo abaixo temos como seriam esses passos. O comando COPY esta copiando todo o conteudo da pasta ´$PATH_LOCAL/CERTIFICADO´, lembrando que o PATH_LOCAL deve ser ajustado de acordo com o caminho de pastas do seu projeto, e executando o script update-ca-trust.

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.0.5

#Inicio Comandos para copia e instalação do certificado
USER root
COPY $PATH_LOCAL/CERTIFICADO /etc/pki/ca-trust/source/anchors/
RUN update-ca-trust
USER default
# Fim Comandos para copia e instalação do certificado

COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/

```

### Gerenciador de pacotes

O [microdnf](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux_atomic_host/7/html/getting_started_with_containers/using_red_hat_universal_base_images_standard_minimal_and_runtimes#adding_software_inside_the_minimal_ubi_container) está configurado no repositório de pacotes Linux do artifactory e pode ser usado para instalar software adicional.

Para realizar a instalação, é preciso mudar para o usuário `root` e depois voltar ao usuário `default`. É possível instalar multiplos pacotes simultaneamente. No exemplo a seguir são instalados dois pacotes.

Também é recomendado utilizar o comando `clean` do microdnf após a instalação, para limpar os arquivos baixados que não são mais necessários. Note que é necessário realizar o clean na mesma instrução `RUN` em que é feita a instalação, caso contrário a limpeza não ocorrerá corretamente.

```dockerfile
USER root
RUN microdnf install -y wget zip && microdnf clean all
USER default
```
IMPORTANTE: A flag `-y` indica confirmação automática e é obrigatória. Sua omissão causa erros pela chamada indevida à entrada do teclado durante o build.

### Configurações regionais

O processo Java está configurado nas imagens com idioma e localidade "pt-BR".

### Usuário

O usuário a ser usado para execução de aplicações é o `default`(185). O diretório de trabalho inicial da imagens é a home desse usuário.

### Pasta de aplicações

Todas as imagens contêm uma pasta preparada para receber aplicações: `/deployments`.

### Portas

As portas expostas são:

* 8080
* 8443
* 8778

## Configurações

Para alterar propriedades de execução do processo Java, deve ser modificada a variável de ambiente `JAVA_TOOL_OPTIONS`. Por exemplo:

```
ENV JAVA_TOOL_OPTIONS="-Duser.language=en -Duser.region=US ${JAVA_TOOL_OPTIONS}"
```
Esse exemplo sobrescreve a configuração padrão de localidade das imagens, que é Português/Brasil, e mantém as demais propriedades já setadas em `JAVA_TOOL_OPTIONS `.


Além de herdar as configurações possíveis nas imagens com apenas JRE, as versões com Maven permitem configurar as credenciais do artifactory para que seja possível baixar artefatos e construir projetos. As variáveis de ambiente a serem fornecidas são(exemplo da seção "environments" do values.yaml):

```
- name: "ATF_PASSWORD"
  valueFrom:
    secretKeyRef:
      name: "atf-secret"
      key: "token"
- name: "ATF_USER"
  value: "dev-maven-user"

```

É mandatório para a segurança da credencial que a senha ou token do artifactory esteja armazenado em *secret*.

## Orientações adicionais

### Copiar executáveis para execução

O usuário de execução do processo Java na imagem é o `default`(185), enquanto ao copiar um arquivo para uma imagem através de um `COPY` no `Dockerfile`, a propriedade padrão é do usuário root. Assim, ao copiar o executável para a imagem, utilize a flag `--chown` do comando `COPY`. Exemplo:

`COPY --chown=185 target/quarkus-app/*.jar /deployments/`


## Atualização do Dockerfile

Neste exemplo será usado o Dockerfile gerado pelo nosso bbdev para aplicações Quarkus java.

### Quarkus 1.7.3.Final

Considerando o Dockerfile abaixo com a imagem lnx-jre-alpine:11.0.3, padrão anterior dos projetos gerados pelo Brave / BBDev:

```dockerfile
FROM atf.intranet.bb.com.br:5001/bb/lnx/lnx-jre-alpine:11.0.3
COPY target/lib/* /opt/lib/
COPY target/*-runner.jar /opt/app.jar
ENTRYPOINT ["java", "-Duser.country=BR", "-Duser.language=pt", "-jar", "/opt/app.jar"]
```

Para atualizar para a imagem dev-java:17.0.5 é necessario alterar o FROM para

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.0.5
```
> Sempre verificar qual a última versão disponível da imagem dev-java e ajustar no comando FROM.

E nos comandos COPY incluir a permissão para o usuario 185 e o local aonde vamos colocar a aplicação.

```dockerfile
COPY --chown=185 target/lib/* /deployments/lib/
COPY --chown=185 target/*-runner.jar /deployments/app.jar

```

Não é necessário informar ENTRYPOINT. Por padrão a imagem já está configurada com um ENTRYPOINT que executa a aplicação Java presente na pasta /deployments.

Dockerfile completo:

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.0.5

COPY --chown=185 target/lib/* /deployments/lib/
COPY --chown=185 target/*-runner.jar /deployments/app.jar

```

### Quarkus 1.12+

Caso o projeto já tenha sido atualizado para uma versão mais recente do Quarkus em algum momento, sendo essa versão com o Fast jar como padrão (A partir da 1.12.0.Final), o Dockerfile deve ficar assim:

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.0.5

COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/

```
> Lembrando: Sempre verificar qual a última versão disponível da imagem dev-java e ajustar no comando FROM.

Nesse caso, a divisão dos comandos de COPY facilita o reuso das camadas de *build* da imagem.
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/imagens-java.md&internalidade=java/imagens-java)
