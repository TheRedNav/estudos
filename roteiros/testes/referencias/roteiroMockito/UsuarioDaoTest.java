package br.com.bb.t99.persistence.dao;

import br.com.bb.t99.exception.ErrosSistema;
import br.com.bb.t99.persistence.models.Usuario;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.Date;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

@QuarkusTest
public class UsuarioDaoTest {

	@Test
	public void testVerificaBuscaSemResultado(){
		//Mock para lancar uma exception sem resultado.
		var emMock = Mockito.mock(EntityManager.class);
		var queryMock = Mockito.mock(TypedQuery.class);

		Mockito.when(queryMock.getResultList()).thenThrow(new NoResultException());
		Mockito.when(emMock.createNamedQuery(Mockito.anyString(), Mockito.any() )).thenReturn(queryMock);

		UsuarioDao dao = new UsuarioDao(emMock);

		var usuarios = dao.buscaUsuarios();

		assertNotNull(usuarios,"O resultado não deve ser nulo.");
		assertTrue(usuarios.isEmpty(), "A lista deveria estar vazia para teste sem resultado.");
	}

	@Test
	public void testVerificaBuscaComException(){
		//Mock para lancar um erro de sql, mas sem as informacoes de SQL
		var emMock = Mockito.mock(EntityManager.class);
		var queryMock = Mockito.mock(TypedQuery.class);

		Mockito.when(queryMock.getResultList()).thenThrow(new PersistenceException());
		Mockito.when(emMock.createNamedQuery(Mockito.anyString(), Mockito.any() )).thenReturn(queryMock);

		UsuarioDao dao = new UsuarioDao(emMock);

		assertThrows(ErrosSistema.ErroExecucaoSQL.class, ()-> dao.buscaUsuarios());
	}


	@Test
	public void testVerificaInsercaoComMock(){
		//Mock para lancar um erro de sql, mas sem as informacoes de SQL
		var emMock = Mockito.mock(EntityManager.class);
		var queryMock = Mockito.mock(Query.class);
	
		Mockito.when(queryMock.executeUpdate()).thenReturn(1);
		Mockito.when(emMock.createNamedQuery(Mockito.anyString() )).thenReturn(queryMock);


		UsuarioDao dao = new UsuarioDao(emMock);
		Usuario usuario = new Usuario(7L, "Meu nome", new Date(System.currentTimeMillis()));
		var res = dao.inserirUsuario(usuario);
		System.out.println(res);
		assertNotNull(res,"O resultado não deve ser nulo.");
	}
}