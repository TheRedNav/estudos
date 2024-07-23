> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/solicitarVDI.md&amp;action_name=enxovalBB/solicitarVDI)

# Solicitar VDI

> :grey_exclamation: **Importante** 
> 
> O conteúdo do roteiro abaixo apresenta informações defasadas.

Para utilizar a VDI é necessário solicitar a criação, acessar via VMWareHorizon ou browser.

Solicite pelo [Fazaí](https://fazai.bb.com.br/vdi), lembrando de selecionar o sistema operacional Linux, com a Matriz SLED.

### 1.2.1 Instalar VMWare Horizon

Em [https://vdi.bb.com.br/](https://vdi.bb.com.br/) é possível acessar sua máquina pelo browser, ou realizar o download do VMWare Horizon.

Confira a seção **1.2 Instalação do Cliente VMware Horizon** no [arquivo de referência](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/referencias/VDI_Manual%20de%20ajuda%20VDI%20VMware%20-%20Wiki-BB.zip) para instalar o cliente VMWare Horizon.

> :grey_exclamation: **Importante** 
> 
> Para visualizar corretamente o arquivo de referência **VDI_Manual de ajuda VDI VMware - Wiki-BB.zip**, extraia-o para a sua máquina local e acesse o arquivo **HTML**.

### 1.2.2 Acessar VDI

O acesso a VDI é detalhado na seção **2.3 Acessando sua estação virtual** no [arquivo de referência](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/referencias/VDI_Manual%20de%20ajuda%20VDI%20VMware%20-%20Wiki-BB.zip).

### 1.2.3 Configurar a VDI

Abrir a `Central de Software` e instalar o **Docker** e executar o **Configura Docker** (deve aparecer no menu após a instalação do **Docker**).

Caso vá desenvolver em Java, instalar a **JDK 11**, **Maven** e **Maven - Configuração**. Após a instalção de todos, executar o **Maven - Configuração** para configurar o Maven para acessar o Artifactory com suas credenciais.

Após a instalação da **JDK 11**, confirme que o caminho da $JAVA_HOME será `/opt/jdk-XXX/`.

Na `Central de Software` tem duas IDEs para desenvolvimento em Java: **Eclipse (BBeclipse-jee)** e **VS Code**. Fica a critério do desenvolvedor qual utilizar.

Caso opte pelo **VS Code**, dê uma olhada na [lista de plugins recomendados](https://fontes.intranet.bb.com.br/dev/publico/roteiros/blob/master/IDEs/plugins-vscode.md) para a IDE.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/solicitarVDI.md&internalidade=enxovalBB/solicitarVDI.md)
