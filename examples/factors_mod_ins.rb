#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: factorization over Z_p, with p-th root

p = 5;
cr = PolyRing.new( GF(p), "u", PolyRing.lex );
puts "Ring: " + str(cr);
puts

#one,u = r.gens();

fu = (u**2+u+1)**p;
puts "fu = ", fu;
puts;

startLog();

t = System.currentTimeMillis();
G = cr.squarefreeFactors(fu);
#G = cr.factors(fu);
t = System.currentTimeMillis() - t;
puts "G = ", str(G.map{ |h,i| str(h)+"**"+str(i)+" " });
#puts "G = ", G;
puts "factor time = " + str(t) + " milliseconds";

qcr = RF(cr);
puts "Ring qcr: " + str(qcr.factory());

#not ok#r = PolyRing(cr,"x",PolyRing.lex );
r = PolyRing.new(qcr,"x",PolyRing.lex );
puts "Ring r: " + str(r);

#f = x**3 - u;
#f = (x - u)**3;
#f = (x - u**3)**3;
#f = (x - u**9)**3;

#f = x**p - u;
#f = (x - u)**p;

p2 = p * 2;
fu = (u**2+u+1)**p;
#f = x**p + 1/fu;
f = x**p + fu;
#f = x**p2 - fu * x**p - fu;
#f = x**p2 + x**p + 1;
#f = x**p2 + 1;

puts "f = ", f;
puts;

t = System.currentTimeMillis();
G = r.squarefreeFactors(f);
#G = r.factors(f);
t = System.currentTimeMillis() - t;
#puts "G = ", str(G.map{ |h,i| str(h)+"**"+str(i)+" " });
puts "#G = ", G.size();
puts "factor time = " + str(t) + " milliseconds";

gu = u**2+u+1;
#g = (x + 1/gu);
g = (x + gu);
puts "g    = " + str(g);
gp = g**p;
puts "g**p = " + str(gp);
puts;


#startLog();
terminate();
