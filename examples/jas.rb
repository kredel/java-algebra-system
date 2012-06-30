# $Id$

=begin rdoc
jruby interface to JAS.
=end

module JAS

  module_function

require "java"
require "rational"
require "mathn"

include_class "java.lang.System"
include_class "java.io.StringReader"
include_class "java.util.ArrayList"
include_class "java.util.Collections"

#require "../lib/log4j.jar"
include_class "org.apache.log4j.BasicConfigurator";
include_class "org.apache.log4j.Logger";

=begin rdoc
Configure the log4j system and start logging.
=end
def startLog()
    BasicConfigurator.configure();
end

#startLog();

=begin rdoc
Mimic Python str() function.
=end
def str(s)
    return s.to_s;
end

include_class "edu.jas.kern.ComputerThreads";

=begin rdoc
Terminate the running thread pools.
=end
def terminate()
    ComputerThreads.terminate();
end

=begin rdoc
Turn off automatic parallel threads usage.
=end
def noThreads()
    puts "nt = ", ComputerThreads.NO_THREADS;
    ComputerThreads.setNoThreads(); #NO_THREADS = #0; #1; #true;
    puts "\nnt = ", ComputerThreads.NO_THREADS;
    puts
end

# set output to Ruby scripting
include_class "edu.jas.kern.Scripting";
Scripting.setLang(Scripting::Lang::Ruby);

include_class "edu.jas.util.ExecutableServer";
include_class "edu.jas.structure.Power";
include_class "edu.jas.arith.BigInteger";
include_class "edu.jas.arith.BigRational";
include_class "edu.jas.arith.ModInteger";
include_class "edu.jas.arith.ModIntegerRing";
include_class "edu.jas.arith.BigDecimal";
include_class "edu.jas.arith.BigComplex";
include_class "edu.jas.arith.BigQuaternion";
include_class "edu.jas.arith.BigOctonion";
include_class "edu.jas.arith.Product";
include_class "edu.jas.arith.ProductRing";


=begin rdoc
Create JAS BigInteger as ring element.
=end
def ZZ(z=0)
    if z.is_a? RingElem
        z = z.elem;
    end
    r = BigInteger.new(z);
    return RingElem.new(r);
end


=begin rdoc
Create JAS ModInteger as ring element.
=end
def ZM(m,z=0,field=false)
    if m.is_a? RingElem
        m = m.elem;
    end
    if z.is_a? RingElem
        z = z.elem;
    end
    if z != 0 and ( z == true or z == false )
        field = z;
        z = 0;
    end
    if field
        mf = ModIntegerRing.new(m,field);
    else
        mf = ModIntegerRing.new(m);
    end
    r = ModInteger.new(mf,z);
    return RingElem.new(r);
end


=begin rdoc
Create JAS ModInteger as field element.
=end
def GF(m,z=0,field=true)
    return ZM(m,z,field);
end

=begin rdoc
Create JAS BigRational as ring element.
=end
def QQ(d=0,n=1)
    if d.is_a? Rational 
        if n != 1
            puts "#{n} ignored\n";
        end
        if d.denominator != 1
            n = d.denominator;
        end
        d = d.numerator;
    end
    if d.is_a? RingElem
        d = d.elem;
    end
    if n.is_a? RingElem
        n = n.elem;
    end
    if n == 1
        if d == 0
            r = BigRational.new();
        else
            r = BigRational.new(d);
        end
    else
        d = BigRational.new(d);
        n = BigRational.new(n);
        r = d.divide(n); # BigRational.new(d,n); only for short integers
    end
    return RingElem.new(r);
end


=begin rdoc
Create JAS BigComplex as ring element.
=end
def CC(re=BigRational.new(),im=BigRational.new())
    if re == 0
        re = BigRational.new();
    end
    if im == 0
        im = BigRational.new();
    end
    if re.is_a? Array 
        if re[0].is_a? Array 
            if re.size > 1
                im = QQ( re[1] );
            end
            re = QQ( re[0] );
        else
            re = QQ(re);
#        re = makeJasArith( re );
        end
    end
    if im.is_a? Array 
        im = QQ( im );
#        im = makeJasArith( im );
    end
    if re.is_a? Numeric
        re = QQ(re);
    end
    if im.is_a? Numeric
        im = QQ(im);
    end
    if re.is_a? RingElem
        re = re.elem;
    end
    if im.is_a? RingElem
        im = im.elem;
    end
    if im.isZERO()
        if re.isZERO()
            c = BigComplex.new();
        else
            c = BigComplex.new(re);
        end
    else
        c = BigComplex.new(re,im);
    end
    return RingElem.new(c);
end


=begin rdoc
Create JAS generic Complex as ring element.
=end
def CR(re=BigRational.new(),im=BigRational.new(),ring=nil)
    if re == 0
        re = BigRational.new();
    end
    if im == 0
        im = BigRational.new();
    end
    if re.is_a? Array 
        if re[0].is_a? Array 
            if re.size > 1
                im = QQ( re[1] );
            end
            re = QQ( re[0] );
        else
            re = QQ(re);
#        re = makeJasArith( re );
        end
    end
    if im.is_a? Array 
        im = QQ( im );
#        im = makeJasArith( im );
    end
    if re.is_a? RingElem
        re = re.elem;
    end
    if im.is_a? RingElem
        im = im.elem;
    end
    if ring == nil
        ring = re.factory();
    end
    r = ComplexRing.new(ring);
    if im.isZERO()
        if re.isZERO()
            c = Complex.new(r);
        else
            c = Complex.new(r,re);
        end
    else
        c = Complex.new(r,re,im);
    end
    return RingElem.new(c);
end


=begin rdoc
Create JAS BigDecimal as ring element.
=end
def DD(d=0)
    if d.is_a? RingElem
        d = d.elem;
    end
    if d.is_a? Float
        d = d.to_s;
    end
    #puts "d type(#{d}) = #{d.class}";
    if d == 0
       r = BigDecimal.new();
    else
       r = BigDecimal.new(d);
    end
    return RingElem.new(r);
end


=begin rdoc
Create JAS BigQuaternion as ring element.
=end
def Quat(re=BigRational.new(),im=BigRational.new(),jm=BigRational.new(),km=BigRational.new())
    if re == 0
        re = BigRational.new();
    end
    if im == 0
        im = BigRational.new();
    end
    if jm == 0
        jm = BigRational.new();
    end
    if km == 0
        km = BigRational.new();
    end
    if re.is_a? Numeric 
        re = QQ(re);
#        re = makeJasArith( re );
    end
    if im.is_a? Numeric 
        im = QQ( im );
    end
    if jm.is_a? Numeric
        jm = QQ( jm );
    end
    if km.is_a? Numeric
       kim = QQ( km );
#        im = makeJasArith( im );
    end
    if re.is_a? RingElem
        re = re.elem;
    end
    if im.is_a? RingElem
        im = im.elem;
    end
    if jm.is_a? RingElem
        jm = jm.elem;
    end
    if km.is_a? RingElem
        km = km.elem;
    end
    c = BigQuaternion.new(re,im,jm,km);
    return RingElem.new(c);
end


=begin rdoc
Create JAS BigOctonion as ring element.
=end
def Oct(ro=BigQuaternion.new(),io=BigQuaternion.new())
    if ro == 0
        ro = BigQuaternion.new();
    end
    if io == 0
        io = BigQuaternion.new();
    end
    if ro.is_a? Array 
        if ro[0].is_a? Array 
            if ro.size > 1
                io = QQ( ro[1] );
            end
            ro = QQ( ro[0] );
        else
            ro = QQ(ro);
#        re = makeJasArith( re );
        end
    end
    if io.is_a? Array 
        io = QQ( io );
#        im = makeJasArith( im );
    end
    if ro.is_a? RingElem
        ro = ro.elem;
    end
    if io.is_a? RingElem
        io = io.elem;
    end
    c = BigOctonion.new(ro,io);
    return RingElem.new(c);
end


=begin rdoc
Proxy for JAS ring elements.

Methods to be used as + - * ** / %.
=end
class RingElem
    include Comparable
    attr_reader :elem, :ring

=begin rdoc
Constructor for ring element.
=end
    def initialize(elem)
        if elem.is_a? RingElem
            @elem = elem.elem;
        else
            @elem = elem;
        end
        begin
            @ring = @elem.factory();
        rescue
            @ring = @elem;
        end
        @elem.freeze
        @ring.freeze
        self.freeze
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @elem.toScript(); 
        #return @elem.toString(); 
    end

=begin rdoc
Convert to float.
=end
    def to_f()
        e = @elem;
        if e.getClass().getSimpleName() == "BigInteger"
            e = BigRational(e);
        end
        if e.getClass().getSimpleName() == "BigRational"
            e = BigDecimal(e);
        end
        if e.getClass().getSimpleName() == "BigDecimal"
            e = e.toString();
        end
        e = e.to_f();
        return e;
    end

=begin rdoc
Zero element of this ring.
=end
    def zero()
        return RingElem.new( @elem.factory().getZERO() );
    end

=begin rdoc
Test if this is the zero element of the ring.
=end
    def isZERO()
        return @elem.isZERO();
    end

=begin rdoc
Test if this is the zero element of the ring.
=end
    def zero?()
        return @elem.isZERO();
    end

=begin rdoc
One element of this ring.
=end
    def one()
        return RingElem.new( @elem.factory().getONE() );
    end

=begin rdoc
Test if this is the one element of the ring.
=end
    def isONE()
        return @elem.isONE();
    end

=begin rdoc
Test if this is the one element of the ring.
=end
    def one?()
        return @elem.isONE();
    end

=begin rdoc
Get the sign of this element.
=end
    def signum()
        return @elem.signum();
    end

=begin rdoc
Absolute value.
=end
    def abs()
        return RingElem.new( @elem.abs() ); 
    end

=begin rdoc
Negative value.
=end
    def -@()
        return RingElem.new( @elem.negate() ); 
    end

=begin rdoc
Positive value.
=end
    def +@()
        return self; 
    end

=begin rdoc
Coerce other to self
=end
    def coerce(other)
        s,o = coercePair(self,other);
        return [o,s]; # keep order for non-commutative
    end

=begin rdoc
Coerce type a to type b or type b to type a.
=end
    def coercePair(a,b)
        #puts "a type(#{a}) = #{a.class}\n";
        #puts "b type(#{b}) = #{b.class}\n";
        begin
            if not a.isPolynomial() and b.isPolynomial()
               s = b.coerceElem(a);
               o = b;
            else
               s = a;
               o = a.coerceElem(b);
            end
        rescue
            s = a;
            o = a.coerceElem(b);
        end
        #puts "s type(#{s}) = #{s.class}\n";
        #puts "o type(#{o}) = #{o.class}\n";
        return [s,o];
    end

=begin rdoc
Coerce other to self
=end
    def coerceElem(other)
        #puts "self  type(#{self}) = #{self.class}\n";
        #puts "other type(#{other}) = #{other.class}\n";
        if @elem.getClass().getSimpleName() == "GenVector"
            if other.is_a? Array 
                o = rbarray2arraylist(other,@elem.factory().coFac,rec=1);
                o = GenVector.new(@elem.factory(),o);
                return RingElem.new( o );
                end
        end
        if @elem.getClass().getSimpleName() == "GenMatrix"
            if other.is_a? Array 
                o = rbarray2arraylist(other,@elem.factory().coFac,rec=2);
                o = GenMatrix.new(@elem.factory(),o);
                return RingElem.new( o );
                end
        end
        if other.is_a? RingElem
            if isPolynomial() and not other.isPolynomial()
                o = @ring.parse( other.elem.toString() ); # not toScript()
                return RingElem.new( o );
            end
            return other;
        end
        #puts "--1";
        if other.is_a? Array
           # assume BigRational or BigComplex
           # assume self will be compatible with them. todo: check this
           puts "other type(#{other})_3 = #{other.class}\n";
           o = makeJasArith(other);
           puts "other type(#{o})_4 = #{o.class}\n";
           ##o = BigRational.new(other[0],other[1]);
           if isPolynomial()
                o = @ring.parse( o.toString() ); # not toScript();
                #o = o.elem;
           elsif @elem.getClass().getSimpleName() == "BigComplex"
                o = CC( o );
                o = o.elem;
           elsif @elem.getClass().getSimpleName() == "BigQuaternion"
                o = Quat( o );
                o = o.elem;
           elsif @elem.getClass().getSimpleName() == "BigOctonion"
                o = Oct( Quat(o) );
                o = o.elem;
           elsif @elem.getClass().getSimpleName() == "Product"
                o = RR(@ring, @elem.multiply(o) ); # valueOf
                #puts "o = #{o}";
                o = o.elem;
           end
           puts "other type(#{o})_5 = #{o.class}\n";
           return RingElem.new(o);
        end
        # test if @elem is a factory itself
        if isFactory()
            if other.is_a? Integer
                o = @elem.fromInteger(other);
            elsif other.is_a? Rational 
                o = @elem.fromInteger( other.numerator );
                o = o.divide( @elem.fromInteger( other.denominator ) );
            elsif other.is_a? Float # ?? what to do ??
                o = @elem.parse( other.to_s );
                ##o = @elem.fromInteger( other.to_i );
            else
                puts "unknown other type(#{other})_1 = #{other.class}\n";
                o = @elem.parse( other.to_s );
            end
            return RingElem.new(o);
        end
        # @elem has a ring factory
        if other.is_a? Integer
            o = @elem.factory().fromInteger(other);
        elsif other.is_a? Rational 
            o = @elem.factory().fromInteger( other.numerator );
            o = o.divide( @elem.factory().fromInteger( other.denominator ) );
        elsif other.is_a? Float # ?? what to do ??
                o = @elem.factory().parse( other.to_s );
                if @elem.getClass().getSimpleName() == "Product"
                    o = RR(@ring, @elem.idempotent().multiply(o) ); # valueOf
                    o = o.elem;
                end
        else
                puts "unknown other type(#{other})_2 = #{other.class}\n";
                o = @elem.factory().parse( other.to_s );
        end
        return RingElem.new(o);
    end

=begin rdoc
Test if this is itself a ring factory.
=end
    def isFactory()
        f = @elem.factory();
        if @elem == f
            return true;
        else
            return false;
        end
    end

=begin rdoc
Test if this is a polynomial.
=end
    def isPolynomial()
        begin
            nv = @elem.ring.nvar;
        rescue
            return false;
        end
        return true;
    end

=begin rdoc
Compare two ring elements.
=end
    def <=>(other)
        #s,o = coercePair(other);
        s,o = self, other
        return s.elem.compareTo( o.elem ); 
    end

=begin rdoc
Test if two ring elements are equal.
=end
    def ===(other)
        return (self <=> other) == 0; 
    end

=begin rdoc
Length of an element.
=end
    def len()
        return self.elem.length(); 
    end

=begin rdoc
Hash value.
=end
    def object_id()
        return @elem.hashCode(); 
    end

=begin rdoc
Multiply two ring elements.
=end
    def *(other)
        #puts "* self  type(#{self}) = #{self.class}\n";
        #puts "* other type(#{other}) = #{other.class}\n";
        s,o = coercePair(self,other);
        return RingElem.new( s.elem.multiply( o.elem ) ); 
    end

=begin rdoc
Add two ring elements.
=end
    def +(other)
        #puts "+ self  type(#{self}) = #{self.class}\n";
        #puts "+ other type(#{other}) = #{other.class}\n";
        s,o = coercePair(self,other);
        return RingElem.new( s.elem.sum( o.elem ) ); 
    end

=begin rdoc
Subtract two ring elements.
=end
    def -(other)
        s,o = coercePair(self,other);
        return RingElem.new( s.elem.subtract( o.elem ) ); 
    end

=begin rdoc
Divide two ring elements.
=end
    def /(other)
        s,o = coercePair(self,other);
        return RingElem.new( s.elem.divide( o.elem ) ); 
    end

=begin rdoc
Modular remainder of two ring elements.
=end
    def %(other)
        s,o = coercePair(self,other);
        return RingElem.new( s.elem.remainder( o.elem ) ); 
    end

=begin rdoc
Can not be used as power.
=end
    def ^(other)
        return nil;
    end

=begin rdoc
Power of this to other.
=end
    def **(other)
        #puts "pow other type(#{other}) = #{other.class}";
        if other.is_a? Integer
            n = other;
        else
            if other.is_a? RingElem
                n = other.elem;
                if n.getClass().getSimpleName() == "BigRational"
                    n = n.numerator().intValue() / n.denominator().intValue();
                end
                if n.getClass().getSimpleName() == "BigInteger" 
                    n = n.intValue();
                end
            end
        end
        if isFactory()
            p = Power.new(@elem).power( @elem, n );
        else
            p = Power.new(@elem.ring).power( @elem, n );
        end
        return RingElem.new( p ); 
    end

=begin rdoc
Test if two ring elements are equal.
=end
    def equal?(other)
        o = other;
        if other.is_a? RingElem
            o = other.elem;
        end
        return @elem.equals(o)
    end

=begin rdoc
Get the factory of this element.
=end
    def factory()
        fac = @elem.factory();
        begin
            nv = fac.nvar;
        rescue
            return RingElem.new(fac);
        end
        #return PolyRing(fac.coFac,fac.getVars(),fac.tord);
        return RingElem.new(fac);
    end

=begin rdoc
Get the generators for the factory of this element.
=end
    def gens()
        ll = @elem.factory().generators();
        #puts "L = #{ll}";
        nn = ll.map {|e| RingElem.new(e) };
        #puts "N = #{nn}";
        return nn;
    end

=begin rdoc
Monic polynomial.
=end
    def monic()
        return RingElem.new( @elem.monic() ); 
    end

=begin rdoc
Evaluate at a for power series.
=end
    def evaluate(a)
        #puts "self  type(#{@elem}) = #{@elen.class}";
        #puts "a     type(#{a}) = #{a.class}";
        x = nil;
        if a.is_a? RingElem
            x = a.elem;
        end
        if a.is_a? Array 
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            x = makeJasArith(a);
        end
        begin
            e = @elem.evaluate(x);
        rescue
            e = 0;            
        end
        return RingElem.new( e );
    end

=begin rdoc
Integrate a power series with constant a or as rational function.

a is the integration constant, r is for partial integration in variable r.
=end
    def integrate(a=0,r=nil)
        #puts "self  type(#{@elem}) = #{@elem.class}";
        #puts "a     type(#{a}) = #{a.class}";
        x = nil;
        if a.is_a? RingElem
            x = a.elem;
        end
        if a.is_a? Array 
            # assume BigRational or BigComplex
            # assume self will be compatible with them. todo: check this
            x = makeJasArith(a);
        end
        begin
            if r != nil
                e = @elem.integrate(x,r);
            else
                e = @elem.integrate(x);
            end
            return RingElem.new( e );
        rescue
            #pass;
        end
        cf = @elem.ring;
        begin
            cf = cf.ring;
        rescue
            #pass;
        end
        integrator = ElementaryIntegration.new(cf.coFac);
        ei = integrator.integrate(@elem); 
        return ei;
    end

=begin rdoc
Differentiate a power series.

r is for partial differentiation in variable r.
=end
    def differentiate(r=nil)
        begin
            if r != nil
                e = @elem.differentiate(r);
            else
                e = @elem.differentiate();
            end
        rescue
            e = @elem.factory().getZERO();
        end
        return RingElem.new( e );
    end

=begin rdoc
Get the coefficients of a polynomial.
=end
    def coefficients()
        a = @elem;
        #L = [ c.toScriptFactory() for c in a.coefficientIterator() ];
        ll = a.coefficientIterator().map { |c| RingElem.new(c) }; 
        return ll
    end

end


include_class "edu.jas.poly.GenPolynomial";
include_class "edu.jas.poly.GenPolynomialRing";
include_class "edu.jas.poly.GenSolvablePolynomial";
include_class "edu.jas.poly.GenSolvablePolynomialRing";
include_class "edu.jas.poly.GenPolynomialTokenizer";
include_class "edu.jas.poly.TermOrder";
include_class "edu.jas.poly.OrderedPolynomialList";
include_class "edu.jas.poly.PolyUtil";
include_class "edu.jas.poly.TermOrderOptimization";
include_class "edu.jas.poly.PolynomialList";
include_class "edu.jas.poly.AlgebraicNumber";
include_class "edu.jas.poly.AlgebraicNumberRing";
include_class "edu.jas.poly.OrderedModuleList";
include_class "edu.jas.poly.ModuleList";
include_class "edu.jas.poly.Complex";
include_class "edu.jas.poly.ComplexRing";


=begin rdoc
Represents a JAS polynomial ring: GenPolynomialRing.

Methods to create ideals and ideals with parametric coefficients.
=end
class Ring
    attr_reader :ring, :pset, :engine, :sqf, :factor

    def initialize(ringstr="",ring=nil)
=begin rdoc
Ring constructor.
=end
        if ring == nil
           sr = StringReader.new( ringstr );
           tok = GenPolynomialTokenizer.new(sr);
           @pset = tok.nextPolynomialSet();
           @ring = @pset.ring;
        else
           @ring = ring;
        end
        @engine = GCDFactory.getProxy(@ring.coFac);
        @sqf = nil;
        @factor = nil;
        begin
            @sqf = SquarefreeFactory.getImplementation(@ring.coFac);
            #puts "sqf: ", @sqf;
        #rescue Rescueion => e
            #puts "error " + str(e)
        rescue
            #pass
        end
        begin
            @factor = FactorFactory.getImplementation(@ring.coFac);
            #puts "factor: ", @factor;
        #rescue Rescueion => e
            #puts "error " + str(e)
        rescue
            #pass
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript();
    end

=begin rdoc
Create an ideal.
=end
    def ideal(ringstr="",list=nil)
        return JAS::SimIdeal.new(self,ringstr,list);
    end

=begin rdoc
Create an ideal in a polynomial ring with parameter coefficients.
=end
    def paramideal(ringstr="",list=nil,gbsys=nil)
        return ParamIdeal.new(self,ringstr,list,gbsys);
    end

=begin rdoc
Get list of generators of the polynomial ring.
=end
    def gens()
        ll = @ring.generators();
        n = ll.map{ |e| RingElem.new(e) };
        return n;
    end

=begin rdoc
Get the one of the polynomial ring.
=end
    def one()
        return RingElem.new( @ring.getONE() );
    end

=begin rdoc
Get the zero of the polynomial ring.
=end
    def zero()
        return RingElem.new( @ring.getZERO() );
    end

=begin rdoc
Get a random polynomial.
=end
    def random(k=5,l=7,d=3,q=0.3)
        r = @ring.random(k,l,d,q);
        if @ring.coFac.isField()
            r = r.monic();
        end
        return RingElem.new( r );
    end

=begin rdoc
Create an element from a string or an object.
=end
    def element(poly)
        if not poly.is_a? String 
           begin
              if @ring == poly.ring 
                 return RingElem.new(poly);
              end
           rescue Exception => e
              # pass
           end
           poly = str(poly);
        end
        i = SimIdeal.new( self, "( " + poly + " )" );
        list = i.pset.list;
        if list.size > 0
           return RingElem.new( list[0] );
        end
    end

=begin rdoc
Compute the greatest common divisor of a and b.
=end
    def gcd(a,b)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        if b.is_a? RingElem
            b = b.elem;
        else
            b = element( b );
            b = b.elem;
        end
        return RingElem.new( @engine.gcd(a,b) );
    end

=begin rdoc
Compute squarefree factors of polynomial.
=end
    def squarefreeFactors(a)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        cf = @ring.coFac;
        if cf.getClass().getSimpleName() == "GenPolynomialRing"
            e = @sqf.recursiveSquarefreeFactors( a );
        else
            e = @sqf.squarefreeFactors( a );
        end
        ll = {};
        for a in e.keySet()
            i = e.get(a);
            ll[ RingElem.new( a ) ] = i;
        end
        return ll;
    end

=begin rdoc
Compute irreducible factorization for modular, integer,
rational number and algebriac number coefficients.
=end
    def factors(a)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        begin
            cf = @ring.coFac;
            if cf.getClass().getSimpleName() == "GenPolynomialRing"
                e = @factor.recursiveFactors( a );
            else
                e = @factor.factors( a );
            end
            ll = {};
            for a in e.keySet()
                i = e.get(a);
                ll[ RingElem.new( a ) ] = i;
            end
            return ll;
        rescue Exception => e
            puts "error " + str(e)
            return nil
        end
    end

=begin rdoc
Compute absolute irreducible factorization for (modular,)
rational number coefficients.
=end
    def factorsAbsolute(a)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        begin
            ll = @factor.factorsAbsolute( a );
##             ll = {};
##             for a in e.keySet()
##                 i = e.get(a);
##                 ll[ RingElem.new( a ) ] = i;
            return ll;
        rescue Exception => e
            puts "error in factorsAbsolute " + str(e)
            return nil
        end
    end

=begin rdoc
Compute real roots of univariate polynomial.
=end
    def realRoots(a,eps=nil)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        if eps.is_a? RingElem
            eps = eps.elem;
        end
        begin
            if eps == nil
                #rr = RealRootsSturm.new().realRoots( a );
                rr = RootFactory.realAlgebraicNumbers( a )
            else
                rr = RootFactory.realAlgebraicNumbers( a, eps );
                ## rr = RealRootsSturm.new().realRoots( a, eps );
                ## rr = [ r.toDecimal() for r in rr ];
                #rr = RealRootsSturm.new().approximateRoots(a,eps);
            end
            rr = rr.map{ |e| RingElem.new(e) };
            return rr;
        rescue Exception => e
            puts "error " + str(e)
            return nil
        end
    end

=begin rdoc
Compute complex roots of univariate polynomial.
=end
    def complexRoots(a,eps=nil)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        if eps.is_a? RingElem
            eps = eps.elem;
        end
        cmplx = false;
        begin
            x = a.ring.coFac.getONE().getRe();
            cmplx = true;
        rescue Exception => e
            #pass;
        end
        begin
            if eps == nil
                #rr = ComplexRootsSturm.new(a.ring.coFac).complexRoots( a );
                if cmplx
                   rr = RootFactory.complexAlgebraicNumbersComplex( a );
                else 
                   rr = RootFactory.complexAlgebraicNumbers( a );
                end
                #R = [ r.centerApprox() for r in R ];
            else
                ## rr = ComplexRootsSturm.new(a.ring.coFac).complexRoots( a, eps );
                ## rr = [ r.centerApprox() for r in rr ];
                ##rr = ComplexRootsSturm.new(a.ring.coFac).approximateRoots( a, eps );
                if cmplx
                   rr = RootFactory.complexAlgebraicNumbersComplex( a, eps );
                else
                   rr = RootFactory.complexAlgebraicNumbers( a, eps );
                end
            end
            rr = rr.map{ |e| RingElem.new(e) };
            return rr;
        rescue Exception => e
            puts "error " + str(e)
            return nil
        end
    end

=begin rdoc
Integrate (univariate) rational function.
=end
    def integrate(a)
        if a.is_a? RingElem
            a = a.elem;
        else
            a = element( a );
            a = a.elem;
        end
        cf = @ring;
        begin
            cf = cf.ring;
        rescue
            #pass;
        end
        integrator = ElementaryIntegration.new(cf.coFac);
        ei = integrator.integrate(a); 
        return ei;
    end

=begin rdoc
Get a power series ring from this ring.
=end
    def powerseriesRing()
        pr = MultiVarPowerSeriesRing.new(@ring);
        return MultiSeriesRing.new("",nil,pr);
    end
end


=begin rdoc
Represents a JAS polynomial ring: GenPolynomialRing.

Provides more convenient constructor. 
Then returns a Ring.
=end
class PolyRing < Ring

    # class instance variables != class variables
    @lex = TermOrder.new(TermOrder::INVLEX)
    @grad = TermOrder.new(TermOrder::IGRLEX)

    @auto_inject = true

    class << self  # means add to class
       attr_reader :lex, :grad
       attr_accessor :auto_inject
    end


=begin rdoc
Ring constructor.

coeff = factory for coefficients,
vars = string with variable names,
order = term order.
=end
    def initialize(coeff,vars,order=self.class.grad)
        if coeff == nil
            raise ValueError, "No coefficient given."
        end
        cf = coeff;
        if coeff.is_a? RingElem
            cf = coeff.elem.factory();
        end
        if coeff.is_a? Ring
            cf = coeff.ring;
        end
        if vars == nil
            raise ValueError, "No variable names given."
        end
        names = vars;
        if vars.is_a? String
           names = GenPolynomialTokenizer.variableList(vars);
        end
        nv = names.size;
        to = self.class.grad;
        if order.is_a? TermOrder
            to = order;
        end
        tring = GenPolynomialRing.new(cf,nv,to,names);
        #want: super(Ring,self).initialize(ring=tring)
        super("",tring) 
        variable_generators()
        if self.class.auto_inject 
           inject_variables();
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript();
    end

=begin rdoc
Define instance variables for generators.
=end
    def variable_generators() 
       PolyRing.instance_eval( "attr_accessor :generators;" )
       @generators = {};
       for i in self.gens()
          begin 
             ivs = i.to_s
             if not ivs.include?(",") and not ivs.include?("(")
                if ivs == "1" or ivs == "1 "
                   ivs = "one"
                end
                @generators[ ivs ] = i;
                self.instance_eval( "def #{ivs}; @generators[ '#{ivs}' ]; end" )
             end
          rescue 
             puts "error: #{i} = " + i.to_s + ", class = " + i.class.to_s;
             #pass
          end
       end
    puts "defined generators: " + @generators.keys().join(", ");  
    end


=begin rdoc
Inject variables for generators in given environment.
=end
    def inject_gens(env) 
       env.class.instance_eval( "attr_accessor :generators;" )
       if env.generators == nil
          env.generators = {};
       end
       #puts "existing generators: " + env.generators.keys().join(", ");  
       for i in self.gens()
          begin 
             ivs = i.to_s
             if not ivs.include?(",") and not ivs.include?("(")
                if ivs == "1" or ivs == "1 "
                   ivs = "one"
                end
                if env.generators[ ivs ] != nil
                   puts "redefining #{ivs}";
                end
                env.generators[ ivs ] = i;
                env.instance_eval( "def #{ivs}; @generators[ '#{ivs}' ]; end" )
                #puts "def #{ivs}; @generators[ '#{ivs}' ]; end"
                first = ivs.slice(0,1);
                if first.count('A-Z') > 0
                   first = first.downcase
                   ivl = first + ivs.slice(1,ivs.length);
                   puts "warning: '" + str(ivs) + "' additionaly renamed to '" + str(ivl) + "' to avoid constant semantics"
                   env.instance_eval( "def #{ivl}; @generators[ '#{ivs}' ]; end" )
                end
             end
          rescue 
             puts "error: #{i} = " + i.to_s + ", class = " + i.class.to_s;
             #pass
          end
       end
    puts "globaly defined generators: " + env.generators.keys().join(", ");  
    end


=begin rdoc
Inject variables for generators in top level environment.
=end
    def inject_variables() 
        require "irb/frame" # must be placed here
        bin = IRB::Frame.bottom(0);
        env = eval "self", bin;
        #puts "env = " + str(env)
        inject_gens(env)
    end

end


=begin rdoc
Create JAS AlgebraicNumber as ring element.
=end
def AN(m,z=0,field=false,pr=nil)
    if m.is_a? RingElem
        m = m.elem;
    end
    if z.is_a? RingElem
        z = z.elem;
    end
    if z != 0 and ( z == true or z == false )
        field = z;
        z = 0;
    end
    #puts "m.getClass() = " + str(m.getClass().getName());
    #puts "field = " + str(field);
    if m.getClass().getSimpleName() == "AlgebraicNumber"
        mf = AlgebraicNumberRing.new(m.factory().modul,m.factory().isField());
    else
        if field
            mf = AlgebraicNumberRing.new(m,field);
        else
            mf = AlgebraicNumberRing.new(m);
        end
    end
    #puts "mf = " + mf.toString();
    if z == 0
        r = AlgebraicNumber.new(mf);
    else
        r = AlgebraicNumber.new(mf,z);
    end
    return RingElem.new(r);
end


include_class "edu.jas.root.RealRootsSturm";
include_class "edu.jas.root.Interval";
include_class "edu.jas.root.RealAlgebraicNumber";
include_class "edu.jas.root.RealAlgebraicRing";
include_class "edu.jas.root.ComplexRootsSturm";
include_class "edu.jas.root.Rectangle";
include_class "edu.jas.root.RootFactory";


=begin rdoc
Create JAS RealAlgebraicNumber as ring element.
=end
def RealN(m,i,r=0)
    if m.is_a? RingElem
        m = m.elem;
    end
    if r.is_a? RingElem
        r = r.elem;
    end
    if i.is_a? Array
        il = BigRational.new(i[0]);
        ir = BigRational.new(i[1]);
        i = Interval.new(il,ir);
    end
    #puts "m.getClass() = " + m.getClass().getName().to_s;
    if m.getClass().getSimpleName() == "RealAlgebraicNumber"
        mf = RealAlgebraicRing.new(m.factory().algebraic.modul,i);
    else
        mf = RealAlgebraicRing.new(m,i);
    end
    if r == 0
        rr = RealAlgebraicNumber.new(mf);
    else
        rr = RealAlgebraicNumber.new(mf,r);
    end
    return RingElem.new(rr);
end


=begin rdoc
Create JAS rational function Quotient as ring element.
=end
def RF(pr,d=0,n=1)
    if d.is_a? Array
        if n != 1
            puts "#{} ignored\n";
        end
        if d.size > 1
            n = d[1];
        end
        d = d[0];
    end
    if d.is_a? RingElem
        d = d.elem;
    end
    if n.is_a? RingElem
        n = n.elem;
    end
    if pr.is_a? RingElem
        pr = pr.elem;
    end
    if pr.is_a? Ring
        pr = pr.ring;
    end
    qr = QuotientRing.new(pr);
    if d == 0
        r = Quotient.new(qr);
    else
        if n == 1
            r = Quotient.new(qr,d);
        else
            r = Quotient.new(qr,d,n);
        end
    end
    return RingElem.new(r);
end


include_class "edu.jas.application.PolyUtilApp";
include_class "edu.jas.application.Residue";
include_class "edu.jas.application.ResidueRing";
include_class "edu.jas.application.Ideal";
include_class "edu.jas.application.Local";
include_class "edu.jas.application.LocalRing";
include_class "edu.jas.application.IdealWithRealAlgebraicRoots";
include_class "edu.jas.application.ComprehensiveGroebnerBaseSeq";


=begin rdoc
Create JAS polynomial Residue as ring element.
=end
def RC(ideal,r=0)
    if ideal == nil
        raise ValueError, "No ideal given."
    end
    if ideal.is_a? SimIdeal
        #puts "ideal.pset = " + str(ideal.pset) + "\n";
        #ideal = Java::EduJasApplication::Ideal.new(ideal.ring,ideal.list);
        ideal = Ideal.new(ideal.pset);
        #ideal.doGB();
    end
    #puts "ideal.getList().get(0).ring.ideal = #{ideal.getList().get(0).ring.ideal}\n";
    if ideal.getList().get(0).ring.getClass().getSimpleName() == "ResidueRing"
        rc = ResidueRing.new( ideal.getList().get(0).ring.ideal );
    else
        rc = ResidueRing.new(ideal);
    end
    if r.is_a? RingElem
        r = r.elem;
    end
    if r == 0
        r = Residue.new(rc);
    else
        r = Residue.new(rc,r);
    end
    return RingElem.new(r);
end


=begin rdoc
Create JAS polynomial Local as ring element.
=end
def LC(ideal,d=0,n=1)
    if ideal == nil
        raise ValueError, "No ideal given."
    end
    if ideal.is_a? SimIdeal
        ideal = Ideal.new(ideal.pset);
        #ideal.doGB();
    end
    #puts "ideal.getList().get(0).ring.ideal = #{ideal.getList().get(0).ring.ideal}\n";
    if ideal.getList().get(0).ring.getClass().getSimpleName() == "LocalRing"
        lc = LocalRing.new( ideal.getList().get(0).ring.ideal );
    else
        lc = LocalRing.new(ideal);
    end
    if d.is_a? Array
        if n != 1
            puts "#{n} ignored\n";
        end
        if d.size > 1
            n = d[1];
        end
        d = d[0];
    end
    if d.is_a? RingElem
        d = d.elem;
    end
    if n.is_a? RingElem
        n = n.elem;
    end
    if d == 0
        r = Local.new(lc);
    else
        if n == 1
            r = Local.new(lc,d);
        else
            r = Local.new(lc,d,n);
        end
    end
    return RingElem.new(r);
end


=begin rdoc
Create JAS regular ring Product as ring element.
=end
def RR(flist,n=1,r=0)
    if not n.is_a? Integer
        r = n;
        n = 1;
    end
    if flist == nil
        raise ValueError, "No list given."
    end
    if flist.is_a? Array
        flist = rbarray2arraylist( flist.map { |x| x.factory() }, rec=1);
        ncop = 0;
    else
        ncop = n;
    end
    if flist.is_a? RingElem
        flist = flist.elem;
        flist = flist.factory();
        ncop = n;
    end
    #puts "flist = " + str(flist);
    #puts "ncop  = " + str(ncop);
    if ncop == 0
        pr = ProductRing.new(flist);
    else
        pr = ProductRing.new(flist,ncop);
    end
    #puts "r type(#{r}) = #{r.class}\n";
    if r.is_a? RingElem
        r = r.elem;
    end
    begin
        #puts "r.class() = #{r.class}\n";
        if r.getClass().getSimpleName() == "Product"
            #puts "r.val = #{r.val}\n";
            r = r.val;
        end
    rescue
        #pass;
    end
    #puts "r = " + r.to_s;
    if r == 0
        r = Product.new(pr);
    else
        r = Product.new(pr,r);
    end
    return RingElem.new(r);
end


=begin rdoc
Convert a Ruby array to a Java ArrayList.

If list is a Ruby array, it is converted, else list is left unchanged.
=end
def rbarray2arraylist(list,fac=nil,rec=1)
    #puts "list type(#{list}) = #{list.class}\n";
    if list.is_a? Array
       ll = ArrayList.new();
       for e in list
           t = true;
           if e.is_a? RingElem
               t = false;
               e = e.elem;
           end
           if e.is_a? Array
               if rec <= 1
                   e = makeJasArith(e);
               else
                   t = false;
                   e = rbarray2arraylist(e,fac,rec-1);
               end
           end
           begin
               n = e.getClass().getSimpleName();
               if n == "ArrayList"
                   t = false;
               end
           rescue
               #pass;
           end
           if t and fac != nil
               #puts "e.p(#{e}) = #{e.class}\n";
               e = fac.parse( str(e) ); #or makeJasArith(e) ?
           end
           ll.add(e);
       end
       list = ll;
    end
    #puts "list type(#{list}) = #{list.class}\n";
    return list
end


=begin rdoc
Construct a jas.arith object.

If item is an ruby array then a BigComplex is constructed. 
If item is a ruby float then a BigDecimal is constructed. 
=end
def makeJasArith(item)
    #puts "item type(#{item}) = #{item.class}\n";
    if item.is_a? Integer
        return BigInteger.new( item );
    end
    if item.is_a? Rational
        return BigRational.new( item.numerator ).divide( BigRational.new( item.denominator ) );
    end
    if item.is_a? Float  # ?? what to do ??
        return BigDecimal.new( item.to_s );
    end
    if item.is_a? Array
        if item.size > 2
            puts "len(item) > 2, remaining items ignored\n";
        end
        puts "item[0] type(#{item[0]}) = #{item[0].class}\n";
        if item.size > 1
            re = makeJasArith( item[0] );
            if not re.isField()
                re = BigRational.new( re.val );
            end
            im = makeJasArith( item[1] );
            if not im.isField()
                im = BigRational.new( im.val );
            end
            jasArith = BigComplex.new( re, im );
        else
            re = makeJasArith( item[0] );
            if not re.isField()
                re = BigRational.new( re.val );
            end
            jasArith = BigComplex.new( re );
        end
        return jasArith;
    end
    puts "unknown item type(#{item}) = #{item.class}\n";
    return item;
end


include_class "edu.jas.gb.DGroebnerBaseSeq";
include_class "edu.jas.gb.EGroebnerBaseSeq";
include_class "edu.jas.gb.GroebnerBaseDistributed";
include_class "edu.jas.gb.GBDist";
include_class "edu.jas.gb.GroebnerBaseParallel";
include_class "edu.jas.gb.GroebnerBaseSeq";
include_class "edu.jas.gb.GroebnerBaseSeqPairSeq";
include_class "edu.jas.gb.ReductionSeq";
include_class "edu.jas.gb.OrderedPairlist";
include_class "edu.jas.gb.OrderedSyzPairlist";
include_class "edu.jas.gb.GroebnerBaseSeqPairParallel";
include_class "edu.jas.gb.SolvableGroebnerBaseParallel";
include_class "edu.jas.gb.SolvableGroebnerBaseSeq";

include_class "edu.jas.gbufd.GroebnerBasePseudoRecSeq";
include_class "edu.jas.gbufd.GroebnerBasePseudoSeq";
include_class "edu.jas.gbufd.RGroebnerBasePseudoSeq";
include_class "edu.jas.gbufd.RGroebnerBaseSeq";
include_class "edu.jas.gbufd.RReductionSeq";
include_class "edu.jas.gbufd.CharacteristicSetWu";

include_class "edu.jas.ufd.GreatestCommonDivisor";
include_class "edu.jas.ufd.PolyUfdUtil";
include_class "edu.jas.ufd.GCDFactory";
include_class "edu.jas.ufd.FactorFactory";
include_class "edu.jas.ufd.SquarefreeFactory";
include_class "edu.jas.ufd.Quotient";
include_class "edu.jas.ufd.QuotientRing";
include_class "edu.jas.integrate.ElementaryIntegration";

#include_class "edu.jas.application.Ideal";

=begin rdoc
Represents a JAS polynomial ideal: PolynomialList and Ideal.

Methods for Groebner bases, ideal sum, intersection and others.
=end
class SimIdeal

    attr_reader :pset, :ring, :list, :roots, :prime, :primary

=begin rdoc
SimIdeal constructor.
=end
    def initialize(ring,polystr="",list=nil)
        @ring = ring;
        if list == nil
           sr = StringReader.new( polystr );
           tok = GenPolynomialTokenizer.new(ring::ring,sr);
           @list = tok.nextPolynomialList();
        else
           @list = rbarray2arraylist(list,rec=1);
        end
        @pset = OrderedPolynomialList.new(@ring.ring,@list);
        @roots = nil;
        @croots = nil;
        @prime = nil;
        @primary = nil;
        #super(@ring::ring,@list) # non-sense, JRuby extends edu.jas.application.Ideal without beeing told
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @pset.toScript();
    end

=begin rdoc
Compare two ideals.
=end
    def <=>(other)
        s,o = self, other
        return s.pset.compareTo( o.pset ); 
    end

=begin rdoc
Compare two ideals.
=end
    def ===(other)
        return (self <=> other) == 0; 
    end

=begin rdoc
Create an ideal in a polynomial ring with parameter coefficients.
=end
    def paramideal()
        return ParamIdeal.new(@ring,"",@list);
    end

=begin rdoc
Compute a Groebner base.
=end
    def GB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            #gg = GroebnerBaseSeq.new().GB(ff);
            #gg = GroebnerBaseSeq.new(ReductionSeq.new(),OrderedPairlist.new()).GB(ff);
            gg = GroebnerBaseSeq.new(ReductionSeq.new(),OrderedSyzPairlist.new()).GB(ff);
        else
            v = nil;
            begin
                v = cofac.vars;
            rescue
                #pass
            end
            if v == nil
                gg = GroebnerBasePseudoSeq.new(cofac).GB(ff);
            else
                gg = GroebnerBasePseudoRecSeq.new(cofac).GB(ff);
            end
        end
        t = System.currentTimeMillis() - t;
        puts "sequential GB executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is a Groebner base.
=end
    def isGB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            b = GroebnerBaseSeq.new().isGB(ff);
        else
            v = nil;
            begin
                v = cofac.vars;
            rescue
                #pass
            end
            if v == nil
                b = GroebnerBasePseudoSeq.new(cofac).isGB(ff);
            else
                b = GroebnerBasePseudoRecSeq.new(cofac).isGB(ff);
            end
        end
        t = System.currentTimeMillis() - t;
        puts "isGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute an e-Groebner base.
=end
    def eGB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            gg = GroebnerBaseSeq.new().GB(ff);
        else
            gg = EGroebnerBaseSeq.new().GB(ff)
        end
        t = System.currentTimeMillis() - t;
        puts "sequential e-GB executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is an e-Groebner base.
=end
    def iseGB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            b = GroebnerBaseSeq.new().isGB(ff);
        else
            b = EGroebnerBaseSeq.new().isGB(ff)
        end
        t = System.currentTimeMillis() - t;
        puts "is e-GB test executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute an d-Groebner base.
=end
    def dGB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            gg = GroebnerBaseSeq.new().GB(ff);
        else
            gg = DGroebnerBaseSeq.new().GB(ff)
        end
        t = System.currentTimeMillis() - t;
        puts "sequential d-GB executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is a d-Groebner base.
=end
    def isdGB()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            b = GroebnerBaseSeq.new().isGB(ff);
        else
            b = DGroebnerBaseSeq.new().isGB(ff)
        end
        t = System.currentTimeMillis() - t;
        puts "is d-GB test executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute in parallel a Groebner base.
=end
    def parUnusedGB(th)
        s = @pset;
        ff = s.list;
        bbpar = GroebnerBaseSeqPairParallel.new(th);
        t = System.currentTimeMillis();
        gg = bbpar.GB(ff);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        puts "parallel-old #{th} executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Compute in parallel a Groebner base.
=end
    def parGB(th)
        s = @pset;
        ff = s.list;
        bbpar = GroebnerBaseParallel.new(th);
        t = System.currentTimeMillis();
        gg = bbpar.GB(ff);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        puts "parallel #{th} executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Compute on a distributed system a Groebner base.
=end
    def distGB(th=2,machine="examples/machines.localhost",port=7114)
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        # G = GroebnerBaseDistributed.Server.new(F,th);
        #G = GBDist.new(th,machine,port).execute(F);
        gbd = GBDist.new(th,machine,port);
        t1 = System.currentTimeMillis();
        gg = gbd.execute(ff);
        t1 = System.currentTimeMillis() - t1;
        gbd.terminate(false);
        t = System.currentTimeMillis() - t;
        puts "distributed #{th} executed in #{t1} ms (#{t-t1} ms start-up)\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Client for a distributed computation.
=end
    def distClient(port=8114)
        s = @pset;
        es = ExecutableServer.new( port );
        es.init();
        return nil;
    end

=begin rdoc
Compute a normal form of this ideal with respect to reducer.
=end
    def NF(reducer)
        s = @pset;
        ff = s.list;
        gg = reducer.list;
        t = System.currentTimeMillis();
        nn = ReductionSeq.new().normalform(gg,ff);
        t = System.currentTimeMillis() - t;
        puts "sequential NF executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",nn);
    end

=begin rdoc
Compute the intersection of this and the given polynomial ring.
=end
    def intersectRing(ring)
        s = Ideal.new(@pset);
        nn = s.intersect(ring.ring);
        return SimIdeal.new(ring,"",nn.getList());
    end

=begin rdoc
Compute the intersection of this and the given ideal.
=end
    def intersect(id2)
        s1 = Ideal.new(@pset);
        s2 = Ideal.new(id2.pset);
        nn = s1.intersect(s2);
        return SimIdeal.new(@ring,"",nn.getList());
    end

=begin rdoc
Compute the elimination ideal of this and the given polynomial ring.
=end
    def eliminateRing(ring)
        s = Ideal.new(@pset);
        nn = s.eliminate(ring.ring);
        r = Ring.new( "", nn.getRing() );
        return SimIdeal.new(r,"",nn.getList());
    end

=begin rdoc
Compute the sum of this and the ideal.
=end
    def sum(other)
        s = Ideal.new(@pset);
        t = Ideal.new(other.pset);
        nn = s.sum( t );
        return SimIdeal.new(@ring,"",nn.getList());
    end

=begin rdoc
Optimize the term order on the variables.
=end
    def optimize()
        p = @pset;
        o = TermOrderOptimization.optimizeTermOrder(p);
        r = Ring.new("",o.ring);
        return SimIdeal.new(r,"",o.list);
    end

=begin rdoc
Compute real roots of 0-dim ideal.
=end
    def realRoots()
        ii = Ideal.new(@pset);
        @roots = PolyUtilApp.realAlgebraicRoots(ii);
        for r in @roots
            r.doDecimalApproximation();
        end
        return @roots;
    end

=begin rdoc
Print decimal approximation of real roots of 0-dim ideal.
=end
    def realRootsPrint()
        if @roots == nil
            ii = Ideal.new(@pset);
            @roots = PolyUtilApp.realAlgebraicRoots(ii);
            for r in @roots
                r.doDecimalApproximation();
            end
        end
        for ir in @roots
            for dr in ir.decimalApproximation()
                puts dr.to_s;
            end
            puts;
        end
    end

=begin rdoc
Compute radical decomposition of this ideal.
=end
    def radicalDecomp()
        ii = Ideal.new(@pset);
        @radical = ii.radicalDecomposition();
        return @radical;
    end

=begin rdoc
Compute complex roots of 0-dim ideal.
=end
    def complexRoots()
        ii = Ideal.new(@pset);
        @croots = PolyUtilApp.complexAlgebraicRoots(ii);
        for r in @croots
            r.doDecimalApproximation();
        end
        return @croots;
    end

=begin rdoc
Print decimal approximation of complex roots of 0-dim ideal.
=end
    def complexRootsPrint()
        if @croots == nil
            ii = Ideal.new(@pset);
            @croots = PolyUtilApp.complexAlgebraicRoots(ii);
            for r in @croots
                r.doDecimalApproximation();
            end
        end
        for ic in @croots
            for dc in ic.decimalApproximation()
                puts dc.to_s;
            end
            puts;
        end
    end

=begin rdoc
Compute prime decomposition of this ideal.
=end
    def primeDecomp()
        ii = Ideal.new(@pset);
        @prime = ii.primeDecomposition();
        return @prime;
    end

=begin rdoc
Compute primary decomposition of this ideal.
=end
    def primaryDecomp()
        ii = Ideal.new(@pset);
##         if @prime == nil:
##             @prime = I.primeDecomposition();
        @primary = ii.primaryDecomposition();
        return @primary;
    end

=begin rdoc
Convert rational coefficients to integer coefficients.
=end
    def toInteger()
        p = @pset;
        l = p.list;
        r = p.ring;
        ri = GenPolynomialRing.new( BigInteger.new(), r.nvar, r.tord, r.vars );
        pi = PolyUtil.integerFromRationalCoefficients(ri,l);
        r = Ring.new("",ri);
        return SimIdeal.new(r,"",pi);
    end

=begin rdoc
Convert integer coefficients to modular coefficients.
=end
    def toModular(mf)
        p = @pset;
        l = p.list;
        r = p.ring;
        rm = GenPolynomialRing.new( mf, r.nvar, r.tord, r.vars );
        pm = PolyUtil.fromIntegerCoefficients(rm,l);
        r = Ring.new("",rm);
        return SimIdeal.new(r,"",pm);
    end

=begin rdoc
Compute a Characteristic Set.
=end
    def CS()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list;
        t = System.currentTimeMillis();
        if cofac.isField()
            gg = CharacteristicSetWu.new().characteristicSet(ff);
        else
            puts "CS not implemented for coefficients #{cofac.toScriptFactory()}\n"; 
            gg = nil;
        end
        t = System.currentTimeMillis() - t;
        puts "sequential char set executed in #{t} ms\n"; 
        return SimIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test for Characteristic Set.
=end
    def isCS()
        s = @pset;
        cofac = s.ring.coFac;
        ff = s.list.clone();
        Collections.reverse(ff); # todo
        t = System.currentTimeMillis();
        if cofac.isField()
            b = CharacteristicSetWu.new().isCharacteristicSet(ff);
        else
            puts "isCS not implemented for coefficients #{cofac.toScriptFactory()}\n"; 
            b = false;
        end
        t = System.currentTimeMillis() - t;
        #puts "sequential is char set executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute a normal form of polynomial p with respect this characteristic set.
=end
    def csReduction(p)
        s = @pset;
        ff = s.list.clone();
        Collections.reverse(ff); # todo
        if p.is_a? RingElem
            p = p.elem;
        end
        t = System.currentTimeMillis();
        nn = CharacteristicSetWu.new().characteristicSetReduction(ff,p);
        t = System.currentTimeMillis() - t;
        #puts "sequential char set reduction executed in #{t} ms\n"; 
        return RingElem.new(nn);
    end

## =begin rdoc
## Syzygy of generating polynomials.
## =end
##     def syzygy()
##         p = @pset;
##         l = p.list;
##         s = SyzygyAbstract().zeroRelations( l );
##         m = Module("",p.ring);
##         return SubModule(m,"",s);
##     end

end


=begin rdoc
Represents a JAS polynomial ideal with polynomial coefficients.

Methods to compute comprehensive Groebner bases.
=end
class ParamIdeal

=begin rdoc
Parametric ideal constructor.
=end
    def initialize(ring,polystr="",list=nil,gbsys=nil)
        @ring = ring;
        if list == nil and polystr != nil
           sr = StringReader.new( polystr );
           tok = GenPolynomialTokenizer.new(ring.ring,sr);
           @list = tok.nextPolynomialList();
        else
           @list = rbarray2arraylist(list,rec=1);
        end
        @gbsys = gbsys;
        @pset = OrderedPolynomialList.new(ring.ring,@list);
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        if @gbsys == nil
            return @pset.toScript();
        else
#            return @pset.toScript() + "\n" + @gbsys.toScript();
            return @pset.toScript() + "\n" + @gbsys.to_s;
        end
    end

=begin rdoc
Optimize the term order on the variables of the coefficients.
=end
    def optimizeCoeff()
        p = @pset;
        o = TermOrderOptimization.optimizeTermOrderOnCoefficients(p);
        r = Ring.new("",o.ring);
        return ParamIdeal.new(r,"",o.list);
    end

=begin rdoc
Optimize the term order on the variables of the quotient coefficients.
=end
    def optimizeCoeffQuot()
        p = @pset;
        l = p.list;
        r = p.ring;
        q = r.coFac;
        c = q.ring;
        rc = GenPolynomialRing.new( c, r.nvar, r.tord, r.vars );
        #puts "rc = ", rc;        
        lp = PolyUfdUtil.integralFromQuotientCoefficients(rc,l);
        #puts "lp = ", lp;
        pp = PolynomialList.new(rc,lp);
        #puts "pp = ", pp;        
        oq = TermOrderOptimization.optimizeTermOrderOnCoefficients(pp);
        oor = oq.ring;
        qo = oor.coFac;
        cq = QuotientRing.new( qo );
        rq = GenPolynomialRing.new( cq, r.nvar, r.tord, r.vars );
        #puts "rq = ", rq;        
        o = PolyUfdUtil.quotientFromIntegralCoefficients(rq,oq.list);
        r = Ring.new("",rq);
        return ParamIdeal.new(r,"",o);
    end

=begin rdoc
Convert rational function coefficients to integral function coefficients.
=end
    def toIntegralCoeff()
        p = @pset;
        l = p.list;
        r = p.ring;
        q = r.coFac;
        c = q.ring;
        rc = GenPolynomialRing.new( c, r.nvar, r.tord, r.vars );
        #puts "rc = ", rc;        
        lp = PolyUfdUtil.integralFromQuotientCoefficients(rc,l);
        #puts "lp = ", lp;
        r = Ring.new("",rc);
        return ParamIdeal.new(r,"",lp);
    end

=begin rdoc
Convert integral function coefficients to modular function coefficients.
=end
    def toModularCoeff(mf)
        p = @pset;
        l = p.list;
        r = p.ring;
        c = r.coFac;
        #puts "c = ", c;
        cm = GenPolynomialRing.new( mf, c.nvar, c.tord, c.vars );
        #puts "cm = ", cm;
        rm = GenPolynomialRing.new( cm, r.nvar, r.tord, r.vars );
        #puts "rm = ", rm;
        pm = PolyUfdUtil.fromIntegerCoefficients(rm,l);
        r = Ring.new("",rm);
        return ParamIdeal.new(r,"",pm);
    end

=begin rdoc
Convert integral function coefficients to rational function coefficients.
=end
    def toQuotientCoeff()
        p = @pset;
        l = p.list;
        r = p.ring;
        c = r.coFac;
        #puts "c = ", c;
        q = QuotientRing.new(c);
        #puts "q = ", q;
        qm = GenPolynomialRing.new( q, r.nvar, r.tord, r.vars );
        #puts "qm = ", qm;
        pm = PolyUfdUtil.quotientFromIntegralCoefficients(qm,l);
        r = Ring.new("",qm);
        return ParamIdeal.new(r,"",pm);
    end

=begin rdoc
Compute a Groebner base.
=end
    def GB()
        ii = SimIdeal.new(@ring,"",@pset.list);
        g = ii.GB();
        return ParamIdeal.new(g.ring,"",g.pset.list);
    end

=begin rdoc
Test if this is a Groebner base.
=end
    def isGB()
        ii = SimIdeal.new(@ring,"",@pset.list);
        return ii.isGB();
    end

=begin rdoc
Compute a comprehensive Groebner base.
=end
    def CGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        if @gbsys == nil
            @gbsys = ComprehensiveGroebnerBaseSeq.new(@ring.ring.coFac).GBsys(ff);
        end
        gg = @gbsys.getCGB();
        t = System.currentTimeMillis() - t;
        puts "sequential comprehensive executed in #{t} ms\n"; 
        return ParamIdeal.new(@ring,"",gg,@gbsys);
    end

=begin rdoc
Compute a comprehensive Groebner system.
=end
    def CGBsystem()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        ss = ComprehensiveGroebnerBaseSeq.new(@ring.ring.coFac).GBsys(ff);
        t = System.currentTimeMillis() - t;
        puts "sequential comprehensive system executed in #{t} ms\n"; 
        return ParamIdeal.new(@ring,nil,ff,ss);
    end

=begin rdoc
Test if this is a comprehensive Groebner base.
=end
    def isCGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        b = ComprehensiveGroebnerBaseSeq.new(@ring.ring.coFac).isGB(ff);
        t = System.currentTimeMillis() - t;
        puts "isCGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Test if this is a comprehensive Groebner system.
=end
    def isCGBsystem()
        s = @pset;
        ss = @gbsys;
        t = System.currentTimeMillis();
        b = ComprehensiveGroebnerBaseSeq.new(@ring.ring.coFac).isGBsys(ss);
        t = System.currentTimeMillis() - t;
        puts "isCGBsystem executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Convert Groebner system to a representation with regular ring coefficents.
=end
    def regularRepresentation()
        if @gbsys == nil
            return nil;
        end
        gg = PolyUtilApp.toProductRes(@gbsys.list);
        ring = Ring.new(nil,gg[0].ring);
        return ParamIdeal.new(ring,nil,gg);
    end

=begin rdoc
Convert Groebner system to a boolean closed representation with regular ring coefficents.
=end
    def regularRepresentationBC()
        if @gbsys == nil
            return nil;
        end
        gg = PolyUtilApp.toProductRes(@gbsys.list);
        ring = Ring.new(nil,gg[0].ring);
        res = RReductionSeq.new();
        gg = res.booleanClosure(gg);
        return ParamIdeal.new(ring,nil,gg);
    end

=begin rdoc
Compute a Groebner base over a regular ring.
=end
    def regularGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        gg = RGroebnerBasePseudoSeq.new(@ring.ring.coFac).GB(ff);
        t = System.currentTimeMillis() - t;
        puts "sequential regular GB executed in #{t} ms\n"; 
        return ParamIdeal.new(@ring,nil,gg);
    end

=begin rdoc
Test if this is Groebner base over a regular ring.
=end
    def isRegularGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        b = RGroebnerBasePseudoSeq.new(@ring.ring.coFac).isGB(ff);
        t = System.currentTimeMillis() - t;
        puts "isRegularGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Get each component (slice) of regular ring coefficients separate.
=end
    def stringSlice()
        s = @pset;
        b = PolyUtilApp.productToString(s);
        return b;
    end

end

include_class "edu.jas.gbmod.ModGroebnerBaseAbstract";
include_class "edu.jas.gbmod.ModSolvableGroebnerBaseAbstract";
include_class "edu.jas.gbmod.SolvableSyzygyAbstract";
include_class "edu.jas.gbmod.SyzygyAbstract";


=begin rdoc
Represents a JAS solvable polynomial ring: GenSolvablePolynomialRing.

Has a method to create solvable ideals.
=end
class SolvableRing < Ring

=begin rdoc
Solvable polynomial ring constructor.
=end
    def initialize(ringstr="",ring=nil)
        if ring == nil
           sr = StringReader.new( ringstr );
           tok = GenPolynomialTokenizer.new(sr);
           @pset = tok.nextSolvablePolynomialSet();
           @ring = @pset.ring;
        else
           @ring = ring;
        end
        if not @ring.isAssociative()
           puts "warning: ring is not associative";
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript(); #.to_s;
    end

=begin rdoc
Create a solvable ideal.
=end
    def ideal(ringstr="",list=nil)
        return SolvableIdeal.new(self,ringstr,list);
    end

=begin rdoc
Get the one of the solvable polynomial ring.
=end
    def one()
        return RingElem.new( @ring.getONE() );
    end

=begin rdoc
Get the zero of the solvable polynomial ring.
=end
    def zero()
        return RingElem.new( @ring.getZERO() );
    end

=begin rdoc
Create an element from a string or object.
=end
    def element(poly)
        if not poly.is_a? String 
           begin
              if @ring == poly.ring 
                 return RingElem.new(poly);
              end
           rescue Exception => e
              # pass
           end
           poly = str(poly);
        end
        ii = SolvableIdeal.new(self, "( " + poly + " )");
        list = ii.pset.list;
        if list.size > 0
            return RingElem.new( list[0] );
        end
    end

end


=begin rdoc
Represents a JAS solvable polynomial ring: GenSolvablePolynomialRing.

Provides more convenient constructor. 
Then returns a Ring.
=end
class SolvPolyRing < SolvableRing

=begin rdoc
Ring constructor.

coeff = factory for coefficients,
vars = string with variable names,
order = term order,
rel = triple list of relations. (e,f,p,...) with e * f = p as relation.
=end
    def initialize(coeff,vars,order,rel=nil)
        if coeff == nil
            raise ValueError, "No coefficient given."
        end
        cf = coeff;
        if coeff.is_a? RingElem
            cf = coeff.elem.factory();
        end
        if coeff.is_a? Ring
            cf = coeff.ring;
        end
        if vars == nil
            raise ValueError, "No variable names given."
        end
        names = vars;
        if vars.is_a? String
            names = GenPolynomialTokenizer.variableList(vars);
        end
        nv = names.size;
        to = PolyRing.lex;
        if order.is_a? TermOrder
            to = order;
        end
        ring = GenSolvablePolynomialRing.new(cf,nv,to,names);
        if rel != nil
            #puts "rel = " + str(rel);
            table = ring.table;
            ll = [];
            for x in rel
                if x.is_a? RingElem
                    x = x.elem;
                end
                ll << x;
            end
            #puts "rel = " + str(L);
	    (0..ll.size-1).step(3) { |i|
                table.update( ll[i], ll[i+1], ll[i+2] );
	    }
        end
        @ring = ring;
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript();
    end
end


=begin rdoc
Represents a JAS solvable polynomial ideal.

Methods for left, right two-sided Groebner basees and others.
=end
class SolvableIdeal

    attr_reader :pset, :ring, :list

=begin rdoc
Constructor for an ideal in a solvable polynomial ring.
=end
    def initialize(ring,ringstr="",list=nil)
        @ring = ring;
        if list == nil
           sr = StringReader.new( ringstr );
           tok = GenPolynomialTokenizer.new(ring.ring,sr);
           @list = tok.nextSolvablePolynomialList();
        else
           @list = rbarray2arraylist(list,rec=1);
        end
        @pset = OrderedPolynomialList.new(ring.ring,@list);
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @pset.toScript().to_s;
    end

=begin rdoc
Compute a left Groebner base.
=end
    def leftGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        gg = SolvableGroebnerBaseSeq.new().leftGB(ff);
        t = System.currentTimeMillis() - t;
        puts "executed leftGB in #{t} ms\n"; 
        return SolvableIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is a left Groebner base.
=end
    def isLeftGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq.new().isLeftGB(ff);
        t = System.currentTimeMillis() - t;
        puts "isLeftGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute a two-sided Groebner base.
=end
    def twosidedGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        gg = SolvableGroebnerBaseSeq.new().twosidedGB(ff);
        t = System.currentTimeMillis() - t;
        puts "executed twosidedGB in #{t} ms\n"; 
        return SolvableIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is a two-sided Groebner base.
=end
    def isTwosidedGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq.new().isTwosidedGB(ff);
        t = System.currentTimeMillis() - t;
        puts "isTwosidedGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute a right Groebner base.
=end
    def rightGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        gg = SolvableGroebnerBaseSeq.new().rightGB(ff);
        t = System.currentTimeMillis() - t;
        puts "executed rightGB in #{t} ms\n"; 
        return SolvableIdeal.new(@ring,"",gg);
    end

=begin rdoc
Test if this is a right Groebner base.
=end
    def isRightGB()
        s = @pset;
        ff = s.list;
        t = System.currentTimeMillis();
        b = SolvableGroebnerBaseSeq.new().isRightGB(ff);
        t = System.currentTimeMillis() - t;
        puts "isRightGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute the intersection of this and the polynomial ring.
=end
    def intersect(ring)
        s = SolvableIdeal.new(@pset);
        nn = s.intersect(ring.ring);
        return SolvableIdeal.new(@ring,"",nn.getList());
    end

=begin rdoc
Compute the sum of this and the other ideal.
=end
    def sum(other)
        s = SolvableIdeal.new(@pset);
        t = SolvableIdeal.new(other.pset);
        nn = s.sum( t );
        return SolvableIdeal.new(@ring,"",nn.getList());
    end

=begin rdoc
Compute a left Groebner base in parallel.
=end
    def parLeftGB(th)
        s = @pset;
        ff = s.list;
        bbpar = SolvableGroebnerBaseParallel.new(th);
        t = System.currentTimeMillis();
        gg = bbpar.leftGB(ff);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        puts "parallel #{th} leftGB executed in #{t} ms\n"; 
        return SolvableIdeal.new(@ring,"",gg);
    end

=begin rdoc
Compute a two-sided Groebner base in parallel.
=end
    def parTwosidedGB(th)
        s = @pset;
        ff = s.list;
        bbpar = SolvableGroebnerBaseParallel.new(th);
        t = System.currentTimeMillis();
        gg = bbpar.twosidedGB(ff);
        t = System.currentTimeMillis() - t;
        bbpar.terminate();
        puts "parallel #{th} twosidedGB executed in #{t} ms\n"; 
        return SolvableIdeal.new(@ring,"",gg);
    end

end


=begin rdoc
Represents a JAS module over a polynomial ring.

Method to create sub-modules.
=end
class CommutativeModule
    attr_reader :ring, :mset, :cols

=begin rdoc
Module constructor.
=end
    def initialize(modstr="",ring=nil,cols=0)
        if ring == nil
           sr = StringReader.new( modstr );
           tok = GenPolynomialTokenizer.new(sr);
           @mset = tok.nextSubModuleSet();
           if @mset.cols >= 0
               @cols = @mset.cols;
           else
               @cols = cols;
           end
        else
           @mset = ModuleList.new(ring.ring,nil);
           @cols = cols;
        end
        @ring = @mset.ring;
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @mset.toScript();
    end

=begin rdoc
Create a sub-module.
=end
    def submodul(modstr="",list=nil)
        return SubModule.new(self,modstr,list);
    end

=begin rdoc
Create an element from a string.
=end
    def element(mods)
        if not mods.is_a? String 
           begin
              if @ring == mods.ring 
                 return RingElem.new(mods);
              end
           rescue Exception => e
              # pass
           end
           mods = str(mods);
        end
        ii = SubModule.new( "( " + mods + " )");
        list = ii.mset.list;
        if list.size > 0
            return RingElem.new( list[0] );
        end
    end

=begin rdoc
Get the generators of this module.
=end
    def gens()
        gm = GenVectorModul.new(@ring,@cols);
        ll = gm.generators();
        nn = ll.map { |e| RingElem.new(e) }; # want use val here, but can not
        return nn;
    end

end


=begin rdoc
Represents a JAS sub-module over a polynomial ring.

Methods to compute Groebner bases.
=end
class SubModule
    attr_reader :modu, :mset, :pset, :cols, :rows, :list

=begin rdoc
Constructor for a sub-module.
=end
    def initialize(modu,modstr="",list=nil)
        @modu = modu;
        if list == nil
           sr = StringReader.new( modstr );
           tok = GenPolynomialTokenizer.new(modu.ring,sr);
           @list = tok.nextSubModuleList();
        else
            if list.is_a? Array
                if list.size != 0
                    if list[0].is_a? RingElem
                        list = list.map { |re| re.elem  };
                    end
                end
                @list = rbarray2arraylist(list,@modu.ring,rec=2);
            else
                @list = list;
            end
        end
        #puts "list = ", str(list);
        #e = @list[0];
        #puts "e = ", e;
        @mset = OrderedModuleList.new(modu.ring,@list);
        @cols = @mset.cols;
        @rows = @mset.rows;
        @pset = @mset.getPolynomialList();
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @mset.toScript(); # + "\n\n" + str(@pset);
    end

=begin rdoc
Compute a Groebner base.
=end
    def GB()
        t = System.currentTimeMillis();
        gg = ModGroebnerBaseAbstract.new().GB(@mset);
        t = System.currentTimeMillis() - t;
        puts "executed module GB in #{t} ms\n"; 
        return SubModule.new(@modu,"",gg.list);
    end

=begin rdoc
Test if this is a Groebner base.
=end
    def isGB()
        t = System.currentTimeMillis();
        b = ModGroebnerBaseAbstract.new().isGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "module isGB executed in #{t} ms\n"; 
        return b;
    end

## =begin rdoc
## Test if this is a syzygy of the polynomials in g.
## =end
##     def isSyzygy(g):
##         l = @list;
##         puts "l = #{l}"; 
##         puts "g = #{g}"; 
##         t = System.currentTimeMillis();
##         z = SyzygyAbstract().isZeroRelation( l, g.list );
##         t = System.currentTimeMillis() - t;
##         puts "executed isSyzygy in #{t} ms\n"; 
##         return z;
##     end

end


=begin rdoc
Represents a JAS module over a solvable polynomial ring.

Method to create solvable sub-modules.
=end
class SolvableModule < CommutativeModule
    attr_reader :ring, :mset, :cols

=begin rdoc
Solvable module constructor.
=end
    def initialize(modstr="",ring=nil,cols=0)
        if ring == nil
           sr = StringReader.new( modstr );
           tok = GenPolynomialTokenizer.new(sr);
           @mset = tok.nextSolvableSubModuleSet();
           if @mset.cols >= 0
               @cols = @mset.cols;
           end
        else
           @mset = ModuleList.new(ring.ring,nil);
           @cols = cols;
        @ring = @mset.ring;
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @mset.toScript();
    end

=begin rdoc
Create a solvable sub-module.
=end
    def submodul(modstr="",list=nil)
        return SolvableSubModule.new(self,modstr,list);
    end

=begin rdoc
Create an element from a string.
=end
    def element(mods)
        if not mods.is_a? String 
           begin
              if @ring == mods.ring 
                 return RingElem.new(mods);
              end
           rescue Exception => e
              # pass
           end
           mods = str(mods);
        end
        ii = SolvableSubModule.new( "( " + mods + " )");
        list = ii.mset.list;
        if list.size > 0
            return RingElem.new( list[0] );
        end
    end

end


=begin rdoc
Represents a JAS sub-module over a solvable polynomial ring.

Methods to compute left, right and two-sided Groebner bases.
=end
class SolvableSubModule
    attr_reader :modu, :mset, :cols, :rows, :list

=begin rdoc
Constructor for sub-module over a solvable polynomial ring.
=end
    def initialize(modu,modstr="",list=nil)
        @modu = modu;
        if list == nil
           sr = StringReader.new( modstr );
           tok = GenPolynomialTokenizer.new(modu.ring,sr);
           @list = tok.nextSolvableSubModuleList();
        else
            if list.is_a? Array
                @list = rbarray2arraylist(list,@modu.ring,rec=2);
            else
                @list = list;
            end
        end
        @mset = OrderedModuleList.new(modu.ring,@list);
        @cols = @mset.cols;
        @rows = @mset.rows;
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @mset.toScript(); # + "\n\n" + str(@pset);
    end

=begin rdoc
Compute a left Groebner base.
=end
    def leftGB()
        t = System.currentTimeMillis();
        gg = ModSolvableGroebnerBaseAbstract.new().leftGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "executed left module GB in #{t} ms\n"; 
        return SolvableSubModule.new(@modu,"",gg.list);
    end

=begin rdoc
Test if this is a left Groebner base.
=end
    def isLeftGB()
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract.new().isLeftGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "module isLeftGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute a two-sided Groebner base.
=end
    def twosidedGB()
        t = System.currentTimeMillis();
        gg = ModSolvableGroebnerBaseAbstract.new().twosidedGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "executed in #{t} ms\n"; 
        return SolvableSubModule.new(@modu,"",gg.list);
    end

=begin rdoc
Test if this is a two-sided Groebner base.
=end
    def isTwosidedGB()
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract.new().isTwosidedGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "module isTwosidedGB executed in #{t} ms\n"; 
        return b;
    end

=begin rdoc
Compute a right Groebner base.
=end
    def rightGB()
        t = System.currentTimeMillis();
        gg = ModSolvableGroebnerBaseAbstract.new().rightGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "executed module rightGB in #{t} ms\n"; 
        return SolvableSubModule.new(@modu,"",gg.list);
    end

=begin rdoc
Test if this is a right Groebner base.
=end
    def isRightGB()
        t = System.currentTimeMillis();
        b = ModSolvableGroebnerBaseAbstract.new().isRightGB(@mset);
        t = System.currentTimeMillis() - t;
        puts "module isRightGB executed in #{t} ms\n"; 
        return b;
    end

end


include_class "edu.jas.ps.UnivPowerSeries";
include_class "edu.jas.ps.UnivPowerSeriesRing";
include_class "edu.jas.ps.UnivPowerSeriesMap";
include_class "edu.jas.ps.Coefficients";
include_class "edu.jas.ps.MultiVarPowerSeries";
include_class "edu.jas.ps.MultiVarPowerSeriesRing";
include_class "edu.jas.ps.MultiVarPowerSeriesMap";
include_class "edu.jas.ps.MultiVarCoefficients";
include_class "edu.jas.ps.StandardBaseSeq";


=begin rdoc
Represents a JAS power series ring: UnivPowerSeriesRing.

Methods for univariate power series arithmetic.
=end
class SeriesRing

    attr_reader :ring

=begin rdoc
Ring constructor.
=end
    def initialize(ringstr="",truncate=nil,ring=nil,cofac=nil,name="z")
        if ring == nil
            if ringstr.size > 0
                sr = StringReader.new( ringstr );
                tok = GenPolynomialTokenizer.new(sr);
                pset = tok.nextPolynomialSet();
                ring = pset.ring;
                vname = ring.vars;
                name = vname[0];
                cofac = ring.coFac;
            end
            if cofac.is_a? RingElem
                cofac = cofac.elem;
            end
            if truncate == nil
                @ring = UnivPowerSeriesRing.new(cofac,name);
            else
                @ring = UnivPowerSeriesRing.new(cofac,truncate,name);
            end
        else
           @ring = ring;
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript();
    end

=begin rdoc
Get the generators of the power series ring.
=end
    def gens()
        ll = @ring.generators();
        nn = ll.map { |e| RingElem.new(e) };
        return nn;
    end

=begin rdoc
Get the one of the power series ring.
=end
    def one()
        return RingElem.new( @ring.getONE() );
    end

=begin rdoc
Get the zero of the power series ring.
=end
    def zero()
        return RingElem.new( @ring.getZERO() );
    end

=begin rdoc
Get a random power series.
=end
    def random(n)
        return RingElem.new( @ring.random(n) );
    end

=begin rdoc
Get the exponential power series.
=end
    def exp()
        return RingElem.new( @ring.getEXP() );
    end

=begin rdoc
Get the sinus power series.
=end
    def sin()
        return RingElem.new( @ring.getSIN() );
    end

=begin rdoc
Get the cosinus power series.
=end
    def cos()
        return RingElem.new( @ring.getCOS() );
    end

=begin rdoc
Get the tangens power series.
=end
    def tan()
        return RingElem.new( @ring.getTAN() );
    end

=begin rdoc
(Inner) class which extends edu.jas.ps.Coefficients

ifunc(int i) must return a value which is used in ((RingFactory)cofac).fromInteger().
jfunc(int i) must return a value of type ring.coFac.
=end
        class Ucoeff < Coefficients
            def initialize(ifunc,jfunc,cofac=nil)
                #puts "ifunc = " + ifunc.to_s + ",";
                #puts "jfunc = " + jfunc.to_s + ",";
                #puts "cofac = " + cofac.to_s + ",";
                super();
                if jfunc == nil && cofac == nil 
                  raise "invalid arguments"
                end
                @coFac = cofac;
                @ifunc = ifunc;
                @jfunc = jfunc;
            end
            def generate(i)
                if @jfunc == nil
                    return @coFac.fromInteger( @ifunc.call(i) );
                else
                    return @jfunc.call(i);
                end
            end
        end

=begin rdoc
Create a power series with given generating function.

ifunc(int i) must return a value which is used in RingFactory.fromInteger().
jfunc(int i) must return a value of type ring.coFac.
clazz must implement the Coefficients abstract class.
=end
    def create(ifunc,jfunc=nil,clazz=nil)
        if clazz == nil
            #puts "ifunc = " + ifunc.to_s + ".";
            #puts "jfunc = " + jfunc.to_s + ".";
            #puts "clazz = " + clazz.to_s + ".";
            cf = Ucoeff.new(ifunc,jfunc,@ring.coFac);
            #puts "cf    = " + cf.to_s + ".";
            ps = UnivPowerSeries.new( @ring, cf );
        else
            ps = UnivPowerSeries.new( @ring, clazz );
        end
        return RingElem.new( ps );
    end

=begin rdoc
Create a power series as fixed point of the given mapping.

psmap must implement the UnivPowerSeriesMap interface.
=end
    def fixPoint(psmap)
        ps = @ring.fixPoint( psmap );
        return RingElem.new( ps );
    end

=begin rdoc
Compute the greatest common divisor of a and b.
=end
    def gcd(a,b)
        if a.is_a? RingElem
            a = a.elem;
        end
        if b.is_a? RingElem
            b = b.elem;
        end
        return RingElem.new( a.gcd(b) );
    end

=begin rdoc
Convert a GenPolynomial to a power series.
=end
    def fromPoly(a)
        if a.is_a? RingElem
            a = a.elem;
        end
        return RingElem.new( @ring.fromPolynomial(a) );
    end

end


=begin rdoc
Represents a JAS power series ring: MultiVarPowerSeriesRing.

Methods for multivariate power series arithmetic.
=end
class MultiSeriesRing

    attr_reader :ring

=begin rdoc
Ring constructor.
=end
    def initialize(ringstr="",truncate=nil,ring=nil,cofac=nil,names=nil)
        if ring == nil
            if ringstr.size > 0
                sr = StringReader.new( ringstr );
                tok = GenPolynomialTokenizer.new(sr);
                pset = tok.nextPolynomialSet();
                ring = pset.ring;
                names = ring.vars;
                cofac = ring.coFac;
            end
            if cofac.is_a? RingElem
                cofac = cofac.elem;
            end
            if truncate == nil
                @ring = MultiVarPowerSeriesRing.new(cofac,names);
            else
                @ring = MultiVarPowerSeriesRing.new(cofac,names.size,truncate,names);
            end
        else
           @ring = ring;
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @ring.toScript();
    end

=begin rdoc
Get the generators of the power series ring.
=end
    def gens()
        ll = @ring.generators();
        nn = ll.map { |e| RingElem.new(e) };
        return nn;
    end

=begin rdoc
Get the one of the power series ring.
=end
    def one()
        return RingElem.new( @ring.getONE() );
    end

=begin rdoc
Get the zero of the power series ring.
=end
    def zero()
        return RingElem.new( @ring.getZERO() );
    end

=begin rdoc
Get a random power series.
=end
    def random(n)
        return RingElem.new( @ring.random(n) );
    end

=begin rdoc
Get the exponential power series, var r.
=end
    def exp(r)
        return RingElem.new( @ring.getEXP(r) );
    end

=begin rdoc
Get the sinus power series, var r.
=end
    def sin(r)
        return RingElem.new( @ring.getSIN(r) );
    end

=begin rdoc
Get the cosinus power series, var r.
=end
    def cos(r)
        return RingElem.new( @ring.getCOS(r) );
    end

=begin rdoc
Get the tangens power series, var r.
=end
    def tan(r)
        return RingElem.new( @ring.getTAN(r) );
    end


=begin
(Inner) class which extends edu.jas.ps.MultiVarCoefficients

ring must be a polynomial or multivariate power series ring.
ifunc(int i) must return a value which is used in ((RingFactory)cofac).fromInteger().
jfunc(int i) must return a value of type ring.coFac.
=end
        class Mcoeff < MultiVarCoefficients
            def initialize(ring,ifunc=nil,jfunc=nil)
                super(ring);
                @coFac = ring.coFac;
                @ifunc = ifunc;
                @jfunc = jfunc;
            end
            def generate(i)
                if @jfunc == nil
                    return @coFac.fromInteger( @ifunc.call(i) );
                else
                    return @jfunc.call(i);
                end
            end
        end

=begin rdoc
Create a power series with given generating function.

ifunc(int i) must return a value which is used in RingFactory.fromInteger().
jfunc(int i) must return a value of type ring.coFac.
clazz must implement the Coefficients abstract class.
=end
    def create(ifunc=nil,jfunc=nil,clazz=nil)
        #puts "ifunc"
        if clazz == nil
            cf = Mcoeff.new(@ring,ifunc,jfunc);
            ps = MultiVarPowerSeries.new( @ring, cf );
        else
            ps = MultiVarPowerSeries.new( @ring, clazz );
        end
        #puts "ps ", ps.toScript();
        return RingElem.new( ps );
    end

=begin rdoc
Create a power series as fixed point of the given mapping.

psmap must implement the MultiVarPowerSeriesMap interface.
=end
    def fixPoint(psmap)
        ps = @ring.fixPoint( psmap );
        return RingElem.new( ps );
    end

=begin rdoc
Compute the greatest common divisor of a and b.
=end
    def gcd(a,b)
        if a.is_a? RingElem
            a = a.elem;
        end
        if b.is_a? RingElem
            b = b.elem;
        end
        return RingElem.new( a.gcd(b) );
    end

=begin rdoc
Convert a GenPolynomial to a power series.
=end
    def fromPoly(a)
        if a.is_a? RingElem
            a = a.elem;
        end
        return RingElem.new( @ring.fromPolynomial(a) );
    end

end


=begin rdoc
Represents a JAS power series ideal.

Method for Standard bases.
=end
class PSIdeal
    attr_reader :ring, :list

=begin rdoc
PSIdeal constructor.
=end
    def initialize(ring,polylist,ideal=nil,list=nil)
        if ring.is_a? Ring or ring.is_a? PolyRing
            ring = MultiVarPowerSeriesRing.new(ring.ring);
        elsif ring.is_a? MultiSeriesRing
            ring = ring.ring;
        end
        @ring = ring;
        #puts "ring = ", ring.toScript();
        if ideal != nil
           polylist = ideal.pset.list;
        end
        if list == nil
            @polylist = rbarray2arraylist( polylist.map { |a| a.elem } );
            #puts "polylist = ", @polylist;
            @list = @ring.fromPolynomial(@polylist);
        else
            @polylist = nil;
            @list = rbarray2arraylist( list.map { |a| a.elem } );
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @list.map { |a| a.toScript() }.join(", ");
    end

=begin rdoc
Compute a standard base.
=end
    def STD(trunc=nil)
        pr = @ring;
        if trunc != nil
            pr.setTruncate(trunc);
        end
        #puts "pr = ", pr.toScript();
        ff = @list;
        #puts "ff = ", ff;
        tm = StandardBaseSeq.new();
        t = System.currentTimeMillis();
        ss = tm.STD(ff);
        t = System.currentTimeMillis() - t;
        puts "sequential standard base executed in #{t} ms\n";
        #Sp = [ RingElem.new(a.asPolynomial()) for a in S ];
        sp = ss.map { |a| RingElem.new(a) };
        #return sp;
        return PSIdeal.new(@ring,nil,nil,sp);
    end

end


class Coeff < Coefficients
  def initialize(cof,&f)
      super() # this is important in jruby 1.5.6!
      #puts "cof type(#{cof}) = #{cof.class}\n";
      @coFac = cof;
      #puts "f type(#{f}) = #{f.class}\n";
      @func = f
  end
  def generate(i)
      #puts "f_3  type(#{@func}) = #{@func.class}\n";
      #puts "f_3  type(#{i}) = #{i.class}\n";
      #return @coFac.getZERO()
      c = @func.call(i)
      #puts "f_3  type(#{c}) = #{c.class}\n";
      if c.is_a? RingElem
          c = c.elem
      end
      return c
  end
end


=begin rdoc
Create JAS UnivPowerSeries as ring element.
=end
def PS(cofac,name,truncate=nil,&f) #=nil,truncate=nil)
    cf = cofac;
    if cofac.is_a? RingElem
        cf = cofac.elem.factory();
    end
    if cofac.is_a? Ring
        cf = cofac.ring;
    end
    if truncate.is_a? RingElem
        truncate = truncate.elem;
    end
    if truncate == nil
        ps = UnivPowerSeriesRing.new(cf,name);
    else
        ps = UnivPowerSeriesRing.new(cf,truncate,name);
    end
    #puts "ps type(#{ps}) = #{ps.class}\n";
    #puts "f  type(#{f}) = #{f.class}\n";
    if f == nil
        r = ps.getZERO();
    else
        #Bug in JRuby 1.5.6? move outside method
        r = UnivPowerSeries.new(ps,Coeff.new(cf,&f));
    end
    return RingElem.new(r);

end


class MCoeff < MultiVarCoefficients
  def initialize(r,&f)
      super(r) # this is important in jruby 1.5.6!
      @coFac = r.coFac;
      @func = f
  end
  def generate(i)
      a = @func.call(i);
      if a.is_a? RingElem
         a = a.elem;
      end
      #puts "f_5  type(#{a}) = #{a.class}\n";
      return a;
  end
end


=begin rdoc
Create JAS MultiVarPowerSeries as ring element.
=end
def MPS(cofac,names,truncate=nil,&f)
    cf = cofac;
    if cofac.is_a? RingElem
        cf = cofac.elem.factory();
    elsif cofac.is_a? Ring
        cf = cofac.ring;
    end
    vars = names;
    if vars.is_a? String
       vars = GenPolynomialTokenizer.variableList(vars);
    end
    nv = vars.size;
    if truncate.is_a? RingElem
        truncate = truncate.elem;
    end
    if truncate == nil
        ps = MultiVarPowerSeriesRing.new(cf,nv,vars);
    else
        ps = MultiVarPowerSeriesRing.new(cf,nv,vars,truncate);
    end
    if f == nil
        r = ps.getZERO();
    else
        r = MultiVarPowerSeries.new(ps,MCoeff.new(ps,&f));
        #puts "r = " + str(r);
    end
    return RingElem.new(r);

end

include_class "edu.jas.vector.GenVector";
include_class "edu.jas.vector.GenVectorModul";
include_class "edu.jas.vector.GenMatrix";
include_class "edu.jas.vector.GenMatrixRing";


=begin rdoc
Create JAS GenVector ring element.
=end
def Vec(cofac,n,v=nil)
    cf = cofac;
    if cofac.is_a? RingElem
        cf = cofac.elem.factory();
    elsif cofac.is_a? Ring
        cf = cofac.ring;
    end
    if n.is_a? RingElem
        n = n.elem;
    end
    if v.is_a? RingElem
        v = v.elem;
    end
    vr = GenVectorModul.new(cf,n);
    if v == nil
        r = GenVector.new(vr);
    else
        r = GenVector.new(vr,v);
    end
    return RingElem.new(r);
end

=begin rdoc
Create JAS GenMatrix ring element.
=end
def Mat(cofac,n,m,v=nil)
    cf = cofac;
    if cofac.is_a? RingElem
        cf = cofac.elem.factory();
    elsif cofac.is_a? Ring
        cf = cofac.ring;
    end
    if n.is_a? RingElem
        n = n.elem;
    end
    if m.is_a? RingElem
        m = m.elem;
    end
    if v.is_a? RingElem
        v = v.elem;
    end
    #puts "cf type(#{cf}) = #{cf.class}";
    if v.is_a? Array
        v = rbarray2arraylist(v,cf,rec=2);
    end
    mr = GenMatrixRing.new(cf,n,m);
    if v == nil
        r = GenMatrix.new(mr);
    else
        r = GenMatrix.new(mr,v);
    end
    return RingElem.new(r);
end


include_class "edu.jas.application.ExtensionFieldBuilder";

=begin rdoc
Extension field builder.
Construction of extension field towers according to then builder pattern.
=end
class EF
    attr_reader :builder

=begin rdoc
Constructor to set base field.
=end
    def initialize(base)
        if base.is_a? RingElem
            #factory = base.elem;
            factory = base.ring;
        else
            factory = base;
        end
        begin
            factory = factory.factory();
        rescue
            #pass
        end
        puts "extension field factory: " + factory.toScript(); # + " :: " + factory.toString();
        if factory.is_a? ExtensionFieldBuilder
            @builder = factory;
        else
            @builder = ExtensionFieldBuilder.new(factory);
        end
    end

=begin rdoc
Create a string representation.
=end
    def to_s()
        return @builder.toScript(); 
        #return @builder.toString(); 
    end

=begin rdoc 
Create an extension field.  If algebraic is given as
string expression, then an algebraic extension field is constructed,
else a transcendental extension field is constructed.  
=end 
    def extend(vars,algebraic=nil) 
        if algebraic == nil
            ef = @builder.transcendentExtension(vars);
        else
            ef = @builder.algebraicExtension(vars,algebraic);
        end
        return EF.new(ef.build());
    end

=begin rdoc
Create a real extension field.
Construct a real algebraic extension field with an isolating interval for a real root.
=end
    def realExtend(vars,algebraic,interval)
        ef = @builder.realAlgebraicExtension(vars,algebraic,interval);
        return EF.new(ef.build());
    end

=begin rdoc
Create a complex extension field.
Construct a complex algebraic extension field with an isolating rectangle for a complex root.
=end
    def complexExtend(vars,algebraic,rectangle)
        ef = @builder.complexAlgebraicExtension(vars,algebraic,rectangle);
        return EF.new(ef.build());
    end

=begin rdoc
Create an polynomial ring extension.
=end
    def polynomial(vars)
        ef = @builder.polynomialExtension(vars);
        return EF.new(ef.build());
    end

=begin rdoc
Get extension field tower.
=end
    def build()
        rf = @builder.build();
        if rf.is_a? GenPolynomialRing
            return PolyRing.new(rf.coFac,rf.getVars(),rf.tord);
        else
            return RingElem.new(rf.getZERO());
        end
    end

end


end

include JAS
