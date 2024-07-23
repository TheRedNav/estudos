package ${groupId}.log;

import  ${groupId}.util.AppConfigProperties;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

@RequestScoped
public class LogBBFactory {

    @Inject
    AppConfigProperties config;

    private LogBB logger;

    @Produces
    public LogBB produceLog(InjectionPoint ip) {
        return produceLog(ip.getMember().getDeclaringClass());
    }

    public LogBB produceLog(Class<?> declaringClass) {
        return produceLog(declaringClass.getName());
    }

    public LogBB produceLog(String className) {
        if(logger == null){
            logger = new LogBB(className, config);
        }
        return logger;
    }

}
