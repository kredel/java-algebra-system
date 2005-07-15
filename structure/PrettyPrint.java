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

    public static boolean isTrue() {
        return toDo;
    }

    public static void setPretty() {
        toDo = true;
    }

    public static void setInternal() {
        toDo = false;
    }

}