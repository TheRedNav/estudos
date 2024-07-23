> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/helmv3.md&amp;action_name=release/helmv3.md)

# Helm v3

## Contexto

[Helm](https://helm.sh/) é uma ferramenta de infraestrutura utilizada nos nossos clusters Kubernetes (incluindo os OpenShift). Recentemente, o time de infraestrutura cloud atualizou sua versão, o que impacta na sintaxe dos arquivos descritores presentes nos repositórios de release.

## Mudanças

Antes, um repositório de release no padrão DevCloud tinha 3 arquivos:

```
Chart.yaml
requirements.yaml
values.yaml
```

Após a mudança, o arquivo `requirements.yaml` deixa de existir, passando a responsabilidade de declarar as dependências para o arquivo `Chart.yaml`.

## Como adaptar minha aplicação

1. Abra o arquivo `requirements.yaml`. Você vai encontrar algo parecido com isso:

```yaml
dependencies:
- alias: nimbus-bot
  name: bb-aplic
  repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
  version: ~5.8.0
- name: dnsingress
  version: 2.0.0
  repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
```

2. Copie todo o conteúdo do arquivo
3. Abra o arquivo `Chart.yaml`. Você vai encontrar algo parecido com isso:

```yaml
apiVersion: v1
appVersion: "0.0.1"
description: Chart do chart-name
name: chart-name
version: 0.0.1
```

4. Cole o conteúdo do arquivo `requirements.yaml` no final. O exemplo mostrado vai ficar assim:

```yaml
apiVersion: v1
appVersion: "0.0.1"
description: Chart do chart-name
name: chart-name
version: 0.0.1
dependencies:
  - alias: nimbus-bot
    name: bb-aplic
    repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
    version: ~5.8.0
  - name: dnsingress
    version: 2.0.0
    repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
```

5. Altere a `apiVersion` para `v2`. A versão final é esta:

```yaml
apiVersion: v2
appVersion: "0.0.1"
description: Chart do chart-name
name: chart-name
version: 0.0.1
dependencies:
  - alias: nimbus-bot
    name: bb-aplic
    repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
    version: ~5.8.0
  - name: dnsingress
    version: 2.0.0
    repository: https://charts-repo.nuvem.bb.com.br/prd/bb/catalogo
```

6. Apague o arquivo `requirements.yaml`.

7. Pronto. Este repositório está compatível com Helm v3. Não esqueça de fazer o mesmo para todos os repositórios de release (des, hml e prd).

*Obs: 
Se no seu ingress possuir o pathType com o valor ImplementationSpecific, mudar para Prefix.


[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/release/helmv3.md&internalidade=release/helmv3)
