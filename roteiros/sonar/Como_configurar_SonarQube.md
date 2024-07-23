> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/sonar/Como_configurar_SonarQube.md&amp;action_name=sonar/Como_configurar_SonarQube)

# Como configurar o acionamento do SonarQube 

Este roteiro ensina a configurar o acionamento do SonarQube para monitorar o código-fonte o seu trabalho. O Sonar é uma plataforma de código aberto que permite a inspeção contínua da qualidade do código. Ele realiza revisões automáticas com análise estática para detectar bugs e problemas de código em diversas linguagens de programação.

## Via linha de comando

Atualmente, a execução do Sonar é uma das últimas etapas da esteira. Ao optar pela configuração por linha de comando, você aciona o Sonar isoladamente, quando quiser, sem a necessidade de aguardar a conclusão das etapas anteriores. Essa configuração funciona para o [Sonar aberto](https://qsonar-snapshot.intranet.bb.com.br/projects) e o [Sonar fechado](https://qsonar.intranet.bb.com.br/projects).

## Requisitos

* O Pengwin instalado
* Um projeto gerado.

### Passo 1: Gerar o token

1. No canto superior direito da tela do SonarQube, clique no ícone de identificação do seu usuário.
2. No menu suspenso, clique em **My Account**. 
3. Clique em **Security**; a seção **Generate Tokens** aparecerá.  
4. Em **Name**, escreva um nome de fácil identificação do seu projeto.
5. Em **Type**, selecione **User Token**.
6. Em **Expires in**, selecione o prazo mais adequado para o seu projeto.
7. Clique em **Generate**; o token aparecerá na tela, destacado na cor verde.
8. Clique em **Copy** para copiar o token e salve a informação para uso posterior.

### Passo 2: Configurar o Sonar no projeto

1. Na sua IDE, localize o arquivo **.m2/settings.xml** do seu projeto.
2. Em **profiles**, localize o **id** do sonar desejado.
3. Em **sonar.login**, cole o token copiado no passo anterior e salve a alteração.
```xml
    <profile>
        <id>sonar</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <!-- Optional URL to server. Default value is http://localhost:9000 -->
            <sonar.host.url>http://qsonar.intranet.bb.com.br:9000</sonar.host.url>
            <sonar.login>TOKEN_DE_USUARIO_SONAR</sonar.login>
        </properties>
    </profile>
    <profile>
        <id>sonar-snap</id>
        <activation>
            <activeByDefault>false</activeByDefault>
        </activation>
        <properties>
            <!-- Optional URL to server. Default value is http://localhost:9000 -->
            <sonar.host.url>http://qsonar-snapshot.intranet.bb.com.br:9000</sonar.host.url>
            <sonar.login>TOKEN_DE_USUARIO_SONAR_SNAPSHOT</sonar.login>
        </properties>
    </profile>
```
### Passo 3: Rodar o comando de execução

1. No terminal do Pengwin, vá para o seu projeto.
2. Rode o comando **mvn install -Dmaven.test.skip=true  sonar:sonar  -Psonar** e aguarde o build.

> :information_source: **Observação** 
> 
> No comando, o **P** indica o *profile*. Portanto, após a letra P, você deve incluir o **id** utilizado para identificar o perfil. Ex.: mvn install -Dmaven.test.skip=true  sonar:sonar  **-Psonar-snap**.
3. Copie o link gerado pelo build para abrir o SonarQube. Se necessário, inclua suas credenciais de acesso.
4. Clique na aba **Issues** para ver os resultados. 

No menu lateral esquerdo, a seção **Severity** contabiliza a quantidade de problemas encontrados, por nível de gravidade. Para corrigir, selecione o item inconsistente e faça os ajustes necessários.

> :grey_exclamation: **Importante** 
> 
> O Penwgin apaga todas as configurações após o término da execução do comando **reconfigurar [....]**. Logo, quando utilizar esse comando, você deve repetir a configuração para personalizar novamente o seu projeto com o acionamento do Sonar.

## Via Jenkins

Caso você queira executar as etapas da esteira conforme a ordem estabelecida, utilize o Jenkins para acionar a análise do Sonar. Com o Jenkins, o Sonar-snapshot funciona somente em versões abertas.

1. Acesse o [Jenkins](https://cloud.ci.intranet.bb.com.br/) com suas credenciais.
2. Selecione o projeto/aplicação que deseja analisar.
3. Clique em **Construir com parâmetros**.
4. Indique a branch que receberá a análise.
5. Clique em **Construir** para gerar o link com os dados do seu projeto.
6. Copie o link gerado pelo build para abrir o SonarQube. Se necessário, inclua suas credenciais de acesso.
7. Clique na aba **Issues** para ver os resultados. 

No menu lateral esquerdo, a seção **Severity** contabiliza a quantidade de problemas encontrados, por nível de gravidade. Para corrigir, selecione o item inconsistente e faça os ajustes necessários.

**Tags:** #pengwin #sonar #sonarsnapshot #jenkins

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/sonar/Como_configurar_SonarQube.md&internalidade=sonar/Como_configurar_SonarQube)
