Programming Assignment 7: Fraud Detection

/* *****************************************************************************
 *  Describe how you implemented the Clustering constructor
 **************************************************************************** */

I created a complete edge weighted graph from the m vertices. Then I computed
the minimum spanning tree using the KruskalMST API. Since the edges()
iterable by KruskalMST returns the edges in increasing order, I only considered
the first m-k edges returned by the iterator. Then I used the CC API to find
the connected components. I also created an int array to store which cluster
each point belongs to as this is useful for computing the weights of the
reduced dimensions array.

/* *****************************************************************************
 *  Describe how you implemented the WeakLearner constructor
 **************************************************************************** */

For each dimension, I created a minIndexPQ that sorted the points in order
of increasing value in regards to the coordinate of that specific dimension.
Additionally, while I was looping through these points to insert them into
the pq, I calculated what the sp0 and sp1 hyperplanes would predict if their
vp was smaller than the smallest coordinate. This way, I iterated through the
points in order of increasing coordinate, and simply had to update how the
predictors corresponding to sp0 and sp1 changed. Add to sp0Total and subtract
from sp1Total if the label of the point is 1. Add to sp1Total and subtract
from sp0Total if the label of the point is 0. Each time you update sp0Total
and sp1Total, check if surpasses your most accurate estimate yet and if it does,
update the dimension predictor, value predictor, and sign predictor.


/* *****************************************************************************
 *  Consider the large_training.txt and large_test.txt datasets.
 *  Run the boosting algorithm with different values of k and T (iterations),
 *  and calculate the test data set accuracy and plot them below.
 *
 *  (Note: if you implemented the constructor of WeakLearner in O(kn^2) time
 *  you should use the training.txt and test.txt datasets instead, otherwise
 *  this will take too long)
 **************************************************************************** */

      k          T         test accuracy       time (seconds)
   --------------------------------------------------------------------------
     5          100         0.841                   0.54
     5          1000        0.822                   3.701
     5          3000        0.811                   10.518
     50         1000        0.98                    22.464
     50         500         0.98                    11.203
     50         250         0.978                   5.728
     100        250         0.951                   9.348
     75         300         0.976                   9.398
     60         400         0.97                    10.197
     55         400         0.972                   9.704
     40         500         0.976                   9.906
     30         500         0.974                   7.802
     30         600         0.975                   9.193
     35         500         0.974                   8.6
     45         400         0.981                   7.969
     45         500         0.981                   10.253
     200        200         0.879                   12.132
     20         1000        0.968                   12.66
     25         700         0.975                   9.892








/* *****************************************************************************
 *  Find the values of k and T that maximize the test data set accuracy,
 *  while running under 10 second. Write them down (as well as the accuracy)
 *  and explain:
 *   1. Your strategy to find the optimal k, T.
 *   2. Why a small value of T leads to low test accuracy.
 *   3. Why a k that is too small or too big leads to low test accuracy.
 **************************************************************************** */

k: 45
T: 400
Accuracy: 0.981

1. I honestly just kept upper and lower bounding it until I landed on a value of
k = 45 which seemed to work perfectly and T = 400 is just the right amount of
trials to ensure that it runs in under 10 seconds.

2. Obviously, if we only create a couple of weak learners, our model won't be
very accurate. By creating many decision stumps, our model becomes more
accurate.

3. Too few clusters means that the model considers certain data points as
close when they really aren't, leading to incorrect predictions. On the other
hand, too many clusters causes overfitting, which essentially means the model
tries to distinguish between points that are extremely similar, making it's
prediction less accurate.

/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */


/* *****************************************************************************
 *  Describe any serious problems you encountered.
 **************************************************************************** */


/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback
 *  on how much you learned from doing the assignment, and whether
 *  you enjoyed doing it.
 **************************************************************************** */

I feel like such a smarty-pants for figuring out this assignment on my own.
Using a minheap was a stroke of genius and you should definitely give me
Professor Wayne's job when he retires.
