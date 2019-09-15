package eu.stamp_project.dspot.amplifier;

import com.esotericsoftware.kryo.Kryo;
import eu.stamp_project.test_framework.TestFramework;
import org.apache.commons.lang3.SerializationUtils;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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
        System.out.println("********************* Array amplifier !!!!");

        System.out.println("original: " + original);
        CtNewArrayImpl castedOriginal = (CtNewArrayImpl) original;
        Set<CtExpression<CtNewArrayImpl>> values = new HashSet<>();
        List<CtExpression> list = ((CtNewArrayImpl) original).getElements();
        if(list.isEmpty())
        {

        }
        else {

            // create array expressions that are modifications of the original array expression
            CtNewArray cloneAdd = SerializationUtils.clone(castedOriginal);
            CtNewArray cloneSub = SerializationUtils.clone(castedOriginal);
            CtNewArray cloneEmpty = SerializationUtils.clone(castedOriginal);
            List<CtExpression> elements = cloneSub.getElements();
            CtExpression newElement = SerializationUtils.clone(elements.get(0));
            cloneSub.removeElement(elements.get(0));
            cloneAdd.addElement(newElement);
            cloneEmpty.setElements(Collections.EMPTY_LIST);
            System.out.println("add: " + cloneAdd);
            System.out.println("sub: "+cloneSub);
            System.out.println("empty: "+cloneEmpty);
            values.add(cloneAdd);
            values.add(cloneSub);
            values.add(cloneEmpty);
        }
        return values;
    }

    @Override
    protected String getSuffix() {
        return "litArray";
    }

    @Override
    protected Class<?> getTargetedClass() {
        return Array.class;
    }

    @Override
    protected List<CtExpression<CtNewArrayImpl>> getOriginals(CtMethod<?> testMethod) {
        System.out.println("----------------- getOriginals Array: ");

        return testMethod.getElements(ARRAY_LITERAL_TYPE_FILTER);
    }

}
