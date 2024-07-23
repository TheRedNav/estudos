package br.com.bb.t99.persistence.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import br.com.bb.t99.exception.ErrosSistema;
import br.com.bb.t99.persistence.models.Usuario;
import br.com.bb.t99.services.UsuarioService;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class TestUnitario {
    
    @Inject
    UsuarioDao dao;

    @Inject
    UsuarioService service;

    @Test
    void testUsuarioNomeDataNascimento(){
        Usuario usuario = new Usuario(7, "João da Silva", null);
        assertEquals("João da Silva", usuario.getNome());
        assertNull(usuario.getNascimento());
    }
 
    @Test
    @Order(1)
    void testQuantidadeUsuarios(){
        var usuarios = dao.buscaUsuarios();

        assertFalse(usuarios.isEmpty());
        assertEquals(6, usuarios.size(),"O total de usuários deve ser 6");
    }

    @Test
    @Order(2)
    void testNomeUsuarioJoao(){
        var usuario = dao.buscarUsuario(1L);
        assertNotNull(usuario);
        assertTrue(usuario.getNome().equals("João da Silva"));
    }

    @Test
    void testNomeUsuarioNotFound(){
        var usuario = dao.buscarUsuario(7L);
        assertNull(usuario);
    }

    @Test
    void testInserirUsuarioMenorValido(){
        Usuario usuario = new Usuario(7, "João da Silva", new Date(System.currentTimeMillis()));
        assertThrows(ErrosSistema.ValidacaoIdadeUsuario.class, ()-> service.inserirUsuario(usuario));
    }

    @Test
    void testInserirUsuarioDataNascimento(){
        Usuario usuario = new Usuario(7, "João da Silva", null);
        assertThrows(ErrosSistema.CampoNaoInformado.class, ()-> service.inserirUsuario(usuario));
    }
}
