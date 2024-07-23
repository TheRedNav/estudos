> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Curio.md&amp;action_name=frameworks/quarkus/quarkus_2/Como_atualizar_Curio) 

# Como atualizar o Curió com o Quarkus 2

Este roteiro ensina a atualizar o [Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio) para a versão 2 do Quarkus. O Curió é um contêiner Docker capaz de prover, consumir, fazer e escutar publicações de operações IIB do catálogo de operações via HTTP Rest para sua aplicação.

## Requisitos
* Concluir todas as configurações do roteiro [Como atualizar o Quarkus para a versão 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Quarkus_2.md).

## Passo 1: Atualizar o application.properties

Para usar as dependências das operações (consumo e provimento):

1. Abra a pasta **/src/main/resources** na sua IDE.
2. Localize o arquivo [application.properties](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).
3. Inclua a configuração, lembrando de substiuir a sigla da operação que deseja utilizar. Caso haja mais de uma operação, separe os pacotes com vírgula e sem espaço entre eles.

Exemplo: 
```properties
# Exclusão no CDI das classes presentes nos pacotes das dependencias de operação IIB
quarkus.arc.exclude-types=br.com.bb.dev.operacao.**,br.com.bb.aic.operacao.**
```

> :information_source: **Observação** 
> 
> O exemplo ignora alguns *beans* CDI que não são usados pelo Curió e podem causar conflito.

## Passo 2: Criar o arquivo .env

1. Abra a raiz do projeto na sua IDE.
2. Crie um novo arquivo [.env_curio](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md).

> :grey_exclamation: **Importante** 
> 
> Não esqueça de incluir o conteúdo deste arquivo no README.md do seu projeto, explicando as configurações que devem ser usadas. Caso esteja usando a propriedade CHA, lembre-se de detalhá-la também.

Exemplo de configuração mínima para o ambiente de desenvolvimento:

```env
KUMULUZEE_SERVER_HTTP_PORT=8081
CURIO_CACHE_CONFIGURACAO_IIB=iib-slave.redis.bdh.desenv.bb.com.br
CURIO_CACHE_CONFIGURACAO_IIB_ID=iib:configuracao:k8s-integracao
CURIO_SIGLA_APLICACAO=sua-sigla
CURIO_APLICACAO_HOST=http://localhost:8080
CURIO_IIB_LOG_LEVEL=FINE
CURIO_DRY_RUN=false
CURIO_MODO_DESENVOLVIMENTO=true
KUMULUZEE_LOGS_LOGGERS0_NAME=br.com.bb
KUMULUZEE_LOGS_LOGGERS0_LEVEL=TRACE
IDH_CHAVE_APLICACAO=
CURIO_OP_PROVEDOR=
CURIO_OP_CONSUMIDOR=
```
> :information_source: **Observação** 
> 
> * As propriedades **CURIO_OP_PROVEDOR** e **CURIO_OP_CONSUMIDOR** devem ser informadas conforme a necessidade de prover (disponibilizar uma operação) ou consumir (chamar uma operação que é provida por outra aplicação). A nomenclatura do valor utilizado deve seguir a [documentação do Curió](https://fontes.intranet.bb.com.br/iib/publico/iib-container/iib-curio/iib-curio#iniciando).
> * A propriedade **IDH_CHAVE_APLICACAO** é o identificador da aplicação no sistema IDH. Caso precise de mais informações, consulte a [Wiki IDH](https://fontes.intranet.bb.com.br/idh/publico/roteiros/-/wikis/home).
> * Caso esteja usando o Windows, altere a porta em **CURIO_HOST_MP_REST_URL** porque a porta 8081 é utilizada pelo Antivírus.

## Passo 3: Atualizar o docker-compose

1. Localize o arquivo [docker-compose](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md) na sua IDE. Geralmente ele está na pasta **run**.
2. Remova a tag **environment** do Curió.
3. Inclua a tag **env_file** com o arquivo **.env_curio**, conforme o exemplo abaixo, para garantir que a execução com o *docker run* e o *docker compose* utilizem as mesmas configurações.

```yaml
  # =============================================================================
  #   Sidecar CURIO Utilizado para realizar a comunicação entre IIB e aplicação
  # =============================================================================
  iib-curio:
    container_name: iib-curio
    image: docker.binarios.intranet.bb.com.br/bb/iib/iib-curio:0.8.1
    # as enviroments estarão presentes no arquivo .env_curio na raiz do projeto, você deve alterar lá as configurações
    env_file:
      - .././.env_curio
    # se estiver acessando no windows, alterar abaixo as portas 8081 e também na variável no .env_curio KUMULUZEE_SERVER_HTTP_PORT para 8091, por exemplo
    # pois a porta 8081 no windows pode já estar ocupada por outros serviços, como o antivírus
    ports:
      - "8081:8081"
    network_mode: host
```

> :information_source: **Observação** 
> 
> Se o seu *docker compose* já tem uma rede configurada, você deve mudar o **network_mode** para **networks** e colocar o nome da rede do *docker compose* na linha logo abaixo, seguindo a identação. 

> :grey_exclamation: **Importante** 
> 
> Para o Quarkus 2.0, a extensão **dev-dx-quarkus** foi descontinuada. Para fazer o tratamento de erros para operações Curió no mesmo formato, sugerimos o uso da dependência [dev-java-erro](https://fontes.intranet.bb.com.br/dev/dev-java-erro).

Com a conclusão do roteiro de atualização do Curió, você pode usar as melhorias e recursos oferecidos pela nova versão do Quarkus para aprimorar seu projeto.

**Tags:** #quarkus #atualizar #versao2 #curio

## A Seguir

* Leia o roteito [Como atualizar a dependência do Banco de Dados - Quarkus 2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_dependencia_BD.md) para atualizar a dependência do Banco de Dados após a atualização do seu projeto para a versão 2 do Quarkus. 
* Se necessário, consulte a página de resolução de problemas - [*troubleshooting*](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/troubleshooting.md).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/quarkus_2/Como_atualizar_Curio.md&internalidade=frameworks/quarkus/quarkus_2/Como_atualizar_Curio)

