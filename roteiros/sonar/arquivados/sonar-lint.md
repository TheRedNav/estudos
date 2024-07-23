> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Configuração do SonarLint
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/sonar/sonar-lint.md&amp;action_name=sonar/sonar-lint)

## Pré requisito: Token

- Acesse https://qsonar.intranet.bb.com.br/ , clique no seu avatar e selecione "My Account":
![image](imagens/SonarToken-01.png)

- Clique em "Security" e gere um novo token:
![image](imagens/SonarToken-02.png)

- Guarde o token, para que ele seja utilizado nos passos a seguir

## IDEs

### IntelliJ

- Instale o plugin do SonarLint via menu: Settings > Plugins

![image](imagens/Sonar-Lint-Intellij-01.png)

- Acesse Settings -> Tools -> SonarLint 

![image](imagens/Sonar-Lint-Intellij-02.png)

- Na tela apresentada, adicione uma conexão "SonarQube/SonarCloud"
- Dê um nome para a conexão e informe a URL do Sonar:https://qsonar.intranet.bb.com.br/

![image](imagens/Sonar-Lint-Intellij-03.png)

- Faça o teste de conectividade na opção proxy settings, teste com a opção Auto-detect.
- Se não funcionar configure o proxy como a imagem e no campo no-proxy informe `localhost, 127.0.0.1, *.labbs.com.br, *.bb.com.br, www3.bancobrasil.com.br`
![image](imagens/Sonar-Lint-Intellij-07.png)

- Clique em "Next"
- Em "Authentication type" informe "Token"

- Copie o token gerado anteriormente e insira nesta tela:
- Clique em "Next" e "Finish"
![image](imagens/Sonar-Lint-Intellij-04.png)

- Acesse Settings -> Tools -> SonarLint > Project Settings
- Selecione "Bing project to SonarQuebe / SonarCloud
- Em "Project" informe o nome do seu projeto:
![image](imagens/Sonar-Lint-Intellij-06.png)



---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/sonar/sonar-lint.md&internalidade=sonar/sonar-lint)
