package org.example.datsanta.part3;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.example.datsanta.part3.Part3Main.generateRandomInt;

public class CollectorGA {
    public List<GiftToChild> collect(List<Part3Main.Item> items, List<Part3Main.Child> childs) {


        return null;
    }

    public static final class Collider {
        Population population = new Population();
        Individual fittest;
        Individual secondFittest;
        int generationCount = 0;
        public int killAllCount = 0;

        List<Part3Main.Item> items;
        List<Part3Main.Child> childs;

        public Collider(final List<Part3Main.Item> items, final List<Part3Main.Child> childs) {
            this.items = items;
            this.childs = childs;
        }

        //Selection
        public void selection() {

            //Select the most fittest individual
            fittest = population.getFittest();

            //Select the second most fittest individual
            secondFittest = population.getSecondFittest();

            if (fittest.fitness < 0) {
                population.initializePopulation(childs, items);
                killAllCount++;
            }
        }

        //Crossover
        public void crossover() {

            //Select a random crossover point
            int crossOverPoint = generateRandomInt(0, population.individuals[0].geneLength);

            //Swap values among parents
            for (int i = 0; i < crossOverPoint; i++) {

                final Part3Main.Item temp = getValueFromMapById(fittest.genes, i);
                setValueFromMapById(fittest.genes, getValueFromMapById(secondFittest.genes, i), i);
                setValueFromMapById(secondFittest.genes, temp, i);
            }

        }

        private Part3Main.Item getValueFromMapById(LinkedHashMap<Part3Main.Child, Part3Main.Item> map, int i) {
            int j = 0;
            for (Part3Main.Item value : map.values()) {
                if (j == i) {
                    return value;
                }
                j++;
            }
            throw new RuntimeException();
        }

        private void setValueFromMapById(LinkedHashMap<Part3Main.Child, Part3Main.Item> map, Part3Main.Item item, int i) {
            int j = 0;
            for (Map.Entry<Part3Main.Child, Part3Main.Item> childItemEntry : map.entrySet()) {
                if (j == i) {
                    map.put(childItemEntry.getKey(), item);
                    return;
                }
                ;
                j++;
            }


            throw new RuntimeException();
        }

        //Mutation
        public void mutation() {

            //Select a random mutation point
            int mutationPoint = generateRandomInt(0, population.individuals[0].geneLength - 1);
            //Flip values at the mutation point
            setValueFromMapById(fittest.genes, items.get(generateRandomInt(0, items.size() - 1)), mutationPoint);

            mutationPoint = generateRandomInt(0, population.individuals[0].geneLength - 1);
            setValueFromMapById(secondFittest.genes, items.get(generateRandomInt(0, items.size() - 1)), mutationPoint);

        }

        //Get fittest offspring
        public Individual getFittestOffspring() {
            if (fittest.fitness > secondFittest.fitness) {
                return fittest;
            }
            return secondFittest;
        }

        //Replace least fittest individual from most fittest offspring
        public void addFittestOffspring() {

            //Update fitness values of offspring
            fittest.calcFitness();
            secondFittest.calcFitness();

            //Get index of least fit individual
            int leastFittestIndex = population.getLeastFittestIndex();

            //Replace least fittest individual from most fittest offspring
            population.individuals[leastFittestIndex] = getFittestOffspring();
        }
    }

    public static final Part3Main.Item NULL_ITEM = new Part3Main.Item(-1, 0, 0, 0, 0);

    public static final class Individual {

        public int fitness;
        public LinkedHashMap<Part3Main.Child, Part3Main.Item> genes = new LinkedHashMap<>();
        public int geneLength = 40;

        public Individual(List<Part3Main.Child> childs, List<Part3Main.Item> list) {
            //Set genes randomly for each individual
            for (int i = 0; i < geneLength; i++) {

                if (generateRandomInt(1, 100) < 40) {
                    genes.put(childs.get(i), list.get(generateRandomInt(0, list.size() - 1)));
                } else {
                    genes.put(childs.get(i), NULL_ITEM);
                }
            }

            fitness = 0;
        }

        //Calculate fitness
        public void calcFitness() {

            fitness = 0;

            int totalV = 0;
            int totalW = 0;
            int bagFully = 0;
            int totalCost = 0;

            //Сейчас учитываются только размеры, вес и цена предмета
            // max price = 20 000 maxV = 100 maxW = 200
            for (Map.Entry<Part3Main.Child, Part3Main.Item> childItemEntry : genes.entrySet()) {
                final Part3Main.Item item = childItemEntry.getValue();
                totalV += item.getVolume();
                totalW += item.getWeight();
                totalCost += item.getCost();
                if (item.getId() >= 0) {
                    bagFully++;
                }
            }

            double totalVPercent = totalV / (double) 100 * 100;
            double totalWPercent = totalW / (double) 200 * 100;
            double bagFullyPercent = bagFully / (double) 40 * 100;

            if (totalVPercent <= 100) {
                fitness = (int) totalVPercent;
            } else {
                fitness = fitness - 50;
            }

            if (totalWPercent <= 100) {
                fitness += (int) totalWPercent;
            } else {
                fitness = fitness - 50;
            }
            if (bagFullyPercent <= 100) {
                fitness += (int) bagFullyPercent;
            } else {
                fitness=fitness - 50;
            }
//            System.out.println(fitness);
        }

    }

    public static final class Population {

        public int popSize = 100;
        public Individual[] individuals = new Individual[100];
        public int fittest = 0;

        //Initialize population
        public void initializePopulation(List<Part3Main.Child> childs, List<Part3Main.Item> list) {
            for (int i = 0; i < individuals.length; i++) {
                individuals[i] = new Individual(childs, list);
            }
        }

        //Get the fittest individual
        public Individual getFittest() {
            int maxFit = Integer.MIN_VALUE;
            int maxFitIndex = 0;
            for (int i = 0; i < individuals.length; i++) {
                if (maxFit <= individuals[i].fitness) {
                    maxFit = individuals[i].fitness;
                    maxFitIndex = i;
                }
            }
            fittest = individuals[maxFitIndex].fitness;
            return individuals[maxFitIndex];
        }

        //Get the second most fittest individual
        public Individual getSecondFittest() {
            int maxFit1 = 0;
            int maxFit2 = 0;
            for (int i = 0; i < individuals.length; i++) {
                if (individuals[i].fitness > individuals[maxFit1].fitness) {
                    maxFit2 = maxFit1;
                    maxFit1 = i;
                } else if (individuals[i].fitness > individuals[maxFit2].fitness) {
                    maxFit2 = i;
                }
            }
            return individuals[maxFit2];
        }

        //Get index of least fittest individual
        public int getLeastFittestIndex() {
            int minFitVal = Integer.MAX_VALUE;
            int minFitIndex = 0;
            for (int i = 0; i < individuals.length; i++) {
                if (minFitVal >= individuals[i].fitness) {
                    minFitVal = individuals[i].fitness;
                    minFitIndex = i;
                }
            }
            return minFitIndex;
        }

        //Calculate fitness of each individual
        public void calculateFitness() {

            for (int i = 0; i < individuals.length; i++) {
                individuals[i].calcFitness();
            }
            getFittest();
        }
    }
}
