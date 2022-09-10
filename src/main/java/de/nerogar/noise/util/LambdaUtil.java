package de.nerogar.noise.util;

import de.nerogar.noise.exception.InvalidArgumentException;
import de.nerogar.noise.exception.NotImplementedException;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

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

    /**
     * Creates a function that represents the getter of a class
     *
     * @param lookup      the lookup
     * @param method      the getter method to implement
     * @param <T>         the type of the object
     * @param <R>         the return type of the getter
     * @throws InvalidArgumentException if the method is not a getter
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> createGetter(MethodHandles.Lookup lookup, Method method) throws Throwable {
        if (method.getParameterTypes().length != 0) {
            throw new InvalidArgumentException("method has parameters");
        }

        Class<?> objectClass = method.getDeclaringClass();
        Class<?> returnType = method.getReturnType();

        MethodType factoryType = MethodType.methodType(Function.class);
        MethodType interfaceMethodType = MethodType.methodType(Object.class, Object.class);
        MethodHandle implementation = lookup.unreflect(method);
        MethodType dynamicMethodType = MethodType.methodType(returnType, objectClass);

        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                factoryType,
                interfaceMethodType,
                implementation,
                dynamicMethodType
        );

        return (Function<T, R>) callSite.getTarget().invokeExact();
    }

    /**
     * Creates a function that represents the getter of a class for a public field
     *
     * @param lookup      the lookup
     * @param field       the field that is returned by the getter
     * @param <T>         the type of the object
     * @param <R>         the return type of the getter
     */
    @SuppressWarnings("unchecked")
    public static <T, R> Function<T, R> createGetter(MethodHandles.Lookup lookup, Field field) throws Throwable {
        Class<?> objectClass = field.getDeclaringClass();

        final MethodHandle setter = lookup.unreflectGetter(field);
        MethodType type = setter.type();
        if (field.getType().isPrimitive()) {
            type = type.wrap().changeReturnType(void.class);
        }

        MethodType factoryType = MethodType.methodType(Function.class, MethodHandle.class);
        MethodType interfaceMethodType = type.erase();
        MethodHandle implementation = MethodHandles.exactInvoker(setter.type());
        MethodType dynamicMethodType = MethodType.methodType(field.getType(), objectClass);

        final CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                factoryType,
                interfaceMethodType,
                implementation,
                dynamicMethodType
        );
        return (Function<T, R>) site.getTarget().invokeExact(setter);
    }

    public static <C, V> BiConsumer<C, V> createSetter(MethodHandles.Lookup lookup, Field field) throws Throwable {
        final MethodHandle setter = lookup.unreflectSetter(field);
        MethodType type = setter.type();
        if (field.getType().isPrimitive()) {
            type = type.wrap().changeReturnType(void.class);
        }
        final CallSite site = LambdaMetafactory.metafactory(
                lookup,
                "accept",
                MethodType.methodType(BiConsumer.class, MethodHandle.class),
                type.erase(),
                MethodHandles.exactInvoker(setter.type()),
                type
        );
        return (BiConsumer<C, V>) site.getTarget().invokeExact(setter);
    }

}
