> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/troubleshooting/troubleshooting-rede.md&amp;action_name=troubleshooting/troubleshooting-rede.md)

# Investigação de problemas de rede

## Aplicação
Esse roteiro se aplica a aplicações construídas nas stacks suportadas por este time dev, no que se refere a comunicação dessas aplicações com serviços externos, quando enfrenta problemas.
Todas as mensagens e termos deste roteiro são conceitos globalmente conhecidos, especialmente refinados para as aplicações da DevCloud, e possuem larga documentação disponível na Internet.

## Ferramentas

### curl 
Nas imagens baseadas em UBI9, como a `dev-java` e a `dev-nodejs` em suas últimas versões, o aplicativo `curl` está disponível no terminal.

Assim, é possível testar conectividade com endereços remotos utilizando Shell via [kubectl](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Install_kubectl.md), respeitando as permissões definidas nos [Acessos do Desenvolvedor](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/PaaS/Acessos-do-desenvolvedor):

Exemplo:

```
➜  kubectl -n dev-brave-api get pod
NAME                                                    READY   STATUS    RESTARTS   AGE
des-dev-brave-api-chart-name-regular-54d854cc7b-cmf65   1/1     Running   0          37d

➜  kubectl -n dev-brave-api exec --stdin --tty des-dev-brave-api-chart-name-regular-54d854cc7b-cmf65 -- sh

sh-4.4$ curl -vv telnet://google.com:443
* Rebuilt URL to: telnet://google.com:443/
*   Trying 142.251.132.14...
* TCP_NODELAY set
* Connected to google.com (142.251.132.14) port 443 (#0)
^C  
sh-4.4$ exit
exit
command terminated with exit code 130

```


## Problemas

### `No such host` / `ERR_NAME_NOT_RESOLVED` / `unknown host` e similares
Esse erro acontece quando o conteiner não consegue resolver o DNS. Isso significa não obter a informação de qual IP está associado àquele nome para fazer a requiisção e pode acontecer por erro na hora da inserção da URL na aplicação pelo desenvolvedor, ou por problema de infraestrutura que cause reflexo na rede do conteiner.

**Resposta do desenvolvedor:** Caso a funcionalidade esteja sendo implantada, confira a URL, seja verificando o valor do parâmetro passado à aplicação, seja inserindo logs na aplicação para verificar se o parâmetro está realmente sendo usada. Caso as informações estejam corretas, verifique em outra máquina ou conteiner se o serviço remoto está realmente no ar. Caso constate que o serviço está no ar e a aplicação não consiga resolver o DNS, abra uma issue na sigla dev, para que o time de suporte redirecione para tratamento da infraestrutura.

### `Connection refused`
"Connection refused" significa que aquele nome foi resolvido para um IP, o IP foi encontrado e recusou a conexão naquela porta. Exemplos de erros cometidos pelo desenvolvedor são, por exemplo, fazer uma requisição para um outro microsserviço usando a porta do conteiner(8080, por exemplo), em vez da porta exposta pelo *service* daquele microsserviço.

**Resposta do desenvolvedor:** Caso a funcionalidade esteja sendo implantada, confira a URL, seja verificando o valor do parâmetro passado à aplicação, seja inserindo logs na aplicação para verificar se o parâmetro está realmente sendo usada. Caso as informações estejam corretas, verifique em outra máquina ou conteiner se o serviço remoto está realmente no ar. 

### `PKIX path building failed` / `unable to find valid certification path to requested target` e similares
Esse erro ocorre quando a máquina cliente não consegue validar a autenticidade do servidor remoto numa conexão SSL. A responsabilidade de validar a cadeia de certificados é da aplicação chamadora.

**Resposta do desenvolvedor:** Ver o roteiro de troubleshooting de certificados.

### `Connection timed out` e similares
A aplicação fez uma requisição para um IP e ninguém respondeu. Isso pode acontecer por o servidor remoto não estar no ar(menos comum quando a resolução de DNS ocorre com sucesso no mesmo momento) ou por restrições de firewall, que normalmente respondem com timeout requisições para URLs não autorizadas.

**Resposta do desenvolvedor:** Verifique em outra máquina fora do cluster (a sua máquina local por exemplo), se o servidor remoto está mesmo no ar. Caso o serviço esteja no ar e seja externo ao BB, verifique se seguiu todos os passos para conexão com URL externa. É necessário abrir uma RLB, ligar a flag `acessoProxy` no chart `bb-aplic` e se conectar à URL utilizando o proxy corporativo. 

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/troubleshooting/troubleshooting-rede.md&internalidade=troubleshooting/troubleshooting-rede)
