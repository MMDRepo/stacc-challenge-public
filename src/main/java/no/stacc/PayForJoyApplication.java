package no.stacc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@SpringBootApplication
@EnableTransactionManagement
public class PayForJoyApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayForJoyApplication.class, args);
    }
}