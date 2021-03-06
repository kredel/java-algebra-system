#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring, PolyRing, Mat, Vec, QQ, GF, DD, RingElem
from jas import terminate

#from edu.jas.arith import BigRational

# example for linear algebra
#
#

p = 11
N = 6;
r = Mat(QQ(),N,N);
#r = Mat(GF(p),N,N);
#print "r = " + str(r);
print "r.factory() = " + str(r.factory());
#print;
#print r.gens().map{ |g| str(g) + ", " };
#print;

v = Vec(QQ(),N);
#v = Vec(GF(p),N);
#print "v = " + str(v);
print "v.factory() = " + str(v.factory());
print;
#print v.gens().map{ |g| str(g) + ", " };
#print;

#print "one:  " + str(r.one);
#print "zero: " + str(r.zero);
#print;
#print "zero: " + str(v.zero);
#print;

a = r.random(11);
print "a:  " + str(a);

b = v.random(7);
print "b:  " + str(b);

c = a * b;
print "c:  " + str(c);

x = a.solve(c);
print "x:  " + str(x);

print "x == b: " + str(x == b);
print;


L, U, P = a.decompLU();
print "P:  " + str(P);
print;

if P.elem.size() != 0:
    print "L:  " + str(L);
    print;
    print "U:  " + str(U);
    print;
    Us = RingElem(a.elem.getUpperScaled());
    print "scale(U):  " + str(Us);
    print;

    x = a.solveLU(P, c);
    print "x:  " + str(x);

    print "x == b: " + str(x == b);
    print;

    d = a.determinant(P)
    print "det(A):  " + str(d) + " ~= " + str(d.elem.getDecimal());
    #print "det(A):  " + str(d) + " ~= " + str(d.elem.getSymmetricVal());
    print;


r = Mat(QQ(), 2*N, N);
a = r.random(11);
#print "b:  " + str(b);

B = a.rowEchelon();
print "B:  " + str(B);

print "rank(B):  " + str(B.rank());
#print "B:  " + str(B);
print;


terminate();
#sys.exit();
