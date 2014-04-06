/*
 * $Id$
 */

package edu.jas.gb;


import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.log4j.Logger;

import edu.jas.poly.GenPolynomial;
import edu.jas.structure.GcdRingElem;


/**
 * Groebner bases via signatur based GBs using jython script. TODO: Computing
 * via the ScriptEngine is way slower than the direct execution in the jython
 * interpreter. Check if a different engine is in the path or if it must be
 * configured in some special way.
 * @author Heinz Kredel
 */

public class GBSigBased<C extends GcdRingElem<C>> extends GroebnerBaseAbstract<C> {


    private static final Logger logger = Logger.getLogger(GBSigBased.class);


    private final boolean debug = logger.isDebugEnabled(); //logger.isInfoEnabled();


    /**
     * Scripting engine.
     */
    public final ScriptEngine engine;


    /**
     * GBSigBased constructor.
     */
    public GBSigBased() {
        engine = new ScriptEngineManager().getEngineByExtension("py");
        if (engine == null) {
            logger.error("no script engine found");
            throw new RuntimeException("no script engine found");
        }
        logger.info("Script engine discovered: " + engine.getClass().getName());
        StringBuffer sb = new StringBuffer();
        sb.append("from jas import PolyRing, ZZ, QQ, arraylist2pylist, pylist2arraylist;\n");
        sb.append("from basic_sigbased_gb import sigbased_gb, arris_algorithm, ggv, ggv_first_implementation, f5, f5z;\n");
        sb.append("sbgb = sigbased_gb();\n");
        sb.append("arri = arris_algorithm();\n");
        sb.append("ggv = ggv();\n");
        sb.append("ggv1 = ggv_first_implementation();\n");
        sb.append("ff5 = f5z();\n");
        String ex = sb.toString();
        if (debug) {
            logger.info("input for evaluation:\n" + ex);
        }
        try {
            Object ans = engine.eval(ex);
            if (ans != null) {
                logger.info("constructor answer: " + ans);
            }
        } catch (ScriptException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the String representation with GB engine.
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GBSigBased[ " + engine.getClass().getName() + " ]";
    }


    /**
     * Cleanup and terminate ThreadPool.
     */
    @Override
    public void terminate() {
    }


    /**
     * Cancel ThreadPool.
     */
    @Override
    public int cancel() {
        return 0;
    }


    /**
     * Groebner base.
     * @param modv module variable number.
     * @param F polynomial list.
     * @return GB(F) a Groebner base of F.
     */
    @Override
    public List<GenPolynomial<C>> GB(int modv, List<GenPolynomial<C>> F) {
        if (F == null || F.isEmpty()) {
            return F;
        }
        if (modv != 0) {
            throw new UnsupportedOperationException("implemented only for modv = 0, not " + modv);
        }
        //GenPolynomialRing<C> pfac = F.get(0).ring;
        List<GenPolynomial<C>> G = new ArrayList<GenPolynomial<C>>();
        long millis = System.currentTimeMillis();
        try {
            engine.put("F", F);
            StringBuffer sb = new StringBuffer();
            //sb.append("r = " + pfac.toScript() + ";\n");
            //sb.append("print str(r);\n");
            //sb.append("print \"F = \" + str(F);\n");
            sb.append("Fp = arraylist2pylist(F);\n");
            //sb.append("Gp = sbgb.basis_sig(Fp);\n");
            //sb.append("Gp = ff5.basis_sig(Fp);\n");
            //sb.append("Gp = arri.basis_sig(Fp);\n");
            sb.append("Gp = ggv1.basis_sig(Fp);\n");
            sb.append("G = pylist2arraylist(Gp);\n");
            String ex = sb.toString();
            if (debug) {
                logger.info("input for evaluation:\n" + ex);
            }
            Object ans = engine.eval(ex);
            if (ans != null) {
                logger.info("answer: " + ans);
            }
            G = (List<GenPolynomial<C>>) engine.get("G");
        } catch (ScriptException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        millis = System.currentTimeMillis() - millis;
        System.out.println("evaluation took " + millis);
        if (debug) {
            logger.info("polynomials G: " + G);
        }
        return G;
    }

}
