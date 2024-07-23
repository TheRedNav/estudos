> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Alterar_propriedades_environments.md&amp;action_name=erros/Alterar_propriedades_environments)

# Alterar propriedades com environments
Este roteiro lista environments, com valores e suas funcionalidades, que alteram propriedades para exceções geradas pelo consumo de operações do Curió.


| Environment                    | Valor padrão                                    | Descrição                                                                                                                                         | 
|:-------------------------------|:------------------------------------------------|:--------------------------------------------------------------------------------------------------------------------------------------------------|
| dev.log.paths.exclusions       | /info,/errors,/metrics,/health,/health/\*,/q/\* | Lista dos paths excluídos para gerar log de requisição e resposta, separados por vírgula, no padrão jax-rs(/usuario/{id}).                        |
| dev.log.path.raw               | false                                           | Habilita o path original requisitado sem incluir os alias do paths param                                                                         |
| dev.log.http.size              | false                                           | Habilita para calcular o tamanho em bytes do body da mensagem                                                                                    |
| dev.log.restclient             | true                                            | Habilita log para todas as requisições realizadas com RestClient para consumo de outros endpoints.                                               |
| dev.erro.paths.exclusions      | /metrics,/health,/health/\*,/q/\*               | Lista dos paths excluídos que não irão gerar erro no padrão gerado pelos filtros de erro, separados por vírgula, no padrão jax-rs(/usuario/{id}). |
| dev.erro.header-message        | error-info                                      | Chave do atributo identificador de erro enviado no header na resposta                                                                             |
| dev.erro.filtro-pacote         | br.com.bb                                       | Package name do projeto para buscar a primeira ocorrência de erro                                                                                 |
| dev.erro.id-padrao             | ERRO_SISTEMA                                    | Identificador de erro padrão utilizada nos filtros, quando o identificador não for informado                                                      |
| dev.erro.msg-padrao            | Erro na execução da solicitação.                | Mensagem padrão para erros não tratados                                                                                                           |
| dev.erro.code-curio            | CURIO_CONSUMO_ERRO                              | Identificador do erro padrão no tratamento de erros no consumo de operação                                                                        |
| dev.erro.msg-curio             | Erro no consumo da operação                     | Mensagem padrão para o erro no tratamento de erros de consumo de operação                                                                         |
| dev.erro.field.message         | MSG_ERRO, message, mensagem, msg                | Nomes das propriedades estáticas usadas para armazenar a mensagem erro dentro da classe de exceção separados por vírgula, ex: msg,mensagem        |
| dev.erro.field.code            | COD_ERRO, codigo, cod, id, identificador        | Nomes das propriedades estativas usadas para armazenar o código de erro dentro da classe de exceção separados por vírgula, ex: cod,idErro         |
| dev.application.name.config    | quarkus.application.name                        | Configuração de environments para obter o nome da aplicação, e se não encontrar na variável padrão será utilizado o valor "none".                 |
| dev.application.version.config | quarkus.application.version                     | Configuração de environments para obter a versão da aplicação, e se não encontrar na variável padrão será utilizado o valor "none".

## A Seguir
* Leia o roteiro [Como tratar erros no Java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Como_tratar_erro_Java.md) para compreender como as environments são utilizadas no tratamento de erro das requisições da aplicação.

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/erros/Alterar_propriedades_environments.md&internalidade=erros/Alterar_propriedades_environments)
 
