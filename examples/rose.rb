#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# example from rose (modified)

#r = Ring.new( "Mod 19 (U3,U4,A46) L" );
#r = Ring.new( "Mod 1152921504606846883 (U3,U4,A46) L" ); # 2^60-93
#r = Ring.new( "Quat(U3,U4,A46) L" );
#r = Ring.new( "Z(U3,U4,A46) L" );
#r = Ring.new( "C(U3,U4,A46) L" );

r = Ring.new( "Rat(A46,U3,U4) G" );
puts "Ring: " + str(r);
puts;

ps = """
(   
 ( U4^4 - 20/7 A46^2 ), 
 ( A46^2 U3^4 + 7/10 A46 U3^4 + 7/48 U3^4 - 50/27 A46^2 - 35/27 A46 - 49/216 ), 
 ( A46^5 U4^3 + 7/5 A46^4 U4^3 + 609/1000 A46^3 
 U4^3 + 49/1250 A46^2 U4^3 - 27391/800000 A46 U4^3 
 - 1029/160000 U4^3 + 3/7 A46^5 U3 U4^2 + 3/5 A46^6 
 U3 U4^2 + 63/200 A46^3 U3 U4^2 + 147/2000 A46^2 
 U3 U4^2 + 4137/800000 A46 U3 U4^2 - 7/20 A46^4 
 U3^2 U4 - 77/125 A46^3 U3^2 U4 - 23863/60000 A46^2 
 U3^2 U4 - 1078/9375 A46 U3^2 U4 - 24353/1920000 
  U3^2 U4 - 3/20 A46^4 U3^3 - 21/100 A46^3 U3^3 
 - 91/800 A46^2 U3^3 - 5887/200000 A46 U3^3 
 - 343/128000 U3^3 ) 
) 
""";

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

#startLog();

rg = f.GB();
#puts "seq Output:", rg;
#puts;

#exit();

rg = f.parGB(2);
#puts "par Output:", rg;
#puts;

f.distClient(); # starts in background
rg = f.distGB(2);
#puts "dist Output:", rg;
#puts;

exit();


