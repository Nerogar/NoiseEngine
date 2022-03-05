package de.nerogar.noise.util;

import java.lang.invoke.*;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class LambdaUtil {

	/**
	 * Creates a runnable that captures all parameters of a method.
	 *
	 * @param lookup     the lookup
	 * @param object     the object the method is called on
	 * @param method     the method to implement
	 * @param parameters an array containing the captured variables of all parameters
	 * @return a runnable
	 * @throws Throwable if the runnable can not be created
	 */

	public static Runnable createRunnable(MethodHandles.Lookup lookup, Object object, Method method, Object[] parameters) throws Throwable {
		Class<?>[] implementationParameterTypes = method.getParameterTypes();

		Object[] capturedParameters = new Object[parameters.length + 1];
		capturedParameters[0] = object;
		System.arraycopy(parameters, 0, capturedParameters, 1, parameters.length);

		Class<?>[] capturedParameterTypes = new Class<?>[capturedParameters.length];
		capturedParameterTypes[0] = object.getClass();
		for (int i = 1; i < capturedParameterTypes.length; i++) {
			capturedParameterTypes[i] = implementationParameterTypes[i - 1];
		}

		MethodType factoryType = MethodType.methodType(Runnable.class, capturedParameterTypes);
		MethodType interfaceMethodType = MethodType.methodType(void.class);
		MethodHandle implementation = lookup.unreflect(method);
		MethodType dynamicMethodType = MethodType.methodType(void.class);

		CallSite callSite = LambdaMetafactory.metafactory(
				lookup,
				"run",
				factoryType,
				interfaceMethodType,
				implementation,
				dynamicMethodType
		                                                 );

		return (Runnable) callSite.getTarget().invokeWithArguments(capturedParameters);
	}

	/**
	 * Creates a consumer that captures all but the last parameter of a method.
	 *
	 * @param lookup     the lookup
	 * @param object     the object the method is called on
	 * @param method     the method to implement
	 * @param parameters an array containing the captured variables of all but the last parameter
	 * @param <T>        the type of the last parameter
	 * @return a consumer
	 * @throws Throwable if the consumer can not be created
	 */
	@SuppressWarnings("unchecked")
	public static <T> Consumer<T> createLastParameterConsumer(MethodHandles.Lookup lookup, Object object, Method method, Object[] parameters) throws Throwable {
		Class<?>[] implementationParameterTypes = method.getParameterTypes();

		Object[] capturedParameters = new Object[parameters.length + 1];
		capturedParameters[0] = object;
		System.arraycopy(parameters, 0, capturedParameters, 1, parameters.length);

		Class<?>[] capturedParameterTypes = new Class<?>[capturedParameters.length];
		capturedParameterTypes[0] = object.getClass();
		for (int i = 1; i < capturedParameterTypes.length; i++) {
			capturedParameterTypes[i] = implementationParameterTypes[i - 1];
		}

		Class<?> freeParameterType = implementationParameterTypes[implementationParameterTypes.length - 1];

		MethodType factoryType = MethodType.methodType(Consumer.class, capturedParameterTypes);
		MethodType interfaceMethodType = MethodType.methodType(void.class, Object.class);
		MethodHandle implementation = lookup.unreflect(method);
		MethodType dynamicMethodType = MethodType.methodType(void.class, freeParameterType);

		CallSite callSite = LambdaMetafactory.metafactory(
				lookup,
				"accept",
				factoryType,
				interfaceMethodType,
				implementation,
				dynamicMethodType
		                                                 );

		return (Consumer<T>) callSite.getTarget().invokeWithArguments(capturedParameters);
	}

}
