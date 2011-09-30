#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: absolute factorization over Q(i)

Qr = PolyRing.new(QQ(),"i",PolyRing.lex);
puts "Qr    = " + str(Qr);
e,a = Qr.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
imag = a**2 + 1;
puts "imag  = " + str(imag);
Qi = AN(imag,true);
puts "Qi    = " + str(Qi.factory());
one,i = Qi.gens();
puts "one   = " + str(one);
puts "i     = " + str(i);
puts;


r = PolyRing.new(Qi,"x",PolyRing.lex)
puts "r    = " + str(r);

one,i,x = r.gens();
puts "one   = " + str(one);
puts "i     = " + str(i);
puts "x     = " + str(x);
puts;


#f = x**7 - 1;
#f = x**6 + x**5 + x**4 + x**3 + x**2 + x + 1;
#f = x**2 - i;
#f = x**3 - i;
#f = x**2 + 1;
#f = x**5 - 1;
#f = x**4 + x**3 + x**2 + x + (1,);
#f = ( x**2 - i - 1 ) * ( x**2 + i + 1 );
f = ( x**2 - i ) * ( x**2 + i + 1 );

puts "f = " + str(f);
puts;

#startLog();

t = System.currentTimeMillis();
g = r.factors(f);
t = System.currentTimeMillis() - t;
#puts "G = " + str(g);
#puts "factor time = " + str(t) + " milliseconds";

f2 = one;
for h, i in g
    puts "h**i = ("  + str(h) + ")**" + str(i);
    h = h**i;
    f2 = f2*h;
end
#puts "f2 = " + str(f2);
puts;

puts "factor time = " + str(t) + " milliseconds, " + "isFactors(f,g): " + str( f==f2 );
puts;


startLog();

t = System.currentTimeMillis();
g = r.factorsAbsolute(f);
t = System.currentTimeMillis() - t;
puts "G = ", g.toScript();
puts
puts "factor time = " + str(t) + " milliseconds";
puts

#sys.exit();
#startLog();

terminate();
