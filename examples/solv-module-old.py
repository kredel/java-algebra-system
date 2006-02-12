#
# jython examples for jas.

from jas import SolvableRing
from jas import SolvableIdeal

# solvable module example

rs = """
#solvable example
# Di E = E Di
# Dj * Di = Di Dj + bi cj E - bj ci E
# ( Dj ), ( Di ), ( Di Dj + bi cj E - bj ci E )
#  W(0,0,0,0,0,0,0,0,0,0,0,0,
#    1,1,1,1,1,1,1)

Rat(b1,b2,b3,b4,b5,b6,c1,c2,c3,c4,c5,c6,
    E,D1,D2,D3,D4,D5,D6)
    G

RelationTable
(
 ( D1 ), ( D6 ), ( D6 D1 - b6 c1 E + b1 c6 E ),
 ( D2 ), ( D6 ), ( D6 D2 - b6 c2 E + b2 c6 E ),
 ( D3 ), ( D6 ), ( D6 D3 - b6 c3 E + b3 c6 E ),
 ( D4 ), ( D6 ), ( D6 D4 - b6 c4 E + b4 c6 E ),
 ( D5 ), ( D6 ), ( D6 D5 - b6 c5 E + b5 c6 E ),

 ( D1 ), ( D5 ), ( D5 D1 - b5 c1 E + b1 c5 E ),
 ( D2 ), ( D5 ), ( D5 D2 - b5 c2 E + b2 c5 E ),
 ( D3 ), ( D5 ), ( D5 D3 - b5 c3 E + b3 c5 E ),
 ( D4 ), ( D5 ), ( D5 D4 - b5 c4 E + b4 c5 E ),
 
 ( D1 ), ( D4 ), ( D4 D1 - b4 c1 E + b1 c4 E ),
 ( D2 ), ( D4 ), ( D4 D2 - b4 c2 E + b2 c4 E ),
 ( D3 ), ( D4 ), ( D4 D3 - b4 c3 E + b3 c4 E ),

 ( D1 ), ( D3 ), ( D3 D1 - b3 c1 E + b1 c3 E ),
 ( D2 ), ( D3 ), ( D3 D2 - b3 c2 E + b2 c3 E ),
 
 ( D1 ), ( D2 ), ( D2 D1 - b2 c1 E + b1 c2 E )
)    
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;


ps = """
(
 ( D2 D4 + D1 ),
 ( D1 D3 + D4 - b1 c3 E + b3 c1 E ),
 ( D1 D6 + b1 c6 E - b6 c1 E ),
 ( -D4 D5 - b4 c5 E + b5 c4 E + 1 )
) 
""";

f = SolvableIdeal( r, ps );
print "Ideal: " + str(f);
print;

rg = f.leftGB();
print "leftGB:", rg;
print "#leftGB = ", rg.list.size();
print;


from edu.jas.module import SolvableSyzygy;
from edu.jas.module import ModuleList;
from edu.jas.module import ModSolvableGroebnerBase;
from edu.jas.poly   import PolynomialList;
from java.lang      import System;

t = System.currentTimeMillis();
s = SolvableSyzygy.leftZeroRelations( rg.list );
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
sl = ModuleList(f.pset.vars,f.pset.tord,s,f.pset.table);

print "leftSyzygy:", sl;
print "#leftSyzygy = ", s.size();
print "#leftSyzygy[0] = ", s[0].size();
print;

#ms = ModSolvableGroebnerBase.leftGB(sl);
#print "leftGB:", ms;
#print;

t = System.currentTimeMillis();
mr = ModSolvableGroebnerBase.leftReductionBase(sl);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
print "leftReductionBase:", mr;
print;

t = System.currentTimeMillis();
mo = ModSolvableGroebnerBase.intersection(4,mr);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
print "leftReductionBase intersect to 4 components:", mo;
print;

t = System.currentTimeMillis();
mg = ModSolvableGroebnerBase.leftGB(mo);
t = System.currentTimeMillis() - t;
print "executed in %s ms" % t; 
print "leftGB:", mg;
print;

## print "s[10+1] = ", s[10];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[10],f.pset.table);
## print "s[10+1] = ", str(pl);

## f2 = rg.list[1];
## f4 = rg.list[3];
## f5 = rg.list[4];

## s11 = s[10];
## s11_2 = s11[1];
## s11_4 = s11[3];
## s11_5 = s11[4];

## s11_2mf2 = s11_2.multiply(f2);
## print "s11_2 * f2 = ", s11_2mf2;

## s11_4mf4 = s11_4.multiply(f4);
## print "s11_4 * f4 = ", s11_4mf4;

## s11_5mf5 = s11_5.multiply(f5);
## print "s11_5 * f5 = ", s11_5mf5;

## syz11 = s11_2mf2.add( s11_4mf4.add( s11_5mf5 ) );
## print "syz11 = ", syz11;


## print "s[9+1] = ", s[9];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[9],f.pset.table);
## print "s[9+1] = ", str(pl);

## print "s[23+1] = ", s[23];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[23],f.pset.table);
## print "s[23+1] = ", str(pl);

## print "s[17+1] = ", s[17];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[17],f.pset.table);
## print "s[17+1] = ", str(pl);

## print "s[4+1] = ", s[4];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[4],f.pset.table);
## print "s[4+1] = ", str(pl);

## print "s[43+1] = ", s[43];
## pl = PolynomialList(f.pset.vars,f.pset.tord,s[43],f.pset.table);
## print "s[43+1] = ", str(pl);


## print "s[1] = ", s[1];
## s0 = s[0];
## print "s0[0] = ", s0[2];

