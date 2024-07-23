> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Configuração padrao de erros Java
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/padraoJavaErros.md&amp;action_name=erros/padraoJavaErros)

Nos projetos java gerados pelo BBDev, ja utiliza uma extensão que implementa esse padrão com a utilização de filtros e analise do codigo do projeto aonde essa extensão esta sendo utilizada, além de exemplo como implementar.

Caso seu projeto tenha sido gerado anteriormente e voce deseje atualizar, basta incluir a dependencia no pom do seu projeto :

```xml
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-dx-quarkus-ext</artifactId>
  <version>0.1.9</version>
</dependency>
```

Essa extensão ja cria um endpoint que retorna a lista dos erros que você utiliza no seu projeto, mas para entender qual classe tem essas informações voce deve criar um Enum que implementa a interface IEnumErro, ela define que seu Enum deve possuir o metodo get() que retornar uma nova instancia de erro a partir dos dados utilizados para a crição de um item do Enum, na extensão ja disponibilizamos um EnumPadrao de erro utilizado para erros não tratados. E no BBDev ja temos um exemplo de como deve ser criado, como no exemplo abaixo:

 ```java
 package br.com.bb.dev.exceptions;

import br.com.bb.dev.ext.error.ChavesMonitoradasSQL;
import br.com.bb.dev.ext.error.Erro;
import br.com.bb.dev.ext.error.interfaces.IEnumChaveMonitorada;
import br.com.bb.dev.ext.error.interfaces.IEnumErro;
import br.com.bb.dev.ext.error.interfaces.IErro;

public enum ErrosSistema implements IEnumErro {
    ERRO_SISTEMA("001","Erro Negocial Teste", ChavesMonitoradasSistema.class),
    INFORME_DATA_NASCIMENTO("002","Informe a data de nascimento"),
    ERRO_EXCLUSAO_USUARIO("003","Não foi possivel excluir o usuario do identificador.",ChavesMonitoradasSistema.class),
    ERRO_INCLUSAO_USUARIO("004","O usuario deve ter mais que %s anos.", ChavesMonitoradasSistema.class),
    TEXTO_ENTRADA_NAO_INFORMADO("005", "Texto de entrada nao foi informado",ChavesMonitoradasSistema.class),
    ERRO_SQL("900","Erro no sistema", ChavesMonitoradasSistema.class,ChavesMonitoradasSQL.class);

    String codigo;
    String mensagem;
    Class<? extends IEnumChaveMonitorada>[] enumsChaves;

    ErrosSistema(String codigo, String mensagem, Class<? extends IEnumChaveMonitorada>... enumsChaves) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.enumsChaves = enumsChaves;
    }
    ErrosSistema(String codigo, String mensagem) {
        this.codigo = codigo;
        this.mensagem = mensagem;
    }

    @Override
    public IErro get() {
        if(enumsChaves == null){
          return new Erro(codigo, mensagem);  
        }
        return new Erro(codigo, mensagem, enumsChaves);
    }
}
 ```

Esse enum possui dois construtores um com 3 paramentros e outro com apenas 2, eles servem para pegar as propriedades informadas na definicao do item do Enum e gerar uma instancia da Classe de erro.
No caso do construtor de 3 parametros, temos o codigo, a mensagem e uma lista de Enums das chaves monitoradas que vamos utilizar para aquele erro, lembrando que depois de definido não podemos incluir novas chaves ou alterar qualquer propriedade da classe Erro. O de 2 parametros so aceita o codigo e mensagem.

A classe Erro independente do construtor utilizado vai criar uma lista com três variaveis monitoradas, ORIGEM-ERRO, MOTIVO-ERRO, ID-REQUISICAO.
A ORIGEM-ERRO é o local aonde o erro ocorreu, ele é obtido pela exceção lançada, ele indica qual linha de qual classe o erro ocorreu dentro do seu projeto.
O MOTIVO-ERRO é razão do erro, ele também é otido pela exeção do java, no caso o cause() da exception.
O ID-REQUEST é um identificador utilizado no Log para cada requisição que é recebida por sua aplicação, assim ficara mais facil de encontrar os logs relacionados a aquela requisição que gerou o erro.

A classe de Erro possui os seguintes metodos:

```java
  //Retorna o codigo de erro.
  String getCode();
  //Retorna a mensagem de erro.
  String getMessage();
  //Retorna um Map não modificavel com as variaveis monitoradas.
  Map<String, String> getVariaveisMonitoradas();
  //Retorna uma Lista de itens unicos não modificavel com as chaves monitoradas.
  Set<String> getChavesMonitoradas();
  // Adiciona um valor a uma variavel monitorada ja incluida na construção da instancia
  IErro addVariavel(IChaveMonitorada chave, String valor);
  // Remove o valor de variavel monitorada
  IErro removeVariavel(IChaveMonitorada chave);
  // Remove o valor de todas as variaveis monitoradas.
  void cleanVariaveisMonitoradas();
```

Outro Enum que temos é o das Chaves Monitoradas, elas são usadas como identificacao das Variaveis Monitoradas, esse Enum tambem devera implementar uma interface, que no caso é a IEnumChaveMonitorada, ela define que o Enum implemente o metodo get() para retornar uma instancia de uma chave monitorada.
A seguir temos um exemplo de como implementar o enum com as chaves, alem do nome temos o tamanho maximo que a variavel monitorada podera ter de valor, esse campo por enquanto será utilizado para cadastramento de mensagens de forma automatica no mensageria. A extensão ja prove dois tipos de chaves a Padrao e a de SQL, essa ultima possui duas chaves SQL-CODE, e QUERY-SQL.

```java
package br.com.bb.dev.ext.error;

import br.com.bb.dev.ext.error.interfaces.IChaveMonitorada;
import br.com.bb.dev.ext.error.interfaces.IEnumChaveMonitorada;

public enum ChavesMonitoradasPadrao implements IEnumChaveMonitorada {
  ORIGEM_ERRO("ORIGEM-ERRO", 120),
  MOTIVO_ERRO("MOTIVO-ERRO", 120),
  ID_REQUISICAO("ID-REQUISICAO", 16),
  ;
  transient IChaveMonitorada chaveMonitorada;

  ChavesMonitoradasPadrao(String chave, int tamanho) {
    chaveMonitorada = new ChaveMonitorada(chave, tamanho);
  }

  @Override
  public IChaveMonitorada get() {
    return chaveMonitorada;
  }
}
```

Esse Enum é mais simples contendo apenas uma propriedade que é a chaveMonitorada que recebe uma instancia de ChaveMonitorada construida informando uma chave o tamanho maximo do campo valor da variavel monitorada.

Por fim temos a ErroNegocialException, que é a exceçao que será utilizada quando quisermos lancar um erro. Para ele ser criado é necessario informar um Erro , ou um Erro e um Throwable(outra exceção).
Os outros construtore estao sendo descontinuados.
Essa exception ira popular as variaveis monitoradas padrão, no caso as MOTIVO-ERRO, ORIGEM-ERRO e ID-REQUISICAO. 
Abaixo temos uma exemplo de lancamento de uma exceção:

```java
  IErro erro = ErrosSistema.ERRO_SISTEMA.get()
    .addVariavel(ChavesMonitoradasSistema.CONTEUDO.get(), "Valor da variavel monitorada")
    .addVariavel(ChavesMonitoradasSistema.MCI.get(), "Valor da variavel monitorada");

  throw new ErroNegocialException(erro);
```

Caso queria especializar o seu erro é possivel criar uma Exception que herde (extends) da classe ErroNegocialException, um exemplo que pode ser seguido é da ErroSqlException gerado pelo BBDev para os projetos que utilizam banco de dados. Nessa caso fizemos um tratamento da exception lancada pelos metodos de execucao de comandos no banco de dados.

```java
package br.com.bb.dev.exceptions;

import org.hibernate.JDBCException;
import br.com.bb.dev.ext.exceptions.ErroNegocialException;
import br.com.bb.dev.ext.error.ChavesMonitoradasPadrao;
import br.com.bb.dev.ext.error.ChavesMonitoradasSQL;

public class ErroSqlException extends ErroNegocialException {

    public ErroSqlException(Exception e){
        super(ErrosSistema.ERRO_SQL.get(), e.getCause());
        int code = -1;
        String sqlQuery = "";
        String motivo;

        if (e.getCause() instanceof JDBCException) {
            JDBCException jdbcException = (JDBCException) e.getCause();

            if (jdbcException.getSQLException() != null) {
                code = jdbcException.getSQLException().getErrorCode();
            }

            if (jdbcException.getSQL() != null && jdbcException.getSQL().length() > 0 ) {
                sqlQuery = jdbcException.getSQL();
            }

            if (jdbcException.getCause() != null) {
                motivo = jdbcException.getCause().getMessage();
            } else {
                motivo = jdbcException.getMessage();
            }
        } else {
            motivo = e.getMessage();
        }

        erro.addVariavel(ChavesMonitoradasPadrao.MOTIVO_ERRO.get(), motivo)
            .addVariavel(ChavesMonitoradasPadrao.ORIGEM_ERRO.get(), getSourceFromStackTraceSqlTrace())
            .addVariavel(ChavesMonitoradasSQL.SQL_CODE.get(), String.valueOf(code))
            .addVariavel(ChavesMonitoradasSQL.QUERY_SQL.get(),sqlQuery);
    }

    private String getSourceFromStackTraceSqlTrace() {
        int index = 0;
        if (this.getStackTrace().length > 1) {
            index = 1;
        }
        StackTraceElement stackTrace = this.getStackTrace()[index];
        return String.format(MENSAGEM_ORIGEM ,stackTrace.getClassName(), stackTrace.getLineNumber());
    }
}
```

## Migração de projetos java ja existentes.

### Java Quarkus

A extensao dev-dx-quarkus-ext atualmente esta funcionando com o os dois padrões de erro o que ja existia conforme descrito nesse [roteiro](errosJava.md) e o mais recente, contendo as informações de variaveis monitoradas, conforme descrito no [roteiro](padraoErros.md).

Se estiver usando a versão sem a utilizacao da extensão, inclua a dependencia da extensao dev-dx-quarkus-ext e comece a criar os novos erros no padrão descrito acima, a extensao consegue trabalhar com os dois padrão, mas atenção ao nomes das classes e dos pacotes pois voce pode ter classes com nomes e funcoes parecidas mas em pacotes diferentes. Se possivel faça a migração gerando um novo projeto pelo BBDev.

Se voce ja estiver utilizando a extensao dev-dx-quarkus-ext basta atualiza-la para a versao 0.1.0 ou superior.  Para habilitar a exibição no padrão anterior basta incluir a enviroment
'quarkus.dev.dx.error-legacy-mode=true' no arquivo application.properties do seu projeto, essa enviroment formata o retorno dos erros, tanto na ocorrencia de erro de um endpoint quanto no endpoint da lista de erros no */errors*.
Essa propriedade é falsa por padrão.

Outra enviroment importante é a **mp.openapi.filter** ela e utilizada para filtar o resultado da documentacao do OpenApi, caso queria que sua documentaçao da api contenha as estruturas de erro e adicione os responses de erro para os casos de 422 e 500 coloque o filtro disponivel na extensão conforme o exemplo abaixo, essa configuração ja vem configurada nos projetos gerados pelo BBDev, ela esta localizada no arquivo application.properties.

```yaml
mp.openapi.filter=br.com.bb.dev.ext.filters.OpenApiFilter
```

### Java Outros

Caso esteja usando um outro framework java, basta implementar o padrao [descrito](padraoErros.md) para os erros.
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/padraoJavaErros.md&internalidade=erros/padraoJavaErros)
