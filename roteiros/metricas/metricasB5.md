> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Metricas
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasB5.md&amp;action_name=metricas/metricasB5)

Monitoração é a tarefa de geração, coleta, agregação e uso de métricas que fornecem informações sobre o estado de um sistema. Com isso podemos verificar se nossos sistemas estão respondendo de acordo com o que esperamos em relação a software, hardware ou negócio. Para isso devemos utilizar ferramentas que gerem métricas dp nosso sistema.

Mas para que monitorar? Existem diversas razões para monitorar um sistema, no caso do nosso escopo do Arq 3.0, utiliando sistemas distribuídos, microserviços e em núvem, podemos apontar como razões: obter um status imediato de nosso sistema; identificar as causas das interrupções com auxílio de paineis(dashboards); conseguir gerar alertas o quanto antes para que se possa realizar ações de manutenção e prevenção mais pró-ativas.

`Obs: Caso algum termo não seja claro, procure a documentação listada ao final deste material`

## B5

O B5(Big Brother Banco do Brasil Barimetrics) trata de uma biblioteca baseada no [BigBrother](https://github.com/labbsr0x/big-brother) para o Banco do Brasil e desenvolvida pela equipe do Barimetrics. Ele já expõe algumas métricas padrão, sem que o desenvolvedor tenha que adiciona-las em seus projetos. Com isso a criação de painéis de forma automática entrega diversas informações desde a criação do projeto.

Esse projeto pode ser utilizado em qualquer aplicação, basta adicionar a dependência correspondente a linguagem e a biblioteca padrão de rotas utilizadas no seu projeto. Por exemplo: Para Javascript com Express, é utilizada a lib [express-monitor](https://github.com/labbsr0x/express-monitor)

## Métricas expostas pelo B5

São elas:
- `request_seconds_bucket` : é uma métrica que define o [histograma](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/Metricas.md#33-histogram) de quantas solicitações estão caindo em intervalos bem definidos representados pelo rótulo `le`;
- `request_seconds_count` : é um contador que representa o número total de solicitações com as ocorrências da label solicitada;
- `request_seconds_sum` : é a soma geral de quanto tempo as solicitações com as ocorrências de uma label exata estão levando;
- `response_size_bytes` : é um contador que calcula quantos dados estão sendo enviados de volta ao usuário para um determinado tipo de solicitação. Ele captura o tamanho da content-length do cabeçalho resposta. Se não houver tal cabeçalho, o valor exposto como métrica será zero;
- `dependency_up` : é uma métrica para registrar o status de uma determinada dependencia se encontra: `up` (1) ou `down` (0). A label `name` registra o nome da dependência;
- `dependency_request_seconds_bucket` : é uma métrica que define o [histograma](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/ de quantas solicitações para uma dependência específica estão caindo nos buckets representados pela label do arquivo;
- `dependency_request_seconds_count` : é um contador que mostra o número total de solicitações para uma dependência específica;
- `dependency_request_seconds_sum` : é um contador que mostra a soma total de tempo em que as solicitações para uma dependência específica estão levando;
- `application_info` : contém informações estáticas de um aplicativo, como seu número de versão semântica;
    
## B5 em projetos Java Quarkus e Template Arq 3

Os projetos criado através do [BBDev-Generator](https://generator.bbdev.dev.intranet.bb.com.br/) a partir (15/10/2020), não se torna necessário incluir a dependência do B5, pois é gerada automaticamente para projetos Java com uso do Quarkus. Caso utilize alguma versão gerada anteriormente, deve seguir os passos a seguir:

1. Adicione ou atualize a versão da sua lib dx:
```xml
<dependency>
  <groupId>br.com.bb.dev</groupId>
  <artifactId>dev-dx-quarkus-ext</artifactId>
  <version>0.1.9</version>
</dependency>
```
2. Adicione a dependencia com o plugin do uso do B5 com o Quarkus:
```xml
<dependency>
  <groupId>br.com.labbs</groupId>
  <artifactId>quarkus-monitor</artifactId>
  <version>0.3.0</version>
/dependency>
```

A primeira dependência se refere a um conjunto de lib que facilitam a vida do desenvolvedor. Essa depêndencia foi criada pela equipe DX e pode ser acessada através do repositório [dev-dx-quarkus-ext](https://fontes.intranet.bb.com.br/dev/dev-dx-quarkus-ext).

A segunda é um plugin para que o B5 possa ser utilizado junto do Quarkus Microprofile, ele corrige, entre outras coisas, a não geração de prefixos de métricas geradas pelo Quarkus, outra mudança foi na mudança no formato das metricas de tempo, que foi alterada do formato percentil para buckets pré definidos.

## B5 em projetos Java Kumuluzee

Para os projetos que utilizam o kulumuzee foi criado um roteiro para realizar a configuração da lib [servlet-monitor](https://github.com/labbsr0x/servlet-monitor) e ainda manter as metricas ja criadas no padrão microprofile, para facilitar o processo de atualização.
Para maiores detalhes de como configurar sua aplicação siga o [roteiro](MetricasB5Kumuluz.md).

## B5 em projetos Typescript e Template Arq 3

Projetos que utilizem a linguagem Typescript e criados através do [BBDev-Generator](https://generator.bbdev.dev.intranet.bb.com.br/) não precisam de nenhuma adição de biblioteca, somente verifique se a lib `"dev-dx-typescript-libs": "^1.2.0"` se encontra em seu package.json e que está atualizada.

## Como configurar

Mais detalhes da configuração nesse outro roteiro: 
- https://fontes.intranet.bb.com.br/ath/publico/b5-barimetrics/-/tree/master#b5-barimetrics

## Referências

- https://prometheus.io/docs/introduction/overview/
- https://github.com/labbsr0x/big-brother
- https://github.com/labbsr0x/servlet-monitor
- https://github.com/labbsr0x/quarkus-monitor/blob/master/README.md
- https://fontes.intranet.bb.com.br/dev/dev-dx-quarkus-ext
- https://generator.bbdev.dev.intranet.bb.com.br/

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/metricas/metricasB5.md&internalidade=metricas/metricasB5)
