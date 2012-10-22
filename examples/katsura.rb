#
# jruby examples for jas.
# $Id$

require "examples/jas"

include_class "edu.jas.gb.Katsura";

# katsura examples

knum = 4
tnum = 2;

#r = Ring( "Mod 19 (B,S,T,Z,P,W) L" );
#r = Ring( "Mod 1152921504606846883 (B,S,T,Z,P,W) L" ); # 2^60-93
#r = Ring( "Quat(B,S,T,Z,P,W) L" );
#r = Ring( "Z(B,S,T,Z,P,W) L" );
#r = Ring( "C(B,S,T,Z,P,W) L" );
#r = Ring( "Rat(B,S,T,Z,P,W) L" );
#puts "Ring: " + str(r);
#puts;

k = Katsura.new(knum);
r = Ring.new( k.varList("Rat","G") );
#r = Ring.new( k.varList("Mod 23","G") );
#r = Ring.new( k.varList("Mod 32003","G") );
#r = Ring.new( k.varList("Mod 536870909","G") );
#r = Ring.new( k.varList("Mod 4294967291","G") );
#r = Ring.new( k.varList("Mod 9223372036854775783","G") );
#r = Ring.new( k.varList("Mod 170141183460469231731687303715884105727","G") );
#no?#r = Ring.new( k.varList("Mod 1152921504606846883","G") );
puts "Ring: " + str(r);
puts;

ps = k.polyList();

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


# rg = f.distGB(2);
#puts "dist Output:", rg;
#puts;

#f.distClient();


