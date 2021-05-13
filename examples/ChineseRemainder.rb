#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: Chines remainder theorem for polynomials

#r = PolyRing.new( ZM(1152921504606846883), "(x,y,z)", PolyRing.lex );
#r = PolyRing.new( CC(), "(x,y,z)", PolyRing.lex );
##r = PolyRing.new( ZZ(), "(x,y,z)", PolyRing.lex );
r = PolyRing.new( QQ(), "x,y,z", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#one,x,y,z = r.gens();

a = r.random(3,4);
b = r.random(2,3);
c = r.random(3,3).abs();

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts;

ff = [[a,b,c], [a+1,b,c], [a,b+1,c], [a,b,c+1]];
puts "ff = " + str(ff.map{ |f| f.map{ |a| a.to_s } });
puts;

ar = x;
br = 3*one;
cr = z - y;
dr = 27*one;
rr = [ar, br, cr, dr];
puts "ff = " + str(rr.map{ |f| f.to_s });
puts;

t = System.currentTimeMillis();
dd = r.CRT("", ff, rr);
t = System.currentTimeMillis() - t;

puts "if existing, Chinese remainder = " + str(dd);
puts;

puts "Chinese remainder time = " + str(t) + " milliseconds" ;
puts;

#cc = a + b * c;
#tt = r.subringmember(dd, cc);
#puts "sub ring member = " + str(tt);
#puts;

#startLog();
terminate();
