#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import Ideal
from jas import startLog

# example cyclic6, optimize term order 
# optimal is no change

#r = Ring( "Rat (x1,x2,x3,x4,x5,x6)  G" );
#r = Ring( "Mod 3 (x1,x2,x3,x4,x5,x6)  L" );
#r = Ring( "Rat (x6,x5,x4,x3,x2,x1)  G" );
#r = Ring( "Mod 19 (x6,x5,x4,x3,x2,x1)  G" );
r = Ring( "Mod 3 (x6,x5,x4,x3,x2,x1) G" );
print "Ring: " + str(r);
print;

ps = """
(
x1 + x2 + x3 + x4 + x5 + x6,
x1*x2 + x1*x6 + x2*x3 + x3*x4 + x4*x5 + x5*x6,
x1*x2*x3 + x1*x2*x6 + x1*x5*x6 + x2*x3*x4 + x3*x4*x5 + x4*x5*x6,
x1*x2*x3*x4 + x1*x2*x3*x6 + x1*x2*x5*x6 + x1*x4*x5*x6 + x2*x3*x4*x5 + x3*x4*x5*x6,
x1*x2*x3*x4*x5 + x1*x2*x3*x4*x6 + x1*x2*x3*x5*x6 + x1*x2*x4*x5*x6 + x1*x3*x4*x5*x6 + x2*x3*x4*x5*x6,
x1*x2*x3*x4*x5*x6 - 1
)
""";
#x1 + x2 + x3 + x4 + x5 + x6,
#x1*x2*x3*x4*x5*x6 - 1

f = Ideal( r, ps );
print "Ideal: " + str(f);
print;

startLog();

o = f.optimize();
print "optimized Ideal: " + str(o);
print;

rg = f.GB();
print "Output:", rg;
print;

#org = o.GB();
#print "opt Output:", org;
#print;

