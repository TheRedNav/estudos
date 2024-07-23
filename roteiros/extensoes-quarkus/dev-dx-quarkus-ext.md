> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Extensão Dev-DX-Quarkus
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/extensoes-quarkus/dev-dx-quarkus-ext.md&amp;action_name=extensoes-quarkus/dev-dx-quarkus-ext)

Para facilitar o desenvolvimento de aplicacoes no Arq3 para aplicações Java-Quarkus, criamos uma extensão que prove as funcionalidades mais comuns, como geração de metricas, log e tratamento de erros.

## Como utilizar

Para utilizar a extensão do quarkus construido pela equipe DX, basta incluir a dependencia no pom do seu projeto conforme abaixo:

```xml
  <dependencies>
    <dependency>
      <groupId>br.com.bb.dev</groupId>      
      <artifactId>dev-dx-quarkus-ext</artifactId>
      <version>0.1.9</version>
    </dependency>
    ...
  </dependencies>
```

## Funcionalidades

### Log

Na extensão Dev-DX , existe um filtro Jax Rs para todos os endpoints existentes na sua aplicação, tanto para requisição quanto para resposta, ele é responsavel por criar o primeiro log da requisição, essa primeira requisiçao gera um UUID unico para identificar a requisiçao, e no final temos o log da resposta, esses log inclui apenas informações basicas como o endpoint acessado, metodo e status code.

```
07:21:36 INFO  [br.co.bb.de.ex.lo.LogBBFilter] (executor-thread-3) b48061ce-92e3-4d7a-b105-efa67d7d3dc0 Requisicao {path=/hello, method=GET}

07:21:36 INFO  [br.co.bb.de.ex.lo.LogBBFilter] (executor-thread-3) b48061ce-92e3-4d7a-b105-efa67d7d3dc0 Resposta {path=/hello, method=GET, statusCode=200}
```

Para manter esse id da requisicao para os restante dos logs utilizados em seu projeto é necessario injetar o Logger da dependencia sl4j conforme o exemplo abaixo:

```java
import org.slf4j.Logger;
import javax.enterprise.context.RequestScoped;

@RequestScoped
public class ClasseExemplo{

  @Inject
  Logger log;

  public void meuMetodoExemplo(){
    log.info("Exemplo de log");
  }
}
```

Em versões anteriores utilizavamos uma anotação @LogBB para indicar os endpoints que desejavam utilizar essa funcionalidade, mas essa anotação foi descontinuada e não tera mais efeito em versoes futuras da extensão.

Caso não queria utilizar esse filtro no seu endpoint, voce pode incluir ele em uam propriedade chamada quarkus.dev.dx.log.paths.exclusions, os endpoints podem ser separados por virgula, e os paramentros de path devem ser indicados conforme configurados no @Path do endpoint, no exemplo abaixo, os endpoints '\cliente\{id}'
e 'clientes\telefone' serão excluidos da geração dos logs de requisição e resposta, no caso o {id} pode ser qualquer valor que representa um id.

```yaml
quarkus.dev.dx.log.paths.exclusions=\cliente\{id},\cliente\telefone
```

### Metricas

Na extensão existe uma anotação @MetricBB, ela gera metricas no padrão do microprofile, funciona de forma semelhante ao log, com a utilização de filtros para interceptar a requisicao e resposta para gerar metricas de tempo e contagem.
Contudo elas estao em processo de desuso em prol das metricas no [padrão B5](../metricas/metricasB5.md)

### Erros

A extensão implementa a estrutura descrita no roteiro de [erros](../erros/padraoJavaErros.md).

### Info

A extensão gera um endpoint padrão /info para indicar o nome da aplicação e versão em execução.

## Enviroments para configuração

Nome da Enviroment                  | Descrição                                        | Valor Padrão
----------------------------------- | ------------------------------------------------ | ---------
dev.dx.log.paths.exclusions         | Lista com endpoints para não realizaro log.      | ""
dev.dx.error-legacy-mode            | Exibir o padrão de erro no modo legado           | false
dev.dx.error-legacy-mode            | Exibir o padrão de erro no modo legado           | false
dev.dx.msg-error-code-default       | Valor padrão para a propriedade code no erro     | 999
dev.dx.msg-error-default            | Valor padrão para a propriedade mensagem         | Erro ao executar.---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/extensoes-quarkus/dev-dx-quarkus-ext.md&internalidade=extensoes-quarkus/dev-dx-quarkus-ext)
