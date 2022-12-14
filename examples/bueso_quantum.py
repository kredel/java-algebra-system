#
# jython examples for jas.
# $Id$
#

from jas import PolyRing, SolvPolyRing, QQ, RF, terminate

# Bueso, Torrecillas, Lobillo, Castro: example 4.4.9, SAC Newsletter 1996

r = PolyRing(QQ(),"q");
#is automatic: one,q = r.gens();

rq = RF(r);

print "r  = " + str(r);
print "rq = " + str(rq);

#                     f < k < l < e
rp = PolyRing(rq,"f,k,l,e", PolyRing.lex);
print "rp = " + str(rp);

relations = [ e, k,  one/(q**2) * k * e,
              e, l,  q**2 * l * e,
              k, f,  one/(q**2) * f * k,
              l, f,  q**2 * f * l,
              e, f,  f * e + (k**2 - l**2)/(q**2 - one/(q**2))
            ];
#commutative  l, k, k * l

print "relations = ", [ str(f) for f in relations ];


rs = SolvPolyRing(rq, "f,k,l,e", PolyRing.lex, relations);
print "rs: " + str(rs);

print "gens() = ", [ str(f) for f in rs.gens() ];


p = k * l - 1;
pl = [p];

ts = rs.ideal("", pl);
print "ts = " + str(ts);

tsg = ts.twosidedGB();
print "twosided GB: " + str(tsg);
print "isTwosidedGB: " + str(tsg.isTwosidedGB());

d = tsg.dimension();
print "d = " + str(d);

print;
terminate();

#exit;
