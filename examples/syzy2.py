#
# jython examples for jas.

from jas import Ring
from jas import Ideal

# ? example

r = Ring( "Rat(x,y,z) L" );
print "Ring: " + str(r);
print;

ps = """
( 
 ( z^3 - y ),
 ( y z - x ),
 ( y^3 - x^2 z ),
 ( x z^2 - y^2 )
) 
""";

f = Ideal( r, ps );
print "Ideal: " + str(f);
print;

rg = f.GB();
print "seq Output:", rg;
print;

from edu.jas.module import Syzygy;
from edu.jas.module import ModuleList;
from edu.jas.module import ModGroebnerBase;

s = Syzygy.zeroRelations( rg.list );
sl = ModuleList(rg.ring,s);

print "syzygy:", sl;
print;

z = Syzygy.isZeroRelation( s, rg.list );

print "is zero s ?",
if z:
    print "true"
else:
    print "false"
print;

zg = sl;

for i in range(1,len(r.ring.vars)+1): 
   print "\n %s. resolution" % i;

   sl = zg;
   mg = ModGroebnerBase.GB(sl);
   print "Mod GB: ", mg;
   print;

   zg = Syzygy.zeroRelations(mg);
   print "syzygies of Mod GB: ", zg;
   print;

   if ModGroebnerBase.isGB( mg ):
       print "is GB";
   else:
       print "is not GB";

   if Syzygy.isZeroRelation(zg,mg):
       print "is Syzygy";
   else:
       print "is not Syzygy";

   if not zg.list:
       break;

