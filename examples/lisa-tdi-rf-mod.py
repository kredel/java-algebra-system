#
# jython examples for jas.
# $Id$
#

import sys;

from jas import SolvableRing
from jas import SolvableIdeal
from jas import startLog
from jas import terminate

startLog();

# LISA TDI computation
# solvable module example
# 2^60-93 = 1152921504606846883

xs = """
""";

#solvable example
# Di E = E Di
# Dj * Di = Di Dj + bi cj E - bj ci E
# ( Dj ), ( Di ), ( Di Dj + bi cj E - bj ci E )
#  W(0,0,0,0,0,0,0,0,0,0,0,0,
#    1,1,1,1,1,1,1)
#Mod 1152921504606846883
#Rat
#    (b1,b2,b3,b4,b5,b6,c1,c2,c3,c4,c5,c6,E,D1,D2,D3,D4,D5,D6)
#    G |7|

# orig: #
#RatFunc(b1,b2,b3,b4,b5,b6,c1,c2,c3,c4,c5,c6)
#       (E,D1,D2,D3,D4,D5,D6)

# to opt: #
#RatFunc(b2,c2,b4,c4,b1,b3,b5,b6,c1,c3,c5,c6)
#       (D1,E,D4,D2,D3,D5,D6)

# to opt rev: #
#RatFunc(c6,c5,c3,c1,b6,b5,b3,b1,c4,b4,c2,b2)
#       (D6,D5,D3,D2,D4,E,D1)

rs = """
# to opt, base to opt: #
ModFunc 3 (b2,c2,b4,c4,b1,b3,b5,b6,c1,c3,c5,c6)
           (E,D4,D1,D6,D5,D3,D2)
#          (D2,D3,D5,D6,D1,D4,E)
           G 

RelationTable
(
 ( D6 ), ( D1 ), ( D1 D6 - { b6 c1 } E + { b1 c6 } E ),
 ( D6 ), ( D2 ), ( D2 D6 - { b6 c2 } E + { b2 c6 } E ),
 ( D6 ), ( D3 ), ( D3 D6 - { b6 c3 } E + { b3 c6 } E ),
 ( D6 ), ( D4 ), ( D4 D6 - { b6 c4 } E + { b4 c6 } E ),
 ( D6 ), ( D5 ), ( D5 D6 - { b6 c5 } E + { b5 c6 } E ),

 ( D5 ), ( D1 ), ( D1 D5 - { b5 c1 } E + { b1 c5 } E ),
 ( D5 ), ( D2 ), ( D2 D5 - { b5 c2 } E + { b2 c5 } E ),
 ( D5 ), ( D3 ), ( D3 D5 - { b5 c3 } E + { b3 c5 } E ),
 ( D5 ), ( D4 ), ( D4 D5 - { b5 c4 } E + { b4 c5 } E ),
 
 ( D4 ), ( D1 ), ( D1 D4 - { b4 c1 } E + { b1 c4 } E ),
 ( D4 ), ( D2 ), ( D2 D4 - { b4 c2 } E + { b2 c4 } E ),
 ( D4 ), ( D3 ), ( D3 D4 - { b4 c3 } E + { b3 c4 } E ),

 ( D3 ), ( D1 ), ( D1 D3 - { b3 c1 } E + { b1 c3 } E ),
 ( D3 ), ( D2 ), ( D2 D3 - { b3 c2 } E + { b2 c3 } E ),
 
 ( D2 ), ( D1 ), ( D1 D2 - { b2 c1 } E + { b1 c2 } E )
)    
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;

ps = """
(
 ( D1 - D2 D4 -  { b2 c4 } E + { b4 c2 } E ),
 ( - D1 D3 + D4 ),
 ( - D1 D6 + 1 ),
 ( - D4 D5 + 1 )
) 
""";

f = SolvableIdeal( r, ps );
print "SolvableIdeal: " + str(f);
print;

#startLog();

rg = f.rightGB();
print "rightGB:", rg;
print;

brg = rg.isRightGB();
print "is rightGB:", brg;
print;

terminate();
sys.exit();

from edu.jas.vector import ModuleList;
from edu.jas.module import SolvableSyzygyAbstract;
from edu.jas.module import ModSolvableGroebnerBase;
from edu.jas.poly   import PolynomialList;
from java.lang      import System;


t = System.currentTimeMillis();
s = SolvableSyzygyAbstract().rightZeroRelationsArbitrary( f.list );
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
sr = ModuleList(f.ring.ring,s);

print "rightSyzygy for f:", sr;
print "#rightSyzygy for f = ", s.size();
print "#rightSyzygy[0] for f = ", s[0].size();
print;


t = System.currentTimeMillis();
mz = SolvableSyzygyAbstract().isRightZeroRelation(sr.list,f.list);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 

if mz:
  print "all right syzygies";
else:
  print "not all right syzygies";

terminate();
sys.exit();

## t = System.currentTimeMillis();
## s = SolvableSyzygyAbstract().leftZeroRelationsArbitrary( f.list );
## t = System.currentTimeMillis() - t;
## print "executed in %s ms" % t; 
## sl = ModuleList(f.ring.ring,s);

## print "leftSyzygy for f:", sl;
## print "#leftSyzygy for f = ", s.size();
## print "#leftSyzygy[0] for f = ", s[0].size();
## print;

## t = System.currentTimeMillis();
## mz = SolvableSyzygyAbstract().isLeftZeroRelation(sl.list,f.list);
## t = System.currentTimeMillis() - t;
## print "executed in %s ms" % t; 

## if mz:
##   print "all left syzygies";
## else:
##   print "not all left syzygies";



t = System.currentTimeMillis();
mr = ModSolvableGroebnerBase().rightGB(sr);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 

print "rightModuleGB for syzygies:", mr;

t = System.currentTimeMillis();
mz = ModSolvableGroebnerBase().isRightGB(mr);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 

if mz:
  print "is right module GB";
else:
  print "is not right module GB";


## t = System.currentTimeMillis();
## ml = ModSolvableGroebnerBase().leftGB(sr);
## t = System.currentTimeMillis() - t;
## print "executed in %s ms" % t; 

## print "leftModuleGB for syzygies:", ml;

## t = System.currentTimeMillis();
## mz = ModSolvableGroebnerBase().isLeftGB(ml);
## t = System.currentTimeMillis() - t;
## print "executed in %s ms" % t; 

## if mz:
##   print "is left module GB";
## else:
##   print "is not left module GB";


terminate();
