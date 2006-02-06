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


from edu.jas.ring   import SolvableGroebnerBaseSeq;

if SolvableGroebnerBaseSeq().isLeftGB( rg.list ):
   print "is left GB";
else:
   print "is not left GB";


from java.util import ArrayList

al = ArrayList();
fac = rg.ring;
rfac = fac.reverse();
print "reversed fac ", rfac;
for i in rg.list:
    ri = i.reverse(rfac);
    al.add(ri);

#print "reversed ", al;

if SolvableGroebnerBaseSeq().isLeftGB( al ):
   print "is right GB";
else:
   print "is not right GB";
