> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/referencias/alocar_recursos_microsservicos.md&amp;action_name=kubernetes/referencias/alocar_recursos_microsservicos)

# Alocação de recursos de CPU e Memória para os Microsserviços

Este roteiro apresenta conceitos fundamentais e traz um caso de uso sobre como alocar recursos de CPU e memória para microsserviços de forma eficiente. O objetivo é ajudar o desenvolvedor a configurar corretamente esses recursos, controlando custos sem comprometer o desempenho das aplicações.

## Conceitos fundamentais 

|Conceito|Definição|Observação|
|--|--|--|
|**Requests**| Configuração do YAML, determina a quantidade mínima de recursos (CPU e/ou memória) que deve ser alocada para cada contêiner. Sem ela, o Pod não será inicializado.| É crucial definir corretamente os requests para evitar subalocação ou superalocação de recursos, garantindo que o pod sempre tenha o mínimo necessário para funcionar.|
|**Limits**| Configuração do YAML, determina a quantidade máxima de recursos (CPU e/ou memória) que cada contêiner pode utilizar. No entanto, não é garantido que o contêiner conseguirá essa quantidade de recursos.| Para CPU, o ideal é que o **limits seja pelo menos 6 vezes maior que o request**, pois há um pico de processamento durante a inicialização do pod.|
|**Recursos alocáveis**| A quantidade de recursos disponíveis para serem alocados aos contêineres, após subtrair os recursos usados pelo sistema e outros serviços.| O conhecimento exato dos recursos alocáveis é vital para evitar a sobrecarga do nó e garantir a eficiência na alocação de recursos. |
|**Recursos compressíveis**| Recursos que podem ser ajustados ou limitados sem causar uma falha imediata no funcionamento do contêiner ou aplicação. O desempenho pode ser degradado, mas o sistema continua funcionando.| A CPU é o exemplo mais comum de um recurso compressível. |
|**Recursos incompressíveis**| Recursos que não podem ser limitados ou ajustados sem causar falhas imediatas ou problemas críticos no funcionamento do contêiner ou aplicação. |A memória é o exemplo mais comum de um recurso incompressível. A configuração correta da memória é essencial para evitar OOM (Out Of Memory), que pode causar falhas críticas na aplicação.|
|**Throttling**| Redução da capacidade de CPU de um pod para não exceder os limites configurados. É utilizado para garantir que os recursos sejam distribuídos de forma justa entre os pods, evitando que um único pod monopolize a CPU.| Por exemplo, se um nó tem 4 CPUs virtuais (vCPUs) e cada pod está configurado para usar no máximo 1 vCPU, o Kubernetes ajustará dinamicamente o uso de CPU para garantir que cada pod não ultrapasse sua alocação, distribuindo eficientemente os recursos disponíveis.|
|**Eviction**| Remoção de um pod do nó devido a falta de recursos críticos, como memória (mais comum para recursos incompressíveis). A política de eviction deve ser bem compreendida para evitar a interrupção de serviços críticos. | Por exemplo, um nó tem 16 GB de memória e 7 pods, com cada pod limitado a 2 GB. Se um novo pod precisar de 4 GB, não haverá memória suficiente disponível. Nesse cenário, o Kubernetes removerá um pod existente para liberar espaço.|
|**Cgroups (Control Groups)**| Mecanismo do kernel Linux que permite a limitação, contabilização e isolamento de recursos de um grupo de processos. Os grupos são usados para controlar e monitorar recursos como CPU e memória.| - |
|**CPU Shares**| As shares definem prioridades relativas de CPU entre diferentes Cgroups. Grupos com mais shares têm acesso proporcionalmente maior à CPU quando houver competição por recursos.| Imagine dois Cgroups, A e B. Se A tiver 200 CPU Shares e B tiver 100 CPU Shares, o CFS (scheduler) tentará alocar o dobro do tempo de CPU para A em relação a B, quando ambos estiverem competindo por recursos.|
|**CFS (Completely Fair Scheduler)**|O CFS garante um escalonamento justo de tempo de CPU entre os processos, levando em consideração as prioridades definidas pelas CPU Shares atribuídas a cada Cgroup. Processos dentro de Cgroups com mais shares terão acesso proporcionalmente maior à CPU.| A compreensão do funcionamento do CFS é importante para otimizar a performance dos pods, especialmente em ambientes com alta concorrência por recursos.|

### Alocação vs. Consumo Real

A alocação incorreta de recursos pode impedir o agendamento de pods ou levar a desperdícios. Quando os requests são muito altos em comparação com o consumo real, os recursos acabam ficando reservados e são subutilizados, enquanto outros pods que precisam desses recursos ficam impedidos de serem agendados.

* **Impactos pro Banco:**
  * custos desnecessários;
  * desperdício de recursos;
  * superdimensionamento;
  * gerenciamento complicado;
  * dificuldade em identificar e resolver problemas de desempenho.
  <br>
* **Impactos pro cliente:** 
  * desempenho inconsistente - latência e disponibilidade;
  * qualidade do serviço;
  * experiência do usuário.
  
### Calculando recursos para um contêiner

Para estimar a necessidade real do contêiner, muitos fatores devem ser considerados. Podemos destacar:
* **Recursos do Sistema Operacional:** o sistema operacional no nó consome CPU e memória;
* **Componentes de Gerenciamento do Kubernetes:** Kubelet, runtime do contêiner (Docker, containerd) e agentes de rede também consomem recursos;
* **Overhead do Pod:** inclui sobrecarga de gerenciamento e execução de processos auxiliares;
* **Escalabilidade e picos de demanda**;
* **Réplicas de contêineres para suportar falhas de nó**;
* **Estratégias de recuperação automática**;
* **Atualizações de software**.
   
## Caso de uso

Você está implantando um serviço em um cluster Kubernetes e precisa configurar os recursos de CPU e memória para garantir que seu contêiner A tenha uma alocação adequada, respeitando os recursos disponíveis para todos os componentes.

Primeiro, você precisa descobrir os limites (**quota Hard**) definidos para recursos dentro do namespace, como CPU e memória. Para isso, utilize o comando `kubectl describe namespace NOME_NAMESPACE`. Este comando pode ser sempre utilizado para consultar e comparar os recursos usados (**quota Used**) e a capacidade máxima do namespace.

Após descobrir o total permitido, você irá estimar o quanto de recursos será consumido por componentes como o kubelet, overhead do pod, etc. para descobrir o quanto de recursos você tem de fato, disponíveis para alocar.

### Diagrama da hierarquia
```
Cluster (Total: 120 CPUs, 200 GiB de Memória)
├── Namespace A
│   ├── Pod A
│   │   ├── Contêiner A
├── Nó 1 (24 CPUs, 40 GiB de Memória)
│   ├── Pod A (Namespace A)
│   │   └── Contêiner A
│   └── Outros componentes do nó: kubelet, kube-proxy, etc. (2 CPUs, 4 GiB de Memória)
├── Nó 2 (24 CPUs, 40 GiB de Memória)
├── Nó 3 (24 CPUs, 40 GiB de Memória)
├── Nó 4 (24 CPUs, 40 GiB de Memória)
└── Nó 5 (24 CPUs, 40 GiB de Memória)
```

Baseado nos valores colocados acima, você subtraiu os valores de consumo das funções básicas do contêiner do valor de capacidade total do nó e chegou a conclusão que ainda possui 22 CPUs e 36 GiB disponíveis de recursos para alocar no contêiner A. 

> :warning: **Atenção** 
> 
> Ter recursos disponíveis não significa que você deve utilizar a capacidade máxima permitida. Ao configurar esses valores, use o bom senso para assegurar eficiência e disponibilidade. Um número excessivo de requests pode resultar em subutilização, enquanto um número insuficiente pode afetar o desempenho. Monitore o consumo real e ajuste conforme necessário.

### Exemplo de configuração do YAML

```yaml
      resources:
        requests:
          cpu: "4000m"    ## Solicitação de 4 CPUs (incluindo 2 para recursos básicos e 2 alocáveis adicionais), garante que não ocorrerá eviction
          memory: "12000Mi"  # 12 GiB = 4 GiB (recursos básicos) + 8 GiB (alocáveis adicionais), garante que não ocorrerá eviction
        limits:
          cpu: "24000m"   # Limite de 24 CPUs (6 vezes maior que o request de 4 CPUs), essa sugestão evita a ocorrência de throttling
          memory: "24000Mi" 
```

> :information_source: **Observação** 
> 
> Note que, mesmo tendo a sua disposição 40 GiB de memória, o arquivo não foi configurado para consumir o recurso na totalidade.
> 
#### Cálculo do CFS

No exemplo acima, configuramos o request de CPU para 4000 milicores (4 CPUs) e o limit para 24000 milicores (24 CPUs). O CFS, que distribui o tempo de CPU com base em shares, calcula esses valores assumindo 1024 shares por CPU. Assim, 6 CPUs correspondem a 4096 shares. 

Se houver concorrência por CPU entre múltiplos pods do nosso exemplo, o CFS distribuirá o tempo de CPU proporcionalmente a esses shares. Se tivermos um pod B, com 3072 shares, o CFS alocará 2/3 do tempo de CPU para o Pod A e 1/3 para o Pod B.

**Tags:** #kubernetes #recursos #microsservicos

## A Seguir
* Leia o roteiro [Configuração de Recursos em Microserviços](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/ICE/rtc) para aprender a ajustar a Quota do Namespace e de Limit Range.
* Acesse a wiki [Conceitos de Gestão de Capacidade e Eficiência](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/ICE/main) para conhecer mais sobre os conceitos relacionados à Gestão de Capacidade e Eficiência na BB Cloud.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/referencias/alocar_recursos_microsservicos.md&internalidade=kubernetes/referencias/alocar_recursos_microsservicos)
