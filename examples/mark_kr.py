#
# jython examples for jas.
# $Id$
#

#import sys;

from jas import Ring, PolyRing, ZZ
from jas import startLog

# mark, d-gb diplom example, due to kandri-rody 1984
#
# The MAS DIIPEGB implementation contains an error because the output e-GB
# is not correct. Also the cited result from k-r contains this error.
# The polynomial 
#
# ( 2 x * y^2 - x^13 + 2 x^11 -   x^9 + 2 x^7 - 2 x^3 ),
#
# is in the DIIPEGB output, but it must be
#
# ( 2 x * y^2 - x^13 + 2 x^11 - 3 x^9 + 2 x^7 - 2 x^3 ),
#
# Test by adding the polynomials to the input.
# Frist polynomial produces a different e-GB. 
# Second polynomial reproduces the e-GB with the second polynomial. 

#r = Ring( "Z(x,y) L" );
r = PolyRing( ZZ(), "x,y", PolyRing.lex );
print "Ring: " + str(r);
print;

ps = """
( 
 ( y**6 + x**4 y**4 - x**2 y**4 - y**4 - x**4 y**2 + 2 x**2 y**2 + x**6 - x**4 ),
 ( 2 x**3 y**4 - x y**4 - 2 x**3 y**2 + 2 x y**2 + 3 x**5 - 2 x** 3 ),
 ( 3 y**5 + 2 x**4 y**3 - 2 x**2 y**3 - 2 y**3 - x**4 y + 2 x**2 y )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

#startLog();

eg = f.eGB();
print "seq e-GB:", eg;
print "is e-GB:", eg.isGB();
print;


#startLog();

dg = f.dGB();
print "seq d-GB:", dg;
print "is d-GB:", dg.isGB();
print;

print "d-GB == e-GB:", eg.list.equals(dg.list);
print "d-GB == e-GB:", eg == dg;
