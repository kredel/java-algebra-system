#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# simple factorization example

r = PolyRing.new( QQ(), "x,y,z", PolyRing.lex); # or PolyRing.grad
puts "PolyRing: " + str(r);
puts;

p = (x+y+z)**11*(x+1)**5*(y+z)**3;
#puts "p: " + p.to_s;
#puts;

f = r.factors( p );
puts "factors: " + f.map{ |k,v| k.to_s + "**" + v.to_s }.join(", ");
puts;

mp = f.map{ |k,v| k**v }.reduce(:*)
puts "p == mp: " + (p==mp).to_s
puts

startLog()
terminate()
