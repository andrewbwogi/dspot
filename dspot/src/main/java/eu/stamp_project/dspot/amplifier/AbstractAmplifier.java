package eu.stamp_project.dspot.amplifier;

import eu.stamp_project.test_framework.TestFramework;
import eu.stamp_project.utils.CloneHelper;
import eu.stamp_project.utils.Counter;
import spoon.reflect.code.*;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtLocalVariableImpl;
import spoon.support.reflect.code.CtNewArrayImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Created by Benjamin DANGLOT
 * benjamin.danglot@inria.fr
 * on 16/07/18
 */
public abstract class AbstractAmplifier<T extends CtElement> implements Amplifier {

    /**
     * String used to mark an element as amplified
     */
    protected final String METADATA_KEY = "amplified";

    /**
     * Checks if the given element has already been amplified
     *
     * @param element
     * @return
     */
    protected boolean hasBeenAmplified(CtElement element) {
        return (element.getMetadata(METADATA_KEY) != null &&
                (boolean) element.getMetadata(METADATA_KEY));
    }

    /**
     * This methods aims at reducing the given list of elements in order to avoid redundant amplification
     * For most of amplification, amplify elements that are before the latest amplified elements produce
     * redundant amplifications.
     * For more information, and a provided example,
     * see https://github.com/STAMP-project/dspot/issues/454
     *
     * @param elementsToBeReduced list of elements to be reduced
     * @return a reduced list of elements, according to previous amplification.
     * If this is the first amplification, no element would be marked as amplified
     */
    protected List<T> reduceAlreadyAmplifiedElements(List<T> elementsToBeReduced) {
        List<T> reducedElements = new ArrayList<>(elementsToBeReduced);
        // we now reduce the literals list, see https://github.com/STAMP-project/dspot/issues/454
        final Integer maxIndex = reducedElements.stream()
                .filter(this::hasBeenAmplified)
                .map(reducedElements::indexOf)
                .max(Integer::compareTo)
                .orElse(-1);
        if (maxIndex > -1 && maxIndex <= reducedElements.size()) {
            reducedElements = reducedElements.subList(maxIndex + 1, reducedElements.size());
        }
        return reducedElements;
    }

    /**
     * This method replace the given original element by the amplified one, by producing a clone of the given test method.
     * The amplified element would be marked as amplified, with the METADATA_KEY,
     * <i>i.e.</i> calling {@link #hasBeenAmplified(CtElement)} returns true.
     *
     * @param originalElement  element to be replaced
     * @param amplifiedElement new element to be used
     * @param testMethod       test method to be cloned
     * @return a clone of the given test method with an amplified element that replaces the original element
     */
    protected CtMethod<?> replace(T originalElement, T amplifiedElement, CtMethod<?> testMethod) {
        originalElement.replace(amplifiedElement);
        amplifiedElement.putMetadata(this.METADATA_KEY, true);
        CtMethod<?> clone = CloneHelper.cloneTestMethodForAmp(testMethod, getSuffix());
        amplifiedElement.replace(originalElement);
        Counter.updateInputOf(clone, 1);
        return clone;
    }

    /**
     * @return This method aims at giving a specific name per Amplifier to the amplified test method
     */
    protected abstract String getSuffix();

    protected abstract List<T> getOriginals(CtMethod<?> testMethod);

    protected abstract Set<T> amplify(T original, CtMethod<?> testMethod);

    @Override
    public Stream<CtMethod<?>> amplify(CtMethod<?> testMethod, int iteration) {

        int[] a = {1,2};
        System.out.println(a.getClass().isArray());

        CtNewArrayImpl newArray = new CtNewArrayImpl<>();
        System.out.println("************** new array: ");
        System.out.println(newArray);

        TypeFilter<CtExpression<Boolean>> LITERAL_TYPE_FILTER = new TypeFilter<CtExpression<Boolean>>(CtExpression.class);


        List<CtNewArrayImpl> c = testMethod.getElements(new TypeFilter<>(CtNewArrayImpl.class));
        System.out.println("................ custom filter");
        for(CtNewArrayImpl o : c){
            System.out.println(o);
            System.out.println(o.getClass());
            System.out.println("------------------");
        }
        System.out.println("................ custom filter end");


        List<CtElement> e = testMethod.getElements(new TypeFilter<>(CtElement.class));
        System.out.println("................ boolean filter");
        for(CtElement o : e){
            System.out.println(o);
            System.out.println(o.getClass());
            System.out.println("------------------");
        }
        System.out.println("................ boolean filter end");

        Boolean b = true;
        //System.out.println(b.getClass().getName());
        int[] i = new int[]{1,2};
        //System.out.println(i.getClass().getName());
        //System.out.println("oooooooooooo statements");
        for(CtStatement s : testMethod.getBody().getStatements()){
            //System.out.println(s);
            //System.out.println(s.getClass());
            if(s instanceof CtLocalVariableImpl){
                /*System.out.println(((CtLocalVariableImpl) s).getAssignment());
                System.out.println(((CtLocalVariableImpl) s).getAssignment().getClass());
                System.out.println(((CtLocalVariableImpl) s).getAssignment().getType());
                System.out.println(((CtLocalVariableImpl) s).getAssignment().getType().getClass());*/
            }
            //System.out.println("----------");
        }


        List<T> originals = this.getOriginals(testMethod);
        System.out.println("................ from getOriginals");
        for(T o : originals){
            System.out.println(o);
        }
        List<T> reducedOriginals = this.reduceAlreadyAmplifiedElements(originals);
        return reducedOriginals.stream()
                .filter(reducedOriginal ->
                        reducedOriginal.getMetadata(METADATA_KEY) == null ||
                                !(boolean) reducedOriginal.getMetadata(METADATA_KEY)
                ).flatMap(original ->
                        this.amplify(original, testMethod)
                                .stream()
                                .map(amplified -> this.replace(original, amplified, testMethod))
                );
    }
}
