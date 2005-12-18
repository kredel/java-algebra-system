/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

import edu.jas.structure.RingElem;

import edu.jas.poly.GenPolynomial;

import edu.jas.util.RemoteExecutable;
import edu.jas.util.ExecutableChannels;

/**
 * Setup to run a distributed GB example.
 * @author Heinz Kredel
 */

public class GBDist<C extends RingElem<C>> {

  /**
   * Execute a distributed GB example.
   * Distribute clients and start master.
   * @param F list of polynomials
   * @param threads number of threads respectivly processes.
   * @param mfile name of the machine file.
   * @param port for GB server. 
   * @return GB(F) a Groebner base for F.
   */
    public ArrayList<GenPolynomial<C>> 
           execute(List<GenPolynomial<C>> F, 
                   int threads, 
                   String mfile, 
                   int port) {

	final String fname;
	if ( mfile == null || mfile.length() == 0 ) {
             fname = "../util/machines";
	} else {
	     fname = mfile;
	}
	final int numc = threads;

	ArrayList<GenPolynomial<C>> G = null;

	ExecutableChannels ec = null;
	try {
            ec = new ExecutableChannels( fname );
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	    return G;
	}
	try {
	    ec.open(numc);
	} catch (IOException e) {
	    e.printStackTrace();
	    return G;
	}

	GBClient<C> gbc 
          = new GBClient<C>( ec.getMasterHost(), ec.getMasterPort() );
	try {
	    for ( int i = 0; i < numc; i++ ) {
	        ec.send( i, gbc );
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return G;
	}

	G = null;
        try {
            G = GroebnerBaseDistributed.<C>Server( F, threads, port );
        } catch (IOException e) {
        }

	try {
	    for ( int i = 0; i < numc; i++ ) {
	       Object o = ec.receive( i );
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return G;
	} catch (ClassNotFoundException e) {
	    e.printStackTrace();
	    return G;
	}
	ec.close();
	return G;
    }

}


/**
 * Objects of this class are to be send to a ExecutableServer.
 */

class GBClient<C extends RingElem<C>> implements RemoteExecutable {

    String host;
    int port;

    /**
     * GBClient.
     * @param host
     * @param port
     */
    public GBClient(String host, int port) {
	this.host = host;
	this.port = port;
    }

     
    /**
     * run.
     */
    public void run() {		// run starts here.
	try {
	    GroebnerBaseDistributed.<C>Client(host, port);
	} catch (IOException ignored) {
	}
    }

}