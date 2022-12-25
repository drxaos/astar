package org.example.datsanta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@SpringBootApplication
public class Worker {
    public static void main(String[] args) {
        SpringApplication.run(Worker.class, args);
    }

    public static record WorkerParams(
            List<Child> nodes,
            int[][] matrix,
            int generationSize,
            int reproductionSize,
            int maxIterations,
            float mutationRate,
            int tournamentSize
    ) {
    }
}

@Controller
class WorkerController {

    private static final Semaphore available = new Semaphore(5, true);


    @ResponseBody
    @PostMapping("/search")
    public static ArrayList<Child> runSearch(
            @RequestBody Worker.WorkerParams params
    ) throws InterruptedException {
        boolean locked = available.tryAcquire(100, TimeUnit.MILLISECONDS);
        if (locked) {
            try {
                System.out.println("available " + available.availablePermits());
                ArrayList<Child> result = GeneticSearch.runSearch(
                        params.nodes(),
                        params.matrix(),
                        params.generationSize(),
                        params.reproductionSize(),
                        params.maxIterations(),
                        params.mutationRate(),
                        params.tournamentSize());
                return result;
            } finally {
                available.release();
                System.out.println("available " + available.availablePermits());
            }
        } else {
            return null;
        }
    }
}
