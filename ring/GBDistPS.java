/*
 * $Id$
 */

package edu.jas.ring;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.io.FileNotFoundException;

import org.apache.log4j.BasicConfigurator;

import edu.jas.util.RemoteExecutable;
import edu.jas.util.ExecutableChannels;

  /**
   * Setup to run a distributed GB example.
   * @author Heinz Kredel
   */

public class GBDistPS {

  /**
   * Execute a distributed GB example.
   * Distribute clients and start master.
   */

    public ArrayList execute(List P, int threads, String mfile, int port) {

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

	GBClientPS gbc = new GBClientPS( ec.getMasterHost(), ec.getMasterPort() );
	try {
	    for ( int i = 0; i < numc; i++ ) {
	        ec.send( i, gbc );
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	    return G;
	}

	G = (ArrayList)P;
        try {
            G = GroebnerBaseDistributedPS.DIRPGBServer( P, threads, port );
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

class GBClientPS implements RemoteExecutable {

    String host;
    int port;

    public GBClientPS(String host, int port) {
	this.host = host;
	this.port = port;
    }

    public void run() {
	try {
	    GroebnerBaseDistributedPS.DIRPGBClient(host, port);
	} catch (IOException ignored) {
	}
    }

}