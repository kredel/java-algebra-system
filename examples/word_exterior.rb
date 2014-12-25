#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# exterior calculus example
# Hartley and Tuckey, 1993,
# GB in Clifford and Grassmann algebras

r = WordPolyRing.new(QQ(), "a,b,c,f,g,h,u,v,w,x,y,z");
puts "WordPolyRing: " + str(r);
puts;

one,a,b,c,f,g,h,u,v,w,x,y,z = r.gens();
puts "a = %s" % a;
puts "z = %s" % z;
puts;

# exterior algebra relations
rs = [
  b * a   - a * b ,
  c * a   - a * c ,
  f * a   - a * f ,
  g * a   - a * g ,
  h * a   - a * h ,
  u * a   - a * u ,
  v * a   - a * v ,
  w * a   - a * w ,
  x * a   - a * x ,
  y * a   - a * y ,
  z * a   - a * z ,

  c * b   - b * c ,
  f * b   - b * f ,
  g * b   - b * g ,
  h * b   - b * h ,
  u * b   - b * u ,
  v * b   - b * v ,
  w * b   - b * w ,
  x * b   - b * x ,
  y * b   - b * y ,
  z * b   - b * z ,

  f * c   - c * f ,
  g * c   - c * g ,
  h * c   - c * h ,
  u * c   - c * u ,
  v * c   - c * v ,
  w * c   - c * w ,
  x * c   - c * x ,
  y * c   - c * y ,
  z * c   - c * z ,

  g * f   - f * g ,
  h * f   - f * h ,
  u * f   - f * u ,
  v * f   - f * v ,
  w * f   - f * w ,
  x * f   - f * x ,
  y * f   - f * y ,
  z * f   - f * z ,

  h * g   - g * h ,
  u * g   - g * u ,
  v * g   - g * v ,
  w * g   - g * w ,
  x * g   - g * x ,
  y * g   - g * y ,
  z * g   - g * z ,

  u * h   - h * u ,
  v * h   - h * v ,
  w * h   - h * w ,
  x * h   - h * x ,
  y * h   - h * y ,
  z * h   - h * z ,

  v * u   - u * v ,
  w * u   - u * w ,
  x * u   - u * x ,
  y * u   - u * y ,
  z * u   - u * z ,

  w * v   - v * w ,
  x * v   - v * x ,
  y * v   - v * y ,
  z * v   - v * z ,

  x * w   - w * x ,
  y * w   - w * y ,
  z * w   - w * z ,

  y * x   - x * y ,
  z * x   - x * z ,

  z * y   - y * z ,

  a * a ,
  b * b ,
  c * c ,
  f * f ,
  g * g ,
  h * h ,
  u * u ,
  v * v ,
  w * w ,
  x * x ,
  y * y ,
  z * z 
];

# ( a*b + c*f + g*h ),
# ( u*v + w*x + y*z ),
# ( a*v + w*x + y*z )

ff = [
 ( a*b + c*f + g*h ),
 ( u*v + w*x + y*z )
];


fi = r.ideal("", ff + rs); 
puts "WordPolyIdeal: " + str(fi);
puts;

#startLog();

gi = fi.GB();
puts "seq GB: " + str(gi);
puts;

# from exterior.rb
#
ee = [
 ( g * h + c * f + a * b ), 
 ( y * z + w * x + u * v ), 
 ( c * f * g + a * b * g ),  
 ( c * f * h + a * b * h ), 
 ( w * x * y + u * v * y ), 
 ( w * x * z + u * v * z ), 
  a * b * c * g, 
  a * b * f * g, 
  a * b * c * h, 
  a * b * f * h, 
  a * b * c * f, 
  u * v * w * y, 
  u * v * x * y, 
  u * v * w * z, 
  u * v * x * z, 
  u * v * w * x
];


ei = r.ideal("", ee + rs); 
puts "WordPolyIdeal: " + str(ei);
puts;

hi = ei.GB();
puts "seq GB: " + str(hi);
puts;

puts "gi == hi: " + (gi === hi).to_s;
puts
