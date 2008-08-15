'''jython interface to JAS.
'''

# $Id$

from java.lang           import System
from java.io             import StringReader
from java.util           import ArrayList

from edu.jas.structure   import *
from edu.jas.arith       import *
from edu.jas.poly        import *
from edu.jas.ring        import *
from edu.jas.module      import *
from edu.jas.vector      import *
from edu.jas.application import *
from edu.jas.util        import *
from edu.jas.ufd         import *
from edu.jas             import *
from edu                 import *
#PrettyPrint.setInternal();
from edu.jas.kern        import ComputerThreads;

from org.apache.log4j    import BasicConfigurator;

def startLog():
    '''Configure the log4j system and start logging.
    '''
    BasicConfigurator.configure();

def terminate():
    '''Terminate the running thread pools.
    '''
    ComputerThreads.terminate();


class Ring:
    '''Represents a JAS polynomial ring: GenPolynomialRing.

    Methods to create ideals and ideals with parametric coefficients.
    '''

    def __init__(self,ringstr="",ring=None):
        '''Ring constructor.
        '''
        if ring == None:
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(sr);
           self.pset = tok.nextPolynomialSet();
           self.ring = self.pset.ring;
        else:
           self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring);

    def ideal(self,ringstr="",list=None):
        '''Create an ideal.
        '''
        return Ideal(self,ringstr,list);

    def paramideal(self,ringstr="",list=None,gbsys=None):
        '''Create an ideal in a polynomial ring with parameter coefficients.
        '''
        return ParamIdeal(self,ringstr,list,gbsys);

    def gens(self):
        '''Get list of generators of the ring.
        '''
        return self.ring.univariateList();


class Ideal:
    '''Represents a JAS polynomial ideal: PolynomialList and Ideal.

    Methods for Groebner bases, ideal sum, intersection and others.
    '''

    def __init__(self,ring,ringstr="",list=None):
        '''Ideal constructor.
        '''
        self.ring = ring;
        if list == None:
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(ring.pset.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = list;
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset);

    def paramideal(self):
        '''Create an ideal in a polynomial ring with parameter coefficients.
        '''
        return ParamIdeal(self.ring,"",self.list);

    def GB(self):
        '''Compute a Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            G = GroebnerBaseSeq().GB(F);
        else:
            v = None;
            try:
                v = cofac.vars;
            except:
                pass
            if v == None:
                G = GroebnerBasePseudoSeq(cofac).GB(F);
            else:
                G = GroebnerBasePseudoRecSeq(cofac).GB(F);
        t = System.currentTimeMillis() - t;
        print "sequential executed in %s ms" % t; 
        return Ideal(self.ring,"",G);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = GroebnerBaseSeq().isGB(F);
        t = System.currentTimeMillis() - t;
        print "isGB executed in %s ms" % t; 
        return b;

    def parGB(self,th):
        '''Compute in parallel a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        bbpar = GroebnerBaseSeqPairParallel(th);
        t = System.currentTimeMillis();
        G = bbpar.GB(F);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        print "parallel-new %s executed in %s ms" % (th, t); 
        return Ideal(self.ring,"",G);

    def parOldGB(self,th):
        '''Compute in parallel a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        bbpar = GroebnerBaseParallel(th);
        t = System.currentTimeMillis();
        G = bbpar.GB(F);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        print "parallel-old %s executed in %s ms" % (th, t); 
        return Ideal(self.ring,"",G);

    def distGB(self,th=2,machine="examples/machines.localhost",port=7114):
        '''Compute on a distributed system a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        # G = GroebnerBaseDistributed.Server(F,th);
        #G = GBDist(th,machine,port).execute(F);
        gbd = GBDist(th,machine,port);
        t1 = System.currentTimeMillis();
        G = gbd.execute(F);
        t1 = System.currentTimeMillis() - t1;
        gbd.terminate(0);
        t = System.currentTimeMillis() - t;
        print "distributed %s executed in %s ms (%s ms start-up)" % (th,t1,t-t1); 
        return Ideal(self.ring,"",G);

    def distClient(self,port=8114):
        '''Client for a distributed computation.
        '''
        s = self.pset;
        es = ExecutableServer( port );
        es.init();
        return None;

    def NF(self,reducer):
        '''Compute a normal form of this ideal with respect to reducer.
        '''
        s = self.pset;
        F = s.list;
        G = reducer.list;
        t = System.currentTimeMillis();
        N = ReductionSeq().normalform(G,F);
        t = System.currentTimeMillis() - t;
        print "sequential executed in %s ms" % t; 
        return Ideal(self.ring,"",N);

    def intersect(self,ring):
        '''Compute the intersection of this and the given polynomial ring.
        '''
        s = jas.application.Ideal(self.pset);
        N = s.intersect(ring.ring);
        return Ideal(self.ring,"",N.getList());

    def sum(self,other):
        '''Compute the sum of this and the ideal.
        '''
        s = jas.application.Ideal(self.pset);
        t = jas.application.Ideal(other.pset);
        N = s.sum( t );
        return Ideal(self.ring,"",N.getList());

    def optimize(self):
        '''Optimize the term order on the variables.
        '''
        p = self.pset;
        o = TermOrderOptimization.optimizeTermOrder(p);
        r = Ring("",o.ring);
        return Ideal(r,"",o.list);

    def toInteger(self):
        '''Convert rational coefficients to integer coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        ri = GenPolynomialRing( BigInteger(), r.nvar, r.tord, r.vars );
        pi = PolyUtil.integerFromRationalCoefficients(ri,l);
        r = Ring("",ri);
        return Ideal(r,"",pi);

    def toModular(self,mf):
        '''Convert integer coefficients to modular coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        rm = GenPolynomialRing( mf, r.nvar, r.tord, r.vars );
        pm = PolyUtil.fromIntegerCoefficients(rm,l);
        r = Ring("",rm);
        return Ideal(r,"",pm);

    def squarefreeFactors(self):
        '''Compute squarefree factors of first polynomial.
        '''
        s = self.pset;
        F = s.list;
        p = F[0]; # only first polynomial
        t = System.currentTimeMillis();
        f = GreatestCommonDivisorSubres().squarefreeFactors(p);
        t = System.currentTimeMillis() - t;
        #print "squarefee part %s " % f;
        #S = ArrayList();
        #S.add(f);
        print "squarefee factors executed in %s ms" % t; 
        return f;


class ParamIdeal:
    '''Represents a JAS polynomial ideal with polynomial coefficients.

    Methods to compute comprehensive Groebner bases.
    '''

    def __init__(self,ring,ringstr="",list=None,gbsys=None):
        '''Parametric ideal constructor.
        '''
        self.ring = ring;
        if list == None and ringstr!= None:
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(ring.pset.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = list;
        self.gbsys = gbsys;
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        if self.gbsys == None:
            return str(self.pset);
        else:
            return str(self.gbsys);
#            return str(self.pset) + "\n" + str(self.gbsys);

    def optimizeCoeff(self):
        '''Optimize the term order on the variables of the coefficients.
        '''
        p = self.pset;
        o = TermOrderOptimization.optimizeTermOrderOnCoefficients(p);
        r = Ring("",o.ring);
        return ParamIdeal(r,"",o.list);

    def optimizeCoeffQuot(self):
        '''Optimize the term order on the variables of the quotient coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        q = r.coFac;
        c = q.ring;
        rc = GenPolynomialRing( c, r.nvar, r.tord, r.vars );
        #print "rc = ", rc;        
        lp = PolyUfdUtil.integralFromQuotientCoefficients(rc,l);
        #print "lp = ", lp;
        pp = PolynomialList(rc,lp);
        #print "pp = ", pp;        
        oq = TermOrderOptimization.optimizeTermOrderOnCoefficients(pp);
        oor = oq.ring;
        qo = oor.coFac;
        cq = QuotientRing( qo );
        rq = GenPolynomialRing( cq, r.nvar, r.tord, r.vars );
        #print "rq = ", rq;        
        o = PolyUfdUtil.quotientFromIntegralCoefficients(rq,oq.list);
        r = Ring("",rq);
        return ParamIdeal(r,"",o);

    def toIntegralCoeff(self):
        '''Convert rational function coefficients to integral function coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        q = r.coFac;
        c = q.ring;
        rc = GenPolynomialRing( c, r.nvar, r.tord, r.vars );
        #print "rc = ", rc;        
        lp = PolyUfdUtil.integralFromQuotientCoefficients(rc,l);
        #print "lp = ", lp;
        r = Ring("",rc);
        return ParamIdeal(r,"",lp);

    def toModularCoeff(self,mf):
        '''Convert integral function coefficients to modular function coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        c = r.coFac;
        #print "c = ", c;
        cm = GenPolynomialRing( mf, c.nvar, c.tord, c.vars );
        #print "cm = ", cm;
        rm = GenPolynomialRing( cm, r.nvar, r.tord, r.vars );
        #print "rm = ", rm;
        pm = PolyUfdUtil.fromIntegerCoefficients(rm,l);
        r = Ring("",rm);
        return ParamIdeal(r,"",pm);

    def toQuotientCoeff(self):
        '''Convert integral function coefficients to rational function coefficients.
        '''
        p = self.pset;
        l = p.list;
        r = p.ring;
        c = r.coFac;
        #print "c = ", c;
        q = QuotientRing(c);
        #print "q = ", q;
        qm = GenPolynomialRing( q, r.nvar, r.tord, r.vars );
        #print "qm = ", qm;
        pm = PolyUfdUtil.quotientFromIntegralCoefficients(qm,l);
        r = Ring("",qm);
        return ParamIdeal(r,"",pm);

    def GB(self):
        '''Compute a Groebner base.
        '''
        I = Ideal(self.ring,"",self.pset.list);
        g = I.GB();
        return ParamIdeal(g.ring,"",g.pset.list);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        I = Ideal(self.ring,"",self.pset.list);
        return I.isGB();

    def CGB(self):
        '''Compute a comprehensive Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        if self.gbsys == None:
            self.gbsys = ComprehensiveGroebnerBaseSeq(self.ring.ring.coFac).GBsys(F);
        G = self.gbsys.getCGB();
        t = System.currentTimeMillis() - t;
        print "sequential comprehensive executed in %s ms" % t; 
        return ParamIdeal(self.ring,"",G,self.gbsys);

    def CGBsystem(self):
        '''Compute a comprehensive Groebner system.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        S = ComprehensiveGroebnerBaseSeq(self.ring.ring.coFac).GBsys(F);
        t = System.currentTimeMillis() - t;
        print "sequential comprehensive system executed in %s ms" % t; 
        return ParamIdeal(self.ring,None,F,S);

    def isCGB(self):
        '''Test if this is a comprehensive Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = ComprehensiveGroebnerBaseSeq(self.ring.ring.coFac).isGB(F);
        t = System.currentTimeMillis() - t;
        print "isCGB executed in %s ms" % t; 
        return b;

    def isCGBsystem(self):
        '''Test if this is a comprehensive Groebner system.
        '''
        s = self.pset;
        S = self.gbsys;
        t = System.currentTimeMillis();
        b = ComprehensiveGroebnerBaseSeq(self.ring.ring.coFac).isGBsys(S);
        t = System.currentTimeMillis() - t;
        print "isCGBsystem executed in %s ms" % t; 
        return b;

    def regularRepresentation(self):
        '''Convert Groebner system to a representation with regular ring coefficents.
        '''
        if self.gbsys == None:
            return None;
        G = PolyUtilApp.toProductRes(self.gbsys.list);
        ring = Ring(None,G[0].ring);
        return ParamIdeal(ring,None,G);

    def regularGB(self):
        '''Compute a Groebner base over a regular ring.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = RGroebnerBasePseudoSeq(self.ring.ring.coFac).GB(F);
        t = System.currentTimeMillis() - t;
        print "sequential regular GB executed in %s ms" % t; 
        return ParamIdeal(self.ring,None,G);

    def isRegularGB(self):
        '''Test if this is Groebner base over a regular ring.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = RGroebnerBasePseudoSeq(self.ring.ring.coFac).isGB(F);
        t = System.currentTimeMillis() - t;
        print "isRegularGB executed in %s ms" % t; 
        return b;

    def stringSlice(self):
        '''Get each component (slice) of regular ring coefficients separate.
        '''
        s = self.pset;
        b = PolyUtilApp.productToString(s);
        return b;


class SolvableRing:
    '''Represents a JAS solvable polynomial ring: GenSolvablePolynomialRing.

    Has a method to create solvable ideals.
    '''

    def __init__(self,ringstr="",ring=None):
        '''Solvable polynomial ring constructor.
        '''
        if ring == None:
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(sr);
           self.pset = tok.nextSolvablePolynomialSet();
           self.ring = self.pset.ring;
        else:
           self.ring = ring;
        if not self.ring.isAssociative():
           print "warning: ring is not associative";

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring);

    def ideal(self,ringstr="",list=None):
        '''Create a solvable ideal.
        '''
        return SolvableIdeal(self,ringstr,list);


class SolvableIdeal:
    '''Represents a JAS solvable polynomial ideal.

    Methods for left, right two-sided Groebner basees and others.
    '''

    def __init__(self,ring,ringstr="",list=None):
        '''Constructor for an ideal in a solvable polynomial ring.
        '''
        self.ring = ring;
        if list == None:
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(ring.ring,sr);
           self.list = tok.nextSolvablePolynomialList();
        else:
           self.list = list;
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset);

    def leftGB(self):
        '''Compute a left Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().leftGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isLeftGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().twosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isTwosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().rightGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isRightGB(F);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;

    def intersect(self,ring):
        '''Compute the intersection of this and the polynomial ring.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        N = s.intersect(ring.ring);
        return SolvableIdeal(self.ring,"",N.getList());

    def sum(self,other):
        '''Compute the sum of this and the ideal.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        t = jas.application.SolvableIdeal(other.pset);
        N = s.sum( t );
        return SolvableIdeal(self.ring,"",N.getList());

    def parLeftGB(self,th):
        '''Compute a left Groebner base in parallel.
        '''
        s = self.pset;
        F = s.list;
        bbpar = SolvableGroebnerBaseParallel(th);
        t = System.currentTimeMillis();
        G = bbpar.leftGB(F);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        print "parallel %s executed in %s ms" % (th, t); 
        return Ideal(self.ring,"",G);

    def parTwosidedGB(self,th):
        '''Compute a two-sided Groebner base in parallel.
        '''
        s = self.pset;
        F = s.list;
        bbpar = SolvableGroebnerBaseParallel(th);
        t = System.currentTimeMillis();
        G = bbpar.twosidedGB(F);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        print "parallel %s executed in %s ms" % (th, t); 
        return Ideal(self.ring,"",G);


class Module:
    '''Represents a JAS module over a polynomial ring.

    Method to create sub-modules.
    '''

    def __init__(self,modstr="",ring=None):
        '''Module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(sr);
           self.mset = tok.nextSubModuleSet();
        else:
           self.mset = ModuleList(ring,None);
        self.ring = self.mset.ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset);

    def submodul(self,modstr="",list=None):
        '''Create a sub-module.
        '''
        return Submodule(self,modstr,list);


class SubModule:
    '''Represents a JAS sub-module over a polynomial ring.

    Methods to compute Groebner bases.
    '''

    def __init__(self,module,modstr="",list=None):
        '''Constructor for a sub-module.
        '''
        self.module = module;
        if list == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(module.ring,sr);
           self.list = tok.nextSubModuleList();
        else:
           self.list = list;
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        self.rows = self.mset.rows;
        #print "cols = %s" % self.cols;
        #self.pset = self.mset.getPolynomialList();

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset); # + "\n\n" + str(self.pset);

    def GB(self):
        '''Compute a Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModGroebnerBaseAbstract().GB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SubModule(self.module,"",G.list);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModGroebnerBaseAbstract().isGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;



class SolvableModule:
    '''Represents a JAS module over a solvable polynomial ring.

    Method to create solvable sub-modules.
    '''

    def __init__(self,modstr="",ring=None):
        '''Solvable module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(sr);
           self.mset = tok.nextSolvableSubModuleSet();
        else:
           self.mset = ModuleList(ring,None);
        self.ring = self.mset.ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset);

    def solvsubmodul(self,modstr="",list=None):
        '''Create a solvable sub-module.
        '''
        return Submodule(self,modstr,list);


class SolvableSubModule:
    '''Represents a JAS sub-module over a solvable polynomial ring.

    Methods to compute left, right and two-sided Groebner bases.
    '''

    def __init__(self,module,modstr="",list=None):
        '''Constructor for sub-module over a solvable polynomial ring.
        '''
        self.module = module;
        if list == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(module.ring,sr);
           self.list = tok.nextSolvableSubModuleList();
        else:
           self.list = list;
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        self.rows = self.mset.rows;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset); # + "\n\n" + str(self.pset);

    def leftGB(self):
        '''Compute a left Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBaseAbstract().leftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract().isLeftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBaseAbstract().twosidedGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract().isTwosidedGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBaseAbstract().rightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract().isRightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed in %s ms" % t; 
        return b;
