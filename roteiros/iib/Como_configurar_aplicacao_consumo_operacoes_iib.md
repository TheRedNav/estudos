> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_configurar_aplicacao_consumo_operacoes_iib.md&amp;action_name=iib/Como_configurar_aplicacao_consumo_operacoes_iib) 

# Como configurar a aplicação para consumir operações IIB via Curió

Este roteiro ensina como configurar sua aplicação para consumir operações IIB em projetos Java utilizando o framework Quarkus, por meio do Curió, com endpoints REST em um contêiner *sidecar*. Ele também traz um exemplo de configuração de consumo de operações IIB. 

## Passo 1: Configurar o docker-compose

1. Localize o arquivo **docker-compose.yaml** na sua IDE. Geralmente ele está na pasta **run**.
2. Dentro de **services**, inclua as informações do Curió.
> :bulb: **Dica** 
> 
> Sempre verifique qual é a última versão estável do [Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/wikis/home) para conferir eventuais parâmetros novos que precisem ser revisados.

```yaml
version: "3.4"
services:
  minha-aplicacao:
    container_name: minha-aplicacao
  ...
    network_mode: host
  iib-curio:
    container_name: iib-curio
    image: docker.binarios.intranet.bb.com.br/bb/iib/iib-curio:0.8.3
    env_file:
      - .././.env_curio
    ports:
      - "8081:8081"
    network_mode: host
```

## Passo 2: Configurar o .env_curio

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo **.env_curio**.
3. Adicione o parâmetro **CURIO_OP_CONSUMIDOR** com as informações da operação a ser consumida. No exemplo, a operação serve para consultar dados básicos de um funcionário BB.

```env
KUMULUZEE_SERVER_HTTP_PORT=8081
CURIO_CACHE_CONFIGURACAO_IIB=iib-slave.redis.bdh.desenv.bb.com.br
CURIO_CACHE_CONFIGURACAO_IIB_ID=iib:configuracao:k8s-integracao
CURIO_SIGLA_APLICACAO=t99
CURIO_APLICACAO_HOST=http://localhost:8080
CURIO_IIB_LOG_LEVEL=FINE
CURIO_DRY_RUN=false
CURIO_MODO_DESENVOLVIMENTO=true
KUMULUZEE_LOGS_LOGGERS0_NAME=br.com.bb
KUMULUZEE_LOGS_LOGGERS0_LEVEL=TRACE
#CURIO_OP_PROVEDOR=
CURIO_OP_CONSUMIDOR=br.com.bb.arh.operacao:Op252416-v1:2.0.0
```
> :bulb: **Dica** 
> 
> Para obter os códigos referentes à operação desejada, vá para o [Catálogo de Operações](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=19&app=ctlCatalogoOperacoes) na Plataforma de Atendimento. Após pesquisar pela operação, acesse-a e clique no botão **Dependência Maven** para visualizar os trechos a serem incluídos em cada arquivo.

## Passo 3: Configurar o pom.xml

> :grey_exclamation: **Importante** 
> 
> Se o projeto foi criado através do [BB-DEV-Generator do Brave](https://brave.dev.intranet.bb.com.br/novo-projeto) e as operações consumidas foram adicionadas, apenas verifique se o arquivo **pom** inclui os artefatos das operações nas dependências e siga para o próximo passo. 

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo **pom.xml**.
3. Em **dependencies**, inclua a dependência da operação IIB para utilizar as classes de requisição e resposta da operação. 

```xml
<dependencies>
   ...
 <dependency>
  	<groupId>br.com.bb.arh.operacao</groupId>
  	<artifactId>Op252416-v1</artifactId>
  	<version>2.0.0</version>
</dependency>
   ...
</dependency>
```
4. Após incluir a dependência, use o comando **mvn compile** para fazer o *reload* do **pom** na sua IDE. Isso permitirá o uso dos recursos de *auto-complete* nas classes de requisição e resposta.

## Passo 4: Criar a interface 

1. No pacote de classes de integração de seu projeto, crie um arquivo **.java**. Esse arquivo definirá a interface que contêm as assinaturas dos endpoints a serem consumidos. No exemplo, a configuração consome um endpoint chamado **POST op252416v1**, provido pelo Curió.
2. Inclua a anotação **@Path** para especificar o endpoint. As classes de requisição e resposta para a operação estão na dependência Maven importada.
3. Inclua a anotação **@RegisterRestClient** com a configuração **(configKey = "curio-api")**.
4. Inclua as anotações **@Produces** e **@Consumes**, ambas com o conteúdo **(MediaType.APPLICATION_JSON + ";charset=utf-8")** para indicar que o endpoint recebe e retorna um JSON em suas requisições e respostas.

> :information_source: **Observação** 
> 
> O Quarkus, através da sua implementação, faz automaticamente as conversões entre o JSON e as classes Java de resposta e requisição por meio da serialização.

```java
package br.com.bb.t99.integracao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.dev.erros.rest.filter.curio.CurioExceptionMapper;

@ApplicationScoped
@RegisterRestClient(configKey = "curio-api")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@RegisterProvider(CurioExceptionMapper.class)
public interface ConsumidorCurio {

    @POST
    @Path("op252416v1")
    @IntegracaoIIB
    DadosRespostaConsultarDadosBasicosFuncionarioBB executarOperacao(
    DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao);
  
  }
```
> :information_source: **Observação** 
> 
> Você pode incluir várias operações na mesma interface, e cada operação seguirá o mesmo padrão.
>
> ```java
> @POST
> @Path("operacao-versao")
> DadosRespostaXXX executarOperacao(DadosRequisicaoXXX requisicao);
> ```

## Passo 5: Configurar o application.properties

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo **application.properties**.
3. Adicione o seguinte código ao arquivo:
```
curio-api/mp-rest/url=http://localhost:8081
```
4. Ao adicionar uma nova operação à aplicação, é crucial configurar para que ela não utilize as configurações do **beans.xml** do **.jar** da operação. Para adicionar mais exclusões, basta incluir outros valores separados por vírgula.

```
# Exclusão das classes dos JAR de operações IIB da descoberta de beans CDI
quarkus.arc.exclude-types=br.com.bb.{sua-sigla}.operacao.**
```

> :bulb: **Dica** 
> 
> Para mais informações sobre essa configuração, acesse a documentação do [Quarkus](https://quarkus.io/guides/cdi-reference#how-to-exclude-types-and-dependencies-from-discovery).

> :information_source: **Observação** 
> 
> Nas versões mais recentes de templates gerados pelo Brave, o modo de rede utilizado no *docker-compose* é o *host*. Portanto, o endereço do Curió no ambiente local e no Kubernetes será *localhost:8081*. <br>
> Para configurações de *docker-compose* que usam redes do tipo *bridge*, no lugar de *localhost*, deve-se usar o nome do contêiner.

## Passo 6: Consumir a operação com o Curió

> :information_source: **Observação** 
> 
> O passo a seguir é um modelo de operação de consumo POST, criado com o propósito de concluir o roteiro. É importante ressaltar que este passo irá variar conforme os outros tipos de operações de consumo, como GET, PUT, DELETE, entre outros. Cada tipo de operação exige configurações adicionais/distintas para atender às necessidades específicas da aplicação.

1. Navegue até a pasta **/src/main/.../rest** do seu projeto.
2. Crie um arquivo **.java**. No exemplo, foi criado um endpoint POST chamado **consumidor**. Este endpoint será responsável por receber dados, invocar internamente a operação IIB para consultar informações básicas do funcionário do BB e, por fim, retornar a resposta para o usuário.
3. Dentro do arquivo recém-criado, inclua a classe que irá prover o endpoint:

```java
@RequestScoped
@Path("consumidor")
public class ConsumidorResource {
...
}
```
4. Na classe, adicione a interface de consumo criada anteriormente utilizando a anotação **@Inject**.

```java
@Inject
@RestClient
ConsumidorCurio sidecarConsumidor;
```
5. Crie o método que realiza a chamada interna ao endpoint de consumo do IIB:

```java
@POST
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@IntegracaoIIB
public Response operacao(DadosRequisicaoConsultarDadosBasicosFuncionarioBB dados){
    return Response.status(Response.Status.OK).entity(sidecarConsumidor.executarOperacao(dados)).build();     
}
```

6. Salve o arquivo e rode o script **run.sh** para executar o projeto.
7. Utilize um software de teste de APIs para testar o consumo da operação.

**Tags:** #iib #consumo #curio #operacao

## A Seguir

* Consulte o [repositório de referências](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/c1337189-versaobeta/iib/referencias) para mais detalhes dos arquivos utilizados nas configurações deste roteiro.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_configurar_aplicacao_consumo_operacoes_iib.md&internalidade=iib/Como_configurar_aplicacao_consumo_operacoes_iib)



