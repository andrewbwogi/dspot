package eu.stamp_project.utils;
import spoon.Launcher;
import spoon.OutputType;
import spoon.pattern.Match;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;



public class CustomPattern {

    void patternTest(){

        // from getSpoonModelOf(String pathToSources, String pathToDependencies) in DSportCompiler
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        // launcher.getEnvironment().setOutputType(OutputType.CLASSES);
        launcher.addInputResource("/home/andrew/Skrivbord/stamp/issue-535/pattern-test/");
        launcher.buildModel();

        // CtModel model = launcher.getModel(); // from spoon docs

        Factory factory = launcher.getFactory();
        List<CtType<?>> classes = factory.Type().getAll();
        System.out.println("class names: " + classes.get(0).getSimpleName());
        System.out.println(classes.get(1).getSimpleName());

        //build a Spoon pattern, from spoon docs
        spoon.pattern.Pattern pattern = PatternBuilder.create(
                new PatternBuilderHelper(classes.get(0)).setBodyOfMethod("testmethod").getPatternElements())
                .configurePatternParameters()
                .build();

        // change name of method of pattern
        classes.get(0).getMethodsByName("testmethod").get(0).setSimpleName("new-name");

        // try to change names of local variables. see if change is reflected everywhere
        List<CtLocalVariable> l = classes.get(0).getMethodsByName("new-name").get(0).getElements(new TypeFilter<>(CtLocalVariable.class));
        System.out.println("--- local variable elements ---");
        l.get(0).setSimpleName("g");
        for(CtLocalVariable v : l)
        {
            System.out.println("simple name: " + v.getSimpleName());
            System.out.println(v.toString());
        }
        System.out.println("--- end local variable elements ---");

        // explore all elements in the class
        List<CtElement> l2 = classes.get(0).getMethodsByName("new-name").get(0).getElements(new TypeFilter<>(CtElement.class));
        System.out.println("*** all elements ***");
        rename(l2);
        System.out.println("*** end all elements ***");


        // new TypeFilter<CtLocalVariable<?>>(CtLocalVariable.class)
        // new TypeFilter<>(CtStatement.class)

        //this pattern works but method names must be exact. change name on all mthods
        spoon.pattern.Pattern pattern2 = PatternBuilder.create(classes.get(0).getMethodsByName("new-name").get(0)).build();

        //change name on all methods to match
        for(CtMethod m : classes.get(1).getMethods()){
            m.setSimpleName("new-name");
        }

        //search for all occurences of the method in the root package
        List<Match> matches = pattern2.getMatches(classes.get(1));

        for(Match m : matches){
            System.out.println("--- new match ---");
            System.out.println(m);
            for(CtElement e : m.getMatchingElements()){
                System.out.println("--- new element ---");
                System.out.println(e.toString());
            }
        }

    }

    void rename(List<CtElement> l){
        boolean first = true;
        if(!l.isEmpty()) {
            System.out.println("--- new element ---");
            for (CtElement v : l) {
                if(!first) {
                    System.out.println("--- in list ---");
                    System.out.println(v.toString());
                    List<CtElement> n = v.getElements(new TypeFilter<>(CtElement.class));
                    rename(n);
                }
                first = false;
            }
        }
    }
}
