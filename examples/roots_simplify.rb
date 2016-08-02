#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# roots simplification

startLog();

r = PolyRing.new(QQ(),"alpha",PolyRing.lex);
puts "r     = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);

puts 
puts "roots of unity" 
puts 

#ap = (a**5 - 1)/(a - 1);
#ap = (a**6 - 1)/(a - 1);
ap = (a**7 - 1)/(a - 1);
#ap = (a**8 - 1)/(a - 1);
puts "ap    = " + str(ap);
x = r.factors(ap); 
puts "x     = " + str( x.map{ |p,e| str(p)+"**"+str(e) }.join(" * "));
#puts "      = " + str(x.keys());
qs2 = AN( x.keys()[-1], true); #x.size==1); # and e==1
puts "qs2   = " + str(qs2.factory());
one,alpha = qs2.gens();
puts "one   = " + str(one);
puts "alpha = " + str(alpha);

eps = QQ(1,10) ** (BigDecimal::DEFAULT_PRECISION); #-3
#eps = QQ(1,10) ** 15;
#eps = nil;
puts "eps = " + str(eps);


puts 
puts "with intermediate primitive element" 
puts 
ip = (a**2 + 1);   # I
w3p = (a**3 - 2);  # root{3}(2)
iq = AN( ip, true);
w3q = AN( w3p, true);

puts "iq    = " + str(iq.factory());
puts "w3q   = " + str(w3q.factory());

ipr = ip.realRoots(eps);
#puts "ipr = " + str( ipr.map{ |z| str(z.elem.ring.root.toDecimal()) }.join(", ") );
puts "iprm = " + str( ipr.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
ipc = ip.complexRoots(eps);
#puts "ipc = " + str( ipc.map{ |z| str(z.elem.ring.root.getDecimalCenter()) }.join("\n") );
puts "ipcm = " + str( ipc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts
for z in ipr
    puts "isRootOfUnity("+str(z)+"): " + str(z.elem.isRootOfUnity())
end;
for z in ipc
    puts "isRootOfUnity("+str(z)+"): " + str(z.elem.isRootOfUnity())
end;
puts
iprc = ipr.map{ |z| z.elem }
ipcc = ipc.map{ |z| z.elem }
ipccc = RootFactory.filterOutRealRoots(ip.elem, ipcc, iprc);
ipcc = ipccc.map{ |z| RingElem.new(z) } 
puts "ipcc = " + str( ipcc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts

w3r = w3p.realRoots(eps);
#puts "w3r = " + str( w3r.map{ |z| str(z.elem.ring.root.toDecimal()) }.join("\n") );
puts "w3rm = " + str( w3r.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
w3r_real = w3r[0];
w3c = w3p.complexRoots(eps);
#puts "w3c = " + str( w3c.map{ |z| str(z.elem.ring.root.getDecimalCenter()) }.join("\n") );
puts "w3cm = " + str( w3c.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
w3r_complex = w3c[2];
puts
for z in w3r
    puts "isRootOfUnity("+str(z)+"): " + str(z.elem.isRootOfUnity())
end;
for z in w3c
    puts "isRootOfUnity("+str(z)+"): " + str(z.elem.isRootOfUnity())
end;
puts
w3rc = w3r.map{ |z| z.elem }
w3cc = w3c.map{ |z| z.elem }
w3ccc = RootFactory.filterOutRealRoots(w3p.elem, w3cc, w3rc);
w3cc = w3ccc.map{ |z| RingElem.new(z) } 
puts "w3cc = " + str( w3cc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts


#puts "w3r_real    = " + str(w3r_real.elem.ring.root); #.toDecimal());
#puts "w3r_complex = " + str(w3r_complex.elem.ring.root); #.getDecimalCenter());
#puts "w3p         = " + str(w3p);
#tt = RootFactory.isRealRoot(w3p.elem, w3r_complex.elem, w3r_real.elem);
#puts "tt          = " + str(tt);
#puts


iw3q = PolyUtilApp.primitiveElement(iq.elem.factory(),w3q.elem.factory());
puts "iw3q  = " + str(iw3q.toScript());

r1 = PolyRing.new(QQ(),"e1",PolyRing.lex);
puts "r1    = " + str(r1);

iw3qp = AN(iw3q.primitiveElem.modul, true);
puts "iw3qp = " + str(iw3qp.factory());
##y = r1.factors(iw3qp.elem.factory().modul);
##puts "y      = " + str( y.map{ |p,e| str(p)+"**"+str(e) }.join(" * "));
#z = r1.factorsAbsolute(iw3qp.elem.factory().modul);
#puts "z     = " + str(z);

#puts "iw3q.A = " + str(iw3q.A);
#puts "iw3q.B = " + str(iw3q.B);

#aa = AN(iw3q.primitiveElem.modul, iw3q.A.val, true);
#bb = AN(iw3q.primitiveElem.modul, iw3q.B.val, true);
aa = AN(iw3q.A, iw3q.A.val);
bb = AN(iw3q.B, iw3q.B.val);
puts "aa = " + str(aa);
puts "bb = " + str(bb);

yy = RingElem.new(iw3qp.ring.modul);
puts "yy = " + str(yy);

rr = yy.realRoots(); #eps
#puts "rr = " + str( rr.map{ |z| str(z.elem.ring.root.toDecimal()) }.join("\n") );
puts "rrm = " + str( rr.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts
cc = yy.complexRoots(); #eps
#puts "cc = " + str( cc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
#puts "cc = " + str( cc.map{ |z| str(z.elem.magnitude()) }.join("\n") );
#puts "cc = " + str( cc.map{ |z| str(z.elem.ring.root) }.join("\n") );
#puts "cc = " + str( cc.map{ |z| str(z.elem.ring.root.getDecimalCenter()) }.join("\n") );
puts "ccm = " + str( cc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts
for z in cc
    puts "isRootOfUnity("+str(z.elem.decimalMagnitude())+"): " + str(z.elem.isRootOfUnity())
end;
puts


puts "ipcm = " + str( ipc.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );
puts "w3cm = " + str( w3c.map{ |z| str(z.elem.decimalMagnitude()) }.join("\n") );

#puts [1,2,3].zip([4,5,6]).map{ |a,b| str(a+b) }.join(", "); 
puts

#zz = ipc.product(w3c);
#puts "zz = " + str( zz.map{ |a,b| str(a.elem.decimalMagnitude().sum(b.elem.decimalMagnitude())) }.join("\n") );
#puts
zz = w3c.product(ipc);
puts "zz = " + str( zz.map{ |a,b| str(a.elem.decimalMagnitude().sum(b.elem.decimalMagnitude())) }.join("\n") );
puts

puts "isRootOfUnity(A) = " + str(iw3q.A.isRootOfUnity());
puts "isRootOfUnity(B) = " + str(iw3q.B.isRootOfUnity());
puts

algroot = RootFactory.algebraicRoots(iw3qp.ring.modul);
puts "algebraic roots: " + str(algroot.toScript());
puts

ualgroot = RootFactory.rootsOfUnity(algroot);
puts "unity algebraic roots: " + str(ualgroot.toScript());
puts


iroot = RootFactory.algebraicRoots(ip.elem);
puts "algebraic roots: iroot = " + str(iroot.toScript());
puts "unity algebraic roots: iroot = " + str(RootFactory.rootsOfUnity(iroot).toScript());
w3root = RootFactory.algebraicRoots(w3p.elem);
puts "algebraic roots: w3root = " + str(w3root.toScript());
puts "unity algebraic roots: w3root = " + str(RootFactory.rootsOfUnity(w3root).toScript());
puts
rootred = Java::EduJasApplication::RootFactory.rootReduce(iroot, w3root);
puts "algebraic roots: rootred = " + str(rootred.toScript());
puts

terminate();
