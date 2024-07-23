> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Erros
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/errosTypescript.md&amp;action_name=erros/errosTypescript)

O projeto blank _Typescript_ gerado utilizando o BBDEV com o gerador **dx-typescript** à partir da versão 0.0.3 já contém um exemplo de geração de mensagem de erros e um endpoint com todos os erros da aplicação.

Ao executar o projeto usando **npm run start** ou **npm run docker** os erros podem ser acessadas no endpoint _/errors_. 

## Implementação

Caso tenha gerado seu projeto com uma versão anterior ou não possua a funcionalidade de listagem dos erros, uma sugestão é a criação dos seguintes itens:
- Classe de erro com o padrão definido pela Arq3 conforme descrito nesse [link](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/endpoints/errors-endpoint.md)
- Handler para tratar as exceções e retornar o erro e o status code no padrão Arq3 conforme descrito nesse [link](https://fontes.intranet.bb.com.br/dev/publico/padroes/blob/master/http-return-codes.md).

## Funcionamento

### Adicionar novo tipo de erro
* Adicionar string com o tipo do erro em na _enum ErrorTypes_ dentro do arquivo [erro.ts](#errots).
* Criar arquivo **erro.novo_tipo.ts** na pasta **errors**, criando uma classe que extenda de [_ErroPadrao_](#errots).
* Dentro do construtor da classe criada, adicione `this.name = ErrorTypes.NOVO_TIPO_ERRO;` para configurar o nome.
* Adicione uma lista para manter padrões para o erro `export const listaErroNovo = [];`.
* No arquivo [error.api.ts](#errorapits), adicione a `listaErroNovo` à listagem de erros que serão mostrados no endpoint _/errors_,
alterando a lista em `this.listErrors = (this.listErrors.concat(listErroNegocio, listErroSQL))`.
* Adicione o novo tipo de erro ao handler de erros, em [error.handler.ts](errorhandlerts), da seguinte forma
```typescript 
  switch (err.name) {
    case ErrorTypes.TIPO_ERRO_NEGOCIAL:
    case ErrorTypes.TIPO_ERRO_NEGOCIAL_MSG:
    case ErrorTypes.TIPO_ERRO_SQL:
    // Adicione o tipo criado aqui
```

### Adicionar padrão de erro
* Para poder reutilizar a chamada do erro, adicione um padrão como definido em 
```typescript
export const ERRO_NEGOCIAL_CPF_INVALIDO: [string, string, string] = ["002", "CPF {0} não é válido",
  "Insira um CPF válido no formato 999.999.999-99"];
``` 
é obrigatório informar pelo menos 2 strings, para os campos
código e mensagem.
* Adicione o padrão de erro a lista de erros que será enviada ao endpoint /_errors_.
* É possível utilizar campos formatáveis, utilizando padrão posicional, como no exemplo `"CPF {0} não é válido"`, a 
string `{0}` recebe o primeiro valor passado a `formatMessage()`.
* Também é possível passar um array de chave/valor no campo variaveisMonitoradas. 

### Adicionar padrão de erro MSG
* Este padrão permite informar `variaveisMonitoradas` que poderão ser traduzidas em mensagens dinâmicas no Catálogo de Mensageria (MSG).
* Para poder reutilizar a chamada do erro, adicione um padrão como definido em 
```typescript
export const ERRO_NEGOCIAL_CPF_INVALIDO_MSG: [string, string, string, string, string, Map<string, string>] = ["002", "CPF {0} não é válido",
  "Insira um CPF válido no formato 999.999.999-99", "Entrada inválida no campo x", "", new Map<string, string>()
  .set("cpf", "{0}")];
```

* Adicione o padrão de erro a lista de erros que será enviada ao endpoint /_errors_.
* É possível utilizar valores formatáveis da lista de Chave/Valor, utilizando padrão posicional, como no exemplo `"cpf", "{0}"`, a 
string `{0}` recebe o primeiro valor passado a `formatVariaveisMonitoradas()`. 
* A mensagem neste caso pouco importa, pois será utilizada a mensagem cadastrada no Catálogo de Mensagens.

### Utilizar padrão de erro
* Lance o erro instanciando uma das classes de erro, informando um dos padrões criados para a classe e formatando os campos necessários, 
```typescript
throw new ErroNovo(...ERRO_NOVO_PADRAO_CRIADO).formatMessage(mensagemFormatada);
```

### Utilizar padrão de erro MSG
* Lance o erro instanciando uma das classes de erro, informando um dos padrões criados para a classe e formatando os campos necessários, 
```typescript
 throw new ErroNovo(...ERRO_NEGOCIAL_CPF_INVALIDO_MSG).formatVariaveisMonitoradas(valorFormatado).formatMessage(mensagemFormatada);
```

### Arquivos

#### erro.ts
Possui a classe padrão de erro que deve ser extendida, e a _enum_ com os tipos de erro.
```typescript 
/**
 * Classe para representar a estrutura da mensagem de erro conforme descrito no padrao de erros, aonde:
 *
 * Obrigatorios
 * code               : Contém o código do erro. Deve ser numérico e conter no máximo 3 dígitos
 * source             : Nome do módulo ou da classe ou, quando operação IIB, número sequencial do erro
 * message            : Mensagem de erro para o usuário
 *
 * Opcionais
 * userHelp           : Orientação sobre como o usuário pode resolver o problema
 * developerMessage   : Mensagem técnica para o desenvolvedor
 * moreInfo           : Complemento do erro para o desenvolvedor
 *
 * https://fontes.int  public async incluirCliente(req: any): Promise<ICliente> {
    const { name, cpf } = req;
    const cliente = new ClienteModel();
    if (this.irrf.validarCPF(cpf)) {
      return cliente.create({
        name, cpf,
      });
    }
    throw new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO).formatMessage(cpf);
  }anet.bb.com.br/dev/publico/padroes/blob/master/endpoints/errors-endpoint.md
 */

export class ErroPadrao extends Error {
  code: string;

  source: string;

  userHelp?: string;

  developerMessage?: string;

  moreInfo?: string;  public async incluirCliente(req: any): Promise<ICliente> {
    const { name, cpf } = req;
    const cliente = new ClienteModel();
    if (this.irrf.validarCPF(cpf)) {
      return cliente.create({
        name, cpf,
      });
    }
    throw new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO).formatMessage(cpf);
  }
  readonly statusCode: number;

  constructor(code: string, message: string, statusCode: number, userHelp?: string,
    developerMessage?: string, moreInfo?: string, variaveisMonitoradas?: Map<string, string>) {
    super(message);
    this.code = code;
    this.source = (this.stack as string).split("\n")[1].replace("at", "").trim();
    this.userHelp = userHelp || "";
    this.developerMessage = developerMessage || "";
    this.moreInfo = moreInfo || "";
    this.statusCode = statusCode;
    this.variaveisMonitoradas = variaveisMonitoradas;
  }

  public toJSON(): {
    code: string,
    source: string,
    message: string,
    userHelp?: string,
    developerMessage?: string,
    moreInfo?: string,
    variaveisMonitoradas?: {},
    } {
    const erroJSON: {
      code: string,
      source: string,
      message: string,
      userHelp?: string,
      developerMessage?: string,
      moreInfo?: string,
      variaveisMonitoradas?: any,
    } = {
      code: this.code,
      source: this.source,
      message: this.message,
    };
    if (this.userHelp) {
      erroJSON.userHelp = this.userHelp;
    }
    if (this.developerMessage) {
      erroJSON.developerMessage = this.developerMessage;
    }
    if (this.moreInfo) {
      erroJSON.moreInfo = this.moreInfo;
    }
    if (this.variaveisMonitoradas) {

      erroJSON.variaveisMonitoradas = {}; 
      this.variaveisMonitoradas.forEach((value, key) => {  
        
          erroJSON.variaveisMonitoradas[key] = value  
      });  

    }

    return erroJSON;
  }

  StringFormat = (str: string, ...args: string[]): string => str.replace(/{(\d+)}/g, (match, index) => args[index] || "");
  MapStringFormat = (map: Map<string, string>, ...args: string []): Map<string, string> => {

      for(const entry of map){
        const [ key, value ] = entry
        map.set(key, value.replace(/{(\d+)}/g, (match, index) => args[index] || ""))
        }

    return map;
  }
  public formatMessage(...args: string[]): ErroPadrao {
    this.message = this.StringFormat(this.message as string, ...args);
    return this;
  }

  public formatUserHelp(...args: string[]): ErroPadrao {
    this.userHelp = this.StringFormat(this.userHelp as string, ...args);
    return this;
  }

  public formatDeveloperMessage(...args: string[]): ErroPadrao {
    this.developerMessage = this.StringFormat(this.developerMessage as string, ...args);
    return this;
  }

  public formatMoreInfo(...args: string[]): ErroPadrao {
    this.moreInfo = this.StringFormat(this.moreInfo as string, ...args);
    return this;
  }

  public formatVariaveisMonitoradas(...args: string[]): ErroPadrao {
    this.variaveisMonitoradas = this.MapStringFormat(this.variaveisMonitoradas as Map<string, string>, ...args);
    return this;
  }
}

// Erros
export enum ErrorTypes {
  TIPO_ERRO_NEGOCIAL = "ErroNegocial",
  TIPO_ERRO_SQL = "ErroSQL",
```

#### erro.negocial.ts
Implementação da classe para erros negociais
```typescript 
import { ErroPadrao, ErrorTypes } from "./erro";

export class ErroNegocial extends ErroPadrao {
  constructor(code: string, message: string, userHelp?: string,
    developerMessage?: string, moreInfo?: string, variaveisMonitoradas?: Map<string, string>) {
    super(code, message, 422, userHelp,
      developerMessage, moreInfo, variaveisMonitoradas);
    this.name = ErrorTypes.TIPO_ERRO_NEGOCIAL;
  }
}

// Erro Generico para situações de erro em regra de negócio
export const ERRO_NEGOCIAL_GENERICO: [string, string] = ["001", "Ocorreu um erro ao processar a requisição"];

export const ERRO_NEGOCIAL_CPF_INVALIDO: [string, string, string] = ["002", "CPF {0} não é válido",
  "Insira um CPF válido no formato 999.999.999-99"];
// Erro com variáveis monitoradas, que serão utilizadas pelo Curió/Catálogo de Mensagens
export const ERRO_NEGOCIAL_CPF_INVALIDO_MSG: [string, string, string, string, string, Map<string, string>] = ["002", "CPF {0} não é válido",
  "Insira um CPF válido no formato 999.999.999-99", "Entrada inválida no campo x", "", new Map<string, string>()
  .set("cpf", "{0}")];
export const listErroNegocio = [new ErroNegocial(...ERRO_NEGOCIAL_GENERICO),
  new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO), new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO_MSG),
];
```

### erro.sql.ts
Implementação da classe para erros de SQL
```typescript
import { ErroPadrao, ErrorTypes } from "./erro";

class ErroSQL extends ErroPadrao {
  constructor(code: string, message: string, userHelp?: string,
    developerMessage?: string, moreInfo?: string) {
    super(code, message, 500, userHelp,
      developerMessage, moreInfo);
    this.name = ErrorTypes.TIPO_ERRO_SQL;
  }
}

// Erro Generico para situações de erro em regra de negócio
export const ERRO_SQL_GENERICO: [string, string, string, string, string] = ["999", "Erro no sistema",
  "Tente novamente mais tarde", "SQL CODE: {0} , QUERY SQL: {1}", "SQL CODE: {0} , MOTIVO: {1}"];

export const listErroSQL = [new ErroSQL(...ERRO_SQL_GENERICO),
];
```

#### error.handler.ts
Dita o funcionamento do servidor _Express_ em caso de erro. 
```typescript 
import express from "express";
import { ErrorTypes } from "../errors/erro";

export const handleError = (err: any, _req: express.Request, res: express.Response,
  next: express.NextFunction): void => {
  switch (err.name) {
    case ErrorTypes.TIPO_ERRO_NEGOCIAL:
    case ErrorTypes.TIPO_ERRO_SQL:
      break;

    default:
      err.statusCode = 500;
      err.toJSON = (): object => ({
        code: "-1",
        source: (err.stack as string).split("\n")[1].replace("at", "").trim(),
        message: err.message,
      });
  }

  res.status(err.statusCode).send(err.toJSON());
};
```
*Observação, ajuste a string PACKAGE_NAME com o nome correto para seu projeto.


### Exemplo de utilização

```typescript
  public async incluirCliente(req: any): Promise<ICliente> {
    const { name, cpf } = req;
    const cliente = new ClienteModel();
    if (this.irrf.validarCPF(cpf)) {
      return cliente.create({
        name, cpf,
      });
    }
    throw new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO).formatMessage(cpf);
  }
```


### Exemplo de utilização MSG 

```typescript
  public async buscarClientePorCPF(cpf: string) {

    const cliente = { cpf };
    this.tagParaTracer = cliente;

    if (this.irrf.validarCPF(cpf)) {
      return new ClienteModel().findOneByCPF(cpf);
    }

    throw new ErroNegocial(...ERRO_NEGOCIAL_CPF_INVALIDO_MSG).formatVariaveisMonitoradas(cpf).formatMessage(cpf);
  }

```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/errosTypescript.md&internalidade=erros/errosTypescript)
