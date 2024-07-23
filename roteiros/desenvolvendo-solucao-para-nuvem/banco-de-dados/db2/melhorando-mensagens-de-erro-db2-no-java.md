> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/melhorando-mensagens-de-erro-db2-no-java.md&amp;action_name=desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/melhorando-mensagens-de-erro-db2-no-java.md)
# Deploy em produção

# Melhorando mensagens de erro do DB2 na versão 12 no Java.

O padrão de URL de conexão via JDBC é:

`jdbc:db2://dsdb2d01.plexdes.bb.com.br:446/DSDB2D01`

Quando acontece alguma exceção, recebemos a mensagem no seguinte formato:

```
Caused by: com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: DB2 SQL Error: SQLCODE=-407, SQLSTATE=23502, SQLERRMC=*N, DRIVER=4.31.10
```

Podemos receber mensagens de erros mais descritivas adicionando o parâmetro `retrieveMessagesFromServerOnGetMessage=true`, a URL de conexão fica assim:

`jdbc:db2://dsdb2d01.plexdes.bb.com.br:446/DSDB2D01:retrieveMessagesFromServerOnGetMessage=true;`

A mensagem passa a ficar no formato abaixo:

```
Caused by: com.ibm.db2.jcc.am.SqlIntegrityConstraintViolationException: AN UPDATE, INSERT, OR SET VALUE IS NULL, BUT THE OBJECT COLUMN *N CANNOT CONTAIN NULL VALUES. SQLCODE=-407, SQLSTATE=23502, DRIVER=4.31.10
```

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/melhorando-mensagens-de-erro-db2-no-java.md&internalidade=desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/melhorando-mensagens-de-erro-db2-no-java)
