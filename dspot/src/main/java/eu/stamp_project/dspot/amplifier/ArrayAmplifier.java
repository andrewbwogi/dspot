package eu.stamp_project.dspot.amplifier;

import eu.stamp_project.test_framework.TestFramework;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class ArrayAmplifier extends AbstractLiteralAmplifier<CtNewArrayImpl>  {

    protected final TypeFilter<CtExpression<CtNewArrayImpl>> ARRAY_LITERAL_TYPE_FILTER = new TypeFilter<CtExpression<CtNewArrayImpl>>(CtExpression.class) {
        @Override
        public boolean matches(CtExpression<CtNewArrayImpl> candidate) {
            System.out.println("================ in matches");

            // keep only literals
            if (! (candidate instanceof CtLiteral || candidate instanceof CtNewArrayImpl)) {
                return false;
            }
            if(candidate instanceof CtLiteral) {
                CtLiteral literal = (CtLiteral) candidate;
                //CtNewArrayImpl<T> literal = (CtNewArrayImpl<T>) candidate;
                try {

                    // don't keep candidates inside assertions and annotations
                    Class<?> clazzOfLiteral = null;
                    if ((literal.getParent() instanceof CtInvocation &&
                            TestFramework.get().isAssert((CtInvocation) literal.getParent()))
                            || isConcatenationOfLiteralInAssertion(literal)
                            || literal.getParent(CtAnnotation.class) != null) {
                        return false;
                    } else if (literal.getValue() == null) {

                        System.out.println("=========================== literal is null");
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
        return Collections.singleton(original);
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
