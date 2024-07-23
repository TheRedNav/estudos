> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Como_configurar_aplicacao_para_ser_monitorada.md&amp;action_name=monitoracao/Como_configurar_aplicacao_para_ser_monitorada)

# Como configurar uma aplicação para ser monitorada

Este roteiro tem como objetivo orientar os responsáveis por monitorar uma aplicação sobre como configurá-la por meio do **Brave** para que seja feito o monitoramento através da ferramenta [Prometheus](https://prometheus.io/), que exibirá as métricas de forma gráfica pelo [Grafana](https://prometheus.io/).

> :information_source: **Observação** 
> 
> Para projetos novos, use a stack [Openshift/AppDynamics](https://onboardingarq3.labbs.com.br/mod/page/view.php?id=106). 

## Passo 1: Configurar aplicação no Brave

1. Acesse o [Brave](https://brave.dev.intranet.bb.com.br/) com suas credenciais (Chave F/C e senha SISBB).
2. No menu à esquerda da tela inicial, clique em **Selecione um Projeto**.
3. Na tela de seleção, informe o repositório, a sigla e o projeto da aplicação a ser monitorada. Para o nosso exemplo, utilizaremos uma aplicação no repositório **Fontes** para a siga **t99**.
> :information_source: **Observação** 
> 
> Caso sua sigla não possua repositório de monitoração, consulte o roteiro [Stack monitoracao](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/MysteryMachine/stack-monitoracao.md).
4. Clique em **Confirmar** para visualizar as informações do projeto selecionado.
5. No menu lateral, clique em **Release** e selecione o ambiente **Desenvolvimento**.

Com a aplicação selecionada seguimos para a sua configuração e para que o Prometheus capture métricas da sua aplicação, você precisa ativar o **Service Monitor**.

6. Ainda na tela da aplicação selecionada, localize e habilite a configuração **Service Monitor**.
7. No campo **Monitor**, use o formato padrão **sigla-monitor**. Para o nosso exemplo, será **t99-monitor**.
8. Em **Sample Limit**, mantenha ou altere o valor. Por padrão, o campo é preenchido com **40000**.
9. Na opção **Label**, clique em **Adicionar**.
10. Informe para os campos:
- **Chave**: a palavra **sigla**.
- **Valor**: a sigla em si. 
11. Na opção **Endpoints**, clique em **Adicionar**.
12. Informe para os campos:
- **Port**: http.
- **Interval**: 10s.
- **Path**: /metrics.

13. No final da tela, clique no botão **Deploy** para salvar a alteração. 
Nesse momento, o **Brave** interagirá com seu projeto de release de desenvolvimento no Gitlab, exibindo a tela para realizar um commit.
14. Insira uma mensagem de commit.
15. Informe a nova branch.
16. Clique no botão **Confirmar**.

Assim, você terá uma configuração realizada com sucesso.

> :grey_exclamation: **Importante** 
> 
>A stack de monitoração é criada e configurada por sigla, então todos os microsserviços dessa sigla devem aparecer no mesmo Monitor. Para o nosso exemplo, utilizamos a stack de monitoração que já foi criada anteriormente para a sigla **t99**.
> Caso ainda não exista um Monitor para sua sigla, acesse o [Roteiro para criação da stack básica de monitoração](https://fontes.intranet.bb.com.br/sgs/publico/roteiros/-/blob/master/Roteiro%20Cria%C3%A7%C3%A3o%20Stack%20B%C3%A1sica.md) para entender como criar um.

## Passo 2: Realizar merge no Gitlab

1. Ainda no **Brave**, no lado direito da tela, em **Funcionalidades**, clique no atalho **Gitlab**.
2. Agora no **Gitlab/Fontes**, faça o merge da branch criada para a master.

> :bulb: **Dica** 
> 
> Caso precise de ajuda para fazer o merge, acesse a aula a [Fazendo o merge request no Fontes](https://onboardingarq3.labbs.com.br/mod/page/view.php?id=99).

## Passo 3: Acompanhar deploy
1. Retorne ao **Brave**.
2. No lado direito da tela, clique no atalho **ArgoCD** para acompanhar o deploy das alterações realizadas para a aplicação que será monitorada. O resultado será similar à imagem a seguir: 

![Imagem Deploy via ArgoCD. Descrição: imagem da interface do ArgoCD exibindo a aplicação a ser monitorada e suas configurações](monitoracao/referencias/imagens/deploy_ArgoCD.png)

Ao fim da execução das orientações desse roteiro, sua aplicação estará configurada para ser monitorada pelo Prometheus/Grafana.  

## Informação extra

No exemplo a seguir, podemos visualizar as métricas obtidas a partir das configurações expostas no painel do Prometheus/Grafana. Veja a contagem de requisições do endpoint `/health/live` no namespace `dev-data-cloud`:

![Imagem Dados Prometheus/Grafana. Descrição: imagem da interface do Prometheus exibindo as métricas de monitoração](monitoracao/referencias/imagens/metricas_Prometheus.png)


**Tags:** #monitoracao #Brave #aplicacao

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/monitoracao/Como_configurar_aplicacao_para_ser_monitorada.md&internalidade=monitoracao/Como_configurar_aplicacao_para_ser_monitorada)
