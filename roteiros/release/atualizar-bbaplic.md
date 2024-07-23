> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/atualizar-bbaplic.md&amp;action_name=release/atualizar-bbaplic.md)

# Como atualizar a versão do BB Aplic

A seguir serão abordados os procedimentos necessários para a atualização da dependência do chart do BB Aplic no seu projeto.

## 1.Atualize a versão no arquivo requirements.yaml

No arquivo requirements.yaml do seu projeto, atualize a versão do BB Aplic. Se atente ao ambiente no qual você está fazendo a atualização, pois o repositório do chart muda. A nível de exemplo utilizaremos a versão 5.8.0:
```
## Aplicações em produção
dependencies:
  - name: bb-aplic
    version: ~5.8.0
    repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo

## Aplicações em homologação e desenvolvimento
dependencies:
  - name: bb-aplic
    version: ~5.8.0
    repository: https://charts-repo.nuvem.bb.com.br/pre-prd/bb/catalogo

```

## 2. Atualize o arquivo values.yaml

Para atualizar o arquivo values.yaml do seu projeto, recomendamos verificar no repositório do chart do BB Aplic [psc-chart-bb-aplic](https://fontes.intranet.bb.com.br/psc/publico/psc-chart-bb-aplic) as alterações de cada versão, acessando os arquivos CHANGELOG.md e values.yaml, conforme a seguir:

### 2.1 Acesse o [psc-chart-bb-aplic](https://fontes.intranet.bb.com.br/psc/publico/psc-chart-bb-aplic) e selecione a branch de acordo com a versão desejada:


![Selecionar branch](release/referencias/imagens/psc-chart-bbaplic-selecionar-branch.png "Selecionar branch")




![Selecionar branch versão bbAplic](release/referencias/imagens/psc-chart-bbaplic-selecionar-branch-release.png "Selecionar branch versão bbAplic")




### 2.2 Após selecionar a branch de acordo com a versão do BB Aplic desejada, verifique os arquivos CHANGELOG.md e values.yaml:


![Branch selecionada](release/referencias/imagens/psc-chart-bbaplic.png "Branch selecionada")



### 2.3 Após acessar o CHANGELOG.md, verifique todas as alterações da versão em questão:


![Acessar CHANGELOG.md](release/referencias/imagens/psc-chart-bbaplic-changelog.png "Acessar CHANGELOG.md")



### 2.4 Verifique também o arquivo values.yaml:


![Acessar values.yaml](release/referencias/imagens/psc-chart-bbaplic-values.png "Acessar values.yaml")



No exemplo em questão, conforme apontado no CHANGELOG.md, a variável acessoProxy foi removida e houve alteração no objeto do path do ingress:


![Acessar values.yaml](release/referencias/imagens/psc-chart-bbaplic-values-ingress.png "Acessar values.yaml")



> :warning: **Atenção:** Em seu projeto, é necessário que a estrutura do arquivo values.yaml esteja de acordo com a estrutura do arquivo values.yaml disponível no psc-chart-bb-aplic, conforme versão selecionada.


## 3. Realize as alterações na branch master e acompanhe o deploy
As alterações citadas nos tópicos 1 e 2 deste roteiro, devem ser feitas na branch master do seu projeto. Assim que concluídas, o deploy iniciará automaticamente. Recomendamos acompanhar o deploy através do Argo CD, conforme respectivo ambiente:

- [Argo CD de desenvolvimento para aplicações rodando no Rancher](https://des.deploy.nuvem.bb.com.br/applications)
- [Argo CD de homologação para aplicações rodando no Rancher](https://hml.deploy.nuvem.bb.com.br/applications)
- [Argo CD de produção para aplicações rodando no Rancher](https://prd.deploy.nuvem.bb.com.br/applications)
- [Argo CD de desenvolvimento para aplicações rodando no OpenShift](https://deploy-des.nuvem.bb.com.br/applications)
- [Argo CD de homologação para aplicações rodando no OpenShift](https://deploy-hml.nuvem.bb.com.br/applications)
- [Argo CD de produção para aplicações rodando no OpenShift](https://deploy-prd.nuvem.bb.com.br/applications)


 > :warning: **Atenção:** Apenas commits na branch master iniciam deploy automaticamente.

No exemplo abaixo, após alterar os arquivos requirements.yaml e values.yaml na branch master do projeto, seguindo os passos 1 e 2, foi verificado o status no Argo CD. Assim que o commit é realizado, os status são alterados para "Progressing". Quando concluído, os status devem ser Healthy, Synced e Sync Ok.

![Alteração requirements.yaml](release/referencias/imagens/alteracao-requirements.png "Alteração requirements.yaml")



![Alteração values.yaml](release/referencias/imagens/alteracao-values.png "Alteração values.yaml")



![Status Argo CD](release/referencias/imagens/status-argo.png "Status Argo CD.yaml")



> :warning: **Atenção:** Caso ocorra algum erro ou algum status fique em looping como progressing, sugerimos verificar os logs no próprio Argo CD e se as alterações em seu projeto foram feitas da maneira correta.


---

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/release/atualizar-bbaplic.md&internalidade=release/atualizar-bbaplic)
