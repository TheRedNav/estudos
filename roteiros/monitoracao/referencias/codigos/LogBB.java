package ${groupId}.log;

import ${groupId}.util.AppConfigProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LogBB {

    private static final String LOG_IN_JSON = "LOG_IN_JSON";

    private static final String REQUEST_ID = "requestId";
    private static final String MESSAGE = "message";
    private static final String DETAILS = "details";
    public static final String SEPARADOR_IGUAL = "=";
    public static final String SEPARADOR_VIRGULA = ", ";
    private String requestId;
    private Logger LOG;

    private static final String INFO ="Info";
    private static final String DEBUG ="Debug";
    private static final String ERROR ="Error";
    private static final String LOCAL ="Local";
    private static final String[] levels = new String[]{LOCAL, INFO,DEBUG, ERROR,"Trace", "Warn","Fatal"};

    private AppConfigProperties config;

    LogBB (String name, AppConfigProperties config) {
        this.config = config;
        LOG =  LogManager.getLogger(name);
        requestId = UUID.randomUUID().toString();
    }

    public static String transformLogLevelEnvToLogLevel(String levelEnv){
        if(StringUtils.isNotBlank(levelEnv)){
            for(String level : levels){
                if(levelEnv.toLowerCase().contains(level.toLowerCase())){
                    return level;
                }
            }
        }
        return levelEnv;
    }

    public String getRequestId() {
        return requestId;
    }

    public void info(String message){
        this.log(Level.INFO, message, null, null, null);
    }

    public void info(String message, Object... args){
        this.log(Level.INFO, message, null, null, args);
    }

    public void info(String message, Map<String, String> detail){
        this.log(Level.INFO, message, detail, null, null);
    }

    public void info(String message, Map<String, String> detail, Object... args){
        this.log(Level.INFO, message, detail, null, args);
    }

    public void debug(String message){
        this.log(Level.DEBUG, message, null, null, null);
    }

    public void debug(String message, Object... args){
        this.log(Level.DEBUG, message, null, null, args);
    }

    public void debug(String message, Map<String, String> detail){
        this.log(Level.DEBUG, message, detail, null, null);
    }

    public void debug(String message, Map<String, String> detail, Object... args){
        this.log(Level.DEBUG, message, detail, null, args);
    }

    public void error(Exception e, String message, Map<String, String> detail,  Object... args){
        this.log(Level.ERROR, message, detail , e, args);
    }

    public void error(Exception e, String message, Map<String, String> detail){
        this.log(Level.ERROR, message, detail , e, null);
    }

    public void error(Exception e, String message,  Object... args){
        this.log(Level.ERROR, message, null , e, args);
    }

    public void error(Exception e, String message){
        this.log(Level.ERROR, message, null , e, null);
    }

    public void error(Exception e){
        this.log(Level.ERROR, e.getMessage(), null , e, null);
    }

    public void log(Level level, String message){
        this.log(level, message, null, null, null);
    }

    public void log(Level level, String message, Object... args){
        this.log(level, message, null,null,args);
    }

    public void log(Level level, String message, Map<String, String> detail ){
        this.log(level, message, detail, null, null);
    }

    public void log(Level level, String message, Map<String, String> detail, Object... args){
        this.log(level, message, detail , null, args);
    }

    private void log(Level level, String message, Map<String, String> detail, Exception e, Object... args){
        String logContent;

        if(config.getLogLevel() != null && config.getLogLevel().equals(LOCAL)){
            logContent = criarConteudoLogEmTexto(e, detail, message, args);
            LOG.log(level, MarkerManager.getMarker(LOG_IN_JSON), logContent, e);
        } else {
            try {
                logContent = criarConteudoLogEmJson(e, detail, message, args);
                LOG.log(level, logContent);
            } catch (JsonProcessingException ignored) {}
        }
    }

    private String criarConteudoLogEmTexto(Exception e, Map<String, String> details, String message, Object[] args) {
        StringBuilder logMessage = new StringBuilder();

        logMessage.append(REQUEST_ID);
        logMessage.append(SEPARADOR_IGUAL);
        logMessage.append(requestId);
        logMessage.append(SEPARADOR_VIRGULA);

        logMessage.append(MESSAGE);
        logMessage.append(SEPARADOR_IGUAL);

        if(args != null) {
            logMessage.append(String.format(message, args));
        } else {
            logMessage.append(message);
        }

        if(details != null && !details.isEmpty()) {
            logMessage.append(SEPARADOR_VIRGULA);
            int tamanho = details.size();
            int cont = 1;
            for(Map.Entry entry: details.entrySet()){
                logMessage.append(entry.getKey());
                logMessage.append(SEPARADOR_IGUAL);
                logMessage.append(entry.getValue());

                if(cont < tamanho) {
                    logMessage.append(SEPARADOR_VIRGULA);
                }

                cont++;
            }
        }

        return logMessage.toString();
    }

    private String criarConteudoLogEmJson (Exception e,Map<String, String> details, String message, Object... args) throws JsonProcessingException {
        Map<String, Object> detailsLog = new HashMap<>();

        detailsLog.put(REQUEST_ID, requestId);

        if(args != null) {
            detailsLog.put(MESSAGE, String.format(message, args));
        } else {
            detailsLog.put(MESSAGE, message);
        }

        if(details != null && !details.isEmpty()) {
            detailsLog.put(DETAILS, details);
        }

        if(e != null){
            detailsLog.put("stackTrace",Arrays.asList(e.getStackTrace()));
        }

        return new ObjectMapper().writeValueAsString(detailsLog);
    }
}
