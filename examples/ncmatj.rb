#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# matrix and polynomial examples:
# conditions for (non) commuting matrices

r = EF.new(QQ()).polynomial("a,b,c,d,e,f,g,h").build()
puts "r = " + str(r)

#one, a, b, c, d, e, f, g, h = r.gens();
puts "h = " + str(h)
puts

x = Mat(r,2,2,[[a,b],[c,d]])
y = Mat(r,2,2,[[e,f],[g,h]])
z = Mat(r,2,2,[[one,0],[0,one]])
puts "x = " + str(x) + ", y = " + str(y) + ", z = " + str(z)
puts

com = x*y - y*x #+ z
puts "commutator = " + com.to_s
puts

ff = r.ideal("", [ com[0,0], com[0,1], com[1,0], com[1,1] ] )
puts "ff = " + str(ff)
puts

gg = ff.GB();
puts "gg = " + str(gg)
puts

#startLog();
#terminate();
