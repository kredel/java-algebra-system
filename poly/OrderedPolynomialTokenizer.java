/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Comparator;
import java.util.Set;
import java.util.Map;
//import java.util.List;
import java.util.ListIterator;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Iterator;
import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import edu.jas.arith.BigRational;

/**
 * OrderedMapPolynomial Tokenizer. 
 * Used to read rational polynomials and lists from input streams.
 * @author Heinz Kredel
 */

public class OrderedPolynomialTokenizer  {

    private String[] vars;
    private TermOrder tord;
    private Reader in;
    private StreamTokenizer tok;
    private boolean debug = false;

    /**
     * constructors
     */

    public OrderedPolynomialTokenizer(Reader r) {
	this(null,new TermOrder(),r);
    }

    public OrderedPolynomialTokenizer(String[] v, TermOrder to, Reader r) {
	vars = v;
	tord = to;
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

    public OrderedPolynomialTokenizer(String[] v, TermOrder to) {
	this(v,to, new BufferedReader( new InputStreamReader( System.in ) ) );
    }


    /**
     * parsing method for RatOrderedMapPolynomial
     * syntax 
     */

    public RatOrderedMapPolynomial nextRatPolynomial() throws IOException {
        if (debug) System.out.println("torder = " + tord);
	RatOrderedMapPolynomial a = new RatOrderedMapPolynomial(tord);
	ExpVector leer = new ExpVector(vars.length);
	RatOrderedMapPolynomial a1 = (RatOrderedMapPolynomial) a.add(BigRational.ONE,leer);

        if (debug) System.out.println("a = " + a);
        if (debug) System.out.println("a1 = " + a1);
	RatOrderedMapPolynomial b = a1;
	RatOrderedMapPolynomial c;
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
		b = (RatOrderedMapPolynomial) b.negate(); //RatOrderedMapPolynomial.DIPNEG( b );
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
		     // c = new RatOrderedMapPolynomial(r,leer);
		     b = (RatOrderedMapPolynomial) b.multiply(r,leer); //RatOrderedMapPolynomial.DIRPPR( b, c ); 
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
		     //c = new RatOrderedMapPolynomial(r,e);
		     b = (RatOrderedMapPolynomial) b.multiply(r,e); //RatOrderedMapPolynomial.DIRPPR( b, c ); 
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
                 if (debug) System.out.println("b = " + b);
		 a = (RatOrderedMapPolynomial) a.add(b); //RatOrderedMapPolynomial.DIPSUM( a, b );
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
	if (debug) System.out.println("b = " + b);
	a = (RatOrderedMapPolynomial) a.add(b); //RatOrderedMapPolynomial.DIPSUM( a, b );
	if (debug) System.out.println("a = " + a);
	// b = a1;
	return a;
    }


    /**
     * parsing method for variable exponent
     * syntax: ^long 
     */
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


    /**
     * parsing method for variable list
     * syntax: (a, b c, de)
     */
    public String[] nextVariableList() throws IOException {
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


    /**
     * parsing method for term order name
     * syntax: termOrderName = L, IL, LEX, G, IG, GRLEX
     */
    public TermOrder nextExpOrd() throws IOException {
	int evord = ExpVector.DEFAULT_EVORD;
	int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) { /* nop */
	}
        if ( tt == StreamTokenizer.TT_WORD ) {
	    //	   System.out.println("TT_WORD: " + tok.sval);
	   if ( tok.sval != null ) {
	      if ( tok.sval.equalsIgnoreCase("L") ) 
                 evord = ExpVector.INVLEX;
	      if ( tok.sval.equalsIgnoreCase("IL") ) 
                 evord = ExpVector.INVLEX;
	      if ( tok.sval.equalsIgnoreCase("LEX") ) 
                 evord = ExpVector.LEX;
	      if ( tok.sval.equalsIgnoreCase("G") ) 
                 evord = ExpVector.IGRLEX;
	      if ( tok.sval.equalsIgnoreCase("IG") ) 
                 evord = ExpVector.IGRLEX;
	      if ( tok.sval.equalsIgnoreCase("GRLEX") ) 
                 evord = ExpVector.GRLEX;
	   }
	}
	return new TermOrder(evord);
    }

    /**
     * parsing method for polynomial list
     * syntax: ( p1, p2, p3, ..., pn )
     */
    public ArrayList nextPolynomialList() throws IOException {
	RatOrderedMapPolynomial a;
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

    /**
     * parsing method for polynomial set
     * syntax: varList termOrderName polyList
     */
    public PolynomialList  nextPolynomialSet() throws IOException {
	ArrayList s = null;
	vars = nextVariableList();
               System.out.print("vars ="); 
	       for (int i = 0; i < vars.length ;i++) {
                   System.out.print( " "+vars[i] ); 
	       }
               System.out.println(); 
	tord = nextExpOrd();
               System.out.println("tord = " + tord); 
	s = nextPolynomialList();
	return new PolynomialList(vars,tord,s);
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
