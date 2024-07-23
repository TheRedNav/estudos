> :grey_exclamation: **Importante** 
> 
> A página de *troubleshooting* serve como um recurso central para solucionar problemas comuns relacionados ao Quarkus. <br>
>O problema é sempre identificado no título, e abaixo oferecemos soluções testadas e diretrizes simples para ajudar na resolução. 

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/troubleshooting.md&amp;action_name=frameworks/quarkus/troubleshooting)

# Falha ao buscar as informações da sequência no banco de dados Db2

**Descrição:** ao editar o arquivo *application.properties* para atualizar a configuração do Banco de Dados após a atualização do seu projeto para a versão 2 do Quarkus, os usuários podem encontrar o seguinte erro:
```
Could not fetch the SequenceInformation from the database: com.ibm.db2.jcc.am.SqlSyntaxErrorException: DB2 SQL Error: SQLCODE=-204, SQLSTATE=42704, SQLERRMC=SYSCAT.SEQUENCES, DRIVER=4.27.25`
```

**Solução:** tente incluir ou alterar o dialeto no arquivo *application.properties*, como indicado a seguir:
```properties
quarkus.hibernate-orm.dialect=org.hibernate.dialect.DB2400Dialect
```
## Ainda não encontrou a solução do seu problema?
Abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!   
