#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: factorization over Q(i)(sqrt2)

Q = PolyRing.new(QQ(),"i",PolyRing.lex);
puts "Q     = " + str(Q);
e,a = Q.gens();
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

Wr = PolyRing.new(Qi,"w2",PolyRing.lex)
puts "Wr    = " + str(Wr);
e,a,b = Wr.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
puts "b     = " + str(b);
w2 = b**2 - 2;
puts "w2    = " + str(w2);
Qw2 = AN(w2,true);
puts "Qw2   = " + str(Qw2.factory());
one,i,w2 = Qw2.gens();
puts "one   = " + str(one);
puts "i     = " + str(i);
puts "w2    = " + str(w2);
puts;

Qiw2 = PolyRing.new(Qw2,"x",PolyRing.lex)
puts "Qiw2  = " + str(Qiw2);

one,i,w2,x  = Qiw2.gens();
puts "one   = " + str(one);
puts "i     = " + str(i);
puts "w2    = " + str(w2);
puts "x     = " + str(x);
puts;

#sys.exit();

f = ( x**2 + 1 ) * ( x**2 - 2 );

puts "f = " + str(f);
puts;

startLog();

t = System.currentTimeMillis();
G = Qiw2.factors(f);
t = System.currentTimeMillis() - t;
#puts "G = ", G;
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

puts "factor time = " + str(t) + " milliseconds," + "isFactors(f,g): " + str(f==g);
puts;

#startLog();
terminate();
