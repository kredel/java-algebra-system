#
# jython interface to jas.
# $Id$
#

from java.lang import System
from java.io import StringReader

from org.apache.log4j import BasicConfigurator;
#BasicConfigurator.configure();

from edu.jas.structure import *
from edu.jas.arith     import *
from edu.jas.poly      import *
from edu.jas.ring      import *
from edu.jas.module    import *
from edu.jas.util      import *

#PrettyPrint.setInternal();


class Ring:

    def __init__(self,ringstr):
        sr = StringReader( ringstr );
        tok = GenPolynomialTokenizer(sr);
        self.pset = tok.nextPolynomialSet();
        self.ring = self.pset.ring;

    def __str__(self):
        return str(self.ring);



class Ideal:

    def __init__(self,ring,ringstr):
        self.ring = ring;
        sr = StringReader( ringstr );
        tok = GenPolynomialTokenizer(ring.pset.ring,sr);
        self.list = tok.nextPolynomialList();
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        return str(self.pset);

    def GB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = GroebnerBaseSeq().GB(F);
        t = System.currentTimeMillis() - t;
        print "sequential executed in %s ms" % t; 
        g = OrderedPolynomialList(s.ring,G);
        return g;
#        return Ideal(self.ring,str(g));

    def parGB(self,th):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = GroebnerBaseParallel(th).GB(F);
        t = System.currentTimeMillis() - t;
        print "parallel %s executed in %s ms" % (th, t); 
        g = OrderedPolynomialList(s.ring,G);
        return g;

    def distGB(self,th=2,machine="util/machines.localhost",port=7114):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        # G = GroebnerBaseDistributed.Server(F,th);
        G = GBDist(th,machine,port).execute(F);
        t = System.currentTimeMillis() - t;
        print "distributed %s executed in %s ms" % (th,t); 
        g = OrderedPolynomialList(s.ring,G);
        return g;

    def distClient(self,port=8114):
        s = self.pset;
        es = ExecutableServer( port );
        es.init();
        return None;

    def NF(self,reducer):
        s = self.pset;
        F = s.list;
        G = reducer.list;
        t = System.currentTimeMillis();
        N = ReductionSeq().normalform(G,F);
        t = System.currentTimeMillis() - t;
        print "sequential executed in %s ms" % t; 
        n = OrderedPolynomialList(s.ring,N);
        return n;


class SolvableRing:

    def __init__(self,ringstr):
        sr = StringReader( ringstr );
        tok = GenPolynomialTokenizer(sr);
        self.pset = tok.nextSolvablePolynomialSet();
        self.ring = self.pset.ring;

    def __str__(self):
        return str(self.ring);


class SolvableIdeal:

    def __init__(self,ring,ringstr):
        self.ring = ring;
        sr = StringReader( ringstr );
        tok = GenPolynomialTokenizer(ring.ring,sr);
        self.list = tok.nextSolvablePolynomialList();
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        return str(self.pset);

    def leftGB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().leftGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = OrderedPolynomialList(s.ring,G);
        return g;

    def twosidedGB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().twosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = OrderedPolynomialList(s.ring,G);
        return g;



class Module:

    def __init__(self,modstr):
        sr = StringReader( modstr );
        tok = GenPolynomialTokenizer(sr);
        self.mset = tok.nextSubModuleSet();
        self.ring = self.mset.ring;
        self.rows = self.mset.rows;

    def __str__(self):
#        return "hi %s[%s]" % self.ring;
#        return str(self.ring);
        return str(self.mset);


class SubModule:

    def __init__(self,module,modstr):
        self.module = module;
        sr = StringReader( modstr );
        tok = GenPolynomialTokenizer(module.ring,sr);
        self.list = tok.nextSubModuleList();
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        self.cols = self.mset.cols;
        #print "cols = %s" % self.cols;
        self.pset = self.mset.getPolynomialList();

    def __str__(self):
        return str(self.mset); # + "\n\n" + str(self.pset);

    def GB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = ModGroebnerBase().GB(self.cols,F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        self.pset = PolynomialList(s.ring,G);
        self.mset = self.pset.getModuleList(self.cols);
        return self;



class SolvableModule:

    def __init__(self,modstr):
        sr = StringReader( modstr );
        tok = GenPolynomialTokenizer(sr);
        self.mset = tok.nextSolvableSubModuleSet();
        self.ring = self.mset.ring;
        self.rows = self.mset.rows;

    def __str__(self):
        return str(self.mset);


class SolvableSubModule:

    def __init__(self,module,modstr):
        self.module = module;
        sr = StringReader( modstr );
        tok = GenPolynomialTokenizer(module.ring,sr);
        self.list = tok.nextSolvableSubModuleList();
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        #print "cols = %s" % self.cols;
        self.pset = self.mset.getPolynomialList();

    def __str__(self):
        return str(self.mset); # + "\n\n" + str(self.pset);

    def leftGB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBase().leftGB(self.cols,F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        self.pset = PolynomialList(s.ring,G);
        self.mset = self.pset.getModuleList(self.cols);
        return self;
