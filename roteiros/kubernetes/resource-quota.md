> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/resource-quota.md&amp;action_name=kubernetes/resource-quota.md)

# Resource quota e resource limits no k8s

## Objetivo

Este roteiro visa detalhar como funciona o provisionamento de recurso computacional dentro do contexto de aplicações da DevCloud.

## Sumário

[[_TOC_]]

## Resource quota

ResourceQuota é um tipo de objeto do Kubernetes usado para definir limites de uso de recurso computacional em um determinado contexto. Na DevCloud, a sua configuração por parte do time de infraestrutura previne que um namespace mal configurado expanda seu consumo de recursos indefinidamente e impacte outras aplicações.

## Resource limit

Resouce limits são políticas aplicadas por recurso, como pods e conteineres.

## Consultando cotas e limites

A saída do comando `kubectl describe namespace <meu-namespace>` inclui Resource quota do namespace e os resource limits aplicáveis aos pods e conteineres daquele namespace.

Por padrão, é algo parecido com isso:

```
Resource Quotas
  Name:                             wlt-carteiras-digitais
  Resource                          Used    Hard
  --------                          ---     ---
  count/configmaps                  2       10
  count/cronjobs.batch              0       5
  count/deployments.apps            1       6
  count/dnsingresses.psc.bb.com.br  1       2
  count/ingresses                   0       5
  count/jobs.batch                  0       10
  count/persistentvolumeclaims      0       2
  count/pods                        2       20
  count/secrets                     8       20
  count/services                    1       12
  count/services.loadbalancers      0       0
  count/services.nodeports          0       0
  count/statefulsets.apps           0       0
  limits.ephemeral-storage          0       10Gi
  limits.memory                     1500Mi  20Gi
  requests.cpu                      20m     5
  requests.ephemeral-storage        0       10Gi
  requests.memory                   1000Mi  10Gi
  requests.storage                  0       5Gi

Resource Limits
 Type       Resource  Min  Max  Default Request  Default Limit  Max Limit/Request Ratio
 ----       --------  ---  ---  ---------------  -------------  -----------------------
 Container  memory    -    8Gi  8Gi              8Gi            -
 Pod        memory    -    8Gi  -                -              -
```

Na seção de "Resource Quotas", é possível ver quanto o namespace usa (na coluna Used) e qual é o limite (na coluna Hard). Note que as cotas incluem não somente memória e cpu, mas também quantidade de certos tipos de recursos. Na saída de exemplo, temos que só é permitida a declaração de dois dnsingress no namespace.

Na seção de "Resource limits", temos limitação de memória por pod e por conteiner. Isso significa que mesmo que o limite de memória desse namespace seja 20Gi, não é possível subir um pod que exija mais de 8Gi de limite de memória.

## Entendendo o uso do namespace

A quantidade de recursos usada pelo namespace inclui todos os objetos ativos simultaneamente. Isso quer dizer que, por exemplo, para namespaces que utilizam o curió, o consumo de recursos deve ser somado entre as seções da aplicação e do curió.

## Resource quota no deploy

Um problema comum, especialmente em produção, é ter o Resource Quota impactando no deploy, impedindo-o na prática.

Suponha um namespace que tenha 10 unidades de CPU de limite no Resource Quota. O time de desenvolvimento, por temer alta carga no ambiente de produção, resolve calcular quantos pods vai utilizar. Soma os limites de CPU da aplicação (por exemplo, 500m) com os do curió (500m também nesse exemplo), e ajusta o `replicaCount` ou o número máximo de réplicas do HPA para 10. Parece uma conta simples, (500m + 500m)x10 vai caber nas 10 unidades de CPU da cota. Ao fazer o ajuste, vai funcionar corretamente.

O problema é que o próximo deploy não vai acontecer corretamente. No momento em que algo for atualizado no values, como a versão da aplicação ou uma nova operação consumida no curió, o Argo vai detectar as mudanças e iniciar a criação dos novos pods em um novo ReplicaSet. E aí está o problema. Utilizando a estratégia padrão de `RollingUpdate`, o k8s vai tentar criar pods da nova versão antes de tirar de operação pods antigos, e esses pods novos não terão mais disponibilidade de cota para serem criados. O deploy fica "congelado" com erro no `ReplicaSet`.

-----

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/resource-quota.md&internalidade=kubernetes/resource-quota)