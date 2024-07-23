> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/arquivados/kubectl-cheat-sheet.md&amp;action_name=kubernetes/arquivados/kubectl-cheat-sheet)

# Cheat Sheet do Kubectl

Cheat Sheet oficial do Kubectl do site do Kubernetes [aqui](https://kubernetes.io/docs/reference/kubectl/cheatsheet/).

Este roteiro contém um Cheat Sheet com os principais comandos usados no BB.

Roteiro para configurar o Kubectl no seu computador [aqui](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Install_kubectl.md).

## Legenda

`<namespace-name>` - nome do namespace, como dev-brave-api

`<pod-name>` - nome do pod, como hml-dev-brave-apiregular-7d4cb6b996-5468v, obtido pelo `kubectl get pods`

`<container-name>` - nome do container, como curio-sidecar, des-dev-brave-api, hml-dev-brave-api ou prd-dev-brave-api

`<secret-name>` - nome da secret, obtido pelo `kubectl get secret`, exemplo `oracle`

## Dica

Quando existe apenas um container dentro do Pod, o nome do container é opcional no comando.


## Contextos

Ver os contextos:

```
$ kubectl config get-contexts
```

Exemplo de saída:

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

O asterisco na coluna "CURRENT" mostra em qual cluster você está executando comandos naquele momento.

Para mudar de cluster, usar o valor da coluna "name". Exemplo:

```
$ kubectl config use-context 1.2-k8s-hm
```

## Pods de um namespace no cluster atual

Exemplo para namespace gpf-extracao-open-banking.

```
kubectl get pods -n <namespace-name>

$ kubectl get pods -n gpf-extracao-open-banking
NAME                                                              READY   STATUS    RESTARTS   AGE
des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7       2/2     Running   1          4d1h
des-gpf-extracao-open-banking-on-demand-regular-6dccc64fd7drfkp   2/2     Running   0          10d
```

O parâmetro `-w` ou `--watch` permite que permaneçamos observando os pods:
```
kubectl get pods -n <namespace-name> -w
```

## Deletar um pod

Exemplo de um pod no namespace dev-brave-api. O segundo parâmetro é o nome do pod obtido pelo `kubectl get pods`.

```
kubectl delete pod -n <namespace-name> <pod-name> --now

$ kubectl delete pod -n dev-brave-api hml-dev-brave-apiregular-7d4cb6b996-5468v --now
```
Importante: **Deletar pods e outras intervenções não funcionam no ambiente de produção.** Para intervenções nesse ambiente, a equipe responsável pelo aplicativo/namespace deve abrir RDI para a equipe DITEC/UOS/GPROM/D3/E31/MONITORAMENTO HIGH-END aplicando o modelo de RDI correspondente à solicitação(por exemplo, "GPROM - CLOUD BB - PRODUCAO - RESTART DE PODS") e substituindo os campos entre ##.

Importante (2): **Ações de intervencação como deletar pods, mesmo em ambientes que não o de produção, exigem papel ALMFE na sigla.** 

## Descrever as informações de um pod
```
kubectl describe pod <pod-name> -n <namespace-name>
```

## Entrar no Shell do Pod e executar comandos

Este recurso é interessante para fazermos testes dentro do nosso container, como versão do sistema operacional, versão do Java, conectividade de rede, etc.

```
kubectl exec -it <pod-name> -n <namespace-name> -c <container-name> <comando para executar>
```

Executando um `ls /` dentro do Curió
```
$ kubectl exec -it des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 -n gpf-extracao-open-banking -c curio-sidecar /bin/sh
[docker] [curio@des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 /opt/iib-curio] [0]$ ls /
bin     dev     docker  etc     home    lib     media   mnt     opt     proc    root    run     sbin    srv     sys     tmp     usr     var
[docker] [curio@des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 /opt/iib-curio] [0]$ exit
```

Executando um `java --version` dentro de um container
```
$ kubectl exec -it des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 -n gpf-extracao-open-banking -c des-gpf-extracao-open-banking /bin/sh
kubectl exec [POD] [COMMAND] is DEPRECATED and will be removed in a future version. Use kubectl exec [POD] -- [COMMAND] instead.
[docker] [root@des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 /app] [0]# java --version
openjdk 11.0.4 2019-07-16 LTS
OpenJDK Runtime Environment 18.9 (build 11.0.4+11-LTS)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.4+11-LTS, mixed mode, sharing)
```

Importante: os desenvolvedores só tem acesso a este recurso nos ambientes de desenvolvimento e homologação.

## Logs do Pod

```
kubectl  logs <pod-name> -n <namespace-name> -c <container-name>
```

Parâmetro "-f" para seguir o log
```
kubectl logs -f <pod-name> -n <namespace-name> -c <container-name>
```

Enviando o resultado do comando para um arquivo seuarquivo.txt novo:
```
kubectl logs <pod-name> -n <namespace-name> -c <container-name> > seuarquivo.txt
```

Apendando o resultado do comando para um arquivo seuarquivoJahExistente.txt já existente:
```
kubectl logs <pod-name> -n <namespace-name> -c <container-name> >> seuarquivoJahExistente.txt
```

Exemplo do comando:
```
$ kubectl logs des-gpf-extracao-open-banking-batch-regular-9fb4d447d-ftkv7 -n gpf-extracao-open-banking -c des-gpf-extracao-open-banking
```

## Secrets

No ambiente de homologação e produção, é preciso ter acesso conforme [este roteiro do PSC](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/PaaS/Acessos-do-desenvolvedor).

### Consultar secrets disponíveis no namespace

```
kubectl get secret -n <namespace-name>
```

### Detalhar secret do namespace

A <secret-name> é obtida no `get secret`

```
kubectl describe secret <secret-name> -n <namespace-name>
```

### Exibir o conteúdo de uma key de uma secret

Primeiro é preciso descobrir o nome da secret do namespace:
```
$ kubectl get secret -n gpf-apib3-batch-posicao-carga-delta
NAME                  TYPE                                  DATA   AGE
default-token-cprjd   kubernetes.io/service-account-token   3      102d
gpf-b3-redis          Opaque                                2      20d
hm-gpf-b3-secret      Opaque                                1      54d
```

Depois é preciso obter o nome das keys presentes na secret:
```
$ kubectl describe secret hm-gpf-b3-secret -n gpf-apib3-batch-posicao-carga-delta
Name:         hm-gpf-b3-secret
Namespace:    gpf-apib3-batch-posicao-carga-delta
Labels:       <none>
Annotations:  <none>

Type:  Opaque

Data
====
DB_PASSWORD:  12 bytes
```

Agora o comando para exibir conteúdo de uma key de uma secret:
```
$ kubectl get secret <SECRET_NAME> -n <namespace-name> -o jsonpath="{.data['KEY_NAME']}" | base64 --decode

$ kubectl get secret hm-gpf-b3-secret -n gpf-apib3-batch-posicao-carga-delta -o jsonpath="{.data['DB_PASSWORD']}" | base64 --decode
<aqui vai aparecer o conteúdo>
```

Se existir um "." no nome da key, como no caso de **hdpgpf.keytab**, faça assim:
```
$ kubectl get secret kafka-keytab -n gpf-extracao-open-banking -o jsonpath="{.data['hdpgpf\.keytab']}"
```

> The `jsonpath` expression `"{.data['KEY_NAME']}"` selects the value of the specified key from the secret's data field. The `base64 --decode` command decodes the Base64-encoded value of the key.

### Cadastrar secret padrão key/valor

```
kubectl -n <namespace-name> create secret generic <secret-name> --from-literal=<nome-item-1-secret>='<valor-item-1-secret>' --from-literal=<nome-item-2-secret>='<valor-item-2-secret>' 
```

Exemplo da criação da secret `keytab` com os pares de valores e conteúdos:
- `id` / `xpto`
- `type` / `kerberos`

```
$ kubectl -n dev-brave-api create secret generic keytab --from-literal=id='xpto' --from-literal=type='kerberos'
```

### Cadastrar secret com um arquivo

É possível incluir um arquivo dentro de uma secret.

```
kubectl -n <namespace-name> create secret generic <secret-name> --from-file=<caminho do arquivo>' 
```

```
kubectl -n dev-brave-api create secret generic banco-central-keystore-content --from-file=./banco-central.jks
```


### Deletar secret

Quando uma secret é deletada, o Pod que a usava passa a ter problemas e precisará ser restartado após a recriação da secret.

```
kubectl -n <namespace-name> delete secret <secret-name>
```

### Editar secret

_Pendente..._

O comando `kubectl -n <namespace-name> edit secret <secret-name>` abre um editor de texto com o conteúdo das secrets e permite a edição delas no arquivo. No entanto, aparentemente isto não funciona, pois fica algum "lixo" dentro do conteúdo das secrets.

Sugestão: deletar e recriar a secret ou fazer a edição diretamente pelo Rancher.

## Copiar arquivo ou pasta do container para o host local

Copie o conteudo /tmp/foo de um pod remoto para /tmp/bar localmente:
```
kubectl cp <namespace-name>/<some-pod>:/tmp/foo -c <container-name> /tmp/bar 
```

```
kubectl cp my-namespace/my-pod:/path/to/file.txt /home/user
```

Se receber o erro **command terminated with exit code 126** significa que sua imagem base não contém o binário do `tar`. Neste caso, você pode copiar o arquivo desta forma:

Abaixo, o arquivo /tmp/teste.log está sendo copiado do container para o seu arquivo local frompod.log
```
kubectl exec -i <some-pod> -n <namespace-name> -c <container-name> -- cat /tmp/teste.log > frompod.log
```

Exemplo onde copiei o arquivo `/mfc-broker-reuters/fix/store/logs/FIX.4.4-BBBRASIL-FXALL.messages.log` de um container para o arquivo `frompod.log` local.
```
kubectl exec -i prd-mfc-broker-reuters-regular-684f68f89d-shkdg -n mfc-broker-reuters -c prd-mfc-broker-reuters -- cat /mfc-broker-reuters/fix/store/logs/FIX.4.4-BBBRASIL-FXALL.messages.log > frompod.log
```




## Copiar arquivo ou pasta do host para dentro do container

_Pendente..._

Segundo [este roteiro](https://medium.com/@nnilesh7756/copy-directories-and-files-to-and-from-kubernetes-container-pod-19612fa74660) teoricamente do jeito deveria funcionar, no entanto, nos testes usando o Kubectl do Windows não funcionou:

POD in a specific container
```
kubectl cp <file-spec-src> <file-spec-dest> -c <specific-container>
```

Copy /tmp/foo local file to /tmp/bar in a remote pod in namespace:
```
kubectl cp /tmp/foo <namespace-name>/<some-pod>:/tmp/bar
```

Copy /tmp/foo from a remote pod to /tmp/bar locally:
```
kubectl cp <namespace-name>/<some-pod>:/tmp/foo /tmp/bar
```

## HPA - Horizontal Pod Autoscaler

Consultar HPA:
```
kubectl get hpa -n <namespace-name>
```

## Service

Obter services de um namespace:
```
kubectl get service -n <namespace-name>

$ get service -n gpf-extracao-open-banking
NAME                          TYPE        CLUSTER-IP      EXTERNAL-IP   PORT(S)           AGE
extracao-open-banking-batch   ClusterIP   10.43.152.180   <none>        80/TCP,8081/TCP   29d
gpf-open-banking-on-demand    ClusterIP   10.43.228.165   <none>        80/TCP,8081/TCP   362d
```

Descrever uma service:
```
kubectl describe service <service> -n <namespace-name>

$ kubectl describe service extracao-open-banking-batch -n gpf-extracao-open-banking
```

## Ingress

Ler o ingress:
```
kubectl get ingress -n <namespace-name>
```

Descrever o ingress:
```
kubectl describe ingress <ingress-name> -n <namespace-name>
```

## Volumes

Ler os volumes de um namespace:
```
kubectl get pvc -n <namespace-name>
```

Descrever o volume:
```
kubectl describe pvc <volume-name> -n <namespace-name>
```

Deletar o volume:
```
kubectl delete pvc <volume-name> -n <namespace-name>
```

Importante: se der o comando de delete do volume, possivelmente o comando só vai ter efeito quando o pod em execução que acesse o volume pare de executar.

## Resource Quota 
Verificar o Resource Quota (limitação de recursos numa namespace).

Importante: Caso haja mais de uma ResourceQuota a com menores valores absolutos (limitação de recursos) é que irá valer.
```
kubectl describe resourceQuota -n <namespace-name>
```




Estamos à disposição para eventuais dúvidas. Se precisar, abra uma issue em https://fontes.intranet.bb.com.br/dev/publico/atendimento.

Sugestões, criticas, melhorias e colaborações são bem-vindas! Obrigado!
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/kubectl-cheat-sheet.md&internalidade=kubernetes/kubectl-cheat-sheet)
