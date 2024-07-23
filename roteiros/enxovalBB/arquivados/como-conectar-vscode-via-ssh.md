> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/arquivados/como-conectar-vscode-via-ssh.md&amp;action_name=enxovalBB/arquivados/como-conectar-vscode-via-ssh)

## Objetivo

Esse guia tem como objetivo configurar a conexão entre Vagrant e VSCode, de modo que o desenvolvedor consiga utilizar um editor/IDE conectado ao Vagrant e ter acesso às funcionalidades gráficas


## 1 - Pré-requisitos e Acessos

Este How-To se utiliza dos mesmos pré-requisitos e acessos deste roteiro:![link-roteiro-config-dell3410](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/c1337189-versaobeta/enxovalBB/arquivados/configura%C3%A7%C3%A3o-ambienteLocal-notebook-dell3410.md)

importante: Gentileza verificar os itens 1.1 e também 1.2 antes de prosseguir



## 2 - Configurar conexão entre Vagrant e VSCode

#### 2.1 -  Abra o VSCode e clique no ícone do plugin Remote Explorer no menu lateral:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_003.png)


#### 2.2 - Clique em "+" (Add New):

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_004.png)

#### 2.3 - Na tela a seguir digite: 

```
ssh vagrant@127.0.0.1 -p 2222 [ENTER]
```

conforme tela:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_006.png)

#### 2.4 - Arquivo config

Na pasta do usuário, que pelo nosso exemplo seria C:\Users\f9999999\.ssh\config

Clique no arquivo  ` .ssh/config` .  

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_007.png)


#### 2.5 - Conectar vscode

Para se conectar clique no ícone connect:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_008.png)

e depois:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_009.png)


Quando solicitado, digite a senha "vagrant" + [ENTER]:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_0010.png)


#### 2.6 - Observacoes finais do item 2.5

2.6.1 -  (eventual) Se solicitado, informe que o tipo de sistema remoto é "Linux".

2.6.2 - Caso ocorra algum erro de conexão verifique se o arquivo .ssh/config está com essa configuração:

```
Host 127.0.0.1
  HostName 127.0.0.1
  User vagrant
  Port 2222
```

E verifique também se a máquina virtual está rodando (vagrant up).


## 3 - Uso do VSCode conectado na máquina virtual no dia a dia

### 3.1 - Verificar conexão com o ambiente remoto

Verifique se o vscode está conectado ao vagrant conforme abaixo: A conexão pode ser verificada pela área em verde no canto inferior, que está destacada na imagem.

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_0011.png)

Feche as demais instâncias do vscode e mantenha apenas a janela conectada conforme figura acima


### 3.2 -  Instalação de extensões no ambiente remoto

 Instalar a extensão: "Extension Pack for Java"
 
 Clicar na aba de extensões. Aparecerá aviso indicando que é necessário instalar também para o ambiente remoto

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_0012.png)

...

### 3.3 -  Abertura de pastas e arquivos

Utilize o menu  para abrir pastas ou clonar repositórios a partir do fontes.intranet.bb.com.br:


![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_0013.png)

### 3.4 -  Interação com o termina do sistema remoto



## 4 - Resultados Finais

O sistema Linux que utilizamos com o Vagrant não tem uma interface gráfica, é utilizado um terminal de comandos. O VSCode ajuda na parte visual. 

Se você consegue conectar um terminal no vagrant pelo vscode, este how-to está completo

então abra um terminal no vscode para interagir com o ambiente remoto provisionado:

![teste-imagems-2](enxovalBB/arquivados/imagens/img-como-conectar-vscode-via-ssh/img_0014.png)


# Referências

*Consulte o guia de referência x para obter uma lista completa de opções.*

# Tags
#guia #etapa_do_devops #vscode #vagrant 
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Build/como-conectar-vscode-via-ssh.md&internalidade=Diataxis/How-To/Build/como-conectar-vscode-via-ssh)
