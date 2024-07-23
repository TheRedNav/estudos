> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Gerador de métricas
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasJavascript.md&amp;action_name=metricas/metricasJavascript)

Para gerar métricas usando _Javascript_ é necessário installar o pacote _prom-client_

```
npm i prom-client
```

# 1 Arquivos utilizados para prover métricas básicas

## 1.1 Arquivo metrics.controller.js

Neste arquivo são inseridas as métricas que se deseja utilizar. A estrutura se dá no seguinte formato:

```
const Prometheus = require('prom-client');
const { Counter, Summary } = require('prom-client');
const appVersion = process.env.npm_package_version

const Metric = {

    prometheus: Prometheus,
    httpRequestsSecondsSummary: new Summary({
        name: "http_requests_seconds_summary",
        help: "request duration in milliseconds",
        labelNames: ["path", "status", "method", "appversion"],
    }),
    httpRequestsCounter: new Counter({
        name: "http_requests_counter",
        help: "Number of requests received",
        labelNames: ["path", "status", "method", "appversion"],
    }),
    httpResponsesCounter: new Counter({
        name: "http_responses_counter",
        help: "Number of responses sent",
        labelNames: ["path", "status", "method", "appversion"],
    }),
    constructor: (() => {
        this.prometheus.collectDefaultMetrics({ prefix: `${appVersion}`, timeout: 5000 });
    }),
    observe: (reqMethod, reqPath, statusCode, start) => {

        const code = statusCode.toString();
        const method = reqMethod.toLowerCase();
        const path = reqPath ? reqPath.toLowerCase() : "";
        const duracao = diffTimeInSeconds(start);

        this.metric.httpRequestsSecondsSummary.labels(path, code, method, appVersion).observe(duracao);
        this.metric.httpRequestsCounter.labels(path, code, method, appVersion).inc();
        this.metric.httpResponsesCounter.labels(path, code, method, appVersion).inc();

    },
}

const diffTimeInSeconds = (start) => {
    const diff = process.hrtime(start);
    return Math.round((diff[0] * 1e9 + diff[1]) / 1000000);
}

module.exports.metric = Metric

module.exports.getMetrics = (req, res) => {

    res.set("Content-Type", this.metric.prometheus.register.contentType)
    this.metric.prometheus.collectDefaultMetrics()
    res.send(this.metric.prometheus.register.metrics())

}
```

## 1.2 Arquivo middleware.js

Contém os _middlewares_ utilizados na aplicação. Entre eles, o _middleware_ responsável por gerar métricas a cada requisição recebida. A verificação começa no recebimento e termina no evento _finish_ da resposta, quando está pronta para ser enviada, conforme exemplo a seguir:

```
const metrics = require('../controllers/metrics-controller').metric;

module.exports = (req, resp, next) => {
    if (endpointForMiddleware(req.path)) {
      const start = process.hrtime();
      resp.on("finish", () => {
        metrics.observe(req.method ? req.method : "",
        req.route ? req.route.path : req.originalUrl,
        resp.statusCode,
        start);
      });
    }
    return next();
  };

const endpointForMiddleware = path => {

    const paths = ['/health', '/ready', '/exemplo'] // Adicione os endpoints que não precisam ser acompanhados pelas métricas
    return paths.some(elem => elem !== path)

}
```

# 1.3 Arquivo server.js

Funcionamento da aplicação, como conexões com bancos de dados, _middlewares_ e _endpoints_. É aqui que é registrada a utilização
do _middleware_ configurado para métricas.

```
const middlewareForMetrics = require('../middlewares/middlewares');

(...seu código)

app.use(middlewareForMetrics) // Inserido antes de registrar os endpoints

```

# 2 Adicionar novas métricas

Para adicionar novas métricas, devem ser inseridos a declaração da métrica, sendo um dos tipos providos pelo _Prometheus_, e um método que atualize o valor da medição. O método deve ser chamado em alguma etapa da aplicação.

## 2.1 Exemplo de adição de nova métrica

Neste exemplo será criada uma métrica de operações de compra de ações realizadas sem o saldo completo para liquidação.

Código para o _Counter_ que irá guardar a quantidade de operações (**metrics.controller.js**):

```
operacoesSaldoInsuficienteCount: new Count({
    name: "operacoesSaldoInsuficiente",
    help: "Operações com saldo menor que custo total",
    labelNames: ["appversion"],
}),
```

Código para a observação da métrica (**metrics.controller.js**):

```
observeTransacaoSaldoInsuficiente: (appVersion) => {
  this.operacoesSaldoInsuficienteCount.labels(appVersion).inc();
}
```

Dentro do endpoint que realiza a ordem de compra, adicionar:

```
if (saldoAtualInsuficiente) {           // Verifica se saldo atual é insuficiente
  metrics.observeCriacaoTransacao();    // Aciona a metrica de contagem
}
```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasJavascript.md&internalidade=metricas/metricasJavascript)
