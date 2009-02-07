#
# jython examples for jas.
# $Id$
#

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
print "seq left GB:", rg;
print;


if rg.isLeftGB():
   print "is left GB";
else:
   print "is not left GB";



rg = f.twosidedGB();
print "seq twosided GB:", rg;
print;

if rg.isLeftGB():
   print "twosided GB is left GB";
else:
   print "twosided GB is not left GB";

if rg.isRightGB():
   print "twosided GB is right GB";
else:
   print "twosided GB is not right GB";

if rg.isTwosidedGB():
   print "is twosided GB";
else:
   print "is not twosided GB";



rg = f.rightGB();
print "seq right GB:", rg;
print;

if rg.isRightGB():
   print "is right GB";
else:
   print "is not right GB";

