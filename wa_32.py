#
# jython examples for jas.

from jas import SolvableRing
from jas import SolvableIdeal


# WA_32 example

rs = """
# solvable polynomials, Weyl algebra A_3,2:
Rat(a,b,e1,e2,e3) L
RelationTable
(
 ( e3 ), ( e1 ), ( e1 e3 - e1 ),
 ( e3 ), ( e2 ), ( e2 e3 - e2 )
)
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;


ps = """
(
 ( e1 e3^3 + e2^10 - a ),
 ( e1^3 e2^2 + e3 ),
 ( e3^3 + e3^2 - b )
)
""";

f = SolvableIdeal( r, ps );
print "SolvableIdeal: " + str(f);
print;


rg = f.leftGB();
print "seq left Output:", rg;
print;


rg = f.twosidedGB();
print "seq twosided Output:", rg;
print;

