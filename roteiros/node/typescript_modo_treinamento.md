> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

**Roteiro para Criação e Uso de Código Typescript em Modo Treinamento**
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/node/typescript_modo_treinamento.md&amp;action_name=node/typescript_modo_treinamento)

Este roteiro contém todos os passos necessários para gerar, rodar e aprender sobre o código gerado em Typescript no Modo Treinamento, **mas não dispensa a formação básica indicada no cursos da plataforma [Onboarding Arq3](https://onboardingarq3.labbs.com.br/)**

Caso tenha alguma dificuldade durante a execução do roteiro, pode abrir uma [Issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues) na área de atendimento da DevCloud (selecionando o template "microsserviço").

...

**Sumário**

[[_TOC_]]


...

**Configurações extras**

- [Roteiros para configurações adicionais](https://fontes.intranet.bb.com.br/dev/publico/roteiros)

...

# 1. Pré-requisitos

Para fazer o curso, é necessário:
- Ter o Docker instalado;
- Ter o Node 20 instalado;
- Conhecimentos básicos sobre comandos no terminal Linux;
- Curso Onboarding Desenvolvimento Cloud concluído;
- Conhecimentos básicos de Javascript e NodeJS.

Obs.: A maioria das dependências necessárias vêm com a [instalação do pacote Arq 3.0 no Pengwin](https://fontes.intranet.bb.com.br/dev/publico/pengwin/-/blob/master/README.md).

## 1.1 Node 20

Para utilizar a versão 20 com npm v10, basta rodar os comandos a seguir:

```
nvm install 20
nvm alias default 20
```

# 2. Gerando o código base em Typescript no Modo Treinamento

Para gerar um código base, siga os passos a seguir:
- Acesse o Brave em [https://brave.dev.intranet.bb.com.br/](https://brave.dev.intranet.bb.com.br/)
- Selecione "Novo Projeto"
- Escolha a opção para gerar somente o "Código base"
- Selecione a stack "Node + Typescript"
- Selecione a versão disponível 
- Marque como "Modo Treinamento"

Quando é gerado no "Modo Treinamento", por padrão é gerado com a sigla "t99" (e não pode ser alterado), então na próxima página informe somente o "Nome do projeto" e clique para prosseguir.

Observe que na próxima tela as informações foram preenchidas automaticamente, então clique em "Confirmar" e será gerado um arquivo para download.

Salve esse arquivo no seu diretório de trabalho (ex.: workspace), descompacte o arquivo e abra com o seu VSCode.

# 3. Rodando o código gerado

Para rodar a aplicação, execute o script `run.sh` utilizando o comando abaixo, na pasta raiz do projeto:

```bash
./run/run.sh
```

Assim que terminar de subir a aplicação, você pode acessá-la em: [http://localhost:3000/](http://localhost:3000/)

# 4. Entendendo a estrutura da aplicação

Como sendo uma aplicação `cloud native`, ela já foi criada com uma configuração inicial contendo os seguintes componentes:

| Componente | Descrição                                            | Endpoint                         |
|:-----------|:-----------------------------------------------------|:---------------------------------|
| *Info*     | Informaçoes da aplicacao                             | [http://localhost:3000/info]     |
| *Docs*     | Documentação da api                                  | [http://localhost:3000/api-docs] |
| *Metrica*  | Metricas expostas pela aplicação                     | [http://localhost:3000/metrics]  |
| *Ready*    | Indica que a aplicação está pronta para responder    | [http://localhost:3000/ready]    |
| *Health*   | Indica que a aplicação está operando normalmente     | [http://localhost:3000/health]   |

Caso necessite alterar algum desses componentes, os fontes estão em `src/dev-libs`.

# 5. Entendendo a API

Ao acessar [http://localhost:3000/api-docs], você terá os seguintes exemplos de endpoints:

| Componente    | Descrição                                          | Endpoint                                              |
|:--------------|:---------------------------------------------------|:------------------------------------------------------|
| *Hello World* | Endpoint para exemplo de requisições GET           | [http://localhost:3000/hello-world]                   |
| *FindAllUsers*| Lista usuários persistidos em Oracle*              | [http://localhost:3000/api-docs/#/Users/FindAllUsers] |
| *CreateUser*  | Insere novo usuário em Oracle*                     | [http://localhost:3000/api-docs/#/Users/CreateUser]   |
| *FindUser*    | Consulta pelo Id um usuário salvo em Oracle*       | [http://localhost:3000/api-docs/#/Users/FindUser]     |

* Oracle rodando em container localmente

# 6. Criando um novo serviço na API com suporte a banco de dados

## 6.1 Criando um novo metódo na controller

Como você pode ter percebido, em nossa API de exemplo existem os serviços de listar usuários, buscar um usuário (por id) e criar um novo usuário, mas não existe o serviço de deletar um usuário existente, então iremos criar esse serviço, começando para camada de negócio/controle.

Na camada de negócio é onde são definidas as regras e comportamento esperados.

Para criar o novo método, siga os passos:

- Navegue até a pasta `src/controller`
- Abra no VSCode o arquivo: `user.controller.ts`

Observe que essa classe `UserController` já possui três imports:

```typescript
import { setTagSpan, traceable } from "jaeger-tracer-decorator";
import { Body, Get, Path, Post, Route, SuccessResponse, Tags } from "tsoa";
import { IUser, UserModel } from "../model/user.model";
```

- O `jaeger-tracer-decorator` é usado para fazer o `tracing` (rastreamento) através das anotações `@traceable` e `@setTagSpan`. Para mais informações sobre o `jaeger-tracer-decorator` [clique aqui](https://github.com/CarlosPanarello/jaeger-tracer-decorator).
- O `tsoa` é usado para instrumentar o código com as anotações `@Body`, `@Get`, `@Path`, `@Post`, `@Route`, `@SuccessResponse` e `@Tags`, a fim de gerar automaticamente a documentação Swagger da sua API. Para consultar a documentação do `tsoa` [clique aqui](https://tsoa-community.github.io/docs/).
- A UserModel será descrita mais a frente, quando tratarmos da camada de persitência.

Na linha do import do `tsoa` adicione a classe `Delete`, da seguinte forma:

```typescript
import { Body, Get, Path, Post, Delete, Route, SuccessResponse, Tags } from "tsoa";
```

Insira o novo método conforme abaixo:

```typescript
  /**
   * Deleta usuário por id
   *
   * @summary Deletar usuário
   *
   * @example userId 123
   */
  @SuccessResponse("200", "Usuário deletado")
  @Delete("/{id}")
  public static async deleteUser(@Path() id: number): Promise<void> {
    const deleted = await UserModel.destroy({ where: { id }, cascade: true });
    if (!deleted) {
      throw new Error("Usuário não encontrado");
    }
  }
```

## 6.2 Definição de serviços REST

Nessa camada são definidas as rotas (endpoints) da sua API, sendo que as rotas são expostas como GET ou POST através do uso das classes Router, Request, Response, NextFunction do `express`, que é um framework muito popular para NodeJS. Para aprender mais sobre esse framework [clique aqui](https://expressjs.com/pt-br/) e sobre NodeJS [clique aqui](https://nodejs.org/en).

Para que as rotas sejam carregadas pelo `express` é necessário que o arquivo `src/routes/api.ts` contenha a chamada da sua classe `user.api.ts` dentro do construtor, isso fará com que todas as rotas sejam mapeadas pela aplicação, seguindo o exemplo abaixo:

```typescript
  import { UserAPI } from "./user.api";

  export class ApisRouter {
    constructor(router: Router) {
      new UserAPI(router);
    }
  }

  export default ApisRouter;
```

Para criarmos uma novo serviço REST e realizar a invocação do novo método, siga os passos:

- Abra no VSCode a sua classe de rotas: `user.api.ts`
- Crie um novo método estático, assíncrono e que receba o parâmetro id
- Seu método também deverá retornar uma promise void
- Lembre de tratar o erro lançado pela controller, no caso de não encontrar o id no banco de dados
- Sua implementação ficará parecida com o exemplo abaixo:

```typescript
  private static async deleteUser(
    req: Request,
    resp: Response,
    next: NextFunction,
  ) {
    try {
      const userId = Number(req.params.userId);
      await UserController.deleteUser(userId);
      resp.status(200).end();
    } catch (error) {
      resp.status(412).json(error.message);
      return next(error);
    }
  }
```

- Vá ao construtor, e adicione o novo método como handler na rota, da seguinte forma:

```typescript
  constructor(router: Router) {
    router.get("/users", UserAPI.findAllUsers);
    router.post("/users", UserAPI.createUser);
    router.get("/users/:userId", UserAPI.findUser);
    router.delete("/users/:userId", UserAPI.deleteUser);
  }
```

Pronto, pode rodar novamente com `./run/run.sh` para conferir.

Use o serviço de listar usuários ([http://localhost:3000/api-docs/#/Users/FindAllUsers](<http://localhost:3000/api-docs/#/Users/FindAllUsers>)) para consultar o id que deseja deletar, em seguida use seu novo serviço para deletar o usuário desejado.

Teste também com um id inexistente para ver se o tratamento de erro está funcionando adequadamente.

## 6.3 Camada de persistência

Na camada de persistência, em `src/model`, é feito o mapeamento objeto relacional (ORM) do banco de dados, a exemplo de `user.model.ts`:

```typescript
import { Optional, literal } from "sequelize";
import {
  AllowNull,
  AutoIncrement,
  Column,
  CreatedAt,
  Default,
  HasOne,
  Model,
  PrimaryKey,
  Table,
  UpdatedAt,
} from "sequelize-typescript";

import AddressModel from "./address.model";

/**
 * @example
 * {
 *   "id": 1,
 *   "name": "Huguinho",
 *   "dateOfBirth": "2008-04-09T11:58:45.000Z"
 * }
 */
export interface IUser {
  id: number;
  name: string;
  dateOfBirth: Date;
}

export type IUserCreation = Optional<IUser, "id">;

@Table({ tableName: "USUARIOS" })
export class UserModel extends Model<IUser, IUserCreation> {
  @PrimaryKey
  @AutoIncrement
  @Column({ field: "ID" })
  public id!: number;

  @Column({ field: "NOME" })
  public name!: string;

  @Column({ field: "NASCIMENTO" })
  public dateOfBirth!: Date;

  @HasOne(() => AddressModel)
  public address!: AddressModel;

  @CreatedAt
  @AllowNull(false)
  @Default(literal("CURRENT_TIMESTAMP"))
  @Column({ field: "CREATED_AT" })
  public createdAt!: Date;

  @UpdatedAt
  @AllowNull(false)
  @Default(literal("CURRENT_TIMESTAMP"))
  @Column({ field: "UPDATED_AT" })
  public updatedAt!: Date;
}

export default UserModel;
```

Nesse exemplo a persistência faz uso do `sequelize` (conheça [aqui](https://sequelize.org/)) e do `sequelize-typescript` (documentação [aqui](https://github.com/sequelize/sequelize-typescript)), sendo que este último facilita o mapeamento objeto relacional através do uso das anotações `@AllowNull`,   `@AutoIncrement`, `@Column`, `@CreatedAt`, `@Default`, `@HasOne`, `@Model`, `@PrimaryKey`, `@Table` e `@UpdatedAt`.

Entretanto observe que não foi necessário adicionar um método `destroy` ao model, pois esse método já é nativo do `sequelize-typescript`.

## 6.4 Docker Compose do banco de dados

Mas para essa API funcionar, nós carregamos um banco de dados que roda em um container localmente, configurado em `run/docker-compose-database.yml` (abaixo). Esse docker-compose carrega a imagem de uma versão leve do oracle: `oracle-free:23.3-slim`:

```yaml
version: "3"
services:
  database:
    container_name: database
    image: gvenzl/oracle-free:23.3-slim
    network_mode: host
    ports:
      - "1521:1521"
    volumes:
      - ../scripts/sql:/container-entrypoint-initdb.d
    environment:
      ORACLE_RANDOM_PASSWORD: yes
      ORACLE_DATABASE: testdb
      APP_USER: testuser
      APP_USER_PASSWORD: testpassword
    healthcheck:
      test: healthcheck.sh
      interval: 10s
      timeout: 5s
      retries: 10
      start_period: 1m
```

Esse `run/docker-compose-database.yml` é adicionado ao docker compose da aplicação em `run/docker-compose.yml` (abaixo), que é responsável por subir a aplicação quando você executa o comando `./run/run.sh`.

```yaml
version: "3"
services:
  app:
    container_name: t99-treinamento-typescript
    image: t99-treinamento-typescript
    build:
      context: ../
      dockerfile: Dockerfile
    user: "${MY_UID}:${MY_GID}"
    depends_on:
          database:
            condition: service_healthy
    command: npm run dev
    network_mode: host
    ports:
      - "3000:3000"
      - "9229:9229"
    volumes:
      - ../:/app
    environment:
      TZ: America/Sao_Paulo
      APP_HOST: 0.0.0.0
      API_PORT: 3000
      ENABLE_LOG_HTTP: true
    healthcheck:
      test: curl -f --noproxy 'localhost' http://localhost:3000/health
      interval: 20s
      timeout: 5s
      retries: 5

  database:
      extends:
        file: docker-compose-database.yml
        service: database

  iib-curio:
      extends:
        file: docker-compose-curio.yml
        service: iib-curio
      depends_on:
        app:
          condition: service_healthy
```

## 6.5 Script de carga de dados

Mas para que o banco de dados não seja carregado vazio, usamos um script de carga `scripts/sql/carga-banco-dados.sql` que cria e insere dados nas tabelas `USUARIOS` e `ENDERECO`.

## 6.6 Conexão com o banco de dados

A conexão com o banco de dados é configurada em `src/config/database.config.ts`:

```typescript
import { Sequelize } from "sequelize-typescript";
import { environment } from "./environment";
import { AddressModel } from "../model/address.model";
import { UserModel } from "../model/user.model";

export class DatabaseConfig {
  static sequelize: Sequelize;

  public static async getConnection() {
    if (DatabaseConfig.sequelize) {
      return DatabaseConfig.sequelize;
    }

    const sequelize = new Sequelize({
      dialect: environment.relationalDB.dialect,
      database: environment.relationalDB.database,
      schema: environment.relationalDB.schema,
      username: environment.relationalDB.username,
      password: environment.relationalDB.password,
      storage: environment.relationalDB.storage,
      host: environment.relationalDB.host,
      port: environment.relationalDB.port,
      models: [AddressModel, UserModel],
    });

    await sequelize.authenticate();
    await sequelize.sync();

    DatabaseConfig.sequelize = sequelize;

    return DatabaseConfig.sequelize;
  }
}
```

Para conexão com o banco de dados é utilizado basicamente o `sequelize-typescript` (documentação [aqui](https://github.com/sequelize/sequelize-typescript)), com uso de variáveis de ambiente e do mapeamento objeto relacional feito anteriormente.


No caso do código gerado em modo treinamento, a variável `relationalDB` é gerada com os seguintes parâmetros e valores para rodar `Oracle` localmente em container:

```typescript
relationalDB: {"dialect":"oracle","database":"testdb","username":"testuser","password":"testpassword","host":"localhost","port":1521},
```

Caso necessite apontar para um banco de dados de algum dos ambientes, altere esses valores na variável `relationalDB` ou sobreescreva os valores das variáveis diretamente com secrets e variáveis no values.yaml do respectivo ambiente.

# 7. Integrando com o legado

## 7.1 Consumindo operações utilizando o Curió

Agora, é hora de aprender como o código da nossa aplicação se integra com o barramento IIB. Vamos começar com o consumo de operações, que é mais fácil e vai te ajudar a entender melhor como o Cúrio funciona. Vamos usar a operação 252416 versão 1.

- Acesse a Plataforma BB e altere para o modo Tecnologia.
- No menu lateral esquerdo, acesse Construção > Catálogo > Operações.
- Após abrir a aplicação, preencha os campos operação com 252416 e versão 1.
- Clique em Pesquisar.
- Na lista de operações que aparece como resultado, clique na operação 252416 v1.
- Após, clique no ícone para gerar a dependência NPM da operação.

Será exibida uma janela popup com a linha de comando npm para instalar a operação, desta forma:

```npm
npm install --save-dev @bb-catalogo-arh/op252416v1
```

### 7.1.1 Configurações do Curió

Agora vamos conhecer o código da sua aplicação. Localize o arquivo `package.json`, na pasta raiz do projeto. Procure pela linha `"dependencies":`. Perceba que nesse trecho de código já temos declarada a dependência que geramos na plataforma.

```json
  "dependencies": {
    "@bb-catalogo-arh/op252416v1": "1.0.0",
```

Também foi implementada uma configuração para que o Curió possa subir em um container e fazer a comunicação com o barramento IIB (que se comunica com o legado), sendo necessário informar a operação (nesse formato abaixo), na variável de ambiente `CURIO_OP_CONSUMIDOR`, que você pode ver no arquivo `.env_curio`, localizado na pasta raiz do projeto.

```shell
KUMULUZEE_SERVER_HTTP_PORT=8081
KUMULUZEE_ENV_NAME=dev
CURIO_CACHE_CONFIGURACAO_IIB=iib-slave.redis.bdh.desenv.bb.com.br
CURIO_CACHE_CONFIGURACAO_IIB_ID=iib:configuracao:k8s-integracao
CURIO_SIGLA_APLICACAO=t99
CURIO_APLICACAO_HOST=http://localhost:3000
CURIO_IIB_LOG_LEVEL=FINE
CURIO_DRY_RUN=false
CURIO_MODO_DESENVOLVIMENTO=true
KUMULUZEE_LOGS_LOGGERS0_NAME=br.com.bb
KUMULUZEE_LOGS_LOGGERS0_LEVEL=TRACE
CURIO_OP_CONSUMIDOR=br.com.bb.ctl.operacao:Op7156536-v1:4.4.0|br.com.bb.arh.operacao:Op252416-v1:2.0.0
CURIO_OP_PROVEDOR=br.com.bb.t99.operacao:Op5207120-v1:2.2.0-SNAPSHOT
```

Na pasta `run/`, você vai perceber que essa mesma `.env_curio` é adicionada ao `docker-compose-curio.yml`, conforme abaixo:

```yaml
version: "3"
services:
  iib-curio:
    container_name: iib-curio
    image: docker.binarios.intranet.bb.com.br/bb/iib/iib-curio:0.8.4
    ports:
      - "8081:8081"
    env_file:
      - ../.env_curio
```

E esse `docker-compose-curio.yml` é adicionado ao docker compose da aplicação em `run/docker-compose.yml`, que é responsável por subir a aplicação quando você executa o comando `./run/run.sh`.


Obs.: Para saber mais sobre o IIB/Curió [clique aqui](<https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio#iib-curi%C3%B3>).

### 7.1.2 Definindo a controller de consumo

Quando um código é gerado informando a operação de consumo, o gerador já disponibiliza automaticamente as interfaces de entrada e saída das operações.

Em `/src/controller`, acesse o arquivo `op252416v1.controller.ts`. Observe que já existe uma assinatura definida para esta operação no import de `IEntradaOp252416v1`. Se você navegar em `node_modules` vai encontrar a dependência `@bb-catalogo-arh/op252416v1` juntamente com as interfaces de entrada e saída dessa operação `op252416v1`. As anotações `@Body, @Post, @Route e @Tags` abaixo, da dependência [tsoa](https://tsoa-community.github.io/docs/), montam automaticamente os dados dessa API no swagger e a dependência [axios](<https://axios-http.com/ptbr/docs/intro>) nos permite acionar o `request` dessa API no formato JSON com base no que foi definido na const `config`.

O NodeJS precisa saber o endereço do servidor que está provendo o endpoint que definimos na interface. A variável `CURIO_CONSUME_URL` provê esse path como `http://localhost:8081`.

```typescript
import axios from "axios";
import { Body, Post, Route, Tags } from "tsoa";

import { IEntradaOp252416v1 } from "@bb-catalogo-arh/op252416v1";

const CURIO_CONSUME_URL = "http://localhost:8081";
const IIB_OPERATION_LOWERCASE = "op252416v1";

@Tags("Curió")
@Route("/")
export default class Op252416v1Controller {
  @Post("/op252416v1")
  public static async consumeOp252416v1(@Body() payload: IEntradaOp252416v1) {
    const url = `${CURIO_CONSUME_URL}/${IIB_OPERATION_LOWERCASE}`;

    const config = {
      method: "post",
      maxBodyLength: Infinity,
      url,
      headers: {
        "Content-Type": "application/json",
      },
      data: payload,
    };

    try {
      const response = await axios.request(config);
      return response.data;
    } catch (e) {
      e.data = "Houve um erro na sua requisição";
      e.success = false;
      throw e;
    }
  }
}
```

### 7.1.3 Definindo a rota de consumo

Em `/src/routes`, acesse o arquivo `op252416v1.api.ts`. Ele faz uso do framework [express](https://expressjs.com/pt-br/) que nos permite mapear a rota `/op252416v1` com uso do `Router`, bem como trabalhar com o `Request` e `Response` das requisições. Veremos aqui também o import da controller da `op252416v1` definida anteriormente.

```typescript
import { Router, Request, Response } from "express";

import Op252416v1Controller from "../controller/op252416v1.controller";

export default class Op252416v1API {
  private static async consumeOp252416v1(req: Request, resp: Response) {
    const payload = req.body;

    try {
      const response = await Op252416v1Controller.consumeOp252416v1(payload);

      resp.json({
        data: response,
        success: true,
      });
    } catch (e) {
      resp.json({
        data: e,
        success: false,
      });
    }
  }

  constructor(router: Router) {
    router.post("/op252416v1", Op252416v1API.consumeOp252416v1);
  }
}
```

### 7.1.4 Testando o consumo via API

Agora rode a aplicação com `./run/run.sh` (caso já não esteja rodando).

Acesse o ednpoint da operação `Op252416v1` em [http://localhost:3000/api-docs/#/Curi%C3%B3/ConsumeOp252416v1](<http://localhost:3000/api-docs/#/Curi%C3%B3/ConsumeOp252416v1>)

Informe no body o seu número de matrícula (somente números):

```json
{
  "entrada": {
    "matrInf": 9999999 // informe aqui sua matrícula
  }
}
```

Você deve receber um retorno semelhante a esse:

```json
{
  "data": {
    "saida": {
      "nomeRet": "FULANO DE TAL",
      "sitRet": 100,
      "prfLocaRet": 9909,
      "cdComisRet": 12321,
      "nmComisRet": "ASSESSOR TI UE",
      "cdRfRet": 6,
      "cdRoRet": "2AUE"
    },
    "controle": {
      "retCode": 0,
      "sqlcode": 0,
      "section": "",
      "sqlca": "",
      "sqlcode2": 0
    }
  },
  "success": true
}
```

Você aprendeu a consumir uma operação através da aplicação da nuvem BB. De forma muito semelhante funciona o provimento. Lembre-se que o Curió expõe um endpoint e faz todo o tratamento para conversar com o barramento IIB. Para isso, você precisa chamar esse endpoint com a requisição no padrão JSON e com os campos corretos.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/node/typescript_modo_treinamento.md&internalidade=Roteiro_typescript_modo_treinamento)
