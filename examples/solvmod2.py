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
#    0,0,0,0,0,0,0,0,0,0,0,0,
#    1,1,1,1,1,1,1)

Rat(b1,b2,b3,b4,b5,b6,c1,c2,c3,c4,c5,c6,
    bi1,bi2,bi3,bi4,bi5,bi6,ci1,ci2,ci3,ci4,ci5,ci6,
    E,D1,D2,D3,D4,D5,D6)

  W(0,0,0,0,0,0,0,0,0,0,0,0,
    0,0,0,0,0,0,0,0,0,0,0,0,
    1,1,1,1,1,1,1)

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
 ( -D4 D5 - b4 c5 E + b5 c4 E + 1 ),
 ( b1 bi1 - 1 ),
 ( b2 bi2 - 1 ),
 ( b3 bi3 - 1 ),
 ( b4 bi4 - 1 ),
 ( b5 bi5 - 1 ),
 ( b6 bi6 - 1 ),
 ( c1 ci1 - 1 ),
 ( c2 ci2 - 1 ),
 ( c3 ci3 - 1 ),
 ( c4 ci4 - 1 ),
 ( c5 ci5 - 1 ),
 ( c6 ci6 - 1 )
) 
""";

f = SolvableIdeal( r, ps );
print "SubIdeal: " + str(f);
print;

rg = f.leftGB();
print "leftGB:", rg;
print;

from edu.jas.module import SolvableSyzygy;
from edu.jas.module import ModuleList;
from edu.jas.module import ModSolvableGroebnerBase;

s = SolvableSyzygy.leftZeroRelations( rg.list );
sl = ModuleList(f.pset.vars,f.pset.tord,s,f.pset.table);

print "leftSyzygy:", sl;
print;

#ms = ModSolvableGroebnerBase.leftGB(sl);
#print "leftGB:", ms;
#print;
