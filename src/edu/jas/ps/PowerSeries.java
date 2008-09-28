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
     * <D extends RingElem<D>> 
     */
    public PowerSeries<C> map(UnaryFunctor<? super C,C> f);


    /**
     * Map a binary function to elements of this and another power series.
     * @return new power series.
     * , D extends RingElem<D>
     */
    public <C2 extends RingElem<C2>> 
	PowerSeries<C> zip(
	    BinaryFunctor<? super C,? super C2,C> f,
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
