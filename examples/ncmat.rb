#
# jruby examples for jas.
# $Id$
#

require "examples/jas"
require "matrix"

# matrix and polynomial examples: 
# conditions for (non) commuting matrices

r = PolyRing.new(ZZ(), "a,b,c,d,e,f,g,h")
puts "r = " + r.to_s
puts

x = Matrix[[a,b],[c,d]]
y = Matrix[[e,f],[g,h]]
puts "x = " + x.to_s + ", y = " + y.to_s
puts

com = x*y - y*x
puts "commutator = " + com.to_s
puts

ff = r.ideal("", [ com[0,0], com[0,1], com[1,0], com[1,1] ] )
puts "ff = " + ff.to_s
puts

gg = ff.GB();
puts "gg = " + gg.to_s
puts

#startLog();
#terminate();
