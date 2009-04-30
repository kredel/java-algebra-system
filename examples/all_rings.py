#
# jython examples for jas.
# $Id$
#

import sys;

from jas import Ring
from jas import PolyRing
from jas import Ideal
from jas import startLog
from jas import terminate
from jas import ZZ, QQ, ZM, DD, CC, Quat, Oct, AN, RealN, RF, RC, LC, RR, PS, Vec, Mat
from edu.jas.arith import BigDecimal


print "------- ZZ = BigInteger ------------";
z1 = ZZ(12345678901234567890);
print "z1 = " + str(z1);
z2 = z1**2 + 12345678901234567890;
print "z2 = " + str(z2);
print;


print "------- QQ = BigRational ------------";
r1 = QQ(1,12345678901234567890);
print "r1 = " + str(r1**3);
r2 = r1**2 + (1,12345678901234567890);
print "r2 = " + str(r2);
print;


print "------- ZM = ModInteger ------------";
m1 = ZM(19,12345678901234567890);
print "m1 = " + str(m1);
m2 = m1**2 + 12345678901234567890;
print "m2 = " + str(m2);
print;


print "------- DD = BigDecimal ------------";
d1 = DD(12345678901234567890);
print "d1 = " + str(d1);
d2 = (1/d1)**2;
print "d2 = " + str(d2);
print;


print "------- CC = BigComplex ------------";
c1 = CC((1,2),(5,));
print "c1 = " + str(c1);
c2 = (1/c1)**2;
print "c2 = " + str(c2);
c3 = CC(0,(1,));
c3 = 1/c3;
print "c3 = " + str(c3);
[one,I] = CC().gens();
print "one = " + str(one);
print "I   = " + str(I);
c4 = c3 + 5 * I;
print "c4 = " + str(c4);
print;


print "------- Quat = BigQuaternion ------------";
[oneQ,IQ,JQ,KQ] = Quat().gens();
print "oneQ = " + str(oneQ);
print "IQ   = " + str(IQ);
print "JQ   = " + str(JQ);
print "KQ   = " + str(KQ);
q1 = 2 + 3 * IQ + 4 * JQ + 5 * KQ;
print "q1 = " + str(q1);
q2 = (1/q1)**2;
print "q2 = " + str(q2);
q3 = q2 * q1 * q1;
print "q3 = " + str(q3);
q4 = (-23,1458) + (-1,243)*IQ + (-4,729)*JQ + (-5,729)*KQ
print "q4 = " + str(q4);
q5 = q2 - q4;
print "q5  = " + str(q5);
print;


print "------- Oct = BigOctonion ------------";
#print [ str(g) for g in Oct().gens() ];
[oneOR,IOR,JOR,KOR,oneOI,IOI,JOI,KOI] = Oct().gens();
print "oneOR = " + str(oneOR);
print "IOR   = " + str(IOR);
print "JOR   = " + str(JOR);
print "KOR   = " + str(KOR);
print "oneOI = " + str(oneOI);
print "IOI   = " + str(IOI);
print "JOI   = " + str(JOI);
print "KOI   = " + str(KOI);
o1 = 2 * oneOR + 3 * IOR + 4 * JOR + 5 * KOR + 6 * oneOI + 7 * IOI + 8 * JOI + 9 * KOI;
print "o1 = " + str(o1);
o2 = (1/o1)**2;
print "o2 = " + str(o2);
o3 = o2 * o1 * o1;
print "o3 = " + str(o3);
o4 = (-69,20164)*oneOR + (-3,20164)*IOR + (-1,5041)*JOR + (-5,20164)*KOR  + (-3,10082)*oneOI + (-7,20164)*IOI + (-2,5041)*JOI + (-9,20164)*KOI;
print "o4 = " + str(o4);
o5 = o2 - o4;
print "o5  = " + str(o5);
print;


print "------- PolyRing(ZZ(),\"x,y,z\") ---------";
r = PolyRing(ZZ(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[one,x,y,z] = r.gens();
print "one = " + str(one);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
p1 = 2 + 3 * x + 4 * y + 5 * z + ( x + y + z )**2;
print "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 5 * z + 4 * y + 3 * x + 2;
print "p2  = " + str(p2);
p3 = p1 - p2;
print "p3  = " + str(p3);
print "p3.factory() = " + str(p3.factory());
print;


print "------- PolyRing(QQ(),\"x,y,z\") ---------";
r = PolyRing(QQ(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[one,x,y,z] = r.gens();
print "one = " + str(one);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
s1 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
print "s1  = " + str(s1);
s2 = (1,2) + (2,3) * x + (2,5) * y + ( x + y + z )**2;
print "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + (2,5) * y + (2,3) * x + (1,2);
print "s3  = " + str(s3);
s4 = s1 - s3;
print "s4  = " + str(s4);
print "s4.factory() = " + str(s4.factory());
print;


print "------- PolyRing(ZM(11),\"x,y,z\") ---------";
r = PolyRing(ZM(11),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[one,x,y,z] = r.gens();
print "one = " + str(one);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
p1 = 12 + 13 * x + 14 * y + 15 * z + ( x + y + z )**2;
print "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 4 * z + 3 * y + 2 * x + 1;
print "p2  = " + str(p2);
p3 = p1 - p2;
print "p3  = " + str(p3);
print "p3.factory() = " + str(p3.factory());
print;


print "------- PolyRing(DD(),\"x,y,z\") ---------";
r = PolyRing(DD(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[one,x,y,z] = r.gens();
print "one = " + str(one);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
p1 = 0.2 + 0.3 * x + 0.4 * y + 0.5 * z + ( x + y + z )**2;
print "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 0.5 * z + 0.40000000000000002220446049250313080847263336181641 * y + 0.29999999999999998889776975374843459576368331909180 * x + 0.200000000000000011102230246251565404236316680908203125;
print "p2  = " + str(p2);
p3 = p1 - p2;
print "p3  = " + str(p3);
print "p3.factory() = " + str(p3.factory());
print;


print "------- PolyRing(CC(),\"x,y,z\") ---------";
r = PolyRing(CC(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[one,I,x,y,z] = r.gens();
print "one = " + str(one);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
s1 = CC((1,2)) + CC((2,3)) * x + CC((2,5)) * y + ( x + y + z )**2;
print "s1  = " + str(s1);
#print "s1.factory() = " + str(s1.factory());
#print "s1.coefficients() = " + str(s1.coefficients());
s2 = ((1,2),) + ((2,3),) * x + ((2,5),) * y + ( x + y + z )**2;
print "s2  = " + str(s2);
#print "s2.factory() = " + str(s2.factory());
#print "s2.coefficients() = " + str(s2.coefficients());
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + (2,5) * y + (2,3) * x + (1,2);
print "s3  = " + str(s3);
#print "s3.factory() = " + str(s3.factory());
#print "s3.coefficients() = " + str(s3.coefficients());
s4 = s3 - s1;
print "s4  = " + str(s4);
print "s4.factory() = " + str(s4.factory());
print;


print "------- PolyRing(Quat(),\"x,y,z\") ---------";
r = PolyRing(Quat(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[oneQ,IQ,JQ,KQ,x,y,z] = r.gens();
print "oneQ = " + str(oneQ);
print "IQ   = " + str(IQ);
print "JQ   = " + str(JQ);
print "KQ   = " + str(KQ);
print "x    = " + str(x);
print "y    = " + str(y);
print "z    = " + str(z);
s1 = Quat((1,2)) + Quat((2,3)) * x + Quat((2,5)) * y + ( x + y + z )**2;
print "s1  = " + str(s1);
s2 = ((1,2),) + ((2,3),) * x + ((2,5),) * y + ( x + y + z )**2;
print "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + (2,5) * y + (2,3) * x + (1,2);
print "s3  = " + str(s3);
s4 = s3 - s1;
print "s4  = " + str(s4);
print "s4.factory() = " + str(s4.factory());
print;


print "------- PolyRing(Oct(),\"x,y,z\") ---------";
r = PolyRing(Oct(),"x,y,z",PolyRing.grad);
print "r = " + str(r);
[oneOR,IOR,JOR,KOR,oneOI,IOI,JOI,KOI,x,y,z] = r.gens();
print "oneOR = " + str(oneOR);
print "IOR   = " + str(IOR);
print "JOR   = " + str(JOR);
print "KOR   = " + str(KOR);
print "oneOI = " + str(oneOI);
print "IOI   = " + str(IOI);
print "JOI   = " + str(JOI);
print "KOI   = " + str(KOI);
print "x     = " + str(x);
print "y     = " + str(y);
print "z     = " + str(z);
s1 = Oct(Quat((1,2))) + Oct(Quat((2,3))) * x + Oct(Quat((2,5))) * y + ( x + y + z )**2;
print "s1  = " + str(s1);
s2 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
print "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + (2,5) * y + (2,3) * x + (1,2);
print "s3  = " + str(s3);
s4 = s3 - s1;
print "s4  = " + str(s4);
print "s4.factory() = " + str(s4.factory());
print;


print "------- AN(alpha**2 - 2) ---------";
r = PolyRing(QQ(),"alpha",PolyRing.lex);
print "r = " + str(r);
[e,a] = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);
sqrt2 = a**2 - 2;
print "sqrt2 = " + str(sqrt2);
Qs2 = AN(sqrt2);
print "Qs2   = " + str(Qs2.factory());
[one,alpha] = Qs2.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
b = alpha**2 - 2;
print "b     = " + str(b);
c = 1 / alpha;
print "c     = " + str(c);
Qs2x = AN(alpha**2 - 2);
print "Qs2x  = " + str(Qs2x.factory());
print;


print "------- GF_17(alpha**2 - 2) ---------";
r = PolyRing(ZM(17),"alpha",PolyRing.lex);
print "r = " + str(r);
[e,a] = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);
sqrt2 = a**2 - 2;
print "sqrt2 = " + str(sqrt2);
Qs2 = AN(sqrt2);
print "Qs2   = " + str(Qs2.factory());
[one,alpha] = Qs2.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
b = alpha**2 - 2;
print "b     = " + str(b);
c = 1 / alpha;
print "c     = " + str(c);
Qs2x = AN(alpha**2 - 2);
print "Qs2x  = " + str(Qs2x.factory());
print;


print "------- RealN(alpha**2 - 2,((1),(2)) ---------";
r = PolyRing(QQ(),"alpha",PolyRing.lex);
print "r = " + str(r);
[e,a] = r.gens();
print "e     = " + str(e);
print "a     = " + str(a);
sqrt2 = a**2 - 2;
print "sqrt2 = " + str(sqrt2);
Qs2r = RealN(sqrt2,(1,2));
print "Qs2r   = " + str(Qs2r.factory());
[one,alpha] = Qs2r.gens();
print "one   = " + str(one);
print "alpha = " + str(alpha);
b = 7 * alpha - 10;
print "b     = " + str(b);
print "b.factory()  = " + str(b.factory());
print "sign(b)      = " + str(b.signum());
print "magnitude(b) = " + str(BigDecimal(b.elem.magnitude()));
c = 1 / b;
print "c     = " + str(c);
print "sign(c)      = " + str(c.signum());
print "magnitude(c) = " + str(BigDecimal(c.elem.magnitude()));
Qs2rx = RealN( alpha**2 - 2, ( 1, 2 )  );
print "Qs2rx  = " + str(Qs2rx.factory());
print;


print "------- PolyRing(PolyRing(QQ(),\"a,b,c\"),\"x,y,z\") ---------";
r = PolyRing(QQ(),"a,b,c",PolyRing.grad);
print "r  = " + str(r);
pr = PolyRing(r,"x,y,z",PolyRing.lex);
print "pr = " + str(pr);
[one,a,b,c,x,y,z] = pr.gens();
print "one = " + str(one);
print "a   = " + str(a);
print "b   = " + str(b);
print "c   = " + str(c);
print "x   = " + str(x);
print "y   = " + str(y);
print "z   = " + str(z);
s1 = QQ(1,2) + ( QQ(2,3) - c ) * x + ( QQ(2,5) + a + b )**2 * y + ( x + y + z )**2;
print "s1  = " + str(s1);
s2 = (1,2) + ( (2,3) - c ) * x + ( (2,5) + a + b )**2 * y + ( x + y + z )**2;
print "s2  = " + str(s2);
s3  = z**2 + ( 2 ) * y * z + ( 2 ) * x * z + y**2 + ( 2 ) * x * y + ( b**2 + 2 * a * b + a**2 + (4,5) * b + (4,5) * a + (4,25) ) * y + x**2 - ( c - (2,3) ) * x + ( (1,2) );
print "s3  = " + str(s3);
s4 = s1 + s2 - 2 * s3;
print "s4  = " + str(s4);
print "s4.factory() = " + str(s4.factory());
x = PolyRing(PolyRing(QQ(),"a, b, c",PolyRing.grad),"x, y, z",PolyRing.lex);
print "x = " + str(x);
print;


print "------- RF(PolyRing(ZZ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing(ZZ(),"a,b,c",PolyRing.lex);
print "r = " + str(r);
rf = RF(r);
print "rf = " + str(rf.factory());
[one,a,b,c] = rf.gens();
print "one   = " + str(one);
print "a     = " + str(a);
print "b     = " + str(b);
print "c     = " + str(c);
q1 = a / b;
print "q1 = " + str(q1);
q2 = ( -2 * c**2 + 4 * b**2 + 4 * a**2 - 7 );
print "q2 = " + str(q2);
q3 = ( -7 * b + 4 * a + 12 );
print "q3 = " + str(q3);
q4 = q2 / q3;
print "q4 = " + str(q4);
q5 = ( 2 * c**2 - 4 * b**2 - 4 * a**2 + 7  ) / (7 * b - 4 * a - 12 );
print "q5 = " + str(q5);
q6 = q4 - q5;
print "q6 = " + str(q6);
print "q6.factory() = " + str(q6.factory());
x = RF(PolyRing(ZZ(),"a, b, c",PolyRing.lex));
print "x  = " + str(x.factory());
print;


print "------- RC(PolyRing(QQ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing(QQ(),"a,b,c",PolyRing.lex);
print "r = " + str(r);
[pone,pa,pb,pc] = r.gens();
print "pone   = " + str(pone);
print "pa     = " + str(pa);
print "pb     = " + str(pb);
print "pc     = " + str(pc);
g1 = pa**2 - 2;
print "g1 = " + str(g1);
g2 = pb**3 - 2;
print "g2 = " + str(g2);
g3 = pc**2 - pa*pb;
print "g3 = " + str(g3);
F = Ideal(r,list=[g1,g2,g3]);
print "F = " + str(F);
rc = RC(F);
print "rc.factory() = " + str(rc.factory());
[one,a,b,c] = rc.gens();
print "one   = " + str(one);
print "a     = " + str(a);
print "b     = " + str(b);
print "c     = " + str(c);
r1 = a*b + c;
print "r1 = " + str(r1);
r2 = r1*r1*r1 - r1*r1 + one;
print "r2 = " + str(r2);
r3 = r2**3 - r1 + one;
print "r3 = " + str(r3);
r4 = ( -120 * a * b**2 * c + 606 * b**2 * c + 1917 * a * b * c + 400 * b * c - 132 * a * c - 673 * c + 432 * a * b**2 + 2130 * b**2 + 1436 * a * b - 72 * b + 100 * a - 1950 );
print "r4 = " + str(r4);
r5 = r3 - r4;
print "r5 = " + str(r5);
print "r5.factory() = " + str(r5.factory());
r6 = 1/r2;
print "r6 = " + str(r6);
r7 = r6 * r2;
print "r7 = " + str(r7);
#F1 = Ideal(PolyRing(QQ(),"a, b, c",PolyRing.lex),list=[( pa**2 - 2 ), ( pb**3 - 2 ), ( pc**2 - pa * pb )]);
#print "F1 = " + str(F1);
rc1 = RC(Ideal(PolyRing(QQ(),"a, b, c",PolyRing.lex),list=[( a**2 - 2 ), ( b**3 - 2 ), ( c**2 - a * b )]));
print "rc1.factory() = " + str(rc1.factory());
print;


print "------- LC(PolyRing(QQ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing(QQ(),"a,b,c",PolyRing.lex);
print "r = " + str(r);
[pone,pa,pb,pc] = r.gens();
print "pone   = " + str(pone);
print "pa     = " + str(pa);
print "pb     = " + str(pb);
print "pc     = " + str(pc);
g1 = pa**2 - 2;
print "g1 = " + str(g1);
g2 = pb**3 - 2;
print "g2 = " + str(g2);
g3 = pc**2 - pa*pb;
print "g3 = " + str(g3);
F = Ideal(r,list=[g1,g2,g3]);
print "F = " + str(F);
lc = LC(F);
print "lc.factory() = " + str(lc.factory());
[one,a,b,c] = lc.gens();
print "one   = " + str(one);
print "a     = " + str(a);
print "b     = " + str(b);
print "c     = " + str(c);
#F1 = Ideal(PolyRing(QQ(),"a, b, c",PolyRing.lex),list=[( pa**2 - 2 ), ( pb**3 - 2 ), ( pc**2 - pa * pb )]);
#print "F1 = " + str(F1);
lc1 = LC(Ideal(PolyRing(QQ(),"a, b, c",PolyRing.lex),list=[( a**2 - 2 ), ( b**3 - 2 ), ( c**2 - a * b )]));
print "lc1.factory() = " + str(lc1.factory());
l1 = a*b + c;
print "l1 = " + str(l1);
l2 = l1*l1*l1 - l1*l1 + one;
print "l2 = " + str(l2);
l3 = 1/l2;
print "l3 = " + str(l3);
l4 = l3 * l2;
print "l4 = " + str(l4);
l5 = a**2 - 2 + 1;
print "l5 = " + str(l5);
l6 = 1/l5;
print "l6 = " + str(l6);
l7 =  (l1 * l2) / l2;
print "l7 = " + str(l7);
print;


print "------- RR( [QQ(),ZM(19),DD()] ) ---------";
r = RR( [QQ(),ZM(19),DD()] );
print "r = " + str(r);
print "r.factory() = " + str(r.factory());
rc1 = RR( [ QQ(), ZM(19), DD() ] );
print [ str(x) for x in r.gens() ];
print "rc1.factory() = " + str(rc1.factory());
[pg0,pg1,pg2] = r.gens();
print "pg0     = " + str(pg0);
print "pg1     = " + str(pg1);
print "pg2     = " + str(pg2);
r1 = pg1 + pg2 + pg0;
print "r1 = " + str(r1);
r2 = r1 * r1 + 7 * r1;
print "r2 = " + str(r2);
r3 = r2**3;
print "r3 = " + str(r3);
r4 = 1/r3;
print "r4 = " + str(r4);
r5 = r4 - r1;
print "r5 = " + str(r5);
r6 = ( (-511,512)*pg0 + 17*pg1 - 0.998046875*pg2 );
print "r6 = " + str(r6);
print;


print "------- PS(QQ(),\"x\") ---------";
r = PS(QQ(),"x");
print "r = " + str(r);
print "r.factory() = " + str(r.factory());
[one,x] = r.gens();
print "one   = " + str(one);
print "x     = " + str(x);
p1 = x**2 - 2;
print "p1 = " + str(p1);
p2 = x**3 - 2;
print "p2 = " + str(p2);
p3 = x**2 - p1 * p2;
print "p3 = " + str(p3);
p4 = - 4 + 3 * x**2 + 2 * x**3 - x**5;
print "p4 = " + str(p4);
def g1(i):
    return r.ring.coFac.fromInteger( 2*i );
def g2(i):
    #print "2*QQ(i) = " + str(QQ(2)*QQ(i))
    return 2*QQ(i);
r = PS(QQ(),"x",g2);
print "r = " + str(r);
print "r.factory() = " + str(r.factory());
[one,x] = r.gens();
print "one   = " + str(one);
print "x     = " + str(x);
p1 = x**2 - r;
print "p1 = " + str(p1);
p2 = x**3 - r/2;
print "p2 = " + str(p2);
print;


print "------- Vec(QQ(),7) ---------";
r = Vec(QQ(),7);
print "r = " + str(r);
print "r.factory() = " + str(r.factory());
print [ str(g) for g in r.gens() ];
[e1,e2,e3,e4,e5,e6,e7] = r.gens();
print "e1 = " + str(e1);
print "e2 = " + str(e2);
print "e3 = " + str(e3);
print "e4 = " + str(e4);
print "e5 = " + str(e5);
print "e6 = " + str(e6);
print "e7 = " + str(e7);
v1 = e1 + e3;
print "v1 = " + str(v1);
#v2 = v1 + 5 * e7;
#print "v2 = " + str(v2);
v3 = v1 - e1 - e3;
print "v3 = " + str(v3);
print;


print "------- Mat(QQ(),3,2) ---------";
r = Mat(QQ(),3,3);
print "r = " + str(r);
print "r.factory() = " + str(r.factory());
#print [ str(g) for g in r.gens() ];
[e11,e12,e13,e21,e22,e23,e31,e32,e33] = r.gens();
print "e11 = " + str(e11);
print "e12 = " + str(e12);
print "e13 = " + str(e13);
print "e21 = " + str(e21);
print "e22 = " + str(e22);
print "e23 = " + str(e23);
print "e31 = " + str(e31);
print "e32 = " + str(e32);
print "e33 = " + str(e33);
m1 = e11 + e31;
print "m1 = " + str(m1);
m2 = m1 * m1 + 5*e22;
print "m2 = " + str(m2);
m3 = m2**3 - 125*e21 - 125*e23;
print "m3 = " + str(m3);
#m4 = 1/m2;
#print "m4 = " + str(m4);
print;


print "------------------------------------";

#print "globals() = " + str(globals());

terminate();
sys.exit();

print "globals() = " + str(globals());
print "locals()  = " + str(locals());
print "vars()    = " + str(vars());

