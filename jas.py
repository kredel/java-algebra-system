#
# jython interface to jas.

from java.lang import System
from java.io import StringReader

from edu.jas.arith  import *
from edu.jas.poly   import *
from edu.jas.ring   import *
from edu.jas.module import *
from edu.jas.util   import *


class Ring:

    def __init__(self,str):
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(sr);
        self.pset = tok.nextPolynomialSet();
        self.vars = self.pset.vars;
        self.tord = self.pset.tord;

    def __str__(self):
        return str(self.pset);


class Ideal:

    def __init__(self,ring,str):
        self.ring = ring;
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(ring.pset.vars,ring.pset.tord,sr);
        self.list = tok.nextPolynomialList();
        self.pset = PolynomialList(ring.pset.vars,ring.pset.tord,self.list);

    def __str__(self):
        return str(self.pset);

    def GB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = GroebnerBase.DIRPGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = PolynomialList(s.vars,s.tord,G);
        return g;

    def parGB(self,thread=2):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = GroebnerBaseParallel.DIRPGB(F,thread);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = PolynomialList(s.vars,s.tord,G);
        return g;

    def distGB(self,thread=2,machine="util/machines.localhost",port=7114):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        # G = GroebnerBaseDistributed.DIRPGB(F,thread);
        G = GBDist().execute(F,thread,machine,port);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = PolynomialList(s.vars,s.tord,G);
        return g;

    def distClient(self,port=8114):
        s = self.pset;
        es = ExecutableServer( port );
        es.init();
        return None;




class SolvRing:
    pset = None;

    def __init__(self,str):
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(sr);
        self.pset = tok.nextSolvablePolynomialSet();

    def __str__(self):
        return str(self.pset);


class SolvIdeal:

    def __init__(self,ring,str):
        self.ring = ring;
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(ring.pset.vars,ring.pset.tord,sr,ring.pset.table);
        self.list = tok.nextSolvablePolynomialList();
        self.pset = PolynomialList(ring.pset.vars,ring.pset.tord,self.list,ring.pset.table);

    def __str__(self):
        return str(self.pset);

    def leftGB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBase.leftGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = PolynomialList(s.vars,s.tord,G,s.table);
        return g;

    def twosidedGB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBase.twosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        g = PolynomialList(s.vars,s.tord,G,s.table);
        return g;




class Module:

    def __init__(self,str):
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(sr);
        self.mset = tok.nextSubModuleSet();
        self.vars = self.mset.vars;
        self.tord = self.mset.tord;

    def __str__(self):
        return str(self.mset);


class SubModule:

    def __init__(self,module,str):
        self.module = module;
        sr = StringReader( str );
        tok = OrderedPolynomialTokenizer(module.mset.vars,module.mset.tord,sr);
        self.list = tok.nextSubModuleList();
        self.mset = ModuleList(module.mset.vars,module.mset.tord,self.list);
        self.cols = self.mset.list[0].size();
        #print "cols = %s" % self.cols;
        self.pset = self.mset.getPolynomialList();

    def __str__(self):
        return str(self.mset) + "\n\n" + str(self.pset);

    def GB(self):
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = GroebnerBase.DIRPGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        self.pset = PolynomialList(s.vars,s.tord,G);
        self.mset = ModuleList.getModuleList(self.cols,self.pset);
        return self;
