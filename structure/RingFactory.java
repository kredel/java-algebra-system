/*
 * $Id$
 */

package edu.jas.structure;

import java.math.BigInteger;
import java.io.Reader;
import java.io.Serializable;

/**
 * RingFactory interface for use with the polynomial classes.
 * 
 * @author Heinz Kredel
 */

public interface RingFactory<C extends RingElem> extends Serializable {

    public C getZERO();

    public C getONE();

    public C fromInteger(long a);

    public C fromInteger(BigInteger a);

    public C random(int n);

    public C copy(C c);

    public C parse(String s);

    public C parse(Reader r);

}
