package main.java;

import java.util.Random;

public class GeneticAlgorithm {
    ReadInput ri;
    // VARY FOR BEST RESULTS;
    // 1. Population size
    private int population_size = 50;
    // 2. Number of generations
    private int number_of_generations = 100;
    // 3. Probability of crossover
    private double crossoverProbability = 0.4;
    // 4. Probability of mutation
    private double mutationProbability;
    // 5. Variety
    private double percentageVariety = 0.1;

    public GeneticAlgorithm(ReadInput ri) {
        this.ri = ri;
    }

    private final Random rand = new Random();

    public int[][] geneticAlgorithm() {
        int row_size = (int) ri.data.get("number_of_caches");
        int col_size = (int) ri.data.get("number_of_videos");
        int[][][] solutions = generateInitialSolutions(population_size, row_size, col_size);
        int[][][] new_generation = new int[population_size][row_size][col_size];
        long[] fitness = new long[population_size];
        long[] new_fitness = new long[population_size];

        for (int generations = 0; generations < number_of_generations; generations++) {
            // Fitness
            calculateFitness(solutions, fitness);

            // Selection
            selectFittestSolutions(solutions, fitness, new_generation);

            // Crossover
            crossover(new_generation);

            // Mutation
            mutate(new_generation, row_size, col_size);

            // Update current generation
            calculateFitness(new_generation, new_fitness);
            updateGeneration(solutions, new_generation, fitness, new_fitness);
        }

        // Find and return the fittest solution
        return getFittestSolution(solutions, fitness);
    }

    // Keep 50 fittest solutions from previous generation and new generation
    private void updateGeneration(int[][][] solutions, int[][][] new_generation, long[] fitness, long[] new_fitness) {
        // sort solutions and new_generation using the two fitness arrays
        quickSort(solutions, fitness);
        quickSort(new_generation, new_fitness);
        // first 10% of population will be randomly chosen to allow for variety
        int ten_percent = (int) (population_size * percentageVariety);
        for (int i = 0; i < ten_percent; i++) {
            switch (rand.nextInt(2)) {
                case 0:
                    solutions[i] = solutions[rand.nextInt(population_size)];
                    break;
                case 1:
                    solutions[i] = new_generation[rand.nextInt(population_size)];
                    break;
            }
        }
        int pointer1 = solutions.length - 1;
        int pointer2 = new_generation.length - 1;
        for (int i = ten_percent; i < new_generation.length; i++) {
            solutions[i] = (fitness[pointer1] > new_fitness[pointer2]) ? solutions[pointer1--]
                    : new_generation[pointer2--];
        }
    }

    private int[][][] generateInitialSolutions(int n, int row_size, int col_size) {
        int[][][] solutions = new int[n][row_size][col_size];
        for (int i = 0; i < n; i++) {
            solutions[i] = generateRandomSolution(row_size, col_size);
            if (ri.fitness(solutions[i]) == -1)
                i -= 1;
        }
        return solutions;
    }

    private void calculateFitness(int[][][] solutions, long[] fitness) {
        for (int i = 0; i < solutions.length; i++) {
            fitness[i] = ri.fitness(solutions[i]);
        }
    }

    private void selectFittestSolutions(int[][][] solutions, long[] fitness, int[][][] new_generation) {
        // Tournament selection
        int n_tournament = 4;
        for (int pos = 0; pos < solutions.length; pos++) {
            int[] competitors = new int[n_tournament];
            for (int i = 0; i < n_tournament; i++) {
                competitors[i] = rand.nextInt(solutions.length);
            }
            int fittestIndex = getFittestIndex(competitors, fitness);
            new_generation[pos] = solutions[fittestIndex];
        }
    }

    private int getFittestIndex(int[] competitors, long[] fitness) {
        int fittestIndex = competitors[0];
        for (int competitor : competitors) {
            if (fitness[competitor] > fitness[fittestIndex]) {
                fittestIndex = competitor;
            }
        }
        return fittestIndex;
    }

    private void crossover(int[][][] new_generation) {
        // Single-point crossover
        for (int i = 0; i < new_generation.length - 1; i += 2) {
            int crossoverPoint = rand.nextInt(new_generation[0].length);
            if (rand.nextDouble() < crossoverProbability) {
                for (int j = crossoverPoint; j < new_generation[0].length; j++) {
                    int[] temp = new_generation[i][j];
                    new_generation[i][j] = new_generation[i + 1][j];
                    new_generation[i + 1][j] = temp;
                }
            }
        }

    }

    private void mutate(int[][][] generation, int row_size, int col_size) {
        // Flip mutation
        mutationProbability = 1 / (row_size * col_size);
        for (int i = 0; i < generation.length; i++) {
            for (int j = 0; j < row_size; j++) {
                for (int k = 0; k < col_size; k++) {
                    if (rand.nextDouble() < mutationProbability) {
                        generation[i][j][k] ^= 1;
                    }
                }
            }
        }
    }

    private int[][] generateRandomSolution(int rows, int cols) {
        int[][] generatedSolution = new int[rows][cols];
        for (int i = 0; i < rand.nextInt(rows * cols); i++) {
            int row = rand.nextInt(rows);
            int col = rand.nextInt(cols);
            generatedSolution[row][col] = 1;
        }
        return generatedSolution;
    }

    private int[][] getFittestSolution(int[][][] solutions, long[] fitness) {
        long maxFitness = Integer.MIN_VALUE;
        int maxIndex = 0;
        for (int i = 0; i < fitness.length; i++) {
            if (fitness[i] > maxFitness) {
                maxFitness = fitness[i];
                maxIndex = i;
            }
        }
        return solutions[maxIndex];
    }

    // Modified QuickSort
    private static void quickSort(int[][][] array3D, long[] list) {
        quickSort(array3D, list, 0, list.length - 1);
    }

    private static void quickSort(int[][][] array3D, long[] list, int low, int high) {
        if (low < high) {
            int p = partition(array3D, list, low, high); // return index of pivot after partition (splitting into two
                                                         // parts)
            quickSort(array3D, list, low, p - 1); // repeat with elements up to pivot (excluding pivot)
            quickSort(array3D, list, p + 1, high); // repeat with elements after pivot (excluding pivot)
        }
    }

    private static void swap(int[][][] array3D, long[] list, int i, int j) {
        long temp = list[i];
        list[i] = list[j];
        list[j] = temp;
        int[][] temp3d = array3D[i];
        array3D[i] = array3D[j];
        array3D[j] = temp3d;
    }

    private static int partition(int[][][] array3D, long[] list, int low, int high) {
        long pivot = list[high]; // last number of list
        int i = (low - 1); // index below first number in list

        for (int j = low; j <= high - 1; j++) { // go through every element of list
            if (list[j] < pivot) { // is current number smaller than pivot
                i++; // increase index (never greater than j)
                swap(array3D, list, i, j); // swap current number with some smaller previous
            }
        }
        swap(array3D, list, i + 1, high); // swap pivot into the right place
        return (i + 1); // return the index of where the pivot is
    }
}
