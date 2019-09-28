package eu.stamp_project.dspot.amplifier;

import com.esotericsoftware.kryo.Kryo;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.utils.program.InputConfiguration;
import org.apache.commons.lang3.SerializationUtils;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.CoreFactory;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtCodeSnippetExpressionImpl;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by Andrew Bwogi
 * abwogi@kth.se
 * on 12/09/19
 */
public class ArrayAmplifier extends AbstractLiteralAmplifier<CtNewArrayImpl>  {

    protected final TypeFilter<CtExpression<CtNewArrayImpl>> ARRAY_LITERAL_TYPE_FILTER = new TypeFilter<CtExpression<CtNewArrayImpl>>(CtExpression.class) {
        @Override
        public boolean matches(CtExpression<CtNewArrayImpl> candidate) {

            // keep only literals
            if (! (candidate instanceof CtLiteral || candidate instanceof CtNewArrayImpl)) {
                return false;
            }

            // don't keep elements of arrays
            if(candidate.getParent() instanceof CtNewArrayImpl){
                return false;
            }
            if(candidate instanceof CtLiteral) {
                CtLiteral literal = (CtLiteral) candidate;
                try {

                    // don't keep candidates inside assertions and annotations
                    if ((literal.getParent() instanceof CtInvocation &&
                            TestFramework.get().isAssert((CtInvocation) literal.getParent()))
                            || isConcatenationOfLiteralInAssertion(literal)
                            || literal.getParent(CtAnnotation.class) != null) {
                        return false;
                    } else if (literal.getValue() == null) {

                        // getting the class of the expected parameter
                        if (literal.getParent() instanceof CtInvocation<?>) {
                            final CtInvocation<?> parent = (CtInvocation<?>) literal.getParent();
                            return parent.getExecutable()
                                    .getDeclaration()
                                    .getParameters()
                                    .get(parent.getArguments().indexOf(literal))
                                    .getType()
                                    .getActualClass().isArray();

                            // getting the class of the assignee
                        } else if (literal.getParent() instanceof CtAssignment) {
                            return ((CtAssignment) literal.getParent())
                                    .getAssigned()
                                    .getType()
                                    .getActualClass().isArray();

                            // getting the class of the local variable
                        } else if (literal.getParent() instanceof CtLocalVariable) {
                            return ((CtLocalVariable) literal.getParent())
                                    .getType()
                                    .getActualClass().isArray();
                        }
                    }
                } catch (Exception e) {

                    // todo: maybe need a warning ?
                    return false;
                }
                return literal.getValue().getClass().isArray();
            }
            else {
                return true;
            }
        }

        private boolean isConcatenationOfLiteralInAssertion(CtLiteral literal) {
            CtElement currentElement = literal;
            while (currentElement.getParent() instanceof CtBinaryOperator) {
                currentElement = currentElement.getParent();
            }
            return currentElement.getParent() instanceof CtInvocation &&
                    TestFramework.get().isAssert((CtInvocation) literal.getParent());
        }
    };

    @Override
    protected Set<CtExpression<CtNewArrayImpl>> amplify(CtExpression<CtNewArrayImpl> original, CtMethod<?> testMethod) {
        //Factory factory = testMethod.getFactory();
        final Factory factory = InputConfiguration.get().getFactory();

        //Factory factory = new CoreFactory();
        System.out.println("********************* Array amplifier !!!!");

        System.out.println("original: " + original);
        Set<CtExpression<CtNewArrayImpl>> values = new HashSet<>();



        if(original instanceof CtLiteral && ((CtLiteral)original).getValue() == null) {
            String type = constructArraysForNull((CtLiteral)original);
            String additionalElement = constructAdditionalElement(getSimpleType(type));
            String array = constructEmptyArray(type,additionalElement,false);
            CtExpression compiled = factory.createCodeSnippetExpression(array).compile();

            // array with one element
            values.add(compiled);
            array = constructEmptyArray(type,"",true);
            compiled = factory.createCodeSnippetExpression(array).compile();

            // empty array
            values.add(compiled);
            return values;



/*
            //System.out.println("in null");
            String type = constructArraysForNull((CtLiteral)original);

            // array with one element
            //System.out.println("type: " + type);
            String additionalElement = constructAdditionalElement(type);
            //System.out.println("additional: " + additionalElement);
            String array = constructEmptyArray(type,additionalElement,false);
            //System.out.println("array: "+array);
            values.add(factory.createCodeSnippetExpression(array));


            // empty array
            array = constructEmptyArray(type,"",true);
            /*System.out.println("array: " + array);
            CtCodeSnippetExpression e = new CtCodeSnippetExpressionImpl();
            e.setValue("new int[][]{}");*/
          /*  CtExpression compiled = factory.createCodeSnippetExpression(array).compile();
//            boolean added = values.add(factory.createCodeSnippetExpression(array));
            values.add(compiled);

            //boolean added = values.add(e);
            //System.out.println(values.size());
            //System.out.println(added);
            /*System.out.println("next");
            CtExpression e = factory.createCodeSnippetExpression(array);
            CtExpression n = SerializationUtils.clone(e);
            System.out.println(n);
            added = values.add(new CtNewArrayImpl<>());
            System.out.println(values.size());
            System.out.println(added);*/
            /*ArrayList<Integer> ai = new ArrayList<>();
            Integer test = new Integer(1);
            Integer test2 = test;
            boolean ad = ai.add(test);
            System.out.println("ad: " + ad);
            ad = ai.add(test);
            System.out.println("ad: " + ad);
            System.out.println(ai.size());*/
            //return values;
        }

        CtNewArrayImpl castedOriginal = (CtNewArrayImpl) original;
        List<CtExpression> list = castedOriginal.getElements();
        if(list.isEmpty())
        {
            System.out.println("--listempty");
            System.out.println("simple name: " + original.getType().getSimpleName());

            String additionalElement = constructAdditionalElement(getSimpleType(original.getType().getSimpleName()));
            System.out.println("additional element: " + additionalElement);
            String array = constructEmptyArray(original.getType().toString(),additionalElement,false);
            System.out.println("array: " + array);

            CtExpression compiled = factory.createCodeSnippetExpression(array).compile();
            System.out.println("compiled: " + compiled);

            values.add(compiled);
        }
        else {

            // create array expressions that are modifications of the original array expression
            CtNewArray cloneAdd = SerializationUtils.clone(castedOriginal);
            CtNewArray cloneSub = SerializationUtils.clone(castedOriginal);
            List<CtExpression> elements = cloneSub.getElements();
            CtExpression newElement = SerializationUtils.clone(elements.get(0));
            cloneSub.removeElement(elements.get(0));
            cloneAdd.addElement(newElement);
            values.add(cloneAdd);
            values.add(cloneSub);
            if(list.size()>1){
                CtNewArray cloneEmpty = SerializationUtils.clone(castedOriginal);
                cloneEmpty.setElements(Collections.EMPTY_LIST);
                values.add(cloneEmpty);
            }
        }
        return values;
    }

    private String getSimpleType(String simpleName){
        int index = simpleName.indexOf("[");
        simpleName = simpleName.substring(0,index);
        System.out.println("newsimple: " + simpleName);
        return simpleName;
    }
    // todo refactor so matches does not duplicate the code below
    private String constructArraysForNull(CtLiteral original) {
        String name = "";
        // getting the class of the expected parameter
        if (original.getParent() instanceof CtInvocation<?>) {
            final CtInvocation<?> parent = (CtInvocation<?>) original.getParent();
            name =  parent.getExecutable()
                    .getDeclaration()
                    .getParameters()
                    .get(parent.getArguments().indexOf(original))
                    .getType()
                    .getActualClass().getTypeName();

            // getting the class of the assignee
        } else if (original.getParent() instanceof CtAssignment) {
            name = ((CtAssignment) original.getParent())
                    .getAssigned()
                    .getType()
                    .getActualClass().getTypeName();

            // getting the class of the local variable
        } else if (original.getParent() instanceof CtLocalVariable) {
            name = ((CtLocalVariable) original.getParent())
                    .getType()
                    .getActualClass().getTypeName();
        }

        System.out.println("--name: " + name);
        return name;

    }

    private String constructAdditionalElement(String type) {
        type = type.toLowerCase();
        if(type.equals("int") || type.equals("integer") || type.equals("short") || type.equals("byte")){
            return "1";
        }
        else if(type.equals("long")){
            return "1L";
        }
        else if(type.equals("float")){
            return "1.1F";
        }
        else if(type.equals("double")){
            return "1.1";
        }
        else if(type.equals("boolean")){
            return "true";
        }
        else if(type.equals("char") || type.equals("character")){
            return "'a'";
        }
        else if(type.equals("string")){
            return "\"a\"";
        }
        else {
            return "null";
        }
    }

    private String constructEmptyArray(String type, String additionalElement,boolean isEmpty) {
        long dimensions;
        if(isEmpty){
            dimensions = 1;
        }
        else {
            dimensions = type.chars().filter(num -> num == '[').count();
        }
        System.out.println("dimensions: " + dimensions);
        StringBuilder sb = new StringBuilder();
        sb.append("new " + type);
        for(int i = 0;i<dimensions;i++){
            sb.append("{");
        }

        // add element
        sb.append(additionalElement);
        for(int i = 0;i<dimensions;i++){
            sb.append("}");
        }
        return sb.toString();
    }


    @Override
    protected String getSuffix() {
        return "litArray";
    }

    @Override
    protected Class<?> getTargetedClass() {
        return Array.class;
    } // not used

    @Override
    protected List<CtExpression<CtNewArrayImpl>> getOriginals(CtMethod<?> testMethod) {
        System.out.println("----------------- getOriginals Array: ");

        System.out.println(testMethod);
        return testMethod.getElements(ARRAY_LITERAL_TYPE_FILTER);
    }

}



/*
    private CtExpression<CtNewArrayImpl> constructArray2(Set<CtExpression<CtNewArrayImpl>> values, CtExpression<CtNewArrayImpl> original, Factory factory, boolean isNull) {
        String type;
        if(isNull){
            type = constructArraysForNull();
        }
        else{
            type = original.getType().toString();
        }
        long dimensions = type.chars().filter(num -> num == '[').count();
        System.out.println("dimensions: " + dimensions);
        StringBuilder sb = new StringBuilder();
        sb.append("new " + type);
        for(int i = 0;i<dimensions;i++){
            sb.append("{");
        }

        // add correct element
        type = type.toLowerCase().substring(0,3);
        System.out.println(type);
        if(type.equals("int")){
            sb.append("1");
        }
        else if(type.equals("sho")){
            sb.append("1");
        }
        else if(type.equals("lon")){
            sb.append("1L");
        }
        else if(type.equals("flo")){
            sb.append("1.1F");
        }
        else if(type.equals("dou")){
            sb.append("1.1");
        }
        else if(type.equals("byt")){
            sb.append("1");
        }
        else if(type.equals("boo")){
            sb.append("true");
        }
        else if(type.equals("cha")){
            sb.append("'a'");
        }
        else if(type.equals("Str")){
            sb.append("\"a\"");
        }
        else {
            sb.append("null");
        }


        for(int i = 0;i<dimensions;i++){
            sb.append("}");
        }
        values.add(factory.createCodeSnippetExpression(sb.toString()));
        if(isNull){

        }
        return factory.createCodeSnippetExpression(sb.toString());
    }
*/

/*
    private CtExpression<CtNewArrayImpl> constructArray(CtExpression<CtNewArrayImpl> original, Factory factory) {
        String expression = original.toString();
        long dimensions = expression.chars().filter(num -> num == '[').count();
        System.out.println("dimensions: " + dimensions);
        StringBuilder sb = new StringBuilder();
        sb.append("new " + original.getType());
        for(int i = 0;i<dimensions;i++){
            sb.append("{");
        }

        // add correct element
        String type = original.getType().toString().toLowerCase().substring(0,3);
        System.out.println(type);
        if(type.equals("int")){
            sb.append("1");
        }
        else if(type.equals("sho")){
            sb.append("1");
        }
        else if(type.equals("lon")){
            sb.append("1L");
        }
        else if(type.equals("flo")){
            sb.append("1.1F");
        }
        else if(type.equals("dou")){
            sb.append("1.1");
        }
        else if(type.equals("byt")){
            sb.append("1");
        }
        else if(type.equals("boo")){
            sb.append("true");
        }
        else if(type.equals("cha")){
            sb.append("'a'");
        }
        else if(type.equals("Str")){
            sb.append("\"a\"");
        }
        else {
            sb.append("null");
        }
        for(int i = 0;i<dimensions;i++){
            sb.append("}");
        }
        return factory.createCodeSnippetExpression(sb.toString());
    }*/
