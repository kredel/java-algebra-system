#!/bin/sh
# run most rb files

echo time jruby examples/all_rings.rb
time jruby -J-cp ../lib/log4j.jar:../lib/junit.jar:. examples/all_rings.rb
echo time jruby examples/trinks.rb
time jruby -J-cp ../lib/log4j.jar:../lib/junit.jar:. examples/trinks.rb
