> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Criando Logs
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/CriandoLogs.md&amp;action_name=monitoracao/CriandoLogs)

Este roteiro explica qual é o padrão de logs exigidos no desenvolvimento de projetos na arquitetura 3.0 do Banco do Brasil e explica o funcionamento destes nas aplicações geradas utilizando os templates padrões do BBDev.

## Formato e Níveis de Log

Recomendamos que o JSON possua os seguintes campos:

**REQUEST_ID**: Identificador da requisição. Recomenda-se utilizar um identificador no formato [UUID](https://tools.ietf.org/html/rfc4122). As principais linguagens fornecem bibliotecas para gerar identificadores utilizando este padrão. 

**MESSAGE**: Mensagem propriamente dita.

**DETAILS**: Detalhes extras referentes a mensagem. Exemplos de alguns detalhes úteis para uma requisição:
  * path
  * method

Ao se logar o body da requisição o usuário deve tomar cuidado para não logar dados sensíveis de clientes e se atentar para que este log seja em nível *Info* ou *Debug*.

Recomenda-se utilizar os níveis de log indicados abaixo:

* **Debug**: informações de apoio ao desenvolvedor com detalhamentos necessários para depurar problemas.

* **Info**: informações de negócio e fluxos.

* **Error** : indica uma excessão (tratada ou não tratada).

Note que ao executar a aplicação definindo um nível de log, todos os logs dos níveis abaixo do definido também serão impressos. Por exemplo: caso sua aplicação esteja com nível *Error* definido, apenas logs do nível *Error* serão impressos, porém, caso o nível do log esteja definido como *Debug*, serão impressos logs com nível *Debug*, *Info* e *Error*.

> Além dos níveis definidos acima as linguagens permitem outros níveis como *Trace*, *Warn* e *Fatal*. A limitação aos três níveis acima visa facilitar o uso.

Para mudar o nível do log é necessário alterar o values da aplicação.

## Log no NodeJs (Typescript)

O projeto blank project para NodeJs utiliza o [Pino](https://github.com/pinojs/pino) como biblioteca para logs.

Caso seu projeto não tenha sido criado pelo BBDev, você pode adicionar nossa biblioteca ao seu projeto.

```sh
npm install dev-dx-typescript-libs
```

As rotas que estendem a classe ApiRouter dispõe podem utilizar o logger dessa forma:

```javascript
  import { ApiRouter } from "dev-dx-typescript-libs/routes";
  ...
  ...
  ...
  export class MinhaRota extends ApiRouter
  ...
  ...
  ...
    this.logger.debug("Variável inicializada com valor 35");
    this.logger.info("Configurando Rotas");
    this.logger.error("Ocorreu erro ao executar o método");
  ...
```
Nos demais módulos, o logger pode ser adicionado com o import abaixo:

```javascript
import { logger } from "dev-dx-typescript-libs/logger";
```
O nível de log padrão é o INFO. Para modificar o nível de log, configure a variável de ambiente LOG_LEVEL

## Sugestões, Dúvidas, Elogios

Gostou deste roteiro? Caso deseje deixar alguma sugestão, postar uma dúvida ou fazer um elogio utilize [a issue deste link](https://fontes.intranet.bb.com.br/dev/publico/roteiros/issues/xx). Você pode também apenas deixar seu like/dislake para termos noção de quantas pessoas leram este tutorial para nos ajudar a decidir quais serão os próximos tutoriais.---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/CriandoLogs.md&internalidade=monitoracao/CriandoLogs)
