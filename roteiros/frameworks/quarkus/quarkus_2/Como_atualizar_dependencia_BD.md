> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD.md&amp;action_name=frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD) 

# Como atualizar a dependência do Banco de Dados - Quarkus 2

Este roteiro ensina a atualizar a dependência do Banco de Dados após a atualização do seu projeto para a versão 2 do Quarkus. A atualização do Quarkus não implica automaticamente na obrigação de atualizar a dependência do banco de dados em seu projeto. Somente execute este roteiro quando for necessário atualizar a dependência por causa de alterações específicas introduzidas na nova versão do Quarkus.

Com as novas versões do Quarkus foram disponibilizadas extensões específicas para o Db2 e o Oracle. Com isso, o processo ficou mais simples, requerendo apenas informações básicas, como a URL de conexão, nome de usuário e senha. No caso de múltiplos bancos configurados, é necessário também informar a propriedade *db-kind*. Algumas configurações são aplicáveis apenas no escopo de *build*, enquanto outras são relevantes no escopo de execução.

> :grey_exclamation: **Importante** 
> 
> As configurações de execução podem ser modificadas sem a necessidade de reconstruir a aplicação. No entanto, o escopo de *build* é restrito ao momento da construção da aplicação, que ocorre durante o processo de compilação do Maven ou na execução da *pipeline* do Jenkins. Por essa razão, algumas propriedades só podem ser definidas uma vez. Na [página do Quarkus](https://quarkus.io/guides/all-config), você encontra a lista de todas as configurações disponíveis. As configurações de escopo de *build* são identificadas com o ícone do cadeado.

## Requisitos
* Concluir todas as configurações do roteiro [Como atualizar o Quarkus para a versão 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2.md).

## Passo 1: Atualizar o pom.xml

1. Abra a raiz do projeto na sua IDE.
2. Localize o [arquivo pom](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Em **dependency**, substitua as dependências dos drivers pela extensão do Bando de Dados que deseja utilizar.

Dependência da extensão Quarkus Db2

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-db2</artifactId>
    </dependency>
```

Dependência da extensão Quarkus Oracle

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-oracle</artifactId>
    </dependency>
```

## Passo 2: Atualizar o application.properties

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo [application.properties](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Vincule as propriedades de configuração a uma **environment** do sistema. Isso permitirá maior flexibilidade na atribuição dos valores.

> :information_source: **Observação** 
> 
> As configurações do banco h2, caso estejam sendo usadas para teste, não precisam ser alteradas.

Db2
```properties
quarkus.datasource.db-kind=db2
quarkus.datasource.jdbc.url=${DB2_URL}
quarkus.datasource.username=${DB2_USER}
quarkus.datasource.password=${DB2_PASSWORD}
```

Oracle
```properties
quarkus.datasource.db-kind=oracle
quarkus.datasource.jdbc.url=${ORA_URL}
quarkus.datasource.username=${ORA_USER}
quarkus.datasource.password=${ORA_PASSWORD}
```

## Passo 3: Criar o arquivo .env

1. Abra a raiz do projeto na sua IDE. 
2. Crie um novo arquivo [.env](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Inclua as **environments** com as informações sensíveis, como usuários, senhas e tokens para separar a configuração do ambiente de desenvolvimento do código propriamente dito. <br>
  3.1 Se precisar conectar-se a mais de um esquema ou banco de dados, duplique as **environments** e adicione aliases.

> :grey_exclamation: **Importante** 
> 
> Este arquivo não deve ser comitado no repositório de código fonte do seu projeto. Para evitar isso, inclua o arquivo *.env* na lista de arquivos ignorados pelo Git. Isso pode ser feito adicionando o nome *.env* no arquivo *.gitignore* do seu projeto. Se o arquivo *.gitignore* não existir, sugerimos usar o site [gitignore.io](https://www.toptal.com/developers/gitignore/) para criá-lo. Neste site, você pode selecionar as ferramentas com as quais trabalha, e ele criará um arquivo com a lista de arquivos que serão ignorados.

Db2
```env
DB2_URL=String_de_conexao_banco_dados
DB2_USER=SEU_USUARIO_DO_BANCO_DE_DADOS
DB2_PASSWORD=SEU_PASSWORD_DO_BANCO_DE_DADOS
```

Oracle
```env
ORA_URL=String_de_conexao_banco_dados
ORA_USER=SEU_USUARIO_DO_BANCO_DE_DADOS
ORA_PASSWORD=SEU_PASSWORD_DO_BANCO_DE_DADOS
```

> :information_source: **Observação** 
> 
> Em certos cenários, o usuário e a senha podem ser a própria matrícula e senha do SISBB do desenvolvedor. Nesses casos, use um *script shell* para criar as variáveis de ambiente apenas para a execução desse comando específico.

> :bulb: **Dica** 
> 
> É recomendável documentar no seu *README.md* as variáveis necessárias para o funcionamento da sua aplicação, incluindo sua finalidade e como obtê-las. Se viável, inclua um exemplo de arquivo *.env* para facilitar a configuração.

## Passo 4: Atualizar o docker-compose

1. Localize o arquivo [docker-compose](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md) na sua IDE. Geralmente ele está na pasta **run**.
2. Ajuste as **environments** de banco para obter as informações do arquivo **.env**.

Db2
```yaml
      - QUARKUS_DATASOURCE_URL=${DB2_URL}
      - QUARKUS_DATASOURCE_USERNAME=${DB2_USER}
      - QUARKUS_DATASOURCE_PASSWORD=${DB2_PASSWORD}
```
Oracle
```yaml
      - QUARKUS_DATASOURCE_URL=${ORA_URL}
      - QUARKUS_DATASOURCE_USERNAME=${ORA_USER}
      - QUARKUS_DATASOURCE_PASSWORD=${ORA_PASSWORD}
```
3. Execute o comando **docker-compose up** a partir da pasta raiz do projeto.

> :information_source: **Observação** 
> 
> O Quarkus prioriza a busca das variáveis de ambiente em relação às configurações no *application.properties*. Primeiro, o Quarkus procura as variáveis já presentes no sistema e, em seguida, as do *application.properties*. 

> :grey_exclamation: **Importante** 
> 
> O padrão de nomenclatura da variável de ambiente não aceita aceita pontos e outros caracteres. Portanto, no *application.properties*, substitua todos os caracteres que não sejam letras ou números por _ e converta tudo para maiúsculas. Para mais detalhes sobre essas configurações, consulte a página de referência de [configuração do Quarkus](https://quarkus.io/guides/config-reference).

> :warning: **Atenção** 
> 
> Certifique-se de remover qualquer configuração de banco presente nas variáveis de ambiente que seja destinada ao tempo de compilação (*build-time*). Você pode ver como na página de [guias do Quarkus](https://quarkus.io/guides/all-config), buscando as configurações aceitas para a versão específica do Quarkus que você está utilizando. As configurações marcadas com um cadeado são destinadas ao *build-time* e devem ser especificadas no arquivo *application.properties*.

Com a conclusão do roteiro de atualização da dependência do Banco de Dados, você já pode usar as melhorias e recursos oferecidos pela nova versão do Quarkus para aprimorar seu projeto.

**Tags:** #quarkus #atualizar #versao2 #oracle #db2

## A Seguir

* Leia o roteiro [Como atualizar o Curió com o Quarkus 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Curio.md) para continuar o processo de atualização com o Quarkus 2.
* Se necessário, consulte a página de resolução de problemas - [*troubleshooting*](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/troubleshooting.md).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD.md&internalidade=frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD)
