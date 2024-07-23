# Entradas e saídas - Microsserviços 

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/ofertas/referencias/tabela_input_output.md&amp;action_name=ofertas/referencias/tabela_input_output)

Esse roteiro apresenta valores de entrada e saída relacionados aos Microsserviços. As tabelas oferecem uma visão clara dos dados envolvidos, simplificando a compreensão do funcionamento da aplicação. 

## Entradas (Inputs)

Refere-se aos dados fornecidos à aplicação. 

|Parâmetro|Tipo|Opções|Regex|Notas|
|-----|-----|-----|----------|-----|
|**sigla**|string|-|&nbsp;(&nbsp;[A&nbsp;-&nbsp;Z&nbsp;0&nbsp;-&nbsp;9]&nbsp;)&nbsp;{3}&nbsp;$|-|
|**project_name**|string|-|^&nbsp;[a&nbsp;-&nbsp;z&nbsp;0&nbsp;-&nbsp;9]&nbsp;{1&nbsp;,&nbsp;30}&nbsp;$|-|
|**descricao_projeto**|string|-|-|-|
|**uor_responsavel**|integer|-|^&nbsp;[1&nbsp;-&nbsp;9]&nbsp;[0&nbsp;-&nbsp;9]&nbsp;*&nbsp;$|-|
|**arquiteturas_referencia**|list-object|arquitetura_referencia|N/A|https://arquitetura.intranet.bb.com.br/docs/category/arquiteturas-de-referencia/|
|**tipo_projeto**|string|<ul><li>DockerImages</li><li>Gradle</li><li>Java</li><li>Javascript</li><li>Python</li></ul>|N/A|-|
|**solution_service**|string|<ul><li>geral\|geral</li><li>produtos_terceiros\|api_mgt</li></ul>|N/A|-|
|**generate_project**|boolean|-|N/A|Somente se *tipo_projeto* Java ou Javascript.|
|**archetype_version_java**|string|<ul><li>17.276.7</li><li>11.276.4</li></ul>|N/A|Somente se *generate_project* e *tipo_projeto* Java.|
|**training_mode**|boolean|-|N/A|Somente se *generate_project* e *tipo_projeto* Java.|
|**database**|string|<ul><li>none</li><li>oracle</li><li>db2</li></ul>|N/A|Somente se *generate_project*.|
|**operations_provider**|list_object|<ul><li>operation_id: string</li><li>operation_version: string</li></ul>|^&nbsp;[1&nbsp;-&nbsp;9]&nbsp;[0&nbsp;-&nbsp;9]&nbsp;*&nbsp;$|Somente se *generate_project*.|
|**operations_consumer**|list_object|<ul><li>operation_id: string</li><li>operation_version: string</li></ul>|^&nbsp;[1&nbsp;-&nbsp;9]&nbsp;[0&nbsp;-&nbsp;9]&nbsp;*&nbsp;$|Somente se *generate_project*.|

## Saídas (Outputs)

Refere-se aos resultados produzidos pela aplicação com base nos dados de entrada fornecidos. 

|Parâmetro|Tipo|Descrição|
|-----|-----|-----|
|**git_codebase_url**|string|Repositório Git com o código da aplicação.|
|**git_des_release_url**|string|Repositório Git de releases do ambiente de desenvolvimento.|
|**git_hml_release_url**|string|Repositório Git de releases do ambiente de homologação.|
|**git_prd_release_url**|string|Repositório Git de releases do ambiente de produção.|
|**jenkins_job_url**|string|URL do job no Jenkins para o projeto codebase.|
|**jenkins_job_webhook_url**|string|URL do webhook do job no Jenkins para o projeto codebase.|
|**jenkins_job_webhook_token**|string|Token do webhook do job no Jenkins para o projeto codebase.|
|**argocd_des_url**|string|URL do projeto no Argocd do repositório de release do ambiente de desenvolvimento.|
|**argocd_hml_url**|string|URL do projeto no Argocd do repositório de release do ambiente de homologação.|
|**argocd_prd_url**|string|URL do projeto no Argocd do repositório de release do ambiente de produção.|
|**deprovision_msg**|string|Data quando a instância será removida automaticamente. Aplicável SOMENTE para projetos da sigla T99.|
|**id_ctl**|integer|ID da aplicação registrada no Catálogo de Aplicações CTL. Não será gerado quando a sigla for T99.|
|**versao_aplicacao**|string|Versão da aplicação registrada no Catálogo de Aplicações CTL. Não será gerado quando a sigla for T99.|
|**sincronizar_ctl**|boolean|Indicador para controle interno. Não será gerado quando a sigla for T99.|
