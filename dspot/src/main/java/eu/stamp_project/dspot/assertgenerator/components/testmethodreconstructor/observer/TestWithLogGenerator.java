package eu.stamp_project.dspot.assertgenerator.components.testmethodreconstructor.observer;

import eu.stamp_project.compare.MethodsHandler;
import eu.stamp_project.dspot.assertgenerator.components.testmethodreconstructor.observer.components.LogBuilder;
import eu.stamp_project.dspot.assertgenerator.utils.AssertionGeneratorUtils;
import eu.stamp_project.utils.CloneHelper;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;
import java.util.List;

public class TestWithLogGenerator {

    /**
     *
     * @param test
     * @param filter
     * @param ctVariableReads
     * @return
     */
    public static CtMethod<?> createTestWithLog(CtMethod test, final String filter,
                                                List<CtLocalVariable<?>> ctVariableReads) {
        CtMethod clone = CloneHelper.cloneTestMethodNoAmp(test);
        clone.setSimpleName(test.getSimpleName() + "_withlog");
        final List<CtStatement> allStatement = clone.getElements(new TypeFilter<>(CtStatement.class));
        allStatement.stream()
                .filter(statement ->
                        (TestWithLogGenerator.isStmtToLog(filter, statement) ||
                                ctVariableReads.contains(statement)) &&
                                isNotFromPreviousAmplification(allStatement, statement, test)
                ).forEach(statement ->
                LogBuilder.addLogStmt(statement,
                        test.getSimpleName() + "__" + indexOfByRef(allStatement, statement))
        );
        return clone;
    }

    private static boolean isStmtToLog(String filter, CtStatement statement) {
        if (!(statement.getParent() instanceof CtBlock)) {
            return false;
        }

        // contract: for now, we do not log values inside loop
        if (statement.getParent(CtLoop.class) != null) {
            return false;
        }
        if (statement instanceof CtInvocation) {
            CtInvocation invocation = (CtInvocation) statement;
            return (invocation.getMetadata(AssertionGeneratorUtils.METADATA_WAS_IN_ASSERTION) != null &&
                    (Boolean) invocation.getMetadata(AssertionGeneratorUtils.METADATA_WAS_IN_ASSERTION)) ||
                    (isCorrectReturn(invocation) && !isGetter(invocation));
        }
        if (statement instanceof CtLocalVariable ||
                statement instanceof CtAssignment ||
                statement instanceof CtVariableWrite) {
            if (statement instanceof CtNamedElement) {
                if (((CtNamedElement) statement).getSimpleName()
                        .startsWith("__DSPOT_")) {
                    return false;
                }
            }
            final CtTypeReference type = ((CtTypedElement) statement).getType();
            if (type.getQualifiedName().startsWith(filter)) {
                return true;
            } else {
                try {
                    return type.getTypeDeclaration().getQualifiedName()
                            .equals("java.lang.String");
                } catch (SpoonClassNotFoundException e) {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    private static boolean isGetter(CtInvocation invocation) {
        return invocation.getArguments().isEmpty() &&
                MethodsHandler.isASupportedMethodName(invocation.getExecutable().getSimpleName());
    }

    private static boolean isCorrectReturn(CtInvocation<?> invocation) {
        return invocation.getType() != null &&
                !(AssertionGeneratorUtils.isVoidReturn(invocation)) &&
                !(invocation.getType() instanceof CtWildcardReference) &&
                invocation.getType().getTypeDeclaration() != null &&
                !("java.lang.Class".equals(invocation.getType().getTypeDeclaration().getQualifiedName()));
    }

    // This method aims at infer from the name of the local variables if it came from a previous amplification.
    // see the use case available here: https://github.com/STAMP-project/dspot/issues/825
    private static boolean isNotFromPreviousAmplification(final List<CtStatement> allStatement,
                                                          CtStatement statement,
                                                          CtMethod<?> test) {
        final String id = test.getSimpleName() + "__" + TestWithLogGenerator.indexOfByRef(allStatement, statement);
        return test.getElements(new TypeFilter<CtLocalVariable>(CtLocalVariable.class) {
            @Override
            public boolean matches(CtLocalVariable element) {
                return element.getSimpleName().endsWith(id);
            }
        }).isEmpty();
    }

    private static int indexOfByRef(List<CtStatement> statements, CtStatement statement) {
        for (int i = 0; i < statements.size(); i++) {
            if (statements.get(i) == statement) {
                return i;
            }
        }
        return -1;
    }
}
