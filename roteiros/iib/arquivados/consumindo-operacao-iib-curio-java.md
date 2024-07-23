> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/arquivados/consumindo-operacao-iib-curio-java.md&amp;action_name=iib/arquivados/consumindo-operacao-iib-curio-java)

# Consumo de operações IIB com Curió em projetos Java

Este guia explica como consumir operações IIB em projetos Java e o framework [Quarkus](https://quarkus.io/) através do Curió, com endpoints REST em um container *sidecar*. Você pode encontrar uma descrição mais detalhada de como o Curió funciona na [wiki](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/wikis/home).

## Configurando as dependências das operações

Se o seu projeto foi criado usando o [BB-DEV-Generator do Brave](https://brave.dev.intranet.bb.com.br/novo-projeto) com adição das operações consumidas, apenas verifique se o `pom.xml` possui em suas dependências os artefatos das operações. Caso deseja adicionar mais operações siga adiante, caso não pule para próxima sessão. 

O primeiro passo é incluir a dependência da operação IIB no arquivo `pom.xml` do seu projeto, a fim de permitir utilizar as classes de requisição e resposta da operação.

Neste exemplo utilizamos a operação 3821497, versão 1, da sigla wbb. Ela é uma operação de eco: recebe como argumento um texto e retorna como resposta o mesmo texto.

> Recomendamos que você faça este guia com uma operação da sua sigla. A operação eco é usada apenas para testes e nem sempre tem algum microsserviço cadastrado para provê-la, havendo grandes chances de sua aplicação receber um timeout ao tentar consumí-la.

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

> Após incluir esta dependência recomendamos que você faça o reload do POM na sua IDE para que você possa utilizar os recursos de auto-complete nas classes de requisição e resposta. Você pode fazer isto com o comando `mvn compile`.

## Configurando o Curió para consumir sua da operação

Agora precisamos configurar o Curió para criar o endpoint REST de consumo da operação. O código abaixo contém a configuração do Curió a ser incluida no seu docker-compose. O curió será incluido na seção services e deve estar na mesma rede da sua aplicação.

> Este guia foi feito utilizando a versão `0.8.3` do Curió. Recomendamos que você sempre confira na [wiki](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/wikis/home) qual é a última versão estável. Esta pode ter parâmetros novos a serem conferidos na própria wiki.

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

> No fragmento acima, foi utilizado um arquivo para definir as environments do curió, conforme última versão do template de projetos gerados pelo Brave. O arquivo se chama .env_curio e fica na raiz do projeto.

## Criando a interface para o consumo da operação no Curió

Você deverá criar uma `interface` que conterá as assinaturas dos endpoints a serem consumidos. No nosso caso, iremos consumir um endpoint chamado POST `op3821497v1`, provido pelo Curió.

Usamos a anotação `@Path` para especificar este endpoint. As classes de requisição e resposta para a operação estão na dependência maven que importamos.

As anotações `@Produces` e `@Consumes` indicam que este endpoint recebe como requisição um JSON e retorna um JSON. O Quarkus, através da sua implementação se encarregará de fazer as conversões entre o JSON e as classes Java de resposta e requisição.

```java
package br.com.bb.wbb.resources;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import br.com.bb.dev.erros.curio.CurioExceptionMapper;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.bb.wbb.operacao.echoWBBV1.bean.requisicao.DadosRequisicaoEchoWBB;
import br.com.bb.wbb.operacao.echoWBBV1.bean.resposta.DadosRespostaEchoWBB;

@RegisterRestClient
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RegisterProvider(CurioExceptionMapper.class)
public interface ConsumidorCurio {

    @POST
    @Path("op3821497v1")
    DadosRespostaEchoWBB executarOperacao(DadosRequisicaoEchoWBB requisicao);

}
```

Você pode incluir varias operações nessa mesma interface, e cada operação vai seguir o mesmo padrão

```java
@POST
@Path("operacao-versao")
DadosRespostaXXX executarOperacao(DadosRequisicaoXXX requisicao);
```

### 4. Configurando o consumo REST

O Quarkus precisa, ainda, saber o endereço do servidor que está provendo o endpoint que definimos na interface. Lembre-se que iremos consumir a operação a partir do Curió, na porta 8081.

No Quarkus a configuração é feita no arquivo `application.properties`, conforme abaixo:

```
br.com.bb.wbb.resources.InterfaceConsumidor/mp-rest/url=http://localhost:8081
```

Esse nome da propriedade pode ser pelo nome do pacote mais o nome da interface, contudo não pode ter mais que 64 caracteres.

Outra opção e usar o `(configKey = "curio-api")` logo apos a anotação `@RegisterRestClient` ,
E ao inves de usar o nome do pacote mais a interface, voce pode usar o nome `curio-api`, ficando assim

```
curio-api/mp-rest/url=http://localhost:8081
```

Ao incluir uma nova operação na aplicação, também é ncessário fazere a seguinte configuração para não utilizar as configurações de beans.xml do jar da operação(Substituir {sigla} pela sigla da operação. Se precisar adicionar outra exclusão basta incluir outro valor separado por vírgula):

```
# Exclusão das classes dos JAR de operações IIB da descoberta de beans CDI
quarkus.arc.exclude-types=br.com.bb.{sigla}.operacao.**
```

Para mais informações sobre essa configuração, acesse a documentação do [quarkus](https://quarkus.io/guides/cdi-reference#how-to-exclude-types-and-dependencies-from-discovery).

Observe que nas versões mais recentes de templates gerados pelo Brave, o modo de rede utilizado no `docker-compose` é o `host`. Dessa forma, o endereço do curió, tanto no ambiente local quando no Kubernetes será `localhost:8081`. Para configurações de `docker-compose` que usem uma rede do tipo `bridge`, no lugar de `localhost` deve ser usado o nome do conteiner.

## Consumindo sua operação com Curió

Agora que já temos tudo configurado para consumir a operação, vamos criar no nosso microserviço um endpoint que faz o consumo. No nosso exemplo faremos um endpoint `POST` bem simples chamado `consumidor`. Este endpoint receberá um texto, chamará internamente a operação IIB de eco e retornará para o usuário a resposta que este endpoint retornou.

Primeiro vamos criar um bean para mapear a nossa requisição.

```java
public class MinhaRequisicao  implements Serializable{

    private static final long serialVersionUID = 1L;

    private String texto;

    public String getTexto(){
        return this.texto;
    }

    public void setTexto(String texto){
        this.texto = texto;
    }
}
```

O próximo passo é criar a nossa classe que irá prover o endpoint `consumidor`.

```java
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Path("/v1/consumidor")
public class Consumidor {
...
}
```

Incluimos na nossa classe a interface de consumo que criamos anteriormente. Esta deve ser incluida via anotação `@Inject` conforme abaixo:

```java
@Inject
@RestClient
ConsumidorCurio sidecarConsumidor;
```

E finalmente criamos o método com a chamada interna ao endpoint de consumo do IIB.

```java
@POST
public Response consumirOperacao(MinhaRequisicao minhaRequisicao){

    DadosRequisicaoEchoWBB requisicao = new DadosRequisicaoEchoWBB();
    requisicao.setTextoDado(minhaRequisicao.getTexto());

    DadosRespostaEchoWBB resposta = sidecarConsumidor.executarOperacao(requisicao);

    return Response.status(Response.Status.OK).entity(resposta).build();
}
```

## Resultados Finais

Pronto. Agora podemos subir a nossa aplicação para testar (rodando o script `run.sh` do seu projeto). Chamando o endpoint POST da nossa aplicação pelo insomnia, temos o resultado abaixo:

![](./imagens/Consumo-RequisicaoComSucesso.png)

# Tags

## #guia #code #consumo #curio #operacao

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/consumindo-operacao-iib-curio-java.md&internalidade=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/consumindo-operacao-iib-curio-java)
