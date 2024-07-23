> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

Regra 1:
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/dns-k8s.md&amp;action_name=kubernetes/dns-k8s)
A integração direta entre microsserviços na Arq3 é permitida para microsserviços da MESMA sigla.

Regra 2:
Para integração entre microsserviços de outras siglas deve-se fazer uso de operações IIB via Curió.

Respeitadas as duas regras, para um microsserviço na Arq3 chamar diretamente um outro microsserviço também na Arq3, há três formas de realizar essa chamada:

## 1. Forma simplificada:

1.1. A forma simplificada tem esse padrão:

```
<nome-microsservico>.<nome-namespace>:<porta-servico>/endpoint

```

1.2. Exemplos:

http://prd-gfp-sync-request-api.gfp-sync-request-api:80

http://prd-gfp-ocorrencias.gfp-ocorrencias:80

1.3. Observações:

Uma observação sobre essa forma simplicada é que os dois pods (tanto serviço-consumidor quanto serviço-provedor) DEVEM estar no **mesmo cluster Kubernetes (k8s)**.

Essa forma simplificada **não passa pelo ingress do k8s**, sendo a mais rápida para conexão.

Entretanto, na forma simplificada, se o serviço-provedor mudar de cluster, irá ocorrer um erro de dns no serviço-consumidor.


Também, por padrão, não são permitidos cruzamentos entre ambientes, exemplo: pods de desenolvimento acessando serviços de produção ou homologação e vice-versa.



## 2. Forma K8s:

2.1. A forma K8s tem o sufixo `.cluster.local` e tem esse padrão:

```
<nome-microsservico>.<nome-namespace>.svc.cluster.local:<porta-servico>/endpoint
```

2.2. Exemplos:

http://prd-gfp-sync-request-api.gfp-sync-request-api.svc.cluster.local:80

http://prd-gfp-ocorrencias.gfp-ocorrencias.svc.cluster.local:80

2.3. Observações:

Valem as mesmas regras acima, da forma simplificada.



## 3. Forma Ingress:


3.1. A forma Ingress tem esse padrão:

```
<nome-microsservico>.<sigla>.bb.com.br
```


3.2. Exemplos:

http://gfp-sync-request-api.gfp.bb.com.br:80


3.3. Observações:

A forma Ingress é a melhor forma e deve ser sempre priorizada. 

Entretanto essa forma **passa pelo ingress k8s**, o que pode causar uma demora a mais na conexão.

Contudo, se houver uma mudança de cluster do serviço-provedor, não ocorrerá erro de dns, uma vez que o dnsservice apontará automaticamente para o novo endereço/ip.



## 4. Ingress externo (fora da rede do BB):

Para provimento um dns-externo, que é acessível fora da rede do BB, siga o roteiro:

https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/Roteiro_solicitacao_DNS_INTERNET.md#para-desenvolvimento-eou-homologa%C3%A7%C3%A3o


Já para consumo de um dns-externo é necessário configurar o proxy na aplicação, conforme roteiro:

https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/proxy/proxy.md
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/dns-k8s.md&internalidade=kubernetes/dns-k8s)
