/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

import edu.jas.util.RemoteExecutable;
import edu.jas.util.ExecutableChannels;

  /**
   * Setup to run a distributed GB example.
   * @author Heinz Kredel
   */

public class GBDist {

  /**
   * Execute a distributed GB example.
   * Distribute clients and start master.
   */

    public ArrayList execute(List F, int threads, String mfile, int port) {

	final String fname;
	if ( mfile == null || mfile.length() == 0 ) {
             fname = "../util/machines";
	} else {
	     fname = mfile;
	}
	final int numc = threads;

	ArrayList G = null;

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

	GBClient gbc = new GBClient( ec.getMasterHost(), ec.getMasterPort() );
	try {
	    for ( int i = 0; i < numc; i++ ) {
	        ec.send( i, gbc );
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return G;
	}

	G = (ArrayList)F;
        try {
            G = GroebnerBaseDistributed.DIRPGBServer( F, threads, port );
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
 * Objects of this class are to be send to a ExecutableServer
 */

class GBClient implements RemoteExecutable {

    String host;
    int port;

    public GBClient(String host, int port) {
	this.host = host;
	this.port = port;
    }

    public void run() {
	try {
	    GroebnerBaseDistributed.DIRPGBClient(host, port);
	} catch (IOException ignored) {
	}
    }

}