package br.com.bb.t99.services;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.Entrada;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.t99.integration.ConsumidorCurio;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import br.com.bb.t99.integration.InfoIntegracao;

import java.io.IOException;
import java.net.URISyntaxException;

@RequestScoped
public class FuncionarioBBService {
    
    @Inject
	@RestClient
	ConsumidorCurio consumidorCurio;

	@Inject
	InfoIntegracao infoIntegracao;
    
    public DadosRespostaConsultarDadosBasicosFuncionarioBB consultaDadosFuncionario(){
        System.out.println(infoIntegracao.getCodigoInterfaceCanal());

		var req = new DadosRequisicaoConsultarDadosBasicosFuncionarioBB();
		req.setEntrada(new Entrada());
		req.getEntrada().setMatrInf(3015985);

		return consumidorCurio.executarOp252416v1(req);
    }

    public DadosRespostaConsultarDadosBasicosFuncionarioBB getDadosFunci(int matricula){
        var entrada = new Entrada();
		var req = new DadosRequisicaoConsultarDadosBasicosFuncionarioBB();
		entrada.setMatrInf(matricula);
		req.setEntrada(entrada);
		return consumidorCurio.executarOp252416v1(req);
    }

}
