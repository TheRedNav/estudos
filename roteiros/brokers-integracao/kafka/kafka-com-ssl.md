> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/brokers-integracao/kafka/kafka-com-ssl.md&amp;action_name=brokers-integracao/kafka/kafka-com-ssl.md)

# Kafka com conexão SSL

### Sobre o certificado do iib-kafka [fonte](https://fontes.intranet.bb.com.br/iib/helm_charts/iib-kafka-documentacao/-/wikis/Documenta%C3%A7%C3%A3o-da-Oferta-para-Cria%C3%A7%C3%A3o-de-Kafka-Red-Hat#511-certificado)
O chart do iib-kafka já vem com geração de certificado autoassinado por default e prazo de validade definido para 5 anos. 

Esse certificado é utilizado para acessar o kafka via rota.
A renovação do certificado também ocorre de forma automática 60 dias antes do mesmo vencer conforme os parâmetros seguintes:
```yaml
certificadoCluster:
  validityDays: 1825
  renewalDays: 60
certificadoClient:
  validityDays: 1825
  renewalDays: 60

```
### Gerando o certificado para acesso via Rota com SSL (via kubectl)
Na criação do chart iib-kafka é gerado uma rota para publicação e consumo de informações nos tópicos com SSL.
Para usar essa funcionalidade é necessário extrair o certificado CER da secret broker-cluster-ca-cert e gerar um JKS conforme exemplo:

Você pode se utilizar deste pequeno script (e fazer suas próprias alterações)
```
# Este é um script simples para geração de truststore

echo "Selecione seu contexto kubernetes"

kubectx <meu-contexto-k8s> # exemplos k8sdesbb103/k8shmlbb103/k8sprdbb103

echo "Selecione o namespace do seu Kafka"

kubectl get secret broker-cluster-ca-cert --namespace=<namespace-kafka> --output=jsonpath='{.data.ca\.crt}' |base64 --decode >> ca.crt

keytool -import -trustcacerts -alias root -file ca.crt -keystore truststore.jks -storepass password -noprompt # Password é a senha padrão

echo "Qual é nome da namespace do serviço à ser utilizado?"

kubectl create secret generic kafkatruststore --from-file=./truststore.jks -n <namespace-servico>

```
Ele cria automaticamente a secret para que o seu serviço possa se conectar ao IIB-Kafka automaticamente.
É possível executar este comando separadamente substituindo so valores correspondentes à namespaceKafka e namespaceServico

E o comando de criação da secret pode ser realizado quantas vezes seja necessário, para todos os serviços que necessitam da truststore.


### Configuração da aplicação Quarkus para utilizar o Kafka via SSL.

Obtenha o endereço da rota do broker iib-kafka

#### O endereço da rota deverá ser consultado na console do openshift em "Networking / Routes / broker-kafka-ocroute-bootstrap"

Exemplo de como localizar a rota e o **certificado** via console do OpenShift

Rota:
![rota1.png](images/rota1.png)

```yaml
      environments:
        - name: kafka.bootstrap.servers
          value: "broker-kafka-ocroute-bootstrap.gpf-red-hat.apps.k8sdesbb111.nuvem.bb.com.br:9092"
        - name: kafka.security.protocol
          value: "SASL_PLAINTEXT"
        - name: kafka.sasl.mechanism
          value: "SCRAM-SHA-512"
        - name: kafka.ssl.protocol #Obrigatório no uso do SSL
          value: "TLSv1.2"
        - name: APP_KAFKA_USERNAME
          valueFrom:
            secretKeyRef:
              name: "kafka-user-password"
              key: "kafka-username"
        - name: APP_KAFKA_PASSWORD
          valueFrom:
            secretKeyRef:
              name: "kafka-user-password"
              key: "kafka-password"
        - name: kafka.sasl.jaas.config
          value: "org.apache.kafka.common.security.scram.ScramLoginModule required username=\"$(APP_KAFKA_USERNAME)\" password=\"$(APP_KAFKA_PASSWORD)\";"
        - name: kafka.group.id
          value: "demo"
        - name: mp.messaging.outgoing.cliente-out.topic
          value: "topico-exemplo-1"
        - name: mp.messaging.incoming.cliente-in.topic
          value: "topico-exemplo-1"
        - name: mp.messaging.incoming.cliente-in.topic.ssl.truststore.location
          value: "/etc/config-kafkatruststore/truststore.jks" # Valor declarado de acordo com o volume mount, baseado na secret criada no passo anterior.
        - name: mp.messaging.outgoing.cliente-out.topic.ssl.truststore.location
          value: "/etc/config-kafkatruststore/truststore.jks" # Valor declarado de acordo com o volume mount, baseado na secret criada no passo anterior.
        - name: mp.messaging.incoming.cliente-in.topic.ssl.truststore.password
          value: "password" # Valor declarado de acordo com a password criada na truststore gerada
        - name: mp.messaging.outgoing.cliente-out.topic.ssl.truststore.password
          value: "password" # Valor declarado de acordo com a password criada na truststore gerada
        - name: mp.messaging.outgoing.cliente-out.topic.ssl.truststore.password
          value: "password" # Valor declarado de acordo com a password criada na truststore gerada
#... demais informações do values ...
          volumeMounts: # Valor declarado para montagem do certificado truststore.jks
        - name: kafkatruststore
          mountPath: /etc/config-kafkatruststore
          readOnly: true
      volumes:
        - name: kafkatruststore
          secret:
            secretName: kafkatruststore

```


[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/brokers-integracao/kafka/kafka-com-ssl.md&internalidade=brokers-integracao/kafka/kafka-com-ssl)

