#
# jython examples for jas.

from jas import Ring
from jas import Ideal

from edu.jas import ring

#Rat(x1,x2,x3,x4,x5,x6) L
#Rat(x6,x5,x4,x3,x2,x1) L

rs = """
# polynomial ring:
Rat(x6,x5,x4,x3,x2,x1) L
""";

ps = """
(
 ( x1 - x2^9 ),
 ( x2 - x3^9 ),
 ( x3 - x4^9 ),
 ( x4 - x5^9 ),
 ( x5 - x6^9 ),
 ( x6 - 1/2 )
)
""";

r = Ring( rs );
print "Ring: " + str(r);

i = Ideal( r, ps );
print "Ideal: " + str(i);

g = i.GB();
print "seq GB:", g;

