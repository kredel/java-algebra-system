/*
 * $Id$
 */

package edu.jas;

/**
 * StrategyEnumeration
 * This class names all possible strategies.
 * @author Heinz Kredel.
 */

public final class StrategyEnumeration {

    public static final StrategyEnumeration FIFO = new StrategyEnumeration();

    public static final StrategyEnumeration LIFO = new StrategyEnumeration();

    private StrategyEnumeration() { }

    public String toString() {
        if (this == FIFO) {
           return "FIFO strategy";
        } else {
           return "LIFO strategy";
        }
    }

}
