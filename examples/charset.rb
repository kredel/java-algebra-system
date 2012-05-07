#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# characteristic set example Circle of Apollonius, from CLO IVA

#r = PolyRing.new( QQ(),"u1,u2,u3,u4,x1,x2,x3,x4,x5,x6,x7,x8",PolyRing.lex );
r = PolyRing.new( QQ(),"u1,u2,x1,x2,x3,x4,x5,x6,x7,x8",PolyRing.lex );
puts "Ring: " + str(r);
puts;

#one,u1,u2,u3,u4,x1,x2,x3,x4,x5,x6,x7,x8 = r.gens();
#one,u1,u2,x1,x2,x3,x4,x5,x6,x7,x8 = r.gens();
#h3 = 2 * x3 - u3; typo
#h4 = 2 * x4 - u4;

h1 = 2 * x1 - u1;
h2 = 2 * x2 - u2;
h3 = 2 * x3 - u1;
h4 = 2 * x4 - u2;
h5 = u2 * x5 + u1 * x6 - u1 * u2;
h6 = u1 * x5 - u2 * x6;
h7 = x1**2 - x2**2 - 2 * x1 * x7 + 2 * x2 * x8;
h8 = x1**2 - 2 * x1 * x7 - x3**2 + 2 * x3 * x7 - x4**2 + 2 * x4 * x8;

g = ( ( x5 - x7 )**2 + ( x6 - x8 )**2 - ( x1 - x7 )**2 - x8**2 );

L = [h1,h2,h3,h4,h5,h6,h7,h8];
#print "L = ", str(L);

f = r.ideal( "", L );
puts "Ideal: " + str(f);
puts;

startLog();

cc = f.CS();
puts "seq char set: " + str(cc);
puts "is char set: " + str(cc.isCS());
puts;

puts "g: " + str(g);
puts;

h = cc.csReduction(g);
puts "h: " + str(h);
puts;

#sys.exit();
