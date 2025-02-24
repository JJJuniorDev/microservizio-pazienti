package Main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {
		"Main",
		"Repository",
		"Services",
		"Controller",
		"Model",
		"Helpers"
		})
@EnableMongoRepositories(basePackages = "Repository")
public class PazientiStart {

	public static void main(String[] args) {
		/* System.out.println("Classpath:");
	    for (String path : System.getProperty("java.class.path").split(File.pathSeparator)) {
            System.out.println(path);
        }*/
		SpringApplication.run(PazientiStart.class, args);
	
	    
	}


}
