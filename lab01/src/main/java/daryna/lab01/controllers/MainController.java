package daryna.lab01.controllers;

import daryna.lab01.ClientTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {

    @GetMapping("/run")
    public Map<String, Long> run() throws InterruptedException {
        ClientTest client = new ClientTest(
                "http://localhost:8080/inc",
                "http://localhost:8080/count",
                "http://localhost:8080/reset"
        );

        Map<String, Long> results = new HashMap<>();
        results.put("1 client", client.run(1, 10_000));
        results.put("2 clients", client.run(2, 10_000));
        results.put("5 clients", client.run(5, 10_000));
        results.put("10 clients", client.run(10, 10_000));

        return results;
    }

    @GetMapping("/runDB")
    public Map<String, Long> runDB() throws InterruptedException {

        ClientTest client = new ClientTest(
                "http://localhost:8080/db/inc",
                "http://localhost:8080/db/count",
                "http://localhost:8080/db/reset"
        );

        Map<String, Long> results = new HashMap<>();
        results.put("1 client", client.run(1, 10_000));
        results.put("2 clients", client.run(2, 10_000));
        results.put("5 clients", client.run(5, 10_000));
        results.put("10 clients", client.run(10, 10_000));

        return results;
    }




}
