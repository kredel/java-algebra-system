#
# jython examples for jas.
# $Id$
#

import sys

from java.lang import System

from jas import PolyRing, Ideal
from jas import terminate, startLog
from jas import ZM, RF, QQ, DD

# system biology examples: GB in RF()
# see: Informatik Spektrum, 2009, February,
# Laubenbacher, Sturmfels: Computer Algebra in der Systembiologie
# example from: http://theory.bio.uu.nl/rdb/books/tb.pdf

# ------------ input rational expression --------------------

r = PolyRing(RF(PolyRing(QQ(),"A",PolyRing.lex)),"L, M, R",PolyRing.lex);
print "PolyRing: " + str(r);
print;

[one,A,L,M,R] = r.gens();

c = 1;
gamma = 1;
v = 1;
c0 = QQ(5,100);   # 0.05
h = 2;
n = 5;
delta = QQ(2,10); # 0.2

f1 = R - 1/(1+A**n);
f2 = M * L - delta * A - ( v * M * A ) / ( h + A ); 
f3 = c0 + c * ( 1 - 1 / ( 1 + A**n ) - gamma * M);


F = [f1,f2,f3];
print "f1 = " + str(f1);
print "f2 = " + str(f2);
print "f3 = " + str(f3);
print;

I = r.ideal( "", list=F );
print "Ideal: " + str(I);
print;

#sys.exit();

G = I.GB();
print "GB: " + str(G);
print;


# ------------ GB and CGB --------------------

r2 = PolyRing(PolyRing(QQ(),"L",PolyRing.lex),"A, M, R",PolyRing.lex);
#r2 = PolyRing(RF(PolyRing(QQ(),"L",PolyRing.lex)),"A, M, R",PolyRing.lex);
print "PolyRing: " + str(r2);
print;

[one,L,A,M,R] = r2.gens();

fi1 = ( (21,20) * A**6 + (21,10) * A**5 + (1,20) * A + (1,10) ) * L - ( (1,5) * A**7 + (29,20) * A**6 + (1,5) * A**2 + (9,20) * A ); 
fi2 = ( A**5 + 1 ) * M - ( (21,20) * A**5 + (1,20) );
fi3 = ( A**5 + 1 ) * R - ( 1 );
#fi4 = L - 1;

Fi = [fi1,fi2,fi3];
print "fi1 = " + str(fi1);
print "fi2 = " + str(fi2);
print "fi3 = " + str(fi3);
#print "fi4 = " + str(fi4);
print;

Ii = r2.ideal( "", list=Fi );
print "Ideal: " + str(Ii);
print;

Gi = Ii.GB();
print "GB: " + str(Gi);
print;

Ipi = r2.paramideal( "", list=Gi.list );
print "ParamIdeal: " + str(Ipi);
print;

cgb = Ipi.CGB();
print "CGB: " + str(cgb);
print;

#sys.exit();

# ------------ real roots --------------------

r3 = PolyRing(QQ(),"A",PolyRing.lex);
print "PolyRing: " + str(r3);
print;

eps = QQ(1,10) ** DD().elem.DEFAULT_PRECISION;
print "eps = ", eps;

[one,A] = r3.gens();

plot={};
plotd={};
br = 1;
for i in range(1,30):
    L = QQ(-1) + (i,10);
    #L = QQ(9,10) + (i,100);
    fr = QQ(5) * ( (1,5) ) * A**7 - ( (21,20) * L - (29,20) ) * A**6 - ( (21,10) * L ) * A**5 + ( (1,5) ) * A**2 - ( (1,20) * L - (9,20) ) * A - ( (1,10) * L );

    print "L  = " + str(DD(L));
    #print "fr = " + str(fr);
    #print;

    t = System.currentTimeMillis();
    R = r3.realRoots(fr);
    t = System.currentTimeMillis() - t;
#    print "R = " + str([ str(DD(r.elem.getRational())) for r in R ]);
    print "R = " + str([ r.elem.decimalMagnitude() for r in R ]);
    plot[float(DD(L))] = R;
    #print "real roots time =", t, "milliseconds";
    if len(R) != br:
        br = len(R);
        print "#(real roots) = %s" % br;
        b = L - (11,100);
        for j in range(1,12):
            L = b + (j,100);
            fri = QQ(5) * ( (1,5) ) * A**7 - ( (21,20) * L - (29,20) ) * A**6 - ( (21,10) * L ) * A**5 + ( (1,5) ) * A**2 - ( (1,20) * L - (9,20) ) * A - ( (1,10) * L );
            R = r3.realRoots(fri);
            print "L = %s, Ri = %s" %(DD(L),[ r.elem.decimalMagnitude() for r in R ]);
            plot[float(DD(L))] = R;
            R = r3.realRoots(fri,eps);
            plotd[float(DD(L))] = [ r.elem.decimalMagnitude() for r in R ];

    t = System.currentTimeMillis();
    R = r3.realRoots(fr,eps);
    plotd[float(DD(L))] = [ r.elem.decimalMagnitude() for r in R ];
    t = System.currentTimeMillis() - t;
    print "R = " + str([ r.elem.decimalMagnitude() for r in R ]);
    print "real roots time =", t, "milliseconds";
    print;

print "real algebraic numbers:";
pk = plot.keys();
pk.sort();
for l in pk:
    print "%s, %s" %(l,[ str(p.elem) for p in plot[l]]);
print;

print "real root approxmations:";
pk = plotd.keys();
pk.sort();
for l in pk:
    print "%s, %s" %(l,[ str(p) for p in plotd[l]]);
print;


# ------------ factor polynomial --------------------
r5 = PolyRing(QQ(),"L",PolyRing.lex);
print "PolyRing: " + str(r5);
print;

[one,L] = r5.gens();
frl = L**5 + (5,31) * L**4 - (10,31) * L**3 + (10,31) * L**2 - (5,31) * L + (1,31);
#frl = (1,4) * L - (1,4);
print "frl = " + str(frl);
print;

t = System.currentTimeMillis();
R = r5.realRoots(frl);
t = System.currentTimeMillis() - t;
print "R = " + str([ r.elem.decimalMagnitude() for r in R ]);
print "real roots time =", t, "milliseconds";

t = System.currentTimeMillis();
R = r5.realRoots(frl,eps);
t = System.currentTimeMillis() - t;
print "R = " + str([ r.elem.decimalMagnitude() for r in R ]);
print "real roots time =", t, "milliseconds";

t = System.currentTimeMillis();
fk = r5.squarefreeFactors(frl);
#fk = r5.factors(frl);
t = System.currentTimeMillis() - t;
#print "fk = " + str(fk);
#print "factor time =", t, "milliseconds";

g = one;
for h, i in fk.iteritems():
    print "h**i = (", h, ")**" + str(i);
    h = h**i;
    g = g*h;

#startLog();
terminate();
