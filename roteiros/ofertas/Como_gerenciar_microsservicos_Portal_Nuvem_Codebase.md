> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/ofertas/Como_gerenciar_microsservicos_Portal_Nuvem_Codebase.md&amp;action_name=ofertas/Como_gerenciar_microsservicos_Portal_Nuvem_Codebase)

# Como provisionar o código base de microsserviços pelo Portal Nuvem

Este roteiro ensina como gerar o código base de microsserviços através do Portal Nuvem (Novo Ofertas), incluindo a criação e desprovisionamento. Microsserviços são componentes isolados que realizam tarefas específicas e comunicam-se com outros microsserviços via APIs. Em ambientes cloud, os microsserviços servem para que os grupos trabalhem de forma independente.

## Provisionar o projeto

O provisionamento ocorrerá apenas através de chamada por oferta-pai, via ansible, visto que esta oferta de codebase será sempre uma oferta-filha. Esta chamada pode ser realizada conforme os snippets de código abaixo. Durante o provisionamento, é feita a vinculação do namespace com a arquitetura de referência.

Template da chamada

```
- name: Microsserviços codebase
  bb_service_broker:
    service_id: oferta-microsservicos-codebase-v1
    plan_id: default
    operation: provision
    parameters:
      sigla: '' # type=sigla_almfd required=true
      project_name: '' # type=string required=true
      descricao_projeto: '' # type=string required=true
      uor_responsavel: '' # type=string required=true
      arquiteturas_referencia: '' # type=list_object required=true
      generate_project: false # type=boolean
      tipo_projeto: '' # type=string required=true
      archetype_version: '' # type=string required=true
      training_mode: false # type=boolean
      database: '' # type=select required=false
      operations_provider: '' # type=list_object required=false
      operations_consumer: '' # type=list_object required=false
      approved_by: '' # type=string required=false
      solution_service: '' # type=select required=true
      custom_gitlab_template_url: '' # type=hidden
      custom_generator_body: '' # type=hidden
  register: output_result
```

Exemplo de chamada

```
- name: Microsserviços codebase
  bb_service_broker:
    service_id: oferta-microsservicos-codebase-v1
    plan_id: default
    operation: provision
    parameters:
      sigla: 'dev'
      project_name: 'dev-exemplo-codebase-api'
      descricao_projeto: 'Este é um projeto de exemplo de chamada à oferta de codebase'
      uor_responsavel: '459606'
      arquiteturas_referencia: [
        {
          "arquitetura_referencia": "seguranca_de_aplicativos_bb-cloud:1.0"
        },
        {
          "arquitetura_referencia": "arquitetura_de_aplicativos_cloud_bb:1.0"
        }
      ]
      generate_project: true
      tipo_projeto: 'Java'
      archetype_version: '17|17.2138.6'
      training_mode: false
      database: 'oracle'
      operations_provider: [
        {
          "numero": "5207120",
          "versao": "1"
        },
        {
          "numero": "252416",
          "versao": "1"
        }
      ]
      operations_consumer: [
        {
          "numero": "5207120",
          "versao": "1"
        },
        {
          "numero": "252416",
          "versao": "1"
        }
      ]
      approved_by: 'F9999999'
      solution_service: 'geral|geral'
      custom_gitlab_template_url: 'https://fontes.intranet.bb.com.br/dev/templates/dev-template-generic.git'
      custom_generator_body: {
        "platform": "Java",
        "sigla": "t99",
        "nomeProjeto": "teste-custom-generator",
        "infoVersion": "17.2138.6",
        "tipoBancoDeDados": "oracle",
        "modoTreinamento": false,
        "operacoesProvimento": [
            {
                "numero": "5207120",
                "versao": "1"
            }
        ],
        "operacoesConsumo": [
            {
                "numero": "252416",
                "versao": "1"
            }
        ]
      }
  register: output_result
```

## Desprovisionar o projeto

O desprovisionamento ocorrerá apenas quando uma instância for desprovisionada em uma oferta-pai, visto que esta oferta de codebase será sempre uma oferta-filha.

## Chamada via API

* Leia o roteiro [Criação e desprovisionamento de microsserviços por API](/ofertas/arquivados/ofertas_CriandoMicroservico.md#cria%C3%A7%C3%A3o-e-desprovisionamento-de-microsservi%C3%A7os-por-api) para usar esta oferta via API.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/oferta-microsservico/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/ofertas/Como_gerenciar_microsservicos_Portal_Nuvem_Codebase.md&internalidade=ofertas/Como_gerenciar_microsservicos_Portal_Nuvem_Codebase)