#
# jython examples for jas.
# $Id$
#

import sys;

from jas import SolvableRing, SolvPolyRing, PolyRing, SolvableIdeal
from jas import QQ, ZZ, GF, RF, startLog, terminate 

startLog();

# solvable module example

#mod=17; 
mod=32003; 
#mod=536870909; 
#mod=1152921504606846883;

coeff = GF(mod)
#coeff = QQ()

p = PolyRing( 
        RF(PolyRing(coeff,"b1,c1",PolyRing.lex)), 
        "E,D1,D2,D3",PolyRing.grad);
print "PolyRing: " + str(p);
print;

relations = [
 ( D3 ), ( D1 ), ( D1 * D3 -      c1 * E + b1      * E ),
 ( D3 ), ( D2 ), ( D2 * D3 -           E +           E ),
 ( D2 ), ( D1 ), ( D1 * D2 -      c1 * E + b1      * E )
];

print "relations: = " + str([ str(f) for f in relations ]);
print;

rp = SolvPolyRing( 
         RF(PolyRing(coeff,"b1,c1",PolyRing.lex)),
         "E,D1,D2,D3",PolyRing.grad,relations);
print "SolvPolyRing: rp = " + str(rp);
print;
print "gens = " + str([ str(f) for f in rp.gens() ]);
print;

F = [ 
 ( D1 - D2 * D3 ),
 ( D2 * D3 + D1 ),
 ( - D1 * D3 + D2 )
];
print "F =" + str([ str(f) for f in F ]);
print

f = rp.ideal( list=F );
print "SolvableIdeal: " + str(f);
print;

#startLog();

rg = f.leftGB();
print "leftGB:" + str(rg);
print;
brg = rg.isLeftGB();
print "is LeftGB:" + str(brg);
print;


rg = f.rightGB();
print "rightGB:" + str(rg);
print;
brg = rg.isRightGB();
print "is rightGB:" + str(brg);
print;

trg = f.twosidedGB();
print "twosidedGB:", trg;
print;
brg = trg.isTwosidedGB();
print "is twosidedGB:" + str(brg);
print;

#terminate();
#sys.exit();

from edu.jas.poly   import ModuleList;
from edu.jas.gbufd  import SolvableSyzygySeq;
from edu.jas.gb     import SolvableGroebnerBaseSeq;
from edu.jas.gb     import SolvableGroebnerBaseParallel;
from edu.jas.poly   import TermOrderOptimization;
from edu.jas.poly   import GenSolvablePolynomialRing;
from edu.jas.ufd    import PolyUfdUtil;
from edu.jas.ufd    import QuotientRing;
from java.lang      import System;


t = System.currentTimeMillis();
s = SolvableSyzygySeq(f.ring.ring.coFac).rightZeroRelationsArbitrary( f.list );
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
sr = ModuleList(f.ring.ring, s);

print "rightSyzygy for f:\n" + str(sr.toScript());
print "#rightSyzygy for f = " + str(s.size());
print "#rightSyzygy[0] for f = " + str(s[0].size());
print;

#for p in sr.list:
#   print "p = " + str( [ str(pc.toScript()) for pc in p] );
#print

# optional:
ir = GenSolvablePolynomialRing(sr.ring.coFac.ring,sr.ring);
print "ir = " + str(ir.toScript());
qrel = sr.ring.table.relationList();
irel = PolyUfdUtil.integralFromQuotientCoefficients(ir,qrel);
ir.addRelations(irel);
print "qr = " + str(sr.ring.toScript());
print "ir = " + str(ir.toScript());
soi = [ PolyUfdUtil.integralFromQuotientCoefficients(ir,sop) for sop in s];
isr = ModuleList(ir, soi);
print "integral f:\n" + str(isr.toScript());
print;

#terminate();
#sys.exit();

t = System.currentTimeMillis();
#imr = ModSolvableGroebnerBaseSeq(isr.ring.coFac).rightGB(isr);
#imr = ModSolvableGroebnerBaseSeq(isr.ring.coFac).leftGB(isr);
#imr = ModSolvableGroebnerBaseSeq(isr.ring.coFac).twosidedGB(isr);
#mr = ModSolvableGroebnerBaseSeq(sr.ring.coFac).rightGB(sr);
mr = SolvableGroebnerBaseSeq().rightGB(sr);
#mr = ModSolvableGroebnerBaseSeq(sr.ring.coFac).leftGB(sr);
#mr = ModSolvableGroebnerBaseSeq(sr.ring.coFac).twosidedGB(sr);
#mr = ModSolvableGroebnerBasePar(sr.ring.coFac).rightGB(sr);
#mr = ModSolvableGroebnerBasePar(sr.ring.coFac).leftGB(sr);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
print;

print "rightModuleGB for syzygies:" + str(mr.toScript());
print;

#for p in mr.list:
#   print "p = " + str( [str(pc.toScript()) for pc in p] ) + "\n";
#print

t = System.currentTimeMillis();
#mz = ModSolvableGroebnerBaseSeq(sr.ring.coFac).isRightGB(mr);
mz = SolvableGroebnerBaseSeq().isRightGB(mr);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 

if mz:
  print "is right module GB";
else:
  print "is not right module GB";
print;

terminate();
#sys.exit(); # for parallel 
