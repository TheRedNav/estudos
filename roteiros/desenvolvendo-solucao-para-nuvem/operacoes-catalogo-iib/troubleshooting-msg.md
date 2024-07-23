> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/troubleshooting-msg.md&amp;action_name=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/troubleshooting-msg.md)

# Execução da imagem e comandos
> :exclamation: Pré-requisitos: Docker

 
1. Executar a imagem docker com o comando a seguir:

 
    `docker run -it --rm redis redis-cli -u redis://iib-slave.redis.bdh.desenv.bb.com.br`

 
2. No cli da imagem, executar o comando keys `msg:mensagem:<numero-da-mensagem>:*` Exemplo:

 
   `keys msg:mensagem:5168825:*`
 

   Deverá retornar:

 
   ```
   1) "msg:mensagem:5168825:00000000:01"
   ```

   Caso retorne `(empty array)` a mensagem não foi cadastrada e/ou publicada no cache. Neste caso, deverá tentar republicar no cache via plataforma (Plataforma         -> Tecnologia ->  Construção -> Mensagens -> Detalhar Mensagem -> Ações -> Publicar Cache.) Se ainda assim não recuperar a mensagem no cache, deverá       ser solicitado suporte ao MSG.
 

3. Para verificar o vinculo de Operação e código de erro utilize o comando `get msg:vinculo:operacao_codigo_erro:<operacao>:<versao>:<codigo_erro>`. Exemplo:
 
    `get msg:vinculo:operacao_codigo_erro:4822743:1:001`
 
    Deverá retornar o código da mensagem correspondente:

 
    ```
    "5168825"
 
    ```

Caso retorne `(nil)` verificar o vínculo de Operação e código de erro no Catálogo de mensagens, disponível na Plataforma BB > Tecnologia > Construção > Catálogo > Mensagens

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/troubleshooting-msg.md&internalidade=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/troubleshooting-msg)
