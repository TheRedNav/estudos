> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VPN.md&amp;action_name=enxovalBB/Como_configurar_VPN)

# Como configurar a VPN Check Point

Este roteiro ensina a instalar e configurar a VPN Check Point no seu computador, seja ele Windows ou Mac. 

## Requisitos

- [Certificado BB Remoto](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_solicitar_acessos_BB.md#passo-2-solicitar-certificado)
- [Arquivo da VPN](https://www.checkpoint.com/pt/quantum/remote-access-vpn/#downloads) baixado 
- Solicitação de [Usuário Administrador Temporário](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_solicitar_acessos_BB.md#passo-3-solicitar-acesso-para-usu%C3%A1rio-administrador-tempor%C3%A1rio) concedida

## Passo 1: Instalar VPN Check Point

### Para Windows

1. Localize e clique duas vezes no arquivo Check Point baixado.
2. Quando aparecer o pop-up de confirmação, clique em **Executar**.
3. No Assistente de Instalação, clique em **Next**.
4. Selecione **Endpoint Security VPN** e clique em **Next**.
5. Marque **I accept the terms in the license agreement** para aceitar os termos e clique em **Next**.
6. Escolha o destino da instalação e clique em **Install**.
7. Informe suas credenciais de administrador temporário quando solicitado e clique em **Sim/Confirmar**.
8. Aguarde a finalização da instalação.
9. Clique em **Finish** para encerrar o Assistente de Instalação.
10. Clique em **Yes** para reiniciar o computador.

Após a instalação, um cadeado aparecerá na barra de notificação.

### Para MAC

1. Localize e clique duas vezes no arquivo Check Point baixado.
2. Clique em **Endpoint_Security_VPN.pkg**.
3. No pop-up de alerta, clique em **Continuar**. 
4. No Assistente de Instalação, clique em **Continuar** .
5. Na tela da Licença de Uso de Software, clique em **Continuar** novamente.
6. Clique em **Concordar** para aceitar os termos.
7. Verifique se há espaço suficiente em disco e clique em **Instalar**.
8. Digite sua senha de administrador e clique em **Instalar Software**.
9. Aguarde a finalização da instalação.
10. Clique em **Fechar** para sair do Assistente de Instalação.

Após a instalação, um cadeado aparecerá na barra de menu. 

## Passo 2: Configurar servidores na VPN

A partir daqui a configuração do Cliente VPN Check Point é a mesma para os Sistemas Operacionais Windows e Mac. 

> :grey_exclamation: **Importante** 
> 
> Execute essa configuração em uma Rede Externa. Pode ser o wi-fi ou roteamento de dados móveis.

1. Abra o **Cliente VPN Check Point**, instalado anteriormente.
2. Verifique se o ícone de um cadeado aparece na barra de notificação (Windows) ou na barra de menu (Mac). <br>
    2.1 No Windows, clique em **Mostrar ícones ocultos** se não visualizar o cadeado.
3. Clique com o botão direito no cadeado e selecione **VPN Options**.
4. Na tela seguinte, clique no botão **New**.
5. No assistente que abrir, clique em **Next**.
6. No campo **Server address or Name**, informe o endereço **bbremoto.bb.com.br**  e clique em **Next**.
7. Se aparecer uma mensagem sobre riscos de segurança, clique em **Trust and Continue**.
8. Selecione **Certificate** como método de autenticação e clique em **Next**.
9. Selecione **Use certificate from Public-Key Cryptographic Standard (PKCS#12) file** e clique em **Next**.
10. Clique em **Yes** quando o assistente perguntar se deseja conectar.

###  Passo 2.1: Adicionar outro site na VPN Check Point

1. Vá ao ícone do cadeado novamente.
2. Se estiver conectado, clique com o botão direito e selecione **Disconnect**, depois confirme.
3. Clique novamente com o botão direito no cadeado e selecione **Connect to...**.
4. Em **Site**, clique na seta ao lado do endereço para abrir o menu suspenso e selecione **New Site**.
5. No Assistente de Configuração, clique em **Next**.
6. No campo **Server address or Name**, informe o mesmo endereço **bbremoto.bb.com.br** .
7. No campo **Display name**, coloque um novo nome e clique em **Next**. Sugerimos utilizar **bbremoto.bb.com.br2**.
8. Selecione **Use certificate from Public-Key Cryptographic Standard (PKCS#12) file** e clique em **Next**.

Você agora tem uma segunda opção de site para acesso em caso de erros. Desconecte a VPN e teste conectar usando o segundo endereço cadastrado.

## Passo 3: Vincular Certificado BB Remoto e Check Point VPN

### Requisito somente para MAC

* No Mac, você deve permitir o aplicativo para não receber o erro *Failed Enforcing Firewall Policy*. Siga as etapas abaixo:

1. Clique no ícone da maçã e selecione **Ajustes do sistema**.
2. No campo de busca, digite **Privacidade e Segurança**.
3. Role a página até a seção **Segurança**.
4. Clique em **Permitir** ao lado da mensagem *O software de sistema do desenvolvedor 'VMware, Inc.' não pôde ser carregado*.
5. Confirme a ação através do seu método de autenticação e tente conectar novamente o Check Point.

A partir daqui a vinculação do Certificado BB Remoto e Check Point VPN é a mesma para os Sistemas Operacionais Windows e Mac.

> :grey_exclamation: **Importante** 
> 
> Execute essa configuração em uma Rede Externa. Pode ser o wi-fi ou roteamento de dados móveis.

1. Caso não esteja conectado, clique com o botão direito no ícone do cadeado e selecione **Connect to...**.
2. Na seção **Please enter your credentials**, clique em **Browse...**.
3. Localize o Certificado BB Remoto em seu computador, selecione-o e clique em **Abrir**.
4. Informe a senha cadastrada para o certificado e clique em **Connect**.
> :information_source: **Observação** 
> 
> Se tiver problemas com a senha, instale o arquivo do Certificado BB Remoto novamente. Se você cadastrou uma senha com mais de 8 dígitos, tente informar apenas os 8 primeiros. Caso o erro persista, solicite um novo certificado. 
5. Aguarde a finalização do processo de conexão.

Após a conexão bem-sucedida, um sinal verde aparecerá no ícone do cadeado. Quando quiser desconectar, clique com o botão direito no ícone do cadeado e selecione **Disconnect**.

Caso queira evitar que o Cliente VPN conecte automaticamente:
1. Clique com o botão direito no ícone do cadeado e selecione **Show Client**.
2. Vá em **VPN** e selecione **VPN Options**.
3. Clique em **Properties**.
4. Na aba **Settings**, desmarque a opção **Enable Always-Connect** e clique em **OK**.

**Tags:** #vpn #checkpoint 

## A Seguir
* Leia o roteiro [Como configurar a VDI BB](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VDI.md) para instalar e configurar a VDI do banco. 

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/enxovalBB/Como_configurar_VPN.md&internalidade=enxovalBB/Como_configurar_VPN)

