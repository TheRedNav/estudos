> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/arquivados/configuração-ambienteLocal-notebook-dell3410.md&amp;action_name=enxovalBB/arquivados/configuração-ambienteLocal-notebook-dell3410)

## Objetivo

Esse guia tem como objetivo configurar o Maven de funcionários para build local em Desktops DELL 3410 utilizando o Vagrant
 

## Pré-requisitos

Para atingir o objetivo, o usuario deve ter:

* Ter chave F com senha validada(logar);
* um notebook dell 3410 ou 3420 fornecido pelo BB com Windows;
* Solicitar administrador temporário na plataforma BB;
* Conceder os seguintes acessos do item 1 abaixo

## 1. - Concessão de acessos

#### 1.1 - Solicite os seguintes acessos ao seu gerente/PO:

* ALMPLTO: Acesso ao Artifactory - atf.intranet.bb.com.br
* ALMFDT99: acesso de desenvolvedor da sigla - Alterar T99 para sua sigla, ex: ALMFDVIP;
* ALMFET99: acesso de mantenedor da sigla - Alterar T99 para sua sigla, ex: ALMFEVIP;
* MWGAAA4: para permitir a instalação do Kubectl;
* VPN07: Acesso Especial (Restrito à Ditec);


#### 1.2 - Como solicitar acesso de administrador temporário do notebook

Anote o hostname da sua máquina, digite:

```
C:\Users\F9999999>hostname
BBTMF-BZZ1YZZ
```

Solicitar o acesso de administrador temporário do notebook dell via plataforma: 

```
"Plataforma BB" -> "Administrativo" -> "EStação de Trabalho" -> "Administrador temporário". 

Após solicitar acesso na plataforma, será necessário: 
- Despacho pelo gerente (AP5);
- Reinicialização da máquina; 
- Ativação do acesso no aplicativo BB Admin do windows
```


#### 1.3 -  Desative o Hyper-V do Windows

Etapas:

```
- Clique no ícone de iniciar do Windows e depois em  "programas e recursos";
- Encontre a opção "ativar ou desativar recursos do Windows";
- Desative o Hyper-V desmarcando a caixa de seleção e clicando OK.
```



## 2. Instalação do Vagrant


#### 2.1 - Criar a pasta KDIMV

Abra o terminal do windows (prompt), altere para a pasta raíz e Crie a pasta kdimv:
```
C:\Users\F9999999>cd \

C:\>mkdir kdimv

C:\>cd kdimv

C:\kdimv>
```

#### 2.2 - Download da imagem Vagrant 

Realize o download do vagrant: [link-download-imagem-vagrant](http://atf.intranet.bb.com.br/artifactory/bb-binarios-local/gaw/vagrant_2.2.16_x86_64.msi) e  execute o instalador


## 3. Instalação do VSCode

### 3.1 - Download do VSCode 
Realize o download do editor vscode: [link-download-vscode](https://code.visualstudio.com/sha/download?build=stable&os=win32-x64-user) e clique no instalador, procedendo com a instalação;


### 3.2 -  Instale a extensão do vscode chamada: Remote - "SSH: ms-vscode-remote.remote-ssh

Esta extensão vai conectar o vscode ao vagrant: 


```
Clique na opção Extensions no menu, ou acesse pelas teclas de atalho "CTRL + SHIFT + X" e

Instale a seguinte extensão: "Remote - SSH: "ms-vscode-remote.remote-ssh";
```

Obs - Caso a rede do bb ofereça restrições de bloqueio, alternativas:

```
A - Utilizar a rede BB Wireless(desconectar cabo), a mudança de rede é automática pelo  Cisco AnyConnect;

ou

B - Download e instalacao manual(consultar documentação vscode oficial)
```


## 4. Configurar settings.xml

#### 4.1 - Gerar arquivo settings.xml

4.1.1 - Acesse https://atf.intranet.bb.com.br e efetue primeiro o login com sua chave e senha do SisBB;

4.1.2 - Após login, no meio da tela, encontrar a área "Set Me Up" e  clicar em "bb-maven-repo";

4.1.3 - Na tela de bb-maven-repo é necessário logar novamente para gerar o arquivo de configuração com o seu token;

4.1.4 - Clique em "Generate Maven Settings", sendo que todas as opções devem apontar para bb-maven-repo na janela "set me up";

4.1.5 - Clique em "Generate Settings", e logo em seguida em "Download Snippet";


#### 4.2 - Conferir o arquivo settings.xml

4.2.1 - Abra o arquivo "settings.xml" no Bloco de Notas e verifique se contém um trecho semelhante ao abaixo contendo seus dados gerados:

```
<username>f9999999</username>
<password>XXXXXXXXXXXXXXXXXXXXXXXXX</password>
```

4.2.2 - logo em seguida, copie o arquivo settings.xml gerado para a pasta do vagrant: "C:\kdimv"


## 5.  Provisionar a máquina virtual para o Vagrant


####  5.1 - Adicionar o box kdi

Entre na pasta do Vagrant e digite o comando 

```
cd c:\kdimv [ENTER]

C:\kdimv> vagrant box add kdi-box https://binarios.intranet.bb.com.br:443/artifactory/generic-bb-binarios-gaw-local/publico/kdi-box.json [ENTER]
```

Aguarde pela mensagem abaixo: `box: Successfully added box 'kdi-box' (v1.0.1) for 'virtualbox'`


Inicie a máquina virtual com o comando: `vagrant init kdi-box [ENTER]`


#### 5.2 -  alteração no arquivo Vagrantfile

##### 5.2.1 - Inserir as linhas:

Com o notepad, adicione as seguintes linhas ao vagrantfile:

```
config.vm.network "forwarded_port", guest: 8081, host: 8091, host_ip: "127.0.0.1"
config.vm.network "forwarded_port", guest: 16686, host: 16686, host_ip: "127.0.0.1"
```


##### 5.2.2 - Substitua os trechos abaixo:

A - Retirar este trecho:

```
# config.vm.provision "shell", inline: <<-SHELL
#   apt-get update
#   apt-get install -y apache2
# SHELL
```

B - Inserir este trecho:

```
config.vm.provision "shell", inline: <<-SHELL
  openssl s_client -showcerts -connect atf.intranet.bb.com.br:5001 < /dev/null | sed -ne '/-BEGIN CERTIFICATE-/,/-END CERTIFICATE-/p' > ca.crt
  mv ca.crt /usr/local/share/ca-certificates/
  update-ca-certificates
SHELL
```

C - Salve o arquivo. 

OBS:
* Caso esteja acessando a rede do BB através de proxy(ex: contratados), realize as configurações adicionais descritas neste  [link](https://fontes.intranet.bb.com.br/gaw/publico/gaw-kdi-box#configurar-proxy-para-rede-de-terceirizadas)



## 6.  Conectar Vagrant

#### 6.1 - Inicialização - Primeira execução do ambiente Vagrant

Realize este procedimento para a primeira execução do Vagrant, execute:

```
cd c:\kdimv [ENTER]

vagrant up [ENTER]

vagrant ssh [ENTER]

```

você está agora conectado ao terminal dentro do Vagrant

#### 6.2 - Configurar conexão entre Vagrant e VSCode
* Este procedimento é para utilizar o vscode acessando o vagrant, permite ter editor visual 

TO-DO - REFAZER ROTEIRO VISUAL - TELAS
TO-DO - REFAZER ROTEIRO VISUAL - TELAS
TO-DO - REFAZER ROTEIRO VISUAL - TELAS

#### 6.3 - Conectar com o ambiente remoto

TO-DO - REFAZER ROTEIRO VISUAL - TELAS
TO-DO - REFAZER ROTEIRO VISUAL - TELAS
TO-DO - REFAZER ROTEIRO VISUAL - TELAS


## 7 - Setar Java e Maven no Vagrant

#### 7.1 - Selecionar versões para desenvolvimento

Com o terminal conectado ao Vagrant, digite:

```
sudo useJDK11.sh

sudo useMaven363.sh
```


#### 7.2 - Configurar kubectl no Vagrant

Siga este roteiro: [link-roteiro-instalar-kubectl](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/blob/master/roteiros/geracao-kubeconfig.md)


#### 7.3 - Como desligar o ambiente

Após fechar o VSCode execute os comandos abaixo no terminal do Windows:

```
cd c:\kdimv [ENTER]
vagrant halt [ENTER]
```






## 8 - Resultados Finais

#### 8.1 - Realize o teste de subir a máquina virtual do Vagrant

Entre na pasta KDIMV e inicialize o vagrant:
```
cd c:\kdimv [ENTER]

vagrant up [ENTER]
```

Agora conecte em um terminal dentro do Vagrant:

```
vagrant ssh [ENTER]
```

* Para sair desse terminal e voltar ao do Windows utilize o comando exit. O Vagrant permanecerá em execução.




## 9 - Referências

Documentação do firefox em: `https://support.mozilla.org`

Documentação do vagrant em: [link-documentacao-vagrant](https://www.vagrantup.com/docs)

Roteiro completo(visual) - Instalação do ambiente local no notebook dell em:
 [link-roteiro-notebook](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/enxovalBB/arquivados/roteiro-config-dell3410.md)


## Tags
#guia #notebook #dell #ambientelocal #certificados #devops---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Build/configuração-ambienteLocal-notebook-dell3410.md&internalidade=Diataxis/How-To/Build/configuração-ambienteLocal-notebook-dell3410)
