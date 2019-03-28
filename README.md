# pathfinder
Takes in a file called smallnet with edge data like source, sink, delay etc. the program finds all possible paths and then pushes 
data on the paths given number of users who want to send their data over the paths. it optimizes the data sent by choosing 
multiple paths to minimize delay, buffer, energy etc. 
the program outputs the paths to be taken by each user and also the amount of data to be sent on each path.
The program acts as a centralized control which optimizes the data sent using multiple paths.
