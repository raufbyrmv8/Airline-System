package az.ingress.flightms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "az.ingress.flightms.config.client")
public class FlightMsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlightMsApplication.class, args);
    }

}
