> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_subir_instancia_local_Redis_Sentinel.md&amp;action_name=databases/Como_subir_instancia_local_Redis_Sentinel)

# Como subir localmente instâncias do Redis Sentinel

Este roteiro ensina a subir localmente instâncias do Redis Sentinel. Ter instâncias disponíveis para simular o ambiente permite que você teste a integração da sua aplicação com o Redis e o modo Sentinel para verificar o funcionamento correto antes de implantar no ambiente desejado.

## Requisitos

* Um projeto Java Quarkus gerado.

## Passo 1: Atualizar o docker-compose

1. Localize o arquivo **docker-compose** na sua IDE. Geralmente ele está na pasta **run**.
2. Adicione as configurações a seguir para criar um ambiente com uma aplicação Quarkus que interage com um cluster do Redis, consistindo em uma instância mestre, uma réplica e várias instâncias do Redis Sentinel para alta disponibilidade.

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
      - QUARKUS_REDIS_HOSTS=redis-sentinel1,redis-sentinel2,redis-sentinel3
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
## Passo 2: Criar o diretório .data 

Crie o diretório **.data** dentro da pasta **run**. Isso pode ser feito usando o comando de criação de diretórios adequado para o sistema operacional em questão.

## Passo 3: Criar o diretório redis-sentinel
Crie o diretório **redis-sentinel** dentro da pasta **run**. Isso pode ser feito usando o comando de criação de diretórios adequado para o sistema operacional em questão.

## Passo 4: Criar o Dockerfile 

1. Dentro de **redis-sentinel**, crie o arquivo **Dockerfile**.
2. Adicione as configurações a seguir para construir uma imagem Docker baseada na imagem oficial do Redis. 
 
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
## Passo 5: Configurar o sentinel.conf

1. Dentro do diretório **redis-sentinel**, crie o arquivo **sentinel.conf**. 
2. Adicione as configurações a seguir para definir parâmetros como a porta, a habilitação de persistência de dados, a configuração de autenticação, entre outros.

```conf
port 26379

dir /tmp

sentinel resolve-hostnames yes
sentinel monitor mymaster redis-worker 6379 $SENTINEL_QUORUM
sentinel down-after-milliseconds mymaster $SENTINEL_DOWN_AFTER
sentinel parallel-syncs mymaster 1
sentinel failover-timeout mymaster $SENTINEL_FAILOVER

```
## Passo 6: Configurar o sentinel-entrypoint.sh

1. Dentro do diretório **redis-sentinel**, crie o *shell* **sentinel-entrypoint.sh**.
2. Adicione as configurações a seguir para automatizar a configuração do Redis Sentinel, permitindo que valores específicos sejam injetados dinamicamente no arquivo de configuração com base em variáveis de ambiente definidas externamente.

```sh
#!/bin/sh

sed -i "s/\$SENTINEL_QUORUM/$SENTINEL_QUORUM/g" /redis/sentinel.conf
sed -i "s/\$SENTINEL_DOWN_AFTER/$SENTINEL_DOWN_AFTER/g" /redis/sentinel.conf
sed -i "s/\$SENTINEL_FAILOVER/$SENTINEL_FAILOVER/g" /redis/sentinel.conf

redis-server /redis/sentinel.conf --sentinel
```

## Passo 7: Atualizar o application.properties

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo **application.properties**.
3. Adicione as configurações a seguir para configurar o cliente Redis no Quarkus para usar o modo Sentinel e conectar-se aos hosts do Redis Sentinel disponíveis.

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
Com a conclusão dessas configurações, você terá uma instância mestre, uma réplica e várias instâncias do Redis Sentinel para desenvolver e depurar suas funcionalidades com mais eficiência. 

**Tags:** #bancodedados #quarkus #redis #sentinel #dockercompose #cache

## A Seguir
* Leia o roteiro [Como configurar o Redis Sentinel](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_configurar_Redis_Sentinel.md) para aprender a solicitar a oferta do Redis pelo Portal OAAS e a configurar o modo Sentinel no seu ambiente.
* Se necessário, consulte a página de resolução de problemas - [*troubleshooting*](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/troubleshooting.md).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/databases/Como_subir_instancia_local_Redis_Sentinel.md&internalidade=databases/Como_subir_instancia_local_Redis_Sentinel)
