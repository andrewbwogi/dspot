package eu.stamp_project.utils;

import org.junit.Test;
import spoon.pattern.Match;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;

import java.util.List;
import java.util.Map;

public class PatternTest {

    @Test
    public void patternTest(){
        CustomPattern p = new CustomPattern();
        // p.patternTest();
        p.traverse();

        int i = 1;

    }
}
