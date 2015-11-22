#
# jython examples for jas.
# $Id$
#

from jas import SolvableRing

from edu.jas.poly   import ModuleList;
from edu.jas.gbufd  import SolvableSyzygySeq;

# WA_32 example

rs = """
# solvable polynomials, Weyl algebra A_3,2:
Rat(a,b,e1,e2,e3) G
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

f = r.ideal( ps );
print "SolvableIdeal: " + str(f);
print;


Z = SolvableSyzygySeq(r.ring.coFac).leftZeroRelationsArbitrary( f.list );
Zp = ModuleList( r.ring, Z );
print "seq left syz Output:", Zp;
print;
if SolvableSyzygySeq(r.ring.coFac).isLeftZeroRelation( Zp.list, f.list ):
   print "is left syzygy";
else:
   print "is not left syzygy";


Zr = SolvableSyzygySeq(r.ring.coFac).rightZeroRelationsArbitrary( f.list );
Zpr = ModuleList( r.ring, Zr );
print "seq right syz Output:", Zpr;
print;
if SolvableSyzygySeq(r.ring.coFac).isRightZeroRelation( Zpr.list, f.list ):
   print "is right syzygy";
else:
   print "is not right syzygy";




rg = f.leftGB();
print "seq left Output:", rg;
print;
if rg.isLeftGB():
   print "is left GB";
else:
   print "is not left GB";
g = rg.list;


rg = f.twosidedGB();
print "seq twosided Output:", rg;
print;
if rg.isTwosidedGB():
   print "is twosided GB";
else:
   print "is not twosided GB";


rgb = rg.rightGB();
print "seq right Output:", rgb;
print;
if rgb.isRightGB():
   print "is right GB";
else:
   print "is not right GB";

