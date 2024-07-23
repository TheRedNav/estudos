package br.com.bb.t99.integracao;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.resposta.DadosRespostaConsultarDadosBasicosFuncionarioBB;
import br.com.bb.dev.erros.rest.filter.curio.CurioExceptionMapper;

@ApplicationScoped
@RegisterRestClient(configKey = "curio-api")
@Consumes(MediaType.APPLICATION_JSON + ";charset=utf-8")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
@RegisterProvider(CurioExceptionMapper.class)
public interface ConsumidorCurio {

    @POST
    @Path("op252416v1")
    @IntegracaoIIB
    DadosRespostaConsultarDadosBasicosFuncionarioBB executarOperacao(
    DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao);
  
  }

