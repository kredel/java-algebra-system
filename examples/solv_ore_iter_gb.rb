#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Iterated Ore extension solvable polynomial example, 

rc = PolyRing.new(QQ(), "x,y,z,t", PolyRing.lex);
#is automatic: [one,x,y,z,t] = rc.gens();

crel = [ z, y,  y * z + x,
         t, y,  y * t + y,
         t, z,  z * t - z
       ];

puts "crel: = [" + crel.map { |r| r.to_s }.join(", ") + "]";
puts;

rcs = SolvPolyRing.new(QQ(), "x,y,z,t", PolyRing.lex, crel);

#exit(0)

rm = PolyRing.new(rcs,"u,v,w",PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = rm.gens();

mrel = [ v, u,  u * v + x,
         w, v,  v * w + y,
         w, u,  u * w - z
       ];

puts "mrel: = [" + mrel.map { |r| r.to_s }.join(", ") + "]";
puts;

rs = SolvPolyRing.new(rcs, "u,v,w", PolyRing.lex, mrel);

#exit(0)

ff = [ (w**2 - v * u) * v*w**2, 
       (u**3 + t) * v*w**2, 
       (v*u**2 - x*y) * v*w**2 
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

#exit(0)
#startLog();

lf = f.leftGB();
puts "lf: " + str(lf);
puts;

ff = [ (w**2 - v * u) * w, 
       (u**3 + t) * w, 
       (v*u**2 - x*y) * w 
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

tf = f.twosidedGB();
puts "tt: " + str(tf);
puts;

#exit(0)

ff = [ u * (w - v), 
       u * (v**2 + t),
       u * (v*u - x*y)
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

rf = f.rightGB();
puts "rf: " + str(rf);
puts;


#----- quotient of rsc: --------------

q = SRF(rcs);
puts "q: " + str(q.ring.toScript());
puts;
#one,x,xi,y,yi,z,zi,t,ti = q.gens();
#puts "x: " + str(x);
#puts "t: " + str(t);
#puts

rm = PolyRing.new(q,"u,v,w",PolyRing.lex);
#is automatic: [one,x,y,z,t,u,v,w] = rm.gens();

mrel = [ v, u,  u * v + x,
         w, v,  v * w + y,
         w, u,  u * w - z
       ];

puts "mrel: = [" + mrel.map { |r| r.to_s }.join(", ") + "]";
puts;

rs = SolvPolyRing.new(q, "u,v,w", PolyRing.lex, mrel);
#is automatic: [one,x,y,z,t,u,v,w] = rs.gens();

puts "x: " + str(x);
puts "w: " + str(w);
puts

#exit(0)

ff = [ (w**2 - v * u) * v*w**2, 
       (u**3 + t) * v*w**2, 
       (v*u**2 - x*y) * v*w**2 
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

#exit(0)
#startLog();

lf = f.leftGB();
puts "lf: " + str(lf);
puts;

#exit(0)

ff = [ (w**2 - v * u) * w, 
       (u**3 + t) * w, 
       (v*u**2 - x*y) * w 
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

tf = f.twosidedGB();
puts "tt: " + str(tf);
puts;

#exit(0)

ff = [ u * (w - v), 
       u * (v**2 + t),
       u * (v*u - x*y)
     ];
f = rs.ideal("", ff);
puts "f: " + str(f);
puts;

rf = f.rightGB();
puts "rf: " + str(rf);
puts;


#startLog();
terminate();
