#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# matrix and polynomial examples:
# conditions for (non) commuting matrices

r = EF.new(QQ()).polynomial("a,b,c,d,e,f,g,h").build()
#r = Ring.new("", r.ring);
puts "r = " + str(r)
#puts
#one, a, b, c, d, e, f, g, h = r.gens();
puts "h = " + str(h)
puts


x = Mat(r,2,2,[[a,b],[c,d]])
y = Mat(r,2,2,[[e,f],[g,h]])
puts "x = " + str(x) + ", y = " + str(y) #+ ", x y = " + str(x*y)
puts

com = x*y - y*x
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
