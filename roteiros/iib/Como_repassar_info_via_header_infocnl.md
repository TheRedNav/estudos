> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_repassar_info_via_header_infocnl.md&amp;action_name=iib/Como_repassar_info_via_header_infocnl)

# Como repassar informações do canal via header de requisição

Este roteiro ensina como repassar informações do canal em operações IIB via header (cabeçalho) de requisição. Algumas operações exigem que o canal seja informado para funcionarem corretamente.

> :bulb: **Dica** 
> 
> Para saber qual canal gerou a requisição ao prover uma operação IIB, é necessário utilizar a versão 0.6.5 do Curió ou superior. Essa informação é repassada pelo Curió no header da requisição quando ocorre a chamada para a operação provida.
 
## Requisitos

* Um projeto gerado pelo Brave com a pasta Integration.
	*	Ao selecionar um **Novo Projeto**, no **Passo 3 - Opções adicionais**, selecione prover ou consumir operações com IIB/Curió para acessar pasta **Integration**.
* Usar o [rest-client do microprofile](https://download.eclipse.org/microprofile/microprofile-rest-client-2.0/microprofile-rest-client-spec-2.0.html). 
> :information_source: **Observação**
> 
> Este rest-client cria os filtros necessários para repassar a informação no cabeçalho da requisição, através do preenchimento do parâmetro **INFO-CNL** no cabeçalho da requisição para o Curió.
* Dependência atualizada conforme o [arquivo pom.xml](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/pom.xml).
* Possuir as classes necessárias. Projetos gerados pelo Brave já vêm com estas classes. Usuários com projetos antigos devem verificar se seus projetos Quarkus possuem as classes listadas abaixo:

| Classe | Descrição |
| --- | --- |
| **[IntegracaoIIB](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/IntegracaoIIB.java)** | Anotação usada para indicar os endpoints que terão esse comportamento. |
| **[InfoIntegracao](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/InfoIntegracao.java)** | RequestScoped para armazenar os dados do header. Isso significa que há uma instância dessa classe por requisição.
| **[IntegracaoFilter](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/IntegracaoFilter.java)** | Filtro que intercepta as requisições anotadas com **@IntegracaoIIB**, extraindo os dados do header da requisição e preenchendo o objeto **InfoIntegracao**. |
| **[EstadoIntegracao](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/EstadoIntegracao.java)** | Classe responsável por guardar o token filtrado, permitindo seu uso futuro. |

> :information_source: **Observação**
> 
> Se seu projeto não usa o Quarkus, é possível criar um filtro semelhante através da geração de um pacote **br.com.bb.sua_sigla.integration**, com as classes acima.

## Repassar informações do canal 

### Passo 1: Recuperar o conteúdo do header

1. Na interface **ConsumidorCurio**, confirme que a anotação `@IntegracaoIIB` foi adicionada no método responsável pelo endpoint da sua operação.
2. Injete a classe `InfoIntegracao` na classe que fará uso do canal.

```java
  @Inject
	InfoIntegracao info;

 //exemplo de uso das informações do canal
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
> :information_source: **Observação**
> 
> Lembre-se de que esta informação está no escopo da requisição e que cada requisição terá seu próprio conjunto de informações.

## Passo 2: Enviar as informações do canal

As informações enviadas via header de requisição podem incluir tanto dados recebidos que precisam ser encaminhados, quanto informações novas que o usuário cria e deseja enviar.

### 2.1: Criar um novo Info_CNL

1. Na sua IDE, vá para **src > main > java > .... > integration**.
2. Localize o arquivo **ConsumidorCurio**.
3. Modifique o método de consumo da operação, adicionando a anotação `@ClientHeaderParam(name="INFO-CNL", value="{geraTokenJwt}")` e o método `geraTokenJwt`. 

Veja que essa anotação inclui o parâmetro **INFO_CNL** com o valor que será gerado no método **geraTokenJwt**.

```java 
  @POST
  @Path("/op2215450v1")
  @ClientHeaderParam(name="INFO-CNL", value="{geraTokenJwt}")
  DadosRespostaCancelarOperacaoPendenteAutorizacao executarOperacaoNovoInfoCnl(DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao);

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

### 2.2: Repassar um Info_CNL existente

1. Na sua IDE, vá para **src > main > java > .... > integration**.
2. Crie uma classe conforme o arquivo **[GeradorHeaderHeimdall](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/referencias/roteiro_infocnl/GeradorHeaderHeimdall.java)**.
3. Em **integration**, localize o arquivo **ConsumidorCurio**. 
4. Altere o método de consumo de operação, adicionando o parâmetro `String headerInfoCnl` junto da anotação `@HeaderParam("INFO-CNL")`.

```java
  @POST
  @Path("/op2215450v1")
  DadosRespostaCancelarOperacaoPendenteAutorizacao executarOperacaoRepassarInfoCnl(@HeaderParam("INFO-CNL") String headerInfoCnl, 
                                                                                   DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao);
```

> :grey_exclamation: **Importante** 
> 
> Nos cenários de repasse de informações, toda vez que você chamar o método **executarOperacaoRepassarInfoCnl**, deve informar o **headerInfoCnl**.

5. Na classe do seu projeto onde a operação é consumida, injete a classe `GeradorHeaderHeimdall`.
6. Injete também a interface `ConsumidorCurio`.
7. Gere um token e repasse-o para o método que o necessita.

```java
// inject para usar o gerador de token
  @Inject
  private GeradorHeaderHeimdall gerarToken;

  @Inject
  @RestClient
  ConsumidorCurio consumidorCurio;

  public void metodoExemplo(){
    
    DadosRequisicaoCancelarOperacaoPendenteAutorizacao requisicao = new DadosRequisicaoCancelarOperacaoPendenteAutorizacao();

    // Exemplo de geracao do token e repasse para o metodo que precisa dele.
    consumidorCurio.executarOperacaoRepassarInfoCnl(gerarToken.geraTokenJwtInfoIntegracao(), requisicao);
  }
```
> :grey_exclamation: **Importante** <br>
> 
> Use este gerador para criar o token e garanta de que ele esteja dentro do mesmo contexto onde os dados presentes no **InfoIntegraçao** foram capturados.

Com a conclusão desses passos, o funcionamento adequado no consumo de operações que dependem da recuperação e repasse das informações de canal estará assegurado.

**Tags:** #header #curio #operacao #iib

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_repassar_info_via_header_infocnl.md&internalidade=iib/Como_repassar_info_via_header_infocnl)
