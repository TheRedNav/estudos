> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/CoberturaIncorreta.md&amp;action_name=testes/CoberturaIncorreta)
# Cobertura de testes em aplicações Quarkus

## Aplicação
Esse roteiro se estende a todas as aplicações Java/Quarkus geradas a partir do Brave e que utilizam a pipeline de construção Java no Jenkins

## TL;DR
Ao executar `mvn verify`, sua aplicação precisa ter o arquivo `site/jacoco/jacoco.xml` dentro da pasta `target`. Caso isso não aconteça, o Sonar não conseguirá obter a cobertura de código dos testes, ainda que seja possível ver a cobertura de outras formas.

## JaCoCo

O JaCoCo (Java Code Coverage) é o plugin que usamos para calcular a cobertura de código. Ele é uma dependência da aplicação definida no `pom.xml` e após qualquer comando Maven que execute os testes, gera um novo relatório. A versão visual do relatório pode ser acessada em  `/target/site/jacoco/index.html`.

## Diagnóstico

Se rodando o comando `mvn verify` não conseguir encontrar o relatório, verifique se a aplicação possui pelo menos uma classe de teste com pelo menos um teste usando a anotação @QuarkusTest, e se está usando a configuração abaixo em seu arquivo application.properties de teste(`src/test/resources`):

```xml
quarkus.jacoco.report-location=site/jacoco
```

## Quarkus 2.x

Para a cobertura ser corretamente medida no Quarkus 2 é necessário:

### 1. Ter a dependência correta no escopo correto
```
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jacoco</artifactId>
      <scope>test</scope>
    </dependency>
```
### 2. Plugin configurado dentro da seção build do pom.xml:
```
      <plugin>
        <groupId>org.jacoco</groupId>
        <artifactId>jacoco-maven-plugin</artifactId>
        <version>${jacoco.version}</version>
        <executions>
          <execution>
            <id>default-prepare-agent</id>
            <goals>
              <goal>prepare-agent</goal>
            </goals>
            <configuration>
              <exclClassLoaders>*QuarkusClassLoader</exclClassLoaders>
              <destFile>${project.build.directory}/jacoco-quarkus.exec</destFile>
              <append>true</append>
            </configuration>
          </execution>
          <execution>
            <id>default-prepare-agent-integration</id>
            <goals>
              <goal>prepare-agent-integration</goal>
            </goals>
            <configuration>
              <exclClassLoaders>*QuarkusClassLoader</exclClassLoaders>
              <destFile>${project.build.directory}/jacoco-quarkus.exec</destFile>
              <append>true</append>
            </configuration>
          </execution>
        </executions>
      </plugin>

```
### 3. Ajuste do caminho do relatório para ser lido pelo Sonar no arquivo src/test/resources/application.properties:
```
# JaCoCo
quarkus.jacoco.report-location=site/jacoco
quarkus.jacoco.data-file=jacoco.exec

```

Se, após adicionar essa configuração, a cobertura não estiver sendo refletida na pasta `target/site/jacoco` (você pode verificar através do relatório visual em `/target/site/jacoco/index.html`), pode ser necessário alterar a propriedade abaixo no arquivo application.properties como mostrado:

```
quarkus.jacoco.data-file=jacoco-quarkus.exec
```


## Quarkus 1.x
Se o relatório não estiver refletindo as classes de testes pode ser um problema na configuração de coleta do jacoco, para corrigir isso precisa alterar o seu pom.xml para realizar essa cobertura, isso foi descrito na documentação do [quarkus](https://quarkus.io/version/1.11/guides/tests-with-coverage#the-coverage-does-not-seem-to-correspond-to-the-reality).

Resumindo voce vai precisar incluir a dependencia do jacoco agent, assim adicione o trecho abaixo dentro da tag `<dependencies>`

```xml
        <dependency>
            <groupId>org.jacoco</groupId>
            <artifactId>org.jacoco.agent</artifactId>
            <classifier>runtime</classifier>
            <scope>test</scope>
            <version>${jacoco.version}</version>
        </dependency>
```

Depois alterar a tag `<build>` e `<profile>` do seu pom.xml conforme abaixo

Build
```xml
    <build>
        <plugins>
            <plugin>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>${surefire-plugin.version}</version>
                <configuration>
                    <excludedGroups>integration</excludedGroups>
                    <systemPropertyVariables>
                        <jacoco-agent.destfile>${project.build.directory}/jacoco-ut.exec</jacoco-agent.destfile>
                        <java.util.logging.manager>org.jboss.logmanager.LogManager</java.util.logging.manager>
                        <maven.home>${maven.home}</maven.home>
                    </systemPropertyVariables>
                </configuration>
                <executions>
                    <execution>
                        <id>integration-tests</id>
                        <phase>integration-test</phase>
                        <goals>
                            <goal>test</goal>
                        </goals>
                        <configuration>
                            <excludedGroups>!integration</excludedGroups>
                            <groups>integration</groups>
                            <systemPropertyVariables>
                                <jacoco-agent.destfile>${project.build.directory}/jacoco-it.exec</jacoco-agent.destfile>
                            </systemPropertyVariables>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>io.quarkus</groupId>
                <artifactId>quarkus-maven-plugin</artifactId>
                <version>${quarkus-plugin.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>            
            <!-- Relatorio do Jacoco para coverage -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
                <version>${jacoco.version}</version>
                <executions>
                    <execution>
                        <id>instrument-ut</id>
                        <goals>
                            <goal>instrument</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>restore-ut</id>
                        <goals>
                            <goal>restore-instrumented-classes</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report-ut</id>
                        <goals>
                            <goal>report</goal>
                        </goals>
                        <configuration>
                            <dataFile>${project.build.directory}/jacoco-ut.exec</dataFile>
                            <outputDirectory>${project.reporting.outputDirectory}/jacoco-ut</outputDirectory>
                        </configuration>
                    </execution>
                    <execution>
                        <id>instrument-it</id>
                        <phase>pre-integration-test</phase>
                        <goals>
                            <goal>instrument</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>restore-it</id>
                        <phase>post-integration-test</phase>
                        <goals>
                            <goal>restore-instrumented-classes</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>report-it</id>
                        <phase>post-integration-test</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                        <configuration>
                            <dataFile>${project.build.directory}/jacoco-it.exec</dataFile>
                            <outputDirectory>${project.reporting.outputDirectory}/jacoco-it</outputDirectory>
                        </configuration>
                    </execution>
                    <execution>
                        <id>merge-results</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>merge</goal>
                        </goals>
                        <configuration>
                            <fileSets>
                                <fileSet>
                                    <directory>${project.build.directory}</directory>
                                    <includes>
                                        <include>*.exec</include>
                                    </includes>
                                </fileSet>
                            </fileSets>
                            <destFile>${project.build.directory}/jacoco.exec</destFile>
                        </configuration>
                    </execution>
                    <execution>
                        <id>post-merge-report</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>report</goal>
                        </goals>
                        <configuration>
                            <dataFile>${project.build.directory}/jacoco.exec</dataFile>
                            <outputDirectory>${project.reporting.outputDirectory}/jacoco</outputDirectory>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
```

Profile
```xml
    <profiles>
        <profile>
            <id>native</id>
            <activation>
                <property>
                    <name>native</name>
                </property>
            </activation>
            <build>
                <plugins>
                    <plugin>
                        <artifactId>maven-failsafe-plugin</artifactId>
                        <version>${surefire-plugin.version}</version>
                        <executions>
                            <execution>
                                <goals>
                                    <goal>integration-test</goal>
                                    <goal>verify</goal>
                                </goals>
                                <configuration>
                                    <systemPropertyVariables>
                                        <native.image.path>
                                            ${project.build.directory}/${project.build.finalName}-runner
                                        </native.image.path>
                                        <java.util.logging.manager>org.jboss.logmanager.LogManager
                                        </java.util.logging.manager>
                                        <maven.home>${maven.home}</maven.home>
                                    </systemPropertyVariables>
                                </configuration>
                            </execution>
                        </executions>
                    </plugin>
                </plugins>
            </build>
            <properties>
                <quarkus.package.type>native</quarkus.package.type>
            </properties>
        </profile>
    </profiles>
```

Geralmente as propriedades definidas ja devem fazer parte do seu pom.xml voce pode verificar se voce possui elas na tag `<properties>`

Properties e versoes
```xml
        <compiler-plugin.version>3.8.1</compiler-plugin.version>
        <quarkus-plugin.version>1.7.3.Final</quarkus-plugin.version>
        <quarkus.platform.version>1.7.3.Final</quarkus.platform.version>
        <surefire-plugin.version>2.22.1</surefire-plugin.version>
        <jacoco.version>0.8.4</jacoco.version>
```

Atenção para a versao do quarkus, nesse exemplo estamos usando a versao 1.7.3.Final, voce pode usar qualquer versão do quarkus 1 de acordo com sua necessidade.

Para versoes acima da 1.7.3 pode se utilizar as seguintes versões

```xml
    <surefire-plugin.version>3.0.0-M5</surefire-plugin.version>
    <jacoco.version>0.8.7</jacoco.version>
```


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/CoberturaIncorreta.md&internalidade=testes/CoberturaIncorreta)
