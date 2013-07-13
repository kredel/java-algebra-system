/*
 * $Id$
 */

package edu.jas.kern;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;


public class PyScriptEngineFactory implements ScriptEngineFactory {


    @Override
    public String getProgram(String... S) {
        StringBuffer sb = new StringBuffer();
        for (String s : S) {
            sb.append(s + "; ");
        }
        return sb.toString();
    }


    @Override
    public ScriptEngine getScriptEngine() {
        return new PyScriptEngine();
    }


    @Override
    public String getOutputStatement(String s) {
        return s;
    }


    @Override
    public String getMethodCallSyntax(String o, String m, String... S) {
        return o + "." + m + "(" + Arrays.toString(S) + ")";
    }


    @Override
    public Object getParameter(String s) {
        return s;
    }


    @Override
    public String getLanguageVersion() {
        return "1.0";
    }


    @Override
    public String getLanguageName() {
        return "JAS jython";
    }


    @Override
    public String getEngineVersion() {
        return "0.1";
    }


    @Override
    public String getEngineName() {
        return "JAS_jython";
    }


    @Override
    public List<String> getNames() {
        List<String> ls = new ArrayList<String>();
        ls.add("jython");
        //System.out.println("getNames: " + ls);
        return Collections.unmodifiableList(ls);
    }


    @Override
    public List<String> getMimeTypes() {
        List<String> ls = new ArrayList<String>();
        //ls.add("x-application/python");
        //System.out.println("getMimeTypes: " + ls);
        return Collections.unmodifiableList(ls);
    }


    @Override
    public List<String> getExtensions() {
        List<String> ls = new ArrayList<String>();
        //ls.add("py");
        //System.out.println("getExtensions: " + ls);
        return Collections.unmodifiableList(ls);
    }

}
