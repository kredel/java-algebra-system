#
# jruby examples for jas.
# $Id: $
#

#load "examples/jas.rb"
require "examples/jas"

#from jas import Ring, PolyRing, SolvPolyRing
#from jas import Ideal
#from jas import Module, SubModule, SolvableModule, SolvableSubModule
#from jas import startLog
#from jas import terminate
#from jas import ZZ, QQ, ZM, DD, CC, Quat, Oct, AN, RealN, RF, RC, LC, RR, PS, Vec, Mat
#from edu.jas.arith import BigDecimal


puts "------- ZZ = BigInteger ------------";
z1 = ZZ(12345678901234567890);
puts "z1 = " + str(z1);
z2 = z1**2 + 12345678901234567890;
puts "z2 = " + str(z2);
puts;


puts "------- QQ = BigRational ------------";
r1 = QQ(1,12345678901234567890);
puts "r1 = " + str(r1**3);
r2 = r1**2 + 1/12345678901234567890;
puts "r2 = " + str(r2);
puts;


puts "------- ZM = ModInteger ------------";
m1 = ZM(19,12345678901234567890);
puts "m1 = " + str(m1);
m2 = m1**2 + 12345678901234567890;
puts "m2 = " + str(m2);
puts;


puts "------- DD = BigDecimal ------------";
d1 = DD(12345678901234567890);
puts "d1 = " + str(d1);
d2 = (1/d1)**2;
puts "d2 = " + str(d2);
puts;


puts "------- CC = BigComplex ------------";
c1 = CC(1/2,5);
puts "c1 = " + str(c1);
c2 = (1/c1)**2;
puts "c2 = " + str(c2);
c3 = CC(0,1);
c3 = 1/c3;
puts "c3 = " + str(c3);
one,I = CC().gens();
puts "one = " + str(one);
puts "I   = " + str(I);
c4 = c3 + 5 * I;
puts "c4 = " + str(c4);
c5 = -396/10201 - 80/10201*I;
puts "c5 = " + str(c5);
puts;


puts "------- Quat = BigQuaternion ------------";
oneQ,IQ,JQ,KQ = Quat().gens();
puts "oneQ = " + str(oneQ);
puts "IQ   = " + str(IQ);
puts "JQ   = " + str(JQ);
puts "KQ   = " + str(KQ);
q1 = 2 + 3 * IQ + 4 * JQ + 5 * KQ;
puts "q1 = " + str(q1);
q2 = (1/q1)**2;
puts "q2 = " + str(q2);
q3 = q2 * q1 * q1;
puts "q3 = " + str(q3);
q4 = -23/1458 + -1/243*IQ + -4/729*JQ + -5/729*KQ
puts "q4 = " + str(q4);
q5 = q2 - q4;
puts "q5  = " + str(q5);
puts;


puts "------- Oct = BigOctonion ------------";
#puts [ str(g) for g in Oct().gens() ];
oneOR,IOR,JOR,KOR,oneOI,IOI,JOI,KOI = Oct().gens();
puts "oneOR = " + str(oneOR);
puts "IOR   = " + str(IOR);
puts "JOR   = " + str(JOR);
puts "KOR   = " + str(KOR);
puts "oneOI = " + str(oneOI);
puts "IOI   = " + str(IOI);
puts "JOI   = " + str(JOI);
puts "KOI   = " + str(KOI);
o1 = 2 * oneOR + 3 * IOR + 4 * JOR + 5 * KOR + 6 * oneOI + 7 * IOI + 8 * JOI + 9 * KOI;
puts "o1 = " + str(o1);
o2 = (1/o1)**2;
puts "o2 = " + str(o2);
o3 = o2 * o1 * o1;
puts "o3 = " + str(o3);
o4 = -69/20164*oneOR + -3/20164*IOR + -1/5041*JOR + -5/20164*KOR  + -3/10082*oneOI + -7/20164*IOI + -2/5041*JOI + -9/20164*KOI;
puts "o4 = " + str(o4);
o5 = o2 - o4;
puts "o5  = " + str(o5);
puts;


puts "------- PolyRing(ZZ(),\"x,y,z\") ---------";
r = PolyRing.new(ZZ(),"x,y,z",PolyRing::grad);
puts "r = " + str(r);
one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
p1 = 2 + 3 * x + 4 * y + 5 * z + ( x + y + z )**2;
puts "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 5 * z + 4 * y + 3 * x + 2;
puts "p2  = " + str(p2);
p3 = p1 - p2;
puts "p3  = " + str(p3);
puts "p3.factory() = " + str(p3.factory());
puts;


puts "------- PolyRing(QQ(),\"x,y,z\") ---------";
r = PolyRing.new(QQ(),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
s1 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
s2 = 1/2 + 2/3 * x + 2/5 * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 2/5 * y + 2/3 * x + 1/2;
puts "s3  = " + str(s3);
s4 = s1 - s3;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
puts;


puts "------- PolyRing(ZM(11),\"x,y,z\") ---------";
r = PolyRing.new(ZM(11),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
p1 = 12 + 13 * x + 14 * y + 15 * z + ( x + y + z )**2;
puts "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 4 * z + 3 * y + 2 * x + 1;
puts "p2  = " + str(p2);
p3 = p1 - p2;
puts "p3  = " + str(p3);
puts "p3.factory() = " + str(p3.factory());
puts;


puts "------- PolyRing(DD(),\"x,y,z\") ---------";
r = PolyRing.new(DD(),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
one,x,y,z = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
p1 = 0.2 + 0.3 * x + 0.4 * y + 0.5 * z + ( x + y + z )**2;
puts "p1  = " + str(p1);
p2  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + 0.5 * z + 0.40000000000000002220446049250313080847263336181641 * y + 0.29999999999999998889776975374843459576368331909180 * x + 0.200000000000000011102230246251565404236316680908203125;
puts "p2  = " + str(p2);
p3 = p1 - p2;
puts "p3  = " + str(p3);
puts "p3.factory() = " + str(p3.factory());
puts;


puts "------- PolyRing(CC(),\"x,y,z\") ---------";
r = PolyRing.new(CC(),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
one,I,x,y,z = r.gens();
puts "one = " + str(one);
puts "I   = " + str(I);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
s1 = CC(1/2) + CC(2/3) * x + 3 * I * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
#puts "s1.factory() = " + str(s1.factory());
#puts "s1.coefficients() = " + str(s1.coefficients());
s2 = CC(1/2) + CC(2/3) * x + 3 * I * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
#puts "s2.factory() = " + str(s2.factory());
#puts "s2.coefficients() = " + str(s2.coefficients());
s3  = z**2 + CC(2) * y * z + CC(2) * x * z + y**2 + CC(2) * x * y + x**2 + CC(0,3) * y + CC(2/3) * x + CC(1/2);
puts "s3  = " + str(s3);
#puts "s3.factory() = " + str(s3.factory());
#puts "s3.coefficients() = " + str(s3.coefficients());
s4 = s3 - s1;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
puts;


puts "------- PolyRing(Quat(),\"x,y,z\") ---------";
r = PolyRing.new(Quat(),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
oneQ,IQ,JQ,KQ,x,y,z = r.gens();
puts "oneQ = " + str(oneQ);
puts "IQ   = " + str(IQ);
puts "JQ   = " + str(JQ);
puts "KQ   = " + str(KQ);
puts "x    = " + str(x);
puts "y    = " + str(y);
puts "z    = " + str(z);
s1 = Quat(1/2) + Quat(2/3) * x + Quat(2/5) * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
s2 = Quat(1/2) + Quat(2/3) * x + Quat(2/5) * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + Quat(2/5) * y + Quat(2/3) * x + Quat(1/2);
puts "s3  = " + str(s3);
s4 = s3 - s1;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
puts;


puts "------- PolyRing(Oct(),\"x,y,z\") ---------";
r = PolyRing.new(Oct(),"x,y,z",PolyRing.grad);
puts "r = " + str(r);
oneOR,IOR,JOR,KOR,oneOI,IOI,JOI,KOI,x,y,z = r.gens();
puts "oneOR = " + str(oneOR);
puts "IOR   = " + str(IOR);
puts "JOR   = " + str(JOR);
puts "KOR   = " + str(KOR);
puts "oneOI = " + str(oneOI);
puts "IOI   = " + str(IOI);
puts "JOI   = " + str(JOI);
puts "KOI   = " + str(KOI);
puts "x     = " + str(x);
puts "y     = " + str(y);
puts "z     = " + str(z);
s1 = Oct(Quat(1/2)) + Oct(Quat(2/3)) * x + Oct(Quat(2/5)) * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
s2 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + QQ(2,5) * y + QQ(2,3) * x + QQ(1,2);
puts "s3  = " + str(s3);
s4 = s3 - s1;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
puts;


puts "------- AN(alpha**2 - 2) ---------";
r = PolyRing.new(QQ(),"alpha",PolyRing.lex);
puts "r = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
sqrt2 = a**2 - 2;
puts "sqrt2 = " + str(sqrt2);
Qs2 = AN(sqrt2);
puts "Qs2   = " + str(Qs2.factory());
one,alpha = Qs2.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
b = alpha**2 - 2;
puts "b     = " + str(b);
c = 1 / alpha;
puts "c     = " + str(c);
Qs2x = AN(alpha**2 - 2);
puts "Qs2x  = " + str(Qs2x.factory());
puts;


puts "------- GF_17(alpha**2 - 2) ---------";
r = PolyRing.new(ZM(17),"alpha",PolyRing.lex);
puts "r = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
sqrt2 = a**2 - 2;
puts "sqrt2 = " + str(sqrt2);
Qs2 = AN(sqrt2);
puts "Qs2   = " + str(Qs2.factory());
one,alpha = Qs2.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
b = alpha**2 - 2;
puts "b     = " + str(b);
c = 1 / alpha;
puts "c     = " + str(c);
Qs2x = AN(alpha**2 - 2);
puts "Qs2x  = " + str(Qs2x.factory());
puts;


puts "------- RealN(alpha**2 - 2,(1,2) ---------";
r = PolyRing.new(QQ(),"alpha",PolyRing.lex);
puts "r = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
sqrt2 = a**2 - 2;
puts "sqrt2 = " + str(sqrt2);
Qs2r = RealN(sqrt2,[1,2]);
puts "Qs2r   = " + str(Qs2r.factory());
one,alpha = Qs2r.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);
b = 7 * alpha - 10;
puts "b     = " + str(b);
puts "b.factory()  = " + str(b.factory());
puts "sign(b)      = " + str(b.signum());
puts "magnitude(b) = " + str(BigDecimal.new(b.elem.magnitude()));
c = 1 / b;
puts "c     = " + str(c);
puts "sign(c)      = " + str(c.signum());
puts "magnitude(c) = " + str(BigDecimal.new(c.elem.magnitude()));
Qs2rx = RealN( alpha**2 - 2, [1,2] );
puts "Qs2rx  = " + str(Qs2rx.factory());
puts;


puts "------- PolyRing(PolyRing(QQ(),\"a,b,c\"),\"x,y,z\") ---------";
r = PolyRing.new(QQ(),"a,b,c",PolyRing.grad);
puts "r  = " + str(r);
pr = PolyRing.new(r,"x,y,z",PolyRing.lex);
puts "pr = " + str(pr);
one,a,b,c,x,y,z = pr.gens();
puts "one = " + str(one);
puts "a   = " + str(a);
puts "b   = " + str(b);
puts "c   = " + str(c);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
s1 = QQ(1,2) + ( QQ(2,3) - c ) * x + ( QQ(2,5) + a + b )**2 * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
s2 = 1/2 + ( 2/3 - c ) * x + ( 2/5 + a + b )**2 * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
s3  = z**2 + ( 2 ) * y * z + ( 2 ) * x * z + y**2 + ( 2 ) * x * y + ( b**2 + 2 * a * b + a**2 + QQ(4,5) * b + QQ(4,5) * a + QQ(4,25) ) * y + x**2 - ( c - QQ(2,3) ) * x + ( QQ(1,2) );
puts "s3  = " + str(s3);
s4 = s1 + s2 - 2 * s3;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
x = PolyRing.new(PolyRing.new(QQ(),"a, b, c",PolyRing.grad),"x, y, z",PolyRing.lex);
puts "x = " + str(x);
puts;


puts "------- RF(PolyRing(ZZ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing.new(ZZ(),"a,b,c",PolyRing.lex);
puts "r = " + str(r);
rf = RF(r);
puts "rf = " + str(rf.factory());
one,a,b,c = rf.gens();
puts "one   = " + str(one);
puts "a     = " + str(a);
puts "b     = " + str(b);
puts "c     = " + str(c);
q1 = a / b;
puts "q1 = " + str(q1);
q2 = ( -2 * c**2 + 4 * b**2 + 4 * a**2 - 7 );
puts "q2 = " + str(q2);
q3 = ( -7 * b + 4 * a + 12 );
puts "q3 = " + str(q3);
q4 = q2 / q3;
puts "q4 = " + str(q4);
q5 = ( 2 * c**2 - 4 * b**2 - 4 * a**2 + 7  ) / (7 * b - 4 * a - 12 );
puts "q5 = " + str(q5);
q6 = q4 - q5;
puts "q6 = " + str(q6);
puts "q6.factory() = " + str(q6.factory());
x = RF(PolyRing.new(ZZ(),"a, b, c",PolyRing.lex));
puts "x  = " + str(x.factory());
puts;


puts "------- RC(PolyRing(QQ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing.new(QQ(),"a,b,c",PolyRing.lex);
puts "r = " + str(r);
pone,pa,pb,pc = r.gens();
puts "pone   = " + str(pone);
puts "pa     = " + str(pa);
puts "pb     = " + str(pb);
puts "pc     = " + str(pc);
g1 = pa**2 - 2;
puts "g1 = " + str(g1);
g2 = pb**3 - 2;
puts "g2 = " + str(g2);
g3 = pc**2 - pa*pb;
puts "g3 = " + str(g3);
F = r.ideal("",list=[g1,g2,g3]);
puts "F = " + str(F);
rc = RC(F,0);
puts "rc.factory() = " + str(rc.factory());
one,a,b,c = rc.gens();
puts "one   = " + str(one);
puts "a     = " + str(a);
puts "b     = " + str(b);
puts "c     = " + str(c);
r1 = a*b + c;
puts "r1 = " + str(r1);
r2 = r1*r1*r1 - r1*r1 + one;
puts "r2 = " + str(r2);
r3 = r2**3 - r1 + one;
puts "r3 = " + str(r3);
r4 = ( -120 * a * b**2 * c + 606 * b**2 * c + 1917 * a * b * c + 400 * b * c - 132 * a * c - 673 * c + 432 * a * b**2 + 2130 * b**2 + 1436 * a * b - 72 * b + 100 * a - 1950 );
puts "r4 = " + str(r4);
r5 = r3 - r4;
puts "r5 = " + str(r5);
puts "r5.factory() = " + str(r5.factory());
r6 = 1/r2;
puts "r6 = " + str(r6);
r7 = r6 * r2;
puts "r7 = " + str(r7);
#F1 = SimIdeal.new(PolyRing.new(QQ(),"a, b, c",PolyRing.lex),list=[( pa**2 - 2 ), ( pb**3 - 2 ), ( pc**2 - pa * pb )]);
#puts "F1 = " + str(F1);
rc1 = RC(SimIdeal.new(PolyRing.new(QQ(),"a, b, c",PolyRing.lex),"",list=[( a**2 - 2 ), ( b**3 - 2 ), ( c**2 - a * b )]));
puts "rc1.factory() = " + str(rc1.factory());
puts;


puts "------- LC(PolyRing(QQ(),\"a,b,c\",PolyRing.lex)) ---------";
r = PolyRing.new(QQ(),"a,b,c",PolyRing.lex);
puts "r = " + str(r);
pone,pa,pb,pc = r.gens();
puts "pone   = " + str(pone);
puts "pa     = " + str(pa);
puts "pb     = " + str(pb);
puts "pc     = " + str(pc);
g1 = pa**2 - 2;
puts "g1 = " + str(g1);
g2 = pb**3 - 2;
puts "g2 = " + str(g2);
g3 = pc**2 - pa*pb;
puts "g3 = " + str(g3);
ff = r.ideal("",list=[g1,g2,g3]);
puts "ff = " + str(ff);
lc = LC(ff,0,1);
puts "lc.factory() = " + str(lc.factory());
one,a,b,c = lc.gens();
puts "one   = " + str(one);
puts "a     = " + str(a);
puts "b     = " + str(b);
puts "c     = " + str(c);
#F1 = SimIdeal.new(PolyRing.new(QQ(),"a, b, c",PolyRing.lex),"",list=[( pa**2 - 2 ), ( pb**3 - 2 ), ( pc**2 - pa * pb )]);
#puts "F1 = " + str(F1);
lc1 = LC(SimIdeal.new(PolyRing.new(QQ(),"a, b, c",PolyRing.lex),"",list=[( a**2 - 2 ), ( b**3 - 2 ), ( c**2 - a * b )]));
puts "lc1.factory() = " + str(lc1.factory());
l1 = a*b + c;
puts "l1 = " + str(l1);
l2 = l1*l1*l1 - l1*l1 + one;
puts "l2 = " + str(l2);
l3 = 1/l2;
puts "l3 = " + str(l3);
l4 = l3 * l2;
puts "l4 = " + str(l4);
l5 = a**2 - 2 + 1;
puts "l5 = " + str(l5);
l6 = 1/l5;
puts "l6 = " + str(l6);
l7 =  (l1 * l2) / l2;
puts "l7 = " + str(l7);
puts;


puts "------- RR( [QQ(),ZM(19),DD()] ) ---------";
r = RR( [ QQ(),ZM(19),DD() ] );
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
rc1 = RR( [ QQ(), ZM(19), DD() ] );
puts r.gens.each { |x| str(x) };
puts "rc1.factory() = " + str(rc1.factory());
pg0,pg1,pg2 = r.gens();
puts "pg0     = " + str(pg0);
puts "pg1     = " + str(pg1);
puts "pg2     = " + str(pg2);
r1 = pg1 + pg2 + pg0;
puts "r1 = " + str(r1);
r2 = r1 * r1 + 7 * r1;
puts "r2 = " + str(r2);
r3 = r2**3;
puts "r3 = " + str(r3);
r4 = 1/r3;
puts "r4 = " + str(r4);
r5 = r4 - r1;
puts "r5 = " + str(r5);
r6 = -511/512 * pg0 + 17 * pg1; #TODO - 0.998046875 * pg2;
puts "r6 = " + str(r6);
puts;


puts "------- PS(QQ(),\"x\") ---------";
r = PS(QQ(),"x");
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
one,x = r.gens();
puts "one   = " + str(one);
puts "x     = " + str(x);
p1 = x**2 - 2;
puts "p1 = " + str(p1);
p2 = x**3 - 2;
puts "p2 = " + str(p2);
p3 = x**2 - p1 * p2;
puts "p3 = " + str(p3);
p4 = - 4 + 3 * x**2 + 2 * x**3 - x**5;
puts "p4 = " + str(p4);
def g1(i)
    return r.ring.coFac.fromInteger( 2*i );
end
def g2(i)
    #puts "2*QQ(i) = " + str(QQ(2)*QQ(i))
    return 2*QQ(i);
end
g3 = Proc.new { |i| 2*QQ(i) }
r = PS(QQ(),"x",&g3);
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
one,x = r.gens();
puts "one   = " + str(one);
puts "x     = " + str(x);
p1 = x**2 - r;
puts "p1 = " + str(p1);
p2 = x**3 - r/2;
puts "p2 = " + str(p2);
puts;


puts "------- Vec(QQ(),7) ---------";
r = Vec(QQ(),7);
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
#puts [ str(g) for g in r.gens() ];
e1,e2,e3,e4,e5,e6,e7 = r.gens();
puts "e1 = " + str(e1);
puts "e2 = " + str(e2);
puts "e3 = " + str(e3);
puts "e4 = " + str(e4);
puts "e5 = " + str(e5);
puts "e6 = " + str(e6);
puts "e7 = " + str(e7);
v1 = e1 + e3;
puts "v1 = " + str(v1);
#v2 = v1 + 5 * e7;
#puts "v2 = " + str(v2);
v3 = v1 - e1 - e3;
puts "v3 = " + str(v3);
puts;


puts "------- Mat(QQ(),3,3) ---------";
r = Mat(QQ(),3,3);
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
#puts [ str(g) for g in r.gens() ];
e11,e12,e13,e21,e22,e23,e31,e32,e33 = r.gens();
puts "e11 = " + str(e11);
puts "e12 = " + str(e12);
puts "e13 = " + str(e13);
puts "e21 = " + str(e21);
puts "e22 = " + str(e22);
puts "e23 = " + str(e23);
puts "e31 = " + str(e31);
puts "e32 = " + str(e32);
puts "e33 = " + str(e33);
m1 = e11 + e31;
puts "m1 = " + str(m1);
m2 = m1 * m1 + 5*e22;
puts "m2 = " + str(m2);
m3 = m2**3 - 125*e21 - 125*e23;
puts "m3 = " + str(m3);
#m4 = 1/m2;
#puts "m4 = " + str(m4);
m5 = [ [ 1, 0, 0 ], [ -125, 125, -125 ], [ 1, 0, 0 ] ]; 
puts "m5 = " + str(m5);
m6 = m3 * m5;
puts "m6 = " + str(m6);
puts;


puts "------- Mat(PolyRing(QQ(),\"x,y,z\",PolyRing.lex),3,3) ---------";
r = Mat(PolyRing.new(QQ(),"x,y,z",PolyRing.lex),3,3);
puts "r = " + str(r);
puts "r.factory() = " + str(r.factory());
#puts [ str(g) for g in r.gens() ];
for g in r.gens()
    puts "g = " + str(g);
end
puts;


puts "------- PolyRing(Mat(QQ(),3,3),\"x,y,z\",PolyRing.lex) ---------";
r = PolyRing.new(Mat(QQ(),3,3),"x,y,z",PolyRing.lex);
puts "r = " + str(r);
for g in r.gens()
    puts "g = " + str(g);
end
puts;


puts "------- SolvPolyRing(QQ(),\"x,y,z\") ---------";
r = PolyRing.new(QQ(),"x,y,z",PolyRing.lex);
puts "r = " + str(r);
pone,px,py,pz = r.gens();
puts "pone = " + str(pone);
puts "px   = " + str(px);
puts "py   = " + str(py);
puts "pz   = " + str(pz);
rel = [ py, px, px * py - 1 , pz, py, py * pz - 1 ];
#puts "rel  = " + str(rel);
sr = SolvPolyRing.new(QQ(),"x,y,z",PolyRing.lex,rel);
puts "sr = " + str(sr);
one,x,y,z = sr.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
puts "one.factory() = " + str(one.factory());
s1 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
puts "s1  = " + str(s1);
s2 = QQ(1,2) + QQ(2,3) * x + QQ(2,5) * y + ( x + y + z )**2;
puts "s2  = " + str(s2);
s3  = z**2 + 2 * y * z + 2 * x * z + y**2 + 2 * x * y + x**2 + QQ(2,5) * y + QQ(2,3) * x + QQ(1,2);
puts "s3  = " + str(s3);
s4 = s1 - s3;
puts "s4  = " + str(s4);
puts "s4.factory() = " + str(s4.factory());
sr1 = SolvPolyRing.new(QQ(),"x, y, z",PolyRing.lex,[( z ), ( y ), ( y * z - 1 ), ( y ), ( x ), ( x * y - 1 )]);
puts "sr1 = " + str(sr1);
s5 = z * y;
puts "s5  = " + str(s5);
s6 = s5**3;
puts "s6  = " + str(s6);
s7 = ( z * y )**3;
puts "s7  = " + str(s7);
s8 = s7 - s6;
puts "s8  = " + str(s8);
s9 = y**3 * z**3 - 3 * y**2 * z**2 + 3 * y * z - 1;
puts "s9  = " + str(s9);
s10 = s9 - s7;
puts "s10 = " + str(s10);
puts;


puts "------- SubModule(PolyRing(QQ(),\"u, v, l\",PolyRing.lex) ---------";
p = PolyRing.new(QQ(),"u, v, l",PolyRing.lex);
puts "p = " + str(p);
one,u,v,l = p.gens();
puts "one = " + str(one);
puts "u   = " + str(u);
puts "v   = " + str(v);
puts "l   = " + str(l);

m = CommutativeModule.new("",ring=p,cols=4);
puts "m = " + str(m);
for g in m.gens()
    puts "g =", str(g);
end


m1  = [ 0, 1, l + v, 0 ]
m2  = [ 0, v, u * l**2, 0 ]
m3  = [ 0, l + 3 * v, 0, u ]
m4  = [ 0, v * l + v**2, u**2, 0 ]
m5  = [ 0, l**2, u, 0 ]
m6  = [ 1, 0, 0, l**2 ]
m7  = [ 1, 0, l + 3 * v, 0 ]
m8  = [ 1, 2, 0, l**2 ]
m9  = [ u, 0, 0, v * l + v**2 ]
m10 = [ l + v, 0, 0, u ]
m11 = [ l**2, 0, 0, v ]
m12 = [ l**2, 0, 2 * u,v ]

ml = [m1,m2,m3,m4,m5,m6,m7,m8,m9,m10,m11,m12];
#ml= [ [ 0, 1, l + v, 0 ], [ 0, v, u * l**2, 0 ], [ 0, l + 3 * v, 0, u ], [ 0, v * l + v**2, u**2, 0 ], [ 0, l**2, u, 0 ], [ 1, 0, 0, l**2 ], [ 1, 0, l + 3 * v, 0 ], [ 1, 2, 0, l**2 ], [ u, 0, 0, v * l + v**2 ], [ l + v, 0, 0, u ], [ l**2, 0, 0, v ], [ l**2, 0, 2 * u,v ] ];
#puts "ml = " + str(ml);
#puts;


sm = m.submodul("",list=ml);
#sm = SubModule(m,list=ml);
puts "sm = " + str(sm);

xm = SubModule.new(PolyRing.new(QQ(),"u, v, l",PolyRing.lex),"",list=[ [ 0, 1, ( l + v ), 0 ], [ 0, v, u * l**2, 0 ], [ 0, ( l + 3 * v ), 0, u ], [ 0, ( v * l + v**2 ), u**2, 0 ], [ 0, l**2, u, 0 ], [ 1, 0, 0, l**2 ], [ 1, 0, ( l + 3 * v ), 0 ], [ 1, 2, 0, l**2 ], [ u, 0, 0, ( v * l + v**2 ) ], [ ( l + v ), 0, 0, u ], [ l**2, 0, 0, v ], [ l**2, 0, 2 * u, v ] ])

#    SubModule(PolyRing.new(QQ(),"u, v, l",PolyRing.lex),list=( ( 0, 1, ( l + v ), 0 ), ( 0, v, u * l**2, 0 ), ( 0, ( l + 3 * v ), 0, u ), ( 0, ( v * l + v**2 ), u**2, 0 ), ( 0, l**2, u, 0 ), ( 1, 0, 0, l**2 ), ( 1, 0, ( l + 3 * v ), 0 ), ( 1, 2, 0, l**2 ), ( u, 0, 0, ( v * l + v**2 ) ), ( ( l + v ), 0, 0, u ), ( l**2, 0, 0, v ), ( l**2, 0, 2 * u, v ) ))

#    SubModule(PolyRing.new(QQ(),"u, v, l",PolyRing.lex),list=( ( 0, (1,), l + v, 0 ), ( 0, v, u * l**2, 0 ), ( 0, l + (3,) * v, 0, u ), ( 0, v * l + v**2, u**2, 0 ), ( 0, l**2, u, 0 ), ( (1,), 0, 0, l**2 ), ( (1,), 0, l + (3,) * v, 0 ), ( (1,), (2,), 0, l**2 ), ( u, 0, 0, v * l + v**2 ), ( l + v, 0, 0, u ), ( l**2, 0, 0, v ), ( l**2, 0, (2,) * u, v ) ));

#    SubModule(PolyRing.new(QQ(),"u, v, l",PolyRing.lex),list=( ( 0, (1,), l + v, 0 ), ( 0, v, u * l**2, 0 ), ( 0, l + (3,) * v, 0, u ), ( 0, v * l + v**2, u**2, 0 ), ( 0, l**2, u, 0 ), ( (1,), 0, 0, l**2 ), ( (1,), 0, l + (3,) * v, 0 ), ( (1,), (2,), 0, l**2 ), ( u, 0, 0, v * l + v**2 ), ( l + v, 0, 0, u ), ( l**2, 0, 0, v ), ( l**2, 0, (2,) * u, v ) ));

puts "xm = " + str(xm);
puts;
#rg = sm.GB();
#puts "rg:", rg;
#puts "isGB:", rg.isGB();
#puts;



puts "------- SolvableSubModule(SolvPolyRing(CC(),\"X,Y,x,y\")) ---------";
r = PolyRing.new(CC(),"X,Y,x,y",PolyRing.lex);
puts "r = " + str(r);
pone,pi,pX,pY,px,py = r.gens();
puts "pone = " + str(pone);
puts "pi   = " + str(pi);
puts "pX   = " + str(pX);
puts "pY   = " + str(pY);
puts "px   = " + str(px);
puts "py   = " + str(py);
#rel = ( py, px, px * py - 1 , pz, py, py * pz - 1 );
rel = [ py, px, pi * px * py, pX, pY, pi * pY * pX ];
puts "rel  = " + str( rel.each { |x| str(x) });

sr = SolvPolyRing.new(CC(),"X,Y,x,y",PolyRing.lex,rel);
puts "sr = " + str(sr);
one,i,X,Y,x,y = sr.gens();
puts "one = " + str(one);
puts "i   = " + str(i);
puts "X   = " + str(X);
puts "Y   = " + str(Y);
puts "x   = " + str(x);
puts "y   = " + str(y);


m1 = [ ( x + 1 ), ( y ) ]
m2 = [ ( x * y ), ( 0 ) ]
m3 = [ ( x - X ), ( x - X ) ]
m4 = [ ( y - Y ), ( y - Y ) ]

ml = [m1,m2,m3,m4];
#puts "ml = " + str(ml);

ssm = SolvableSubModule.new( sr, "", list=ml );
puts "ssm: " + str(ssm);
puts;


xsm = SolvableSubModule.new(SolvPolyRing.new(CC(),"X, Y, x, y",PolyRing.lex,rel=[y, x, ( CC(0,1) * x * y ), Y, X, ( CC(0,1) * X * Y )]),"",list=[ [ x - X, x - X ], [ x + CC(1), y ], [ y - Y, y - Y ], [ x * y, 0 ] ]);
#     SolvableSubModule(SolvPolyRing.new(CC(),"X, Y, x, y",PolyRing.lex,rel=(y, x, ( ((0,),(1,)) * x * y ), Y, X, ( ((0,),(1,)) * X * Y )]),list=[ ( x - X, x - X ), ( x + ((1,),), y ), ( y - Y, y - Y ), ( x * y, 0 ) ));
#     SolvableSubModule(SolvPolyRing.new(CC(),"X, Y, x, y",PolyRing.lex,(( y ), ( x ), ( ((0,),(1,)) * x * y ),( Y ), ( X ), ( ((0,),(1,)) * X * Y ))),list=( ( x - X, x - X ), ( x + ((1,),), y ), ( y - Y, y - Y ), ( x * y, 0 ) ));
puts "xsm: " + str(xsm);
puts;

## mlg = ssm.leftGB();
## puts "mlg:", mlg;
## puts;

## mtg = ssm.twosidedGB();
## puts "mtg:", mtg;
## puts;

puts "------------------------------------";

#puts "globals() = " + str(globals());

terminate();

#puts "globals() = " + str(globals());
#puts "locals()  = " + str(locals());
#puts "vars()    = " + str(vars());

terminate();
__END__

to_skip = <<TOSKIP

TOSKIP
