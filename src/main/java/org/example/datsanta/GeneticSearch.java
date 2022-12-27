package org.example.datsanta;

import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GeneticSearch {
    @Setter
    private int generationSize;
    private int genomeSize;
    private int numberOfCities;
    @Setter
    private int reproductionSize;
    @Setter
    private int maxIterations;
    @Setter
    private float mutationRate;
    @Setter
    private int tournamentSize;
    private GeneticSelectionType selectionType;
    private int[][] travelPrices;
    private int startingCity;
    private int targetFitness;

    Random randomSeed = new Random();

    public GeneticSearch(int numberOfCities, GeneticSelectionType selectionType, int[][] travelPrices, int startingCity, int targetFitness) {
        this.numberOfCities = numberOfCities;
        this.genomeSize = numberOfCities - 1;
        this.selectionType = selectionType;
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.targetFitness = targetFitness;

        generationSize = 5000;
        reproductionSize = 200;
        maxIterations = 2000;
        mutationRate = 0.2f;
        tournamentSize = 40;
    }

    public List<GeneticGenome> initialPopulation() {
        List<GeneticGenome> population = new ArrayList<>();
        for (int i = 0; i < generationSize; i++) {
            population.add(new GeneticGenome(numberOfCities, travelPrices, startingCity));
        }
        return population;
    }

    public List<GeneticGenome> selection(List<GeneticGenome> population) {
        List<GeneticGenome> selected = new ArrayList<>();
        GeneticGenome winner;
        for (int i = 0; i < reproductionSize; i++) {
            if (selectionType == GeneticSelectionType.ROULETTE) {
                selected.add(rouletteSelection(population));
            } else if (selectionType == GeneticSelectionType.TOURNAMENT) {
                selected.add(tournamentSelection(population));
            }
        }

        return selected;
    }

    public GeneticGenome rouletteSelection(List<GeneticGenome> population) {
        int totalFitness = population.stream().map(GeneticGenome::getFitness).mapToInt(Integer::intValue).sum();
        Random random = new Random(randomSeed.nextInt());
        int selectedValue = random.nextInt(totalFitness);
        float recValue = (float) 1 / selectedValue;
        float currentSum = 0;
        for (GeneticGenome genome : population) {
            currentSum += (float) 1 / genome.getFitness();
            if (currentSum >= recValue) {
                return genome;
            }
        }
        int selectRandom = random.nextInt(generationSize);
        return population.get(selectRandom);
    }

    public <E> List<E> pickNRandomElements(List<E> list, int n) {
        Random r = new Random(randomSeed.nextInt());
        int length = list.size();

        if (length < n) return null;

        for (int i = length - 1; i >= length - n; --i) {
            Collections.swap(list, i, r.nextInt(i + 1));
        }
        return list.subList(length - n, length);
    }

    public GeneticGenome tournamentSelection(List<GeneticGenome> population) {
        List<GeneticGenome> selected = pickNRandomElements(population, tournamentSize);
        return Collections.min(selected);
    }

    public GeneticGenome mutate(GeneticGenome salesman) {
        Random random = new Random(randomSeed.nextInt());
        float mutate = random.nextFloat();
        if (mutate < mutationRate) {
            List<Integer> genome = salesman.getGenome();
            Collections.swap(genome, random.nextInt(genomeSize), random.nextInt(genomeSize));
            return new GeneticGenome(genome, numberOfCities, travelPrices, startingCity);
        }
        return salesman;
    }

    public List<GeneticGenome> createGeneration(List<GeneticGenome> population) {
        List<GeneticGenome> generation = new ArrayList<>();
        int currentGenerationSize = 0;
        while (currentGenerationSize < generationSize) {
            List<GeneticGenome> parents = pickNRandomElements(population, 2);
            List<GeneticGenome> children = crossover(parents);
            children.set(0, mutate(children.get(0)));
            children.set(1, mutate(children.get(1)));
            generation.addAll(children);
            currentGenerationSize += 2;
        }
        return generation;
    }

    public List<GeneticGenome> crossover(List<GeneticGenome> parents) {
        // housekeeping
        Random random = new Random(randomSeed.nextInt());
        int breakpoint = random.nextInt(genomeSize);
        List<GeneticGenome> children = new ArrayList<>();

        // copy parental genomes - we copy so we wouldn't modify in case they were
        // chosen to participate in crossover multiple times
        List<Integer> parent1Genome = new ArrayList<>(parents.get(0).getGenome());
        List<Integer> parent2Genome = new ArrayList<>(parents.get(1).getGenome());

        // creating child 1
        for (int i = 0; i < breakpoint; i++) {
            int newVal;
            newVal = parent2Genome.get(i);
            Collections.swap(parent1Genome, parent1Genome.indexOf(newVal), i);
        }
        children.add(new GeneticGenome(parent1Genome, numberOfCities, travelPrices, startingCity));
        parent1Genome = parents.get(0).getGenome(); // reseting the edited parent

        // creating child 2
        for (int i = breakpoint; i < genomeSize; i++) {
            int newVal = parent1Genome.get(i);
            Collections.swap(parent2Genome, parent2Genome.indexOf(newVal), i);
        }
        children.add(new GeneticGenome(parent2Genome, numberOfCities, travelPrices, startingCity));

        return children;
    }

    public GeneticGenome optimize() {
        List<GeneticGenome> population = initialPopulation();
        GeneticGenome globalBestGenome = population.get(0);
        long start = System.currentTimeMillis();
        for (int i = 0; i < maxIterations && System.currentTimeMillis() - start < 60000; i++) {
            List<GeneticGenome> selected = selection(population);
            population = createGeneration(selected);
            globalBestGenome = Collections.min(population);
            if (globalBestGenome.getFitness() < targetFitness)
                break;
        }
        return globalBestGenome;
    }

    public void printGeneration(List<GeneticGenome> generation) {
        for (GeneticGenome genome : generation) {
            System.out.println(genome);
        }
    }


    public static void printTravelPrices(int[][] travelPrices, int numberOfCities) {
        for (int i = 0; i < numberOfCities; i++) {
            for (int j = 0; j < numberOfCities; j++) {
                System.out.print(travelPrices[i][j]);
                if (travelPrices[i][j] / 10 == 0)
                    System.out.print("  ");
                else
                    System.out.print(' ');
            }
            System.out.println("");
        }
    }

    public static ArrayList<Child> runSearch(
            List<Child> nodes,
            int[][] matrix,
            int generationSize,
            int reproductionSize,
            int maxIterations,
            float mutationRate,
            int tournamentSize
    ) {
        int numberOfCities = nodes.size();
        int[][] travelPrices = matrix;

        //printTravelPrices(travelPrices, numberOfCities);

        GeneticSearch geneticAlgorithm = new GeneticSearch(numberOfCities, GeneticSelectionType.TOURNAMENT, travelPrices, 0, 0);
        geneticAlgorithm.setGenerationSize(generationSize);
        geneticAlgorithm.setReproductionSize(reproductionSize);
        geneticAlgorithm.setMaxIterations(maxIterations);
        geneticAlgorithm.setMutationRate(mutationRate);
        geneticAlgorithm.setTournamentSize(tournamentSize);
        GeneticGenome result = geneticAlgorithm.optimize();
        //System.out.println(result);

//        GeneticSearch geneticAlgorithm2 = new GeneticSearch(numberOfCities, GeneticSelectionType.TOURNAMENT, travelPrices, 0, 0);
//        GeneticGenome result2 = geneticAlgorithm2.optimize();
//        System.out.println(result2);

//        List<Integer> genome = result.fitness < result2.fitness ? result.genome : result2.genome;
        List<Integer> genome = result.genome;

        ArrayList<Child> path = new ArrayList<>();
        path.add(nodes.get(0));
        genome.forEach(n -> path.add(nodes.get(n)));
        path.add(nodes.get(0));

        return path;
    }
}
