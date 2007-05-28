
/*
 * $Id$
 */

package edu.jas.ufd;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;

import org.apache.log4j.Logger;

import edu.jas.structure.RingElem;
import edu.jas.structure.GcdRingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.ModInteger;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigRational;

import edu.jas.poly.GenPolynomial;

import edu.jas.ufd.GreatestCommonDivisor;
import edu.jas.ufd.GreatestCommonDivisorAbstract;
import edu.jas.ufd.GreatestCommonDivisorSubres;
import edu.jas.ufd.GreatestCommonDivisorPrimitive;
import edu.jas.ufd.GreatestCommonDivisorModular;
import edu.jas.ufd.GreatestCommonDivisorModEval;


/**
 * Greatest common divisor parallel proxy.
 * @author Heinz Kredel
 */

public class GCDProxy<C extends GcdRingElem<C>>
             extends GreatestCommonDivisorAbstract<C> {
    //       implements GreatestCommonDivisor<C> {

    private static final Logger logger = Logger.getLogger(GCDProxy.class);
    private boolean debug = logger.isDebugEnabled();


    /**
      * GCD engines.
      */

    public final GreatestCommonDivisor<C> e1;

    public final GreatestCommonDivisor<C> e2;


    /**
      * Thread pool.
      */

    protected ExecutorService pool; 


    /**
      * Thread pool size.
      */

    protected static final int anzahl = 2;


    /**
      * Thread poll intervall.
      */

    protected static final int dauer = 5;



    /**
     * Proxy constructor.
     */
     public GCDProxy(GreatestCommonDivisor<C> e1, GreatestCommonDivisor<C> e2 ) {
         this.e1 = e1; 
         this.e2 = e2; 
         if ( pool == null ) {
            pool = Executors.newFixedThreadPool(anzahl);
         }
     }                                              


    /**
     * Terminate proxy.
     */
     public void terminate() {
         List<Runnable> r = pool.shutdownNow();
         if ( r.size() != 0 ) {
            // throw new RuntimeException("there are unfinished tasks " + r);
            // System.out.println("there are " + r.size() + " unfinished tasks ");
            logger.info("there are " + r.size() + " unfinished tasks ");
         }
     }


    /** Get the String representation as RingFactory.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "GCDProxy[ " 
            + e1.getClass().getName() + ", "
            + e2.getClass().getName() + " ]";
    }


    /**
     * Univariate GenPolynomial greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate GenPolynomial.
     * @param S univariate GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<C> baseGcd( GenPolynomial<C> P,
                                     GenPolynomial<C> S ) {
           throw new RuntimeException("baseGcd not implemented");
    }


    /**
     * Univariate GenPolynomial recursive greatest comon divisor.
     * Uses pseudoRemainder for remainder.
     * @param P univariate recursive GenPolynomial.
     * @param S univariate recursive GenPolynomial.
     * @return gcd(P,S).
     */
    public GenPolynomial<GenPolynomial<C>> 
           recursiveGcd( GenPolynomial<GenPolynomial<C>> P,
                         GenPolynomial<GenPolynomial<C>> S ) {
           throw new RuntimeException("recursiveGcd not implemented");
    }


    /**
     * GenPolynomial greatest comon divisor.
     * Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
     public GenPolynomial<C> gcd( final GenPolynomial<C> P, final GenPolynomial<C> S ) {
         GenPolynomial<C> g = null;

         Callable<GenPolynomial<C>> c0;
         Callable<GenPolynomial<C>> c1;
         List<Callable<GenPolynomial<C>>> cs = new ArrayList<Callable<GenPolynomial<C>>>(2);
         
         cs.add( new Callable<GenPolynomial<C>>() {
                     public GenPolynomial<C> call() {
                         GenPolynomial<C> g = e1.gcd(P,S);
                         if ( debug ) {
                            logger.info("GCDProxy done e1 " + e1);
                         }
                         return g;
                     }
                 }
                 );

         cs.add( new Callable<GenPolynomial<C>>() {
                     public GenPolynomial<C> call() {
                         GenPolynomial<C> g = e2.gcd(P,S);
                         if ( debug ) {
                            logger.info("GCDProxy done e2 " + e2);
                         }
                         return g;
                     }
                 }
                 );
        
         try {
             g = pool.invokeAny( cs );
         } catch (InterruptedException ignored) { 
         } catch (ExecutionException ignored) { 
         }
         return g;
     }


    /**
     * GenPolynomial greatest comon divisor.
     * Main entry driver method.
     * @param P GenPolynomial.
     * @param S GenPolynomial.
     * @return gcd(P,S).
     */
     public GenPolynomial<C> gcd2( final GenPolynomial<C> P, final GenPolynomial<C> S ) {
         GenPolynomial<C> g = null;

         Future<GenPolynomial<C>> f0;
         Future<GenPolynomial<C>> f1;
         f0 = pool.submit( new Callable<GenPolynomial<C>>() {
                               public GenPolynomial<C> call() {
                                     return e1.gcd(P,S);
                               }
                           }
                         );
         f1 = pool.submit( new Callable<GenPolynomial<C>>() {
                               public GenPolynomial<C> call() {
                                   return e2.gcd(P,S);
                               }
                           }
                         );

         while ( g == null ) {
             try {
                 if ( g == null && f0.isDone() /*&& ! f0.isCancelled()*/ ) {
                    g = f0.get(); 
                    f1.cancel(true);
                    if ( debug ) {
                       logger.info("GCDProxy done e1 " + e1);
                    }
                 }
                 if ( g == null && f1.isDone() /*&& ! f1.isCancelled()*/ ) {
                    g = f1.get(); 
                    f0.cancel(true);
                    if ( debug ) {
                       logger.info("GCDProxy done e2 " + e2);
                    }
                 }
                 if ( g == null ) {
                    Thread.currentThread().sleep(dauer);
                 }
             } catch (InterruptedException ignored) { 
             } catch (CancellationException ignored) { 
             } catch (ExecutionException ignored) { 
             }
         }
         if ( !f0.isDone() || !f1.isDone() ) {
            logger.info("GCDProxy not done, f0 = " + f0 + ", f1 = " + f1);
         }
         return g;
     }

}
