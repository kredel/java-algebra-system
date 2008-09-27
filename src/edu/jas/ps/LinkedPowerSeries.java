/*
 * $Id$
 */

package edu.jas.ps;


import java.util.Map;
import java.util.Iterator;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Linked list power series implementation.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public class LinkedPowerSeries<C extends RingElem<C>> 
             implements PowerSeries< C > {


    interface LazyTail<C extends RingElem<C>> {
        LinkedPowerSeries<C> eval();
    }



    /**
     * Internal data structure.
     */
    private C head;
    private LinkedPowerSeries<C> tail = null;
    private LazyTail<C> lazyTail;

    private int order = 11;


    /**
     * Private constructor.
     */
    private LinkedPowerSeries() {
    }


    /**
     * Constructor.
     */
    public LinkedPowerSeries(C head, LazyTail<C> tail) {
        this.head = head;
        this.lazyTail = tail;
    }


    /**
     * To String.
     * @return string representation of this.
     */
    public String toString() {
        return toString(order);
    }


    /**
     * To String with given order.
     * @return string representation of this to given order.
     */
    public String toString(int order) {
        StringBuffer sb = new StringBuffer();
        LinkedPowerSeries<C> s = this;
        for (int i = 0; i < order; i++ ) {
            C c = s.leadingCoefficient();
            //sb.append("x^" + c.toString() + " + ");
            sb.append(c.toString() + ", ");
            s = s.reductum();
        }
        //       sb.append("O(x^"+order+")");
        sb.append("...");
        return sb.toString();
    }


    /**
     * Leading coefficient.
     * @return first coefficient.
     */
    public C leadingCoefficient() {
        return head;
    }


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public LinkedPowerSeries<C> reductum() {
        if ( tail == null ) {
            tail = lazyTail.eval();
            lazyTail = null;
        }
        return tail;
    }


    /**
     * Coefficient.
     * @return i-th coefficient.
     */
    public C coefficient(int i) {
        if ( i < 0 ) {
            throw new IndexOutOfBoundsException("negative index not allowed");
        }
        LinkedPowerSeries<C> ps = this;
        while ( i-- > 0 ) {
            ps = ps.reductum();
        }
        return ps.leadingCoefficient();
    }


    /**
     * Prepend a new leading coefficient.
     * @return new power series.
     */
    public LinkedPowerSeries<C> prepend(C h) {
        return new LinkedPowerSeries<C>(h,
                                        new LazyTail<C>() {
                                            public LinkedPowerSeries<C> eval() {
                                                return LinkedPowerSeries.this;
                                            }
                                        }
                                        );
    }


    /**
     * Select elements.
     * @return new power series.
     */
    public LinkedPowerSeries<C> select(Selector<? super C> sel) {
        LinkedPowerSeries<C> ps = this;
        C c = null;
        do { 
            c = ps.leadingCoefficient();
            ps = ps.reductum();
        } while( !sel.select(c));
        final LinkedPowerSeries<C> fps = ps;
        return new LinkedPowerSeries<C>(c,
                                        new LazyTail<C>() {
                                            public LinkedPowerSeries<C> eval() {
                                                return fps;
                                            }
                                        }
                                        );
    }


    /**
     * Map a unary function to this power series.
     * @return new power series.
     */
    public <D extends RingElem<D>> LinkedPowerSeries<D> map(final UnaryFunctor<? super C,D> f) {
        final LinkedPowerSeries<C> fps = reductum();
        return new LinkedPowerSeries<D>(
                                        f.eval( leadingCoefficient() ),
                                        new LazyTail<D>() {
                                            public LinkedPowerSeries<D> eval() {
                                                return /*reductum().*/ fps.map(f); // ?? 
                                            }
                                        }
                                        );
    }


    /**
     * Map a binary function to elements of this and another power series.
     * @return new power series.
     */
    public <C2 extends RingElem<C2>, D extends RingElem<D>> 
        LinkedPowerSeries<D> zip(
            final BinaryFunctor<? super C,? super C2,D> f,
            final PowerSeries<C2> ps
                           ) {
        return new LinkedPowerSeries<D>(
                                        f.eval( leadingCoefficient(), ps.leadingCoefficient() ),
                                        new LazyTail<D>() {
                                            public LinkedPowerSeries<D> eval() {
                                                return reductum().zip( f, ps.reductum() );
                                            }
                                        }
                                        );
    }

 
    /**
     * Fix point construction.
     * @return fix point wrt map.
     */
    public static <C extends RingElem<C>> 
        LinkedPowerSeries<C> fixPoint(PowerSeriesMap<C> map) {
             LinkedPowerSeries<C> ps1 = new LinkedPowerSeries<C>();
             LinkedPowerSeries<C> ps2 = (LinkedPowerSeries<C>) map.map(ps1);
             ps1.head = ps2.head;
             ps1.lazyTail = ps2.lazyTail;
             return ps2;
    }


}
