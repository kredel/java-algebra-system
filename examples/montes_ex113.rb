#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# Montes JSC 2002, 33, 183-208, example 11.3
# integral function coefficients

R = PolyRing.new( PolyRing.new(QQ(),"r, l, z",PolyRing.lex), "c1, c2, s1, s2", PolyRing.lex );
puts "Ring: " + str(R);
puts;

one,r,l,z,c1,c2,s1,s2 = R.gens();
puts "gens: " + r.gens().each{ |f| str(f) }.join(",");
puts;

f1 = r - c1 + l * ( s1 * s2 - c1 * c2 );
f2 = z - s1 - l * ( s1 * c2 + s2 * c1 );
f3 = s1**2 + c1**2 - 1;
f4 = s2**2 + c2**2 - 1;

F = [f1,f2,f3,f4];

puts "F: " + F.each{ |f| str(f) }.join(",");
puts;

startLog();

If = R.paramideal( "", list = F );
puts "ParamIdeal: " + str(If);
puts;

## G = If.GB();
## puts "GB: " + str(G);
## puts;
## sys.exit();

GS = If.CGBsystem();
#GS = If.CGBsystem();
#GS = If.CGBsystem();
puts "CGBsystem: " + str(GS);
puts;

bg = GS.isCGBsystem();
if bg
    puts "isCGBsystem: true";
else
    puts "isCGBsystem: false";
end
puts;

#terminate();
#exit();

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
#------------------------------------------
#exit();

