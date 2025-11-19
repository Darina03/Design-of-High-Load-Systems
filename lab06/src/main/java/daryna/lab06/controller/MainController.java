package daryna.lab06.controller;

import daryna.lab06.ClientTest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MainController {


    @GetMapping("/runHazelcast")
    public Map<String, Long> runHazelcastTest() throws InterruptedException {
        Map<String, Long> results = new HashMap<>();

        int[] clients = {1, 2, 5, 10};

        for (int i = 0; i < clients.length; i++) {
            long counterId = i + 1;
            ClientTest client = new ClientTest(
                    "http://localhost:8080/hazelcast/inc/" + counterId,
                    "http://localhost:8080/hazelcast/count/" + counterId,
                    "http://localhost:8080/hazelcast/reset/" + counterId
            );

            System.out.printf("--- Test with %d client(s) on Counter ID %d ---%n", clients[i], counterId);
            results.put(clients[i] + " client(s)", client.run(clients[i], 10_000));
        }

        return results;
    }
}

