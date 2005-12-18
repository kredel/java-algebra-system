/*
 * $Id$
 */

package edu.jas.structure;


/**
 * PrettyPrint.
 * Defines global pretty print status.
 * @author Heinz Kredel
 */

public class PrettyPrint {

    private static boolean toDo = true;

    protected PrettyPrint() {
    }

    /**
     * isTrue.
     * @return true, if to use pretty printing, else false.
     */
    public static boolean isTrue() {
        return toDo;
    }


    /**
     * setPretty.
     * Set use pretty printing to true.
     */
    public static void setPretty() {
        toDo = true;
    }


    /**
     * setInternal.
     * Set use pretty printing to false.
     */
    public static void setInternal() {
        toDo = false;
    }

}