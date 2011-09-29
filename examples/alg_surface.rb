#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# polynomial examples: 

Yr = EF.new(QQ()).extend("x,y").extend("z","z^2 + x^2 + y^2 - 1").build();
#puts "Yr    = " + str(Yr);
#puts

one,x,y,z = Yr.gens();
puts "one   = " + str(one);
puts "x     = " + str(x);
puts "y     = " + str(y);
puts "z     = " + str(z);
puts;

f = (1+z)*(1-z); # / ( x**2 + y**2 );
puts "f     = " + str(f);
puts;

g = f / (1 - z); 
puts "g     = " + str(g);
puts;

#startLog();
terminate();
