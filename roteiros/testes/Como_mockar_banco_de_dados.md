> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_mockar_banco_de_dados.md&amp;action_name=testes/Como_mockar_banco_de_dados)

# Como configurar e usar o Mockito para Banco de Dados 

Este roteiro ensina a configurar o [Mockito](https://site.mockito.org/), uma biblioteca Java de código aberto que permite criar mocks (simulações) de objetos para testes. Utilize mocks sempre que quiser verificar o comportamento de um método que acessa o banco de dados, sem realmente consultar o banco de dados.   

## Requisitos  

- Um projeto Java + Quarkus gerado pelo [Brave](https://brave.dev.intranet.bb.com.br/novo-projeto/codigo-base).  
- Classes de DAO configuradas com *NamedNativeQuery*.

## Configurar e usar o Mockito para Banco de Dados 

No nosso roteiro, criaremos a *public class* **UsuarioDaoTest** e usaremos o Mockito para rodar três cenários de testes. 

### Cenário 1

Aqui, vamos criar o **testVerificaBuscaSemResultado** para testar o método **buscaUsuarios**. Esse método deve retornar uma lista vazia de usuários.  

1. Na sua IDE, vá para **src > test > java/br/com/bb/{suasigla} > persistencia > dao > {nomeclassedeteste}.java**. 
2. Inclua a anotação **@QuarkusTest** e declare a **public class**.   

~~~java  
@QuarkusTest  
public class UsuarioDaoTest {  
}  
~~~  

3. Em seguida, inclua a anotação **@Test** junto com o nome do seu teste. Escolha uma nomenclatura que descreva o objetivo do seu teste. 
4. Crie um mock do **EntityManager**. 
5. Crie um mock do **TypedQuery**.  

> :information_source: **Observação**  
> O *EntityManager* e o *TypedQuery* são classes frequentemente usadas para interagir com bancos de dados em aplicações Java baseadas em JPA. Mockar essas classes permite testar o comportamento do código sem acessar um banco de dados real.  

~~~java  
@Test 
public void testVerificaBuscaSemResultado(){ 
    var emMock = Mockito.mock(EntityManager.class); 
    var queryMock = Mockito.mock(TypedQuery.class); 
} 
~~~  

6. Na sequência, use o **Mockito.when(). thenThrow()** para lançar uma exceção. Aqui, quando o método **getResultList()** for chamado, uma **NoResultException** será lançada. 
7. Use o **Mockito.when(). thenReturn()** para ter como retorno a consulta mockada. 
> :information_source: **Observação**  
> **thenReturn():** configura o mock para retornar um valor específico quando o método mockado é chamado. <br> 
> **thenThrow():** configura o mock para lançar uma exceção específica quando o método mockado é chamado. 

~~~java  
Mockito.when(queryMock.getResultList()).thenThrow(new NoResultException()); 
Mockito.when(emMock.createNamedQuery(Mockito.anyString(), Mockito.any() ))thenReturn(queryMock); 
~~~ 
8. Crie o objeto usando o **EntityManager** mockado. 
9. Inclua o método **buscaUsuarios()**. 
10. Inclua as verificações **assertNotNull** e **assertTrue** para confirmar que a busca foi feita e não retornará resultados. 

~~~java 
UsuarioDao dao = new UsuarioDao(emMock); 
 
var usuarios = dao.buscaUsuarios(); 

assertNotNull(usuarios,"O resultado não deve ser nulo."); 
assertTrue(usuarios.isEmpty(), "A lista deveria estar vazia para teste sem resultado."); 
~~~  

### Cenário 2

Agora, vamos incluir um teste que lance uma *exception*. Lembre-se que é possível incluir quantos testes quiser no mesmo arquivo. 

11. Inclua outra anotação **@Test** junto com o nome do seu teste.  
12. Crie um mock do **EntityManager**. 
13. Crie um mock do **TypedQuery**.
14. Na sequência, use o **Mockito.when(). thenThrow()** para lançar uma exceção. Aqui, quando o método **getResultList()** for chamado, a **PersistenceException** será lançada. 
15. Use o **Mockito.when(). thenReturn()** para retornar a consulta mockada. 
16. Inclua a verificação **assertThrows** para garantir que a exceção lançada é a esperada.

~~~java  
@Test  
public void testVerificaBuscaComException(){
    var emMock = Mockito.mock(EntityManager.class); 
    var queryMock = Mockito.mock(TypedQuery.class);
 
    Mockito.when(queryMock.getResultList()).thenThrow(new PersistenceException()); 
    Mockito.when(emMock.createNamedQuery(Mockito.anyString(), Mockito.any() )).thenReturn(queryMock);  

    assertThrows(ErrosSistema.ErroExecucaoSQL.class, ()-> dao.buscaUsuarios());
}
~~~  

### Cenário 3

O último teste serve para validar que a função de adicionar usuários funciona corretamente. 

17. Inclua outra anotação **@Test** junto com o nome do seu teste. 
18. Crie um mock do **EntityManager**. 
19. Crie um mock do **Query**. 

~~~java     
@Test  
public void testVerificaInsercaoComMock(){  

    var emMock = Mockito.mock(EntityManager.class);  
    var queryMock = Mockito.mock(Query.class);  
}  
~~~

20. Na sequência, use o **Mockito.when(). thenReturn()** para retornar o valor **1** quando o método **executeUpdate()** for chamado, simulando que uma linha foi afetada e a inserção bem-sucedida. 
21. Inclua outro **Mockito.when(). thenReturn()** para retornar a consulta.   

~~~java
Mockito.when(queryMock.executeUpdate()).thenReturn(1); 
Mockito.when(emMock.createNamedQuery(Mockito.anyString() )).thenReturn(queryMock);  
~~~
22. Crie a instância **Usuário** e inclua os argumentos desejados para inserir um usuário. 
23. Acrescente o método **inserirUsuario()**. 
24. Inclua a verificação **assertNotNull** para confirmar que o resultado não foi nulo e o usuário foi adicionado ao banco de dados.
~~~java
UsuarioDao dao = new UsuarioDao(emMock);  
Usuario usuario = new Usuario(7L, "Meu nome", new Date(System.currentTimeMillis()));  

var res = dao.inserirUsuario(usuario);  

assertNotNull(res,"O resultado não deve ser nulo."); 
~~~  
25. Após a inclusão de todos os testes que deseja mockar, execute o comando `mvn test`. Isso gerará um arquivo HTML com a cobertura de código da aplicação.  
26. Vá para **target > target/site/jacoco index.html** para consultar o arquivo e verificar o resultado dos testes.  

**Tags:** #testes #mockito #mock #bancodedados 

## A Seguir  

* Para ver o arquivo completo utilizado nesse roteiro, acesse as [referências](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroMockito/UsuarioDaoTest.java). 
* Para ver as classes DAO utilizadas nesse roteiro, acesse os arquivos [Usuario.java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroMockito/Usuario.java) e [UsuarioDao.java](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroMockito/UsuarioDao.java).
* Para saber como realizar testes com o JUnit, acesse o roteiro [Testes com JUnit](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/referencias/roteiroJUnit/Testes_JUnit.md)

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_mockar_banco_de_dados.md&internalidade=testes/Como_mockar_banco_de_dados) 
