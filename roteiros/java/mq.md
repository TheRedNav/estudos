> :exclamation: Dê um feedback para esse documento no rodapé.[^1]

# Utilizando MQ com java quarkus
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/mq.md&amp;action_name=java/mq.md)

## Conceitos

O MQ (Message Queue) é um sistema de mensageria que permite que uma mensagem seja postada em um topico para ser lida posteriormente de forma assincrona. No Banco do Brasil uma dessas soluções é disponibilizada pela IBM , o IBM MQ, vamos chama-lo apenas de MQ para facilitar a explicação.

Essa solução é bastante usada para processamento assicrono com grandes volumes de requisições.

Ele consiste em ter pelo menos dois atores/sistemas, um que ira realizar a produção da mensagem no topico e outro que fara o consumo da mensagem desse mesmo topico.

Esse topico pode possuir caracteristicas proprias como o tamanho maximo de mensagens que ele pode possuir, quantidade de replicas para o topico, se vai possuir um topico de erro etc, geralmente isso é administrado pela equipe XXX.

Ao ler uma mensagem, o consumidor deve indicar que realizou essa ação para que o gerenciador do MQ saiba que aquela mensagem já foi processada e assim disponibilizar a proxima ao proximo consumidor. Essa ação é feita de forma bem parecida com as transações de um banco de dados, aonde o consumidor devera indicar que a leitura da mensagem foi realizada.

Existem 4 modos de confirmação de leitura:
1. AUTO_ACKNOWLEDGE: 
    A confirmação é realizada no mesmo momento da leitura, isso proporciona maior vazão na fila, mas a responsabilidade do processamento da mensagem fica maior para o consumidor, e nesse cenario se o consumidor gerar um erro não tratado no momento da leitura essa mensagem será perdida.
2. CLIENT_ACKNOWLEDGE:
    O consumidor deverá enviar uma confirmação de que processou a mensagem com sucesso, enquanto isso não é feito a mensagem fica bloqueada para outros consumidores até um tempo pre-definido, se não for confirmada dentro desse periodo ela ficara disponivel para leitura novamente. Dessa forma vamos ter uma garantia maior no processamento, mas em troca pode ocasionar um represamento maior das mensagens na fila.
3. DUPS_OK_ACKNOWLEDGE:
    Funciona de forma parecida com o modo `CLIENT_ACKNOWLEDGE`, contudo ele não realiza o bloqueio da mensagem para outros consumidores o que pode gerar um processamento em duplicidade. A vantagem é uma menor saturação do MQ uma vez que não existe o controle de bloqueio e garante que a mensagem vai ser processada, contudo a desvantagem seria a duplicidade da mensagem.
4. SESSION_TRANSACTED:
    Nesse modo é criada uma sessão aonde é realizado a leitura das mensagens que depois
    podem ser confirmadas em um unico comando de commit, é o modo que mais se parece com um banco de dados. A vantagem é a possibilidade de confirmação em bloco e realização de rollbacks, a desvantagem é um gerenciamento maior por parte do MQ para controlar essas sessões.

## Implementação com Quarkus

Fizemos um projeto de exemplo com uma descrição no README.md do projeto explicando como ele funciona e como executa-lo 

[MQ EXEMPLO BB](https://fontes.intranet.bb.com.br/dev/publico/exemplos/mq-quarkus-exemplo)


---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/java/mq/mq.md&internalidade=java/mq/mq)
