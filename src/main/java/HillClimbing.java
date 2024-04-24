package main.java;

import java.util.Random;

public class HillClimbing {
    ReadInput ri;
    private final Random rand = new Random();
    int solution[][];

    public HillClimbing(ReadInput ri) {
        this.ri = ri;
        int row = (int) ri.data.get("number_of_caches");
        int col = (int) ri.data.get("number_of_videos");
        solution = generateRandomSolution(row, col);
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

    public int[][] hillClimbing() {
        long fitness = ri.fitness(solution);
        long max_fitness = fitness;
        long new_fitness;
        int fit_row = 0, fit_col = 0;
        for (int row = 0; row < solution.length; row++) {
            for (int col = 0; col < solution[0].length; col++) {
                solution[row][col] ^= 1;
                new_fitness = ri.fitness(solution);
                System.out.println(new_fitness);
                if (new_fitness > max_fitness) {
                    max_fitness = new_fitness;
                    fit_row = row;
                    fit_col = col;
                }
                solution[row][col] ^= 1;
            }
        }
        System.out.println(max_fitness);
        if (max_fitness > fitness) {
            solution[fit_row][fit_col] ^= 1;
            hillClimbing();
        }
        return solution;
    }
}
