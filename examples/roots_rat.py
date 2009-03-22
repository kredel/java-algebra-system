#
# jython examples for jas.
# $Id$
#

from java.lang import System
from java.lang import Integer

from jas import Ring
from jas import Ideal
from jas import terminate
from jas import startLog

from jas import QQ
from jas import DD

# polynomial examples: real roots over Q

#r = Ring( "Rat(x) L" );
r = Ring( "Q(x) L" );

print "Ring: " + str(r);
print;

[one,x] = r.gens();


f1 = x * ( x - 1 ) * ( x - 2 ) * ( x - 3 ) * ( x - 4 ) * ( x - 5 ) * ( x - 6 ) * ( x - 7 ) ;

f2 = ( x - (1,2) ) * ( x - (1,3) ) * ( x - (1,4) ) * ( x - (1,5) ) * ( x - (1,6) ) * ( x - (1,7) ) ;

f3 = ( x - (1,2**2) ) * ( x - (1,2**3) ) * ( x - (1,2**4) ) * ( x - (1,2**5) ) * ( x - (1,2**6) ) * ( x - (1,2**7) ) ;

f = f1 * f2 * f3;
#f = f1 * f2;
#f = f1 * f3;
#f = f2 * f3;
f = f1;

#f = ( x**2 - 2 );


print "f = ", f;
print;

startLog();

t = System.currentTimeMillis();
R = r.realRoots(f);
t = System.currentTimeMillis() - t;
print "R = ", R;
print "real roots time =", t, "milliseconds";

eps = QQ(1,10) ** DD().elem.DEFAULT_PRECISION;
print "eps = ", eps;

t = System.currentTimeMillis();
R = r.realRoots(f,eps);
t = System.currentTimeMillis() - t;
print "R = ", R;
print "real roots time =", t, "milliseconds";

#startLog();
#terminate();
