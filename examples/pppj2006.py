#
# jython examples for jas.
# $Id$
#

import sys;

from jas import PolyRing, ZZ
from jas import startLog

# pppj 2006 paper examples

#r = Ring( "Z(x1,x2,x3) L" );
r = PolyRing( ZZ(), "x1,x2,x3", PolyRing.lex );
print "Ring: " + str(r);
print;

#unused:
ps = """
( 
 ( 3 x1^2 x3^4 + 7 x2^5 - 61 )
) 
""";

f = 3 * x1**2 * x3**4 + 7 * x2**5 - 61;
print "f = " + str(f);
print;

#id = r.ideal( ps );
id = r.ideal( "", [f] );
print "Ideal: " + str(id);
print;

#startLog();

pps = """
 3 x1^2 x3^4 + 7 x2^5 - 61
""";

ri = r.ring;
print "ri = " + str(ri);
print

pol = id.pset;
print "pol = " + str(pol);
print

pol = ri.parse( pps );
print "pol = " + str(pol);
print
