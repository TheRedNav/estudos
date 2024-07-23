> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Debugando Aplicações Typescript
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/debug/debug-typescript.md&amp;action_name=debug/debug-typescript)

## Configurando aplicação

Para debugar aplicações que executam via docker devemos utilizar a opção de debug remoto.
As aplicações criadas utilizando o BBDev já estão configuradas para debug remoto na porta 9229, caso sua aplicação não tenha sido gerada pelo BBDev basta adicionar os comandos de script na propridade script do package.json.
E as configurações do nodemon pela propriedade nodemonConfig.
      
```json
  "scripts": {
    "docker": "node_modules/.bin/tsc && docker-compose up --remove-orphans --build --force-recreate",
    "type": "node_modules/.bin/tsc",
    "watch": "node_modules/.bin/tsc -w",
    "clean": "rm -rf ./dist/* && echo Clean finished!!!",
    "build": "npm run clean && npm install && npm run type && npm run lint && echo Build finished!!!",
    "develop": "node_modules/.bin/nodemon --legacy-watch --delay 1 --inspect=0.0.0.0:9229 ./dist/server.js",
    "lint": "node_modules/.bin/tsc --noEmit && eslint \"**/*.{js,ts}\" --quiet --fix",
    "start": "node --inspect=9229 -r ts-node/register ./src/server.ts",
    "start:watch": "nodemon",
    "serve": "node ./dist/server.js",
    "test": "jest",
    "unit-test": "jest --testPathPattern=ut.test\\.ts$",
    "integrationTest": "jest --testPathPattern=it.test\\.js$"
  },
  "nodemonConfig": {
    "ignore": [
      "**/*.test.ts",
      "**/*.spec.ts",
      ".git",
      "node_modules"
    ],
    "watch": [
      "src"
    ],
    "ext": "ts"
  }
```

## Configurando VScode
    Acessar a opção o menu Debug > Add Configuration > Java , ele vai criar um arquivo chamado launch.json que deve ser configurado conforme abaixo.
    A propriedade port deve ser a mesma utilizada na etapa anterior, no caso escolhemos a 9229.
    Mude a propriedade projectName conforme o nome utilizado em seu projeto.
    Execute o comando npm run docker, ou docker-compose up na raiz do projeto para subir sua aplicação.
    Para comecar a debugar pressione F5, ou clique no icone de Bug na lateral esquerda e depois em start selecioando a opção de Attach.
    Depois basta adicionar os breakpoints em seu projeto.
      
```json
{
  // Use IntelliSense to learn about possible attributes.
  // Hover to view descriptions of existing attributes.
  // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
  "version": "0.2.0",
  "configurations": [
    {
      "type": "node",
      "request": "launch",
      "name": "Start in Docker",
      "protocol": "auto",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "docker"],
      "port": 9229,
      "restart": true,
      "timeout": 45000,
      "localRoot": "${workspaceFolder}/dist",
      "remoteRoot": "/app/dist",
      "outFiles": ["${workspaceFolder}/dist/**/*.js"],
      "skipFiles": ["<node_internals>/**/*.js"],
      "console": "integratedTerminal",
      "internalConsoleOptions": "neverOpen"
    },
    {
      "type": "node",
      "request": "launch",
      "name": "Start Local",
      "protocol": "auto",
      "runtimeExecutable": "npm",
      "runtimeArgs": ["run", "develop"],
      "port": 9229,
      "restart": true,
      "timeout": 45000,
      "localRoot": "${workspaceFolder}/src",
      "remoteRoot": "${workspaceFolder}/src",
      "console": "integratedTerminal",
      "internalConsoleOptions": "neverOpen"
    },
    {
      "type": "node",
      "request": "attach",
      "name": "Attach",
      "protocol": "auto",
      "port": 9229,
      "restart": true,
      "localRoot": "${workspaceFolder}/dist",
      "remoteRoot": "/app/dist",
      "outFiles": ["${workspaceFolder}/dist/**/*.js"],
      "skipFiles": ["<node_internals>/**/*.js"]
    }
  ]
}

```

Caso esteja com problemas ao conectar o debug do vscode, verifique as portas utilizadas com a que é apresentado no inicio do log da aplicação, conforme a linha abaixo:

```sh
Debugger listening on ws://0.0.0.0:9229/9efd5890-a79f-4263-94cc-0e1865cb4cca
```

Para maiores informações utilize a documentação de debug do [vscode](https://code.visualstudio.com/docs/typescript/typescript-debugging)




---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/debug/debug-typescript.md&internalidade=debug/debug-typescript)
