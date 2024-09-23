> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/referencias/release_aplicacao_nuvembb.md&amp;action_name=release/referencias/release_aplicacao_nuvembb)

# Release de aplicação da Nuvem BB

O processo de release de aplicativos da Nuvem BB é alinhado com o [terceiro princípio](https://12factor.net/pt_br/config) dos Twelve Factors, que propõe que os próprios desenvolvedores configurem o ambiente de execução da aplicação. Após, essa configuração deve ser armazenada e versionada. Para isso, é criado um repositório de código específico para cada ambiente de execução do Banco, identificados como DES (desenvolvimento), HML (homologação) e PRD (produção), além do repositório principal do código fonte. O Git é a ferramenta utilizada para controlar o versionamento.

A configuração do ambiente de execução da aplicação é essencialmente gerenciada por dois arquivos: o *requirements.yaml* e o *values.yaml*. O primeiro descreve as dependências necessárias, enquanto o segundo permite a parametrização dos valores definidos por essas dependências. No Banco, essas dependências são chamadas de *Charts*. Este roteiro apresenta detalhes da configuração dos recursos fornecidos pelo chart **BBAplic**. Esse chart e os recursos dele são fundamentais para a maioria das aplicações da Nuvem BB, permitindo a integração do serviço com o ambiente de execução de aplicativos do Banco.

## BBAplic

O [BB Aplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic) é o chart padrão para aplicações em contêiner desenvolvidas pelo Banco do Brasil. Por ser uma PaaS (plataforma como serviço), ele incorpora uma série de elementos essenciais do Kubernetes, relacionados no quadro abaixo. 

| Elemento | Definição | Usabilidade | Observação |
| -- | -- | -- | -- | 
| Service | É um recurso que unifica um conjunto de portas fornecidas pela aplicação, permitindo sua utilização em múltiplas instâncias como um único recurso. | Em uma aplicação, um Service pode agrupar as instâncias de um microserviço. Assim, independentemente de quantas instâncias estão em execução, os usuários sempre se conectam ao mesmo Service, que distribui as solicitações entre elas de forma balanceada.| Nos ambientes de desenvolvimento e homologação, defina somente um pod (1 réplica) para atendimento. <br> Para o ambiente de produção é recomendado pelo menos 3 réplicas. Após monitorar o consumo (tráfego/memória/CPU), você pode redimensionar o número de réplicas de acordo com a necessidade. |
| HPA (Horizontal Pod Autoscaling) | É um recurso que ajusta automaticamente o número de pods em execução com base na carga de trabalho atual do cluster, promovendo alta disponibilidade e resiliência das aplicações. | Em uma aplicação, quando o tráfego aumenta, o HPA pode adicionar mais pods automaticamente para lidar com a demanda extra. À medida que o tráfego diminui, o HPA escala para baixo, reduzindo o número de pods para economizar recursos. | Se sua aplicação for uma exceção, ao habilitar o HPA será necessário definir os números mínimo e máximo de réplicas, juntamente com os percentuais de memória e CPU que atuarão como gatilho para o autoescalonamento. |
| NodeSelector | É um recurso que atribui pods a nós específicos com base em labels associadas aos nós e aos pods. | A estrutura atual permite a alocação de pods em nós dentro dos clusters K8s apenas com a declaração **nodeSelector: {}**. Por padrão, deve vir vazio.| Não há uma lista pública das labels dos nodes. Em casos específicos de aplicações que requerem nodes específicos, os roteiros disponibilizados no repositório Git público detalham quais labels devem ser adicionadas para garantir a configuração adequada do ambiente. <br> Exemplo de configuração específica - suporte ao serviço de SMTP: **acesso.node.k8s.bb/smtp: true**. |
| Liveness Probe | É uma configuração que verifica se um contêiner está em execução e saudável. | Ao configurar o Liveness Probe em uma aplicação, o Kubernetes verifica periodicamente o endpoint **/health/live** para garantir que o contêiner esteja em execução e saudável. Se o contêiner não responder ou responder com erro, o Kubernetes reinicia o contêiner para restaurar sua saúde. | **initialDelaySeconds:** 120 <br> **httpGet-Path:** /health/live <br> **httpGet-Port:** 8080 ou conforme a porta que o seu contêiner docker expõe. <br> **periodSeconds:** 10 <br> **timeoutSeconds:** 5 <br> **failureThreshold:** 6 <br> **successThreshold:** 1 |
| Readiness Probe | É uma configuração que verifica se um contêiner está pronto para atender solicitações de rede. | Ao configurar o Readiness Probe em uma aplicação, o Kubernetes verifica periodicamente o endpoint **/health/ready** para garantir que a aplicação esteja pronta para atender, permitindo que o tráfego de rede seja direcionado para o contêiner somente quando estiver pronto. | **initialDelaySeconds:** 120 <br> **httpGet-Path:** /health/ready <br> **httpGet-Port:** 8080  ou de acordo com a porta que o seu contêiner docker expõe. <br> **periodSeconds:** 10 <br> **timeoutSeconds:** 5 <br> **failureThreshold:** 6 <br> **successThreshold:** 1 |
| Ingress | É um recurso que gerencia o acesso externo aos serviços em um cluster, normalmente HTTP. | Um Ingress possibilita o direcionamento do tráfego para diferentes ambientes, como produção e teste, dentro do cluster Kubernetes, utilizando regras de roteamento. | Caso o seu Service tenha a propriedade **name** declarada, você precisa configurar a seção **paths** do Ingress, fazendo referência ao nome do serviço e à porta correspondente. <br> Confira o [chart bb-aplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic) para visualizar os valores padrão dos parâmetros de configuração do objeto Ingress. <br> Confira o [chart dnsservice](https://charts.nuvem.bb.com.br/charts/bb-cloud/dnsservice) para visualizar os padrões de nomenclatura de DNS. |
| Curió | É um contêiner Docker capaz de prover, consumir, publicar e escutar operações do catálogo de operações IIB através de HTTP REST para sua aplicação. | Ao configurar o Curió, é possível definir em quais operações ele atua como Consumidor, utilizando dados de outras operações, e em quais ele atua como Provedor, fornecendo dados para outras operações do sistema. | Confira o [README do Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio/-/blob/master/README.md) para informações sobre as configurações. |
| Service Monitor | É a maneira pela qual seu microsserviço se comunica com a stack de Monitoração associada à sua sigla. | Ao configurar o monitoramento dentro de um namespace específico, o Service Monitor acompanha e registra métricas relacionadas ao desempenho e disponibilidade dos recursos dentro do namespace. | Confira o [arquivo YAML](https://fontes.intranet.bb.com.br/sgs/publico/plataforma-monitoracao/-/blob/master/roteiros/AppD_Instrumentacao_aplicacoes_rancher.md#valuesyaml) para visualizar as labels esperadas pelo Prometheus nas configurações de Service Monitor no chart do bb-aplic da aplicação. | 

**Tags:** #bbaplic #charts #dns

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil?

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/referencias/release_aplicação_nuvembb.md&internalidade=release/referencias/release_aplicação_nuvembb)