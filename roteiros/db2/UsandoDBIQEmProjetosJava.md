> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Plugin DBIQ para Maven 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/db2/UsandoDBIQEmProjetosJava.md&amp;action_name=db2/UsandoDBIQEmProjetosJava)

## 1. Descrição

O [DBIQ](https://www.infotel.com/us/mainframe-software/db-iq/) é a ferramenta utilizada pelo BB para verificar a performance das queries DB2 em programas que façam acesso a este antes de autorizar a execução destes em ambiente de produção. 

Está sendo desenvolvido na esteira de CI (Jenkins) um passo que executará o DBIQ para os microserviços desenvolvidos em Java que acessem o DB2, via JDBC. Este passo utilizará um plug-in maven que varre o código em busca das queries e as submete ao DBIQ através de um endpoint REST. 

É possível também executar o plugin na máquina do usuário, bastando para isto seguir as instruções contidas na seção seguinte. É recomendado que esta verificação seja feita antes do deploy de aplicações Java DB2 em ambiente de produção. **Aplicação devem ser baixadas apenas caso o return code do DBIQ seja menor ou igual a 4**.  

## 2. Configuração do Projeto

Incluir o plugin no pom.xml do projeto:

```xml
<build>
...
    <plugins>
    ...
        <plugin>
            <groupId>br.com.bb.dev</groupId>
            <artifactId>dev-dbiq-maven-plugin</artifactId>
            <version>0.2.0</version>
            <executions>
                <execution>
                    <goals>
                        <goal>dbiq</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
....
</build>
```

Em projetos Java utilizando **Kumuluzee**, para que a verificação seja efetuada, as queries devem ser definidas de forma estática e declaradas em arquivos XML.

No *persistence.xml* (localizado no diretório *\<projectPath\>/src/main/resources/META-INF/*) deve-se mapear os arquivos que contém as queries utilizando-se a tag *mapping-file*.


As queries devem estar em arquivo xml no seguinte formato: 

```xml
    <named-native-query name="QUERY_TESTE" result-class="br.com.bb.t99.model.TabelaTesteNaoExiste">
        <query><![CDATA[
			SELECT NR_CRT 
            FROM DB2T99.TABELA_TESTE_NAO_EXISTE 
            WHERE DATA > '01.01.2019'; 
        ]]></query>
        </query>
    </named-native-query>
```

O plugin irá processar esses arquivos e submeter cada query definida na tag *named-native-query* à validação do DBIQ.

Para projetos desenvolvidos em **Quarkus**, as queries devem ser colocadas utilizando a annotation `NamedNativeQuery`, conforme exemplo abaixo. Neste caso, não é necessário o arquivo `persistence.xml`. 

```java
@Entity
@Table(name="USUARIOS", schema="exemplo")
@NamedNativeQueries({
    @NamedNativeQuery(name="CONSULTAR_USUARIO", 
                      query = "SELECT id, nome, nascimento from exemplo.USUARIOS", 
                      resultClass = Usuario.class),
    @NamedNativeQuery(name="CONSULTAR_USUARIO_ID", 
                      query = "SELECT id, nome, nascimento from exemplo.USUARIOS WHERE id = :idUsuario", 
                      resultClass = Usuario.class),
})
public class Usuario {

    public Usuario(long id, String nome, Date nascimento) {
        this.id = id;
        this.nome = nome;
        this.nascimento = copyDate(nascimento);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nome;

    private Date nascimento;
    ...
}
```

## 3. Uso

Executar na raiz do projeto:
```
mvn package -Ddbiq.user=<usuario> -Ddbiq.password=<senha>
```

onde:

*\<usuario\>* : usuário de acesso ao DB2/DBIQ para o ambiente de HOMOLOGAÇÃO.

*\<senha\>* : senha do usuário definido acima para o ambiente de HOMOLOGAÇÃO.


## 4. Configuração do Plugin

1. **dbiq.skip** : Desabilita a execução do plugin (default: false).
1. **dbiq.unbreakable** : Desativa a *quebra* do build em caso de erro na validação das queries (default: true).
1. **dbiq.environment** : Ambiente de execução do DBIQ (default: homologação).
1. **dbiq.user** : Usuário de acesso ao DB2/DBIQ do ambiente definido em *dbiq.environment*.
1. **dbiq.password** : Senha do usuário DB2/DBIQ definido em *dbiq.user* para o ambiente definido em *dbiq.environment*. 

## 5. Execução do plugin em projetos gerados com BBDEV 

Os projetos gerados pelo BBDEV já executam o plugin do dbiq por padrão quando utilizando o script run.sh, onde internamente (dentro do Dockerfile.dev) será executado o maven com os parâmetros necessários para a execução do dbiq conforme abaixo: 

```
RUN  mvn clean package -Ddbiq.skip=false -Ddbiq.unbreakable=true -Ddbiq.user=$DBIQ_USER -Ddbiq.password=$DBIQ_TOKEN
```

(os parâmetros $DBIQ_USER e $DBIQ_TOKEN recebem chave e senha de homologação do desenvolvedor, solicitadas via o script do run.sh)

O resultado da execução pode ser encontrado no console conforme abaixo: 

```
[INFO] --------------------- Relatório final DBIQ - INICIO -----------------------
[INFO] 
[INFO]          Avaliações OK     : 0
[INFO]          Warnings          : 0
[INFO]          Disallows         : 0
[INFO]          Erros SQL         : 5
[INFO]          Erros na avaliação: 0
[INFO] 
[INFO]          Return Code       : 9 (Erro de SQL)
[INFO] 
[INFO] --------------------- Relatório final DBIQ - FINAL  -----------------------
```

Note que neste caso todas as queries foram recusadas porque o template de código do BBDEV utiliza tabelas inexistentes no DB2 de alta plataforma. 

Após alterar as queries para as do seu projeto, verifique novamente o relatório para verificar se o resultado da execução delas foi OK. Caso o return code for maior do que 4, não é permitida a baixa do projeto em ambiente de produção. ---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/db2/UsandoDBIQEmProjetosJava.md&internalidade=db2/UsandoDBIQEmProjetosJava)
