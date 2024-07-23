> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Migrando um projeto do Kumuluzee para Quarkus 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/migracao-quarkus.md&amp;action_name=frameworks/quarkus/migracao-quarkus)

## Introdução 

Porque migrar seu projeto para Quarkus: 

* Possibilidade de Hot Deploy (Live Coding)
* Framework em constante evolução 
* Patrocinado pela Red Hat 
* Tempo de uptime melhor que o do Kumuluzee
* Melhor uso da memória quando em comparação com o Kumuluzee
* Suporte a Build Nativo

O Quarkus e o Kumuluzee são implementações da especificação [Microprofile](https://projects.eclipse.org/projects/technology.microprofile). 

O MicroProfile é um conjunto de especificações mantidas pela fundação Eclipe que busca otimizar o desenvolvimento de microserviços agregando diversas tecnologias, inclusive algumas do Java EE/Jakarta EE. 

Como ambos o Kumuluzee e o Quarkus seguem a especificação do Microprofile, a migração é facilitada já que uma boa parte das ferramentas utilizadas possuem as mesmas interfaces. O objetivo deste tutorial é explicar como migrar as partes que são diferentes.

## Plano de Migração  

Nossa recomendação é que você gere um template novo em quarkus, utilizando o [brave](https://brave.dev.intranet.bb.com.br/plan), e em seguida faça as alterações descritas neste tutorial para portar a lógica do seu projeto antigo. 

Dica: você pode seguir estes passos para fazer a migração do template: 

1. crie uma branch nova no seu projeto (ex: `migracao-quarkus`). 

2. faça clone do projeto para sua máquina (caso ainda não tenha) e mude para a branch que você criou (`git checkout migracao-quarkus`) 

3. copie as pastas e arquivos dos arquivos .java do seu projeto em um diretorio temporario.

4. delete todos os seus arquivos, exceto o .git e Jenkinsfile, senão você perderá o controle de versão e a configuração de build de projeto pelo Jenkins. 

5. faça o comando `git add .` para adicionar as alterações ao git 

6. certifique-se que esteja na branch nova citada nos passos anteriores, faça o commit das alterações (ex: `git commit -m "Apagando projeto antigo`)

7. Na pasta do projeto descompacte o projeto recem gerado, a partir desse ponto voce pode seguir os mesmos passos da criação de um novo projeto gerado pelo brave.

8. Agora vem a parte mais complicada, copie os arquivos da pasta temporaria descrito no item 3 para a nova estrutura do projeto gerado.
Os topicos abaixo tem os detalhes para migração que devem ser considerados.


9. Ao finalizar o passo 8 execute o git para adicionar( `git add .`) os arquivos e para fazer o commit(`git commit`) e finalmente o push (`git push -u origin migracao-quarkus `) para enviar as modificoes locais para a nova branch. **Atenção** verifique se seu arquivo .gitignore esta configurado para nao subir arquvios .env e a pasta target por exemplo.


## Configuração do Quarkus 

No Kumuluzee, as nossas configurações do framework eram feitas através do arquivo `config.yaml` (dentro da pasta `resources`), como o exemplo abaixo. 

```yaml
kumuluzee:
  name: br.com.dx.dev-test
  artifactId: ${project.artifactId}
  groupId: ${project.groupId}
  version: ${project.version}
  env:
    name: test
  datasources:
    - jndi-name: jdbc/datasource
      connection-url: jdbc:db2://meudatabase
      driver-class: com.dx.mg2.jcc.DB2Driver
```

No quarkus, este arquivo tem um outro nome: `application.properties` (também na pasta `resources`). Portanto, passe para este as configurações que você tiver feito no Kumuluzee. Lembre-se que este não é um arquivo yaml... É simplesmente um arquivo chave-valor. Não use tabulações. Recomendamos a leitura da documentação oficial do quarkus sobre este arquivo em https://quarkus.io/guides/config. 

Seguindo o [roteiro de migração para o Quarkus 2](./atualizacao-quarkus.md) você pode ajustar o seu application.properties.


## Utilizando banco de dados

Se tiver informado uma opção de banco, ele irá criar as configurações para o banco e voce vai precisar configurar a url de conexão de banco, usuario e senha.
De preferencia por separar os pacotes com os DAO (classes que acessam o banco) e o models(classes de modelo que representam uma tabela).

Uma diferença é que no quarkus as querys devem ficam na anotação "@NamedNativeQueries", e não no arquivo `persistence.xml`, caso precise de um exemplo, gere um projeto com a opção de treinamento.---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/migracao-quarkus.md&internalidade=frameworks/quarkus/migracao-quarkus)
