import edu.princeton.cs.algs4.CC;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.KruskalMST;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;
import java.util.Iterator;

// basically just point segregation #notcool #openborders
public class Clustering {

    // locations
    private Point2D[] loc;
    // number of clusters
    private int numClusters;
    // stores the cluster that the ith point is in
    private int[] clNum;

    // run the clustering algorithm and create the clusters
    public Clustering(Point2D[] locations, int k) {
        if (locations == null)
            throw new IllegalArgumentException("null arguments in clustering");

        if (k < 1 || k > locations.length)
            throw new IllegalArgumentException("incorrect range for k");


        // checks that the array doesn't contain null arguments
        for (int i = 0; i < locations.length; i++) {
            if (locations[i] == null)
                throw new IllegalArgumentException("one of the points is null");
        }

        loc = locations;
        clNum = new int[loc.length];
        numClusters = k;

        int m = loc.length;

        // create a complete edge weighted graph of m vertices
        EdgeWeightedGraph ewg = new EdgeWeightedGraph(m);
        for (int i = 0; i < m; i++) {
            for (int j = i + 1; j < m; j++) {
                double weight = loc[i].distanceTo(loc[j]);
                Edge e = new Edge(i, j, weight);
                ewg.addEdge(e);
            }
        }

        // compute minimum spanning tree
        KruskalMST mst = new KruskalMST(ewg);

        // forms a new graph of connected components
        // create an array of edges. set an edge to null if it is one of the
        // k-1 heaviest edges
        Iterator<Edge> edgesIt = mst.edges().iterator();

        // creates new graph that only contains the m-k least weighted edges
        // of the mst

        EdgeWeightedGraph newGraph = new EdgeWeightedGraph(m);
        int counter = m - k;

        while (counter > 0) {
            Edge e = edgesIt.next();
            newGraph.addEdge(e);
            counter--;
        }

        CC connectedComponents = new CC(newGraph);

        // stores the cluster that each point is in
        for (int i = 0; i < m; i++)
            clNum[i] = connectedComponents.id(i);
    }

    // private helper class so I can store a minheap of edges
    private class EdgeComparator implements Comparator<Edge> {
        public int compare(Edge edge1, Edge edge2) {
            return Double.compare(edge1.weight(), edge2.weight());
        }
    }


    // return the cluster of the ith point
    public int clusterOf(int i) {
        if (i < 0 || i > loc.length - 1)
            throw new IllegalArgumentException("incorrect range for i");

        return clNum[i];
    }

    // use the clusters to reduce the dimensions of an input
    public double[] reduceDimensions(double[] input) {
        if (input == null)
            throw new IllegalArgumentException("null argument in reduce");
        if (input.length != loc.length)
            throw new IllegalArgumentException("input has to be of length m");

        // reduced input
        double[] reduced = new double[numClusters];

        for (int i = 0; i < loc.length; i++) reduced[clNum[i]] += input[i];

        return reduced;
    }

    // unit testing (required)
    public static void main(String[] args) {

        Point2D[] points = new Point2D[9];
        int counter = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Point2D p = new Point2D(i / (3.0), j * j);
                points[counter] = p;
                counter++;
            }
        }

        Clustering c = new Clustering(points, 4);

        StdOut.println(c.clusterOf(3));

        double weightCounter = 1;
        double[] input = new double[9];

        for (int i = 0; i < 9; i++) {
            input[i] = weightCounter++;
            StdOut.print(input[i] + " ");
        }
        StdOut.println();

        double[] reduced = c.reduceDimensions(input);

        for (int i = 0; i < 4; i++)
            StdOut.print(reduced[i] + " ");
    }
}
