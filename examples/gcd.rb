#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: gcd

#r = PolyRing.new( ZM(1152921504606846883), "(x,y,z)", PolyRing.lex );
r = PolyRing.new( QQ(), "(x,y,z)", PolyRing.lex );
#r = PolyRing.new( CC(), "(x,y,z)", PolyRing.lex );
#r = PolyRing.new( ZZ(), "(x,y,z)", PolyRing.lex );

puts "Ring: " + str(r);
puts;

one,x,y,z = r.gens();

a = r.random();
b = r.random();
c = r.random().abs();
#c = 1; 
#a = 0;

puts "a = " + str(a);
puts "b = " + str(b);
puts "c = " + str(c);
puts;

ac = a * c;
bc = b * c;

puts "ac = " + str(ac);
puts "bc = " + str(bc);
puts;

#startLog();

t = System.currentTimeMillis();
d = r.gcd(ac,bc);
t = System.currentTimeMillis() - t;

#d = d.monic();
puts "d = " + str(d);
puts "d = " + str(d.monic());
puts;

puts "gcd time = " + str(t) + " milliseconds" ;

if r.ring.coFac.isField()
   m = c % d;
   #puts "m = " + str(m);
   puts;
   if m.isZERO()
      puts "isGcd(c,d): true" ;
   else
      puts "isGcd(c,d): " + str(m);
   end
end
puts;


#startLog();
terminate();
