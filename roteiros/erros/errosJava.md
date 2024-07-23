> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Erros
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/errosJava.md&amp;action_name=erros/errosJava)

O projeto blank _Java_ gerado utilizando o BBDEV com o plugin dx-kumuluzee à partir da versão 0.0.5 já contém um exemplo de geração de mensagem de erros e um endpoint com todos os erros da aplicação.

Ao executar o projeto usando o script run.sh os erros podem ser acessadas no endpoint /errors. 

## Implementação

Caso tenha gerado seu projeto com uma versão anterior ou não possua a funcionalidade de listagem dos erros, uma sugestão é a criação dos seguintes itens:
- Enum com todas as mensagens de erros
- Classe de erro com o padrão definido pela Arq3 conforme descrito nesse [link](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/errors-endpoint.md)
- Uma classe para representar a lista de erros.
- Exception que tenha uma propriedade da classe de erro citada anteriormente 
- Handler para tratar as exceções e retornar o erro e o status code no padrão Arq3 conforme descrito nesse [link](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/http-return-codes.md).

## Funcionamento

Criar uma nova mensagem de erro no [EnumMensagens](#enum-de-mensagens-de-erro).

A mensagem será utilizada como parametro de entrada na criação da [exception negocial](#exception-negocial).

Ao lançar uma exceção negocial, a classe de [handler](#classe-de-handler-negocial) irá interceptar a execução, a resposta será de acordo com o erro informado na criação da exception, [conforme exemplo](#exemplo-de-utilização).




*Observação não esqueça de incluir a linha de package nas classes abaixo conforme o nome do seu projeto.

#### Enum de Mensagens de Erro

```java 
public enum EnumMensagens {
    ERRO_APLICACAO("000","Erro ao executar", "%s", "%s", "%s"),
    ERRO_SQL("999","Erro no sistema", "Tente novamente mais tarde",
            "SQL CODE: %s , QUERY SQL: %s", "SQL CODE: %s , MOTIVO: %s"),
    INFORME_DATA_NASCIMENTO("001","Informe a data de nascimento", "", "", ""),
    ERRO_EXCLUSAO_USUARIO("002","Não foi possivel excluir o usuario do identificador %s.","","", ""),
    ERRO_INCLUSAO_USUARIO("003","O usuario deve ter mais que %s anos.","","",""),
    ;

    String mensagem;
    String codigo;

    String userHelp;
    String developerMessage;
    String moreInfo;

    EnumMensagens(String codigo, String mensagem, String userHelp, String developerMessage, String moreInfo) {
        this.codigo = codigo;
        this.mensagem = mensagem;
        this.userHelp = userHelp;
        this.developerMessage = developerMessage;
        this.moreInfo = moreInfo;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getUserHelp() { return userHelp; }

    public String getDeveloperMessage() { return developerMessage; }

    public String getMoreInfo() { return moreInfo; }
}
```

#### Classe de Erro

```java
import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang.StringUtils;

/**
 * Classe para representar a estrutura da mensagem de erro conforme descrito no padrao de erros, aonde:
 *
 * Obrigatorios
 * code               : Contém o código do erro. Deve ser numérico e conter no máximo 3 dígitos
 * source             : Nome do módulo ou da classe ou, quando operação IIB, número sequencial do erro
 * message            : Mensagem de erro para o usuário
 *
 * Opcionais
 * userHelp           : Orientação sobre como o usuário pode resolver o problema
 * developerMessage   : Mensagem técnica para o desenvolvedor
 * moreInfo           : Complemento do erro para o desenvolvedor
 *
 * https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/errors-endpoint.md
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Erro {

    private String code;
    private String message;
    private String source;

    private String userHelp;
    private String developerMessage;
    private String moreInfo;

    public Erro() {
        this.code = "";
        this.message = "";
        this.source = "";
    }

    public Erro(String mensagem, String source) {
        this.code = "";
        this.message = mensagem;
        this.source = source;
    }

    public Erro(EnumMensagens msg, String source) {
        this(msg);
        this.source = source;
    }

    public Erro(EnumMensagens msg) {
        this.code = msg.getCodigo();
        this.message =  msg.getMensagem();
        this.userHelp = msg.getUserHelp();
        this.moreInfo = msg.getMoreInfo();
        this.developerMessage = msg.getDeveloperMessage();
        this.source = "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getUserHelp() {
        return userHelp;
    }

    public void setUserHelp(String userHelp) {
        this.userHelp = userHelp;
    }

    public String getDeveloperMessage() {
        return developerMessage;
    }

    public void setDeveloperMessage(String developerMessage) {
        this.developerMessage = developerMessage;
    }

    public String getMoreInfo() {
        return moreInfo;
    }

    public void setMoreInfo(String moreInfo) {
        this.moreInfo = moreInfo;
    }

    public static class Builder{

        private String message;
        private EnumMensagens enumMensagens;
        private String source;
        private String[] messageArgs = {""};
        private String[] moreInfoArgs = {""};
        private String[] userHelpArgs = {""};
        private String[] developerMessageArgs = {""};

        public Builder (EnumMensagens enumErro, String source) {
            this.enumMensagens = enumErro;
            this.source = source;
        }

        public Builder (String message, String source) {
            this.message = message;
            this.source = source;
        }

        public Builder withFormatedMessage(String... args){
            if(args != null) {
                messageArgs = args;
            }
            return this;
        }

        public Builder withFormatedMoreInfo(String... args){
            if(args != null) {
                moreInfoArgs = args;
            }
            return this;
        }

        public Builder withFormatedUserHelp(String... args){
            if(args != null) {
                userHelpArgs = args;
            }
            return this;
        }

        public Builder withFormatedDeveloperMessage(String... args){
            if(args != null) {
                developerMessageArgs = args;
            }
            return this;
        }

        public Erro build(){

            Erro erro;
            if(StringUtils.isBlank(message)){
                erro = new Erro(this.enumMensagens, this.source);
            } else {
                erro = new Erro(this.message, this.source);
            }

            erro.setMessage(String.format(erro.getMessage(), messageArgs));
            erro.setDeveloperMessage(String.format(erro.getDeveloperMessage(), developerMessageArgs));
            erro.setUserHelp(String.format(erro.getUserHelp(), userHelpArgs));
            erro.setMoreInfo(String.format(erro.getMoreInfo(), moreInfoArgs));

            return erro;
        }
    }
}
```

#### Classe para Lista de Erros

```java 
import java.util.ArrayList;
import java.util.List;

public class ListaErro {

    private List<Erro> errors;

    public ListaErro() {
        this.errors = new ArrayList<>();
    }

    public ListaErro(List<Erro> errors) {
        this.errors = errors;
    }

    public ListaErro(Erro error) {
        this.errors = new ArrayList<>();
        this.errors.add(error);
    }

    public List<Erro> getErrors() {
        return errors;
    }

    public void setErrors(List<Erro> errors) {
        this.errors = errors;
    }
}

```

#### Exception Negocial

```java 
public class ErroNegocialException extends Exception {

    static final String LINHA = " - linha: ";
    static final String PONTO = " .";
    private final String[] args;
    private final EnumMensagens enumMensagem;

    protected ErroNegocialException(String message, Throwable cause){
        super(message, cause);
        this.args = new String[]{""};
        this.enumMensagem = null;
    }
    protected ErroNegocialException(String message){
        super(message);
        this.args = new String[]{""};
        this.enumMensagem = null;
    }

    public ErroNegocialException(EnumMensagens enumMensagem, String... args) {
        super(enumMensagem.getMensagem());
        this.args = args;
        this.enumMensagem = enumMensagem;
    }

    public ErroNegocialException(EnumMensagens enumMensagem) {
        super(enumMensagem.getMensagem());
        this.enumMensagem = enumMensagem;
        args = new String[]{""};
    }

    public ErroNegocialException(EnumMensagens enumMensagem,Throwable cause, String... args) {
        super(enumMensagem.getMensagem(), cause);
        this.enumMensagem = enumMensagem;
        this.args = args;
    }

    public ErroNegocialException(EnumMensagens enumMensagem, Throwable cause) {
        super(enumMensagem.getMensagem(), cause);
        this.enumMensagem = enumMensagem;
        args = new String[]{""};
    }

    private String getSourceFromStackTrace() {
        StackTraceElement stackTrace = this.getStackTrace()[0];
        return stackTrace.getClassName() + LINHA + stackTrace.getLineNumber() + PONTO;
    }

    public Erro getErro() {
        if(this.enumMensagem != null){
            return new Erro.Builder(this.enumMensagem, getSourceFromStackTrace()).withFormatedMessage(this.args).build();
        } else {
            return new Erro.Builder(this.getMessage(), getSourceFromStackTrace()).withFormatedMessage(this.args).build();
        }
    }
}

```

#### Classe de Handler Negocial

```java 
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ErroNegocialHandler implements ExceptionMapper<ErroNegocialException> {
    private static final Logger LOG = LogManager.getLogger(ErroNegocialHandler.class.getName());

    @Override
    public Response toResponse(ErroNegocialException erroNegocial) {
        LOG.error("Error with exception log", erroNegocial);

        return Response
                .status(422)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ListaErro(erroNegocial.getErro()))
                .build();
    }
}
```

#### Classe de Filtro/Handler de Exception

```java 
import com.kumuluz.ee.logs.LogManager;
import com.kumuluz.ee.logs.Logger;
import org.apache.commons.lang.StringUtils;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class ExceptionHandler implements ExceptionMapper<Exception> {
    private static final Logger LOG = LogManager.getLogger(ExceptionHandler.class.getName());

    protected static final String LINHA = " - linha: ";
    protected static final String PONTO = " .";
    protected static final String PACKAGE_NAME = "br.com.bb.sigla_do_seu_sistema";

    @Override
    public Response toResponse(Exception e) {
        LOG.error("Error with exception log", e);

        String source = "";
        String motivo = e.getMessage();

        for(StackTraceElement stackTraceElement : e.getStackTrace()){
            //Pega o primeiro elemento da lista de stackTrace caso nao encontre dentro
            if(StringUtils.isBlank(source)){
                source = stackTraceElement.getClassName() + LINHA + stackTraceElement.getLineNumber() + PONTO;
            }
            //Pega a primeira ocorrencia de erro dentro do codigo do projeto pelo package name
            if(stackTraceElement.getClassName().contains(PACKAGE_NAME)){
                source = stackTraceElement.getClassName() + LINHA + stackTraceElement.getLineNumber() + PONTO;
                break;
            }
        }

        Erro erro = new Erro.Builder(EnumMensagens.ERRO_APLICACAO, source)
                .withFormatedDeveloperMessage(motivo).build();

        return Response
                .status(500)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(new ListaErro(erro))
                .build();
    }
}
```

*Observação, ajuste a string PACKAGE_NAME com o nome correto para seu projeto.


#### Exemplo de utilização

```java
    private void validarUsuario(Usuario usuario) throws ErroNegocialException {
        if(usuario.getNascimento() == null){
            throw new ErroNegocialException(EnumMensagens.INFORME_DATA_NASCIMENTO);
        }

        LocalDate dataAtual = LocalDate.now();
        int idade = dataAtual.getYear()- usuario.getNascimento().toInstant().atZone(ZoneId.of("UTC")).getYear();

        if( idade < IDADE_MINIMA_PARA_MAIORIDADE){
            throw new ErroNegocialException(EnumMensagens.INFORME_DATA_NASCIMENTO, IDADE_MINIMA_PARA_MAIORIDADE.toString());
        }
    }
```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/errosJava.md&internalidade=erros/errosJava)
