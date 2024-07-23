> :grey_exclamation: **Importante** 
> 
> A página de *troubleshooting* serve como um recurso central para solucionar problemas comuns relacionados ao Kubernetes. <br>
>O problema é sempre identificado no título, e abaixo oferecemos soluções testadas e diretrizes simples para ajudar na resolução. 

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/troubleshooting.md&amp;action_name=kubernetes/troubleshooting)

## O kubectl reconheceu as configurações, mas o computador não reconhece o host da API do Kubernetes

**Descrição:** ao executar o comando kubectl version, **terceirizados** podem encontrar o seguinte erro:
```
Unable to connect to the server: dial tcp: lookup kube-api.nuvem.desenv.bb.com.br on [2804:14d:1:0:181:213:132:3]:53: 
no such host
```
**Solução:** utilizar o proxy corporativo. Como o kubectl lê as configurações de proxy do terminal, é necessário configurar o proxy corporativo. Exporte as variáveis de ambiente do proxy antes de utilizar o `kubectl`. Certifique-se de adaptar o endereço do proxy de acordo com as configurações específicas da sua empresa: <br> 
```
export http_proxy=http://170.66.49.180:3128; export https_proxy=http://170.66.49.180:3128
kubectl version
``` 
> :bulb: **Dica** 
> 
> Para evitar a necessidade de exportar as variáveis de ambiente toda vez que utilizar o kubectl, pode-se criar um alias no arquivo **.bashrc** ou **.zshrc.**, localizados na pasta do usuário(~). A inserção de uma barra antes de kubectl dentro do alias resolve a ambiguidade, permitindo que o alias tenha o mesmo nome do comando original: `alias kubectl="http_proxy=http://170.66.49.180:3128 https_proxy=http://170.66.49.180:3128 \kubectl"`. 

## Ainda não encontrou a solução do seu problema?
Abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!   


