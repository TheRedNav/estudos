> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/atualizacao-quarkus-3.md&amp;action_name=frameworks/quarkus/atualizacao-quarkus-3)

# Atualizando o Quarkus 2 para o Quarkus 3

Este roteiro destina-se a desenvolvedores Cloud e Quarkus que possuem aplicações rodando na versão 2 do Quarkus e desejam atualizá-las para a versão 3. Esta atualização proporcionará o uso de versões mais confiáveis para suas soluções.

## Nota sobre a atualização

> :information_source: Apesar do passo a passo a seguir, alguns problemas de compatibilidade podem ocorrer, uma vez que cada aplicação tem suas particularidades e libs diferentes. Recomendamos que, para resolvê-los, consulte a documentação do Quarkus e utilize uma boa IDE, como Eclipse ou IntelliJ, para resolver esses problemas mais facilmente.

## Requisitos

- Docker;
- Linux ou WSL devidamente configurados;
- Java localmente instalado na versão 17 ou 21;
- Maven localmente instalado na versão 3.9.5 ou superior;
- Arquivo settings.xml do maven devidamente configurados em ~/.m2;
- Um projeto rodando com o Quarkus 2

Caso esteja utilizando Quarkus 1, veja o roteiro abaixo antes:

[Como atualizar o Quarkus para a versão 2
](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2.md)

## Nota sobre a transição do pacote `javax` para `jakarta`

Com a atualização do Quarkus 3, é importante observar que houve uma mudança no pacote das APIs Java EE de javax para jakarta. Isso ocorreu como parte da transição do Java EE para o Jakarta EE, após a transferência da manutenção e desenvolvimento do Java EE para a Eclipse Foundation.

Ao atualizar sua aplicação para o Quarkus 3, você pode encontrar referências ao pacote javax em seu código-fonte, especialmente em bibliotecas de terceiros. Certifique-se de atualizar essas referências para usar o pacote jakarta correspondente, conforme necessário.

Além disso, verifique as dependências do seu projeto para garantir que todas as dependências que anteriormente utilizavam javax tenham sido atualizadas para versões compatíveis com o jakarta.

Essa mudança é crucial para garantir a compatibilidade da sua aplicação com o Quarkus 3 e futuras versões, alinhando-a com os padrões e desenvolvimentos mais recentes do Jakarta EE.

## Passo 1: Atualizar dependências e configs do pom.xml

- Atualize, no `pom.xml`, **caso possua**, as libs `dev-java-erro`, `dev-java-erro-core`  e `dev-java-erro-tracer` para as versões `2.1.1`, `2.1.0` e `3.0.0-SNAPSHOT`, respectivamente.
```
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-java-erro</artifactId>
  <version>2.1.1</version>
</dependency>
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-java-erro-core</artifactId>
  <version>2.1.0</version>
</dependency>
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-java-erro-tracer</artifactId>
  <version>3.0.0-SNAPSHOT</version>
</dependency>
```
> :information_source: **Nota!** Aplicações que fazem Tracing e utilizam a lib dev-java-erro-tracer, para realizar o upgrade para o quarkus 3, é preciso fazer uso da versão `3.0.0-SNAPSHOT`, que está com o Opentelemetry, já que o Opentrace não é mais suportado.

Depois, verifique as seguintes bibliotecas e propriedades (ou versões) no `pom.xml`, atualizando-as:
```
<compiler-plugin.version>3.12.1</compiler-plugin.version>
<failsafe.useModulePath>false</failsafe.useModulePath>
<maven.compiler.release>21</maven.compiler.release>
<project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
<project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
<quarkus.platform.artifact-id>quarkus-bom</quarkus.platform.artifact-id>
<quarkus.platform.group-id>com.redhat.quarkus.platform</quarkus.platform.group-id>
<quarkus.platform.version>3.8.4.redhat-00002</quarkus.platform.version>
<surefire-plugin.version>3.2.5</surefire-plugin.version>
<wiremock.version>3.0.1</wiremock.version>
<jacoco.version>0.8.11</jacoco.version>
<io.jsonwebtoken.jjwt.version>0.11.5</io.jsonwebtoken.jjwt.version>
<com.auth0.java-jwt.version>3.19.4</com.auth0.java-jwt.version>
```

Ainda no `pom.xml`, verifique as `dependências do Quarkus` e prováveis modificações ( 2 -> 3 ), conforme abaixo:

- Dependências padrão para Quarkus 2:
```
<dependency>
  <groupId>br.com.labbs</groupId>
  <artifactId>quarkus-monitor</artifactId>
  <version>0.3.0</version>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-jackson</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-opentracing</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-client</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
<dependency>
  <groupId>com.github.tomakehurst</groupId>
  <artifactId>wiremock-jre8</artifactId>
  <scope>test</scope>
  <version>${wiremock.version}</version>
</dependency>
```

- Novas dependências padrão para Quarkus 3:
```
<dependency>
  <groupId>br.com.labbs</groupId>
  <artifactId>quarkus-monitor-reactive</artifactId>
  <version>2.0.0</version>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-client-reactive-jackson</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-rest-client-reactive</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-openapi</artifactId>
</dependency>
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-smallrye-health</artifactId>
</dependency>
  <dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-micrometer-registry-prometheus</artifactId>
</dependency>
  <dependency>
  <groupId>org.wiremock</groupId>
  <artifactId>wiremock</artifactId>
  <scope>test</scope>
  <version>${wiremock.version}</version>
</dependency>
```

> :exclamation: Observe que, uma aplicação que utilize o Opentrace, por exemplo, precisa remover todas as dependências relacionadas a ele e adicionar as dependências do `Opentelemetry` abaixo para fazer a migração.
```
<dependency>
  <groupId>io.quarkus</groupId>
  <artifactId>quarkus-opentelemetry</artifactId>
</dependency>
<dependency>
  <groupId>io.opentelemetry</groupId>
  <artifactId>opentelemetry-extension-trace-propagators</artifactId>
</dependency>
```

> :exclamation: **Obs.:** Caso sua aplicação não faça Tracing, podem ser retiradas as libs do Opentracing e não há necessidade de adicionar o Opentelemetry. Além disso, para evitar Warnings indesejados no terminal, retire, também o `Jaeger` do docker-compose,yaml e qualquer resquício de tracing das classes.

## Passo 2: Configurar o Maven

Certifique-se de que o arquivo `/.mvn/wrapper/maven-wrapper.properties`, esteja com as seguintes configurações:

```
distributionUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/apache-maven/3.9.5/apache-maven-3.9.5-bin.zip
wrapperUrl=https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
```

- Além disso, vale a pena checar possíveis modificações necessárias em arquivos maven conforme os modelos abaixo:

[/.mvn/wrapper/MavenWrapperDownloader.java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/MavenWrapperDownloader.java)

[/mvnw](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/mvnw)

[/mvnw.cmd](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/mvnw.cmd)

## Passo 3: Atualizar os arquivos da pasta run

- Para otimizar as aplicações da nuvem do BB, foram feitas algumas mudanças em arquivos usados para rodar o projeto. Confira abaixo:

[/run/docker-compose.yaml](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/docker-compose.yaml)

[/run/run.sh](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/run.sh)

[/run/update-run.sh](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/frameworks/quarkus/ref_quarkus3/update-run.sh)

> :warning: O docker-compose.yaml acima não posssui Jaeger. Portanto, caso faça Tracing e utilize Opentelemetry, será preciso configurá-lo. Lembrando que não há suporte para Jaeger em aplicações que rodam no Openshift.

## Passo 4: imports da lib dev-java-erro

Os `imports` da dev-java-erro passam a ser da seguinte maneira:

```
import br.com.bb.dev.erros.core.exceptions.BBRuntimeException;
import br.com.bb.dev.erros.core.model.IncluirNaListaErros;
import br.com.bb.dev.erros.core.Constantes;
import br.com.bb.dev.erros.tracer.TracedWithRequestID;
import br.com.bb.dev.erros.rest.filter.curio.CurioExceptionMapper;
```

## Passo 5: Alterar propriedades

Adicione aos arquivos `application.properties`, do main e do test, as propriedades abaixo:

```
quarkus.jackson.fail-on-empty-beans=false

quarkus.index-dependency.dev-java-erro.group-id=br.com.bb.dev

quarkus.index-dependency.dev-java-erro.artifact-id=dev-java-erro
```

Em seguida, somente no `application.properties` do main, atualize a seguinte propriedade:

```
mp.openapi.filter=br.com.bb.dev.erros.rest.filter.openapi.OpenApiFilter
```

Agora, somente no `application.properties` do test, confira se as propriedades do jacoco estão conforme abaixo:

- Para Java 21:
```
quarkus.jacoco.report-location=target/site/jacoco
quarkus.jacoco.data-file=target/jacoco-quarkus.exec
```

- Para Java 17:
```
quarkus.jacoco.report-location=site/jacoco
quarkus.jacoco.data-file=jacoco-quarkus.exec
```

## Passo 6: Atualizar Dockerfile e Jenkinsfile

- Altere a linha a seguir do `Dockerfile`:
```
docker.binarios.intranet.bb.com.br/bb/dev/dev-java:VERSAO_DESEJADA
```
> :information_source: Em `VERSÃO_DESEJADA`, coloque uma das versões compiladas das imagens dev-java da devCloud (Ex.: `21.3.4`). Todas as versões compiladas podem ser vistas [neste link](https://binarios.intranet.bb.com.br/artifactory/docker-bb-local/bb/dev/dev-java/). Para saber a versão mais atual, consulte também o [portal devCloud](https://cloud.dev.intranet.bb.com.br/stacks-desenvolvimento)

- Em seguida, altere o `Jenkinsfile`:
```
nomePod = 'jdk21'
```

> :exclamation: Caso opte por alguma versão 17 do java, e não pela 21 (a mais atual suportada pelo ambiente Cloud BB), lembre-se de equalizar no `pom.xml` o `<maven.compiler.release>`, no `Dockerfile` a imagem java e no `Jenkinsfile` a linha acima.

## Passo 7: Atualizar ambiente via Docker

Execute o seguinte comando via terminal na raiz do projeto:

```
docker run -it --rm -v $HOME/.m2/:/home/default/.m2/ -v $HOME/.m2/:/root/.m2/ -v /var/run/docker.sock:/var/run/docker.sock -v /usr/bin/docker:/usr/bin/docker -v $(pwd):/app --network host -u 0 docker.binarios.intranet.bb.com.br/bb/dev/dev-java:21.3.4 /bin/sh -c "mkdir -p /root/.quarkus && cat > /root/.quarkus/config.yaml << EOF
registries:
- registry.quarkus.redhat.com
- registry.quarkus.io

EOF
cd /app
./mvnw com.redhat.quarkus.platform:quarkus-maven-plugin:3.8.4.redhat-00002:update -N -U"
```

## Passo 8: rodar o projeto localmente

Rode os comandos abaixo em sequência após fazer as mudanças acima:
```
mvn clean install
mvn verify
```

Por fim, rode a aplicação pelo run.sh ou como preferir para garantir que está tudo funcionando corretamente.

## Nota final

É importante ressaltar que a atividade de atualização é complexa, requer atenção e domínio técnico das tecnologias envolvidas, principalmente quando se trata de versões MAJOR, pois quase sempre ocorrem quebras de compatibilidade. Trata-se, também, de algo bastante relevante em decorrência de correções de vulnerabilidades e de disponibilização de novas features que contribuem para melhor desenvolvimento e intregação das aplicações como um todo.

Caso identifique algum erro, abra uma issue [aqui](https://fontes.intranet.bb.com.br/dev/publico/developer-stacks/-/issues).

> :bulb: **Dica!** Para verificar tudo de mais atual nas Stacks da devCloud, você pode gerar um Código Base pelo [Brave](https://brave.dev.intranet.bb.com.br/) e compará-lo com a sua aplicação.


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/atualizacao-quarkus.md&internalidade=frameworks/quarkus/atualizacao-quarkus)
