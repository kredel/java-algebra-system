#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: complex roots over Q

r = PolyRing.new(CR(QQ()),"x",PolyRing.lex);
puts "Ring: " + str(r);
puts;

one,I,x = r.gens();
puts "one = " + one.to_s;
puts "I   = " + I.to_s;
puts "x   = " + x.to_s;
puts;


f1 = x**3 - 2;

f2 = ( x - I ) * ( x + I ) * ( x - 2 * I ) * ( x + 1/2 * I );

f3 = ( x**3 - 2 * I );

#f = f1 * f2 * f3;
#f = f1 * f2;
#f = f1 * f3;
#f = f2 * f3;
f = f2;

puts "f = " + f.to_s;
puts;

startLog();

t = System.currentTimeMillis();
rr = r.complexRoots(f);
t = System.currentTimeMillis() - t;
#puts "rr = " + str(rr);
puts "rr = " + str(rr.map{ |a| str(a.elem.ring.getRoot()) }); 
puts "complex roots time = " + str(t) + " milliseconds";

#eps = QQ(1,10) ** (BigDecimal::DEFAULT_PRECISION-3);
eps = QQ(1,10) ** 10;
puts "eps = " + str(eps);

t = System.currentTimeMillis();
rr = r.complexRoots(f,eps);
t = System.currentTimeMillis() - t;
#puts "rr = ", [ str(r) for r in rr ];
puts "rr = " + str(rr.map{ |a| str(a.elem.decimalMagnitude()) }); 
puts "complex roots refinement time = " + str(t) + " milliseconds";

#startLog();
terminate();
