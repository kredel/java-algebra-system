/*
 * $Id$
 */

package edu.jas.ps;


import java.util.Map;
import java.util.Iterator;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import edu.jas.arith.BigInteger;


/**
 * Test power series implementations.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class PowerSeriesTest {

    public static void main(String[] args) {
        example1();
        example2();
        example3();
        example4();
        example4();
        example5();
        example6();
        example31();
        example32();
        example7();
        example8();
        example9();
        example10();
        example11();
    }

    static LinkedPowerSeries<BigInteger> integersFrom(final int start) {
        return new LinkedPowerSeries<BigInteger>( new BigInteger(start),
                                                  new LinkedPowerSeries.LazyTail<BigInteger>() {
                                                      public LinkedPowerSeries<BigInteger> eval() {
                                                             return integersFrom( start+1 ); 
                                                      }
                                                  }
                                                );
    } 

    static ArrayPowerSeries<BigInteger> integersPS(final int start) {
        return new ArrayPowerSeries<BigInteger>( 
                                                new ArrayPowerSeries.Coefficients<BigInteger>() {
                                                    public BigInteger get(int i) {
                                                        return new BigInteger(i); 
                                                    }
                                                  }
                                                );
    } 


    static LinkedPowerSeries<BigInteger> fibonacci(final int a, final int b) {
        return new LinkedPowerSeries<BigInteger>( new BigInteger(a),
                                                  new LinkedPowerSeries.LazyTail<BigInteger>() {
                                                      public LinkedPowerSeries<BigInteger> eval() {
                                                          return fibonacci( b, a+b ); 
                                                      }
                                                  }
                                                );
    } 

    //----------------------


    static class Sum<C extends RingElem<C>> implements BinaryFunctor<C,C,C> {
        public C eval(C c1, C c2) {
            return c1.sum(c2);
        }
    }


    static class Odds<C extends RingElem<C>> implements Selector<C> {
	C two;
	RingFactory<C> fac;
	public Odds(RingFactory<C> fac) {
	    this.fac = fac;
            two = fac.fromInteger(2);
	    //System.out.println("two = " + two);
	}
        public boolean select(C c) {
	    //System.out.print("c = " + c);
	    if ( c.remainder( two ).isONE() ) {
	        //System.out.println(" odd");
		return true;
	    } else { 
                //System.out.println(" even");
		return false;
            }
        }
    }

    //----------------------


    public static void example1() {
        LinkedPowerSeries<BigInteger> integers = integersFrom(0);

        LinkedPowerSeries<BigInteger> s = integers;
        for (int i = 0; i < 20; i++ ) {
            BigInteger c = s.leadingCoefficient();
            System.out.print(c.toString() + ", ");
            s = s.reductum();
        }
        System.out.println("...");
    }


    public static void example2() {
        ArrayPowerSeries<BigInteger> integers = integersPS(0);

        ArrayPowerSeries<BigInteger> s = integers;
        for (int i = 0; i < 20; i++ ) {
            BigInteger c = s.leadingCoefficient();
            System.out.print(c.toString() + ", ");
            s = s.reductum();
        }
        System.out.println("...");
    }


    public static void example3() {
        LinkedPowerSeries<BigInteger> integers = integersFrom(0);
        System.out.println("integers = " + integers);
    }


    public static void example31() {
        LinkedPowerSeries<BigInteger> fibs = fibonacci(0,1);
        System.out.println("fibs = " + fibs);
    }


    public static void example32() {
        LinkedPowerSeries<BigInteger> integers = integersFrom(0);
        LinkedPowerSeries<BigInteger> squares = integers.map(
                            new UnaryFunctor<BigInteger,BigInteger>() {
                                public BigInteger eval(BigInteger x) {
                                       return x.multiply(x);
                                }
                            }
                                                             );
        System.out.println("squares = " + squares);
    }


    public static void example4() {
        ArrayPowerSeries<BigInteger> integers = integersPS(0);
        System.out.println("integers = " + integers);
    }


    public static void example5() {
        final BigInteger a = new BigInteger(1);
        final BigInteger b = new BigInteger(2);
        final BigInteger c = new BigInteger(3);
        PowerSeries<BigInteger> abc = LinkedPowerSeries.<BigInteger>fixPoint(
                                  new PowerSeriesMap<BigInteger>() {
                                      public PowerSeries<BigInteger> map(PowerSeries<BigInteger> ps) {
                                             return ps.prepend(c).prepend(b).prepend(a);
                                      } 
                                  }
                                                 );
        System.out.println("abc = " + abc);
    }


    public static void example6() {
        ArrayPowerSeries<BigInteger> integers;
        integers = ArrayPowerSeries.<BigInteger>fixPoint(
               new PowerSeriesMap<BigInteger>() {
                   public PowerSeries<BigInteger> map(PowerSeries<BigInteger> ps) {
                          return ps.map(
                                        new UnaryFunctor<BigInteger,BigInteger>() {
                                            public BigInteger eval( BigInteger s ) {
                                                   return s.sum( new BigInteger(1) );
                                            }
                                        }
                                         ).prepend(new BigInteger(0));
                   }
               }
							 );
        System.out.println("integers1 = " + integers);
        System.out.println("integers2 = " + integers);
    }


    public static void example7() {
        final BigInteger z = new BigInteger(0);
        final BigInteger one = new BigInteger(1);
        LinkedPowerSeries<BigInteger> fibs;
        fibs = LinkedPowerSeries.<BigInteger>fixPoint(
               new PowerSeriesMap<BigInteger>() {
                   public PowerSeries<BigInteger> map(PowerSeries<BigInteger> ps) {
                          return ps.prepend(z).zip(
                                                   new Sum<BigInteger>(),
                                                   ps.prepend(one).prepend(z)
                                                    );
                   }
               }
                                                      );
        System.out.println("fibs = " + fibs.toString(/*20*/));
    }


    public static void example8() {
        final BigInteger z = new BigInteger(0);
        final BigInteger one = new BigInteger(1);
        ArrayPowerSeries<BigInteger> fibs;
        fibs = ArrayPowerSeries.<BigInteger>fixPoint(
               new PowerSeriesMap<BigInteger>() {
                   public PowerSeries<BigInteger> map( PowerSeries<BigInteger> ps ) {
                          return ps.zip( new Sum<BigInteger>(),
                                         ps.prepend( one )
                                         ).prepend(z );
                          }
                   }
                                                      );
        System.out.println("fibs1 = " + fibs.toString(/*20*/));
        System.out.println("fibs2 = " + fibs.toString(/*20*/));
    }

    public static void example9() {
        ArrayPowerSeries<BigInteger> integers = integersPS(0);
        System.out.println("      integers = " + integers);
        ArrayPowerSeries<BigInteger> doubleintegers = integers.sum(integers);
        System.out.println("doubleintegers = " + doubleintegers);
        ArrayPowerSeries<BigInteger> nulls = integers.subtract(integers);
        System.out.println("null  integers = " + nulls);
	doubleintegers = integers.multiply(new BigInteger(2));
        System.out.println("doubleintegers = " + doubleintegers);
        nulls = integers.multiply(new BigInteger(0));
        System.out.println("null  integers = " + nulls);
        ArrayPowerSeries<BigInteger> odds = integers.select( new Odds<BigInteger>(new BigInteger()) );
        System.out.println("odd   integers = " + odds);
    }


    public static void example10() {
	final BigInteger fac = new BigInteger();
        ArrayPowerSeries<BigInteger> integers = integersPS(0);
        System.out.println("      integers = " + integers);
        ArrayPowerSeries<BigInteger> ONE = new ArrayPowerSeries<BigInteger>(
                   new ArrayPowerSeries.Coefficients<BigInteger>() {
                       public BigInteger get(int i) {
                           if ( i == 0 ) { 
                               return fac.getONE();
                           } else {
                               return fac.getZERO();
                           }
                       }
                   }//, null
                                                       );
        System.out.println("ONE  = " + ONE);
        ArrayPowerSeries<BigInteger> ZERO = new ArrayPowerSeries<BigInteger>(
                   new ArrayPowerSeries.Coefficients<BigInteger>() {
                       public BigInteger get(int i) {
                           return fac.getZERO();
                       }
                   }//, null
                                                           );
        System.out.println("ZERO = " + ZERO);
        ArrayPowerSeries<BigInteger> ints  = integers.multiply(ONE);
        System.out.println("integers       = " + ints);
        ArrayPowerSeries<BigInteger> nulls = integers.multiply(ZERO);
        System.out.println("null  integers = " + nulls);
        ArrayPowerSeries<BigInteger> nints = integers.negate();
        System.out.println("-integers      = " + nints);
        ArrayPowerSeries<BigInteger> one   = ONE.multiply(ONE);
        System.out.println("integers one   = " + one);
        ArrayPowerSeries<BigInteger> ints2 = integers.multiply(integers);
        System.out.println("integers 2     = " + ints2);
        ArrayPowerSeries<BigInteger> inv1  = ONE.inverse();
        System.out.println("integers inv1  = " + inv1);
        ArrayPowerSeries<BigInteger> int1  = integers.reductum();
        System.out.println("integers int1  = " + int1);
        ArrayPowerSeries<BigInteger> intinv = int1.inverse();
        System.out.println("integers intinv = " + intinv);
        ArrayPowerSeries<BigInteger> one1  = int1.multiply(intinv);
        System.out.println("integers one1  = " + one1);
        ArrayPowerSeries<BigInteger> ii    = intinv.inverse();
        System.out.println("integers ii    = " + ii);
        ArrayPowerSeries<BigInteger> rem   = integers.subtract(integers.divide(int1).multiply(int1));
        System.out.println("integers rem   = " + rem);
    }


    public static void example11() {
	final BigInteger fac = new BigInteger();
        ArrayPowerSeries<BigInteger> integers = integersPS(0);
        System.out.println("      integers = " + integers);
        ArrayPowerSeries<BigInteger> int2     = integers.multiply(new BigInteger(2));
        System.out.println("    2*integers = " + int2);
        System.out.println("integers < 2*integers  = " + integers.compareTo(int2));
        System.out.println("2*integers > integers  = " + int2.compareTo(integers));
        System.out.println("2*integers == integers = " + int2.equals(integers));
        System.out.println("integers == integers   = " + integers.equals(integers));
        System.out.println("integers.hashCode()    = " + integers.hashCode());
    }

}
