#!/usr/bin/env bash

# this script is meant to be run from dspot module
# this script clean the .class inside the folder src/main/resources
# and copy the new one from target/classes
# this is done to have matched version with sources and compiled resources

rm -rf src/main/resources/compare/
cp -r target/classes/eu/stamp_project/dspot/assertiongenerator/assertiongenerator_components/methodreconstructor_components/observer_components/testwithloggenerator_components/objectlogsyntaxbuilder_constructs src/main/resources/compare/
