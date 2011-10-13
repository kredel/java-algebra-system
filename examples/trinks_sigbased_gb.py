#
# jython examples for jas.
# $Id$
#

from java.lang import System, Integer

from jas import PolyRing, ZZ, QQ, ZM
from jas import terminate, startLog

from basic_sigbased_gb import sigbased_gb
from basic_sigbased_gb import ggv, ggv_first_implementation
from basic_sigbased_gb import coeff_free_sigbased_gb
from basic_sigbased_gb import arris_algorithm, min_size_mons
from basic_sigbased_gb import f5, f5z

#print "Signature based GBs"

from staggered_linear_basis import staglinbasis


#r = PolyRing( QQ(), "(B,S,T,Z,P,W)", PolyRing.lex );
r = PolyRing( ZM(32003), "(B,S,T,Z,P,W)", PolyRing.lex );
#r = PolyRing( ZM(19), "(B,S,T,Z,P,W)", PolyRing.lex );
print "Ring: " + str(r);
print;

[one,B,S,T,Z,P,W] = r.gens();

p1 = 45 * P + 35 * S - 165 * B - 36; 
p2 = 35 * P + 40 * Z + 25 * T - 27 * S;
p3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2; 
p4 = -9 * W + 15 * T * P + 20 * S * Z; 
p5 = P * W + 2 * T * Z - 11 * B**3;
p6 = 99 * W - 11 * B * S + 3 * B**2;
p7 = 10000 * B**2 + 6600 * B + 2673;

F = [p1,p2,p3,p4,p5,p6,p7];
#F = [p1,p2,p3,p4,p5,p6];

f = r.ideal( list=F );
print "Ideal: " + str(f);
print;

#startLog();

rg = f.GB();
rg = f.GB();
if not rg.isGB():
    print "seq Output:", rg;
    print;

#-------------------

sbgb = sigbased_gb();
arri = arris_algorithm();
arrm = min_size_mons();
ggv  = ggv();
ggv1 = ggv_first_implementation();
f5  = f5();
ff5 = f5z();

if True:
    gg = staglinbasis(F);
    gg = staglinbasis(F);
    t = System.currentTimeMillis();
    gg = staglinbasis(F);
    t = System.currentTimeMillis() - t;
    print "stag executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "stag Output:" + str([ str(ggg) for ggg in gg]);
    print;

if True:
    gg = sbgb.basis_sig(F);
    t = System.currentTimeMillis();
    gg = sbgb.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "sbgb executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "sbgb Output:" + str([ str(ggg) for ggg in gg]);
    print;

if True:
    gg = ff5.basis_sig(F);
    t = System.currentTimeMillis();
    gg = ff5.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "f5   executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "f5   Output:" + str([ str(ggg) for ggg in gg]);
    print;

if True:
    gg = ggv1.basis_sig(F);
    t = System.currentTimeMillis();
    gg = ggv1.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "ggv  executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "ggv  Output:" + str([ str(ggg) for ggg in gg]);
    print;

if True:
    gg = arri.basis_sig(F);
    t = System.currentTimeMillis();
    gg = arri.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "arri executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "arri Output:" + str([ str(ggg) for ggg in gg]);
    print;

