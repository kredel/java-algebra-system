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

import org.apache.log4j.Logger;

import edu.jas.arith.BigRational;

/**
 * OrderedMapPolynomial Tokenizer. 
 * Used to read rational polynomials and lists from input streams.
 * @author Heinz Kredel
 */

public class OrderedPolynomialTokenizer  {

    private static final Logger logger = Logger.getLogger(OrderedPolynomialTokenizer.class);

    private String[] vars;
    private TermOrder tord;
    private Reader in;
    private StreamTokenizer tok;
    private boolean debug = false; // even more debugging

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
	// tok.eolIsSignificant(true); no more
	tok.eolIsSignificant(false);
        tok.wordChars('0','9');
	tok.wordChars('a', 'z');
	tok.wordChars('A', 'Z');
	tok.wordChars('/', '/');
	tok.wordChars(128 + 32, 255);
	tok.whitespaceChars(0, ' ');
	tok.commentChar('#');
	tok.quoteChar('"');
	tok.quoteChar('\'');
	//tok.slashStarComments(true); does not work

    }

    public OrderedPolynomialTokenizer(String[] v, TermOrder to) {
	this(v,to, new BufferedReader( new InputStreamReader( System.in ) ) );
    }


    /**
     * parsing method for RatOrderedMapPolynomial
     * syntax 
     */

    public RatOrderedMapPolynomial nextRatPolynomial() throws IOException {
        logger.debug("torder = " + tord);
	RatOrderedMapPolynomial a = new RatOrderedMapPolynomial(tord);
	ExpVector leer = new ExpVector(vars.length);
	RatOrderedMapPolynomial a1 = (RatOrderedMapPolynomial) a.add(BigRational.ONE,leer);

        if (debug) logger.debug("a = " + a);
        logger.debug("a1 = " + a1);
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
            logger.debug("while tt = " + tok);
	    if ( tt == StreamTokenizer.TT_EOF ) break;
	    switch ( tt ) {
	    case ')': 
	    case ',': 
		 return a;
	    case '-': 
		 b = (RatOrderedMapPolynomial) b.negate(); 
	    case '+': 
	    case '*': 
                 tt = tok.nextToken();
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
		     b = (RatOrderedMapPolynomial) b.multiply(r,leer); 
                     tt = tok.nextToken();
	             if (debug) logger.debug("tt,digit = " + tok);
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
		     //  System.out.println("ie: " + ie);
		     r = BigRational.RNONE;
		     e = new ExpVector( vars.length, ix, ie);
		     //c = new RatOrderedMapPolynomial(r,e);
		     b = (RatOrderedMapPolynomial) b.multiply(r,e); 
                     tt = tok.nextToken();
	             if (debug) logger.debug("tt,letter = " + tok);
		 }
		 break;
	    default: //skip 
	    }
            if ( tt == StreamTokenizer.TT_EOF ) break;
	    // complete polynomial
            tok.pushBack();
	    switch ( tt ) {
	    case '-': 
	    case '+': 
	    case '*': 
	    case ')': 
	    case ',': 
                 logger.debug("b, = " + b);
		 a = (RatOrderedMapPolynomial) a.add(b); 
		 b = a1;
		 break;
	    case '\n':
                 tt = tok.nextToken();
	         if (debug) logger.debug("tt,nl = " + tt);
 	    default: // skip ?
		 if (debug) logger.debug("default: " + tok);
	    }
	}
	if (debug) logger.debug("b = " + b);
	a = (RatOrderedMapPolynomial) a.add(b); 
	logger.debug("a = " + a);
	// b = a1;
	return a;
    }


    /**
     * parsing method for variable exponent
     * syntax: ^long | **long
     */
    public long nextExponent() throws IOException {
	// syntax: ^long | **long
	long e = 1;
	char first;
	int tt;
        tt = tok.nextToken();
	if ( tt == '^' ) {
           if (debug) logger.debug("exponent ^");
           tt = tok.nextToken();
    	   if ( tok.sval != null ) {
              first = tok.sval.charAt(0);
	      if ( digit(first) ) {
	          e = Long.parseLong( tok.sval );
		  return e;
	      }
	   }
	}
	if ( tt == '*' ) {
           tt = tok.nextToken();
           if ( tt == '*' ) {
              if (debug) logger.debug("exponent **");
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
	}
 	tok.pushBack();
	return e;
    }


    /**
     * parsing method for comments 
     * syntax: (* comment *) | /_* comment *_/ without _
     * @false does not work with this pushBack()
     */
    public String nextComment() throws IOException {
	// syntax: (* comment *) | /* comment */ 
	StringBuffer c = new StringBuffer();
	int tt;
        if (debug) logger.debug("comment: " + tok);
        tt = tok.nextToken();
        if (debug) logger.debug("comment: " + tok);
	if ( tt == '(' ) {
           tt = tok.nextToken();
           if (debug) logger.debug("comment: " + tok);
           if ( tt == '*' ) {
              if (debug) logger.debug("comment: ");
	      while (true) { 
                 tt = tok.nextToken();
                 if ( tt == '*' ) {
                    tt = tok.nextToken();
                    if ( tt == ')' ) {
			return c.toString();
		    } 
		    tok.pushBack();
	         }
		 c.append(tok.sval);
	      }
	   } 
           tok.pushBack();
           if (debug) logger.debug("comment: " + tok);
	}
 	tok.pushBack();
        if (debug) logger.debug("comment: " + tok);
	return c.toString();
    }

    // unused
    public void nextComma() throws IOException {
	int tt;
    	if ( tok.ttype == ',' ) {
           if (debug) logger.debug("comma: ");
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
	   logger.debug("variable list");
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
	logger.debug("polynomial list");
        while ( true ) {
               tt = tok.nextToken();
               if ( tok.ttype == ',' ) continue;
               if ( tt == '(' ) {
	          a = nextRatPolynomial();
                  tt = tok.nextToken();
                  if ( tok.ttype != ')' ) tok.pushBack();
	       } else { tok.pushBack();
                  a = nextRatPolynomial();
	       }
               logger.info("next = " + a); 
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
    public PolynomialList nextPolynomialSet() throws IOException {
	ArrayList s = null;
	//String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

	vars = nextVariableList();
               String dd = "vars ="; 
	       for (int i = 0; i < vars.length ;i++) {
                   dd+= " "+vars[i]; 
	       }
               logger.info(dd); 
	tord = nextExpOrd();
               logger.info("tord = " + tord); 
	s = nextPolynomialList();
               logger.info("s = " + s); 
	// comments += nextComment();
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
