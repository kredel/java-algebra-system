/*
 * $Id$
 */

package edu.jas.poly;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.util.ArrayList;

import edu.jas.arith.BigRational;

/**
 * Polynomial Tokenizer. 
 * Used to read polynomials and lists from input streams.
 * @author Heinz Kredel
 */

public class PolynomialTokenizer  {

    private String[] vars;
    private int evord;
    private Reader in;
    private StreamTokenizer tok;
    private boolean debug = false;

    public PolynomialTokenizer(Reader r) {
	this(null,RatPolynomial.DEFAULT_EVORD,r);
    }

    public PolynomialTokenizer(String[] v, int o, Reader r) {
	vars = v;
	evord = o;
	in = r;

	tok = new StreamTokenizer( r );
	tok.resetSyntax();
	tok.eolIsSignificant(true);
        tok.wordChars('0','9');
	tok.wordChars('a', 'z');
	tok.wordChars('A', 'Z');
	tok.wordChars('/', '/');
	tok.wordChars(128 + 32, 255);
	tok.whitespaceChars(0, ' ');
	//	tok.commentChar('/');
	tok.quoteChar('"');
	tok.quoteChar('\'');

    }

    public PolynomialTokenizer(String[] v, int o) {
	this(v,o, new BufferedReader( new InputStreamReader( System.in ) ) );
    }

    public RatPolynomial nextRatPolynomial() throws IOException {
	RatPolynomial a = new RatPolynomial(vars.length,evord);
	ExpVector leer = new ExpVector(vars.length);
	RatPolynomial a1 = new RatPolynomial( BigRational.RNONE, leer );
	RatPolynomial b = a1;
	RatPolynomial c;
	int tt, oldtt;
	String rat = "";
	char first;
	BigRational r;
	ExpVector e;
	int ix;
	long ie;
	while ( true ) {
	    // next input. determine next action
	    tt = tok.nextToken();
            if (debug) System.out.println("tt = " + tt);
	    if ( tt == StreamTokenizer.TT_EOF ) break;
	    switch ( tt ) {
	    case ')': 
	    case ',': 
		 //System.out.println(",: ");
		 return a;
	    case '-': 
		 //System.out.println("-: " + tok.sval);
                 b = RatPolynomial.DIRPNG( b );
	    case '+': 
		 //System.out.println("+: " + tok.sval);
	    case '*': 
		 //System.out.println("*: " + tok.sval);
                 tt = tok.nextToken();
	         if (debug) System.out.println("tt = " + tt);
		 break;
	    default: // skip
	    }
	    // read coefficient, monic monomial and polynomial
	    if ( tt == StreamTokenizer.TT_EOF ) break;
	    switch ( tt ) {
	    case StreamTokenizer.TT_WORD: 
		 //System.out.println("TT_WORD: " + tok.sval);
		 if ( tok.sval == null ) break;
		 // read coefficient
		 first = tok.sval.charAt(0);
		 if ( digit(first) ) {
		     r = new BigRational( tok.sval );
		     // ie = nextExponent();
		     // r = r^ie;
		     c = new RatPolynomial(r,leer);
		     b = RatPolynomial.DIRPPR( b, c ); 
                     tt = tok.nextToken();
	             if (debug) System.out.println("tt = " + tt);
		 } 
                 if ( tt == StreamTokenizer.TT_EOF ) break;
		 // read polynomial (not jet implemented)
		 if ( tok.sval == null ) break;
		 // read monomial 
		 first = tok.sval.charAt(0);
		 if ( letter(first) ) {
		     ix = indexVar( tok.sval );
		     if ( ix < 0 ) break;
		     //  System.out.println("ix: " + ix);
		     ie = nextExponent();
		     r = BigRational.RNONE;
		     e = new ExpVector( vars.length, ix, ie);
		     c = new RatPolynomial(r,e);
		     b = RatPolynomial.DIRPPR( b, c ); 
                     tt = tok.nextToken();
	             if (debug) System.out.println("tt = " + tt);
		 }
		 break;
	    default: //skip 
	    }
            if ( tt == StreamTokenizer.TT_EOF ) break;
	    // complete polynomial
            tok.pushBack();
	    switch ( tt ) {
	    case '-': 
		 //System.out.println("-: " + tok.sval);
	    case '+': 
		 //System.out.println("+: " + tok.sval);
	    case '*': 
		 //System.out.println("*: " + tok.sval);
	    case ')': 
		 //System.out.println("): ");
	    case ',': 
		 //System.out.println(",: ");
		 a = RatPolynomial.DIRPSM( a, b );
		 b = a1;
		 break;
	    case '\n':
		 //System.out.println("nl: " );
                 tt = tok.nextToken();
	         if (debug) System.out.println("tt = " + tt);
 	    default: // skip ?
		// System.out.println("default: " + tok);
	    }
	}
	a = RatPolynomial.DIRPSM( a, b );
	// b = a1;
	return a;
    }

    public long nextExponent() throws IOException {
	// syntax: ^long
	long e = 1;
	char first;
	int tt;
        tt = tok.nextToken();
	if ( tt == '^' ) {
           tt = tok.nextToken();
    	   if ( tok.sval != null ) {
              first = tok.sval.charAt(0);
	      if ( digit(first) ) {
	          e = Long.parseLong( tok.sval );
		  return e;
	      }
	   }
	}
 	tok.pushBack();
	return e;
    }

    // unused
    public void nextComma() throws IOException {
	int tt;
    	if ( tok.ttype == ',' ) {
           System.out.println("comma: ");
           tt = tok.nextToken();
	}
    }

    public String[] nextVariableList() throws IOException {
	// syntax: (a, b c, de)
	ArrayList l = new ArrayList();
	int tt;
        tt = tok.nextToken();
    	if ( tt == '(' ) {
	   //System.out.println("(: ");
           tt = tok.nextToken();
	   while ( true ) {
                 if ( tt == StreamTokenizer.TT_EOF ) break;
                 if ( tt == ')' ) break;
		 if ( tt == StreamTokenizer.TT_WORD ) {
		     //System.out.println("TT_WORD: " + tok.sval);
		     l.add( tok.sval );
		 }
                 tt = tok.nextToken();
	   }
	}
	Object[] ol = l.toArray();
	String[] v = new String[ol.length];
	for (int i=0; i < v.length; i++ ) {
	    v[i] = (String) ol[i];
	}
	return v;
    }

    public int nextExpOrd() throws IOException {
	// syntax: ordName
	int evord = RatPolynomial.DEFAULT_EVORD;
	int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) return evord;
        if ( tt == StreamTokenizer.TT_WORD ) {
	    //	   System.out.println("TT_WORD: " + tok.sval);
	   if ( tok.sval == null ) return evord;
	   if ( tok.sval.equalsIgnoreCase("L") ) 
              return ExpVector.INVLEX;
	   if ( tok.sval.equalsIgnoreCase("IL") ) 
              return ExpVector.INVLEX;
	   if ( tok.sval.equalsIgnoreCase("LEX") ) 
              return ExpVector.LEX;
	   if ( tok.sval.equalsIgnoreCase("G") ) 
              return ExpVector.IGRLEX;
	   if ( tok.sval.equalsIgnoreCase("IG") ) 
              return ExpVector.IGRLEX;
	   if ( tok.sval.equalsIgnoreCase("GRLEX") ) 
              return ExpVector.GRLEX;
	}
	return evord;
    }

    public ArrayList nextPolynomialList() throws IOException {
	// syntax: ( p1, p2, p3, ..., pn )
	RatPolynomial a;
        ArrayList L = new ArrayList();
	int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) return L;
        if ( tt != '(' ) return L;
        while ( true ) {
	       a = nextRatPolynomial();
               System.out.println("a = " + a); 
	       L.add( a );
               if ( tok.ttype == StreamTokenizer.TT_EOF ) break;
               if ( tok.ttype == ')' ) break;
         }
         return L;
    }

    public PolynomialList  nextPolynomialSet() throws IOException {
	// syntax: varList evOrd polyList
	ArrayList s = null;
	vars = nextVariableList();
               System.out.println("vars = " + vars); 
	evord = nextExpOrd();
               System.out.println("evord = " + evord); 
	s = nextPolynomialList();
	return new PolynomialList(vars,evord,s);
    }

    private boolean digit(char x) {
	return '0' <= x && x <= '9';
    }

    private boolean letter(char x) {
	return ( 'a' <= x && x <= 'z' ) || ( 'A' <= x && x <= 'Z' );
    }

    private int indexVar(String x) {
	int i;
	for ( i = 0; i < vars.length; i++ ) { 
	    if ( x.equals( vars[i] ) ) return vars.length-i-1;
	}
	return -1; // not found
    }

}
