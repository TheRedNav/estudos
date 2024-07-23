> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_conectar_vscode_via_ssh.md&amp;action_name=enxovalBB/Como_conectar_vscode_via_ssh)


# Como configurar a conexão entre Vagrant e VSCode

Este roteiro ensina a configurar a conexão entre Vagrant e VSCode para utilizar um editor/IDE conectado ao Vagrant. O sistema Linux que utilizamos com o Vagrant não tem uma interface gráfica, apenas um terminal de comandos, e, por isso, o VSCode atua na parte visual. 

## Requisitos

- Ter [Vagrant e VSCode instalados](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/enxovalBB/Como_configurar_ambiente_virtual_dev.md) na máquina.
- A extensão **Remote - SSH** instalada no VSCode.

## Configurar conexão entre Vagrant e VSCode

1. Abra o **VSCode**.
2. No menu lateral esquerdo, clique em **Remote Explorer**, representado pelo ícone ![Imagem do ícone Remote Explorer. Descrição: uma tela de computador com um círculo na lateral inferior direita](enxovalBB/referencias/imagens/icone_remote_explorer.png) ou acesse pelas teclas de atalho **Ctrl+Shift+E**.
3. No menu retrátil do **REMOTE EXPLORER**, selecione a opção **Remotes (Tunnels/SSH)**.
4. Clique em **New Remote**, identificado pelo ícone de **+** para adicionar um novo SSH.
4. No campo **Enter SSH Connection Command**, execute o comanto **ssh vagrant@127.0.0.1 -p 2222**.
5. No campo **Select SSH configuration file to update**, informe o caminho do arquivo. Ex.: C:\Users\f9999999\.ssh\config.
6. Aguarde uma mensagem que aparecerá no canto inferior direito informando que o ambiente foi adicionado.
7. Clique em **Current Window**, identificado pelo ícone da seta para a direita OU clique em **New Window**, identificado pelo ícone do folder para conectar-se ao ambiente 127.0.0.1.

![Imagem dos ícones de conexão. Descrição: uma tela de interface com dois ícones que representam as duas formas possíveis de conexão.](enxovalBB/referencias/imagens/conexao_host.png)
<br>
<br>
8. No campo **Select the platform of the remote host "127.0.0.1"**, selecione **Linux**. <br>
9. Ao ser solicitado, digite a senha utilizada no Vagrant e tecle **Enter**.

> :grey_exclamation: **Importante** 
> 
> - A senha acima é **vagrant**.
> - Cerifique-se também de que a máquina virtual está rodando (vagrant up).

10. Caso ocorra algum erro de conexão, verifique se o arquivo **.ssh\config** apresenta a seguinte configuração:
```
Host 127.0.0.1
HostName 127.0.0.1
User vagrant
Port 2222
```

## Outras informações

- No canto inferior esquerdo do VSCode, você sempre pode consultar a barra de status para ver a qual host você está conectado.

![Imagem do status de conexão entre VSCode e Vagrant. Descrição: interface do VSCode com campo circulado em vermelho sinalizando que a conexão estão ok, localizado no canto inferior esquerdo da tela.](enxovalBB/referencias/imagens/conexao_ssh.png)

- Toda extensão baixada pode ser instalada no ambiente remoto. Basta localizar a extensão e clicar no botão **Install in SSH:127.0.0.1**.

![Imagem do aviso de instalação de extensão para ambiente remoto. Descrição: interface do VSCode com itens circulados em vermelho sinalizando o botão para instalar a extensão no ambiente remoto configurado.](enxovalBB/referencias/imagens/instalar_extensao.png)

- Para abrir um terminal no VSCode, tecle **Ctrl+Shift+'**.

![Imagem do terminal no VSCode. Descrição: interface do VSCode destacando com círculo em vermelho o terminal de interação entre VSCode e ambiente remoto.](enxovalBB/referencias/imagens/interacao_terminal.png)

Com a conclusão da conexão com o Vagrant, você pode clonar repositórios e usar os comandos do git no terminal.

> :bulb: **Dica**
> 
> Para você que está iniciando na Cloud do BB, recomendamos matricular-se nos cursos disponíveis na [Plataforma Onboarding DevCloud](https://onboardingarq3.labbs.com.br/).

**Tags:** #vscode #vagrant 

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_conectar_vscode_via_ssh.md&internalidade=enxovalBB/Como_conectar_vscode_via_ssh)
