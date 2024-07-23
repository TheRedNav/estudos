> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

Para consumir uma api disponibilizada pela Arq3 é preciso configurar o CORS na api para permitir o acesso.
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/plataforma/integracao_quarkus_arq3.md&amp;action_name=plataforma/integracao_quarkus_arq3)

Aqui demonstraremos como fazer essa configuração nas aplicações criadas utilizando o Quarkus.

Para fazer esta configuração do CORS, vamos testar as duas possibilidades abaixo:

**1) Configurar uma propriedade dentro da aplicação - application.properties**

https://quarkus.io/guides/http-reference , onde tem o item "5. CORS filter" que explica como configurar o CORS no Quarkus.

Original do site do Quarkus:

```properties
quarkus.http.cors=true
quarkus.http.cors.origins=http://foo.com,http://www.bar.io
quarkus.http.cors.methods=GET,PUT,POST
quarkus.http.cors.headers=X-Custom
quarkus.http.cors.exposed-headers=Content-Disposition
quarkus.http.cors.access-control-max-age=24H
```

Como iremos tentar (copiando de outro projeto):

```properties
quarkus.http.cors=true
#quarkus.http.cors.origins=*
#quarkus.http.cors.methods=GET, PUT, POST, DELETE, PATCH, OPTIONS
#quarkus.http.cors.headers=X-Custom
quarkus.http.cors.exposed-headers=Content-Disposition
quarkus.http.cors.access-control-max-age=24H
```


**2) Colocando a configuração direto no ingress:**

```yaml
      nginx.ingress.kubernetes.io/enable-cors: "true"
      nginx.ingress.kubernetes.io/cors-allow-origin: "*"
      nginx.ingress.kubernetes.io/cors-allow-methods: "PUT, GET, POST, OPTIONS"
      nginx.ingress.kubernetes.io/cors-allow-headers: "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,ticket-cnl"
```

Abaixo o ingress completo

```yaml
  ingress:
    enable: true
    hostname: autorregulacao-api.sca.desenv.bb.com.br
    servicePort: 80
    annotations:
      kubernetes.io/ingress.class: nginx
      nginx.ingress.kubernetes.io/proxy-connect-timeout: '30'
      nginx.ingress.kubernetes.io/proxy-read-timeout: '1800'
      nginx.ingress.kubernetes.io/proxy-send-timeout: '1800'
      nginx.ingress.kubernetes.io/enable-cors: "true"
      nginx.ingress.kubernetes.io/cors-allow-origin: "*"
      nginx.ingress.kubernetes.io/cors-allow-methods: "PUT, GET, POST, OPTIONS"
      nginx.ingress.kubernetes.io/cors-allow-headers: "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Authorization,ticket-cnl"
    paths:
      - path: /op4805619v1
        backend:
          serviceName: sca-autorregulacao-api
          servicePort: 80
      - path: /
        backend:
          serviceName: sca-autorregulacao-api
          servicePort: 80
    tls:
      - secretName: sca-desenv-tls
        hosts:
          - autorregulacao-api.sca.desenv.bb.com.br
```


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/plataforma/integracao_quarkus_arq3.md&internalidade=plataforma/integracao_quarkus_arq3)
