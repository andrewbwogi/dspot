package eu.stamp_project.dspot.assertgenerator.components.utils;

import eu.stamp_project.compare.MethodsHandler;
import eu.stamp_project.compare.ObjectLog;
import eu.stamp_project.utils.CloneHelper;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtNamedElement;
import spoon.reflect.declaration.CtTypedElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.reference.CtWildcardReference;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.SpoonClassNotFoundException;

import java.util.List;
import java.util.function.Predicate;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 3/3/17
 */
public class Utils {

    static boolean isCorrectReturn(CtInvocation<?> invocation) {
        return invocation.getType() != null &&
                !(isVoidReturn(invocation)) &&
                !(invocation.getType() instanceof CtWildcardReference) &&
                invocation.getType().getTypeDeclaration() != null &&
                !("java.lang.Class".equals(invocation.getType().getTypeDeclaration().getQualifiedName()));
    }

    public static boolean isVoidReturn(CtInvocation invocation) {
        return (invocation.getType().equals(invocation.getFactory().Type().voidType()) ||
                invocation.getType().equals(invocation.getFactory().Type().voidPrimitiveType()));
    }

    public static CtMethod<?> createTestWithLog(CtMethod test, final String filter,
                                         List<CtLocalVariable<?>> ctVariableReads) {
        CtMethod clone = CloneHelper.cloneTestMethodNoAmp(test);
        clone.setSimpleName(test.getSimpleName() + "_withlog");
        final List<CtStatement> allStatement = clone.getElements(new TypeFilter<>(CtStatement.class));
        allStatement.stream()
                .filter(statement ->
                        (isStmtToLog(filter, statement) ||
                                ctVariableReads.contains(statement)) &&
                                isNotFromPreviousAmplification(allStatement, statement, test)
                ).forEach(statement ->
                        addLogStmt(statement,
                                test.getSimpleName() + "__" + indexOfByRef(allStatement, statement))
                );
        return clone;
    }

    // This method aims at infer from the name of the local variables if it came from a previous amplification.
    // see the use case available here: https://github.com/STAMP-project/dspot/issues/825
    private static boolean isNotFromPreviousAmplification(final List<CtStatement> allStatement,
                                                          CtStatement statement,
                                                          CtMethod<?> test) {
        final String id = test.getSimpleName() + "__" + indexOfByRef(allStatement, statement);
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

    private static boolean isGetter(CtInvocation invocation) {
        return invocation.getArguments().isEmpty() &&
                MethodsHandler.isASupportedMethodName(invocation.getExecutable().getSimpleName());
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
            return (invocation.getMetadata(METADATA_WAS_IN_ASSERTION) != null &&
                    (Boolean) invocation.getMetadata(METADATA_WAS_IN_ASSERTION)) ||
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

    private static int getSize(CtBlock<?> block) {
        return block.getStatements().size() +
                block.getStatements().stream()
                        .filter(statement -> statement instanceof CtBlock)
                        .mapToInt(childBlock -> Utils.getSize((CtBlock<?>) childBlock))
                        .sum();
    }

    // This method will add a log statement at the given statement AND at the end of the test.
    @SuppressWarnings("unchecked")
    private static void addLogStmt(CtStatement stmt, String id) {
        if (stmt instanceof CtLocalVariable && ((CtLocalVariable) stmt).getDefaultExpression() == null) {
            return;
        }

        final Factory factory = stmt.getFactory();

        final CtTypeAccess<ObjectLog> typeAccess = factory.createTypeAccess(
                factory.Type().createReference(ObjectLog.class)
        );

        final CtExecutableReference objectLogExecRef = factory.createExecutableReference()
                .setStatic(true)
                .setDeclaringType(factory.Type().createReference(ObjectLog.class))
                .setSimpleName("log");
        objectLogExecRef.setType(factory.Type().voidPrimitiveType());

        final CtInvocation invocationToObjectLog = factory.createInvocation(typeAccess, objectLogExecRef);

        CtStatement insertAfter;
        if (stmt instanceof CtVariableWrite) {//TODO
            CtVariableWrite varWrite = (CtVariableWrite) stmt;
            insertAfter = stmt;
        } else if (stmt instanceof CtLocalVariable) {
            CtLocalVariable localVar = (CtLocalVariable) stmt;
            final CtVariableAccess variableRead = factory.createVariableRead(localVar.getReference(), false);// TODO checks static
            invocationToObjectLog.addArgument(variableRead);
            invocationToObjectLog.addArgument(factory.createLiteral(localVar.getSimpleName()));
            insertAfter = stmt;
        } else if (stmt instanceof CtAssignment) {
            CtAssignment localVar = (CtAssignment) stmt;
            invocationToObjectLog.addArgument(localVar.getAssigned());
            invocationToObjectLog.addArgument(factory.createLiteral(localVar.getAssigned().toString()));
            insertAfter = stmt;
        } else if (stmt instanceof CtInvocation) {
            // in case of a new Something() or a method call,
            // we put the new Something() in a local variable
            // then we replace it by the an access to this local variable
            // and we add a log statement on it
            CtInvocation invocation = (CtInvocation) stmt;
            if (isVoidReturn(invocation)) {
                invocationToObjectLog.addArgument(invocation.getTarget());
                invocationToObjectLog.addArgument(factory.createLiteral(
                        invocation.getTarget().toString().replace("\"", "\\\""))
                );
                insertAfter = invocation;
            } else {
                final CtLocalVariable localVariable = factory.createLocalVariable(
                        getCorrectTypeOfInvocation(invocation),
                        "o_" + id,
                        invocation.clone()
                );
                try {
                    stmt.replace(localVariable);
                } catch (ClassCastException e) {
                    throw new RuntimeException(e);
                }
                invocationToObjectLog.addArgument(factory.createVariableRead(localVariable.getReference(), false));
                invocationToObjectLog.addArgument(factory.createLiteral("o_" + id));
                insertAfter = localVariable;
            }
        } else if (stmt instanceof CtConstructorCall) {
            final CtConstructorCall constructorCall = (CtConstructorCall<?>) stmt;
            final CtLocalVariable<?> localVariable = factory.createLocalVariable(
                    constructorCall.getType(),
                    "o_" + id,
                    constructorCall.clone()
            );
            try {
                getTopStatement(stmt).insertBefore(localVariable);
            } catch (IndexOutOfBoundsException e) {
                throw new RuntimeException(e);
            }

            try {
                stmt.replace(factory.createVariableRead(localVariable.getReference(), false));
            } catch (ClassCastException e) {
                throw new RuntimeException(e);
            }
            invocationToObjectLog.addArgument(factory.createVariableRead(localVariable.getReference(), false));
            invocationToObjectLog.addArgument(factory.createLiteral("o_" + id));
            insertAfter = localVariable;
        } else {
            throw new RuntimeException("Could not find the proper type to add log statement" + stmt.toString());
        }

        // clone the statement invocation for add it to the end of the tests
        CtInvocation invocationToObjectLogAtTheEnd = invocationToObjectLog.clone();
        invocationToObjectLogAtTheEnd.addArgument(factory.createLiteral(id + "___" + "end"));
        invocationToObjectLog.addArgument(factory.createLiteral(id));

        //TODO checks this if this condition is ok.
        if (getSize(stmt.getParent(CtMethod.class).getBody()) + 1 < 65535) {
            insertAfter.insertAfter(invocationToObjectLog);
        }

        // if between the two log statements there is only log statement, we do not add the log end statement
        if (shouldAddLogEndStatement.test(invocationToObjectLog) &&
                getSize(stmt.getParent(CtMethod.class).getBody()) + 1 < 65535) {
            stmt.getParent(CtBlock.class).insertEnd(invocationToObjectLogAtTheEnd);
        }
    }

    public static CtTypeReference getCorrectTypeOfInvocation(CtInvocation<?> invocation) {
        final CtTypeReference type = invocation.getType().clone();
        type.getActualTypeArguments().removeIf(CtTypeReference::isGenerics);
        return type;
    }

    private static final Predicate<CtStatement> shouldAddLogEndStatement = statement -> {
        final List<CtStatement> statements = statement.getParent(CtBlock.class).getStatements();
        for (int i = statements.indexOf(statement) + 1; i < statements.size(); i++) {
            if (!(statements.get(i) instanceof CtInvocation) ||
                    !((CtInvocation) statements.get(i)).getTarget().equals(statement.getFactory().createTypeAccess(
                            statement.getFactory().Type().createReference(ObjectLog.class)))) {
                return true;
            }
        }
        return false;
    };

    public final static String METADATA_WAS_IN_ASSERTION = "Was-Asserted";

    public final static String METADATA_ASSERT_AMPLIFICATION = "A-Amplification";

    public static CtStatement getTopStatement(CtElement start) {
        CtElement topStatement = start;
        while (!(topStatement.getParent() instanceof CtStatementList)) {
            topStatement = topStatement.getParent();
        }
        return (CtStatement) topStatement;
    }
}
