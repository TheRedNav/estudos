> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Atualizando para o Quarkus 2
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/arquivados/atualizacao-quarkus.md&amp;action_name=frameworks/arquivados/atualizacao-quarkus)

## Glossario dos Arquivos

Antes de começar é preciso saber aonde ficam os principais arquivos que vamos alterar no seu projeto.
Faremos referências a varios deles e para facilitar temos abaixo uma lista com descrição de cada um e para que servem

* pom.xml: arquivo de xml de configuração do maven
  * localização: raiz do projeto.
  * função: possui a descrição da versao da aplicação, dependencias do projeto e configurações de build.
* dockerfile: arquivo de configuração da imagem docker usado pelo jenkins para construção de imagem.
  * localização: raiz do projeto.
  * função: descrição de como vai ser construido a imagem com um S.O. e a aplicação no formato jar.
* docker-compose: Arquivo de configuração para execução de um ou mais containers docker.
  * localização: Geralmente na pasta `/run/docker-compose.yaml`
  * função: possuir as configurações para executar as imagens docker, como environments, portas e rede.
* application.properties: Arquivo properties com chave/valor para configurar sua aplicação.
  * Localização: Na pasta `/src/main/resources`.
  * Função: Ter as configurações da sua aplicação.
* .env: arquivo de environments
  * localização na raiz do projeto
  * não deve ser comitado no git e seu conteudo deve ser explicado no ReadMe do projeto
* .env_curio: arquivo de enviroments do curio
  * localização na raiz do projeto
  * não deve ser comitado no git e seu conteudo deve ser explicado no ReadMe do projeto*
* run.sh: Shell script para executar a build do projeto e executar o docker compose
  * localização: Geralmente na pasta `/run/run.sh`
  * função: Receber os inputs para atualizar as configuracoes de acesso do maven, realizar o build do projeto e executar docker-compose para as imagens dockers presentes no docker-compose.
* .gitignore: arquivo de configuração do git
  * localização: raiz do projeto.
  * função: possui a lista de arquivos que devem ser ignorados no momento do commit para evitar arquivos mais criticos façam parte do repositorio.
* .dockerignore: arquivo de configuração do Docker
  * localização: raiz do projeto.
  * função: possui a lista de arquivos que devem ser ignorados na geração da imagem docker, evitando que código fonte vá para a imagem, por exemplo.

## Atualizar versão do quarkus no pom.xml

O primeiro item para atualizar é o pom do seu projeto, ele fica na pasta raiz do projeto. Nele que definimos as bibliotecas as quais o seu projeto depende e as instruções para realizar a construção da sua aplicação, como por exemplo a execução dos testes e analise de cobertura e como o .jar que é gerado.

No seu pom.xml buscar a tag `<properties>` a atualizar as propriedades conforme o exemplo abaixo, a versão do quarkus fica na propriedade `quarkus.platform.version` e você podera atualizar ela para qualquer versao disponivel acima da versão 2.

Algumas versões mais antigas do quarkus possuiam duas propriedades para versão uma para a plataforma e outra para o plugin, nesses casos voce pode remover a propriedade
`quarkus-plugin.version`.

```xml
  <properties>
    <compiler-plugin.version>3.8.1</compiler-plugin.version>
    <failsafe.useModulePath>false</failsafe.useModulePath>
    <maven.compiler.release>11</maven.compiler.release>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <quarkus.platform.artifact-id>quarkus-bom</quarkus.platform.artifact-id>
    <quarkus.platform.group-id>com.redhat.quarkus.platform</quarkus.platform.group-id>
    <quarkus.platform.version>2.7.6.Final-redhat-00006</quarkus.platform.version>
    <surefire-plugin.version>3.0.0-M5</surefire-plugin.version>
    <wiremock.version>2.27.2</wiremock.version>
    <jacoco.version>0.8.7</jacoco.version>
  </properties>
```

* **Observação** 

1. Caso o projeto possua outras propriedades especificas para o seu projeto mantenha elas dentro da tag `<properties>`.
2. Outra tag para verificação é a  `<dependencyManagement>` que deve ficar parecida com a descrita abaixo:


  ```xml
    <dependencyManagement>
      <dependencies>
        <dependency>
          <groupId>${quarkus.platform.group-id}</groupId>
          <artifactId>${quarkus.platform.artifact-id}</artifactId>
          <version>${quarkus.platform.version}</version>
          <type>pom</type>
          <scope>import</scope>
        </dependency>
      </dependencies>
    </dependencyManagement>
  ```
  
Além dela, incluir, se ainda não existir, as tags abaixo:

```xml
<repositories>
    <repository>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
      <id>redhat</id>
      <url>https://binarios.intranet.bb.com.br:443/artifactory/maven-redhat-remote</url>
    </repository>
  </repositories>
  <pluginRepositories>
    <pluginRepository>
      <releases>
        <enabled>true</enabled>
      </releases>
      <snapshots>
        <enabled>false</enabled>
      </snapshots>
      <id>redhat</id>
      <url>https://binarios.intranet.bb.com.br:443/artifactory/maven-redhat-remote</url>
    </pluginRepository>
  </pluginRepositories>
```
  

## Atualizar DockerFile e DockerFile.dev

Nas versões mais recentes, o Quarkus mudou a forma de empacotar os binários da aplicação na pasta /target. Por isso, a cópia dos arquivos no Dockerfile deve ser ajustada.

Outro ponto é que passamos a recomendar o uso de imagens com base nas imagens da [Red Hat](https://catalog.redhat.com/software/containers/explore).
Disponibilizamos 4 versões de imagens para Java, detalhadas na [documentação](../../java/imagens-java.md). Todas já são derivadas das imagens da RedHat.

Abaixo há um exemplo de Dockerfile para uma aplicação Quarkus rodando em Java 11.


DockerFile

```dockerfile
FROM atf.intranet.bb.com.br:5001/bb/dev/dev-java:11.0.16
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
```

A última linha do arquivo (comando ENTRYPOINT) é opcional. Caso não seja informada, será invocado o ENTRYPOINT da imagem base, que é um script que executa a aplicação Java presente na pasta /deployments com parâmetros de JVM ajustados. Mais detalhes [aqui](https://github.com/fabric8io-images/run-java-sh/blob/master/fish-pepper/run-java-sh/readme.md).

O arquivo Dockerfile.dev foi descontinuado e não vai ser necessário para utilizar o Quarkus 2.

## .dockerignore

A mudança no empacotamento do Quarkus ao longo das versões também impacta o arquivo `.dockerignore`. 

Sem alterá-lo, o desenvolvedor pode experimentar erros estranhos durante a execução do COPY no docker build:

```
Building dev-xpto
Step 1/6 : FROM atf.intranet.bb.com.br:5001/bb/dev/dev-java:17.0.3
---> 4b20a1dc7edb
Step 2/6 : COPY --chown=185 target/quarkus-app/*.jar /deployments/
ERROR: Service 'dev-xpto' failed to build: COPY failed: no source files were specified
```

Isso acontece porque o `.dockerignore` evitou que esses arquivos estivessem disponíveis no tempo de build. É importante manter um `.dockerignore` correto tanto da parte de fazer funcionar o build e a aplicação, quanto de levar o mínimo possível para dentro da imagem(não levando o código fonte, por exemplo).

Exemplo de `.dockerignore` para versão 2.6 do Quarkus:

```
*
!target/*-runner
!target/*-runner.jar
!target/lib/*
!target/quarkus-app/*
```



## Banco de Dados

Desde a versão 1.7 o quarkus disponibiliza uma extensão especifica para db2. Para o oracle isso comecou na versão 1.13. Antes a configuração de banco de dados era feita via extensão do jdbc do quarkus, aonde voce tinha que informar o driver, e o dialeto e a propriedade db-kind era configurada com o valor `other`, as extensoes especificas não precisam de tantas configurações, bastando informar a url de conexao usuario e senha, se tiver mais de um banco configurado, nesse caso voce tambem precisa informar a propriedade db-kind.

Vale ressaltar que algumas configurações são aplicaveis apenas no escopo de build, e outras no escopo de execução. O escopo de build só pode ser utilizado no momento na construção da aplicação, essa fase ocorre sempre que voce executa um comando de build do maven ou na esteira do jenkins. Por isso algumas dessas propriedades so podem ser definidas uma unica vez. Na pagina do [quarkus](https://quarkus.io/guides/all-config) temos a lista de todas as configurações disponiveis, as que são de build são precedidas por um :lock:. As de execução podem ser alteradas sem precisar fazer um novo build. Por isso ao verificar qual configuração voce vai utilizar certifique qual o escopo ela esta vinculada.

### Atualizando pom.xml

Para atualizar a extensão do quarkus para o banco de dados, remova as dependencias dos drivers. Elas ficam na tag `<dependencies>` do seu pom.xml.

DB2

```xml
      <dependency>
        <groupId>br.com.bb.dem</groupId>
        <artifactId>dem-db2jcc4</artifactId>
        <version>4.21.29</version>
      </dependency>
      <dependency>
        <groupId>br.com.bb.dem</groupId>
        <artifactId>dem-db2jcc-license-cisuz</artifactId>
        <version>1.0.0</version>
      </dependency>
```

Oracle

```xml
    <dependency>
      <groupId>com.oracle.jdbc</groupId>
      <artifactId>ojdbc8</artifactId>
      <version>12.2.0.1</version>
    </dependency>
```

Depois de remover incluia a extensao do quarkus com o banco que deseja utilizar dentro da tag `<dependencies>` do seu pom.xml.

Dependencia da extensão quarkus db2

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-db2</artifactId>
    </dependency>
```

Dependencia da extensão quarkus oracle

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-oracle</artifactId>
    </dependency>
```

### Atualizando application.properties

Para as configurações de acesso, url, usuario e senha, vamos vincular a propriedade de configuração a um environment do sistema para permitir uma maior flexibilidade na atribuição dos valores.
Isso pode ser feito indicando o valor com `${NOME_ENV}` conforme os exemplos abaixo.
Essas environments serão atribuidas valores no proximo topico.

Essas configurações precisam ser alteradas no application.properties, as configurações do banco h2 caso estejam sendo usadas para teste não precisam ser alteradas.

Exemplo com Db2

```properties
quarkus.datasource.db-kind=db2
quarkus.datasource.jdbc.url=${DB2_URL}
quarkus.datasource.username=${DB2_USER}
quarkus.datasource.password=${DB2_PASSWORD}
```

Exemplo com Oracle

```properties
quarkus.datasource.db-kind=oracle
quarkus.datasource.jdbc.url=${ORA_URL}
quarkus.datasource.username=${ORA_USER}
quarkus.datasource.password=${ORA_PASSWORD}
```

* Observação: caso utilize o DB2 e aconteça um erro parecido com `Could not fetch the SequenceInformation from the database: com.ibm.db2.jcc.am.SqlSyntaxErrorException: DB2 SQL Error: SQLCODE=-204, SQLSTATE=42704, SQLERRMC=SYSCAT.SEQUENCES, DRIVER=4.27.25`, pode ser preciso configurar o dialeto incluindo no application.properties a seguinte configuração :

```properties
quarkus.hibernate-orm.dialect=org.hibernate.dialect.DB2400Dialect
```

### Utilizar o arquivo .env

Sugerimos a criação do arquivo .env, ele irá possuir as variaveis de ambiente (environments) do seu sistema, principalmente aquelas que são mais criticas, como usuarios, senhas e tokens.
Por causa disso esse arquivo não deve ser comitado no repositorio de codigo fonte do seu projeto.

Para evitar isso é **importante** incluir esse arquivo , .env, na lista de arquivos ignorados pelo git, isso é feito incluindo o nome `.env' no arquivo .gitignore do seu projeto, caso esse arquivo não exista sugerimos usar o site [https://gitignore.io/](https://gitignore.io/) para criar um para você, aonde e possivel escolher as ferramentas com as quais trabalha e ele cria um arquivo com a lista de arquivos que serão ignorados.

Exemplo de arquivo .env com banco de dados db2.

```env
DB2_URL=String_de_conexao_banco_dados
DB2_USER=SEU_USUARIO_DO_BANCO_DE_DADOS
DB2_PASSWORD=SEU_PASSWORD_DO_BANCO_DE_DADOS
```

* **Observações**

- 1: Em alguns casos o usuário e senha podem ser a propria matricula e senha do SISBB do desenvolvedor, nesses casos utilizamos um shell script para criar as environments apenas para aquela execução de comando.

- 2: Caso precise se conecetar em mais de um esquema ou banco de dados, duplique as enviroments citadas acima adicionando aliases.

- 3: É uma boa prática descrever no seu README.md as variáveis que sua aplicação precisa para funcionar, o que elas fazem, e aonde obtê-las. Se possível é interessante colocar um exemplo de .env.

### Atualizar o docker-compose.yaml

O docker-compose.yaml possui as configurações e environments que sua aplicação precisa, com relação as configurações sobre banco de dados que fizemos até agora, precisamos ajustar as environments de banco para obter as informações do arquivo .env.

Vale ressaltar que o quarkus possui uma prioridade de busca sobre as environments, primeiro ele busca as variáveis de ambiente (environments) que ja estão no sistema, depois as que estão no application.properties. Outro ponto é a nomenclatura utilizada, no application.properties é aceito ponto e outros caracteres, mas para converter ele para o padrão de nomenclatura de environment, para isso substitua tudo o que não for letra ou numero por `_` e tudo em maiúsculo.
Para maiores informações sobre isso acesse a pagina do [quarkus](https://quarkus.io/guides/config-reference) sobre configuração da aplicação.

Remover toda configuração de banco presente nas environments da sua aplicação que seja de build-time, voce pode conferir isso na pagina do [quarkus](https://quarkus.io/guides/all-config) com as configuracoes que ele aceita (Atenção para a versão escolhida do quarkus na pagina de configuração), todas que tiverem um cadeado na frente são de build-time e devem ficar no application.properties.

E colocar apenas as configurações de url de conexão, usuario e senha conforme abaixo, nesse caso estamos usando o exemplo do DB2, se fosse oracle seria com o prefixo `ORA_` conforme definimos nas explicações acima

```yaml
      - QUARKUS_DATASOURCE_URL=${DB2_URL}
      - QUARKUS_DATASOURCE_USERNAME=${DB2_USER}
      - QUARKUS_DATASOURCE_PASSWORD=${DB2_PASSWORD}
```

Dessa forma ele ira pegar as informações do arquivo .env que possui os valores para as environments , DB2_URL, DB2_USER e DB2_PASSWORD.
Caso ele de erro de não encontrar esses dados certifique que o arquivo .env esta na raiz do projeto e que voce esta executando o comando do docker-compose up a partir da pasta raiz do projeto.


## Atualizando aplicação com CURIÓ


### Atualizando application.properties

Para usar as dependencias das operações, tanto para consumo quanto provimento, é necessario incluir a seguinte configuração no `application.properties` do seu projeto.
Substiuindo a {sigla} pela sigla de cada operação que deseja utilizar, seja consumo ou provimento. Caso haja mais de uma, separar os pacotes com vírgula e sem espaço entre eles.

Exemplo: 
```yml
# Exclusão no CDI das classes presentes nos pacotes das dependencias de operação IIB
quarkus.arc.exclude-types=br.com.bb.dev.operacao.**,br.com.bb.aic.operacao.**
```

Isso é feito para ignorar alguns beans CDI que não são usados pelo curió e podem causar conflito.

### Criar arquivo de enviroments

Para facilitar a manipulação das variáveis de ambiente do CURIO, crie um arquivo na raiz do projeto com o nome `.env_curio` nele teremos todas as configurações do curio, [aqui](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio).

Lembre-se de deixar o conteudo desse arquivo dentro do README.md do seu projeto explicando as configurações que deveram ser usadas, e caso utilize a propriedade CHA

Abaixo temos a configuração minima para ambiente de desenvolvimento:

```
KUMULUZEE_SERVER_HTTP_PORT=8081
CURIO_CACHE_CONFIGURACAO_IIB=iib-slave.redis.bdh.desenv.bb.com.br
CURIO_CACHE_CONFIGURACAO_IIB_ID=iib:configuracao:k8s-integracao
CURIO_SIGLA_APLICACAO=sua-sigla
CURIO_APLICACAO_HOST=http://localhost:8080
CURIO_IIB_LOG_LEVEL=FINE
CURIO_DRY_RUN=false
CURIO_MODO_DESENVOLVIMENTO=true
KUMULUZEE_LOGS_LOGGERS0_NAME=br.com.bb
KUMULUZEE_LOGS_LOGGERS0_LEVEL=TRACE
IDH_CHAVE_APLICACAO=
CURIO_OP_PROVEDOR=
CURIO_OP_CONSUMIDOR=
```
**Observação:**

1. As propriedades `CURIO_OP_PROVEDOR` e `CURIO_OP_CONSUMIDOR` devem ser informadas de acordo com a necessidade de prover(disponibilizar uma operacao) ou consumir(chamar uma operação que é provida por outra aplicação). A nomeclatura do valor utilizado deve seguir a [documentação do curio](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio#iniciando)

3. A propriedade `IDH_CHAVE_APLICACAO` é o identificador da aplicação no sistema IDH,
 ele pode ser obtida seguindo o [roteiro](https://fontes.intranet.bb.com.br/idh/publico/roteiros/-/wikis/home)


### Atualizando docker-compose

Depois de criar o arquivo .env_curio altere seu docker compose removendo a tag environment do curio e incuindo a tag env_file com o arquivo .env_curio, conforme o exemplo abaixo.
Assim tanto a execução com o docker run e o docker compose vao utilizar as mesmas configurações.

```yaml
  # =============================================================================
  #   Sidecar CURIO Utilizado para realizar a comunicação entre IIB e aplicação
  # =============================================================================
  iib-curio:
    container_name: iib-curio
    image: atf.intranet.bb.com.br:5001/bb/iib/iib-curio:0.6.5
    # as enviroments estarão presentes no arquivo .env_curio na raiz do projeto, voce deve alterar la as configuracoes
    env_file:
      - .././.env_curio
    # se estiver acessando no windows, alterar abaixo as portas 8081 e também na variável no .env_curio KUMULUZEE_SERVER_HTTP_PORT para 8091 por exemplo
    # pois a porta 8081 no windows pode já estar ocupada por outros serviços  como o antivirus
    ports:
      - "8081:8081"
    network_mode: host
```

**Observação:**

1. O atributo `ports` deve ser alterado caso esteja usando windows, pois a porta 8081 ja esta em uso pelo Antivirus, caso queria acessar o curio via windows altere para outra porta como a 8091 e altere tambem a identificação da porta na variavel `CURIO_HOST_MP_REST_URL` na enviroments da sua aplicação.

2. Esse exemplo esta usando o modo host para o `network_mode` se o seu docker compose ja possuir uma rede configurada voce deve mudar o `network_mode` para `networks:` e colocar o nome da rede do docker compose na linha abaixo seguindo a identação, geralmente esta presente no final do arquivo. 


## Script Run

O script run.sh foi alterado para permitir a iteração com quarkus-cli presente na versao 2 do quarkus. Por causa disso não estamos mais utilizando o docker-compose no desenvolvimento com alteração direta no codigo, nesses casos quando for realizado uma alteração no codigo é necessario realizar um novo build.
Assim criamos um modo de execução local para aplicação e uma opção de subir o curio usando o docker.
No proprio script existe a descrição de como ele funciona e como ele deve ser usado.
Para atualizar baixe uma copia do [run.sh](http://atf.intranet.bb.com.br/artifactory/bb-binarios-local/dev/scripts/run.sh) e substitua o existente e certifique-se de que esse arquivo possiu permissão de execução.

Isso pode ser feito indo em um terminal linux e indo na pasta raiz do seu projeto e execute o comando

```shell
chmod +x ./run/run.sh
``` 

**Observação:**

1. Caso use o curio será necessario configurar o curio com o `.env_curio` como citado no [topico anterior](#atualizando-docker-compose).

2. Caso use o docker-compose será necessario fazer o ajuste descrito no proximo topico e o descrito no topico do [curio](#atualizando-docker-compose)


## Maven Wrapper

Criar ou modificar o arquivo `.mvn/wrapper/maven-wrapper.properties` para o seguinte conteúdo:

```
distributionUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/apache-maven/3.8.4/apache-maven-3.8.4-bin.zip
wrapperUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar  
 
```
(deixar uma linha em branco no fim do arquivo, fazendo o total de 3 linhas)

Apagar o jar do maven wrapper na mesma pasta, se houver.


## Docker-Compose

Substitua seu docker-compose.yaml [por esse](./arquivos/docker-compose.yaml).
Ele foi simplificado e ja possui uma imagem para sua aplicação, uma para o curio e outra com o jaeger. Voce pode adicionar outras conforme sua necessidade, mas nesse caso mudamos a configuração de rede para o modo `host`.


A parte de configuração da aplicação esta definida entre a [linha 3](./arquivos/docker-compose.yaml#L3) ate a [linha 33](./arquivos/docker-compose.yaml#L33) nesse trecho substitua o termo `nome-aplicacao` pelo nome da sua aplicação.

O curio foi configurado entre a [linha 34](./arquivos/docker-compose.yaml#L34) ate a [linha 46](./arquivos/docker-compose.yaml#L46), e mesmo trecho citado na parte de migração do 
[topico de atualização do docker compose do curio](#atualizando-docker-compose).

Por fim temos o jaeger  entre a [linha 47](./arquivos/docker-compose.yaml#L47) ate a [linha 62](./arquivos/docker-compose.yaml#L62) ele server para visualizar os tracing da aplicação e voce pode consultar acessando o endereço [localhost:16686](localhost:16686)


## Melhorias no Swagger 

A habilitação e configuração do Swagger na versão 2.x do Quarkus pode ser feita utilizando apenas variáveis do application.properties.

Assim, caso possua uma classe AppConfig no projeto, veja esse [roteiro](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/swagger.md#alternativa-para-configuracao-do-swagger) para possivelmente excluí-la ou editá-la.

Voce pode alterar algumas propriedades do swagger durante a execução da aplicação acessando o [http://localhost:8080/dev/](http://localhost:8080/dev/) quando a aplicacao estiver executando no modo dev.

Mais detalhes (aqui)[https://quarkus.io/guides/openapi-swaggerui#providing-application-level-openapi-annotations]

## Cobertura de testes

Nas versões mais recentes do Quarkus, foi criada uma extensão do JaCoCo que substitui toda a configuração do pom.xml. Para funcionar na esteira, é necessário no entanto que o nome do arquivo de saída do JaCoCo seja ajustado, conforme a seguir:

```
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jacoco</artifactId>
      <scope>test</scope>
    </dependency>
```

Para o Sonar conseguir ler o resultado do relatório gerado, e consequentemente conseguir a pontuação do motor de liberação, é necessario alterar o nome do arquivo com o relatorio da cobertura para 'jacoco.exec'. Para isso inclua o seguinte trecho em seu 'application.properties':

```
#jacoco
quarkus.jacoco.report-location=site/jacoco
quarkus.jacoco.data-file=jacoco.exec
```

De forma alternativa, caso queira manter o padrão de pastas definido pelo plugin quarkus-jacoco, é possível informar ao Sonar o caminho do relatório.

Além disso, é possível também informar ao Sonar os pacotes que devem ser excluidos da estatística de cobertura, como por exemplo, o pacote /integration/, que já vem no projeto padrão do bb-dev, ou pastas que só contêm POJOs ou DTOs. Isso é útil para considerar na estatística de cobertura de código, apenas as classes que são relevantes e que de fato devem ser testadas, evitando assim, a criação de testes inúteis que testam por exemplo getters, setters, equals e hashcode.


```
    <properties>
        ...
        <sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/jacoco-report/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
        <sonar.coverage.exclusions>**/integration/*</sonar.coverage.exclusions>
        ...
    </properties>
```

## Lib de erros

Para o Quarkus 2.0 a extensão dev-dx-quarkus foi descontinuada. Para que se possa fazer o tratamento de erros para operações Curió no mesmo formato, sugerimos o uso da dependência [dev-java-erro](https://fontes.intranet.bb.com.br/dev/dev-java-erro)

## B5

A monitoração das aplicações continua através da biblioteca [quarkus-monitor](https://github.com/labbsr0x/quarkus-monitor), agora hospedada no Github do Labbs, 
na versão 0.3.0.

Caso esteja usando a versão anterior dessa biblioteca substitua pela nova versão conforme abaixo:

```xml
<dependency>
  <groupId>br.com.labbs</groupId>
  <artifactId>quarkus-monitor</artifactId>
  <version>0.3.0</version>
</dependency>
```

O quarkus-monitor mais recente utiliza o [Micrometer](https://micrometer.io/docs/registry/prometheus) para geração das metricas.

Caso o projeto utilize Quarkus 1.7.3, é provável que ele utilize Microprofile Metrics. Nas versões mais recentes do Quarkus, subir os dois provedores de métricas causa erro. Então, ao adicionar a `quarkus-monitor:0.3.0`, remova a `smalllrye-metrics` e a `smallrye-fault-tolerance`, além da descontinuada `dev-dx-quarkus-ext`. As duas últimas importam a `smalllrye-metrics` transitivamente. Se possível analise suas outras dependências para garantir a unicidade dos provedores de métricas.

***Atenção:*** Ao mudar a versão da biblioteca, podem ocorrer mudanças nas métricas geradas pela aplicação. Faça os testes no Prometheus e Grafana antes de subir para produção.

## Metricas e Health

Nas novas versões do Quarkus, os endpoints automáticos do estado da aplicação ficam no path `/q`. Por exemplo, `/q/health` para health check e `/q/metrics` para métricas. Isso pode ser alterado configurando a propriedade [quarkus.http.non-application-root-path](https://quarkus.io/guides/all-config#quarkus-vertx-http_quarkus.http.non-application-root-path) no application.properties.

Caso queira remover o `/q`, mantendo a compatibilidade anterior, inclua no application.properties a entrada: `quarkus.http.non-application-root-path=/`

É importante ressaltar que caso nenhum ajuste seja feito no application.properties, e seja mantido o novo padrão Quarkus de endpoints de monitoração com `/q`, é necessário ajustar o values.yaml de cada ambiente nas seções de livenessProbe, readinessProbe e serviceMonitor.


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/atualizacao-quarkus.md&internalidade=frameworks/quarkus/atualizacao-quarkus)
