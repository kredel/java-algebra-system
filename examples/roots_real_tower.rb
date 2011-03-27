#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: real roots tower over Q

r = EF.new(QQ()).realExtend("q","q^3 - 3", "[1,2]").realExtend("w", "w^2 - q", "[1,2]").realExtend("s", "s^5 - 2", "[1,2]").polynomial("x").build();

puts "Ring: " + str(r);
puts;

one,q,w,s,x = r.gens();


f = x**2 - w * s;

puts "f = " + f.to_s;
puts;


startLog();

t = System.currentTimeMillis();
rr = r.realRoots(f);
t = System.currentTimeMillis() - t;
#puts "rr = " + str(rr);
puts "rr = " + str(rr.map{ |a| str(a.elem.ring.getRoot())+"," }); 
puts "real roots time = " + str(t) + " milliseconds";

eps = QQ(1,10) ** (BigDecimal::DEFAULT_PRECISION);
puts "eps = " + str(eps);

t = System.currentTimeMillis();
rr = r.realRoots(f,eps);
t = System.currentTimeMillis() - t;
#puts "rr = ", [ str(r) for r in rr ];
puts "rr = " + str(rr.map{ |a| str(a.elem.decimalMagnitude())+"," }); 
puts "real roots time = " + str(t) + " milliseconds";

#startLog();
terminate();
