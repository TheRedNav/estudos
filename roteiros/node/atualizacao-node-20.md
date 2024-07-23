> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/node/atualizacao-node-20.md&amp;action_name=node/atualizacao-node-20)
# Atualizando o Node 18 para o Node 20

Este roteiro destina-se a desenvolvedores Cloud e Node/Typescript que possuem aplicações rodando na versão 18 do Node e desejam atualizá-las para a versão 20. Esta atualização proporcionará o uso de versões mais confiáveis para suas soluções.

## Requisitos

É preciso ter o Node 18 ou 20 instalado no seu ambiente e versão 10 do npm.

Para checar sua versão, rode os comandos a seguir no seu terminal WSL:

```
node -v
npm -v
```

Se você for usuário do Pengwin e desejar utilizar a versão 18, basta rodar o seguinte comando no seu terminal WSL:

```
usarNode18
npm install -g npm@10
```

Já caso você queria utilizar a versão 20 com npm v10, basta rodar os comandos a seguir no WSL:

```
nvm install 20
nvm alias default 20
```

## Alterando os arquivos do seu projeto

Para alterar a imagem Node `v18` para `v20` no  `Dockerfile`, rode o seguinte comando dentro do seu projeto via terminal WSL:

```
sed -i 's|docker.binarios.intranet.bb.com.br/bb/dev/dev-nodejs:18.15.0|docker.binarios.intranet.bb.com.br/bb/dev/dev-nodejs:20.11.0|g' Dockerfile
```

Em seguida, será preciso atualizar a dependência `tedious` no `package.json`, caso tenha. Para isso, rode o seguinte comando também dentro do seu projeto via terminal WSL:

```
npm install tedious@18.1.0 --save
```

## Sobre a atualização das bibliotecas e métodos depreciados

Ao atualizar sua aplicação para o Node 20, verifique se há atualizações disponíveis para todas as bibliotecas, métodos e dependências utilizadas.

> :exclamation: Obs.: As atualizações resultam principalmente das depedências utilizadas, o que muda em cada projeto. A atualização que provemos é baseada no template Node/Typescript que também oferecemos suporte.

Dessa maneira, um projeto pode atualizar para o `Node 20` simplesmente mudando a imagem no `Dockerfile`, como também pode precisar atualizar inúmeras dependências e métodos que as utilizam.

## Extra

Caso queira saber de dependências depreciadas no seu projeto, além do `npm audit`, você pode rodar o seguinte comando:

```
npx npm-check-updates
```

Esse comando analisa o arquivo `package.json` e compara as versões das dependências dele com as versões LTS disponíveis no `NPM Registry`.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/atualizacao-quarkus.md&internalidade=frameworks/quarkus/atualizacao-quarkus)
