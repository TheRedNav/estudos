> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# MÉTRICAS
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasJava.md&amp;action_name=metricas/metricasJava)

O projeto blank _Java_ gerado utilizando o BBDEV com o plugin dx-kumuluzee à partir da versão 0.0.5 já contém as [métricas padrão](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/metrics-endpoint.md) definidas pela Arq3.

Ao executar o projeto usando o script run.sh as métricas podem ser acessadas no endpoint /metrics. 

## Métricas padrão do Kumuluzee

O Kumuluzee implementa o padrão de métricas do [Microprofile](https://download.eclipse.org/microprofile/microprofile-metrics-2.0.1/microprofile-metrics-spec-2.0.1.html). As métricas da podem ser acessadas pelo endereço /metrics. Ela é divida em três contextos, `base`, `vendor` e `application`. O contexto `vendor` é utilizado para métricas disponibilizadas pelo fornecedor de uma biblioteca e podera ser acessado pelo endereco /metrics/vendor. O contexto `base` possui informações sobre a JVM e informações de consumo de memória e cpu. O contexto `application` contem as métricas da aplicação em si e pode ser acessado pelo /metrics/application , e nesse contexto que iremos colocar as métricas da nossa aplicação. Todas esses contextos tambem serão exibidos no endereço /metrics.


## Métricas de requisição

Uma das métricas padrões é referente ao tempo de resposta, identificada por `http_requests_seconds_summary` e quantidade de requisições feitas a um determinado endpoint, identificada por `http_response_counter​`. Essas métricas já estão presentes no projeto blank _Java_, elas possuem quatro tags como identificação dos endpoints executados conforme abaixo:

- path: descrição do endpoint que gerou a métrica
- status: descrição do http status code de retorno
- method: descrição do metodo http da métrica, GET, POST, PUT, DELETE etc...
- appVersion: versão da aplicação que gerou a métrica.

Segue abaixo um exemplo das métricas geradas:

```
# TYPE application_http_requests_counter_total counter
application_http_requests_counter_total{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 1
# TYPE application_http_requests_seconds_summary_rate_per_second gauge
application_http_requests_seconds_summary_rate_per_second{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.0040757634969143305
# TYPE application_http_requests_seconds_summary_one_min_rate_per_second gauge
application_http_requests_seconds_summary_one_min_rate_per_second{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.0036631277777468448
# TYPE application_http_requests_seconds_summary_five_min_rate_per_second gauge
application_http_requests_seconds_summary_five_min_rate_per_second{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.08986579282344433
# TYPE application_http_requests_seconds_summary_fifteen_min_rate_per_second gauge
application_http_requests_seconds_summary_fifteen_min_rate_per_second{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.15318566767292968
# TYPE application_http_requests_seconds_summary_mean_seconds gauge
application_http_requests_seconds_summary_mean_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.318
# TYPE application_http_requests_seconds_summary_max_seconds gauge
application_http_requests_seconds_summary_max_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.318
# TYPE application_http_requests_seconds_summary_min_seconds gauge
application_http_requests_seconds_summary_min_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.318
# TYPE application_http_requests_seconds_summary_stddev_seconds gauge
application_http_requests_seconds_summary_stddev_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 0.0
# TYPE application_http_requests_seconds_summary_seconds summary
application_http_requests_seconds_summary_seconds_count{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario"} 1
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.5"} 0.318
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.75"} 0.318
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.95"} 0.318
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.98"} 0.318
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.99"} 0.318
application_http_requests_seconds_summary_seconds{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",quantile="0.999"} 0.318
# TYPE application_http_response_counter_total counter
application_http_response_counter_total{appVersion="1.0-SNAPSHOT",method="GET",path="favicon.ico",status="500"} 1
# TYPE application_http_response_counter_total counter
application_http_response_counter_total{appVersion="1.0-SNAPSHOT",method="GET",path="v1/usuario",status="200"} 1
```

## Inclusão de métricas padrão
Caso seu codigo não tenha as métricas de requisição, basta incluir a classe abaixo, lembrando que esse exemplo é para quem utiliza o kumuluzee. Caso sua aplicação não possua a classe AppConfigProperties você pode cria-la conforme descrito nesse [roteiro](https://fontes.intranet.bb.com.br/dev/publico/roteiros/blob/master/info/info.md), ou apenas substituir a chamada por um metodo que retorne a versão atual da sua aplicação. Não se esqueça de incluir o package na classe abaixo.


```java 
import br.com.bb.ppp.util.AppConfigProperties;

import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Timer;
import org.eclipse.microprofile.metrics.Metadata;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.metrics.MetricType;
import org.eclipse.microprofile.metrics.MetricUnits;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.util.concurrent.TimeUnit;

@Provider
@RequestScoped
public class MetricaHandler implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    MetricRegistry registry;

    @Inject
    AppConfigProperties config;

    private Timer timerSuccessful;
    private long initTimeMilliseconds;

    private static final String PATH = "path";
    private static final String STATUS = "status";
    private static final String METHOD = "method";
    private static final String APP_VERSION = "appVersion";

    @Override
    public void filter(ContainerRequestContext request) {
        Tag pathTag = new Tag(PATH, request.getUriInfo().getPath());
        Tag methodTag = new Tag(METHOD, request.getMethod());
        Tag appVersionTag = new Tag(APP_VERSION,  config.getVersion() );

        Metadata metadataTimer = Metadata.builder()
                .withName("http_requests_seconds_summary")
                .withUnit(MetricUnits.SECONDS)
                .withType(MetricType.TIMER)
                .build();

        timerSuccessful = registry.timer(metadataTimer, pathTag, methodTag, appVersionTag);

        registry.counter("http_requests_counter", pathTag, methodTag, appVersionTag).inc();

        initTimeMilliseconds = System.currentTimeMillis();
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response){
        Tag pathTag = new Tag(PATH, request.getUriInfo().getPath());
        Tag statusTag = new Tag(STATUS, String.valueOf(response.getStatus()));
        Tag methodTag = new Tag(METHOD, request.getMethod());
        Tag appVersionTag = new Tag(APP_VERSION,  config.getVersion());

        registry.counter("http_response_counter​",pathTag, statusTag, methodTag, appVersionTag).inc();
        timerSuccessful.update(System.currentTimeMillis() - initTimeMilliseconds, TimeUnit.MILLISECONDS);
    }
}

```


## Criação de novas métricas

As métricas podem ser implementadas de duas maneiras por anotações inseridas acima da declaração do metodo que se deseja metrificar e estão dividos em quatro tipos:

- @Counted: Métrica para realizar contagem de quantas vezes um determinado metodo foi invocado, ele apenas aceita o incremento.

```java
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.TEXT_PLAIN)
@Path("/v1/hello")
public class HelloResource {

    @GET
    @Counted(name = "nome_minha_metrica_counted", absolute = true)
    public Response hello(){
        return Response.status(Response.Status.OK).entity("Hello World!").build();
    }

}
```

Retorno ao acessar /metrics
```
# TYPE application_nome_minha_metrica_counted_total counter
application_nome_minha_metrica_counted_total 1
```

- @Gauge: Métrica para realizar o incrementou ou decremento de um valor, geralmente utilizado em métricas de volume e de carga. Ele deve ser utilizado em um metodo que retorne um valor numerico, além disso é necessário informar a unit que será utilizada. Essa unit será incluida como sufixo no nome da métrica.

```java
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.TEXT_PLAIN)
@Path("/v1/hello")
public class HelloResource {

    @GET
    public Response hello(){
        this.testeGauge();
        return Response.status(Response.Status.OK).entity("Hello World!").build()
                ;
    }

    @Gauge(name = "nome_minha_metrica_gauge", absolute = true, unit = "resposta_para_tudo")
    private int testeGauge(){
        return 42;
    }

}
```

Retorno ao acessar /metrics

```
# TYPE application_nome_minha_metrica_gauge_resposta_da_vida gauge
application_nome_minha_metrica_gauge_resposta_para_tudo 42
```

- @Metered: Métrica para registrar a frequencia de invocação de um metodo, essa anotação gera 5 métricas relacionadas, um contador e outras 4 com o intervalo de frequencia em um segundo, por minuto, por cinco e quinze minutos.

```java
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Metered;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.TEXT_PLAIN)
@Path("/v1/hello")
public class HelloResource {

    @GET
    @Metered(name = "nome_minha_metrica_metered", absolute = true)
    public Response hello(){
        return Response.status(Response.Status.OK).entity("Hello World!").build();
    }

}
```
Retorno ao acessar /metrics

```
# TYPE application_nome_minha_metrica_metered_total counter
application_nome_minha_metrica_metered_total 6
# TYPE application_nome_minha_metrica_metered_rate_per_second gauge
application_nome_minha_metrica_metered_rate_per_second 1.5338623446461557
# TYPE application_nome_minha_metrica_metered_one_min_rate_per_second gauge
application_nome_minha_metrica_metered_one_min_rate_per_second 0.0
# TYPE application_nome_minha_metrica_metered_five_min_rate_per_second gauge
application_nome_minha_metrica_metered_five_min_rate_per_second 0.0
# TYPE application_nome_minha_metrica_metered_fifteen_min_rate_per_second gauge
application_nome_minha_metrica_metered_fifteen_min_rate_per_second 0.0
```

- @Timed: Métrica para registrar o tempo gasto na execução de um metodo, essa anotação gera as mesmas métricas criadas pelo @Metered além de criar um gauge com o valor de media, maximo, minimo e desvio padrão, e também cria uma métrica do tipo summary com os quantis de 0.5, 0.75, 0.95, 0.98, 0.99 e 0.999.

```java
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Timed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Produces(MediaType.TEXT_PLAIN)
@Path("/v1/hello")
public class HelloResource {

    @GET
    @Timed(name = "nome_minha_metrica_Timed", absolute = true)
    public Response hello(){
        return Response.status(Response.Status.OK).entity("Hello World!").build();
    }

}
```
Retorno ao acessar /metrics

```
# TYPE application_nome_minha_metrica_Timed_rate_per_second gauge
application_nome_minha_metrica_Timed_rate_per_second 1.5367554542284463
# TYPE application_nome_minha_metrica_Timed_one_min_rate_per_second gauge
application_nome_minha_metrica_Timed_one_min_rate_per_second 0.0
# TYPE application_nome_minha_metrica_Timed_five_min_rate_per_second gauge
application_nome_minha_metrica_Timed_five_min_rate_per_second 0.0
# TYPE application_nome_minha_metrica_Timed_fifteen_min_rate_per_second gauge
application_nome_minha_metrica_Timed_fifteen_min_rate_per_second 0.0
# TYPE application_nome_minha_metrica_Timed_mean_seconds gauge
application_nome_minha_metrica_Timed_mean_seconds 8.01804752200313E-4
# TYPE application_nome_minha_metrica_Timed_max_seconds gauge
application_nome_minha_metrica_Timed_max_seconds 0.004445504
# TYPE application_nome_minha_metrica_Timed_min_seconds gauge
application_nome_minha_metrica_Timed_min_seconds 5.098E-5
# TYPE application_nome_minha_metrica_Timed_stddev_seconds gauge
application_nome_minha_metrica_Timed_stddev_seconds 0.001615015308146374
# TYPE application_nome_minha_metrica_Timed_seconds summary
application_nome_minha_metrica_Timed_seconds_count 6
application_nome_minha_metrica_Timed_seconds{quantile="0.5"} 1.02633E-4
application_nome_minha_metrica_Timed_seconds{quantile="0.75"} 1.0308000000000001E-4
application_nome_minha_metrica_Timed_seconds{quantile="0.95"} 0.004445504
application_nome_minha_metrica_Timed_seconds{quantile="0.98"} 0.004445504
application_nome_minha_metrica_Timed_seconds{quantile="0.99"} 0.004445504
application_nome_minha_metrica_Timed_seconds{quantile="0.999"} 0.004445504
```

Para saber mais sobre basta acessar a documentação do [Microprofile](https://download.eclipse.org/microprofile/microprofile-metrics-2.0.1/microprofile-metrics-spec-2.0.1.html).---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasJava.md&internalidade=metricas/metricasJava)
