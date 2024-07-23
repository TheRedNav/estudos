> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/arquivados/como-instalar-certificados-navegador-para-windows.md&amp;action_name=enxovalBB/arquivados/como-instalar-certificados-navegador-para-windows)

## Objetivo

Esse guia tem como objetivo configurar certificados no navegador firefox e sistema operacional windows, fornecido com o notebook DELL 3410/3420.

Os certificados devem ser instalados nos 3 ambientes do BB: desenvolvimento, homologação e produção


## Pré-requisitos

* Ter um notebook dell 3410 ou 3420 fornecido pelo BB;
* Ter Sistema operacional Windows 10 fornecido pelo BB;
* Navegador homologado Firefox


## 1 - Download de todos os certificados necessários:

### 1.1 - Realize o download dos 3 certificados de desenvolvimento abaixo:


- Certificado Raiz AC Banco do Brasil v3  --> [raiz_v3](https://pki.desenv.bb.com.br/ACRAIZC/cacerts/raiz_v3.der)


- Autoridade Certificadora Usuários  --> [acus_v1](http://pki.desenv.bb.com.br/ACINTB2/cacerts/acus_v1.der)

- Autoridade Certificadora Servidores v1  -->  [acsr_v1](http://pki.desenv.bb.com.br/ACINTA5/cacerts/acsr_v1.der)


### 1.2 - Realize o download dos 3 certificados de homologação abaixo:


- Certificado Raiz AC Banco do Brasil v3  --> [raiz_v3](https://pki.hm.bb.com.br/ACRAIZC/cacerts/raiz_v3.der)

- Autoridade Certificadora Usuários  --> [acus_v1](https://pki.hm.bb.com.br/ACINTB2/cacerts/acus_v1.der)

- Autoridade Certificadora Servidores v1  -->  [acsr_v1](https://pki.hm.bb.com.br/ACINTA5/cacerts/acsr_v1.der)



### 1.3 - Realize o download dos 3 certificados de produção abaixo:

- Certificado Raiz AC Banco do Brasil v3  --> [raiz_v3](https://pki.bb.com.br/ACRAIZC/cacerts/raiz_v3.der)

- Autoridade Certificadora Usuários  --> [acus_v1](https://pki.hm.bb.com.br/ACINTB2/cacerts/acus_v1.der)

- Autoridade Certificadora Servidores v1  -->  [acsr_v1](https://pki.bb.com.br/ACINTA5/cacerts/acsr_v1.der)



## 2 - Setar confiabilidade
...PENDENTE

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Build/como-instalar-certificados-navegador-para-windows.md&internalidade=Diataxis/How-To/Build/como-instalar-certificados-navegador-para-windows)
