> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Ref_configuracao_CORS.md&amp;action_name=seguranca/Ref_configuracao_CORS)

Este roteiro detalha a configuração do CORS (Cross-Origin Resource Sharing). Esse mecanismo baseado em cabeçalhos HTTP pode ser utilizado em aplicações criadas com o Quarkus para permitir o consumo de APIs disponibilizadas pela Arq3. É essencial configurar o CORS para permitir que recursos de um domínio sejam acessados a partir de outro. 

## Configuração via application.properties

Esta abordagem possibilita definir diretamente na aplicação Quarkus como o CORS deve funcionar, proporcionando flexibilidade e controle sobre as políticas de CORS dentro da aplicação.

```properties
quarkus.http.cors=true
quarkus.http.cors.origins=*
quarkus.http.cors.methods=GET, PUT, POST, DELETE, PATCH, OPTIONS
quarkus.http.cors.headers=X-Custom
quarkus.http.cors.exposed-headers=Content-Disposition
quarkus.http.cors.access-control-max-age=24H
```
|Elemento|Descrição|
|---|---|
|**quarkus.http.cors**|Habilita ou desabilita o CORS.|
|**quarkus.http.cors.origins**|Especifica os domínios permitidos. Pode ser uma lista separada por vírgulas ou * para permitir todos os domínios.|
|**quarkus.http.cors.methods**|Métodos HTTP permitidos.|
|**quarkus.http.cors.headers**|Cabeçalhos permitidos no request.|
|**quarkus.http.cors.exposed-headers**|Cabeçalhos expostos ao cliente.|
|**quarkus.http.cors.access-control-max-age**|Tempo de cache das permissões CORS pelo navegador. Use o formato padrão `java.time.Duration`.|

> :grey_exclamation: **Importante** 
> 
> Para ambas as abordagens, evite a configuração que permite todos os domínios nos ambientes de produção. Prefira sempre especificar os domínios confiáveis para proteger sua aplicação ou cluster contra possíveis riscos de segurança.

## Configuração via anotações do Ingress NGINX

Esta abordagem possibilita gerenciar o CORS no nível do gateway de entrada (Ingress), aplicando as regras de CORS antes das solicitações atingirem sua aplicação. É útil para múltiplas aplicações e configurações centralizadas de CORS.

```yaml
nginx.ingress.kubernetes.io/enable-cors: "true"
nginx.ingress.kubernetes.io/cors-allow-origin: "*.bb.com.br"
nginx.ingress.kubernetes.io/cors-allow-methods: "PUT, GET, POST, DELETE, OPTIONS"
nginx.ingress.kubernetes.io/cors-allow-headers: "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,ticket-cnl"
```
|Elemento|Descrição|
|---|---|
|**nginx.ingress.kubernetes.io/enable-cors**|Habilita ou desabilita o CORS.|
|**nginx.ingress.kubernetes.io/cors-allow-origin**|Especifica os domínios permitidos. Pode ser uma lista separada por vírgulas ou * para permitir todos os domínios.|
|**nginx.ingress.kubernetes.io/cors-allow-methods**|Métodos HTTP permitidos.|
|**nginx.ingress.kubernetes.io/cors-allow-headers**|Cabeçalhos permitidos no request.|

> :warning: **Atenção** 
> 
> Se optar por utilizar as duas abordagens em paralelo, evite conflitos entre as duas configurações. Certifique-se de que as regras de CORS definidas no Ingress não contradizem aquelas definidas na aplicação.

**Tags:** #quarkus #api #cors

## A Seguir
Consulte o guia [HTTP Referece](https://pt.quarkus.io/guides/http-reference) para saber mais sobre as diferentes funcionalidades HTTP disponíveis para Quarkus.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Ref_configuracao_CORS.md&internalidade=seguranca/Ref_configuracao_CORS)
