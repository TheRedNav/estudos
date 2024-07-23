package br.com.bb.t99.util;

import static com.github. tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import com.github.tomakehurst.wiremock.*;

public class WiremockSetup implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(10000);
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
                        "   \"cdComisRet\": 201,\n" +
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
    }

    @Override
    public void stop() {
        if (null != wireMockServer) {
            wireMockServer.stop();
        }
    }
}