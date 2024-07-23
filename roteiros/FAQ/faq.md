> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

[[_TOC_]]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/faqs/faq.md&amp;action_name=faqs/faq)


ArgoCD
===
## Por quê minha aplicação não foi deployada no argoCD?

Primeiro, verifique se o *alias* no **requirements.yaml** corresponde ao que está informado no arquivo **values.yaml**. Depois, verifique no arquivo **requirements.yaml** se o repositório do bbaplic está correto:
```text
 repository: https://chartmuseum.devops.nuvem.bb.com.br/bb/catalogo 
```

Se estiver trabalhando no ambiente de desenvolvimento, verifique se a pipeline de Continuous Delivery no Jenkins foi concluída com sucesso. Consulte o [Roteiro de Pipeline-CD](https://fontes.intranet.bb.com.br/psc/publico/atendimento/blob/master/roteiros/pipeline-cd.md) para mais informações.

[Chart default de aplicações BB - bb-aplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic) 

Banco de dados
===

## Como testar a minha url de conexao com a base de dados?
- [Issue #972 - Timeout para acessar o mongodb do silo de des](https://fontes.intranet.bb.com.br/psc/publico/atendimento/issues/972)

Curió / IIB 
===
## Onde encontro a documentação oficial do Curió? 
A [wiki](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/-/wikis/home) do Curió contém as informações que você precisa, como a versão mais recente e o link para o README dessa versão, onde os parâmetros necessários são descritos.

## Como implementar o provimento de uma operação utilizando o Curió? 
* [Roteiros Operações IIB](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master#ledger-opera%C3%A7%C3%B5es-iib)

## Como crio a operação no catálogo? 
* [Wiki da Administração de Serviços (AS)](https://fontes.intranet.bb.com.br/ctl/publico/atendimento)

## Devo me preocupar com os erros DATALOSS.OUTBOUND.BCAST e DATALOSS.INBOUND.BCAST dados pelo Curió? 

Ao fornecer operações através do Rendevouz, o Curió pode ocasionalmente apresentar os seguintes erros:

**DATALOSS.OUTBOUND.BCAST**: indica que outro servidor de Rendevouz perdeu um pacote e está solicitando a retransmissão por parte de alguém que possua esse pacote. Nesse caso, o problema não está no servidor de Rendevouz ao qual o Curió está conectado. Trata-se apenas de um alerta indicando que outro servidor está enfrentando problemas.

**DATALOSS.INBOUND.BCAST**: indica que o servidor de Rendevouz ao qual o Curió está conectado está perdendo pacotes. O próprio Rendevouz possui um mecanismo para solicitar a retransmissão dessa mensagem a algum outro servidor do barramento. O Rendevouz se responsabiliza por reenviar esse pacote ou algum outro worker ou scheduler irá atender à requisição. Caso nenhum host do barramento retransmita o pacote por algum motivo, ocorrerá um timeout, o que seria um problema para a aplicação. Entretanto, esses timeouts são raros.  

Durante o desenvolvimento, esses erros ocorrem com certa frequência devido à instabilidade do ambiente em comparação com os ambientes de homologação e produção. Em geral, os desenvolvedores não precisam se preocupar com essas mensagens, a menos que percebam que a aplicação está sendo afetada. A equipe de integração mantém logs de todos os casos e realiza o acompanhamento. Se ocorrerem timeouts (o pior cenário), eles serão alertados e buscarão uma solução.

DNS
===

## [Chart do DNS Ingress](https://charts.nuvem.bb.com.br/charts/bb-cloud/dnsingress)

## Como configuro a porta e o DNS do meu serviço?

Para expor a porta do seu serviço com o projeto já criado a partir da [**Oferta K8s**](https://pgi3.intranet.bb.com.br/autobots/14-oferta-k8s), é necessário modificar o arquivo values.yaml. Descomente as linhas abaixo e ajuste os valores conforme indicado.

```yaml
 service:
    enable: true
    name: nome-do-servico
    type: ClusterIP
    ports: 
      - name: http
        port: 80
        targetPort: 1234
```

No caso presente, o valor de **targetPort** representa a porta que deseja expor com o seu serviço, enquanto **port** é a porta que será efetivamente exposta (geralmente não modificada).

Agora, para configurar o DNS, é necessário incluir os seguintes parâmetros no início do arquivo **values.yaml**:

```yaml
dnsingress:
  urls:
  - hostname: teste1.psc.desenv.bb.com.br
    ingressClass: nginx
  ...
```
Os **nome-do-servico** e **sigla** devem ser substituídos de acordo com o serviço criado. 

Também é necessário adicionar as seguintes linhas ao arquivo **requirements.yaml**. Essas linhas adicionam o chart de DNS criado pela infraestrutura como uma dependência.

```yaml
dependencies:
  - name: dnsingress
    version: 2.0.0
    repository: https://charts-repo.nuvem.bb.com.br/pre-prd/bb/catalogo
```

## Como checar se meu DNS foi criado?
* [Roteiro de referência para a verificação da aplicação/serviços](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/troubleshooting-ingress)
* [Troubleshooting do DNSIngress](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/troubleshooing-dnsingress)

## Timeout - Como trocar o Ingress pelo DNS interno(caminho mais curto para requisições internas)
- [Issue #1269 - trocando Ingress pelo DNS interno](<https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/1269>)
- [Issue #16317 - Job: hook PreStop e PostStart apresentando falha](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/16317#note_1767443)

## Como habilitar DNS Externo
* [Roteiro_solicitacao_DNS_INTERNET](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/Roteiro_solicitacao_DNS_INTERNET#informa%C3%A7%C3%B5es-para-o-campo-url)

## Como solicitar certificados
* [Roteiro Solicitação de Certificados](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/solicitacao-de-certificados)

## Como ter um dnsservice externo, personalizado
- [Issue #861 - Como ter DNS Externo personalizado](<https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/861>)
- [Issue #227 - Como ter DNS Externo personalizado](<https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/227>)

## Como excluir meu dnsservice?
- [Issue #959 - Dnsservice não promoveu os domínios](<https://fontes.intranet.bb.com.br/psc/publico/atendimento/issues/959>)
- [Issue #582 - Dnsservice não exlcui, fazer sync prune](<https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/582>)

## erro DNSConfigForming, NetworkNotReady
- [Issue 1357 - Diversos projetos com erro DNSConfigForming, NetworkNotReady](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/1357#note_74882)
Kubernetes/Deployment 
===
## Como funciona o roteamento das requisições quando o modo canário está ativado? 
- [Issue #2003 - Estratégia de roteamento utilizando Deployment Canário](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues/2003)

## O que devo fazer para solicitar restart de pod em produção?

Para intervenções no ambiente de produção, restartar ou remover recursos do k8s, como pods, pvcs, replicasets, dnsingress e outros, a solicitação deve ser feita via [Plataforma ITSM](https://bb.service-now.com/now/sow/home). Assista ao tutorial sobre [como abrir um registro de incidente](https://banco365.sharepoint.com/:v:/r/sites/PlataformaITSMnaNuvem/Videos_Treinamento_ServiceNow/Gerenciamento%20de%20Incidentes/Gerenciamento%20de%20Incidentes%20-%20Como%20Criar%20um%20Incidente.webm?csf=1&web=1&e=ovZrK4).<br>
 Em caso de dúvidas, entre em contato com o time responsável através da comunidade [Plataforma ITSM na Nuvem](https://engage.cloud.microsoft/main/groups/eyJfdHlwZSI6Ikdyb3VwIiwiaWQiOiIxMzMyODYwMTkwNzIifQ/all) ou solicite acesso ao canal [Implantação Servicenow - ITSM](https://teams.microsoft.com/l/team/19%3AKDNYQ2YyaDUolnDtQ8uPTaSzqf2eHln_uN0Nyyu9NKg1%40thread.tacv2/conversations?groupId=bda4e178-ad76-46e2-9c9d-ab30dac98ee2&tenantId=ea0c2907-38d2-4181-8750-b0b190b60443).

> :grey_exclamation: **Importante** 
> 
> * Os registros automatizados, provenientes das integrações, continuarão sendo aceitos pelo GSTI até que a migração de todas as ferramentas que abrem esses registros seja concluída. 
> * As ferramentas AppDynamics, vROPs e OMi já estão integradas à Plataforma.

Docker
===

## Como configurar docker para acesso com vpn
* [Roteiro DockerVPN](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/docker/DockerVPN.md)    

Logs
===

## Qual o atual entendimento para quem quer armazenar logs?
* [Issue 6603 - Armazenamento de LOGS - Kubernetes - Arq3](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/6603)

## Posso utilizar o chart guarda logs? 
O chart guarda-log foi descontinuado porque era experimental.
  
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/faqs/faq.md&internalidade=faqs/faq)
