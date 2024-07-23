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

    //exemplo de repasse das informações de canal por meio da criação de um novo InfoCNL
    @POST
    @Path("op252416v1")
    @IntegracaoIIB
    @ClientHeaderParam(name="INFO-CNL", value="{geraTokenJwt}")
    DadosRespostaConsultarDadosBasicosFuncionarioBB executarOperacaoNovoInfoCnl(DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao);
  
    default String geraTokenJwt(String headerName) {
      if ("INFO-CNL".equals(headerName)) {
          return JWT.create()
            // Interface do canal de Atendimento: 0068 API-PROGRAMAçãO DE APLIC
            .withClaim("interfaceCanal", 68)
            // Implementação da Interface: 0029 API0001
            .withClaim("implementacaoInterfaceCanal", 29)
            .withClaim("idiomaCanal", 2 )
            .withClaim("ticketCanal", "")
            .sign(Algorithm.none());          
      }
      throw new UnsupportedOperationException("Nome do Header desconhecido");
    }

    //exemplo de repasse das informações de canal por meio de um InfoCNL existente
    @POST
    @Path("op252416v1")
    @IntegracaoIIB
    DadosRespostaConsultarDadosBasicosFuncionarioBB executarOperacaoRepassarInfoCnl(@HeaderParam("INFO-CNL") String headerInfoCnl, 
                                                                                    DadosRequisicaoConsultarDadosBasicosFuncionarioBB requisicao);

  }

