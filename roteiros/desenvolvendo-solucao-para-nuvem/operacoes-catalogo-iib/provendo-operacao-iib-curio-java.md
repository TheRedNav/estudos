> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/provendo-operacao-iib-curio-java.md&amp;action_name=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/provendo-operacao-iib-curio-java.md)

# Provimento de operações IIB com Curió em projetos Java

Este guia explica como funciona o provimento e como implementá-lo manualmente, caso deseje incluir tardiamente o provimento ou não possa gerar o projeto pelo Brave. Para provimento em outras linguagens, favor conferir orientações na [issue #1445](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/1445).

O provimento da operação é feito utilizando o Curió, um container que inicia no mesmo pod da sua aplicação, quando configurado. Para prover uma operação utilizando o Curió, o seu projeto deverá ter um endpoint `POST` com o nome e versão da operação no formato op{numero-operacao}v{versao-operacao}(ex: op3821497v1), que receba como entrada um JSON com os dados da requisição e posteriormente devolva um JSON com os dados de resposta, conforme cadastrado no Catálogo de Operações. Recomendamos que seja feita a leitura da [WIKI do Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/wikis/home) para entender melhor o seu funcionamento e qual versão deve ser utilizada, bem como os parâmetros dela. Este tutorial se baseia na versão 0.8.3.

## Criando a operação no catálogo

O primeiro passo é criar a operação (ou versionar uma já existente). As instruções para uso do catálogo estão disponíveis no link abaixo:

[Manual Catalogo](https://fontes.intranet.bb.com.br/ctl/publico/atendimento/-/wikis/Cat%C3%A1logo-de-Opera%C3%A7%C3%B5es)

As operações devem estar configuradas como **Provimento High End** para que possa ser provida via container.

## Configurando dependência pom da operação

O próximo passo é incluir a dependência da operação IIB no arquivo pom.xml do seu projeto, a fim de permitir utilizar as classes de requisição e resposta da operação.

Neste exemplo utilizamos a operação 3821497, versão 1, do WBB. Ela é uma operação de echo, que recebe como argumento um texto e retorna como resposta o mesmo texto.

Para obter o xml da operação, acesse a plataforma e selecione a área [TECNLOGIA](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=19#/). No menu lateral navegue nas opções contrução/catálago/operações para abrir o aplicativo "Catálogo de Operações". Procure pela operação desejada e depois ao acessá-la procure pelo botão "Dependência Maven", será apresentado o xml da dependência maven.

Inclua o xml obtido no pom do seu projeto dentro da seção `dependencies`:

```xml
<dependencies>
   ...
   <dependency>
       <groupId>br.com.bb.wbb.operacao</groupId>
       <artifactId>Op3821497-v1</artifactId>
       <version>1.9.0-SNAPSHOT</version>
   </dependency>
   ...
</dependency>
```

> Após incluir esta dependência, recomendamos que você faça o reload do POM na sua IDE para que você possa utilizar os recursos de auto-complete nas classes de requisição e resposta. Você pode fazer isto com o comando `mvn compile`.

## Criando a classe para o provimento da operação

O próximo passo é criar a classe que vai fazer o provimento da operação. Conforme abaixo, o recurso deve ser definido para consumir e prover JSON. O Path deve ser definido no formato op`numero-operacao`v`versao-operacao` para ser chamado pelo Curió.

```java

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

...

@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/op3821497v1")
public class Op3821497v1 {
...
}
```

Crie então o método responsável por atender as requisições POST conforme abaixo. Os parâmetros de entrada e de saída serão a classe de requisição e resposta contidas no jar da operação.

No método servir apresentado sugerimos que a lógica seja feita por dois métodos:

1.  `validarEntrada` - responsável por validar se cada campo da entrada atende ao esperado;
2.  `tratarRequisicao` - responsável pela lógica de negócio.

```java
import javax.ws.rs.POST;

import br.com.bb.wbb.operacao.echoWBBV1.bean.requisicao.DadosRequisicaoEchoWBB;
import br.com.bb.wbb.operacao.echoWBBV1.bean.resposta.DadosRespostaEchoWBB;

...

    @POST
    public DadosRespostaEchoWBB servir(DadosRequisicaoEchoWBB requisicao) throws ErroNegocialException {

        validarEntrada(requisicao);

        DadosRespostaEchoWBB resposta = tratarRequisicao(requisicao);

        return resposta;
    }
```

No `validarEntrada` verificamos apenas se o único campo da nossa requisição (`textoDado`) está preenchido com um valor diferente de espaços. Caso não esteja, lançaremos uma `ErroNegocialException` com uma mensagem. `TextoNaoInformadoException` é uma classe do nosso sistema que herda de `BBException`, conforme descrito pela biblioteca [dev-java-erro](https://fontes.intranet.bb.com.br/dev/dev-java-erro).

```java
private void validarEntrada(DadosRequisicaoEchoWBB requisicao) throws ErroNegocialException{

    if(requisicao.getTextoDado().equals("")){
        throw new TextoNaoInformadoException();
    }

}
```

Para fins didáticos, um exemplo curto de como poderia ser a classe `TextoNaoInformadoException`:

```java

import br.com.bb.dev.erros.exceptions. BBException;

public class TextoNaoInformadoException extends BBException {

    public TextoNaoInformadoException() {
        super("015", "Texto de entrada não informado");
    }
```

No `tratarRequisicao` abaixo, apenas setamos o campo da entrada na resposta, conforme comportamento esperado da nossa operação.

```java
private DadosRespostaEchoWBB tratarRequisicao(DadosRequisicaoEchoWBB requisicao) throws ErroNegocialException{

    DadosRespostaEchoWBB resposta = new DadosRespostaEchoWBB();

    resposta.setTextoDado(requisicao.getTextoDado());

    return resposta;
}
```

## Fazer o provimento via Curió em ambiente local usando docker-compose

Para testar o provimento da operação local, o curió deve ser incluído no docker-compose, conforme exemplo abaixo. Na variável `CURIO_OP_PROVEDOR` devem ser incluídos os valores da dependência maven, separados por `:` (dois pontos). Caso haja mais de uma operação a ser provida, separe por `|` (barra). Observe que o container do seu microsserviço e o do curió devem estar na mesma rede.

```yaml
version: "3.4"
services:
  dev-core-java:
    container_name: dev-core-java
  ...
    network_mode: host
  iib-curio:
    container_name: iib-curio
    image: docker.binarios.intranet.bb.com.br/bb/iib/iib-curio:0.8.3
    # as enviroments estarão presentes no arquivo .env_curio na raiz do projeto, voce deve alterar la as configuracoes
    env_file:
      - .././.env_curio
    # se windows, alterar abaixo as portas 8081 e também na variável KUMULUZEE_SERVER_HTTP_PORT para 8090 por exemplo
    # pois a porta 8081 no windows pode já estar ocupada por outros serviços
    ports:
      - "8081:8081"
    network_mode: host
```

## Resultados Finais

Após subir o projeto na sua máquina (seja via docker-compose up ou via script run.sh), o curió irá se registrar no iib de desenvolvimento provendo a operação a partir da sua máquina. Os testes poderão então ser feitos a partir do aplicativo "Catálogo de Operações" localizado na plataforma na área [TECNLOGIA](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=19#/). Caso haja mais de uma máquina provendo a operação, as requisições serão escalonadas usando estratégia round-robin.

Os testes da sua implementação da operação também podem ser feitos diretamente no endpoint REST gerado pelo seu microsserviço, sem passar pelo Curió, conforme ilustrado na figura abaixo.

![ ](./imagens/ChamadaEndpoint-Sucesso.png)


[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/provendo-operacao-iib-curio-java.md&internalidade=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/provendo-operacao-iib-curio-java)
