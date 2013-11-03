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

import mpi.MPIException;

import org.apache.log4j.BasicConfigurator;

import edu.jas.gb.GroebnerBaseAbstract;
import edu.jas.gb.GroebnerBaseDistributedEC;
import edu.jas.gb.GroebnerBaseDistributedHybridEC;
import edu.jas.gb.GroebnerBaseDistributedHybridMPI;
import edu.jas.gb.GroebnerBaseDistributedMPI;
import edu.jas.gb.GroebnerBaseParallel;
import edu.jas.gb.GroebnerBaseSeq;
import edu.jas.gb.OrderedSyzPairlist;
import edu.jas.gb.ReductionPar;
import edu.jas.gb.ReductionSeq;
import edu.jas.gbufd.GBFactory;
import edu.jas.kern.ComputerThreads;
import edu.jas.kern.MPIEngine;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.GenPolynomialTokenizer;
import edu.jas.poly.PolynomialList;
import edu.jas.util.CatReader;
import edu.jas.util.ExecutableServer;


/**
 * Simple setup to run a GB example in MPI environment. <br />
 * Usage: RunGB [seq(+)|par(+)|dist(1)(+)|disthyb|cli] &lt;file&gt;
 * #procs/#threadsPerNode [machinefile]
 * @author Heinz Kredel
 */

public class RunMPIGB {


    /**
     * Check result GB if it is a GB.
     */
    static boolean doCheck = false;


    /**
     * Enable logging.
     */
    static boolean doLog = true;


    /**
     * main method to be called from commandline <br />
     * Usage: RunMPIGB [seq|par(+)|dist(+)|disthyb|cli] &lt;file&gt;
     * #procs/#threadsPerNode [machinefile]
     */

    public static void main(String[] args) throws IOException, MPIException {

        MPIEngine.setCommandLine(args); //args = MPI.Init(args);

        String[] allkinds = new String[] { "seq", "seq+", 
                                           "par", "par+", 
                                           "dist", "dist+", 
                                           "disthyb", "disthyb+", 
                                           "distmpi", "distmpi+", 
                                           "disthybmpi", "disthybmpi+", 
                                           "cli" };

        String usage = "Usage: RunGB [ "
                        + RunGB.join(allkinds, " | ") 
                        + "[port] ] " 
                        + "<file> " 
                        + "#procs/#threadsPerNode " 
                        + "[machinefile] " 
                        + "[check] [nolog]";

        if (args.length < 1) {
            System.out.println("args: " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }

        boolean plusextra = false;
        String kind = args[0];
        boolean sup = false;
        int k = -1;
        for (int i = 0; i < args.length; i++) {
            int j = RunGB.indexOf(allkinds, args[i]);
            if (j < 0) {
                continue;
            }
            sup = true;
            k = i;
            kind = args[k];
            break;
        }
        if (!sup) {
            System.out.println("args(sup): " + Arrays.toString(args));
            System.out.println(usage);
            return;
        }
        if (kind.indexOf("+") >= 0) {
            plusextra = true;
        }
        System.out.println("kind: " + kind + ", k = " + k);

        final int GB_SERVER_PORT = 7114;
        int port = GB_SERVER_PORT;

        if (kind.equals("cli")) {
            if (args.length - k >= 2) {
                try {
                    port = Integer.parseInt(args[k + 1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println("args(port): " + Arrays.toString(args));
                    System.out.println(usage);
                    return;
                }
            }
            RunGB.runClient(port);
            return;
        }

        String filename = null;
        if (!kind.equals("cli")) {
            if (args.length - k < 2) {
                System.out.println("args(file): " + Arrays.toString(args));
                System.out.println(usage);
                return;
            }
            filename = args[k + 1];
        }

        int j = RunGB.indexOf(args, "check");
        if (j >= 0) {
            doCheck = true;
            RunGB.doCheck = true;
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
                    System.out.println("args(threadsPerNode): " + Arrays.toString(args));
                    System.out.println(usage);
                    return;
                }
            }
            try {
                threads = Integer.parseInt(t);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                System.out.println("args(threads): " + Arrays.toString(args));
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

        Reader problem = RunGB.getReader(filename);
        if (problem == null) {
            System.out.println("args(file): " + filename);
            System.out.println("args(file): examples.jar(" + filename + ")");
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
        //Reader polyreader = new CatReader(new StringReader("("), problem); // ( has gone
        Reader polyreader = problem;
        GenPolynomialTokenizer tok = new GenPolynomialTokenizer(pfac, polyreader);
        PolynomialList S = null;
        try {
            S = new PolynomialList(pfac, tok.nextPolynomialList());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("S =\n" + S);

        if (doLog) {
            BasicConfigurator.configure();
        }

        if (kind.startsWith("seq")) {
            RunGB.runSequential(S, plusextra);
        } else if (kind.startsWith("par")) {
            RunGB.runParallel(S, threads, plusextra);
        } else if (kind.startsWith("distmpi")) {
            runMpi(S, threads, mfile, port, plusextra);
        } else if (kind.startsWith("disthybmpi")) {
            runHybridMpi(S, threads, threadsPerNode, mfile, port, plusextra);
        } else if (kind.startsWith("disthyb")) {
            RunGB.runMasterHyb(S, threads, threadsPerNode, mfile, port, plusextra);
        } else if (kind.startsWith("dist")) {
            RunGB.runMaster(S, threads, mfile, port, plusextra);
        }
        ComputerThreads.terminate();
        //System.exit(0);
    }


    @SuppressWarnings("unchecked")
    static void runMpi(PolynomialList S, int threads, String mfile, int port, boolean plusextra)
                    throws IOException, MPIException {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed MPI (" + threads + ", " + mfile + ", " + port
                        + ") ...");
        GroebnerBaseDistributedMPI gbd = null;
        GroebnerBaseDistributedMPI gbds = null;
        if (plusextra) {
            gbds = new GroebnerBaseDistributedMPI(threads, new OrderedSyzPairlist());
        } else {
            gbd = new GroebnerBaseDistributedMPI(threads);
        }
        t1 = System.currentTimeMillis();
        if (plusextra) {
            G = gbds.GB(L);
        } else {
            G = gbd.GB(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (plusextra) {
            gbds.terminate();
        } else {
            gbd.terminate();
        }
        MPIEngine.terminate();
        if (G == null) {
            return; // mpi.rank != 0
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (plusextra) {
            System.out.print("m+ ");
        } else {
            System.out.print("m ");
        }
        System.out.println("= " + threads + ", time = " + t1 + " milliseconds, " + (t - t1) + " start-up "
                        + ", total = " + t);
        RunGB.checkGB(S);
        System.out.println("");
    }


    @SuppressWarnings("unchecked")
    static void runHybridMpi(PolynomialList S, int threads, int threadsPerNode, String mfile, int port,
			     boolean plusextra) throws IOException, MPIException {
        List L = S.list;
        List G = null;
        long t, t1;

        t = System.currentTimeMillis();
        System.out.println("\nGroebner base distributed hybrid MPI (" + threads + "/" + threadsPerNode + ", "
                        + mfile + ", " + port + ") ...");
        GroebnerBaseDistributedHybridMPI gbd = null;
        GroebnerBaseDistributedHybridMPI gbds = null;
        if (plusextra) {
            gbds = new GroebnerBaseDistributedHybridMPI(threads, threadsPerNode, new OrderedSyzPairlist());
        } else {
            gbd = new GroebnerBaseDistributedHybridMPI(threads, threadsPerNode);
        }
        t1 = System.currentTimeMillis();
        if (plusextra) {
            G = gbds.GB(L);
        } else {
            G = gbd.GB(L);
        }
        t1 = System.currentTimeMillis() - t1;
        if (plusextra) {
            gbds.terminate();
        } else {
            gbd.terminate();
        }
        MPIEngine.terminate();
        if (G == null) {
            return; // mpi.rank != 0
        }
        S = new PolynomialList(S.ring, G);
        System.out.println("G =\n" + S);
        System.out.println("G.size() = " + G.size());
        t = System.currentTimeMillis() - t;
        if (plusextra) {
            System.out.print("m+ ");
        } else {
            System.out.print("m ");
        }
        System.out.println("= " + threads + ", ppn = " + threadsPerNode + ", time = " + t1
                        + " milliseconds, " + (t - t1) + " start-up " + ", total = " + t);
        RunGB.checkGB(S);
        System.out.println("");
    }

}
