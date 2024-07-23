> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Redis Cache
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/arquivados/redis.md&amp;action_name=databases/arquivados/redis)

:exclamation: Atenção: Este conteúdo foi alterado para remover informações obsoletas e aceita contribuições.

*Redis é uma base de dados em rede open-source, que armazena chaves com durabilidade opcional.*


## Solicitar Redis via Oferta OAAS


### 1. Acesse a oferta OAAS para solicitar o Redis na categoria Banco de Dados.



![Redis está na categoria Banco de Dados](databases/arquivados/images/redis-1.png)

- **Link direto:** https://portal.nuvem.bb.com.br/catalogo/Bancos%20de%20Dados 


### 2. Preencher o formulário com os dados solicitados.

#### 2.1 Caso você obtenha a mensagem `Você não possui acesso a esta oferta`, solicite ao Gerente de Equipe.


![Redis está na categoria Banco de Dados](databases/arquivados/images/redis-1.png)


#### 2.2 Oferta vai retornar duas instâncias do Redis Cache.

![Redis retorno Oferta](databases/arquivados/images/redis-4.png)


- **Uma instância serve como replica, ou seja, não vai aceitar a inclusão de registros**


### 3. Conectando no Redis Cache com sua aplicação

Os servidores estão configurados no modo [Sentinel](https://redis.io/docs/management/sentinel/) do Redis. Verifique na documentação oficial como proceder com a sua linguagem de programação.


### 4 Problemas Comuns

- Caso encontre o problema citado na inclusão de registros, altere o servidor para gravação. Lembrando que dos 2 servidores Redis disponibilizados, um funciona como replica, vai aceitar apenas consultas.

```
Exception in thread "main" redis.clients.jedis.exceptions.JedisConnectionException: Could not get a resource from the pool
```

### 5 Exemplo de código em Quarkus
#### 5.1 Configuração recomendada.

```properties
# Configurações DEFAULT, opcionais a partir da versão 3.3.0 do QUARKUS 
quarkus.redis.master-name=mymaster
quarkus.redis.role=master
# Ativando o modo Sentinel
quarkus.redis.client-type=sentinel
# Múltiplos HOST do modo Sentinel
# A porta padrão do modo sentinal é 26379
# Na oferta do ambiente, os hosts serão diferentes, mas a porta se mantém a padrão
quarkus.redis.hosts=redis://localhost:26379,redis://localhost:26380,redis://localhost:26381
```
fonte: [issue 40189 do quarkus](https://github.com/quarkusio/quarkus/issues/40189)

#### 5.2 Como executar localmente?
- Subindo uma instância local de Redis-Sentinel apenas para fins de teste.

Arquivo docker-compose:
```yaml
version: "3.4"
services:
  quarkus-redis-exemplo:
    container_name: quarkus-redis-exemplo
    image: quarkus-redis-exemplo:latest
    build:
      context: ../
      dockerfile: ./Dockerfile
    environment:
      #Configuração REDIS
      - QUARKUS_REDIS_CLIENT_TYPE=SENTINEL
      #- QUARKUS_REDIS_HOSTS=redis://localhost:26379
      #- QUARKUS_REDIS_HOSTS=redis-sentinel1,redis-sentinel2,redis-sentinel3
    network_mode: host
    ports:
      - "8080:8080" # Server
  redis-worker:
    image: redis
    volumes:
      - "./.data:/data"
    ports:
    - "6379:6379"
  redis-replica:
    image: redis
    command: redis-server --replicaof redis-worker 6379
    links:
      - redis-worker
    volumes:
      - "./.data:/data"

  # Instância 1
  redis-sentinel1:
    build:
      context: ./redis-sentinel
    ports:
      - "26379:26379"
    links:
      - redis-worker

  # Instância 2
  redis-sentinel2:
    build:
      context: ./redis-sentinel
    ports:
      - "26380:26379"
    links:
      - redis-worker

  # Instância 3
  redis-sentinel3:
    build:
      context: ./redis-sentinel
    ports:
      - "26381:26379"
    links:
      - redis-worker

```
DockerFile do Redis
```Dockerfile
FROM redis

ENV SENTINEL_QUORUM 2
ENV SENTINEL_DOWN_AFTER 1000
ENV SENTINEL_FAILOVER 1000

RUN mkdir -p /redis

WORKDIR /redis

COPY sentinel.conf .
COPY sentinel-entrypoint.sh /usr/local/bin/

RUN chown redis:redis /redis/* && \
    chmod +x /usr/local/bin/sentinel-entrypoint.sh

EXPOSE 26379

ENTRYPOINT ["sentinel-entrypoint.sh"]

```
Arquivo sentinel.conf

```conf
port 26379

dir /tmp

sentinel resolve-hostnames yes
sentinel monitor mymaster redis-worker 6379 $SENTINEL_QUORUM
sentinel down-after-milliseconds mymaster $SENTINEL_DOWN_AFTER
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster $SENTINEL_FAILOVER

```
Arquivo sentinel-entrypoint.sh
```sh
#!/bin/sh

sed -i "s/\$SENTINEL_QUORUM/$SENTINEL_QUORUM/g" /redis/sentinel.conf
sed -i "s/\$SENTINEL_DOWN_AFTER/$SENTINEL_DOWN_AFTER/g" /redis/sentinel.conf
sed -i "s/\$SENTINEL_FAILOVER/$SENTINEL_FAILOVER/g" /redis/sentinel.conf

redis-server /redis/sentinel.conf --sentinel
```

#### 5.3 Porque usar o modo Sentinel do Redis?

Esta é a lista completa das capacidades do Sentinel visto de um quadro geral:

Monitoramento. O Sentinel verifica constantemente se as instâncias mestre e de réplica estão funcionando conforme o esperado.
- Notificação: O Sentinel pode notificar o administrador do sistema ou outros programas de computador, por meio de uma API, de que algo está errado com uma das instâncias monitoradas do Redis.
- Failover automático. Se um mestre não estiver funcionando conforme o esperado, o Sentinel pode iniciar um processo de failover onde uma réplica é promovida a mestre, as outras réplicas adicionais são reconfiguradas para usar o novo mestre e os aplicativos que usam o servidor Redis são informados sobre o novo endereço a ser usado. ao conectar.
- Provedor de configuração. O Sentinel atua como uma fonte de autoridade para a descoberta de serviços dos clientes: os clientes se conectam aos Sentinels para solicitar o endereço do mestre Redis atual responsável por um determinado serviço. Se ocorrer um failover, o Sentinels reportará o novo endereço

Redis Sentinel é um sistema distribuído tolerante à falhas:

O próprio Sentinel foi projetado para ser executado em uma configuração onde há vários processos do Sentinel cooperando entre si. 
As vantagens de ter vários processos do Sentinel cooperando são as seguintes:

A detecção de falhas é realizada quando vários Sentinelas concordam que um determinado "mestres" não está mais disponível. Isso reduz a probabilidade de falsos positivos.

O Sentinel funciona mesmo que nem todos os processos do Sentinel estejam funcionando, tornando o sistema robusto contra falhas. Afinal, não há vantagem em ter um sistema de tolerância à falhas que, por si só, é um gargalo de falha.
A soma de Sentinels, instâncias Redis ("mestres" e réplicas) e clientes conectados ao Sentinel e Redis, também formam um sistema distribuído maior com propriedades específicas.
fonte: [documentação oficial do Redis](https://redis.io/docs/latest/operate/oss_and_stack/management/sentinel/)

#### 5.4 Exemplo de classe (Service) Java:
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
Fonte: [documentação oficial do quarkus](https://pt.quarkus.io/guides/redis)

#### 5.5 Retenção dos registros de cache:
Sendo o Redis um sistema que utiliza dados em memória, a (não expiração) dos registros irá resultar em uso elevado de recursos da namespace, nesse caso pode ser prudente definir uma estratégia de invalidação de registros OU incluir um prazo para a expiração dos registros no momento da inserção.

Fonte: [Documentação oficial do REDIS](https://redis.io/docs/latest/commands/expire)

**Tags:** #redis #cache #bancodedados #quarkus

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil?

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/redis.md&internalidade=databases/redis)

