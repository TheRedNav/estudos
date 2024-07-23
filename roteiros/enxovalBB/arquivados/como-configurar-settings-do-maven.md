> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/arquivados/como-configurar-settings-do-maven.md&amp;action_name=enxovalBB/arquivados/como-configurar-settings-do-maven)

## Objetivo

Esse guia tem como objetivo configurar o arquivo `settings.xml` para build dos projetos Java com Maven.


## Pré-requisitos

* Ter um notebook dell 3410 ou 3420 fornecido pelo BB;
* Ter sistema operacional Windows 10 fornecido pelo BB;
* Ter instalado a imagem Vagrant (kdi-box);
* Navegador Firefox na versão homologada e com os certificados do BB instalados;


OBS: o site do ATF (https://atf.intranet.bb.com.br) encontra-se em migração para o Binários (https://binarios.bb.com.br).


## 1 - Download dos certificados necessários para acesso ao site do ATF :

### 1.1 - Realize o download dos certificados abaixo, importe no navegador e marque "Confiar sempre":


- Certificado Raiz AC Banco do Brasil v3  --> [raiz_v3](https://pki.bb.com.br/ACRAIZC/cacerts/raiz_v3.der)

- Autoridade Certificadora Servidores v1  -->  [acsr_v1](https://pki.bb.com.br/ACINTA5/cacerts/acsr_v1.der)

Mais detalhes de como importar os certificados listados acima, podem ser encontrados neste roteiro [aqui](link).


## 2 - Acesso ao site do ATF:

#### 2.1 - Acessar o site do ATF:

2.1.1 - Abra o navegador Firefox, que tenha os dois certificados acima instalados;

2.2.1 - Acesse https://atf.intranet.bb.com.br, clique em "Log In" e efetue o login com sua chave (f ou c em minúsculo) e senha do SisBB;


#### 2.2 - Gerar arquivo settings.xml

2.2.2 - Após login no site do ATF, no meio da tela, encontrar a área "Set Me Up" e  clicar em "bb-maven-repo";

2.2.3 - Na tela de bb-maven-repo é necessário logar novamente para gerar o arquivo de configuração com o seu token;

2.2.4 - Clique em "Generate Maven Settings", sendo que todas as opções devem apontar para bb-maven-repo na janela "set me up";

2.2.5 - Clique em "Generate Settings", e logo em seguida em "Download Snippet";


#### 2.3 - Conferir o arquivo settings.xml

2.3.1 - Abra o arquivo "settings.xml" no Bloco de Notas e verifique se contém um trecho semelhante ao abaixo contendo seus dados gerados:

```
<username>f9999999</username>
<password>XXXXXXXXXXXXXXXXXXXXXXXXX</password>
```

#### 2.4 - Copiar o arquivo settings.xml

2.4.1 - logo em seguida, copie o arquivo settings.xml gerado de "C:\Users\f9999999\Downloads" para a pasta do vagrant: "C:\kdimv"

2.4.2 - caso precise atualizar o token, 




---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Build/como-configurar-settings-do-maven.md&internalidade=Diataxis/How-To/Build/como-configurar-settings-do-maven)
