#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: factorization over Q(i)

#r = Ring( "AN[ (i) (i^2 + 1) ] (x) L" );
#r = Ring( "AN[ (a) (4 a^2 + 1) ] (x) L" );
#r = Ring( "AN[ (a) (a^4 + 2 a^2 - 4 a + 2) ] (x) L" );

#puts "Ring: " + str(r);
#puts;

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
b = i**2 + 1;
puts "b     = " + str(b);
c = 1 / i;
puts "c     = " + str(c);
#Qix = AN(i**2 + 1);
#puts "Qix   = " + str(Qix.factory());
puts;

r = PolyRing.new(Qi,"x",PolyRing.lex)
puts "r    = " + str(r);

one,i,x = r.gens();
puts "one   = " + str(one);
puts "i     = " + str(i);
puts "x     = " + str(x);

#f = x**15 - 1;
#f = x * ( x + 1 )**2 * ( x**2 + x + 1 )**3;
#f = x**6 - 3 * x**5 + x**4 - 3 * x**3 - x**2 - 3 * x+ 1;
#f = x**(3*11) + 3 * x**(2*11) - x**(11);
#f = x**(3*11*11) + 3 * x**(2*11*11) - x**(11*11);
#f = x**(3*11*11*11) + 3 * x**(2*11*11*11) - x**(11*11*11);
#f = (x**2+1)*(x-3)*(x-5)**3;
#f = x**4 + 1;
#f = x**12 + x**9 + x**6 + x**3 + 1;
#f = x**24 - 1;
#f = x**20 - 1;
#f = x**22 - 1;
#f = x**8 - 40 * x**6 + 352 * x**4 - 960 * x**2 + 576;
#f = 362408718672000 * x**9 + 312179013226080 * x**8 - 591298435728000 * x**6 - 509344705789920 * x**5 - 1178946881112000 * x**2 - 4170783473878580 * x - 2717923400363451;

#f = 292700016000 * x**8 + 614670033600 * x**7 - 417466472400 * x**6 - 110982089400 * x**5 + 1185906158780 * x**4 - 161076194335 * x**3 + 204890011200 * x**2 - 359330651400 * x - 7719685302;

#f = x**10 - 212 * x**9 - 1760 * x**8 + 529 * x**7 - 93699 * x**6 - 726220 * x**5 + 37740 * x**4 + 169141 * x**3 + 24517680 * x**2 - 9472740;

#f = x**4 + 1;

#f = x**3 - x**2 + x - 1;

#f = x**8 + 4 * x**6 + 8 * x**4 - 8 * x**2 + 4;

f = x**6 - 5 * x**4 + 5 * x**2 + 4;
# ==
f = ( x**3 + 2 * i * x**2 - 3 * x - 4 * i ) * ( x**3 - 2 * i * x**2 - 3 * x + 4 * i );
#f = ( x**3 + ( 2 * i ) * x**2 - ( 3 ) * x - ( 4 * i ) ) * ( x**3 - ( 2 * i ) * x**2 - ( 3 ) * x + ( 4 * i ) );

#f = x**16 + 272 * x**12 - 7072 * x**8 + 3207424 * x**4 + 12960000;
#f = x**16 + 16 * x**12 + 96 * x**8 + 256 * x**4 + 256;
#f = x**24 + 272 * x**20 - 7072 * x**16 + 3207424 * x**12 + 12960000 * x**8;

puts "f = " + str(f);
puts;

startLog();

t = System.currentTimeMillis();
#G = r.squarefreeFactors(f);
G = r.factors(f);
t = System.currentTimeMillis() - t;
puts "#G = " + str(G.size);
#puts "factor time =", t, "milliseconds";
puts;

g = one;
for h, i in G
    puts "h**i = (" + str(h) + ")**" + str(i);
    h = h**i;
    g = g*h;
end
#puts "g = ", g;
puts;

puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): " + str(f==g);
puts;

#startLog();
terminate();
