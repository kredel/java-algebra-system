#
# jruby examples for jas.
# $Id$
#

require "examples/jas"

# roots simplification

#startLog();

r = PolyRing.new(QQ(),"I",PolyRing.lex);
puts "r     = " + str(r);
e,a = r.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);

eps = QQ(1,10) ** (BigDecimal::DEFAULT_PRECISION); #-3
#eps = QQ(1,10) ** 7;
#eps = nil;
puts "eps   = " + str(eps);

ip = (a**2 + 1);   # I
iq = AN( ip, 0, true);
puts "iq    = " + str(iq.factory());
puts

#iroot = RootFactory.algebraicRoots(ip.elem);
iroot = ip.algebraicRoots();
puts "algebraic roots: iroot         = " + str(iroot);
puts "unity algebraic roots: iroot   = " + str(iroot.rootsOfUnity());
#RootFactory.rootRefine(iroot, eps.elem);
iroot = iroot.rootRefine(eps);
puts "algebraic roots refined: iroot = " + str(iroot.elem.toDecimalScript());
idroot = ip.decimalRoots(eps);
puts "decimal roots: idroot          = " + str(idroot);
puts


r2 = PolyRing.new(QQ(),"w_3_2",PolyRing.lex);
puts "r2    = " + str(r2);
e,a = r2.gens();
puts "e     = " + str(e);
puts "a     = " + str(a);
w3p = (a**3 - 2);  # root{3}(2)
w3q = AN( w3p, 0, true);
puts "w3q   = " + str(w3q.factory());
puts

#w3root = RootFactory.algebraicRoots(w3p.elem);
w3root = w3p.algebraicRoots();
puts "algebraic roots: w3root         = " + str(w3root);
puts "unity algebraic roots: w3root   = " + str(w3root.rootsOfUnity());
w3root = w3root.rootRefine(eps);
puts "algebraic roots refined: w3root = " + str(w3root.elem.toDecimalScript());
w3droot = w3p.decimalRoots(eps);
puts "decimal roots: w3droot          = " + str(w3droot);
puts


puts "with intermediate primitive element" 
puts 

#rootred = Java::EduJasApplication::RootFactory.rootReduce(iroot.elem, w3root.elem);
rootred = iroot.rootReduce(w3root);
#rootred = r.rootReduce(iroot,w3root);
puts "algebraic roots: rootred       = " + str(rootred);
puts "unity algebraic roots: rootred = " + str(rootred.rootsOfUnity());
# somewhat slow:
#rootred.rootRefine(eps);
#puts "algebraic roots refined: rootred = " + str(rootred.toDecimalScript());
##puts

#decroot = RootFactory.decimalRoots(rootred.p, eps.elem);
decroot = rootred.decimalRoots(eps);
puts "decimal roots: decroot         = " + str(decroot);
puts

##puts "primitive element: primElem    = " + str(rootred.elem.pelem.primitiveElem.toScript());
#puts "primitive element: A           = " + str(rootred.elem.pelem.A.toScript());
#puts "primitive element: B           = " + str(rootred.elem.pelem.B.toScript());
#puts "primitive element: Aring       = " + str(rootred.elem.pelem.Aring.toScript());
#puts "primitive element: Bring       = " + str(rootred.elem.pelem.Bring.toScript());
#puts

terminate();
