/*
 * Created on 03.10.2004
 * $Id$
  */

package edu.jas.ring;

/**
 * Class to produce a system of equations as defined by Katsura
 * 
 * @author kredel
 *
  */
public class Katsura {

    public static void main(String[] args) {
        if ( args.length == 0 ) {
           System.out.println("usage: Katsura N <order> <var>");
           return;
        }
        int n = Integer.parseInt(args[0]);
        Katsura k = null;
        if ( args.length == 1 ) {               
           k = new Katsura(n);
        }
        if ( args.length == 2 ) {               
           k = new Katsura("u",n, args[1]);
        }
        if ( args.length == 3 ) {               
        k = new Katsura(args[2],n, args[1]);
                        }
        System.out.println("#Katsura equations for N = " + n + ":");
                System.out.println("" + k);
    }

    final int N;
    final String var;
    final String order;

    public Katsura(int n) {
           this("u", n);
    }

    public Katsura(String v, int n) {
           this(v, n, "G");
    }

    public Katsura(String var, int n, String order) {
           this.var = var;
           this.N = n;
           this.order = order;
    }

    String sum1() {
           StringBuffer s = new StringBuffer();
           for (int i = -N; i <= N; i++) {
               s.append(variable(i));
               if (i < N) {
                   s.append(" + ");
               }
           }
           s.append(" - 1");
           return s.toString();
    }

    String sumUm(int m) {
           StringBuffer s = new StringBuffer();
           for (int i = -N; i <= N; i++) {
               s.append(variable(i));
               s.append("*");
               s.append(variable(m - i));
               if (i < N) {
                  s.append(" + ");
               }
           }
           s.append(" - " + variable(m));
           return s.toString();
    }

    String varList(String order) {
           StringBuffer s = new StringBuffer();
           s.append("(");
           for (int i = 0; i <= N; i++) {
               s.append(variable(i));
               if (i < N) {
                  s.append(",");
               }
           }
           s.append(")  ");
           s.append(order);
           return s.toString();
    }

    public String toString() {
           StringBuffer s = new StringBuffer();
           s.append(varList(order));
           s.append("\n");
           s.append("(\n");
           //for (int m = -N + 1; m <= N - 1; m++) { doubles polynomials
           for (int m = 0; m <= N - 1; m++) {
               s.append(sumUm(m));
               s.append(",\n");
           }
           s.append(sum1());
           s.append("\n");
           s.append(")\n");
           return s.toString();
    }

    String variable(int i) {
           if (i < 0) {
               return variable(-i);
           }
           if (i > N) {
               return "0";
           }
           return var + i;
    }

}
