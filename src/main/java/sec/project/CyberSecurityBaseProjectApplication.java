package sec.project;

import org.apache.catalina.Context;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CyberSecurityBaseProjectApplication implements EmbeddedServletContainerCustomizer {

    public static void main(String[] args) throws Throwable {
//        SpringApplication.run(CyberSecurityBaseProjectApplication.class);
        SpringApplication app = new SpringApplication(CyberSecurityBaseProjectApplication.class);

        Map<String, Object> properties = new HashMap<>();
        properties.put("server.session.cookie.http-only", "false");
        properties.put("spring.output.ansi.enabled", "ALWAYS");
//        properties.put("logging.level.org.springframework.security", "DEBUG");

        app.setDefaultProperties(properties);
        app.run();
    }

    public void customize(final ConfigurableEmbeddedServletContainer container) {
        ((TomcatEmbeddedServletContainerFactory) container).addContextCustomizers(new TomcatContextCustomizer() {
            @Override
            public void customize(Context context) {
                context.setUseHttpOnly(false);
            }
        });
    }
}
