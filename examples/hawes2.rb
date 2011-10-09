#
# jruby examples for jas.
# $Id$
#
## \begin{PossoExample}
## \Name{Hawes2}
## \Parameters{a;b;c}
## \Variables{x;y[2];z[2]}
## \begin{Equations}
## x+2y_1z_1+3ay_1^2+5y_1^4+2cy_1 \&
## x+2y_2z_2+3ay_2^2+5y_2^4+2cy_2 \&
## 2 z_2+6ay_2+20 y_2^3+2c \&
## 3 z_1^2+y_1^2+b \&
## 3z_2^2+y_2^2+b \&
## \end{Equations}
## \end{PossoExample}

require "examples/jas"

#startLog();

# Hawes & Gibson example 2
# rational function coefficients

#r = Ring.new( "RatFunc(a, c, b) (y2, y1, z1, z2, x) G" );
r = PolyRing.new( RF(PolyRing.new(ZZ(),"a, c, b",PolyRing.lex)), "y2, y1, z1, z2, x", PolyRing.grad );
puts "Ring: " + str(r);
puts;

ps = """
(
 ( x + 2 y1 z1 + { 3 a } y1^2 + 5 y1^4 + { 2 c } y1 ),
 ( x + 2 y2 z2 + { 3 a } y2^2 + 5 y2^4 + { 2 c } y2 ), 
 ( 2 z2 + { 6 a } y2 + 20 y2^3 + { 2 c } ), 
 ( 3 z1^2 + y1^2 + { b } ), 
 ( 3 z2^2 + y2^2 + { b } ) 
) 
""";

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

rg = f.GB();
rg = f.GB();
puts "GB: " + str(rg);
#puts "GB: " + str(rg.pset);
puts;

bg = rg.isGB();
puts "isGB: " + str(bg);
puts;

startLog();
terminate();
#sys.exit();

