package br.com.bb.t99.integration;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.Saida;
import br.com.bb.dev.erros.core.curio.CurioConsumoException;
import io.quarkus.test.Mock;
import org.eclipse.microprofile.rest.client.inject.RestClient;

// @Mock
// @RestClient
public class ConsumidorCurioMock { //implements ConsumidorCurio  {

	// @Override
	public DadosRespostaConsultarDadosBasicosFuncionarioBB executarOp252416v1(DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao) {

		// Exemplo de logica para consulta de Matricula para forcar um resultado esperado
		if (requisicao.getEntrada().getMatrInf() == 3015985){
			var resposta =  new DadosRespostaConsultarDadosBasicosFuncionarioBB();
			var saida = new Saida();
			saida.setNomeRet("NomeRet-teste");
			resposta.setSaida(saida);

			return resposta;
		}
		// Caso não recebe o parametro acima irá lancar uma CurioConsumoException
		throw new CurioConsumoException("999","Erro simulado de um consumo");
	}

}
