> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/certificado/Como_solicitar_certificados_PKI_SSL_interno.md&amp;action_name=certificado/Como_solicitar_certificados_PKI_SSL_interno)


# Como solicitar certificados PKI - SSL interno

Este roteiro ensina como solicitar certificados PKI no Portal OAAS. Os certificados do tipo PKI utilizados em protocolos SLL reforçam a segurança da comunicação na web. As aplicações e microsserviços com esse tipo de certificado permitem a transmissão segura de dados entre clientes e servidores.

## Requisitos

* O papel **PKI00OPR** vinculado ao perfil do solicitante. Se você ainda não possui o papel, acesse o [Painel do Acesso Fácil](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html?cd_modo_uso=1&app=aceSegurancaAcessoPainel#/) e solicite-o.

## Solicitar um certificado PKI

1. Acesse o [Portal OAAS](https://portal.nuvem.bb.com.br/).

2. No menu lateral esquerdo, vá para **Catálogo de Serviços**, identificado pelo ícone da caixa aberta, e navegue até a categoria **Segurança**.
   > :bulb: **Dica**
   >
   > Caso não consiga visualizar a categoria **Segurança** na tela, é possível acessá-la via [Portal](https://portal.nuvem.bb.com.br/catalogo/Seguran%C3%A7a).
3. Clique em **PKI – Certificado SSL Interno**.  
4. No campo **Informe um nome para a sua instância**, escreva um nome. Recomendamos seguir o padrão ‘ambiente-sigla-aplicação’. Ex.: hm-dev-metricas-cloud.
5. No menu suspenso, selecione o grupo do seu time.
   > :bulb: **Dica**
   >
   > Cada grupo possui papéis LDAP específicos que definem o acesso às instâncias. Se o seu time ainda não tem grupo ou o grupo existente não atende às suas necessidades, você pode [criar um novo grupo](https://fontes.intranet.bb.com.br/sgh/publico/atendimento/-/wikis/Portal-OaaS/03-Inst%C3%A2ncias) e definir suas preferências.  
6. Preencha as seguintes informações do certificado:
    * **Ambiente:** identifique em qual ambiente o certificado será utilizado.
    * **Nome Comum – CN:** escreva a URL completa da aplicação para a qual o certificado será emitido.
    * **Nomes Alternativos:** se houver, insira o nome de domínio alternativo e clique em **Adicionar**. Repita a ação para cada nome a mais, um por vez.
    * **Validade:** selecione 1 ano.
    * **Senha:** deve possuir EXATAMENTE 8 caracteres, e ao menos 01 letra maiúscula, 01 letra minúscula e 01 número. 
    * **Sigla:** selecione a sigla da aplicação.  
</div>

7. Clique em **Criar**; a janela **Resumo do provisionamento** abrirá.

8. Após revisar as informações, clique em **Confirmar** para concluir a solicitação do certificado. Uma nova janela com a mensagem de ‘solicitação realizada com sucesso’ aparecerá na tela.

9. Clique em **Acessar solicitações pendentes**.

10. Na aba **Minhas solicitações**, localize o ID da solicitação de certificado. Note que todas as aprovações pendentes aparecem listadas primeiro, garanta que você anotou o ID correto.

11. Informe o ID ao seu gerente de equipe. O gerente deve possuir o papel **PKIA5APV**, que possibilita aprovar as solicitações para novos certificados.   

Agora, você deve aguardar a aprovação para continuar o processo. Para conferir o status do seu pedido, acesse a aba [Minhas solicitações](https://portal.nuvem.bb.com.br/requests/user).

> :information_source: **Observação**   
>   
> Ambientes de **Homologação** e **Desenvolvimento** podem demandar a instalação de certificados, caso o navegador não reconheça a autoridade certificadora. Em **Produção**, tal problema não é esperado, pois os certificados já estão instalados.  

Para o ambiente de Homologação, baixe e instale no seu navegador:  

* [Certificado Raiz AC Banco do Brasil v3](https://pki.hm.bb.com.br/ACRAIZC/cacerts/raiz_v3.der) 
* [Certificado Intermediário da AC SERVIDORES v1](https://pki.hm.bb.com.br/ACINTA5/cacerts/acsr_v1.der)  

Para o ambiente de Desenvolvimento, baixe e instale no seu navegador:  

* [Certificado Raiz AC Banco do Brasil v3](https://pki.desenv.bb.com.br/ACRAIZC/cacerts/raiz_v3.der) 
* [Certificado Intermediário da AC SERVIDORES v1](https://pki.desenv.bb.com.br/ACINTA5/cacerts/acsr_v1.der) 

**Tags:** #certificado #ssl #tls #aplicação #solicitar #interno #pki #oaas
   
## A seguir  

* Leia o roteiro [Como exportar o certificado para o **OpenShift**](https://fontes.intranet.bb.com.br/pki/publico/atendimento/-/wikis/Pki-OAAS/Envio-para-secret-openshift) para saber mais sobre as possíveis ações para o certificado gerado.
* Leia o roteiro [Como exportar o certificado para o **Rancher**](https://fontes.intranet.bb.com.br/pki/publico/atendimento/-/wikis/Pki-OAAS/Envio-para-secret-k8s#a%C3%A7%C3%A3o-de-exportar-certificado-para-a-secret-do-k8s-rancher) para saber mais sobre as possíveis ações para o certificado gerado.  
* Leia o roteiro [Como criar manualmente secrets no Openshift](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/criacao-alteracao-de-secrets-openshift-manual) para aprender a inserir um secret no Openshift. 
* Leia o roteiro [Como solicitar certificados internos](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/solicitacao-de-certificados-interno) para outras informações e processos relacionados aos certificados. 

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/certificado/Como_solicitar_certificados_PKI_SSL_interno.md&internalidade=certificado/Como_solicitar_certificados_PKI_SSL_interno)
