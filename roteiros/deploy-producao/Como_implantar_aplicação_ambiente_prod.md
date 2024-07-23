> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/deploy-producao/Como_implantar_aplicação_ambiente_prod.md&amp;action_name=deploy-producao/Como_implantar_aplicação_ambiente_prod)

# Como implantar uma aplicação no ambiente de produção

Esse roteiro explica como realizar os passos necessários para que uma aplicação atualizada seja implantada no ambiente de Produção. Alguns passos são feitos pelo DEV, outros exigem cargos específicos para execução. Na intro de cada passo está descrito quem executa a ação em questão.

> :information_source: **Observação** 
> 
> Esse roteiro pode ser utilizado como guia para implantar aplicações novas no ambiente de Produção porque os passos são os mesmos. No entanto, aplicações novas exigem o preenchimento de campos e informações que não serão abordados aqui. 

## Requisitos

- Uma História no quadro da sua equipe.
- Um *namespace* da sua sigla.
- Uma aplicação atualizada.

## Passo 1: Criar uma Entrega no GenTI

Após o DEV concluir a ação planejada para a História, ele cria a Entrega correspondente para avaliação. 

1. Acesse o [**GenTI**](https://genti.intranet.bb.com.br).
2. Clique na **História** correspondente. 
3. Na barra superior, clique na aba **Itens de Trabalho**.
4. Na seção **Criar Item de Trabalho**, clique em **Entrega**; uma janela abrirá.
5. Na aba **Visão Geral**, preencha as informações obrigatórias:
    * **Resumo**: identifique sua entrega.
    * **Categoria:** selecione o seu time.
    * **Planejado:** selecione a opção desejada.
6. Na aba **Links**, clique na **seta** ao lado de **Incluir Relacionado** e selecione **Incluir Item de Entrega**.
7. Na janela **Selecionar Itens de Trabalho**, selecione a história correspondente à entrega e clique em **OK**. 
8. Confirme que a história foi vinculada; ela deve aparecer no menu suspenso **Relacionado**.
9. Clique em **Salvar**.

## Passo 2: Cadastrar evidências de teste

O DEV inclui na História as evidências de que testes foram realizados com sucesso para que o Gestor de Valor possa homologar a História. 

1. Caso tenha fechado, abra novamente a **Entrega**.
2. Abaixo do botão **Salvar**, altere o status para **Solicitar Teste**.
3. Na **História**, navegue até a seção **Cenário de Teste**.
4. Clique em **Clique aqui para acessar o Teste de Aceitação**.
5. Na janela **Cadastro de Evidêncas de Testes**, preencha os campos:
    * **Tipo de Teste:** selecione entre as opções o tipo de teste realizado.
    * **Ambiente:** selecione o ambiente onde o teste de aceitação foi executado.
    * **Tipo de Artefato**: mantenha como GenTI.
    * **N° História/Ação/S.E/RDI**: a informação já vem vinculada.
    * **N° Entrega**: adicione o número da entrega feita no passo 1.
    * **Sigla:** escreva a sigla da sua equipe.
    * **Componente:** escreva o *namespace* da aplicação.
    * **Versão:** escreva a versão gerada pela imagem Docker.
    * **Resultado do Teste:** mantenha a opção **Passou**.
    * **Evidências:** inclua as evidências do teste realizado.
6. Clique em **Salvar**.
7. Altere o status da **História** para **Iniciar Homologação** e comunique seu Gestor.

## Passo 3: Homologar a História

O Gestor de Valor analisa a História e, se ela atender todos os critérios de aceitação, deve homologá-la. 

1. Na **História**, troque o status de **Iniciar Homologação** para **Homologada**.

> :grey_exclamation: **Importante** 
> 
> A Entrega também deve ter o status alterado para **Homologada**. Essa alteração deve ser feita pelo DEV. 

## Passo 4: Criar o *release* 

> :grey_exclamation: **Importante** 
> 
> Só é possível continuar o processo de implantação com a História e a Entrega homologadas no GenTI.

Com a homologação concluída, o DEV cria um *release* para alterar a informação da versão da aplicação. 

1. Abra o [Brave](https://brave.dev.intranet.bb.com.br/).
2. Na barra lateral esquerda, clique em **Selecione um Projeto**, representado pelo ícone da nuvem.
3. Na janela **Seleção de Projeto**, selecione seu **Repositório**, **Sigla** e **Projeto**.
4. Clique em **Confirmar**. Observe que o ícone da nuvem será renomeado com o título do seu projeto e apresentará novas opções de ações.
5. Na barra lateral esquerda, clique no ícone do foguete **Release**.
6. Selecione o ambiente de **Produção**. 
7. Na aba **BBAplic**, altere  a **Versão** para a versão do *build* gerado.
8. Clique em **Deploy**.
9. No campo **Informe uma nova branch**, escreva a *branch* para onde o projeto deve ser enviado. Nunca faça o *commit* direto na *master*.
10. No campo de texto, escreva o nome da *branch*. Esse nome aparecerá no título do pedido de *merge request*. 
11. Selecione o *checkbox* que confirma o *deploy*. 
11. Clique em **Confirmar**.  

## Passo 5: Realizar o *merge request*

O DEV solicita um *merge request*, porque no ambiente de Produção o responsável por realizar o *merge* na *branch master* é o AIC Fontes (Automação).
 
1. De volta a página principal **Release**, no lado direito da tela, clique no ícone do **Gitlab** para ser direcionado ao repositório.
2. No canto superior esquerdo, clique em **merge request** para mesclar a sua *branch* na *master*. 
3. No campo **Description**, escreva usando o padrão **#genti{número da Entrega}** e informe **#dd-MM-yyyy** e/ou **#hh:mm** para agendar o *deploy*. Exemplo: `#genti123456 #20-02-2024 #07:05`
4. Clique em **Create merge request**.
5. Localize a seção **ci-user** e veja o cargo permitido para aprovar a solicitação. Como requerente, você nunca poderá aprovar para si mesmo.

> :information_source: **Observação** 
> 
> Se a pontuação no Motor de liberação for maior que 160 pontos e a implantação for agendada fora da janela de horário do cliente, um colega de time pode aprovar para você.


## Passo 6: Implantar a aplicação 

A implantação é concluída quando alguém com o cargo exigido (esse cargo varia conforme a pontuação do Motor de Liberação) autorizar o lançamento da aplicação na Plataforma de Atendimento.

1. Abra a Plataforma na seção de [Tecnologia](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html#/).
2. No menu lateral esquerdo, clique em **Construção**, representado pelo ícone da chave inglesa.
3. Na seção **Ambiente de Integração Contínua**, clique em **Release de Software**.
4. Localize a aplicação desejada e selecione-a.
5. Confirme as informações e, se estiverem corretas, clique em **Aprovar**.

Pronto! Agora é só acompanhar o *deploy* no **ArgoCD**. Em alguns instantes a sua aplicação estará executando em produção. 

**Tags:** #deploy #produção #release #api #motor #liberação #entrega

## A seguir

- Leia o roteiro [Release de Software](https://fontes.intranet.bb.com.br/aic/publico/atendimento/-/blob/master/docs/Release%20Arq%203.0/Processo%20de%20Release%20Simplificado.md) para saber mais sobre a utilização do pipeline de Release de Software padrão ARQ 3.0
- Leia o roteiro [Motor de Liberação - Plataforma Distribuída e Cloud](https://fontes.intranet.bb.com.br/ath/publico/atendimentomotor/-/wikis/home) para entender como funciona a aplicação responsável pela automação das etapas de análise e quais são as exigências para o processo de liberação de softwares.

 ## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/deploy-producao/Como_implantar_aplicação_ambiente_prod.md&internalidade=deploy-producao/DeployProducao)
