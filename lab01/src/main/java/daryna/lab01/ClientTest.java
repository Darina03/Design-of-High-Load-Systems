package daryna.lab01;

import lombok.AllArgsConstructor;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class ClientTest {
    private final String incUrl;
    private final String countUrl;
    private final String resetUrl;


    public long run(int clients, int requestsPerClient) throws InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        try {
            HttpRequest resetRequest = HttpRequest.newBuilder()
                    .uri(URI.create(resetUrl))
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.noBody())
                    .build();

            client.send(resetRequest, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.err.println("Failed to reset counter: " + e.getMessage());
        }

        ExecutorService executor = Executors.newFixedThreadPool(clients);

        long start = System.nanoTime();

        for (int i = 0; i < clients; i++) {
            executor.submit(() -> {
                try {
                    for (int j = 0; j < requestsPerClient; j++) {
                        HttpRequest request = HttpRequest.newBuilder()
                                .uri(URI.create(incUrl))
                                .timeout(Duration.ofSeconds(10))
                                .POST(HttpRequest.BodyPublishers.noBody())
                                .build();
                        client.send(request, HttpResponse.BodyHandlers.ofString());
                    }
                } catch (Exception e) {
                    System.err.println("Request failed: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);

        long end = System.nanoTime();
        double elapsedSec = (end - start) / 1_000_000_000.0;
        int totalRequests = clients * requestsPerClient;
        double throughput = totalRequests / elapsedSec;

        System.out.printf("Clients=%d, Requests=%d, Time=%.2f s, Throughput=%.2f req/s%n",
                clients, totalRequests, elapsedSec, throughput);

        try {
            HttpRequest countRequest = HttpRequest.newBuilder()
                    .uri(URI.create(countUrl))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(countRequest, HttpResponse.BodyHandlers.ofString());
            System.out.printf("Counter value after test: %s%n", response.body());
        } catch (Exception e) {
            System.err.println("Failed to fetch counter: " + e.getMessage());
        }

        return (long) throughput;
    }



}
