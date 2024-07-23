package br.com.bb.t99.rest;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.bb.arh.operacao.consultarDadosBasicosFuncionarioBBV1.bean.requisicao.DadosRequisicaoConsultarDadosBasicosFuncionarioBB;
import br.com.bb.t99.integracao.ConsumidorCurio;
import br.com.bb.t99.integracao.IntegracaoIIB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@RequestScoped
@Path("consumidor")
public class ConsumidorResource {
    @Inject
    @RestClient
    ConsumidorCurio sidecarConsumidor;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @IntegracaoIIB
    public Response operacao(DadosRequisicaoConsultarDadosBasicosFuncionarioBB dados) {
        return Response.status(Response.Status.OK).entity(sidecarConsumidor.executarOperacao(dados)).build();     
    }
}
