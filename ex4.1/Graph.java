package edu.ucalgary.oop;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

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

                    GraphNode node1 = graph.nodes.computeIfAbsent(node1Name, name -> new GraphNode(name));
                    GraphNode node2 = graph.nodes.computeIfAbsent(node2Name, name -> new GraphNode(name));

                    int weight = 1;
                    if (line.contains("weight")) {
                        weight = Integer.parseInt(line.split("weight=")[1].split("\\]")[0].trim());
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
    public static String testGraph() {
        StringBuilder result = new StringBuilder();
        Graph graph = new Graph();

        GraphNode nodeA = graph.addNode("A");
        GraphNode nodeB = graph.addNode("B");
        GraphNode nodeC = graph.addNode("C");

        graph.addEdge(nodeA, nodeB, 5);
        graph.addEdge(nodeB, nodeC, 3);

        result.append("Node A connected to: ").append(nodeDataToString(nodeA.edges.keySet())).append("\n");
        result.append("Node B connected to: ").append(nodeDataToString(nodeB.edges.keySet())).append("\n");
        result.append("Node C connected to: ").append(nodeDataToString(nodeC.edges.keySet())).append("\n");

        graph.removeEdge(nodeA, nodeB);
        result.append("Removed edge between A and B\n");

        result.append("Node A connected to: ").append(nodeDataToString(nodeA.edges.keySet())).append("\n");
        result.append("Node B connected to: ").append(nodeDataToString(nodeB.edges.keySet())).append("\n");
        result.append("Node C connected to: ").append(nodeDataToString(nodeC.edges.keySet())).append("\n");

        graph.removeNode(nodeB);
        result.append("Removed node B\n");

        result.append("Node A connected to: ").append(nodeDataToString(nodeA.edges.keySet())).append("\n");
        result.append("Node C connected to: ").append(nodeDataToString(nodeC.edges.keySet())).append("\n");

        return result.toString();
    }

    public static String nodeDataToString(Set<GraphNode> nodes) {
        return nodes.stream()
                .map(GraphNode::getData)
                .collect(Collectors.joining(", "));
    }

    public static void main(String[] args) {
        String result = testGraph();
        System.out.println(result);
    }
}
