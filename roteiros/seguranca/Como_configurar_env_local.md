> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Como_configurar_env_local.md&amp;action_name=seguranca/Como_configurar_env_local)


# Como configurar senhas em environment local
Este roteiro ensina a configurar senhas e tokens no ambiente de desenvolvimento local por meio de variáveis.

> :information_source: **Observação** 
> 
> Uma variável de ambiente (environment) é um valor nomeado dinamicamente que pode afetar o comportamento de processos em execução em um computador, ela faz parte do ambiente no qual um processo opera, por exemplo, um processo pode consultar o valor da variável de ambiente temporário para determinar um local adequado para armazenar arquivos temporários. Por questão de segurança, tokens e senhas não podem ficar disponíveis/visíveis na aplicação, para isso temos as variáveis de ambiente como solução.   

Vale lembrar que variáveis de ambiente também são usadas para pegar valores de secrets nos ambientes de Desenvolvimento, Homologação e Produção.

## Configurar variáveis de ambiente localmente

1. Na raiz do seu projeto, crie um arquivo chamado **.env** .
2. Defina um nome para a variável de ambiente e um valor, separados por **=**, como a seguir:

```shell
URL_USERS_API=valor_da_env
```
3. Na seção **environment**, inclua a variável de ambiente no docker-compose, como a seguir:

~~~yaml
environment:
    - URL_API=${URL_USERS_API}
~~~

4. No arquivo **.gitignore** do seu projeto, inclua o arquivo **.env** . Assim, esse arquivo não será commitado no seu repositório.

> :warning: **Atenção**
>
> Antes de realizar o commit, certifique-se de que não está enviando o arquivo **.env**.

5. Execute a aplicação usando `./run/run.sh -dc -f` para que as variáveis do arquivo **.env** sejam disponibilizadas na sua aplicação.

6. Documente as variáveis de ambiente no README.md do seu projeto para que os desenvolvedores do projeto possam ter suas próprias secrets.

  
> :bulb:**Dica**
>
> As variáveis de ambiente ficam acessíveis para a aplicação também através do **aplication.properties**. Nele, as variáveis devem ser chamadas usando o **$** e nome da variável entre chaves. Veja no exemplo a seguir uma variável de ambiente chamada `URL_USERS_API` sendo acessada via application.properties.
>```shell
> users-api/mp-rest/url=${URL_USERS_API}
>```


**Tags:** #enviroment #senha #token #variaveis

## Saiba mais
* Leia a documentação do [Docker](https://docs.docker.com/compose/environment-variables/variable-interpolation/) sobre definir, usar e gerenciar variáveis.
* Entenda sobre aplication.proprities com a aula [Criando variáveis de ambiente](https://onboardingarq3.labbs.com.br/mod/page/view.php?id=279).

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/seguranca/Como_configurar_env_local.md&internalidade=seguranca/configuracao_env_local)
