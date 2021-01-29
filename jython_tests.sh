#!/bin/sh
# run some py files

#OPTS="-J-cp ../lib/log4j.jar:../lib/junit.jar:."
OPTS="-J-cp ../lib/log4j-core-2.13.2.jar:../lib/log4j-api-2.13.2.jar:../lib/junit.jar-4.12.jar:../lib/hamcrest-core-1.3.jar:."

echo jython $OPTS -J-verbose:gc examples/all_rings.py
time jython $OPTS -J-verbose:gc examples/all_rings.py
echo jython $OPTS -J-verbose:gc examples/intprog.py
time jython $OPTS -J-verbose:gc examples/intprog.py
echo jython $OPTS -J-verbose:gc examples/intprog2.py
time jython $OPTS -J-verbose:gc examples/intprog2.py
echo jython $OPTS -J-verbose:gc examples/katsura.py
time jython $OPTS -J-verbose:gc examples/katsura.py
echo jython $OPTS -J-verbose:gc examples/preimage.py
time jython $OPTS -J-verbose:gc examples/preimage.py
echo jython $OPTS -J-verbose:gc examples/quantumplane_syz.py
time jython $OPTS -J-verbose:gc examples/quantumplane_syz.py
echo jython $OPTS -J-verbose:gc examples/rose.py
time jython $OPTS -J-verbose:gc examples/rose.py
echo jython $OPTS -J-verbose:gc examples/solvablemodule.py
time jython $OPTS -J-verbose:gc examples/solvablemodule.py
echo jython $OPTS -J-verbose:gc examples/solvmodright.py
time jython $OPTS -J-verbose:gc examples/solvmodright.py
echo jython $OPTS -J-verbose:gc examples/syz.py
time jython $OPTS -J-verbose:gc examples/syz.py
echo jython $OPTS -J-verbose:gc examples/syzsolv.py
time jython $OPTS -J-verbose:gc examples/syzsolv.py
echo jython $OPTS -J-verbose:gc examples/trinks.py
time jython $OPTS -J-verbose:gc examples/trinks.py
echo jython $OPTS -J-verbose:gc examples/mark.py
time jython $OPTS -J-verbose:gc examples/mark.py
echo jython $OPTS -J-verbose:gc examples/u_2_wa_1.py
time jython $OPTS -J-verbose:gc examples/u_2_wa_1.py
echo jython $OPTS -J-verbose:gc examples/u_sl_2.py
time jython $OPTS -J-verbose:gc examples/u_sl_2.py
echo jython $OPTS -J-verbose:gc examples/u_sl_3.py
time jython $OPTS -J-verbose:gc examples/u_sl_3.py
echo jython $OPTS -J-verbose:gc examples/u_sl_3_prod.py
time jython $OPTS -J-verbose:gc examples/u_sl_3_prod.py
echo jython $OPTS -J-verbose:gc examples/u_so_3.py
time jython $OPTS -J-verbose:gc examples/u_so_3.py
echo jython $OPTS -J-verbose:gc examples/wa_1.py
time jython $OPTS -J-verbose:gc examples/wa_1.py
echo jython $OPTS -J-verbose:gc examples/wa_32.py
time jython $OPTS -J-verbose:gc examples/wa_32.py
echo jython $OPTS -J-verbose:gc examples/wa_32_syz.py
time jython $OPTS -J-verbose:gc examples/wa_32_syz.py
echo jython $OPTS -J-verbose:gc examples/chebyshev.py
time jython $OPTS -J-verbose:gc examples/chebyshev.py
echo jython $OPTS -J-verbose:gc examples/legendre.py
time jython $OPTS -J-verbose:gc examples/legendre.py
echo jython $OPTS -J-verbose:gc examples/arith.py
time jython $OPTS -J-verbose:gc examples/arith.py
echo jython $OPTS -J-verbose:gc examples/powerseries.py
time jython $OPTS -J-verbose:gc examples/powerseries.py
echo jython $OPTS -J-verbose:gc examples/polynomial.py
time jython $OPTS -J-verbose:gc examples/polynomial.py
