> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# 1 Definições
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/Metricas.md&amp;action_name=metricas/Metricas)

As métricas são uma representação numérica de nossos dados e, como tal, podem aproveitar totalmente o poder da modelagem e previsão matemática para derivar conhecimento do comportamento do nosso sistema ao longo de intervalos de tempo no presente e no futuro - em outras palavras, uma série temporal 

# 2 Por que utilizar?
* Gerar insumos para um acompanhamento visual do estado da aplicação
* Gerar alertas para situações que demandam intervenção
* Comparar a performance de alterações

# 3 Tipos de métricas
O _Prometheus_ trabalha basicamente com 4 tipos de métricas, descritas abaixo. (Para uma descrição mais detalhada confira a [Documentação do Prometheus](https://prometheus.io/docs/concepts/metric_types/))

## 3.1 _Counter_
Contador para valores que apenas pode sofrer incrementos, nunca diminuir.

### 3.1.1 Exemplos
* Número de requisições recebidas
* Quantidade de erros
* Quantidade de execuções de uma função

## 3.2 _Gauge_
Medidor para valores que podem subir ou descer.

### 3.2.1 Exemplos
* Temperatura
* Quantidade de processos em execução
* Quantidade de pods habilitados
* Utilização de recursos (CPU/Ram)

## 3.3 _Histogram_
Separa os valores observados em _buckets_ pré-definidos. Cada _bucket_ guarda a quantidade de observações com valor
inferior ou igual ao seu.

### 3.3.1 Exemplos
* Latência das respostas (quando há valores esperados)

## 3.4 _Summary_
O tipo sumário traz os percentuais de observação de um valor. Os valores são separados em percentis para facilitar a
visualização de mínimo, máximo e a mediana. É também disponibilizada uma quantidade de observações e o somatório 
total dos valores.

### 3.4.1 Exemplos
* Latência das respostas
* Timestamps de utilização de determinada funcionalidade


# 4 Métricas padrão da cloud BB
O padrão de métricas que todo projeto na cloud deve conter está disponível em [https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/metrics-endpoint.md](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/metrics-endpoint.md)---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/Metricas.md&internalidade=metricas/Metricas)
