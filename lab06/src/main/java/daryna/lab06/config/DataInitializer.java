package daryna.lab06.config;

import daryna.lab06.service.CounterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CounterService counterService;

    @Override
    public void run(String... args)  {
        for (long i = 1; i <= 4; i++) {
            counterService.initCounterIfNotExists(i);
        }
    }
}