
/*
 * $Id$
 */

package edu.jas.poly;

import java.util.Comparator;

import edu.jas.arith.Coefficient;

import java.util.Map;

/**
 * Polynomial Interface. 
 * For implementations based different Maps, e.g. 
 * SortedMap / TreeMap, HashMap / LinkedHashMap.
 */

interface Polynomial {

    public int getOrd();

    public Comparator getDescendComparator();

    public Comparator getAscendComparator();

    public Map getMap();

    public String[] getVars();

    public String[] setVars(String[] v);

    public int length();

    public String toString(String[] vars); 

    public boolean equals( Object B ); 

    public void reSort();

    public void reSort(int ev);

    public Polynomial getZERO();

    public Polynomial getONE();

    public Map.Entry LM();  // deprecated
    public Map.Entry leadingMonomial();

    public ExpVector LEV(); // deprecated
    public ExpVector leadingExpVector();

    public int NOV(); // deprecated
    public int numberOfVariables(); 

    public Object LBC(); // deprecated
    public Object leadingBaseCoefficient(); 

    public Polynomial add(Polynomial B);

    public Polynomial subtract(Polynomial B);

    public Polynomial multiply(Polynomial B);

    public Polynomial multiply(Coefficient b);

    public Polynomial multiply(Coefficient b, ExpVector e);

    public Polynomial multiply(Map.Entry m);

    public Polynomial negate();

    public boolean isZERO();

    public boolean isONE();

}
