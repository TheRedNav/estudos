> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/arquivados/Install_kubectl.md&amp;action_name=kubernetes/arquivados/Install_kubectl.md)

# Kubectl

O [kubectl](https://kubernetes.io/docs/tasks/tools/#kubectl) é a ferramenta de linha de comando do Kubernetes. Serve para executar comandos nos clusters, do que pode-se lançar mão em necessidades como deletar pods com problemas, ver logs, consultar secrets, deletar PVCs com problemas, entre outros.

## Motivação

O uso de comandos básicos no kubectl garante a autonomia do desenvolvedor de aplicações em nuvem k8s, em consonância com a prática DevOps.

## Instalação

Consulte a [página oficial](https://kubernetes.io/docs/tasks/tools/#kubectl) e clique na página específica de instalação correspondente ao seu Sistema Operacional e siga as instruções.

Importante: A versão atual do server é 1.19(essa informação pode estar desatualizada quando da leitura desse tutorial. Se houver uma forma de consultar a versão atual do server, sugerir a alteração deste tutorial). O Kubernetes recomenda a instalação de uma versão *minor* a mais ou a menos, no máximo, do que a do server. Veja na página de instalação a nota com a maneira de instalar uma versão específica.

Ao finalizar a instalação, teste:

```
kubectl version
```

A saída deve ser parecida com:

```
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"25", GitVersion:"v1.25.4", GitCommit:"872a965c6c6526caa949f0c6ac028ef7aff3fb78", GitTreeState:"clean", BuildDate:"2022-11-09T13:36:36Z", GoVersion:"go1.19.3", Compiler:"gc", Platform:"darwin/amd64"}
Kustomize Version: v4.5.7
The connection to the server localhost:8080 was refused - did you specify the right host or port?
```

Na última linha, um erro pelo fato do kubectl por padrão buscar um cluster na própria máquina. Se as outras informações apareceram, a instalação deu certo.

## Configuração dos clusters BB

Consulte o [guia de geração do kubeconfig](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/geracao-kubeconfig) para gerar seu aquivo yaml. Siga as instruções atentamente e gere um token para cada um dos clusters de cada um dos ranchers listados(No momento em que esse tutorial foi escrito, eram 12 tokens ao todo). Ao terminar de inserir as informações no arquivo de template sugerido no guia, apague os clusters aos quais você não tem acesso. **É necessário apagar da seção "Users" e da seção "Clusters" do arquivo. Atenção à indentação, pois é um arquivo yaml!**

Para começar a usar as configurações, coloque o arquivo de configuração no exato caminho `~/.kube/config`, se usar Linux ou Mac, onde `~` é a sua pasta de usuário. O arquivo não deve ter extensão. No Mac é possível conferir se ele está com uma extensão oculta utilizando Botão direito do mouse > Get info, no Finder. Se preferir fazer essa operação por linha de comando, na pasta onde está o template, e supondo que o nome do arquivo seja o original(kubeconfig-bbcloud-template.yaml), digite:

```
mkdir ~/.kube
mv kubeconfig-bbcloud-template.yaml ~/.kube/config
```

Caso sua máquina seja Windows, [siga essas instruções](https://kubernetes.io/docs/concepts/configuration/organize-cluster-access-kubeconfig/).

Para verificar se a configuração foi carregada com sucesso, use novamente o comando de versão:

```
kubectl version
```

A mensagem de erro da última linha deve ter sumido.

É possível que, para terceirizados, esse comando mostre um outro erro:

```
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"25", GitVersion:"v1.25.4", GitCommit:"872a965c6c6526caa949f0c6ac028ef7aff3fb78", GitTreeState:"clean", BuildDate:"2022-11-09T13:36:36Z", GoVersion:"go1.19.3", Compiler:"gc", Platform:"darwin/amd64"}
Kustomize Version: v4.5.7
Unable to connect to the server: dial tcp: lookup kube-api.nuvem.desenv.bb.com.br on [2804:14d:1:0:181:213:132:3]:53: no such host
```

Na última linha, um "no such host" no endereço da API. Isso significa que o kubectl reconheceu as configurações, mas seu computador não conhece o host. É necessário usar o proxy corporativo para acessá-lo. O kubectl lê as informações de proxy do terminal, assim, é possível fazer export antes de usar, e o comando funcionará(exemplo com o endereço de proxy da IBM):

```
export http_proxy=http://170.66.49.180:3128; export https_proxy=http://170.66.49.180:3128
kubectl version
```

Para evitar rodar o `export` toda vez que for necessário usar o kubectl, uma solução simples é criar um alias no arquivo `.bashrc` ou `.zshrc`, que ficam na pasta do usuário(~). No exemplo abaixo, foi usado o endereço de proxy da IBM. Adaptar caso você seja funcionário de outra empresa:

```.zshrc
alias kubectl="http_proxy=http://170.66.49.180:3128 https_proxy=http://170.66.49.180:3128 \kubectl"
```
Na linha acima, a barra antes de `kubectl` dentro do alias faz a desambiguação para o alias poder ter o mesmo nome do comando original. Se quiser saber mais detalhes, veja [essa resposta no askubuntu](https://askubuntu.com/a/525242).

Exemplo de saída de sucesso:

```
WARNING: This version information is deprecated and will be replaced with the output from kubectl version --short.  Use --output=yaml|json to get the full version.
Client Version: version.Info{Major:"1", Minor:"25", GitVersion:"v1.25.4", GitCommit:"872a965c6c6526caa949f0c6ac028ef7aff3fb78", GitTreeState:"clean", BuildDate:"2022-11-09T13:36:36Z", GoVersion:"go1.19.3", Compiler:"gc", Platform:"darwin/amd64"}
Kustomize Version: v4.5.7
Server Version: version.Info{Major:"1", Minor:"19", GitVersion:"v1.19.10", GitCommit:"98d5dc5d36d34a7ee13368a7893dcb400ec4e566", GitTreeState:"clean", BuildDate:"2021-04-15T03:20:25Z", GoVersion:"go1.15.10", Compiler:"gc", Platform:"linux/amd64"}
WARNING: version difference between client (1.25) and server (1.19) exceeds the supported minor version skew of +/-1
```
No caso acima, vemos um warning da questão de compatibilidade de versões relatada no início desse tutorial. Para boa parte dos casos de uso, no entanto, isso não será um problema.


## Uso

Ver os contextos:

```
kubectl config get-contexts
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
kubectl config use-context 1.2-k8s-hm
```

Exemplos de uso:

Pods de um namespace no cluster atual(exemplo para namespace dev-brave-api):
```
kubectl get pods -n dev-brave-api
```

Ver informações de um namespace no cluster atual(exemplo para namespace dev-brave-api):

```
kubectl describe dev-brave-api
```

Deletar um pod(exemplo de um pod no namespace dev-brave-api. O segundo parâmetro é o nome do pod obtido pelo `kubectl get pods`):

```
kubectl delete pod -n dev-brave-api hml-dev-brave-apiregular-7d4cb6b996-5468v --now
```
Importante: **Deletar pods e outras intervenções não funcionam no ambiente de produção.** Para intervenções nesse ambiente, a equipe responsável pelo aplicativo/namespace deve abrir RDI para a equipe DITEC/UOS/GPROM/D3/E31/MONITORAMENTO HIGH-END aplicando o modelo de RDI correspondente à solicitação(por exemplo, "GPROM - CLOUD BB - PRODUCAO - RESTART DE PODS") e substituindo os campos entre ##.

Importante (2): **Ações de intervencação como deletar pods, mesmo em ambientes que não o de produção, exigem papel ALMFE na sigla.** 

Mais comandos kubectl [aqui](https://kubernetes.io/docs/reference/kubectl/cheatsheet/).

Estamos à disposição para eventuais dúvidas. Se precisar, abra uma issue em https://fontes.intranet.bb.com.br/dev/publico/atendimento.

Sugestões, criticas, melhorias e colaborações são bem-vindas! Obrigado!
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Install_kubectl.md&internalidade=kubernetes/Install_kubectl)
