#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# mark, d-gb diplom example, due to kandri-rody 1984
#
# The MAS DIIPEGB implementation contains an error because the output e-GB
# is not correct. Also the cited result from k-r contains this error.
# The polynomial 
#
# ( 2 x * y^2 - x^13 + 2 x^11 -   x^9 + 2 x^7 - 2 x^3 ),
#
# is in the DIIPEGB output, but it must be
#
# ( 2 x * y^2 - x^13 + 2 x^11 - 3 x^9 + 2 x^7 - 2 x^3 ),
#
# Test by adding the polynomials to the input.
# Frist polynomial produces a different e-GB. 
# Second polynomial reproduces the e-GB with the second polynomial. 

#r = Ring.new( "Z(x,y) L" );
r = PolyRing.new( ZZ(), "(x,y)", PolyRing.lex );
puts "Ring: " + str(r);
puts;

ps = """
( 
 ( y**6 + x**4 y**4 - x**2 y**4 - y**4 - x**4 y**2 + 2 x**2 y**2 + x**6 - x**4 ),
 ( 2 x**3 y**4 - x y**4 - 2 x**3 y**2 + 2 x y**2 + 3 x**5 - 2 x** 3 ),
 ( 3 y**5 + 2 x**4 y**3 - 2 x**2 y**3 - 2 y**3 - x**4 y + 2 x**2 y )
) 
""";

f = r.ideal( ps );
puts "Ideal: " + str(f);
puts;

#startLog();

eg = f.eGB( );
puts "seq e-GB: " + str(eg);
puts "is e-GB: " + str(eg.isGB());
puts;


#startLog();

dg = f.dGB();
puts "seq d-GB: " + str(dg);
puts "is d-GB: " + str(dg.isGB());
puts;

puts "d-GB == e-GB: "+ str(eg===dg);
puts
