> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Trabalhando com o header INFO_CNL do iib Curio
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Curio_InfoCnl.md&amp;action_name=iib/Curio_InfoCnl)
## Recuperar o Canal

Ao prover uma operação iib é possivel saber o canal que gerou a requisição, para isso é necessario utilizar a versao 0.6.5 do curio ou superior.

Essa informação é repassada pelo curio no header da requisição quando o mesmo realiza a chamada para a operação que esta sendo provida.

A seguir temos as classes necessarias para realizar a leitura desse header se voce estiver usando o quarkus no seu projeto, caso contrario tente fazer um filtro parecido como que descrevemos a seguir.

Crie um pacote como `br.com.bb.sua_sigla.seu_projeto.integration`.
Nele voce vai criar 3 classes, uma anotacao, a classe com os dados do header, e o filtro.

- A classe de anotacao IntegracaoBB vai ser usada para indicar os endpoints que vao ter esse comportamento.

- A classe InfoIntegracao vai ser uma RequestScopped para armazenar os dados do header, ou seja vai existir uma instancia dessa classe por requisição.

- A classe IntegracaoFilter vai ser o filtro que vai pegar a requisicao que foi anotada com @IntegracaoBB e vai extrair os dados do header da requisicao e preencher o InfoIntegracao.

- A classe EstadoIntegracao será o guardião do token filtrado, permitindo seu uso futuro.

Para ler o conteudo do header precisa adicionar a seguintes dependencias no seu pom.xml na tag <dependencies>.

> Observação: estamos usando o jackson como conversor de json, caso esteja usando outro vai ser preciso ajustar para lib que vc tem preferencia.

``` xml
    <!-- Dependências do JWT -->
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-api</artifactId>
      <version>0.11.2</version>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-impl</artifactId>
      <version>0.11.2</version>
    </dependency>
    <dependency>
      <groupId>io.jsonwebtoken</groupId>
      <artifactId>jjwt-jackson</artifactId>
      <version>0.11.2</version>
    </dependency>
```


### InfoIntegracao
```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.enterprise.context.RequestScoped;

/**
 * Classe com escopo de request para armazenar os dados obtidos do header com as informações de integração.
 */
@RequestScoped
public class InfoIntegracao {

	public static final int INTERFACE_CANAL_PLATAFORMA = 55;
	public static final int INTERFACE_CANAL_COBOL= 3;
	public static final int INTERFACE_CANAL_APF_WEB= 12;
	public static final int INTERFACE_CANAL_APF_MOBILE = 20;
	public static final int INTERFACE_CANAL_HBK = 67;
	public static final int INTERFACE_CANAL_API = 68;
	public static final int INTERFACE_API_IMPL = 29;

	static final String INTERFACE_CANAL = "areaNaoAutenticada.infoCnl.interfaceCanal";
	static final String IMPL_CANAL = "areaNaoAutenticada.infoCnl.implementacaoInterfaceCanal";
	static final String TICKET_CNL = "areaNaoAutenticada.infoCnl.ticketCanal";

	Map<String, Object> propriedades = new HashMap<>();

	public int getCodigoInterfaceCanal(){
		return getAsInt(INTERFACE_CANAL);
	}

	public int getImplementacaoInterfaceCanal(){
		return getAsInt(IMPL_CANAL);
	}

	public String getTicketCanal(){
		return getAsString(TICKET_CNL);
	}

	void putAll(Map<String, Object> map){
		propriedades.putAll(map);
	}

	public Object get(String nomeProp){
		return propriedades.get(nomeProp);
	}

	public String getAsString(String nomeProp){
		return String.valueOf(this.get(nomeProp));
	}

	public int getAsInt(String nomeProp){
		return Integer.parseInt(String.valueOf(propriedades.getOrDefault(nomeProp,0)));
	}

	@Override
	public String toString() {
		return InfoIntegracao.class.getSimpleName() + "[" +
				propriedades.entrySet().stream()
						.map(e -> e.getKey() + "=" + e.getValue())
						.collect(Collectors.joining(",")) + "]";
	}
}


```

### Anotação @IntegracaoBB
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
 * Tambem inclui a anotação para realização do Tracer da aplicação
 */
@Traced
@NameBinding
@InterceptorBinding
@Retention(value = RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD })
public @interface IntegracaoIIB {}
```

### Filtro IntegracaoFilter

```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.jackson.io.JacksonDeserializer;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.ext.Provider;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.jsonwebtoken.security.SignatureException;
import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static br.com.bb.dev.integration.InfoIntegracao.INTERFACE_CANAL;

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

	static final ObjectMapper objectMapper = new ObjectMapper().setSerializationInclusion(Include.NON_EMPTY)
			.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
	static final JwtParser jwtDeserializer = Jwts.parserBuilder()
			.deserializeJsonWith(new JacksonDeserializer<>(objectMapper)).build();

	static final String HEADER_NAO_ENCONTRADO = "O header: {} não foi encontrado no contexto na chamada ao endpoint: {}.";
	static final String ERRO_HEADER = "Erro na utilização do header: %s na chamada ao endpoint: %s.";

	public static final String INFO_INTEGRACAO = "info-integracao";
	public static final String INFO_CNL = "infoCnl";
	public static final String AREA_NAO_AUTENTICADA = "areaNaoAutenticada";
	public static final String REGEX_OP_IIB = "/op[0-9]+v[0-9]{1,2}";
	public static final String ESTADO_INTEGRACAO = "ESTADO-INTEGRACAO";

	InfoIntegracao infoIntegracao;
	EstadoIntegracao estadoIntegracao;
	Tracer tracer;

	public IntegracaoFilter(InfoIntegracao infoIntegracao, EstadoIntegracao estadoIntegracao, Tracer tracer){
		this.infoIntegracao = infoIntegracao;
		this.estadoIntegracao = estadoIntegracao;
		this.tracer = tracer;
	}

	/**
	 * Filtro para receber o info-integração para os endpoints que provem uma operação.
	 * @param requestContext contexto de request dos endpoints providos pela aplicação
	 */
	@Override
	public void filter(ContainerRequestContext requestContext) {
		var path = requestContext.getUriInfo().getPath();

		if(!path.matches(REGEX_OP_IIB) ){
			return;
		}

		if(!requestContext.getHeaders().containsKey(INFO_INTEGRACAO)){
			LOG.debug(HEADER_NAO_ENCONTRADO,INFO_INTEGRACAO, path);
			return;
		}

		try {
			//Atribuindo o token estado integracao
			this.estadoIntegracao.setToken(requestContext.getHeaderString(ESTADO_INTEGRACAO));
			infoIntegracao.putAll(flatten(jwtDeserializer.parseClaimsJwt(requestContext.getHeaderString(INFO_INTEGRACAO)).getBody()));
			//Incluindo o canal da requisicao no tracer
			tracer.activeSpan().setTag(INTERFACE_CANAL, infoIntegracao.getCodigoInterfaceCanal());
		} catch (ExpiredJwtException | MalformedJwtException | SignatureException | IllegalArgumentException jwtException){
			LOG.error(String.format(ERRO_HEADER, INFO_INTEGRACAO, path), jwtException);
		}
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

	/**
	 * Metodo para fazer o "achatamento" das propriedades do conteudo do header em um map, exemplo, caso uma propriedade seja
	 * composta por outras esse metodo vai concatenar as chaves usando "." como separador ou "[]" como indicador de elementos em uma
	 * lista. No caso da interfaceCanal que esta dentro do infoCnl que por sua vez faz parte da areaNaoAutenticada, a chave da propriedade
	 * ficaria assim areaNaoAutenticada.infoCnl.interfaceCanal.
	 * @param mapToFlat
	 * @return Map com o nome da propriedade e o valor
	 */
	Map<String,Object> flatten(Map<String, Object> mapToFlat){
		return mapToFlat.entrySet().stream()
				.flatMap(this::flatten).collect(LinkedHashMap::new,
						(map, entry) -> map.put(entry.getKey(), entry.getValue()),
						LinkedHashMap::putAll);
	}

	Stream<Map.Entry<String,Object>> flatten(Map.Entry<String, Object> entry){
		if(entry==null){
			return Stream.empty();
		}

		Object value = entry.getValue();

		if(value instanceof Map<?,?>){
			Map<?,?> properties = (Map<?,?>) value;
			return properties.entrySet().stream()
					.flatMap(e-> flatten(new AbstractMap.SimpleEntry<>(entry.getKey()+"."+e.getKey(), e.getValue())));
		}

		if(value instanceof List<?>){
			List<?> list = (List<?>) value;
			return IntStream.range(0, list.size())
					.mapToObj(i -> new AbstractMap.SimpleEntry<String, Object>(entry.getKey() + "[" + i + "]", list.get(i)))
					.flatMap(this::flatten);
		}
		return Stream.of(entry);
	}

}
```

### Filtro EstadoIntegracao

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

Depois de incluir as classes acima vc podera utiliza-las da seguinte maneira.

Adicione a anotacao @IntegracaoBB no metodo que é responsavel pelo endpoint, conforme o exemplo:

```java
    @IntegracaoBB
    @POST
    @APIResponse(responseCode = "200", description = "Listar contratos de Assessoria",
        content = { @Content(mediaType = "application/json", 
        schema = @Schema(implementation = DadosRespostaListarContratosAssessoriaFinanceira.class))})
    public DadosRespostaListarContratosAssessoriaFinanceira servir(
        @RequestBody(required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON, 
                     schema = @Schema(implementation = DadosRequisicaoListarContratosAssessoriaFinanceira.class)))DadosRequisicaoListarContratosAssessoriaFinanceira requisicao) throws BBException {      
        validarEntrada(requisicao);

        DadosRespostaListarContratosAssessoriaFinanceira resposta = tratarRequisicao(requisicao);

        return resposta;
    }
```

Para verificar o conteudo do header voce deve injetar a classe InfoIntegracao na classe que vai fazer uso do canal, conforme o exemplo abaixo, lembrando que ela é do escopo de requisição e que cada requisição vai possuir um conjunto proprio de informações

```java
  @Inject
	InfoIntegracao info;

 private void validarEntrada(DadosRequisicaoListarContratosAssessoriaFinanceira requisicao) throws BBException {
    switch (info.getInterfaceCanal()) {
      case InfoIntegracao.INTERFACE_CANAL_PLATAFORMA: 
      case InfoIntegracao.INTERFACE_CANAL_COBOL:
          if(requisicao.getCodigoClienteContrato() == 0 ){
              throw new BBException("001","Campo Código Cliente Carteira inválido ou não informado.", Map.of("NOME-CAMPO", "Código Cliente Carteira"));

          }
          break;

      case InfoIntegracao.INTERFACE_CANAL_APF_MOBILE:
      case InfoIntegracao.INTERFACE_CANAL_APF_WEB:
      case InfoIntegracao.INTERFACE_CANAL_HBK:
          if(requisicao.getNumeroAgenciaContaCorrenteContrato() == 0 ){
              throw new BBException("001","Campo Número Agência inválido ou não informado.", Map.of("NOME-CAMPO", "Número Agência"));

          }
          if(requisicao.getNumeroContaCorrenteContrato() == 0 ){
              throw new BBException("001","Campo Número da Conta Corrente inválido ou não informado.", Map.of("NOME-CAMPO", "Número Conta corrente"));

          }    
          break;

      default:
          throw new BBException("001","Campo Código do Canal inválido ou não informado.", Map.of("NOME-CAMPO", "Canal da Interface"));

      }
    }
```

## Repassar a informação do Canal

No caso do repasse de informação do canal, isso pode ser feito repassando o que foi recebido ou criando um novo.
Mas nos dois casos é necessario que o consumo da operação seja feito usando o [rest-client do microprofile](https://download.eclipse.org/microprofile/microprofile-rest-client-2.0/microprofile-rest-client-spec-2.0.html), pois com ele podemos criar os filtros necessarios para repassar a informação no header da requisição, no caso preenchendo o parametro 'INFO-CNL' no header da requisicao para o curio.

### Alterar pom.xml

Primeiro adicione a dependencia abaixo no seu pom.xml dentro da tag <dependencies>.

```xml
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.11.0</version>
</dependency>
```

### Criando um novo Info_CNL
Esse sera para quando voce precisa gerar um novo token e não repassar um existente.

Caso esteja usando a InterfaceConsumidor/ConsumidorCurio do RestClient para comunicar com o Curio,
esse é o modelo que geramos quando usamos bbdev-generator que geralmente fica localizado no pacote `br.com.bb.sua_sigla.seu_projeto.interfaces` ou `br.com.bb.dev.integration`.

Nesse caso voce precisa modificar o metodo que faz o consumo da operação, adicione a anotacao `@ClientHeaderParam(name="INFO-CNL", value="{geraTokenJwt}")` e o metodo gerarTokenJwt conforme o exemplo abaixo:

```java
  // Metodo que vai precisar o token.    
  @POST
  @Path("/op2215450v1")
  @ClientHeaderParam(name="INFO-CNL", value="{geraTokenJwt}")
  DadosRespostaCancelarOperacaoPendenteAutorizacao executarOperacao(DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao) throws CurioNegocioException;

 default String geraTokenJwt(String headerName) {
        if ("INFO-CNL".equals(headerName)) {
        	String jwt = JWT.create()
                  // Interface do canal de Atendimento: 0068 API-PROGRAMAçãO DE APLIC
                  .withClaim("interfaceCanal", 68)
                  // Implementação da Interface: 0029 API0001
        	        .withClaim("implementacaoInterfaceCanal", 29)
        	        .withClaim("idiomaCanal", 2 )
        	        .withClaim("ticketCanal", "")
        	        .sign(Algorithm.none());
        	return jwt;
        }
        throw new UnsupportedOperationException("Nome do Header desconhecido");
    }

```

Essa anotação vai incluir o parametro `INFO_CNL` com o valor que vai ser gerado no metodo gerarTokenJwt.

### Repassando um Info_CNL ja existente

Caso esteja usando a InterfaceConsumidor do RestClient para comunicar com o Curio, esse é o modelo que geramos quando usamos bbdev-generator que geralmente fica localizado no pacote `br.com.bb.sua_sigla.seu_projeto.interfaces`.

Essa solução depende da implementação citada no topico acima [Recuperar o Canal](#recuperar-o-canal).

Nesse caso voce precisa modificar o metodo que faz o consumo da operação, adicione o parametro com a anotação @HeaderParam("INFO-CNL").
Observe no exemplo abaixo usado na operação op2215450v1:

```java
  @POST
  @Path("/op2215450v1")
  DadosRespostaCancelarOperacaoPendenteAutorizacao executarOperacao(@HeaderParam("INFO-CNL") String headerInfoCnl, 
    DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao) throws CurioNegocioException;
```

Nessa situação toda vez que for preciso chamar o metodo executarOperacao vai ser preciso informar o headerInfoCnl.

Para criar um headerInfoCnl vai ser preciso criar a classe abaixo no pacote `br.com.bb.sua_sigla.seu_projeto.integration`, citado no [Recuperar o Canal](#recuperar-o-canal).


Classe para gerar o token usado no INFO_CNL:

```java
package br.com.bb.sua_sigla.seu_projeto.integration;

import javax.enterprise.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RequestScoped
public class GeradorHeaderHeimdall {

  private static final Logger LOG = LoggerFactory.getLogger(GeradorHeaderHeimdall.class.getName());
  private static final String IDENTIFICADOR_HEADER_INFO_CNL = "INFO-CNL";

  @Inject
  InfoIntegracao infoIntegracao;

  public String geraTokenJwtInfoIntegracao() {
    return geraTokenJwt(IDENTIFICADOR_HEADER_INFO_CNL, infoIntegracao);
  }

  public String geraTokenJwtInfoIntegracao(InfoIntegracao dados) {
    return geraTokenJwt(IDENTIFICADOR_HEADER_INFO_CNL, dados);
  }

  private String geraTokenJwt(String headerName, InfoIntegracao dados) {
        if (IDENTIFICADOR_HEADER_INFO_CNL.equals(headerName)) {
          String jwt = null;
          if(isValido(dados)) {
            jwt = JWT.create()
              .withClaim("interfaceCanal", dados.getInterfaceCanal())
              .withClaim("implementacaoInterfaceCanal", dados.getImplementacaoInterfaceCanal())
              .withClaim("idiomaCanal", 2 )
              .withClaim("ticketCanal", dados.getTicketCanal())
              .sign(Algorithm.none());
          } else {
            jwt = JWT.create()
              .withClaim("interfaceCanal", InfoIntegracao.INTERFACE_CANAL_API)
              .withClaim("implementacaoInterfaceCanal", InfoIntegracao.INTERFACE_API_IMPL)
              .withClaim("idiomaCanal", 2 )
              .withClaim("ticketCanal", "")
              .sign(Algorithm.none());
          }
          return jwt;
        }
        throw new UnsupportedOperationException("Nome do Header desconhecido");
    }

  private boolean isValido(InfoIntegracao dados) {
    return dados != null && 
      dados.getImplementacaoInterfaceCanal() > 0 &&
      dados.getInterfaceCanal() > 0;
  }
}
```

Depois é necessario usar esse gerador para criar o token, mas ele so vai funcionar se estiver dentro do mesmo contexto aonde houve a captura dos dados presentes no InfoIntegração, segue o exemplo abaixo:

```java

  // inclua esse inject para usar o gerador de token
  @Inject
  private GeradorHeaderHeimdall gerarToken;

  @Inject
  @RestClient
  InterfaceConsumidor curioConsumidor;

  public void metodoExemplo(){
    
    DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao = new DadosRequisicaoCancelarOperacaoPendenteAutorizacao();
    // Implementacao do seu codigo

    // Exemplo de geracao do token e repasse para o metodo que precisa dele.
    curioConsumidor.executarOperacao( gerarToken.geraTokenJwtInfoIntegracao(), requisicao);
  }

```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Curio_InfoCnl.md&internalidade=iib/Curio_InfoCnl)
