#!/bin/sh
# run some rb files

#OPTS="-J-cp ../lib/log4j.jar:../lib/junit.jar:."
OPTS="-J-cp ../lib/log4j-core-2.13.2.jar:../lib/log4j-api-2.13.2.jar:../lib/junit.jar-4.12.jar:../lib/hamcrest-core-1.3.jar:. -I."

echo jruby $OPTS -J-verbose:gc examples/all_rings.rb
time jruby $OPTS -J-verbose:gc examples/all_rings.rb
echo jruby $OPTS -J-verbose:gc examples/trinks.rb
time jruby $OPTS -J-verbose:gc examples/trinks.rb
echo jruby $OPTS -J-verbose:gc examples/0dim_primary-decomp.rb
time jruby $OPTS -J-verbose:gc examples/0dim_primary-decomp.rb
echo jruby $OPTS -J-verbose:gc examples/0dim_prime-decomp.rb
time jruby $OPTS -J-verbose:gc examples/0dim_prime-decomp.rb
echo jruby $OPTS -J-verbose:gc examples/0dim_radical.rb
time jruby $OPTS -J-verbose:gc examples/0dim_radical.rb
echo jruby $OPTS -J-verbose:gc examples/cgb_0.rb
time jruby $OPTS -J-verbose:gc examples/cgb_0.rb
echo jruby $OPTS -J-verbose:gc examples/cgb_2.rb
time jruby $OPTS -J-verbose:gc examples/cgb_2.rb
echo jruby $OPTS -J-verbose:gc examples/chebyshev.rb
time jruby $OPTS -J-verbose:gc examples/chebyshev.rb
echo jruby $OPTS -J-verbose:gc examples/e-gb.rb
time jruby $OPTS -J-verbose:gc examples/e-gb.rb
echo jruby $OPTS -J-verbose:gc examples/eliminate.rb
time jruby $OPTS -J-verbose:gc examples/eliminate.rb
echo jruby $OPTS -J-verbose:gc examples/factors.rb
time jruby $OPTS -J-verbose:gc examples/factors.rb
echo jruby $OPTS -J-verbose:gc examples/factors_abs.rb
time jruby $OPTS -J-verbose:gc examples/factors_abs.rb
echo jruby $OPTS -J-verbose:gc examples/factors_abs_complex.rb
time jruby $OPTS -J-verbose:gc examples/factors_abs_complex.rb
echo jruby $OPTS -J-verbose:gc examples/factors_abs_mult.rb
time jruby $OPTS -J-verbose:gc examples/factors_abs_mult.rb
echo jruby $OPTS -J-verbose:gc examples/factors_algeb.rb
time jruby $OPTS -J-verbose:gc examples/factors_algeb.rb
echo jruby $OPTS -J-verbose:gc examples/getstart.rb
time jruby $OPTS -J-verbose:gc examples/getstart.rb
echo jruby $OPTS -J-verbose:gc examples/hawes2.rb
time jruby $OPTS -J-verbose:gc examples/hawes2.rb
echo jruby $OPTS -J-verbose:gc examples/module.rb
time jruby $OPTS -J-verbose:gc examples/module.rb
echo jruby $OPTS -J-verbose:gc examples/polynomial.rb
time jruby $OPTS -J-verbose:gc examples/polynomial.rb
echo jruby $OPTS -J-verbose:gc examples/polypower.rb
time jruby $OPTS -J-verbose:gc examples/polypower.rb
echo jruby $OPTS -J-verbose:gc examples/powerseries.rb
time jruby $OPTS -J-verbose:gc examples/powerseries.rb
echo jruby $OPTS -J-verbose:gc examples/prime-decomp.rb
time jruby $OPTS -J-verbose:gc examples/prime-decomp.rb
