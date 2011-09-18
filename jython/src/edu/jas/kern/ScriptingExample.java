/*
 * $Id$
 */

package edu.jas.kern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public class ScriptingExample {

    public static void main(String[] args) {

        ScriptEngine jas = new ScriptEngineManager().getEngineByExtension("py");
        System.out.println("JAS service discovered: " + jas);
	if ( jas == null ) {
            ScriptEngineManager scriptManager = new ScriptEngineManager();
            scriptManager.registerEngineExtension("py", new PyScriptEngineFactory());
	    jas = scriptManager.getEngineByExtension("py");
	}
	if ( jas == null ) {
	    System.out.println("No JAS engine found");
	    return;
	}
        System.out.println("Using JAS engine: " + jas);
        try {
            long millis = System.currentTimeMillis();
	    //String ex = "x = 1; print(4*2*x)";
	    String ex = "from jas import PolyRing, ZZ; r = PolyRing(ZZ(),\"x,y,z\",PolyRing.lex); print str(r);"
                      + "[one,x,y,z] = r.gens(); p = ((x*y)+z)**33; print \"p = \" + str(p)";
            System.out.println("ex: " + ex);
            Object ans = jas.eval(ex);
            millis = System.currentTimeMillis() - millis;
            System.out.println("answer: " + ans);
            System.out.println("evaluation took " + millis);

        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

