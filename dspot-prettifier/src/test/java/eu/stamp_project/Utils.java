package eu.stamp_project;

import eu.stamp_project.dspot.common.automaticbuilder.AutomaticBuilder;
import eu.stamp_project.dspot.common.configuration.UserInput;
import eu.stamp_project.dspot.common.test_framework.TestFramework;
import eu.stamp_project.dspot.common.compilation.DSpotCompiler;
import eu.stamp_project.dspot.common.configuration.InitializeDSpot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.visitor.chain.CtQueryable;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.List;
import java.util.Set;


/**
 * User: Simon
 * Date: 25/11/16
 * Time: 11:16
 */
public class Utils {

	public static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

	private static UserInput configuration;

	public static void init(UserInput configuration) {
		InitializeDSpot initializeDSpot = new InitializeDSpot();
		final AutomaticBuilder automaticBuilder = configuration.getBuilderEnum().getAutomaticBuilder(configuration);
		final String dependencies = initializeDSpot.completeDependencies(configuration, automaticBuilder);
		final DSpotCompiler compiler = DSpotCompiler.createDSpotCompiler(
				configuration,
				dependencies
		);
		configuration.setFactory(compiler.getLauncher().getFactory());
		initializeDSpot.initHelpers(configuration);
		Utils.configuration = configuration;
	}

	public static CtClass<?> findClass(String fullQualifiedName) {
		return Utils.configuration.getFactory().Class().get(fullQualifiedName);
	}

	public static CtMethod<?> findMethod(CtClass<?> ctClass, String methodName) {
		Set<CtMethod<?>> mths = ctClass.getMethods();
		return mths.stream()
				.filter(mth -> mth.getSimpleName().endsWith(methodName))
				.findFirst()
				.orElse(null);
	}

	public static CtMethod findMethod(String className, String methodName) {
		return findClass(className).getMethods().stream()
				.filter(mth -> mth.getSimpleName().endsWith(methodName))
				.findFirst()
				.orElse(null);
	}

	public static List<CtMethod<?>> getAllTestMethodsFrom(String className) {
		return getAllTestMethodsFrom(getFactory().Type().get(className));
	}

	public static List<CtMethod<?>> getAllTestMethodsFrom(CtType<?> testClass) {
		return testClass.filterChildren(new TypeFilter<CtMethod<?>>(CtMethod.class) {
			@Override
			public boolean matches(CtMethod<?> element) {
				return TestFramework.get().isTest(element);
			}
		}).list();
	}

	@SuppressWarnings("unchecked")
	public static <T> void replaceGivenLiteralByNewValue(CtQueryable parent, T newValue) {
		((CtLiteral<T>)parent.filterChildren(new FILTER_LITERAL_OF_GIVEN_TYPE(newValue.getClass()))
				.first())
				.replace(getFactory().createLiteral(newValue));
	}

	public static Factory getFactory() {
		return Utils.configuration.getFactory();
	}

	public static final class FILTER_LITERAL_OF_GIVEN_TYPE extends TypeFilter<CtLiteral> {

		private Class<?> clazz;

		public FILTER_LITERAL_OF_GIVEN_TYPE(Class<?> clazz) {
			super(CtLiteral.class);
			this.clazz = clazz;
		}

		@Override
		public boolean matches(CtLiteral element) {
			return clazz.isAssignableFrom(element.getValue().getClass()) ||
					element.getValue().getClass().isAssignableFrom(clazz);
		}
	}
}
