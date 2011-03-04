#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# polynomial examples: factorization over Q(sqrt(2))(x)(sqrt(x))[y]

Q = PolyRing.new(QQ(),"w2",PolyRing.lex);
puts "Q     = " + str(Q);
e,a = Q.gens();
#puts "e     = " + str(e);
puts "a     = " + str(a);

root = a**2 - 2;
puts "root  = " + str(root);
Q2 = AN(root,field=true);
puts "Q2    = " + str(Q2.factory());
one,w2 = Q2.gens();
#puts "one   = " + str(one);
#puts "w2    = " + str(w2);
puts;

Qp = PolyRing.new(Q2,"x",PolyRing.lex);
puts "Qp    = " + str(Qp);
ep,wp,ap = Qp.gens();
#puts "ep    = " + str(ep);
#puts "wp    = " + str(wp);
#puts "ap    = " + str(ap);
puts;

Qr = RF(Qp);
puts "Qr    = " + str(Qr.factory());
er,wr,ar = Qr.gens();
#puts "er    = " + str(er);
#puts "wr    = " + str(wr);
#puts "ar    = " + str(ar);
puts;

Qwx = PolyRing.new(Qr,"wx",PolyRing.lex);
puts "Qwx   = " + str(Qwx);
ewx,wwx,ax,wx = Qwx.gens();
#puts "ewx   = " + str(ewx);
puts "ax    = " + str(ax);
#puts "wwx   = " + str(wwx);
puts "wx    = " + str(wx);
puts;

rootx = wx**2 - ax;
puts "rootx = " + str(rootx);
Q2x = AN(rootx,field=true);
puts "Q2x   = " + str(Q2x.factory());
ex2,w2x2,ax2,wx = Q2x.gens();
#puts "ex2   = " + str(ex2);
#puts "w2x2  = " + str(w2x2);
#puts "ax2   = " + str(ax2);
#puts "wx    = " + str(wx);
puts;


Yr = PolyRing.new(Q2x,"y",PolyRing.lex)
puts "Yr    = " + str(Yr);

e,w2,x,wx,y = Yr.gens();
puts "e     = " + str(e);
puts "w2    = " + str(w2);
puts "x     = " + str(x);
puts "wx    = " + str(wx);
puts "y     = " + str(y);
puts;

f = ( y**2 - x ) * ( y**2 - 2 );
#f = ( y**2 - x )**2 * ( y**2 - 2 )**3;
#f = ( y**4 - x * 2 );
#f = ( y**7 - x * 2 );
#f = ( y**2 - 2 );
#f = ( y**2 - x );
#f = ( w2 * y**2 - 1 );
#f = ( y**2 - 1/x );
#f = ( y**2 - (1,2) );
#f = ( y**2 - 1/x ) * ( y**2 - (1,2) );

puts "f = ", f;
puts;

#sys.exit();

startLog();

t = System.currentTimeMillis();
G = Yr.factors(f);
t = System.currentTimeMillis() - t;
#puts "G = ", G;
#puts "factor time =", t, "milliseconds";

#exit();

puts "f    = " + str(f);
g = one;
for h, i in G do
    if i > 1 then
        puts "h**i = " + str(h) + "**" + str(i);
    else
        puts "h    = " + str(h);
    end
    h = h**i;
    g = g*h;
end
#puts "g    = " +  str(g);

if f == g then
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): true" ;
else
    puts "factor time = " + str(t) + " milliseconds," + " isFactors(f,g): " + str(f==g);
end
puts;

#startLog();
terminate();
