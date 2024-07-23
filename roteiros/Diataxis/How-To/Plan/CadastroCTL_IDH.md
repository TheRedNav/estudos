> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Plan/CadastroCTL_IDH.md&amp;action_name=Diataxis/How-To/Plan/CadastroCTL_IDH.md)

# Registro de namespace no Catálogo de Aplicações

## Objetivo

Este guia descreve como registrar um namespace/microsserviço no Catálogo de Aplicações. Após acionamento desta oferta, será possível gerar as chaves IDH via SISBB. Esta oferta visa simplificar o processo de cadastro de aplicações no CTL/IDH, e visa atender serviços legados da Cloud BB - Em breve os novos serão cadastrados automaticamente pela oferta principal de `Microsserviços (Arq 3.0)`.

:warning:  **Esta oferta não deve ser utilizada para projetos na sigla T99 e/ou outros projetos de treinamento! . Também está restrita à aplicações que estão atualmente na Cloud BB.**

## Procedimento

Siga os passos a seguir:

### Acessando portal ofertas para registrar microserviço no Catálogo

1. Acesse o [portal de ofertas da nuvem](https://portal.nuvem.bb.com.br/). Caso não consiga acessar a página por conta de certificado, instale os certificados do banco disponíveis em https://wiki.nuvem.bb.com.br/portal-oaas/instalando-certificado-bb.

2. O portal de ofertas da nuvem usa grupos para organizar os pedidos de provisionamento do seu time/equipe dentro do portal. Você precisará de um grupo para criar o seu microserviço. Através do menu `Instâncias` é possível listar os grupos aos quais você tem acesso. Verifique se o seu microserviço se encaixa em um dos grupos listados. Caso seja necessário criar um grupo, clique em "Criar Novo Grupo" e preencha os campos.

### Registrando  o microserviço no Catálogo

3. Para registrar o microserviço no CTL (Catálogo de Aplicações), no menu lateral esquerdo escolha a opção `Catálogo de Serviços` e a seguir a opção `Desenvolvimento`. Das opções disponibilizadas, escolha `Oferta Heimdall/CTL`, conforme abaixo:

  ![ ](./imagens/oferta_heimdall.png)<br>

4. Preencha o formulário conforme instruções e exemplo abaixo:

  * **Nome para a instância**: nome da instância no portal nuvem.
  * **Grupo**: escolha o grupo onde ficará esta instância no portal nuvem
  * **Sigla**: sigla do microserviço
  * **Nome do Microserviço**:  nome do microserviço, **sem sigla** :bangbang:.
  * **Descrição do Microserviço**: texto livre descrevendo o microserviço.
  * **Uor Equipe Responsável**: Informe uma UOR que será responsável pelo microsserviço. :warning: **Importante!** Esta informação poderá ser utilizada em processos da ditec(Exemplo: Equipe Responsável para resolução de RDI). Portanto, preencha-a com atenção.

  ![ ](./imagens/oferta_heimdall2.png)<br>

5. Confira os dados na tela seguinte e confime a solicitação.

  ![ ](./imagens/oferta_heimdall3.png)<br>

6. Aguarde o processamento (pode levar alguns minutos).

### Resultados Finais

Após concluído será apresentada a tela à seguir, confirmando que o namespace/microsserviço foi cadastrado no Catálogo de Aplicações, retornando o ID relacionado.

![ ](./imagens/oferta_heimdall4.png)<br>

Agora você pode consultar seu namespace/microsserviço no Catálogo de Aplicações em `Plataforma BB -> Tecnologia -> Construção -> Catálogo -> Aplicações` . Devido à automação, a aplicação já virá com status `Estado 500 - Disponível no Ambiente de Produção`, no entanto alguns campos poderão ser editados a qualquer momento - além do vínculo/desvinculo de operações.

## Próximos passos (manuais):
  - Vincular Provimento/Consumo de Operações no CTL. 
  - [Gerar chaves IDH nos respectivos ambientes.](https://fontes.intranet.bb.com.br/idh/publico/roteiros/-/wikis/Criando-uma-credencial-para-a-aplica%C3%A7%C3%A3o#criando-credencial-da-aplica%C3%A7%C3%A3o-no-idh)
  - [Vincular chave IDH na variável de ambiente do Curió.](https://fontes.intranet.bb.com.br/idh/publico/roteiros/-/wikis/Configura%C3%A7%C3%A3o-da-credencial-de-aplica%C3%A7%C3%A3o#aplica%C3%A7%C3%A3o-arq-30)


# Referências

- https://banco365.sharepoint.com.mcas.ms/sites/Heimdall/SitePages/Autentica%C3%A7%C3%A3o-e-Autoriza%C3%A7%C3%A3o-de-Aplica%C3%A7%C3%B5es.aspx
- https://ditec.intranet.bb.com.br/#/player-video/http:%2F%2Fvideostvditec.servicos.bb.com.br%2Frepositorio%2Finovacao%2Fcomunicacao%2FTVDITEC%2F2020%2FSET%2Fn_G25_-_Seguranca_Para_Provedores_E_Consumidores_De_Servicos-1.webm?cat=todos&page=21&title=%23G25%20ARQ%203.0


# Tags
#guia #plan #heimdall #idh
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/Plan/CadastroCTL_IDH.md&internalidade=Diataxis/How-To/Plan/CadastroCTL_IDH)
