package main.java;

import java.io.*;
import java.util.*;

public class ReadInput {
    public Map<String, Object> data;

    public ReadInput() {
        data = new HashMap<String, Object>();
    }

    public long fitness(int[][] solution) {
        int cache_capacity = (int) data.get("cache_size");
        int[] video_sizes = (int[]) data.get("video_size_desc");
        int num_caches = solution.length;
        long total_score = 0;
        long total_requests = 0;

        // Check cache capacity
        for (int cache = 0; cache < num_caches; cache++) {
            int assigned_videos_sum = 0;
            for (int file = 0; file < solution[cache].length; file++) {
                if (solution[cache][file] == 1) {
                    assigned_videos_sum += video_sizes[file];
                    if (assigned_videos_sum > cache_capacity)
                        return -1; // Cache overflow
                }
            }
        }

        // Calculate score
        HashMap<String, String> requests = (HashMap<String, String>) data.get("video_ed_request");
        for (Map.Entry<String, String> entry : requests.entrySet()) {
            String[] keyParts = entry.getKey().split(",");
            int video_id = Integer.parseInt(keyParts[0]);
            int endpoint_id = Integer.parseInt(keyParts[1]);
            long number_of_requests = Integer.parseInt(entry.getValue());
            long database_latency = ((List<Integer>) data.get("ep_to_dc_latency")).get(endpoint_id);
            long min_latency = database_latency;

            List<List<Integer>> ep_to_cache_latency = (List<List<Integer>>) data.get("ep_to_cache_latency");
            for (int cache_id = 0; cache_id < num_caches; cache_id++) {
                int ep_cache_latency = ep_to_cache_latency.get(endpoint_id).get(cache_id);
                if (solution[cache_id][video_id] == 1 && ep_cache_latency < min_latency) {
                    min_latency = ep_cache_latency;
                }
            }

            total_score += (database_latency - min_latency) * number_of_requests;
            total_requests += number_of_requests;
        }

        // Calculate fitness score
        if (total_requests == 0) {
            return 0; // Avoid division by zero
        } else {
            double average_latency = (double) total_score / total_requests;
            return (long) average_latency * 1000;
        }
    }

    public void readGoogle(String filename) throws IOException {

        BufferedReader fin = new BufferedReader(new FileReader(filename));

        String system_desc = fin.readLine();
        String[] system_desc_arr = system_desc.split(" ");
        int number_of_videos = Integer.parseInt(system_desc_arr[0]);
        int number_of_endpoints = Integer.parseInt(system_desc_arr[1]);
        int number_of_requests = Integer.parseInt(system_desc_arr[2]);
        int number_of_caches = Integer.parseInt(system_desc_arr[3]);
        int cache_size = Integer.parseInt(system_desc_arr[4]);

        Map<String, String> video_ed_request = new HashMap<String, String>();
        String video_size_desc_str = fin.readLine();
        String[] video_size_desc_arr = video_size_desc_str.split(" ");
        int[] video_size_desc = new int[video_size_desc_arr.length]; ////////////
        for (int i = 0; i < video_size_desc_arr.length; i++) {
            video_size_desc[i] = Integer.parseInt(video_size_desc_arr[i]);
        }

        List<List<Integer>> ed_cache_list = new ArrayList<List<Integer>>();
        List<Integer> ep_to_dc_latency = new ArrayList<Integer>();
        List<List<Integer>> ep_to_cache_latency = new ArrayList<List<Integer>>();
        for (int i = 0; i < number_of_endpoints; i++) {
            ep_to_dc_latency.add(0);
            ep_to_cache_latency.add(new ArrayList<Integer>());

            String[] endpoint_desc_arr = fin.readLine().split(" ");
            int dc_latency = Integer.parseInt(endpoint_desc_arr[0]);
            int number_of_cache_i = Integer.parseInt(endpoint_desc_arr[1]);
            ep_to_dc_latency.set(i, dc_latency);

            for (int j = 0; j < number_of_caches; j++) {
                ep_to_cache_latency.get(i).add(ep_to_dc_latency.get(i) + 1);
            }

            List<Integer> cache_list = new ArrayList<Integer>();
            for (int j = 0; j < number_of_cache_i; j++) {
                String[] cache_desc_arr = fin.readLine().split(" ");
                int cache_id = Integer.parseInt(cache_desc_arr[0]);
                int latency = Integer.parseInt(cache_desc_arr[1]);
                cache_list.add(cache_id);
                ep_to_cache_latency.get(i).set(cache_id, latency);
            }
            ed_cache_list.add(cache_list);
        }

        for (int i = 0; i < number_of_requests; i++) {
            String[] request_desc_arr = fin.readLine().split(" ");
            String video_id = request_desc_arr[0];
            String ed_id = request_desc_arr[1];
            String requests = request_desc_arr[2];
            video_ed_request.put(video_id + "," + ed_id, requests);
        }

        data.put("number_of_videos", number_of_videos);
        data.put("number_of_endpoints", number_of_endpoints);
        data.put("number_of_requests", number_of_requests);
        data.put("number_of_caches", number_of_caches);
        data.put("cache_size", cache_size);
        data.put("video_size_desc", video_size_desc);
        data.put("ep_to_dc_latency", ep_to_dc_latency);
        data.put("ep_to_cache_latency", ep_to_cache_latency);
        data.put("ed_cache_list", ed_cache_list);
        data.put("video_ed_request", video_ed_request);

        fin.close();

    }

    public String toString() {
        String result = "";

        // for each endpoint:
        for (int i = 0; i < (Integer) data.get("number_of_endpoints"); i++) {
            result += "enpoint number " + i + "\n";
            // latendcy to DC
            int latency_dc = ((List<Integer>) data.get("ep_to_dc_latency")).get(i);
            result += "latency to dc " + latency_dc + "\n";
            // for each cache
            for (int j = 0; j < ((List<List<Integer>>) data.get("ep_to_cache_latency")).get(i).size(); j++) {
                int latency_c = ((List<List<Integer>>) data.get("ep_to_cache_latency")).get(i).get(j);
                result += "latency to cache number " + j + " = " + latency_c + "\n";
            }
        }

        return result;
    }

    public static void main(String[] args) throws IOException {
        Scanner input = new Scanner(System.in);
        ReadInput ri = new ReadInput();
        // ask what file to use as input
        System.out.println("What file would you like to use? (example / me_at_the_zoo / trending_today / kittens)");
        String inputFile = input.nextLine();
        ri.readGoogle("input/" + inputFile + ".in");
        // ask what algorithm to run
        System.out.println("What algorithm would you like to run? (Genetic/Hill Climbing)");
        String algorithm = input.nextLine();
        if (algorithm.equalsIgnoreCase("genetic")) {
            GeneticAlgorithm ga = new GeneticAlgorithm(ri);
            int[][] solution = ga.geneticAlgorithm();
            System.out.println("The fitness is: " + ri.fitness(solution));
        } else {
            HillClimbing na = new HillClimbing(ri);
            int[][] s = na.hillClimbing();
            System.out.println("The fitness is: " + ri.fitness(s));
        }
    }
}
