> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Rodando o Sonar em versões abertas
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/sonar/arquivados/sonar-snapshot.md&amp;action_name=sonar/arquivados/sonar-snapshot)


Para solicitar a analise do sonar antes de fechar uma versão na master, podemos realizar a analise usando o sonar-snapshot em versoes abertas.

Para isso siga os seguintes passos:

1. Ir na opção `Construir com parametros` do jenkins do sua aplicação 
![image](imagens/Sonar-Snapshot-Jenkins-01.png)

2. Informar a branch que sera realizada a analise, lembrando que a versão deve ser aberta, e clicar em `Construir`.
![image](imagens/Sonar-Snapshot-Jenkins-02.png)

3. Depois de realizar o build da versão ir em [http://qsonar-snapshot.intranet.bb.com.br:9000/](https://qsonar-snapshot.intranet.bb.com.br:9000/) logar e ir para seu projeto para ver o resultado.
---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/sonar/sonar-snapshot.md&internalidade=sonar/sonar-snapshot)
