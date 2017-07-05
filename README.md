# Insight Data Engineering Challenge
A solution to the anomaly detection problem for the Insight Data Engineering Program implemented in Java.

### Task

A Product Manager at Market-ter approaches you with a new idea to encourage users to spend more money, without serving them pesky ads for items.
"If User A makes a large purchase, we should flag them to make sure User B and User C are influenced by it. We could highlight these large purchases in their "feed". We could also send an email to User D, recommending that they become friends with User A. They won't find these emails annoying because they share the mutual friend, User C.

But we can't send our users too many emails, so we should only do this with really high purchases that are considered "anomalies" - those that are 3 standard deviations above the average within their social network. These emails will ensure that our top spenders are the most connected and influential!"

Despite the excitement, you realize the Product Manager hasn't fully thought out two specific aspects of the problem:

1. Social networks change their purchasing behavior over time, so we shouldn't average over the full history of transactions. **How many transactions should we include in the average?**

2. Users only accept "nearby" recommendations. Recommendations might work for a "friend of a friend" (`Degree = 2`), but would a "friend of a friend of a friend" (`Degree = 3`) still work? **How many "degrees" should a social network include?**

Since the Product Manager doesn't know these factors yet, your platform must be flexible enough to easily adjust these parameters. Also, it will take in a lot of data, so it has to efficiently scale with the size of the input.

With this coding challenge, you should demonstrate a strong understanding of computer science fundamentals. We won't be wowed by your knowledge of various available software libraries but will be impressed by your ability to pick and use the best data structures and algorithms for the job.

We're looking for clean, well-thought-out code that correctly implements the desired feature in an optimized way and highlights your ability to write production-quality code and clear documentation.

For this challenge, you'll need two flexible parameters 

`D`: the number of degrees that defines a user's social network.

`T`: the number of consecutive purchases made by a user's social network (not including the user's own purchases)

A purchase amount is anomalous if it's more than 3 standard deviations from the mean of the last `T` purchases in the user's `D`th degree social network. As an expression, an anomalous amount is anything greater than `mean + (3 * sd)` where `sd` stands for standard deviation (see the FAQ for the mean and standard deviation). 

### Number of degrees in social network (`D`)
 
`D` should not be hardcoded, and will be at least `1`.

A value of `1` means you should only consider the friends of the user. A value of `2` means the social network extends to friends and "friends of friends".

For example, if `D = 1`, User A's social network would only consist of User B and User C but not User D.

If `D = 2`, User A's social network would consist of User B, User C, and User D.

### Tracked number of purchases in the user's network (`T`)

`T` also shouldn't be hardcoded, and will be at least `2`.

The latest purchase is the one with the highest timestamp. If 2 purchases have the same timestamp, the one listed first would be considered the earlier one.

If a user's social network has less than 2 purchases, we don't have enough historical information, so no purchases should be considered anomalous at that point. 

If a user's social network has made 2 or more purchases, but less than `T`, we should still proceed with the calucations to determine if the purchases are anomalous.

More Details can be found in the official repo: https://github.com/InsightDataScience/anomaly_detection

# Graph Representation
Since the number of users are unknown at the start of the program and user ids are not guaranteed to be consecutive, an adjacency list representation was choosen to keep track of each user and his/her adjacent nodes.  

# Keeping track of a User's Social Network
The solution uses a dictionary structure that stores Users in Key,Value pairs as <User, User's Social Network> and tracks all users who are within D steps of the User Key.  This prevents the need for a costly breadth-first search at every transaction event.  Any transaction events that the User recieves are also automatically pushed to the User's Social Network.  A breadth first search is only performed if a befriend or unfriend event occurs within its social network.

# Merging k sorted lists
Merging k sorted lists is performed in O(nklog(k)).  A pointer is placed at the tail of every list and each tail element is placed into a maxheap.  The root of the heap is taken out and the pointer for that list is decremented and the next element for that list is placed into the maxheap.  This continues until we reach a size T sorted array or there are no more values to sort

# Running Programs
The main program can be executed with

./run.sh yourBatchFile yourStreamFile yourOutputFile

All anomalies will be written to _yourOutputFile

# Dependecies
- gson-2.8.1


run.sh expects the gson-2.8.1.jar to be located in the src/main/java folder.

The code is expected to be compiled using Java1.8
