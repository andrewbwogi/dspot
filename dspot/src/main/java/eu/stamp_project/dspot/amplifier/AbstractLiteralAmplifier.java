package eu.stamp_project.dspot.amplifier;

import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.utils.AmplificationHelper;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.List;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 18/09/17
 */
public abstract class AbstractLiteralAmplifier<T> extends AbstractAmplifier<CtExpression<T>> {

    protected CtType<?> testClassToBeAmplified;

    protected final TypeFilter<CtExpression<T>> LITERAL_TYPE_FILTER = new TypeFilter<CtExpression<T>>(CtExpression.class) {
        @Override
        public boolean matches(CtExpression<T> candidate) {
            System.out.println("================ in matches");

            // keep only literals
            if (! (candidate instanceof CtLiteral)) {
                return false;
            }
            //if()
            CtLiteral<T> literal = (CtLiteral<T>) candidate;
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
                        clazzOfLiteral = parent.getExecutable()
                                .getDeclaration()
                                .getParameters()
                                .get(parent.getArguments().indexOf(literal))
                                .getType()
                                .getActualClass();

                        // getting the class of the assignee
                    } else if (literal.getParent() instanceof CtAssignment) {
                        clazzOfLiteral = ((CtAssignment) literal.getParent())
                                .getAssigned()
                                .getType()
                                .getActualClass();

                        // getting the class of the local variable
                    } else if (literal.getParent() instanceof CtLocalVariable) {
                        clazzOfLiteral = ((CtLocalVariable) literal.getParent())
                                .getType()
                                .getActualClass();
                    }
                } else {
                    clazzOfLiteral = literal.getValue().getClass();
                }

                // keep candidates that have the class T (the type of the current literal amplifier)
                return getTargetedClass().isAssignableFrom(clazzOfLiteral);
            } catch (Exception e) {

                // todo: maybe need a warning ?
                return false;
            }
        }

        private boolean isConcatenationOfLiteralInAssertion(CtLiteral<T> literal) {
            CtElement currentElement = literal;
            while (currentElement.getParent() instanceof CtBinaryOperator) {
                currentElement = currentElement.getParent();
            }
            return currentElement.getParent() instanceof CtInvocation &&
                    TestFramework.get().isAssert((CtInvocation) literal.getParent());
        }
    };

    @Override
    protected List<CtExpression<T>> getOriginals(CtMethod<?> testMethod) {
        System.out.println("----------------- getOriginals: " + LITERAL_TYPE_FILTER.getClass().getName());
        System.out.println("----------------- getOriginals: " + LITERAL_TYPE_FILTER.getClass().getTypeName());
        System.out.println("----------------- getOriginals: " + LITERAL_TYPE_FILTER.getClass().getCanonicalName());


        return testMethod.getElements(LITERAL_TYPE_FILTER);
    }

    @Override
    public void reset(CtType testClass) {
        AmplificationHelper.reset();
        this.testClassToBeAmplified = testClass;
    }

    protected abstract Class<?> getTargetedClass();


}
