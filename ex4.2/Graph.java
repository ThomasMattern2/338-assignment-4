public class Graph {
    // ...

    // Method with inefficient node selection logic
    public List<GraphNode> slowSP(GraphNode g) {
        Map<GraphNode, Integer> distances = new HashMap<>();
        Map<GraphNode, GraphNode> parents = new HashMap<>();
        Set<GraphNode> visited = new HashSet<>();
        
        // Initialize distances to infinity for all nodes
        for (GraphNode node : nodes.values()) {
            distances.put(node, Integer.MAX_VALUE);
        }
        distances.put(g, 0);  // Distance from source node to itself is 0
        
        while (visited.size() < nodes.size()) {
            // Find the node with the minimum distance from the source node
            GraphNode current = null;
            int minDist = Integer.MAX_VALUE;
            for (GraphNode node : nodes.values()) {
                if (!visited.contains(node) && distances.get(node) < minDist) {
                    current = node;
                    minDist = distances.get(node);
                }
            }
            
            if (current == null) {
                break;  // All remaining nodes are unreachable
            }
            
            visited.add(current);
            
            // Update distances and parents for adjacent nodes
            for (Map.Entry<GraphNode, Integer> entry : current.edges.entrySet()) {
                GraphNode neighbor = entry.getKey();
                int weight = entry.getValue();
                int newDist = distances.get(current) + weight;
                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    parents.put(neighbor, current);
                }
            }
        }
        
        // Build and return the path from the source node to each node
        List<GraphNode> path = new ArrayList<>();
        for (GraphNode node : nodes.values()) {
            if (node == g) {
                continue;
            }
            GraphNode current = node;
            path.clear();
            while (current != null) {
                path.add(0, current);
                current = parents.get(current);
            }
            if (path.get(0) != g) {
                // Node is unreachable
                path.clear();
            }
            node.edges.clear();  // Remove the weights from the edges
            node.edges.putAll(parents.get(node).edges);  // Replace with the actual edges
            node.edges.put(node, distances.get(node));  // Add the distance from the source node
            node.edges.keySet().retainAll(path);  // Keep only the edges on the path
        }
        return new ArrayList<>(nodes.values());
    }

    public List<GraphNode> slowSP(GraphNode g) {
        List<GraphNode> path = new ArrayList<>();
        path.add(g);
        while (true) {
            GraphNode current = path.get(path.size() - 1);
            int smallestWeight = Integer.MAX_VALUE;
            GraphNode nextNode = null;
            for (Map.Entry<GraphNode, Integer> entry : current.edges.entrySet()) {
                if (entry.getValue() < smallestWeight && !path.contains(entry.getKey())) {
                    smallestWeight = entry.getValue();
                    nextNode = entry.getKey();
                }
            }
            if (nextNode == null) {
                break;
            }
            path.add(nextNode);
        }
        return path;
    }
    
    public List<GraphNode> fastSP(GraphNode g) {
        List<GraphNode> path = new ArrayList<>();
        path.add(g);
        while (true) {
            GraphNode current = path.get(path.size() - 1);
            int smallestWeight = Integer.MAX_VALUE;
            GraphNode nextNode = null;
            for (Map.Entry<GraphNode, Integer> entry : current.edges.entrySet()) {
                if (entry.getValue() < smallestWeight && !path.contains(entry.getKey())) {
                    smallestWeight = entry.getValue();
                    nextNode = entry.getKey();
                }
            }
            if (nextNode == null) {
                break;
            }
            int nextNodeIndex = path.indexOf(nextNode);
            if (nextNodeIndex != -1) {
                path.subList(nextNodeIndex, path.size()).clear();
            }
            path.add(nextNode);
        }
        return path;
    }
    