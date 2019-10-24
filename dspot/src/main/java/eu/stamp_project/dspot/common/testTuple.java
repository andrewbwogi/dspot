package eu.stamp_project.dspot.common;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;

import java.util.List;

public class testTuple {
    public CtType<?> testClassToBeAmplified;
    public List<CtMethod<?>> testMethodsToBeAmplified;

    public testTuple(CtType<?> testClassToBeAmplified, List<CtMethod<?>> testMethodsToBeAmplified){
        this.testClassToBeAmplified = testClassToBeAmplified;
        this.testMethodsToBeAmplified = testMethodsToBeAmplified;
    }
}
