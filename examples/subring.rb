#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: subring

#r = PolyRing.new( ZM(1152921504606846883), "(x,y,z)", PolyRing.lex );
#r = PolyRing.new( CC(), "(x,y,z)", PolyRing.lex );
#r = PolyRing.new( ZZ(), "(x,y,z)", PolyRing.lex );
r = PolyRing.new( QQ(), "x,y,z", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#one,x,y,z = r.gens();

a = r.random(3,4);
b = r.random(3,4);
c = r.random(3,3).abs();

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts;

t = System.currentTimeMillis();
dd = r.subring("", [a,b,c]);
t = System.currentTimeMillis() - t;

puts "sub ring = " + str(dd.map{ |a| a.elem.to_s });
puts;

puts "subring time = " + str(t) + " milliseconds" ;
puts;

cc = a + b * c;
tt = r.subringmember(dd, cc);
puts "sub ring member = " + str(tt);
puts;

#startLog();
terminate();
