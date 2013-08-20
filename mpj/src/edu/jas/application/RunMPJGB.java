/*
 * $Id$
 */

package edu.jas.application;


import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.BasicConfigurator;

import edu.jas.gb.GBDist;
import edu.jas.gb.GBDistHybrid;
import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseDistributedHybridMPJ;
import edu.jas.gb.GroebnerBaseDistributedMPJ;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.OrderedSyzPairlist;
import edu.jas.gb.ReductionPar;
import edu.jas.gb.ReductionSeq;
import edu.jas.gbufd.GBFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.MPJEngine;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.util.CatReader;
import edu.jas.util.ExecutableServer;


/**
 * Simple setup to run a GB example in MPJ environment. <br />
 * Usage: RunGB [seq(+)|par(+)|dist(1)(+)|disthyb|cli] &lt;file&gt;
 * #procs/#threadsPerNode [machinefile]
 * @author Heinz Kredel
 */

public class RunMPJGB {


    /**
     * Check result GB if it is a GB.
     */
    static boolean doCheck = false;


    /**
     * main method to be called from commandline <br />
     * Usage: RunMPJGB [seq|par(+)|dist(1)(+)|disthyb|cli] &lt;file&gt;
     * #procs/#threadsPerNode [machinefile]
     */

    public static void main(java.lang.String[] args) throws IOException {

        BasicConfigurator.configure();
        MPJEngine.setCommandLine(args); //args = MPI.Init(args);

        String usage = "Usage: RunMPJGB " + "[ seq | seq+ | par | par+ "
                        + "| dist | dist1 | dist+ | dist1+ | disthyb1 | distmpj " + "| cli [port] ] "
                        + "<file> " + "#procs/#threadsPerNode " + "[machinefile] <check>";
        if (args.length < 1) {
            System.out.println("args: " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }

        boolean pairseq = false;
        String kind = args[0];
        String[] allkinds = new String[] { "seq", "seq+", "par", "par+", "dist", "dist1", "dist+", "dist1+",
                "disthyb1", "distmpj", "distmpj+", "disthybmpj", "disthybmpj+", "cli" };
        boolean sup = false;
        int k = -1;
        for (int i = 0; i < args.length; i++) {
            int j = indexOf(allkinds, args[i]);
            if (j < 0) {
                continue;
            }
            sup = true;
            k = i;
            kind = args[k];
            if (kind.indexOf("+") >= 0) {
                pairseq = true;
            }
            break;
        }
        if (!sup) {
            System.out.println("args(sup): " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }
        System.out.println("kind: " + kind + ", k = " + k);

        //boolean once = false;
        final int GB_SERVER_PORT = 7114;
        //inal int EX_CLIENT_PORT = GB_SERVER_PORT + 1000; 
        int port = GB_SERVER_PORT;

        if (kind.equals("cli")) {
            if (args.length - k >= 2) {
                try {
                    port = Integer.parseInt(args[k + 1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("args(int): " + Arrays.toString(args));
                    System.out.println(usage);
                    return;
                }
            }
            runClient(port);
            return;
        }

        String filename = null;
        if (!kind.equals("cli")) {
            if (args.length - k < 2) {
                System.out.println("args(cli): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
            filename = args[k + 1];
        }

        int j = indexOf(args, "check");
        if (j >= 0) {
            doCheck = true;
        }

        int threads = 0;
        int threadsPerNode = 1;
        if (kind.startsWith("par") || kind.startsWith("dist")) {
            if (args.length - k < 3) {
                System.out.println("args(par|dist): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
            String tup = args[k + 2];
            String t = tup;
            int i = tup.indexOf("/");
            if (i >= 0) {
                t = tup.substring(0, i).trim();
                tup = tup.substring(i + 1).trim();
                try {
                    threadsPerNode = Integer.parseInt(tup);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("args(int): " + Arrays.toString(args));
                    System.out.println(usage);
                    return;
                }
            }
            try {
                threads = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("args(int): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
        }

        String mfile = null;
        if (kind.startsWith("dist")) {
            if (args.length - k >= 4) {
                mfile = args[k + 3];
            } else {
                mfile = "machines";
            }
        }

        Reader problem = null;
        try {
            problem = new InputStreamReader(new FileInputStream(filename), Charset.forName("UTF8"));
            problem = new BufferedReader(problem);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("args(file): " + filename);
            System.out.println("args(file): " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }

        RingFactoryTokenizer rftok = new RingFactoryTokenizer(problem);
        GenPolynomialRing pfac = null;
        try {
            pfac = rftok.nextPolynomialRing();
            rftok = null;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Reader polyreader = new CatReader(new StringReader("("), problem); // ( has gone
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(pfac, polyreader);
        PolynomialList S = null;
        try {
            S = new PolynomialList(pfac, tok.nextPolynomialList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("S =\n" + S);

        if (kind.startsWith("seq")) {
            runSequential(S, pairseq);
        }
        if (kind.startsWith("par")) {
            runParallel(S, threads, pairseq);
        }
        if (kind.startsWith("dist1")) {
            runMasterOnce(S, threads, mfile, port, pairseq);
        } else if (kind.startsWith("disthyb1")) {
            runMasterOnceHyb(S, threads, threadsPerNode, mfile, port, pairseq);
        } else if (kind.startsWith("distmpj")) {
            runMpj(S, threads, mfile, port, pairseq);
        } else if (kind.startsWith("disthybmpj")) {
            runHybridMpj(S, threads, threadsPerNode, mfile, port, pairseq);
        } else if (kind.startsWith("dist")) {
            runMaster(S, threads, mfile, port, pairseq);
        }
        ComputerThreads.terminate();
        //System.exit(0);
    }


    @SuppressWarnings("unchecked")
    static void runMpj(PolynomialList S, int threads, String mfile, int port, boolean pairseq)
                    throws IOException {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed MPJ (" + threads + ", " + mfile + ", " + port
                        + ") ...");
        GroebnerBaseDistributedMPJ gbd = null;
        GroebnerBaseDistributedMPJ gbds = null;
        if (pairseq) {
            gbds = new GroebnerBaseDistributedMPJ(threads, new OrderedSyzPairlist());
        } else {
            gbd = new GroebnerBaseDistributedMPJ(threads);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.GB(L);
        } else {
            G = gbd.GB(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate();
        } else {
            gbd.terminate();
        }
        MPJEngine.terminate();
        if (G == null) {
            return; // mpi.rank != 0
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("m+ ");
        } else {
            System.out.print("m ");
        }
        System.out.println("= " + threads + ", time = " + t1 + " milliseconds, " + (t - t1) + " start-up "
                        + ", total = " + t);
        checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runHybridMpj(PolynomialList S, int threads, int threadsPerNode, String mfile, int port,
                    boolean pairseq) throws IOException {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed hybrid MPJ (" + threads + "/" + threadsPerNode + ", "
                        + mfile + ", " + port + ") ...");
        GroebnerBaseDistributedHybridMPJ gbd = null;
        GroebnerBaseDistributedHybridMPJ gbds = null;
        if (pairseq) {
            gbds = new GroebnerBaseDistributedHybridMPJ(threads, threadsPerNode, new OrderedSyzPairlist());
        } else {
            gbd = new GroebnerBaseDistributedHybridMPJ(threads, threadsPerNode);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.GB(L);
        } else {
            G = gbd.GB(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate();
        } else {
            gbd.terminate();
        }
        MPJEngine.terminate();
        if (G == null) {
            return; // mpi.rank != 0
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("m+ ");
        } else {
            System.out.print("m ");
        }
        System.out.println("= " + threads + ", ppn = " + threadsPerNode + ", time = " + t1
                        + " milliseconds, " + (t - t1) + " start-up " + ", total = " + t);
        checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runMaster(PolynomialList S, int threads, String mfile, int port, boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed (" + threads + ", " + mfile + ", " + port + ") ...");
        GBDist gbd = null;
        GBDist gbds = null;
        if (pairseq) {
            //gbds = new GBDistSP(threads,mfile, port);
            gbds = new GBDist(threads, new OrderedSyzPairlist(), mfile, port);
        } else {
            gbd = new GBDist(threads, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.execute(L);
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate(false);
        } else {
            gbd.terminate(false);
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", time = " + t1 + " milliseconds, " + (t - t1) + " start-up "
                        + ", total = " + t);
        checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runMasterOnce(PolynomialList S, int threads, String mfile, int port, boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed[once] (" + threads + ", " + mfile + ", " + port
                        + ") ...");
        GBDist gbd = null;
        GBDist gbds = null;
        if (pairseq) {
            //gbds = new GBDistSP(threads, mfile, port);
            gbds = new GBDist(threads, new OrderedSyzPairlist(), mfile, port);
        } else {
            gbd = new GBDist(threads, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.execute(L);
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            gbds.terminate(true);
        } else {
            gbd.terminate(true);
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", time = " + t1 + " milliseconds, " + (t - t1) + " start-up "
                        + ", total = " + t);
        checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runMasterOnceHyb(PolynomialList S, int threads, int threadsPerNode, String mfile, int port,
                    boolean pairseq) {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed hybrid[once] (" + threads + "/" + threadsPerNode
                        + ", " + mfile + ", " + port + ") ...");
        GBDistHybrid gbd = null;
        GBDistHybrid gbds = null;
        if (pairseq) {
            //System.out.println("... not implemented.");
            //return;
            // gbds = new GBDistSP(threads, mfile, port);
            gbds = new GBDistHybrid(threads, threadsPerNode, new OrderedSyzPairlist(), mfile, port);
        } else {
            gbd = new GBDistHybrid(threads, threadsPerNode, mfile, port);
        }
        t1 = System.currentTimeMillis();
        if (pairseq) {
            G = gbds.execute(L);
        } else {
            G = gbd.execute(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (pairseq) {
            //gbds.terminate(true);
        } else {
            //gbd.terminate(true);
            gbd.terminate(false); // plus eventually killed by script
        }
        t = System.currentTimeMillis() - t;
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        if (pairseq) {
            System.out.print("d+ ");
        } else {
            System.out.print("d ");
        }
        System.out.println("= " + threads + ", ppn = " + threadsPerNode + ", time = " + t1
                        + " milliseconds, " + (t - t1) + " start-up " + ", total = " + t);
        checkGB(S);
        System.out.println("");
    }


    static void runClient(int port) {
        System.out.println("\nGroebner base distributed client (" + port + ") ...");

        ExecutableServer es = new ExecutableServer(port);
        es.init();
    }


    @SuppressWarnings("unchecked")
    static void runParallel(PolynomialList S, int threads, boolean pairseq) {
        List L = S.list;
        List G;
        long t;
        GroebnerBaseAbstract bb = null;
        GroebnerBaseAbstract bbs = null;
        if (pairseq) {
            //bbs = new GroebnerBaseSeqPairParallel(threads);
            bbs = new GroebnerBaseParallel(threads, new ReductionPar(), new OrderedSyzPairlist());
        } else {
            bb = new GroebnerBaseParallel(threads);
        }
        System.out.println("\nGroebner base parallel (" + threads + ") ...");

        t = System.currentTimeMillis();
        if (pairseq) {
            G = bbs.GB(L);
        } else {
            G = bb.GB(L);
        }
        t = System.currentTimeMillis() - t;
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());

        if (pairseq) {
            System.out.print("p+ ");
        } else {
            System.out.print("p ");
        }
        System.out.println("= " + threads + ", time = " + t + " milliseconds");
        if (pairseq) {
            bbs.terminate();
        } else {
            bb.terminate();
        }
        checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runSequential(PolynomialList S, boolean pairseq) {
        List L = S.list;
        List G;
        long t;
        GroebnerBaseAbstract bb = null;
        if (pairseq) {
            //bb = new GroebnerBaseSeqPairSeq();
            bb = new GroebnerBaseSeq(new ReductionSeq(), new OrderedSyzPairlist());
        } else {
            bb = new GroebnerBaseSeq();
        }
        System.out.println("\nGroebner base sequential ...");
        t = System.currentTimeMillis();
        G = bb.GB(L);
        t = System.currentTimeMillis() - t;
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        if (pairseq) {
            System.out.print("seq+, ");
        } else {
            System.out.print("seq, ");
        }
        System.out.println("time = " + t + " milliseconds");
        checkGB(S);
        System.out.println("");
    }


    static void checkGB(PolynomialList S) {
        if (!doCheck) {
            return;
        }
        GroebnerBaseAbstract bb = GBFactory.getImplementation(S.ring.coFac);
        long t = System.currentTimeMillis();
        boolean chk = bb.isGB(S.list);
        t = System.currentTimeMillis() - t;
        System.out.println("check isGB = " + chk + " in " + t + " milliseconds");
    }


    static int indexOf(String[] args, String s) {
        for (int i = 0; i < args.length; i++) {
            if (s.equals(args[i])) {
                return i;
            }
        }
        return -1;
    }

}
