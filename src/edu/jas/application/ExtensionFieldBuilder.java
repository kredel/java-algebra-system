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
public class ExtensionFieldBuilder implements Serializable {


    /**
     * The current factory.
     */
    public final RingFactory factory; // must be a raw type


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
    public ExtensionFieldBuilder(RingFactory base) {
        factory = base;
    }


    /**
     * Build the field tower.
     * TODO: build at the end and optimize field tower for faster computation.
     */
    public RingFactory build() {
        return factory;
    }


    /**
     * Set base field.
     * @param base the base field for the extensions.
     */
    public static ExtensionFieldBuilder baseField(RingFactory base) {
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Transcendent field extension.
     * @param vars names for the transcendent generators.
     */
    public ExtensionFieldBuilder transcendentExtension(String vars) {
        String[] variables = GenPolynomialTokenizer.variableList(vars);
        GenPolynomialRing pfac = new GenPolynomialRing(factory,variables);
        QuotientRing qfac = new QuotientRing(pfac);
        RingFactory base = (RingFactory) qfac;
        return new ExtensionFieldBuilder(base);
    }


    /**
     * Algebraic field extension.
     * @param var name for the algebraic generator.
     * @param expr generating expresion, a univariate polynomial in var.
     */
    public ExtensionFieldBuilder algebraicExtension(String var, String expr) {
        String[] variables = new String[] { var };
        GenPolynomialRing pfac = new GenPolynomialRing(factory,variables);
        GenPolynomial gen = pfac.parse(expr);
        AlgebraicNumberRing afac = new AlgebraicNumberRing(gen);
        RingFactory base = (RingFactory) afac;
        return new ExtensionFieldBuilder(base);
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
