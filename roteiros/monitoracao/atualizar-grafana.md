> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/atualizar-grafana.md&amp;action_name=monitoracao/atualizar-grafana.md)

# Atualização do Grafana

## Motivação

Charts antigos do repositório do banco apontam para o repositório atf, que está descontinuado, causando erros de download da imagem (ErrImagePull). Além disso, manter os softwares atualizados é uma prática de segurança.

## Como atualizar

Verificar em https://charts.nuvem.bb.com.br/charts/bb-cloud/grafana a versão atual. Incluir no requirements, de preferência com um `~` para garantir atualização automática de versões *minor*.

Exemplo para o requirements(atentar que em produção o repositório muda para `https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo`)

```requirements.yml
  - name: grafana
    version: ~7.1.5
    repository: https://charts-repo.nuvem.bb.com.br/pre-prd/bb/catalogo
    condition: grafana.enabled
```

Verificar o conteúdo do [values minimal](https://fontes.intranet.bb.com.br/psc/publico/psc_helm_charts/raw/master/sgs/grafana/values.minimal.yaml), que muda em relação a versões anteriores. Isso significa que o values do seu Grafana deve ser atualizado e refletir esse novo formato.

A seção `plugins` mudou de lugar, e fica dentro de um objeto `bb`. O mesmo aconteceu com a seção `ldapBB`, que virou `ldap` dentro de `bb` e, se especificada, precisa ter os papéis incluídos.

Exemplo:

Antes:

```
plugins:
  - meuplugin
  
ldapBB:
  enabled: true
```

Depois:

```
bb:
  ldap:
    # digite papel e grupo do LDAP, o grafana usará o LDAP do ambiente onde estiver subindo
    papel: ALMFDDEV
    grupo: ALM
  plugins:
    download_dir: /var/lib/grafana/plugins
    # Para utilizar algum plugin da lista, basta descomentar a(s) linha(s) e apagar o '[]'
    list: []
    # - meuplugin
```

O formato de declarar os dashboards também mudou. Veja a documentação da [esteira](https://fontes.intranet.bb.com.br/sgs/publico/roteiros/-/blob/master/Esteira%20de%20Dashboards%20Grafana.md), especialmente a parte que diz "Para Grafanas com versões apartir de à 7.0.0". Resumidamente, o dashboard deve ser provisionado com url absoluta do [Portal de Monitoração](http://portal.sgs.intranet.bb.com.br). Visite o Portal para conferir os dashboards disponíveis e copiar suas URLs. Exemplo:

```
dashboards: 
    default: 
        # Dashboard padrão recursos de namespace
      namespace:
        url: http://portal.sgs.intranet.bb.com.br/dashboards/download/default/k8s-compute-resources-namespace.json
```

## Erros comuns

## Erro no download da imagem

```
Failed to pull image "atf.intranet.bb.com.br:5001/grafana/grafana:7.5.5"
```

Esse erro indica que seu namespace continua tentando baixar a imagem do atf, e numa versão antiga. Provavelmente, porque não conseguiu sincronizar, e assim pegar as alterações do seu values. Consulte o 'Sync error' do Argo e procure o problema.

## Field is immutable

Ao clicar em 'Sync failed', no Argo, algo parecido com isso:
```
Deployment.apps "prd-ipa-portal-monitor-grafana" is invalid: spec.selector: Invalid value: v1.LabelSelector{MatchLabels:map[string]string{"app.kubernetes.io/instance":"prd-ipa-portal-monitor", "app.kubernetes.io/name":"grafana"}, MatchExpressions:[]v1.LabelSelectorRequirement(nil)}: field is immutable
```

Esse erro é causado pela tentativa de atualização de recursos imutáveis no Kubernetes. Para alguns recursos, o k8s precisa remover e recriar. Para ambientes de desenvolvimento e homologação, fazer 'Sync' com 'Prune' e 'Force' marcados no Argo é suficiente. 

> :warning: **Atenção** 
> 
> Para PRODUÇÃO, abra um registro de incidente na Plataforma **ITSM**, vincule ao Grupo de Atribuição **GS GPROM - High End - Monitoração** e solicite a realização do *Sync* com *Prune* e *Force*, no namespace. 

## Erro da versão do chart

No Argo:
```
Error: error unpacking grafana-7.1.5.tgz in bbmonit: apiVersion 'v2' is not valid. The value must be "v1"
```

Significa que você não modificou seu Chart.yaml como especificado nos passos acima.

No seu `Chart.yaml`, troque

```yaml
apiVersion: v1
```

por

```yaml
apiVersion: v2
```

## Erro no download de plugins

No log do pod, algo parecido com isso:
```
W
Error: ✗ Get "https://grafana.com/api/plugins/repo/map%5Blist:%5B%5D%5D": context deadline exceeded (Client.Timeout exceeded while awaiting heade
rs)

```

Significa normalmente que a seção de plugins está fora do lugar. Volte ao início do roteiro para verificar a solução.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/atualizar-grafana.md&internalidade=monitoracao/atualizar-grafana)
