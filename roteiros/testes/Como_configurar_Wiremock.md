> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_Wiremock.md&amp;action_name=testes/Como_configurar_Wiremock)

# Como configurar e usar o WireMock

Este roteiro ensina a configurar o [WireMock](https://wiremock.org/), uma ferramenta open-source amplamente utilizada para testes de APIs. O WireMock suporta várias linguagens e pode ser usado como biblioteca, wrapper de cliente ou servidor standalone. Aqui, usaremos o WireMock como biblioteca para testar o consumo de uma operação via Curió.

**Principais características do WireMock:**

- Stub de respostas HTTP com correspondência por URL, cabeçalhos e padrões de corpo.
- Verificação de requisições.
- Gravação/reprodução de stubs.
- Atrasos configuráveis nas respostas e injeção de falhas.
- Proxy condicional por requisição.
- Proxy de navegador para inspeção e substituição de requisições.
- Simulação de comportamento stateful.

## Requisitos 
- Um projeto Java + Quarkus gerado pelo [Brave](https://brave.dev.intranet.bb.com.br/novo-projeto/codigo-base).

> :information_source: **Observação** 
> 
> O nosso projeto de exemplo consome a operação **252416v1**.

## Passo 1: Instalar o WireMock

Para utilizar a biblioteca do WireMock, a dependência Maven é necessária.

1. Abra a raiz do projeto na sua IDE.
2. Localize o arquivo **pom.xml**.
3. Procure pela dependência e inclua o trecho a seguir:
```pom
<dependency>
    <groupId>org.wiremock</groupId>
    <artifactId>wiremock</artifactId>
    <scope>test</scope>
    <version>${wiremock.version}</version>
</dependency>
```
4. Procure pela propriedade e inclua a versão do WireMock a seguir:
```pom
  <properties>
    <wiremock.version>3.0.1</wiremock.version>
  </properties>
```

## Passo 2: Inicializar o WireMock

Para inicializar o WireMock é preciso criar uma instância do WireMockServer com a configuração da porta desejada.

1. Na sua IDE, vá para **src > test > java/br/com/bb/{suasigla}**.
2. Crie um pacote **utils** e, dentro dele, crie a classe **WiremockSetup.java**.
3. Vá para **src > test > java/br/com/bb/{suasigla}/WiremockSetup.java**.
4. Na criação da classe, faça com que ela implemente  a interface **QuarkusTestResourceLifecycleManager**.
5. Declare `WireMockServer wireMockServer;` para criar o objeto.
```java
public class WiremockSetup implements QuarkusTestResourceLifecycleManager {
  WireMockServer wireMockServer; 
}
```  
6. Crie o método **start** para inicializar o servidor.
7. Escolha uma porta que não esteja em uso para configurar o servidor. No nosso exemplo, utilizamos a porta 10000: 
```java
@Override
    public Map<String, String> start() {
        //Configuração dos Stubs
        wireMockServer = new WireMockServer(10000);
        wireMockServer.start();
        return Collections.singletonMap("curio-host/mp-rest/url", wireMockServer.baseUrl());
    }
```
8. Crie o método **stop** para interromper o servidor:
```java
    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
```

## Passo 3: Configurar Classe de Testes
1. Vá para **src > test > java/br/com/bb/{suasigla}**.
2. Dentro de **services**, crie a classe para testes do endpoint. Siga o mesmo padrão de nomenclatura da classe do seu endpoint, adicionando a palavra **Test** ao final do nome do arquivo.
3. Antes da declaração da classe, adicione as anotações `@QuarkusTest` e `@QuarkusTestResource(WiremockSetup.class)`. No nosso exemplo, a classe ficou assim:

```java
@QuarkusTest
@QuarkusTestResource(WiremockSetup.class)
class FuncionarioBBServiceTest {

}
```

> :information_source: **Observação** 
> 
> O nome da classe usado na anotação **@QuarkusTestResource** deve corresponder ao nome da classe criada no passo 2.

## Passo 4: Configurar stubs/mappings

Os stubs/mappings são usados para definir como o servidor responderá às solicitações específicas realizadas em seus endpoints. O retorno pode ser feito diretamente em forma de **String**, declarada dentro do método *withBody* ou pode ser feito por meio de um arquivo **JSON**.

### Método 1: String
Neste método, as configurações são realizadas diretamente no código Java. É recomendado para projetos com mudanças rápidas e frequentes nos testes.

1. No arquivo **WiremockSetup.java**, inclua as respostas simuladas necessárias. No nosso exemplo, configuramos um stub para uma requisição POST que retorna uma String:

```java
wireMockServer.start();

wireMockServer.stubFor(post(urlEqualTo("/op252416v1"))
    .willReturn(aResponse()
        .withHeader("Content-Type", "application/json")
        .withBody(
            "{\n" +
                " \"saida\": {\n" +
                "   \"nomeRet\": \"NomeRet-teste\",\n" +
                "   \"sitRet\": 204,\n" +
                "   \"prfLocaRet\": 203,\n" +
                "   \"edComisRet\": 201,\n" +
                "   \"nmComisRet\": \"NmComisRet-teste\",\n" +
                "   \"cdRfRet\": 202,\n" +
                "   \"cdRoRet\": \"codigoRO-teste\"\n" +
                " }, \n" +
                " \"controle\": {\n" +
                "   \"retCode\": 100,\n" +
                "   \"sqlcode\": 101,\n" +
                "   \"section\": \"section-teste\",\n" +
                "   \"sqlca\": \"sqlca-teste\",\n" +
                "   \"sqlcode2\": 102\n" +
                " }\n" +
                "}"
)));

return Collections.singletonMap("curio-host/mp-rest/url", wireMockServer.baseUrl());
```

### Método 2: JSON
Neste método, as configurações são definidas em um arquivo JSON, que é armazenado na pasta *resources* do projeto. É recomendado para melhor organização, manutenção e reutilização de código, especialmente em grandes projetos.

1. Vá para **src > test > resources**.
2. Crie uma pasta chamada **mappings**.
3. Dentro de **mappings**, crie um arquivo JSON e inclua os mappings desejados. Utilizando o exemplo acima, no JSON, o arquivo ficaria assim:
```json
{
  "request": {
    "method": "POST",
    "url": "/op252416v1"
  },
  "response": {
    "headers": {
      "Content-Type": "application/json"
    },
    "body": "{\n" +
            " \"saida\": {\n" +
            "   \"nomeRet\": \"NomeRet-teste\",\n" +
            "   \"sitRet\": 204,\n" +
            "   \"prfLocaRet\": 203,\n" +
            "   \"edComisRet\": 201,\n" +
            "   \"nmComisRet\": \"NmComisRet-teste\",\n" +
            "   \"cdRfRet\": 202,\n" +
            "   \"cdRoRet\": \"codigoRO-teste\"\n" +
            " }, \n" +
            " \"controle\": {\n" +
            "   \"retCode\": 100,\n" +
            "   \"sqlcode\": 101,\n" +
            "   \"section\": \"section-teste\",\n" +
            "   \"sqlca\": \"sqlca-teste\",\n" +
            "   \"sqlcode2\": 102\n" +
            " }\n" +
            "}"
  }
}
```

4. Retorne ao arquivo **WiremockSetup.java** e inclua o caminho do arquivo recém-criado, conforme o método escolhido:
```java
WireMockServer wireMockServer = new WireMockServer(wireMockConfig().usingFilesUnderClasspath("src/test/resources/mappings"));
wireMockServer.start();
```
5. Inclua todas as respostas simuladas necessárias para os seus testes.

## Passo 5: Testar as configurações

1. No arquivo da sua classe de testes, crie um método que valide o resultado da consulta. No exemplo, a nossa classe de testes é a **FuncionarioBBServiceTest.java**:

```java
    @Test
    void verificar_resultado_consulta() throws URISyntaxException, IOException, InterruptedException {
        var resultadoRestClient = funcionarioBBService.consultaDadosFuncionario();
        Assertions.assertEquals("NomeRet-teste", resultadoRestClient.getSaida().getNomeRet());
    }
```
2. Execute o comando `./run/run.sh`.
3. No terminal, analise o resultado verificando se os testes foram executados com sucesso.

> :bulb: **Dica** 
> 
> Assista à [Guilda sobre WireMock](https://s3.servicos.bb.com.br/pns1devdf001/GuildaCloud/Videos/G112testando123mockalemdoalcance.mp4) para aprender mais sobre o assunto!

**Tags:** #java #mock #wiremock #testes

## A Seguir
- Para ver os arquivos completos utilizados nesse roteiro, acesse as [referências](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/testes/referencias/roteiroWiremock).
- Para saber como realizar testes com o JUnit, acesse o roteiro [Testes com JUnit](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroJUnit/Testes_JUnit.md)

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_Wiremock.md&internalidade=testes/Como_configurar_Wiremock)
