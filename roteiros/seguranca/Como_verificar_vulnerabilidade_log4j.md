> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Como_verificar_vulnerabilidade_log4j.md&amp;action_name=seguranca/Como_verificar_vulnerabilidade_log4j)

# Como verificar vulnerabilidade da lib log4j
Este roteiro ensina como verificar se sua aplicação está usando uma versão do log4j com a vulnerabilidade **CVE-2021-44228** e como corrigi-la.

> :information_source: **Observação**
>
> As versões do log4j entre a 2.9-beta9 e 2.14.1 possuem uma falha que permite a execução de código malicioso apenas com o print do comando dentro do log.


## Passo 1: Verificar a existência da versão vulnerável
Para realizar a verificação das aplicações com log4j:

1. Se você utiliza o Maven, execute o comando `mvn dependency:tree`. Se você utiliza o Gradle, execute `gradle dependencies`.
2. Verifique se existe alguma referência ao **log4j**.
3. Verifique se a versão encontrada está entre as versões 2.9-beta9 e 2.14.1.
4. Em caso afirmativo, siga para o próximo passo para atualizar a versão e corrigir a vulnerabilidade.

## Passo 2: Corrigir a vulnerabilidade
Para corrigir a vulnerabilidade é necessário que sejam realizadas as atualizações da versão log4j e da versão da imagem base java.

### Atualizar a versão do log4j
1. Se existir uma referência direta, atualize a versão da biblioteca no **POM.xml** para 2.17.1 ou superior.
2. Se existir uma referência indireta ao log4j, ou seja, se ela estiver dentro de outra biblioteca, atualize a biblioteca que faz uso do log4j.
3. Caso não seja possível atualizar a biblioteca que faz uso do log4j, utilize um dos métodos abaixo:
- Para uma versão igual ou superior a 2.10, configure a propriedade **log4j2.formatMsgNoLookups** ou a variável **LOG4J_FORMAT_MSG_NO_LOOKUPS** para **true** para amenizar a vulnerabilidade. 
- Para versões entre 2.7 e 2.14.1, modifique o PatternLayout para o conversor com o formato `%m{nolookups}` no lugar de `%m`.
- Para versões entre 2.0-beta9 e 2.10.0, remova a classe **JndiLookup** do classpath com o comando `zip -q -d log4j-core-*.jar org/apache/logging/log4j/core/lookup/JndiLookup.class`.
- Se nenhum dos métodos acima forem válidos, exclua a lib do log4j dentro da biblioteca e adicione a versão mais nova do log4j no seu projeto. Contudo, é necessário realizar testes na sua aplicação, pois esse tipo de solução pode gerar erros de **NoClassDefFoundError** ou **NoSuchMethodError**, principalmente quando a versão utilizada é muito mais antiga que a versão 2.17.1 do log4j. Para excluir utilize as  [tags de exclusion](https://maven.apache.org/guides/introduction/introduction-to-optional-and-excludes-dependencies.html#dependency-exclusions) do Maven.

> :information_source: **Observação** 
> 
> Se estiver usando o Kumuluzee, atualize a versão do kumuluzee-logs no properties do seu pom.xml para versao 1.4.5 com o comando `<kumuluzee-logs.version>1.4.5</kumuluzee-logs.version>`.


### Atualizar a versão da imagem base java
Atualize a versão do java presente no seu dockerfile para uma das [versões recomendadas](https://cloud.dev.intranet.bb.com.br/stacks-desenvolvimento), conforme o modelo:

```
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:XX.X.X
```

## Saiba mais
Para conhecer mais sobre a vulnerabilidade do log4j, acesse:
- [CVE](https://www.cve.org/CVERecord?id=CVE-2021-44228).
- [Cloudflare](https://blog.cloudflare.com/cve-2021-44228-log4j-rce-0-day-mitigation/).
- [Spring Boot](https://spring.io/blog/2021/12/10/log4j2-vulnerability-and-spring-boot).
- [National Vulnerability Database](https://nvd.nist.gov/vuln/detail/CVE-2021-44228).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Como_verificar_vulnerabilidade_log4j.md&internalidade=seguranca/Como_verificar_vulnerabilidade_log4j)
