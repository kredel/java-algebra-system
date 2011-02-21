/*
 * $Id$
 */

package edu.jas.application;


import java.io.Serializable;

import edu.jas.structure.RingFactory;
import edu.jas.structure.GcdRingElem;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.ufd.QuotientRing;
import edu.jas.ufd.Quotient;


/**
 * Biulder for extension field towers.
 * @author Heinz Kredel
 */
public class ExtensionFieldBuilder<C extends GcdRingElem<C>> implements Serializable {


    /**
     * The current factory.
     */
    public final RingFactory<C> factory;


    /**
     * Constructor not for use.
     */
    protected ExtensionFieldBuilder() {
        throw new IllegalArgumentException("do not use this constructor");
    }


    /**
     * Constructor.
     * @param base the base field.
     */
    public ExtensionFieldBuilder(RingFactory<C> base) {
        factory = base;
    }


    /**
     * Build the field tower.
     */
    public RingFactory<C> build() {
        return factory;
    }


    /**
     * Transcendent field extension.
     * @param vars names for the transcendent generators.
     */
    public ExtensionFieldBuilder<C> transcendentExtension(String vars) {
        String[] variables = GenPolynomialTokenizer.variableList(vars);
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(factory,variables);
        QuotientRing<C> qfac = new QuotientRing<C>(pfac);
        RingFactory<C> base = (RingFactory<C>) (RingFactory) qfac;
        return new ExtensionFieldBuilder<C>(base);
    }


    /**
     * Algebraic field extension.
     * @param var name for the algebraic generator.
     * @param expr generating expresion, a univariate polynomial in var.
     */
    public ExtensionFieldBuilder<C> algebraicExtension(String var, String expr) {
        String[] variables = new String[] { var };
        GenPolynomialRing<C> pfac = new GenPolynomialRing<C>(factory,variables);
        GenPolynomial<C> gen = pfac.parse(expr);
        AlgebraicNumberRing<C> afac = new AlgebraicNumberRing<C>(gen);
        RingFactory<C> base = (RingFactory<C>) (RingFactory) afac;
        return new ExtensionFieldBuilder<C>(base);
    }


    /**
     * String representation of the ideal.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuffer s = new StringBuffer(" ");
        s.append(factory.toString());
        s.append(" ");
        return s.toString();
    }


    /**
     * Get a scripting compatible string representation.
     * @return script compatible representation for this Element.
     * @see edu.jas.structure.Element#toScript()
     */
    public String toScript() {
        // Python case
        StringBuffer s = new StringBuffer(" ");
        s.append(factory.toScript());
        s.append(" ");
        return s.toString();
    }

}
