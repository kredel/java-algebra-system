#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# finite fields and algebraic field extensions

startLog();

#puts "------- finite field GF(17,5) ---------";
r = FF(17,5);
puts "r     = " + str(r.ring.toScript);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);

ap = a**5 - a + 1;
puts "ap    = " + str(ap);
ai = 1/ap;
puts "ai    = " + str(ai) + ", ai*ap = " + str(ai*ap);

q = FF(17,5);
puts "q     = " + str(q.ring.toScript);
qe,qa = q.gens();
puts "qe    = " + str(qe);
puts "qa    = " + str(qa);

qap = qa**5 - qa + 1;
puts "qap   = " + str(qap);

s = ap - qap;
puts "s     = " + str(s);

ar = PolyRing.new(r,"beta",PolyRing.lex);
puts "ar    = " + str(ar);
e,a,b = ar.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
puts "b     = " + str(b);

p = a**5 - a + beta**5 - (a**3 + a)*beta;
#p = p*p;
puts "p     = " + str(p);
x = p.factors(); 
puts "x     = " + x.map{ |p,i| str(p)+"**"+str(i) }.join(", ");
g = x.map{ |p,i| p**i }.reduce(e, :*);
puts "g     = " + str(g);
puts "isFactors(p,x): " + str(p==g);
puts;

#mod = r.ring.modul;
#puts "mod   = " + str(mod);

pg = r.gens()[1];
#pg = RingElem.new(r.ring.modul.ring.generators()[1]);
puts "pg               = " + str(pg);
pol = pg**(17**5) - pg;
puts "pg**(17**5) - pg = " + str(pol);
pol = pg**(17**5-1);
puts "pg**(17**5-1)    = " + str(pol);
puts "17**5            = " + str(17**5);
puts

terminate();
