> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/dbeaver/Como_conectar_db2.md&amp;action_name=dbeaver/Como_conectar_db2)

# Como usar o DBeaver para conectar ao Db2
Este roteiro ensina como usar o DBeaver para conectar com o banco de dados IBM Db2. O DBeaver é uma ferramenta de gerenciamento de banco de dados que suporta diversos sistemas e oferece uma interface gráfica poderosa e amigável para interação com os bancos de dados.

## Requisitos
* DBeaver instalado. 
    * Por questões de segurança, recomendamos o download e a instalação através do Portal da Empresa.
    * Como alternativa, faça o [download](https://dbeaver.io/files/) diretamente pelo site oficial do DBeaver somente das versões homologadas no aplicativo BB Verifica Software. Se escolher esta forma de download, você precisa baixar os arquivos do [repositório drivers/bd2](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/dbeaver/referencias/drivers/db2). 

## Passo 1: Criar nova conexão
O objetivo da nova conexão é permitir que você se conecte a um banco de dados específico a partir do DBeaver.

1. Abra o **DBeaver**.
2. No canto superior esquerdo, clique em **Nova Conexão de Bancos** (ícone do plugin).
3. Na janela **Connect to a database**, localize o banco de dados da **IBM Db2**. Você verá duas opções: LUW e z/OS.
4. Selecione a opção **IBM Db2 z/OS**.
> :information_source: **Observação** 
> 
> Se você selecionar a opção LUW, a conexão será estabelecida, mas os metadados podem não ser carregados corretamente. Isso resultará em problemas com o recurso de autocompletar.

5. Clique em **Avançar**; você irá para a seção **Configurações genéricas de conexão JDBC**.

## Passo 2: Incluir drivers

> :information_source: **Observação** 
> 
> Este passo só é necessário se você baixou uma versão do DBeaver pela página oficial. Downloads feitos pelo Portal da Empresa já vêm com os drivers necessários.

Cada banco de dados requer um driver específico devido às diferentes implementações e protocolos de comunicação nos sistemas de gerenciamento de banco de dados (SGBDs). Inserir um driver no DBeaver habilita o software a entender a linguagem e a comunicação exclusivas do banco de dados que você deseja usar.

1. Na aba **Main**, clique no botão **Edit Driver Settings**.
2. Vá para a aba **Libraries**.
3. Selecione a biblioteca padrão existente e clique em **Delete**.
4. Clique em **Add File**, selecione todos os arquivos previamente baixados do repositório **drivers/bd2** e clique em **Ok**.

## Passo 3: Informar parâmetros de conexão

Os parâmetros são essenciais para garantir que o DBeaver interaja corretamente com o banco de dados desejado e acesse as informações armazenadas nele.

Novamente na aba **Main**:

1. Em **Host**, preencha com **gwdb2.bb.com.br**.
2. Em **Port**, preencha com **50100**.
3. Em **Database/Schema**, preencha conforme o ambiente desejado: <br>

|Ambiente|Database/Schema|
|-|-|
|Desenvolvimento D0G1| DSDB2D01|
|Homologação D8G1 (Geral)| BDB2H01|
|Homologação D8G5 (Finanças)|HMDB2G5|
|Homologação D8G7 (Seguridade e crédito)| HMDB2G7|
|Produção D3G1 (Corporativo)| BRDB2P1|
|Produção DCG1 (Finanças)| B3DB2G1|
|Produção D3G3 (Intranet)| BDB2P0R|
|Produção D3G4 (Datawarehouse)| BDB2P04|
|Produção 2 - D2G1 (Crédito, seguridade e gestão)| B2DB2G1|
|Produção 2 - D2G2 (Internet)| B2DB2G2|
|Produção 2 - D2G3 (Swift/WBI)| B2DB2G3|
|Produção 2 - D2G4 (Compensação)| B2DB2G4|
|Produção 3 - DCG2 (Compensação)| B3DB2G2 |

4. Informe seu **Username**.

> :grey_exclamation: **Importante** 
> 
> Desmarque a opção **Save password locally**. Se você alterar a senha, o DBeaver tentará reconectar usando a senha antiga e como consequência o seu usuário será revogado, impossibilitando a conexão.

5. Clique em **Concluir**; a sua conexão aparecerá no lado esquerdo da tela. 

Até o momento, você configurou uma possível conexão, mas ainda não está efetivamente conectado ao banco de dados.

## Passo 4: Ajustar timestamp no formato Db2 z/OS

Ajustar o formato de data garante a consistência e precisão dos dados, e também facilita a comunicação entre o DBeaver e o Db2 z/OS.

1. Clique com o botão direito do mouse na conexão recém-criada e selecione **Edit Connection**.
2. Na janela **Connection Settings**, abra o menu suspenso **Data editor**.
3. Clique em **Data Formats**.
4. Marque a caixa de seleção **Datasource "sua conexão" settings** para habilitar a configuração.  
5. Em **Language**, troque para **en - inglês**.
6. Em **Country**, troque para **US - Estados Unidos**.
7. Em **Type**, troque para **Registro de data e hora**.
8. Clique em **Ok**; uma mensagem de confirmação da alteração aparecerá. 

Caso já queira se conectar ao banco de dados:
1. Dê dois cliques na conexão para abrir a janela **User Credentials**. 
2. O seu usuário já estará salvo, mas você precisará informar sua senha. Não marque a opção **Save password** para evitar a revogação de seu usuário. 
3. Clique em **Ok** para efetuar sua conexão.

**Tags:** #dbeaver #bancodedados #conectar #db2

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/dbeaver/Como_conectar_db2.md&internalidade=dbeaver/Como_conectar_db2)
