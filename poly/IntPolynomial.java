/*
 * $Id$
 */

package edu.jas.poly;

import java.util.TreeMap;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.SortedMap;
import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import edu.jas.arith.BigInteger;

public class IntPolynomial extends TreePolynomial {

    public IntPolynomial() { 
	super();
    }

    public IntPolynomial(int eo, Comparator ho, Comparator lo) { 
	super(eo,ho,lo);
    }

    public IntPolynomial(int r) { 
	super(r);
    }

    public IntPolynomial(int r, int eo) { 
	super(r,eo);
    }

    public IntPolynomial(TreeMap t, int eo) { 
	super(t,eo);
    }

    public IntPolynomial(Map t, int eo) { 
	super(t,eo);
    }

    public IntPolynomial(TreeMap t) { 
	super(t);
    }

    public IntPolynomial(Map t) { 
	super(t);
    }

    public IntPolynomial(TreeMap t, int eo, String[] v) { 
	super(t,eo,v);
    }

    public IntPolynomial(Map t, int eo, String[] v) { 
	super(t,eo,v);
    }

    public IntPolynomial(BigInteger a, ExpVector e) { 
	super(a,e);
    }

    public Object clone() { 
       Map c = null;
       if ( val instanceof TreeMap ) 
          c = (Map)((TreeMap)val).clone();
       if ( val instanceof LinkedHashMap ) 
          c = (Map)((LinkedHashMap)val).clone();
       return new IntPolynomial(c,evord,vars); 
    }

    public Polynomial getZERO() { 
       return ZERO;
    }

    public Polynomial getONE() { 
       return ONE; 
    }

    public static final IntPolynomial ZERO = new IntPolynomial();
    public static final IntPolynomial ONE = new IntPolynomial(
			                        BigInteger.ONE,
						new ExpVector()
                                                );

    public String toString(String[] v) { 
	StringBuffer erg = new StringBuffer();
        Set ent = val.entrySet();
        Iterator it = ent.iterator();
	if ( ! it.hasNext() ) return erg.toString();
        Map.Entry y = (Map.Entry) it.next();
        ExpVector f = (ExpVector) y.getKey(); 
	BigInteger a = (BigInteger) y.getValue();
	boolean neg = false;
        while ( true ) {
	    if ( neg ) {
	       erg.append( a.negate() );
	    } else {
              erg.append(a);
	    }
            neg = false;
	    erg.append(" " + f.toString(v));
	    if ( it.hasNext() ) {
                y = (Map.Entry) it.next();
	        f = (ExpVector) y.getKey(); 
	        a = (BigInteger) y.getValue();
		if ( a.signum() < 0 ) {
		   erg.append(" - ");
		   neg = true;
		} else {
                   erg.append(" + ");
		   neg = false;
		}
	    } else break; 
	} 
        return erg.toString(); 
    }


    /**
     * One ?.
     */

    // abstract public boolean isONE() 

    public static boolean DIIPON(IntPolynomial a) {  
	if ( a == null ) return false;
        return a.isONE();
    }

    /**
     * Random polynomial.
     */

    private final static Random random = new Random();

    public static IntPolynomial DIIRAS(int r, int k, int l, int e, float q) {  
        IntPolynomial x = new IntPolynomial(r);
        Map C = x.getMap();
	if ( ! (C instanceof SortedMap) ) C = new TreeMap( x.horder );
        for (int i = 0; i < l; i++ ) { 
            ExpVector U = ExpVector.EVRAND(r,e,q);
            BigInteger a = (BigInteger) BigInteger.ZERO.random(k);
	    if ( random.nextFloat() < 0.4f ) a = (BigInteger)a.negate();
	    if ( ! a.isZERO() ) C.put( U, a );
        }
	if ( x.getMap() instanceof LinkedHashMap) {
           C = new LinkedHashMap( C );
	   x.setMap( C );
	}
        return x;
    }

}
