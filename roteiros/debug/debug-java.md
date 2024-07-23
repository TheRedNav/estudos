> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Debug no JAVA
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/debug/debug-java.md&amp;action_name=debug/debug-java)

ATENÇÃO! Este roteiro foi criado para debug de aplicações JAVA + QUARKUS (template padrão da NuvemBB), caso utilize JAVA com outros framework pode não funcionar.

## 1 Debug Remoto

Como executamos as aplicações via DOCKER é necessário utilizar a opção de debug remoto. As aplicações criadas utilizando o template da JAVA + QUARKUS estão configuradas para debug remoto na porta 5005.

### 1.1 Configuração de aplicações JAVA não geradas via template

Caso sua aplicação JAVA não tenha sido gerada via template basta adicionar os seguintes argumentos na execução do jar da sua aplicação no Dockerfile ou via linha de comando, último atributo do argumento indica a porta que será feita a comunicação, que pode variar de acordo com framework utilizado.

  ```sh
  ...
    java -jar -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:<PORTA> <MEU_APP>.jar
  ...
  ```

  Caso esteja utilizando docker-compose, basta incluir uma variável de ambiente com os argumentos abaixo.

  ```yaml
  ...
  environment:
    - JAVA_TOOL_OPTIONS=-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:<PORTA>
  ...
  ```

## 2. Configurando IDEs

### 2.1 VScode

1. Acessar o menu Run and Debug (barra lateral) > create a launch.json file (logo abaixo do botão Run and Debug), ele vai criar um arquivo chamado launch.json na pasta .vscode do projeto. Caso tenha dificuldade em localizar a opção crie a pasta .vscode e dentro dela o arquivo launch.json.

2. Abra o arquivo launch.json e susbstitua o conteúdo do arquivo para com o seguinte:

```json
{
  // Use IntelliSense to learn about possible attributes.
  // Hover to view descriptions of existing attributes.
  // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
  "version": "0.2.0",
  "configurations": [
    {
      "type": "java",
      "name": "Attach to Remote Program",
      "request": "attach",
      "hostName": "<The host name or ip address of remote debuggee>",
      "port": "<debug port of remote debuggee>"
    },
  ]
}
```
3. Certifique-se que a propriedade type seja java.

3. Altere a propriedade name conforme o nome utilizado em seu projeto.

4. No hostname informe ```"localhost"```.

5. Altere a propriedade port, para projetos criados a partir do template é 5005 para outros deve ser a mesma configurada na etapa anterior.

6. Para comecar a debugar pressione F5, ou clique no icone de Bug na lateral esquerda e depois em start. Nesse ponto pode haver erro de conexão caso esteje rodando o projeto de forma remota, utilizando Vagrant, WSL, etc., desa forma atente que o VSCode deve estar configurado para fazer ```port-forward``` para o debug, a forma mais simples de verificar é utilizando o comando ```CTRL + SHIFT + P``` e pesquisando por "Forward a port", será exibido uma tela com as portas, caso a porta do debug não apareça adicione a mesma, basta clicar em ```Forward a port``` ou ```Add a port```, e informe apenas o número.

7. Depois basta adicionar os breakpoints em seu projeto, para isso abra um arquivo do código do tipo java e clicar na linha que deseja, será exibida uma marcação na forma de um círculo vermelho.

### 2.2 Intellij

1. Para configurar o debug remoto no Intellij basta ir no menu Run > Edit Configurations.

2. Clique no sinal de ```+``` no canto superior esquerdo e selecionar a opção ```Remove JVM Debug```.

3. No campo ```name``` informe o nome do projeto.

4. Certifique-se que o campo ```host``` esteja preenchido com ```localhost```.

5. No campo ```port``` a porta conforme definida na passo 1.

6. Para iniciar o debug acesse a opção do menu Run > Debug e selecionar a configuração de debug.

7. Para adicionar ```breakpoint``` no código abra o arquivo java, clique na linha que deseja e será exibida uma marcação na forma de um círculo vermelho.

## 3. Debugando JVM usando JMX

O JMX é uma extensão que fornece uma forma de expor recursos Java, para monitorar e gerenciar sua aplicação. É possível observar dados interessantes como consumo de cpu, memória, etc. Essa ferramenta não deve ser habilitado em produção ou homologação, pois degrada a performance de tais ambientes.

Diferente do debug remoto que pode ser utilizado em tempo de desenvolvimento, esse recurso é melhor suportado com a aplicação executada de forma tradicional, através da linha de comando informando variáveis de ambiente, além de que a aplicação deve ser previamente compilada. Se estiver utilizando o template de aplicação JAVA + Quarkus da nuvemBB, isso pode ser feito através do comandos a seguir:

1.  ```mvn clean install```, serão gerados artefatos.

2. Verifique se o arquivo ```quarkus-run.jar``` na pasta ```target/quarkus-app/``` foi criado.

3. Execute o comando abaixo, a partir da pasta do projeto, vamos utilizar como padrão a porta 8999 para o jmx:

```sh
  java -jar -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8999 -Dcom.sun.management.jmxremote.rmi.port=8999 -Djava.rmi.server.hostname=localhost -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.local.only=false target/quarkus-app/quarkus-run.jar
```
Caso precise fazer o teste em desenv ou homologacao, coloque esses argumentos na env JAVA_OPTS e depois execute o comando de portfoward do kubectl conforme abaixo: 
```sh
  kubectl -n <nome-namespace> port-forward <nome-pod> 8999:8999
```
O nome namespace deve ser por exemplo, vip-fatura-pdf e o pod voce consegue pelo comando kubectl get pods -n <nome namespace>.

4. Depois baixe o Visualvm no [visualvm.github.io](https://visualvm.github.io/download.html), descompacte e execute o programa que fica na pasta bin. Caso esteja no linux execute com o comando ./visualvm através da linha de comando pelo terminal, ou abra o arquivo visualvm.exe caso esteja no Windows.

5. Para adicionar um monitoramento acessa opção File > Add JMX Configuration e preencha o campo connection com o host e a porta descrita na etapa anterior, ficando dessa forma ```localhost:8999```. CLique em OK.

6. Depois de configurado basta clicar no item com o mesmo nome da Connection que foi adicionada no passo anterior no menu do lado esquerdo, então voce podera acompanhar o consumo de cpu, memória, quantidade de threads, quantidade de classes instanciadas.

OBS.: Caso não esteja utilizando o template JAVA + Quarkus, compile sua aplicação e altere o caminho para o ```.jar``` de execução no comando citado no item 3 de acordo com o framework qe esteja utilizando.
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/debug/debug-java.md&internalidade=debug/debug-java)
