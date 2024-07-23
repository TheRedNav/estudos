> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/arquivados/release.md&amp;action_name=release/arquivados/release)

# Release de Aplicação da NuvemBB

## 1. Configuração

O ***release*** de aplicação da NuvemBB é baseado no princípio ***III Configuração*** do [Twelve Factors](https://12factor.net/pt_br/config), o que significa que o próprio desenvolvedor deve configurar o ambiente em que a aplicação será executada, além disso essa configuração deve ser armazenada e versionada. Dessa forma para cada ambiente de execução do Banco existe um repositório de código, além do repositório do código fonte, que se inciam sugestivamente com ***DES***, ***HML*** e ***PRD*** (desenvolvimento, homologação e produção respectivamente), e o ***GIT*** é a ferramenta utilizada para controlar o versionamento. 

A configuração é controlada por basicamente pelos arquivos o ***requirements.yaml*** e o ***values.yaml***, sendo o primeiro uma declaração das dependências e o segundo uma parametrização dos valores definidos pela dependência, sendo que chamamos essa dependência de ***Chart***.

Vamos estudar a configuração dos recursos providos por dois ***charts*** em específco, o ***BBAplic*** e o ***DNSService***, que são a base da maioria das aplicações da NuvemBB, e disponibilizam recursos para que o serviço se integre ao ambiente de execução de aplicações do Banco.

>Existem outros ***Charts*** disponíveis para serem utilizados na NuvemBB, é possível consultar em [Chart Museum](https://charts.nuvem.bb.com.br/), inclusive o próprio BBAplic e DNSService estão listados no catálogo.

## 2. BBAplic

Como nome sugere, é um ***chart*** padrão para aplicações da NuvemBB. O mesmo disponibiliza um ***template*** para configurar recursos executar a aplicação, abaixo detalharemos os principais elementos do template e seus respectivos subrecursos. Atente que apesar de ser de próposito geral, o foco do BBAplic é suportar provimento de serviços através da NuvemBB, então considere o escopo do serviços providos pela aplicação para definir se é ideal utilizar esse chart.

### 2.1 Service

O ***Service*** agrupa um conjunto portas disponilizadas pela sua aplicação, que pode executar em multíplas instâncias, em um único recurso.

Imagine que sua aplicação está executando com várias réplicas, o ***Service*** concentrará as requisições recebidas e as balanceará entre essa réplicas da aplicação de acordo com a porta de destino dessa requisição.

![Service Diagram](release/arquivados/imagens/service-diagram.svg)

O service também mapea portas de origem e de destino, por exemplo: requisições recebidas na porta 80 do serviço podem ser redirecionadas para porta 8080 da aplicação.

![Service Port Redirect](release/arquivados/imagens/service-port-redirect.svg)

Se refere ao número de pods (máquinas virtuais) que serão levantadas para atendimento do serviço.

Nos ambientes de desenvolvimento e homologação defina somente um pod (1 réplica) para atendimento, sendo que para o ambiente de produção é recomendado pelo menos 3 réplicas.

Depois de monitorar o consumo (tráfego/memória/CPU) em produção você pode redimensionar o número de réplicas de acordo com a necessidade.

## 2.2. HPA (Horizontal Pod Autoscaling)

Este recurso Se refere ao número de réplicas da aplicação que serão executadas para atender o serviço conforme a utilização de CPU e Memória.

## 5. Incluindo NodeSelector

Se refere a algum recurso que o Node do Kubernentes precisa ter para rodar a aplicação, como por exemplo:

```yaml
feature.node.k8s.bb/nas: true   # node que possui suporte a criação de volumes
acesso.node.k8s.bb/smtp: true   # node com suporte ao serviço de SMTP

```
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
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/release.md&internalidade=release/release)
