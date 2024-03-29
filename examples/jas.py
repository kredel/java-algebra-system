'''jython interface to JAS.
'''

# $Id$

from java.lang           import System
from java.io             import StringReader
from java.util           import ArrayList, List, Collections

from edu.jas.structure   import RingElem, RingFactory, Power
from edu.jas.arith       import BigInteger, BigRational, BigComplex, BigDecimal,\
                                ModInteger, ModIntegerRing, ModLong, ModLongRing, ModInt, ModIntRing,\
                                BigQuaternion, BigQuaternionRing, BigOctonion,\
                                Product, ProductRing, ArithUtil, PrimeList, PrimeInteger
from edu.jas.poly        import GenPolynomial, GenPolynomialRing, Monomial,\
                                IndexFactory, GenExteriorPolynomial, GenExteriorPolynomialRing,\
                                GenSolvablePolynomial, GenSolvablePolynomialRing,\
                                RecSolvablePolynomial, RecSolvablePolynomialRing,\
                                RecSolvableWordPolynomial, RecSolvableWordPolynomialRing,\
                                QLRSolvablePolynomial, QLRSolvablePolynomialRing,\
                                GenWordPolynomial, GenWordPolynomialRing,\
                                ExpVector,\
                                Word, WordFactory,\
                                GenPolynomialTokenizer, OrderedPolynomialList, PolyUtil,\
                                TermOrderOptimization, TermOrder, TermOrderByName,\
                                PolynomialList, AlgebraicNumber, AlgebraicNumberRing,\
                                OrderedModuleList, ModuleList,\
                                Complex, ComplexRing
from edu.jas.ps          import UnivPowerSeries, UnivPowerSeriesRing,\
                                UnivPowerSeriesMap, Coefficients, \
                                MultiVarPowerSeries, MultiVarPowerSeriesRing,\
                                MultiVarPowerSeriesMap, MultiVarCoefficients,\
                                StandardBaseSeq
from edu.jas.gb          import EReductionSeq, DGroebnerBaseSeq, EGroebnerBaseSeq,\
                                GroebnerBaseDistributedEC, GroebnerBaseDistributedHybridEC,\
                                GroebnerBaseSeq, GroebnerBaseSeqIter, GroebnerBaseSeqPairSeq,\
                                OrderedPairlist, OrderedSyzPairlist, ReductionSeq,\
                                GroebnerBaseParallel, GroebnerBaseSeqPairParallel,\
                                SolvableGroebnerBaseParallel, SolvableGroebnerBaseSeq,\
                                SolvableReductionSeq, WordGroebnerBaseSeq
from edu.jas.gbufd       import GroebnerBasePseudoRecSeq, GroebnerBasePseudoSeq,\
                                SolvableGroebnerBasePseudoSeq, SolvablePseudoReductionSeq,\
                                PseudoReductionSeq, GroebnerBasePseudoParallel,\
                                WordGroebnerBasePseudoSeq, WordPseudoReductionSeq,\
                                SolvableGroebnerBasePseudoRecSeq, WordGroebnerBasePseudoRecSeq,\
                                RGroebnerBasePseudoSeq, RGroebnerBaseSeq, RReductionSeq,\
                                GroebnerBaseFGLM, GroebnerBaseWalk,\
                                CharacteristicSetWu, PolyGBUtil,\
                                SolvableSyzygySeq, SyzygySeq
#from edu.jas.gbmod       import ModGroebnerBaseSeq, ModSolvableGroebnerBaseSeq
from edu.jas.vector      import GenVector, GenVectorModul, BasicLinAlg,\
                                GenMatrix, GenMatrixRing, LinAlg
from edu.jas.application import FactorFactory, PolyUtilApp, RingFactoryTokenizer,\
                                Residue, ResidueRing, Ideal,\
                                Local, LocalRing, IdealWithRealAlgebraicRoots,\
                                SolvableIdeal, SolvableResidue, SolvableResidueRing,\
                                SolvableLocal, SolvableLocalRing,\
                                SolvableLocalResidue, SolvableLocalResidueRing,\
                                ResidueSolvablePolynomial, ResidueSolvablePolynomialRing,\
                                LocalSolvablePolynomial, LocalSolvablePolynomialRing,\
                                WordIdeal, WordResidue, WordResidueRing,\
                                AlgebraicRootsPrimElem, RootFactoryApp,\
                                ComprehensiveGroebnerBaseSeq, ExtensionFieldBuilder
from edu.jas.kern        import ComputerThreads, StringUtil, Scripting
from edu.jas.ufd         import GreatestCommonDivisor, PolyUfdUtil, GCDFactory,\
                                SquarefreeFactory, Quotient, QuotientRing
from edu.jas.fd          import SolvableQuotient, SolvableQuotientRing,\
                                QuotSolvablePolynomial, QuotSolvablePolynomialRing
from edu.jas.root        import RealRootsSturm, Interval, RealAlgebraicNumber, RealAlgebraicRing,\
                                ComplexRootsSturm, Rectangle, RealArithUtil, RootFactory
from edu.jas.integrate   import ElementaryIntegration
from edu.jas.util        import ExecutableServer
#from edu.jas             import structure, arith, poly, ps, gb, gbmod, vector,\
#                                application, util, ufd
from edu                 import jas
#PrettyPrint.setInternal();

from org.python.core     import PyInstance, PyList, PyTuple,\
                                PyInteger, PyLong, PyFloat, PyString
# not suitable PySequence

# set output to Python scripting
Scripting.setLang(Scripting.Lang.Python);

def startLog():
    '''Configure the log4j system and start logging.

    BasicConfigurator from log4j version 1 is no more supported, please use log4j2 configuration.
    '''
    #print "BasicConfigurator from log4j version 1 is no more supported, please use log4j2 configuration";

print "Java Algebra System (JAS) version 2.7"

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

auto_inject = True
'''Define variables automatically in the global namespace.
'''

def inject_variable(name, value):
    '''Inject a variable into the main global namespace

    INPUT:
     - "name"  - a string
     - "value" - anything 

    Found in Sage.
    AUTHORS:
     - William Stein 
    '''
    assert type(name) is str
    import sys
    depth = 0
    redef = None
    while True:
        G = sys._getframe(depth).f_globals
        #print "G = `%s` " % G;
        depth += 1
        if depth > 100:
            print "depth limit %s reached " % depth;
            break
        if G is None:
            continue
        # orig: if G["__name__"] == "__main__" and G["__package__"] is None:
        try:
            if G["__name__"] is None:
                break
        except:
            #print "G[__name__] undefined";
            break
        if G["__name__"] == "__main__":
            try:
                if G["__package__"] is None:
                    break
            except:
                break
    if G is None:
        print "error at G no global environment found for `%s` = `%s` " % (name, value);
        return
    if name in G:
       redef = name;
       #print "redefining global variable `%s` from `%s` " % (name, G[name]);
    G[name] = value
    return redef

def inject_generators(gens):
    '''Inject generators as variables into the main global namespace

    INPUT:
     - "gens"  - generators
    '''
    for v in gens:
        #print "vars = " + str(v);
        redef = [];
        s = str(v);
        if s.find("/") < 0 and s.find("(") < 0 and s.find(",") < 0 and s.find("{") < 0 and s.find("[") < 0 and s.find("|") < 0:
           if s[0:1] == "1":
              s = "one" + s[1:]
              #print "var = " + s;
           rd = inject_variable(s,v)
           if rd != None:
              redef.append(rd);
        if redef != []:
           print "WARN: redefined variables " + ", ".join(redef);

def nameFromValue(v):
    '''Get a meaningful name from a value.

    INPUT:
     - "v" - the given value.
    '''
    import re;
    ri = re.compile(r'\A[0-9].*');
    vs = str(v);
    vs = vs.replace(" ","");
    vs = vs.replace("\n","");
    if vs.find("(") >= 0:
       vs = vs.replace("(","");
       vs = vs.replace(")","");
    if vs.find("{") >= 0:
       vs = vs.replace("{","");
       vs = vs.replace("}","");
    if vs.find("[") >= 0:
       vs = vs.replace("[","");
       vs = vs.replace("]","");
    if vs.find(",") >= 0:
       vs = vs.replace(",","");
    #if vs.find("|") >= 0:
    #    vs = vs.replace("|","div"); # case "1"?
    if vs.find("/") >= 0:
       vs = vs.replace("/","div");
    if vs[0:1] == "1" and not ri.match(vs):
       vs = 'one' + vs[1:];
    if vs == "1":
       vs = "one";
    if vs == "0i1" or vs == "0I1":
       vs = "I";
    if vs.find("|") >= 0 or ri.match(vs):
       #print "0vs = " + str(vs);
       return None;
    return vs;


class Ring:
    '''Represents a JAS polynomial ring: GenPolynomialRing.

    Methods to create ideals and ideals with parametric coefficients.
    '''

    def __init__(self,ringstr="",ring=None,fast=False):
        '''Ring constructor.

        ringstr string representation to be parsed.
        ring JAS ring object.
        '''
        if ring == None:
           sr = StringReader( ringstr );
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextPolynomialRing();
           #tok = GenPolynomialTokenizer(pfac,sr);
           #plist = tok.nextPolynomialList();
           #self.pset = PolynomialList(pfac,plist);
           self.ring = pfac;
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;
        if fast:
            return;
        self.engine = Ring.getEngineGcd(self.ring);
        self.sqf = Ring.getEngineSqf(self.ring);
        self.factor = Ring.getEngineFactor(self.ring);
        self.variable_generators();

    def getEngineGcd(r):
        '''Get the polynomial gcd engine implementation.

        r is the given polynomial ring.
        '''
        if isinstance(r,RingElem):
            r = r.elem;
        if not isinstance(r,GenPolynomialRing):
            return None;
        try:  
            i = GCDFactory.getProxy(r.coFac);
        except:
            i = None
        return i;
    
    getEngineGcd = staticmethod(getEngineGcd);

    def getEngineSqf(r):
        '''Get the polynomial squarefree engine implementation.

        r is the given polynomial ring.
        '''
        if isinstance(r,RingElem):
            r = r.elem;
        if not isinstance(r,GenPolynomialRing):
            return None;
        try:  
            i = SquarefreeFactory.getImplementation(r.coFac);
        except:
            i = None
        return i;
    
    getEngineSqf = staticmethod(getEngineSqf);

    def getEngineFactor(r):
        '''Get the polynomial factorization engine implementation.

        r is the given polynomial ring.
        '''
        if isinstance(r,RingElem):
            r = r.elem;
        if not isinstance(r,GenPolynomialRing):
            return None;
        try:  
            i = FactorFactory.getImplementation(r.coFac);
        except:
            i = None
        return i;
    
    getEngineFactor = staticmethod(getEngineFactor);

    def variable_generators(self):
        '''Define instance variables for generators.
        '''
        vns = []
        redef = []
        #print "dict: " + str(self.__dict__)
        for v in self.gens(): #ring.generators():
            #vr = RingElem(v);
            #print "vars = " + str(v);
            vs = nameFromValue(v);
            if vs is None:
                #print "0vs = " + str(vs);
                continue;
            try:
                if self.__dict__[vs] is None:
                    self.__dict__[vs] = v;
                else:
                    print vs + " not redefined to " + str(v);
            except:
                self.__dict__[vs] = v;
            if auto_inject:
                rd = inject_variable(vs,v)
                vns.append(vs)
                if rd != None:
                   redef.append(rd);
        if auto_inject:
            print "globally defined variables: " + ", ".join(vns)
            if redef != []:
               print "WARN: redefined global variables: " + ", ".join(redef);
        #print "dict: " + str(self.__dict__)

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

    def __eq__(self,other):
        '''Test if two rings are equal.
        '''
        if not isinstance(other, Ring):
            return False;
        s = self.ring;
        t = other.ring;
        return s.equals(t)

    def ideal(self,ringstr="",list=None):
        '''Create an ideal.
        '''
        return Ideal(self,ringstr,list=list);

    def paramideal(self,ringstr="",list=None,gbsys=None):
        '''Create an ideal in a polynomial ring with parameter coefficients.
        '''
        return ParamIdeal(self,ringstr,list,gbsys);

    def powerseriesRing(self):
        '''Get a power series ring from this ring.
        '''
        pr = MultiVarPowerSeriesRing(self.ring);
        return MultiSeriesRing(ring=pr);

    def gens(self):
        '''Get list of generators of the polynomial ring.
        '''
        L = self.ring.generators();
        N = [ RingElem(e) for e in L ];
        return N;

    def inject_variables(self):
        '''Inject generators as variables into the main global namespace
        '''
        inject_generators(self.gens());

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

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        I = Ideal(self, "( " + poly + " )");
        list = I.pset.list;
        if len(list) > 0:
            return RingElem( list[0] );

    def gcd(self,a,b):
        '''Compute the greatest common divisor of a and b.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( a );
            a = a.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        else:
            b = self.element( b );
            b = b.elem;
        return RingElem( self.engine.gcd(a,b) );

    def squarefreeFactors(self,a):
        '''Compute squarefree factors of polynomial.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        else:
            a = self.element( a );
            a = a.elem;
        cf = self.ring.coFac;
        if isinstance(cf,GenPolynomialRing):
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
            a = self.element( a );
            a = a.elem;
        try:
            cf = self.ring.coFac;
            if isinstance(cf,GenPolynomialRing) and cf.isCommutative():
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
            a = self.element( a );
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
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.realRoots(eps);

    def complexRoots(self,a,eps=None):
        '''Compute complex roots of univariate polynomial.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.complexRoots(eps);

    def algebraicRoots(self,a,eps=None):
        '''Algebraic real and Compute complex roots of univariate polynomial.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.algebraicRoots(eps);

    def rootRefine(self,a,eps=None):
        '''Compute algebraic roots refinement.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.rootRefine(eps);

    def decimalRoots(self,a,eps=None):
        '''Compute decimal approximation of real and complex roots of univariate polynomial.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.decimalRoots(eps);

    def rootsOfUnity(self,a):
        '''Roots of unity of real and complex algebraic numbers.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.rootsOfUnity();

    def rootReduce(self,a, b):
        '''Root reduce of real and complex algebraic numbers.
           Compute an extension field with a primitive element.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.rootReduce(b);

    def integrate(self,a):
        '''Integrate rational function or power series.
        '''
        if not isinstance(a,RingElem):
            a = RingElem(a);
        return a.integrate();

    def subring(self,polystr="",list=None):
        '''Sub ring generators as Groebner base.
        '''
        if list == None:
           sr = StringReader( polystr );
           tok = GenPolynomialTokenizer(self.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = pylist2arraylist(list,rec=1);
        sr = PolyGBUtil.subRing(self.list);
        srr = [ RingElem(a) for a in sr ];
        return srr;

    def subringmember(self, list, a):
        '''Sub ring member test. list is a Groebner base.
           Test if a \in K[list].
        '''
        sr = [ p.elem for p in list];
        if isinstance(a,RingElem):
            a = a.elem;
        b = PolyGBUtil.subRingMember(sr, a);
        return b;

    def CRT(self,polystr="", list=None, rem=None):
        '''Chinese remainder theorem.
        '''
        if list == None:
           sr = StringReader( polystr );
           tok = GenPolynomialTokenizer(self.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = pylist2arraylist(list,rec=2);
        if rem == None:
           raise ValueError, "No remainders given."
        else:
           self.remlist = pylist2arraylist(rem,rec=1);
        #print "list = " + str(self.list);
        #print "remlist = " + str(self.remlist);
        #print
        h = PolyGBUtil.chineseRemainderTheorem(self.list, self.remlist);
        if h != None:
            h = RingElem(h);
        return h;

    def CRTinterpol(self,polystr="", list=None, rem=None):
        '''Chinese remainder theorem, interpolation.
        '''
        if list == None:
           sr = StringReader( polystr );
           tok = GenPolynomialTokenizer(self.ring,sr);
           self.list = tok.nextPolynomialList();
        else:
           self.list = pylist2arraylist(list,rec=2);
        if rem == None:
           raise ValueError, "No remainders given."
        else:
           self.remlist = pylist2arraylist(rem,rec=1);
        #print "ring = " + str(self.ring);
        #print "list = " + str(self.list);
        #print "remlist = " + str(self.remlist);
        #print
        h = PolyGBUtil.CRTInterpolation(self.ring, self.list, self.remlist);
        if h != None:
            h = RingElem(h);
        return h;


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
        self.croots = None;
        self.prime = None;
        self.primary = None;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset.toScript());

    def __eq__(self,other):
        '''Test if two ideals are equal.
        '''
        o = other;
        if isinstance(other,Ideal):
            o = other.pset;
        return self.pset.equals(o)

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
        kind = "";
        t = System.currentTimeMillis();
        if cofac.isField():
            G = GroebnerBaseSeq(ReductionSeq(),OrderedSyzPairlist()).GB(F);
            #G = GroebnerBaseSeqIter(ReductionSeq(),OrderedSyzPairlist()).GB(F);
            #G = GroebnerBaseSeq().GB(F);
            kind = "field"
        else:
            if isinstance(cofac,GenPolynomialRing) and cofac.isCommutative():
                G = GroebnerBasePseudoRecSeq(cofac).GB(F);
                kind = "pseudorec"
            else:
                G = GroebnerBasePseudoSeq(cofac).GB(F);
                kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) GB executed in %s ms" % (kind, t); 
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
            if isinstance(cofac,GenPolynomialRing) and cofac.isCommutative():
                b = GroebnerBasePseudoRecSeq(cofac).isGB(F);
            else:
                b = GroebnerBasePseudoSeq(cofac).isGB(F);
        t = System.currentTimeMillis() - t;
        #print "isGB executed in %s ms" % t; 
        return b;

    def eGB(self):
        '''Compute an e-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
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
        b = EGroebnerBaseSeq().isGB(F)
        t = System.currentTimeMillis() - t;
        print "is e-GB test executed in %s ms" % t; 
        return b;

    def eExtGB(self):
        '''Compute an extended e-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        G = EGroebnerBaseSeq().extGB(F)
        t = System.currentTimeMillis() - t;
        print "sequential extended e-GB executed in %s ms" % t;
        return G; #Ideal(self.ring,"",G);

    def iseExtGB(self, eg):
        '''Test if eg is an extended e-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        b = EGroebnerBaseSeq().isMinReductionMatrix(eg)
        t = System.currentTimeMillis() - t;
        print "sequential test extended e-GB executed in %s ms" % t;
        return b;

    def dGB(self):
        '''Compute an d-Groebner base.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
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
        b = DGroebnerBaseSeq().isGB(F)
        t = System.currentTimeMillis() - t;
        print "is d-GB test executed in %s ms" % t; 
        return b;

    def parNewGB(self,th):
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

    def parGB(self,th):
        '''Compute in parallel a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        cofac = s.ring.coFac;
        if cofac.isField():
            bbpar = GroebnerBaseParallel(th);
        else:
            bbpar = GroebnerBasePseudoParallel(th,cofac);
        t = System.currentTimeMillis();
        G = bbpar.GB(F);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        print "parallel %s executed in %s ms" % (th, t); 
        return Ideal(self.ring,"",G);

    def distGB(self,th=2,machine="examples/machines.localhost",port=55711):
        '''Compute on a distributed system a Groebner base.
        '''
        s = self.pset;
        F = s.list;
        t = System.currentTimeMillis();
        #old: gbd = GBDist(th,machine,port);
        gbd = GroebnerBaseDistributedEC(machine,th,port);
        #gbd = GroebnerBaseDistributedHybridEC(machine,th,port);
        t1 = System.currentTimeMillis();
        G = gbd.GB(F);
        t1 = System.currentTimeMillis() - t1;
        gbd.terminate();
        t = System.currentTimeMillis() - t;
        print "distributed %s executed in %s ms (%s ms start-up)" % (th,t1,t-t1); 
        return Ideal(self.ring,"",G);

    def distClient(self,port=4711): #8114
        '''Client for a distributed computation.
        '''
        e1 = ExecutableServer( port );
        e1.init();
        e2 = ExecutableServer( port+1 );
        e2.init();
        self.exers = [e1,e2];
        return None;

    def distClientStop(self):
        '''Stop client for a distributed computation.
        '''
        for es in self.exers:
            es.terminate();
        return None;

    def eReduction(self,p):
        '''Compute a e-normal form of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        if isinstance(p,RingElem):
            p = p.elem;
        t = System.currentTimeMillis();
        n = EReductionSeq().normalform(G,p);
        t = System.currentTimeMillis() - t;
        print "sequential eReduction executed in %s ms" % t; 
        return RingElem(n);

    def eReductionRec(self,row,p):
        '''Compute a e-normal form with recording of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        row = pylist2arraylist(row,None,rec=1);
        if isinstance(p,RingElem):
            p = p.elem;
        t = System.currentTimeMillis();
        n = EReductionSeq().normalform(row,G,p);
        t = System.currentTimeMillis() - t;
        print "sequential eReduction recording executed in %s ms" % t;
        return row, RingElem(n);

    def iseReductionRec(self, row, p, n):
        '''Test if n is a e-normal form with recording of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        row = pylist2arraylist(row,None,rec=1);
        if isinstance(p,RingElem):
            p = p.elem;
        if isinstance(n,RingElem):
            n = n.elem;
        t = System.currentTimeMillis();
        b = EReductionSeq().isReductionNF(row,G,p,n);
        t = System.currentTimeMillis() - t;
        return b;

    def reduction(self,p):
        '''Compute a normal form of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        if isinstance(p,RingElem):
            p = p.elem;
        if self.ring.ring.coFac.isField():
           n = ReductionSeq().normalform(G,p);
        else:
           n = PseudoReductionSeq().normalform(G,p);
           #F = PseudoReductionSeq().normalformFactor(G,p);
           #print "F.multiplicator = " + str(F.multiplicator)
           #n = F.pol
        return RingElem(n);

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

    def lift(self,p):
        '''Represent p as element of this ideal.
        '''
        G = self.pset.list;
        z = self.ring.ring.getZERO();
        R = [ z for x in G];
        if isinstance(p,RingElem):
            p = p.elem;
        if self.ring.ring.coFac.isField():
           n = ReductionSeq().normalform(R,G,p);
        else:
           n = PseudoReductionSeq().normalform(R,G,p);
        if not n.isZERO():
           raise ValueError, "p ist not a member of the ideal"
        return [ RingElem(f) for f in R ];

    def interreduced_basis(self):
        '''Compute a interreduced ideal basis of this.

        Compatibility method for Sage/Singular.
        '''
        F = self.pset.list;
        if self.ring.ring.coFac.isField():
           N = ReductionSeq().irreducibleSet(F);
        else:
           N = PseudoReductionSeq().irreducibleSet(F);
        #N = GroebnerBaseSeq().minimalGB(F);
        return [ RingElem(n) for n in N ];

    def intersectRing(self,ring):
        '''Compute the intersection of this and the given polynomial ring.
        '''
        s = jas.application.Ideal(self.pset);
        N = s.intersect(ring.ring);
        return Ideal(ring,"",N.getList());

    def intersect(self,id2):
        '''Compute the intersection of this and the given ideal id2.
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

    def sat(self,id2):
        '''Compute the saturation of this with respect to given ideal id2.
        '''
        s1 = jas.application.Ideal(self.pset);
        s2 = jas.application.Ideal(id2.pset);
        #Q = s1.infiniteQuotient(s2);
        Q = s1.infiniteQuotientRab(s2);
        return Ideal(self.ring,"",Q.getList());

    def sum(self,other):
        '''Compute the sum of this and the ideal.
        '''
        s = jas.application.Ideal(self.pset);
        t = jas.application.Ideal(other.pset);
        N = s.sum( t );
        return Ideal(self.ring,"",N.getList());

    def univariates(self):
        '''Compute the univariate polynomials in each variable of this ideal.
        '''
        s = jas.application.Ideal(self.pset);
        L = s.constructUnivariate();
        N = [ RingElem(e) for e in L ];
        return N;

    def inverse(self,p):
        '''Compute the inverse polynomial modulo this ideal, if it exists.
        '''
        s = jas.application.Ideal(self.pset);
        if isinstance(p,RingElem):
            p = p.elem;
        i = s.inverse(p);
        return RingElem(i);

    def eInverse(self,p):
        '''Compute the e-inverse polynomial modulo this e-ideal, if it exists.
        '''
        if isinstance(p,RingElem):
            p = p.elem;
        i = EGroebnerBaseSeq().inverse(p, self.list);
        return RingElem(i);

    def iseInverse(self,i,p):
        '''Test if i is a e-inverse of p modulo this e-ideal.
        '''
        if isinstance(i,RingElem):
            i = i.elem;
        if isinstance(p,RingElem):
            p = p.elem;
        r = EReductionSeq().normalform(self.list, i.multiply(p));
        return r.abs().isONE();


    def optimize(self):
        '''Optimize the term order on the variables.
        '''
        p = self.pset;
        o = TermOrderOptimization.optimizeTermOrder(p);
        r = Ring("",o.ring);
        return Ideal(r,"",o.list);

    def dimension(self):
        '''Compute the dimension of the ideal.
        '''
        I = jas.application.Ideal(self.pset);
        d = I.dimension();
        return d;

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

    def decomposition(self):
        '''Compute irreducible decomposition of this ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.irrdec = I.decomposition();
        return self.irrdec;

    def complexRoots(self):
        '''Compute complex roots of 0-dim ideal.
        '''
        I = jas.application.Ideal(self.pset);
        self.croots = jas.application.PolyUtilApp.complexAlgebraicRoots(I);
        for R in self.croots:
            R.doDecimalApproximation();
        return self.croots;

    def complexRootsPrint(self):
        '''Print decimal approximation of complex roots of 0-dim ideal.
        '''
        if self.croots == None:
            I = jas.application.Ideal(self.pset);
            self.croots = jas.application.PolyUtilApp.realAlgebraicRoots(I);
            for R in self.croots:
                R.doDecimalApproximation();
        for Ic in self.croots:
            for Dc in Ic.decimalApproximation():
                print str(Dc);
            print;

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
        if isinstance(mf,RingElem):
            mf = mf.ring;
        rm = GenPolynomialRing( mf, r.nvar, r.tord, r.vars );
        pm = PolyUtil.fromIntegerCoefficients(rm,l);
        r = Ring("",rm);
        return Ideal(r,"",pm);

    def CS(self):
        '''Compute a Characteristic Set.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list;
        t = System.currentTimeMillis();
        if cofac.isField():
            G = CharacteristicSetWu().characteristicSet(F);
        else:
            print "CS not implemented for coefficients %s" % cofac.toScriptFactory();
            G = None;
        t = System.currentTimeMillis() - t;
        print "sequential CS executed in %s ms" % t; 
        return Ideal(self.ring,"",G);

    def isCS(self):
        '''Test for Characteristic Set.
        '''
        s = self.pset;
        cofac = s.ring.coFac;
        F = s.list.clone(); 
        Collections.reverse(F); # todo
        t = System.currentTimeMillis();
        if cofac.isField():
            b = CharacteristicSetWu().isCharacteristicSet(F);
        else:
            print "isCS not implemented for coefficients %s" % cofac.toScriptFactory();
            b = False;
        t = System.currentTimeMillis() - t;
        return b;

    def csReduction(self,p):
        '''Compute a normal form of p with respect to this characteristic set.
        '''
        s = self.pset;
        F = s.list.clone(); 
        Collections.reverse(F); # todo
        if isinstance(p,RingElem):
            p = p.elem;
        t = System.currentTimeMillis();
        n = CharacteristicSetWu().characteristicSetReduction(F,p);
        t = System.currentTimeMillis() - t;
        #print "sequential char set reduction executed in %s ms" % t; 
        return RingElem(n);

    def syzygy(self):
        '''Syzygy of generating polynomials.
        '''
        p = self.pset;
        l = p.list;
        t = System.currentTimeMillis();
        s = SyzygySeq(p.ring.coFac).zeroRelations( l );
        t = System.currentTimeMillis() - t;
        print "executed syzygy in %s ms" % t; 
        m = Module("",p.ring); #cols=s[0].size()
        return SubModule(m,"",s);

    def isSyzygy(self,m):
        '''Test if this is a syzygy of the module in m.
        '''
        p = self.pset;
        g = p.list;
        l = m.list;
        t = System.currentTimeMillis();
        z = SyzygySeq(p.ring.coFac).isZeroRelation( l, g );
        t = System.currentTimeMillis() - t;
        print "executed isSyzygy in %s ms" % t; 
        return z;


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
            return self.pset.toScript();
        else:
#            return self.gbsys.toString(); # toScript() not available
            return self.pset.toScript() + "\n" + self.gbsys.toScript();

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
        if isinstance(mf,RingElem):
            mf = mf.ring;
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
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextSolvablePolynomialRing();
           #tok = GenPolynomialTokenizer(pfac,sr);
           #plist = tok.nextSolvablePolynomialList();
           #self.pset = PolynomialList(pfac,plist);
           self.ring = pfac;
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;
        if not self.ring.isAssociative():
           print "warning: ring is not associative";
        else:
           print "ring is associative";
        Ring.__init__(self,ring=self.ring)

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

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        I = SolvableIdeal(self, "( " + poly + " )");
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
        #self.pset = PolynomialList(ring.ring,self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.pset.toScript());

    def __cmp__(self,other):
        '''Compare two ideals.
        '''
        t = False;
        if not isinstance(other,SolvableIdeal):
            return t;
        t = self.pset.compareTo(other.pset);
        return t; 

    def __eq__(self,other):
        '''Test if two ideals are equal.
        '''
        if not isinstance(other, SolvableIdeal):
            return False;
        s = self.pset;
        t = other.pset;
        return s.equals(t)

    def leftGB(self):
        '''Compute a left Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): #and cofac.isCommutative():
           G = SolvableGroebnerBasePseudoRecSeq(cofac).leftGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              G = SolvableGroebnerBaseSeq().leftGB(F);
              kind = "field|nocom"
           else:
              G = SolvableGroebnerBasePseudoSeq(cofac).leftGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) left GB executed in %s ms" % (kind, t); 
        return SolvableIdeal(self.ring,"",G);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): #and cofac.isCommutative():
           b = SolvableGroebnerBasePseudoRecSeq(cofac).isLeftGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              b = SolvableGroebnerBaseSeq().isLeftGB(F);
              kind = "field|nocom"
           else:
              b = SolvableGroebnerBasePseudoSeq(cofac).isLeftGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) isLeftGB executed in %s ms" % (kind, t); 
        return b;

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): # and cofac.isCommutative():
           G = SolvableGroebnerBasePseudoRecSeq(cofac).twosidedGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              G = SolvableGroebnerBaseSeq().twosidedGB(F);
              kind = "field|nocom"
           else:
              G = SolvableGroebnerBasePseudoSeq(cofac).twosidedGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) twosided GB executed in %s ms" % (kind, t); 
        return SolvableIdeal(self.ring,"",G);

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): # and cofac.isCommutative():
           b = SolvableGroebnerBasePseudoRecSeq(cofac).isTwosidedGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              b = SolvableGroebnerBaseSeq().isTwosidedGB(F);
              kind = "field|nocom"
           else:
              b = SolvableGroebnerBasePseudoSeq(cofac).isTwosidedGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) isTwosidedGB executed in %s ms" % (kind, t); 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): # and cofac.isCommutative():
           G = SolvableGroebnerBasePseudoRecSeq(cofac).rightGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              G = SolvableGroebnerBaseSeq().rightGB(F);
              kind = "field|nocom"
           else:
              G = SolvableGroebnerBasePseudoSeq(cofac).rightGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) right GB executed in %s ms" % (kind, t); 
        return SolvableIdeal(self.ring,"",G);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.pset.list;
        kind = "";
        t = System.currentTimeMillis();
        if isinstance(cofac,GenPolynomialRing): # and cofac.isCommutative():
           b = SolvableGroebnerBasePseudoRecSeq(cofac).isRightGB(F);
           kind = "pseudorec"
        else:
           if cofac.isField() or not cofac.isCommutative():
              b = SolvableGroebnerBaseSeq().isRightGB(F);
              kind = "field|nocom"
           else:
              b = SolvableGroebnerBasePseudoSeq(cofac).isRightGB(F);
              kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) isRightGB executed in %s ms" % (kind, t); 
        return b;

    def intersectRing(self,ring):
        '''Compute the intersection of this and the polynomial ring.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        N = s.intersect(ring.ring);
        return SolvableIdeal(self.ring,"",N.getList());

    def intersect(self,other):
        '''Compute the intersection of this and the other ideal.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        t = jas.application.SolvableIdeal(other.pset);
        N = s.intersect( t );
        return SolvableIdeal(self.ring,"",N.getList());

    def sum(self,other):
        '''Compute the sum of this and the other ideal.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        t = jas.application.SolvableIdeal(other.pset);
        N = s.sum( t );
        return SolvableIdeal(self.ring,"",N.getList());

    def univariates(self):
        '''Compute the univariate polynomials in each variable of this twosided ideal.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        L = s.constructUnivariate();
        N = [ RingElem(e) for e in L ];
        return N;

    def toQuotientCoefficients(self):
        '''Convert to polynomials with SolvableQuotient coefficients.
        '''
        if isinstance(self.pset.ring.coFac,SolvableResidueRing):
           cf = self.pset.ring.coFac.ring;
        else:
           if isinstance(self.pset.ring.coFac,GenSolvablePolynomialRing):
              cf = self.pset.ring.coFac;
              #else: if @pset.ring.coFac.getClass().getSimpleName() == "GenPolynomialRing"
              #   cf = @pset.ring.coFac;
              #   puts "cf = " + cf.toScript();
           else:
              return self;
        rrel = self.pset.ring.table.relationList();
        rrel.addAll(self.pset.ring.polCoeff.coeffTable.relationList());
        #print "rrel = " + str(rrel);
        qf = SolvableQuotientRing(cf);
        qr = QuotSolvablePolynomialRing(qf,self.pset.ring);
        #print "qr = " + str(qr);
        qrel = [ RingElem(qr.fromPolyCoefficients(r)) for r in rrel ];
        qring = SolvPolyRing(qf,self.ring.ring.getVars(),self.ring.ring.tord,qrel);
        #print "qring = " + str(qring);
        qlist = [ qr.fromPolyCoefficients(self.ring.ring.toPolyCoefficients(r)) for r in self.list ];
        qlist = [ RingElem(r) for r in qlist ];
        return SolvableIdeal(qring,"",qlist);

    def inverse(self,p):
        '''Compute the inverse polynomial modulo this ideal, if it exists.
        '''
        s = jas.application.SolvableIdeal(self.pset);
        if isinstance(p,RingElem):
            p = p.elem;
        i = s.inverse(p);
        return RingElem(i);

    def leftReduction(self,p):
        '''Compute a left normal form of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        if isinstance(p,RingElem):
            p = p.elem;
        n = SolvableReductionSeq().leftNormalform(G,p);
        return RingElem(n);

    def rightReduction(self,p):
        '''Compute a right normal form of p with respect to this ideal.
        '''
        s = self.pset;
        G = s.list;
        if isinstance(p,RingElem):
            p = p.elem;
        n = SolvableReductionSeq().rightNormalform(G,p);
        return RingElem(n);

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

    def leftSyzygy(self):
        '''left Syzygy of generating polynomials.
        '''
        p = self.pset;
        l = p.list;
        t = System.currentTimeMillis();
        s = SolvableSyzygySeq(p.ring.coFac).leftZeroRelationsArbitrary( l );
        t = System.currentTimeMillis() - t;
        print "executed left syzygy in %s ms" % t; 
        m = SolvableModule("",p.ring);
        return SolvableSubModule(m,"",s);

    def isLeftSyzygy(self,m):
        '''Test if this is a left syzygy of the module in m.
        '''
        p = self.pset;
        g = p.list;
        l = m.list;
        t = System.currentTimeMillis();
        z = SolvableSyzygySeq(p.ring.coFac).isLeftZeroRelation( l, g );
        t = System.currentTimeMillis() - t;
        print "executed isLeftSyzygy in %s ms" % t; 
        return z;

    def rightSyzygy(self):
        '''right Syzygy of generating polynomials.
        '''
        p = self.pset;
        l = p.list;
        t = System.currentTimeMillis();
        s = SolvableSyzygySeq(p.ring.coFac).rightZeroRelationsArbitrary( l );
        t = System.currentTimeMillis() - t;
        print "executed right syzygy in %s ms" % t; 
        m = SolvableModule("",p.ring);
        return SolvableSubModule(m,"",s);

    def isRightSyzygy(self,m):
        '''Test if this is a right syzygy of the module in m.
        '''
        p = self.pset;
        g = p.list;
        l = m.list;
        t = System.currentTimeMillis();
        z = SolvableSyzygySeq(p.ring.coFac).isRightZeroRelation( l, g );
        t = System.currentTimeMillis() - t;
        print "executed isRightSyzygy in %s ms" % t; 
        return z;

    def dimension(self):
        '''Compute the dimension of the ideal.
        '''
        I = jas.application.SolvableIdeal(self.pset);
        d = I.dimension();
        return d;



class Module:
    '''Represents a JAS module over a polynomial ring.

    Method to create sub-modules.
    '''

    def __init__(self,modstr="",ring=None,cols=0):
        '''Module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextPolynomialRing();
           #tok = GenPolynomialTokenizer(pfac,sr);
           #mlist = tok.nextSubModuleList();
           #self.mset = ModuleList(pfac,mlist);
           #if self.mset.cols >= 0:
           #    self.cols = self.mset.cols;
           #else:
           self.ring = pfac
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;
        self.mset = ModuleList(self.ring,None); # because of toScript
        if cols < 0:
           cols = 0;
        self.cols = cols;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript());

    def submodul(self,modstr="",list=None):
        '''Create a sub-module.
        '''
        return SubModule(self,modstr,list);

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        I = SubModule(self, "( " + poly + " )");
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

    def inject_variables(self):
        '''Inject generators as variables into the main global namespace
        '''
        inject_generators(self.gens());


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
        #G = ModGroebnerBaseSeq(self.module.ring.coFac).GB(self.mset);
        G = GroebnerBaseSeq().GB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed module GB in %s ms" % t; 
        return SubModule(self.module,"",G.list);

    def isGB(self):
        '''Test if this is a Groebner base.
        '''
        t = System.currentTimeMillis();
        #b = ModGroebnerBaseSeq(self.module.ring.coFac).isGB(self.mset);
        b = GroebnerBaseSeq().isGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isGB executed in %s ms" % t; 
        return b;

    def isSyzygy(self,g):
        '''Test if this is a syzygy of the vectors in g.
        '''
        l = self.list;
        if isinstance(g,Ideal):
           s = g.pset.list;
        else:
           if isinstance(g,SubModule):
              s = g.mset;
              l = self.mset;
           else:
              raise ValueError, "unknown type %s" % g.getClass().getName();
        #print "l = %s" % l;
        #print "s = %s" % s;
        t = System.currentTimeMillis();
        z = SyzygySeq(self.module.ring.coFac).isZeroRelation( l, s );
        t = System.currentTimeMillis() - t;
        print "executed isSyzygy in %s ms" % t; 
        return z;

    def syzygy(self):
        '''Compute syzygys of this module.
        '''
        l = self.mset;
        t = System.currentTimeMillis();
        p = SyzygySeq(self.module.ring.coFac).zeroRelations( l );
        t = System.currentTimeMillis() - t;
        print "executed module syzygy in %s ms" % t; 
        #print "p = " + str(p.ring.toScript());
        m = Module("",p.ring,p.cols);
        return SubModule(m,"",p.list);


class SolvableModule(Module):
    '''Represents a JAS module over a solvable polynomial ring.

    Method to create solvable sub-modules.
    '''

    def __init__(self,modstr="",ring=None,cols=0):
        '''Solvable module constructor.
        '''
        if ring == None:
           sr = StringReader( modstr );
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextSolvablePolynomialRing();
           #tok = GenPolynomialTokenizer(pfac,sr);
           #mlist = tok.nextSolvableSubModuleList();
           #self.mset = PolynomialList(pfac,mlist);
           #if self.mset.cols >= 0:
           #    self.cols = self.mset.cols;
           self.ring = pfac;
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;
        self.mset = ModuleList(self.ring,None);
        if cols < 0:
           cols = 0;
        self.cols = cols;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.mset.toScript());

    def submodul(self,modstr="",list=None):
        '''Create a solvable sub-module.
        '''
        return SolvableSubModule(self,modstr,list);

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        I = SolvableSubModule(self, "( " + poly + " )");
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
        #G = SolvableGroebnerBaseSeq(self.module.ring.coFac).leftGB(self.mset);
        G = SolvableGroebnerBaseSeq().leftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed left module GB in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isLeftGB(self):
        '''Test if this is a left Groebner base.
        '''
        t = System.currentTimeMillis();
        #b = SolvableGroebnerBaseSeq(self.module.ring.coFac).isLeftGB(self.mset);
        b = SolvableGroebnerBaseSeq().isLeftGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isLeftGB executed in %s ms" % t; 
        return b;

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        t = System.currentTimeMillis();
        #G = SolvableGroebnerBaseSeq(self.module.ring.coFac).twosidedGB(self.mset);
        G = SolvableGroebnerBaseSeq().twosidedGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed twosided module GB in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        t = System.currentTimeMillis();
        #b = SolvableGroebnerBaseSeq(self.module.ring.coFac).isTwosidedGB(self.mset);
        b = SolvableGroebnerBaseSeq().isTwosidedGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isTwosidedGB executed in %s ms" % t; 
        return b;

    def rightGB(self):
        '''Compute a right Groebner base.
        '''
        t = System.currentTimeMillis();
        #G = SolvableGroebnerBaseSeq(self.module.ring.coFac).rightGB(self.mset);
        G = SolvableGroebnerBaseSeq().rightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "executed right module GB in %s ms" % t; 
        return SolvableSubModule(self.module,"",G.list);

    def isRightGB(self):
        '''Test if this is a right Groebner base.
        '''
        t = System.currentTimeMillis();
        #b = SolvableGroebnerBaseSeq(self.module.ring.coFac).isRightGB(self.mset);
        b = SolvableGroebnerBaseSeq().isRightGB(self.mset);
        t = System.currentTimeMillis() - t;
        print "module isRightGB executed in %s ms" % t; 
        return b;

    def isLeftSyzygy(self,g):
        '''Test if this is a syzygy of the vectors in g.
        '''
        l = self.list;
        if isinstance(g,SolvableIdeal):
           s = g.pset.list;
        else:
           if isinstance(g,SolvableSubModule):
              s = g.mset;
              l = self.mset;
           else:
              raise ValueError, "unknown type %s" % g.getClass().getName();
        #print "l = %s" % l; 
        #print "g = %s" % g; 
        t = System.currentTimeMillis();
        z = SolvableSyzygySeq(self.module.ring.coFac).isLeftZeroRelation( l, s );
        t = System.currentTimeMillis() - t;
        print "executed isLeftSyzygy in %s ms" % t; 
        return z;

    def leftSyzygy(self):
        '''Compute left syzygys of this module.
        '''
        l = self.mset;
        t = System.currentTimeMillis();
        p = SolvableSyzygySeq(self.module.ring.coFac).leftZeroRelationsArbitrary( l );
        t = System.currentTimeMillis() - t;
        print "executed left module syzygy in %s ms" % t; 
        #print "p = " + str(p.ring.toScript());
        m = SolvableModule("",p.ring,p.cols);
        return SolvableSubModule(m,"",p.list);

    def isRightSyzygy(self,g):
        '''Test if this is a syzygy of the vectors in g.
        '''
        l = self.list;
        if isinstance(g,SolvableIdeal):
           s = g.pset.list;
        else:
           if isinstance(g,SolvableSubModule):
              s = g.mset;
              l = self.mset;
           else:
              raise ValueError, "unknown type %s" % g.getClass().getName();
        #print "l = %s" % l; 
        #print "g = %s" % g; 
        t = System.currentTimeMillis();
        z = SolvableSyzygySeq(self.module.ring.coFac).isRightZeroRelation( l, s );
        t = System.currentTimeMillis() - t;
        print "executed isRightSyzygy in %s ms" % t; 
        return z;

    def rightSyzygy(self):
        '''Compute right syzygys of this module.
        '''
        l = self.mset;
        t = System.currentTimeMillis();
        # no: p = SolvableSyzygySeq(self.module.ring.coFac).rightZeroRelations( l );
        p = SolvableSyzygySeq(self.module.ring.coFac).rightZeroRelationsArbitrary( l );
        t = System.currentTimeMillis() - t;
        print "executed right module syzygy in %s ms" % t; 
        #print "p = " + str(p.ring.toScript());
        m = SolvableModule("",p.ring,p.cols);
        return SolvableSubModule(m,"",p.list);


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
                tok = RingFactoryTokenizer(sr);
                pfac = tok.nextPolynomialRing();
                #tok = GenPolynomialTokenizer(pfac,sr);
                #plist = tok.nextPolynomialList();
                #pset = PolynomialList(pfac,plist);
                ring = pfac;
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

    def inject_variables(self):
        '''Inject generators as variables into the main global namespace
        '''
        inject_generators(self.gens());

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
                tok = RingFactoryTokenizer(sr);
                pfac = tok.nextPolynomialRing();
                #tok = GenPolynomialTokenizer(pfac,sr);
                #plist = tok.nextPolynomialList();
                #pset = PolynomialList(pfac,plist);
                ring = pfac;
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

    def inject_variables(self):
        '''Inject generators as variables into the main global namespace
        '''
        inject_generators(self.gens());

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
        ll = [ e.toScript() for e in self.list ]
        return "( " + ", ".join(ll) + " )"; 

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
               #n = e.getClass().getSimpleName();
               if isinstance(e,ArrayList) or isinstance(e,LinkedList):
                   t = False;
           except:
               pass;
           if t and fac != None:
               #print "e.p(%s) = %s :: %s" % (e,e,fac);
               e = fac.parse( str(e) ); #or makeJasArith(e) ?
           L.add(e);
       list = L;
    #print "list type(%s) = %s" % (list,type(list));
    return list


def arraylist2pylist(list,rec=1):
    '''Convert a Java ArrayList to a Python list.

    If list is a Java ArrayList list, it is converted, else list is left unchanged.
    '''
    #print "list type(%s) = %s" % (list,type(list));
    if isinstance(list,List):
       L = [];
       for e in list:
           if not isinstance(e,RingElem):
               e = RingElem(e);
           L.append(e);
       list = L;
    #print "list type(%s) = %s" % (list,type(list));
    return list


def makeJasArith(item):
    '''Construct a jas.arith object.
    If item is a python tuple or list then a BigRational, BigComplex is constructed.
    If item is a python float then a BigDecimal is constructed.
    Otherwise, item is returned unchanged.
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
        #print "makeJasArith: type(%s) = %s" % (jasArith,type(jasArith));
        return jasArith;
    #print "makeJasArith: unknown item type(%s) = %s" % (item,type(item));
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
    if z != 0 and ( z == False or z == True ): # never true
        field = z;
        z = 0;
    if m < ModLongRing.MAX_LONG:
       if m < ModIntRing.MAX_INT:
           if field:
              mf = ModIntRing(m,field);
           else:
              mf = ModIntRing(m);
           r = ModInt(mf,z);
       else:
           if field:
              mf = ModLongRing(m,field);
           else:
              mf = ModLongRing(m);
           r = ModLong(mf,z);
    else:
       if field:
          mf = ModIntegerRing(m,field);
       else:
          mf = ModIntegerRing(m);
       r = ModInteger(mf,z);
    return RingElem(r);


def ZML(m,z=0,field=False):
    '''Create JAS ModLong as ring element.
    '''
    return ZM(m,z,field);


def ZMI(m,z=0,field=False):
    '''Create JAS ModInt as ring element.
    '''
    return ZM(m,z,field);


def GF(m,z=0):
    '''Create JAS ModInteger as field element.
    '''
    #print "m = %s" % m
    return ZM(m,z,True);


def GFL(m,z=0):
    '''Create JAS ModLong as field element.
    '''
    #print "m = %s" % m
    return ZM(m,z,True);


def GFI(m,z=0):
    '''Create JAS ModInt as field element.
    '''
    return ZM(m,z,True);


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
    #print "d type(%s) = %s" % (r,type(r));
    if im.isZERO():
        if re.isZERO():
            c = Complex(r);
        else:
            c = Complex(r,re);
    else:
        c = Complex(r,re,im);
    #print "d type(%s) = %s" % (c,type(c));
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
    cf = BigQuaternionRing();
    c = BigQuaternion(cf, re,im,jm,km);
    return RingElem(c);


def Oct(ro=0,io=0):
    '''Create JAS BigOctonion as ring element.
    '''
    cf = BigQuaternionRing();
    if ro == 0:
        ro = BigQuaternion(cf);
    if io == 0:
        io = BigQuaternion(cf);
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
#    if (z == True or z == False): # not working
#        field = z;
#        z = 0;
    #print "m.getClass() = " + str(m.getClass().getName());
    #print "field = " + str(field);
    if isinstance(m,AlgebraicNumber):
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


_finiteFields = {}; '''List of already constructed FiniteFields.'''


def FF(p,n,z=0):
    '''Create JAS Field element as ring element. 
       FF has p<sup>n</sup> elements.
    '''
    if isinstance(p,RingElem):
        p = p.elem;
    if isinstance(n,RingElem):
        n = n.elem;
    if isinstance(z,RingElem):
        z = z.elem;
    if (p == 0): 
        raise "p ==";
    if (n == 0): 
        raise "n ==";
    field = True;
    if not PrimeInteger.isPrime(p):
      field = False;
      raise ValueError, str(p) + " not prime.";
    mf = _finiteFields.get( (p,n) );
    if mf == None:
       cf = GF(p).ring;
       mf = PolyUfdUtil.algebraicNumberField(cf,n);
       _finiteFields[ (p,n) ] = mf;
    #print "mf = " + mf.toScript();
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
        i = pylist2arraylist(i,BigRational(),1);
        i = Interval(i[0],i[1]);
    #print "m.getClass() = " + str(m.getClass().getName());
    if isinstance(m,RealAlgebraicNumber):
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
    if isinstance(ideal.getList().get(0).ring,ResidueRing):
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
    if isinstance(ideal.getList().get(0).ring,LocalRing):
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


def SRF(pr,d=0,n=1):
    '''Create JAS rational function SolvableQuotient as ring element.
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
    qr = SolvableQuotientRing(pr);
    if d == 0:
        r = SolvableQuotient(qr);
    else:
        if n == 1:
            r = SolvableQuotient(qr,d);
        else:
            r = SolvableQuotient(qr,d,n);
    return RingElem(r);


def SRC(ideal,r=0):
    '''Create JAS polynomial SolvableResidue as ring element.
    '''
    #print "ideal = " + str(ideal);
    if ideal == None: # does not work
        print "ideal = " + str(ideal);
        if False:
            raise ValueError, "No ideal given."
    if isinstance(ideal,SolvableIdeal):
        ideal = jas.application.SolvableIdeal(ideal.pset);
        #ideal.doGB();
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    if isinstance(ideal.getList().get(0).ring,SolvableResidueRing):
        rc = SolvableResidueRing( ideal.getList().get(0).ring.ideal );
    else:
        rc = SolvableResidueRing(ideal);
    if isinstance(r,RingElem):
        r = r.elem;
    if r == 0:
        r = SolvableResidue(rc);
    else:
        r = SolvableResidue(rc,r);
    return RingElem(r);


def SLC(ideal,d=0,n=1):
    '''Create JAS polynomial SolvableLocal as ring element.
    '''
    if ideal == None:
        #print "ideal = " + str(ideal);
        if False:
            raise ValueError, "No ideal given."
    if isinstance(ideal,SolvableIdeal):
        ideal = jas.application.SolvableIdeal(ideal.pset);
        #ideal.doGB();
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    if isinstance(ideal.getList().get(0).ring,SolvableLocalRing):
        lc = SolvableLocalRing( ideal.getList().get(0).ring.ideal );
    else:
        lc = SolvableLocalRing(ideal);
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
        r = SolvableLocal(lc);
    else:
        if n == 1:
            r = SolvableLocal(lc,d);
        else:
            r = SolvableLocal(lc,d,n);
    return RingElem(r);


def SLR(ideal,d=0,n=1):
    '''Create JAS polynomial SolvableLocalResidue as ring element.
    '''
    if ideal == None:
        #print "ideal = " + str(ideal);
        if False:
            raise ValueError, "No ideal given."
    if isinstance(ideal,SolvableIdeal):
        ideal = jas.application.SolvableIdeal(ideal.pset);
        #ideal.doGB();
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    cfr = ideal.getList().get(0).ring;
    if isinstance(cfr,SolvableLocalResidueRing):
        lc = SolvableLocalResidueRing( cfr.ideal );
    else:
        lc = SolvableLocalResidueRing(ideal);
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
        r = SolvableLocalResidue(lc);
    else:
        if n == 1:
            r = SolvableLocalResidue(lc,d);
        else:
            r = SolvableLocalResidue(lc,d,n);
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
    try:
        #print "r.class() = %s" % r.getClass().getSimpleName();
        if isinstance(r,Product):
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
        class Coeff( Coefficients ):
            def __init__(self,cofac):
                self.coFac = cofac;
            def generate(self,i):
                a = f(i);
                if isinstance(a,RingElem):
                    a = a.elem;
                #print "a = " + str(a);
                return a;
        r = UnivPowerSeries(ps,Coeff(cf));
    return RingElem(r);


def MPS(cofac,names,f=None,truncate=None):
    '''Create JAS MultiVarPowerSeries as ring element.
    '''
    cf = cofac;
    if isinstance(cofac,RingElem):
        cf = cofac.elem.factory();
    if isinstance(cofac,Ring):
        cf = cofac.ring;
    vars = names;
    if isinstance(vars,PyString):
       vars = GenPolynomialTokenizer.variableList(vars);
    nv = len(vars);
    if isinstance(truncate,RingElem):
        truncate = truncate.elem;
    if truncate == None:
        ps = MultiVarPowerSeriesRing(cf,nv,vars);
    else:
        ps = MultiVarPowerSeriesRing(cf,nv,vars,truncate);
    if f == None:
        r = ps.getZERO();
    else:
        class MCoeff( MultiVarCoefficients ):
            def __init__(self,r):
                MultiVarCoefficients.__init__(self,r);
                self.coFac = r.coFac;
            def generate(self,i):
                a = f(i);
                if isinstance(a,RingElem):
                    a = a.elem;
                return a;
        r = MultiVarPowerSeries(ps,MCoeff(ps));
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
    if isinstance(v,PyList) or isinstance(v,PyTuple):
        v = pylist2arraylist(v,cf,rec=1);
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
    if isinstance(v,PyList) or isinstance(v,PyTuple):
        v = pylist2arraylist(v,cf,rec=2);
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
            if not a.isAlgNum() and b.isAlgNum():
                s = b.coerce(a);
                o = b;
            else:
                s = a;
                o = a.coerce(b);
    except:
        s = a;
        o = a.coerce(b);
    return (s,o);


def isJavaInstance(a):
    '''Test if a is a Java instance.
    '''
    #print "a type(%s) = %s" % (a,type(a));
    try:
        c = a.getClass();
    except:
        return False;
    return True;
    

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
        try:
            return str(self.elem.toScript()); 
        except:
            return str(self.elem); 

    def zero(self):
        '''Zero element of this ring.
        '''
        return RingElem( self.ring.getZERO() );

    def isZERO(self):
        '''Test if this is the zero element of the ring.
        '''
        return self.elem.isZERO();

    def one(self):
        '''One element of this ring.
        '''
        return RingElem( self.ring.getONE() );

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
        if isinstance(self.elem,GenVector):
            #print "self, other = %s, %s " % (self,other);
            if isinstance(other,PyTuple) or isinstance(other,PyList):
                o = pylist2arraylist(other,self.elem.factory().coFac,rec=1);
                o = GenVector(self.elem.factory(),o);
                return RingElem( o );
        if isinstance(self.elem,GenMatrix):
            #print "self, other = %s, %s " % (type(self),type(other));
            #print "o type(%s) = %s, str = %s" % (o,type(o),str(o));
            if isinstance(other,PyTuple) or isinstance(other,PyList):
                o = pylist2arraylist(other,self.elem.factory().coFac,rec=2);
                o = GenMatrix(self.elem.factory(),o);
                return RingElem( o );
            if isinstance(other,GenVector):
                o = other;
                return RingElem( o );
        if isinstance(self.elem,GenPolynomial) or isinstance(self.elem,AlgebraicNumber):
            #print "self, other = %s, %s " % (type(self),type(other));
            #print "o type(%s) = %s, str = %s" % (other,type(other),str(other));
            if isinstance(other,PyInteger) or isinstance(other,PyLong):
               o = self.ring.fromInteger(other);
               return RingElem( o );
            if isinstance(other,PyList) or isinstance(other,PyTuple):
               #print "self, other = %s, %s " % (type(self),type(other));
               o = self.ring.parse( str(makeJasArith(other)) );
               return RingElem( o );
            if isinstance(other,RingElem):
                o = other.elem;
            else:
                o = other;
            if o == None:
                return RingElem( GenPolynomial(self.ring) )
            if isJavaInstance(o):
                #print "self.elem, o = %s, %s " % (type(self.ring.coFac),type(o));
                if isinstance(o,ExpVector): # want startsWith or substring(0,8) == "ExpVector":
                    #o = GenPolynomial(self.ring,o);
                    o = self.ring.valueOf(o);
                    return RingElem( o );
                #o = self.ring.parse( str(makeJasArith(o)) );
                #o = self.ring.valueOf( makeJasArith(o) );
                if isinstance(self.elem,GenPolynomial):
                    if self.ring.coFac.getClass().getSimpleName() == o.getClass().getSimpleName():
                        #o = GenPolynomial(self.ring,o);
                        o = self.ring.valueOf(o);
                        return RingElem( o );
                else: # AlgebraicNumber
                    if self.ring.ring.coFac.getClass().getSimpleName() == o.getClass().getSimpleName():
                        o = self.ring.valueOf(o);
                        return RingElem( o );
        if isinstance(other,RingElem):
            if self.isPolynomial() and not other.isPolynomial():
                #print "parse self.ring = %s" % (self.ring);
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
            if isinstance(self.elem,BigComplex):
                #print "other type(%s) = %s" % (o,type(o));
                o = CC( o );
                o = o.elem;
            if isinstance(self.elem,BigQuaternion):
                #print "other type(%s) = %s" % (o,type(o));
                o = Quat( o );
                o = o.elem;
            if isinstance(self.elem,BigOctonion):
                #print "other type(%s) = %s" % (o,type(o));
                o = Oct( Quat(o) );
                o = o.elem;
            if isinstance(self.elem,Product):
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
                    print "coerce_1: unknown other type(%s) = %s" % (other,type(other));
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
                if isinstance(self.elem,Product):
                    o = RR(self.ring, self.elem.idempotent().multiply(o) ); # valueOf
                    o = o.elem;
                else:
                    o = self.elem.factory().getZERO().sum( o );
            else:
                print "coerce_2: unknown other type(%s) = %s" % (other,type(other));
                print "coerce_2:          self type(%s) = %s" % (self,type(self));
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

    def isAlgNum(self):
        '''Test if this is an algebraic number.
        '''
        try:
            nv = self.elem.ring.ring.nvar;
        except:
            #print "isAlgNum fail: type(%s) = %s" % (self.elem,type(self.elem));
            return False;
        #print "isAlgNum true: type(%s) = %s" % (self.elem,type(self.elem));
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

    def __len__(self):
        '''Length of the element.
        '''
        return self.elem.length(); 

    def __mul__(self,other):
        '''Multiply two ring elements.
        '''
        [s,o] = coercePair(self,other);
        #print "self  type(%s) = %s" % (s,type(s));
        #print "other type(%s) = %s" % (o,type(o));
        if isinstance(s.elem,GenMatrix) and isinstance(o.elem,GenVector):
            return RingElem( BasicLinAlg().rightProduct(o.elem, s.elem) );
        if isinstance(s.elem,GenVector) and isinstance(o.elem,GenMatrix):
            return RingElem( BasicLinAlg().leftProduct(s.elem, o.elem) );
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

    def __pow__(self,other,n=None):
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
                if isinstance(n,BigRational): 
                    n = n.numerator().intValue() / n.denominator().intValue();
                #if isinstance(n,BigInteger):  # does not work
                if isinstance(n,BigInteger) or isinstance(n,Long) or isinstance(n,Integer): 
                    n = n.intValue();
        if n == None:
            n = other;
        if self.isFactory():
            p = Power(self.elem).power( self.elem, n );
        else:
            p = Power(self.ring).power( self.elem, n );
        return RingElem( p ); 

    def __eq__(self,other):
        '''Test if two ring elements are equal.
        '''
        if other == None:
            return False;
        [s,o] = coercePair(self,other);
        return s.elem.equals(o.elem)

    def __ne__(self,other):
        '''Test if two ring elements are not equal.
        '''
        if other == None:
            return False;
        [s,o] = coercePair(self,other);
        return not self.elem.equals(o.elem)

    def __float__(self):
        '''Convert to Python float.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        e = self.elem;
        if isinstance(e,BigInteger):
            e = BigRational(e);
        if isinstance(e,BigRational):
            e = BigDecimal(e);
        if isinstance(e,BigDecimal):
            e = e.toString();
        e = float(e);
        return e

    def __getitem__(self,i):
        '''Matrix or vector entry.
        '''
        if isinstance(i,RingElem):
            i = i.elem;
        if isinstance(self.elem,GenMatrix):
            e = self.elem.getRow(i);
            return RingElem( e );
        if isinstance(self.elem,GenVector):
            e = self.elem.get(i);
            return RingElem( e );
        raise ValueError, "no matrix or vector " + str(self.ring)

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

    def inject_variables(self):
        '''Inject generators as variables into the main global namespace
        '''
        inject_generators(self.gens());

    def monic(self):
        '''Monic polynomial.
        '''
        return RingElem( self.elem.monic() ); 

    def homogenize(self,var='h'):
        '''homogenize polynomial.

        INPUT:
           - "var" - variable name to use for homogenization
        '''
        if not self.isPolynomial():
           return self
        if var in self.ring.vars:
           r = self.ring.extend(1);
           print "WARN: case for variable %s not implemented, new name %s" % (var,r.vars[-1])
        else:
           r = self.ring.extend([var]);
        h = self.elem;
        h = h.homogenize(r);
        return RingElem(h);

    def evaluate(self,a):
        '''Evaluate at a for power series or polynomial.
        '''
        #print "self  type(%s) = %s" % (self,type(self));
        #print "a     type(%s) = %s" % (a,type(a));
        x = None;
        if isinstance(a,RingElem):
            x = a.elem;
        if isinstance(a,PyTuple) or isinstance(a,PyList):
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            #x = makeJasArith(a);
            x = pylist2arraylist(a);
        else:
            x = pylist2arraylist([a]);
        try:
            if isinstance(self.elem,UnivPowerSeries):
                e = self.elem.evaluate(x[0]);
            else: 
                if isinstance(self.elem,MultiVarPowerSeries):
                    e = self.elem.evaluate(x);
                else:
                    x = [ p.leadingBaseCoefficient() for p in x ];
                    #puts "x     = " + x[0].getClass().getSimpleName().to_s;
                    e = PolyUtil.evaluateAll(self.ring.coFac, self.elem, x);
        except:
            e = 0;            
        return RingElem( e );

    def integrate(self,a=0,r=None):
        '''Integrate a power series or rational function with constant a.

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
        # power series
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
        # rational function
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

    def random(self,n=3):
        '''Random element.

        n size for random element will be less than 2**n.
        '''
        if isinstance(n,RingElem):
            n = n.elem;
        return RingElem( self.elem.factory().random(n) );

    def gcd(self,b):
        '''Compute the greatest common divisor of this/self and b.

        '''
        a = self.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        else:
            b = element( b );
            b = b.elem;
        if self.isPolynomial():
           engine = Ring.getEngineGcd(self.ring); 
           return RingElem( engine.gcd(a,b) );
        else:
           return RingElem( a.gcd(b) );

    def squarefreeFactors(self):
        '''Compute squarefree factors of polynomial.

        '''
        a = self.elem;
        if self.isPolynomial():
           sqf = Ring.getEngineSqf(self.ring); 
           if sqf == None:
              raise ValueError, "squarefreeFactors not implemented for " + self.ring.to_s;
           cf = self.ring.coFac;
           if isinstance(cf,GenPolynomialRing):
               e = sqf.recursiveSquarefreeFactors( a );
           else:
               e = sqf.squarefreeFactors( a );
           L = {};
           for a in e.keySet():
               i = e.get(a);
               L[ RingElem( a ) ] = i;
           return L;
        else:
           raise ValueError, "squarefreeFactors not implemented for " + a.to_s;

    def factors(self):
        '''Compute irreducible factorization for modular, integer,
           rational number and algebriac number coefficients.

        '''
        a = self.elem;
        if self.isPolynomial():
           factor = Ring.getEngineFactor(self.ring); 
           if factor == None:
              raise ValueError, "factors not implemented for " + self.ring.to_s;
           cf = self.ring.coFac;
           if isinstance(cf,GenPolynomialRing):
               e = factor.recursiveFactors( a );
           else:
               e = factor.factors( a );
           L = {};
           for a in e.keySet():
               i = e.get(a);
               L[ RingElem( a ) ] = i;
           return L;
        else:
           raise ValueError, "factors not implemented for " + a.to_s;

    def factorsAbsolute(self):
        '''Compute absolute irreducible factorization for (modular,)
        rational number coefficients.
        '''
        a = self.elem;
        if self.isPolynomial():
           factor = Ring.getEngineFactor(self.ring); 
           if factor == None:
              raise ValueError, "factors not implemented for " + self.ring.to_s;
           try:
              L = factor.factorsAbsolute( a );
##            L = {};
##            for a in e.keySet():
##                i = e.get(a);
##                L[ RingElem( a ) ] = i;
              return L;
           except Exception, e:
              raise ValueError, "error in factorsAbsolute " + str(e);
        else:
           raise ValueError, "factors not implemented for " + a.to_s;

    def realRoots(self,eps=None):
        '''Compute real roots of univariate polynomial.
        '''
        a = self.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            if eps == None:
                #R = RealRootsSturm().realRoots( a );
                R = RootFactory.realAlgebraicNumbers( a );
            else:
                R = RootFactory.realAlgebraicNumbers( a, eps );
            R = [ RingElem(r) for r in R ];
            #R = [ RingElem(BigDecimal(r.getRational())) for r in R ];
            return R;
        except Exception, e:
            print "error " + str(e)
            return None

    def complexRoots(self,eps=None):
        '''Compute complex roots of univariate polynomial.
        '''
        a = self.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        cmplx = False;
        try:
            x = a.ring.coFac.getONE().getRe();
            cmplx = True;
        except Exception, e:
            pass;
        try:
            if eps == None:
                if cmplx:
                    R = RootFactory.complexAlgebraicNumbersComplex(a);
                else:
                    R = RootFactory.complexAlgebraicNumbers(a);
#                R = ComplexRootsSturm(a.ring.coFac).complexRoots( a );
#                R = [ r.centerApprox() for r in R ];
#                R = [ r.ring.getRoot() for r in R ];
                R = [ RingElem(r) for r in R ];
            else:
                if cmplx:
                    R = RootFactory.complexAlgebraicNumbersComplex(a,eps);
                else:
                    R = RootFactory.complexAlgebraicNumbers(a,eps);
                R = [ RingElem(r) for r in R ];
#                R = [ r.decimalMagnitude() for r in R ];
#                R = ComplexRootsSturm(a.ring.coFac).complexRoots( a, eps );
#                R = ComplexRootsSturm(a.ring.coFac).approximateRoots( a, eps );
            return R;
        except Exception, e:
            print "error " + str(e)
            return None

    def algebraicRoots(self,eps=None):
        '''Compute algebraic roots, i.e. the real and complex roots of univariate polynomial.
        '''
        a = self.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            if eps == None:
                R = RootFactory.algebraicRoots(a);
            else:
                R = RootFactory.algebraicRoots(a, eps);
            #R = [ RingElem(r) for r in R ];
            return RingElem(R);
        except Exception, e:
            print "error " + str(e)
            return None

    def rootRefine(self,eps=None):
        '''Compute algebraic roots refinement.
        '''
        a = self.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            RootFactory.rootRefine(a, eps);
            #R = [ RingElem(r) for r in R ];
            return RingElem(a);
        except Exception, e:
            print "error " + str(e)
            return None

    def decimalRoots(self,eps=None):
        '''Compute decimal approximation of real and complex roots of univariate polynomial.
        '''
        a = self.elem;
        if isinstance(eps,RingElem):
            eps = eps.elem;
        try:
            R = RootFactory.decimalRoots(a, eps);
            #R = [ RingElem(r) for r in R ];
            return RingElem(R);
        except Exception, e:
            print "error " + str(e)
            return None

    def rootsOfUnity(self):
        '''Roots of unity of real and complex algebraic numbers.
        '''
        a = self.elem;
        try:
            if isinstance(a,AlgebraicRootsPrimElem):
                R = RootFactoryApp.rootsOfUnity(a);
            else:
                R = RootFactory.rootsOfUnity(a);
            #R = [ RingElem(r) for r in R ];
            return RingElem(R);
        except Exception, e:
            print "error " + str(e)
            return None

    def rootReduce(self, other):
        '''Root reduce of real and complex algebraic numbers.
           Compute an extension field with a primitive element.
        '''
        a = self.elem;
        b = other;
        if isinstance(b,RingElem):
            b = b.elem;
        try:
            R = RootFactoryApp.rootReduce(a, b);
            #R = [ RingElem(r) for r in R ];
            return RingElem(R);
        except Exception, e:
            print "error " + str(e)
            return None

    def contFrac(self, prec):
        '''Continued fraction computation of rational and real algebraic numbers.
        '''
        a = self.elem;
        if isinstance(a,RealAlgebraicNumber):
            b = prec;
            if isinstance(b,RingElem):
                b = b.elem;
            if b < 1:
                b = 1;
            d = RealArithUtil.continuedFraction(a, b);
            return d;
        if isinstance(a,BigRational):
            d = ArithUtil.continuedFraction(a);
            return d;
        raise ValueError, "type " + str(type(a)) + " not supported";

    def contFracApprox(self, lst):
        '''Continued fraction expansion to approximate fraction.
        '''
        if lst == None:
            d = BigRational.ZERO;
            return RingElem( d );
        nb = ArithUtil.continuedFractionApprox(lst);
        return RingElem( nb );

    def solve(self, b):
        '''Solve system of linear equations.
        '''
        if isinstance(b,RingElem):
            b = b.elem;
        x = LinAlg().solve(self.elem, b);
        return RingElem( x );

    def decompLU(self):
        '''Decompose to LU matrix. this is modified.
        '''
        p = LinAlg().decompositionLU(self.elem);
        uu = self.elem.getUpper();
        ll = self.elem.getLower();
        return [ RingElem(ll), RingElem(uu), RingElem(p) ];

    def solveLU(self, p, b):
        '''Solve with LU matrix.
        '''
        if isinstance(b,RingElem):
            b = b.elem;
        if isinstance(p,RingElem):
            p = p.elem;
        x = LinAlg().solveLU(self.elem, p, b);
        return RingElem(x);

    def determinant(self, p=None):
        '''Determinant from LU matrix.
        '''
        a = self.elem;
        if p == None:
            p = LinAlg().decompositionLU(a);
        if isinstance(p,RingElem):
            p = p.elem;
        if p.isEmpty():
            d = self.ring.coFac.getZERO();
        d = LinAlg().determinantLU(a, p);
        return RingElem(d);

    def rowEchelon(self):
        '''Row echelon form matrix.
        '''
        re = LinAlg().rowEchelonForm(self.elem);
        res = LinAlg().rowEchelonFormSparse(re);
        return RingElem(res);

    def rank(self):
        '''Rank from row echelon form matrix.
        '''
        r = LinAlg().rankRE(self.elem);
        return r;

    def nullSpace(self):
        '''Null space basis. {v_i} with v_i * self = 0.
        '''
        r = LinAlg().nullSpaceBasis(self.elem);
        return r;

    def coefficients(self):
        '''Get the coefficients of a polynomial.
        '''
        a = self.elem;
        #L = [ c.toScriptFactory() for c in a.coefficientIterator() ];
        L = [ RingElem(c) for c in a.coefficientIterator() ];
        return L

    def cpp(self):
        '''(exterior) polynomial coefficient primitive part.
        '''
        a = self.elem;
        r = a.coeffPrimitivePart();
        p = RingElem(r);
        return p

    def interiorLeftProduct(self,other):
        '''Inner left product of two exterior vectors / polynomials.
        '''
        [s,o] = coercePair(self,other);
        #print "self  type(%s) = %s" % (s,type(s));
        #print "other type(%s) = %s" % (o,type(o));
        return RingElem( s.elem.interiorLeftProduct( o.elem ) );

    def interiorRightProduct(self,other):
        '''Inner right product of two exterior vectors / polynomials.
        '''
        [s,o] = coercePair(self,other);
        #print "self  type(%s) = %s" % (s,type(s));
        #print "other type(%s) = %s" % (o,type(o));
        return RingElem( s.elem.interiorRightProduct( o.elem ) );

#----------------
# Compatibility methods for Sage/Singular:
# Note: the meaning of lt and lm is swapped compared to JAS.
#----------------

    def parent(self):
        '''Parent in Sage is factory in JAS.

        Compatibility method for Sage/Singular.
        '''
        return self.factory();

    def __call__(self,num):
        '''Apply this to num.
        '''
        if num == 0:
            return self.zero();
        if num == 1:
            return self.one();
        return RingElem( self.ring.fromInteger(num) );

    def lm(self):
        '''Leading monomial of a polynomial.

        Compatibility method for Sage/Singular.
        Note: the meaning of lt and lm is swapped compared to JAS.
        '''
        ev = self.elem.leadingExpVector();
        return ev;

    def lc(self):
        '''Leading coefficient of a polynomial.

        Compatibility method for Sage/Singular.
        '''
        c = self.elem.leadingBaseCoefficient();
        return RingElem(c);

    def lt(self):
        '''Leading term of a polynomial.

        Compatibility method for Sage/Singular.
        Note: the meaning of lt and lm is swapped compared to JAS.
        '''
        ev = self.elem.leadingMonomial();
        return Monomial(ev);

    def degree(self):
        '''Degree of a polynomial.
        '''
        try:
            ev = self.elem.degree();
            #ev = self.elem.totalDegree();
        except:
            return None;
        return ev;

    def base_ring(self):
        '''Coefficient ring of a polynomial.
        '''
        try:
            ev = self.elem.ring.coFac;
        except:
            return None;
        return RingElem(ev);

    def is_field(self):
        '''Test if this RingElem is field.
        '''
        return self.elem.isField();

    def monomials(self):
        '''All monomials of a polynomial.

        Compatibility method for Sage/Singular.
        '''
        ev = self.elem.getMap().keySet();
        return ev;

    def divides(self,other):
        '''Test if self divides other.

        Compatibility method for Sage/Singular.
        '''
        [s,o] = coercePair(self,other);
        return o.elem.remainder( s.elem ).isZERO(); 

    def ideal(self,list):
        '''Create an ideal.

        Compatibility method for Sage/Singular.
        '''
        r = Ring("",ring=self.ring,fast=True);
        return r.ideal("",list=list);

    def monomial_quotient(self,a,b,coeff=False):
        '''Quotient of ExpVectors.

        Compatibility method for Sage/Singular.
        '''
        if isinstance(a,RingElem):
            a = a.elem;
        if isinstance(b,RingElem):
            b = b.elem;
        if coeff == False:
            if isinstance(a,GenPolynomial):
                return RingElem( a.divide(b) );
            else:
                return RingElem( GenPolynomial(self.ring, a.subtract(b)) );
        else:
            # assume JAS Monomial
            c = a.coefficient().divide(b.coefficient());
            e = a.exponent().subtract(b.exponent())
            return RingElem( GenPolynomial(self.ring, c, e) );

    def monomial_divides(self,a,b):
        '''Test divide of ExpVectors.

        Compatibility method for Sage/Singular.
        '''
        #print "JAS a = " + str(a) + ", b = " + str(b);
        if isinstance(a,RingElem):
            a = a.elem;
        if isinstance(a,GenPolynomial):
            a = a.leadingExpVector();
        if not isinstance(a,ExpVector):
            raise ValueError, "No ExpVector a given " + str(a) + ", " + str(b)
        if b == None:
            return False;
        if isinstance(b,RingElem):
            b = b.elem;
        if isinstance(b,GenPolynomial):
            b = b.leadingExpVector();
        if not isinstance(b,ExpVector):
            raise ValueError, "No ExpVector b given " + str(a) + ", " + str(b)
        return a.divides(b);

    def monomial_pairwise_prime(self,e,f):
        '''Test if ExpVectors are pairwise prime.

        Compatibility method for Sage/Singular.
        '''
        if isinstance(e,RingElem):
            e = e.elem;
        if isinstance(f,RingElem):
            f = f.elem;
        # assume JAS ExpVector
        c = e.gcd(f);
        return c.isZERO();

    def monomial_lcm(self,e,f):
        '''Lcm of ExpVectors.

        Compatibility method for Sage/Singular.
        '''
        if isinstance(e,RingElem):
            e = e.elem;
        if isinstance(f,RingElem):
            f = f.elem;
        # assume JAS ExpVector
        c = e.lcm(f);
        return c;

    def reduce(self,F):
        '''Compute a normal form of self with respect to F.

        Compatibility method for Sage/Singular.
        '''
        s = self.elem;
        Fe = [ e.elem for e in F ];
        if self.ring.coFac.isField():
           n = ReductionSeq().normalform(Fe,s);
        else:
           n = PseudoReductionSeq().normalform(Fe,s);
        return RingElem(n);

#----------------
# End of compatibility methods
#----------------


class Order(TermOrderByName):
    '''Collection of JAS and other CAS term order names.

    Defines names for TermOrders.
    See U{TermOrderByName<http://krum.rz.uni-mannheim.de/jas/doc/api/edu/jas/poly/TermOrderByName.html>}.
    '''
    # empty


class PolyRing(Ring):
    '''Represents a JAS polynomial ring: GenPolynomialRing.

    Provides more convenient constructor. 
    Then returns a Ring.

    Example of the construction a polynomial ring over the 
    rational numbers QQ() in the variables 'x' and 'y', together 
    with the input of a polynomial (x+y)**3.
    >>> from jas import PolyRing, QQ
    >>> r = PolyRing(QQ(),"x,y")
    globally defined variables: one, x, y

    The example works with p = (x+y)**3, but in doctests the
    full notation r.x and r.y must be used for x respectively y.
    >>> p = (r.x+r.y)**3
    >>> print p
    ( y**3 + 3 * x * y**2 + 3 * x**2 * y + x**3 )

    '''

    def __init__(self,coeff,vars,order=Order.IGRLEX):
        '''Ring constructor.

        coeff = factory for coefficients,
        vars = string with variable names,
        order = term order or weight matrix.
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
        to = Order.IGRLEX;
        if isinstance(order,TermOrder):
            to = order;
        if isinstance(order,PyList) or isinstance(order,PyTuple):
            #print "order = " + str(order);
            to = TermOrder.reverseWeight(order);
        tring = GenPolynomialRing(cf,nv,to,names);
        self.ring = tring;
        Ring.__init__(self,ring=tring)

    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();

    lex = Order.INVLEX
    '''Abreviation for INVLEX.
    '''

    grad = Order.IGRLEX
    '''Abreviation for IGRLEX.
    '''


class SolvPolyRing(SolvableRing):
    '''Represents a JAS solvable polynomial ring: GenSolvablePolynomialRing.

    Provides more convenient constructor. 
    Then returns a Ring.
    '''

    def __init__(self,coeff,vars,order=Order.IGRLEX,rel=[]):
        '''Ring constructor.

        coeff = factory for coefficients,
        vars = string with variable names,
        order = term order,
        rel = triple list of relations. (e,f,p,...) with e * f = p as relation
        and e, f and p are commutative polynomials.
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
        #to = PolyRing.lex;
        to = Order.IGRLEX;
        if isinstance(order,TermOrder):
            to = order;
        L = [];
        for x in rel:
            if isinstance(x,RingElem):
               x = x.elem;
            L.append(x);
        constSolv = False;
        for i in range(0,len(L),3):
            #print "L[i+1] = " + str(L[i+1]);
            if L[i+1].isConstant():
               constSolv = True;
        cfs = cf.toScript();
        if cfs[0] == "0":
           cfs = cf.toScriptFactory();
        recSolv = isinstance(cf,GenPolynomialRing);
        recSolvWord = isinstance(cf,GenWordPolynomialRing);
        resWord = isinstance(cf,WordResidueRing);
        quotSolv = isinstance(cf,SolvableQuotientRing);
        resSolv = isinstance(cf,SolvableResidueRing);
        locSolv = isinstance(cf,SolvableLocalRing);
        locresSolv = isinstance(cf,SolvableLocalResidueRing);
        if recSolv and not constSolv:
           recSolv = False;
        #print "cf = " + str(cf.getClass().getSimpleName()) + ", quotSolv = " + str(quotSolv) + ", recSolv = " + str(recSolv);
        if recSolv:
            ring = RecSolvablePolynomialRing(cf,nv,to,names);
            table = ring.table;
            coeffTable = ring.coeffTable;
        else:
            if resSolv:
                #ring = ResidueSolvablePolynomialRing(cf,nv,to,names);
                ring = QLRSolvablePolynomialRing(cf,nv,to,names);
                table = ring.table;
                coeffTable = ring.polCoeff.coeffTable;
            else:
                if recSolvWord:
                   print "RecSolvableWordPolynomialRing: " + cfs;
                   ring = RecSolvableWordPolynomialRing(cf,nv,to,names);
                   table = ring.table;
                   coeffTable = ring.coeffTable;
                else:
                   if resWord:
                      print "ResWordSolvablePolynomialRing: " + cfs;
                      #ring = GenSolvablePolynomialRing(cf,nv,to,names); # ??
                      #ring = RecSolvableWordPolynomialRing(cf,nv,to,names);
                      ring = ResidueSolvableWordPolynomialRing(cf,nv,to,names);
                      #print "ring = " + str(ring.toScript());
                      table = ring.table;
                      coeffTable = ring.polCoeff.coeffTable;
                      #coeffTable = ring.coeffTable;
                   else:
                      if quotSolv:
                         #ring = QuotSolvablePolynomialRing(cf,nv,to,names);
                         ring = QLRSolvablePolynomialRing(cf,nv,to,names);
                         table = ring.table;
                         coeffTable = ring.polCoeff.coeffTable;
                      else:
                         if locSolv:
                            #ring = LocalSolvablePolynomialRing(cf,nv,to,names);
                            ring = QLRSolvablePolynomialRing(cf,nv,to,names);
                            table = ring.table;
                            coeffTable = ring.polCoeff.coeffTable;
                         else:
                            if locresSolv:
                               #ring = LocalResidueSolvablePolynomialRing(cf,nv,to,names);
                               ring = QLRSolvablePolynomialRing(cf,nv,to,names);
                               table = ring.table;
                               coeffTable = ring.polCoeff.coeffTable;
                            else:
                               ring = GenSolvablePolynomialRing(cf,nv,to,names);
                               table = ring.table;
                               coeffTable = table;
        #print "rel = " + str(L);
        for i in range(0,len(L),3):
            print "adding relation: " + str(L[i]) + " * " + str(L[i+1]) + " = " + str(L[i+2]);
            if L[i+1].isConstant():
               if recSolv or recSolvWord:
                  coeffTable.update( L[i], L[i+1], L[i+2] );
               else: 
                  if resSolv or resWord or quotSolv or locSolv or locresSolv:
                     coeffTable.update(ring.toPolyCoefficients(L[i]),
                                       ring.toPolyCoefficients(L[i+1]),
                                       ring.toPolyCoefficients(L[i+2]) );
            else:
                #print "L[i], L[i+1], L[i+2]: " + str(L[i]) + ", " + str(L[i+1]) + ", " + str(L[i+2]);
                table.update( L[i], L[i+1], L[i+2] );
                if locresSolv or locSolv or quotSolv or resSolv or resWord:
                   #print "ring.polCoeff.table " + str(ring.polCoeff.table.toScript());
                   ring.polCoeff.table.update( ring.toPolyCoefficients(L[i]),
                                               ring.toPolyCoefficients(L[i+1]),
                                               ring.toPolyCoefficients(L[i+2]) );

        self.ring = ring;
        SolvableRing.__init__(self,ring=self.ring)

    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();


class EF:
    '''Extension field builder.

    Construction of extension field towers according to the builder pattern.
    '''

    def __init__(self,base):
        '''Constructor to set base field.
        '''
        if isinstance(base,RingElem):
            #factory = base.elem;
            factory = base.ring;
        else:
            factory = base;
        try:
            factory = self.factory.factory();
        except:
            pass
        print "extension field factory: " + factory.toScript(); # + " :: " + factory.toString();
        #print "d type(%s) = %s" % (factory,type(factory));
        if isinstance(factory,ExtensionFieldBuilder):
            self.builder = factory;
        else:
            self.builder = ExtensionFieldBuilder(factory);

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.builder.toScript()); 

    def extend(self,vars,algebraic=None):
        '''Create an extension field.

        If algebraic is given as string expression, then an algebraic
        extension field is constructed, else a transcendental
        extension field is constructed.
        '''
        if algebraic == None:
            ef = self.builder.transcendentExtension(vars);
        else:
            if isinstance(algebraic,PyInteger) or isinstance(algebraic,PyLong):
               ef = self.builder.finiteFieldExtension(algebraic);
            else:
               ef = self.builder.algebraicExtension(vars,algebraic);
        return EF(ef.build());

    def realExtend(self,vars,algebraic,interval):
        '''Create a real extension field.

        Construct a real algebraic extension field with an isolating interval for a real root.
        '''
        ef = self.builder.realAlgebraicExtension(vars,algebraic,interval);
        return EF(ef.build());

    def complexExtend(self,vars,algebraic,rectangle):
        '''Create a complex extension field.

        Construct a complex algebraic extension field with an isolating rectangle for a complex root.
        '''
        ef = self.builder.complexAlgebraicExtension(vars,algebraic,rectangle);
        return EF(ef.build());

    def polynomial(self,vars):
        '''Create a polynomial ring extension.
        '''
        ef = self.builder.polynomialExtension(vars);
        return EF(ef.build());

    def matrix(self,n):
        '''Create a matrix ring extension.
        '''
        ef = self.builder.matrixExtension(n);
        return EF(ef.build());

    def build(self):
        '''Get extension field tower.

        '''
        rf = self.builder.build();
        if isinstance(rf,GenPolynomialRing):
            return PolyRing(rf.coFac,rf.getVars(),rf.tord);
        else:
            return Ring("", rf);


#------------------------------------

class ExtRing(Ring):
    '''Represents a JAS exterior form / vector / polynomial ring: GenExteriorPolynomialRing.

    '''

    def __init__(self,ringstr="",ring=None):
        '''Exterior vector / polynomial ring constructor.
        '''
        print "ExtRing: ";
        if ring == None:
           #raise ValueError, "parse of exterior polynomials not implemented"
           sr = StringReader( ringstr );
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextPolynomialRing();
           wfac = GenExteriorPolynomialRing(pfac);
           list = tok.nextExteriorPolynomialList(wfac);
           self.ring = wfac;
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;
        Ring.__init__(self,ring=self.ring)

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

#    def ideal(self,ringstr="",list=None):
#        '''Create a word ideal.
#        '''
#        return ExtPolyIdeal(self,ringstr,list);

    def one(self):
        '''Get the one of the exterior vector / polynomial ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the exterior vector / polynomial ring.
        '''
        return RingElem( self.ring.getZERO() );

    def random(self,n):
        '''Get a random exterior vector / polynomial.
        '''
        return RingElem( self.ring.random(n) );

    def random(self,k,l,d):
        '''Get a random exterior vector / polynomial.
        '''
        return RingElem( self.ring.random(k,l,d) );

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        sr = StringReader(poly);
        top = GenPolynomialTokenizer(sr);
        ep = tok.nextExteriorPolynomial(self.ring);
        if ep != None:
            return RingElem( ep );


class ExtPolyRing(ExtRing):
    '''Represents a JAS exterior form / vector / polynomial ring: GenExteriorPolynomialRing.

    Provides more convenient constructor.
    Then returns a Ring.
    '''

    def __init__(self,coeff,s,var="E"):
        '''Ring constructor.

        coeff = factory for coefficients,
        s = size of index list,
        var = string with one variable name.
        '''
        print "ExtPolyRing: ";
        if coeff == None:
            raise ValueError, "No coefficient given."
        cf = coeff;
        if isinstance(coeff,RingElem):
            cf = coeff.elem.factory();
        if isinstance(coeff,Ring):
            cf = coeff.ring;
        if s == None:
            raise ValueError, "No variable size given."
        names = var;
        if not isinstance(var,PyString):
            names = GenPolynomialTokenizer.variableList(var);
        wf = IndexFactory(s, names);
        ring = GenExteriorPolynomialRing(cf,wf);
        self.ring = ring;
        Ring.__init__(self,ring=self.ring)

    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();


#------------------------------------


class WordRing(Ring):
    '''Represents a JAS free non-commutative polynomial ring: GenWordPolynomialRing.

    Has a method to create word ideals.
    <b>Note:</b> watch your step: check that jython does not reorder multiplication.
    '''

    def __init__(self,ringstr="",ring=None):
        '''Word polynomial ring constructor.
        '''
        if ring == None:
           #raise ValueError, "parse of word polynomials not implemented"
           sr = StringReader( ringstr );
           tok = RingFactoryTokenizer(sr);
           pfac = tok.nextPolynomialRing();
           wfac = GenWordPolynomialRing(pfac);
           #list = tok.nextWordPolynomialList(wfac);
           self.ring = wfac;
        else:
           if isinstance(ring,Ring):
              self.ring = ring.ring;
           else:
              self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return str(self.ring.toScript());

    def ideal(self,ringstr="",list=None):
        '''Create a word ideal.
        '''
        return WordPolyIdeal(self,ringstr,list);

    def one(self):
        '''Get the one of the word polynomial ring.
        '''
        return RingElem( self.ring.getONE() );

    def zero(self):
        '''Get the zero of the word polynomial ring.
        '''
        return RingElem( self.ring.getZERO() );

    def random(self,n):
        '''Get a random word polynomial.
        '''
        return RingElem( self.ring.random(n) );

    def random(self,k,l,d):
        '''Get a random word polynomial.
        '''
        return RingElem( self.ring.random(k,l,d) );

    def element(self,poly):
        '''Create an element from a string or object.
        '''
        if not isinstance(poly,str):
            try:
                if self.ring == poly.ring:
                    return RingElem(poly);
            except Exception, e:
                pass
            poly = str(poly);
        I = WordPolyIdeal(self, "( " + poly + " )"); # should work
        list = I.list;
        if len(list) > 0:
            return RingElem( list[0] );


class WordPolyRing(WordRing):
    '''Represents a JAS free non-commutative polynomial ring: GenWordPolynomialRing.

    Provides more convenient constructor. 
    Then returns a Ring.
    <b>Note:</b> watch your step: check that jython does not reorder multiplication.
    '''

    def __init__(self,coeff,vars):
        '''Ring constructor.

        coeff = factory for coefficients,
        vars = string with variable names.
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
        wf = WordFactory(names);
        ring = GenWordPolynomialRing(cf,wf);
        self.ring = ring;

    def __str__(self):
        '''Create a string representation.
        '''
        return self.ring.toScript();


class WordPolyIdeal:
    '''Represents a JAS word polynomial ideal.

    Methods for two-sided Groebner basees and others.
    <b>Note:</b> watch your step: check that jython does not reorder multiplication.
    '''

    def __init__(self,ring,ringstr="",list=None):
        '''Constructor for an ideal in a word polynomial ring.
        '''
        self.ring = ring;
        if list == None:
           #raise ValueError, "parse of word polynomials not implemented"
           sr = StringReader( ringstr );
           tok = GenPolynomialTokenizer(sr);
           self.list = tok.nextWordPolynomialList(ring.ring);
        else:
           if isinstance(list,WordPolyIdeal):
              self.list = list.list;
              self.ideal = list;
           else:
              self.list = pylist2arraylist(list,rec=1);
              self.ideal = jas.application.WordIdeal(ring.ring, self.list);

    def __str__(self):
        '''Create a string representation.
        '''
        #ll = [ e.toScript() for e in self.list ]
        #return "( " + ", ".join(ll) + " )"; 
        return self.ideal.toScript();

    def GB(self):
        '''Compute a two-sided Groebner base.
        '''
        return self.twosidedGB();

    def twosidedGB(self):
        '''Compute a two-sided Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.ideal.list;
        kind = "";
        t = System.currentTimeMillis();
        if cofac.isField() or not cofac.isCommutative():
            G = self.ideal.GB();
            kind = "field|nocom"
        else:
            if isinstance(cofac,GenPolynomialRing): # and cofac.isCommutative():
                G = WordGroebnerBasePseudoRecSeq(cofac).GB(F);
                self.ideal = WordIdeal(self.ring.ring, G);
                kind = "pseudorec"
            else:
                G = WordGroebnerBasePseudoSeq(cofac).GB(F);
                self.ideal = WordIdeal(self.ring.ring, G);
                kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) twosidedGB executed in %s ms" % (kind, t); 
        return self;

    def isGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        return self.isTwosidedGB();

    def isTwosidedGB(self):
        '''Test if this is a two-sided Groebner base.
        '''
        cofac = self.ring.ring.coFac;
        F = self.ideal.list;
        kind = "";
        t = System.currentTimeMillis();
        if cofac.isField() or not cofac.isCommutative():
            b = self.ideal.isGB();
            kind = "field|nocom"
        else:
            if isinstance(cofac,GenPolynomialRing):
                b = WordGroebnerBasePseudoRecSeq(cofac).isGB(F);
                kind = "pseudorec"
            else:
                b = WordGroebnerBasePseudoSeq(cofac).isGB(F);
                kind = "pseudo"
        t = System.currentTimeMillis() - t;
        print "sequential(%s) isTwosidedGB executed in %s ms" % (kind, t); 
        return b;

    def __cmp__(self, other):
        '''Compare two ideals.
        '''
        if not isinstance(other, WordPolyIdeal):
            return False;
        s = self.ideal;
        t = other.ideal;
        return s.compareTo(t);

    def __eq__(self,other):
        '''Test if two ideals are equal.
        '''
        if not isinstance(other, WordPolyIdeal):
            return False;
        s = self.ideal;
        t = other.ideal;
        return s.equals(t)

    def sum(self,other):
        '''Compute the sum of this and the other ideal.
        '''
        s = self.ideal;
        t = other.ideal;
        N = s.sum(t);
        return WordIdeal(self.ring, "", N);


def WRC(ideal,r=0):
    '''Create JAS polynomial WordResidue as ring element.
    '''
    #print "ideal = " + str(ideal);
    if ideal == None: # does not work
        print "ideal = " + str(ideal);
        if False:
            raise ValueError, "No ideal given."
    if isinstance(ideal,WordPolyIdeal):
        ideal = ideal.ideal
        #ideal.doGB();
    if not isinstance(ideal,WordIdeal):
       raise ValueError, "No ideal given."
    #print "ideal.getList().get(0).ring.ideal = %s" % ideal.getList().get(0).ring.ideal;
    if isinstance(ideal.getList().get(0).ring,WordResidueRing):
        rc = WordResidueRing( ideal.getList().get(0).ring.ideal );
    else:
        rc = WordResidueRing(ideal);
    if isinstance(r,RingElem):
        r = r.elem;
    if r == 0:
        r = WordResidue(rc);
    else:
        r = WordResidue(rc,r);
    return RingElem(r);


# doctest:
if __name__ == '__main__':
   import doctest, sys
   doctest.testmod(sys.modules[__name__])
