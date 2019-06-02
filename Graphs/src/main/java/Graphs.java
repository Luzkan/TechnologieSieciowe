import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.*;
import java.util.ArrayList;

public class Graphs {

    private static final double defaultReliability = 0.95;
    private static final int testNum = 1;
    private static final int testVersion = 2;
    private static final boolean laud = false;

    private static final int secondTaskTestAmount = 1;
    private static final int bytesInPacket = 1;
    private static final int capacity = 3000;

    /* Ver 1: Linked-list   (1-2-3-4-5-6-7-8-..19-20)
       Ver 2: Circle:       (19-20-1-2-..-18-19-20..)
       Ver 3: Ver 2 +       (1, 10) + (5, 15)
                              0.8       0.7
       Ver 4: Ver 3 +       four random (n, m)
                                         0.4          */

    public static void main(String[] args) {

        SimpleDirectedWeightedGraph<Integer, DefaultWeightedEdge> graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        for(int i = 1; i <= 20; i++) {
            graph.addVertex(i);
        }

        // Note: That's a big pain to copy / clone / spoof / mirror a Graph
        //       The way I've done it below is probably the best
        //       Other methods are very resource consuming
        //       Not only memory/processing power wise, but also you as a human being
        //       ... unless you are a wise man of code, then please tell me better solution!!! <3

        // Measures how many tests has passed correctly on sample size of testNum
        int testPassed = 0;
        for(int i = 1; i <= testNum; i++) {
            if(laud) System.out.println("- Test number [" + i + "] has began -");

            // Graph recreation
            for(int k = 1; k <= 19 ; k++){
                DefaultWeightedEdge t = graph.addEdge(k, (k+1));
                graph.setEdgeWeight(t, defaultReliability);
            }

            // Various test versions
            if(testVersion == 2 || testVersion == 3 || testVersion == 4) {
                DefaultWeightedEdge ver2 = graph.addEdge(1, 20);
                graph.setEdgeWeight(ver2, defaultReliability);
                if(testVersion == 3 || testVersion == 4){
                    DefaultWeightedEdge ver3a = graph.addEdge(1, 10);
                    graph.setEdgeWeight(ver3a, 0.8);
                    DefaultWeightedEdge ver3b = graph.addEdge(5, 15);
                    graph.setEdgeWeight(ver3b, 0.7);
                    if(testVersion == 4){
                        boolean notPassedCreation = true;
                        while(notPassedCreation) {
                            // Random two numbers from range <1, 20>
                            int a = (int) Math.ceil(Math.random() * 20);
                            int b = (int) Math.ceil(Math.random() * 20);

                            int c = (int) Math.ceil(Math.random() * 20);
                            int d = (int) Math.ceil(Math.random() * 20);

                            int e = (int) Math.ceil(Math.random() * 20);
                            int f = (int) Math.ceil(Math.random() * 20);

                            int g = (int) Math.ceil(Math.random() * 20);
                            int h = (int) Math.ceil(Math.random() * 20);

                            if (pP(a, b) && pP(c, d) && pP(e, f) && pP(g, h) && bP(a, b, c, d, e, f, g, h)) {
                                DefaultWeightedEdge ver4ab = graph.addEdge(a, b);
                                graph.setEdgeWeight(ver4ab, 0.4);
                                DefaultWeightedEdge ver4cd = graph.addEdge(c, d);
                                graph.setEdgeWeight(ver4cd, 0.4);
                                DefaultWeightedEdge ver4ef = graph.addEdge(e, f);
                                graph.setEdgeWeight(ver4ef, 0.4);
                                DefaultWeightedEdge ver4gh = graph.addEdge(g, h);
                                graph.setEdgeWeight(ver4gh, 0.4);
                                notPassedCreation = false;
                            }
                        }
                    }
                }
            }

            // Holds edges to be deleted further on
            ArrayList<DefaultWeightedEdge> edges = new ArrayList<>();

            // Loop through the edges of graph
            for (DefaultWeightedEdge e : graph.edgeSet()) {
                if (Math.random() > graph.getEdgeWeight(e)) {
                    if(laud) System.out.println("Edge [" + e + "] has broken!");
                    edges.add(e);
                }
            }

            // Remove them finally
            for(DefaultWeightedEdge e : edges){
                graph.removeEdge(e);
            }

            // Test if the graph is reliable.
            ConnectivityInspector<Integer, DefaultWeightedEdge> inspectorTest = new ConnectivityInspector<>(graph);
            boolean connectedGraph = inspectorTest.isConnected();
            if(connectedGraph) {
                if(laud) System.out.println("> Graph [" + i + "] has proved to be reliable.");
                testPassed++;
            }

            // Remove every single edge (part 1 of remaking a graph)
            ArrayList<DefaultWeightedEdge> edgesLeft = new ArrayList<>(graph.edgeSet());

            // Remove them ^
            for(DefaultWeightedEdge e : edgesLeft){
                graph.removeEdge(e);
            }
        }
        System.out.println("> END: Tests passed: [" + testPassed + "/" + testNum + "]\n> Version: " + testVersion);




        /* TASK 2 */
        // IT LOOKS HORRIBLE AND EVERYTHING SHOULD BE MOVED AND REWORKED TO NOT MAKE COPIES OF CODE AND OTHER HORRENDOUS STUFF (#DEADLINE CODING)

        WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond = new WeightedMultigraph<>(DefaultWeightedEdge.class);
        for(int i = 1; i <= 10; i++) {
            graphSecond.addVertex(i);
        }

        double totalTimeMyGraph = 0;
        double totalTimeCircleGraph = 0;
        double totalTimePetersenGraph = 0;

        for(int q = 0; q < secondTaskTestAmount; q++) {

            // Fill up the matrix with random packets size value that is transferred per second
            int[][] matrixIntensity = new int[11][11];
            for(int k = 1; k <= 10; k++){
                for(int n = 1; n <= 10; n++){
                    matrixIntensity[k][n] = (int) Math.ceil(Math.random() * 100);
                }
            }

            // My Graph
            createGraphMyGraph(graphSecond, defaultReliability);
            double averageT = averageTime(matrixIntensity, graphSecond);
            //System.out.println("My Graph: " + averageT);
            totalTimeMyGraph += averageT;
            ArrayList<DefaultWeightedEdge> edgesLeft = new ArrayList<>(graphSecond.edgeSet());
            for (DefaultWeightedEdge e : edgesLeft) {
                graphSecond.removeEdge(e);
            }


            // Circle Graph
            createCircleGraph(graphSecond, defaultReliability);
            double averageTc = averageTime(matrixIntensity, graphSecond);
            //System.out.println("Circle Graph: " + averageTc);
            totalTimeCircleGraph += averageTc;
            ArrayList<DefaultWeightedEdge> edgesLeftk = new ArrayList<>(graphSecond.edgeSet());
            for (DefaultWeightedEdge e : edgesLeftk) {
                graphSecond.removeEdge(e);
            }


            // Petersen Graph
            createPetersenGraph(graphSecond, defaultReliability);
            double averageTd = averageTime(matrixIntensity, graphSecond);
            //System.out.println("Peterson Graph: " + averageTd);
            totalTimePetersenGraph += averageTd;
            ArrayList<DefaultWeightedEdge> edgesLeftkk = new ArrayList<>(graphSecond.edgeSet());
            for (DefaultWeightedEdge e : edgesLeftkk) {
                graphSecond.removeEdge(e);
            }
        }

        System.out.println("My Graph time: " + totalTimeMyGraph + "\nCircle Graph time: " + totalTimeCircleGraph + "\nPetersen Graph time: " + totalTimePetersenGraph);

        int testGood = 0;
        for(int measureOfGoodness = 0; measureOfGoodness < 100; measureOfGoodness++) {
            if(testGraphReliability(graphSecond, "Petersen", 0.95, 10))
                testGood++;
        }

        System.out.println("Amount of tests that ran succesfully: " + testGood);

    }

    private static boolean testGraphReliability(WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond, String graph, double defaultReliability, int maxTime) {

        // Fill up the matrix with random packets size value that is transferred per second
        int[][] matrixIntensity = new int[11][11];
        for(int k = 1; k <= 10; k++){
            for(int n = 1; n <= 10; n++){
                matrixIntensity[k][n] = (int) Math.ceil(Math.random() * 100);
            }
        }

        double totalTime = 0;

        switch(graph) {
            case ("Petersen"): {
                createPetersenGraph(graphSecond, defaultReliability);
            }
            case ("Circle"): {
                createCircleGraph(graphSecond, defaultReliability);
            }
            case ("My"):{
                createGraphMyGraph(graphSecond, defaultReliability);
            }

            int reliabilityTime = 0;
            boolean connectedGraph = true;

            while(connectedGraph) {
                //System.out.println("Peterson Graph: " + averageTd);

                // Holds edges to be deleted further on
                ArrayList<DefaultWeightedEdge> edges = new ArrayList<>();

                // Loop through the edges of graph
                for (DefaultWeightedEdge e : graphSecond.edgeSet()) {
                    if (Math.random() > graphSecond.getEdgeWeight(e)) {
                        edges.add(e);
                    }
                }

                // Remove them finally
                for(DefaultWeightedEdge e : edges){
                    graphSecond.removeEdge(e);
                }

                // Test if the graph is reliable.
                ConnectivityInspector<Integer, DefaultWeightedEdge> inspectorTest = new ConnectivityInspector<>(graphSecond);
                connectedGraph = inspectorTest.isConnected();

                reliabilityTime++;

                if(reliabilityTime == maxTime){
                    ArrayList<DefaultWeightedEdge> edgesLeftkk = new ArrayList<>(graphSecond.edgeSet());
                    for (DefaultWeightedEdge e : edgesLeftkk) {
                        graphSecond.removeEdge(e);
                    }
                    return true;
                }
            }
            System.out.println("Test Failed! Reliability Time: " + reliabilityTime);
        }

        ArrayList<DefaultWeightedEdge> edgesLeftkk = new ArrayList<>(graphSecond.edgeSet());
        for (DefaultWeightedEdge e : edgesLeftkk) {
            graphSecond.removeEdge(e);
        }

        return false;

    }

    private static double averageTime
            (int[][] matrixIntensity,
             WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond){

        double matrixSum = 0;
        for(int k = 1; k <= 10; k++){
            for(int n = 1; n <= 10; n++){
                matrixSum += matrixIntensity[k][n];
            }
        }

        double sumEdgesEquation = 0;
        for (DefaultWeightedEdge e : graphSecond.edgeSet()) {

            int i = graphSecond.getEdgeSource(e);
            int j = graphSecond.getEdgeTarget(e);

            double a = calculateA(i, j, graphSecond, matrixIntensity);
            double c = capacity;

            sumEdgesEquation += (a/((c/bytesInPacket)-a));
        }

        //System.out.println("Matrix: " + matrixSum);
        //System.out.println("Edges Sum: " + sumEdgesEquation);
        return (1/matrixSum) * sumEdgesEquation;
    }

    // Sum of intensity on shortest paths
    private static double calculateA(int i, int j,
                     WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond, int[][] matrix){

        // Get all packets that will go through that edge in this graph
        int packetsInEdge = 0;
        for(int a = 1; a < 10; a++){
            for(int b = 1; b < 10; b++) {
                GraphPath<Integer, DefaultWeightedEdge> shortest_path = DijkstraShortestPath.findPathBetween(graphSecond, a, b);

                if (shortest_path.getEdgeList().contains(graphSecond.getEdge(i, j))) {
                    packetsInEdge += matrix[a][b];
                }
            }
        }

        if(capacity < packetsInEdge){
            System.out.println("[Error] Edge (" + i + "," + j + ") overloaded" );
            return 999999999;
        }

        return packetsInEdge;
    }


    //previousPairs
    private static boolean pP(int a, int b){
        return !(a == b || a == (b + 1) || (a + 1) == b) && !(a == 1 && b == 10) && !(a == 5 && b == 15) && !(a == 1 && b == 20);
    }

    //betweenPairs
    private static boolean bP(int a, int b, int c, int d, int e, int f, int g, int h){
        return (a != c || b != d) && (a != e || b != f) && (a != g || b != h) && (c != e || d != f) && (c != g || d != h) && (e != g || f != h);
    }

    private static void createGraphMyGraph(WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond, double defaultReliability){
        DefaultWeightedEdge e1 = graphSecond.addEdge(1, 2);
        graphSecond.setEdgeWeight(e1, defaultReliability);
        DefaultWeightedEdge e2 = graphSecond.addEdge(2, 3);
        graphSecond.setEdgeWeight(e2, defaultReliability);
        DefaultWeightedEdge e3 = graphSecond.addEdge(3, 4);
        graphSecond.setEdgeWeight(e3, defaultReliability);
        DefaultWeightedEdge e4 = graphSecond.addEdge(4, 5);
        graphSecond.setEdgeWeight(e4, defaultReliability);
        DefaultWeightedEdge e5 = graphSecond.addEdge(5, 1);
        graphSecond.setEdgeWeight(e5, defaultReliability);

        DefaultWeightedEdge e6 = graphSecond.addEdge(6, 7);
        graphSecond.setEdgeWeight(e6, defaultReliability);
        DefaultWeightedEdge e7 = graphSecond.addEdge(7, 8);
        graphSecond.setEdgeWeight(e7, defaultReliability);
        DefaultWeightedEdge e8 = graphSecond.addEdge(8, 9);
        graphSecond.setEdgeWeight(e8, defaultReliability);
        DefaultWeightedEdge e9 = graphSecond.addEdge(9, 10);
        graphSecond.setEdgeWeight(e9, defaultReliability);
        DefaultWeightedEdge e10 = graphSecond.addEdge(10, 6);
        graphSecond.setEdgeWeight(e10, defaultReliability);

        DefaultWeightedEdge e11 = graphSecond.addEdge(1, 6);
        graphSecond.setEdgeWeight(e11, defaultReliability);
        DefaultWeightedEdge e12 = graphSecond.addEdge(2, 7);
        graphSecond.setEdgeWeight(e12, defaultReliability);
        DefaultWeightedEdge e13 = graphSecond.addEdge(3, 8);
        graphSecond.setEdgeWeight(e13, defaultReliability);
        DefaultWeightedEdge e14 = graphSecond.addEdge(4, 9);
        graphSecond.setEdgeWeight(e14, defaultReliability);
        DefaultWeightedEdge e15 = graphSecond.addEdge(5, 10);
        graphSecond.setEdgeWeight(e15, defaultReliability);
    }

    private static void createCircleGraph(WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond, double defaultReliability){
        DefaultWeightedEdge k1 = graphSecond.addEdge(1, 2);
        graphSecond.setEdgeWeight(k1, defaultReliability);
        DefaultWeightedEdge k2 = graphSecond.addEdge(2, 3);
        graphSecond.setEdgeWeight(k2, defaultReliability);
        DefaultWeightedEdge k3 = graphSecond.addEdge(3, 4);
        graphSecond.setEdgeWeight(k3, defaultReliability);
        DefaultWeightedEdge k4 = graphSecond.addEdge(4, 5);
        graphSecond.setEdgeWeight(k4, defaultReliability);
        DefaultWeightedEdge k5 = graphSecond.addEdge(5, 6);
        graphSecond.setEdgeWeight(k5, defaultReliability);

        DefaultWeightedEdge k6 = graphSecond.addEdge(6, 7);
        graphSecond.setEdgeWeight(k6, defaultReliability);
        DefaultWeightedEdge k7 = graphSecond.addEdge(7, 8);
        graphSecond.setEdgeWeight(k7, defaultReliability);
        DefaultWeightedEdge k8 = graphSecond.addEdge(8, 9);
        graphSecond.setEdgeWeight(k8, defaultReliability);
        DefaultWeightedEdge k9 = graphSecond.addEdge(9, 10);
        graphSecond.setEdgeWeight(k9, defaultReliability);
        DefaultWeightedEdge k10 = graphSecond.addEdge(10, 1);
        graphSecond.setEdgeWeight(k10, defaultReliability);
    }

    private static void createPetersenGraph(WeightedMultigraph<Integer, DefaultWeightedEdge> graphSecond, double defaultReliability) {
        DefaultWeightedEdge d1 = graphSecond.addEdge(1, 2);
        graphSecond.setEdgeWeight(d1, defaultReliability);
        DefaultWeightedEdge d2 = graphSecond.addEdge(2, 3);
        graphSecond.setEdgeWeight(d2, defaultReliability);
        DefaultWeightedEdge d3 = graphSecond.addEdge(3, 4);
        graphSecond.setEdgeWeight(d3, defaultReliability);
        DefaultWeightedEdge d4 = graphSecond.addEdge(4, 5);
        graphSecond.setEdgeWeight(d4, defaultReliability);
        DefaultWeightedEdge d5 = graphSecond.addEdge(5, 1);
        graphSecond.setEdgeWeight(d5, defaultReliability);

        DefaultWeightedEdge d6 = graphSecond.addEdge(6, 8);
        graphSecond.setEdgeWeight(d6, defaultReliability);
        DefaultWeightedEdge d7 = graphSecond.addEdge(7, 9);
        graphSecond.setEdgeWeight(d7, defaultReliability);
        DefaultWeightedEdge d8 = graphSecond.addEdge(8, 10);
        graphSecond.setEdgeWeight(d8, defaultReliability);
        DefaultWeightedEdge d9 = graphSecond.addEdge(9, 6);
        graphSecond.setEdgeWeight(d9, defaultReliability);
        DefaultWeightedEdge d10 = graphSecond.addEdge(10, 7);
        graphSecond.setEdgeWeight(d10, defaultReliability);

        DefaultWeightedEdge d11 = graphSecond.addEdge(1, 6);
        graphSecond.setEdgeWeight(d11, defaultReliability);
        DefaultWeightedEdge d12 = graphSecond.addEdge(2, 7);
        graphSecond.setEdgeWeight(d12, defaultReliability);
        DefaultWeightedEdge d13 = graphSecond.addEdge(3, 8);
        graphSecond.setEdgeWeight(d13, defaultReliability);
        DefaultWeightedEdge d14 = graphSecond.addEdge(4, 9);
        graphSecond.setEdgeWeight(d14, defaultReliability);
        DefaultWeightedEdge d15 = graphSecond.addEdge(5, 10);
        graphSecond.setEdgeWeight(d15, defaultReliability);
    }
}

//        DefaultWeightedEdge e1 = graph.addEdge(1, 2);
//        graph.setEdgeWeight(e1, defaultReliability);

//        for(int k = 1; k <= 19 ; k++){
//            DefaultWeightedEdge t = graph.addEdge(k, (k+1));
//            graph.setEdgeWeight(t, defaultReliability);
//        }

//        System.out.println("Shortest path from vertex1 to vertex20:");
//        GraphPath<Integer, DefaultWeightedEdge> shortest_path = DijkstraShortestPath.findPathBetween(graph, 1, 20);
//        System.out.println(shortest_path);