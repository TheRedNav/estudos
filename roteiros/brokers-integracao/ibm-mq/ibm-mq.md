> :exclamation: Dê um feedback para esse documento no rodapé.[^1]
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/brokers-integracao/ibm-mq/ibm-mq.md&amp;action_name=brokers-integracao/ibm-mq/ibm-mq.md)
# IBM WebSphere MQ

[Roteiro da IBM sobre o IBM WebSphere MQ](https://www.ibm.com/docs/pt-br/ibm-mq/7.5?topic=ssfksj-7-5-0-com-ibm-mq-pro-doc-q001020--htm)

- [Conceitos](#conceitos)
- [Equipe responsável GESIT](#equipe-responsável-gesit)
- [Implementação com Quarkus](#implementação-com-quarkus)
- [Roteiro para solicitar a criação de filas](#roteiro-para-solicitar-a-criação-de-filas)
- [Painel WEB para visualizar as filas MQ](https://app.mqs.intranet.bb.com.br/mq)
- Visualização de Filas no ambiente mainframe
  - [Desenvolvimento](#visualização-de-filas-no-ambiente-mainframe-desenvolvimento)
  - [Homologação](#visualização-de-filas-no-ambiente-mainframe-homologação)
  - [Produção](#visualização-de-filas-no-ambiente-mainframe-produção)  
- [Códigos de Erro do MQ](#códigos-de-erro-do-mq)
- [JOB MQ mainframe para teste de leitura de fila](#job-mq-mainframe-para-teste-de-leitura-de-fila)
- [JOB MQ mainframe para teste de gravação em fila](#job-mq-mainframe-para-teste-de-gravação-em-fila)


## Conceitos

O MQ (Message Queue) é um sistema de mensageria que permite que uma mensagem seja postada em um topico para ser lida posteriormente de forma assincrona. No Banco do Brasil uma dessas soluções é disponibilizada pela IBM , o IBM MQ, vamos chama-lo apenas de MQ para facilitar a explicação.

Essa solução é bastante usada para processamento assicrono com grandes volumes de requisições.

É possível utilizar o MQ no formato de tópico ou de fila.

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

### Integração entre plataformas

Elas servem para fazermos integração entre diversas plataformas diferentes, como:
- Ambiente batch e online (CICS)
- Mainframe e plataforma baixa

### Queue Manager

É o Queue Manager que gerencia as suas filas. Ele depende do ambiente (des/hom/prod), do provedor da fila (onde roda ele) e do tipo de integração a ser feita.

### Correlation ID

É possível fazer um PUT informando um Código de Correlação (correlation ID) e só quem vai conseguir tirar a mensagem é quem fizer um GET pelo correlation ID.

## Equipe responsável GESIT

[DITEC/GESIT/G1/E3 DESEMPENHO MF/IIT E MQ](https://humanograma.intranet.bb.com.br/uor/338675)

## Implementação com Quarkus

Fizemos um projeto de exemplo com uma descrição no README.md do projeto explicando como ele funciona e como executa-lo 

[MQ EXEMPLO BB](https://fontes.intranet.bb.com.br/dev/publico/exemplos/mq-quarkus-exemplo)

## Roteiro para solicitar a criação de filas

Entrar no GSTI

Gerenciamento de requisições

Requisições

Abrir Nova Requisição

Clicar em "Mais resultados"

Clicar em "Integração"

Clicar em "Criação de Fila MQ"

- Plataforma de Criação da Fila: (Indicação da plataforma onde a fila será criada): z/OS

Daí no final, será gerado um pedido que precisa ser despachado pelo gerente de equipe.

## Visualização de Filas no ambiente mainframe - desenvolvimento

Entrar no TSO de desenvolvimento:
> Logon Request ....... DSAT &USERID/&USERPW PROC(TSO390) 

Digitar MQADM

Preencher os parâmetros abaixo:

```
DESA             IBM WebSphere MQ for z/OS V8.0.0 - Main Menu               
Complete fields. Then press Enter.                                          
Action  . . . . . . . . . . 1     0. List with filter   4. Manage           
                                  1. List or Display    5. Perform          
                                  2. Define like        6. Start            
                                  3. Alter              7. Stop             
                                  8. Command                                
Object type . . . . . . . . QUEUE         +                                 
Name  . . . . . . . . . . . QE.IMC.FIX50                                    
Disposition . . . . . . . . Q  Q=Qmgr, C=Copy, P=Private, G=Group,          
                               S=Shared, A=All                              
Connect name  . . . . . . . Q0G1  - local queue manager or group            
Target queue manager  . . . Q0G1                                            
           - connected or remote queue manager for command input            
Action queue manager  . . . *     - command scope in group                  
Response wait time  . . . . 30    5 - 999 seconds                           

(C) Copyright IBM Corporation 1993,2014. All rights reserved.               
```
 

Dar enter até aparecer a listagem das filas conforme abaixo
```
Type action codes, then press Enter.  Press F11 to display queue status.    
 1=Display   2=Define like   3=Alter   4=Manage                             
    Name                                              Type      Disposition
<>  QE.IMC.FIX50                                      QUEUE     QMGR    Q0G1
_   QE.IMC.FIX50                                      QLOCAL    QMGR    Q0P1
- Selecionar com 1 (display) e dar enter para exibir a tela abaixo
Queue name  . . . . . . . . . QE.IMC.FIX50                               
Disposition . . . . . . . . : QMGR    Q0P1                               
Description . . . . . . . . : SINAL BOVESPA - PROTOCOLO FIX CA           
                              NAL 50                                     
Put enabled . . . . . . . . : Y  Y=Yes, N=No                             
Get enabled . . . . . . . . : Y  Y=Yes, N=No                             
Usage . . . . . . . . . . . : N  N=Normal, X=XmitQ                       
Storage class . . . . . . . : BATCH2                                     
CF structure name . . . . . :                                            
Dynamic queue type  . . . . : N  N=Non-dynamic (Predefined), T=Temporary,
                                 P=Permanent, S=Shared                   
Page set identifier . . . . :                                            
Use counts - Output . . . . : 0             Input . . . . : 0            
Current queue depth . . . . : 0                                          
```

Dicas:
> Campo Current queue depth = quantidade de mensagens paradas na fila

> Use counts - Output       = quantidade de processos conectados na fila para fazer PUT (gravação)

> Use counts - Input        = quantidade de processos conectados na fila para fazer GET (leitura)

## Visualização de Filas no ambiente mainframe - homologação

Entrar no TSO de homologação
> HMBT &USERID/&USERPW proc(TSO390) 

Digitar MQADM

```
HOMB             IBM WebSphere MQ for z/OS V8.0.0 - Main Menu                  

Complete fields. Then press Enter.                                             
Action  . . . . . . . . . . 1     0. List with filter   4. Manage              
                                  1. List or Display    5. Perform             
                                  2. Define like        6. Start               
                                  3. Alter              7. Stop                
                                  8. Command                                   
Object type . . . . . . . . QUEUE         +                                    
Name  . . . . . . . . . . . QE.IMC*                                            
Disposition . . . . . . . . A  Q=Qmgr, C=Copy, P=Private, G=Group,             
                               S=Shared, A=All                                 
                                                                               
Connect name  . . . . . . . Q8G1  - local queue manager or group               
Target queue manager  . . .                                                    
           - connected or remote queue manager for command input               
Action queue manager  . . . *     - command scope in group                     
Response wait time  . . . . 30    5 - 999 seconds                              

(C) Copyright IBM Corporation 1993,2014. All rights reserved.          
```

## Visualização de Filas no ambiente mainframe - produção

Entrar no TSO de produção (é preciso saber em qual SYSPLEX está a fila)
>  BRBT &USERID/&USERPW proc(TSO390) 

Queue managers comuns em produção:
> Q3G1 no SYSPLEX I  ou Q2G3 no SYSPLEX II

Complete fields. Then press Enter.                                          

                                                                            
```
Action  . . . . . . . . . . 1     0. List with filter   4. Manage           
                                  1. List or Display    5. Perform          
                                  2. Define like        6. Start            
                                  3. Alter              7. Stop             
                                  8. Command                                
Object type . . . . . . . . QUEUE         +                                 
Name  . . . . . . . . . . . QE.IMC.FIX*                                     
Disposition . . . . . . . . A  Q=Qmgr, C=Copy, P=Private, G=Group,          
                               S=Shared, A=All                              

Connect name  . . . . . . . Q3G1  - local queue manager or group            
Target queue manager  . . . Q3G1                                            
           - connected or remote queue manager for command input            
Action queue manager  . . . *     - command scope in group                  
Response wait time  . . . . 30    5 - 999 seconds                           

(C) Copyright IBM Corporation 1993,2014. All rights reserved. 
```

## Códigos de Erro do MQ

É possível pesquisar pelos erros na internet ou também no ambiente mainframe, conforme abaixo:

Entrar em qualquer CICS

Apertar tecla `Pause Break`

Digitar "MQRCXXXX" onde o "XXXX" é o código do erro.

Exemplo: MQRC2087 
> QUEUE MANAGER REMOTO DESCONHECIDO 

## JOB MQ mainframe para teste de leitura de fila

Para se conectar em uma Fila MQ e fazer a leitura dos dados, é possível rodar o job de teste abaixo.

Você precisa informar os parâmetros na sequência, como no exemplo:
> PARM=('Q0G1,QE.IIB.BB.BSB.MVS.PB.4076543.1,99999,B,S')

Significado dos parâmetros:
- VAR1=QMGR A CONECTAR: EX. Q0D1, Q0P3, Q0F1, ETC              
- VAR2=NOME DA FILA A SER LIDA                                 
- VAR3=QUANTIDADE DE MENSAGENS A SER LIDA (5 CARACTERES)       
- VAR4=B PARA BROWSE OU D PARA DELETE                          
- VAR5=S PARA SYNCPOINT OU N PARA NO-SYNCPOINT 

```
//J909GET JOB 'TESTE',                                                  
//         ROS.D189516.FELIPE.H,REGION=0M,MSGCLASS=R,                   
//         COND=((08,EQ),(12,EQ),(16,EQ),(888,EQ)),                     
//*        TYPRUN=SCAN,                         * TESTE DE JCL INIBIDO. 
//         CLASS=A                                                      
//*-------------------------------------------------------------------* 
//* VERIFIQUE A REAL NECESSIDADE DE UTILIZAR OS CARTOES (JOBLIB)        
//* ABAIXO (INIBIDOS P/ EVITAR CONFLITOS C/ PROCS CORPORATIVAS):        
//*-------------------------------------------------------------------* 
//*JOBLIB   DD DSN=SISTE.CICS.LOADLIB,DISP=SHR        * DESEN 1 C/ DB2. 
//*         DD DSN=SISTE.CICS.LOADLIB.SEC,DISP=SHR    * DESEN 2 C/ DB2. 
//*         DD DSN=SISTE.LOADLIB,DISP=SHR             * DESEN 1 S/ DB2. 
//*         DD DSN=SISTE.LOADLIB.SEC,DISP=SHR         * DESEN 2 S/ DB2. 
//*         DD DSN=CICS.CPDLIB,DISP=SHR               * PRODU   C/ DB2. 
//*         DD DSN=CPDLIB,DISP=SHR                    * PRODU   S/ DB2. 
//*-------------------------------------------------------------------* 
//* I560GET JOB   '(J357NPD220102,HORA:163918)','TSO.&SYSUID',          
//*          REGION=0M,NOTIFY=&SYSUID,                                  
//*          COND=((12,EQ),(16,EQ),(888,EQ)),                           
//*          MSGCLASS=T,MSGLEVEL=1,CLASS=O                              
//*------------------------------------------------------------------*  
/*JOBPARM SYSAFF=DESA                                                   
//*-       VAR1=QMGR A CONECTAR: EX. Q0D1, Q0P3, Q0F1, ETC              
//*-       VAR2=NOME DA FILA A SER LIDA                                 
//*-       VAR3=QUANTIDADE DE MENSAGENS A SER LIDA (5 CARACTERES)       
//*-       VAR4=B PARA BROWSE OU D PARA DELETE                          
//*-       VAR5=S PARA SYNCPOINT OU N PARA NO-SYNCPOINT                 
//*------------------------------------------------------------         
//GET          EXEC PGM=MQSGETDI,                                       
//     PARM=('Q0G1,QE.IIB.BB.BSB.MVS.PB.4076543.1,99999,B,S')           
//*    PARM=('Q0P1,QE.IIB.LOGGTR.AAPF.A,99999,D,S')                     
//*    PARM=('Q0P2,QE.IIB.LOGGTR.AAPJ.A,99999,D,S')                     
//*    PARM=('Q0P1,QE.IIB.LOGGTR.GRDR,99999,D,S')                       
//*    PARM=('Q0P1,QE.IIB.LOGGTR.INTRANET.A,99999,D,S')                 
//*    PARM=('Q0P2,QE.IIB.LOGGTR.OUTROS.A,99999,D,S')                   
//*    PARM=('Q0P1,QE.IIB.LOGGTR.AAPF.A,99999,B,S')                     
//*    PARM=('Q0P1,QE.IIB.LOGGTR.AAPJ.A,99999,D,S')                     
//*    PARM=('Q0P1,QE.IIB.LOGGTR.OUTROS.A,99999,D,S')                   
//*    PARM=('Q0P1,QE.IIB.LOGGTR.INTRANET.A,99999,D,S')                 
//*    PARM=('Q0F1,QE.CRV.MATH.RSPT,99999,D,S')                 
//*    PARM=('Q0G2,QE.IIB.RQSC.SAID.MF,99999,D,S')              
//*    PARM=('Q0P2,QE.IIB.RQSC.ENTD.MF,99999,D,S')              
//*    PARM=('Q0G2,QE.IIB.RPST.SAID.MF,99999,D,S')              
//*    PARM=('Q0G2,QE.IIB.RPST.SAID.MF,99999,D,S')              
//*    PARM=('Q0F1,QE.IIB.RQSC.ENTD.MF,99999,D,S')              
//*    PARM=('Q0F1,QE.IIB.BSB.MVS.PB,99999,D,S')                
//*    PARM=('Q0P2,QE.IIB.CIC.SEM.PTL.BACKOUT,99999,B,N')       
//*    PARM=('Q0P2,QE.IIB.BB.BSB.MVS.PB.763782.1,99999,B,N')    
//*    PARM=('Q0G1,CSQ.CE352707A5145569,99999,B,N')             
//STEPLIB      DD DISP=SHR,DSN=CPDLIB                           
//SYSOUT       DD   SYSOUT=*                                    
//SYSPRINT     DD   SYSOUT=*                                    
//*------------------------------------------------------------ 
```

## JOB MQ mainframe para teste de gravação em fila

No exemplo abaixo, as mensagens abaixo serão enviadas para a fila MQ QE.IIB.BOD.TST.03 que está no Queue Manager Q0G1.
```
TESTE FELIPE 01 
TESTE FELIPE 02 
TESTE FELIPE 03
```

Importante: preservar o número de caracteres da Fila e da coluna onde começa o Queue Manager (Q0G1):
> PARM=('QE.IIB.BOD.TST.03              Q0G1')

```
//I560FHGW  JOB   'PUT MSG MQS','TSO.&SYSUID',                         
//          REGION=4M,                                                 
//          COND=((12,EQ),(16,EQ),(888,EQ)),                           
//          MSGCLASS=T,MSGLEVEL=1,CLASS=O                              
//*------------------------------------------------------------------* 
//* CADA LINHA DENTRO DO SYSIN SERAH UMA MENSAGEM A SER ENVIADA    --* 
//*------------------------------------------------------------------* 
//PUT1     EXEC PGM=IIBPMQSP,                                          
//             PARM=('QE.IIB.BOD.TST.03              Q0G1')            
//STEPLIB  DD DSN=SISTE.LOADLIB,DISP=SHR                               
//         DD DSN=DSS.MQSP01.V710.SCSQLOAD,DISP=SHR                    
//         DD DSN=DSS.MQSP01.V710.SCSQANLE,DISP=SHR                    
//         DD DSN=DSS.MQSP01.V710.SCSQAUTH,DISP=SHR                    
//STDOUT   DD SYSOUT=*                                                 
//STDERR   DD SYSOUT=*                                                 
//SYSPRINT DD SYSOUT=*                                                 
//SYSIN    DD *                                                        
TESTE FELIPE 01 
TESTE FELIPE 02 
TESTE FELIPE 03 
```

---
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/brokers-integracao/ibm-mq/ibm-mq.md&internalidade=brokers-integracao/ibm-mq/ibm-mq)
