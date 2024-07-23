> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/certificado/RoteiroCertificadoImagem.md&amp;action_name=certificado/RoteiroCertificadoImagem)
## Como adicionar certificados em imagens docker?

### O que é um certificado SSL?

O termo <b>SSL</b>, ou <i>Secure Sockets Layer</i>, refere-se a um protocolo de segurança digital que possibilita a comunicação criptografada entre um navegador e um domínio de site. Apesar de sua relevância passada, a tecnologia está em desuso e está sendo gradualmente substituída pelo TLS (Transport Layer Security).

Em situações nas quais não há certificado SSL/TLS, os dados enviados do cliente para o servidor são transmitidos como textos simples, o que significa que podem ser capturados e lidos facilmente por terceiros. Por outro lado, em aplicações com certificado SSL/TLS, os dados são criptografados durante a transmissão, o que torna mais difícil o vazamento de dados sensíveis.


### Por que minha imagem docker precisa de um certificado?

O uso do SSL/TLS é essencial para garantir a segurança na transmissão de informações sensíveis, como dados pessoais, informações de pagamento, ou credenciais de acesso:

Garante a proteção durante a transmissão desses dados, evitando que sejam capturados por terceiros mal-intencionados.
Ao contrário da transferência de dados em texto simples, onde a conexão com o servidor não é criptografada, o SSL/TLS oferece uma camada de segurança robusta, dificultando a interceptação da conexão e a subsequente tentativa de roubo de dados por hackers.

### Aonde conseguir os certificados do BB?

> Certificados oficiais

Para cada ambiente - desenvolvimento, homologação e produção - existe uma PKI correspondente. 

PKI ou <i>Public Key Infrastructure</i> é a Infraestrutura de Chave Pública, que permite a geração, armazenamento, distribuição e gerenciamento de certificados digitais que irão garantir a integridade das informações em ambientes digitais.

Abaixo seguem os endereços dos sites das PKIs para cada ambiente, nos quais os certificados oficiais do BB podem ser encontrados:

PKI de produção:
https://pki.bb.com.br

PKI de homologação:
https://pki.hm.bb.com.br

PKI de desenvolvimento:
https://pki.desenv.bb.com.br

Mas, nos sites mencionados acima, há uma variedade de certificados disponíveis. Então, quais desses certificados preciso baixar?

Atualmente, em novembro de 2023, estamos empregando a terceira geração de certificados (v3), os quais têm validade até 2035.

Para sua imagem docker, serão necessários:
1.o certificado raiz (=raiz_v3), encontrado em:
- Produção: https://pki.bb.com.br/ACRAIZC/cacerts/raiz_v3.der
- Homologação: https://pki.hm.bb.com.br/ACRAIZC/cacerts/raiz_v3.der
- Desenvolvimento: https://pki.desenv.bb.com.br/ACRAIZC/cacerts/raiz_v3.der

2.o certicado de servidores v1 (=servidores_v1), encontrado em:
- Produção: https://pki.bb.com.br/ACINTA5/cacerts/acsr_v1.der
- Homologação: https://pki.hm.bb.com.br/ACINTA5/cacerts/acsr_v1.der
- Desenvolvimento: https://pki.desenv.bb.com.br/ACINTA5/cacerts/acsr_v1.der

E preciso baixar os certificados de todos ambientes? Em geral, sim.

E se utilizo uma imagem `dev/dev-java` ou `dev/dev-node` preciso fazer os passos abaixo? Não, não precisa porque essas imagens que já estão com os certificados do BB instalados. 

Caso esteja customizando uma imagem docker de terceiros, entre no portal PKI do ambiente correspondente, por exemplo, PKI de produção [pki Produção](https://pki.bb.com.br) e copie o certificado raiz_v3 :
  - Menu lateral esquerdo
     - localize a seção "AUTORIDADE CERTIFICADORA BB v3":
        - clique em "Autoridade Certificadora Servidores v1": 
          - e baixe os dois certificados: 
            - "Certificado Raiz AC Banco do Brasil v3"
            - "Certificado Intermediário da AC SERVIDORES v1"
---

Note que os certificados da PKI-BB estão em formato de arquivo de extensão .DER (binário).
Note também que existem dois certificados, um da autoridade certificadora (=raiz_v3) e um que é chamado de certificado da intermediária (=acsr_v1).

Os certificados no formato .DER são codificados em binário (NHR), enquanto os certificados no formato .CRT são em texto codificado em Base64, que é mais fácil para compartilhar entre humanos e sistemas.

Então, para importar em uma imagem docker, precisaremos converter o arquivo .DER. para .CRT no próximo passo.

#### Convertendo um arquivo .DER para .CRT com OpenSSL

Para converter um arquivo .DER para .CRT usando o OpenSSL, siga estes passos a seguir:

1. Obtenha o OpenSSL: Certifique-se de ter o OpenSSL instalado no seu sistema.

2. Abra o Terminal ou Prompt de Comando: Acesse o terminal ou prompt de comando no seu sistema operacional.

3. Execute o comando de conversão: No Terminal ou Prompt de comando, digite o comando abaixo, substituindo os nomes dos arquivos conforme necessário:

``` bash
openssl x509 -inform DER -outform PEM -in raiz_v3.der -out raiz_v3.crt
```

Para converter o certificado da intermediária, repita o comando acima, substitundo `raiz_v3.der` pelo o nome do arquivo .DER que você deseja converter e `raiz_v3.crt` pelo nome que deseja para o novo arquivo .CRT, exemplo:

``` bash
openssl x509 -inform DER -outform PEM -in acsr_v1.der -out acsr_v1.crt
```


4. Verifique o arquivo gerado: Após a execução do comando, verifique se o arquivo .CRT foi gerado com sucesso e se não está em branco.

Com estes passos descritos acima, você pode converter um arquivo .DER em .CRT utilizando o OpenSSL, facilitando o uso desses certificados em diferentes contextos ou sistemas.

#### Copiando os certificados .CRT para o projeto GIT

> Imaginando um exemplo abaixo que já tenha feito um git clone https://fontes.intranet.bb.com.br/sigla/meuprojetoimagem.git para C:\Temp\projetos se for Windows ou /home/minhachave/Desktop/projetos se for Linux

1. Crie, dentro do seu projeto git da imagem, uma pasta para os certificados: Estando dentro da pasta do seu projeto da imagem, use o comando `mkdir certs` para criar uma pasta denominada certs.
2. Copie os certificados para a pasta do projeto: Para isso, utilize o comando cp (no Linux/macOS) ou copy (no Windows) para copiar os certificados .CRT da pasta que converteu os arquivos .DER para a pasta do projeto. Por exemplo:

No Linux/macOS:

```bash
cp /home/minhachave/Downloads/raiz_v3.crt /home/minhachave/Desktop/projetos/meuprojetoimagem/certs
```

No Windows:

```bash
copy C:\User\minhachave\Downloads\raiz_v3.crt C:\Temp\projetos\meuprojetoimagem\certs\raiz_v3.crt
```

OBS: repita o passo acima para o certificado da intermediária (=acsr_v1), alterando o nome do arquivo.

3. Verifique as mudanças: Após copiar os certificados, verifique se foram adicionados corretamente à pasta do projeto executando `git status` no terminal. Isso mostrará os arquivos não rastreados.

4. Adicione e comite as mudanças: Use os comandos `git add certs/raiz_v3.crt` e `git add certs/acsrv_v1.crt`  para adicionar os certificados e `git commit -m "Adicionando certificados .CRT"` para fazer um commit das alterações.

5. Envie as Alterações para o Repositório Remoto: Se o seu projeto possui um repositório remoto, utilize `git push` para enviar as alterações para o repositório compartilhado.

Ao seguir esses passos, você pode facilmente adicionar certificados .CRT a um projeto Git, permitindo que eles sejam versionados e compartilhados com a equipe de desenvolvimento.


#### Copiando os certificados no Dockerfile

> O exemplo abaixo tem como base uma imagem RedHat Ubi 9. Mas se caso estiver usando uma outra imagem, exemplo, Debian, Ubuntu, os comandos poderão variar mas a lógica é a mesma: copiar os certificados para uma pasta-padrão de certificados do sistema operacional e um comando de importar certificados.

Após ter comitado o projeto de imagem com a pasta `certs` criada, o próximo passo é adicionar as seguintes instruções no Dockerfile:

1. Abra o arquivo Dockerfile com um editor de texto;
2. Adicione as linhas abaixo da tag LABEL, conforme exemplo:

```Dockerfile
FROM (binarios/nomeimage:versão)

LABEL 

USER root
COPY certs /etc/pki/ca-trust/source/anchors/
RUN update-ca-trust
USER 185

```


3. Salve o arquivo Dockerfile;
4. Commit no git com `git add Dockerfile` e `git commit -m "Adicionando certificados .CRT"`;
5. Realize o comando `git push`;


### Observações importantes

Em alguns casos, dependendo da lib utilizada, será necessário importar os certificados diretamente para a aplicação, além dos passos acima de importar para o sistema operacional da imagem docker. 

Outra observação importante é que algumas imagens de terceiros e aplicações utilizam o formato de arquivo .PEM ao invés de .CRT. 

O formato .PEM foi criado para agrupar, EM UM MESMO ARQUIVO, um monte de arquivos .CRT. Sendo assim, os certificados da raiz e da intermediária estariam em um ÚNICO ARQUIVO. Porém na prática vemos arquivos .PEM com um certificado apenas, o que contraria um pouco o objetivo para o qual foi criado que era de facilitar a importação de muitos certificados em um <i>bundle</i>.


#### Para mais informações
Caso o problema com certificados seja outro, tente ver também [Solicitação de certificados interno](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/solicitacao-de-certificados-interno) e [Geração de certificados PKI-BB Servidores](https://fontes.intranet.bb.com.br/pki/publico/atendimento/-/wikis/Pki-OAAS/Geracao) 
criados e mantidos pelas equipes que mantém o serviço.

# Tags
#certificado #ssl #tls #docker #solicitar #interno #imagem #pki

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/certificado/RoteiroCertificadoImagem.md&internalidade=certificado/RoteiroCertificadoImagem)
