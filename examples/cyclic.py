#
# jython examples for jas.
# $Id$
#

from java.lang import System

from jas import Ring, RingElem, Ideal, PolyRing, GF, Order;

from basic_sigbased_gb import ggv_first_implementation, arris_algorithm, f5z;

from staggered_linear_basis import staglinbasis

from edu.jas.gb import Cyclic;

# cyclic examples

knum = 5;
tnum = 2;

k = Cyclic(knum);
#r = Ring( "", k.ring );
#r = PolyRing( GF(23), k.ring.vars,  Order.IGRLEX );
r = PolyRing( GF(32003), k.ring.vars,  Order.IGRLEX );
#r = PolyRing( GF(536870909), k.ring.vars,  Order.IGRLEX );
#r = PolyRing( GF(4294967291), k.ring.vars,  Order.IGRLEX );
#r = PolyRing( GF(9223372036854775783), k.ring.vars,  Order.IGRLEX );
#r = PolyRing( GF(170141183460469231731687303715884105727), k.ring.vars,  Order.IGRLEX );


print "Ring: " + str(r);
print;

ps = k.polyList();
#ps = k.cyclicPolys();
print "ps = " + str(ps);

f = r.ideal( ps );
print "Ideal: " + str(f);
print;

if False:
    rg = f.parGB(tnum);
    for th in range(tnum,0,-1):
       rg = f.parGB(th);
       #print "par Output:", rg;
       #print;

rg = f.GB();
#print "seq Output:", rg;
print;

arri = arris_algorithm();
ggv1  = ggv_first_implementation();
ff5  = f5z();

F = [ RingElem(e) for e in f.list ];
print "F:", str([ str(e) for e in F]);
print;

if False:
    ##gg = staglinbasis(F);
    #gg = staglinbasis(F);
    t = System.currentTimeMillis();
    gg = staglinbasis(F);
    t = System.currentTimeMillis() - t;
    print "stag executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "stag no GB";
    print "stag Output:" + str([ str(ggg) for ggg in gg]);
    print;

if False:
    #gg = ff5.basis_sig(F);
    t = System.currentTimeMillis();
    gg = ff5.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "f5   executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "f5 no GB";
    print "f5   Output:" + str([ str(ggg) for ggg in gg]);
    print;

if True:
    #gg = ggv1.basis_sig(F);
    t = System.currentTimeMillis();
    gg = ggv1.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "ggv  executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "ggv1 no GB";
    print "ggv1 Output:" + str([ str(ggg) for ggg in gg]);
    print;

if False:
    #gg = arri.basis_sig(F);
    t = System.currentTimeMillis();
    gg = arri.basis_sig(F);
    t = System.currentTimeMillis() - t;
    print "arri executed in " + str(t) + " milliseconds";
    if not r.ideal(list=gg).isGB():
        print "arri no GB";
    print "arri Output:" + str([ str(ggg) for ggg in gg]);
    print;
