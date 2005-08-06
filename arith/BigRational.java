/*
 * $Id$
 */

package edu.jas.arith;

import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

import java.math.BigInteger;
import java.util.Random;
import java.io.Reader;

/**
 * BigRational class based on BigInteger implementing the RingElem interface
 * and with the familiar SAC ststic method names.
 * @author Heinz Kredel
 */

public class BigRational implements RingElem<BigRational>, 
                                    RingFactory<BigRational> {

    /**
     * Numerator and denominator.
     */
    protected final BigInteger num;
    protected final BigInteger den;

    /* from history: */
    private final static BigInteger IZERO = BigInteger.ZERO;
    private final static BigInteger IONE = BigInteger.ONE;

    /**
     * Constants 0 and 1.
     */
    public final static BigRational ZERO 
                        = new BigRational(BigInteger.ZERO);
    public final static BigRational ONE 
                        = new BigRational(BigInteger.ONE);

    /* from history: */
    private final static BigRational RNZERO = ZERO;
    private final static BigRational RNONE = ONE;

    private final static Random random = new Random();


    /**
     * Constructors for BigRational
     */

    protected BigRational(BigInteger n, BigInteger d) {
        // assume gcd(n,d) == 1
	num = n; 
        den = d;
    } 

    public BigRational(BigInteger n) {
	num = n; 
        den = IONE; // be aware of static initialization order
        //den = BigInteger.ONE;
    } 

    public BigRational(long n, long d) {
        BigInteger nu = BigInteger.valueOf(n);
        BigInteger de = BigInteger.valueOf(d);
        BigRational r = RNRED(nu,de);
	num = r.num; 
        den = r.den;
    } 

    public BigRational(long n) {
	num = BigInteger.valueOf(n); 
        den = IONE;
    } 

    public BigRational() {
	num = IZERO; den = IONE;
    } 

    public BigRational(String s) throws NumberFormatException {
        if ( s == null ) {
           num = IZERO; den = IONE;
           return;
	}
        if ( s.length() == 0) {
           num = IZERO; den = IONE;
           return;
	}
	BigInteger n;
	BigInteger d;
	int i = s.indexOf('/');
	if ( i < 0 ) {
	    num = new BigInteger( s );
            den = BigInteger.ONE;
            return;
	} else {
	    n = new BigInteger( s.substring(0,i) );
	    d = new BigInteger( s.substring( i+1, s.length() ) );
            BigRational r = RNRED( n, d );
	    num = r.num;
	    den = r.den;
	    return;
	}
    } 

    public BigRational clone() {
        return new BigRational( num, den );
    }

    public BigRational copy(BigRational c) {
        return new BigRational( c.num, c.den );
    }

    public BigInteger numerator() {
        return num;
    } 

    public BigInteger denominator() {
        return den;
    } 

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append(num);
	if ( ! den.equals(BigInteger.ONE) ) {
           s.append("/").append(den);
        }
        return s.toString();
    } 

    public BigRational getZERO() {
        return ZERO;
    }

    public BigRational getONE() {
        return ONE;
    }

    public BigRational fromInteger(BigInteger a) {
	return new BigRational(a);
    }

    public static BigRational valueOf(BigInteger a) {
	return new BigRational(a);
    }

    public BigRational fromInteger(long a) {
	return new BigRational(a);
    }

    public static BigRational valueOf(long a) {
	return new BigRational(a);
    }

    public boolean isZERO() {
	return num.equals( BigInteger.ZERO );
    }

    public boolean isONE() {
	return num.equals( den );
    }

    public boolean isUnit() {
	return ( ! isZERO() );
    }

    public boolean equals( Object b) {
	if ( ! ( b instanceof BigRational ) ) {
           return false;
        }
	BigRational br = (BigRational) b;
	return    num.equals( br.num ) 
               && den.equals( br.den );
    }

    public int hashCode() {
        return 37 * num.hashCode() + den.hashCode();
    }


/** Rational number reduction to lowest terms.  A and B are integers,
B non-zero.  R is the rational number A/B in canonical form. */

  public static BigRational RNRED(BigInteger n, BigInteger d) {
      BigInteger num;
      BigInteger den;
      if ( n.equals(IZERO) ) {
	  num = n; den = IONE;
          return new BigRational(num,den);
      }
      BigInteger C = n.gcd(d);
      num = n.divide(C);
      den = d.divide(C);
      if ( den.signum() < 0 ) {
        num = num.negate(); den = den.negate();
      }
      return new BigRational(num,den);
  } 


/** Rational number absolute value.  R is a rational number.  S is the
absolute value of R. */

  public BigRational abs() {
      if ( RNSIGN( this ) >= 0 ) {
	  return this;
      } else {
	  return RNNEG(this);
      }
  }

  public static BigRational RNABS(BigRational R) {
      if ( R == null ) return null;
      return R.abs();
  }


/** Rational number comparison.  R and S are rational numbers.
t=SIGN(R-S).*/

  public int compareTo(BigRational S) {
      BigInteger J2Y;
      BigInteger J3Y;
      BigInteger R1;
      BigInteger R2;
      BigInteger S1;
      BigInteger S2;
      int J1Y;
      int SL;
      int TL;
      int RL;
      if ( this.equals( ZERO ) ) {
        return - RNSIGN( S );
      }
      if ( S.equals( ZERO ) ) {
        return RNSIGN( this );
      }
      R1 = num; //this.numerator(); 
      R2 = den; //this.denominator();
      S1 = S.num;
      S2 = S.den;
      RL = R1.signum();
      SL = S1.signum();
      J1Y = (RL - SL);
      TL = (J1Y / 2);
      if ( TL != 0 ) {
        return TL;
      }
      J3Y = R1.multiply( S2 );
      J2Y = R2.multiply( S1 );
      TL = J3Y.compareTo( J2Y );
      return TL;
  }

  public static int RNCOMP(BigRational R, BigRational S) {
      if ( R == null ) return Integer.MAX_VALUE;
      return R.compareTo(S);
  }


/** Rational number denominator.  R is a rational number.  b is the
denominator of R, a positive integer. */

  public static BigInteger RNDEN(BigRational R) {
      if ( R == null ) return null;
      return R.den;
  }


/** Rational number difference.  R and S are rational numbers.  T=R-S. */

  public BigRational subtract(BigRational S) {
      BigRational J1Y;
      BigRational T;
      J1Y = RNNEG( S );
      T = RNSUM( this, J1Y );
      return T;
  }

  public static BigRational RNDIF(BigRational R, BigRational S) {
      if ( R == null ) return S.negate();
      return R.subtract(S);
  }


/** Rational number decimal write.  R is a rational number.  n is a
non-negative integer.  R is approximated by a decimal fraction D with
n decimal digits following the decimal point and D is written in the
output stream.  The inaccuracy of the approximation is at most
(1/2)*10**-n.  If ABS(D) is greater than ABS(R) then the last digit is
followed by a minus sign, if ABS(D) is less than ABS(R) then by a
plus sign. */

  public static void RNDWR(BigRational R, int NL) {             
      BigInteger num = R.num;
      BigInteger den = R.den;
      /* BigInteger p = new BigInteger("10");
         p = p.pow(NL);
      */
      double n = num.doubleValue();
      double d = den.doubleValue();
      double r = n/d;
      System.out.print( String.valueOf( d ) );
      return;
  }


/** Rational number from integer.  A is an integer.  R is the rational
number A/1. */

  public static BigRational RNINT(BigInteger A) {
      BigRational R = new BigRational( A );
      return R;
  }


/** Rational number inverse.  R is a non-zero rational number.  S=1/R. */

  public BigRational inverse() {
      BigInteger R1 = num; //R.nominator();
      BigInteger R2 = den; //R.denominator();
      BigInteger S1;
      BigInteger S2;
      if ( R1.signum() >= 0 ) {
        S1 = R2;
        S2 = R1;
      } else {
        S1 = R2.negate();
        S2 = R1.negate();
      }
      return new BigRational(S1,S2);
  }

  public static BigRational RNINV(BigRational R) {
      if ( R == null ) return null;
      return R.inverse();
  }


/** Rational number negative.  R is a rational number.  S=-R. */

  public BigRational negate() {
      BigInteger n = num.negate();
      return new BigRational( n, den );
  }

  public static BigRational RNNEG(BigRational R) {
      if ( R == null ) return null;
      return R.negate();
  }


/** Rational number numerator.  R is a rational number.  a is the
numerator of R, an integer. */

  public static BigInteger RNNUM(BigRational R) {
      if ( R == null ) return null;
      return R.num;
  }


/** Rational number product.  R and S are rational numbers.  T=R*S. */

  public BigRational multiply(BigRational S) {
      BigInteger D1 = null;
      BigInteger D2 = null;
      BigInteger R1 = null;
      BigInteger R2 = null;
      BigInteger RB1 = null;
      BigInteger RB2 = null;
      BigInteger S1 = null;
      BigInteger S2 = null;
      BigInteger SB1 = null;
      BigInteger SB2 = null;
      BigRational T;
      BigInteger T1;
      BigInteger T2;
      if ( this.equals( ZERO ) || S.equals( ZERO ) ) {
        T = ZERO;
        return T;
      }
      R1 = num; //this.numerator(); 
      R2 = den; //this.denominator();
      S1 = S.num;
      S2 = S.den;
      if ( R2.equals( IONE ) && S2.equals( IONE ) ) {
        T1 = R1.multiply( S1 );
        T = new BigRational( T1, IONE );
        return T;
      }
      if ( R2.equals( IONE ) ) {
        D1 = R1.gcd( S2 ); 
        RB1 = R1.divide( D1 );
        SB2 = S2.divide( D1 );
        T1 = RB1.multiply( S1 );
        T = new BigRational( T1, SB2 );
        return T;
      }
      if ( S2.equals( IONE ) ) {
        D2 = S1.gcd( R2 );
        SB1 = S1.divide( D2 );
        RB2 = R2.divide( D2 );
        T1 = SB1.multiply( R1 );
        T = new BigRational( T1, RB2 );
        return T;
      }
      D1 = R1.gcd( S2 ); 
      RB1 = R1.divide( D1 );
      SB2 = S2.divide( D1 );
      D2 = S1.gcd( R2 );
      SB1 = S1.divide( D2 );
      RB2 = R2.divide( D2 );
      T1 = RB1.multiply( SB1 );
      T2 = RB2.multiply( SB2 );
      T = new BigRational( T1, T2 );
      return T;
  }

  public static BigRational RNPROD(BigRational R, BigRational S) {             
      if ( R == null ) return S;
      return R.multiply(S);
  }


/** Rational number quotient.  R and S are rational numbers, S non-zero.
T=R/S. */


  public BigRational divide(BigRational S) {
      BigRational T;
      T = RNPROD( this, (BigRational)S.inverse() );
      return T;
  }

  public static BigRational RNQ(BigRational R, BigRational S) {
      if ( R == null ) return S.inverse();
      return R.divide( S );
  }


/* Rational number remainder.  R and S are rational numbers, S non-zero.
T=0=R-(R/S)*S. */

  public BigRational remainder(BigRational S) {
      if ( S.isZERO() ) {
          throw new RuntimeException("division by zero");
      }
      return ZERO;
  }


/** Rational number, random.  n is a positive beta-integer.  Random
integers A and B are generated using IRAND(n).  Then R=A/(ABS(B)+1),
reduced to lowest terms. */

  public BigRational random(int n) {
      return RNRAND( n );
  }

  public static BigRational RNRAND(int NL) {             
      BigInteger A;
      BigInteger B;
      BigRational R;
      A = new BigInteger( NL, random );
      B = new BigInteger( NL, random );
      B = B.abs();
      B = B.add( IONE );
      R = RNRED( A, B );
      return R;
  }


/** Rational number sign.  R is a rational number.  s=SIGN(R). */

  public int signum() {
      int SL;
      if ( this.equals(ZERO) ) {
        SL = 0;
      } else {
        SL = num.signum();
      }
      return SL;
  }

  public static int RNSIGN(BigRational R) {
      if ( R == null ) return Integer.MAX_VALUE;
      return R.signum();
  }


/** Rational number sum.  R and S are rational numbers.  T=R+S. */

  public BigRational add(BigRational S) {
      BigInteger D = null;
      BigInteger E;
      BigInteger J1Y;
      BigInteger J2Y;
      BigInteger R1 = null;
      BigInteger R2 = null;
      BigInteger RB2 = null;
      BigInteger S1 = null;
      BigInteger S2 = null;
      BigInteger SB2 = null;
      BigRational T;
      BigInteger T1;
      BigInteger T2;
      if ( this.equals( ZERO ) ) {
        T = S;
        return T;
      }
      if ( S.equals( ZERO ) ) {
        T = this;
        return T;
      }
      R1 = num; //this.numerator(); 
      R2 = den; //this.denominator();
      S1 = S.num;
      S2 = S.den;
      if ( R2.equals( IONE ) && S2.equals( IONE ) ) {
        T1 = R1.add( S1 );
        T = new BigRational( T1, IONE );
        return T;
      }
      if ( R2.equals( IONE ) ) {
        T1 = R1.multiply( S2 );
        T1 = T1.add( S1 );
        T = new BigRational( T1, S2 );
        return T;
      }
      if ( S2.equals( IONE ) ) {
        T1 = R2.multiply( S1 );
        T1 = T1.add( R1 );
        T = new BigRational( T1, R2 );
        return T;
      }
      D = R2.gcd( S2 );
      RB2 = R2.divide( D );
      SB2 = S2.divide( D );
      J1Y = R1.multiply( SB2 );
      J2Y = RB2.multiply( S1 );
      T1 = J1Y.add( J2Y );
      if ( T1.equals( IZERO ) ) {
        T = ZERO;
        return T;
      }
      if ( ! D.equals( IONE ) ) {
        E = T1.gcd( D );
        if ( ! E.equals( IONE ) ) {
          T1 = T1.divide( E );
          R2 = R2.divide( E );
        }
      }
      T2 = R2.multiply( SB2 );
      T = new BigRational( T1, T2 );
      return T;
  }

  public static BigRational RNSUM(BigRational R, BigRational S) {
      if ( R == null ) return S;
      return R.add( S );
  }

    public BigRational parse(String s) {
        return new BigRational(s);
    }

    public BigRational parse(Reader r) {
        return ZERO;
    }

}

