package org.example.datsanta.part3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class Part3Main {

    String s = """
        {
            gifts: [
                {
                    id,
                    price,
                    volume,
                    weight,
                    cost
                }
            ],
            childs:[
                {
                    id,
                    age,
                    gender
                }
            ]
        }
        """;

    public static void main(String[] args) {
        List<Item> items = generateItems(10000);
        List<Child> childs = generateChilds(40);

        List<GiftToChild> result = new CollectorGA().collect(items, childs);


        final CollectorGA.Collider demo = new CollectorGA.Collider(items, childs);
        CollectorGA.Population population = demo.population;

        //Initialize population
        population.initializePopulation(childs, items);

        //Calculate fitness of each individual
        population.calculateFitness();

        System.out.println("Generation: " + demo.generationCount + " Fittest: " + population.fittest);

        //While population gets an individual with maximum fitness
        while (demo.population.fittest < 265) {
            ++demo.generationCount;

            //Do selection
            demo.selection();

            //Do crossover
            demo.crossover();

            //Do mutation under a random probability
            if (generateRandomInt(0, Integer.MAX_VALUE - 1) % 7 < 5) {
                demo.mutation();
            }

            //Add fittest offspring to population
            demo.addFittestOffspring();

            //Calculate new fitness value
            population.calculateFitness();

            System.out.println("Generation: " + demo.generationCount + " Fittest: " + demo.population.fittest);
        }

        System.out.println("\nSolution found in generation " + demo.generationCount);
        System.out.println("Fitness: " + demo.population.getFittest().fitness);
        System.out.print("Genes: ");
        final LinkedHashMap<Child, Item> genes = demo.population.getFittest().genes;
        int totalV = 0;
        int totalW = 0;
        int totalFulled = 0;
        for (Map.Entry<Child, Item> entry : genes.entrySet()) {
            totalV += entry.getValue().getVolume();
            totalW += entry.getValue().getWeight();
            if (entry.getValue().getId() >= 0) {
                totalFulled++;
            }
        }

        System.out.println("total v: [%s] total w: [%s]".formatted(totalV, totalW));
        System.out.println("total fulled [%s]".formatted(totalFulled));
        System.out.println("kill all count: [%s]".formatted(demo.killAllCount));
        System.out.println("");

    }

    private static List<Child> generateChilds(int size) {
        List<Child> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            String gender = generateRandomInt(0, 1) == 0 ? "male" : "female";
            result.add(
                new Child()
                    .setId(i)
                    .setAge(generateRandomInt(0, 10))
                    .setGender(gender)
            );
        }

        return result;
    }

    private static List<Item> generateItems(int size) {
        List<Item> result = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            result.add(
                new Item()
                    .setId(i)
                    .setCost(generateRandomInt(10, 500))
                    .setVolume(generateRandomInt(2, 7))
                    .setWeight(generateRandomInt(4, 12))
                    .setType(generateRandomInt(1, 14))
            );
        }

        return result;
    }

    @Data
    @Accessors(chain = true)
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Item {
        int id;
        int volume;
        int weight;
        int cost;
        int type;
    }

    @Data
    @Accessors(chain = true)
    public static class Child {
        int id;
        int age;
        String gender;
    }

    public static int generateRandomInt(int from, int to) {
        return ThreadLocalRandom.current().nextInt(from, to + 1);
    }
}
