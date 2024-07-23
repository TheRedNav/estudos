> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/dbeaver/Como_conectar_oracle.md&amp;action_name=dbeaver/Como_conectar_oracle)

# Como usar o DBeaver para conectar ao Oracle
Este roteiro ensina como usar o DBeaver para conectar com o banco de dados Oracle. O DBeaver é uma ferramenta de gerenciamento de banco de dados que suporta diversos sistemas e oferece uma interface gráfica poderosa e amigável para interação com os bancos de dados.

## Requisitos
* DBeaver instalado. 
    * Por questões de segurança, recomendamos o download e a instalação através do Portal da Empresa.
    * Como alternativa, faça o [download](https://dbeaver.io/files/) diretamente pelo site oficial do DBeaver somente das versões homologadas no aplicativo BB Verifica Software. Se escolher esta forma de download, você precisa baixar o arquivo do [repositório drivers/oracle](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/tree/master/dbeaver/referencias/drivers/oracle). 

## Passo 1: Criar nova conexão
O objetivo da nova conexão é permitir que você se conecte a um banco de dados específico a partir do DBeaver.

1. Abra o **DBeaver**.
2. No canto superior esquerdo, clique em **Nova Conexão de Bancos** (ícone do plugin).
3. Na janela **Connect to a database**, localize e selecione o banco de dados da **Oracle**. 
4. Clique em **Avançar**; você irá para a seção **Configurações genéricas de conexão JDBC**.

## Passo 2: Incluir Drivers

> :information_source: **Observação** 
> 
> Este passo só é necessário se você baixou uma versão do DBeaver pela página oficial. Downloads feitos pelo Portal da Empresa já vêm com o driver necessário.

Cada banco de dados requer um driver específico devido às diferentes implementações e protocolos de comunicação nos sistemas de gerenciamento de banco de dados (SGBDs). Inserir um driver no DBeaver habilita o software a entender a linguagem e a comunicação exclusivas do banco de dados que você deseja usar.

1. Na aba **Main**, clique no botão **Edit Driver Settings**.
2. Vá para a aba **Libraries**.
3. Selecione a biblioteca padrão existente e clique em **Delete**.
4. Clique em **Add File**, selecione o arquivo previamente baixado do repositório **drivers/oracle** e clique em **Ok**.

## Passo 3: Informar parâmetros de conexão

Os parâmetros são essenciais para garantir que o DBeaver interaja corretamente com o banco de dados desejado e acesse as informações armazenadas nele.

Novamente na aba **Main**:

1. Em **Host**, preencha com **exa01-scan.desenv.bb.com.br**.
2. Em **Port**, preecha com **1521**.
3. Em **Database/Schema**, preencha conforme o ambiente desejado.
4. Informe seu **Username**.

> :grey_exclamation: **Importante** 
> 
> Desmarque a opção **Save password locally**. Se você alterar a senha, o DBeaver tentará reconectar usando a senha antiga e como consequência o seu usuário será revogado, impossibilitando a conexão.

5. Clique em **Concluir**; a sua conexão aparecerá no lado esquerdo da tela. 

Até o momento, você configurou uma possível conexão, mas ainda não está efetivamente conectado ao banco de dados.

Caso já queira se conectar ao banco de dados:

1. Dê dois cliques na conexão para abrir a janela **User Credentials**. 
2. O seu usuário já estará salvo, mas você precisará informar sua senha. Não marque a opção **Save password** para evitar a revogação de seu usuário. 
3. Clique em **Ok** para efetuar sua conexão.

## Banco de Dados Oracle em Produção

O Oracle em Produção opera com redundância, utilizando dois servidores simultaneamente. Um servidor está sempre ativo, enquanto o outro atua como contingência. Ao criar o usuário de produção, você receberá um e-mail contendo os parâmetros de conexão. Existem duas opções disponíveis para se conectar:

* Opção 1: crie duas conexões seguindo os passos acima, cada uma com um host diferente, por exemplo, **host exa01-scan.servicos.bb.com.br** e **host exa02-scan.servicos.bb.com.br**. Apenas uma conexão será funcional em um determinado momento, dependendo de onde o banco de dados estiver ativo.

* Opção 2: crie uma conexão **Custom** e informe o **tnsname** recebido por e-mail.

![Oracle](dbeaver/referencias/imagens/oracle-custom.png)


**Tags:** #dbeaver #bancodedados #conectar #oracle

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/dbeaver/Como_conectar_db2.md&internalidade=dbeaver/Como_conectar_db2)


