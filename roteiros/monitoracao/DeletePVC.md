> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Como deletar o PVC do prometheus
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/DeletePVC.md&amp;action_name=monitoracao/DeletePVC)

Este procedimento se aplica quando o Prometheus fica indisponível e no log do pod do Prometheus (no ArgoCD) aparece o erro:

```
"err":"opening storage failed: block dir: \"/prometheus/01EWT9MKVMR3A25C9M66XADZ4C\"
```

Isto significa que o PVC (volume persistente) foi corrompido e só resta deletar o PVC para corrigir a situação.

> Será necessário acesso ao papel ALMFD ou ALMFE na sigla e executar alguns comandos `kubectl`, então caso não possua, instale de acordo com [este outro roteiro](<https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/roteiros/geracao-kubeconfig.md>).

Para isso, é necessário primeiro listar os PVCs da sua stack de monitoração com o comando:

COMANDO PARA LISTAR OS PVCs

```shell
kubectl get pvc -n abc-monitor   # onde abc-monitor é o nome do seu namespace
```

O PVC do Prometheus tem um nome parecido com este:

```shell
prometheus-des-abc-monitor-prometheus-prometheus-db-prometheus-des-abc-monitor-prometheus-prometheus-0   # onde abc-monitor é o nome do seu namespace
```

Obs.: Se tiver mais de um PVC, pegue o nome de todos eles.

COMANDO PARA PEGAR O NOME DO PROMETHEUS

```shell
kubectl get prometheus -n abc-monitor  # onde abc-monitor é o nome do seu namespace
```

Agora você vai precisar executar dois comandos na sequência, pois você só consegue deletar o PVC se ele não estiver sendo utilizado pelo Prometheus, mas quando você deleta o Prometheus, o ArgoCD já inicia a sua subida novamente, então o segundo comando precisa ser muito rápido.

COMANDO PARA DELETAR O PROMETHEUS:

```shell
kubectl delete prometheus des-abc-monitor-prometheus-prometheus --namespace abc-monitor   # onde abc-monitor é o nome do seu namespace
```

COMANDO PARA DELETAR O PVC

```shell
kubectl delete pvc -n abc-monitor prometheus-des-abc-monitor-prometheus-prometheus-db-prometheus-des-abc-monitor-prometheus-prometheus-0 --grace-period=0 --force   # onde abc-monitor é o nome do seu namespace
```

Liste novamente os PVCs para conferir, pois se deu certo o tempo de criação (AGE) do PVC do Prometheus será bem recente (segundos ou minutos).

COMANDO PARA LISTAR OS PVCs

```shell
kubectl get pvc -n abc-monitor   # onde abc-monitor é o nome do seu namespace
```

Se não funcionou, o PVC ficou com status `Terminating` ou continua com tempo de criação em dias, repita os comandos deletando o Prometheus e o PVC, na sequência.

Caso persista o problema, abra uma issue em: 
- https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/DeletePVC.md&internalidade=monitoracao/DeletePVC)
