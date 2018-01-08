# Programming Assignment 2: Deques and Randomized Queues

## Goal of the Assignment
Write a generic data type for a deque and a randomized queue. The goal of this assignment is to implement elementary data structures using arrays and linked lists, and to introduce you to generics and iterators.

## Dequeue.
A double-ended queue or deque (pronounced “deck”) is a generalization of a stack and a queue that supports adding and removing items from either the front or the back of the data structure. 

## Randomized queue.
A randomized queue is similar to a stack or queue, except that the item removed is chosen uniformly at random from items in the data structure. 


## Client.
Write a client program Permutation.java that takes an integer k as a command-line argument; reads in a sequence of strings from standard input using StdIn.readString(); and prints exactly k of them, uniformly at random. Print each item from the sequence at most once.