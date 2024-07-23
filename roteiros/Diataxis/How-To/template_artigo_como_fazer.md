# Template ‘Como fazer’  

Este template apresenta todas as categorias de informações que podem aparecer em documentações que ensinam os usuários a executar alguma ação. As categorias estão ordenadas conforme a sequência que deve aparecer no documento.   

## 1 - Métrica de acesso   

Para metrificar os acessos via Matomo, é indispensável inserir a mensagem de feedback junto com o código no início do documento. Acrescente o código logo abaixo da mensagem; ele ficará invisível para o usuário. Substitua os parâmetros URL e action_nome, conforme a localização do documento. 

### Sintaxe markdown  

```  
> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]  
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/template_artigo_como_fazer.md&amp;action_name=Diataxis/How-To/template_artigo_como_fazer)   
``` 

## 2 - Título   

Deve obrigatoriamente começar com ‘Como’, seguido de verbo e complemento. O título deve explicitar a ação que será detalhada no roteiro. É recomendado escrever os passos primeiros e depois nomear o documento com a ação que melhor se encaixa no que foi ensinado.  

### Sintaxe markdown  

Para o título primário, acrescente uma hashtag (#) no início.   

```   
# Como carregar o banco de dados Oracle  
```   
## 2.1 - Introdução

Escreva um texto breve, deixando explícito qual ação o usuário aprenderá no roteiro e qual a utilidade da funcionalidade. 

Por exemplo, "Este roteira ensina como realizar o processo de carregamento de um banco de dados Oracle. O carregamento adequado de dados em um banco Oracle é crucial para garantir a integridade e eficiência do armazenamento de informações. Essa ação permite que aplicativos e sistemas conectem-se de maneira segura ao banco de dados, facilitando a transmissão protegida de dados entre diferentes componentes do sistema e garantindo a robustez da gestão de dados".  

Este trecho não exige nenhuma marcação específica em markdown.

## 3 - Requisitos  

Lista com tudo que o usuário deve ter para que ele possa executar a ação. São exemplos de requisitos: configurações prévias, permissões, equipamentos, entre outros.  

Caso não haja requisitos para listar, esta categoria não deve ser incluída no documento.  

### Sintaxe markdown  

Para listas, use o asterisco (*). 

```   
* Conta na Oracle.  
```  

## 4 - Instruções 

As instruções devem ser identificadas por um título secundário, seguidas pelos passos numerados. Usualmente, cada ação que o usuário executa no computador é um novo passo numerado na documentação.  

Em cenários que envolvem mais etapas num mesmo documento, os títulos secundários devem ser dispostos na sequência desejada de execução pelo usuário. Isso assegura que, ao seguir essa ordem, o usuário chegue ao fim do documento concluindo com êxito a ação proposta no título primário. Por exemplo, se o título primário do documento for 'Como carregar o banco de dados Oracle' e o processo de carregamento envolver duas etapas, cada uma delas é uma instrução. 

### Sintaxe markdown  

Para o título secundário, acrescente duas hashtags (##) no início.  

```   
## Estabelecer conexão com o Oracle 

1. Ação 1. 

2. Numerar quantas ações forem necessárias 

. 

. 

5. Ação final que conclui o ato de estabelecer a conexão. 

## Testar conexão 

1. Ação 1. 

2. Numerar quantas ações forem necessárias 

. 

. 

5. Ação final que conclui o ato de testar a conexão. 
``` 

## 4.1 - Tags 

As tags são úteis para as ferramentas de busca. Adicione as tags junto com as instruções, no fim da seção. 

### Sintaxe markdown  

Coloque a palavra Tags em negrito.  

``` 
**Tags:** #bancodedados #oracle 
``` 

## 5 - A seguir 

Adicione outros roteiros relacionados ao assunto do artigo que você entende serem úteis para o usuário ampliar seu conhecimento sobre o assunto. Comunique nas frases sobre o que os roteiros tratam.  

Quando estiver abordando um processo macro, onde cada roteiro é uma etapa que precisa ser executada numa ordem específica, coloque nessa seção SOMENTE o link da próxima etapa. Dessa forma o usuário permanecerá na ordem correta de execução das ações.    

### Sintaxe markdown  

Para inserir links, utilize colchetes [] para definir as palavras do texto que ficarão destacadas e parênteses () para inserir o endereço https do roteiro.  

``` 
* Leia o roteiro [Como ativar restrições de chave estrangeira](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/restricao-chave-estrangeira) para habilitar as restrições de chaves estrangeiras nas tabelas de destino.  

* Leia o roteiro [Como replicar banco de dados continuamente](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/replicar-banco-continuo) para criar um pipeline separado e replicar as alterações no banco de dados.
``` 

## 6 - Precisa de ajuda? 

Utilize sempre a mesma frase, para padronização. 

### Sintaxe markdown 

``` 
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 
```   

## 7 - Este roteiro foi útil? 

Adicione os emojis e link para feedback. 

### Sintaxe markdown 

``` 
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/template_artigo_como_fazer.md&internalidade=Diataxis/How-To/template_artigo_como_fazer) 
``` 

# Roteiro completo em markdown 

Copie e cole no seu roteiro, trocando as informações necessárias.  

``` 
> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]  
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/template_artigo_como_fazer.md&amp;action_name=Diataxis/How-To/template_artigo_como_fazer)   

# Como carregar o banco de dados Oracle  

Este roteira ensina como realizar o processo de carregamento de um banco de dados Oracle. O carregamento adequado de dados em um banco Oracle é crucial para garantir a integridade e eficiência do armazenamento de informações. Essa ação permite que aplicativos e sistemas conectem-se de maneira segura ao banco de dados, facilitando a transmissão protegida de dados entre diferentes componentes do sistema e garantindo a robustez da gestão de dados.

## Requisitos   

* Conta na Oracle.  

## Estabelecer conexão com o Oracle 

1. Ação 1. 

2. Numerar quantas ações forem necessárias 

. 

. 

5. Ação final que conclui o ato de estabelecer a conexão. 

## Testar conexão 

1. Ação 1. 

2. Numerar quantas ações forem necessárias 

. 

. 

5. Ação final que conclui o ato de testar a conexão. 
 
**Tags:** #bancodedados #oracle 

## A seguir 

* Leia o roteiro [Como ativar restrições de chave estrangeira](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/restricao-chave-estrangeira) para habilitar as restrições de chaves estrangeiras nas tabelas de destino.  

* Leia o roteiro [Como replicar banco de dados continuamente](https://fontes.intranet.bb.com.br/psc/publico/atendimento/-/wikis/Roteiros/replicar-banco-continuo) para criar um pipeline separado e replicar as alterações no banco de dados. 

## Precisa de ajuda? 

Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar! 

## Este roteiro foi útil? 

[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/Diataxis/How-To/template_artigo_como_fazer.md&internalidade=Diataxis/How-To/template_artigo_como_fazer) 
``` 

 

 
