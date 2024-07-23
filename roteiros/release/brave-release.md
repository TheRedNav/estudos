> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/brave-release.md&amp;action_name=release/brave-release.md)

---
tags:
  - "configurar"
  - "dns"
  - "brave"
  - "release"
---

> :exclamation: Dê um feedback para esse documento no rodapé.[^1]


**Sumário**

[[_TOC_]] 

# BRAVE RELEASE

A funcionalidade de **Release** do [BRAVE](https://brave.dev.intranet.bb.com.br) possibilita a configuração do release de aplicações da NuvemBB de forma facilitada. O objetivo é fornecer uma interface para edição dos arquivos do repositório de liberação conforme os padrões definidos no chart do BBAplic.

>Atenção!!! A ferramenta possui algumas restrições e sugerimos evitar seu uso em aplicações da NuvemBB com as seguintes características:
>- Tratar-se de aplicações de ***Monitoração***;
>- Fazer uso de ***Canário***;

Para acessar a funcionalidade de **Release** do Brave primeiramente você deverá **contextualizar o seu projeto**.  Para isso, basta selecionar a opção "Selecione um Projeto" do menu lateral, preencher os campos Repositório, Sigla e Projeto, e, em seguida, clicar em "Confirmar". 

![Contextualizar projeto](release/referencias/imagens/selecionarProjeto.png "Selecione um Projeto")

![Contextualizar projeto](release/referencias/imagens/selecaoProjeto.png "Contextualizar projeto")


Após a contextualização, acesse o menu lateral e escolha a opção **"Release"**. Em seguida, selecione o ambiente desejado.

![Funcionalidade release](release/referencias/imagens/selecionarRelease.png "Selecionar Release")

![Funcionalidade release](release/referencias/imagens/selecionarAmbiente.png "Selecionar Ambiente")

A seguir serão descritos os itens de configuração que compõem a funcionalidade de Release do Brave. 


## 1. Configurando o DNS da aplicação via Release do Brave

Para realizar a configuração do DNS, é necessário que a dependência do chart dnsingress esteja listada no requirements.yaml de seu projeto.
![Requirements](release/referencias/imagens/requirementsDNS.png "Requirements")


Caso ainda não tenha incluído esta dependência, a aba DNS não estará disponível no Brave.
![Requirements](release/referencias/imagens/semDNS.png "Requirements sem DNS")

![Aba DNS indisponível](release/referencias/imagens/semAbaDNS.png "Aba DNS indisponível")




Portanto, será necessário adicionar a dependência manualmente. Acesse o requirements.yaml de seu projeto e faça a inclusão:

![Acesse o projeto](release/referencias/imagens/acessarProjeto.png "Acessando o projeto")


Adicione a dependência no arquivo requirements.yaml, respeitando a indentação e faça commit da alteração:

![Atualizando Requirements](release/referencias/imagens/atualizandoRequirements.png "Atualizando o arquivo requirements.yaml")


Após realizar o commit, acesse o Brave novamente. A aba requirements estará atualizada e a aba DNS disponível:

![Requirements Atualizado](release/referencias/imagens/requirementsAtualizado.png "Aba Requirements atualizada")

![Aba DNS disponível](release/referencias/imagens/abaDNSdisponivel.png "Aba DNS disponível")


Se a dependência DNS já estiver listada no requirements.yaml, a aba DNS da funcionalidade de Release estará disponível:

![Aba DNS](release/referencias/imagens/abaDNS.png "Aba DNS")

Através do **"toggle"** você poderá habilitar ou desabilitar a utilização do DNS para a sua aplicação.

É permitido definir uma ou mais URLS. Ao adicionar uma URL, através do botão "+ Adicionar" você deve preencher a URL desejada no campo **Hostname**. E informar o **IngressClass**, que por default vem preenchido com "nginx".

A seguir, descrevemos os padrões de nomenclatura de URL definidos para o BB:

>Padrão de nomenclatura de URL para o ambiente de **Desenvolvimento**: 
>
>**subdominio.sigla.desenv.bb.com.br** 
>
>-api.bba.desenv.bb.com.br  
>-monitor.bba.desenv.bb.com.br  
>-prometheus.bba.desenv.bb.com.br  

>Padrão de nomenclatura de URL para o ambiente de **Homologação**:
>
>**subdominio.sigla.hm.bb.com.br** 
>
>-api.bba.hm.bb.com.br  
>-monitor.bba.hm.bb.com.br  
>-prometheus.bba.hm.bb.com.br  

>Padrão de nomenclatura de URL para o ambiente de **Produção**:
>
>**subdominio.sigla.servicos.bb.com.br** - Para acesso de máquina  
>**subdominio.sigla.intranet.bb.com.br** - Para acesso humano     
>
>-api.bba.servicos.bb.com.br  
>-monitor.bba.intranet.bb.com.br  
>-prometheus.bba.intranet.bb.com.br


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/brave-release.md&internalidade=release/brave-release)
