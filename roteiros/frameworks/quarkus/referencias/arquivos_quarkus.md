> :speech_balloon: Deixe o seu feedback sobre este roteiro no rodapé. [^1]   
![](https://eni.bb.com.br/eni1/matomo.php?idsite=469&amp;rec=1&amp;url=https://fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md&amp;action_name=frameworks/quarkus/referencias/arquivos_quarkus) 

# Arquivos mais comuns encontrados em projetos Quarkus

Esse roteiro apresenta as descrições, localizações e funções dos principais arquivos relacionados a atualização de projetos Quarkus. 

| Arquivo | Descrição | Localização | Função |
| - | - | - | - |
| pom.xml | Arquivo POM (Project Object Model) do Maven | Raiz do projeto | Configurar o projeto Java, incluindo suas dependências, plugins e configurações de construção. |
| Dockerfile | Arquivo de texto | Raiz do projeto | Construir uma imagem Docker com um sistema operacional e uma aplicação Java no formato JAR. |
| docker-compose | Arquivo YAML | Geralmente na pasta /run/docker-compose.yaml | Definir os serviços, redes e volumes para um aplicativo Docker multicontêiner. |
| application.properties | Arquivo de propriedades | Pasta /src/main/resources | Configurar o aplicativo Quarkus. | 
| .env | Arquivo de ambiente | Raiz do projeto | Definir variáveis de ambiente; usado principalmente com o Docker Compose. **NÃO** deve ser comitado no Git e seu conteúdo deve ser explicado no README do projeto. |
| .env_curio | Arquivo de ambiente do Curió | Raiz do projeto | Definir variáveis de ambiente. **NÃO** deve ser comitado no Git e seu conteúdo deve ser explicado no README do projeto. |
| run.sh | Script de shell | Raiz do projeto | Receber os inputs para atualizar as configurações de acesso do Maven; realizar o build do projeto; executar o Docker Compose para inicializar os serviços contidos no arquivo docker-compose.yaml, os quais incluem as imagens Docker necessárias para o ambiente de execução da aplicação. |
| .gitignore | Arquivo de configuração do Git | Raiz do projeto | Especificar quais arquivos e diretórios devem ser ignorados pelo controle de versão. |
| .dockerignore | Arquivo de configuração do Docker | Raiz do projeto | Especificar quais arquivos e diretórios devem ser ignorados durante a construção de uma imagem Docker. |
| maven-wrapper.properties | Arquivo de configuração do Maven | Pasta .mvn/wrapper | Configurar o Maven (gerenciador de pacotes) do projeto. |

## Precisa de ajuda?
Em caso de problemas na execução de qualquer roteiro, abra uma [nova issue](https://fontes.intranet.bb.com.br/dev/publico/atendimento/-/issues) e relate a situação. O time de atendimento da devCloud está disponível para auxiliar!  

## Este roteiro foi útil?
[^1]: [👍👎](http://feedback.dev.intranet.bb.com.br/?origem=roteiros&url_origem=fontes.intranet.bb.com.br/dev/publico/roteiros/-/blob/master/frameworks/quarkus/referencias/arquivos_quarkus.md&internalidade=frameworks/quarkus/referencias/arquivos_quarkus)
