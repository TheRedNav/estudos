> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/configuracao_log_file.md&amp;action_name=frameworks/quarkus/configuracao_log_file.md)

# Gravando log da aplicação em um arquivo dentro de um volume do kubernetes

Este roteiro descreve como configurar para que a aplicação salve o log em um arquivo no volume do kubernetes, e também como acessar esse volume e copiar o arquivo gerado para a maquina local.

As configurações feitas são do tipo `runtime` , sendo assim não e necessário realizar um novo build da aplicação.

Índice
- [Importante](#importante)
- [Motivação](#motivação)
- [Log File](#oracle)
- [Log usabilidade](#log-usabilidade)
- [Log console](#log-console)
- [Configuração log file](#configuração-log-file-1)
- [Local salvo](#local-salvo)
- [Volume](#volume)
- [Listar arquivos e diretórios do volume](#listar-arquivos-e-diretórios-do-volume)
- [Copiar arquivo do volume](#copiar-arquivo-do-volume)
- [Mais informações](#mais-informações)

## Importante

Este roteiro foi desenvolvido utilizando o quarkus 2.7, para as outras verões também e possivel realizar essa configuração. Para mais informações acesse [CONFIGURING LOGGING](https://quarkus.io/guides/logging) e selecione a versão.

## Motivação

No funcionamento da Arq3, ate o momento que esse roteiro foi criado, o log das pods era perdido quando uma nova pod subia, ou quando a mesma sofria restart. Assim não era possível acompanhar oque havia acontecido com a aplicação.
Para resolver esse problema foi feia a poc para salvar o log em arquivo.

## Log File

Log File é um tipo de arquivo que pode ser utilizado de diversas maneiras para acompanhar todas as ações executadas em softwares ou sistemas operacionais.

Com base nos registros gerados em arquivos de log, é possível identificar quais eventos ocorreram no ambiente e, dessa forma, entender as causas de eventuais erros ou falhas.

Esses arquivos também são importantes para acompanhar o que acontece em uma aplicação, pois existem diversas situações que devem ser monitoradas para garantir o perfeito funcionamento da página, além de resolver os problemas com mais agilidade.


## Log usabilidade

Adicionar logs na aplicação e uma boa pratica. através dos logs podemos monitorar o que esta acontecendo em nossa aplicação. Abaixo a um exemplo de log do tipo INFO.

```Java
import org.jboss.logging.Logger;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


@Path("/hello")
@RequestScoped
public class HelloWorldResources {

    private static final Logger LOG = Logger.getLogger(HelloWorldResources.class);

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response hello() {
        LOG.info("Hello");
        return Response.status(Response.Status.OK).entity("Hello!").build();
    }
}
```

Existem outros tipos de log, utilizados pelo Quarkus para mais informações acesse [Logging levels](https://quarkus.io/guides/logging#logging-levels).

Ao acessar o endpoint acima  no console e possível ver o *Hello* na saída:

Imagem console


## Log console

O log de console e algo que temos iteração a todo momento quando desenvolvemos uma aplicação.
O texto *Hello* que apareceu no console ao acessar o endpoint foi configurado através do arquivo `application.properties`.
Há duas configurações importantes para o log que aparece no console:

* `quarkus.log.console.format`: Formata as mensagem do log.
* `quarkus.log.console.level`: Define nível de log exibido no console.

Abaixo a configuração das variáveis utilizadas para a poc:

```properties
# Configure console log.
# Format log messages to have shorter time and shorter category prefixes.
quarkus.log.console.format=%d{HH:mm:ss} %-5p [%c{2.}] (%t) %X{requestID} %s%e%n
# Enable console DEBUG logging with the exception of Quarkus logs that have a logging level set to INFO.
quarkus.log.console.level=DEBUG
```
Para mais informações sobre configurações [Console logging](https://quarkus.io/guides/logging#quarkus-log-logging-log-config_quarkus.log.console-console-logging).

## Configuração log file

Para que o mesmo log exibido no console seja salvo em um arquivo, e necessário fazer algumas configurações ainda no `application.properties`, sendo elas:
* `quarkus.log.file.enable`: Ativar registro de arquivo.
* `quarkus.log.file.path` : Define o caminho onde o arquivo de log vai ser salvo.
* `quarkus.log.file.level`: Define nível de log exibido no console.
* `quarkus.log.file.format`: Formata as mensagem do log.

Abaixo a configuração das variáveis utilizadas para a poc:

```properties
# Enable file logging and set a path to the log file.
quarkus.log.file.enable=true
quarkus.log.file.path=/logs/gpfestudolog/trace.log
# Enable INFO log messages in a log file.
quarkus.log.file.level=INFO
# Set a format for the log file output.
quarkus.log.file.format=%d{yyyy-MM-dd HH:mm:ss} %-5p [%c{2.}] (%t) %X{requestID} %s%e%n
# Configure rotation.
# The maximum file size of the log file after which a rotation is executed.
quarkus.log.file.rotation.max-file-size=10M
# The maximum number of backups to keep.
quarkus.log.file.rotation.max-backup-index=10
#File handler rotation file suffix. When used, the file will be rotated based on its suffix. Example fileSuffix: .yyyy-MM-dd
quarkus.log.file.rotation.file-suffix=.gz
# Indicates whether to rotate log files on server initialization. You need to either set a max-file-size or configure a file-suffix for it to work.
quarkus.log.file.rotation.rotate-on-boot=true
```

As configurações de rotation, definem o nome do arquivo, tamanho e suffixo do mesmo além da configuração de rotação.

Para mais informações sobre configurações [File logging](https://quarkus.io/guides/logging#quarkus-log-logging-log-config_quarkus.log.file-file-logging).


## Local salvo

Quando a aplicação sobre o arquivo com o log e gerado no caminho `/logs/gpfestudolog`, com o nome de `trace.log` como foi definido nas configurações. Porem ao restart a pod ou subir uma nova versão perdemos o arquivo gerado.
Para resolver esse problema foi criado um volume. 

## Volume

Para o problema mencionado acima, foi criado um `persistenceVolume` com as seguintes configurações:

```yaml
  persistenceVolume:
    enable: true
    persistentVolumeClaim: "pvc-estudo-logs"
    storageClass: "nas-client"
    storageSize: "1Gi"
```

Outra configuração foi para copiar os arquivos gerados na pasta `/logs/gpfestudolog`. 

```yaml
  deployment:
    ...
    containers:
      ...
      volumeMounts:
        - mountPath: "/logs/gpfestudolog"
          name: "logs-estudo"
    volumes:
      - name: "logs-estudo"
        persistentVolumeClaim:
          claimName: "pvc-estudo-logs"
```

A configuração abaixo mapeia a pasta `/logs/gpfestudolog` e copia os arquivos para o volume, assim caso a pod restart ou uma nova pod suba, o arquivo não e perdido.

## Listar arquivos e diretórios do volume

Primeiro e necessário abrir bash no pod.

`kubectl  exec -it <some-pod> -n <some-namespace> -c  <some-container> bash`

- [Kubernetes Persistent Volumes: How to List and Copy Files and Directories](https://blog.pilosus.org/posts/2019/05/24/k8s-volumes-list-copy/)

## Copiar arquivo do volume

Com a utilização do log em arquivo uma das necessidades e realizar a cópia do arquivo para a máquina local. Abaixo há um exemplo de como resolver essa necessidade. 

Copie o conteudo /tmp/foo de um pod remoto para /tmp/bar localmente:

```
kubectl cp <some-namespace>/<some-pod>:/tmp/foo /tmp/bar 
```

Opção utilizando container:

```
kubectl cp <some-namespace>/<some-pod>:/tmp/foo -c <container-name> /tmp/bar 
```
 

## Mais informações

- [Configuring logging with Quarkus](https://access.redhat.com/documentation/en-us/red_hat_build_of_quarkus/1.11/html-single/configuring_logging_with_quarkus/index)


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/configuracao_log_file.md&internalidade=frameworks/quarkus/configuracao_log_file)
