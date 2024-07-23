> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

## Início

Antes de configurar o Jaeger, certifique-se que possui, no pom.xml, a extensão:

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-smallrye-opentracing</artifactId>
</dependency>
```

## Configuração
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/tracing/tracing.md&amp;action_name=tracing/tracing)


A configuração de qual servidor jaeger que voce deseja utilizar deve ser feita por meio das variaveis de ambiente. 

* No caso do desenvolvimento local essa configuração é realizada no arquivo docker-compose.yaml. 
* Para os ambientes de deploy ela é realizada no arquivo values.yaml. 

A variavel de ambiente responsavel por essa configuração é a `JAEGER_AGENT_HOST_PORT`, que deve conter o endereco e porta do servidor, outras variavies podem ser utilizadas conforme a necessidade, para mais informações acesse a documentação do [jaeger][jaeger_doc] e as particularidades do client utilizado.

[Documentação Jaeger](https://www.jaegertracing.io/docs/deployment)

Segue exemplo de configuração do values.yaml para projetos utilizando Quarkus: 

```yaml

  deployment:
    ... 
    containers:
      ... 
      environments: 
        ... 
        - name: QUARKUS_JAEGER_AGENT_HOST_PORT
          valueFrom:
            fieldRef:
              fieldPath: status.hostIP
        - name: QUARKUS_JAEGER_SERVICE_NAME
          value: "nome-da-minha-aplicacao" 
        - name: QUARKUS_JAEGER_SAMPLER_TYPE     #Utilizar o value:"const"
          value: "const"
        - name: QUARKUS_JAEGER_SAMPLER_PARAM
          value: "1"
```


Existe um Jaeger funcionando como serviço em cada ambiente. É acessando algum deles que você irá poder consultar seus traces:

  -  Desenvolvimento: https://tracing.nuvem.desenv.bb.com.br
  -  Homologação: https://tracing.nuvem.hm.bb.com.br
  -  Produção: http://tracing.svc.nuvem.bb.com.br

...

## Como descobrir a url do Jaeger de um cluster específico no k8s:

 as principais são estas:

| cluster | url Jaeger|
| ------ | ------ |
| k8s-desenv | tracing.nuvem.desenv.bb.com.br |
| k8s-hm | tracing.nuvem.hm.bb.com.br  | 
| k8s-servicos |  tracing.svc.nuvem.bb.com.br | 

* Dica: Mas caso utilize outro cluster, é possível descobrir qual a url do jaeger de um cluster diferente, com o comando abaixo:

```
kubectl get ingress -n psc-jaeger-operator

NAME                               CLASS    HOSTS                                                   ADDRESS                                        PORTS     AGE
kdd-jaeger-operator-jaeger-query   <none>   tracing.data.nuvem.desenv.bb.com.br                     172.24.193.149,172.24.193.150,172.24.193.153   80, 443   57d

```

## Troubleshooting

**1. Como testar se o seu POD tem conexão com o AGENT do jaeger?**
Verificar o valor da variavel de ambiente JAEGER_AGENT_HOST_PORT dentro do pod, com esse endereço tente fazer uma conexão de dentro do seu pod para esse endereço para verificar se existe comunicação entre eles.

**2. A variável QUARKUS_JAEGER_SAMPLER_TYPE**
Atualmente deve sempre conter o value: "const"(igual demonstrado no values de exemplo deste roteiro), default na configuração do BB por enquanto. Se utilizar "remote", 
a aplicação espera que o servidor decida quantos  SPAMS e em que intervalo deve coletar, ainda não configurado no BB.
fonte_issue: https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues/4008


          



---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/tracing/tracing.md&internalidade=tracing/tracing)
