package edu.mst.grbcp5;

import java.util.Arrays;
import java.util.concurrent.Semaphore;

public class Main {

  public static void main( String[] args ) {

    /* Local variables */
    WordCounter wordCounter;

    if( args.length != 1 ) {
      System.out.println( "Usage: WordCounter dataFile.dat" );
      return;
    }

    /* Initialize */
    wordCounter = new WordCounter( args[ 0 ] );

    System.out.println( "Files to search: " +
      Arrays.toString( wordCounter.getFileNames() )
    );

    wordCounter.getWordCount( "the" );

  }

}
