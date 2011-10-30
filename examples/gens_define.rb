#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"

# test auto defined generators

startLog();

r = PolyRing.new( CC(), "(b,s,t,z,p,w)", PolyRing.lex );
puts "Ring: " + r.to_s;
puts;

puts "1 = " + str(r.one);
puts "I = " + str(r.I); 
puts "b = " + str(r.b);
puts "s = " + str(r.s);
puts "t = " + str(r.t);
puts "z = " + str(r.z);
puts "p = " + str(r.p);
puts "w = " + str(r.w);
puts;

x = r.b * r.w + ( r.s - r.t + r.one )**2 + r.z**5;
puts "x = " + str(x**2);
puts
