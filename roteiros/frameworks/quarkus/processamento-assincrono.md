> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Processamento assíncrono
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/processamento-assincrono.md&amp;action_name=frameworks/quarkus/processamento-assincrono)

"Assíncrono", no contexto de software, é o processamento ou a técnica utilizada em sua implementação que desvincula a resposta da requisição, fazendo com que o usuário/sistema chamador não precise aguardar em modo bloqueado um processamento acabar.

Uma das formas mais utilizadas de se implementar processamento assíncrono é com o uso de MQ (Message queue), que tem, entre suas vantagens, o fato de poder interligar diferentes sistemas e até mesmo diferentes plataformas. Essa solução se torna exagerada se não estão presentes as necessidades citadas.

Entre os requisitos que podem levar à correta escolha de uma implementação de processamento assíncrono, estão:

- Processamento muito demorado, gerando risco de timeout no cliente
- Processamento não crítico. Por exemplo, enviar uma notificação de operação realizada: além de não ser desejável ser afetado por eventuais demoras no envio, se o envio da notificação falhar  a operação principal não pode ser abortada


## Quarkus

A forma mais óbvia de implementação de processamento assíncrono em Java é manipulando threads. No entanto, essa não é nem a maneira mais fácil nem a melhor. Manipular threads diretamente pode gerar estados indesejados na aplicação e prejudicar o desempenho dos pods, uma vez que uma thread em Java é equivalente a uma thread de SO. Normalmente em aplicações de negócio, delegamos ao framework a manipulação das threads.

Implementar processamento assíncrono em Quarkus é muito simples utilizando o Vert.x.

Para tornar o Vert.x disponível no seu projeto, adicione a extensão como dependência:

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-vertx</artifactId>
    </dependency>
```

Na classe que vai disparar o processamento, utilize o CDI para obter referência ao `EventBus`:

```java
  @Inject
  EventBus bus;
```

Este é o import da classe:

```java
import io.vertx.core.eventbus.EventBus;
```

Nesse exemplo da aplicação de selo digital, temos um endpoint que solicita a avaliação de um namespace mas não espera a resposta, retornando imediatamente um código HTTP 202 (accepted):

```java
  @POST
  @Path("/manual/{namespace}")
  @Operation(summary = "Aplicar Validação do Selo no namespace.",
      description = "Executa a pesquisa das informações de codificação, build, release e execução "
          + "para aplicação as validações das regras para obter o Selo digital do namespace informado ")
  @APIResponse(responseCode = "202", description = "Solicitação aceita e em processamento.")
  public Response aplicarSeloManual(@PathParam("namespace") String namespace) {
    bus.send("aplicarSelo", namespace);
    return Response.status(Status.ACCEPTED).build();
  }
```

Do outro lado, temos a classe que recebe a requisição de processamento, enquanto o cliente já recebeu como resposta um HTTP status 202 com o corpo da resposta vazio:

```java
	@ConsumeEvent(value = "aplicarSelo", blocking = true)
	public void aplicarSeloManual(String namespace) {
		avaliarSeloManualmente(namespace);
	}
```

A anotação `ConsumeEvent` tem esse import:

```java
import io.quarkus.vertx.ConsumeEvent;
```

Pronto! Está implementado o processamento de forma assíncrona.

Para aplicações com requisitos muito estritos de latência e vazão, vale a pena conhecer o [Mutiny](https://quarkus.io/guides/mutiny-primer)


### Parametrização de tempo máximo de worker thread do Vert.x

Caso o processo que será executado de maneira assíncrona no método anotado pelo `@ConsumeEvent` demore mais de 60 segundos, no exemplo o `aplicarSeloManual()`, começará a aparecer um warning parecido com este no log da sua aplicação:
```
20:33:30 WARN  [io.ve.co.im.BlockedThreadChecker] (vertx-blocked-thread-checker)  Thread Thread[extracao-in-pool-0,5,main] has been blocked for 303974 ms, time limit is 60000 ms: io.vertx.core.VertxException: Thread blocked
```
É apenas um warinng e, em princípio, não vai afetar a aplicação. Mas, se você souber que a thread pode demorar mais do que 60 segundos, é possível configurar o parâmetro abaixo:
```
# The maximum amount of time the worker thread can be blocked. O default são 60 segundos (60S)
quarkus.vertx.max-worker-execute-time=120S
```

Este parâmetro pode ser configurado no deploy.---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/processamento-assincrono.md&internalidade=frameworks/quarkus/processamento-assincrono)
