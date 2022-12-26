package org.example.datsanta;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class GeneticRequest {

    public static List<String> workers = List.of(
            "http://localhost:8080/search",
            "https://e9f2-178-140-43-166.eu.ngrok.io/search",//комп
            "http://192.168.0.107:8080/search",
            "https://17ca-178-140-43-166.eu.ngrok.io/search",
            "https://6a54-81-94-235-186.eu.ngrok.io/search"
            //"https://e5a1-77-222-98-160.eu.ngrok.io/search"
    );
    static Map<Integer, AtomicLong> lastCall = new ConcurrentHashMap<>();

    public static ArrayList<Child> runSearch(
            List<Child> nodes,
            int[][] matrix,
            int generationSize,
            int reproductionSize,
            int maxIterations,
            float mutationRate,
            int tournamentSize
    ) {
        try {
            int active = DsTest.activeGenetic.incrementAndGet();
            System.out.println("active genetic: " + active);


            Worker.WorkerParams workerParams = new Worker.WorkerParams(
                    nodes, matrix, generationSize, reproductionSize, maxIterations, mutationRate, tournamentSize);
            while (true) {
                ArrayList<String> shuffled = new ArrayList<>(workers);
                Collections.shuffle(shuffled);

                for (int i = 0; i < shuffled.size(); i++) {
                    String worker = shuffled.get(i);

                    AtomicLong atomicLastCall = lastCall.computeIfAbsent(i, (i1) -> new AtomicLong(System.currentTimeMillis()));
                    if (System.currentTimeMillis() - atomicLastCall.get() < 1000) {
                        continue;
                    } else {
                        atomicLastCall.set(System.currentTimeMillis());
                    }

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
                            //System.out.println("got BUSY from " + worker);
                        }
                    } catch (Exception e) {
                        System.out.println("got " + e.getClass() + " from " + worker);
                        Thread.sleep(10000);
                    }
                }

                //System.out.println("no free worker");

                Thread.sleep(1500);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
