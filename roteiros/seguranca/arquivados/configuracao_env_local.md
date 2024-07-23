> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Configuração de senhas em environment local
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/arquivados/configuracao_env_local.md&amp;action_name=seguranca/arquivados/configuracao_env_local)

Para configurar senhas e tokens no ambiente de desenvolvimento local sugerimos a seguinte alternativa:

- Criar um arquivo com o nome .env e colocar ele na raiz do seu projeto.

- Colocar a enviroment que voce deseja ocultar no padrao NOME_ENV=VALOR_ENV

- Documente quais environments precisam ser ocultas no README.md do seu projeto para que os devs do projeto possam ter suas proprias secrets.

- Incluir o .env no arquivo .gitignore do seu projeto, assim esse arquivo não será comitado no seu repositorio, **antes de realizar o commit certifique-se de que não esta enviando o arquivo .env**.

- As environments desse arquivo estaram disponiveis para sua aplicação quando executar o docker-compose pelo comando;

  ```shell
  ./run/run.sh
  ```

Mais informações na documentação do [docker](https://docs.docker.com/compose/env-file/)---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/configuracao_env_local.md&internalidade=seguranca/configuracao_env_local)
