package eu.stamp_project.utils;
import spoon.Launcher;
import spoon.OutputType;
import spoon.pattern.Match;
import spoon.pattern.PatternBuilder;
import spoon.pattern.PatternBuilderHelper;
import spoon.reflect.CtModel;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.CodeFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.reference.CtArrayTypeReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtNewArrayImpl;
import spoon.support.reflect.reference.CtArrayTypeReferenceImpl;
import spoon.support.reflect.reference.CtTypeReferenceImpl;

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

    void traverse(){
        TypeFactory typeFactory = new TypeFactory();


        // from getSpoonModelOf(String pathToSources, String pathToDependencies) in DSportCompiler
        Launcher launcher = new Launcher();
        launcher.getEnvironment().setNoClasspath(true);
        launcher.addInputResource("/home/andrew/Skrivbord/stamp/issue-667/test-pattern/");
        launcher.buildModel();
        Factory factory = launcher.getFactory();
        List<CtType<?>> classes = factory.Type().getAll();

        List<CtElement> elements = classes.get(0).getMethodsByName("testmethod5").get(0).getElements(new TypeFilter<>(CtElement.class));

        for (CtElement v : elements) {
            System.out.println("--- new element --- : " + v.getClass().getSimpleName());
            System.out.println(v.toString());
        }

        System.out.println("************** after traverse *****************");
        CtComment com = factory.createComment();
        com.setContent("test");
        System.out.println(com);
        final CtCodeSnippetStatement statementInConstructor = factory.createCodeSnippetStatement("this.dates = dates");
        System.out.println(statementInConstructor);





        //CtNewArray<CtArrayTypeReferenceImpl<CtTypeReferenceImpl<CtLiteralImpl<Integer>>>> ar2 = factory.createNewArray();


        //CtNewArray<CtTypeReference<Boolean>> ar = factory.createNewArray();

        //ar.addDimensionExpression(lit);
        //ar.addElement(lit);




        CtNewArray<CtArrayTypeReference<CtTypeReference<CtLiteral<Integer>>>> ar = factory.createNewArray();

        CodeFactory codeFactory = new CodeFactory(factory);





        CtTypeReference<Integer> ty = typeFactory.integerPrimitiveType();
        CtTypeReference<Integer> ty2 = typeFactory.createArrayReference("test");
        CtArrayTypeReference<CtTypeReference<CtLiteral<Integer>>> atr = factory.createArrayTypeReference();
        CtLiteralImpl lit = new CtLiteralImpl<Integer>();
        lit.setValue(1);
        // CtNewArray<CtArrayTypeReference<CtTypeReference<CtLiteral<Integer>>>> ar3 = factory.createNewArray();
        CtNewArray<Integer> ar3 = factory.createNewArray();
        //CtNewArray<CtTypeReference<CtLiteral<Integer>>> ar3 = factory.createNewArray();

        // must we setParent?
        ar3.setType(ty2);
        ar3.addDimensionExpression(lit);
        ar3.addElement(lit.clone());

        System.out.println("ar3: " + ar3);

        // lets wait with new, lets just create an array expression, and look inside its contents



        System.out.println(lit);
        System.out.println("simple name: " + ar.getClass().getSimpleName());
        new CtLiteralImpl<Integer>();

        for (CtElement v : ar3.getElements()) {
            System.out.println("--- new element --- : " + v.getClass().getSimpleName());
            System.out.println(v.toString());
        }



        // beginning from zero

        // CtNewArrayImpl: new int[]{ 1 }
        CtNewArray<CtArrayTypeReference<CtTypeReference<Integer>>> newArray = new CtNewArrayImpl<>();

        // CtArrayTypeReferenceImpl: int[]
        CtArrayTypeReference<CtTypeReference<Integer>> arrayTypeReference = new CtArrayTypeReferenceImpl<>();

        // CtTypeReferenceImpl: int
        CtTypeReference<Integer> intTypeReference1 = typeFactory.integerPrimitiveType();

        // CtLiteralImpl: 1
        CtLiteralImpl literal = new CtLiteralImpl<Integer>();
        literal.setValue(1);

        // CtTypeReferenceImpl: int
        CtTypeReference<Integer> intTypeReference2 = typeFactory.integerPrimitiveType();
/*
        arrayTypeReference.setComponentType(intTypeReference1);
        newArray.setType(arrayTypeReference);
        literal.setType(intTypeReference2);
        newArray.setType(arrayTypeReference);
        newArray.addDimensionExpression(intTypeReference1);
        newArray.addElement(literal);
*/
    }
}
