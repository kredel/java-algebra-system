#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: ideal prime decomposition
# TRANSACTIONS OF THE AMERICAN MATHEMATICAL SOCIETY
# Volume 296, Number 2. August 1986
# ON THE DEPTH OF THE SYMMETRIC ALGEBRA
# J. HERZOG M. E. ROSSI AND G. VALLA

#r = PolyRing.new(QQ(),"x,t,z,y",PolyRing.lex);
#r = PolyRing.new(QQ(),"x,y,t,z",PolyRing.lex);
#r = EF.new(QQ()).extend("x").polynomial("y,z,t").build(); #,PolyRing.lex);
#c = PolyRing.new(QQ(),"t,y",PolyRing.lex);

#c = PolyRing.new(ZM(32003,0,True),"t",PolyRing.lex);
#c = PolyRing.new(GF(32003),"t",PolyRing.lex);
c = PolyRing.new(ZZ(),"t",PolyRing.lex);
r = PolyRing.new(RF(c),"z,y,x",PolyRing.lex);
puts "Ring: " + str(r);
puts;

#automatic: one,t,z,y,x = r.gens();
puts "one = " + str(one);
puts "x   = " + str(x);
puts "y   = " + str(y);
puts "z   = " + str(z);
puts "t   = " + str(t);

f1 = x**3 - y**7;
f2 = x**2 * y - x * t**3 - z**6;
f3 = z**2 - t**3;
#f3 = z**19 - t**23;

puts "f1 = " + str(f1);
puts "f2 = " + str(f2);
puts "f3 = " + str(f3);
puts;

F = r.ideal( "", list=[f1,f2,f3] );
puts "F = " + str(F);
puts;

#sys.exit();
startLog();

t = System.currentTimeMillis();
P = F.radicalDecomp();
#P = F.primeDecomp();
t1 = System.currentTimeMillis() - t;
puts "P = " + str(P);
puts;
puts "prime/radical decomp time = " + str(t1) + " milliseconds";
puts;

puts "F = " + str(F);
puts;

#startLog();
terminate();
