#
# jruby examples for jas.
# $Id$

require "examples/jas"

java_import "edu.jas.gb.Cyclic";

# cyclic examples

knum = 5;
tnum = 2;

k = Cyclic.new(knum);
#r = Ring.new( "", k.ring );
#r = PolyRing.new( GF(23), k.ring.vars,  Order::IGRLEX );
r = PolyRing.new( GF(32003), k.ring.vars,  Order::IGRLEX );
#r = PolyRing.new( GF(536870909), k.ring.vars,  Order::IGRLEX );
#r = PolyRing.new( GF(4294967291), k.ring.vars,  Order::IGRLEX );
#r = PolyRing.new( GF(9223372036854775783), k.ring.vars,  Order::IGRLEX );
#r = PolyRing.new( GF(170141183460469231731687303715884105727), k.ring.vars,  Order::IGRLEX );
puts "Ring: " + str(r);
puts;

ps = k.polyList();
#ps = k.cyclicPolys();
puts "ps : " + ps.to_s;
puts;

#exit();

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

#puts "Range: " + Range.new(tnum,2,-1).to_s;
#puts "Range: " + (tnum...2).to_s;
#puts;

rg = f.parGB(tnum);
for th in tnum.downto(2) do 
   rg = f.parGB(th);
   #puts "par(#{th}) Output:" + rg.to_s;
   #puts;
end

rg = f.GB();
#puts "seq Output:", rg;
puts;

terminate();
