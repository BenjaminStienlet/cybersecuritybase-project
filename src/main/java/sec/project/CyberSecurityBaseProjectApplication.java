package sec.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class CyberSecurityBaseProjectApplication {

    public static void main(String[] args) throws Throwable {
//        SpringApplication.run(CyberSecurityBaseProjectApplication.class);
        SpringApplication app = new SpringApplication(CyberSecurityBaseProjectApplication.class);

        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.output.ansi.enabled", "ALWAYS");
//        properties.put("logging.level.org.springframework.security", "DEBUG");

        app.setDefaultProperties(properties);
        app.run();
    }
}
