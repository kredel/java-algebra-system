#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# polynomial examples: prime / primary decomposition in Q(sqrt(2))(x)(sqrt(x))[y,z]

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


Yr = PolyRing.new(Q2x,"y,z",PolyRing.lex)
puts "Yr    = " + str(Yr);

e,w2,x,wx,y,z = Yr.gens();
puts "e     = " + str(e);
puts "w2    = " + str(w2);
puts "x     = " + str(x);
puts "wx    = " + str(wx);
puts "y     = " + str(y);
puts "z     = " + str(z);
puts;

f1 = ( y**2 - x ) * ( y**2 - 2 );
#f1 = ( y**2 - x ) * ( y**2 - 2 )**2;
f2 = ( z**2 - y**2 ); 

puts "f1 = ", f1;
puts "f2 = ", f2;
puts;

#sys.exit();

startLog();

F = Yr.ideal( "", [f1,f2] );

puts "F = ", F;
puts;

#sys.exit();

t = System.currentTimeMillis();
P = F.primeDecomp();
#P = F.primaryDecomp();
t1 = System.currentTimeMillis() - t;
puts "P = ", P;
puts;
puts "prime/primary decomp time = " + str(t1) + " milliseconds";
puts;

puts "F = ", F;
puts;

#startLog();
terminate();
