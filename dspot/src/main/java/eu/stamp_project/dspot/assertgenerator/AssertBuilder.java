package eu.stamp_project.dspot.assertgenerator;

import eu.stamp_project.test_framework.assertions.AssertEnum;
import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.utils.TypeUtils;
import eu.stamp_project.utils.program.InputConfiguration;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.*;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtExecutableReference;
import spoon.reflect.reference.CtFieldReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 3/17/17
 */
public class AssertBuilder {

    public static final int MAX_NUMBER_OF_CHECKED_ELEMENT_IN_LIST = 5;

    private static final Predicate<Object> isFloating = value ->
            value instanceof Double || value.getClass() == double.class ||
                    value instanceof Float || value.getClass() == float.class;

    static List<CtStatement> buildAssert(CtMethod<?> testMethod,
                                         Set<String> notDeterministValues,
                                         Map<String, Object> observations,
                                         Double delta) {
        final Factory factory = InputConfiguration.get().getFactory();
        final Translator translator = new Translator(factory);
        final List<CtStatement> invocations = new ArrayList<>();
        for (String observationKey : observations.keySet()) {
            if (!notDeterministValues.contains(observationKey)) {
                Object value = observations.get(observationKey);
                final CtExpression variableRead = translator.translate(observationKey);
                if (value == null) {
                    final CtInvocation<?> assertNull = TestFramework.get()
                            .buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_NULL, Collections.singletonList(variableRead));
                    invocations.add(assertNull);
                    variableRead.setType(factory.Type().NULL_TYPE);
                } else {
                    /* Boolean */
                    if (value instanceof Boolean) {
                        invocations.add(
                                TestFramework.get()
                                        .buildInvocationToAssertion(testMethod,
                                                (Boolean) value ? AssertEnum.ASSERT_TRUE : AssertEnum.ASSERT_FALSE,
                                                Collections.singletonList(variableRead)
                                        )
                        );
                        /* Primitive collection */
                    } else if (TypeUtils.isPrimitiveCollection(value)) {
                        Collection valueCollection = (Collection) value;
                        if (valueCollection.isEmpty()) {
                            final CtInvocation<?> isEmpty = factory.createInvocation(variableRead,
                                    factory.Type().get(Collection.class).getMethodsByName("isEmpty").get(0).getReference()
                            );
                            invocations.add(
                                    TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_TRUE,
                                            Collections.singletonList(isEmpty)
                                    )
                            );
                        } else {
                            invocations.addAll(buildSnippetAssertCollection(factory, testMethod, observationKey, (Collection) value));
                        }
                    } else if (TypeUtils.isArray(value)) {
                        if(isPrimitiveArray(value)){
                            CtExpression expectedValue = factory.createCodeSnippetExpression(getNewArrayExpression(value));
                            List<CtExpression> list;
                            if(getArrayComponentType(value) == float.class){
                                list = Arrays.asList(expectedValue,variableRead,factory.createLiteral(0.1F));
                            }
                            else if(getArrayComponentType(value) == double.class){
                                list = Arrays.asList(expectedValue,variableRead,factory.createLiteral(0.1));
                            }
                            else {
                                list = Arrays.asList(expectedValue,variableRead);
                            }
                            invocations.add(TestFramework.get().buildInvocationToAssertion(testMethod,
                                    AssertEnum.ASSERT_ARRAY_EQUALS, list));
                        }
                    } else if (TypeUtils.isPrimitiveMap(value)) {//TODO
                        Map valueCollection = (Map) value;
                        if (valueCollection.isEmpty()) {
                            final CtInvocation<?> isEmpty = factory.createInvocation(variableRead,
                                    factory.Type().get(Map.class).getMethodsByName("isEmpty").get(0).getReference()
                            );
                            invocations.add(TestFramework.get().buildInvocationToAssertion(
                                    testMethod,
                                    AssertEnum.ASSERT_TRUE,
                                    Collections.singletonList(isEmpty)
                                    )
                            );
                        } else {
                            invocations.addAll(buildSnippetAssertMap(factory, testMethod, observationKey, (Map) value));
                        }
                    } else {
                        /* Other types */
                        addTypeCastIfNeeded(variableRead, value);
                        if (isFloating.test(value)) {
                            invocations.add(
                                    TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_EQUALS,
                                            Arrays.asList(
                                                    printPrimitiveString(factory, value),
                                                    variableRead,
                                                    factory.createLiteral(delta)
                                            )));
                        } else {
                            if (value instanceof String) {
                                if (AssertGeneratorHelper.canGenerateAnAssertionFor((String) value)) {
                                    invocations.add(TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_EQUALS,
                                            Arrays.asList(printPrimitiveString(factory, value),
                                                    variableRead)));
                                }
                            } else {
                                invocations.add(TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_EQUALS,
                                        Arrays.asList(printPrimitiveString(factory, value),
                                                variableRead)));
                            }
                        }
                    }
                    variableRead.setType(factory.Type().createReference(value.getClass()));
                }
            }
        }
        return invocations;
    }

    private static void addTypeCastIfNeeded(CtExpression<?> variableRead, Object value) {
        if (value instanceof Short) {
            variableRead.addTypeCast(variableRead.getFactory().Type().shortPrimitiveType());
        } else if (value instanceof Integer) {
            variableRead.addTypeCast(variableRead.getFactory().Type().integerPrimitiveType());
        } else if (value instanceof Long) {
            variableRead.addTypeCast(variableRead.getFactory().Type().longPrimitiveType());
        } else if (value instanceof Byte) {
            variableRead.addTypeCast(variableRead.getFactory().Type().bytePrimitiveType());
        } else if (value instanceof Float) {
            variableRead.addTypeCast(variableRead.getFactory().Type().floatPrimitiveType());
        } else if (value instanceof Double) {
            variableRead.addTypeCast(variableRead.getFactory().Type().doublePrimitiveType());
        } else if (value instanceof Character) {
            variableRead.addTypeCast(variableRead.getFactory().Type().characterPrimitiveType());
        }
    }

    // TODO we need maybe limit assertion on a limited number of elements
    @SuppressWarnings("unchecked")
    private static List<CtInvocation<?>> buildSnippetAssertCollection(Factory factory, CtMethod<?> testMethod, String expression, Collection value) {
        final CtVariableAccess variableRead = factory.createVariableRead(
                factory.createLocalVariableReference().setSimpleName(expression),
                false
        );
        final CtExecutableReference contains = factory.Type().get(Collection.class).getMethodsByName("contains").get(0).getReference();
        return (List<CtInvocation<?>>) value.stream()
                .limit(Math.min(value.size(), MAX_NUMBER_OF_CHECKED_ELEMENT_IN_LIST))
                .map(factory::createLiteral)
                .map(o ->
                        TestFramework.get().buildInvocationToAssertion(
                                testMethod, AssertEnum.ASSERT_TRUE,
                                Collections.singletonList(factory.createInvocation(variableRead,
                                        contains, (CtLiteral) o
                                        )
                                )
                        )
                )
                .collect(Collectors.toList());
    }

    // TODO we need maybe limit assertion on a limited number of elements
    @SuppressWarnings("unchecked")
    private static List<CtInvocation<?>> buildSnippetAssertMap(Factory factory, CtMethod<?> testMethod, String expression, Map value) {
        final CtVariableAccess variableRead = factory.createVariableRead(
                factory.createLocalVariableReference().setSimpleName(expression),
                false
        );
        final CtExecutableReference containsKey = factory.Type().get(Map.class).getMethodsByName("containsKey").get(0).getReference();
        final CtExecutableReference get = factory.Type().get(Map.class).getMethodsByName("get").get(0).getReference();
        return (List<CtInvocation<?>>) value.keySet().stream()
                .flatMap(key ->
                        Arrays.stream(new CtInvocation<?>[]{
                                        TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_TRUE,
                                                Collections.singletonList(factory.createInvocation(variableRead,
                                                        containsKey, factory.createLiteral(key)
                                                        )
                                                )
                                        ),
                                        TestFramework.get().buildInvocationToAssertion(testMethod, AssertEnum.ASSERT_EQUALS,
                                                Arrays.asList(factory.createLiteral(value.get(key)),
                                                        factory.createInvocation(variableRead,
                                                                get, factory.createLiteral(key))
                                                )
                                        )
                                }
                        )
                ).collect(Collectors.toList());
    }

    private static CtExpression printPrimitiveString(Factory factory, Object value) {
        if (value instanceof String ||
                value instanceof Short ||
                value.getClass() == short.class ||
                value instanceof Double ||
                value.getClass() == double.class ||
                value instanceof Float ||
                value.getClass() == float.class ||
                value instanceof Long ||
                value.getClass() == long.class ||
                value instanceof Character ||
                value.getClass() == char.class ||
                value instanceof Byte ||
                value.getClass() == byte.class ||
                value instanceof Integer ||
                value.getClass() == int.class) {
            return getFieldReadOrLiteral(factory, value);
        } else {
            return factory.createCodeSnippetExpression(value.toString());
        }
    }

    private static CtExpression getFieldReadOrLiteral(Factory factory, Object value) {
        if (isAFieldRead(value, factory)) {
            return getCtFieldRead(value, factory);
        } else {
            return factory.createLiteral(value);
        }
    }

    private static CtFieldRead getCtFieldRead(Object value, Factory factory) {
        final CtFieldRead fieldRead = factory.createFieldRead();
        final CtClass<?> doubleClass = factory.Class().get(value.getClass());
        final CtField<?> field = doubleClass.getField(getRightField(value, factory));
        final CtFieldReference<?> reference = field.getReference();
        fieldRead.setVariable(reference);
        return fieldRead;
    }

    private static final Class<?>[] supportedClassesForFieldRead = new Class[]{Integer.class, Double.class};

    private static String getRightField(Object value, Factory factory) {
        return Arrays.stream(supportedClassesForFieldRead).map(aClass ->
                factory.Class().get(aClass)
                        .getFields()
                        .stream()
                        .filter(CtModifiable::isStatic)
                        .filter(CtModifiable::isFinal)
                        .filter(ctField -> {
                            try {
                                return value.equals(aClass.getField(ctField.getSimpleName()).get(null));
                            } catch (Exception ignored) {
                                return false;
                            }
                        })
                        .findFirst()
                        .map(CtNamedElement::getSimpleName)
                        .orElse("")
        ).filter(s -> !s.isEmpty())
                .findFirst()
                .orElse(value.toString());
    }

    /**
     * This method checks if the given value is a field. To do this, it uses the classes in <code>supportedClassesForFieldRead</code>
     * and reflection
     *
     * @param value   value to checkEnum
     * @param factory factory with spoon model
     * @return true if the value is a field read, false otherwise
     */
    private static boolean isAFieldRead(Object value, Factory factory) {
        return (!Pattern.compile("\\d*").matcher(value.toString()).matches()) &&
                Arrays.stream(supportedClassesForFieldRead).anyMatch(aClass ->
                        factory.Class().get(aClass)
                                .getFields()
                                .stream()
                                .filter(CtModifiable::isStatic)
                                .filter(CtModifiable::isFinal)
                                .anyMatch(ctField -> {
                                    try {
                                        return value.equals(aClass.getField(ctField.getSimpleName()).get(null));
                                    } catch (Exception ignored) {
                                        return false;
                                    }
                                }));
    }

    private static Boolean isPrimitiveArray(Object value) {
        Class clazz = getArrayComponentType(value);
        return clazz == short.class ||
                clazz == double.class ||
                clazz == float.class ||
                clazz == long.class ||
                clazz == char.class ||
                clazz == byte.class ||
                clazz == int.class;
    }

    private static String getNewArrayExpression(Object obj){
        StringBuilder sb = new StringBuilder();
        ArrayList<Integer> al = new ArrayList<>();
        getArrayInstance(obj,sb,al);
        System.out.println("---------------- new: " + sb.toString());
        /*int dimensions = 0;
        int maxDimensions = 0;

        for(int i = 0; i<sb.length(); i++) {
            if (sb.charAt(i) == '{')
                dimensions++;
            if (sb.charAt(i) == '}')
                dimensions--;
            if (dimensions > maxDimensions)
                maxDimensions = dimensions;
        }*/

        /*for(int i = 0; sb.charAt(i) == '{'; i++)
            dimensions = i+1;*/
        int maxDimensions = 1 + obj.getClass().getName().lastIndexOf('[');
        for(int i = maxDimensions; i>0; i--){
            sb.insert(0,"[]");
        }
        for(int i = 1; i<sb.length(); i++){
            if(sb.charAt(i-1) == '}' && sb.charAt(i) == '{')
                sb.insert(i,",");
        }
        sb.insert(0,"new " + getArrayComponentType(obj));
        return sb.toString();
    }

    private static void getArrayInstance(Object obj, StringBuilder sb, ArrayList al) {
        sb.append("{");
        int size = Array.getLength(obj);
        al.add(size);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(obj, i);
            if (value.getClass().isArray()) {
                getArrayInstance(value, sb, al);
            } else {
                addValue(value,sb);
                if(i+1 < size)
                    sb.append(",");
            }
        }
        sb.append("}");
    }

    private static void addValue(Object value,StringBuilder sb) {
        if(value instanceof Character) {
            switch ((char) value) {
                case '\t':
                    sb.append("'\\t'");
                    break;
                case '\b':
                    sb.append("'\\b'");
                    break;
                case '\n':
                    sb.append("'\\n'");
                    break;
                case '\r':
                    sb.append("'\\r'");
                    break;
                case '\f':
                    sb.append("'\\f'");
                    break;
                case '\'':
                    sb.append("'\\''");
                    break;
                case '\"':
                    sb.append("'\\\"'");
                    break;
                case '\\':
                    sb.append("'\\\\'");
                    break;
                default:
                    sb.append("'" + value + "'");
            }
        }
        else if(value instanceof Float) {
            sb.append(value + "F");
        }
        else {
            sb.append(value);
        }
    }

    private static Class getArrayComponentType(Object obj) {
        int size = Array.getLength(obj);
        for (int i = 0; i < size; i++) {
            Object value = Array.get(obj, i);
            if (value.getClass().isArray()) {
                Class clazz = getArrayComponentType(value);
                if(clazz != null)
                    return clazz;
            } else {
                return obj.getClass().getComponentType();
            }
        }
        return null;
    }
}
