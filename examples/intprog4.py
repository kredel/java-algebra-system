#
# jython for jas example 3 integer programming.
# $Id$
#
# CLO2, p374,c
# 3 A + 2 B +   C + D = 45
#   A + 2 B + 3 C + E = 21
# 2 A +   B +   C + F = 18
#
# max: 3 A + 4 B + 2 C 
#

import sys;

from jas import Ring

#r = Ring( "Rat(w1,w2,w3,w4,w5,w6,z1,z2,z3) W( (0,0,0,0,0,0,1,1,1),(-3,-4,-2,0,0,0,0,0,0) )" );
#r = Ring( "Rat(w1,w2,w3,w4,w5,w6,z1,z2,z3) W( (0,0,0,0,0,0,1,1,1),( 6, 5, 5,1,1,1,0,0,0)*2 )" );
#r = Ring( "Rat(w1,w2,w3,w4,w5,w6,z1,z2,z3) W( (0,0,0,0,0,0,1,1,1),( 3, 1, 3,1,1,1,0,0,0)   )" );
r = Ring(  "Rat(w1,w2,w3,w4,w5,w6,z1,z2,z3) W( (0,0,0,0,0,0,1,1,1),( 9, 6, 8,2,2,2,0,0,0)   )" );
print "Ring: " + str(r);
print;


ps = """
( 
 ( z1^3 z2   z3^2 - w1 ),
 ( z1^2 z2^2 z3   - w2 ),
 ( z1   z2^3 z3   - w3 ),
 ( z1             - w4 ),
 ( z2             - w5 ),
 ( z3             - w6 )
) 
""";

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

rg = f.GB();
print "seq Output:", rg;
print;


pf = """
( 
 ( z1^45 z2^21 z3^18 )
) 
""";

fp = r.ideal( pf );
print "Ideal: " + str(fp);
print;

nf = fp.NF(rg);
print "NFs: " + str(nf);
print;

