> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2.md&amp;action_name=frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2) 

# Como atualizar o Quarkus para a versão 2

Este roteiro mostra como atualizar o Quarkus para a segunda versão. É essencial manter o Quarkus atualizado, pois as versões mais recentes trazem melhorias e aprimoramentos de segurança. 

> :warning: **Atenção** 
>  
> Antes de começar, analise o impacto das mudanças ao atualizar o Quarkus. Certifique-se de entender as mudanças, os novos recursos e as possíveis quebras de compatibilidade. Realize testes de unidade, integração e sistema para garantir que todas as funcionalidades da sua aplicação continuarão funcionando conforme o esperado na nova versão.

## Requisitos

* Um projeto gerado com o Quarkus 1.

## Passo 1: Atualizar o pom.xml

1. Abra a raiz do projeto na sua IDE.
2. Localize o [arquivo pom](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Localize a tag **properties**. É dentro dessa tag que você adiciona todas as propriedades específicas do seu projeto.
4. Dentro da tag, localize a propriedade **quarkus.platform.version** e atualize para a versão desejada. Você pode atualizar para qualquer versão disponível acima da versão 2.

> :information_source: **Observação** 
> 
> Algumas versões mais antigas do Quarkus possuem duas propriedades, uma para a versão da plataforma e outra para a versão do plugin. Nesses casos, você pode remover a propriedade **quarkus.plugin.version**.
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
5. Verifique a tag **dependencyManagement**, que deve ficar conforme o exemplo abaixo: 
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
  6. Caso o seu projeto não possua as tags **repositories** e **pluginRepositories**, adicione-as ao arquivo conforme o exemplo abaixo: 
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
7. Em **build**, altere o  **plugin** do Quarkus conforme o exemplo abaixo: 
```xml
<plugin>
  <groupId>${quarkus.platform.group-id}</groupId>
  <artifactId>quarkus-maven-plugin</artifactId>
  <version>${quarkus.platform.version}</version>
  <extensions>true</extensions>
  <executions>
    <execution>
      <goals>
        <goal>build</goal>
        <goal>generate-code</goal>
        <goal>generate-code-tests</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
8. Verifique ou inclua, caso ainda não tenha no seu arquivo, a dependência de erro abaixo:  
```xml
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-java-erro</artifactId>
  <version>1.3.0</version>
</dependency>
```

## Passo 2: Configurar o Swagger (opcional)

Configurar o Swagger em aplicações Quarkus é uma prática altamente recomendada, embora não obrigatória. Isso simplifica a documentação, colaboração e teste de APIs, entre outros benefícios. 

A maioria das configurações do Swagger para o Quarkus são aplicadas durante o processo de construção (*build*), quando o projeto é compilado e gera um arquivo *.jar*. Essas configurações não podem ser alteradas dinamicamente em tempo de execução e as propriedades não afetam o comportamento da aplicação quando são definidas como variáveis de ambiente, por exemplo, no arquivo *values.yaml* ou no ambiente do *Docker Compose*. 

Para ver todas as configurações disponíveis, consulte a [documentação do Quarkus](https://quarkus.io/guides/openapi-swaggerui#configuration-reference). Lembre-se de que as propriedades listadas na documentação com um cadeado antes do nome são configurações aplicadas durante o *build*.

> :grey_exclamation: **Importante** 
> 
> Se o seu projeto foi gerado com a versão 1.7.3 ou anterior, você pode simplificar removendo a classe *AppConfig*. Se ela contiver qualquer lógica, basta remover a anotação *@OpenAPIDefinition* e seu conteúdo. Em seguida, prossiga com o roteiro.

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo [application.properties](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Realize as configurações conforme o exemplo abaixo, não esquecendo de alterar os valores conforme os dados do seu projeto:

```properties
# Configuracao do caminho para acessar a documentacao da aplicacao em swagger
quarkus.swagger-ui.always-include=true
quarkus.health.openapi.included=true
quarkus.swagger-ui.urls.default=/api-docs-json
quarkus.smallrye-openapi.path=/api-docs-json
quarkus.swagger-ui.path=/api-docs
quarkus.swagger-ui.filter=true
mp.openapi.extensions.smallrye.info.title=${quarkus.application.name}
mp.openapi.extensions.smallrye.info.version=${quarkus.application.version}
%test.mp.openapi.extensions.smallrye.info.title=minhaApp
%test.mp.openapi.extensions.smallrye.info.version=1.0.0
mp.openapi.extensions.smallrye.info.description=Descreva a descricao da sua aplicacao, altere na propriedade mp.openapi.extensions.smallrye.info.description do application.properties
mp.openapi.extensions.smallrye.info.contact.name=Coloque sua equipe aqui, altere na propriedade mp.openapi.extensions.smallrye.info.contact.name do application.properties
mp.openapi.extensions.smallrye.info.contact.url=https://fontes.intranet.bb.com.br/sua-sigla/sua-aplicacao
mp.openapi.servers=http://localhost:8080
```

## Passo 3: Atualizar o DockerFile 

1. Abra a raiz do projeto na sua IDE.
2. Localize o [Dockerfile](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Ajuste a cópia dos arquivos. No exemplo abaixo, o Quarkus será rodado em Java 11.

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:11.0.16
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/

ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
```
> :grey_exclamation: **Importante** 
> 
> O arquivo **Dockerfile.dev** foi descontinuado e não é mais necessário para usar o Quarkus 2. Remova-o do projeto.

> :bulb: **Dica** 
> 
> Consulte o roteiro [Como usar imagens base para desenvolver aplicações Java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md) para obter as imagens Java disponibilizadas pelo banco.

## Passo 4: Atualizar o .dockerignore

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo [.dockerignore](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Faça a seguinte configuração para incluir arquivos e diretórios específicos dentro de **target/** na imagem Docker. Essas configurações excluem, ao mesmo tempo, o que não é necessário para a imagem.
```
*
!target/*-runner
!target/*-runner.jar
!target/lib/*
!target/quarkus-app/*
```
> :grey_exclamation: **Importante** 
> 
> Sem essa alteração, o desenvolvedor pode encontrar erros durante a execução do comando COPY no Docker *build*.

## Passo 5: Atualizar o Script Run

> :grey_exclamation: **Importante** 
> 
> O *script run.sh* foi alterado para permitir a interação com o *quarkus-cli* na versão 2 do Quarkus e por isso o *docker-compose* não é mais utilizado no desenvolvimento com alteração direta no código. Nesses casos, quando for realizada uma alteração no código, será necessário realizar um novo *build*. No próprio script você encontra a descrição de como ele funciona.

1. Baixe uma cópia do [run.sh](frameworks/quarkus/referencias/run.sh).
2. Substitua o *script* existente.
3. Vá para a pasta raiz do seu projeto e execute o comando **chmod +x ./run/run.sh** para certificar que o arquivo possui permissão de execução. 

## Passo 6: Configurar o Maven Wrapper

1. Abra a raiz do projeto na sua IDE.
2. Localize o [arquivo Maven](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Crie ou modifique o arquivo **.mvn/wrapper/maven-wrapper.properties** conforme abaixo:

```properties
distributionUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/apache-maven/3.8.4/apache-maven-3.8.4-bin.zip
wrapperUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar  
 
```
4. Deixe uma linha em branco no fim do arquivo, fazendo o total de 3 linhas. 
5. Apague o arquivo **.jar** na mesma pasta, se houver.

## Passo 7: Atualizar o docker-compose

1. Localize o arquivo [docker-compose](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md) na sua IDE. Geralmente ele está na pasta **run**.
2. Substitua o arquivo antigo pelo [arquivo docker](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/docker-compose.yaml) do diretório **referencias**. 
3. Substitua o termo **nome-aplicacao** pelo nome da sua aplicação em todas as referências presentes entre a linha 3 até a linha 33.

> :information_source: **Observação** 
> 
> O novo YAML foi simplificado e possui duas imagens para a sua aplicação, uma do Curió e outra do Jaeger. Você pode adicionar outras conforme sua necessidade. Para visualizar o *tracing* da aplicação, acesse o endereço [localhost:16686](localhost:16686).

Após aplicar as atualizações nos arquivos mencionados, você finaliza a atualização do Quarkus. Como resultado, seu projeto será aprimorado, proporcionando acesso a novos recursos e melhorias de desempenho. Além disso, sua aplicação será mais confiável, uma vez que a atualização inclui correções de bugs.

> :grey_exclamation: **Importante** 
> 
> O OpenTracing não é mais mantido pela CNCF e por esse motivo o Quarkus vem deixando de oferecer suporte, conforme lança versões futuras. A partir da versão 1.13.0.Final, o Quarkus recomenda o uso da extensão OpenTelemetry para tracing (rastreamento). Veja [como migrar do OpenTracing para o OpenTelemetry](https://pt.quarkus.io/guides/telemetry-opentracing-to-otel-tutorial).  

## Passo 8: Atualizar a dependência de teste

Nas versões mais recentes do Quarkus, há uma extensão do JaCoCo que substitui toda a configuração do pom.xml. Para funcionar na esteira, é necessário que o nome do arquivo de saída do JaCoCo seja ajustado.

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo **pom**.
3. Ajuste o **artifactId**.

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jacoco</artifactId>
      <scope>test</scope>
    </dependency>
```

Para o Sonar conseguir ler o resultado do relatório gerado, e consequentemente conseguir a pontuação do motor de liberação, é necessário alterar o **application.properties**.

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo application.properties.
3. Altere o nome do arquivo com o relatório da cobertura para **jacoco.exec**. 

```properties
#jacoco
quarkus.jacoco.report-location=site/jacoco
quarkus.jacoco.data-file=jacoco.exec
```

<!-- De forma alternativa, caso queira manter o padrão de pastas definido pelo plugin *quarkus.jacoco*, é possível informar ao Sonar o caminho do relatório.

Também é possível informar ao Sonar os pacotes que devem ser excluidos da estatística de cobertura, como por exemplo, o pacote **/integration/**, que já vem no projeto padrão do bb-dev, ou pastas que só contêm POJOs ou DTOs. Essa abordagem é útil ao considerar a cobertura de código, focando apenas nas classes relevantes que realmente precisam ser testadas. Isso evita a criação de testes desnecessários que cobrem, por exemplo, *getters, setters, equals* e *hashcode*.

```xml
    <properties>
        ...
        <sonar.coverage.jacoco.xmlReportPaths>${project.build.directory}/jacoco-report/jacoco.xml</sonar.coverage.jacoco.xmlReportPaths>
        <sonar.coverage.exclusions>**/integration/*</sonar.coverage.exclusions>
        ...
    </properties>
``` -->

## Passo 9: Atualizar a biblioteca e os endpoints de monitoração

### B5 quarkus monitor

> :warning: **Atenção** 
> 
> Ao mudar a versão da biblioteca, podem ocorrer mudanças nas métricas geradas pela aplicação. Faça os testes no Prometheus e Grafana antes de subir para produção.

A monitoração das aplicações continua através da biblioteca [quarkus-monitor](https://github.com/labbsr0x/quarkus-monitor), agora na versão 0.3.0 e hospedada no Github do Labbs. O *quarkus-monitor* mais recente utiliza o [Micrometer](https://micrometer.io/docs/registry/prometheus) para geração das métricas.

Para atualizar a versão da biblioteca:

1. Abra o arquivo **pom.xml** na sua IDE.
2. Nas dependências, localize a tag  **version** do labbs.
3. Inclua a versão **0.3.0**.

```xml
<dependency>
  <groupId>br.com.labbs</groupId>
  <artifactId>quarkus-monitor</artifactId>
  <version>0.3.0</version>
</dependency>
```

> :grey_exclamation: **Importante** 
> 
> Nas versões mais recentes do Quarkus, subir dois provedores de métricas causa erro. Então, se o seu projeto utiliza o Microprofile Metrics e você adicionar a métrica **quarkus-monitor:0.3.0**, terá que remover a métrica **smallrye-metrics**.  Remova também a **smallrye-fault-tolerance** e a **dev-dx-quarkus-ext** porque elas importam a **smallrye-metrics** transitivamente. Se possível, analise suas outras dependências para garantir a unicidade dos provedores de métricas.

### Métricas e Health

Nas novas versões do Quarkus, os endpoints automáticos para verificar o estado da aplicação estão localizados no caminho **/q**. Por exemplo, temos o **/q/health** e o **/q/metrics**. Quando você faz uma solicitação GET para o **/q/health**, o Quarkus responde fornecendo informações sobre o estado da aplicação. Isso inclui verificar se a aplicação está em execução e se todos os serviços essenciais estão funcionando corretamente. Já quando uma solicitação GET é feita para o **/q/metrics**, o Quarkus responde fornecendo uma série de métricas. Essas métricas podem incluir detalhes sobre o uso de CPU, memória, tempos de resposta de solicitações HTTP, contagem de solicitações, e muito mais.

> :bulb: **Dica** 
> 
> Leia o texto [Path resolution in Quarkus](https://pt.quarkus.io/blog/path-resolution-in-quarkus/) se quiser saber mais sobre como determinar o caminho ou rota corretos para recursos, endpoints ou arquivos em um aplicativo Quarkus. 

Para remover o **/q** do *path*, mantendo a compatibilidade anterior:

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o **arquivo application.properties**.
3. Inclua a entrada **quarkus.http.non-application-root-path=/**.

> :grey_exclamation: **Importante** 
> 
> É importante ressaltar que caso nenhum ajuste seja feito no **application.properties**, e você mantenha o novo padrão Quarkus de endpoints de monitoração com **/q**, você deve ajustar o **values.yaml** de cada ambiente nas seções **livenessProbe**, **readinessProbe** e **serviceMonitor**.

**Tags:** #quarkus #atualizar #versao2 

## A Seguir

* Leia o roteiro [Como atualizar o Curió com o Quarkus 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Curio.md) para continuar o processo de atualização.
* Leia o roteito [Como atualizar a dependência do Banco de Dados - Quarkus 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD.md) para atualizar a dependência do Banco de Dados após a atualização do seu projeto para a versão 2 do Quarkus.
* Acesse o [Projeto de exemplo do Quarkus](https://fontes.intranet.bb.com.br/dev/publico/exemplos/quarkus-exemplo/-/blob/develop/README.md) para conhecer mais sobre o projeto base.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2.md&internalidade=frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2)
