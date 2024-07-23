> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/curio-local-docker.md&amp;action_name=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/curio-local-docker)

# Fazer o provimento via Curió em ambiente local usando Docker

Os passos abaixo se aplicam somente aos projetos gerados pelo [Brave](https://brave.dev.intranet.bb.com.br/novo-projeto).

Para subir um container de curió em modo ```host``` com as operações cadastradas na sua aplicação, execute:

```shell
docker run -p 8081:8081 --env-file "$PWD"/.env_curio --name CURIO --rm docker.binarios.intranet.bb.com.br/bb/iib/iib-curio:0.8.3
```

O arquivo .env_curio é gerado junto com seu template de projeto (para projetos gerados na versão 2+ do [archetype](https://fontes.intranet.bb.com.br/dev/dev-archetype-quarkus)). Caso os dados de provimento ou consumo mudem, esse arquivo deve ser atualizado.

Fazendo dessa forma, o endereço a ser configurado do curió na aplicação é ```localhost:8081```.

# Tags
#guia #etapa_do_devops
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/curio-local-docker.md&internalidade=desenvolvendo-solucao-para-nuvem/operacoes-catalogo-iib/curio-local-docker)
