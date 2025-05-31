package project.doklipnews;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableCaching
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class DoklipnewsApplication {

    public static void main(String[] args) {
        SpringApplication.run(DoklipnewsApplication.class, args);
    }

}
