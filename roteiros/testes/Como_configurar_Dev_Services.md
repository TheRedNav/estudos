> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_dev_services.md&amp;action_name=testes/Como_configurar_dev_services)

# Como configurar Dev Services

Este roteiro ensina a configurar e utilizar a funcionalidade [Dev Services](https://pt.quarkus.io/guides/dev-services) (Testcontainers) em aplicações Java Quarkus para integrar com bancos de dados rodando em contêineres. Essa funcionalidade permite que você inicie serviços de infraestrutura localmente durante o desenvolvimento e/ou testes. Sua principal vantagem é a semelhança entre o ambiente de testes de integração e o de produção.

> :warning: **Atenção** 
> 
> Essa abordagem tende a ser consideravelmente mais lenta.  Ao utilizá-la, é recomendável separar a execução dos testes de unidade e integração para otimizar o processo de desenvolvimento e teste.

## Requisitos
* Verificar o funcionamento das pipelines.
> :grey_exclamation: **Importante** 
> 
> O arquivo **testcontainers.properties** fica no diretório **home**. Se diferentes partes do projeto tentarem usar contêineres para serviços como bancos de dados, enquanto outras partes não configuram isso corretamente, podem surgir problemas de compatibilidade.

## Passo 1: Configurar o pom.xml

É necessário adicionar configurações para integrar o plugin de execução de testes de integração e verificação de integridade. 

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo **pom.xml**.
3. Procure pelo plugin e inclua o trecho a seguir. O plugin está configurado para executar testes cujos arquivos terminem com **IT.java**.

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

## Passo 2: Atualizar o application.properties

1. Na sua IDE, abra a pasta **/src/main/resources**.
2. Localize o arquivo **application.properties**.
3. Adicione as informações para configurar o banco de dados e habilitar o Dev Services. Nesse exemplo, as configurações de banco de dados foram comentadas e substituídas por configurações para o banco de dados DB2.

```properties
quarkus.datasource.db-kind=<oracle|db2>
quarkus.datasource.devservices.enabled=true
quarkus.datasource.devservices.init-script-path=scripts/init.sql 
quarkus.datasource.health.enabled=true
```

## Passo 3: Aceitar licença de imagem
 
1. No diretório **test/resources**, crie um arquivo chamado **container-license-acceptance.txt**.
2. Inclua a URL:
    * Para o DB2: **docker.io/ibmcom/db2:11.5.7.0a**. 
    * Para o Oracle: **docker.io/oraclelinux:9**.

> :bulb: **Dica** 
> 
> Além das propriedades acima, pode-se indicar explicitamente qual imagem utilizar através da propriedade
`quarkus.datasource.devservices.> image=<image>:<tag>`.

## Passo 4: Adicionar os scripts de inicialização para testes

Para os exemplos do passo 4 funcionarem, é necessário adicionar um script de inicialização.

1. Na sua IDE, vá para **src  > test > resources > scripts**.
2. Crie o arquivo **init.sql** e inclua as informações do banco dados.

**Exemplo de código**

```sql
CREATE SEQUENCE student_seq START WITH 1 INCREMENT BY 1 NOCACHE;
CREATE TABLE MY_STUDENTS (
    ID INT NOT NULL,
    NAME VARCHAR(100) NULL
);
```

## Passo 5: Adicionar arquivos para testes

Foram adicionados os arquivos de teste **DummieTest.java** e **HelloWorldResourceIT.java** para demonstrar testes de unidade e integração, respectivamente.

**Exemplo do arquivo de teste DummieTest.java:**

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

**Exemplo de código do arquivo de teste HelloWorldResourceIT.java:**

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
> :information_source: **Observação** 
> 
> A anotação **@QuarkusTest** habilita o contexto de injeção de dependência (DI), incluindo o Dev Services para bancos de dados. Com isso, testes de integração podem ser realizados garantindo a disponibilidade do banco de dados configurado.

## Passo 6: Executar testes

Para executar testes de integração, utilize o comando `mvn verify`. <br>
<br>
Para executar testes de unidade, utilize o comando `mvn test`.

> :bulb: **Dica** 
> 
> Para reutilizar test containers, adicione `testcontainers.reuse.enable=false` no arquivo **~/.testcontainers.properties**.

**Tags:** #testes #quarkus ##devservices

## A Seguir
Leia o guia [Dev Services For Databases](https://pt.quarkus.io/guides/databases-dev-services) para mais informações sobre o assunto.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_dev_services.md&internalidade=testes/Como_configurar_dev_services) 
