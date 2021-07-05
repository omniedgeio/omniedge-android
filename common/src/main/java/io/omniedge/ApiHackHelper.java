package io.omniedge;

import android.os.Build;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Android P 调用黑名单中变量，方法帮助类。
 * <p>
 * 注意：该类classloader会替换成系统classloader，该类中只能做反射相关工作，不能有自己的类。
 */
public class ApiHackHelper {
    private static final boolean sDebug = false;

    private static class ReflectorHolder {
        private static final ReflectorFactory.IReflector INSTANCE = ReflectorFactory.getReflector();
    }

    private static ReflectorFactory.IReflector getReflector() {
        return ReflectorHolder.INSTANCE;
    }

    /**
     * 将Field or Method设置成可访问
     *
     * @param accessibleObject 需要被设置成可访问的对象(Field or Method)
     * @return 可执行Field
     */
    public static AccessibleObject toAccessible(AccessibleObject accessibleObject) {
        if (accessibleObject != null && !accessibleObject.isAccessible()) {
            accessibleObject.setAccessible(true);
        }
        return accessibleObject;
    }

    /**
     * 反射获取类中Field变量
     *
     * @param clazz     类名
     * @param fieldName 方法名
     * @return 可执行Field
     * @throws NoSuchFieldException 异常
     */
    public static Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        return (Field) toAccessible(getReflector().getField(clazz, fieldName));
    }

    /**
     * 反射获取类中Method
     *
     * @param clazz      类名
     * @param methodName 方法名
     * @param params     参数
     * @return 可执行Method
     * @throws NoSuchMethodException 异常
     */
    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... params) throws NoSuchMethodException {
        return (Method) toAccessible(getReflector().getMethod(clazz, methodName, params));
    }

    /**
     * 反射获取类的构造方法
     *
     * @param clazz  类名
     * @param params 参数
     * @return 可执行Method
     * @throws NoSuchMethodException 异常
     */
    public static Constructor getConstructor(Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
        return (Constructor) toAccessible(getReflector().getConstructor(clazz, params));
    }

    /**
     * 反射获取class
     *
     * @param className 类名
     * @return class
     * @throws ClassNotFoundException 异常
     */
    public static Class<?> getClass(String className) throws ClassNotFoundException {
        return getReflector().getClass(className);
    }

    static final class ReflectorFactory {

        public static IReflector getReflector() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P
                    || (Build.VERSION.SDK_INT == Build.VERSION_CODES.O_MR1 && Build.VERSION.PREVIEW_SDK_INT > 0)) {
                return DoubleReflector.getInstance();
            } else {
                return Reflector.getInstance();
            }
        }

        public interface IReflector {

            /**
             * 反射获取类中Field变量
             *
             * @param clazz 类名
             * @param fieldName 变量名
             * @return 可执行Field
             * @throws NoSuchFieldException 异常
             */
            Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException;

            /**
             * 反射获取类中Method
             *
             * @param clazz 类名
             * @param methodName 方法名
             * @param params 参数
             * @return 可执行Method
             * @throws NoSuchMethodException 异常
             */
            Method getMethod(Class<?> clazz, String methodName, Class<?>... params) throws NoSuchMethodException;

            /**
             * 反射获取类的构造方法
             *
             * @param clazz 类名
             * @param params 参数
             * @return 可执行Method
             * @throws NoSuchMethodException 异常
             */
            Constructor getConstructor(Class<?> clazz, Class<?>... params) throws NoSuchMethodException;

            /**
             * 反射获取class
             *
             * @param className class name
             * @return class
             * @throws ClassNotFoundException 异常
             */
            Class<?> getClass(String className) throws ClassNotFoundException;
        }

        public static class DoubleReflector extends Reflector {
            private Method mGetDeclaredConstructorMethod;
            private Method mGetDeclaredMethod;
            private Method mGetDeclaredFieldMethod;
            private Method mForNameMethod;


            private static class LazyHolder {
                private static final IReflector INSTANCE = new DoubleReflector();
            }

            public static IReflector getInstance() {
                return LazyHolder.INSTANCE;
            }

            private DoubleReflector() {
                try {
                    mGetDeclaredConstructorMethod = Class.class.getDeclaredMethod("getDeclaredConstructor", Class[].class);
                    mGetDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);
                    mGetDeclaredFieldMethod = Class.class.getDeclaredMethod("getDeclaredField", String.class);
                    mForNameMethod = Class.class.getDeclaredMethod("forName", String.class);
                } catch (Exception e) {
                    if (sDebug) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
                Field field = null;
                if (mGetDeclaredFieldMethod != null) {
                    try {
                        field = (Field) mGetDeclaredFieldMethod.invoke(clazz, fieldName);
                    } catch (Exception e) {
                        if (sDebug) {
                            e.printStackTrace();
                        }
                    }
                }
                return field != null ? field : super.getField(clazz, fieldName);
            }

            @Override
            public Method getMethod(Class<?> clazz, String methodName, Class<?>... params) throws NoSuchMethodException {
                Method method = null;
                if (mGetDeclaredMethod != null) {
                    try {
                        method = (Method) mGetDeclaredMethod.invoke(clazz, methodName, params);
                    } catch (Exception e) {
                        if (sDebug) {
                            e.printStackTrace();
                        }
                    }
                }
                return method != null ? method : super.getMethod(clazz, methodName, params);
            }

            @Override
            public Constructor getConstructor(Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
                Constructor constructor = null;
                if (mGetDeclaredConstructorMethod != null) {
                    try {
                        constructor = (Constructor) mGetDeclaredConstructorMethod.invoke(clazz, (Object) params);
                    } catch (Exception e) {
                        if (sDebug) {
                            e.printStackTrace();
                        }
                    }
                }
                return constructor != null ? constructor : super.getConstructor(clazz, params);
            }

            @Override
            public Class<?> getClass(String className) throws ClassNotFoundException {
                Class<?> cls = null;
                if (mForNameMethod != null) {
                    try {
                        cls = (Class<?>) mForNameMethod.invoke(null, className);
                    } catch (Exception e) {
                        if (sDebug) {
                            e.printStackTrace();
                        }
                    }
                }

                return cls != null ? cls : super.getClass(className);
            }
        }

        public static class Reflector implements IReflector {
            private static class LazyHolder {
                private static final IReflector INSTANCE = new Reflector();
            }

            public static IReflector getInstance() {
                return LazyHolder.INSTANCE;
            }

            @Override
            public Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
                return clazz.getDeclaredField(fieldName);
            }

            @Override
            public Method getMethod(Class<?> clazz, String methodName, Class<?>... params) throws NoSuchMethodException {
                return clazz.getDeclaredMethod(methodName, params);
            }

            @Override
            public Constructor getConstructor(Class<?> clazz, Class<?>... params) throws NoSuchMethodException {
                return clazz.getDeclaredConstructor(params);
            }

            @Override
            public Class<?> getClass(String className) throws ClassNotFoundException {
                return this.getClass().forName(className);
            }
        }
    }
}
