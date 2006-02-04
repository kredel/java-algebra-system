#
# jython examples for jas.

from jas import SolvableRing
from jas import SolvableIdeal


# U(so_3) example

rs = """
# solvable polynomials, U(so_3):
Rat(x,y,z) G
RelationTable
(
 ( y ), ( x ), ( x y - z ),
 ( z ), ( x ), ( x z + y ),
 ( z ), ( y ), ( y z - x ) 
)
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;


ps = """
(
 ( x^2 + y^3 )
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

