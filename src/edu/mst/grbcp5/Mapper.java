package edu.mst.grbcp5;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class Mapper extends Thread {

  private Semaphore filesSemaphore;
  private SynchronizedQueue<String> filesQueue;
  private Semaphore countSemaphore;
  private SynchronizedQueue<Integer> countQueue;
  private String word;

  public Mapper(
    Semaphore filesSemaphore,
    SynchronizedQueue< String > filesQueue,
    Semaphore countSemaphore,
    SynchronizedQueue< Integer > countQueue
  ) {

    /* Initialize instance variables */
    this.filesSemaphore = filesSemaphore;
    this.filesQueue = filesQueue;
    this.countSemaphore = countSemaphore;
    this.countQueue = countQueue;
    this.word = null;

  }

  @Override
  public void run() {

    /* Local variables */
    String currentFileName;
    int currentCount;
    Scanner fileScanner;

    /* Quit if no search word has been set */
    if( word == null ) {
      System.out.println( "No search word set." );
      return;
    }

    /* Main procedure loop */
    while( true ) {
      currentFileName = null;
      currentCount = -1;
      fileScanner = null;

      /* Get file to search */
      try {
        this.filesSemaphore.acquire();

        /* Check if queue is empty */
        if( this.filesQueue.size() == 0 ) {
          this.filesSemaphore.release();
          this.word = null;
          return;
        }

        /* Get next file */
        currentFileName = this.filesQueue.remove();

        /* Give back semaphore */
        this.filesSemaphore.release();

      } catch ( InterruptedException e ) {
        e.printStackTrace();
        currentFileName = null;
      }

      /* Check we actually acquired a file name */
      if( currentFileName == null )
        continue;

      /* Map file */
      try {
        fileScanner = new Scanner( new File( currentFileName ) );
        currentCount = getWordCount( fileScanner, this.word );
      } catch ( FileNotFoundException e ) {
        e.printStackTrace();
        continue;
      }

      /* Check we actually acquired a count */
      if( currentCount < 0 ) {
        System.out.println( "Recieved an invalid count" );
        continue;
      }

      /* Update count queue */
      try {
        this.countSemaphore.acquire();
        this.countQueue.add( currentCount );
        this.countSemaphore.release();
      } catch ( InterruptedException e ) {
        e.printStackTrace();
      }

    } /* Procedure Loop */

  } /* run */

  private static int getWordCount( Scanner fileScanner, String word ) {

    String currentWord;
    String lowerWord;
    int count;

    count = 0;
    lowerWord = word.toLowerCase();

    while( fileScanner.hasNext() ) {
      currentWord = fileScanner.next();

      if( currentWord.toLowerCase().equals( lowerWord ) ) {
        count++;
      }

    }

    return count;
  }

  public String getWord() {
    return word;
  }

  public void setWord( String word ) {
    this.word = word;
  }
}
