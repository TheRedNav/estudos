> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_utilizar_token_atendimento_curio.md&amp;action_name=iib/Como_utilizar_token_atendimento_curio)

# Como utilizar um token de atendimento em operações via Curió

Este roteiro ensina a recuperar um usuário logado e repassá-lo no consumo de operações. Para repassar usuários em operações IIB na Cloud, via Curió, é necessário um token de atendimento. Esse token é um JWT assinado pelo GCS e é recebido pela aplicação provedora como *header* *Authorization*, no formato `Bearer <token>`. 

## Requisitos

* Primeiro, confira se você tem as [configurações e dependências necessárias](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/roteiroTokenAtendimento.md#pr%C3%A9-requisitos-na-aplica%C3%A7%C3%A3o-arq3) para utilizar um token de atendimento.
> :grey_exclamation: **Importante** 
>
> No Curió do microserviço, a variável de ambiente `CURIO_ACR_ATIVAR_TOKEN_ATENDIMENTO` deve estar definina como `"true"`.

* Sempre que utilizar um token de atendimento, [você deve autênticá-lo](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_autenticar_token_atendimento.md) para conseguir executar as ações desse roteiro. 
* [Dependências do Quarkus adicionadas ao arquivo pom.xml](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/roteiroTokenAtendimento.md#utilizando-as-extens%C3%B5es-do-quarkus) do seu projeto.


## Recuperar o usuário logado
Para recuperar o usuário logado no IIB são injetados contextos e dependências em qualquer classe do fluxo dentro do contexto da *request*:

1. Nos **imports**, adicione **org.eclipse.microprofile.jwt.JsonWebToken**.
2. Declare uma **variável do tipo JsonWebToken** dentro de sua classe.
3. Adicione a anotação **@Inject** logo acima da variável criada.
4. Declare o método **getClaim()** da classe JsonWebToken para recuperar os atributos do token. 

> :information_source: **Observação** 
> 
> Os atributos disponíveis podem ser conferidos no roteiro [Token de Atendimento - Documentação](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/TokenAtendimentoDoc.md).

#### Exemplo

```java
package br.com.bb.dev.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.inject.Inject;

import br.com.bb.dev.operacao.echoV1.bean.requisicao.DadosRequisicaoEcho;
import br.com.bb.dev.operacao.echoV1.bean.resposta.DadosRespostaEcho;

import org.eclipse.microprofile.jwt.JsonWebToken;

@Path("/op4365805v1")
@ApplicationScoped
public class Op4365805v1 {

  	@Inject
  	JsonWebToken jwt;
	
	@POST
   	public DadosRespostaEcho servir(DadosRequisicaoEcho requisicao) {
		DadosRespostaEcho retorno = new DadosRespostaEcho();
		retorno.setTextoDado("Hello " + jwt.getClaim("name"));
	   	return retorno;
   	}
}
```

> :bulb: **Dica** 
>
> O `subject` do token é o MCI do usuário logado, e pode ser acessado tanto pelo método `getSubject()` quanto pelo `getClaim("sub")`.

## Repassar o usuário para operações consumidas dentro de um provimento
Para consumir uma operação dentro do provimento e repassar todos os *headers* do Curió para que, inclusive, o usuário permaneça logado nas operações posteriores:

1. Adicione a anotação **@RegisterClientHeaders** na sua interface [restclient](https://download.eclipse.org/microprofile/microprofile-rest-client-1.2.1/microprofile-rest-client-1.2.1.html#_jax_rs) do Curió.
2. No arquivo **application.properties**, adicione `org.eclipse.microprofile.rest.client.propagateHeaders=Authorization,ESTADO-INTEGRACAO,INFO-CNL`. 

#### Exemplo

```java
package br.com.bb.dev.integration;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.bb.dev.operacao.publicacaoTextoV1.bean.publicacao.DadosPublicacaoPublicacaoTexto;

@RegisterRestClient
@RegisterClientHeaders
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public interface ConsumidorCurio {

	@POST
	@Path("op4676630v1")
	DadosPublicacaoPublicacaoTexto executarOperacao(DadosPublicacaoPublicacaoTexto requisicao);

}
```

> :grey_exclamation: **Importante** 
> 
> Embora a identidade seja validada na operação chamadora, isso não substitui a necessidade de validação em todas as operações invocadas pela requisição. Cada operação que faz uso dos dados do token deve realizar suas próprias verificações independentes.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_utilizar_token_atendimento_curio.md&internalidade=iib/Como_utilizar_token_atendimento_curio)
