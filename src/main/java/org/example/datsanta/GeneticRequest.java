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

public class GeneticRequest {

    public static List<String> workers = List.of(
            "http://localhost:8080/search"
    );

    public static ArrayList<Child> runSearch(
            List<Child> nodes,
            int[][] matrix,
            int generationSize,
            int reproductionSize,
            int maxIterations,
            float mutationRate,
            int tournamentSize
    ) {
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
                        return answer;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
