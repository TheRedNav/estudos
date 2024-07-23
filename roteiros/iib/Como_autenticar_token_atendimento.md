> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_autenticar_token_atendimento.md&amp;action_name=iib/Como_autenticar_token_atendimento)

# Como autenticar tokens de atendimento

Este roteiro ensina a validar a autenticidade do token de atendimento. Para evitar falsificações de token durante as chamadas de operação, é crucial verificar sua assinatura. O GCS (Gerenciador Componentes de Segurança Multicanal) fornece um emissor e uma chave pública para cada ambiente, os quais devem ser utilizados nos parâmetros de configuração da extensão Microprofile.

## Requisitos
* [Configurações e dependências necessárias](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/roteiroTokenAtendimento.md#pr%C3%A9-requisitos-na-aplica%C3%A7%C3%A3o-arq3) para utilizar um token de atendimento.

## Autenticar um token de atendimento

### Para testes locais usando o perfil dev
1. Abra o arquivo **application.properties**.
2. Adicione as propriedades abaixo: 

```
mp.jwt.verify.publickey.location=https://token.gcs.desenv.bb.com.br/api/v1/jwks.json
mp.jwt.verify.issuer=https://token.gcs.desenv.bb.com.br
```

> :grey_exclamation: **Importante** 
> 
> O Rest Assured (uma biblioteca para testar endpoints REST no Quarkus) também usará essa configuração para verificar o JWT em solicitações durante os testes unitários. No entanto, isso não é ideal e não funcionará no pipeline do Jenkins. Portanto, recomendamos configurar especificamente para os testes. Consulte a [seção de testes da documentação](https://quarkus.io/guides/security-jwt#integration-testing) do Quarkus e escolha uma abordagem adequada. 

### Para testes no pod (docker-compose ou k8s)
1. Abra o arquivo **values.yaml**. 
2. Adicione a variável de ambiente **MP_JWT_VERIFY_PUBLICKEY_LOCATION** e preencha o **value** conforme o [ambiente desejado](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_autenticar_token_atendimento.md#informa%C3%A7%C3%B5es-de-parametriza%C3%A7%C3%A3o-dos-ambientes). 
3. Adicione a variável de ambiente **MP_JWT_VERIFY_ISSUER** e preencha o **value** conforme o [ambiente desejado](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_autenticar_token_atendimento.md#informa%C3%A7%C3%B5es-de-parametriza%C3%A7%C3%A3o-dos-ambientes).

```yml
gcs-exemplo-jwt:
  # ...
  deployment:
    # ... 
    containers:
      # ...
      environments:
        # ...
        # Insira nesta area as variaveis de ambiente do JWT
        - name: "MP_JWT_VERIFY_PUBLICKEY_LOCATION"
          value: "https://token.gcs.desenv.bb.com.br/api/v1/jwks.json"
        - name: "MP_JWT_VERIFY_ISSUER"
          value: "https://token.gcs.desenv.bb.com.br"
    # ...
```

## Informações de parametrização dos ambientes

### Desenvolvimento 

**Issuer:** https://token.gcs.desenv.bb.com.br <br>
**Url (JWK Url):** https://token.gcs.desenv.bb.com.br/api/v1/jwks.json

### Homologação

**Issuer:** https://token.gcs.hm.bb.com.br <br>
**Url (JWK Url):** https://token.gcs.hm.bb.com.br/api/v1/jwks.json

### Produção

**Issuer:** https://token.gcs.intranet.bb.com.br <br>
**Url (JWK Url):** https://token.gcs.intranet.bb.com.br/api/v1/jwks.json

## A Seguir
* Depois de verificar se o token é válido, seguindo as validações do JWT conforme a RFC, é crucial também confirmar se o escopo do token está alinhado com os requisitos do seu negócio. Leia o roteiro [Relacionamentos do Token de Atendimento](https://fontes.intranet.bb.com.br/gcs/publico/docs/autenticacao/-/blob/master/tokenAtendimento/RelacionamentosTokenAtendimento.md) para explorar os diferentes escopos e os critérios específicos de validação associados a cada um deles.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/iib/Como_autenticar_token_atendimento_curio.md&internalidade=iib/Como_autenticar_token_atendimento_curio)
