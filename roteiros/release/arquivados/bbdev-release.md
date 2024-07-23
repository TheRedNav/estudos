> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

**BBDev Release**
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/arquivados/bbdev-release.md&amp;action_name=release/arquivados/bbdev-release)

O BBDev Release (https://release.bbdev.dev.intranet.bb.com.br) é uma solução que visa facilitar a edição do arquivo **values.yaml** dos repositórios de release de aplicações que rodam na Cloud BB.


...

**Sumário**

[[_TOC_]]


...

## 1. Acesso e visualização de Siglas [](#anchors-in-markdown)

O BBDev Release recupera as siglas que o desenvolvedor possui acesso no Gitlab/Fontes.

Para obter acesso a alguma sigla, consulte [este outro roteiro](<https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/RoteiroCompleto.md#41-pap%C3%A9is-de-acesso-alm>)


## 2. Selecionando o ambiente

Na tela inicial do BBDev Release (https://release.bbdev.dev.intranet.bb.com.br), depois de selecionada a sigla e o projeto é possível visualizar os 3 repositórios de release release "des-", "hml-" e "prd-".

Selecione o ambiente que você deseja configurar, então o respectivo **values.yaml** do ambiente selecionado será alterado.

| Ambiente      |       |
| :------------:|:-------------|
| des           | desenvolvimento |
| hml           | homologação      |
| prd           | produção      |

![Ambiente](release/arquivados/imagens/bbdev-release-tela-001-ambiente.png)


## 3. Definindo a versão da imagem docker da aplicação para deploy

Para realizar deploy de uma imagem docker, habilite a seção `deployment` e defina a versão da aplicação.

> :exclamation: - É preciso que esta versão da aplicação tenha sido compilada anteriormente no [Jenkins](<https://cloud.ci.intranet.bb.com.br/>) e publicada no [Artifactory](<http://atf.intranet.bb.com.br/artifactory/webapp/>), conforme o [passo 8 deste outro roteiro](<https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/RoteiroCompleto.md#8-gerar-uma-nova-vers%C3%A3o-da-imagem-do-seu-projeto-no-atf>).

![Versão](release/arquivados/imagens/bbdev-release-tela-002-deploy-versao.png)


## 4. Definindo o número de réplicas

Se refere ao número de pods (máquinas virtuais) que serão levantadas para atendimento do serviço.

Nos ambientes de desenvolvimento e homologação defina somente um pod (1 réplica) para atendimento, sendo que para o ambiente de produção é recomendado pelo menos 3 réplicas.

Depois de monitorar o consumo (tráfego/memória/CPU) em produção você pode redimensionar o número de réplicas de acordo com a necessidade.

![Réplicas](release/arquivados/imagens/bbdev-release-tela-003-deploy-replicas.png)

## 5. Incluindo NodeSelector

Se refere a algum recurso que o Node do Kubernentes precisa ter para rodar a aplicação, como por exemplo:

```yaml
feature.node.k8s.bb/nas: true   # node que possui suporte a criação de volumes
acesso.node.k8s.bb/smtp: true   # node com suporte ao serviço de SMTP

```

![NodeSelector](release/arquivados/imagens/bbdev-release-tela-005-nodeselector.png)


## 6. Configurando o Readiness Probe e o Liveness Probe


Para que a nuvem BB (Kubernetes) possa monitorar a saúde da sua aplicação, dois endpoints são necessários que sejam expostos pela sua aplicação com os seguintes objetivos:

  1. [/health/live] testar quando a aplicação está "viva";
  2. [/health/ready] testar se a aplicação está pronta para atender.
 
Comece ativando esses dois recursos "Readiness Probe" e o "Liveness Probe"

Em seguida, configure conforme as informações a seguir:

**Liveness Probe**:
- initialDelaySeconds: 120
- httpGet-Path: /health/live
- httpGet-Port: 8080  # ou de acordo com a porta que o seu container docker expõe
- periodSeconds: 10
- timeoutSeconds: 5
- failureThreshold: 6
- successThreshold: 1

**Readiness Probe**:
- initialDelaySeconds: 120
- httpGet-Path: /health/ready
- httpGet-Port: 8080  # ou de acordo com a porta que o seu container docker expõe
- periodSeconds: 10
- timeoutSeconds: 5
- failureThreshold: 6
- successThreshold: 1


## 7. Configurando o Ingress

O Ingress oferece uma maneira de abstrair um conjunto de serviços. Utilizando um conjunto de regras, 
ele é capaz de rotear tráfego inbound para serviços internos e pods do cluster.

O BBDev Release permite configurar a porta que sua aplicação irá atender por http, que normalmente é porta 80. 

Permite também ativar o TLS, que é a conexão segura, informando uma secret onde estará armazenado seu certificado. 
Para mais informações acesse [este outro roteiro](<https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/ROTEIROS.md>).

No campo Host você deve informar o DNS definido para o serviço. 
Para mais informações sobre os padrões de nomenclatura de DNS, 
acesse [esta documentação do chart de DNSService](<https://charts.nuvem.bb.com.br/charts/bb-cloud/dnsservice>).

![Ingress](release/arquivados/imagens/bbdev-release-tela-006-ingress.png)

## 8. Configurando o Service

Um Service agrupa um conjunto de endpoints de pod em um único recurso.

O BBDev Release permite configurar a origem e destino da requisições, 
como por exemplo: se você configurou no Ingress para seu DNS atender na porta 80, 
você deverá colocar 80 no campo **Origem**.

Sendo que no **Destino** você deve informar a porta que a aplicação responde, como por exemplo, o Quarkus responde na porta 8080, o Curió responde na porta 8081, etc.

O **Nome** do service é atribuído automaticamente pelo Kubernentes, portanto, recomenda-se deixar esse campo em branco.

![Service](release/arquivados/imagens/bbdev-release-tela-007-service.png)

## 9. Utilizando o Curió

O Curió é um container docker capaz de prover, consumir, fazer e escutar publicações de operações IIB do catálogo de operações via HTTP Rest para sua aplicação.

O BBDev Release permite configurar em quais operações o microsserviço é **Consumidor** e em quais operações o microsserviço é **Provedor**.

Para mais informações sobre o Curió acesse [essa outra documentação](<https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio>).


![Curió](release/arquivados/imagens/bbdev-release-tela-008-curio.png)

## 10. Utilizando o Service Monitor

O Service Monitor é a forma do seu microsserviço se comunicar com a stack de Monitoração da sua sigla.

Basicamente só é necessário informar qual é o namespace de monitoração da sua sigla que o monitor passará a enxergar o seu namespace.

![ServiceMonitor](release/arquivados/imagens/bbdev-release-tela-009-service-monitor.png)

## 11. Utilizando o HPA

HPA ou Horizontal Pod Autoscaler é um controle de autoescalonamento horizontal de pods do Kubernetes.

Geralmente, [definindo de forma fixa um número de réplicas](#4-definindo-o-número-de-réplicas) já atende 99% dos casos, por isso recomendamos deixar esse recurso desabilitado.

![HPA](release/arquivados/imagens/bbdev-release-tela-010-hpa.png)

Caso sua aplicação seja uma exceção, ao habilitar o HPA será necessário definir os números mínimo e 
máximo de réplicas e os percentuais de memória e CPU que serão o gatilho para o autoescalonamento.

![HPA](release/arquivados/imagens/bbdev-release-tela-010-hpa-2.png)


## 12. Configurando o DNS

Informe o Host da sua aplicação, conforme padrões de nomenclatura de DNS definidos [nessa documentação do chart de DNSService](<https://charts.nuvem.bb.com.br/charts/bb-cloud/dnsservice>).

![DNS](release/arquivados/imagens/bbdev-release-tela-011-dns.png)

## 13. Salvando as suas alterações

Após concluir as configurações desejadas, salve as alterações clicando no botão `deploy`. 
Será necessário confirmar a branch que será aplicado o deploy.
Se for selecionada a branch **master**, o ArgoCD vai providenciar a sincronização automaticamente.
No caso do ambiente de **produção**, será necessário comitar em outra branch e solicitar um **Merge Request** para a master.

![DNS](release/arquivados/imagens/bbdev-release-tela-012-salvar-alteracoes.png)

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/bbdev-release.md&internalidade=release/bbdev-release)
