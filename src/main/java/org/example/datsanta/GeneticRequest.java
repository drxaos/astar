package org.example.datsanta;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class GeneticRequest {

    public static List<String> workers = List.of(
            "http://localhost:8080/search",
            "https://727e-178-140-43-166.eu.ngrok.io/search",
            "https://0d8e-81-94-235-186.eu.ngrok.io/search",
            "https://3d37-178-140-43-166.eu.ngrok.io/search",
            "https://e5a1-77-222-98-160.eu.ngrok.io/search"
    );
    static AtomicLong lastCall = new AtomicLong(System.currentTimeMillis());

    public static ArrayList<Child> runSearch(
            List<Child> nodes,
            int[][] matrix,
            int generationSize,
            int reproductionSize,
            int maxIterations,
            float mutationRate,
            int tournamentSize
    ) {
        while (System.currentTimeMillis() - lastCall.get() < 1000) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        lastCall.set(System.currentTimeMillis());

        int active = DsTest.activeGenetic.incrementAndGet();
        System.out.println("active genetic: " + active);


        Worker.WorkerParams workerParams = new Worker.WorkerParams(
                nodes, matrix, generationSize, reproductionSize, maxIterations, mutationRate, tournamentSize);
        while (true) {
            for (String worker : workers) {
                try {
                    HttpHeaders headers = new HttpHeaders();
                    HttpEntity<Worker.WorkerParams> entity = new HttpEntity<>(workerParams, headers);
                    final ResponseEntity<ArrayList<Child>> exchange = new RestTemplate()
                            .exchange(
                                    new URI(worker),
                                    HttpMethod.POST,
                                    entity,
                                    new ParameterizedTypeReference<ArrayList<Child>>() {
                                    }
                            );
                    ArrayList<Child> answer = exchange.getBody();
                    if (answer != null) {
                        System.out.println("got result from " + worker);

                        active = DsTest.activeGenetic.decrementAndGet();
                        System.out.println("active genetic: " + active);

                        return answer;
                    } else {
                        System.out.println("got BUSY from " + worker);
                    }
                } catch (Exception e) {
                    System.out.println("got " + e.getClass() + " from " + worker);
                }
            }

            System.out.println("no free worker");

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
