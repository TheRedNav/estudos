> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_Mockito.md&amp;action_name=testes/Como_configurar_Mockito)

# Como configurar e usar o Mockito  

Este roteiro ensina a configurar o [Mockito](https://site.mockito.org/), uma biblioteca Java de código aberto que permite criar mocks (simulações) de objetos para testes unitários. Realize mocks sempre que quiser fazer simulações de requisições reais. No nosso exemplo, criaremos a *public class* **ConsultaFunciTest** e usaremos o Mockito para rodar dois cenários de testes.  

## Requisitos  

- Um projeto Java + Quarkus gerado pelo [Brave](https://brave.dev.intranet.bb.com.br/novo-projeto/codigo-base).  

## Configurar e usar o Mockito  

No primeiro exemplo, vamos mockar um *restClient* Java/Quarkus que faz uma requisição para o Curió a fim de consumir a operação 252416v1. Essa operação consulta e retorna dados básicos de identificação de um funcionário.  

> :information_source: **Observação**   
>   
> Tanto a classe quanto os testes usam a anotação **@QuarkusTest**.  

1. Na sua IDE, vá para **src > test > java/br/com/bb/{suasigla} > rest > {nomeclassedeteste}.java**. 
2. Inclua a anotação **@QuarkusTest** e declare a sua *public class*.   
3. Inclua a anotação **@InjectMock** para indicar que o Quarkus deve injetar um mock da interface ou classe especificada.  
4. Inclua a anotação **@RestClient** para indicar que este é um cliente REST.    

> :grey_exclamation: **Importante**  
>  
> A anotação **@InjectMock** precisa do pacote **io.quarkus.test.InjectMock**. Não esqueça de incluir o **import**. 

~~~java  
@QuarkusTest  
public class ConsultaFunciTest {  

    @InjectMock  
    @RestClient  
    ConsumidorCurio consumidorCurio;  
      
}  
~~~  

5. Em seguida, inclua a anotação **@Test** junto com o nome do seu teste. Escolha uma nomenclatura que descreva o objetivo do seu teste. 
6. Inicialize um objeto da classe **Saida** e configure seus atributos conforme necessário.  
7. Inicialize um objeto da classe **DadosRespostaConsultarDadosBasicosFuncionarioBB** e configure-o para usar o objeto **Saida** criado anteriormente.  

~~~java  
@Test  
void testConsultarMatriculaComSucesso(){  

    var saida = new Saida();  
    saida.setNomeRet("João da Silva");  

    var resposta =  new DadosRespostaConsultarDadosBasicosFuncionarioBB();  
    resposta.setSaida(saida);
}
~~~  

8. Na sequência, use o **Mockito.when().thenReturn()** para especificar que, quando o método **executarOp252416v1** for chamado com qualquer string como argumento, ele deve retornar o objeto **resposta**.  

~~~java  
Mockito.when(consumidorCurio.executarOp252416v1(Mockito.any(DadosRequisicaoConsultarDadosBasicosFuncionarioBB.class))).thenReturn(resposta);  
~~~  

9. Agora, inclua a resposta que será verificada quando a requisição HTTP for feita.

~~~java  
given()  
    .when().get("/consulta/1234567")  
    .then()  
    .statusCode(200)  
    .body(containsString("João da Silva") );
}      
~~~  

No segundo exemplo, vamos mockar uma operação que consulta e retorna um erro. Você pode incluir quantos cenários de teste quiser no mesmo arquivo.   

10. Inclua outra anotação **@Test** junto com o nome do seu teste. Não esqueça de escolher uma nomenclatura que descreva o objetivo do seu teste.  

~~~java  
@Test  
void consultarMatriculaComErro(){  
}
~~~  

11. Na sequência, use o **Mockito.when().thenThrow()** para especificar que, quando o método **executarOp252416v1** for chamado com qualquer string como argumento, ele deve retornar o objeto **CurioConsumoException**.    
12. Inclua a resposta que será verificada quando a requisição HTTP for feita, junto com o código e a mensagem de erro.     

> :information_source: **Observação**  
>  
> **thenReturn():** configura o mock para retornar um valor específico quando o método mockado é chamado. <br> 
> **thenThrow():** configura o mock para lançar uma exceção específica quando o método mockado é chamado. 

~~~java     
Mockito.when(consumidorCurio.executarOp252416v1(Mockito.any(DadosRequisicaoConsultarDadosBasicosFuncionarioBB.class))).thenThrow(new CurioConsumoException("999","Erro simulado de um consumo"));  

given()  
    .when().get("/consulta/1111111")  
    .then()  
    .statusCode(500)  
    .body(containsString("\"CURIO-ERRO-CODE\":\"999\""))  
    .body(containsString("\"CURIO-ERRO-MENSAGEM\":\"Erro simulado de um consumo\""));  
}  
~~~  

13. Após a inclusão de todos os testes que deseja mockar, execute o comando `mvn test`. Isso gerará um arquivo HTML com a cobertura de código da aplicação.  
14. Vá para **target > target/site/jacoco index.html** para consultar o arquivo e verificar o resultado dos testes.  

**Tags:** #testes #mockito #mock  

## A Seguir  

- Para ver o arquivo completo utilizado nesse roteiro, acesse as [referências](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroMockito/ConsultaFunciTest.java). 
- Para saber como realizar testes com o JUnit, acesse o roteiro [Testes com JUnit](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroJUnit/Testes_JUnit.md)

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_configurar_Mockito.md&internalidade=testes/Como_configurar_Mockito) 
