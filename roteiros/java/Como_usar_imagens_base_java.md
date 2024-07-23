> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md&amp;action_name=java/Como_usar_imagens_base_java)

# Como usar imagens base para desenvolver aplicações Java

Este roteiro ensina a utilizar um Dockerfile para personalizar o ambiente de execução do contêiner Docker conforme as necessidades da sua aplicação. Um Dockerfile é um arquivo de texto que define as instruções para construir uma imagem de contêiner. O uso dessas imagens simplifica o ciclo de desenvolvimento, teste e implantação, oferecendo isolamento, reprodutibilidade e escalabilidade.

## Requisitos
* Uma [imagem base](https://fontes.intranet.bb.com.br/dev/imagens-docker/java). 

> :grey_exclamation: **Importante** 
> 
> * A escolha da imagem base deve ser feita conforme as necessidades individuais do seu projeto. Acesse o repositório e consulte as tags (versões) disponíveis. 
> * Recomendamos utilizar sempre a versão mais atualizada da imagem disponível. 
> * Todas as imagens são derivadas das [imagens UBI oficiais](https://www.redhat.com/en/blog/introducing-red-hat-universal-base-image), as quais são homologadas e têm suporte da Red Hat.

## Informações técnicas básicas

| Info | Valor | 
| - | - | 
| Arquitetura | amd64 | 
| Diretório de trabalho | /home/default | 
| Idioma e localidade | pt-BR | 
| Pasta de aplicações | /deployments | 
| Portas | 8080 - 8443 - 8778 | 
| Usuário default | 185 |

## Menu de ações 

* [Atualizar o Dockerfile](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md#atualizar-o-dockerfile)
* [Copiar executáveis para execução](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md#copiar-execut%C3%A1veis-para-execu%C3%A7%C3%A3o)
* [Gerenciar pacotes de software](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md#gerenciar-pacotes-de-software)
* [Incluir um certificado](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md#incluir-um-certificado)
* [Mudar propriedades de execução do processo Java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md#mudar-propriedades-de-execu%C3%A7%C3%A3o-do-processo-java)

## Atualizar o Dockerfile

Um Dockerfile atualizado garante que o contêiner seja construído com as configurações e dependências mais atualizadas e adequadas para o aplicativo ou serviço que estamos executando.

#### Quarkus 1.7.3.Final

A imagem **lnx-jre-alpine:11.0.3** era o padrão anterior dos projetos gerados pelo Brave / BBDev. Para atualizar seu Dockerfile para uma imagem mais recente do repositório:  

1. Troque o comando **FROM** antigo pelo comando atualizado. Lembre-se de que o exemplo fornecido pode estar desatualizado. Sempre verifique o repositório oficial das imagens para garantir que esteja utilizando a versão mais recente.
2. Nos comandos **COPY**, inclua a permissão para o usuário 185 e a nova localização da aplicação.

**Exemplo**

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.11.4

COPY --chown=185 target/lib/* /deployments/lib/
COPY --chown=185 target/*-runner.jar /deployments/app.jar

```
> :information_source: **Observação** 
> 
> Não é necessário informar um **ENTRYPOINT**. Por padrão, a imagem já possui um **ENTRYPOINT** que executa a aplicação Java localizada na pasta **/deployments**.

### Quarkus 1.12+

Se o projeto foi atualizado para uma versão mais recente do Quarkus que utiliza o formato Fast Jar como padrão (a partir da versão 1.12.0.Final), o Dockerfile também pode ser atualizado para uma imagem mais recente do repositório.

1. Troque o comando **FROM** antigo pelo comando atualizado. Lembre-se de que o exemplo fornecido pode estar desatualizado. Sempre verifique o repositório oficial das imagens para garantir que esteja utilizando a versão mais recente.
2. Nos comandos **COPY**, inclua a permissão para o usuário 185 e a nova localização da aplicação. Aqui, a divisão dos comandos de **COPY** facilita o reuso das camadas de build da imagem.

**Exemplo**

```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.11.4

COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/

```
### Quarkus 3

**Exemplo**
```dockerfile
FROM docker.binarios.intranet.bb.com.br/bb/dev/dev-java:17.11.4
 
COPY --chown=185 target/quarkus-app/*.jar /deployments/
COPY --chown=185 target/quarkus-app/lib /deployments/lib/
COPY --chown=185 target/quarkus-app/app /deployments/app/
COPY --chown=185 target/quarkus-app/quarkus /deployments/quarkus/
 
#descomente a linha abaixo APENAS para debug
# ENV JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=127.0.0.1:8000

#caso queira informar o ENTRYPOINT 
ENTRYPOINT ["java", "-jar", "/deployments/quarkus-run.jar"]
```

## Copiar executáveis para execução

Nas nossas imagens, o processo Java é executado pelo usuário padrão chamado **default**. No entanto, ao adicionar arquivos à imagem usando o comando **COPY** no Dockerfile, o padrão é que esses arquivos pertençam ao usuário **root**. Para garantir que os arquivos copiados mantenham as permissões corretas e pertençam ao usuário apropriado, é necessário utilizar a opção **--chown** do comando **COPY**. 

**Exemplo**
```dockerfile
COPY --chown=185 target/quarkus-app/*.jar /deployments/
```

## Gerenciar pacotes de software

O [microdnf](https://access.redhat.com/documentation/en-us/red_hat_enterprise_linux_atomic_host/7/html/getting_started_with_containers/using_red_hat_universal_base_images_standard_minimal_and_runtimes#adding_software_inside_the_minimal_ubi_container) é um gerenciador de pacotes feito para imagens UBI, da Red Hat. Com ele, desenvolvedores podem instalar, remover e atualizar pacotes dentro do contêiner, adaptando o ambiente sem sobrecarregar o contêiner com recursos desnecessários. O **microdnf** está configurado no repositório de pacotes Linux do Artifactory e pode ser usado para instalar softwares adicionais. Para realizar a instalação, siga os passos:

1. Troque para o usuário **root**.
2. Após o **RUN**, adicione os pacotes desejados. É possível instalar múltiplos pacotes simultaneamente. No nosso exemplo, serão instalados dois pacotes.
3. Utilize o comando **clean** para limpar os arquivos baixados que não são mais necessários. Lembre-se de executar o **clean** na mesma instrução **RUN** da instalação, caso contrário, a limpeza não ocorrerá corretamente.
4. Retorne ao usuário **default**.

> :grey_exclamation: **Importante** 
> 
> A flag **-y** indica confirmação automática e é obrigatória. Sua omissão causa erros devido à chamada incorreta à entrada do teclado durante o build.

**Exemplo**
```dockerfile
USER root
RUN microdnf install -y wget zip && microdnf clean all
USER default
```

## Incluir um certificado

Todas as imagens já incluem os certificados do Banco do Brasil, os quais são instalados e configurados na JVM. Isso é feito para evitar problemas e adicionar uma camada extra de segurança ao realizar requisições HTTPS para outras aplicações. No entanto, se você precisar adicionar um novo certificado, tanto no *cacerts* quanto no Sistema Operacional da imagem, é possível fazê-lo da seguinte forma:

1. Troque para o usuário **root** da imagem.
2. Realize a cópia do certificado para a imagem. No nosso exemplo, estamos copiando o conteúdo da pasta **$PATH_LOCAL/CERTIFICADO**. Você deve ajustar o **PATH_LOCAL** de acordo com o caminho de pastas do seu projeto.
3. Execute o script **update-ca-trust**.
4. Retorne ao usuário **default**.

**Exemplo**

```dockerfile
FROM docker.binarios.intranet.bb.com.br/ubi9/openjdk-17-runtime:1.17

#início comandos para cópia e instalação do certificado
USER root
COPY $PATH_LOCAL/CERTIFICADO /etc/pki/ca-trust/source/anchors/
RUN update-ca-trust
USER default
#fim comandos para cópia e instalação do certificado
```

## Mudar propriedades de execução do processo Java

O comando **JAVA_TOOL_OPTIONS** permite configurar opções de linha de comando que se aplicam a todas as instâncias da Java Virtual Machine (JVM) onde esta variável de ambiente está definida. Um exemplo de modificação do comportamento padrão é alterar o idioma e a região usados pela aplicação Java. 

No exemplo a seguir, estamos sobrescrevendo a configuração padrão de localidade das imagens, que é pt-BR, e definindo o idioma como inglês (en) e a região do usuário como Estados Unidos (US). As demais propriedades já setadas em **JAVA_TOOL_OPTIONS** são mantidas.

**Exemplo**
```dockerfile
ENV JAVA_TOOL_OPTIONS="-Duser.language=en -Duser.region=US ${JAVA_TOOL_OPTIONS}"
```
**Tags:** #java #dockerfile #imagem #base #quarkus #comandos

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/Como_usar_imagens_base_java.md&internalidade=java/Como_usar_imagens_base_java)
