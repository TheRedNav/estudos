> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Como_tratar_erros_Java.md&amp;action_name=erros/Como_tratar_erros_Java)

# Como tratar erros no Java
Esse roteiro ensina como configurar e usar a biblioteca `dev-java-erro` para realizar o tratamento de erros em aplicações Java.

## Requisitos
* Java 11 ou superior.
* CDI.
* API JAX-RS 2.0 (RestEasy).
* Implementação de json, como jackson ou jsonb.
* Aplicação que utilize um framework com o padrão Microprofile 4, como o Quarkus.

## Passo 1: Incluir a dependência no POM.xml

> :grey_exclamation: **Importante**
>
> Antes de incluir a dependência, verifique se a inclusão já foi feita no Brave quando o projeto foi gerado. Caso já tenha sido incluída, não deve ser feita novamente.

No projeto Java/Quarkus, adicione a dependência da biblioteca no `pom.xml`, conforme o trecho abaixo:

```xml
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-java-erro</artifactId>
  <version>2.1.1</version>
</dependency> 
```

## Passo 2: Analisar exemplos do código base

Temos alguns exemplos de erro na classe `exception/ErrosSistema.java`:

```java
package br.com.bb.dev.exception;

import br.com.bb.dev.erros.core.exceptions.BBRuntimeException;
import br.com.bb.dev.erros.core.model.IncluirNaListaErros;
import java.util.Map;

@IncluirNaListaErros
public class ErrosSistema {

	public static final class ExemploErroNegocialSemArgumentos extends BBRuntimeException {
		public static final String MSG_ERRO = "Sua Mensagem de erro";
		public static final String COD_ERRO = "001"; // Codigo do erro

		public ExemploErroNegocialSemArgumentos() {
			super(COD_ERRO, MSG_ERRO);
		}
	}

	public static final class ExemploErroNegocialComVariavelMonitoradaNaFormatacao extends BBRuntimeException {
		public static final String MSG_ERRO = "Mensagem de erro com formatacao para o texto : %s";
		public static final String COD_ERRO = "002"; // Codigo do erro

		public ExemploErroNegocialComVariavelMonitoradaNaFormatacao(String texto) {
			super(COD_ERRO, MSG_ERRO);
			this.put("TEXTO", texto);
		}
	}
}
```

> :bulb: **Dica**
> 
> Além desses exemplos é possível verificar os outros construtores da classe `BBRuntimeException`. No VSCode, pressione a tecla **Ctrl** e clique no nome na classe para visualizar seu código.

A partir do exemplo, podemos observar que:
- A classe `BBException` e a classe `BBRuntimeException` são bem semelhantes, mas a diferença entre elas é que a primeira lança exceções do tipo *checked* e a segunda do tipo *unchecked*, ou seja, a primeira lança a exceção de forma verificada e a segunda não verificada.
- A classe `BBRuntimeException` possui doze construtores e três métodos públicos, além de estender `RuntimeException` e implementar a interface `BBErrorMap`. Essa interface, por sua vez, estende a classe `Map<K,V>` e implementa a interface `BBError`.
- A interface `BBError` possui dois atributos do tipo String e quatro métodos:

```java
//Retorna o codigo de erro.
String getCode();
//Retorna a mensagem de erro.
String getMessage();
// Converte a mensagem de erro para string e retorna
default String getMessage(Object... messageArgs) {
    return String.format(this.getMessage(), messageArgs);
}
// Retorna o codigo de status do erro
default int getStatusCode() {
    return 422;
}
```
Outra forma de utilizar essa classe é informar outro construtor que recebe um objeto  `BBError` da classe `BBRuntimeException`:

```java
var erro = new ErrosSistema.ExemploErroNegocialSemArgumentos();
throw new BBRuntimeException(erro);
```

## Passo 3: Tratar erros
A seguir, vamos mostrar como utilizar a biblioteca `dev-java-erro` para tratar exceções relacionadas à manipulação de banco de dados e uso do Curió. 

### Tratamento de erros relacionados a SQL
Aqui temos um exemplo de erro para ser utilizado quando manipulamos banco de dados em nossa aplicação:


```java
public static final class ErroExecucaoSQL extends BBRuntimeException {
    public static final String MSG_ERRO = "Sua Mensagem de erro";
    public static final String COD_ERRO = "900"; // Codigo do erro

    public ErroExecucaoSQL(Throwable e) {
        super(COD_ERRO, MSG_ERRO, e);
        if (e.getCause() instanceof JDBCException) {
            JDBCException jdbcException = (JDBCException) e.getCause();

            if (jdbcException.getSQLException() != null) {
                this.put("SQL_CODE", String.valueOf(jdbcException.getSQLException().getErrorCode()));
            }

            if (jdbcException.getSQL() != null && jdbcException.getSQL().length() > 0 ) {
                this.put("SQL_QUERY", jdbcException.getSQL());
            }

            if (jdbcException.getCause() != null) {
                this.put(Constantes.VAR_MOTIVO, jdbcException.getCause().getMessage());
            } else {
                this.put(Constantes.VAR_MOTIVO, jdbcException.getMessage());
            }
        }
    }
}
```

Observe que no final do código foi utilizada a classe `Constantes`, que reúne um conjunto de constantes que podem ser utilizadas para padronizar as mensagens de erro e assim facilitar a consulta ao *stacktrace*. Abaixo, a reprodução de parte da classe:

```java
public final class Constantes {
  ...
  public static final String VAR_REQUISICAO = "ID-REQUISICAO";
  public static final String VAR_ORIGEM = "ORIGEM-ERRO";
  public static final String VAR_MOTIVO = "MOTIVO-ERRO";
  ...
}
```

### Tratamento de erros relacionados ao Curió
A biblioteca `dev-java-erro` também disponibiliza o filtro `CurioExceptionMapper` para ser adicionado à interface de consumo de operações do Curió. Esse filtro intercepta as repostas com um status code maior ou igual a 400, originadas no consumo de uma operação IIB. Nesses casos, o filtro tenta obter o erro do Curió quando informado ou cria um com base nas informações de retorno.
É necessário indicar explicitamente onde ele será utilizado. Para isso, basta incluir o filtro como `Provider` na interface `RestClient` usada para realizar as chamadas às operações definidas como consumidoras na sua aplicação. O registro é feito com a anotação de classe `@RegisterProvider(CurioExceptionFilter.class)`, conforme o exemplo abaixo:

```java
@RegisterRestClient
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(CurioExceptionMapper.class)
public interface InterfaceConsumidor {
    @POST
    @Path("op3821497v1")
    DadosRespostaEchoWBB executarOperacao(DadosRequisicaoEchoWBB requisicao);
}
```

O filtro acima gera uma exceção do tipo `CurioConsumoException` ao consumir uma operação, seja por um erro negocial ou por um erro na comunicação entre a sua aplicação e o Curió.
No momento da chamada de uma operação de consumo, caso um erro ocorra e o mesmo não seja capturado, ele será tratado como um erro do tipo `BBException` pelos filtros da própria biblioteca. Nesse caso, o identificador utilizado será o mesmo retornado pelo objeto de erro do consumo da operação.

## Outros filtros
Um filtro importante é o `mp.openapi.filter` que vem configurado nos projetos gerados pelo Brave e está localizado dentro do arquivo `application.properties`:
 
```python
# Filtro para adicionar os responses de erro no openapi(swagger)
mp.openapi.filter=br.com.bb.dev.erros.rest.filter.openapi.OpenApiFilter
```
Ele é utilizado para filtrar o resultado da documentação do OpenApi, adicionar os responses de erro para os casos de 422 e 500, além de incluir na documentação da API as estruturas de erro apresentadas no Swagger.

A biblioteca `dev-java-erro` também possui um endpoint `/errors`, que lista os erros da sua aplicação.   
Para que os erros apareçam no endpoint, a classe deve atender a um dos requisitos abaixo:

* Ter a anotação `@IncluirNaListaErros` e herdar de `BBRuntimeException` ou `BBException`;
* Ter a anotação `@IncluirNaListaErros` e possuir classes internas que herdam de `BBRuntimeException` ou `BBException`;
* Usar um Enum que implemente a interface BBError e que possua a anotação `@IncluirNaListaErros`.


> :bulb: **Dica**
>
> A biblioteca `dev-java-erro` dispõe de uma lista de variáveis de ambiente para facilitar a sua configuração. Caso não sejam configuradas, valores padrão serão utilizados. Para conhecer as *environments* disponíveis, acesse o roteiro [Alterar propriedades com environments](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Ref_Alterar_propriedades_environments.md).

## A seguir
- Confira a documentação atualizada da biblioteca [dev-java-erro](https://fontes.intranet.bb.com.br/dev/dev-java-erro).


## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Como_tratar_erros_Java.md&internalidade=erros/Como_tratar_erros_Java)
