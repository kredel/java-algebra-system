# -*- coding: utf-8 -*-
#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# GB examples, ISSAC system challenge 1997

#r = PolyRing.new( QQ(), "w,z,y,x", PolyRing.grad );
r = PolyRing.new( ZZ(), "w,z,y,x", PolyRing.grad );
#r = PolyRing.new( ZZ(), "w,z,y,x", PolyRing.lex );
puts "Ring: " + str(r);
puts;


f1 = 8*w**2 + 5*w*x - 4*w*y + 2*w*z + 3*w + 5*x**2 + 2*x*y - 7*x*z - 7*x + 7*y**2 
 - 8*y*z - 7*y + 7*z**2 - 8*z + 8;

f2 = 3*w**2 - 5*w*x - 3*w*y - 6*w*z + 9*w + 4*x**2 + 2*x*y - 2*x*z + 7*x + 9*y**2 
 + 6*y*z + 5*y + 7*z**2 + 7*z + 5;

f3 = - 2*w**2 + 9*w*x + 9*w*y - 7*w*z - 4*w + 8*x**2 + 9*x*y - 3*x*z + 8*x 
 + 6*y**2 - 7*y*z + 4*y - 6*z**2 + 8*z + 2;

f4 = 7*w**2 + 5*w*x + 3*w*y - 5*w*z - 5*w + 2*x**2 + 9*x*y - 7*x*z + 4*x - 4*y**2
 - 5*y*z + 6*y - 4*z**2 - 9*z + 2;

ff = [f1,f2,f3,f4];

f = r.ideal( "", ff );
puts "Ideal: " + str(f);
puts;

#startLog();

#exit(0)

rg = f.GB();
puts "seq GB:", rg;
puts;

#terminate();

