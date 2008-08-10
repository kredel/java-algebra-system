/*
 * $Id$
 */

package edu.jas.application;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import edu.jas.poly.PolynomialList;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Container for a Groebner system. 
 * It contains a list of colored polynomial systems and a
 * list of parametric polynomials representing the 
 * corresponding comprehensive Groebner base.
 * @param <C> coefficient type
 */
public class GroebnerSystem<C extends GcdRingElem<C>> {


    private static final Logger logger = Logger.getLogger(GroebnerSystem.class);


    private final boolean debug = logger.isDebugEnabled();


    /**
     * List of coloreds systems.
     */
    public final List<ColoredSystem<C>> list;


    /**
     * Comprehensive Groebner base for this Groebner system.
     */
    protected PolynomialList<GenPolynomial<C>> cgb;


    /**
     * Constructor for a Groebner system.
     * @param S a list of colored systems.
     */
    public GroebnerSystem(List<ColoredSystem<C>> S) {
        this.list = S;
        this.cgb = null;
    }


    /**
     * Get the String representation.
     * @see java.lang.Object#toString()
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer("\nGroebnerSystem: \n");
        if (list.size() > 0) {
            s.append("polynomial ring : " + list.get(0).green.ring + "\n");
        } else {
            s.append("parameter polynomial ring : " + condition.zero.list.ring + "\n");
        }
        s.append("conditions == 0 : " + getConditionZero() + "\n");
        s.append("conditions != 0 : " + getConditionNonZero() + "\n");
        if (debug) {
            s.append("green coefficients:\n" + getGreenCoefficients() + "\n");
            s.append("red coefficients:\n" + getRedCoefficients() + "\n");
        }
        s.append("colored polynomials:\n" + list + "\n");
        s.append("uncolored polynomials:\n" + getPolynomialList() + "\n");
        if (debug) {
            s.append("essential polynomials:\n" + getEssentialPolynomialList() + "\n");
        }
        if (pairlist != null) {
            s.append(pairlist.toString() + "\n");
        }
        return s.toString();
    }
     */


    /**
     * Is this Groebner system equal to other.
     * @param c other Groebner system.
     * @return true, if this is equal to other, else false.
     */
    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object c) {
        GroebnerSystem<C> cs = null;
        try {
            cs = (GroebnerSystem<C>) c;
        } catch (ClassCastException e) {
            return false;
        }
        if (cs == null) {
            return false;
        }
        boolean t = list.equals(cs.list);
        return t;
    }


    /**
     * Hash code for this colored system.
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        int h;
        h = list.hashCode();
        return h;
    }


    /**
     * Check invariants. Check if all polynomials are determined and if the
     * color of all coefficients is correct with respect to the condition.
     * @return true, if all invariants are met, else false.
    public boolean checkInvariant() {
        if (!isDetermined()) {
            return false;
        }
        // Condition<C> cond = condition;
        for (ColorPolynomial<C> s : list) {
            if (!s.checkInvariant()) {
                System.out.println("notInvariant " + s);
                System.out.println("condition:   " + condition);
                return false;
            }
            for (GenPolynomial<C> g : s.green.getMap().values()) {
                if (condition.color(g) != Condition.Color.GREEN) {
                    System.out.println("notGreen   " + g);
                    System.out.println("condition: " + condition);
                    System.out.println("colors:    " + s);
                    return false;
                }
            }
            for (GenPolynomial<C> r : s.red.getMap().values()) {
                if (condition.color(r) != Condition.Color.RED) {
                    System.out.println("notRed     " + r);
                    System.out.println("condition: " + condition);
                    System.out.println("colors:    " + s);
                    return false;
                }
            }
            for (GenPolynomial<C> w : s.white.getMap().values()) {
                if (condition.color(w) != Condition.Color.WHITE) {
                    // System.out.println("notWhite " + w);
                    // System.out.println("condition: " + condition);
                    // System.out.println("colors: " + s);
                    continue; // no error
                    // return false;
                }
            }
        }
        return true;
    }
     */


    /**
     * Is this colored system completely determined.
     * @return true, if each ColorPolynomial is determined, else false.
    public boolean isDetermined() {
        for (ColorPolynomial<C> s : list) {
            if (s.isZERO()) {
                continue;
            }
            if (!s.isDetermined()) {
                System.out.println("notDetermined " + s);
                System.out.println("condition: " + condition);
                return false;
            }
        }
        return true;
    }
     */

}
