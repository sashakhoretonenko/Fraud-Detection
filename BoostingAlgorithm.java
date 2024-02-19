import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

// badabang badaboost
public class BoostingAlgorithm {

    // cluster
    private Clustering cluster;
    // reduced n x k matrix
    private double[][] reducedMatrix;
    // labels
    private int[] labels;
    // queue of weak learners
    private Queue<WeakLearner> learners;
    // number of points
    private int n;
    // numer of locations
    private int m;
    // current weights
    private double[] weights;


    // create the clusters and initialize your data structures
    public BoostingAlgorithm(double[][] input, int[] labels,
                             Point2D[] locations, int k) {
        // error if arguments are null
        if (input == null || labels == null || locations == null)
            throw new IllegalArgumentException("null argument");

        // error if k is not within prescribed range
        if (k < 1 || k > locations.length)
            throw new IllegalArgumentException("k outside of prescribed range");

        // corber case to check that we can actually reference the first column
        if (input.length == 0 || input[0] == null || input[0].length == 0)
            throw new IllegalArgumentException("arrays are of length 0");

        // checks that lengths are compatible
        if (input.length != labels.length || locations.length != input[0].length)
            throw new IllegalArgumentException("incompatible lengths");

        // checks that nothing in input is null and that all labels are correct
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null)
                throw new IllegalArgumentException("null array in input");
            if (labels[i] != 0 && labels[i] != 1)
                throw new IllegalArgumentException("invalid label");
        }

        for (int i = 0; i < locations.length; i++) {
            if (locations[i] == null)
                throw new IllegalArgumentException("null element in locations");
        }

        // declares instance variables
        n = input.length;
        m = locations.length;
        this.labels = labels;
        learners = new Queue<WeakLearner>();
        weights = new double[n];
        for (int i = 0; i < n; i++) weights[i] = 1.0 / n;

        cluster = new Clustering(locations, k);

        // reduces dimensions
        reducedMatrix = new double[n][k];
        for (int i = 0; i < n; i++)
            reducedMatrix[i] = cluster.reduceDimensions(input[i]);


    }

    // return the current weights
    public double[] weights() {
        return weights;
    }

    // apply one step of the boosting algorithm
    public void iterate() {
        WeakLearner learner = new WeakLearner(reducedMatrix, weights, labels);
        learners.enqueue(learner);
        // keeps track of total weights for normalizing
        double totalWeight = 0;

        for (int i = 0; i < n; i++) {
            if (labels[i] != learner.predict(reducedMatrix[i])) weights[i] *= 2;
            totalWeight += weights[i];
        }
        // normalizes
        for (int i = 0; i < n; i++) weights[i] /= totalWeight;
    }

    // return the prediction of the learner for a new sample
    public int predict(double[] sample) {
        if (sample == null || sample.length != m)
            throw new IllegalArgumentException("sample null or invalid length");

        // number of times 0 and 1 are predicted, respectively
        int num0 = 0;
        int num1 = 0;

        double[] reduced = cluster.reduceDimensions(sample);

        for (WeakLearner learner : learners) {
            if (learner.predict(reduced) == 0) num0++;
            else num1++;
        }

        if (num0 >= num1) return 0;
        else return 1;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet test = new DataSet(args[1]);
        int k = Integer.parseInt(args[2]);
        int iterations = Integer.parseInt(args[3]);

        Stopwatch stop = new Stopwatch();

        // train the model
        BoostingAlgorithm model = new BoostingAlgorithm(
                training.input, training.labels, training.locations, k);

        for (int t = 0; t < iterations; t++)
            model.iterate();

        // calculate the training data set accuracy
        double trainingAccuracy = 0;
        for (int i = 0; i < training.n; i++)
            if (model.predict(training.input[i]) == training.labels[i])
                trainingAccuracy += 1;
        trainingAccuracy /= training.n;

        // calculate the test data set accuracy
        double testAccuracy = 0;
        for (int i = 0; i < test.n; i++)
            if (model.predict(test.input[i]) == test.labels[i])
                testAccuracy += 1;
        testAccuracy /= test.n;

        StdOut.println("Training accuracy of model: " + trainingAccuracy);
        StdOut.println("Test accuracy of model:     " + testAccuracy);


        StdOut.println(stop.elapsedTime());

        // we called weights, happy now checkstyle?
        StdOut.println(model.weights().length);
    }
}
