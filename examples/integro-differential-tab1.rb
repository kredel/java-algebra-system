#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# non-commutative polynomial examples: 
# rewrite rules for integro-differential algebra, 
# RR, JSC 2008, table 1

w = WordPolyRing.new(QQ(),"fg,hf,df,lf,ef,d,l,f,g,h,p,e");
puts "WordPolyRing: " + str(w);
puts;

one,fg,hf,df,lf,ef,d,l,f,g,h,p,e = w.gens();
puts "one = " + str(one);
puts "fg = " + str(fg);
puts "hf = " + str(hf);
puts "df = " + str(df);
puts "lf = " + str(lf);
puts "ef = " + str(ef);

puts "d = " + str(d);
puts "l = " + str(l);
puts "f = " + str(f);
puts "g = " + str(g);
puts "h = " + str(h);
puts "p = " + str(p);
puts "e = " + str(e);
puts;

e = 1 - l*d;
puts "e = " + str(e);

r1 = f * g - fg; 
r2 = h * p - p;
r3 = h * f - hf*h;

r4 = d * f - ( df + f * d );
r5 = d * h - 0;
r6 = d * l - 1;

r7 = l * f * l - ( lf * l - l * lf ); 
r8 = l * f * d - ( f - l * df - ef * e );
r9 = l * f * h - ( lf*h );

r10 = d * lf - f;
r11 = l * df - f;

r12 = d * p - 0;
r13 = p * h - h;

#ww = w.ideal("",[r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11,r12,r13]);
ww = w.ideal("",[r1,r2,r3,r4,r5,r6,r7,r8,r9,r10,r11]);
puts "ww = " + str(ww);

startLog()

tt = ww.GB();
puts "tt = " + str(tt);
puts;

