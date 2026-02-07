package function;

import com.email.generator.dto.EmailRequest;
import com.email.generator.service.EmailGeneratorService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class EmailGeneratorFunction {

    @Bean
    public Function<EmailRequest, String> generateEmail(
            EmailGeneratorService emailGeneratorService) {

        return emailGeneratorService::generateEmail;
    }
}
