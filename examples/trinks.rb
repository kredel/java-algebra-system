#
# jruby examples for jas.
# $Id$
#

load "examples/jas.rb"

#startLog();

#from jas import Ring
#from jas import Ideal
#from jas import startLog
#from jas import terminate

# trinks 6/7 example

#r = Ring.new( "Mod 19 (B,S,T,Z,P,W) L" );
#r = Ring.new( "Mod 1152921504606846883 (B,S,T,Z,P,W) L" ); # 2^60-93
#r = Ring.new( "Quat(B,S,T,Z,P,W) L" );
#r = Ring.new( "Z(B,S,T,Z,P,W) L" );
#r = Ring.new( "C(B,S,T,Z,P,W) L" );
r = Ring.new( "Rat(B,S,T,Z,P,W) L" );
puts "Ring: " + r.to_s;
puts;


ps = """
( 
 ( 45 P + 35 S - 165 B - 36 ), 
 ( 35 P + 40 Z + 25 T - 27 S ), 
 ( 15 W + 25 S P + 30 Z - 18 T - 165 B**2 ), 
 ( - 9 W + 15 T P + 20 S Z ), 
 ( P W + 2 T Z - 11 B**3 ), 
 ( 99 W - 11 B S + 3 B**2 ),
 ( 10000 B**2 + 6600 B + 2673 )
) 
""";

# ( 10000 B**2 + 6600 B + 2673 )
# ( B**2 + 33/50 B + 2673/10000 )

#f = Ideal.new( r, ps );
#puts "Ideal: " + str(f);
#puts;

f = r.ideal( ps );
puts "Ideal: " + f.to_s;
puts;

#startLog();

rg = f.GB();
#puts "seq Output:", rg;
#puts;

rg = f.parGB(2);
#puts "par Output:", rg;
#puts;

f.distClient(); # starts in background
rg = f.distGB(2);
#puts "dist Output:", rg;
#puts;

terminate();

exit(0); # required because of distClient

