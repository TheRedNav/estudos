> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/banco-de-dados-no-java-com-quarkus.md&amp;action_name=desenvolvendo-solucao-para-nuvem/banco-de-dados/banco-de-dados-no-java-com-quarkus.md)

# Usando Banco de Dados corporativos em Projetos Java com Quarkus

Este tutorial descreve como desenvolver projetos em Java com Quarkus que acessam o DB2 e Oracle.

Ao gerar um projeto pelo [Brave no menu Novo Projeto->Gere um código base pelo BB-DEV-Generator](https://brave.dev.intranet.bb.com.br/novo-projeto) selecionando uma das opções de banco de dados, DB2 ou Oracle. O projeto estará configurado com as bibliotecas necessárias e com configurações padrões de conexão, contudo você ainda vai precisar configurar usuário, senha e url de conexão no arquivo `.env` na raiz do seu projeto.

**Atenção:**

> Observação: Para banco de dados DB2, atualmente foi autorizado pela GESIT o uso do driver JDBC em baixa/container apenas para queries estáticas, porque estas facilitam a verificação da performance em tempo de compilação e o rastreamento de queries problemáticas em tempo de execução. Neste guia, iremos implementar queries estáticas.

## Configurando as dependências

> :exclamation: Atenção! Se o seu projeto foi gerado pelo Brave com suporte a Banco de Dados, não é necessário realizar este procedimento.

Será necessário configurar o projeto adicionando as dependências abaixo ao arquivo pom.xml:

- quarkus-jdbc-oracle ou quarkus-jdbc-db2 (conforme o banco escolhido)
- quarkus-hibernate-orm - Dependência do hibernate para fazer mapeamento relacional de objetos
- quarkus-narayana-jta - Dependência para controle de transação, usado para fazer comits de uma ou mais operações SQL
- quarkus-jdbc-h2 - Dependência utilizada apenas no escopo de testes para realização de testes com banco de dados.

Exemplo de como elas ficam no pom.xml da sua aplicação

```xml
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-oracle</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-hibernate-orm</artifactId>
    </dependency>
    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-narayana-jta</artifactId>
    </dependency>

    <dependency>
      <groupId>io.quarkus</groupId>
      <artifactId>quarkus-jdbc-h2</artifactId>
      <scope>test</scope>
    </dependency>
```

## Configurando application.properties

No application.properties, localizado em `/src/main/resources`, temos algumas configurações especificas do banco de dados, nele voce pode adcionar a quantidade de conexões simultaneas, tempo para timeout na obtenção de uma conexão e muitas outras para otimizar a conexão com o banco de dados, na documetação do quarkus para a versao 2.7 temos essa lista de configurações possiveis para [conexão](https://quarkus.io/version/2.7/guides/all-config#quarkus-agroal_quarkus-agroal-agroal-database-connection-pool) e para o [hibernate-orm](https://quarkus.io/version/2.7/guides/all-config#quarkus-hibernate-orm_quarkus-hibernate-orm-hibernate-orm), lembrando que a configuração padrão que utilizamos atende a maioria dos casos. Caso esteja usando outra versão do Quarkus, altere na própria página do guia para a versão desejada.

O mínimo necessário já se encontra configurado com as seguintes propriedades:

```properties
# Tipo de banco de dados, oracle, db2 ou h2
quarkus.datasource.db-kind=oracle
# Usuario do banco de Dados
quarkus.datasource.username=${DB_USER}
# Senha do banco de Dados
quarkus.datasource.password=${DB_PASSWORD}
# URL de conexão para o banco de dados
quarkus.datasource.jdbc.url=${DB_URL}
# Habilita o health-check do banco de dados
quarkus.datasource.health.enabled=true
```

**IMPORTANTE**: A depender do tipo de banco de dados e da versão que se acessa, pode ser necessário mudar o dialeto padrão. Confira os dialetos disponíveis na [documentação do Hibernate](https://docs.jboss.org/hibernate/orm/5.6/javadocs/org/hibernate/dialect/package-summary.html).

A não alteração no dialeto quando necessária, normalmente causa erros de sintaxe envolvendo _schema_.

Caso queira usar o h2 como banco de dados para testes, voce pode ter essas mesmas configurações no mesmo application.properties mas com o prefixo `%test.` ou criar um arquivo application.properties dentro da pasta de testes `/src/test/resources`.

As configurações de usuário, senha e url, estão apontando para environments que estão definidas dentro do arquivo `.env` na raiz do projeto, exceto para o h2 que possui essas configurações preechidas com usuário e senha padrão e a string de conexão apontando para executar o script para carga do modelo e dos dados.

> Observação: Para obter acesso aos SGBDs, observe o roteiro [Uso de Banco de Dados na Nuvem.](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/uso-de-banco-de-dados-na-nuvem.md)

## Configurando .env

Verifique se o arquivo `.env` existe na raiz do projeto, caso negativo, crie-o, usando o modelo abaixo:

```.env
#Endereço para que a aplicação realize a conexão com o banco de dados.
DB_URL=
#Usuário e senha impessoal para acesso ao banco de dados
DB_USER=
DB_PASSWORD=
```

Para maiores informações sobre como configurar a URL acesse esse [documento](https://ad.intranet.bb.com.br/?p=6074).

Lembre-se de comentar no Readme do projeto a necessidade e onde configurar, mas nunca comite a informação no repositório e verifique se o seu .gitignore esta configurado corretamente para ignorar o arquivo .env.

Caso o projeto tenha sido gerado pelo Brave, no script de execução `run.sh` temos o modo `-b` para solicitar o usuário pessoal, nesse caso ele vai ignorar o que está salvo no .env e vai solicitar seu usuário e senha, isso para que voce não precise salvar o seu usuário e senha no arquivo de enviroments. Verifique se seu usuário pessoal possui acesso aos bancos de dados e tabelas que irá utilizar.

## Configurando docker-compose

O docker-compose tambem irá utilizar o arquivo `.env`, dessa forma, não é necessário fazer alterações adicionais no mesmo.
Para projetos gerados pelo Brave, caso queira utilizar o seu usuário pessoal utilizar o comando `-b` na execução do script `run.sh` junto com o comando `-dc` para executar no modo docker-compose.

## Configurações do health check do database

Os projetos gerados pelo Brave com a opcao de banco de dados já possuem a configuração de health check para o banco de dados. Caso seu projeto não tenha ou se quiser desabilitar, configure o application.properties conforme abaixo:

```yaml
quarkus.datasource.health.enabled=true
```

## Implementação

Nos projetos gerados pelo Brave, estamos utilizando o hibernate como mapeamento classe relacional, isso significa que
cada classe vai ser uma representação de uma tabela de banco de dados. Assim, primeiro vamos precisar fazer o mapeamento das tabelas em classes e depois criar os comandos SQL para realizar as consultas.

Recomendamos a utilização de uma classe para o mapeamento, geralmente localizada em uma pasta chamada `model` e outra
classe responsavel por executar as consultas e iteragir com o banco de dados, essas classes devem possuir um sufixo `DAO`
no nome que significa `DATA ACESS OBJECT`. Falaremos mais sobre eles nos tópicos abaixo.

### Criando os models e consultas

A primeira coisa que devemos indicar para nossa classe é que ela é uma entidade, fazemos isso com a anotação `@Entity`, depois temos a anotação `@Table`, ela possui duas propriedades que devem ser preenchidas, name para o nome da tabela e schema para o esquema de banco de dados que essa tabela faz parte.

Tambem podemos ter a anotação `@NamedNativeQueries` ou `@NamedNativeQuery`, a primeira é apenas uma forma de fazer o agrupamento de uma ou mais @NamedNativeQuery.

Depois, dentro da classe, vamos ter anotações específicas para o mapeamento das colunas, existem varias anotações para
realizar a configuração, aqui vamos abordar apenas as mais usadas.

- `@Id`: propriedade mapeada que faz parte do identificador da coluna

- `@Column`: propriedade para indicar qual coluna ele faz referência, se o nome da propriedade for o mesmo nome da coluna não precisa dessa configuração

As consultas ficam dentro da anotação `@NamedNativeQuery`. Na propriedade query, essa consulta deve ser feita no padrão SQL. Também temos o atributo name, para o nome da query que vai ser executada pelo DAO, e o resultClass que é a classe que será o resultado da consulta, que pode ser uma classe mapeada de uma tabela ou uma classe [DTO](https://pt.stackoverflow.com/questions/31362/o-que-%C3%A9-um-dto) que vai receber apenas o resultado da consulta.

```java
@Entity
@Table(name="USUARIOS", schema="exemplo")
@NamedNativeQueries({
    @NamedNativeQuery(name="CONSULTAR_USUARIO", query = "SELECT id, nome, nascimento from exemplo.USUARIOS", resultClass = Usuario.class),
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="nome")
    private String nome;

    private Date nascimento;

    public Usuario() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Date getNascimento() {
        return nascimento;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

    public Usuario(long id, String nome, Date nascimento) {
        this.id = id;
        this.nome = nome;
        this.nascimento = nascimento;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", dataNascimento=" + nascimento +
                '}';
    }
}
```

### Criando DTOs

Caso sua consulta retorne um objeto que não é uma tabela, podemos criar uma classe DTO para representar esse resultado.
Para realizar o mapeamento do resultado da query com a classe, você deve incluir uma anotação na classe que possui a query, esse sera o mapeamento do resultado com o DTO, e os nomes das colunas devem ser iguais aos nomes das propriedades da classe.

Esse mapeamento é feito com a anotação `@SqlResultSetMapping` na classe DTO, onde dizemos a qual mapping ele faz referência pelo campo name, e no `@ConstructorResult` indicamos os atributos e o tipo que serão usados no construtor da classe DTO. A ordem das colunas tem que estar de acordo com a ordem do método construtor da classe DTO.

```java
@SqlResultSetMapping(
		name = "UsuarioPorEstadoSetMapping",
		classes = @ConstructorResult(
				targetClass = UsuarioPorEstadoDTO.class,
				columns = {
						@ColumnResult(name = "estado", type = String.class),
						@ColumnResult(name = "quantidade", type = Long.class)})
)
```

Na anotação `@NamedNativeQuery`, na classe que mapeia a entidade, devemos indicar qual o mapping vamos usar atribuindo o nome do mapping na propriedade resultSetMapping, conforme exemplo abaixo:

```java
@NamedNativeQuery(name="CONSULTAR_USUARIO_POR_ESTADO", query = "SELECT end.estado as estado, count(end.usuario_id) as quantidade " +
        " from exemplo.USUARIOS user " +
        " inner join exemplo.ENDERECO end on end.usuario_id = user.id  " +
        " group by end.estado ",
        resultSetMapping = "UsuarioPorEstadoSetMapping",
        resultClass = UsuarioPorEstadoDTO.class),
```

### Criando o DAO

As classes DAO, são responsáveis por fazer a comunicação com o banco de dados e enviar os comando de execução das querys criadas no models. Para realizar isso ela precisa ter acesso ao `EntityManager`, que é responsavel pela abstração e controle das conexões com o banco de dados. Ele verifica se existe conexão disponível e cria uma se não existir. Além disso, controla o pool de conexões de acordo com as configurações.

Para executar os comandos no EntityManager, temos que executar o método createNamedQuery passando o nome da namedQuery e a classe de retorno. Como resultado, esse método retorna um objeto do tipo Query ou TypedQuery. Para os métodos de exclusão ou atualização pegamos a resposta no método .executeUpdate() e para as consultas podemos usar o getResultList() para listas, ou getSingleResult() para resultados únicos.

Outro ponto importante é o uso da anotação `@Transactional` nos métodos. Essa anotação é responsável por controlar a transação nos comandos SQL, criando uma cadeia com um ou mais métodos para organizá-las em uma única transação no banco de dados, fazendo o commit quando executada com sucesso ou rollback em caso de erro. Erros são disparados por qualquer exception lançada, que podem ser configuradas de acordo com o erro.

A seguir temos alguns exemplos dos pontos citados acima.

```java
package br.com.bb.suasigla.persistence.dao;

import br.com.bb.suasigla.exception.ErrosSistema;
import br.com.bb.suasigla.persistence.models.Usuario;
import br.com.bb.suasigla.persistence.dto.UsuarioPorEstadoDTO;
import org.eclipse.microprofile.opentracing.Traced;
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Traced
@RequestScoped
public class UsuarioDao {

	EntityManager em;

	public UsuarioDao(EntityManager em){
		this.em = em;
	}

	public List<UsuarioPorEstadoDTO> buscarUsuarioPorEstado(){
		TypedQuery<UsuarioPorEstadoDTO> query = em
				.createNamedQuery("CONSULTAR_USUARIO_POR_ESTADO", UsuarioPorEstadoDTO.class);
        try {
			return query.getResultList();
		} catch (NoResultException e){
			return new ArrayList<>();
		} catch (PersistenceException e){
			throw new ErrosSistema.ErroExecucaoSQL(e);
		}
	}

	public List<Usuario> buscaUsuarios() {
		String nameQuery = "CONSULTAR_USUARIO";

		TypedQuery<Usuario> query = em
				.createNamedQuery(nameQuery, Usuario.class);

		try {
			return query.getResultList();
		} catch (NoResultException e){
			return new ArrayList<>();
		} catch (PersistenceException e){
			throw new ErrosSistema.ErroExecucaoSQL(e);
		}
	}

	public Usuario buscarUsuario(Long id) {
		String nameQuery = "CONSULTAR_USUARIO_ID";

		TypedQuery<Usuario> query = em
				.createNamedQuery(nameQuery, Usuario.class);

		query.setParameter("idUsuario", id);

		try {
			return query.getSingleResult();
		} catch (NoResultException e){
			return null;
		} catch (PersistenceException e){
			throw new ErrosSistema.ErroExecucaoSQL(e);
		}
	}

	@Transactional
	public int inserirUsuario(Usuario usuario){
		String nameQuery = "INSERIR_USUARIO";
		return insertOrUpdate(usuario, nameQuery);
	}

	@Transactional
	public int excluirUsuario(long id) {
		String nameQuery = "EXCLUIR_USUARIO";

		Query query = em
				.createNamedQuery(nameQuery);

		query.setParameter("idUsuario", id);

		try {
			return query.executeUpdate();
		} catch (NoResultException e){
			return 0;
		} catch (PersistenceException e){
			throw new ErrosSistema.ErroExecucaoSQL(e);
		}
	}

	@Transactional
	public int atualizarUsuario(Usuario usuario){
		String nameQuery = "ATUALIZAR_USUARIO";
		return insertOrUpdate(usuario, nameQuery);
	}

	@Transactional
	private int insertOrUpdate(Usuario usuario, String nameQuery) {
		Query query = em
				.createNamedQuery(nameQuery);

		query.setParameter("idUsuario", usuario.getId());
		query.setParameter("nomeUsuario", usuario.getNome());
		query.setParameter("dataNascimento", usuario.getNascimento());

		try {
			return query.executeUpdate();
		} catch (NoResultException e) {
			return 0;
		} catch (PersistenceException e) {
			throw new ErrosSistema.ErroExecucaoSQL(e);
		}
	}
}

```

## Observações para execução das queries de banco

Os tópicos abaixo são itens que devem ser estudados e aplicados conforme a necessidade do projeto. Em alguns cenarios vale o uso da anotação @Transacional da lib `javax.transaction.Transactional` ou o uso da anotação @Immutable da lib `org.hibernate.annotations.Immutable` para evitar atualizações desnecessárias da entidade anotada. Abaixo temos mais detalhes.

### Transacional

A anotação `@Transacional` deve ser incluída nos métodos que iniciam ou fazem parte de uma transação. Qualquer método chamado subsequentemente que possua essa anotação fara parte transação existente e qualquer comando de banco de dados fará parte dessa transação, que será comitada quando o primeiro metodo executado com a anotação `@Transacional` for finalizado. Em caso de execeção que não seja tratada, essa transação irá executar um rollback. Para maiores informações segue alguns links:

- [javaDoc](https://docs.oracle.com/javaee/7/api/javax/transaction/Transactional.html)
- [devmedia](https://www.devmedia.com.br/java-transaction-api-jta-na-plataforma-java-ee-7/31820)

### Immutable

Outra anotação que pode ser utilizada, é a `@Immutable` que deve ser colocada na classe que representa uma entidade de banco de dados. Essas classes são aquelas que possuem a anotação `@Entity`.
Essa anotação previne atualização do objeto no banco quando alguma propriedade é alterada.

- [javaDoc](https://docs.jboss.org/hibernate/orm/5.2/javadocs/org/hibernate/annotations/Immutable.html)
- [baeldung](https://www.baeldung.com/hibernate-immutable)

## Como inserir a URL de conexão do banco de dados corretamente

Este guia da equipe de Administração de Dados ajuda a configurar as urls de conexão aos bancos de dados corretamente, tem também uma referência de utilização:

[Roteiro de configuração dos banco de dados](https://ad.intranet.bb.com.br/?p=6074)

## Possíveis erros com banco de dados

Para casos de erros com banco de dados, verifique [o roteiro para solução de problemas](<(https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/solucao-de-problemas.md)>)

---

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/banco-de-dados-no-java-com-quarkus.md&internalidade=desenvolvendo-solucao-para-nuvem/banco-de-dados/banco-de-dados-no-java-com-quarkus)
