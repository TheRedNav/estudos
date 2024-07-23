> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/processamento-arquivos/arquivados/bb-sia.md&amp;action_name=processamento-arquivos/arquivados/bb-sia)

# BB SIA

Com o BB SIA conseguimos fazer integração via arquivos com os microsserviços rodando na cloud.


- [Sharepoint Integração via Arquivos](https://banco365.sharepoint.com.mcas.ms/sites/IntegraoviaArquivos39/SitePages/BB-SIA.aspx?ga=1)
- [Exemplo projeto Java Quarkus com integração com o BB-SIA](https://fontes.intranet.bb.com.br/dev/publico/exemplos/quarkus-bb-sia)
- [GMT - Catálogo de serviços](#gmt-catálogo-de-serviços)
- [Integração usando o servidor WebService do BB SIA](#integração-usando-o-servidor-webservice-do-bb-sia)
- [Integração usando o Bucket S3](#integração-usando-o-bucket-s3)
- [Pré-requisitos](#pré-requisitos)
- Exemplos de fluxos:
  - [Mainframe -> Cloud](#exemplo-de-fluxo-mainframe-cloud)
  - [Cloud -> Mainframe](#exemplo-de-fluxo-cloud-mainframe)
- [Servidor BB SIA](#servidor-bb-sia)
- [Equipe responsável pelo BB SIA](#equipe-responsável-pelo-bb-sia)
- [Caso de uso do RCP](#caso-de-uso-do-rcp)
- [Diagrama integração Draw.io](#diagrama-feito-no-drawio-sobre-o-bb-sia)


## GMT - Catálogo de Serviços

Com esta ferramenta é possível acessar o:
- Cadastramento de FTAs (fluxo de transmissão de arquivos)
- Cadastramento de usuários
- Acesso ao SIA-WEB
- Monitorador

### Desenvolvimento
https://gmtedi.desenv.bb.com.br/cadastro/

### Homologa
https://gmtedi.hm.bb.com.br/cadastro/

### Produção
https://gmtedi.bb.com.br/cadastro/

### Documentação do SIA WEB

Para acessar a documentação do SIA WEB, é preciso acessar a URL sem informar nada após o `com.br`, exemplo:
- https://gmtedi.desenv.bb.com.br

Logo abaixo do local onde informarmos o usuário/senha, existe um link `Documentação e Downloads`.

### **[ATENÇÃO]** - Cuidado com URL

Na versão atual do site do gmtedi (14/06/2023), quando acessamos o SIA WEB, o menu superior "desparece". Para fazer ele voltar, é preciso incluir manualmente um `/cadastro` na url, como:
- https://gmtedi.desenv.bb.com.br/cadastro/

Menu superior:

![Menu BB SIA](processamento-arquivos/referencias/imagens/menu-bb-sia.JPG)


## Integração usando o servidor WebService do BB SIA

![Diagrama BB SIA](processamento-arquivos/referencias/imagens/diagrama-bb-sia-web.JPG)

## Integração usando o Bucket S3

![Diagrama BB SIA](processamento-arquivos/referencias/imagens/diagrama-bb-sia-bucket-s3.JPG)

Sequência:
1. Mainframe envia arquivo para BB-SIA por meio de um FTA cadastrado
2. BB-SIA envia arquivo para Bucket S3
3. BB-SIA envia mensagem para fila MQ informando nome do Arquivo enviado ao Bucket S3
4. Microsserviço lê mensagem do MQ com o nome do arquivo (não dá commit no MQ por enquanto)
5. Microsserviço lê arquivo do Bucket S3 e dá commit no MQ

## Pré-requisitos
- Cadastrar um **FTA** (Fluxo de Transmissão de Arquivos) no BB SIA informando as características da transmissão (exemplo Mainframe para a cloud ou cloud para o Mainframe).
- Do ponto de vista do microsserviço, será preciso:
  - **Interagir com um servidor do BB SIA via HTTP para fazer download ou upload dos arquivos**.
  - Ou receber os arquivos vi Bucket S3
- Se o tamanho do arquivo for muito grande, é recomendado alocar alguma mídia externa ao microsserviço para armazenar o arquivo, como um **volume** no Kubernetes.

## Exemplo de fluxo Mainframe -> Cloud

### Transmissão do Mainframe para o BB SIA

No final da PROCEDURE incluímos um **step** como o abaixo para startar uma transmissão para o BB SIA por meio do programa **GMTPSEND**:

```
//BBSIA    EXEC PGM=GMTPSEND,                                       
//         PARM='01&HLQ..RCP.RCPFCASH.D&DTMOV..SS000101           ' 
//SYSIN    DD DUMMY    
```

### Recepção do BB SIA

O servidor do BB SIA recebe este arquivo e o mantém disponível por 6 dias (importante confirmar com equipe do BB SIA).

### Download do arquivo pelo Microsserviço via BB SIA WebService

É preciso definir alguma estratégia para "ativar" o microsserviço para que ele faça o download, algumas ideias:
- Criar uma schedule e fazer polling no servidor do BB SIA verificando se o arquivo já está disponível.
- Criar uma operação IIB para estimular o processo.

O microsserviço acessa via HTTP o servidor do BB SIA e faz download do arquivo, o salvando dentro de um volume do Kubernetes.

### Download do arquivo pelo Microsserviço via Bucket S3

O microsserviço permanece escutando mensagens de uma fila MQ. Quando chegar uma mensagem, ela terá o nome do arquivo enviado pelo BB-SIA. Bastará neste momento tratar este arquivo que estará no Bucket S3.

Importante dar COMMIT no MQ somente após confirmar a existência do arquivo no Bucket S3.

## Exemplo de fluxo Cloud -> Mainframe

### Upload de arquivo pelo Microsserviço

Microsserviço lê arquivo de volume e, via HTTP, faz upload do arquivo no servidor BB SIA.

### Recepção do BB SIA

O servidor do BB SIA recebe este arquivo e o mantém disponível por 6 dias (importante confirmar com equipe do BB SIA).

### Envio do arquivo para o mainframe

Uma procedure do Mainframe é startada automaticamente para receber o arquivo.

Outra opção é o arquivo ser simplesmente disponibilizado no mainframe e uma procedure cíclica agrupar os arquivos pendentes e os processar.

## Servidor BB SIA

O servidor BB SIA possui algumas APIs onde é possível fazer download ou upload dos arquivos.

Também é possível listar os arquivos existentes para o seu FTA e consultar os metadados dos arquivos, por exemplo: a data da última leitura feita no arquivo.

## Equipe responsável pelo BB SIA

[DITEC/GESIT/G4/E2 INTEGRACAO ARQUIVOS](https://humanograma.intranet.bb.com.br/uor/288349)

Segundo a explicação do [Sharepoint Integração via Arquivos](https://banco365.sharepoint.com.mcas.ms/sites/IntegraoviaArquivos39/SitePages/BB-SIA.aspx?ga=1) desta equipe, o **BB SIA**:
> é a solução de transferência de arquivos do Banco do Brasil, seja para transferência de arquivos internos (entre Mainframe, servidores, estações de trabalho, etc) ou externos (troca de arquivos com clientes, parceiros e sistemas externos).

> é uma camada de gerenciamento aplicada sobre todo File Transfer do BB, sendo capaz de fazer uso de qualquer ferramenta ou protocolo suportada pelo BB.

> está disponível como self-service, para qualquer cliente da DITEC que necessite de transmissão de arquivos.

> prevê o acompanhamento do ciclo de vida do arquivo, desde a sua geração até a entrega ao destinatário final. O acompanhamento é realizado através de um número de protocolo único. Cada ciclo de vida cadastrado no BB SIA é chamado de FTA - Fluxo de Transmissão de Arquivos.

> O manual do BB SIA, que descreve a solução e seus módulos, está disponível na internet para qualquer cliente, em: https://gmtedi.bb.com.br


## Caso de uso do RCP

Os colegas do sistema **RCP** desenvolveram um microsserviço usando Spring Batch que faz esta integração com o BB SIA. Atualmente eles estão usando, inclusive, o GED para manter um backup dos arquivos.

O colega [Vinícius Rodrigues Tonha](https://humanograma.intranet.bb.com.br/F9540649) conhece este processo.

## Diagrama integração Draw.io

[Diagrama feito no Draw.io sobre o BB SIA](processamento-arquivos/referencias/imagens/Integracao%20de%20arquivos%20entre%20mainframe%20e%20a%20nuvem.drawio)
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/processamento-arquivos/bb-sia.md&internalidade=processamento-arquivos/bb-sia)

