import edu.princeton.cs.algs4.IndexMinPQ;
import edu.princeton.cs.algs4.StdOut;

public class WeakLearner {
    // number of points
    private int n;
    // number of dimensions
    private int k;

    // predicted dimension
    private int dp;
    // predicted value
    private double vp;
    // predicted sign
    private int sp;


    // train the weak learner
    // input is an n x k matrix
    // weights is an array of length n
    // labels is an array of length n with input 0 for clean and 1 for fraud
    public WeakLearner(double[][] input, double[] weights, int[] labels) {
        if (input == null || weights == null || labels == null)
            throw new IllegalArgumentException("WeakLearner constructor"
                                                       + "arguments are null");


        if (labels.length != weights.length || labels.length != input.length)
            throw new IllegalArgumentException("lengths of arguments are"
                                                       + "incompatible");

        if (input.length == 0 || input[0] == null || input[0].length == 0)
            throw new IllegalArgumentException("arrays are of length 0");

        // iterate through and check that the arguments are valid
        for (int i = 0; i < input.length; i++) {
            if (input[i] == null)
                throw new IllegalArgumentException("null array");
            if (weights[i] < 0)
                throw new IllegalArgumentException("negative weight");
            if (labels[i] != 0 && labels[i] != 1)
                throw new IllegalArgumentException("label not 0 or 1");
        }

        // initialize variables
        n = input.length;
        k = input[0].length;

        // stores the most accurate prediction
        double mostAccurate = 0;

        for (int i = 0; i < k; i++) {
            // indexed min pq used to sort the k dimensions by weight
            IndexMinPQ<Double> minheap = new IndexMinPQ<Double>(n);

            double sp0Total = 0;
            double sp1Total = 0;

            // reads in dimension values to the pq and calculates totals
            for (int j = 0; j < n; j++) {

                double point = input[j][i];
                double weight = weights[j];
                int label = labels[j];

                minheap.insert(j, point);
                if (label == 1) sp0Total += weight;
                else sp1Total += weight;
            }

            if (sp0Total >= sp1Total && sp0Total > mostAccurate) {
                mostAccurate = sp0Total;
                dp = i;
                vp = minheap.minKey() - 1;
                sp = 0;
            }
            else if (sp1Total > sp0Total && sp1Total > mostAccurate) {
                mostAccurate = sp1Total;
                dp = i;
                vp = minheap.minKey() - 1;
                sp = 1;
            }

            while (!minheap.isEmpty()) {
                double minKey = minheap.minKey();
                int indexOfMinKey = minheap.delMin();
                int label = labels[indexOfMinKey];
                double weight = weights[indexOfMinKey];

                if (label == 0) {
                    sp0Total += weight;
                    sp1Total -= weight;
                }
                else {
                    sp0Total -= weight;
                    sp1Total += weight;
                }

                // handles corner case for points with ientical coordinates
                while (!minheap.isEmpty() && minheap.minKey() == minKey) {
                    minKey = minheap.minKey();
                    indexOfMinKey = minheap.delMin();
                    label = labels[indexOfMinKey];
                    weight = weights[indexOfMinKey];

                    if (label == 0) {
                        sp0Total += weight;
                        sp1Total -= weight;
                    }
                    else {
                        sp0Total -= weight;
                        sp1Total += weight;
                    }
                }

                if (sp0Total >= sp1Total && sp0Total > mostAccurate) {
                    mostAccurate = sp0Total;
                    dp = i;
                    vp = minKey;
                    sp = 0;
                }
                else if (sp1Total > sp0Total && sp1Total > mostAccurate) {
                    mostAccurate = sp1Total;
                    dp = i;
                    vp = minKey;
                    sp = 1;
                }
            }
        }
    }

    // return the prediction of the learner for a new sample
    public int predict(double[] sample) {
        if (sample == null) throw new IllegalArgumentException("null sample");
        if (sample.length != k) throw new IllegalArgumentException(
                "incorrect length of sample");

        if (sp == 0) {
            if (sample[dp] <= vp) return 0;
            else return 1;
        }
        else {
            if (sample[dp] <= vp) return 1;
            else return 0;
        }
    }

    // return the dimension the learner uses to separate the data
    public int dimensionPredictor() {
        return dp;
    }


    // return the value the learner uses to separate the data
    public double valuePredictor() {
        return vp;
    }

    // return the sign the learner uses to separate the data
    public int signPredictor() {
        return sp;
    }

    // unit testing (required)
    public static void main(String[] args) {
        // read in the terms from a file
        DataSet training = new DataSet(args[0]);
        DataSet test = new DataSet(args[1]);

        double[] weights = new double[training.labels.length];
        for (int i = 0; i < weights.length; i++) weights[i] = 1;

        // train the model
        WeakLearner model = new WeakLearner(training.input, weights,
                                            training.labels);


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

        // checkstyle stuff
        StdOut.println(model.dimensionPredictor());
        StdOut.println(model.valuePredictor());
        StdOut.println(model.signPredictor());

    }
}
