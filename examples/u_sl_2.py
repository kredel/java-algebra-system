#
# jython examples for jas.

from jas import SolvableRing
from jas import SolvableIdeal


# U(sl_2) example

rs = """
# solvable polynomials, U(sl_2):
Rat(e,f,h) G
RelationTable
(
 ( f ), ( e ), ( e f - h ),
 ( h ), ( e ), ( e h + 2 e ),
 ( h ), ( f ), ( f h - 2 f ) 
)
""";

r = SolvableRing( rs );
print "SolvableRing: " + str(r);
print;


ps = """
(
 ( e^2 + f^3 )
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

