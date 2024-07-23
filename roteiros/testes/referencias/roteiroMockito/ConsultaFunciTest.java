package br.com.bb.t99.rest;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.Saida;
import br.com.bb.dev.erros.core.curio.CurioConsumoException;
import br.com.bb.t99.integration.ConsumidorCurio;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;


import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;

@QuarkusTest
public class ConsultaFunciTest {

	@InjectMock
	@RestClient
	ConsumidorCurio consumidorCurio;

	@Test
	void consultarMatriculaComSucessoTest(){
		
		var saida = new Saida();
		saida.setNomeRet("João da Silva");
		
		var resposta =  new DadosRespostaConsultarDadosBasicosFuncionarioBB();
		resposta.setSaida(saida);

		Mockito.when(consumidorCurio.executarOp252416v1(Mockito.any(DadosRequisicaoConsultarDadosBasicosFuncionarioBB.class))).thenReturn(resposta);

		given()
			.when().get("/consulta/1234567")
			.then()
			.statusCode(200)
			.body(containsString("João da Silva") );
	}

	@Test
	void consultarMatriculaComErro(){

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