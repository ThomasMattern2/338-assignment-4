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

    public List<Graph> slowSP(Graph source, Graph target) {
        Map<Graph, Integer> dist = new HashMap<>();
        Map<Graph, Graph> prev = new HashMap<>();
        Set<Graph> unvisited = new HashSet<Graph>(nodes.values());

        // Initialize distances to infinity and previous nodes to null
        for (Graph node : nodes.values()) {
            dist.put(node, Integer.MAX_VALUE);
            prev.put(node, null);
        }
        dist.put(source, 0);

        while (!unvisited.isEmpty()) {
            // Inefficient node selection: linear search through unvisited nodes
            Graph u = null;
            int minDist = Integer.MAX_VALUE;
            for (Graph node : unvisited) {
                if (dist.get(node) < minDist) {
                    u = node;
                    minDist = dist.get(node);
                }
            }

            unvisited.remove(u);
            for (Map.Entry<Graph, Integer> entry : u.getEdges().entrySet()) {
                Graph v = entry.getKey();
                int weight = entry.getValue();
                int alt = dist.get(u) + weight;
                if (alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                }
            }
        }

        // Construct shortest path
        List<Graph> path = new ArrayList<>();
        Graph node = target;
        while (node != null) {
            path.add(node);
            node = prev.get(node);
        }
        Collections.reverse(path);
        return path;
    }

    public List<Graph> fastSP(Graph source, Graph target) {
        Map<Graph, Integer> dist = new HashMap<>();
        Map<Graph, Graph> prev = new HashMap<>();
        PriorityQueue<Graph> queue = new PriorityQueue<>(Comparator.comparingInt(dist::get));
        Set<Graph> visited = new HashSet<>();

        // Initialize distances to infinity and previous nodes to null
        for (Graph node : nodes.values()) {
            dist.put(node, Integer.MAX_VALUE);
            prev.put(node, null);
        }
        dist.put(source, 0);
        queue.offer(source);

        while (!queue.isEmpty()) {
            Graph u = queue.poll();
            visited.add(u);
            for (Map.Entry<Graph, Integer> entry : u.getEdges().entrySet()) {
                Graph v = entry.getKey();
                int weight = entry.getValue();
                int alt = dist.get(u) + weight;
                if (!visited.contains(v) && alt < dist.get(v)) {
                    dist.put(v, alt);
                    prev.put(v, u);
                    queue.offer(v);
                }
            }
        }

        // Construct shortest path
        List<Graph> path = new ArrayList<>();
        Graph node = target;
        while (node != null) {
            path.add(node);
            node = prev.get(node);
        }
        Collections.reverse(path);
        return path;
    }
}
