> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/testes/Como_solicitar_acessos_BB.md&amp;action_name=testes/Como_solicitar_acessos_BB)


# Como solicitar acessos BB
Este roteiro orienta novos contratados sobre as solicitações necessárias para o acesso à VPN do banco, emissão de certificado e instalação de ferramentas.

> :information_source: **Observação**
>
> Antes de iniciar, entre em contato com o ponto focal da empresa responsável pelas interações com o BB e se certifique de que as devidas liberações, como a criação do seu usuário (chave C) e uso da VDI, tenham sido realizadas.

## Requisitos
* Chave C.
* Acesso a [Intranet BB](https://intra.bb.com.br/login).

## Passo 1: Solicitar acessos
1. Na parte superior da página da intranet BB, clique em **Plataforma BB** (ícone da letra P).
2. Na barra superior da plataforma, em **Selecione a área de trabalho**, clique em **Negócios**.
2. No menu à esquerda da plataforma, selecione a opção **Segurança** (ícone do cadeado).
3. Vá para **Acesso fácil** e clique em **Painel**.
4. No campo de pesquisa, digite o código **VPN25** e clique em **Buscar**. Esse papel te permitirá acessar a VPN.
5. No quadro **Papéis Encontrados**, marque a checkbox à esquerda do código para selecionar o papel em questão.
6. Clique em **Solicitar Acesso**.
7. Realize as instruções de 4 a 6 para o código **PKI99**. Esse papel te permitirá acessar a VPN e gerar o certificado.      
8. Realize as instruções de 4 a 6 para o código **MWGAAA4**. Esse papel te permitirá acessar a internet.    
9. Informe o seu gerente BB para que ele realize a aprovação.   
Com as solicitações aprovadas, você poderá solicitar seu certificado de acesso à VPN.

## Passo 2: Solicitar certificado
1. Ainda na área de trabalho **Negócios** da Plataforma BB, no menu à esquerda, selecione a opção **Segurança** (ícone do cadeado).
2. Vá para **Acesso fácil** e clique em **Solicitação de Certificado VPN**. 
3. Crie uma senha, **diferente** da senha da Rede MAN, com 8 dígitos, contendo uma letra maiúscula, uma letra minúscula e um número.

> :warning: **Atenção**
>
>Guarde esta senha, porque ela será utilizada para conectar a VPN e não há possibilidade de recuperação posterior.

4. Após informar os campos **Senha** e **Confirmação da Senha**, clique em **Confirmar**. Será exibido um pop-up com as informações do arquivo para download. 
5. Baixe o certificado **vpn_cert.p12**. Ele será utilizado para acessar a VPN do BB juntamente com a senha que criou anteriormente.

## Passo 3: Solicitar acesso para usuário administrador temporário
Para instalar ferramentas é preciso ser administrador temporário da máquina.
1. Na barra superior da [Plataforma BB](https://plataforma.atendimento.bb.com.br:49286/estatico/gaw/app/spas/index/index.app.html#/), em **Selecione a área de trabalho**, clique em **Administrativo**.
2. No menu à esquerda, selecione a opção **Estação de Trabalho** (ícone do notebook).
3. Vá para **Administrador Temporário** e clique em **Solicitação**.
4. Clique em **Nova Solicitação**.
5. Informe os campos:
- **Hostname da estação**, adicione o nome do computador.
> :bulb: **Dica**
>
>Para descobrir o nome do seu computador, na barra de pesquisa do seu computador, digite **Nome do computador** e clique no resultado **Exibir o nome do computador**. Na tela de configurações, o nome do computador estará no campo **Nome do dispositivo**.

- **Domínio do Usuário**, informe o seu domínio **Intrabb**, **Mesop** ou **Worldnet**.
- **Justificativa**, selecione uma justificativa entre **Instalação de Software**, **Altera Configuração do SO** ou **Outro motivo**.
- **Qual(is) software(s) será(ão) instalado(s)**, descreva o que será instalado ou configurado.

6. Clique em **Incluir Administrador**.
7. Solicite ao seu gerente a concessão e aprovação do acesso como administrador temporário. Ele precisa ter o papel **WIN0ADM0**. 

Por ser temporário, esse acesso tem a duração de 4 horas a partir da aprovação.

**Tags:** #VPN #acesso #certificado

## A Seguir
* Leia o roteiro [Como configurar a VPN Check Point](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_configurar_VPN.md) para instalar e configurar a VPN pelo Check Point com o seu certificado e senha.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/enxovalBB/Como_solicitar_acessos_BB.md&internalidade=enxovalBB/Como_solicitar_acessos_BB)
