> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_configurar_Redis_Sentinel.md&amp;action_name=databases/Como_configurar_Redis_Sentinel)

# Como configurar o Redis Sentinel

Este roteiro ensina a solicitar a oferta do Redis pelo Portal OAAS e a configurar o Quarkus para interagir com o Redis Sentinel. O Redis é um banco de dados em rede *open-source* que armazena chaves com durabilidade opcional. O Redis Sentinel, por sua vez, é uma parte integrante do Redis que oferece alta disponibilidade para sistemas Redis distribuídos.

**Características do modo Sentinel**
* **Monitoramento Contínuo:** o Sentinel verifica constantemente o status das instâncias mestre e de réplica do Redis para detectar problemas.
* **Notificação de Problemas:** ele pode notificar automaticamente sobre problemas por meio de uma API, mantendo a integridade do sistema.
* **Failover Automático:** em caso de falha do servidor mestre, o Sentinel inicia automaticamente um processo de failover e informa aos aplicativos o novo endereço a ser usado.
* **Provedor de Configuração:** os clientes se conectam aos Sentinels para obter o endereço do mestre Redis atual.
* **Tolerância a Falhas Distribuída:** o Sentinel foi projetado para operar em uma configuração distribuída, oferecendo detecção precisa de falhas e robustez do sistema contra falhas individuais.

## Requisitos
* Uma [instância local do Redis-Sentinel](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_subir_instancia_local_Redis_Sentinel.md) para fins de teste. 

## Passo 1: Solicitar o Redis via Portal OAAS

1. Acesse o [**Portal OAAS**](https://portal.nuvem.bb.com.br/).
2. No menu lateral esquerdo, vá para **Catálogo de Serviços**, identificado pelo ícone da caixa aberta, e navegue até a categoria **[Bancos de Dados](https://portal.nuvem.bb.com.br/catalogo/Bancos%20de%20Dados)**.
> :information_source: **Observação** 
> 
> Caso você receba a mensagem 'Você não possui acesso a esta oferta', solicite o acesso ao seu Gerente de Equipe. 
3. Clique em **Redis**.  
4. No campo **Informe um nome para a sua instância**, escreva um nome. Recomendamos seguir o padrão ‘ambiente-sigla-aplicação’. Ex.: hm-dev-metricas-cloud.
5. No menu suspenso, selecione o grupo do seu time.
> :bulb: **Dica**
>
> Cada grupo possui papéis LDAP específicos que definem o acesso às instâncias. Se o seu time ainda não tem grupo ou o grupo existente não atende às suas necessidades, você pode [criar um novo grupo](https://fontes.intranet.bb.com.br/sgh/publico/atendimento/-/wikis/Portal-OaaS/03-Inst%C3%A2ncias) e definir suas preferências.  
6. Preencha as seguintes informações:
    * **Nome da APP:** identifique a aplicação para controle de inventário e monitoração do serviço conforme o padrão 'SIGLA + NOME'. A sigla deve conter 3 caracteres, o nome deve conter até 5 caracteres (somente letras), seguido por dois números. Use letras maiúsculas. Ex.: BDHAPLIC01. 
    * **Ambiente:** selecione o ambiente onde o serviço será provisionado.
    * **Sigla:** selecione a sigla para onde provisionará o Redis. As siglas são carregadas a partir dos acessos à transação ALMFD.
7. Clique em **Criar**; a janela **Resumo do provisionamento** abrirá.
8. Após revisar as informações, clique em **Confirmar** para concluir a solicitação da oferta. Uma nova janela com a mensagem de ‘solicitação realizada com sucesso’ aparecerá na tela. Seu pedido entrará na fila e será aprovado por alguém da equipe responsável.

> :information_source: **Observação** 
> 
> Em caso de urgência, localize o ID da solicitação e informe-o à equipe de [Banco de Dados High-End](https://humanograma.intranet.bb.com.br/uor/338678) para agilizar a criação da oferta.

Após a aprovação, a instância retornará a lista de hosts provisionados e a senha será enviada para o seu e-mail. A oferta entrega três servidores, sendo um servidor principal e dois servidores réplicas. Um dos servidores réplica fica no site secundário para garantia de alta disponibilidade e distribuição de carga de leitura.

> :grey_exclamation: **Importante** 
> 
> As instâncias que servem como réplica não aceitam a inclusão de registros. 

## Passo 2: Criar o .env

1. Na pasta **raiz**, crie o arquivo **.env**.
2. Adicione a configuração a seguir para configurar os *hosts* através da variável de ambiente **REDIS_HOSTS**. Inclua os *hosts* provisionados, gerados pela oferta.

```env
REDIS_HOSTS=redis:endereco_host_1:porta,redis:endereco_host_2:porta,redis:endereco_host_3:porta
```

## Passo 3: Atualizar o application.properties

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo **application.properties**.
3. Localize a propriedade **quarkus.redis.hosts** e inclua a variável de ambiente **REDIS_HOSTS**.

```properties
quarkus.redis.master-name=mymaster
quarkus.redis.role=master
quarkus.redis.hosts=${REDIS_HOSTS}
```

## Passo 4: Atualizar o pom.xml

1. Na pasta **raiz**, localize o arquivo **pom.xml**.
2. Adicione a configuração a seguir para incluir a dependência **redis-client**.

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
     <artifactId>quarkus-redis-client</artifactId>
    </dependency>
```

Com essas alterações, você terá o Redis no modo Sentinel configurado no seu ambiente. Abaixo, segue um exemplo de utilização do Redis Sentinel. 

**Exemplo de classe (Service) Java**

```java
package br.com.bb.dev.service.redis;

import io.quarkus.redis.datasource.ReactiveRedisDataSource;
import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.keys.ReactiveKeyCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class IncrementService {

    private ReactiveKeyCommands<String> keyCommands;
    private ValueCommands<String, Long> countCommands;

    public IncrementService(RedisDataSource ds, ReactiveRedisDataSource reactive) {
        countCommands = ds.value(Long.class);
        keyCommands = reactive.key();

    }


    public long get(String key) {
        Long value = countCommands.get(key);
        if (value == null) {
            return 0L;
        }
        return value;
    }

    public void set(String key, Long value) {
        countCommands.set(key, value);
    }

    public void increment(String key, Long incrementBy) {
        countCommands.incrby(key, incrementBy);
    }

    public Uni<Void> del(String key) {
        return keyCommands.del(key)
                .replaceWithVoid();
    }

    public Uni<List<String>> keys() {
        return keyCommands.keys("*");
    }
}
```

> :bulb: **Dica** 
> 
> Para outras configurações, acesse o [Guia Quarkus - USANDO O CLIENTE REDIS](https://pt.quarkus.io/guides/redis).

> :warning: **Atenção** 
> 
> No Redis, a não expiração de registros pode causar uso excessivo de recursos da *namespace*. Defina uma estratégia de expiração ou invalidação dos registros para garantir eficiência. Acesse a [documentação oficial do REDIS](https://redis.io/docs/latest/commands/expire) para consultar a sintaxe dos comandos.

**Tags:** #redis #cache #bancodedados #quarkus #sentinel

## A Seguir
* Se necessário, consulte a página de resolução de problemas - [*troubleshooting*](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/troubleshooting.md).
  
## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil?

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_configurar_Redis_Sentinel.md&internalidade=databases/Como_configurar_Redis_Sentinel)
