package ${groupId}.log;

import org.apache.logging.log4j.Level;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.container.*;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Provider
@LogApiBB
@ApplicationScoped
public class LogApiBBFilter implements ContainerRequestFilter,  ContainerResponseFilter {

    @Inject
    private LogBB logger;

    @Override
    public void filter(ContainerRequestContext request) throws IOException {
        Map<String, String> infoLog = new HashMap<>();
        infoLog.put("path", request.getUriInfo().getPath());
        infoLog.put("method", request.getMethod());
        logger.info("Requisição", infoLog);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) throws IOException {
        Map<String, String> infoLog = new HashMap<>();
        infoLog.put("path", request.getUriInfo().getPath());
        infoLog.put("method", request.getMethod());
        infoLog.put("statusCode", String.valueOf(response.getStatus()));
        logger.info("Resposta", infoLog);
    }
}

