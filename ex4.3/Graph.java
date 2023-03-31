package edu.ucalgary.oop;

import java.util.*;

class Edge {
    int src, dest, weight;

    public Edge(int src, int dest, int weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}

class Graph {
    int V, E;
    List<Edge> edges;

    public Graph(int V, int E) {
        this.V = V;
        this.E = E;
        edges = new ArrayList<>();
    }

    public void addEdge(int src, int dest, int weight) {
        edges.add(new Edge(src, dest, weight));
    }

    private int find(int[] parent, int i) {
        if (parent[i] == -1) {
            return i;
        }
        return find(parent, parent[i]);
    }

    private void union(int[] parent, int x, int y) {
        int xRoot = find(parent, x);
        int yRoot = find(parent, y);
        parent[xRoot] = yRoot;
    }

    private boolean hasCycle(int src, int dest, int[] parent) {
        int x = find(parent, src);
        int y = find(parent, dest);

        if (x == y) {
            return true;
        }

        union(parent, x, y);
        return false;
    }

    public Graph mst() {
        Collections.sort(edges, Comparator.comparingInt(edge -> edge.weight));

        int[] parent = new int[V];
        Arrays.fill(parent, -1);

        Graph mst = new Graph(V, V - 1);

        int e = 0;
        for (Edge edge : edges) {
            if (e == V - 1) {
                break;
            }

            if (!hasCycle(edge.src, edge.dest, parent)) {
                mst.addEdge(edge.src, edge.dest, edge.weight);
                e++;
            }
        }

        return mst;
    }
}
