> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/ofertas/Como_gerenciar_microsserviços_Portal_Nuvem.md&amp;action_name=ofertas/Como_gerenciar_microsserviços_Portal_Nuvem)

# Como gerenciar microsserviços pelo Portal Nuvem

Este roteiro ensina como gerenciar microsserviços através do Portal Nuvem (Novo Ofertas), incluindo a criação e desprovisionamento. Microsserviços são componentes isolados que realizam tarefas específicas e comunicam-se com outros microsserviços via APIs. Em ambientes cloud, os microsserviços servem para que os grupos trabalhem de forma independente.

## Requisitos

* [Certificados instalados](https://fontes.intranet.bb.com.br/sgh/publico/atendimento/-/wikis/Portal-OaaS/01-Primeiros%20Passos#acesso-ao-portal-de-ofertas).
* [Grupo de acesso](https://fontes.intranet.bb.com.br/sgh/publico/atendimento/-/wikis/Portal-OaaS/03-Inst%C3%A2ncias) dentro do Portal.
* Acesso **ALMFD** na sigla onde será criado o projeto.
* Sigla com o status **Em Produção** no DPR (Sisbb -> DPR 02.71).
* Verificar  os critérios técnicos na página [Devo ir para a nuvem](https://cloud.dev.intranet.bb.com.br/devo-ir-para-nuvem).
> :bulb: **Dica** 
> 
> Consulte o roteiro de [referência das entradas e saídas](./ofertas/referencias/tabela_input_output.md) relacionadas aos Microsserviços, caso tenha dúvidas sobre como preencher os campos.

## Criar um microsserviço pela interface

1. Acesse o [Portal OAAS](https://portal.nuvem.bb.com.br/).

2. No menu lateral esquerdo, vá para **Catálogo de Serviços**, identificado pelo ícone da caixa aberta, e navegue até a categoria **Desenvolvimento**.

3. Clique em **Microsserviços**. 

4. Preencha o formulário:

    * **Informe um nome para a sua instância:** escreva o nome da instância no Portal Nuvem. Recomendamos seguir o padrão ‘ambiente-sigla-aplicação’.
    > :warning: **Atenção** 
    > 
    > Projetos terminados com o sufixo **-piloto** serão tratados como pilotos pela esteira.
    * **Grupo:** no menu suspenso, selecione o grupo onde ficará o microsserviço no Portal.
    * **Sigla:** selecione a sigla para o seu microsserviço.
    * **Nome do Microsserviço:** escreva o nome do microsserviço, sem a sigla.
    * **Descrição do Microsserviço:** campo de texto livre para descrição do microsserviço.
    * **Uor Equipe Responsável:** informe a UOR da equipe ou divisão que será responsável pelo microsserviço. 
    > :grey_exclamation: **Importante** 
    > 
    > A informação da equipe poderá ser utilizada em processos da Ditec, por exemplo, para estabelecer a equipe responsável pela resolução de RDIs. 
    * **Arquiteturas:** clique em **Adicionar** para abrir o menu suspenso e selecione as arquiteturas de referência disponíveis que atenderão às necessidades da solução.
    * **Tipo de Projeto:** selecione a linguagem que será utilizada no projeto. 
    > :bulb: **Dica** 
    > 
    > Para projetos Java e Javascript, é possível inicializar o repositório com código base gerado pelo BBDevGenerator. Para isso, habilite a opção **Criar projeto com código base** e selecione a **Versão do Projeto**. Os outros campos são dados opcionais que podem ser preenchidos, caso necessário.
    * **Solução / Serviço:** selecione em que tipo de cluster sua aplicação deve ser hospedada. Se a sua aplicação não precisa rodar num dos clusters especializados disponíveis, selecione a opção **Geral**.
</div>

5. Clique em **Criar**; a janela **Resumo do provisionamento** abrirá.

![ ](./imagens/Ofertas-tela5.png)

6. Após revisar as informações, clique em **Confirmar** para concluir a criação do microsserviço. Uma nova janela com a mensagem de ‘solicitação realizada com sucesso’ aparecerá na tela.

Agora, você deve aguardar a aprovação do **Arquiteto Responsável** para concluir a criação do seu microsserviço. Para conferir o status do seu pedido, acesse a aba [Minhas solicitações](https://portal.nuvem.bb.com.br/requests/user).

> :grey_exclamation: **Importante**  
> 
> O **Arquiteto Responsável** precisa do papel **DEVARQAP** vinculado ao seu perfil para autorizar a solicitação. Além de aprovar a criação, o arquiteto ficará vinculado como o responsável pelo microsserviço, portanto peça a aprovação para um arquiteto da equipe encarregada pelo microsserviço em questão.

Após a aprovação, localize em [Minhas instâncias](https://portal.nuvem.bb.com.br/view-instances/all) o seu microsserviço. Clique em **Detalhar** e confirme que todos os objetos necessários foram criados. Confira também as URLs para acessar cada um deles (repositórios no fontes, job no Jenkins, namespace no Kubernetes, etc).

  ![ ](./imagens/Ofertas-tela7.png)

## Desprovisionar o projeto

Faça essa solicitação se o projeto contiver erros ou se foi descontinuado. O processo de remoção inclui as seguintes etapas: 

* Remoção dos *namespaces* do Kubernetes nos três ambientes. 
* Remoção do *job* no Jenkins. 
* Remoção dos repositórios de *release*. 
* Arquivamento do *namespace* no Catálogo de Aplicações (aplica-se a ofertas criadas após 06-07-2021). 
* Se o projeto estiver associado à sigla T99, remoção do repositório de código. 
* Se o projeto estiver associado a outra sigla, ele será apenas arquivado, permitindo a consulta do código. Se necessário, é possível solicitar o desarquivamento.

1. Acesse o [Portal OAAS](https://portal.nuvem.bb.com.br/).

2. No menu lateral esquerdo, vá para **Instâncias**, identificado pelo ícone do cubo.

3. No lado direito da tela, clique em **Minhas instâncias**. Serão listadas todas as instâncias que você tem acesso.

4. Localize o microsserviço (status Ativo) que deseja desprovisionar. 

5. Clique em **Opções**.

6. Clique em **Remove Instância**. 

7. Na tela de confirmação de confirmação da exclusão da instância, escreva o **ID** da sua instância e selecione o **Motivo do desprovisionamento**. 

8. Clique em **Remover**; o processo de exclusão será iniciado.

Ao final do processo, o status do microsserviço será atualizado para **Removido**. 

**Tags:** #microsserviço # criar #desprovisionar #portaloaas

## A seguir

* Acesse [este link](https://web.microsoftstream.com/video/b7c41998-7e54-494c-a468-688b2cc37fde) caso queira aprender sobre esse assunto em vídeo. É preciso solicitar acesso ao Office 365.
* Leia o roteiro [Criação e desprovisionamento de microsserviços por API](/ofertas/arquivados/ofertas_CriandoMicroservico.md#cria%C3%A7%C3%A3o-e-desprovisionamento-de-microsservi%C3%A7os-por-api) para configurar um microsserviço via API.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/ofertas/Como_gerenciar_microsserviços_Portal_Nuvem.md&internalidade=ofertas/Como_gerenciar_microsserviços_Portal_Nuvem)
