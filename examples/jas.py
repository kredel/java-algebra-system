'''jython interface to JAS.
'''

# $Id$

from java.lang           import System
from java.io             import StringReader
from java.util           import ArrayList

from org.apache.log4j    import BasicConfigurator;

from edu.jas.structure   import RingElem, RingFactory, Power
from edu.jas.arith       import BigInteger, BigRational, BigComplex, BigDecimal,\
                                ModInteger, ModIntegerRing, BigQuaternion, BigOctonion,\
                                Product, ProductRing
from edu.jas.poly        import GenPolynomial, GenPolynomialRing,\
                                GenSolvablePolynomial, GenSolvablePolynomialRing,\
                                GenPolynomialTokenizer, OrderedPolynomialList, PolyUtil,\
                                TermOrderOptimization, TermOrder, PolynomialList,\
                                AlgebraicNumber, AlgebraicNumberRing,\
                                OrderedModuleList, ModuleList,\
                                Complex, ComplexRing
from edu.jas.ps          import UnivPowerSeries, UnivPowerSeriesRing,\
                                UnivPowerSeriesMap, Coefficients, \
                                MultiVarPowerSeries, MultiVarPowerSeriesRing,\
                                MultiVarPowerSeriesMap, MultiVarCoefficients,\
                                StandardBaseSeq
from edu.jas.gb          import DGroebnerBaseSeq, EGroebnerBaseSeq,\
                                GroebnerBaseDistributed, GBDist, GroebnerBaseParallel,\
                                GroebnerBaseSeq, GroebnerBaseSeqPairSeq,\
                                ReductionSeq, GroebnerBaseSeqPairParallel,\
                                SolvableGroebnerBaseParallel, SolvableGroebnerBaseSeq
from edu.jas.gbufd       import GroebnerBasePseudoRecSeq, GroebnerBasePseudoSeq,\
                                RGroebnerBasePseudoSeq, RGroebnerBaseSeq, RReductionSeq
from edu.jas.gbmod       import ModGroebnerBaseAbstract, ModSolvableGroebnerBaseAbstract,\
                                SolvableSyzygyAbstract, SyzygyAbstract
from edu.jas.vector      import GenVector, GenVectorModul,\
                                GenMatrix, GenMatrixRing
from edu.jas.application import PolyUtilApp, Residue, ResidueRing, Ideal,\
                                Local, LocalRing, IdealWithRealAlgebraicRoots,\
                                ComprehensiveGroebnerBaseSeq
from edu.jas.kern        import ComputerThreads, StringUtil
from edu.jas.ufd         import GreatestCommonDivisor, PolyUfdUtil, GCDFactory,\
                                FactorFactory, SquarefreeFactory, Quotient, QuotientRing
from edu.jas.root        import RealRootsSturm, Interval, RealAlgebraicNumber, RealAlgebraicRing,\
                                ComplexRootsSturm, Rectangle
from edu.jas.integrate   import ElementaryIntegration
from edu.jas.util        import ExecutableServer
from edu.jas             import structure, arith, poly, ps, gb, gbmod, vector,\
                                application, util, ufd
from edu                 import jas
#PrettyPrint.setInternal();

from org.python.core     import PyInstance, PyJavaInstance, PyList, PyTuple,\
                                PyInteger, PyLong, PyFloat, PyString


def startLog():
    '''Configure the log4j system and start logging.
    '''
    BasicConfigurator.configure();

def terminate():
    '''Terminate the running thread pools.
    '''
    ComputerThreads.terminate();

def noThreads():
    '''Turn off automatic parallel threads usage.
    '''
    print "nt = ", ComputerThreads.NO_THREADS;
    ComputerThreads.setNoThreads(); #NO_THREADS = #0; #1; #True;
    print "nt = ", ComputerThreads.NO_THREADS;


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
        self.engine = GCDFactory.getProxy(self.ring.coFac);
        try:
            self.sqf = SquarefreeFactory.getImplementation(self.ring.coFac);
#            print "sqf: ", self.sqf;
#        except Exception, e:
#            print "error " + str(e)
        except:
            pass
        try:
            self.factor = FactorFactory.getImplementation(self.ring.coFac);
            #print "factor: ", self.factor;
        except:
            pass
#        except Exception, e:
#            print "error " + str(e)

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

    def ideal(self,ringstr="",list=None):
        '''Create an ideal.
        '''
        return Ideal(self,ringstr,list=list);

    def paramideal(self,ringstr="",list=None,gbsys=None):
        '''Create an ideal in a polynomial ring with parameter coefficients.
        '''
        return ParamIdeal(self,ringstr,list,gbsys);

    def gens(self):
        '''Get list of generators of the polynomial ring.
        '''
        L = self.ring.generators();
        N = [ RingElem(e) for e in L ];
        return N;

    def one(self):
        '''Get the one of the polynomial ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the polynomial ring.
        '''
        return RingElem( self.ring.getZERO() );

    def random(self,k=5,l=7,d=3,q=0.3):
        '''Get a random polynomial.
        '''
        r = self.ring.random(k,l,d,q);
        if self.ring.coFac.isField():
            r = r.monic();
        return RingElem( r );

    def element(self,polystr):
        '''Create an element from a string.
        '''
        I = Ideal(self, "( " + polystr + " )");
        list = I.pset.list;
        if len(list) > 0:
            return RingElem( list[0] );

    def gcd(self,a,b):
        '''Compute the greatest common divisor of a and b.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        else:
            b = self.element( str(b) );
            b = b.elem;
        return RingElem( self.engine.gcd(a,b) );

    def squarefreeFactors(self,a):
        '''Compute squarefree factors of polynomial.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        cf = self.ring.coFac;
        if cf.getClass().getSimpleName() == "GenPolynomialRing":
            e = self.sqf.recursiveSquarefreeFactors( a );
        else:
            e = self.sqf.squarefreeFactors( a );
        L = {};
        for a in e.keySet():
            i = e.get(a);
            L[ RingElem( a ) ] = i;
        return L;

    def factors(self,a):
        '''Compute irreducible factorization for modular, integer,
        rational number and algebriac number coefficients.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        try:
            cf = self.ring.coFac;
            if cf.getClass().getSimpleName() == "GenPolynomialRing":
                e = self.factor.recursiveFactors( a );
            else:
                e = self.factor.factors( a );
            L = {};
            for a in e.keySet():
                i = e.get(a);
                L[ RingElem( a ) ] = i;
            return L;
        except Exception, e:
            print "error " + str(e)
            return None

    def factorsAbsolute(self,a):
        '''Compute absolute irreducible factorization for (modular,)
        rational number coefficients.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        try:
            L = self.factor.factorsAbsolute( a );
##             L = {};
##             for a in e.keySet():
##                 i = e.get(a);
##                 L[ RingElem( a ) ] = i;
            return L;
        except Exception, e:
            print "error in factorsAbsolute " + str(e)
            return None

    def realRoots(self,a,eps=None):
        '''Compute real roots of univariate polynomial.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            if eps == None:
                R = RealRootsSturm().realRoots( a );
            else:
##                 R = RealRootsSturm().realRoots( a, eps );
##                 R = [ r.toDecimal() for r in R ];
                R = RealRootsSturm().approximateRoots(a,eps);
                R = [ RingElem(r) for r in R ];
            return R;
        except Exception, e:
            print "error " + str(e)
            return None

    def complexRoots(self,a,eps=None):
        '''Compute complex roots of univariate polynomial.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            if eps == None:
                R = ComplexRootsSturm(a.ring.coFac).complexRoots( a );
                #R = [ r.centerApprox() for r in R ];
            else:
##                 R = ComplexRootsSturm(a.ring.coFac).complexRoots( a, eps );
##                 R = [ r.centerApprox() for r in R ];
                R = ComplexRootsSturm(a.ring.coFac).approximateRoots( a, eps );
                R = [ RingElem(r) for r in R ];
            return R;
        except Exception, e:
            print "error " + str(e)
            return None

    def integrate(self,a):
        '''Integrate (univariate) rational function.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( str(a) );
            a = a.elem;
        cf = self.ring;
        try:
            cf = cf.ring;
        except:
            pass;
        integrator = ElementaryIntegration(cf.coFac);
        ei = integrator.integrate(a); 
        return ei;

    def powerseriesRing(self):
        '''Get a power series ring from this ring.
        '''
        pr = MultiVarPowerSeriesRing(self.ring);
        return MultiSeriesRing(ring=pr);


class Ideal:
    '''Represents a JAS polynomial ideal: PolynomialList and Ideal.

    Methods for Groebner bases, ideal sum, intersection and others.
    '''

    def __init__(self,ring,polystr="",list=None):
        '''Ideal constructor.
        '''
        self.ring = ring;
        if list == None:
           sr = StringReader( polystr );
           tok = GenPolynomialTokenizer(ring.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = pylist2arraylist(list,rec=1);
        self.pset = OrderedPolynomialList(ring.ring,self.list);
        self.roots = None;
        self.prime = None;
        self.primary = None;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset.toScript());

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
        print "sequential GB executed in %s ms" % t; 
        return Ideal(self.ring,"",G);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            b = GroebnerBaseSeq().isGB(F);
        else:
            v = None;
            try:
                v = cofac.vars;
            except:
                pass
            if v == None:
                b = GroebnerBasePseudoSeq(cofac).isGB(F);
            else:
                b = GroebnerBasePseudoRecSeq(cofac).isGB(F);
        t = System.currentTimeMillis() - t;
        print "isGB executed in %s ms" % t; 
        return b;


    def eGB(self):
        '''Compute an e-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            G = GroebnerBaseSeq().GB(F);
        else:
            G = EGroebnerBaseSeq().GB(F)
        t = System.currentTimeMillis() - t;
        print "sequential e-GB executed in %s ms" % t; 
        return Ideal(self.ring,"",G);


    def iseGB(self):
        '''Test if this is an e-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            b = GroebnerBaseSeq().isGB(F);
        else:
            b = EGroebnerBaseSeq().isGB(F)
        t = System.currentTimeMillis() - t;
        print "is e-GB test executed in %s ms" % t; 
        return b;


    def dGB(self):
        '''Compute an d-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            G = GroebnerBaseSeq().GB(F);
        else:
            G = DGroebnerBaseSeq().GB(F)
        t = System.currentTimeMillis() - t;
        print "sequential d-GB executed in %s ms" % t; 
        return Ideal(self.ring,"",G);


    def isdGB(self):
        '''Test if this is a d-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            b = GroebnerBaseSeq().isGB(F);
        else:
            b = DGroebnerBaseSeq().isGB(F)
        t = System.currentTimeMillis() - t;
        print "is d-GB test executed in %s ms" % t; 
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
        print "sequential NF executed in %s ms" % t; 
        return Ideal(self.ring,"",N);

    def intersectRing(self,ring):
        '''Compute the intersection of this and the given polynomial ring.
        '''
        s = jas.application.Ideal(self.pset);
        N = s.intersect(ring.ring);
        return Ideal(ring,"",N.getList());

    def intersect(self,id2):
        '''Compute the intersection of this and the given ideal.
        '''
        s1 = jas.application.Ideal(self.pset);
        s2 = jas.application.Ideal(id2.pset);
        N = s1.intersect(s2);
        return Ideal(self.ring,"",N.getList());

    def eliminateRing(self,ring):
        '''Compute the elimination ideal of this and the given polynomial ring.
        '''
        s = jas.application.Ideal(self.pset);
        N = s.eliminate(ring.ring);
        r = Ring( ring=N.getRing() );
        return Ideal(r,"",N.getList());

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

    def realRoots(self):
        '''Compute real roots of 0-dim ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.roots = jas.application.PolyUtilApp.realAlgebraicRoots(I);
        for R in self.roots:
            R.doDecimalApproximation();
        return self.roots;

    def realRootsPrint(self):
        '''Print decimal approximation of real roots of 0-dim ideal.
        '''
        if self.roots == None:
            I = jas.application.Ideal(self.pset);
            self.roots = jas.application.PolyUtilApp.realAlgebraicRoots(I);
            for R in self.roots:
                R.doDecimalApproximation();
        D = [];
        for Ir in self.roots:
            for Dr in Ir.decimalApproximation():
                print str(Dr);
            print;

    def radicalDecomp(self):
        '''Compute radical decomposition of this ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.radical = I.radicalDecomposition();
        return self.radical;

    def complexRoots(self):
        '''Compute complex roots of 0-dim ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.croots = jas.application.PolyUtilApp.complexAlgebraicRoots(I);
        #for R in self.croots:
        #    R.doDecimalApproximation();
        return self.croots;

    def primeDecomp(self):
        '''Compute prime decomposition of this ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.prime = I.primeDecomposition();
        return self.prime;

    def primaryDecomp(self):
        '''Compute primary decomposition of this ideal.
        '''
        I = jas.application.Ideal(self.pset);
##         if self.prime == None:
##             self.prime = I.primeDecomposition();
        self.primary = I.primaryDecomposition();
        return self.primary;

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

##     def syzygy(self):
##         '''Syzygy of generating polynomials.
##         '''
##         p = self.pset;
##         l = p.list;
##         s = SyzygyAbstract().zeroRelations( l );
##         m = Module("",p.ring);
##         return SubModule(m,"",s);


class ParamIdeal:
    '''Represents a JAS polynomial ideal with polynomial coefficients.

    Methods to compute comprehensive Groebner bases.
    '''

    def __init__(self,ring,polystr="",list=None,gbsys=None):
        '''Parametric ideal constructor.
        '''
        self.ring = ring;
        if list == None and polystr != None:
           sr = StringReader( polystr );
           tok = GenPolynomialTokenizer(ring.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = pylist2arraylist(list,rec=1);
        self.gbsys = gbsys;
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        if self.gbsys == None:
            return str(self.pset.toScript());
        else:
            return str(self.gbsys.toString());
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

    def regularRepresentationBC(self):
        '''Convert Groebner system to a boolean closed representation with regular ring coefficents.
        '''
        if self.gbsys == None:
            return None;
        G = PolyUtilApp.toProductRes(self.gbsys.list);
        ring = Ring(None,G[0].ring);
        res = RReductionSeq();
        G = res.booleanClosure(G);
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


class SolvableRing(Ring):
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
        return str(self.ring.toScript());

    def ideal(self,ringstr="",list=None):
        '''Create a solvable ideal.
        '''
        return SolvableIdeal(self,ringstr,list);

    def one(self):
        '''Get the one of the solvable polynomial ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the solvable polynomial ring.
        '''
        return RingElem( self.ring.getZERO() );

    def element(self,polystr):
        '''Create an element from a string.
        '''
        I = SolvableIdeal(self, "( " + polystr + " )");
        list = I.pset.list;
        if len(list) > 0:
            return RingElem( list[0] );


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
           self.list = pylist2arraylist(list,rec=1);
        self.pset = OrderedPolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset.toScript());

    def leftGB(self):
        '''Compute a left Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().leftGB(F);
        t = System.currentTimeMillis() - t;
        print "executed leftGB in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isLeftGB(F);
        t = System.currentTimeMillis() - t;
        print "isLeftGB executed in %s ms" % t; 
        return b;

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().twosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "executed twosidedGB in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isTwosidedGB(F);
        t = System.currentTimeMillis() - t;
        print "isTwosidedGB executed in %s ms" % t; 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        G = SolvableGroebnerBaseSeq().rightGB(F);
        t = System.currentTimeMillis() - t;
        print "executed rightGB in %s ms" % t; 
        return SolvableIdeal(self.ring,"",G);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq().isRightGB(F);
        t = System.currentTimeMillis() - t;
        print "isRightGB executed in %s ms" % t; 
        return b;

    def intersect(self,ring):
        '''Compute the intersection of this and the polynomial ring.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        N = s.intersect(ring.ring);
        return SolvableIdeal(self.ring,"",N.getList());

    def sum(self,other):
        '''Compute the sum of this and the other ideal.
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
        print "parallel %s leftGB executed in %s ms" % (th, t); 
        return SolvableIdeal(self.ring,"",G);

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
        print "parallel %s twosidedGB executed in %s ms" % (th, t); 
        return SolvableIdeal(self.ring,"",G);


class Module:
    '''Represents a JAS module over a polynomial ring.

    Method to create sub-modules.
    '''

    def __init__(self,modstr="",ring=None,cols=0):
        '''Module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(sr);
           self.mset = tok.nextSubModuleSet();
           if self.mset.cols >= 0:
               self.cols = self.mset.cols;
           else:
               self.cols = cols;
        else:
           self.mset = ModuleList(ring.ring,None);
           self.cols = cols;
        self.ring = self.mset.ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript());

    def submodul(self,modstr="",list=None):
        '''Create a sub-module.
        '''
        return SubModule(self,modstr,list);

    def element(self,modstr):
        '''Create an element from a string.
        '''
        I = SubModule(self, "( " + modstr + " )");
        list = I.mset.list;
        if len(list) > 0:
            return RingElem( list[0] );

    def gens(self):
        '''Get the generators of this module.
        '''
        gm = GenVectorModul(self.ring,self.cols);
        L = gm.generators();
        #for g in L:
        #    print "g = ", str(g);
        N = [ RingElem(e) for e in L ]; # want use val here, but can not
        return N;


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
            if isinstance(list,PyList) or isinstance(list,PyTuple):
                if len(list) != 0:
                    if isinstance(list[0],RingElem):
                        list = [ re.elem for re in list ];
                self.list = pylist2arraylist(list,self.module.ring,rec=2);
            else:
                self.list = list;
        #print "list = ", str(list);
        #e = self.list[0];
        #print "e = ", e;
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        self.rows = self.mset.rows;
        #print "list = %s" % self.list;
        #print "cols = %s" % self.cols;
        #print "mset = %s" % self.mset.toString();
        #print "mset = %s" % self.mset.toScript();
        self.pset = self.mset.getPolynomialList();

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript()); # + "\n\n" + str(self.pset);

    def GB(self):
        '''Compute a Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModGroebnerBaseAbstract().GB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed module GB in %s ms" % t; 
        return SubModule(self.module,"",G.list);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModGroebnerBaseAbstract().isGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isGB executed in %s ms" % t; 
        return b;

##     def isSyzygy(self,g):
##         '''Test if this is a syzygy of the polynomials in g.
##         '''
##         l = self.list;
##         print "l = %s" % l; 
##         print "g = %s" % g; 
##         t = System.currentTimeMillis();
##         z = SyzygyAbstract().isZeroRelation( l, g.list );
##         t = System.currentTimeMillis() - t;
##         print "executed isSyzygy in %s ms" % t; 
##         return z;


class SolvableModule(Module):
    '''Represents a JAS module over a solvable polynomial ring.

    Method to create solvable sub-modules.
    '''

    def __init__(self,modstr="",ring=None,cols=0):
        '''Solvable module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = GenPolynomialTokenizer(sr);
           self.mset = tok.nextSolvableSubModuleSet();
           if self.mset.cols >= 0:
               self.cols = self.mset.cols;
        else:
           self.mset = ModuleList(ring.ring,None);
           self.cols = cols;
        self.ring = self.mset.ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript());

    def submodul(self,modstr="",list=None):
        '''Create a solvable sub-module.
        '''
        return SolvableSubModule(self,modstr,list);

    def element(self,modstr):
        '''Create an element from a string.
        '''
        I = SolvableSubModule(self, "( " + modstr + " )");
        list = I.mset.list;
        if len(list) > 0:
            return RingElem( list[0] );


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
            if isinstance(list,PyList) or isinstance(list,PyTuple):
                self.list = pylist2arraylist(list,self.module.ring,rec=2);
            else:
                self.list = list;
        self.mset = OrderedModuleList(module.ring,self.list);
        self.cols = self.mset.cols;
        self.rows = self.mset.rows;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript()); # + "\n\n" + str(self.pset);

    def leftGB(self):
        '''Compute a left Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBaseAbstract().leftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed left module GB in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract().isLeftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isLeftGB executed in %s ms" % t; 
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
        print "module isTwosidedGB executed in %s ms" % t; 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        t = System.currentTimeMillis();
        G = ModSolvableGroebnerBaseAbstract().rightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed module rightGB in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract().isRightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isRightGB executed in %s ms" % t; 
        return b;


class SeriesRing:
    '''Represents a JAS power series ring: UnivPowerSeriesRing.

    Methods for univariate power series arithmetic.
    '''

    def __init__(self,ringstr="",truncate=None,ring=None,cofac=None,name="z"):
        '''Ring constructor.
        '''
        if ring == None:
            if len(ringstr) > 0:
                sr = StringReader( ringstr );
                tok = GenPolynomialTokenizer(sr);
                pset = tok.nextPolynomialSet();
                ring = pset.ring;
                vname = ring.vars;
                name = vname[0];
                cofac = ring.coFac;
            if isinstance(cofac,RingElem):
                cofac = cofac.elem;
            if truncate == None:
                self.ring = UnivPowerSeriesRing(cofac,name);
            else:
                self.ring = UnivPowerSeriesRing(cofac,truncate,name);
        else:
           self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

    def gens(self):
        '''Get the generators of the power series ring.
        '''
        L = self.ring.generators();
        N = [ RingElem(e) for e in L ];
        return N;

    def one(self):
        '''Get the one of the power series ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the power series ring.
        '''
        return RingElem( self.ring.getZERO() );

    def random(self,n):
        '''Get a random power series.
        '''
        return RingElem( self.ring.random(n) );

    def exp(self):
        '''Get the exponential power series.
        '''
        return RingElem( self.ring.getEXP() );

    def sin(self):
        '''Get the sinus power series.
        '''
        return RingElem( self.ring.getSIN() );

    def cos(self):
        '''Get the cosinus power series.
        '''
        return RingElem( self.ring.getCOS() );

    def tan(self):
        '''Get the tangens power series.
        '''
        return RingElem( self.ring.getTAN() );

    def create(self,ifunc=None,jfunc=None,clazz=None):
        '''Create a power series with given generating function.

        ifunc(int i) must return a value which is used in RingFactory.fromInteger().
        jfunc(int i) must return a value of type ring.coFac.
        clazz must implement the Coefficients abstract class.
        '''
        class coeff( Coefficients ):
            def __init__(self,cofac):
                self.coFac = cofac;
            def generate(self,i):
                if jfunc == None:
                    return self.coFac.fromInteger( ifunc(i) );
                else:
                    return jfunc(i);
        if clazz == None:
            ps = UnivPowerSeries( self.ring, coeff(self.ring.coFac) );
        else:
            ps = UnivPowerSeries( self.ring, clazz );
        return RingElem( ps );

    def fixPoint(self,psmap):
        '''Create a power series as fixed point of the given mapping.

        psmap must implement the UnivPowerSeriesMap interface.
        '''
        ps = self.ring.fixPoint( psmap );
        return RingElem( ps );

    def gcd(self,a,b):
        '''Compute the greatest common divisor of a and b.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        return RingElem( a.gcd(b) );

    def fromPoly(self,a):
        '''Convert a GenPolynomial to a power series.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        return RingElem( self.ring.fromPolynomial(a) );


class MultiSeriesRing:
    '''Represents a JAS power series ring: MultiVarPowerSeriesRing.

    Methods for multivariate power series arithmetic.
    '''

    def __init__(self,ringstr="",truncate=None,ring=None,cofac=None,names=None):
        '''Ring constructor.
        '''
        if ring == None:
            if len(ringstr) > 0:
                sr = StringReader( ringstr );
                tok = GenPolynomialTokenizer(sr);
                pset = tok.nextPolynomialSet();
                ring = pset.ring;
                names = ring.vars;
                cofac = ring.coFac;
            if isinstance(cofac,RingElem):
                cofac = cofac.elem;
            if truncate == None:
                self.ring = MultiVarPowerSeriesRing(cofac,names);
            else:
                self.ring = MultiVarPowerSeriesRing(cofac,len(names),truncate,names);
        else:
           self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

    def gens(self):
        '''Get the generators of the power series ring.
        '''
        L = self.ring.generators();
        N = [ RingElem(e) for e in L ];
        return N;

    def one(self):
        '''Get the one of the power series ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the power series ring.
        '''
        return RingElem( self.ring.getZERO() );

    def random(self,n):
        '''Get a random power series.
        '''
        return RingElem( self.ring.random(n) );

    def exp(self,r):
        '''Get the exponential power series, var r.
        '''
        return RingElem( self.ring.getEXP(r) );

    def sin(self,r):
        '''Get the sinus power series, var r.
        '''
        return RingElem( self.ring.getSIN(r) );

    def cos(self,r):
        '''Get the cosinus power series, var r.
        '''
        return RingElem( self.ring.getCOS(r) );

    def tan(self,r):
        '''Get the tangens power series, var r.
        '''
        return RingElem( self.ring.getTAN(r) );

    def create(self,ifunc=None,jfunc=None,clazz=None):
        '''Create a power series with given generating function.

        ifunc(int i) must return a value which is used in RingFactory.fromInteger().
        jfunc(int i) must return a value of type ring.coFac.
        clazz must implement the Coefficients abstract class.
        '''
        class coeff( MultiVarCoefficients ):
            def __init__(self,r):
                MultiVarCoefficients.__init__(self,r);
                self.coFac = r.coFac;
            def generate(self,i):
                if jfunc == None:
                    return self.coFac.fromInteger( ifunc(i) );
                else:
                    return jfunc(i);
        #print "ifunc"
        if clazz == None:
            ps = MultiVarPowerSeries( self.ring, coeff(self.ring) );
        else:
            ps = MultiVarPowerSeries( self.ring, clazz );
        #print "ps ", ps.toScript();
        return RingElem( ps );

    def fixPoint(self,psmap):
        '''Create a power series as fixed point of the given mapping.

        psmap must implement the UnivPowerSeriesMap interface.
        '''
        ps = self.ring.fixPoint( psmap );
        return RingElem( ps );

    def gcd(self,a,b):
        '''Compute the greatest common divisor of a and b.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        return RingElem( a.gcd(b) );

    def fromPoly(self,a):
        '''Convert a GenPolynomial to a power series.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        return RingElem( self.ring.fromPolynomial(a) );


class PSIdeal:
    '''Represents a JAS power series ideal.

    Method for Standard bases.
    '''

    def __init__(self,ring,polylist,ideal=None,list=None):
        '''PSIdeal constructor.
        '''
        if isinstance(ring,Ring) or isinstance(ring,PolyRing):
            ring = MultiVarPowerSeriesRing(ring.ring);
        if isinstance(ring,MultiSeriesRing):
            ring = ring.ring;
        self.ring = ring;
        #print "ring = ", ring.toScript();
        if ideal != None:
           polylist = ideal.pset.list;
        if list == None:
            self.polylist = pylist2arraylist( [ a.elem for a in polylist ] );
            #print "polylist = ", self.polylist;
            self.list = self.ring.fromPolynomial(self.polylist);
        else:
            self.polylist = None;
            self.list = pylist2arraylist( [ a.elem for a in list ] );

    def __str__(self):
        '''Create a string representation.
        '''
        return str([ a.toScript() for a in self.list ]);

    def STD(self,trunc=None):
        '''Compute a standard base.
        '''
        pr = self.ring;
        if trunc != None:
            pr.setTruncate(trunc);
        #print "pr = ", pr.toScript();
        F = self.list;
        #print "F = ", F;
        tm = StandardBaseSeq();
        t = System.currentTimeMillis();
        S = tm.STD(F);
        t = System.currentTimeMillis() - t;
        print "sequential standard base executed in %s ms" % t;
        #Sp = [ RingElem(a.asPolynomial()) for a in S ];
        Sp = [ RingElem(a) for a in S ];
        #return Sp;
        return PSIdeal(self.ring,None,list=Sp);


def pylist2arraylist(list,fac=None,rec=1):
    '''Convert a Python list to a Java ArrayList.

    If list is a Python list, it is converted, else list is left unchanged.
    '''
    #print "list type(%s) = %s" % (list,type(list));
    if isinstance(list,PyList) or isinstance(list,PyTuple):
       L = ArrayList();
       for e in list:
           t = True;
           if isinstance(e,RingElem):
               t = False;
               e = e.elem;
           if isinstance(e,PyList) or isinstance(e,PyTuple):
               if rec <= 1:
                   e = makeJasArith(e);
               else:
                   t = False;
                   e = pylist2arraylist(e,fac,rec-1);
           try:
               n = e.getClass().getSimpleName();
               if n == "ArrayList":
                   t = False;
           except:
               pass;
           if t and fac != None:
               #print "e.p(%s) = %s" % (e,e.getClass().getName());
               e = fac.parse( str(e) ); #or makeJasArith(e) ?
           L.add(e);
       list = L;
    #print "list type(%s) = %s" % (list,type(list));
    return list


def makeJasArith(item):
    '''Construct a jas.arith object.
    If item is a python tuple or list then a BigRational, BigComplex is constructed. 
    If item is a python float then a BigDecimal is constructed. 
    '''
    #print "item type(%s) = %s" % (item,type(item));
    if isinstance(item,PyInteger) or isinstance(item,PyLong):
        return BigInteger( item );
    if isinstance(item,PyFloat): # ?? what to do ??
        return BigDecimal( str(item) );
    if isinstance(item,PyTuple) or isinstance(item,PyList):
        if len(item) > 2:
            print "len(item) > 2, remaining items ignored";
        #print "item[0] type(%s) = %s" % (item[0],type(item[0]));
        isc = isinstance(item[0],PyTuple) or isinstance(item[0],PyList)
        if len(item) > 1:
            isc = isc or isinstance(item[1],PyTuple) or isinstance(item[1],PyList);
        if isc:
            if len(item) > 1:
                re = makeJasArith( item[0] );
                if not re.isField():
                    re = BigRational( re.val );
                im = makeJasArith( item[1] );
                if not im.isField():
                    im = BigRational( im.val );
                jasArith = BigComplex( re, im );
            else:
                re = makeJasArith( item[0] );
                jasArith = BigComplex( re );
        else:
            if len(item) > 1:
                jasArith = BigRational( item[0] ).divide( BigRational( item[1] ) );
            else:
                jasArith = BigRational( item[0] );
        return jasArith;
    print "unknown item type(%s) = %s" % (item,type(item));
    return item;


def ZZ(z=0):
    '''Create JAS BigInteger as ring element.
    '''
    if isinstance(z,RingElem):
        z = z.elem;
    r = BigInteger(z);
    return RingElem(r);


def ZM(m,z=0,field=False):
    '''Create JAS ModInteger as ring element.
    '''
    if isinstance(m,RingElem):
        m = m.elem;
    if isinstance(z,RingElem):
        z = z.elem;
    if z != 0 and ( z == True or z == False ):
        field = z;
        z = 0;
    if field:
        mf = ModIntegerRing(m,field);
    else:
        mf = ModIntegerRing(m);
    r = ModInteger(mf,z);
    return RingElem(r);


def QQ(d=0,n=1):
    '''Create JAS BigRational as ring element.
    '''
    if isinstance(d,PyTuple) or isinstance(d,PyList):
        if n != 1:
            print "%s ignored" % n;
        if len(d) > 1:
            n = d[1];
        d = d[0];
    if isinstance(d,RingElem):
        d = d.elem;
    if isinstance(n,RingElem):
        n = n.elem;
    if n == 1:
        if d == 0:
            r = BigRational();
        else:
            r = BigRational(d);
    else:
        d = BigRational(d);
        n = BigRational(n);
        r = d.divide(n); # BigRational(d,n); only for short integers
    return RingElem(r);


def CC(re=BigRational(),im=BigRational()):
    '''Create JAS BigComplex as ring element.
    '''
    if re == 0:
        re = BigRational();
    if im == 0:
        im = BigRational();
    if isinstance(re,PyTuple) or isinstance(re,PyList):
        if isinstance(re[0],PyTuple) or isinstance(re[0],PyList):
            if len(re) > 1:
                im = QQ( re[1] );
            re = QQ( re[0] );
        else:
            re = QQ(re);
#        re = makeJasArith( re );
    if isinstance(im,PyTuple) or isinstance(im,PyList):
        im = QQ( im );
#        im = makeJasArith( im );
    if isinstance(re,RingElem):
        re = re.elem;
    if isinstance(im,RingElem):
        im = im.elem;
    if im.isZERO():
        if re.isZERO():
            c = BigComplex();
        else:
            c = BigComplex(re);
    else:
        c = BigComplex(re,im);
    return RingElem(c);

def CR(re=BigRational(),im=BigRational(),ring=None):
    '''Create JAS generic Complex as ring element.
    '''
    if re == 0:
        re = BigRational();
    if im == 0:
        im = BigRational();
    if isinstance(re,PyTuple) or isinstance(re,PyList):
        if isinstance(re[0],PyTuple) or isinstance(re[0],PyList):
            if len(re) > 1:
                im = QQ( re[1] );
            re = QQ( re[0] );
        else:
            re = QQ(re);
#        re = makeJasArith( re );
    if isinstance(im,PyTuple) or isinstance(im,PyList):
        im = QQ( im );
#        im = makeJasArith( im );
    if isinstance(re,RingElem):
        re = re.elem;
    if isinstance(im,RingElem):
        im = im.elem;
    if ring == None:
        ring = re.factory();
    r = ComplexRing(ring);
    if im.isZERO():
        if re.isZERO():
            c = Complex(r);
        else:
            c = Complex(r,re);
    else:
        c = BigComplex(r,re,im);
    return RingElem(c);

def DD(d=0):
    '''Create JAS BigDecimal as ring element.
    '''
    if isinstance(d,RingElem):
        d = d.elem;
    if isinstance(d,PyFloat):
        d = str(d);
    #print "d type(%s) = %s" % (d,type(d));
    if d == 0:
       r = BigDecimal();
    else:
       r = BigDecimal(d);
    return RingElem(r);


def Quat(re=BigRational(),im=BigRational(),jm=BigRational(),km=BigRational()):
    '''Create JAS BigQuaternion as ring element.
    '''
    if re == 0:
        re = BigRational();
    if im == 0:
        im = BigRational();
    if jm == 0:
        jm = BigRational();
    if km == 0:
        km = BigRational();
    if isinstance(re,PyTuple) or isinstance(re,PyList):
        if isinstance(re[0],PyTuple) or isinstance(re[0],PyList):
            if len(re) > 1:
                im = QQ( re[1] );
            re = QQ( re[0] );
        else:
            re = QQ(re);
#        re = makeJasArith( re );
    if isinstance(im,PyTuple) or isinstance(im,PyList):
        im = QQ( im );
    if isinstance(jm,PyTuple) or isinstance(jm,PyList):
        jm = QQ( jm );
    if isinstance(km,PyTuple) or isinstance(km,PyList):
       kim = QQ( km );
#        im = makeJasArith( im );
    if isinstance(re,RingElem):
        re = re.elem;
    if isinstance(im,RingElem):
        im = im.elem;
    if isinstance(jm,RingElem):
        jm = jm.elem;
    if isinstance(km,RingElem):
        km = km.elem;
    c = BigQuaternion(re,im,jm,km);
    return RingElem(c);


def Oct(ro=BigQuaternion(),io=BigQuaternion()):
    '''Create JAS BigOctonion as ring element.
    '''
    if ro == 0:
        ro = BigQuaternion();
    if io == 0:
        io = BigQuaternion();
    if isinstance(ro,PyTuple) or isinstance(ro,PyList):
        if isinstance(ro[0],PyTuple) or isinstance(ro[0],PyList):
            if len(ro) > 1:
                io = QQ( ro[1] );
            ro = QQ( ro[0] );
        else:
            ro = QQ(ro);
#        re = makeJasArith( re );
    if isinstance(io,PyTuple) or isinstance(io,PyList):
        io = QQ( io );
#        im = makeJasArith( im );
    if isinstance(ro,RingElem):
        ro = ro.elem;
    if isinstance(io,RingElem):
        io = io.elem;
    c = BigOctonion(ro,io);
    return RingElem(c);


def AN(m,z=0,field=False,pr=None):
    '''Create JAS AlgebraicNumber as ring element.
    '''
    if isinstance(m,RingElem):
        m = m.elem;
    if isinstance(z,RingElem):
        z = z.elem;
    if z != 0 and ( z == True or z == False ):
        field = z;
        z = 0;
    #print "m.getClass() = " + str(m.getClass().getName());
    #print "field = " + str(field);
    if m.getClass().getSimpleName() == "AlgebraicNumber":
        mf = AlgebraicNumberRing(m.factory().modul,m.factory().isField());
    else:
        if field:
            mf = AlgebraicNumberRing(m,field);
        else:
            mf = AlgebraicNumberRing(m);
    #print "mf = " + mf.toString();
    if z == 0:
        r = AlgebraicNumber(mf);
    else:
        r = AlgebraicNumber(mf,z);
    return RingElem(r);


def RealN(m,i,r=0):
    '''Create JAS RealAlgebraicNumber as ring element.
    '''
    if isinstance(m,RingElem):
        m = m.elem;
    if isinstance(r,RingElem):
        r = r.elem;
    if isinstance(i,PyTuple) or isinstance(i,PyList):
        il = BigRational(i[0]);
        ir = BigRational(i[1]);
        i = Interval(il,ir);
    #print "m.getClass() = " + str(m.getClass().getName());
    if m.getClass().getSimpleName() == "RealAlgebraicNumber":
        mf = RealAlgebraicRing(m.factory().algebraic.modul,i);
    else:
        mf = RealAlgebraicRing(m,i);
    if r == 0:
        rr = RealAlgebraicNumber(mf);
    else:
        rr = RealAlgebraicNumber(mf,r);
    return RingElem(rr);


def RF(pr,d=0,n=1):
    '''Create JAS rational function Quotient as ring element.
    '''
    if isinstance(d,PyTuple) or isinstance(d,PyList):
        if n != 1:
            print "%s ignored" % n;
        if len(d) > 1:
            n = d[1];
        d = d[0];
    if isinstance(d,RingElem):
        d = d.elem;
    if isinstance(n,RingElem):
        n = n.elem;
    if isinstance(pr,RingElem):
        pr = pr.elem;
    if isinstance(pr,Ring):
        pr = pr.ring;
    qr = QuotientRing(pr);
    if d == 0:
        r = Quotient(qr);
    else:
        if n == 1:
            r = Quotient(qr,d);
        else:
            r = Quotient(qr,d,n);
    return RingElem(r);


def RC(ideal,r=0):
    '''Create JAS polynomial Residue as ring element.
    '''
    if ideal == None:
        raise ValueError, "No ideal given."
    if isinstance(ideal,Ideal):
        ideal = jas.application.Ideal(ideal.pset);
        #ideal.doGB();
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    if ideal.getList().get(0).ring.getClass().getSimpleName() == "ResidueRing":
        rc = ResidueRing( ideal.getList().get(0).ring.ideal );
    else:
        rc = ResidueRing(ideal);
    if isinstance(r,RingElem):
        r = r.elem;
    if r == 0:
        r = Residue(rc);
    else:
        r = Residue(rc,r);
    return RingElem(r);


def LC(ideal,d=0,n=1):
    '''Create JAS polynomial Local as ring element.
    '''
    if ideal == None:
        raise ValueError, "No ideal given."
    if isinstance(ideal,Ideal):
        ideal = jas.application.Ideal(ideal.pset);
        #ideal.doGB();
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    if ideal.getList().get(0).ring.getClass().getSimpleName() == "LocalRing":
        lc = LocalRing( ideal.getList().get(0).ring.ideal );
    else:
        lc = LocalRing(ideal);
    if isinstance(d,PyTuple) or isinstance(d,PyList):
        if n != 1:
            print "%s ignored" % n;
        if len(d) > 1:
            n = d[1];
        d = d[0];
    if isinstance(d,RingElem):
        d = d.elem;
    if isinstance(n,RingElem):
        n = n.elem;
    if d == 0:
        r = Local(lc);
    else:
        if n == 1:
            r = Local(lc,d);
        else:
            r = Local(lc,d,n);
    return RingElem(r);


def RR(flist,n=1,r=0):
    '''Create JAS regular ring Product as ring element.
    '''
    if not isinstance(n,PyInteger):
        r = n;
        n = 1;
    if flist == None:
        raise ValueError, "No list given."
    if isinstance(flist,PyList) or isinstance(flist,PyTuple):
        flist = pylist2arraylist( [ x.factory() for x in flist ], rec=1);
        ncop = 0;
    else:
        ncop = n;
    if isinstance(flist,RingElem):
        flist = flist.elem;
        flist = flist.factory();
        ncop = n;
    #print "flist = " + str(flist);
    #print "ncop  = " + str(ncop);
    if ncop == 0:
        pr = ProductRing(flist);
    else:
        pr = ProductRing(flist,ncop);
    #print "r type(%s) = %s" % (r,type(r));
    if isinstance(r,RingElem):
        r = r.elem;
    #if isinstance(r,PyJavaInstance):
    try:
        #print "r.class() = %s" % r.getClass().getSimpleName();
        if r.getClass().getSimpleName() == "Product":
            #print "r.val = %s" % r.val;
            r = r.val;
    except:
        pass;
    #print "r = " + str(r);
    if r == 0:
        r = Product(pr);
    else:
        r = Product(pr,r);
    return RingElem(r);


def PS(cofac,name,f=None,truncate=None):
    '''Create JAS UnivPowerSeries as ring element.
    '''
    cf = cofac;
    if isinstance(cofac,RingElem):
        cf = cofac.elem.factory();
    if isinstance(cofac,Ring):
        cf = cofac.ring;
    if isinstance(truncate,RingElem):
        truncate = truncate.elem;
    if truncate == None:
        ps = UnivPowerSeriesRing(cf,name);
    else:
        ps = UnivPowerSeriesRing(cf,truncate,name);
    if f == None:
        r = ps.getZERO();
    else:
        class coeff( Coefficients ):
            def __init__(self,cofac):
                self.coFac = cofac;
            def generate(self,i):
                a = f(i);
                if isinstance(a,RingElem):
                    a = a.elem;
                #print "a = " + str(a);
                return a;
        r = UnivPowerSeries(ps,coeff(cofac));
    return RingElem(r);


def MPS(cofac,names,f=None,truncate=None):
    '''Create JAS MultiVarPowerSeries as ring element.
    '''
    cf = cofac;
    if isinstance(cofac,RingElem):
        cf = cofac.elem.factory();
    if isinstance(cofac,Ring):
        cf = cofac.ring;
    if isinstance(truncate,RingElem):
        truncate = truncate.elem;
    if truncate == None:
        ps = MultiVarPowerSeriesRing(cf,names);
    else:
        ps = MultiVarPowerSeriesRing(cf,names,truncate);
    if f == None:
        r = ps.getZERO();
    else:
        class coeff( MultiVarCoefficients ):
            def __init__(self,r):
                MultiVarCoefficients.__init__(self,r);
                self.coFac = r.coFac;
            def generate(self,i):
                a = f(i);
                if isinstance(a,RingElem):
                    a = a.elem;
                return a;
        r = MultiVarPowerSeries(ps,coeff(ps));
        #print "r = " + str(r);
    return RingElem(r);


def Vec(cofac,n,v=None):
    '''Create JAS GenVector ring element.
    '''
    cf = cofac;
    if isinstance(cofac,RingElem):
        cf = cofac.elem.factory();
    if isinstance(cofac,Ring):
        cf = cofac.ring;
    if isinstance(n,RingElem):
        n = n.elem;
    if isinstance(v,RingElem):
        v = v.elem;
    vr = GenVectorModul(cf,n);
    if v == None:
        r = GenVector(vr);
    else:
        r = GenVector(vr,v);
    return RingElem(r);


def Mat(cofac,n,m,v=None):
    '''Create JAS GenMatrix ring element.
    '''
    cf = cofac;
    if isinstance(cofac,RingElem):
        cf = cofac.elem.factory();
    if isinstance(cofac,Ring):
        cf = cofac.ring;
    if isinstance(n,RingElem):
        n = n.elem;
    if isinstance(m,RingElem):
        m = m.elem;
    if isinstance(v,RingElem):
        v = v.elem;
    #print "cf type(%s) = %s" % (cf,type(cf));
    mr = GenMatrixRing(cf,n,m);
    if v == None:
        r = GenMatrix(mr);
    else:
        r = GenMatrix(mr,v);
    return RingElem(r);


def coercePair(a,b):
    '''Coerce type a to type b or type b to type a.
    '''
    #print "a type(%s) = %s" % (a,type(a));
    #print "b type(%s) = %s" % (b,type(b));
    try:
        if not a.isPolynomial() and b.isPolynomial():
            s = b.coerce(a);
            o = b;
        else:
            s = a;
            o = a.coerce(b);
    except:
        s = a;
        o = a.coerce(b);
    return (s,o);
    

class RingElem:
    '''Proxy for JAS ring elements.

    Methods to be used as + - * ** / %.
    '''

    def __init__(self,elem):
        '''Constructor for ring element.
        '''
        if isinstance(elem,RingElem):
            self.elem = elem.elem;
        else:
            self.elem = elem;
        try:
            self.ring = self.elem.factory();
        except:
            self.ring = self.elem;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.elem.toScript()); 

    def zero(self):
        '''Zero element of this ring.
        '''
        return RingElem( self.elem.factory().getZERO() );

    def isZERO(self):
        '''Test if this is the zero element of the ring.
        '''
        return self.elem.isZERO();

    def one(self):
        '''One element of this ring.
        '''
        return RingElem( self.elem.factory().getONE() );

    def isONE(self):
        '''Test if this is the one element of the ring.
        '''
        return self.elem.isONE();

    def signum(self):
        '''Get the sign of this element.
        '''
        return self.elem.signum();

    def __abs__(self):
        '''Absolute value.
        '''
        return RingElem( self.elem.abs() ); 

    def __neg__(self):
        '''Negative value.
        '''
        return RingElem( self.elem.negate() ); 

    def __pos__(self):
        '''Positive value.
        '''
        return self; 

    def coerce(self,other):
        '''Coerce other to self.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        #print "other type(%s) = %s" % (other,type(other));
        #print "self.elem class(%s) = %s" % (self.elem,self.elem.getClass());
        if self.elem.getClass().getSimpleName() == "GenVector":
            #print "self, other = %s, %s " % (self,other);
            if isinstance(other,PyTuple) or isinstance(other,PyList):
                o = pylist2arraylist(other,self.elem.factory().coFac,rec=1);
                o = GenVector(self.elem.factory(),o);
                return RingElem( o );
        if self.elem.getClass().getSimpleName() == "GenMatrix":
            #print "self, other = %s, %s " % (type(self),type(other));
            #print "o type(%s) = %s, str = %s" % (o,type(o),str(o));
            if isinstance(other,PyTuple) or isinstance(other,PyList):
                o = pylist2arraylist(other,self.elem.factory().coFac,rec=2);
                o = GenMatrix(self.elem.factory(),o);
                return RingElem( o );
        if isinstance(other,RingElem):
            if self.isPolynomial() and not other.isPolynomial():
                #print "self.ring = %s" % (self.ring);
                o = self.ring.parse( other.elem.toString() ); # not toScript()
                #print "o type(%s) = %s, str = %s" % (o,type(o),str(o));
                return RingElem( o );
            return other;
        #print "--1";
        if isinstance(other,PyTuple) or isinstance(other,PyList):
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            o = makeJasArith(other);
            #print "other class(%s) = %s" % (o,o.getClass());
            if self.isPolynomial():
                #print "other type(%s) = %s" % (o,type(o));
                o = self.ring.parse( o.toString() ); # not toScript();
                #o = o.elem;
            if self.elem.getClass().getSimpleName() == "BigComplex":
                #print "other type(%s) = %s" % (o,type(o));
                o = CC( o );
                o = o.elem;
            if self.elem.getClass().getSimpleName() == "BigQuaternion":
                #print "other type(%s) = %s" % (o,type(o));
                o = Quat( o );
                o = o.elem;
            if self.elem.getClass().getSimpleName() == "BigOctonion":
                #print "other type(%s) = %s" % (o,type(o));
                o = Oct( Quat(o) );
                o = o.elem;
            if self.elem.getClass().getSimpleName() == "Product":
                #print "other type(%s) = %s" % (o,type(o));
                o = RR(self.ring, self.elem.multiply(o) ); # valueOf
                #print "o = %s" % o;
                o = o.elem;
            return RingElem(o);
        #print "--2";
        # test if self.elem is a factory itself
        if self.isFactory():
            if isinstance(other,PyInteger) or isinstance(other,PyLong):
                o = self.elem.fromInteger(other);
            else:
                if isinstance(other,PyFloat): # ?? what to do ??
                    o = self.elem.fromInteger( int(other) );
                else:
                    print "unknown other type(%s) = %s" % (other,type(other));
                    o = self.elem.parse( str(other) );
            return RingElem(o);
        #print "--3";
        #print "other type(%s) = %s" % (other,type(other));
        # self.elem has a ring factory
        if isinstance(other,PyInteger) or isinstance(other,PyLong):
            o = self.elem.factory().fromInteger(other);
        else:
            if isinstance(other,PyFloat): # ?? what to do ??
                #print "other type(%s) = %s" % (other,type(other));
                #print "self  type(%s) = %s" % (self.elem,self.elem.getClass().getName());
                o = BigDecimal(other);
                if self.elem.getClass().getSimpleName() == "Product":
                    o = RR(self.ring, self.elem.idempotent().multiply(o) ); # valueOf
                    o = o.elem;
                else:
                    o = self.elem.factory().getZERO().sum( o );
            else:
                print "unknown other type(%s) = %s" % (other,type(other));
                o = self.elem.factory().parse( str(other) );
        #print "--4";
        return RingElem(o);

    def isFactory(self):
        '''Test if this is itself a ring factory.
        '''
        f = self.elem.factory();
        if self.elem == f:
            return True;
        else:
            return False;

    def isPolynomial(self):
        '''Test if this is a polynomial.
        '''
        try:
            nv = self.elem.ring.nvar;
        except:
            return False;
        return True;

    def __cmp__(self,other):
        '''Compare two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return s.elem.compareTo( o.elem ); 

    def __hash__(self):
        '''Hash value.
        '''
        return self.elem.hashCode(); 

    def __mul__(self,other):
        '''Multiply two ring elements.
        '''
        [s,o] = coercePair(self,other);
        #print "self  type(%s) = %s" % (s,type(s));
        #print "other type(%s) = %s" % (o,type(o));
        return RingElem( s.elem.multiply( o.elem ) ); 

    def __rmul__(self,other):
        '''Reverse multiply two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return o.__mul__(s);

    def __add__(self,other):
        '''Add two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return RingElem( s.elem.sum( o.elem ) ); 

    def __radd__(self,other):
        '''Reverse add two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return o.__add__(s);

    def __sub__(self,other):
        '''Subtract two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return RingElem( s.elem.subtract( o.elem ) ); 

    def __rsub__(self,other):
        '''Reverse subtract two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return o.__sub__(self);

    def __div__(self,other):
        '''Divide two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return RingElem( s.elem.divide( o.elem ) ); 

    def __rdiv__(self,other):
        '''Reverse divide two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return o.__div__(s);

    def __mod__(self,other):
        '''Modular remainder of two ring elements.
        '''
        [s,o] = coercePair(self,other);
        return RingElem( s.elem.remainder( o.elem ) ); 

    def __xor__(self,other):
        '''Can not be used as power.
        '''
        return None;

    def __pow__(self,other):
        '''Power of this to other.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        #print "pow other type(%s) = %s" % (other,type(other));
        if isinstance(other,PyInteger) or isinstance(other,PyLong):
            n = other;
        else:
            if isinstance(other,RingElem): 
                n = other.elem;
                #if isinstance(n,BigRational): # does not work
                if n.getClass().getSimpleName() == "BigRational": 
                    n = n.numerator().intValue() / n.denominator().intValue();
                #if isinstance(n,BigInteger):  # does not work
                if n.getClass().getSimpleName() == "BigInteger": 
                    n = n.intValue();
        if self.isFactory():
            p = Power(self.elem).power( self.elem, n );
        else:
            p = Power(self.elem.ring).power( self.elem, n );
        return RingElem( p ); 

    def __eq__(self,other):
        '''Test if two ring elements are equal.
        '''
        o = other;
        if isinstance(other,RingElem):
            o = other.elem;
        return self.elem.equals(o)

    def __ne__(self,other):
        '''Test if two ring elements are not equal.
        '''
        o = other;
        if isinstance(other,RingElem):
            o = other.elem;
        return not self.elem.equals(o)

    def __float__(self):
        '''Convert to Python float.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        e = self.elem;
        if e.getClass().getSimpleName() == "BigInteger":
            e = BigRational(e);
        if e.getClass().getSimpleName() == "BigRational":
            e = BigDecimal(e);
        if e.getClass().getSimpleName() == "BigDecimal":
            e = e.toString();
        e = float(e);
        return e;

    def factory(self):
        '''Get the factory of this element.
        '''
        fac = self.elem.factory();
        try:
            nv = fac.nvar;
        except:
            return RingElem(fac);
        #return PolyRing(fac.coFac,fac.getVars(),fac.tord);
        return RingElem(fac);

    def gens(self):
        '''Get the generators for the factory of this element.
        '''
        L = self.elem.factory().generators();
        #print "L = %s" % L;
        N = [ RingElem(e) for e in L ];
        #print "N = %s" % N;
        return N;

    def monic(self):
        '''Monic polynomial.
        '''
        return RingElem( self.elem.monic() ); 

    def evaluate(self,a):
        '''Evaluate at a for power series.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        #print "a     type(%s) = %s" % (a,type(a));
        x = None;
        if isinstance(a,RingElem):
            x = a.elem;
        if isinstance(a,PyTuple) or isinstance(a,PyList):
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            x = makeJasArith(a);
        try:
            e = self.elem.evaluate(x);
        except:
            e = 0;            
        return RingElem( e );

    def integrate(self,a=0,r=None):
        '''Integrate a power series with constant a or as rational function.

        a is the integration constant, r is for partial integration in variable r.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        #print "a     type(%s) = %s" % (a,type(a));
        x = None;
        if isinstance(a,RingElem):
            x = a.elem;
        if isinstance(a,PyTuple) or isinstance(a,PyList):
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            x = makeJasArith(a);
        try:
            if r != None:
                e = self.elem.integrate(x,r);
            else:
                e = self.elem.integrate(x);
            return RingElem( e );
        except:
            pass;
        cf = self.elem.ring;
        try:
            cf = cf.ring;
        except:
            pass;
        integrator = ElementaryIntegration(cf.coFac);
        ei = integrator.integrate(self.elem); 
        return ei;

    def differentiate(self,r=None):
        '''Differentiate a power series.

        r is for partial differentiation in variable r.
        '''
        try:
            if r != None:
                e = self.elem.differentiate(r);
            else:
                e = self.elem.differentiate();
        except:
            e = self.elem.factory().getZERO();
        return RingElem( e );

    def coefficients(self):
        '''Get the coefficients of a polynomial.
        '''
        a = self.elem;
        #L = [ c.toScriptFactory() for c in a.coefficientIterator() ];
        L = [ RingElem(c) for c in a.coefficientIterator() ];
        return L


class PolyRing(Ring):
    '''Represents a JAS polynomial ring: GenPolynomialRing.

    Provides more convenient constructor. 
    Then returns a Ring.
    '''

    def __init__(self,coeff,vars,order=TermOrder(TermOrder.IGRLEX)):
        '''Ring constructor.

        coeff = factory for coefficients,
        vars = string with variable names,
        order = term order.
        '''
        if coeff == None:
            raise ValueError, "No coefficient given."
        cf = coeff;
        if isinstance(coeff,RingElem):
            cf = coeff.elem.factory();
        if isinstance(coeff,Ring):
            cf = coeff.ring;
        if vars == None:
            raise ValueError, "No variable names given."
        names = vars;
        if isinstance(vars,PyString):
            names = GenPolynomialTokenizer.variableList(vars);
        nv = len(names);
        to = PolyRing.lex;
        if isinstance(order,TermOrder):
            to = order;
        tring = GenPolynomialRing(cf,nv,to,names);
        #want: super(Ring,self).__init__(ring=tring)
        self.ring = tring;
        self.engine = GCDFactory.getProxy(self.ring.coFac);
        try:
            self.sqf = SquarefreeFactory.getImplementation(self.ring.coFac);
        except:
            pass
#        except Exception, e:
#            print "error " + str(e)
        try:
            self.factor = FactorFactory.getImplementation(self.ring.coFac);
#        except Exception, e:
#            print "error " + str(e)
        except:
            pass


    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();

    lex = TermOrder(TermOrder.INVLEX)

    grad = TermOrder(TermOrder.IGRLEX)


class SolvPolyRing(SolvableRing):
    '''Represents a JAS solvable polynomial ring: GenSolvablePolynomialRing.

    Provides more convenient constructor. 
    Then returns a Ring.
    '''

    def __init__(self,coeff,vars,order,rel=None):
        '''Ring constructor.

        coeff = factory for coefficients,
        vars = string with variable names,
        order = term order,
        rel = triple list of relations. (e,f,p,...) with e * f = p as relation.
        '''
        if coeff == None:
            raise ValueError, "No coefficient given."
        cf = coeff;
        if isinstance(coeff,RingElem):
            cf = coeff.elem.factory();
        if isinstance(coeff,Ring):
            cf = coeff.ring;
        if vars == None:
            raise ValueError, "No variable names given."
        names = vars;
        if isinstance(vars,PyString):
            names = GenPolynomialTokenizer.variableList(vars);
        nv = len(names);
        to = PolyRing.lex;
        if isinstance(order,TermOrder):
            to = order;
        ring = GenSolvablePolynomialRing(cf,nv,to,names);
        if rel != None:
            #print "rel = " + str(rel);
            table = ring.table;
            L = [];
            for x in rel:
                if isinstance(x,RingElem):
                    x = x.elem;
                L.append(x);
            #print "rel = " + str(L);
            for i in range(0,len(L),3):
                table.update( L[i], L[i+1], L[i+2] );
        self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();
