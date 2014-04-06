/*
 * $Id$
 */

package edu.jas.kern;


import java.io.Reader;

import javax.script.AbstractScriptEngine;
import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngineFactory;
import javax.script.SimpleBindings;

import org.python.util.PythonInterpreter;


public class PyScriptEngine extends AbstractScriptEngine
/*implements Invocable, Compilable*/{


    PythonInterpreter pyint = new PythonInterpreter();


    public PyScriptEngine() {
        super();
        //pyint.execfile(this.getClass().getResourceAsStream("/jas.py"));
    }


    public PyScriptEngine(Bindings b) {
        super(b);
        //pyint.execfile(this.getClass().getResourceAsStream("/jas.py"));
    }


    @Override
    public Object eval(Reader r, ScriptContext c) {
        throw new RuntimeException("eval(Reader r,..) not implemented");
    }


    @Override
    public Object eval(String s, ScriptContext c) {
        //Obejct o = pyint.eval(s);
        //return pyint.eval(s).__str__().toString();
        pyint.exec(s);
        return null;
    }


    @Override
    public ScriptEngineFactory getFactory() {
        return new PyScriptEngineFactory();
    }


    @Override
    public Bindings createBindings() {
        return new SimpleBindings();
    }


    @Override
    public String toString() {
        ScriptEngineFactory sf = getFactory();
        return "PyScriptEngine(" + sf.getLanguageName() + ", " + sf.getLanguageVersion() + ")";
    }

}
