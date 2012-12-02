#
# jruby examples for jas.
# $Id$
#

#load "examples/jas.rb"
require "examples/jas"


# trinks 6/7 example

# QQ = rational numbers, ZZ = integers, CC = complex rational numbers, GF = finite field
#r = PolyRing.new( GF(19),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing.new( GF(1152921504606846883),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing.new( GF(2**60-93),"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing.new( CC,"B,S,T,Z,P,W", PolyRing.lex);
#r = PolyRing.new( ZZ,"B,S,T,Z,P,W", PolyRing.lex); 
r = PolyRing.new( QQ,"B,S,T,Z,P,W", PolyRing.lex);
puts "Ring: " + r.to_s;
puts;

# sage like: with generators for the polynomial ring
one,B,S,T,Z,P,W = r.gens(); # capital letter variables not automaticaly included
#one,I,B,S,T,Z,P,W = r.gens(); 

f1 = 45 * P + 35 * S - 165 * B - 36;
f2 = 35 * P + 40 * Z + 25 * T - 27 * S;
f3 = 15 * W + 25 * S * P + 30 * Z - 18 * T - 165 * B**2;
f4 = - 9 * W + 15 * T * P + 20 * S * Z;
f5 = P * W + 2 * T * Z - 11 * B**3;
f6 = 99 * W - 11 *B * S + 3 * B**2;
f7 = 10000 * B**2 + 6600 * B + 2673;
#f7 = B**2 + 33/50 * B + 2673/10000; # fractions work with ruby
#puts "f1 = " + f1.to_s;

F = [ f1, f2, f3, f4, f5, f6, f7 ]; # smaller, faster
#F = [ f1, f2, f3, f4, f5, f6 ]; # bigger, needs more time
puts "F = " + F.map { |f| f.to_s }.join(",");
puts

f = r.ideal( "", F );
puts "Ideal: " + f.to_s;
puts;

#startLog();

rg = f.GB();
puts "seq Output:", rg;
puts;

rg = f.parGB(2);
puts "par Output:", rg;
puts;

terminate();
return # if using ZZ coefficients

f.distClient(); # starts in background, needs socket permission
rg = f.distGB(2);
#puts "dist Output:", rg;
#puts;

f.distClientStop(); # stops them
terminate();

