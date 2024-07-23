> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

## Kumuluzee com Métricas B5
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/MetricasB5Kumuluz.md&amp;action_name=metricas/MetricasB5Kumuluz)
  
Esse roteiro é baseado no readme do projeto servlet-monitor que implementa as métricas no padrão B5 e pode ser consultado clicando aqui [servlet-monitor](https://github.com/labbsr0x/servlet-monitor).

- Lembre-se de substituir ```<sigla>``` do código pela sigla do projeto.
  
- Inclua a dependência abaixo no pom.xml do seu projeto:

```xml
     <dependency>
         <groupId>br.com.labbs</groupId>
         <artifactId>servlet-monitor</artifactId>
         <version>0.1.0</version>
     </dependency>
```

- As métricas do B5 serão coletadas pelo filtro servlet definidos no arquivo web.xml da aplicação.

- Para que o Kumuluz continue coletando as metricas do microprofile é necessário criar um filtro que integre as duas coletas, a do B5 e do Microprofile, para isso crie a classe MetricsMicroprofileCollector.java em src/main/br/com/bb/```<sigla>```/filter, opcionalmente você pode criar no local e com nome que quiser, mas lembre-se de apontar corretamente depois no web.xml. Segue abaixo a classe criada.

```java
  package br.com.bb.<sigla>.filter;

  import br.com.labbs.monitor.MonitorMetrics;
  import com.kumuluz.ee.metrics.producers.MetricRegistryProducer;
  import com.kumuluz.ee.metrics.prometheus.PrometheusMetricWriter;
  import com.kumuluz.ee.metrics.utils.RequestInfo;
  import io.prometheus.client.exporter.common.TextFormat;
  import java.io.IOException;
  import java.io.PrintWriter;
  import javax.servlet.Filter;
  import javax.servlet.FilterChain;
  import javax.servlet.FilterConfig;
  import javax.servlet.ServletException;
  import javax.servlet.ServletRequest;
  import javax.servlet.ServletResponse;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import org.eclipse.microprofile.metrics.MetricRegistry;

  public class MetricsMicroprofileCollector implements Filter {

    private MetricRegistry registry = MetricRegistryProducer.getApplicationRegistry();

    FilterConfig filterConfig = null;
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
      this.filterConfig = filterConfig;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {
      final HttpServletRequest httpRequest = (HttpServletRequest) request;
      final HttpServletResponse httpResponse = (HttpServletResponse) response;
      PrintWriter writer = response.getWriter();

      writer = response.getWriter();

      httpResponse.setStatus(200);
      try {
        TextFormat.write004(writer, MonitorMetrics.INSTANCE.collectorRegistry.metricFamilySamples());
        writer.flush();

        PrometheusMetricWriter prometheusMetricWriter = new PrometheusMetricWriter(writer);
        prometheusMetricWriter.write(new RequestInfo(httpRequest).getRequestedRegistries());

      } finally {
        writer.close();
      }
      chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
  }
```
- Adicione o filtro abaixo no arquivo web.xml da aplicação, que deve estar em src/main/webapp/WEB-INF, caso o arquivo não exista será necessário criá-lo, caso exista cuidado para não duplicar ou excluir as tags e aponte corretamente no ```<filter-class>metricsMicroprofileFilter</filter-class>``` o caminho da classe que criou no passo anterior. O arquivo deve ficar com o seguinte conteúdo:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
         http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

	<filter>
		<filter-name>metricsFilter</filter-name>
		<filter-class>br.com.labbs.monitor.filter.MetricsCollectorFilter</filter-class>
	</filter>

	<filter>
        <filter-name>metricsMicroprofileFilter</filter-name>
        <filter-class>br.com.bb.<sigla>.metrics.MetricsMicroprofileCollector</filter-class>
    </filter>

	<!-- Este filtro deve ser o primeiro <filter-mapping>. Assim será possível obter
		mensurações de latência e tamanho de resposta mais assertivas -->
    <filter-mapping>
        <filter-name>metricsFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

	<filter-mapping>
		<filter-name>metricsMicroprofileFilter</filter-name>
		<url-pattern>/metrics</url-pattern>
	</filter-mapping>	
	
	<servlet>
		<servlet-name>Metrics</servlet-name>
		<servlet-class>br.com.labbs.monitor.exporter.MetricsServlet</servlet-class>
	</servlet>

	<servlet-mapping>
		<servlet-name>Metrics</servlet-name>
		<url-pattern>/metrics</url-pattern>
	</servlet-mapping>
</web-app>
```

- Se você fazer build e rodar o projeto é possível através do /metrics observar que as métricas do microprofile foram mantidas e as novas métricas estão sendo registradas, conforme abaixo. Caso dê algum erro, a aplicação não rode ou não aparecerem métricas tanto do microprofile quanto do B5 revise os passos anteriores e tente novamente.

  Exemplo métrica B5:

    - request_seconds_bucket...
    - request_seconds_count...
    - request_seconds_sum...

   Exemplo métrica MicroProfile:

     - vendor_web_instrumentation...
     - application_...


- No arquivo web.xml podemos também configurar o coletor das métricas B5 incluindo init parameters dentro da tag referente a servlet com o nome Metrics:

- Para sobescrever os valores padrãoes dos "bucktes", sendo os padrões 0.1, 0.3, 1.5 e 10.5:
        
```xml
<init-param>
  <param-name>buckets</param-name>
  <param-value>0.1,0.3,2,10</param-value>
</init-param>
```

- É possível excluir caminhos da aplicação da coleta de métricas com o seguinte código:

```xml
<init-param>
  <param-name>exclusions</param-name>
  <param-value>/metrics,/static</param-value>
</init-param>
```

- Existem outras possibilidades de configuração, que podem ser consultadas no readme do (servlet-monitor)[https://github.com/labbsr0x/servlet-monitor], por vamos utilizar a configuração de exclusão, e nosso web.xml ficará assim:

```xml
<?xml version="1.0" encoding="UTF-8"?>
  <web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
          http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
          version="3.1">

    <filter>
        <filter-name>metricsFilter</filter-name>
        <filter-class>br.com.labbs.monitor.filter.MetricsCollectorFilter</filter-class>
    </filter>
    <filter>
        <filter-name>metricsMicroprofileFilter</filter-name>
        <filter-class>br.com.bb.tfa.metrics.MetricsMicroprofileCollector</filter-class>
    </filter>

    <!-- Este filtro deve ser o primeiro <filter-mapping>. Assim será possível obter
        mensurações de latência e tamanho de resposta mais assertivas -->
    <filter-mapping>
        <filter-name>metricsFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>

    <filter-mapping>
        <filter-name>metricsMicroprofileFilter</filter-name>
        <url-pattern>/metrics</url-pattern>
    </filter-mapping>	
    
    <servlet>
        <servlet-name>Metrics</servlet-name>
        <servlet-class>br.com.labbs.monitor.exporter.MetricsServlet</servlet-class>
        <init-param>
            <param-name>exclusions</param-name>
            <param-value>/metrics,/static</param-value>
        </init-param>
    </servlet>

    <servlet-mapping>
        <servlet-name>Metrics</servlet-name>
        <url-pattern>/metrics</url-pattern>
    </servlet-mapping>
</web-app>
```

- Agora o caminho /metrics foi excluído da coleta de métricas do B5.

- A métrica application_info deve estar exibindo o resultado "unknown", vamos consertar isso. Crie o arquivo application.properties em src/main/resources, ou caso exista inclua a seguinte propriedade:

```
  application.version=${project.version}
```

- Certifique-se de que no pom.xml existe a configuração abaixo dentro da tag <resources>, caso negativo a inclua:

```xml
<resource>
  <directory>src/main/resources</directory>
  <filtering>true</filtering>
</resource>
```

- Se tudo funcionar a métrica application_info deve exibir a versão da aplicação. 
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/MetricasB5Kumuluz.md&internalidade=metricas/MetricasB5Kumuluz)
