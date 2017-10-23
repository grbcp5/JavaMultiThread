package edu.mst.grbcp5;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class SynchronizedQueue<T> {

  private Queue<T> queue;

  public SynchronizedQueue( ) {
    this.queue = new LinkedList<>();
  }

  public SynchronizedQueue( T[] initialValues ) {
    this.queue = new LinkedList<>( Arrays.asList( initialValues ) );
  }

  public synchronized T remove() {
    return queue.remove();
  }

  public synchronized int size() {
    return queue.size();
  }

  public synchronized boolean add( T t ) {
    return queue.add( t );
  }

}
