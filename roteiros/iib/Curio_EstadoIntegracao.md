> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Trabalhando com o header ESTADO-INTEGRACAO do iib Curio
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Curio_EstadoIntegracao.md&amp;action_name=iib/Curio_EstadoIntegracao)

Esse header possui informações transacionais do iib, por enquanto não temos como descriptografar seu conteudo.
Pois o uso das chaves para tal são de acesso mais restrito.
Por enquanto podemos apenas repassar o valor criptografado que recebemos ao prover uma operação iib ao consumo de outra dentro 
do mesmo fluxo de requisição.

Esse header tem uma prioridade menor quando outros headers são informados ao curio no consumo de outro operação, por exemplo,
se for enviado na mesma requisição o ESTADO-INTEGRACAO e o INFO-CNL, o conteudo do ESTADO-INTEGRACAO será desconsiderado e sera utilizado apenas o conteudo do INFO-CNL.

Para mais informações sobre o INFO-CNL e como utiliza-lo para obter as informações sobre o canal acesso o roteiro: [Obter e Repassar o canal em operações IIB Curio](./iib/Curio_InfoCnl.md) 


## Recuperar o ESTADO-INTEGRACAO

Para recuperar e armazenar o conteudo desse header precisamos ter duas classes uma para o filtro e outra armazenar o conteudo e uma classe para anotação.
Assim precisamos criar a classe IntegracaoFilter para o filtro, a classe EstadoIntegracao para armazenar o conteudo e anotação IntegracaoIIB para ser usada nos metodos utilizados para disponibilizar um endpoint e para executar a chamada a um endpoint, essas classes podem ficar dentro do pacote `br.com.bb.sua_sigla.seu_projeto.integration` do seu projeto.

Se seu projeto foi gerado ja com a versão 2 do quarkus essa implementação ja foi realizada seguindo esse mesmo padrão, mas unificado para obter o header INFO-INTEGRACAO.

### IntegracaoIIB
```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import org.eclipse.microprofile.opentracing.Traced;

import javax.interceptor.InterceptorBinding;
import javax.ws.rs.NameBinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação para utilizar nos metodos que realização ou recebem requisicoes http/rest para utilizar o filtro {@link IntegracaoFilter}
 * para receber e enviar dados de integração no header da requisição.
 */
@NameBinding
@InterceptorBinding
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface IntegracaoIIB {}
```

### EstadoIntegracao
```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import javax.enterprise.context.RequestScoped;
import java.util.Optional;

/**
 * Classe com escopo de request para armazenar o token do estado de integração, esse token esta criptografado e deve ser apenas
 * repassado do provimento da operação para o consumo de operação, ele possui informações de segurança.
 */
@RequestScoped
public class EstadoIntegracao {
	private String token = "";

	void setToken(String token) {
		this.token = Optional.ofNullable(token).orElse("");
	}

	public String getToken() {
		return this.token;
	}
}

```
### IntegracaoFilter com filtro para receber o header
```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filtro para recuperar os dados do estado de intregação.
 * Eles funcionam apenas nos endpoints que possuam a operação no path, ex: /op123456v1.
 * Os dados do Estado-Integração serão repassados automaticamente da operação de provimento para as operações de consumo quando utilizado
 * o rest-client.
 */
@Provider
@IntegracaoIIB
@ApplicationScoped
public class IntegracaoFilter implements ContainerRequestFilter {

	static final Logger LOG = LoggerFactory.getLogger(IntegracaoFilter.class.getName());
	public static final String REGEX_OP_IIB = "/op[0-9]+v[0-9]{1,2}";
	public static final String ESTADO-INTEGRACAO = "ESTADO-INTEGRACAO";

	EstadoIntegracao estadoIntegracao;

	public IntegracaoFilter(EstadoIntegracao estadoIntegracao){
		this.estadoIntegracao = estadoIntegracao;
	}

	/**
	 * Filtro para receber o estadoIntegracao para os endpoints que provem uma operação.
	 * @param requestContext contexto de request dos endpoints providos pela aplicação
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) {
		var path = requestContext.getUriInfo().getPath();

		if(!path.matches(REGEX_OP_IIB) ){
			return;
		}

		this.estadoIntegracao.setToken(requestContext.getHeaderString(ESTADO-INTEGRACAO));
	}

}

```
***Atenção:*** ajuste o nome do package de acordo com a sigla do seu projeto.


## Repassar o ESTADO-INTEGRACAO

Para o exemplo que vamos utilizar o unico requisito é usar o restclient do microprofile, essa lib facilita a configuração de chamadas rest usando uma interface que possui anotações para configuração da chamada rest, esse é padrão que utilizamos nos projetos gerados pelo Brave.
Caso não esteja usando esse padrão voce pode utilizar o filtro anterior e injetar o EstadoIntegracao para obter o token e repassa-lo no header `ESTADO-INTEGRACAO` da chamada http para o curio, que não sera demostrado nesse exemplo.

Além disso vamos utilizar a classe EstadoIntegração que criamos no exemplo de [recuperar estado integracao](#recuperar-o-estado-integracao) e a anotação `IntegracaoIIB`.

Mas para realizar o repasse precisamos alterar o filtro `IntegracaoFilter` para incluir o filtro para as requisicoes feitas a partir da aplicação para o curio usando o restclient conforme a classe abaixo:

### IntegracaoFilter com filtro para repassar o header

```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Filtro para recuperar os dados do info-integração e para repassar o token do estado de intregação.
 * Eles funcionam apenas nos endpoints que possuam a operação no path, ex: /op123456v1.
 * Os dados da info-integração serão armazenados na classe {@link InfoIntegracao} que possui o escopo de request.
 * Os dados do Estado-Integração serão repassados automaticamente da operação de provimento para as operações de consumo quando utilizado
 * o rest-client.
 * Outra função do filtro é a adição do codigo do canal no tracer da requisição
 */
@Provider
@IntegracaoIIB
@ApplicationScoped
public class IntegracaoFilter implements ContainerRequestFilter, ClientRequestFilter {

	static final Logger LOG = LoggerFactory.getLogger(IntegracaoFilter.class.getName());

	public static final String REGEX_OP_IIB = "/op[0-9]+v[0-9]{1,2}";
	public static final String ESTADO_INTEGRACAO = "ESTADO-INTEGRACAO";

	EstadoIntegracao estadoIntegracao;

	public IntegracaoFilter(EstadoIntegracao estadoIntegracao){
		this.estadoIntegracao = estadoIntegracao;
	}

	/**
	 * Filtro para receber o Estado-Integracao para os endpoints que provem uma operação.
	 * @param requestContext contexto de request dos endpoints providos pela aplicação
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) {
		var path = requestContext.getUriInfo().getPath();

		if(!path.matches(REGEX_OP_IIB) ){
			return;
		}

		this.estadoIntegracao.setToken(requestContext.getHeaderString(ESTADO_INTEGRACAO));
	}

	/**
	 * Filtro para repassar o estado de integração para o consumo de operacoes ibb
	 * @param clientRequestContext contexto de request dos endpoints do restclient
	 */
	@Override
	public void filter(ClientRequestContext clientRequestContext)  {
		//Se o path possuir o padrão /opXXXXvXX vai ser adicionar o token de estado de integração
		if(clientRequestContext.getUri().getPath().matches(REGEX_OP_IIB) && !estadoIntegracao.getToken().isBlank()){
			clientRequestContext.getHeaders().add(ESTADO_INTEGRACAO, estadoIntegracao.getToken());
		}
	}

}

```
***Atenção:*** ajuste o nome do package de acordo com a sigla do seu projeto.

## Utilização

A utilização dos filtros é feita incluindo a anotação `@IntegracaoIBB` nos metodos que são usados como endpoints da aplicação ou nos que são usado para chamar um endpoint do curio, outra forma é colocar na classe que vai ter os endpoint ou na intefarce que faz a integração com o Curio conforme os exemplos abaixo:


### Usando filtro na interface para chamar uma operação IIB
```java
package br.com.bb.sua_sigla.seu_projeto.rest.iib;

import br.com.bb.sua_sigla.seu_projeto.integration.IntegracaoIIB;

import br.com.bb.t99.operacao.cadastrarClienteV1.bean.requisicao.DadosRequisicaoCadastrarCliente;
import br.com.bb.t99.operacao.cadastrarClienteV1.bean.resposta.DadosRespostaCadastrarCliente;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import br.com.bb.dev.tracer.TracedWithRequestID;
import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/op5207120v1")
@RequestScoped
@TracedWithRequestID
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Tag(name = "Operações IIB", description = "Endpoints para provimento de Operação IIB")
public class Op5207120v1 {

    @POST
    @IntegracaoIIB
    @Operation(summary = "CadastrarCliente",
        description = "Descricao da CadastrarCliente alterar a descrição na anotacao @Operation do metodo proverOperacao da classe Op5207120v1.")
    public DadosRespostaCadastrarCliente proverOperacao(DadosRequisicaoCadastrarCliente requisicao) {
        var resposta = new DadosRespostaCadastrarCliente();
        return resposta;
    }
}
```

### Usando filtro na interface para chamar uma operação IIB
```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import br.com.bb.dev.erros.curio.CurioExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;

@ApplicationScoped
@RegisterRestClient(configKey = "curio-host")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@RegisterProvider(CurioExceptionMapper.class)
public interface ConsumidorCurio {

  @POST
  @Path("op252416v1")
  @IntegracaoIIB
  DadosRespostaConsultarDadosBasicosFuncionarioBB executarOp252416v1(
  DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao);

}
``` ---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Curio_EstadoIntegracao.md&internalidade=iib/Curio_EstadoIntegracao)
