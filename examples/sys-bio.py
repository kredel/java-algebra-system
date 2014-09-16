#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import PolyRing, ZM, GF
from jas import terminate, startLog

# system biology examples: GB in Z_2
# see: Informatik Spektrum, 2009, February,
# Laubenbacher, Sturmfels: Computer Algebra in der Systembiologie

r = PolyRing(GF(2),"M, B, A, L, P",PolyRing.lex);
print "PolyRing: " + str(r);
print;

#automatic: [one,M,B,A,L,P] = r.gens();

f1 = M - A;
f2 = B - M;
f3 = A - A - L * B - A * L * B;
f4 = P - M;
f5 = L - P - L - L * B - L * P - L * B * P;
## t1 = M - 1;
## t2 = B - 1;
## t3 = A - 1;
## t4 = L - 1;
## t5 = P - 1;
#
## t1 = M;
## t2 = B;
## t3 = A;
## t4 = L;
## t5 = P;
#
## t1 = M;
## t2 = B;
## t3 = A;
## t4 = L - 1;
## t5 = P;
#
t1 = M;
t2 = B;
t3 = A - 1;
t4 = L - 1;
t5 = P;


F = [f1,f2,f3,f4,f5];
#F = [f1,f2,f3,f4,f5,t1,t2,t3,t4,t5];

I = r.ideal( "", list=F );
print "Ideal: " + str(I);
print;

G = I.GB();
print "GB: " + str(G);
print;


#startLog();
terminate();
