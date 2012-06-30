#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Suzuki and Sato, ISSAC 2006, example 1
# integral function coefficients


#r = PolyRing.new( QQ(),"b,a,z,y,x", PolyRing.lex );
#r = PolyRing.new( RF( PolyRing.new(QQ(),"a, b",PolyRing.lex) ), "z,y,x", PolyRing.lex );
r = PolyRing.new( PolyRing.new(QQ(),"b, a",PolyRing.lex), "z,y,x", PolyRing.lex );
puts "Ring: " + str(r);
puts;

#one,c,b,a,z,y,x = r.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

#f1 = x**2 - a;
#f2 = y**3 - b;

f1 = x**3 - a;
f2 = y**4 - b;
f3 = x + y - z;

F = [f1,f2,f3];

puts "F: " + F.each{ |f| str(f) }.join(",");
puts;

startLog();

If = r.paramideal( "", list = F );
puts "ParamIdeal: " + str(If);
puts;

G = If.GB();
puts "GB: " + str(G);
puts;

terminate();
exit();

GS = If.CGBsystem();
puts "CGBsystem: " + str(GS);
puts;

bg = GS.isCGBsystem();
if bg 
    puts "isCGBsystem: true";
else
    puts "isCGBsystem: false";
end
puts;

terminate();
exit();

CG = If.CGB();
puts "CGB: " + str(CG);
puts;

bg = CG.isCGB();
if bg
    puts "isCGB: true";
else
    puts "isCGB: false";
end
puts;

terminate();

