# BB SIA Rest – Webservices - APIs

Webservices disponibilizados pelo Banco do Brasil para viabilizar a troca de arquivos e informações com seus clientes através do protocolo HTTP/S, dentro dos princípios REST, com formato de dados JSON.
> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Plugin DBIQ para Maven 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/template_api.md&amp;action_name=Diataxis/template_api)
## Autenticação e credenciais

Infos 

## Endpoints
<details>
  <summary>GET Consulta uploads possíveis</summary>

Para que um cliente possa enviar (upload) um arquivo para o Banco do Brasil, este upload deve estar previamente autorizado
e identificado. Este serviço irá relacionar todos os uploads autorizados para o cliente e suas respectivas identificações – este código identificador deverá ser utilizado no momento do upload.

<details>
  <summary>Exemplo de Requisição</summary>

<br> Em **{token}** deve ser informada a chave de acesso que identifica o cliente. 
  ```
  GET 
  URL: /gmt-catalogo-api/listaUploads/
  Authorization: Bearer {token}
  ```
</details>

<details>
<summary>Exemplo de Resposta</summary>

|Código| Descrição|Formato dos dados de saída|
|--|--|--|
|200| SUCESSO: Cliente identificado com sucesso, será retornada a lista de uploads possíveis.|application/json|

Os campos **fta** e **evento** devem ser utilizados no serviço de upload para identificar o arquivo sendo transmitido. O campo **nome** apenas informa ao cliente qual o arquivo que se trata.

```json
{
  "remessa": [
    {
      "fta": integer,
      "nome": "string",
      "evento": integer
    }
  ]
}
```
</details>
</details>

## Endpoints

### GET Consulta uploads possíveis

Para que um cliente possa enviar (upload) um arquivo para o Banco do Brasil, este upload deve estar previamente autorizado
e identificado. Este serviço irá relacionar todos os uploads autorizados para o cliente e suas respectivas identificações – este código identificador deverá ser utilizado no momento do upload.

<details>
  <summary>Exemplo de Requisição</summary>

<br> Em **{token}** deve ser informada a chave de acesso que identifica o cliente. 
  ```
  GET 
  URL: /gmt-catalogo-api/listaUploads/
  Authorization: Bearer {token}
  ```
</details>

<details>
<summary>Exemplo de Resposta</summary>

|Código| Descrição|Formato dos dados de saída|
|--|--|--|
|200| SUCESSO: Cliente identificado com sucesso, será retornada a lista de uploads possíveis.|application/json|

Os campos **fta** e **evento** devem ser utilizados no serviço de upload para identificar o arquivo sendo transmitido. O campo **nome** apenas informa ao cliente qual o arquivo que se trata.

```json
{
  "remessa": [
    {
      "fta": integer,
      "nome": "string",
      "evento": integer
    }
  ]
}
```
</details>

## Endpoints
<details>
  <summary>GET Consulta uploads possíveis</summary>

<br> Para que um cliente possa enviar (upload) um arquivo para o Banco do Brasil, este upload deve estar previamente autorizado
e identificado. Este serviço irá relacionar todos os uploads autorizados para o cliente e suas respectivas identificações – este código identificador deverá ser utilizado no momento do upload.

#### Exemplo de Requisição

<br> Em **{token}** deve ser informada a chave de acesso que identifica o cliente. 
  ```
  GET 
  URL: /gmt-catalogo-api/listaUploads/
  Authorization: Bearer {token}
  ```

#### Exemplo de Resposta

|Código| Descrição|Formato dos dados de saída|
|--|--|--|
|200| SUCESSO: Cliente identificado com sucesso, será retornada a lista de uploads possíveis.|application/json|

Os campos **fta** e **evento** devem ser utilizados no serviço de upload para identificar o arquivo sendo transmitido. O campo **nome** apenas informa ao cliente qual o arquivo que se trata.

```json
{
  "remessa": [
    {
      "fta": integer,
      "nome": "string",
      "evento": integer
    }
  ]
}
```
</details>

teste.
