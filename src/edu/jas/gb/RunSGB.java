/*
 * $Id$
 */

package edu.jas.gb;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenSolvablePolynomial;
import edu.jas.poly.PolynomialList;
import edu.jas.kern.ComputerThreads;


/**
 * Simple setup to run a solvable GB example. <br />
 * Usage: RunSGB [seq|par] [irr|left|right|two] &lt;file&gt; #procs 
 * @author Heinz Kredel
 */

// Usage: RunSGB [seq|par|dist|cli] &lt;file&gt; #procs [machinefile]

public class RunSGB {

    /**
     * main method to be called from commandline <br />
     * Usage: RunSGB [seq|par] [irr|left|right|two] &lt;file&gt; #procs 
     */

    public static void main( java.lang.String[] args ) {

        BasicConfigurator.configure();

        String usage = "Usage: RunSGB "
            + "[ seq | par ] "
            //        + "[ seq | par | dist | cli [port] ] "
            + "[ irr | left | right | two ] "
            + "<file> "
            + "#procs ";
        //  + "[machinefile]";
        if ( args.length < 3 ) {
            System.out.println(usage);
            return;
        }

        String kind = args[0];
        String[] allkinds = new String[] { "seq", "par", "par+"};
        // String[] allkinds = new String[] { "seq", "par", "dist", "cli"  };
        boolean sup = false;
        for ( int i = 0; i < allkinds.length; i++ ) {
            if ( kind.equals( allkinds[i] ) ) {
                sup = true;
            }
        }
        if ( ! sup ) {
            System.out.println(usage);
            return;
        }
        String[] allmeth = new String[] { "irr", "left", "right", "two"};
        String action = args[1];
	sup = false;
        for ( int i = 0; i < allmeth.length; i++ ) {
            if ( action.equals( allmeth[i] ) ) {
                sup = true;
            }
        }
        if ( ! sup ) {
            System.out.println(usage);
            return;
        }

        String filename = args[2];

        int threads = 0;
        if (kind.startsWith("par")) {
            if (args.length < 4) {
                System.out.println(usage);
                return;
            }
            String tup = args[3];
            String t = tup;
            try {
                threads = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println(usage);
                return;
            }
        }

        Reader problem = null;
        try { 
            problem = new FileReader( filename );
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println(usage);
            return;
        }

        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(problem);
        PolynomialList S = null; 
        try {
            S = tok.nextSolvablePolynomialSet();
        } catch (IOException e) { 
            e.printStackTrace(); 
            return;
        }
        System.out.println("S =\n" + S); 

        if ( kind.equals("seq") ) {
            runSequential( S, action );
        }

        if ( kind.equals("par") ) {
            runParallel( S, threads, action );
        }
	//ComputerThreads.terminate();
    }


    /**
     * run Sequential.
     * @param S polynomial list.
     * @param action what to to.
     */
    @SuppressWarnings("unchecked")
    static void runSequential(PolynomialList S, String action) {
        List<GenSolvablePolynomial> L = S.list; 
        List<GenSolvablePolynomial> G = null;
        long t;
        SolvableReduction sred = new SolvableReductionSeq();
        SolvableGroebnerBase sbb = new SolvableGroebnerBaseSeq();

        t = System.currentTimeMillis();
        System.out.println("\nSolvable GB [" + action + "] sequential ..."); 
        if ( action.equals("irr") ) {
            G = sred.leftIrreducibleSet(L);
        }
        if ( action.equals("left") ) {
            G = sbb.leftGB(L);
        }
        if ( action.equals("right") ) {
            G = sbb.rightGB(L);
        }
        if ( action.equals("two") ) {
            G = sbb.twosidedGB(L);
        }
        if ( G == null ) {
            System.out.println("unknown action = " + action + "\n"); 
            return;
        }
        S = new PolynomialList( S.ring, G );
        System.out.println("G =\n" + S ); 
        System.out.println("G.size() = " + G.size() ); 
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds" ); 
        System.out.println(""); 
    }


    /**
     * run Parallel.
     * @param S polynomial list.
     * @param action what to to.
     */
    @SuppressWarnings("unchecked")
    static void runParallel(PolynomialList S, int threads, String action) {
        List<GenSolvablePolynomial> L = S.list; 
        List<GenSolvablePolynomial> G = null;
        long t;
        SolvableReduction sred = new SolvableReductionPar();
        SolvableGroebnerBaseParallel sbb = new SolvableGroebnerBaseParallel(threads);

        t = System.currentTimeMillis();
        System.out.println("\nSolvable GB [" + action + "] parallel " + threads + " threads ..."); 
        if ( action.equals("irr") ) {
            G = sred.leftIrreducibleSet(L);
        }
        if ( action.equals("left") ) {
            G = sbb.leftGB(L);
        }
        if ( action.equals("right") ) {
            G = sbb.rightGB(L);
        }
        if ( action.equals("two") ) {
            G = sbb.twosidedGB(L);
        }
        if ( G == null ) {
            System.out.println("unknown action = " + action + "\n"); 
            return;
        }
	if ( G.size() > 0 ) {
            S = new PolynomialList( G.get(0).ring, G );
	} else {
            S = new PolynomialList( S.ring, G );
	}
        System.out.println("G =\n" + S ); 
        System.out.println("G.size() = " + G.size() ); 
        t = System.currentTimeMillis() - t;
        System.out.println("time = " + t + " milliseconds" ); 
        System.out.println(""); 
        sbb.terminate();
    }

}
