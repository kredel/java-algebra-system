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

from edu.jas.ring   import SolvableGroebnerBaseSeq;

if SolvableGroebnerBaseSeq().isLeftGB( rg.list ):
   print "is left GB";
else:
   print "is not left GB";


from java.util import ArrayList
from edu.jas.poly import OrderedPolynomialList

al = ArrayList();
fac = rg.ring;
rfac = fac.reverse();
print "reversed fac ", rfac;
for i in rg.list:
    ri = i.reverse(rfac);
    al.add(ri);

ap = OrderedPolynomialList( rfac, al );
print "reversed ", ap;

if SolvableGroebnerBaseSeq().isLeftGB( al ):
   print "twosided GB is right GB";
else:
   print "twosided GB is not right GB";


# compute right GB

bl = ArrayList();
for i in f.list:
    ri = i.reverse(rfac);
    bl.add(ri);

rpi = OrderedPolynomialList( rfac, bl );
print "reversed ideal ", rpi;

rgb = SolvableGroebnerBaseSeq().leftGB( bl );
rp = OrderedPolynomialList( rfac, rgb );

print "left reversed GB ", rp;

cl = ArrayList();
for i in rgb:
    ri = i.reverse(fac);
    cl.add(ri);

rc = OrderedPolynomialList( fac, cl );


print "seq right GB output", rc;
print;

if SolvableGroebnerBaseSeq().isRightGB( cl ):
   print "is right GB";
else:
   print "is not right GB";


drgb = SolvableGroebnerBaseSeq().rightGB( f.list );
drp = OrderedPolynomialList( fac, drgb );

print "right GB ", drp;

if SolvableGroebnerBaseSeq().isRightGB( drgb ):
   print "is right GB";
else:
   print "is not right GB";
