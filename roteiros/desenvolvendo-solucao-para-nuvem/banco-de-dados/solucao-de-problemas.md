> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Solução de Problemas no uso de Bancos de Dados
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/solucao-de-problemas.md&amp;action_name=desenvolvendo-solucao-para-nuvem/banco-de-dados/solucao-de-problemas)
Este roteiro visa auxiliar a resolução de erros no Banco de Dados.

## DB2 erros

Verifique o código do erro e as intruções para resolução, conforme indicado a seguir.

### SQL Error Code -4743 em produção

Caso receba o erro `Caused by: com.ibm.db2.jcc.am.SqlException: DB2 SQL Error: SQLCODE=-4743, SQLSTATE=56038` ao executar uma query no ambiente de produção, você pode estar tentando executar uma query com funções da versão 12 do DB2. Essa versão do DB2 já está disponível em produção, mas não foi feito o bind para todas as bases ainda, por isso o uso de algumas funções nas queries não vai funcionar. 

Para resolver inclua o parâmetro `currentPackagePath=V12R1M505` na string de conexão conforme abaixo:

```yaml
        - name: QUARKUS_DATASOURCE_JDBC_URL
          value: 'jdbc:db2://brdb2p1.plexbsb.bb.com.br:446/BRDB2P1:clientProgramName=APPCLOUD;clientApplicationInformation=dev-hello-js;currentPackagePath=V12R1M505'
```

> Para que isso funcione corretamente você devera usar um driver jdbc relativo à versão 11.1 FP1 do db2luw ou superior, caso contrario voce irá receber o erro -30025.

Também vai ser necessário refazer o bind do seu banco de dados para a versão 12, mediante solicitação para a Gesit.

Documentação do erro: https://www.ibm.com/docs/en/db2-for-zos/12?topic=codes-4743

Issue já resolvida com o mesmo problema: https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/4336

### Erro de Sequence ao acessar o D3G4

Caso tenha um erro de SqlSyntaxErrorException, parecido com esse abaixo:

```
Could not fetch the SequenceInformation from the database: com.ibm.db2.jcc.am.SqlSyntaxErrorException: DB2 SQL Error: SQLCODE=-204, SQLSTATE=42704, SQLERRMC=SYSCAT.SEQUENCES, DRIVER=4.27.25
```

Pode ser a configuração do tipo de dialeto utilizado para comunicacao com D3G4 do DB2.

Nesse caso adicione a seguinte linha no seu /src/main/resources/application.properties :

```
quarkus.hibernate-orm.dialect=org.hibernate.dialect.DB2400Dialect
```
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/solucao-de-problemas.md&internalidade=desenvolvendo-solucao-para-nuvem/banco-de-dados/solucao-de-problemas)
