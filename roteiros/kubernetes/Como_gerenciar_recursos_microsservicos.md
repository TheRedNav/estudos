> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_gerenciar_recursos_microsservicos.md&amp;action_name=kubernetes/Como_gerenciar_recursos_microsservicos)

# Como gerenciar os recursos de microsserviços 
 
Este roteiro ensina como utilizar o Brave para consultar e alterar o consumo de recursos de um namespace no Kubernetes. Além disso, apresenta o **Spotfire**, uma ferramenta que indica a eficiência de uso de CPU e memória. Ambas as ferramentas são essenciais para alocar corretamente recursos para os microsserviços.

> :grey_exclamation: **Importante** 
> 
> No Banco, não há restrições para a quantidade de recursos que você pode solicitar nos requests. Configure os valores de request com base nas necessidades reais e nos dados de uso. Requests excessivos podem causar subutilização, enquanto requests insuficientes podem afetar o desempenho. Monitore o consumo real e ajuste conforme necessário.
 
## Requisitos
* Acesso de desenvolvedor à sigla do projeto

## Verificar o consumo de recursos no Brave

1. Acesse o [Brave](https://brave.dev.intranet.bb.com.br/) e faça as autenticações necessárias.
2. No menu lateral esquerdo, clique em **Selecione um Projeto** (segundo ícone do menu).
3. Selecione o **Repositório**, **Sigla** e **Projeto**.
4. Clique em **Confirmar** e aguarde o carregamento da página.

A seção **Uso de recursos** apresenta as seguintes informações:

* **Quantidade de CPU Solicitada (Request):** a quantidade mínima de CPU que foi solicitada para garantir o funcionamento do microsserviço.
* **Quantidade de CPU Permitida (Limit):** a quantidade máxima de CPU que o microsserviço pode utilizar.
* **Pico de Consumo Estimado:** a estimativa do consumo máximo de CPU durante o período indicado.

A seção **Selo Cloud** apresenta as seguintes informações:

* **Avaliação:** o resultado da avaliação do Selo. Possíveis resultados: ouro, prata, bronze e *no compliance*.
* **Versão:** da aplicação que foi avaliada pelo Selo.
* **Selo:** versão atual do Selo.
* **Data:** quando a análise foi feita.

> :information_source: **Observação** 
> 
> Critérios que incentivam o uso racional dos recursos de infraestrutura são direcionadores para o Selo Ouro.
> A partir do segundo semestre de 2024, o Selo aplicará essas regras relacionadas a CPU:
> * **Regra de Requisição de CPU:** a requisição configurada de CPU deve ser de no mínimo 50m e deve ser menor ou igual ao pico utilizado entre 45 e 15 dias atrás (charts bb-aplic e mosaik).
> * **Regra de Limite de CPU:** o limite configurado de CPU deve ser de no mínimo 3000m e deve ser maior ou igual ao pico utilizado entre 45 e 15 dias atrás (charts bb-aplic e mosaik).

## Alterar o uso de recursos no Brave

É possível ajustar o consumo de recursos de sua aplicação, se necessário.

1. No menu lateral esquerdo do Brave, confirme que você já tem um projeto selecionado. 
2. Clique em **Release** (ícone do foguete).
3. Selecione o ambiente.
3. Em **BBAplic**, acesse a aba **Aplicação**.
4. Localize a seção **Resources**.
5. Altere os valores conforme necessário.
6. Vá para o final da página e clique em **Deploy**.
7. Verifique as alterações realizadas.
8. No campo **Informe uma nova branch**, escreva um nome para criar uma branch.
9. Clique em **Confirmar**.
10. Aguarde a mensagem de sucesso.

> :warning: **Atenção** 
> 
> Siga as orientações do [Onboarding](https://onboardingarq3.labbs.com.br/mod/page/view.php?id=99) para fazer o deploy em desenvolvimento ou homologação e o roteiro [deploy em produção](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/deploy-producao/Como_implantar_aplicação_ambiente_prod.md) para enviar as alterações da branch para o ambiente de produção.

# Acompanhar o consumo de recursos consolidado no Spotfire
 
1. Acesse o [Painel](https://bam.bb.com.br/spotfire/wp/analysis?file=/[Pública]/VITEC/UOS/GESIT/E92/HIGH-END/V_KUBE_EFA_CLST) no SpotFire e faça a autenticação na página.

## Entendendo o painel

* O painel mostra a eficiência do uso de memória e CPU. Na parte inferior esquerda da tela, estão as seguintes abas:  **Uor de Negócio**, **Sigla** e **Cluster**. O nome de cada aba reflete o nível de agrupamento dos dados.
* Todas as abas apresentam os filtros **Mês**, **Sigla**, **Ambiente**, **Orquestrador**, **Site** e **Cluster**, os quais podem ser combinados e redefinidos.
* Ao selecionar/combinar filtros, nas três abas serão exibidos os dados de **Request (cores)**, **Consumo CPU**, **EFA CPU** (a razão entre o consumo de CPU e o request), **Requests (Gigabytes)** e **Consumo RAM**.

#### Observações sobre os painéis

* A aba **Uor de Negócio** exibe dois cards com os valores de eficiência para RAM e CPU para todo o ecossistema.
* A aba **Sigla**, além dos dados listados anteriormente, exibe os dados **EFA RAM**, **CPU Ociosa**, **RAM Ociosa** e as tabelas **Detalhamento a nível de Namespace** e **Recomendação a Nível de Container**.
* A aba **Cluster**, além dos dados listados anteriormente, exibe os dados **EFA RAM** e as tabelas **Detalhamento a nível de Namespace** e **Recomendação a Nível de Container**.

**Tags:** #kubernetes #recursos #microsservicos #brave #spotfire

## A Seguir
* Leia a documentação de referência [Alocação de recursos de CPU e Memória para os Microsserviços](https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/referencias/alocar_recursos_microsservicos.md) para aprender conceitos fundamentais sobre o assunto.

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_gerenciar_recursos_microsservicos.md&amp;action_name=kubernetes/Como_gerenciar_recursos_microsservicos)
