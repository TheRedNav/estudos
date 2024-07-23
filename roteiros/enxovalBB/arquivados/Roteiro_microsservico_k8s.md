> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

**Passo a passo para criação de microsserviço na BB Cloud**
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/arquivados/Roteiro_microsservico_k8s.md&amp;action_name=enxovalBB/arquivados/Roteiro_microsservico_k8s)

Este roteiro contém todos os passos necessários para implementação e implantação de uma aplicação na BB Cloud, **mas não dispensa a formação básica indicada no curso da plataforma Digitizar [Desenvolvimento na nuvem BB](https://digitizar.intranet.bb.com.br/digitizar/course/view.php?id=249)**

Caso tenha alguma dificuldade durante a execução do roteiro, pode abrir uma [Issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues) na área de atendimento da Arq 3.0 (selecionando o template "microsserviço"). 


...

**Sumário**

[[_TOC_]]


...

**Configurações extras**

- [Roteiros para configurações adicionais](https://fontes.intranet.bb.com.br/dev/publico/roteiros)

...

# 1. Definir e Configurar Ambiente de Desenvolvimento

Atualmente há duas opções de configuração de ambiente suportado para o desenvolvimento em nuvem no BB:
- Configuração Enxoval BB (Windows 10 / Dell 3410 / 3420)
- Solicitar VDI (Matriz Linux SLED)

## 1.1 Configuração Enxoval BB (Windows 10 / Dell 3410 / 3420)

Se você recebeu o notebook Dell 3410 ou 3420 com Windows 10 e deseja configurar o ambiente nesse computador, siga esse roteiro:

- https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/EnxovalBB/roteiro-config-dell3410.md

## 1.2 Solicitar VDI

Para utilizar a VDI é necessário solicitar a criação, acessar via VMWareHorizon ou browser.

Solicite pelo [Fazaí](https://fazai.bb.com.br/vdi), lembrando de selecionar o sistema operacional Linux, com a Matriz SLED.

### 1.2.1 Instalar VMWare Horizon

Em [https://vdi.bb.com.br/](https://vdi.bb.com.br/) é possível acessar sua máquina pelo browser, ou realizar o download do VMWare Horizon.

Os primeiros passos [deste outro roteiro](https://wiki.bb.com.br/index.php/VDI/Manual_de_ajuda_VDI_VMware) detalham a instalação do VMWare Horizon.

### 1.2.2 Acessar VDI

O acesso a VDI é detalhado no passo **Acessando sua estação virtual** [deste outro roteiro](https://wiki.bb.com.br/index.php/VDI/Manual_de_ajuda_VDI_VMware).

### 1.2.3 Configurar a VDI

Abrir a `Central de Software` e instalar o **Docker** e executar o **Configura Docker** (deve aparecer no menu após a instalação do **Docker**).

Caso vá desenvolver em Java, instalar a **JDK 11**, **Maven** e **Maven - Configuração**. Após a instalção de todos, executar o **Maven - Configuração** para configurar o Maven para acessar o Artifactory com suas credenciais.

Após a instalação da **JDK 11**, confirme que o caminho da $JAVA_HOME será `/opt/jdk-XXX/`.

Na `Central de Software` tem duas IDEs para desenvolvimento em Java: **Eclipse (BBeclipse-jee)** e **VS Code**. Fica a critério do desenvolvedor qual utilizar.

Caso opte pelo **VS Code**, dê uma olhada na [lista de plugins recomendados](https://fontes.intranet.bb.com.br/dev/publico/roteiros/blob/master/IDEs/plugins-vscode.md) para a IDE.

# 2. Criar projeto BB Cloud

> :exclamation: Caso necessite discutir a solução antes de criar o projeto, abra uma [issue](<https://fontes.intranet.bb.com.br/dev/publico/atendimento/issues>) pra gente com suas dúvidas, utilizando o template "solicitação de consultoria". 

A criação de projetos BB Cloud no Kubernetes é feita através do  [Portal Nuvem](https://portal.nuvem.bb.com.br/). Você pode seguir as instruções do roteiro [Criando Microserviços pelo Portal Nuvem](./ofertas/CriandoMicroservico.md).  

Serão criados por essa automação os seguintes itens:

| Item                                    | Descrição                                                     |
| --------------------------------------- | ------------------------------------------------------------- |
| Projeto (codebase)                      | Código da aplicação cloud                                     |
| Repositório de releases desenvolvimento | Repositório contendo manifestos kubernetes do release DEV     |
| Repositório de releases homologação     | Repositório contendo manifestos kubernetes do release HML     |
| Repositório de releases produção        | Repositório contendo manifestos kubernetes do release PRD     |
| Jenkins Job                             | Job de compilação no Jenkins                                  |
| Hook Integração Fontes-Jenkins          | Acionamento da compilação nos eventos de Push do repositório  |
| Chart dummy no Releases                 | Repositório de releases populado com chart dummy              |
| Deploy em Desenvolvimento               | Configurado deploy no ArgoCD para ambiente de desenvolvimento |
| Deploy em Homologação                   | Configurado deploy no ArgoCD para ambiente de homologação     |
| Deploy em Produção                      | Configurado deploy no ArgoCD para ambiente de produção        |
| Hook integração Fontes-ArgoCD           | Hook para sincronizar ArgoCD e Fontes                         |

# 3. Clonar o projeto gerado

Clonar no git significa copiar o projeto do repositório remoto (fontes) para sua máquina local. 


```bash
git clone <endereco-repositorio>
```

# 4. Criando um projeto BB Cloud utilizando o Brave

Acesse o [Brave](https://brave.dev.intranet.bb.com.br) para gerar seu projeto, na opção **Plan**. 

Após o download, descompacte o arquivo no seu diretório de trabalho (ex.: \kdimv).


## 4.1 Configuração GIT

Abra esta pasta e execute o comando abaixo para transformá-la em um projeto git:

```
git init
```

Logo após, execute o comando abaixo para setar a url do projeto git como a do projeto criado no ofertas. Não se esqueça de trocar <nome-projeto-com-sigla> pelo nome do seu projeto. Ex: dev-projeto-exemplo.

```
git remote add origin https://fontes.intranet.bb.com.br/dev/<nome-projeto-com-sigla>/<nome-projeto-com-sigla>.git
```

O próximo passo é atualizar o seu código com o código existente no repositório remoto. Quando a oferta é executada, o repositório do projeto já é criado com alguns arquivos como o Jenkinsfile, o README e o CHANGELOG. Execute os comandos abaixo para fazer esta sincronização:

```
git fetch origin
git pull origin master
```

Pronto. Agora é só fazer as alterações no código para prover sua lógica de negócio e comitar usando os seguintes comandos:

```
git add .
git commit -m 'Commit Inicial'
git push --set-upstream origin master
```

# 5. Gerar uma nova versão da imagem do seu projeto no ATF

Altere a versão do seu projeto no arquivo **pom.xml** (Java) ou **package.json** (NodeJs). Utilize o versionamento semântico como descrito em https://semver.org/. Faça o _commit_ e _push_ das alterações. O push irá acionar a esteira de _build_ no [Jenkins](https://cloud.ci.intranet.bb.com.br/).

Você pode acessar o [Jenkins](https://cloud.ci.intranet.bb.com.br/) para verificar se todos os passos da esteira foram executados com sucesso, conforme abaixo:

![Execução da esteira Jenkins para projeto Java](./imagens/Jenkins-EsteiraJavaNova.png)

Ao fim do processo de _build_ será gerada uma versão da imagem do seu projeto no [Artifactory](https://atf.intranet.bb.com.br/artifactory/webapp/#/artifacts/browse/tree/General/bb-docker-repo/bb).

# 6. Configuração e deploy em desenvolvimento

Recomendamos que, a fim de evitar erros de edição manual do values.yaml, as configurações sejam feitas através do Brave.

- Acesse o [Brave](https://brave.dev.intranet.bb.com.br) para gerar seu projeto, na opção **Release**. 

- Siga [essa outra documentação](<https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/bbdev-release.md>) para configuração do seu namespace.

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Roteiro_microsservico_k8s.md&internalidade=Roteiro_microsservico_k8s)
