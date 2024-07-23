package br.com.bb.t99.integracao;

import javax.enterprise.context.RequestScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@RequestScoped
public class GeradorHeaderHeimdall {

  private static final Logger LOG = LoggerFactory.getLogger(GeradorHeaderHeimdall.class.getName());
  private static final String IDENTIFICADOR_HEADER_INFO_CNL = "INFO-CNL";

  @Inject
  InfoIntegracao infoIntegracao;

  public String geraTokenJwtInfoIntegracao() {
    return geraTokenJwt(IDENTIFICADOR_HEADER_INFO_CNL, infoIntegracao);
  }

  public String geraTokenJwtInfoIntegracao(InfoIntegracao dados) {
    return geraTokenJwt(IDENTIFICADOR_HEADER_INFO_CNL, dados);
  }

  private String geraTokenJwt(String headerName, InfoIntegracao dados) {
        if (IDENTIFICADOR_HEADER_INFO_CNL.equals(headerName)) {
          String jwt = null;
          if(isValido(dados)) {
            jwt = JWT.create()
              .withClaim("interfaceCanal", dados.getInterfaceCanal())
              .withClaim("implementacaoInterfaceCanal", dados.getImplementacaoInterfaceCanal())
              .withClaim("idiomaCanal", 2 )
              .withClaim("ticketCanal", dados.getTicketCanal())
              .sign(Algorithm.none());
          } else {
            jwt = JWT.create()
              .withClaim("interfaceCanal", InfoIntegracao.INTERFACE_CANAL_API)
              .withClaim("implementacaoInterfaceCanal", InfoIntegracao.INTERFACE_API_IMPL)
              .withClaim("idiomaCanal", 2 )
              .withClaim("ticketCanal", "")
              .sign(Algorithm.none());
          }
          return jwt;
        }
        throw new UnsupportedOperationException("Nome do Header desconhecido");
    }

  private boolean isValido(InfoIntegracao dados) {
    return dados != null && 
      dados.getImplementacaoInterfaceCanal() > 0 &&
      dados.getInterfaceCanal() > 0;
  }
}
