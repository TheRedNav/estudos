> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_ambiente_virtual_dev.md&amp;action_name=enxovalBB/Como_configurar_ambiente_virtual_dev)

# Como configurar o ambiente de desenvolvimento virtual

Este roteiro ensina a configurar o ambiente de desenvolvimento virtual em um notebook corporativo com sistema Windows. A configuração do ambiente envolve a instalação e configuração do [VSCode](https://code.visualstudio.com/), [VirtualBox](https://www.virtualbox.org/) e [Vagrant](https://www.vagrantup.com/).

O Visual Studio Code (VSCode) é um editor de código-fonte desenvolvido pela Microsoft, que oferece recursos para editar, depurar e colaborar em projetos de software. Ele possui uma vasta variedade de extensões e ferramentas integradas. A Oracle VM VirtualBox é uma plataforma de virtualização de código aberto, que possibilita aos desenvolvedores executarem diversos sistemas operacionais em um único dispositivo. O Vagrant é uma ferramenta de código aberto utilizada para criar e gerenciar ambientes de desenvolvimento virtuais de forma portátil.

> :grey_exclamation: **Importante** 
> 
> Não realize estas configurações na VDI.

## Requisitos

- Ser o usuário Administrador da máquina.
- Possuir no mínimo 8GB de memória RAM em sua máquina física.
- Uma versão do Windows 10 instalado.

## Passo 1: Solicitar os papéis necessários

1. Acessa a Plataforma BB.
2. Vá para **Negócios > Segurança > Acesso Fácil > [Painel](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=1&app=aceSegurancaAcessoPainel#/)**.
2. No campo de busca, encontre os papéis abaixo e solicite-os:
  * **AICREAD**: concede acesso ao Binários - binarios.intranet.bb.com.br;
  * **ALMFDT99**: concede acesso de desenvolvedor da sigla. Altere T99 pela sua sigla, ex: ALMFDVIP, ALMFDSGN, ALMFDCMU;
  * **ALMFET99**: concede acesso de mantenedor da sigla. Altere T99 pela sua sigla, ex: ALMFEVIP, ALMFESGN, ALMFECMU;
  * **MWGAAA4**: concede acesso a conteúdos da internet;
  * **VPN07**: possibilita o acesso não presencial, via VPN (restrito à DITEC). Se você trabalha apenas presencialmente em algum edifício do BB, este acesso não é necessário.
3. Peça para um gerente liberar os seus pedidos, após concluir as solicitações de acesso na Plataforma. 

Se necessário, retorne ao [Painel](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=1&app=aceSegurancaAcessoPainel#/) e busque pelos acessos para consultar o status das solicitações.

## Passo 2: Instalar e configurar o VSCode

### Instalação do VSCode

1. Baixe sempre a versão mais recente do [VSCode para Windows](https://code.visualstudio.com/Download#). 
2. Siga as instruções do assistente de instalação para concluir a instalação em seu computador. Se necessário, consulte a [documentação de instalação](https://code.visualstudio.com/docs/setup/windows) do produto. 

> :bulb: **Dica** 
> 
> Utilize o recurso de tradução automática do seu navegador para ler as instruções em português.

### Instalação de extensões

1. Abra o **VSCode**.
2. No menu lateral esquerdo, clique em **Extensions** ![Imagem do ícone Extensions. Descrição: 4 quadrados, sendo que 1 quadrado não está conectado aos outros](enxovalBB/referencias/imagens/icone_extensions.png) ou acesse pelas teclas de atalho **Ctrl+Shift+X**. 
3. Na caixa de busca, localize a extensão **Remote - SSH**.
4. Clique em **Install** ou **Instalar**, caso o seu VSCode já tenha o LanguagePack em Português. Após a conclusão da instalação, aparecerá um ícone de engrenagem no mesmo local onde antes estava o botão *Install/Instalar*. Isso indica que a configuração foi realizada com sucesso.

## Passo 3: Instalar e configurar o VirtualBox

1. Clique no [link de download](https://download.virtualbox.org/virtualbox/6.1.26/VirtualBox-6.1.26-145957-Win.exe). <br>
    1.1 Dependendo do seu navegador, o download pode começar automaticamente ou você pode ser redirecionado para uma página de confirmação. Se for redirecionado, clique no botão para iniciar o download.
2. Após a conclusão do download, localize o arquivo de instalação em seu computador. 
3. Dê um duplo clique no arquivo de instalação para iniciar o processo de instalação.

> :information_source: **Observação** 
> 
> Não é necessário instalar o *VirtualBox Extension Pack*.

4. Siga as instruções do assistente de instalação para concluir a instalação em seu computador. 
5. Certifique-se de ler e concordar com os termos de uso durante o processo de instalação.
6. Conclua a instalação.

## Passo 4: Instalar e configurar o Vagrant

1. Clique no link de download: [opção 1](https://binarios.intranet.bb.com.br/artifactory/generic-bb-binarios-gaw-local/publico/vagrant_2.2.16_x86_64.msi) ou [opção 2](http://atf.intranet.bb.com.br/artifactory/bb-binarios-local/gaw/vagrant_2.2.16_x86_64.msi). <br>
    1.1 Dependendo do seu navegador, o download pode começar automaticamente ou você pode ser redirecionado para uma página de confirmação. Se for redirecionado, clique no botão para iniciar o download.
2. Siga as instruções do assistente de instalação para concluir a instalação em seu computador. 
3. Acesse o Terminal do Windows (cmd).
4. Digite **vagrant -v**. Se você receber como resposta a versão baixada, sua instalação foi concluída com sucesso.

## Passo 5: Desativar o Hyper-V do Windows 

> :grey_exclamation: **Importante** 
> 
> É necessário desativar o Hyper-V do Windows para garantir o funcionamento adequado do Vagrant com o VirtualBox.

1. No campo de pesquisa da máquina, escreva **Ativar ou desativar recursos do Windows**.
2. Na janela Recursos do Windows, localize o **Hyper-V** na lista e desmarque a caixa ao lado dele.
3. Clique em **OK** e aguarde enquanto o Windows faz as alterações necessárias.
4. Uma vez concluído, reinicie o computador para aplicar as alterações.

## Passo 6: Configurar um cliente Maven

1. Acesse o repositório de [Binários](https://binarios.intranet.bb.com.br/) e faça o login (chave e senha SISBB).
2. No canto superior direito, clique na sua matrícula,
3. Selecione **Set Me Up**.
4. Na seção **Set Up a Client**, selecione o **Maven**.
5. Em **Repository**, selecione **maven-bb-dev-local**.
6. Abaixo de **Configure**, coloque sua senha SISBB no campo de senha e clique em **Generate Password & Create Instructions**.
7. Copie o token gerado e siga as instruções que aparecem na tela, conforme o seu cenário.
8. Localize a habilite a opção **Mirror Any**. <br>
    7.1 No menu retrátil, mantenha a opção **maven**.
8. Clique em **Generate Settings**.
9. Localize o **Snippet** e clique no ícone de download para baixar o arquivo.
10. No Bloco de Notas, abra o arquivo **settings.xml**.
11. Verifique se o seu arquivo contém um trecho semelhante ao abaixo.
```
<username>f1234567</username>
<password>XXXXXXXXXXXXXXXXXXXXXXXXX</password>
```
12. No mesmo arquivo, localize a tag mirror **<id>maven-test</id>** e troque para **<id>central</id>**.
![Imagem mostrando a localização da tag mirror e como trocar o id de maven-test para central](
enxovalBB/referencias/imagens/maven_para_central.png)

## Passo 7: Configurar a pasta kdimv

1. No Disco Local (:C/), crie uma pasta chamada **kdimv**.
2. Mova o **settings.xml** gerado pelo Maven para esta pasta.
3. Crie um documento de texto chamado **.npmrc**.

> :grey_exclamation: **Importante** 
> 
> O documento de texto não deve ter extensão.

## Passo 8: Incluir arquivos de configuração no Vagrant

1. Acesse o Terminal do Windows (cmd).
2. Execute **cd c:\kdimv**.
3. Execute o comando abaixo para incluir a VirtualBox, base da nossa máquina virtual, no Vagrant.
```
vagrant box add kdi-box https://binarios.intranet.bb.com.br:443/artifactory/generic-bb-binarios-gaw-local/publico/kdi-box.json
```
4. Digite **vagrant init kdi-box**.
5. Na pasta kmdiv, abra o arquivo **Vagrantfile**.
6. Localize a linha com o comando **config.vm.box = "kdi-box"** e inclua logo abaixo desse comando as seguintes informações:
```
config.vm.network "forwarded_port", guest: 8081, host: 8091, host_ip: "127.0.0.1"
config.vm.network "forwarded_port", guest: 16686, host: 16686, host_ip: "127.0.0.1"
```
7. Salve o arquivo.

> :warning: **Atenção** 
> 
> Se estiver acessando a rede do BB através de proxy, realize as configurações adicionais descritas [neste roteiro](https://fontes.intranet.bb.com.br/gaw/publico/gaw-kdi-box#configurar-proxy-para-rede-de-terceirizadas).

8. Certifique-se que o VirtualBox está em execução na máquina e então execute **vagrant up**. A primeira execução pode demorar, pois o ambiente será criado e várias ferramentas serão instaladas e configuradas.
9. Quando acabar, execute **vagrant ssh** para confirmar que o ambiente foi criado. A partir de agora, você está logado em um terminal dentro do seu ambiente virtual.

> :bulb: **Dica** 
> 
> Se a execução parar por mais de 5 minutos em **default: SSH auth method: private key**, abra o VirtualBox, clique com o botão direito na máquina virtual e selecione **Exibir**.

10. Execute **sudo useJDK11.sh** para selecionar a versão necessária do JDK.
11. Execute **sudo useMaven363.sh** para selecionar a versão necessária do Maven.
12. Execute **git config user.name "F9999999 Seu Nome Completo"**.
13. Execute **git config user.email seuemail@bb.com.br**. Ambos os comandos são necessários para permitir que você suba alterações do projeto.

**Tags:** #vagrant #ambiente #vscode #vm #chavec

## A Seguir
* Leia o roteiro [Como configurar a conexão entre Vagrant e VSCode](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_conectar_vscode_via_ssh.md) para utilizar um editor/IDE conectado ao Vagrant e ter acesso às funcionalidades gráficas por meio do VSCode.
* Leia o roteiro [Como configurar o ambiente de desenvolvimento local](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/enxovalBB/Como_configurar_ambiente_local_dev.md#passo-5-configurar-o-java_home-workspace-no-ambiente-virtual) para configurar o JAVA_HOME (workspace) no ambiente virtual.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_ambiente_virtual_dev.md&internalidade=enxovalBB/Como_configurar_ambiente_virtual_dev)
