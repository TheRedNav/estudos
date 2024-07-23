> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VDI.md&amp;action_name=enxovalBB/Como_configurar_VDI)

# Como configurar a VDI BB
Este roteiro orienta novos contratados a configurar a VDI em sua VPN. Com a VDI você poderá acessar seu desktop BB a partir de uma máquina remota. 

## Requisitos
* Acesso à rede MAN.
* [Check Point](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VPN.md) configurado na máquina.
* Solicitação de [Usuário Administrador Temporário](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_solicitar_acessos_BB.md#passo-3-solicitar-acesso-para-usuário-administrador-temporário) concedida. 

> :bulb: **Dica** 
> 
> Caso você ainda não tenha acesso à rede MAN, solicite-o ao seu gerente BB. Ele criará uma senha temporária que você deverá alterar pela sua [Conta Office](https://myaccount.microsoft.com/).

## Passo 1: Acessar a VDI
Depois de se conectar à VPN pelo Check Point, siga as instruções para o seu primeiro acesso à VDI.
1. Em seu navegador, acesse https://vdi.bb.com.br/. 
2. Faça login com **intrabb\suachaveC**, por exemplo **intrabb\C1234567**, e a senha da rede MAN. Após o login, será apresentada uma tela com a VDI disponível para o seu usuário. 
3. Clique sobre a opção disponível apresentada.
4. Após a abertura, você acessará seu desktop BB. O BB disponibilizou máquinas Linux com a distribuição SUSE SLED e Windows.
5. Teste o acesso à Internet a partir da sua VDI. Para navegar, será solicitado o seu usuário e senha SISBB. Caso não consiga navegar em nenhum site, peça para o seu gestor verificar seus acessos.
6. Faça logoff, se o acesso à sua VDI estiver OK.


## Passo 2: Instalar aplicativo VMWare
1. Faça o [dowload](https://customerconnect.omnissa.com/downloads/info/slug/desktop_end_user_computing/vmware_horizon_clients/horizon_8) da ferramenta.
2. Com o papel de administrador temporário autorizado, faça o procedimento de instalação padrão. 
3. Após a instalação, na sua máquina, busque e execute o aplicativo **VMware Horizon Client**.
4. Na tela inicial, clique em **+ Add Server** para adicionar um novo servidor.
5. Digite **https://vdi.bb.com.br** para o servidor de conexão e clique em **Conectar**.
6. Faça login com **intrabb\suachaveC**, por exemplo **intrabb\C1234567**, e a senha da rede MAN. 

Após o login, será apresentada a VDI disponível e você poderá acessá-la a partir do aplicativo instalado em sua máquina.

> :bulb:**Dica**
>
> O login na VDI também pode ser feito utilizando **chave C** e **senha da Rede MAN** com a opção de acesso **INTRABB** selecionada.

**Tags:** #VDI #VPN #administrador

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VDI.md&internalidade=enxovalBB/Como_configurar_VDI)
