package org.example.datsanta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class GeneticGenome implements Comparable {
    List<Integer> genome;
    int[][] travelPrices;
    int startingCity;
    int numberOfCities = 0;
    int fitness;

    public GeneticGenome(int numberOfCities, int[][] travelPrices, int startingCity){
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        genome = randomSalesman();
        fitness = this.calculateFitness();
    }

    public GeneticGenome(List<Integer> permutationOfCities, int numberOfCities, int[][] travelPrices, int startingCity){
        genome = permutationOfCities;
        this.travelPrices = travelPrices;
        this.startingCity = startingCity;
        this.numberOfCities = numberOfCities;
        fitness = this.calculateFitness();
    }

    public int calculateFitness(){
        int fitness = 0;
        int currentCity = startingCity;
        for ( int gene : genome) {
            fitness += travelPrices[currentCity][gene];
            currentCity = gene;
        }
        fitness += travelPrices[genome.get(numberOfCities-2)][startingCity];
        return fitness;
    }

    private List<Integer> randomSalesman(){
        List<Integer> result = new ArrayList<Integer>();
        for(int i=0; i<numberOfCities; i++) {
            if(i!=startingCity)
                result.add(i);
        }
        Collections.shuffle(result);
        return result;
    }

    public List<Integer> getGenome() {
        return genome;
    }

    public int getStartingCity() {
        return startingCity;
    }

    public int getFitness() {
        return fitness;
    }

    public void setFitness(int fitness) {
        this.fitness = fitness;
    }

    @Override
    public int hashCode() {
        return genome.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Path: ");
        sb.append(startingCity);
        for ( int gene: genome ) {
            sb.append(" ");
            sb.append(gene);
        }
        sb.append(" ");
        sb.append(startingCity);
        sb.append("\nLength: ");
        sb.append(this.fitness);
        return sb.toString();
    }


    @Override
    public int compareTo(Object o) {
        GeneticGenome genome = (GeneticGenome) o;
        if(this.fitness > genome.getFitness())
            return 1;
        else if(this.fitness < genome.getFitness())
            return -1;
        else
            return 0;
    }
}
