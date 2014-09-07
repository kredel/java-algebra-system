#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import startLog
from jas import terminate


# Nabashima, ISSAC 2007, example Ex-Fig-2
# integral function coefficients

r = Ring( "IntFunc(a, b, c) (x) L" );
print "Ring: " + str(r);
print;

ps = """
(
 ( { a } x^3 ),
 ( { b } x^2 ),
 ( { c } x )
) 
""";

#startLog();

f = r.paramideal( ps );
print "ParamIdeal: " + str(f);
print;

gs = f.CGBsystem();
print "CGBsystem: " + str(gs);
print;

bg = gs.isCGBsystem();
if bg:
    print "isCGBsystem: true";
else:
    print "isCGBsystem: false";
print;

#sys.exit();

gs = f.CGB();
print "CGB: " + str(gs);
print;

bg = gs.isCGB();
if bg:
    print "isCGB: true";
else:
    print "isCGB: false";
print;

terminate();
