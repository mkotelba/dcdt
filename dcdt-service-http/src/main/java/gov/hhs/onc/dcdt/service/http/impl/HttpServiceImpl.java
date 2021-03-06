package gov.hhs.onc.dcdt.service.http.impl;

import gov.hhs.onc.dcdt.beans.Phase;
import gov.hhs.onc.dcdt.context.AutoStartup;
import gov.hhs.onc.dcdt.http.HttpTransportProtocol;
import gov.hhs.onc.dcdt.service.ServiceContextConfiguration;
import gov.hhs.onc.dcdt.service.http.HttpService;
import gov.hhs.onc.dcdt.service.http.config.HttpServerConfig;
import gov.hhs.onc.dcdt.service.http.server.HttpServer;
import gov.hhs.onc.dcdt.service.impl.AbstractToolService;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@AutoStartup(false)
@Component("httpServiceImpl")
@Phase(Phase.PHASE_PRECEDENCE_HIGHEST + 2)
@ServiceContextConfiguration({ "spring/spring-service-http.xml", "spring/spring-service-http-*.xml" })
public class HttpServiceImpl extends AbstractToolService<HttpTransportProtocol, HttpServerConfig, HttpServer> implements HttpService {
    public HttpServiceImpl() {
        super(HttpServer.class);
    }

    @Autowired(required = false)
    @Override
    public void setServers(List<HttpServer> servers) {
        super.setServers(servers);
    }

    @Override
    @Resource(name = "taskExecServiceHttp")
    public void setTaskExecutor(ThreadPoolTaskExecutor taskExec) {
        super.setTaskExecutor(taskExec);
    }
}
