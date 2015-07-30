#
# jython examples for jas.
# $Id$
#

from jas import Ring

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

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

rg = f.GB();
print "seq Output:", rg;
print;

from edu.jas.gbufd  import SyzygySeq;
from edu.jas.poly   import ModuleList;
from edu.jas.gb     import GroebnerBaseSeq;

s = SyzygySeq(r.ring.coFac).zeroRelations( rg.list );
sl = ModuleList(rg.pset.ring,s);

print "syzygy:", sl;
print;

z = SyzygySeq(r.ring.coFac).isZeroRelation( s, rg.list );

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
   #mg = ModGroebnerBaseSeq(r.ring.coFac).GB(sl);
   mg = GroebnerBaseSeq().GB(sl);
   print "Mod GB: ", mg;
   print;

   zg = SyzygySeq(r.ring.coFac).zeroRelations(mg);
   print "syzygies of Mod GB: ", zg;
   print;

   #if ModGroebnerBaseSeq(r.ring.coFac).isGB( mg ):
   if GroebnerBaseSeq().isGB( mg ):
       print "is GB";
   else:
       print "is not GB";

   if SyzygySeq(r.ring.coFac).isZeroRelation(zg,mg):
       print "is Syzygy";
   else:
       print "is not Syzygy";

   if not zg.list:
       break;

