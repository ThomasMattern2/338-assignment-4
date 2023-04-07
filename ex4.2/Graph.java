package edu.ucalgary.oop;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.github.sh0nk.matplotlib4j.*;
import com.github.sh0nk.matplotlib4j.builder.*;
import com.github.sh0nk.matplotlib4j.builder.HistBuilder.HistType;
import com.github.sh0nk.matplotlib4j.kwargs.*;

public class Graph {
    private final Map<String, GraphNode> nodes;

    public Graph() {
        nodes = new HashMap<>();
    }

    public GraphNode addNode(String data) {
        GraphNode node = new GraphNode(data);
        nodes.put(data, node);
        return node;
    }

    public void removeNode(GraphNode node) {
        nodes.values().forEach(n -> n.edges.remove(node));
        nodes.remove(node.data);
    }

    public void addEdge(GraphNode n1, GraphNode n2, int weight) {
        n1.edges.put(n2, weight);
        n2.edges.put(n1, weight);
    }

    public void removeEdge(GraphNode n1, GraphNode n2) {
        n1.edges.remove(n2);
        n2.edges.remove(n1);
    }

    public static Graph importFromFile(String fileName) {
        Graph graph = new Graph();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("strict graph")) {
                    continue;
                }
                if (line.contains("--")) {
                    String[] parts = line.split("--");
                    String node1Name = parts[0].trim();
                    String node2Name = parts[1].split("\\[")[0].trim();

                    // Ignore edges with missing nodes
                    if (node1Name.isEmpty() || node2Name.isEmpty()) {
                        continue;
                    }

                    GraphNode node1 = graph.nodes.computeIfAbsent(node1Name, name -> new GraphNode(name));
                    GraphNode node2 = graph.nodes.computeIfAbsent(node2Name, name -> new GraphNode(node2Name));

                    int weight = 1;
                    if (line.contains("weight")) {
                        String[] weightParts = line.split("weight=");
                        if (weightParts.length > 1) {
                            weight = Integer.parseInt(weightParts[1].split("\\]")[0].trim());
                        }
                    }
                    graph.addEdge(node1, node2, weight);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return graph;
    }

    public static class GraphNode {
        private final String data;
        private final Map<GraphNode, Integer> edges;

        public GraphNode(String data) {
            this.data = data;
            this.edges = new HashMap<>();
        }

        public String getData() {
            return data;
        }

        public Map<GraphNode, Integer> getEdges() {
            return edges;
        }
    }

    public Map<GraphNode, Integer> slowSP(GraphNode source) {
        Map<GraphNode, Integer> dist = new HashMap<>();
        PriorityQueue<GraphNode> pq = new PriorityQueue<>(Comparator.comparing(node -> dist.get(node)));

        for (GraphNode node : nodes.values()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        // Initialize distances for all nodes before adding them to the PriorityQueue
        for (GraphNode node : nodes.values()) {
            pq.add(node);
        }

        while (!pq.isEmpty()) {
            GraphNode u = pq.poll();

            for (Map.Entry<GraphNode, Integer> edge : u.edges.entrySet()) {
                GraphNode v = edge.getKey();
                int weight = edge.getValue();

                int alt = dist.get(u) + weight;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        return dist;
    }

    private GraphNode findMinDistNode(Set<GraphNode> nodes, Map<GraphNode, Integer> dist) {
        int minDist = Integer.MAX_VALUE;
        GraphNode minNode = null;

        for (GraphNode node : nodes) {
            if (dist.get(node) < minDist) {
                minDist = dist.get(node);
                minNode = node;
            }
        }
        return minNode;
    }

    public Map<GraphNode, Integer> fastSP(GraphNode source) {
        Map<GraphNode, Integer> dist = new HashMap<>();
        PriorityQueue<GraphNode> pq = new PriorityQueue<>(Comparator.comparing(node -> dist.get(node)));

        for (GraphNode node : nodes.values()) {
            dist.put(node, Integer.MAX_VALUE);
        }
        dist.put(source, 0);

        // Initialize distances for all nodes before adding them to the PriorityQueue
        for (GraphNode node : nodes.values()) {
            pq.add(node);
        }

        while (!pq.isEmpty()) {
            GraphNode u = pq.poll();

            for (Map.Entry<GraphNode, Integer> edge : u.edges.entrySet()) {
                GraphNode v = edge.getKey();
                int weight = edge.getValue();

                int alt = dist.get(u) + weight;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    pq.remove(v);
                    pq.add(v);
                }
            }
        }
        return dist;
    }

    public static void main(String[] args) throws IOException, PythonExecutionException {
        Graph graph = Graph.importFromFile("random.dot");
        GraphNode source = graph.nodes.get("0"); // Assuming "0" is the source node

        if (source == null) {
            System.out.println("Error: Source node 0 not found in the graph");
            return;
        }

        long slowStartTime = System.nanoTime();
        Map<GraphNode, Integer> slowShortestPaths = graph.slowSP(source);
        long slowEndTime = System.nanoTime();
        long slowDuration = slowEndTime - slowStartTime;

        long fastStartTime = System.nanoTime();
        Map<GraphNode, Integer> fastShortestPaths = graph.fastSP(source);
        long fastEndTime = System.nanoTime();
        long fastDuration = fastEndTime - fastStartTime;

        // Print the results
        System.out.println("Slow Dijkstra's shortest paths:");
        slowShortestPaths.forEach((k, v) -> System.out.println(k.getData() + ": " + v));
        System.out.println("Slow Dijkstra's execution time: " + slowDuration + " nanoseconds");

        System.out.println("\nFast Dijkstra's shortest paths:");
        fastShortestPaths.forEach((k, v) -> System.out.println(k.getData() + ": " + v));
        System.out.println("Fast Dijkstra's execution time: " + fastDuration + " nanoseconds");

        long maxTime = Math.max(slowDuration, fastDuration);
        long minTime = Math.min(slowDuration, fastDuration);
        System.out.println("Max execution time: " + maxTime + " nanoseconds");
        System.out.println("Min execution time: " + minTime + " nanoseconds");

        List<Long> slowExecutionTimes = new ArrayList<>();
        List<Long> fastExecutionTimes = new ArrayList<>();

     // Loop through all the nodes in the graph
     for (GraphNode node : graph.nodes.values()) {
         // Run the slow and fast Dijkstra's algorithms for the node
         Map<GraphNode, Integer> slowShortestPathsNode = graph.slowSP(node);
         Map<GraphNode, Integer> fastShortestPathsNode = graph.fastSP(node);

         // Calculate the execution time for the slow and fast algorithms
         long slowTime = System.nanoTime();
         graph.slowSP(node);
         long slowExecutionTime = System.nanoTime() - slowTime;

         long fastTime = System.nanoTime();
         graph.fastSP(node);
         long fastExecutionTime = System.nanoTime() - fastTime;

         // Store the execution times in the list
         slowExecutionTimes.add(slowExecutionTime);
         fastExecutionTimes.add(fastExecutionTime);

     }
        // Create a histogram of the execution times
        Plot plt = Plot.create();
        plt.hist().add(slowExecutionTimes);
        plt.hist().add(fastExecutionTimes);

        plt.xlabel("Execution Time (nanoseconds)");
        plt.ylabel("Frequency");
        plt.title("Distribution of Execution Times");
        plt.show();
    }
}
