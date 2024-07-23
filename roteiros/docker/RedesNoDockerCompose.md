> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

## Configuração da rede no docker-compose 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/docker/RedesNoDockerCompose.md&amp;action_name=docker/RedesNoDockerCompose)

O arquivo *docker-compose.yaml* dos projetos gerados pelo BBDev vem setado para subir o projeto na seguinte rede: 

```yaml
networks:
  seu-projeto-net:
    driver: bridge
    ipam:
      config:
        - subnet: 192.168.203.192/27
```

Com esta configuração, os seus containers subirão cada um com um IP entre `192.168.203.193` e `192.168.203.222` (Você poderá portanto subir 29 containers no mesmo docker-compose). 

Caso esteja numa estação de sistema operacional do tipo Linux, o parâmetro BIP deve, preferencialmente estar setado para o seguinte endereço: `192.168.203.129/27`. Caso contrário, poderá ocorrer conflito de rede. Caso você esteja utilizando Suse, verifique o BIP no arquivo `/etc/docker/daemon.json` e, caso esteja diferente, utilize o aplicativo "Configura Docker" para ajustar as configurações. No Ubuntu, edite manualmente o BIP no arquivo `/etc/docker/daemon.json`

Caso esteja numa estação com Windows, o BIP a ser informado é a própria rede, no caso, `192.168.203.128/27` e não `192.168.203.129/27`.

Caso você já tenha subido outro microservico na rede `192.168.203.192/27`, você pode deve alterar o docker-compose para utilizar um dos enderecos a seguir: 

 * `192.168.203.160/27`
 * `192.168.203.224/27` (Apenas caso você não esteja utilizando [Swarm](https://docs.docker.com/engine/swarm/)) 

 Neste caso não se esqueça de mudar os mapeamentos das portas para o localhost para que não haja conflito, conforme exemplo abaixo: 

Microservico 1: Temos um container que sobe na porta 8080. Queremos que ele seja mapeado para a porta 8080 do nosso localhost, para podermos testá-lo:  

```yaml 
  ports:
  - "8080:8080" (porta 8080 do localhost vai mapear a 8080 do microservico)
```
Microservico 2: Temos um container que sobe na porta 8080. Queremos que ele seja mapeado para a porta 8081 do nosso localhost, para podermos testá-lo:  

``` yaml 
  ports:
  - "8081:8080" (porta 8081 do localhost vai mapear a 8080 do microservico) 
```

## Verificando redes do docker 

Você pode verificar que redes estão rodando no seu docker com o comando abaixo: 

```bash
docker network ls
```

Exemplo: 

```
laecio:~/Documentos/RepositoriosGit/roteiros$ docker network ls 
NETWORK ID          NAME                  DRIVER              SCOPE
72160f59c395        bridge                bridge              local
f3f23d2276f2        host                  host                local
7191eb1635cb        none                  null                local
9201a0216275        run_dev-rede-01-net   bridge              local
```

Você pode verificar as configurações de uma rede com o seguinte comando:

```bash
docker network inspect 9201a216275
```

no qual `9201a0216275` é o id da rede, obtido no passo anterior, que queremos verificar a configuração.

Caso você tenha encerrado o seu microserviço utilizando Ctr-C, os containers são derrubados porém a rede permanece ativa, o que pode causar conflito caso você precise subir outro microserviço nesta mesma rede. Uma opção é entrar na pasta onde se encontra o arquivo docker-compose.yaml e executar o comando abaixo: 

```bash
docker-compose down 
```

Outra opção é executar o comando abaixo, utilizando o nome da rede capturado com o comando anterior: 

```bash
docker network remove run_dev-rede-01-net
```---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/docker/RedesNoDockerCompose.md&internalidade=docker/RedesNoDockerCompose)
