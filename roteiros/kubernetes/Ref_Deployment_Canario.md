> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1] 
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_utilizar_deployment_Canario.md&amp;action_name=kubernetes/Como_utilizar_deployment_Canario)

# Deployment Canário
Este roteiro detalha o uso do **deploymentCanario**, função disponível no chart [BB Aplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic).   
Essa função reduz o risco de indisponibilidade ao lançar uma nova versão. Com a estratégia Canário, é possível testar a nova versão da aplicação em alguns *pods* paralelamente aos *pods* da versão estável, redirecionando uma pequena parte das requisições para a nova versão. Após a validação do funcionamento da nova versão, ela pode ser promovida para o **deployment** regular com segurança.

## Ativar o Canário para a aplicação
No **values.yaml** padrão do chart **bb-aplic** há uma seção para ativar o **deployment** Canário, inicialmente desligada:
1. Encontre o **deploymentCanario**.
1. Altere o valor da tag **enable** para **true**.
1. Informe um número de réplicas em **replicaCount**.
Segue exemplo da configuração:

```yaml
deploymentCanario:
  enable: true
  deploymentControle: false   # Criação do deployment de controle
  imagePullSecrets: atfregistry
  replicaCount: 2

  ...

```
O **replicaCount** define a quantidade de *pods* que serão criados para o Canário e para o controle. Por exemplo, se você tiver 10 *pods* da aplicação rodando e definir 2 no **deploymentCanario**, serão criados 2 *pods* Canário e 2 *pods* controle, totalizando 14 *pods*.   

O **deploymentControle** possibilita a comparação entre a configuração do *deploy* regular e o Canário.
Ao ativá-lo com valor **true**, o controle subirá a mesma quantidade de *pods* do Canário utilizando as configurações do *deploy* regular. Por exemplo, se você definir  duas réplicas para o Canário, também serão criadas mais duas réplicas iguais ao *deploy* regular. Assim, é possível avaliar as métricas de 2 *pods* Canário em comparação com as métricas de 2 *pods* controle, independentemente da quantidade de *pods* do *deploy* regular.   

Anteriormente, o controle era ativado sempre junto com o Canário, mas nas versões mais recentes do chart do bb-aplic pode ser ligado/desligado pela propriedade **deploymentControle**.   

As demais configurações são similares às configurações do seu *deployment* regular. É possível definir a versão do contêiner que será executado, suas variáveis de ambiente, recursos, volumes e todas as outras configurações de acordo com o necessário para que a nova versão possa ser executada.

### Balanceamento de carga das requisições entre o deploy regular e o Canário
Não é possível definir a quantidade de requisições que vão para o *deploy* regular ou para o Canário via configuração.
As requisições para a sua aplicação no Kubernetes são roteadas pelo service, que faz o balanceamento de carga. São considerados todos os *pods* que estejam rodando e saudáveis para o balanceamento.   
Então, utilizando o exemplo anterior, com 10 *pods* no *deployment* regular, 2 *pods* Canário e 2 *pods* controle, as requisições serão balanceadas entre os 14 *pods*. Dessa forma cerca de 14% das requisições serão atendidas pelos 2 *pods* Canário.

## Utilizando Canário em aplicações com Curió
Ao utilizar o Curió com o Canário, cada pod do Canário subirá com um contêiner do Curió, assim como os demais *pods*. Porém, as configurações do Curió serão as mesmas entre o Canário e o regular.   
Não é possível definir configurações específicas para o Canário, como novas operações IIB ou mudança de recursos. Caso seja necessário incluir ou alterar as configurações do Curió, essas configurações valerão para todos os *pods*.

## Utilizando o Canário em conjunto com o HPA
> :grey_exclamation: **Importante** 
> 
> O HPA é nativo do Kubernetes, diferente do Canário, que é uma implementação interna do Banco no chart bb-aplic. Isso os torna incompatíveis quando se pretende aumentar a quantidade de réplicas do Canário. 

Com o HPA ativo, a definição da quantidade de réplicas do chart do bb-aplic é desligada no momento da sincronização, tanto para o *deploy* regular quanto para o Canário. Dessa forma, só vale a quantidade de réplicas do HPA para o *deploy* regular, e para o Canário e controle a quantidade é fixada em uma réplica, independente do que for configurado em **replicaCount**.   

Para utilizar mais réplicas do Canário é necessário desligar o HPA no *deployment* e definir manualmente a quantidade de *pods* para o *deployment* regular e para o Canário.

## Saiba mais 
Conheça sobre conceitos do *deployment* Canário e a sua integração com o chart **bb-aplic**:
* Artigo explicando sobre [*Relesase Canary*](https://martinfowler.com/bliki/CanaryRelease.html).
* Documentação do [BB Aplic](https://charts.nuvem.bb.com.br/charts/bb-cloud/bb-aplic)

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/kubernetes/Como_utilizar_deployment_Canario.md&internalidade=kubernetes/Como_utilizar_deployment_Canario)
