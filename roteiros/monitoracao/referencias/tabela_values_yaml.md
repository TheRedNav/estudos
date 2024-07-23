> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/referencias/tabela_values_yaml.md&amp;action_name=monitoracao/referencias/tabela_values_yaml)

# values.yaml  
 
Esse roteiro apresenta descrições dos elementos presentes no arquivo **values.yaml**, relacionados à criação e ao disparo de alarmes de projetos Cloud. As tabelas seguem a mesma ordem de exibição do template localizado no fontes do projeto de monitoração da sua sigla. 
 
## prometheus 

Esse trecho habilita a instância e define as *labels* que serão adicionadas a todos os artefatos, permitindo a auto-identificação/monitoramento pelo Prometheus.

```yaml
prometheus: 
  enabled: true

  commonLabels: 
    tier: t99 
```

| Elemento | Descrição | 
| --- | --- | 
| **enabled** (*obrigatório*) |	Valor booleano que habilita o deploy da instância Prometheus. | 
| **tier** (*opcional*) | *Label* para identificação da sigla do projeto. | 

## defaultRules

Esse trecho habilita regras padrão e permite a inclusão de *labels* adicionais nos alarmes gerados. Os elementos *rulesAnnotations* e *rulesLabels* são empregados para adicionar marcadores às regras padrão habilitadas, indicando informações específicas sobre a associação delas ao Prometheus. Quando essas regras geram alarmes, essas informações são transmitidas para o Alertmanager, tornando a identificação dos alarmes mais fácil nas ferramentas correspondentes.

```yaml
  defaultRules: 
    prometheus: true

    rulesAnnotations: 
      clusterName: 'Desenvolvimento' 

    rulesLabels: 
      sigla: t99 
      omi: sim 
      type: nuvem
```

| Elemento | Descrição | 
| --- | --- | 
| **prometheus** (*obrigatório*) | Valor booleano que habilita as regras padrão para o Prometheus. |  
| **clusterName** (*obrigatório*) | Identificação do cluster. Valores possíveis: desenvolvimento, homologação ou produção. | 
| **sigla** (*opcional*) | Sigla da aplicação que gera o alarme. Deve conter três letras e ser sempre a mesma ao longo do arquivo porque o Prometheus busca essa label para monitorar. Caso não seja definido nenhum valor neste campo, o Prometheus criará uma *label*, igual a definida no template do bb-aplic. | 
| **omi**	(*opcional*) | Relacionado à ferramenta HP OMI da GPROM. Se preenchido com valor *sim*, a ferramenta recebe o alerta. | 
| **type** (*opcional*) | Relacionado ao tipo de incidente. Valores possíveis: nuvem, negócio, BD (banco de dados) ou SO (sistema operacional). | 

## additionalPrometheusRules

Esse trecho configura as regras para gerar alertas com base nas métricas exportadas pela aplicação (/metrics).

```yaml
  additionalPrometheusRules:
    - name: aplicacaot99
      groups:
        - name: t99-alerta-erro 
          rules:
          - alert: ExcessoErrosAplicacao
            expr: sum(increase(application_http_response_counter_total{namespace="t99-f6102738-teste", status="500"}[1h])) > 10
            for: 5m
            labels:
              severity: Critical
              sigla: t99
              msteams: sim
              type: nuvem
            annotations:
              message: 'Excesso de erros - (500) por hora'
              clusterName: 'Desenvolvimento'
              runbook_url: https://fontes.intranet.bb.com.br/sgs/publico/roteiros/-/blob/master/procedimentos/template-alerta.md
```

| Elemento | Descrição |
| --- | --- |
| **name** (*obrigatório*) | Identificação da aplicação e grupo de regras. |
| **name** (*obrigatório*) | Identificação do grupo de regras. |
| **alert** (*obrigatório*)| Identificação do alerta. |
| **expr** (*obrigatório*)| Expressão que quando atendida, gera o alerta. |
| **for** (*obrigatório*)| Duração pela qual a expressão deve ser válida para gerar o alerta. |
| **severity** (*obrigatório*)| Nível de gravidade do problema. Se preenchido com valor *Critical*, abre RDI. Valores possíveis: Critical, Major ou Warning. |
| **sigla** (*obrigatório*) | Sigla da aplicação que gera o alarme. Deve conter três letras. | 
| **type** (*obrigatório*) | Relacionado ao tipo de incidente. Valores possíveis: nuvem, negócio, BD (banco de dados) ou SO (sistema operacional). |
| **msteams** (*opcional*) | Relacionado a ferramenta Microsoft Teams. Se preenchido com valor *sim*, gera alarme na ferramenta. Dependendo do canal no Teams, é necessário acrescentar mais informações de identificação, por exemplo, 'msteamsdev'. |
| **message** (*opcional*)| O título da mensagem de alerta. Aparece também como título do RDI. |
| **runbook_url** (*obrigatório*)| Uma URL válida, com instruções criadas por você para a solução do problema ou encaminhamento do alerta. |
| **clusterName** (*obrigatório*)| Identificação do cluster. Valores possíveis: desenvolvimento, homologação ou produção. |

## ingress - Prometheus

Esse trecho gerencia o acesso externo ao Prometheus, via web. 

```yaml
  ingress: 
    enabled: true
    hosts: 
      - prometheus.dev.desenv.bb.com.br
```

| Elemento | Descrição | 
| --- | --- | 
| **enabled** (*obrigatório*) |	Valor booleano que habilita o *ingress*. |
| **hosts** (*obrigatório*)| Lista de hosts associados ao *ingress*, indica os domínios que ele deve gerenciar. A URL muda conforme a sigla. |

## resources

Esse trecho estabelece os recursos mínimos e máximos de uso de CPU e memória para o pod Prometheus.

```yaml
  resources: 
    limits: 
      cpu: 300m 
      memory: 600Mi 
    requests: 
      cpu: 150m 
      memory: 300Mi 
```

| Elemento | Descrição | 
| --- | --- |
| **limits/cpu** (*opcional*) |	Limite máximo de CPU para o Prometheus. | 
| **limits/memory** (*opcional*) | Limite máximo de memória para o Prometheus. |
| **requests/cpu** (*opcional*) |	Solicitação mínima de CPU para o Prometheus. |
| **requests/memory** (*opcional*) | Solicitação mínima de memória para o Prometheus. |	

## prometheusSpec 

Esse trecho faz a correspondência entre as aplicações e o Prometheus, especificando via labels quais grupos de *services* devem ser monitorados. Também permite que o Prometheus busque os *service monitors* de todos os *namespaces*, mesmo quando o Prometheus não está no mesmo *namespace* que as aplicações.

```yaml
  prometheusSpec: 

    serviceMonitorNamespaceSelector: 
      any: true 

    serviceMonitorSelector: 
      matchLabels: 
        tier: t99
```  

| Elemento | Descrição |
| --- | --- |   
| **any** (*opcional*) | Valor booleano que habilita a busca de *service monitors* em todos os *namespaces*. | 
| **tier** (*opcional*) | *Label* para identificação da sigla do projeto. Indica que o Prometheus deve buscar essa *label*. | 


## alertmanager

Esse trecho habilita o recebimento dos alertas e estabelece as *labels* que o Prometheus utilizará para identificar o Alertmanager como alvo.

```yaml
alertmanager:
  enabled: true

  commonLabels: 
    tier: t99
```

| Elemento | Descrição | 
| --- | --- | 
| **enabled** (*obrigatório*) |	Valor booleano que habilita o Alertmanager. | 
| **tier** (*obrigatório*) | *Label* para identificação da sigla do projeto. | 

## config

Esses campos configuram o gerenciamento dos alertas, com informações como o alcance, os intervalos de tempo e destinos específicos para cada tipo de alerta.

```yaml
  config:
    global: 
      resolve_timeout: 5m
    route: 
      group_by: ['job'] 
      group_wait: 30s 
      group_interval: 5m 
      repeat_interval: 2h 
      receiver: 'null'
      routes:
      - receiver: omi
        match: 
          omi: "sim"
        continue: true 
      - receiver: msteams
        match:
          msteams: "sim"
        continue: true 
```

| Elemento | Descrição | 
| --- | --- | 
| **resolve_timeout** (*obrigatório*) | Tempo máximo permitido para a resolução de um alerta. Ao configurar um valor, você determina o período que o sistema aguardará a confirmação ou resolução do alerta antes de tomar medidas adicionais.|
| **group_by** (*obrigatório*)| Agrupa alertas com base nos critérios específicos definidos aqui. | 
| **group_wait** (*obrigatório*)| Tempo máximo que o sistema aguarda para agrupar alertas semelhantes. | 
| **group_interval** (*obrigatório*)| Intervalo de tempo mínimo entre notificações para o mesmo grupo de alertas. | 
| **repeat_interval** (*obrigatório*)| Intervalo de tempo para repetição das notificações. | 
| **receiver** (*opcional*)| Define o receptor dos alertas que correspondem às configurações definidas aqui. Quando se trata da definição de rotas, caso um alerta não corresponda a nenhum dos critérios de correspondência, ele será tratado no nível raiz e encaminhado para um receptor nulo (null). |
| **msteams** (*opcional*)| Label específica do receptor, deve ser a mesma informada no *receiver*. Se preenchida com o valor *sim*, envia o alarme para o receptor. |
| **continue** (*obrigatório*)| Se preenchido com o valor *true*, o alerta será enviado para o próximo receptor. Se preenchido com o valor *false*, a avaliação encerra ao encontrar o primeiro *match*.  |

## receivers

Esses campos configuram a integração de cada receptor (*receiver*). Os receptores incluídos anteriormente devem ter equivalência nessa seção.

```yaml
    receivers:  
    - name: 'omi' 
      webhook_configs: 
        - url: http://webhook-snmp.psc-proxy.svc.cluster.local 
    - name: 'msteams' 
      webhook_configs: 
      - url: http://alertmanager-receiver-msteams
```

| Elemento | Descrição | 
| --- | --- | 
| **name** (*obrigatório*) | Identificação da ferramenta (receptor). |
| **url** (*obrigatório*) | A URL para onde as informações do webhook devem ser enviadas. |

## ingress - Alertmanager

Esse trecho gerencia o acesso externo ao Alertmanager, via web. 

```yaml
  ingress:
    enabled: true
    hosts:
      - alertmanager.dev.desenv.bb.com.br
```

| Elemento | Descrição | 
| --- | --- | 
| **enabled** (*obrigatório*) |	Valor booleano que habilita o *ingress*. |
| **hosts** (*obrigatório*)| Lista de hosts associados ao *ingress*, indica os domínios que ele deve gerenciar. A URL muda conforme a sigla.|

## alertmanager-receiver-msteams

Esse trecho configura um receptor específico para encaminhar alertas do Alertmanager para o Teams.

```yaml
alertmanager-receiver-msteams:
  commonLabels:
    tier: t99
 
  serviceMonitor:
    enable: true

  connectors:
    - msteams: https://outlook.office.com/webhook/9522046a-0f5b-4210-a5e1-1884774abbbe@ea0c2907-38d2-4181-8750-b0b190b60443/IncomingWebhook/472fcd4e216848dfbc1f15bdfa9232a8/19c5cf33-61ff-494f-95fc-fbe64e4c2fa3
```

| Elemento | Descrição | 
| --- | --- |
| **tier** (*obrigatório*) | *Label* para identificação da sigla do projeto. |
| **enable** (*obrigatório*) | Valor booleano que habilita o monitoramento do pod. |
| **msteams** (*obrigatório*)| URL específica [gerada pelo seu canal no Teams](https://learn.microsoft.com/pt-br/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook?tabs=dotnet). |

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/monitoracao/referencias/tabela_values_yaml.md&internalidade=monitoracao/referencias/tabela_values_yaml)
