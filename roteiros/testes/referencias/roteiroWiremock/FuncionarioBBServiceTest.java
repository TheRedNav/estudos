package br.com.bb.t99.services;

import io.quarkus.test.InjectMock;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.bb.t99.integration.InfoIntegracao;
import br.com.bb.t99.util.WiremockSetup;

import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URISyntaxException;

@QuarkusTest
@QuarkusTestResource(WiremockSetup.class)
class FuncionarioBBServiceTest {

    @Inject
    FuncionarioBBService funcionarioBBService;

    @InjectMock
    InfoIntegracao infoIntegracao;
    
    @BeforeEach
    public void setup() {
        Mockito.when(infoIntegracao.getCodigoInterfaceCanal()).thenReturn(999);
        Mockito.when(infoIntegracao.getTicketCanal()).thenReturn("Ticket");
        Mockito.when(infoIntegracao.getImplementacaoInterfaceCanal()).thenReturn(999);
    }

    @Test
    void verificar_resultado_consulta() throws URISyntaxException, IOException, InterruptedException {
        var resultadoRestClient = funcionarioBBService.consultaDadosFuncionario();
        System.out.println(resultadoRestClient.getSaida().getNomeRet());
        Assertions.assertEquals("NomeRet-teste", resultadoRestClient.getSaida().getNomeRet());
        
    }
}
