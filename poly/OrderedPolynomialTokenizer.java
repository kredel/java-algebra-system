/*
 * $Id$
 */

package edu.jas.poly;

import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

import java.io.StreamTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.jas.arith.Coefficient;
import edu.jas.arith.BigRational;
import edu.jas.arith.BigInteger;
import edu.jas.arith.BigComplex;
import edu.jas.arith.BigQuaternion;

/**
 * OrderedMapPolynomial Tokenizer. 
 * Used to read rational polynomials and lists from input streams.
 * @author Heinz Kredel
 */

public class OrderedPolynomialTokenizer  {

    private static final Logger logger = Logger.getLogger(OrderedPolynomialTokenizer.class);

    private String[] vars;
    private TermOrder tord;
    private RelationTable table;
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
        tok.wordChars('/', '/'); // wg. rational numbers
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
                     // r = BigRational.RNONE;
                     e = new ExpVector( vars.length, ix, ie);
                     //c = new RatOrderedMapPolynomial(r,e);
                     b = (RatOrderedMapPolynomial) b.multiply(e); 
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
        List l = new ArrayList();
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
     * parsing method for coefficient ring
     * syntax: Rat | Q | Int | Z | Complex | C | Quat 
     */
    public Coefficient nextCoefficientRing() throws IOException {
        Coefficient coeff = null;
        int tt;
        tt = tok.nextToken();
        if ( tok.sval != null ) {
           if ( tok.sval.equalsIgnoreCase("Q") ) {
              coeff = new BigRational();
           }
           if ( tok.sval.equalsIgnoreCase("Rat") ) {
              coeff = new BigRational();
           }
           if ( tok.sval.equalsIgnoreCase("Int") ) {
              coeff = new BigInteger();
           }
           if ( tok.sval.equalsIgnoreCase("Z") ) {
              coeff = new BigInteger();
           }
           if ( tok.sval.equalsIgnoreCase("C") ) {
              coeff = new BigComplex();
           }
           if ( tok.sval.equalsIgnoreCase("Complex") ) {
              coeff = new BigComplex();
           }
           if ( tok.sval.equalsIgnoreCase("Quat") ) {
              coeff = new BigQuaternion();
           }
        } 
        if ( coeff == null ) {
           tok.pushBack();
           coeff = new BigRational();
        }
        return coeff;
    }


    /**
     * parsing method for weight list
     * syntax: (w1, w2, w3, ..., wn)
     */
    public long[] nextWeightList() throws IOException {
        List l = new ArrayList();
        long[] w = null;
        long e;
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '(' ) {
           logger.debug("weight list");
           tt = tok.nextToken();
           while ( true ) {
                 if ( tt == StreamTokenizer.TT_EOF ) break;
                 if ( tt == ')' ) break;
                 if ( tok.sval != null ) {
                    first = tok.sval.charAt(0);
                    if ( digit(first) ) {
                       e = Long.parseLong( tok.sval );
                       l.add( new Long(e) );
                       //System.out.println("w: " + e);
                    }
                 }
                 tt = tok.nextToken(); // also comma
           }
        }
        Object[] ol = l.toArray();
        w = new long[ ol.length ];
        for ( int i=0; i < w.length; i++ ) {
            w[i] = ((Long)ol[ ol.length-i-1 ]).longValue();
        }
        return w;
    }

    /**
     * parsing method for split index
     * syntax: |i|
     */
    public int nextSplitIndex() throws IOException {
        int e = -1; // =unknown
        char first;
        int tt;
        tt = tok.nextToken();
        if ( tt == '|' ) {
           logger.debug("split index");
           tt = tok.nextToken();
           if ( tt == StreamTokenizer.TT_EOF ) {
              return e;
           }
           if ( tok.sval != null ) {
              first = tok.sval.charAt(0);
              if ( digit(first) ) {
                 e = Integer.parseInt( tok.sval );
                 //System.out.println("w: " + i);
              }
              tt = tok.nextToken();
              if ( tt != '|' ) {
                 tok.pushBack();
              }
           }
        } else {
          tok.pushBack();
        }
        return e;
    }


    /**
     * parsing method for term order name
     * syntax: termOrderName = L, IL, LEX, G, IG, GRLEX
     *         W(weights)
     */
    public TermOrder nextTermOrder() throws IOException {
        int evord = TermOrder.DEFAULT_EVORD;
        int tt;
        tt = tok.nextToken();
        if ( tt == StreamTokenizer.TT_EOF ) { /* nop */
        }
        if ( tt == StreamTokenizer.TT_WORD ) {
           // System.out.println("TT_WORD: " + tok.sval);
           if ( tok.sval != null ) {
              if ( tok.sval.equalsIgnoreCase("L") ) {
                 evord = TermOrder.INVLEX;
              }
              if ( tok.sval.equalsIgnoreCase("IL") ) {
                 evord = TermOrder.INVLEX;
              }
              if ( tok.sval.equalsIgnoreCase("LEX") ) {
                 evord = TermOrder.LEX;
              }
              if ( tok.sval.equalsIgnoreCase("G") ) {
                 evord = TermOrder.IGRLEX;
              }
              if ( tok.sval.equalsIgnoreCase("IG") ) {
                 evord = TermOrder.IGRLEX;
              }
              if ( tok.sval.equalsIgnoreCase("GRLEX") ) {
                 evord = TermOrder.GRLEX;
              }
              if ( tok.sval.equalsIgnoreCase("W") ) {
                 long[] w = nextWeightList();
                 int s = nextSplitIndex();
                 if ( s <= 0 ) {
                    return new TermOrder( w );
                 } else {
                    return new TermOrder( w, s );
                 }
              }
           }
        }
        int s = nextSplitIndex();
        if ( s <= 0 ) {
           return new TermOrder( evord );
        } else {
           return new TermOrder( evord, evord, vars.length, s );
        }
    }

    /**
     * parsing method for polynomial list
     * syntax: ( p1, p2, p3, ..., pn )
     */
    public List nextPolynomialList() throws IOException {
        RatOrderedMapPolynomial a;
        List L = new ArrayList();
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
     * parsing method for solvable polynomial relation table
     * syntax: ( p_1, p_2, p_3, ..., p_n+3 )
     * semantics: p_n+1 * p_n+2 = p_n+3 
     */
    public RelationTable nextRelationTable() throws IOException {
        RelationTable table = new RelationTable();
        List rels = null;
        OrderedPolynomial p;
        int tt;
        tt = tok.nextToken();
        if ( tok.sval != null ) {
           if ( tok.sval.equalsIgnoreCase("RelationTable") ) {
              rels = nextPolynomialList();
           }
        } 
        if ( rels == null ) {
           tok.pushBack();
           return table;
        } 
        for ( Iterator it = rels.iterator(); it.hasNext(); ) {
            p = (OrderedPolynomial)it.next();
            ExpVector e = p.leadingExpVector();
            if ( it.hasNext() ) {
               p = (OrderedPolynomial)it.next();
               ExpVector f = p.leadingExpVector();
               if ( it.hasNext() ) {
                  p = (OrderedPolynomial)it.next();
                  p = new RatSolvableOrderedMapPolynomial(table,p);
                  table.update( e, f, p );
               }
            }
        }
        if ( debug ) {
           logger.info("table = " + table);
        }
        return table;
    }

    /**
     * parsing method for polynomial set
     * syntax: varList termOrderName polyList
     */
    public PolynomialList nextPolynomialSet() throws IOException {
        List s = null;
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        Coefficient coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
             String dd = "vars ="; 
             for (int i = 0; i < vars.length ;i++) {
                 dd+= " "+vars[i]; 
             }
             logger.info(dd); 

        tord = nextTermOrder();
             logger.info("tord = " + tord); 

        s = nextPolynomialList();
             logger.info("s = " + s); 
        // comments += nextComment();

        return new PolynomialList(vars,tord,s);
    }


    /**
     * parsing method for solvable polynomial set
     * syntax: varList termOrderName relationTable polyList
     */
    public PolynomialList nextSolvablePolynomialSet() throws IOException {
        List s = null;
        //String comments = "";
        //comments += nextComment();
        //if (debug) logger.debug("comment = " + comments);

        Coefficient coeff = nextCoefficientRing();
        logger.info("coeff = " + coeff); 

        vars = nextVariableList();
             String dd = "vars ="; 
             for (int i = 0; i < vars.length ;i++) {
                 dd+= " "+vars[i]; 
             }
             logger.info(dd); 

        tord = nextTermOrder();
             logger.info("tord = " + tord); 

        table = nextRelationTable();
             logger.info("table = " + table); 

        s = nextPolynomialList();
             logger.info("s = " + s); 
        // comments += nextComment();

        OrderedPolynomial p;
        List sp = new ArrayList( s.size() );
        for ( Iterator it = s.iterator(); it.hasNext(); ) {
            p = (OrderedPolynomial)it.next();
            p = new RatSolvableOrderedMapPolynomial(table,tord,p);
            sp.add( p );
        }
        s = sp;
        return new PolynomialList(vars,tord,s,table);
    }


    private boolean digit(char x) {
        return '0' <= x && x <= '9';
    }

    private boolean letter(char x) {
        return ( 'a' <= x && x <= 'z' ) || ( 'A' <= x && x <= 'Z' );
    }

    private int indexVar(String x) {
        for ( int i = 0; i < vars.length; i++ ) { 
            if ( x.equals( vars[i] ) ) { 
               return vars.length-i-1;
            }
        }
        return -1; // not found
    }

}
