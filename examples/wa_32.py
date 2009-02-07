#
# jython examples for jas.
# $Id$
#

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
print "seq left GB:", rg;
print;

#from edu.jas.gb   import SolvableGroebnerBaseSeq;

if rg.isLeftGB():
   print "is left GB";
else:
   print "is not left GB";


rg = f.parLeftGB(2); # 2 threads
print "par left GB:", rg;
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


rg = f.parTwosidedGB(2);
print "par twosided GB:", rg;
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
