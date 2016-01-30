#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# examples: from Mathematica term orders

Scripting.setCAS(Scripting::CAS::Math);
#Scripting.setCAS(Scripting::CAS::Sage);
#Scripting.setCAS(Scripting::CAS::Singular);

r = PolyRing.new(ZZ(),"x,y,z",Order::Lexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [ -10*x**5*y**4*z**2, 
       7*x**2*y**5*z**3,
      -10*x**2*y*z**5,
      -7*x*y**5*z**4,
      6*x*y**4*z**3,
      6*x*y**3*z**3,
      3*x*y**2*z,
      y**4*z,
      -7*y**2*z,
      2*z**5 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::NegativeLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [ 
       2*z**5,
       -7*y**2*z,
       y**4*z,
       3*x*y**2*z,
       6*x*y**3*z**3,
       6*x*y**4*z**3,
       -7*x*y**5*z**4,
       -10*x**2*y*z**5,
       7*x**2*y**5*z**3,
       -10*x**5*y**4*z**2
 ]

puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::DegreeLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
  -10*x**5*y**4*z**2,
  7*x**2*y**5*z**3,
  -7*x*y**5*z**4,
  -10*x**2*y*z**5,
  6*x*y**4*z**3,
  6*x*y**3*z**3,
  y**4*z,
  2*z**5,
  3*x*y**2*z,
  -7*y**2*z
 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::NegativeDegreeReverseLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
    -7*y**2*z,3*x*y**2*z,
    y**4*z,
    2*z**5,
    6*x*y**3*z**3,
    6*x*y**4*z**3,
    -10*x**2*y*z**5,
    7*x**2*y**5*z**3,
    -7*x*y**5*z**4,
    -10*x**5*y**4*z**2
 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::DegreeReverseLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
    -10*x**5*y**4*z**2,
    7*x**2*y**5*z**3,
    -7*x*y**5*z**4,
    6*x*y**4*z**3,
    -10*x**2*y*z**5,
    6*x*y**3*z**3,
    y**4*z,
    2*z**5,
    3*x*y**2*z,
    -7*y**2*z
 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::NegativeDegreeLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
     -7*y**2*z,
     3*x*y**2*z,
     y**4*z,
     2*z**5,
     6*x*y**3*z**3,
     -10*x**2*y*z**5,
     6*x*y**4*z**3,
     7*x**2*y**5*z**3,
     -7*x*y**5*z**4,
     -10*x**5*y**4*z**2
 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::NegativeReverseLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
     -7 * y**2 * z, 
     3 * x * y**2 * z, 
     y**4 * z, 
     -10 * x**5 * y**4 * z**2, 
     6 * x * y**3 * z**3, 
     6 * x * y**4 * z**3, 
     7 * x**2 * y**5 * z**3, 
     -7 * x * y**5 * z**4, 
     2 * z**5, 
     -10 * x**2 * y * z**5 
 ]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;
puts "mm == fm: " + (mm == fm).to_s;
puts;


r = PolyRing.new(ZZ(),"x,y,z",Order::ReverseLexicographic);
puts "Ring: " + str(r);
puts;

ff =   -10*x**5*y**4*z**2 +
       7*x**2*y**5*z**3 +
       -10*x**2*y*z**5 +
       -7*x*y**5*z**4 +
       6*x*y**4*z**3 +
       6*x*y**3*z**3 +
       3*x*y**2*z +
       y**4*z +
       -7*y**2*z +
       2*z**5;
puts "ff = " + str(ff); 
puts;
fm = ff.elem.iterator().map{ |a| RingElem.new(r.ring.valueOf(a)) };

mm = [
     -10 * x**2 * y * z**5, 
     2 * z**5, 
     -7 * x * y**5 * z**4, 
     7 * x**2 * y**5 * z**3, 
     6 * x * y**4 * z**3, 
     6 * x * y**3 * z**3, 
     -10 * x**5 * y**4 * z**2, 
     y**4 * z, 
     3 * x * y**2 * z, 
     -7 * y**2 * z
]
puts "mm = " + mm.map{ |a| a.to_s }.join(", ");
puts;

puts "mm == fm: " + (mm == fm).to_s;
puts;
