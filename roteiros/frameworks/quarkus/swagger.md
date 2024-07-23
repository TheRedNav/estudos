> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Configurando Swagger em aplicações Quarkus
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/swagger.md&amp;action_name=frameworks/quarkus/swagger)

Caso seu projeto tenha sido gerado com a versão 1.7.3 ou inferior, voce pode remover a classe AppConfig do seu projeto, se ela possuir alguma logica remova apenas a anotação ´@OpenAPIDefinition` e seu conteudo, e depois pode seguir esse roteiro normalmente.

## Propriedades

A maioria das configurações do swagger para o quarkus são utilizadas em tempo de build, elas são usadas quando o projeto é construido gerando um arquivo .jar.
Assim essas configurações não são possiveis de alteração, dentre elas a que ativa e desativa a utilização do swagger por exemplo.
Por isso essas propriedades não alteram o comportamento da aplicação quando colocadas como enviromnets da aplicação, por exemplo no values.yaml ou no enviroments do docker compose. As que mais usamos nos projetos estão na tabela abaixo, para ver todas basta ir na documentação do [quarkus](https://quarkus.io/guides/openapi-swaggerui#configuration-reference), lembrando que as propriedades no link da documetação com cadeado antes do nome são propriedades utilizadas em tempo de build.
Como essas propriedades de build, elas devem ficar no arquivo `aplication.properties` localizado na pasta `/src/main/resources` do seu projeto.

Você pode configurar varias partes da apresentação do swagger com configuração no proprio `aplication.properties`, a seguir temos um conjunto de configurações, atenção para alterar os valores conforme os dados do seu projeto:

```
# Configuracao do caminho para acessar a documentacao da aplicacao em swagger
quarkus.swagger-ui.always-include=true
quarkus.health.openapi.included=true
quarkus.swagger-ui.urls.default=/api-docs-json
quarkus.smallrye-openapi.path=/api-docs-json
quarkus.swagger-ui.path=/api-docs
quarkus.swagger-ui.filter=true
mp.openapi.extensions.smallrye.info.title=${quarkus.application.name}
mp.openapi.extensions.smallrye.info.version=${quarkus.application.version}
%test.mp.openapi.extensions.smallrye.info.title=minhaApp
%test.mp.openapi.extensions.smallrye.info.version=1.0.0
mp.openapi.extensions.smallrye.info.description=Descreva a descricao da sua aplicacao, altere na propriedade mp.openapi.extensions.smallrye.info.description do application.properties
mp.openapi.extensions.smallrye.info.contact.name=Coloque sua equipe aqui, altere na propriedade mp.openapi.extensions.smallrye.info.contact.name do application.properties
mp.openapi.extensions.smallrye.info.contact.url=https://fontes.intranet.bb.com.br/sua-sigla/sua-aplicacao
mp.openapi.servers=http://localhost:8080
```

## Valores por ambiente 

A propriedade mp.openapi.servers pode ser sobrescrita no values.yml de cada ambiente. Por exemplo, no repositório de desenv:

```yaml
      - name: MP_OPENAPI_SERVERS
        value: https://endereco-do-sua-aplicacao.desenv.bb.com.br
```

## Ícones

Caso queria adicinonar os icones do banco na sua pagina do swagger, coloque esses dois arquivos [favicon.ico](./arquivos/favicon.ico) e [logo.png](./arquivos/logo.png) dentro da pasta
`src\main\resources\META-INF\branding`, crie a pasta `branding` se não existir.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/swagger.md&internalidade=frameworks/quarkus/swagger)
