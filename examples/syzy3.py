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

from edu.jas.module import Syzygy;
from edu.jas.module import ModuleList;
from edu.jas.module import ModGroebnerBase;

R = Syzygy().resolution( f.pset );

for i in range(0,R.size()): 
   print "\n %s. resolution" % (i+1);
   print "\n ", R[i];

