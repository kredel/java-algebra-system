/*
 * $Id$
 */

package edu.jas.ps;


import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;


/**
 * Power series interface.
 * Adds methods specific to power series.
 * @param <C> ring element type
 * @author Heinz Kredel
 */

public interface PowerSeries<C extends RingElem<C>> 
                 /*extends RingElem< PowerSeries<C> >*/ {


    /**
     * Leading coefficient.
     * @return first coefficient.
     */
    public C leadingCoefficient();


    /**
     * Reductum.
     * @return this - leading monomial.
     */
    public PowerSeries<C> reductum();


    /**
     * Coefficient.
     * @return i-th coefficient.
     */
    public C coefficient(int i);


    /**
     * Prepend a new leading coefficient.
     * @return new power series.
     */
    public PowerSeries<C> prepend(C c);


    /**
     * Select elements.
     * @return new power series.
     */
    public PowerSeries<C> select(Selector<? super C> sel);


    /**
     * Map a unary function to this power series.
     * @return new power series.
     */
    public <D extends RingElem<D>> PowerSeries<D> map(UnaryFunctor<? super C,D> f);


    /**
     * Map a binary function to elements of this and another power series.
     * @return new power series.
     */
    public <C2 extends RingElem<C2>, D extends RingElem<D>> 
	PowerSeries<D> zip(
	    BinaryFunctor<? super C,? super C2,D> f,
	    PowerSeries<C2> ps
	    );


    /**
     * Is unit.
     * @return true, if this power series is invertible, else false.
     */
    public boolean isUnit();


    /**
     * Inverse power series.
     * @return ps with this * ps = 1.
     */
    public PowerSeries<C> inverse();


}
