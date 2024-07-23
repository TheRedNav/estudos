> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/certificados-java.md&amp;action_name=java/certificados-java.md)

## Objetivo

Este roteiro visa fornecer orientações para identificar e resolver problemas relacionados a certificados em aplicações **Java**, especificamente quando erros como `PKIX path building failed`  e `PKIX path validation failed` são encontrados.

## Sumário

[[_TOC_]]

## Certificados SSL de servidor

Os certificados SSL/TLS existem para garantir comunicação privada entre cliente e servidor remoto. Para que essa comunicação funcione corretamente, é necessário que o servidor configure corretamente o certificado que vai apresentar aos clientes quando da abertura de conexão, e que o cliente confie na autoridade que assinou aquele certificado que o servidor apresenta.

Tomando como exemplo o servidor `https://binarios.intranet.bb.com.br`, clicando no cadeado do navegador e depois em "Mais Informações", é possível ver a seguinte tela:

![Firefox Mais Informações](./images/firefoxVerCertificado.png)

Clicando em "Ver certificado", estão disponíveis as informações da cadeia:

![Cadeia de certificados](./images/cadeiaCertificados.png)

Isso significa que o servidor `binarios.intranet.bb.com.br` tem seu certificado específico assinado pelo certificado intermediário AC Banco do Brasil - SERVIDORES v1, que por sua vez é assinado pelo certificado raiz AC Banco do Brasil v3.

Para que uma máquina cliente aceite o certificado apresentado por `binarios.intranet.bb.com.br`, é preciso que o caminho, ou hierarquia(o `PKIX path` das mensagens de erro) seja montado desde o certificado apresentado pelo servidor até uma autoridade certificadora raiz cuja máquina cliente **já confia**.

Para esse caso, existem duas possibilidades:

1. O servidor apresenta apenas seu certificado de `binarios.intranet.bb.com.br`. Nesse caso a máquina cliente vai precisar ter o certificado intermediário AC Banco do Brasil - SERVIDORES v1 na sua trustore, além do certificado raiz AC Banco do Brasil v3.
2. O servidor apresenta a cadeia de certificados inteira. Nesse caso, a máquina cliente só precisa conhecer o certificado raiz.


## Adicionar certificados como confiáveis no Java

A forma padrão de confiar em certificados (normalmente os certificados raiz e intermediários) é usando o keytool para adicioná-los à trustStore, no arquivo cacerts. Os mais links abaixo mostram o passo-a-passo:

* [Tutorial sobre Adição de Certificados à trustStore](https://www.tutorialworks.com/java-trust-ssl/)
* [Tutorial do Red Hat sobre Adicionar um Certificado à trustStore usando o keytool](https://access.redhat.com/documentation/en-us/red_hat_jboss_data_virtualization/6.2/html/security_guide/add_a_certificate_to_a_truststore_using_keytool)

Quando isso é feito em máquinas locais, deve-se atentar que cada instalação de Java tem seu próprio cacerts, fazendo com que o processo precise ser repetido para as diferentes versões.


## Adicionar certificados como confiáveis nas imagens base Java

Imagens base Java oferecidas pelo time DEV possuem os certificados do Banco do Brasil instalados e configurados na JVM. Por padrão, a imagem base da Red Hat confia nos certificados da [Mozilla CA list](https://wiki.mozilla.org/CA/Included_Certificates). Caso precise adicionar um certificado novo, siga os passos do [roteiro](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/imagens-java.md#certificados).

## Troubleshooting:

### Causas comuns de erro na validação de certificados:

* **Autoridades Desconhecidas**: Caso o certificado do servidor seja assinado por uma autoridade desconhecida pelo sistema. Isso inclui certificados auto-assinados.
* **Cadeias de Certificados Incompletas**: Quando as cadeias de certificados não estão completamente definidas, resultando em lacunas na validação.
* **Certificados Revogados**: Se o certificado do servidor tiver sido revogado após a emissão, a validação falhará.
* **Alterações nos Certificados**: Modificações nos certificados do servidor podem impactar a confiança estabelecida anteriormente.
* **Interferência de Firewalls/Proxies**: Firewalls ou proxies podem interferir na troca de certificados, levando a erros de validação.
* **Desalinhamento do Relógio do Sistema**: Problemas de sincronização de relógio entre cliente e servidor podem resultar em erros de validação.


#### Como investigar:

Para analisar problemas de certificados dentro dos pods, é possível ativar o modo de debug de SSL. Isso pode ser feito adicionando o seguinte argumento ao Java:

```
-Djavax.net.debug=all
```

Como exemplo específico, na imagem base Java, pode-se utilizar, no Dockerfile:

```
ENV JAVA_TOOL_OPTIONS="-Djavax.net.debug=all ${JAVA_TOOL_OPTIONS}"
```

Ou no values.yaml:

```
JAVA_TOOL_OPTIONS="-Djavax.net.debug=all ${JAVA_TOOL_OPTIONS}"
```

Isso evita que as opções padrão sejam sobrescritas, e no caso do values, evita que seja necessário um novo build da aplicação.

Cmom o debug de rede ligado, é possível analisar a troca de mensagens entre cliente e servidor no momento de estabelecer a conexão SSL. Isso deve ser mantido ligado somente pelo tempo necessário para fazer testes rápidos, pois a produção de log é muito maior e vai afetar seriamente o desempenho do pod.

### Obtendo Certificados do Servidor com OpenSSL:

Para obter os certificados do servidor com OpenSSL a partir de um terminal, utilize o seguinte comando:

```
openssl s_client -showcerts -connect exemplo.com:443
```

Substitua "exemplo.com" pelo nome de domínio ou endereço do servidor que você deseja verificar. Esse método é uma alternativa ao uso do navegador.


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/certificados-java.md&internalidade=java/certificados-java)
