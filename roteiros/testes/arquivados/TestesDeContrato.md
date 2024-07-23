> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/TestesDeContrato.md&amp;action_name=testes/TestesDeContrato)

# Testes de contrato

Este roteiro lista as implementações para testes de contrato (API) utilizando a arquitetura de microsserviços.

## KumuluzEE

### Requisitos
Para a implementação no framework utilizado pelo BBDev você deve atender alguns requisitos. Sua máquina deve possuir instalado e configurado:

- Docker
- Java 11
- Maven

### Dependências

Devem ser feitas algumas alterações no arquivo pom.xml do seu projeto para inclusão de dependências e configurações.

O KumuluzEE faz uso do *KumuluzEE Arquillian Container Adapter* que inicia um servidor antes de rodar os testes para que os testes possam interagir com um ambiente próximo do utilizado em produção.

Dependência do KumuluzEE Arquillian Container:
```xml
<dependency>
    <groupId>com.kumuluz.ee.testing</groupId>
    <artifactId>kumuluzee-arquillian-container</artifactId>
    <version>${kumuluzee-arquillian-container.version}</version>
    <scope>test</scope>
</dependency>
```
Para usar o framework Arquilian, adicione o BOM Arquilian como gerenciador de dependências:
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.jboss.arquillian</groupId>
            <artifactId>arquillian-bom</artifactId>
            <version>${arquillian.version}</version>
            <scope>import</scope>
            <type>pom</type>
        </dependency>
    </dependencies>
</dependencyManagement>
```
Adicione o JUnit ao seu projeto:
```xml
<dependency>
    <groupId>org.jboss.arquillian.junit</groupId>
    <artifactId>arquillian-junit-container</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>${junit.version}</version>
    <scope>test</scope>
</dependency>
```

### Escrevendo testes

o KumuluzEE Arquilian COntainer Adapter suporta CDI e  Arquillian Resource injectio. Segue o exemplo usado pelo BBDev:

```java
@RunWith(Arquillian.class)
public class UsuarioResourceTest {
    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addPackages(true, "${groupId}")
                .addAsResource("config.yaml", "config.yaml")
                .addAsResource("test-persistence.xml",
                        "META-INF/persistence.xml")
                .addAsResource("db_queries/CONSULTAR_USUARIO.xml",
                        "db_queries/CONSULTAR_USUARIO.xml")
                .addAsResource("db_queries/CONSULTAR_USUARIO_ID.xml",
                        "db_queries/CONSULTAR_USUARIO_ID.xml")
                .addAsResource("db_queries/EXCLUIR_USUARIO.xml",
                        "db_queries/EXCLUIR_USUARIO.xml")
                .addAsResource("db_queries/ATUALIZAR_USUARIO.xml",
                        "db_queries/ATUALIZAR_USUARIO.xml")
                .addAsResource("db_queries/INSERIR_USUARIO.xml",
                        "db_queries/INSERIR_USUARIO.xml")

                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
    }

    @Test
    public void testPostUser() throws ParseException {
        Usuario usuario = new Usuario();
        usuario.setNome("José");
        usuario.setId(100);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Date date = (Date)formatter.parse("25/04/1982");

        usuario.setNascimento(date);
        final Response response = createTarget()
                .path("/v1/usuario")
                .request(MediaType.APPLICATION_JSON).post(Entity.json(usuario));
        Assert.assertEquals(201, response.getStatus());
    }

    @Test
    public void testGetAllUser(){
        final Response response = createTarget()
                .path("/v1/usuario")
                .request(MediaType.APPLICATION_JSON).get();
        Assert.assertEquals(200, response.getStatus());
    }

    private WebTarget createTarget(){
        Client client = ClientBuilder.newClient();
        return client.target("http://localhost:9090");
    }
}

```

## Quarkus

### Requisitos

- JDK 1.8 ou superior com a variável de ambiente JAVA_HOME configurada corretamente;
- Apache Maven 3.5.3 ou superior;

Uma solução pode ser vista no próprio git do projeto [Quarkus](https://github.com/quarkusio/quarkus-quickstarts) na pasta `getting-started-testing`.

### Dependências

Verifique se o arquivo pom.xml possui as dependências abaixo. A `quarkus-junit5` é necessário para os testes pois fornece a notação @QuarkusTest que controla o framework de testes. O uso do `rest-assured` não é obrigatório, mas recomendado por facilitar as assertivas e testes HTTP.

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-junit5</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>io.rest-assured</groupId>
    <artifactId>rest-assured</artifactId>
    <scope>test</scope>
</dependency>
```
Por usar JUnit 5, A versão do [Surefire Maven Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/) deve ser setado, pois a versão default não é suportada. Também foi setado o logmannager para garantir que os logs estejam configurados corretamente.

### Escrevendo testes
```java
package org.acme.quickstart;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("hello"));
    }

    @Test
    public void testGreetingEndpoint() {
        String uuid = UUID.randomUUID().toString();
        given()
          .pathParam("name", uuid)
          .when().get("/hello/greeting/{name}")
          .then()
            .statusCode(200)
            .body(is("hello " + uuid));
    }

}
```

## Saiba mais 
Para conhecer mais sobre o tema acesse as documentações:
- [Kumuluzee](https://github.com/kumuluz/kumuluzee-testing/tree/master/kumuluzee-arquillian-container)
- [Quarkus](https://quarkus.io/guides/getting-started-testing)

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/TestesDeContrato.md&internalidade=testes/TestesDeContrato)
 

