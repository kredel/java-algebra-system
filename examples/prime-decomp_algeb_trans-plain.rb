#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

#startLog();

# polynomial examples: prime / primary decomposition in Q[w2,x,wx,y,z]

Q = PolyRing.new(QQ(),"w2,x,wx,y,z",PolyRing.lex);
puts "Q     = " + str(Q);
e,w2,x,wx,y,z = Q.gens();
puts "e     = " + str(e);
puts "w2    = " + str(w2);
puts "x     = " + str(x);
puts "wx    = " + str(wx);
puts "y     = " + str(y);
puts "z     = " + str(z);

w1 = w2**2 - 2;
w2 = wx**2 - x;
f1 = ( y**2 - x ) * ( y**2 - 2 );
#f1 = ( y**2 - x ) * ( y**2 - 2 )**2;
f2 = ( z**2 - y**2 ); 

puts "w1 = " + str(w1);
puts "w2 = " + str(w2);
puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts;

#sys.exit();

startLog();

F = Q.ideal( "", [w1,w2,f1,f2] );

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
