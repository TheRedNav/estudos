> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Como_gerar_alertas_monitoracao.md&amp;action_name=monitoracao/Como_gerar_alertas_monitoracao)

# Como gerar alertas de monitoração 

Esse roteiro explica como definir regras de monitoração no Prometheus, configurar o Alertmanager (responsável pelo gerenciamento dos alertas), o HP OMI (ferramenta da GPROM) e o Microsoft Teams para receber as mensagens que informam quando as regras foram atendidas e se há algum problema na aplicação. 

Para ilustrar, nosso exemplo acionará um alerta no Teams da equipe CloudBooster quando ocorrerem 500 erros em 1 hora, no ambiente de desenvolvimento. O alerta será disparado porque ultrapassará o limite tolerável de 10 ocorrências para o período.  

> :information_source: **Observação** 
> 
> Esse roteiro não pode ser executado em aplicações Openshift + AppDynamics. Ele funciona somente para aplicações que utilizam a stack **Rancher+Prometheus+Grafana+Alertmanager**.

## Requisitos
* Um *namespace* de monitoração da sua sigla. 
* Uma aplicação a ser monitorada. 
* O [serviceMonitor da aplicação habilitado](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Troubleshooting.md#11-service-monitor).   
* Uma [instância Prometheus habilitada](https://charts.nuvem.bb.com.br/charts/bb-cloud/prometheus). 
* Uma [imagem Alertmanager](https://charts.nuvem.bb.com.br/charts/bb-cloud/alertmanager).
* Uma [equipe no MsTeams](https://support.microsoft.com/pt-br/office/criar-uma-equipe-do-zero-no-microsoft-teams-174adf5f-846b-4780-b765-de1a0a737e2b). 
 
> :grey_exclamation: **Importante** 
> 
> O arquivo YAML, que faz parte desse roteiro, contém diversas informações que já vêm preenchidas por padrão e que devem ser mantidas. Somente os campos e valores diretamente relacionados às ações do roteiro serão mencionados.

## Passo 1: Habilitar as regras *default* 

Para que o Prometheus entenda que precisa monitorar as regras criadas, elas precisam ser habilitadas no arquivo **values.yaml** do ambiente desejado. Nessa seção também são incluídas *labels* que auxiliam na identificação dos alarmes.

### Seção defaultRules

1. Em **prometheus**, escreva o valor booleano **true** para habilitar as regras criadas.
2. Em **clusterName** (dentro de rulesAnnotations), escreva o nome do seu cluster. Essa informação é necessária para a identificação do alarme nas ferramentas de comunicação.
3. Em **sigla** (dentro de rulesLabels), identifique a sigla da sua aplicação. Toda sigla deve conter três letras e ser a mesma ao longo do arquivo.
4. Em **omi**, escreva **sim** para habilitar o recebimento do alerta na ferramenta OMI. 
5. Em **type**, identifique qual é o [tipo de incidente](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/referencias/tabela_values_yaml.md#defaultrules) relacionado ao alerta. 

#### Exemplo

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

## Passo 2: Criar as regras personalizadas

A personalização das regras de geração de alertas é realizada por meio da inserção de valores nos campos desta seção.

### Seção additionalPrometheusRules

1. No primeiro **name**, escolha um nome que englobe a sua aplicação e o grupo de regras personalizadas.  
2. No segundo **name** (dentro de groups), defina um nome para o seu grupo de regras personalizadas.  
3. Em **alert**, identifique o alerta. 
4. Em **expr**, escreva a fórmula que estabelece uma regra. Aqui estamos criando uma regra que diz que quando ocorrerem 500 erros em 1 hora, um alerta será disparado porque ultrapassou-se o limite tolerável de 10 ocorrências para o período.
5. Em **for**, defina por quanto tempo a expressão deve ser válida para gerar o alerta.  
6. Em **severity** (dentro de labels), defina a [gravidade do alerta](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/referencias/tabela_values_yaml.md#additionalprometheusrules). 
7. Em **sigla**, repita a mesma sigla ao longo do arquivo.
8. Em **omi**, escreva **sim** para habilitar o recebimento do alerta na ferramenta OMI. 
9. Em **msteamscb**, escreva **sim** para habilitar o recebimento do alerta no Teams da sua equipe. Lembre-se que você deve personalizar esse campo conforme o nome da sua equipe no Teams.
10. Em **type**, repita o mesmo tipo de incidente ao longo do arquivo.
11. Em **message** (dentro de annotations), escolha um título para a mensagem de alerta.
12. Em **clusterName**, repita o mesmo cluster ao longo do arquivo.
13. Em **runbook_url**, adicione uma [URL válida](https://fontes.intranet.bb.com.br/sgs/publico/roteiros/-/blob/master/procedimentos/template-alerta.md) contendo instruções para que o seu usuário saiba como proceder quando receber a mensagem. Essa URL deve conter instruções para correção do problema ou encaminhamento para o time responsável pela correção.

#### Exemplo
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
              omi: sim
              msteamscb: sim
              type: nuvem
            annotations:
              message: 'Excesso de erros - (500) por hora'
              clusterName: 'Desenvolvimento'
              runbook_url: https://fontes.intranet.bb.com.br/sgs/publico/roteiros/-/blob/master/procedimentos/template-alerta.md

```
> :grey_exclamation: **Importante** 
>
> Se o alarme possuir as labels **omi: sim** e **severity: critical**, o HP OMI abrirá automaticamente um [incidente](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Como_gerar_alertas_monitoracao.md#abertura-de-rdi) para a equipe GSTI - DITEC/UOS/GPROM - 1° NÍVEL - PLATAF.DISTRIB / HIGH-END. Certifique-se de incluir no campo **runbook_url** o procedimento de resolução. Crie uma página de RunBook para cada alerta em uma pasta pública de sua sigla, facilitando o acesso e tratamento pela equipe de suporte do GPROM.

## Passo 3: Habilitar o ingress - Prometheus (opcional)

Caso queira acessar a aplicação Prometheus via interface no browser, é necessário habilitar o *ingress* e inserir uma URL válida. 

### Seção ingress

1. Em **enabled**, escreva o valor booleano **true** para habilitar o *ingress*.
2. Em **hosts**, adicione a URL do Prometheus para que esse domínio seja gerenciado pelo *ingress*.

#### Exemplo
```yaml
  ingress: 
    enabled: true
    hosts: 
      - prometheus.dev.desenv.bb.com.br
```
> :grey_exclamation: **Importante**
>
> Se o *ingress* estiver habilitado, assegure-se de incorporar a mesma URL válida ao implantar o [chart do DNS Service](https://charts.nuvem.bb.com.br/charts/bb-cloud/dnsservice). Esse chart é utilizado para realizar a publicação automática em nosso serviço de DNS.

## Passo 4: Habilitar o Alertmanager

O Alertmanager gerencia os alertas enviados pelo Prometheus, evitando a duplicação dos alarmes, além de agrupá-los e roteá-los para as demais interfaces.

### Seção alertmanager

1. Em **enabled** (dentro de alertmanager), escreva o valor booleano **true** para habilitar o Alertmanager.
2. Em **tier** (dentro de commonLabels), repita a mesma sigla do projeto ao longo do arquivo.

#### Exemplo
```yaml
alertmanager:
  enabled: true

  commonLabels: 
    tier: t99
```

## Passo 5: Configurar o destinatário do alerta

Todas as ferramentas que a equipe utiliza devem ser configuradas para receber os alertas.  

### Seção config

1. Em **receiver** (dentro de routes), escreva o nome da ferramenta por onde você receberá as mensagens de alerta. 
2. Dentro de **match**, crie uma *label* com o mesmo nome da ferramenta e preencha com o valor **"sim"**.
3. Em **continue**, escreva o valor booleano **true** para que o envio das mensagens continue e o próximo destinatário configurado também receba o alerta.

### Seção receivers

4. Em **name**, repita o nome da ferramenta. Aqui, é necessário colocar o nome entre dois apóstrofos.
5. Em **url**, preencha com o padrão do exemplo. Para o Teams, lembre-se de alterar o final da URL para o nome da sua equipe.

#### Exemplo
```yaml
  config:
    route: 
      routes:
      - receiver: omi
        match:
          omi: "sim"
        continue: true 
      - receiver: msteamscb
        match:
          msteamscb: "sim"
        continue: true 
  
    receivers:
    - name: 'omi'
      webhook_configs:
        - url: http://webhook-snmp.psc-proxy.svc.cluster.local
    - name: 'msteamscb'
      webhook_configs:
        - url: http://alertmanager-receiver-msteams/msteamscb
```

## Passo 6: Habilitar o ingress - Alertmanager (opcional)

Caso queira acessar a aplicação Alertmanager via interface no navegador, é necessário habilitar o *ingress* e inserir uma URL válida. 

### Seção ingress

1. Em **enabled**, escreva o valor booleano **true** para habilitar o *ingress*.
2. Em **hosts**, adicione a URL do Alertmanager para que esse domínio seja gerenciado pelo *ingress*.

#### Exemplo
```yaml
  ingress: 
    enabled: true
    hosts: 
      - alertmanager.dev.desenv.bb.com.br
```  

## Passo 7: Configurar a URL do webhook

A configuração da URL do webhook é necessária para que o Alertmanager possa enviar mensagens para o seu canal específico no Teams quando ocorrerem eventos de alerta.

### Seção alertmanager-receiver-msteams

1. Em **tier** (dentro de commonLabels), repita a mesma sigla do projeto ao longo do arquivo.
2. Em **enable** (dentro de serviceMonitor), escreva o valor booleano **true** para habilitar o monitoramento do pod.
3. Em **msteamscb** (dentro de connectors), insira A [URL do webhook do Microsoft Teams](https://learn.microsoft.com/pt-br/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook?tabs=dotnet). Essa URL é única para cada canal e é gerada quando você configura um *webhook* de entrada para o canal desejado. Lembre-se que esse campo terá outras letras após a palavra **teams**, conforme o nome da sua equipe.

#### Exemplo
```yaml
alertmanager-receiver-msteams:
  commonLabels:
    tier: t99
 
  serviceMonitor:
    enable: true

  connectors:
    - msteamscb: https://banco365.webhook.office.com/webhookb2/dc11407b-03e7-447a-9984-202059a596e8@ea0c2907-38d2-4181-8750-b0b190b60443/IncomingWebhook/620d26eb1de1456aa612dac1a59e2746/9f15931e-d2d6-45cb-ac2e-bb33ada0cf57
```

Após concluir as configurações, acesse as URLS do Prometheus e Alertmanager para confirmar que sua aplicação está sendo monitorada e gerando alertas. Em ambas, consulte a aba **Alerts**.

## Abertura de RDI 

> :information_source: **Observação** 
>
> Foi implementado o LDAP no HP OMI de Desenvolvimento e Produção. Para visualizar os eventos no console, é necessário solicitar [o papel HPO04 - (Consulta)](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=1&app=aceSegurancaAcessoPainel#/). 
> Se não houver regra criada para sua UOR, envie um e-mail para ditec.gesit.monit@bb.com.br com o assunto: "ARQ3 - Regra de acesso - UOR XXXXXX".  

### Produção

Os alertas do Cluster de Produção são encaminhados para o [OMI de Produção](http://go.b/omi) e um RDI é criado no [GSTI de Produção](https://gsti.bb.com.br/hps/index.do).

### Homologação

Os alertas do Cluster de Homologação são encaminhados para o [OMI de Homologação](https://omi.hm.bb.com.br/topaz/login.jsp) e um RDI é criado no [GSTI de Homologação](https://hps.hm.bb.com.br/).

### Desenvolvimento

Os alertas do Cluster de Desenvolvimento são encaminhados para o [OMI de Desenvolvimento](https://omi.desenv.bb.com.br/) e um RDI é criado no [GSTI de Homologação](https://hps.hm.bb.com.br/).

> :information_source: **Observação** 
> 
> Se tiver dificuldades para acessar o GSTI de homologação, contate a equipe [DITEC/GEINT/G1/E1](https://humanograma.intranet.bb.com.br/uor/521799) para receber orientações.

**Tags:** #prometheus #alertmanager #alertas #monitorar #regras
   
## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Como_gerar_alertas_monitoracao.md&internalidade=monitoracao/Como_gerar_alertas_monitoracao)
