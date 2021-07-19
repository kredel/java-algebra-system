#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: absolute factorization over Z_p

#r = PolyRing.new( GF(19), "x", PolyRing.lex );
#r = PolyRing.new( GF(23), "x", PolyRing.lex );
r = PolyRing.new( GF(1152921504606846883), "x", PolyRing.lex );
#r = PolyRing.new(GF(32003),"(x)",PolyRing.lex);
#r = PolyRing.new(GF(65413),"(x)",PolyRing.lex);
#r = PolyRing.new(GF(536870909),"(x)",PolyRing.lex);
puts "Ring: " + str(r);
puts

#one,x = r.gens();


#f = x**4 - 1;
#f = x**3 + 1;
f = x**3 - x - 1;


puts "f = ", f;
puts;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
#G = r.factors(f);
G = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
puts "G = ", G.toScript();
puts "factor time = " + str(t) + " milliseconds";

#startLog();
terminate();
