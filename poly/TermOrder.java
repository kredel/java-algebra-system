/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Comparator;
import java.io.Serializable;

import edu.jas.poly.ExpVector;

/**
 * Term order class for ordered polynomials. 
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
    // split termorders
    private final int evord2;
    private final int evbeg1;
    private final int evend1;
    private final int evbeg2;
    private final int evend2;

    private final long[] weight;

    private final Comparator<ExpVector> horder;  // highest first 
    private final Comparator<ExpVector> lorder;  // lowest first
    private final Comparator<ExpVector> sugar;   // graded lowest first

    private abstract class EVorder implements Comparator<ExpVector>, 
                                              Serializable {
           public abstract int compare(ExpVector e1, ExpVector e2); 
    }


    /**
     * Default constructor
     */

    public TermOrder() {
        this(DEFAULT_EVORD);
    }


    /**
     * Constructor for given order
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
     * Constructor for given weight order
     */

    public TermOrder(long[] w) {
        if ( w == null ) {
           throw new IllegalArgumentException("invalid term order weight");
        }
        weight = w;
        this.evord = 0;
        this.evord2 = 0;
        evbeg1 = 0;
        evend1 = weight.length;
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
                         return - horder.compare( e1, e2 );
                     }
        };

        // sugar = new EVsugar();
        sugar = horder;
    }


    /**
     * Constructor for weighted split orders
     */

    public TermOrder(long[] w, int split) {
        if ( w == null ) {
           throw new IllegalArgumentException("invalid term order weight");
        }
        weight = w;
        this.evord = 0;
        this.evord2 = 0;
        evbeg1 = 0;
        evend1 = split;
        evbeg2 = split;
        evend2 = weight.length;
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
     * Default constructor for split orders
     */

    public TermOrder(int r, int split) {
        this(DEFAULT_EVORD,DEFAULT_EVORD,r,split);
    }

    /**
     * Constructor for given split orders
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
           throw new IllegalArgumentException("invalid term order: "+evord + " 2 "+ evord2);
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


    public int getEvord() { 
        return evord; 
    }

    public int getEvord2() { 
        return evord2; 
    }

    public long[] getWeight() { 
        return weight; 
    }


    public Comparator<ExpVector> getDescendComparator() { 
        return horder; // highest first
    }

    public Comparator<ExpVector> getAscendComparator() { 
        return lorder; // lowest first
    }

    public Comparator<ExpVector> getSugarComparator() { 
        return sugar; // graded lowest first
    }


    public boolean equals( Object B ) { 
       if ( ! (B instanceof TermOrder) ) return false;
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
       if ( weight == b.weight ) {
          return true;
       }
       if ( weight != null && b.weight == null ) {
          return false;
       }
       if ( weight == null && b.weight != null ) {
          return false;
       }
       if ( weight.length != b.weight.length ) {
          return false;
       }
       for ( int i = 0; i < weight.length; i++ ) {
           if ( weight[i] != b.weight[i] ) {
              return false;
           }
       }
       return true;
    }


    public String weightToString() {
	StringBuffer erg = new StringBuffer();
        if ( weight != null ) {
           erg.append("weight(");
           for ( int i = 0; i < weight.length; i++ ) {
               erg.append(""+weight[ weight.length-i-1 ]);
               if ( i < weight.length-1 ) {
                  erg.append(",");
               }
           }
           erg.append(")");
        }
        return erg.toString();
    }


    public String toString() {
	StringBuffer erg = new StringBuffer();
        if ( weight != null ) {
           erg.append("W(");
           for ( int i = 0; i < weight.length; i++ ) {
               erg.append(""+weight[ i ]);
               if ( i < weight.length-1 ) {
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
	case INVLEX: erg.append("INVLEX("+evord+")");  break ;
	case IGRLEX: erg.append("IGRLEX("+evord+")");  break ;
	case LEX:    erg.append("LEX("+evord+")");     break ;
	case GRLEX:  erg.append("GRLEX("+evord+")");   break ;
        default:     erg.append("invalid("+evord+")"); break ;
	}
        if ( evord2 <= 0 ) {
           return erg.toString();
        }
        erg.append("["+evbeg1+","+evend1+"]");
	switch ( evord2 ) {
	case INVLEX: erg.append("INVLEX("+evord2+")");  break ;
	case IGRLEX: erg.append("IGRLEX("+evord2+")");  break ;
	case LEX:    erg.append("LEX("+evord2+")");     break ;
	case GRLEX:  erg.append("GRLEX("+evord2+")");   break ;
        default:     erg.append("invalid("+evord2+")"); break ;
	}
        erg.append("["+evbeg2+","+evend2+"]");
        return erg.toString();
    }
}
