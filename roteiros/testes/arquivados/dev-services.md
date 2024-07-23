# Introdução
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/dev-services/dev-services.md&amp;action_name=dev-services/dev-services)

Este roteiro descreve como configurar e utilizar Dev Services (test containers) em aplicações Java Quarkus para integração a banco de dados rodando em container. Desta forma, o Dev Services permite que você inicie serviços de infraestrutura localmente durante o desenvolvimento e/ou teste.


## Alterações


### Alterações no `pom.xml`


É necessário adicionar configurações para integrar o plugin `maven-failsafe-plugin` para execução de testes de integração e verificação de integridade. O plugin é configurado para executar testes cujos arquivos terminam com `IT.java`.


Exemplo de configuração no `pom.xml`:




```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <executions>
        <execution>
          <goals>
              <goal>integration-test</goal>
              <goal>verify</goal>
         </goals>
       </execution>
    </executions>
    <configuration>
        <skipTests>false</skipTests>
        <includes>
            <include>**/*IT.java</include>
        </includes>
    </configuration>
 </plugin>


```


### Alterações no arquivo `application.properties`


São necessárias as seguintes modificações no arquivo `application.properties` para configurar o banco de dados e habilitar Dev Services. Nesse exemplo abaixo, as configurações de banco de dados foram comentadas e substituídas por configurações para o banco de dados DB2.
Recomendamos que se utilize o dev-services apenas em contexto de desenvolvimento e/ou testes /src/test/resources.


Exemplo de configuração no `application.properties`:




```properties
quarkus.datasource.db-kind=<oracle|db2>
quarkus.datasource.devservices.enabled=true
quarkus.datasource.devservices.init-script-path=scripts/init.sql quarkus.datasource.health.enabled=true`
```
Além das propriedades acima pode-se indicar explicitamente qual imagem utilizar através da propriedade abaixo:


```properties
quarkus.datasource.devservices.image=<image>:<tag>
```


### Aceite de licença de imagem DB2


No caso de DB2 deve-se criar o arquivo container-license-acceptance.txt no diretório resource com o conteúdo abaixo: 
```properties
docker.io/ibmcom/db2:11.5.7.0a
```


### Adição de arquivos para testes


Foram adicionados arquivos de teste `DummieTest.java` e `HelloWorldResourceIT.java` para demonstrar testes de unidade e integração, respectivamente.


Exemplo de código do arquivo de teste `DummieTest.java`:




```java
package br.com.bb.t99.rest;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
public class DummieTest {


@Test
public void dbTeste() {
    assertTrue(true);
    } 
}
```




Exemplo de código do arquivo de teste `HelloWorldResourceIT.java`:


A anotação `@QuarkusTest` habilita o contexto de injeção de dependência (DI), incluindo o Dev Services para bancos de dados. Com isso, testes de integração podem ser realizados, garantindo a disponibilidade do banco de dados configurado.


```java
@QuarkusTest


public class HelloWorldResourceIT {


    @Inject
    public EntityManager em;


    @Test
    public void dbTeste() {
        em.createNativeQuery("select 1 from dual").getFirstResult();
    }
}
```


### Adição de scripts de inicialização para testes


Para esse exemplo funcionar, é necessário adicionar um script de inicialização `init.sql` no diretório `src/test/resources/scripts` para ser utilizado durante os testes.


Exemplo de código do arquivo `init.sql`:




```sql
CREATE SEQUENCE student_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE MY_STUDENTS (
ID NUMBER(38,0) NOT NULL,
NAME VARCHAR2(100) NULL
);
```


## Execução de testes


- Para executar testes de integração, utilize o comando `mvn verify`.
- Para executar testes de unidade, utilize o comando `mvn test`.


## Reutilizar test containers


No arquivo abaixo  `~/.testcontainers.properties` adicione a seguinte linha:


```properties
testcontainers.reuse.enable=false
```


## Conclusão

Ao utilizar Dev Services, a principal vantagem é a proximidade do ambiente de testes de integração com o de produção. No entanto, essa abordagem tende a ser consideravelmente mais lenta. Portanto, recomenda-se explicitamente separar a execução dos testes de unidade e integração para otimizar o processo de desenvolvimento e teste.
Além disso, deve-se verificar como as pipelines estão funcionando pois o arquivo `testcontainers.properties` fica no diretório home e podem ocorrer problemas de compatibilidade se alguns tentarem utilizar containers e outros não. 
Este é um exemplo básico de como configurar e utilizar Dev Services em aplicações Java Quarkus. Adapte conforme necessário para o seu projeto específico.


