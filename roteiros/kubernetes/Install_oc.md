> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Install_oc.md&amp;action_name=kubernetes/Install_oc)
# Instalando o OC

No Windows, utilizando o Vagrant e o OpenShift, ao invés de usarmos o Kubectl, que vem instalado no Vagrant,  vamos utilizar o OC (OpenShift Command Line Interface (CLI)), vamos fazer a instalação.

Acesse: [Red Hat OpenShift Container Platform (RHOCP)](https://console.apps.k8sdesbb111.nuvem.bb.com.br/), faça login utilizando **LDAP**, clique no ícone "**?**" e depois em "**Command Line Tools**":


![](img/oc_command.png)

Agora clique em "Download oc for Linux for x86_64" e faça o download do arquivo, será algo parecido com:

![](img/oc_windowns.png)

Realize a extração do arquivo, clique com o botão direito do mouse sobre o arquivo, **7-Zip** e depois em **Extrair Aqui**.

![](img/oc_desempacota.png)

Copie o arquivo "**oc**" para a pasta utilizada para instalação do Vagrant (a pasta padrão é a kdimv, localizada em "C:\Users\F1234567\kdimv", onde F1234567 corresponde a sua matrícula).

Agora no **terminal do VSCode conectado ao Vagrant**, use os comandos:

Executar comando como root:
```shell
    sudo su
```
Executar comando como root:
```shell
    cp /vagrant/oc /usr/local/bin/
```
Executar comando como root:
```shell
    exit
```

Após isso, caso necessário, reinicie o terminal ou o VScode.
Agora é so fazer o [login seguindo estes passos](https://onboardingarq3.labbs.com.br/mod/page/view.php?id=76) 

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Install_oc.md&internalidade=kubernetes/Install_oc)

