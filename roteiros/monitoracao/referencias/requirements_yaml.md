> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

**requirements.yaml**
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/referencias/requirements_yaml.md&amp;action_name=monitoracao/referencias/requirements_yaml)

```yaml
dependencies:
  - name: prometheus
    version: 8.0.9
    repository: https://chartmuseum.devops.nuvem.bb.com.br/bb/catalogo
    condition: prometheus.enabled
  - name: grafana
    version: 6.0.6
    repository: https://chartmuseum.devops.nuvem.bb.com.br/bb/catalogo
    condition: grafana.enabled
  - name: alertmanager
    version: 0.0.11
    repository: https://chartmuseum.devops.nuvem.bb.com.br/bb/catalogo
    condition: alertmanager.enabled    
  - name: dnsservice
    version: 0.1.1
    repository: https://chartmuseum.devops.nuvem.bb.com.br/bb/infra
```---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/requirements.md&internalidade=monitoracao/requirements)
