> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

O blank _Typescript_ utiliza o pacote _prom-client_ para gerar informações ao _Prometheus_.
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasTypescript.md&amp;action_name=metricas/metricasTypescript)

# 1 Arquivos utilizados para prover métricas básicas
## 1.1 Arquivo metrics.controller.ts
Neste arquivo são inseridas as métricas que se deseja utilizar. A estrutura se dá no seguinte formato:
```
class Metric {
    public prometheus = Prometheus;                 // Objeto para prover métricas básicas

    private httpRequestsSecondsSummary = new Summary({                // Verificação de latência
      name: "http_requests_seconds_summary",
      help: "request duration in milliseconds",
      labelNames: ["path", "status", "method", "appversion"],
    });

    private httpRequestsCounter = new Counter({                       // Acompanhamento de requisições
      name: "http_requests_counter",
      help: "Number of requests received",
      labelNames: ["path", "status", "method", "appversion"],
    });

    private httpResponsesCounter = new Counter({                      // Acompanhamento de respostas
      name: "http_responses_counter",
      help: "Number of responses sent",
      labelNames: ["path", "status", "method", "appversion"],
    });

    private nomeMetrica = new TipoMetrica({            // Métrica adicional, TipoMetrica deve ser alguma das 
        atributoMetrica1: valor,                    //opções providas pelo Prometheus
        atributoMetrica2: valor,
        ...
    });          
    
    ...

    constructor() {                                 // Construtor, utilizado para guardar versão e iniciar
        ...                                         //observação de métricas básicas
        this.prometheus.collectDefaultMetrics({     
            prefix: prefixoDasMetricas,             // O prefixo idealmente contém a versão da aplicação
            timeout: quantidadeMs                   // Quantidade de milissegundos entre as verificações 
        });        
        ...                               
    }

    public observe(method: string, path: string,                  // Método a ser chamado para gerar métricas
        statusCode: number, start: [number, number]): void {      //a cada requisição
      path = path ? path.toLowerCase() : "";                      // Guarda caminho da requisição
      method = method.toLowerCase();                              // Método HTTP utilizado

      const duracao = diffTimeInSeconds(start);                   // Calcular duração da requisição

      const code: string = String(statusCode);                    // Código HTTP da resposta

      this.httpRequestsSecondsSummary.labels(path, code, method, environment.app.version).observe(duracao);
      this.httpRequestsCounter.labels(path, code, method, environment.app.version).inc();
      this.httpResponsesCounter.labels(path, code, method, environment.app.version).inc();
    }

    public observeMetricaAdicional(...) {           // Outros métodos para gerar insumo ao Prometheus

    }                                             

}
```

## 1.2 Arquivo middleware.ts
Contém os _middlewares_ utilizados na aplicação. Entre eles, o _middleware_ responsável por gerar métricas a cada requisição recebida. A verificação começa no recebimento e termina no evento _finish_ da resposta, quando está pronta para ser enviada, conforme exemplo a seguir:
```
export const middlewareForMetrics = (req: express.Request, resp: express.Response, next: express.NextFunction) => {
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
```

# 1.3 Arquivo server.ts
Funcionamento da aplicação, como conexões com bancos de dados, _middlewares_ e _endpoints_. É aqui que é registrada a utilização
do _middleware_ configurado para métricas.

```
    this.server.use(middlewareForMetrics); // Inserido antes de registrar os endpoints
```

# 2 Adicionar novas métricas
Para adicionar novas métricas, devem ser inseridos a declaração da métrica, sendo um dos tipos providos pelo _Prometheus_, e um método que atualize o valor da medição. O método deve ser chamado em alguma etapa da aplicação.

## 2.1 Exemplo de adição de nova métrica
Neste exemplo será criada uma métrica de operações de compra de ações realizadas sem o saldo completo para liquidação.

Código para o _Counter_ que irá guardar a quantidade de operações (**metrics.controller.ts**):
```
private operacoesSaldoInsuficienteCount = new Counter({
  name: "operacoesSaldoInsuficiente",
  help: "Operações com saldo menor que custo total",
  labelNames: ["appversion"],
});
```

Código para a observação da métrica (**metrics.controller.ts**):
```
public observeTransacaoSaldoInsuficiente(): void {
  this.operacoesSaldoInsuficienteCount.inc();
}
```

Dentro do endpoint que realiza a ordem de compra, adicionar:
```
if (saldoAtualInsuficiente) {           // Verifica se saldo atual é insuficiente
  metrics.observeCriacaoTransacao();    // Aciona a metrica de contagem
}
```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasTypescript.md&internalidade=metricas/metricasTypescript)
