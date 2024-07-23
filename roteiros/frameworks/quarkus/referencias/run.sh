#!/bin/bash
set -e

# Funcao para parar a execução do curio
function stopCurio() {
  DOCKER_CURIO=$(docker ps -a | grep CURIO | wc -l)
  if [[ ${DOCKER_CURIO} -ge 1 ]]; then
    printf '======================================= CURIO STOP ========================================\n'
    printf '\n'
    docker stop CURIO 
    docker rm CURIO
  fi
}

# Função para verificar se existe alguma imagem de curio com o nome CURIO em execução
# se existir ele vai parar a imagem e subir uma nova com as envs definidas no .env_curio
function startCurio() {
  printf '========================== INICIANDO EXECUCAO DO CURIO NO DOCKER ==========================\n'
  printf '\n'
  DOCKER_CURIO=$(docker ps -a | grep CURIO | wc -l)
  if [[ ${DOCKER_CURIO} -ge 1 ]]; then
    stopCurio
  fi
  printf '==================================== DOCKER RUN CURIO =====================================\n'
  DOCKER_CURIO_IMAGE="bb/iib/iib-curio"
  DOCKER_COMPOSE_CURIO_VERSION=$(cat "$PWD"/run/docker-compose.yaml |grep image: |grep curio |grep -v '#' |awk -F: '{print $3}')
  echo "Versão do Curió no docker-compose.yaml:" $DOCKER_COMPOSE_CURIO_VERSION
  if [[ -n "$DOCKER_COMPOSE_CURIO_VERSION" ]]; then
    DOCKER_CURIO_IMAGE_VERSION=$DOCKER_COMPOSE_CURIO_VERSION
  else
    echo "Versão do Curió não encontrada no arquivo docker-compose.yaml. Subindo versão default (0.8.4)..."
    DOCKER_CURIO_IMAGE_VERSION="0.8.4"
  fi
  DOCKER_CURIO_FULL_IMAGE="$DOCKER_REGISTRY/$DOCKER_CURIO_IMAGE:$DOCKER_CURIO_IMAGE_VERSION"
  docker run -d --network host --env-file "$PWD"/.env_curio --name CURIO "$DOCKER_CURIO_FULL_IMAGE"
  DOCKER_CURIO_RUNNING=$(docker ps -a | grep CURIO | grep Up | wc -l)
  if [[ ${DOCKER_CURIO_RUNNING} -ge 1 ]]; then
      trap 'docker stop CURIO && docker rm CURIO 1>/dev/null && exit 1' SIGINT; (docker logs --follow CURIO & wait $!)
  fi
}

# Funcao para parar a execução do curio
function stopJaeger() {
  DOCKER_JAEGER=$(docker ps -a | grep JAEGER | wc -l)
  if [[ ${DOCKER_JAEGER} -ge 1 ]]; then
    printf '======================================= JAEGER STOP ========================================\n'
    printf '\n'
    docker stop JAEGER
    docker rm JAEGER
  fi
}

# Função para verificar se existe alguma imagem de Jaeger com o nome IBM_MQ em execução
# se existir ele vai parar a imagem e subir uma nova com as envs definidas no .env_jaeger
function startJaeger() {
  printf '========================== INICIANDO EXECUCAO DO JAEGER NO DOCKER ==========================\n'
  printf '\n'
  DOCKER_JAEGER=$(docker ps -a | grep JAEGER | wc -l)
  if [[ ${DOCKER_JAEGER} -ge 1 ]]; then
    stopJaeger
  fi
  printf '==================================== DOCKER RUN JAEGER =====================================\n'
  DOCKER_JAEGER_IMAGE="jaegertracing/all-in-one"
  DOCKER_JAEGER_IMAGE_VERSION="latest"
  DOCKER_JAEGER_FULL_IMAGE="$DOCKER_REGISTRY/$DOCKER_JAEGER_IMAGE:$DOCKER_JAEGER_IMAGE_VERSION"
  DOCKER_JAEGER_ENVFILE="$PWD/.env_jaeger"
  if [[ -f ${DOCKER_JAEGER_ENVFILE} ]]; then
    docker run -d --network host --env-file $DOCKER_JAEGER_ENVFILE -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 --name JAEGER "$DOCKER_JAEGER_FULL_IMAGE"
  else
    echo "O arquivo .env_jaeger não foi encontrado. Subindo modo default..."
    docker run -d --network host -p 5775:5775/udp -p 6831:6831/udp -p 6832:6832/udp -p 5778:5778 -p 16686:16686 -p 14268:14268 -p 9411:9411 --name JAEGER "$DOCKER_JAEGER_FULL_IMAGE"
  fi
  DOCKER_JAEGER_RUNNING=$(docker ps -a | grep JAEGER | grep Up | wc -l)
  if [[ ${DOCKER_JAEGER_RUNNING} -ge 1 ]]; then
    trap 'docker stop JAEGER && docker rm JAEGER 1>/dev/null && exit 1' SIGINT; (docker logs --follow JAEGER & wait $!)
  fi
}

# Verifica as configurações, se possui java na versao correta e configura o settings.xml
function verificaConfig() {
  printf '\n'
  printf '=========================== VERIFICANDO CONFIGURAÇÃO DO SISTEMA ===========================\n'

  baixarDevJavaConfig $2

  JAVA_VERSAO=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | sed '/^1\./s///' | cut -d'.' -f1)

  if [[ ${JAVA_VERSAO} -le 10 ]]; then
    textoErro "Não foi encontrado Java configurado na sua máquina ou a versão do JAVA_HOME é anterior à 11. Configure o Java 11 ou superior e tente novamente."
    exit 1
  fi

  printf "Foi encontrado a versão '${JAVA_VERSAO}' do java em seu sistema.\n"
  OPTION_PROXY_JAVA=""
  if [[ -z "$PROXY_EMPRESA" ]]; then
    read -r -p "Endereço do proxy da empresa no formato 'http://<ip>:<porta>'(deixe em branco se não usar VPN):" PROXY_EMPRESA
  fi
  if [[ ! -z "$PROXY_EMPRESA" ]]; then
    PROXY_JAVA_PROTOCOL=$(echo "$PROXY_EMPRESA" | grep "://" | sed -e's,^\(.*://\).*,\1,g')
    PROXY_JAVA_URL=$(echo "${PROXY_EMPRESA/$PROXY_JAVA_PROTOCOL/}")
    PROXY_JAVA_PROTOCOL=$(echo "$PROXY_JAVA_PROTOCOL" | tr '[:upper:]' '[:lower:]')
    PROXY_JAVA_HOSTPORT=$(echo "${PROXY_JAVA_URL}/" | cut -d"/" -f1)
    PROXY_JAVA_HOST=$(echo "$PROXY_JAVA_HOSTPORT" | cut -d":" -f1)
    PROXY_JAVA_PORT=$(echo "$PROXY_JAVA_HOSTPORT" | grep ":" | cut -d":" -f2)
    OPTION_PROXY_JAVA="-Dhttps.proxyHost=$PROXY_JAVA_HOST -Dhttps.proxyPort=$PROXY_JAVA_PORT -Dhttp.proxyHost=$PROXY_JAVA_HOST -Dhttp.proxyPort=$PROXY_JAVA_PORT -Dhttp.nonProxyHosts=localhost"
  fi
  if [[ $1 == "true" ]]; then
    printf "Informe a sua senha de sudo da maquina:\n"
    sudo java -Duser.home=$HOME $OPTION_PROXY_JAVA -jar "$PWD"/run/dev-java-config.jar
    STATUS_JAVA_CONFIG="${?}"
  else
    ARQUIVO_JAVA_CONFIG="$PWD"/run/dev-java-config.jar
	if [[ -f $ARQUIVO_JAVA_CONFIG ]]; then
		java  $OPTION_PROXY_JAVA -jar $ARQUIVO_JAVA_CONFIG
		STATUS_JAVA_CONFIG="${?}"
	else
	 textoErro "Não foi encontrado o arquivo ./run/dev-java-config.jar. Baixe o arquivo de $DEV_JAVA_CONFIG_URL."
	fi
  fi

  if [[ ${STATUS_JAVA_CONFIG} != 0 ]]; then
    textoErro "Não foi possivel executar a configuração do settings do maven e ou certificados do cacerts do java \n"
    exit 1
  fi
}

# Executa a aplicação usando o docker compose usando o arquivo docker-compose.yaml
function executaDockerCompose() {
  printf "\n"
  printf '============================= MODO DOCKER COMPOSE SELECIONADO ==============================\n'
  printf "\n"
  printf '============================= REALIZANDO O BUILD DA APLICAÇÃO ==============================\n'
  printf "\n"
  ./mvnw clean install
  printf '=============================== INICIANDO O DOCKER COMPOSE ================================\n'
  if [[ $1 == "true" ]]; then
     export DB_USER_PES=$USER_DB2
     export DB_PASSWORD_PES=$PASSWORD_DB2
  fi

  NOME_CONTAINER=$(awk '/container_name:/ {print $2; exit}' "$PWD"/run/docker-compose.yaml | tr -d '[:space:]')
  CONTAINERS_RUNNING=$(docker-compose -f "$PWD"/run/docker-compose.yaml ps 2</dev/null | grep -q 'running'; echo $?)
  CONTAINER_EXISTS=$(docker-compose -f "$PWD"/run/docker-compose.yaml ps 2</dev/null | grep -q "$NOME_CONTAINER"; echo $?)

  # Verifica se há containers em execução
  if [ "$CONTAINERS_RUNNING" -eq 0 ]; then
      # Verifica se o container específico existe
      if [ "$CONTAINER_EXISTS" -eq 0 ]; then
          docker-compose -f "$PWD"/run/docker-compose.yaml stop "$NOME_CONTAINER"
          docker-compose -f "$PWD"/run/docker-compose.yaml down --rmi all
      fi
  fi
  
  docker-compose -f "$PWD"/run/docker-compose.yaml up --build --force-recreate
  
  if [[ $1 == "true" ]]; then
    unset DB_USER_PES
    unset DB_PASSWORD_PES
  fi

}

# desligar o Docker compose
function executaDockerComposeDown() {
  printf "\n"
  printf '====================== EXECUTANDO O DOCKER COMPOSE DOWN APOS CTRL+C =======================\n'
  docker-compose -f "$PWD"/run/docker-compose.yaml down
  exit
}

# Executa a aplicação no MODO local executando o quarkus no MODO dev
function executaModoLocal() {
  printf "\n"
  printf '================================= MODO LOCAL SELECIONADO ==================================\n'
  printf '============================ EXECUTANDO APLICACAO NO MODO DEV =============================\n'
  MAVEN_PROPERTIES_FILE="$PWD/.mvn/wrapper/maven-wrapper.properties"
  if [[ ! -f  ${MAVEN_PROPERTIES_FILE} ]]; then
    baixarDevMvnProperties $DEV_MAVEN_WRAPPER_PROPERTIES_URL
  fi

  if [[ $1 == "true" ]]; then
    COMMAND="./mvnw compile quarkus:dev -DQUARKUS_DATASOURCE_USERNAME=$USER_DB2 -DQUARKUS_DATASOURCE_PASSWORD=$PASSWORD_DB2 -Duser.language=pt -Duser.region=BR"
  else
    COMMAND="./mvnw compile quarkus:dev -Duser.language=pt -Duser.region=BR"
  fi
  DOCKER_CURIO_RUNNING=$(docker ps -a | grep CURIO | grep Up | wc -l)
  if [[ ${DOCKER_CURIO_RUNNING} -ge 1 ]]; then
    (trap 'kill 0' SIGINT; docker logs --follow CURIO | grep ERROR & eval "$COMMAND")
  else
    (trap 'kill 0' SIGINT; docker ps | grep ERROR & eval "$COMMAND")
  fi
}

function solicitarUsuarioPessoalDb2(){
  echo "Informe sua matricula para acesso ao DB2"
  read -r -p "Matricula: " USER_DB2
  # mudar para minusculo
  if [[ -z "$USER_DB2" ]]; then
    textoErro "Por favor informe a chave do usuário em minúsculo (F/C/E/T/Z) "
    exit 1
  fi
  if [[ "${USER_DB2}" -gt 8 ]]; then
    textoErro "Por favor informe a matricula com até 8 caracteres."
    exit 1
  fi
  read -r -sp "Senha do SisBB: " PASSWORD_DB2
  if [[ -z "$PASSWORD_DB2" ]]; then
    textoErro "Por favor informe a senha."
    exit 1
  fi
  echo
}

function solicitaCredenciais() {
  echo "Informe sua matrícula de usuário:"
  read -r -p "Chave: " USER_BINARIOS
  if [[ "${USER_BINARIOS}" -gt 8 ]]; then
    textoErro "Por favor informe a matricula com até 8 caracteres."
    exit 1
  fi
  read -r -sp "Senha do SisBB: " PASSWORD_BINARIOS
  if [[ -z "$PASSWORD_BINARIOS" ]]; then
    textoErro "Por favor informe a senha."
    exit 1
  fi
  echo -e ''
  if [[ "${USER_BINARIOS}" =~ ^[c,C] ]]; then
    read -r -p "Endereço do proxy da empresa no formato 'http://<ip>:<porta>':" PROXY_EMPRESA
    OPTION_PROXY=" -x $PROXY_EMPRESA"
  fi
  echo
}

function baixarDevJavaConfig() {
  if [ -r "$PWD/run/dev-java-config.jar" ]; then
     echo "Encontrou o arquivo dev-java-config.jar."
  else
    echo "Não encontrou o arquivo dev-java-config.jar, iniciando o download de " $1
    LINK=$1
    solicitaCredenciais
    set +e
    CODE=$(curl -w %{http_code} ${OPTION_PROXY} -u ${USER_BINARIOS}:${PASSWORD_BINARIOS} -k "${LINK}" -o ./run/dev-java-config.jar)
    ERRO=$?
    set -e
    if [[ "$CODE" =~ ^2 ]]; then
      echo "Baixou o dev-java-config.jar com sucesso"
    else
      textoErro "Não foi possivel baixar o dev-java-config.jar, retornou status code: $CODE. \n"
      textoErro "O comando curl retornou o erro: $ERRO . Erros do curl: https://curl.se/libcurl/c/libcurl-errors.html \n"
      textoErro "Tente baixar manualmente do endereço $1 e coloque dentro da pasta run do projeto. \n"
      rm -rf ./run/dev-java-config.jar
      exit 1
    fi
  fi
}

function baixarDevMvnProperties() {
  if [ -r "$PWD/.mvn/wrapper/maven-wrapper.properties" ]; then
     echo "Encontrou o arquivo maven-wrapper.properties"
  else
    echo "Não encontrou o arquivo maven-wrapper.properties, iniciando o download de " $1
    mkdir -p "$PWD/.mvn"
    mkdir -p "$PWD/.mvn/wrapper"
    LINK=$1
    solicitaCredenciais
    set +e
    CODE=$(curl -w %{http_code} ${OPTION_PROXY} -u ${USER_BINARIOS}:${PASSWORD_BINARIOS} -k "${LINK}" -o ./mvn/wrapper/maven-wrapper.zip)
    ERRO=$?
    set -e
    if [[ "$CODE" =~ ^2 ]]; then
      echo "Baixou o maven-wrapper.properties com sucesso"
      unzip ./mvn/wrapper/maven-wrapper.zip
    else
      textoErro "Não foi possivel baixar o maven-wrapper.properties, retornou status code: $CODE. \n"
      textoErro "O comando curl retornou o erro: $ERRO . Erros do curl: https://curl.se/libcurl/c/libcurl-errors.html \n"
      textoErro "Tente baixar manualmente do endereço $1 e coloque dentro da pasta run do projeto. \n"
      rm -rf $PWD/.mvn/wrapper/maven-wrapper.properties
      exit 1
    fi
  fi
}


function removerMavenWrapperCorrompido() {
    WRAPPER_CORROMPIDO=$(find . -name "maven-wrapper.jar" -size -1k | grep /.mvn/wrapper/maven-wrapper.jar | wc -l)
    if [[ ${WRAPPER_CORROMPIDO} -ge 1 ]]; then
      printf "\n"
      printf "Excluindo maven-wrapper corrompido. Possivelmente você não está conseguindo acessar\n"
      printf " o repositório de binários. Realize a cópia manual do link abaixo para a pasta \n"
      printf " .mvn/wrapper/ \n"
      printf "https://binarios.intranet.bb.com.br/artifactory/maven/org/apache/maven/wrapper/maven-wrapper/3.1.0/maven-wrapper-3.1.0.jar \n"
      find . -name "maven-wrapper.jar" -size -1k -delete
      printf "\n"
    fi
}

function converteFormtadoMvnwDOSparaUnix() {
  DOS_FORMAT=$(grep -U $'\015' mvnw  | wc -l)
  if [[ ${DOS_FORMAT} -ge 1 ]]; then
    textoAlerta "Seu arquivo mvnw esta no formato DOS(CRLF), realizando conversão.\n"
    set +e
    vim mvnw -c "set ff=unix" -c ":wq"
    ERRO=$?
    set -e
    if [[ "$ERRO" -gt 0 ]]; then
      textoErro "Erro: não foi possivel converter o mvnw do padrao DOS (CRLF) para o padrao UNIX(LF).\n"
      exit 1
    fi
  fi
}

function textoAlerta() {
  printf "%b" "\e[1;33m"
  printf "$1"
  printf "\e[0m"
}

function textoErro() {
  printf "%b" "\e[1;31m"
  printf "$1"
  printf "\e[0m"
}

function verificarUsoCurio() {
  OP_NO_POM=$(grep -o -i ".operacao</groupId>" pom.xml  | wc -l)
  MD_CURIO=$1
  if [[ ${OP_NO_POM} -ge 1 && ${MD_CURIO} == "false" ]]; then
    textoAlerta "Você possui operações no seu POM e não selecionou a opção para subir o CURIO\n"
    textoAlerta "Caso queria subir com o CURIO execute o run.sh com o paramentro -c \n"
    textoAlerta "Ou use a opção -h para exibir os comando aceitos pelo run.sh.\n"
  fi
}

function helper() {
  printf "===========================================================================================\n"
  printf "======================= HELPER PARA SCRIPT DE EXECUÇÃO DA APLICACAO =======================\n"
  printf "Script de configuração e validação do java e maven para permitir a execução da sua \n"
  printf " aplicação com três possibilidades de execução:\n"
  printf "\n"
  printf "1- Padrão: quando não se informa o modo execução, sua aplicação será executada no modo dev \n"
  printf "   do quarkus usando o comando ./mvnw compile quarkus:dev, permitindo o uso do quarkus-cli.\n"
  printf "   Nesse modo o script ira executar apenas sua aplicação.\n"
  printf "\n"
  printf "2- Com curio: realiza duas ações, a primeira executa o docker-run para subir o curio na   \n"
  printf "   versão 0.7.0, caso queria atualizar substituia todas as ocorrencias dentro do projeto. \n"
  printf "   a segunda é a execução aplicação no modo dev do quarkus usando o comando:              \n"
  printf "   ./mvnw compile quarkus:dev permitindo o uso do quarkus-cli.  \n"
  printf "   Para ver o log do curio, em outro terminal use o comando 'docker logs -f CURIO' assim  \n"
  printf "   pode ver se o CURIO subiu corretamente, outra opção é acessar o localhost:8081/health, \n"
  printf "   isso se seu curio estiver configurado na porta 8081, ou na porta configurada do curio. \n"
  printf "\n"
  printf "3- Com docker-compose: utiliza a configuração localizado em /run/docker-compose.yaml para \n"
  printf "   subir varias imagens docker, com o curio, jaeger e também construir uma imagem da      \n"
  printf "   aplicação, nesse modo o quarkus executa no modo prod, o mesmo usado nos ambientes de   \n"
  printf "   deploy (Desenv, Homologa, Produção) e sem a opção de hot-deploy e sem o quarkus-cli.   \n"
  printf "\n"
  printf "\n"
  printf "*Esse script pode ser atualizado executando o script update-run.sh, ele vai realizar a    \n"
  printf "  substituição do arquivo localizado em /run/run.sh, copiando do endereço do link abaixo  \n"
  printf "  Link: https://binarios.intranet.bb.com.br/artifactory/generic-bb-binarios-dev-local/dev/scripts/run.sh    \n"
  printf "\n"
  printf "Comandos de ajuda: \n"
  printf "\n"
  echo "-h  : Exibe a lista de comandos "
  printf "Comandos de configuração: \n"
  printf "\n"
  echo "-b  : MODO Banco de Dados com usuario SISBB que possua acesso as tabelas, usar apenas se não "
  printf "        possuir um usuario impessoal para acesso ao banco.\n"
  echo "-f  : MODO forçado, ignora a configuração e validação do java e do settings.xml pelo ."
  printf "        dev-java-config.\n"
  echo "-s  : MODO que executa o dev-java-config com permissão de sudo, usar somente quando houver erro"
  printf "        de acesso negado nos certificados do java.\n"
  printf "\n"
  printf "Comandos de execução: \n"
  printf "\n"
  echo "-c  : MODO CURIO, executa o docker run para a imagem do CURIO com as configurações localizadas "
  printf "        no arquivo .env_curio na raiz do projeto. \n"
  printf "        Deve usar esse modo quando sua aplicação possuir integração com o IIB, tanto para \n"
  printf "        consumir ou prover operação. \n"
  echo "-j  : MODO JAEGER, executa o docker run para a imagem do JAEGER com as configurações localizadas "
  printf "        no arquivo .env_jaeger na raiz do projeto. \n"
  printf "        Deve usar esse modo quando precisar fazer testes com tracing da sua aplicação.\n"
  echo "-dc : MODO DOCKER-COMPOSE, executa sua aplicação com o docker-compose, deve ser usado para "
  printf "        testar a imagem docker que vai ser usada em produção, nesse modo ele não permite   \n"
  printf "        a execução do hot-deploy do quarkus, utilize esse modo para validar suas configurações. \n"

  printf "*Obs:Se os dois modos de execução forem informados, apenas o modo CURIO será executado.\n"
  printf "===========================================================================================\n"
  exit 0;
}

export MVNW_REPOURL="https://binarios.intranet.bb.com.br/artifactory/maven"

DEV_JAVA_CONFIG_URL='https://binarios.intranet.bb.com.br/artifactory/generic-bb-binarios-dev-local/dev/dev-java-config/dev-java-config.jar'
DEV_MAVEN_WRAPPER_PROPERTIES_URL="https://binarios.intranet.bb.com.br/artifactory/generic-bb-binarios-dev-local/dev/dev-mvn-wrapper/dev-maven-wrapper-properties.zip"

DOCKER_REGISTRY="docker.binarios.intranet.bb.com.br"

MODO_FORCADO="false"
MODO_SUDO="false"
MODO_CURIO="false"
MODO_JAEGER="false"
MODO_COMPOSE="false"
MODO_BD_IMP="false"
MODO_VERIFICA_CONFIG="false"
EXIBIR_HELPER="false"

while [ $# -gt 0 ]; do
    case $1 in
    -s) MODO_SUDO="true"
      shift
      ;;
    -f) MODO_FORCADO="true"
      shift
      ;;
    -dc) MODO_COMPOSE="true"
      shift
      ;;
    -c) MODO_CURIO="true"
      shift
      ;;
    -j) MODO_JAEGER="true"
      shift
      ;;
    -b) MODO_BD_IMP="true"
      shift
      ;;
    -h) EXIBIR_HELPER="true"
      shift
      ;;
    *) echo "Opção $1 Invalida!"
      shift
      ;;
    esac
done

if [[ ${EXIBIR_HELPER} == "true" ]]; then
  helper
else
  printf "===========================================================================================\n"
  printf "======================== INICIANDO SCRIPT DE EXECUÇÃO DA APLICACAO ========================\n"
  textoAlerta "Para saber sobre as opções de execução, execute script com -h, ex: ./run/run.sh -h \n"
fi

if [[ ${MODO_FORCADO} == "false" ]]; then
  verificaConfig ${MODO_SUDO} ${DEV_JAVA_CONFIG_URL}
fi

# Muda para o arquivo para formato linux LF
converteFormtadoMvnwDOSparaUnix
# Verifica se o wrapper esta ok
removerMavenWrapperCorrompido

if [[ ${MODO_BD_IMP} == "true" ]]; then
  solicitarUsuarioPessoalDb2
fi

if [[ ${MODO_COMPOSE} == "true" && (${MODO_CURIO} == "true" || ${MODO_JAEGER} == "true") ]]; then
  textoAlerta "Não é possível iniciar o modo compose -dc juntamente com outro modo. Para saber sobre as opções de execução, execute script com -h, ex: ./run/run.sh -h \n"
  exit 0		
fi

if [[ ${MODO_COMPOSE} == "true" ]]; then
  trap stopCurio INT
  trap stopJaeger INT
  trap executaDockerComposeDown INT
  executaDockerCompose $MODO_BD_IMP
  executaDockerComposeDown
fi

if [[ ${MODO_COMPOSE} == "false" ]]; then
  if [[ ${MODO_CURIO} == "true" ]]; then
    verificarUsoCurio $MODO_CURIO
    executaModoLocal $MODO_BD_IMP & startCurio
    trap stopCurio INT
  fi
  if [[ ${MODO_JAEGER} == "true" ]]; then
    trap stopJaeger INT
    startJaeger
  fi
  if [[ ${MODO_CURIO} == "false" && ${MODO_JAEGER} == "false" ]]; then
    executaModoLocal $MODO_BD_IMP
  fi
fi																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																																							  
