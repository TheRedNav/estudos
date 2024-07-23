> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_ambiente_local_dev.md&amp;action_name=enxovalBB/Como_configurar_ambiente_local_dev)

# Como configurar o ambiente de desenvolvimento local

Este roteiro ensina os funcionários BB (chave F) a configurar o ambiente de desenvolvimento local em um notebook corporativo com sistema Windows. A configuração do ambiente envolve a instalação e configuração do [VSCode](https://code.visualstudio.com/) e a instalação e configuração do [Pengwin](https://fontes.intranet.bb.com.br/dev/publico/pengwin). 

O Visual Studio Code (VSCode) é um editor de código-fonte desenvolvido pela Microsoft, que permite editar, depurar e colaborar em projetos de software, com uma ampla gama de extensões e ferramentas integradas. O Pengwin é uma aplicação desenvolvida pelo BB que prepara seu ambiente com os aplicativos necessários tanto para desenvolver na nuvem quanto para outros ambientes do BB. 

## Requisitos

* Ser um funcionário BB (chave F) lotado na DITEC.
* Um notebook corporativo com Windows e associado à sua matrícula. 
* Windows atualizado (versão superior a 19044.2364), conforme a documentação do [Pengwin](https://fontes.intranet.bb.com.br/dev/publico/pengwin#verificando-o-windows).

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

Baixe sempre a versão mais recente do [VSCode para Windows](https://code.visualstudio.com/Download#). Se necessário, consulte a [documentação de instalação](https://code.visualstudio.com/docs/setup/windows) do produto.

> :bulb: **Dica** 
> 
> Utilize o recurso de tradução automática do seu navegador para ler as instruções em português.

### Instalação de extensões

1. Abra o **VSCode**.
2. No menu lateral esquerdo, clique em **Extensions** ![Imagem do ícone Extensions. Descrição: 4 quadrados, sendo que 1 quadrado não está conectado aos outros](enxovalBB/referencias/imagens/icone_extensions.png) ou acesse pelas teclas de atalho **Ctrl+Shift+X**. 
3. Na caixa de busca, localize as duas extensões necessárias: 
* **Remote - Development: ms-vscode-remote.vscode-remote-extensionpack**;
* **Extension Pack for Java: vscjava.vscode-java-pack**;
4. Clique em **Install** ou **Instalar**, caso o seu VSCode já tenha o LanguagePack em Português. Após a conclusão da instalação, aparecerá um ícone de engrenagem no mesmo local onde antes estava o botão *Install/Instalar*. Isso indica que a extensão foi baixada com sucesso.

## Passo 3: Instalar o WSL 2 e o PengWin

1. No campo de busca da sua máquina, escreva **Portal da Empresa** e abra a aplicação.
2. No canto superior esquerdo da tela, localize a caixa **Pesquisar Aplicativos**.
3. Digite **WSL** e selecione a opção **WSL2 - Windows Subsystem for Linux**.
4. Clique em **Instalar**.
5. Após instalar o WSL2, repita os passos 3 e 4 para instalar o **PengWin**.
6. Abra o **PengWin**.
7. No formulário **Dados Pessoais**, preencha as informações solicitadas. <br>
     7.1 Mantenha o **Proxy BB** habilitado.
8. Observe que ele carregou automaticamente os seus dados pessoais, confira e clique em **Continuar**.
9. Clique em **Instalar** e aguarde o fim da instalação. 
10. Reinicie a máquina. 

> :grey_exclamation: **Importante** 
> 
> Sempre que você trocar a senha do SISBB, será necessário [atualizar a sua senha no Pengwin](https://fontes.intranet.bb.com.br/dev/publico/pengwin#e-se-eu-trocar-a-senha-no-sisbb).

## Passo 4: Conectar o VSCode com o Pengwin

Essa conexão é necessária porque o VSCode fica instalado na máquina local, enquanto o Pengwin e as outras aplicações estão numa máquina virtual.

1. No menu lateral esquerdo do **VSCode**, clique em **Remote Explorer** ![Imagem do ícone Remote Explorer. Descrição: uma tela de computador com um círculo na lateral inferior direita](enxovalBB/referencias/imagens/icone_remote_explorer.png) ou acesse pelas teclas de atalho **Ctrl+Shift+E**.
2. Clique em **WSL Target**.
3. Em **Pengwin-WSL**, clique no ícone da **seta para a direita**, localizado ao lado de *default distro*.
4. Abra um terminal **(Ctrl+Shift+')**.
5. Execute os comandos a seguir para criar o diretório **git**:
```
cd
mkdir git
```

## Passo 5: Configurar o JAVA_HOME (workspace) no ambiente virtual

O workspace é essencialmente um projeto que inclui uma ou mais pastas raiz onde você pode armazenar todos os elementos relacionados ao projeto, como configurações, extensões, etc. Ao selecionar *Save Workspace As*, será criado um arquivo *.code-workspace* com algumas configurações padrão, as quais você pode editar conforme desejar. 

1. No canto superior esquerdo do **VSCode**, clique em **File**.
2. Clique em **Open Folder**.
3. Selecione **git**, e clique em **OK**.
4. Novamente, no canto superior esquerdo, clique em **File**.
5. Clique em **Save Workspace As**, e após, em **OK**.
6. Mais uma vez, clique em **File**.
7. Clique em **Preference > Settings**.
8. Após, clique na aba **Workspace**.

![Imagem mostrando a tela esperada e a posição da aba Workspace](enxovalBB/referencias/imagens/vs_code_workspace.png)

10. No canto superior direito, clique em **Open Settings JSON** ![Imagem do ícone Open Settings. Descrição: uma folha de papel com uma seta apontando para a direita na lateral superior esquerda](enxovalBB/referencias/imagens/icone_open_settings.png).
11. No arquivo json **git.code-workspace**, atualize os dados conforme a seguir:
  ```
  {
    "folders": [
      {
        "path": "/git"
      }
    ],
    "settings": {
      "java.jdt.ls.home": "/usr/lib/jvm/java-17-openjdk-amd64"
    }
  }
  ```
12. Salve e feche o arquivo.
13. No menu lateral esquerdo do **VSCode**, clique novamente em **Remote Explorer** para abrir seu ambiente virtual.
14. Vá para **git /home/wsl/git** e clique no ícone da **seta para a direita** para conectar-se ao Pengwin.

  ![Imagem contendo pastas que podem ser abertas dentro do Penwin-WSL. A direita de cada caminho existe uma seta para a direita e dois outros ícones.](enxovalBB/referencias/imagens/vs_code_workspace_conectar.png)

## Passo 6: Verificar instalação e versão dos pacotes para cloud (opcional)

O PengWin inclui várias [ferramentas essenciais](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/enxovalBB/referencias/principais_softwares_pengwin.md) para desenvolver na Cloud BB. Caso queira validar que as ferramentas foram baixadas junto com a instalação do Pengwin:  

1. No **VSCode**, abra um novo terminal **(Ctrl+Shift+')**.
2. Execute os comandos abaixo. As saídas devem mostrar a versão instalada de cada ferramenta. 

| Comando | Saída (exemplo) |
| - | - |
|**java --version**| java 11.0.19|
|**javac --version**| javac 11.0.19|
|**docker --version**| Docker version 24.0.5, build ced0996|
|**docker-compose --version**| Docker Compose version v2.14.2|
|**git --version**| git version 2.34.1|
|**mvn -version**| Apache Maven 3.6.3|
|**oc version**| Client Version: 4.11.0-202212070335.p0.g1928ac4.assembly.stream-1928ac41|
|**curl --version**|curl 7.81.0 (x86_64-pc-linux-gnu)|

## Passo 7: Configurar o Openshift CLI

1. Acesse [Red Hat OpenShift Container Platform (RHOCP)](https://console.apps.k8sdesbb111.nuvem.bb.com.br/).
2. Faça o login com LDAP.
3. Clique no seu nome de usuário; isso abrirá um menu retrátil.
4. Clique em **Copy login command**. 
5. Clique em **Display Token**.
6. Copie o conteúdo do campo **Log in with this token**.
7. No **VSCode**, abra um novo terminal **(Ctrl+Shift+')**, cole e execute o comando iniciando com **oc login**. <br>

Exemplo
```
oc login --token=seu_token --server=https://api.k8sdesbb111.nuvem.bb.com.br:6443
```
9. Execute o comando **oc version --client** para testar se o OC foi configurado corretamente. <br>

Exemplo de saída 
```
Client Version: 4.11.0-202212070335.p0.g1928ac4.assembly.stream-1928ac41
Kustomize Version: v4.5.4
```
> :bulb: **Dica**
> 
>  Os comandos do kubectl funcionam no OC.

Se a conexão foi estabelecida, você terá duas janelas do VSCode abertas: uma conectada ao ambiente remoto e outra local no Windows. Mantenha apenas a janela com o **WSL:Pengwin-WSL** conectada ao ambiente remoto. Você pode abrir outras janelas conectadas à máquina remota ou não.

## Passo 8: Instalar extensões no ambiente remoto

No **passo 3** deste roteiro, foi instalado o pacote **Extension Pack for Java**. Após a conclusão da configuração do Pengwin, este pacote de extensões também deve estar no ambiente remoto. Se você buscar pelo pacote em **Extensions**, verá um banner indicando a necessidade de instalação. Refaça as ações do **Passo 3** para instalar as extensões do Java no ambiente remoto. <br>

![Imagem mostrando mesangem do VSCode indicando que e extensão não existe no ambiente remoto](enxovalBB/referencias/imagens/vs-code-install-extensions-pengwin.png)

> :information_source: **Observação** 
> 
> O sistema Linux que o BB utiliza com o Pengwin não tem uma interface gráfica. Toda interação com ele deve ser feita a partir de comandos em um terminal.

## Passo 9: Instalar OpenJDK21

1. No terminal do Pengwin, rode o camando `sudo apt-get install openjdk-21-jdk`.

> :bulb: **Dica**
> 
> A senha do PengWin é **wsl**.

2. Aguarde a conclusão da instalação.
3. Após a conclusão, rode o comando `sudo update-alternatives --config java` para mudar a versão do Java.
4. Selecione o **Java 21** digitando o número correspondente.

> :grey_exclamation: **Importante** 
> 
> O Quarkus 3 apresenta algumas incompatibilidades com determinadas versões do Java e do Maven. Ao usar o Quarkus 3, certifique-se de que a versão do Java é igual ou superior a 17 e a do Maven é igual ou superior a 3.9.5.

## Passo 10: Alterar a versão do Maven

> :bulb: **Dica**
> 
> O Pengwin possui alguns comandos exclusivos que facilitam o desenvolvimento. Abra um terminal (**Ctrl+Shift+'**), digite **ajuda** e aperte **ENTER** para ver a lista completa dos comandos.

No ambiente Cloud, é recomendado sempre utilizar as versões mais recentes do Maven. Para atualizar a versão, utilize os comandos exclusivos do Pengwin: 

1. Abra um terminal no VSCode (**Ctrl+Shift+'**).
2. Execute o comando `usarMvn395`. Lembre-se de verificar e alterar a versão do roteiro pela versão mais recentes do produto.

Com a conclusão deste roteiro, seu ambiente local está pronto, com todas as ferramentas necessárias para iniciar o desenvolvimento em Nuvem.

> :bulb: **Dica**
> 
> Para você que está iniciando na Cloud do BB, recomendamos matricular-se nos cursos disponíveis na [Plataforma Onboarding DevCloud](https://onboardingarq3.labbs.com.br/).

**Tags:** #pengwin #vscode #ambiente #local #corporativo 

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_ambiente_local_dev.md&internalidade=enxovalBB/Como_configurar_ambiente_local_dev)
