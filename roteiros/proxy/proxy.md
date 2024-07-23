> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Proxy
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/proxy/proxy.md&amp;action_name=proxy/proxy)

## 1. Situações Frequentes

1.1. Casos de usos: para algumas aplicações pode ser necessário o acesso a urls ou apis externas ao Banco do Brasil. Dentre os casos podemos destacar:

* Acesso a APis de empresas coligadas como a BB Seguridade, BB Tur, etc.
* Acesso a APIs de empresas parceiras.
* Acesso a informações que se encontram em domínios públicos.

Deve-se fazer o uso do proxy por questões de segurança.

1.2. O roteiro para liberação no GSS das uls/apis externas encontra-se em: https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/acesso-internet-sem-autentica%C3%A7%C3%A3o

A partir de 16/11/2021, conforme [comunicado](https://connections.bb.com.br/blogs/7b230200-4840-43af-8160-a9bffd1bdb7e/entry/16_11_Fim_das_solicita%C3%A7%C3%B5es_de_de_regra_de_firewall_Aplicativo_GSS?lang=pt_br) ,  liberação de urls externas é feita via PLATAFORMA > SUPORTE TÉCNICO, serviço "Solicitação de Regra de Firewall".

1.3. Após seguir o roteiro abaixo, teste a conectividade do seu pod, conforme item 4 deste roteiro, antes de abrir uma issue.



## 2. Adicionando configuração de proxy diretamente no values.yaml

### 2.1. Configurando o values: Para versões que utilizam o **bb Aplic 5.2.1** ou superiores (a versão do BB aplic utilizado pode ser verificado no arquivo `requirements.yaml`), basta adicionar a funcionalidade `acessoProxy: true` em seu  `values.yaml` conforme documentação([BBAplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic/5.3.2)).

Ex:

```yaml
deployment:
  enable: true
  [...]
ingress:
  enable: true
  [...]
acessoProxy: true #observe a identação.
    
```

Além disso, precisamos configurar as variáveis de proxy para os aplicativos. As configurações podem variar conforme a biblioteca ou executável em uso, mas aqui estão alguns exemplos mais comuns: 

### 2.2. Para imagens dev-java ou dev-java-jdk, não é necessário alterar o Dockerfile, o que facilita na hora de executar as imagens em desenvolvimento.

No values.yaml dos ambientes, declare as variáveis da seguinte forma(a ordem importa!):

```yaml
deployment:
  enable: true
  [...]
  containers:
  [...]  
  environments:
    - name: PROXYHOST
      value: "cachebb.psc-proxy"
    - name: PROXYPORT
      value: "80"   # Em desenvolvimento e homologação utilizar porta 8887
    - name: NONPROXY
      value: "localhost|127.0.0.1|*.bb.com.br"
    - name: JAVA_TOOL_OPTIONS
      value: -Duser.language=pt -Duser.region=BR -Dhttp.proxyHost=$(PROXYHOST) -Dhttp.proxyPort=$(PROXYPORT) -Dhttps.proxyHost=$(PROXYHOST) -Dhttps.proxyPort=$(PROXYPORT) -Dhttp.nonProxyHosts=$(NONPROXY)
    [...]  
```

* Atenção para a porta, que no ambiente de desenvolvimento e homologação foi alterada para 8887 conforme [comentário nesta issue](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/issues/2250#note_374908).

* Atenção para a env NONPROXY, aqui você DEVE incluir os endpoints que NÃO deverão passar pelo proxy. Exemplos: 

  1. Se a aplicação conecta com um DB Oracle ou MQ, que está dentro da rede do BB, chamando, por exemplo: exa01-scan.bb.com.br, a inclusão do `*.bb.com.br` no NONPROXY é suficiente, conforme modelo acima . 

  2. Entretanto, se a aplicação chama o mesmo DB Oracle ou MQ diretamente pelo ip (algo não recomendado), tipo 172.17.X.X, esse ip também deve ser incluído na lista do NONPROXY por estar dentro da rede do BB:  ` value: "localhost|127.0.0.1|*.bb.com.br|172.17.x.x" `.


### 2.3. Para Java/Quarkus na imagem base lnx-jre-alpine(versões mais antigas):

```Dockerfile
FROM atf.intranet.bb.com.br:5001/bb-infra/lnx/lnx-jre-alpine:8.191.2
RUN mkdir /app

COPY target/*-runner.jar /app/app.jar

EXPOSE 8080

CMD [ "sh", "-c", "java -Djava.library.path=/usr/local/lib -Dhttps.proxyHost=$PROXYHOST -Dhttps.proxyPort=$PROXYPORT -Dhttp.proxyHost=$PROXYHOST -Dhttp.proxyPort=$PROXYPORT -Dhttp.nonProxyHosts=$NONPROXY -jar /app/app.jar"]
```

e no arquivo values.yaml: 

```yaml
deployment:
  enable: true
  [...]
  containers:
  [...]  
  environments:
    - name: PROXYHOST
      value: "cachebb.psc-proxy"
    - name: PROXYPORT
      value: "80"   # Em desenvolvimento e homologação utilizar porta 8887
    - name: NONPROXY
      value: "localhost|127.0.0.1|*.bb.com.br"
   [...]  
```

* Ver avisos do item 2.2!


### 2.4. Já para Java com Springboot , além das variáveis acima, é necessário alterar o values assim, acrescentando MAIS três variáveis, em minúsculo (http_proxy, https_proxy e no_proxy):

```yaml
deployment:
  enable: true
  [...]
  containers:
  [...]  
  environments:
    - name: PROXYHOST
      value: "cachebb.psc-proxy"
    - name: PROXYPORT
      value: "80"   # Em desenvolvimento e homologação utilizar porta 8887
    - name: NONPROXY
      value: "localhost|127.0.0.1|*.bb.com.br"
    - name: http_proxy
      value: "http://cachebb.psc-proxy:80"   # Em desenvolvimento e homologação utilizar porta 8887
    - name: https_proxy
      value: "http://cachebb.psc-proxy:80"   # Em desenvolvimento e homologação utilizar porta 8887
    - name: no_proxy
      value: "localhost,127.0.0.1,*.bb.com.br"       
   [...]  
```

* Ver avisos do item 2.2!


### 2.5. Para Java com a lib apache.httpclient (versão 4.5.5) , além das variáveis do item 2.2, é necessário incluir no código da chamada http/https assim:

```java
String proxyHostname = System.getenv("PROXYHOST");
int proxyPort = Integer.parseInt(System.getenv("PROXYPORT"));

HttpHost proxy = new HttpHost(proxyHostname, proxyPort, HttpHost.DEFAULT_SCHEME_NAME);
DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
CloseableHttpClient httpclient = HttpClients.custom()
                    .setRoutePlanner(routePlanner)
                    .build();
```

Atenção: a forma de configurar proxy com essa lib apache.httpclient MUDA conforme a versão, se 4.1, 4.3 ou 4.5. Consulte a documentação da lib e o StackOverFlow abaixo:

https://stackoverflow.com/questions/4955644/apache-httpclient-4-1-proxy-settings



### 2.6. Para Java com a lib com.squareup.okhttp3, também é necessário forçar o uso do proxy no código, tal como exemplo abaixo e da issue [3388](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/3388#note_280274):

```java
        Response res = null;
        OkHttpClient cliente;

        // pega as variaveis de proxy setadas no values.yaml
        String proxyHostname = System.getenv("PROXYHOST");
	    // String proxy_hostname = "cachebb.psc-proxy";
        int proxyPort = Integer.parseInt(System.getenv("PROXYPORT"));
        // int proxy_port = 80; 

        if (proxyHostname.isEmpty()){
        Proxy proxy = new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(proxyHostname, proxyPort));
        final OkHttpClient.Builder builder = new OkHttpClient.Builder().proxy(proxy);
        } else {
         final OkHttpClient.Builder builder = new OkHttpClient.Builder();
        }
        
	    RequestBuilder builder = RequestBuilder.get(RequestBuilder.constructHttpUrl(url, new String[0]));
        builder.header(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        builder.header(HttpHeaders.ACCEPT, HttpMediaType.APPLICATION_JSON);
         
         res = cliente.newCall(builder.build()).execute();
[...]
```



### 2.7. Para Node:

```DockerFile
FROM atf.intranet.bb.com.br:5001/bb/lnx/lnx-node-alpine:10.16.0
WORKDIR /src/app

COPY . /src/app
CMD ["sh", "-c", "NODE_ENV=$NODE_ENV SERVER_AUTHORIZATION_KEY=$SERVER_AUTHORIZATION_KEY https_proxy=$https_proxy http_proxy=$http_proxy no_proxy=$no_proxy npm start"]

```


É interessante que passe essas variáveis de ambiente via values.yaml, pois o endereço de proxy pode ser alterado sem que tenha que efetuar o build novamente da imagem.

```yaml 
  deployment:
    enable: true
    [...]
    containers:
      [...]
      environments:
        - name: http_proxy
          value: "http://cachebb.psc-proxy:80"   # Em desenvolvimento e homologação utilizar porta 8887
        - name: https_proxy
          value: "http://cachebb.psc-proxy:80"   # Em desenvolvimento e homologação utilizar porta 8887
        - name: no_proxy
          value: "localhost,127.0.0.1,.bb.com.br" 
```

Atenção para a env NO_PROXY, aqui você DEVE incluir os endpoints que NÃO deveram passar pelo proxy, seguindo a mesma observação do item 2.2.

Para node com a biblioteca Axios, veja as observações abaixo, na qual é necessário forçar o http-agent ou substituir a lib pela axios-proxy.

### 3. Observações importantes

- É altamente recomendável utilizar versões do Quarkus >= 1.61, pois resolve o problema de conexão com o curió caso esteja consumindo e utilizando o proxy conforme relatado na issue [#1733](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/1733#note_105199)
- Caso faça uso de javascript e utilize a biblioteca Axios para acessar externamente, utilize uma das soluções apontadas na issue [#1610](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/1610#note_92502)



### 4. Testes de conectividade

Antes de abrir uma issue, é possível entrar no pod pelo Rancher ou Kubectl para verificar se o pod já tem conectividade com o proxy, com a URL desejada e até realizar um diagnóstico mais avançado.  

Lembrando que a URL externa deve ser liberada através do GSS, cujo roteiro encontra-se em: https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/acesso-internet-sem-autentica%C3%A7%C3%A3o

A partir de 16/11/2021, conforme [comunicado](https://connections.bb.com.br/blogs/7b230200-4840-43af-8160-a9bffd1bdb7e/entry/16_11_Fim_das_solicita%C3%A7%C3%B5es_de_de_regra_de_firewall_Aplicativo_GSS?lang=pt_br) ,  liberação de urls externas é feita via PLATAFORMA > SUPORTE TÉCNICO, serviço "Solicitação de Regra de Firewall".


Para isso, entre no pod (necessário acesso ALMFD + SIGLA) e verifique se a imagem tem o executável wget ou curl e, se tiver, é possível testar a conectividade desta forma:

#### 4.1 Acessar o pod via Rancher

As aplicações estão divididas entre três instâncias do Rancher, localize a sua em um dos endereços abaixo:

- caas.nuvem.bb.com.br
- caas.apps.nuvem.bb.com.br
- caas.data.nuvem.bb.com.br

Pesquise a sua aplicação no menu **Global** pelo nome do namespace ou então pela sigla, e localize o pod em **Resources** > **Workloads**, clique no menu à direita do nome do pod e selecione a opção **Execute Shell**. Será aberto um terminal para a execução de comandos dentro do pod.

#### 4.2 Acessar o pod via Kubectl

Se ainda não tiver o Kubectl configurado na sua máquina, faça a configuração seguindo [esse roteiro](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/roteiros/geracao-kubeconfig.md). No final do roteiro estão os comandos para selecionar o cluster e listar os pods da sua aplicação.

Após listar os pods execute o comando abaixo para abrir um terminal dentro do container.

```bash
$ kubectl -n nome-do-namespace exec -it -c nome-do-container nome-do-pod -- sh
```
Obs: Lembre-se de substituir no comando os dados abaixo:

- **nome-do-namespace**: Nome do seu namespace, que começa sempre pela sua sigla
- **nome-do-pod**: Nome do pod em que deseja executar o teste
- **nome-do-container**: Nome do container. O seu pod pode ter apenas o container da aplicação, ou então pode ter também o container do Curió. Se só tiver a aplicação o `-c nome-do-container` pode ser retirado do comando, mas se tiver também o Curió, deve ser informado o nome do container da aplicação. Para listar os nomes dos containers do seu pod execute o comando `kubectl -n nome-do-namespace get pods nome-do-pod -o jsonpath='{.spec.containers[*].name}'`

#### 4.3 Executar o teste com 'wget'

* Atenção para a porta utilizada nos testes, que no ambiente de desenvolvimento e homologação foi alterada para 8887 conforme [comentário nesta issue](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/issues/2250#note_374908).

4.3.1. Para URLs sem autenticação:

*Em desenvolvimento e homologação:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:8887 -e http_proxy=http://cachebb.psc-proxy:8887 --method POST $url

$ cat debug.txt
```

*Em produção:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:80 -e http_proxy=http://cachebb.psc-proxy:80 --method POST $url

$ cat debug.txt
```

Obs: Se o método for GET, PUT ou OPTIONS, altere o método (=parâmetro --method) no comando acima. Algumas versões mais antigas do wget não permitem alteração do método e nesse caso, utilize o curl conforme seção 4.4.

A variável `$url` do comando acima deve ser trocada pela url que deseja testar, exemplo: `https://www.google.com`. 

Logo, um exemplo de teste de conectividade em produção de uma url seria assim: 

```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:80 -e http_proxy=http://cachebb.psc-proxy:80 --method GET https://www.google.com
$ cat debug.txt
```

4.3.2. Para URLs com autenticação tipo Basic:

*Em desenvolvimento e homologação:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:8887 -e http_proxy=http://cachebb.psc-proxy:8887 --method POST --http-user $usuario --http-password $password $url

$ cat debug.txt
```

*Em produção:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:80 -e http_proxy=http://cachebb.psc-proxy:80 --method POST --http-user $usuario --http-password $password $url

$ cat debug.txt
```


4.3.3. Para URLs com autenticação tipo Bearer:

*Em desenvolvimento e homologação:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:8887 -e http_proxy=http://cachebb.psc-proxy:8887 --method POST --header="Authorization: Bearer $TOKEN" $url

$ cat debug.txt
```

*Em produção:*
```bash
$ wget -v -o debug.txt -d -e use_proxy=yes -e https_proxy=http://cachebb.psc-proxy:80 -e http_proxy=http://cachebb.psc-proxy:80 --method POST --header="Authorization: Bearer $TOKEN" $url

$ cat debug.txt
```

#### 4.4 Executar o teste com 'curl'

Caso a imagem Docker da sua aplicação não tenha o **wget** instalado, você também pode fazer o teste com o **curl** conforme abaixo.

4.4.1. Para URLs sem autenticação:

*Em desenvolvimento e homologação:*
```bash
$ curl -vvv -x http://cachebb.psc-proxy:8887 -X POST -o debug.txt $url
```

*Em produção:*
```bash
$ curl -vvv -x http://cachebb.psc-proxy:80 -X POST -o debug.txt $url
```

* Obs: Se o método for GET, PUT ou OPTIONS, altere o método (=parâmetro -X) acima.

Também, a variável `$url` do comando acima deve ser trocada pela url que deseja testar, exemplo: `https://www.google.com`. 

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/proxy/proxy.md&internalidade=proxy/proxy)
