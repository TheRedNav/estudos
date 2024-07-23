> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Ref_chamada_dns.md&amp;action_name=kubernetes/Ref_chamada_dns)

Este roteiro detalha como configurar a comunicação entre microsserviços no Kubernetes. Os
microsserviços na Arq3 que pertencem à mesma sigla (ou domínio) podem se comunicar diretamente entre si. Já os microsserviços pertencentes a diferentes siglas devem se comunicar através de operações IIB via Curió.

Há três formas de realizar a comunicação entre microsserviços:

## 1 - Ingress (recomendado)

Esta forma de conexão utiliza o Ingress do Kubernetes, o que pode causar uma certa demora na conexão. No entanto, deve ser priorizada devido à sua robustez e flexibilidade na gestão do tráfego de entrada.

Além disso, se houver uma mudança de cluster do serviço-provedor, não haverá erro de DNS. Isso ocorre porque o dnsservice atualizará automaticamente para o novo endereço/IP, garantindo continuidade e estabilidade na conexão.

A forma Ingress segue o padrão **nome-microsservico.sigla.bb.com.br**.

**Exemplo**
```
http://gfp-sync-request-api.gfp.bb.com.br:80
```
## 2 - Simplificada

Esta forma de conexão é a mais rápida porque não passa pelo ingress do k8s. No entanto, possui algumas regras importantes:
*  Para funcionar corretamente, tanto o serviço-consumidor quanto o serviço-provedor devem estar no mesmo cluster Kubernetes (k8s).
*  Se o serviço-provedor mudar de cluster, o serviço-consumidor enfrentará um erro de DNS, causando interrupções na comunicação.
*  Não são permitidos cruzamentos entre ambientes. Isso significa que pods de desenvolvimento não podem acessar serviços de produção ou homologação, e vice-versa.

A forma simplificada segue o padrão **nome-microsservico.nome-namespace:porta-servico/endpoint**.

**Exemplo**
```
http://prd-gfp-ocorrencias.gfp-ocorrencias:80
```

## 3 - K8s

Esta forma de conexão exige o sufixo **.cluster.local**. 

A forma K8s segue o padrão **nome-microsservico.nome-namespace.svc.cluster.local:porta-servico/endpoint**. 

As mesmas regras da forma simplificada valem para a K8s.

**Exemplo**
```
http://prd-gfp-sync-request-api.gfp-sync-request-api.svc.cluster.local:80
```

## Ingress externo (fora da rede do BB):

Para provimento um dns-externo siga as orientações do roteiro [Solicitação DNS_INTERNET](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/Roteiro_solicitacao_DNS_INTERNET.md#para-desenvolvimento-eou-homologa%C3%A7%C3%A3o).

Para o consumo de um dns-externo é necessário configurar o proxy na aplicação, conforme o roteiro [Proxy](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/proxy/proxy.md).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Ref_chamada_dns.md&amp;action_name=kubernetes/Ref_chamada_dns)
