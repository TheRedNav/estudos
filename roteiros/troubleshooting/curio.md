> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/troubleshooting/curio.md&amp;action_name=troubleshooting/curio.md)
# Troubleshooting Curió
 
## 1. Erro de configuração do container (docker)
### Descrição
O [Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio) necessita que algumas variáveis de ambiente estejam devidamente configuradas para que o container fique saudável.

### Sintomas
#### 1. Pod do Curió em `CrashLoopBackOff` 
No [ArgoCD](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/VisualizandoLogs.md#consulta-pelo-argocd), o pod do curió pode estar com `status` igual a  `CrashLoopBackOff`. Isso indica que mesmo reinicializando o serviço, não é possível obter um container saudável.

#### 2. Mensagem de erro do Curió indica problema com a definição das variáveis
Ao selecionar o pod da aplicação, e verificar a aba `log`, deve-se escolher o item referente ao Curió no menu lateral.
Logo abaixo do desenho em ASCII art do Curió, você verá as informações sobre a incialização do Curió, incluindo os valores das variáveis. Pode acontecer que alguma das variáveis não esteja inicializada, ou possua um valor incorreto.

Exemplo de mensagem de erro de variável não definida:
```java
2022-10-19 17:07:22,210 INFO :br.com.bb.iib.curio.config.base.CurioConfig - CURIO_OP_PROVEDOR:
br.com.bb.iib.curio.config.base.CurioConfigException: Erro ao processar operação '' - Formato esperado <groupId>:<artifactId>:<version> (valor
es disponíveis no CTL no item 'Dependência Maven')
        at br.com.bb.iib.curio.config.base.CurioConfigOperacoes.parseOperacaoInfo(CurioConfigOperacoes.java:54)
        at br.com.bb.iib.curio.config.base.CurioConfigOperacoes.loadConfig(CurioConfigOperacoes.java:31)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
```

### Solução

Uma vez identificada a variável que está com problemas, você deverá corrigir o arquivo `values.yml` do respectivo ambiente onde se apresenta o erro.

Na [página inicial da documentação do Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio) consta uma listagem de todas as variáveis de ambiente que o Curió espera e uma breve descrição dos possíveis valores, incluindo máscaras e formatos quando necessário.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/troubleshooting/curio.md&internalidade=troubleshooting/curio)