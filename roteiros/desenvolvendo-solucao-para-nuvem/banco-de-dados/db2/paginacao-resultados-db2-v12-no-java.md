> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/paginacao-resultados-db2-v12-no-java.md&amp;action_name=desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/paginacao-resultados-db2-v12-no-java.md)

# Paginação de resultados usando Db2 na versão 12 no Java

A paginação de resultados pode ser feita por pagina e numero de itens por pagina na propria query sql com os comandos
OFFSET e FETCH FIRST x ROWS.

O comando OFFSET serve para "pular" um numero x de registros e o FETCH FIRST x ROWS serve para utilizar um numero x de registros.

Usando os dois comandos juntos podemos fazer uma paginação e retornar somente os registros necessarios conforme a query abaixo:

```sql
SELECT * FROM SEU_SCHEMA.SUA_TABELA WHERE SUA_CONDICAO_BUSCA OFFSET 100 ROWS FETCH FIRST 20 ROWS ONLY;
```

Nesse exemplo de query, estamos fazendo uma consulta aonde os primeiros 100 registros serão ignorados e os proximos 20 serão utilizados.

Em alguns cenarios é necessario incluir dois parametros na string de conexão para que a aplicação possa se conectar com o banco de dados e utilizar esses comandos de paginação,
eles devem ser incluidos caso sua aplicação apresente o erro de sql code -4743, conforme descrito nesse [roteiro](../solucao-de-problemas.md)

Os parametros são:

- currentPackagePath=V12R1M5055

Exemplo de string de conexão com os paramentros indicados acima

```txt
jdbc:db2://dsdb2d01.plexdes.bb.com.br:446/DSDB2D01:currentPackagePath=V12R1M505;
```

> Para que isso funcione corretamente você devera usar um driver jdbc relativo à versão 11.1 FP1 do db2luw ou superior, caso contrario voce irá receber o erro -30025.

Mais sobre esse assunto:

- [Documentação IBM](https://www.ibm.com/docs/en/db2-for-zos/12?topic=release-sql-pagination-support)

- [Issue 4336](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/4336)

- [Roteiro de solução](../solucao-de-problemas.md)

## Exemplo de codigo

Em seu codigo você podera utilizar duas variavies de entrada para controlar a paginação, uma para a quantidade de itens por pagina e outra a pagina na qual se deseja recuperar os resultados,
e outra variavel de saida para indicar a continuidade de resultados.

Resaltando que isso é apenas uma sugestão.

```java
import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class AnalistaDao {

    @PersistenceContext
    EntityManager em;

    private static final String SQL_CONSULTA = "SELECT CD_USU_RSP_ANL, IN_USU_APTO, IN_USU_DFNT "
        + " FROM DB2DLD.USU_RSP_ANL_IDCO "
        + " WHERE CD_USU_RSP_ANL = :codigoAnalista "
        + " OFFSET :iniciarEm ROWS FETCH FIRST :itensPorPagina ROWS ONLY ";

    private static final String SQL_TOTAL_CONSULTA = "SELECT count(*) FROM DB2DLD.USU_RSP_ANL_IDCO "
        + " WHERE CD_USU_RSP_ANL = :codigoAnalista";

    public List<Analista> consultarAnalista(Analista analista,int pagina, int itensPorPagina) throws ErroSqlException {
        // Observação 1
        int iniciarEm = (pagina-1) * itensPorPagina;
        boolean indicadorContinuidade = false;

        // Observação 2
        Query query = em.createNativeQuery(SQL_CONSULTA, Analista.class);

        query.setParameter("codigoAnalista", analista.getCodigoAnalista());
        query.setParameter("iniciarEm", iniciarEm);
        query.setParameter("itensPorPagina", itensPorPagina);

        try {
            List<Analista> analistas = (List<Analista>) query.getResultList();

            // Observação 3 - Solução 1 - Indicador Continuidade
            indicadorContinuidade = analistas.size() == itensPorPagina;

            // Observação 4 - Solução 2 - Indicador Continuidade
            var total = buscaQuantidadeRegistrosNaConsulta(analista);
            indicadorContinuidade = pagina*itensPorPagina < total;

            return analistas;
        } catch (NoResultException e){
            return new ArrayList<>();
        } catch (PersistenceException  e){
            throw new ErroSqlException(e);
        }
    }

    // Metodo para solução 2 para indicador de continuidade
    public int buscaQuantidadeRegistrosNaConsulta(Analista analista) throws PersistenceException{
        // Observação 2
        Query queryCount = em.createNativeQuery(SQL_TOTAL_CONSULTA);
        queryCount.setParameter("codigoAnalista", analista.getCodigoAnalista());

        return (Integer)queryCount.getSingleResult();
    }
}
```

### Observações sobre o codigo

- Observação 1: A pagina 1 indica que o inicio deve ser em zero, por isso temos essa subtração do numero da pagina em menos 1.

- Observação 2: Nesse exemplo estamos usando o metodo createNativeQuery, mas pode ser feito tambem com o createNamedQuery, aonde as querys ficam na anotação @NamedQuery na classe de entidade relacionada com a query.

- Observação 3: Solução 1 para indicador de continuidade. O indicador pode ser criado a partir do calculo da quantidade de itens obtidos para aquela pagina. Se a quantidade for menor que o numero maximo de itens por pagina significa que que não existe mais resultado para as proximas paginas. Contudo essa solução tem vantagem e desvantagem. A vantagem seria a nao necessidade de uma nova consulta para indicar o total de resultados. A desvantagem, para alguns casos, seria os casos aonde o total de registro seja uma divisão com resultado inteiro da quantidade total de itens pela quantidade de itens por pagina, nesses casos ele gera um falso positivo para o indicativo de continuidade nesses casos, gerando uma consulta de pagina sem resultado.

- Observação 4: Solução 2 para indicador de continuidade. Nessa solução temos que realizar uma nova consulta no banco para retornar o total de registros para aquela consulta e com isso verificar se o total é maior que a quantidade de itens por pagina multiplicado pela pagina atual. Assim como a solução 1 ela tem uma vantagem e desvantagem. A vantagem seria a precisão para indicar para todos os casos quando existe ou não continuação. A desvantagem seria realizar sempre uma consulta para ter o total de registros.

### Outras sugestões

Caso deseje verificar a query utilizada e os paramentros utilizados usando o Quarkus voce pode incluir no application.properties as seguintes linhas

```txt
%dev.quarkus.hibernate-orm.log.sql=true
%dev.quarkus.hibernate-orm.log.bind-parameters=true
```

> Elas vão atuar apenas no modo de desenvolvimento (não confundir com execução no ambiente do k8s de desenvolvimento) e vão exibir a query utilizada e os paramentros utilizados. Isso não é recomendado para o ambiente de produção.

---

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/paginacao-resultados-db2-v12-no-java.md&internalidade=desenvolvendo-solucao-para-nuvem/banco-de-dados/db2/paginacao-resultados-db2-v12-no-java)
