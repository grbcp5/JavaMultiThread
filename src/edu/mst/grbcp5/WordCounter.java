package edu.mst.grbcp5;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Semaphore;

public class WordCounter {

  public static int DEFAULT_MAPPER_THREAD_COUNT = 4;
  public static int DEFAULT_REDUCER_THREAD_COUNT = 2;

  private String[] fileNames;
  private int mapperThreadCount;
  private int reducerThreadcount;
  private SynchronizedQueue<String> filesQueue;
  private SynchronizedQueue<Integer> countQueue;
  private Semaphore filesSemaphore;
  private Semaphore countSemaphore;
  private Semaphore doneCountingSemaphore;
  private Mapper[] mappers;

  public WordCounter( String dataFilePath, int mapperThreads, int reducerThreads ) {

    /* Local variables */
    Scanner dataFileScanner;
    String fileName;
    List<String> files;

    this.mapperThreadCount = mapperThreads;
    this.reducerThreadcount = reducerThreads;

    try {
      /* Save reference */
      File dataFile = new File( dataFilePath );

      /* Read in from data file */
      dataFileScanner = new Scanner( dataFile );

      /* Read in to list */
      files = new LinkedList<>();
      while( dataFileScanner.hasNext() ) {
        /* Get next file name */
        fileName = dataFileScanner.next();

        /* Add that file to list */
        files.add( fileName );
      }

      /* Save all files in an array */
      this.fileNames = files.toArray( new String[ files.size() ] );

    } catch ( FileNotFoundException e ) {
      e.printStackTrace();
      fileNames = new String[0];
    }

    /* Initialize local variables */
    filesQueue = new SynchronizedQueue<>( this.fileNames );
    countQueue = new SynchronizedQueue<>( new Integer[] {0} );
    filesSemaphore = new Semaphore( 1, true );
    countSemaphore = new Semaphore( 1, true );
    doneCountingSemaphore = new Semaphore( 1, true );

    mappers = new Mapper[ this.mapperThreadCount ];
    for ( int i = 0; i < this.mapperThreadCount; i++ ) {
      mappers[ i ] = new Mapper(
        filesSemaphore,
        filesQueue,
        countSemaphore,
        countQueue
      );
    }

  }

  public WordCounter( String dataFilePath ) {

    this(
      dataFilePath,
      DEFAULT_MAPPER_THREAD_COUNT,
      DEFAULT_REDUCER_THREAD_COUNT
    );

  }

  public String[] getFileNames() {
    return fileNames;
  }

  public int getWordCount( String word ) {

    try {
      this.doneCountingSemaphore.acquire();
    } catch ( InterruptedException e ) {
      e.printStackTrace();
    }

    /* Start each mapper thread */
    for ( Mapper mapper : this.mappers ) {
      mapper.setWord( word );
      mapper.start();
    }

    /* Wait for each mapper thread to complete */
    for( Mapper mapper : this.mappers ) {
      try {
        mapper.join();
      } catch ( InterruptedException e ) {
        e.printStackTrace();
      }
    }

    this.doneCountingSemaphore.release();

    System.out.println( "Count queue size: " + countQueue.size() );
    while( countQueue.size() > 0 ) {
      System.out.println( "Count Queue value: " + countQueue.remove() );
    }

    return 0;
  }

}
