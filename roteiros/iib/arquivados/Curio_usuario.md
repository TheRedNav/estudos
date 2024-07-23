> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Usuário logado em operações IIB
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/arquivados/Curio_usuario.md&amp;action_name=iib/arquivados/Curio_usuario)

## Contexto

O repasse de usuário em operações IIB na Cloud, via Curió, é feito através do token de atendimento. O token de atendimento é um JWT assinado pelo GCS. 

Mais informações e requisitos de como ativar, [aqui](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/roteiroTokenAtendimento.md).

Quando sua aplicação provedora Java na Arq3.0 já cumpre os pré-requisitos determinados pelo GCS de versão mínima de Curió, chave IDH configurada e nível de segurança da operação no catálogo, e o conteiner do curió já está com a env de habilitação do token, configurar o consumo do token no provimento da operação é o último passo, do qual trataremos neste roteiro.

## Token

O token de atendimento é recebido pela aplicação provedora como *header* *Authorization*, no formato `Bearer <token>`. Para tratá-lo no Quarkus, adicione as seguintes dependências ao seu projeto:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt</artifactId>
</dependency>
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-jwt-build</artifactId>
</dependency>

```

## Garantindo a veracidade do token

Para prevenir a falsificação do token nas chamadas a operação, é necessário validar a sua assinatura. O GCS disponibiliza um emissor e uma chave pública por ambiente, que devem ser usados nos parâmetros de configuração da extensão Microprofile JWT adicionada no passo anterior(valores de desenvolvimento):


```
mp.jwt.verify.publickey.location=https://token.gcs.desenv.bb.com.br/api/v1/jwks.json
mp.jwt.verify.issuer=https://token.gcs.desenv.bb.com.br
```

***IMPORTANTE***: O Rest assured (biblioteca de teste de endpoints Rest no Quarkus) também vai usar essa configuração para validar o JWT em requisições durante os testes unitários. Isso não é desejável e também não vai funcionar na esteira Jenkins. Por isso, recomendamos que seja feita uma configuração específica para testes. Consulte a [seção de testes da documentação](https://quarkus.io/guides/security-jwt#integration-testing) do Quarkus e escolha uma abordagem. 

Essas propriedades devem ser devidamente sobrescritas nos ambientes de homologação e produção com os valores correspondentes. Por exemplo, no values.yaml do repositório de homologação:

```yaml
	environments:

        - name: "MP_JWT_VERIFY_PUBLICKEY_LOCATION"
          value: "https://token.gcs.hm.bb.com.br/api/v1/jwks.json"
        - name: "MP_JWT_VERIFY_ISSUER"
          value: "https://token.gcs.hm.bb.com.br"

```

E de produção:

```yaml
	environments:

        - name: "MP_JWT_VERIFY_PUBLICKEY_LOCATION"
          value: "https://token.gcs.intranet.bb.com.br/api/v1/jwks.json"
        - name: "MP_JWT_VERIFY_ISSUER"
          value: "https://token.gcs.intranet.bb.com.br"

```

## Recuperando o usuário logado

Para acessar os atributos do token, lançamos mão do CDI para injetá-lo, por exemplo, na classe de *Resource* da operação. De acordo com os requisitos de negócio, essa injeção pode ser feita em qualquer classe do fluxo, desde que ainda esteja dentro do contexto de *request*. 

O exemplo abaixo utiliza uma operação de echo para responder uma saudação ao usuário logado no IIB:

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

## Atributos do token

Para recuperar atributos(claims) do token, deve ser utilizado o método `getClaim`, da classe `JsonWebToken`. O `subject` do token é o MCI do usuário logado. Este tanto pode ser obtido invocando o método `getSubject()` quanto `getClaim("sub")`. Os atributos disponíveis,estão sujeitos a atualizações e podem ser conferidos [aqui](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/TokenAtendimentoDoc.md).


## Repasse do usuário para operações consumidas


Quando é necessário consumir uma operação dentro do provimento, é importante que haja o repasse de todos os headers do Curió para que, inclusive, o usuário permaneça logado nas operações posteriores.

Para repassar os headers no Quarkus, adicione na sua interface *rest client* do Curió a anotação `@RegisterClientHeaders` como no exemplo:


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

E inclua no seu `application.properties` a seguinte linha:

```
org.eclipse.microprofile.rest.client.propagateHeaders=Authorization,ESTADO-INTEGRACAO,INFO-CNL
```

Documentação [aqui](https://download.eclipse.org/microprofile/microprofile-rest-client-1.2.1/microprofile-rest-client-1.2.1.html#_jax_rs)

É importante ressaltar que a validação de identidade na operação chamadora não elimina a necessidade de validação em cada uma das operações que são invocadas a partir daquela requisição. Todas as operações que utilizam dados do token devem fazer verificações independentes.---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Curio_usuario.md&internalidade=iib/Curio_usuario)
