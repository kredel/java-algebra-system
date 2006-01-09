/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Comparator;
import java.util.Arrays;
import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.jas.poly.ExpVector;


/**
 * Term order class for ordered polynomials. 
 * Implements the most used term orders: 
 * graded, lexicographical, weight aray and block orders.
 * Does not implement orders by linear forms over Q[t].
 * Objects of this class are immutable.
 * @author Heinz Kredel
 */

public final class TermOrder implements Serializable {

    public static final int LEX     = 1;
    public static final int INVLEX  = 2;
    public static final int GRLEX   = 3;
    public static final int IGRLEX  = 4;
    public static final int REVLEX  = 5;
    public static final int REVILEX = 6;
    public static final int REVTDEG = 7;
    public static final int REVITDG = 8;

    public final static int DEFAULT_EVORD = IGRLEX;
    //public final static int DEFAULT_EVORD = INVLEX;

    private final int evord;
    // for split termorders
    private final int evord2;
    private final int evbeg1;
    private final int evend1;
    private final int evbeg2;
    private final int evend2;

    private static final Logger logger = Logger.getLogger(TermOrder.class);
    private boolean debug = logger.isDebugEnabled();


    /**
     * Defined array of weight vectors.
     */
    private final long[][] weight;


    /**
     * Defined descending order comparator. 
     * Sorts the highest terms first.
     */
    private final Comparator<ExpVector> horder;


    /**
     * Defined ascending order comparator. 
     * Sorts the lowest terms first.
     */
    private final Comparator<ExpVector> lorder;


    /**
     * Defined sugar order comparator. 
     * Sorts the graded lowest terms first.
     */
    private final Comparator<ExpVector> sugar;


    /**
     * Comparator for ExpVectors.
     */
    private abstract class EVorder implements Comparator<ExpVector>, 
                                              Serializable {
           public abstract int compare(ExpVector e1, ExpVector e2); 
    }


    /**
     * Constructor for default term order.
     */
    public TermOrder() {
        this(DEFAULT_EVORD);
    }


    /**
     * Constructor for given term order.
     * @param evord requested term order indicator / enumerator.
     */
    public TermOrder(int evord) {
        if ( evord < LEX || REVITDG < evord ) {
           throw new IllegalArgumentException("invalid term order: "+evord);
        }
        this.evord = evord;
        this.evord2 = 0;
        weight = null;
        evbeg1 = 0;
        evend1 = Integer.MAX_VALUE;
        evbeg2 = evend1;
        evend2 = evend1;
        switch ( evord ) { // horder = new EVhorder();
            case TermOrder.LEX:    { 
                horder = new EVorder() {
                   public int compare(ExpVector e1, ExpVector e2) {
                       return   ExpVector.EVILCP( e1, e2 ); 
                   }
                };
                break; 
            }
            case TermOrder.INVLEX: { 
                horder = new EVorder() {
                   public int compare(ExpVector e1, ExpVector e2) {
                       return - ExpVector.EVILCP( e1, e2 ); 
                   }
                };
                break; 
            }
            case TermOrder.GRLEX:  { 
                horder = new EVorder() {
                   public int compare(ExpVector e1, ExpVector e2) {
                       return   ExpVector.EVIGLC( e1, e2 ); 
                   }
                };
                break; 
            }
            case TermOrder.IGRLEX: { 
                horder = new EVorder() {
                   public int compare(ExpVector e1, ExpVector e2) {
                       return - ExpVector.EVIGLC( e1, e2 ); 
                   }
                };
                break; 
            }
            default:     { 
                horder = null;
            }
        }
        if ( horder == null ) {
           throw new IllegalArgumentException("invalid term order: "+evord);
        }

        // lorder = new EVlorder();
        lorder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                         return - horder.compare( e1, e2 );
                     }
        };

        // sugar = new EVsugar();
        sugar = new EVorder() {
               public int compare(ExpVector e1, ExpVector e2) {
                      return   ExpVector.EVIGLC( e1, e2 ); 
               }
        };
    }


    /**
     * Constructor for given exponent weights.
     * @param w weight vector of longs.
     */
    public TermOrder(long[] w) {
	this( new long[][] { w } );
    }


    /**
     * Constructor for given exponent weights.
     * @param w weight array of longs.
     */
    public TermOrder(long[][] w) {
        if ( w == null || w.length == 0 ) {
           throw new IllegalArgumentException("invalid term order weight");
        }
        weight = w;
        this.evord = 0;
        this.evord2 = 0;
        evbeg1 = 0;
        evend1 = weight[0].length;
        evbeg2 = evend1;
        evend2 = evend1;

        horder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                         return - ExpVector.EVIWLC( weight, e1, e2 ); 
            }
        };

        // lorder = new EVlorder();
        lorder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                         return + ExpVector.EVIWLC( weight, e1, e2 ); 
			 // return - horder.compare( e1, e2 );
                     }
        };

        // sugar = new EVsugar();
        sugar = horder;
    }


    /**
     * Constructor for given weighted split orders.
     * Unnecessary, to be removed.
     * @param w weight vector of longs.
     * @param split index.
     */
    public TermOrder(long[] w, int split) {
	this( new long[][] { w }, split );
    }


    /**
     * Constructor for given weighted split orders.
     * Unnecessary, to be removed.
     * @param w weight array of longs.
     * @param split index.
     */
    public TermOrder(long[][] w, int split) {
        if ( w == null || w.length == 0 ) {
           throw new IllegalArgumentException("invalid term order weight");
        }
        weight = w;
        this.evord = 0;
        this.evord2 = 0;
        evbeg1 = 0;
        evend1 = split;
        evbeg2 = split;
        evend2 = weight[0].length;
        if ( evbeg2 > evend2 ) {
           throw new IllegalArgumentException("invalid term order split");
        }

        horder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVIWLC( 
                                                   weight,
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVIWLC( 
                                                   weight,
                                            e1, e2, evbeg2, evend2 ); 
                     }
        };

        // lorder = new EVlorder();
        lorder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                         return - horder.compare( e1, e2 );
                     }
        };

        // sugar = new EVsugar();
        sugar = horder;
    }


    /**
     * Constructor for default split order.
     * @param r max number of exponents to compare.
     * @param split index.
     */
    public TermOrder(int r, int split) {
        this(DEFAULT_EVORD,DEFAULT_EVORD,r,split);
    }


    /**
     * Constructor for given split order.
     * @param ev1 requested term order indicator for first block.
     * @param ev2 requested term order indicator for second block.
     * @param r max number of exponents to compare.
     * @param split index.
     */
    public TermOrder(int ev1, int ev2, int r, int split) {
        if ( ev1 < LEX || REVITDG < ev1 ) {
           throw new IllegalArgumentException("invalid term order: "+ev1);
        }
        if ( ev2 < LEX || REVITDG < ev2 ) {
           throw new IllegalArgumentException("invalid term order: "+ev2);
        }
        this.evord = ev1;
        this.evord2 = ev2;
        weight = null;
        evbeg1 = 0;
        evend1 = split;
        evbeg2 = split;
        evend2 = r;
        if ( evbeg2 > evend2 ) {
           throw new IllegalArgumentException("invalid term order split");
        }

        switch ( evord ) { // horder = new EVhorder();
            case TermOrder.LEX:    { 
                switch ( evord2 ) { 
                    case TermOrder.LEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.INVLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.GRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.IGRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    default:     { 
                       horder = null;
                    }
                }
                break; 
            }
            case TermOrder.INVLEX: { 
                switch ( evord2 ) { 
                    case TermOrder.LEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.INVLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.GRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.IGRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVILCP( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    default:     { 
                       horder = null;
                    }
                }
                break; 
            }
            case TermOrder.GRLEX:  { 
                switch ( evord2 ) { 
                    case TermOrder.LEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.INVLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.GRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.IGRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t =   ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    default:     { 
                       horder = null;
                    }
                }
                break; 
            }
            case TermOrder.IGRLEX: { 
                switch ( evord2 ) { 
                    case TermOrder.LEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.INVLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVILCP( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.GRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return    ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    case TermOrder.IGRLEX:    { 
                       horder = new EVorder() {
                          public int compare(ExpVector e1, ExpVector e2) {
                              int t = - ExpVector.EVIGLC( 
                                            e1, e2, evbeg1, evend1 );
                              if ( t != 0 ) {
                                  return t;
                              }
                              return  - ExpVector.EVIGLC( 
                                            e1, e2, evbeg2, evend2 ); 
                          }
                       };
                    break;
                    }
                    default:     { 
                       horder = null;
                    }
                }
                break; 
            }
            default:     { 
                horder = null;
            }
        }
        if ( horder == null ) {
           throw new IllegalArgumentException("invalid term order: "
                                             +evord + " 2 "+ evord2);
        }

        lorder = new EVorder() {
                     public int compare(ExpVector e1, ExpVector e2) {
                         return - horder.compare( e1, e2 );
                     }
        };

        // sugar = new EVsugar();
        sugar = new EVorder() {
                    public int compare(ExpVector e1, ExpVector e2) {
                        return   ExpVector.EVIGLC( e1, e2 ); 
                    }
        };
    }


    /**
     * Get the first defined order indicator. 
     * @return evord.
     */
    public int getEvord() { 
        return evord; 
    }


    /**
     * Get the second defined order indicator. 
     * @return evord2.
     */
    public int getEvord2() { 
        return evord2; 
    }


    /**
     * Get the split index. 
     * @return split.
     */
    public int getSplit() { 
        return evbeg2; 
    }


    /**
     * Get the weight array. 
     * @return weight.
     */
    public long[][] getWeight() { 
        return weight; 
    }


    /**
     * Get the descending order comparator. 
     * Sorts the highest terms first.
     * @return horder.
     */
    public Comparator<ExpVector> getDescendComparator() { 
        return horder; 
    }


    /**
     * Get the ascending order comparator. 
     * Sorts the lowest terms first.
     * @return lorder.
     */
    public Comparator<ExpVector> getAscendComparator() { 
        return lorder; 
    }


    /**
     * Get the sugar order comparator. 
     * Sorts the graded lowest terms first.
     * @return sugar.
     */
    public Comparator<ExpVector> getSugarComparator() { 
        return sugar; 
    }


    /** Comparison with any other object.
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( Object B ) { 
       if ( ! (B instanceof TermOrder) ) {
          return false;
       }
       TermOrder b = (TermOrder)B;
       boolean t =    evord  == b.getEvord()
                   && evord2 == b.evord2
                   && evbeg1 == b.evbeg1
                   && evend1 == b.evend1
                   && evbeg2 == b.evbeg2
                   && evend2 == b.evend2;
       if ( ! t ) {
          return t;
       }
       if ( ! Arrays.equals(weight,b.weight) ) {
          return false;
       }
       return true;
    }


    /**
     * String representation of weight vector.
     * @see java.lang.Object#toString()
     */
    public String weightToString() {
	StringBuffer erg = new StringBuffer();
        if ( weight != null ) {
           erg.append("weight(");
           for ( int j = 0; j < weight.length; j++ ) {
	       long[] wj = weight[j];
               erg.append("(");
               for ( int i = 0; i < wj.length; i++ ) {
                   erg.append(""+wj[ wj.length-i-1 ]);
                   if ( i < wj.length-1 ) {
                      erg.append(",");
                   }
               }
               erg.append(")");
               if ( j < weight.length-1 ) {
                  erg.append(",");
               }
	   }
           erg.append(")");
        }
        return erg.toString();
    }


    /**
     * String representation of TermOrder.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
	StringBuffer erg = new StringBuffer();
        if ( weight != null ) {
           erg.append("W(");
           for ( int j = 0; j < weight.length; j++ ) {
	       long[] wj = weight[j];
               erg.append("(");
               for ( int i = 0; i < wj.length; i++ ) {
                   erg.append(""+wj[ wj.length-i-1 ]);
                   if ( i < wj.length-1 ) {
                      erg.append(",");
                   }
               }
               erg.append(")");
               if ( j < weight.length-1 ) {
                  erg.append(",");
               }
	   }
           erg.append(")");
           if ( evend1 == evend2 ) {
              return erg.toString();
           }
           erg.append("["+evbeg1+","+evend1+"]");
           erg.append("["+evbeg2+","+evend2+"]");
           return erg.toString();
        }
	switch ( evord ) {
	case INVLEX: erg.append("INVLEX");  break ;
	case IGRLEX: erg.append("IGRLEX");  break ;
	case LEX:    erg.append("LEX");     break ;
	case GRLEX:  erg.append("GRLEX");   break ;
        default:     erg.append("invalid("+evord+")"); break ;
	}
        if ( evord2 <= 0 ) {
           return erg.toString();
        }
        erg.append("["+evbeg1+","+evend1+"]");
	switch ( evord2 ) {
	case INVLEX: erg.append("INVLEX");  break ;
	case IGRLEX: erg.append("IGRLEX");  break ;
	case LEX:    erg.append("LEX");     break ;
	case GRLEX:  erg.append("GRLEX");   break ;
        default:     erg.append("invalid("+evord2+")"); break ;
	}
        erg.append("["+evbeg2+","+evend2+"]");
        return erg.toString();
    }


    /**
     * Extend variables. Used e.g. in module embedding.
     * Extend TermOrder by i elements.
     * @param r current number of variables.
     * @param i number of variables to extend.
     * @return extended TermOrder.
     */
    public TermOrder extend(int r, int i) {
        if ( weight != null ) {
           throw new RuntimeException("extend not implemented for weights");
        }
        if ( evord2 != 0 ) {
           logger.warn("TermOrder is already extended");
           return new TermOrder(evord,evord2,r+i,evend1+i);
        }
        return new TermOrder(evord,evord,r+i,i);
    }


    /**
     * Contract variables. Used e.g. in module embedding.
     * Contract TermOrder to non split status.
     * @return contracted TermOrder.
     */
    public TermOrder contract() {
        if ( weight != null ) {
           throw new RuntimeException("extend not implemented for weights");
        }
        if ( evord2 == 0 ) {
           logger.warn("TermOrder is already contracted");
        }
        return new TermOrder(evord);
    }


}
