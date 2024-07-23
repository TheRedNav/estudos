> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md&amp;action_name=kubernetes/Como_usar_kubectl_em_cluster)

# Como usar o kubectl para executar comandos nos clusters

Este roteiro ensina como executar os principais comandos usados no BB através do kubectl. O kubectl é a ferramenta de linha de comando para interagir com clusters Kubernetes. Com a ferramenta, os desenvolvedores conseguem gerenciar seus aplicativos e recursos dentro do ambiente Kubernetes de forma mais eficiente e flexível.

## Requisitos

* O kubectl instalado e configurado: [Linux](https://kubernetes.io/pt-br/docs/tasks/tools/install-kubectl-linux/) - [macOS](https://kubernetes.io/docs/tasks/tools/install-kubectl-macos/) - [Windows](https://kubernetes.io/docs/tasks/tools/install-kubectl-windows/)
* O [arquivo **~/.kube/config**](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_configurar_kubectl.md) configurado.
> :information_source: **Observação** 
> 
> Após a instalação, se você receber o aviso *"The connection to the server localhost:8080 was refused - did you specify the right host or port?"* junto com as informações sobre a versão do cliente e do Kustomize, sua instalação foi bem-sucedida. Essa mensagem é exibida porque o kubectl, por padrão, tenta se conectar a um cluster na máquina local.

## Marcadores usados em comandos Kubernetes

Utilize os seguintes marcadores para preencher corretamente os valores necessários ao executar comandos Kubernetes.

> :bulb: **Dica** 
> 
> Quando há apenas um contêiner dentro do pod, é opcional utilizar o nome do contêiner no comando.

| Marcador | Descrição |
| - | - |
| `<namespace-name>` | Refere-se ao nome do *namespace* Kubernetes onde um recurso está localizado. Por exemplo, **dev-brave-api**. |
| `<pod-name>` | Refere-se ao nome do pod Kubernetes, que é uma instância de um ou mais contêineres em execução. Por exemplo, **hml-dev-brave-apiregular-7d4cb6b996-5468v**. | 
| `<container-name>` | Refere-se ao nome do contêiner dentro de um pod Kubernetes. Cada pod contém um ou mais contêineres em execução. O nome do contêiner geralmente é específico para cada aplicação ou serviço. Por exemplo, **prd-dev-brave-api**. |
| `<secret-name>` | Refere-se ao nome de um segredo Kubernetes. Segredos são objetos que contêm dados sensíveis, como senhas, tokens de acesso, chaves SSH, etc. Por exemplo, **oracle**.| 

> :grey_exclamation: **Importante** 
> 
> Para executar ações que modificam os ambientes de **Desenvolvimento** e **Homologação**, o usuário precisa do papel **ALMFE** na sigla. Comandos de leitura podem ser executados sem restrições.
> 
> Para executar ações que modificam o ambiente de **Produção**, a equipe responsável pelo *namespace* deve abrir um RDI para a equipe **DITEC/UOS/GPROM/D3/E31/MONITORAMENTO HIGH-END**, aplicando o modelo de RDI correspondente à solicitação e substituindo os campos entre as cerquilhas (##). Comandos de leitura podem ser executados sem restrições.

## Menu de ações

* [Entrar no shell do pod e executar comandos](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#entrar-no-shell-do-pod-e-executar-comandos)
* [Gerenciar o Deployment](#gerenciar-o-deployment)
* [Gerenciar o Ingress](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#gerenciar-o-ingress)
* [Gerenciar namespaces](#gerenciar-namespaces)
* [Gerenciar secrets](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#gerenciar-secrets)
* [Gerenciar services](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#gerenciar-services)
* [Gerenciar pods](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#gerenciar-pods)
* [Gerenciar volumes (PVCs)](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#gerenciar-volumes-pvcs)
* [Listar HPAs](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#listar-hpas)
* [Obter o Resource Quota](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#obter-informa%C3%A7%C3%B5es-do-resource-quota) 
* [Visualizar contextos](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#visualizar-contextos)
* [Visualizar os logs](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_usar_kubectl_em_cluster.md#visualizar-os-logs)

## Entrar no shell do pod e executar comandos 

Entrar no *shell* do pod permite que você depure, diagnostique ou execute comandos específicos dentro do ambiente.

**Comando:** `kubectl exec -it <pod-name> -n <namespace-name> -c <container-name> <comando para executar>`

**Exemplo 1** <br>
Execução do comando `ls /` dentro do Curió.

1. **Entrada:** <br>
$ kubectl exec -it des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 -n gpf-extracao-open-banking -c curio-sidecar /bin/sh <br>
2. **Saída:** <br>
[docker] [curio@des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 /opt/iib-curio] [0] <br>
3. **Entrada:** <br>
$ ls / <br>
4. **Saída:** <br>
bin     dev     docker  etc     home    lib     media   mnt     opt     proc    root    run     sbin    srv     sys     tmp     usr     var <br>
[docker] [curio@des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 /opt/iib-curio] [0]

**Exemplo 2**<br> 
Cópia do arquivo *hello*, que está localizado dentro do contêiner, para o arquivo *hellocopy* no host local. <br>

* **Entrada:** <br>
kubectl exec t99-curso-nuvem-f1647417-chart-name-regular-658c596d97-2k9q9 -n t99-curso-nuvem-f1647417 -c t99-curso-nuvem-f1647417 cat hello.txt > hellocopy.txt

## Gerenciar o Deployment
O Deployment oferece atualizações de forma declarativa para Pods e ReplicaSets.

### Listar recurso de Deployment
**Comando:** `kubectl get deployment -n <namespace-name>`

### Deletar o Deployment
**Comando:** `kubectl delete deployment <deployment-name> -n <namespace-name>`

> :red_circle: **Cuidado** 
> 
> Esse comando exclui imediatamente o Deployment específico dentro do *namespace* fornecido, além de deletar também o ReplicaSet e o Pod associados ao Deployment.

## Gerenciar namespaces

Os namespaces fornecem um mecanismo para isolar grupos de recursos dentro de um único cluster.

### Listar namespaces do cluster
**Comando:** `kubectl get namespaces`

### Descrever um namespace
**Comando:** `kubectl describe namespace <namespace-name>`

## Gerenciar o Ingress

Utilizar o *Ingress* permite que você gerencie o acesso externo aos serviços em execução dentro do cluster. 

### Listar os recursos de Ingress
**Comando:** `kubectl get ingress -n <namespace-name>`

### Obter informações dos recursos de Ingress
**Comando:** `kubectl describe ingress <ingress-name> -n <namespace-name>` <br>

## Gerenciar secrets

Os *secrets* são objetos que armazenam e gerenciam informações confidenciais de forma segura. Utilizar *secrets* garante que as suas informações sensíveis sejam acessadas apenas por aplicativos autorizados. Ao criar e gerenciar *secrets*, é importante considerar práticas de segurança recomendadas.

> :grey_exclamation: **Importante** 
> 
> Para gerenciar *secrets* nos ambientes de Homologação e Produção, é preciso atender aos requisitos do roteiro [Acessos do desenvolvedor](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/PaaS/Acessos-do-desenvolvedor).

### Criar um secret genérico com um par de chave/valor

**Comando:** `kubectl -n <namespace-name> create secret generic <secret-name> --from-literal=<nome-item-1-secret>='<valor-item-1-secret>' --from-literal=<nome-item-2-secret>='<valor-item-2-secret>'`

**Exemplo** <br>
Criação do *secret* `keytab` com os pares de valores e conteúdos: 
- id = `xpto`
- type = `kerberos`

* **Entrada:** <br>
$ kubectl -n dev-brave-api create secret generic keytab --from-literal=id='xpto' --from-literal=type='kerberos'

### Listar os secrets disponíveis no namespace
**Comando:** `kubectl get secret -n <namespace-name>`

**Exemplo de saída**
```
NAME                  TYPE                                  DATA   AGE
default-token-cprjd   kubernetes.io/service-account-token   3      102d
gpf-b3-redis          Opaque                                2      20d
hm-gpf-b3-secret      Opaque                                1      54d
```

### Obter informações de um secret do namespace
**Comando:** `kubectl describe secret <secret-name> -n <namespace-name>`

**Exemplo de saída**
```
Name:         hm-gpf-b3-secret
Namespace:    gpf-apib3-batch-posicao-carga-delta
Labels:       <none>
Annotations:  <none>

Type:  Opaque

Data
====
DB_PASSWORD:  12 bytes
```
### Deletar um secret
**Comando:** `kubectl -n <namespace-name> delete secret <secret-name>`

> :warning: **Atenção** 
> 
> Se um *secret* for deletado, o pod que o estava utilizando enfrentará problemas e precisará ser reiniciado após a recriação do *secret*.

## Gerenciar services

Utilizar comandos com o *service* permite que você execute operações para gerenciar a comunicação entre os diferentes componentes de uma aplicação dentro do cluster. 

### Listar os services de um namespace
**Comando:** `kubectl get service -n <namespace-name>`

**Exemplo de saída**

```
NAME                          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)           AGE
extracao-open-banking-batch   ClusterIP   10.43.152.180   <none>        80/TCP,8081/TCP   29d
gpf-open-banking-on-demand    ClusterIP   10.43.228.165   <none>        80/TCP,8081/TCP   362d
```

### Obter informações de um service específico dentro de um namespace
**Comando:** `kubectl describe service <service> -n <namespace-name>`

## Gerenciar pods
Os pods são a menor unidade escalável no Kubernetes e são essenciais para implantar e executar aplicativos nesse ambiente.  

### Listar os pods do namespace

**Comando:** `kubectl get pods -n <namespace-name>`

**Exemplo** 

1. **Entrada:** <br>
$ kubectl get pods -n gpf-extracao-open-banking <br>
2. **Saída:** <br>
```
NAME                                                              READY   STATUS    RESTARTS   AGE
des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7       2/2     Running   1          4d1h
des-gpf-extracao-open-banking-on-demand-regular-6dccc64fd7drfkp   2/2     Running   0          10d
```
> :bulb: **Dica** 
> 
> Para monitorar continuamente os pods, adicione o parâmetro `-w` ou `--watch` no final do comando:
`kubectl get pods -n <namespace-name> -w`.

### Obter as informações de um pod

**Comando:** `kubectl describe pod <pod-name> -n <namespace-name>`

### Deletar um pod

**Comando:** `kubectl delete pod -n <namespace-name> <pod-name> --now` <br>
> :red_circle: **Cuidado** 
> 
> Esse comando exclui imediatamente o pod específico dentro do *namespace* fornecido.

## Gerenciar volumes (PVCs)

PersistentVolumeClaims (PVCs) são recursos usados para solicitar e provisionar armazenamento persistente, fornecendo uma abstração simplificada para gerenciar o armazenamento de dados. Utilizar comandos com PVCs permite que você crie, liste, descreva, modifique e exclua solicitações de armazenamento persistente.

### Ler o volume de um namespace
**Comando:** `kubectl get pvc -n <namespace-name>`

### Obter informações do volume de um namespace
**Comando:** `kubectl describe pvc <volume-name> -n <namespace-name>`

### Deletar o volume de um namespace
**Comando:** `kubectl delete pvc <volume-name> -n <namespace-name>`

> :warning: **Atenção** 
> 
> Se o comando de exclusão do volume for executado, é possível que ele só tenha efeito quando o pod em execução, que acessa o volume, parar de executar.

## Listar HPAs

Os HPAs são usados para ajustar automaticamente o número de réplicas de um conjunto de pods com base em métricas específicas, como o uso da CPU ou a quantidade de tráfego de rede. Listar os HPAs em um *namespace* permite que você veja quais recursos estão configurados para escalar automaticamente com base em métricas definidas.

**Comando:** `kubectl get hpa -n <namespace-name>`

## Obter informações do Resource Quota

Utilizar esse comando permite que você obtenha informações sobre a alocação de recursos, limites e uso atual dentro do namespace especificado. 

**Comando:** `kubectl describe resourceQuota -n <namespace-name>`

> :warning: **Atenção** 
> 
> No Kubernetes, ao lidar com múltiplas cotas em um mesmo *namespace*, os limites mais restritivos serão aplicados. Por exemplo, se uma ResourceQuota estabelece um limite de 1GB de memória para os pods e outra define um limite de 2GB, o Kubernetes aplicará o limite mais baixo, ou seja, 1GB. Certifique-se de definir as cotas de recursos adequadamente para evitar possíveis excedentes e garantir uma distribuição equitativa dos recursos.

## Visualizar contextos

Contextos são conjuntos de parâmetros que determinam os clusters, os usuários e os *namespaces* padrão que o kubectl utilizará para a comunicação com o cluster Kubernetes. Visualizar os contextos permite que você veja facilmente os diferentes contextos configurados e identifique qual está atualmente selecionado para uso.

**Comando:** `$ kubectl config get-contexts`

**Exemplo de saída**
```
CURRENT   NAME                 CLUSTER          AUTHINFO         NAMESPACE
*         1.1-k8s-desenv       k8s-desenv       k8s-desenv       
          1.2-k8s-hm           k8s-hm           k8s-hm           
          1.3-k8s-servicos     k8s-servicos     k8s-servicos     
          1.5-k8s-automacao    k8s-automacao    k8s-automacao    
          2.1-k8s-apps-des     k8s-apps-des     k8s-apps-des     
          2.2-k8s-apps-hml     k8s-apps-hml     k8s-apps-hml     
          2.3-k8s-apps-prd     k8s-apps-prd     k8s-apps-prd     
          2.4-k8s-spi-prd      k8s-spi-prd      k8s-spi-prd      
          2.5-k8s-canais-prd   k8s-canais-prd   k8s-canais-prd   
          2.5-k8s-gke-prd      k8s-gke-prd      k8s-gke-prd      
          3.1-k8s-data-des     k8s-data-des     k8s-data-des     
          3.2-k8s-data-hml     k8s-data-hml     k8s-data-hml     
          3.3-k8s-data-prd     k8s-data-prd     k8s-data-prd 
```
> :bulb: **Dica** 
> 
> O asterisco na coluna **CURRENT** indica o cluster no qual os comandos estão atualmente sendo executados. Para mudar para outro cluster, adicione ao comando o valor encontrado na coluna **NAME**: `$ kubectl config use-context 1.2-k8s-hm`.

## Visualizar os logs

Visualizar os logs de um pod específico permite que você depure, diagnostique problemas ou monitore o comportamento de um aplicativo em execução.

**Comando:** `kubectl logs <pod-name> -n <namespace-name>`

> :bulb: **Dica** 
> 
> Os parâmetros podem ser posicionados em qualquer ordem dentro do comando, desde que os parâmetros estejam corretamente escritos e em conformidade com a sintaxe. Por exemplo, para seguir os logs em tempo real, exibindo novos logs conforme eles são gerados, adicione o parâmetro `-f` ou `--follow`:
`kubectl logs <pod-name> -n <namespace-name> -f`.

**Tags:** #kubectl #kubernetes #comandos #cluster

## A seguir
* Acesse a página oficial do Kubernetes para consultar outros [comandos kubectl e flags](https://kubernetes.io/pt-br/docs/reference/kubectl/cheatsheet/) frequentemente usados.
* Para comandos mais específicos, acesse [Kubectl Kubernetes CheatSheet](https://github.com/dennyzhang/cheatsheet-kubernetes-A4).
* Se necessário, consulte a página de resolução de problemas - [*troubleshooting*](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/troubleshooting.md).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_.md&internalidade=ofertas/Como_)
