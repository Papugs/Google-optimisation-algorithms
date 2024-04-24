# Google HashCode

## Video Streaming Optimization

This repository contains a solution for optimizing video streaming infrastructure based on the problem statement from the Online Qualification Round of Hash Code 2017.
[Google Hash Code 2017](https://labs.ebury.rocks/wp-content/uploads/2017/03/hashcode2017_streaming_videos.pdf)

## Try it for yourself

1. Simply clone the repository
2. Compile the code using: ```javac -d bin src/main/java/*.java```
3. Finally, Run the program: ```java -cp bin main.java.ReadInput```

## Problem Statement

The task involves optimizing video-serving infrastructure by strategically placing videos in cache servers to minimize the average waiting time for all requests. Given descriptions of cache servers, network endpoints, videos, and predicted requests for individual videos, the goal is to decide which videos to place in which cache servers.

## Input Data

The input data set is provided as a plain text file with the following format:

Number of videos (V)
Number of endpoints (E)
Number of request descriptions (R)
Number of cache servers (C)
Capacity of each cache server in megabytes (X)
Sizes of individual videos in megabytes
Description of each endpoint including latency to the data center, number of connected cache servers, and latencies to each connected cache server
Request descriptions including video ID, endpoint ID, and number of requests
Submissions

The submission format should start with a line containing the number of cache server descriptions (N), followed by N lines describing the videos cached in each cache server. Each cache server description includes the cache server ID and the IDs of the videos stored in that cache server.

## Scoring / Fitness

The score is calculated as the average time saved per request in microseconds. The time saved for each request is the difference between the latency of serving the video from the data center and the latency of serving it from the cache server. The total score is the sum of the time saved for individual request descriptions, divided by the total number of requests, and multiplied by 1000.

## Example

An example input file and submission file are provided for reference.