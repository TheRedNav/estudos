> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Fazendo uso de Mocks no Quarkus
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/arquivados/mockQuarkus.md&amp;action_name=testes/arquivados/mockQuarkus)

Mocks são essenciais para execução dos testes, com eles é possivel substituir valores de classes ou retorno dos metodos para que os testes possam focar em partes especificas do codigo e criar cenarios especificos para os testes.

O quarkus ja possui libs para realização de testes, no caso o Junit 5 e Mockito, não sendo necessario nenhuma outra lib do junit ou mockito para realização dos testes

Na pagina de [guides do quarkus](https://quarkus.io/guides/getting-started-testing) temos uma explicação de como fazer os testes e como utilizar o mocks, mas aqui vamos dar exemplos dos usos mais comuns como banco de dados e curio.


## Mocks

Se você codifica utilizando alguma metodologia de testes(TDD, BDD), ou simplesmente deseja criar um teste para uma uma classe existente que possua dependências de outras classes para sua execução, pode ser que precise utilizar mocks para a construção de seus testes de unidade.

Ao testar a unidade de nosso programa, queremos focar simplesmente a sua funcionalidade e não de suas dependências(seja porque confiamos na sua implementação ou porque já foram testadasa de outra menira).

Para atingir nosso objetivo, devemos fornecer a esse teste, uma substituição destas dependências de uma maneira em que possamos controlar. Desse jeito, podemos forçar o retorno de valores, lançamento de exceções ou simplesmente a simplificação de métodos com um retorno fixo.

Essa substituição é chamada de Mock e fará com que seu teste não precise estar integrado(Podemos deixar isso para os testes de integração).

Um exemplo clássico de uso de mocks, pode ser visto quando sua classe faz alguma operação com um banco de dados antes de efetuar o cálculo propriamente dito. Nesse caso, se conectarmos diretamente ao banco, estaremos sujeitos a disponibilidade e integriadade do mesmo, coisa que em um ambiente de desenvolvimento raramente encontramos. Para isso podemos mockar a classe que faria a conexão com o banco de dados, forçando a mesma a entregar um objeto esperado.

```
Mockar ou não mockar?

Devemos ter cuidado ao mockar, pois nem tudo deve ser mockado. Muitas vezes esse teste deve ser feito realmente integrado, substituindo o teste de unidade. Isso deve ser analizado pelo desenvolvedor para que possa tomar a melhor decisão em cada caso.
```


### Utilizando @InjectMock

Primeiro ponto com relação a utilizar os mocks, é saber se sua classe de teste vai utilizar outras classes que vao ser fornecidas programaticamente ou pela execução da aplicação no modo teste.cução.

Quando usamos a anotação `@QuarkusTest` em uma classe, estamos indicando que queremos que a aplicação seja executada com toda sua estrutra para então fazer os testes.
Esse modo facilita os testes de integração dos componentes do sistema, pois o CDI vai criar todas as classes necessarias para execução da aplicação, contudo vale lembrar que o ambiente de testes é fechado sem qualquer acesso externo, seja banco de dados (exceto h2 que executa em memoria) ou outros recursos, como apis ou IIB.
Assim caso precise realizar o mock de classes que façam essa integração externa voce pode utilizar o `@InjectMock`
ao inves do `@Inject`, ele ja ira criar um mock da classe injetada e voce podera configurar o que cada metodo vai retornar para cada conjunto de parametros informados.

Vale lembrar que ao usar dessa forma voce pode fazer o mock de dependencias indiretas da sua classe sem se preocupar em fazer toda a cadeia de dependecias. Por exemplo se a classe a ser testada depende da classe A e essa por sua vez depende da classe B, você pode fazer o mock na classe de teste usando o `@InjectMock` para a classe B sem precisar mexer em nada na classe A.

Exemplo de como utilizar o  `@InjectMock` para consumo de operação na classe de teste :

Classe que desejo testar:

```java
package br.com.bb.dev.rest;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.Entrada;
import br.com.bb.dev.integration.ConsumidorCurio;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/consulta")
@RequestScoped
public class ConsultaFunci {

	@Inject
	@RestClient
	ConsumidorCurio consumidorCurio;

	@GET
	@Path("/{matricula}")
	@Produces(MediaType.TEXT_PLAIN)
	public Response consultaPorNumeroMatricula(@PathParam("matricula") int matricula) {

		var entrada = new Entrada();
		var req = new DadosRequisicaoConsultarDadosBasicosFuncionarioBB();
		entrada.setMatrInf(matricula);
		req.setEntrada(entrada);
		var nome = consumidorCurio.executarOp252416v1(req).getSaida().getNomeRet();

		return Response.status(Response.Status.OK).entity("Nome: "+nome).build();
	}
}
```

Classe de Teste

```java
package br.com.bb.dev.rest;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.Entrada;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.Saida;
import br.com.bb.dev.erros.curio.CurioConsumoException;
import br.com.bb.dev.erros.curio.CurioExceptionMapper;
import br.com.bb.dev.erros.model.MensagensErro;
import br.com.bb.dev.integration.ConsumidorCurio;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ConsultaFunciTest {

	@InjectMock
	@RestClient
	ConsumidorCurio consumidorCurio;

	@Test
	void consultar_matricula_com_sucesso(){
		var resposta =  new DadosRespostaConsultarDadosBasicosFuncionarioBB();
		var saida = new Saida();
		saida.setNomeRet("João da Silva");
		resposta.setSaida(saida);

		var entrada = new Entrada();
		var req = new DadosRequisicaoConsultarDadosBasicosFuncionarioBB();
		entrada.setMatrInf(1234567);
		req.setEntrada(entrada);

		Mockito.when(consumidorCurio.executarOp252416v1(Mockito.any(DadosRequisicaoConsultarDadosBasicosFuncionarioBB.class))).thenReturn(resposta);

		given()
				.when().get("/consulta/1234567")
				.then()
				.statusCode(200)
				.body(containsString("João da Silva") );
	}

	@Test
	void consultar_matricula_com_erro(){
		var entrada = new Entrada();
		var req = new DadosRequisicaoConsultarDadosBasicosFuncionarioBB();
		entrada.setMatrInf(1234567);
		req.setEntrada(entrada);

		Mockito.when(consumidorCurio.executarOp252416v1(Mockito.any(DadosRequisicaoConsultarDadosBasicosFuncionarioBB.class)))
				.thenThrow(new CurioConsumoException("999","Erro simulado de um consumo"));

		given()
				.when().get("/consulta/1111111")
				.then()
				.statusCode(500)
				.body(containsString("\"CURIO-ERRO-CODE\":\"999\""))
				.body(containsString("\"CURIO-ERRO-MENSAGEM\":\"Erro simulado de um consumo\""));
	}

}
```


### Utilizando @Mock do quarkus


Outra opção de mock é criar uma nova classe dentro do diretorio `src/test/java` e estender ou herdar da classe/interface que se deseja mockar e incluir a anotação `@Mock`, mas do pacote `io.quarkus.test.Mock`, é uma anotação que inclui a anotação `@Alternative` e `@Priority(1)`.

Usando essa anotação o CDI, que controla o ciclo de vida das instancias de suas classes, irá substituir a classe real por essa mockada na execução durante a fase de testes. 

Na documentação do [quarkus](https://quarkus.io/version/2.7/guides/getting-started-testing#cdi-alternative-mechanism) temos maiores detalhes sobre isso.

**Atenção**: Se essa classe for criada fora da pasta de testes seu projeto irá utilizar esse mock gerando erros no seu sistema.


Exemplo de como utilizar o `@Mock` para consumo de operação, testando a classe `ConsultaFunci` do exemplo anterior :

Criar uma classe, `ConsumidorCurioMock` que ira implementar da interface de consumo do Curio, `ConsumidorCurio`.

```java
package br.com.bb.dev.integration;


import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.Saida;
import br.com.bb.dev.erros.curio.CurioConsumoException;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Mock
@RestClient
public class ConsumidorCurioMock implements ConsumidorCurio  {

	@Override
	public DadosRespostaConsultarDadosBasicosFuncionarioBB executarOp252416v1(DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao) {

		// Exemplo de logica para consulta de Matricula para forcar um resultado esperado
		if (requisicao.getEntrada().getMatrInf() == 1234567){
			var resposta =  new DadosRespostaConsultarDadosBasicosFuncionarioBB();
			var saida = new Saida();
			saida.setNomeRet("João da Silva");
			resposta.setSaida(saida);

			return resposta;
		}
		// Caso não recebe o parametro acima irá lancar uma CurioConsumoException
		throw new CurioConsumoException("999","Erro simulado de um consumo");
	}

}
```

Exemplo de como ficaria o teste, sem o uso do Mockito do exemplo anterior:

```java
package br.com.bb.dev.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ConsultaFunciTest {


	@Test
	void consultar_matricula_com_sucesso(){

		given()
				.when().get("/consulta/1234567")
				.then()
				.statusCode(200)
				.body(containsString("João da Silva") );
	}

	@Test
	void consultar_matricula_com_erro(){

		given()
				.when().get("/consulta/1111111")
				.then()
				.statusCode(500)
				.body(containsString("\"CURIO-ERRO-CODE\":\"999\""))
				.body(containsString("\"CURIO-ERRO-MENSAGEM\":\"Erro simulado de um consumo\""));
	}

}
```


Para mais informações de como usar Mocks com mockito:

- [Documentação do Quarkus sobre Mock](https://quarkus.io/guides/getting-started-testing#mock-support)
- [Curso Alura](https://www.alura.com.br/conteudo/mocks-java-mockito)
- [Baeldung topico sobre Mockito](https://www.baeldung.com/tag/mockito/)
- [Mockito](https://site.mockito.org/)


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/mockQuarkus.md&internalidade=testes/mockQuarkus)
