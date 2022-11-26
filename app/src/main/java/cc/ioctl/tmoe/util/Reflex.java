package cc.ioctl.tmoe.util;

import static cc.ioctl.tmoe.util.Utils.loge;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.regex.Pattern;

import de.robv.android.xposed.XposedBridge;

public class Reflex {
    private Reflex() {
        throw new AssertionError("no instance for you!");
    }

    public static Object getStaticObjectOrNull(Class<?> clazz, String name, Class<?> type) {
        try {
            return getStaticObject(clazz, name, type);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Object getStaticObjectOrNull(Class<?> clazz, String name) {
        return getStaticObjectOrNull(clazz, name, null);
    }

    public static Object getStaticObject(Class<?> clazz, String name) throws NoSuchFieldException {
        return getStaticObject(clazz, name, null);
    }

    public static Object getStaticObject(Class<?> clazz, String name, Class<?> type) throws NoSuchFieldException {
        Field f = findField(clazz, type, name);
        f.setAccessible(true);
        try {
            return f.get(null);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new AssertionError(e);
        }
    }

    public static Object invokeVirtualAny(Object obj, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                _argt = m[i].getParameterTypes();
                if (_argt.length == argt.length) {
                    for (ii = 0; ii < argt.length; ii++) {
                        if (!argt[ii].equals(_argt[ii])) {
                            continue loop;
                        }
                    }
                    if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                        continue;
                    }
                    if (method == null) {
                        method = m[i];
                        //here we go through this class
                    } else {
                        throw new NoSuchMethodException(
                                "Multiple methods found for __attribute__((any))" + paramsTypesToString(
                                        argt) + " in " + obj.getClass().getName());
                    }
                }
            }
        } while (method == null && !Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(
                    "__attribute__((a))" + paramsTypesToString(argt) + " in " + obj.getClass()
                            .getName());
        }
        method.setAccessible(true);
        return method.invoke(obj, argv);
    }


    public static String paramsTypesToString(Class... c) {
        if (c == null) {
            return null;
        }
        if (c.length == 0) {
            return "()";
        }
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < c.length; i++) {
            sb.append(c[i] == null ? "[null]" : c[i].getName());
            if (i != c.length - 1) {
                sb.append(",");
            }
        }
        sb.append(")");
        return sb.toString();
    }

    public static Object invokeStaticAny(Class<?> clazz, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                _argt = m[i].getParameterTypes();
                if (_argt.length == argt.length) {
                    for (ii = 0; ii < argt.length; ii++) {
                        if (!argt[ii].equals(_argt[ii])) {
                            continue loop;
                        }
                    }
                    if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                        continue;
                    }
                    if (method == null) {
                        method = m[i];
                        //here we go through this class
                    } else {
                        throw new NoSuchMethodException(
                                "Multiple methods found for __attribute__((any))" + paramsTypesToString(
                                        argt) + " in " + clazz.getName());
                    }
                }
            }
        } while (method == null && !Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(
                    "__attribute__((a))" + paramsTypesToString(argt) + " in " + clazz.getName());
        }
        method.setAccessible(true);
        return method.invoke(null, argv);
    }

    public static Object invokeVirtualDeclaredModifierAny(Object obj, int requiredMask,
                                                          int excludedMask, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = clazz.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if ((m[i].getModifiers() & requiredMask) != requiredMask) {
                    continue;
                }
                if ((m[i].getModifiers() & excludedMask) != 0) {
                    continue;
                }
                if (method == null) {
                    method = m[i];
                    //here we go through this class
                } else {
                    throw new NoSuchMethodException(
                            "Multiple methods found for __attribute__((any))" + paramsTypesToString(
                                    argt) + " in " + obj.getClass().getName());
                }
            }
        }
        if (method == null) {
            throw new NoSuchMethodException(
                    "__attribute__((a))" + paramsTypesToString(argt) + " in " + obj.getClass()
                            .getName());
        }
        method.setAccessible(true);
        return method.invoke(obj, argv);
    }

    /**
     * DO NOT USE, it's fragile instance methods are counted, both public/private, static methods
     * are EXCLUDED, count from 0
     *
     * @param obj
     * @param ordinal                the ordinal of instance method meeting the signature
     * @param expected               how many instance methods are expected there
     * @param argsTypesAndReturnType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invokeVirtualDeclaredOrdinal(Object obj, int ordinal, int expected,
                                                      boolean strict, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method[] candidates = new Method[expected];
        int count = 0;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = clazz.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if (Modifier.isStatic(m[i].getModifiers())) {
                    continue;
                }
                if (count < expected) {
                    candidates[count++] = m[i];
                } else {
                    if (!strict) {
                        break;
                    }
                    throw new NoSuchMethodException(
                            "More methods than expected(" + expected + ") at " + paramsTypesToString(
                                    argt) + " in " + obj.getClass().getName());
                }
            }
        }
        if (strict && count != expected) {
            throw new NoSuchMethodException(
                    "Less methods(" + count + ") than expected(" + expected + ") at "
                            + paramsTypesToString(argt) + " in " + obj.getClass().getName());
        }
        Arrays.sort(candidates, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return strcmp(o1.getName(), o2.getName());
        });
        candidates[ordinal].setAccessible(true);
        return candidates[ordinal].invoke(obj, argv);
    }

    public static int strcmp(String stra, String strb) {
        int len = Math.min(stra.length(), strb.length());
        for (int i = 0; i < len; i++) {
            char a = stra.charAt(i);
            char b = strb.charAt(i);
            if (a != b) {
                return a - b;
            }
        }
        return stra.length() - strb.length();
    }

    /**
     * DO NOT USE, it's fragile instance methods are counted, both public/private, static methods
     * are EXCLUDED, count from 0
     *
     * @param obj
     * @param fixed                  which class
     * @param ordinal                the ordinal of instance method meeting the signature
     * @param expected               how many instance methods are expected there
     * @param argsTypesAndReturnType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Object invokeVirtualDeclaredFixedModifierOrdinal(Object obj, int requiredMask, int excludedMask,
                                                                   Class<?> fixed, int ordinal, int expected, boolean strict,
                                                                   Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method[] candidates = new Method[expected];
        int count = 0;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = fixed.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if (Modifier.isStatic(m[i].getModifiers())) {
                    continue;
                }
                if ((m[i].getModifiers() & requiredMask) != requiredMask) {
                    continue;
                }
                if ((m[i].getModifiers() & excludedMask) != 0) {
                    continue;
                }
                if (count < expected) {
                    candidates[count++] = m[i];
                } else {
                    if (!strict) {
                        break;
                    }
                    throw new NoSuchMethodException(
                            "More methods than expected(" + expected + ") at " + paramsTypesToString(
                                    argt) + " in " + obj.getClass().getName());
                }
            }
        }
        if (strict && count != expected) {
            throw new NoSuchMethodException(
                    "Less methods(" + count + ") than expected(" + expected + ") at "
                            + paramsTypesToString(argt) + " in " + obj.getClass().getName());
        }
        Arrays.sort(candidates, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return strcmp(o1.getName(), o2.getName());
        });
        candidates[ordinal].setAccessible(true);
        return candidates[ordinal].invoke(obj, argv);
    }

    /**
     * DO NOT USE, it's fragile instance methods are counted, both public/private, static methods
     * are EXCLUDED, count from 0
     *
     * @param obj
     * @param ordinal                the ordinal of instance method meeting the signature
     * @param expected               how many instance methods are expected there
     * @param argsTypesAndReturnType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Deprecated
    public static Object invokeVirtualDeclaredOrdinalModifier(Object obj, int ordinal, int expected,
                                                              boolean strict, int requiredMask, int excludedMask,
                                                              Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method[] candidates = new Method[expected];
        int count = 0;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = clazz.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if (Modifier.isStatic(m[i].getModifiers())) {
                    continue;
                }
                if ((m[i].getModifiers() & requiredMask) != requiredMask) {
                    continue;
                }
                if ((m[i].getModifiers() & excludedMask) != 0) {
                    continue;
                }
                if (count < expected) {
                    candidates[count++] = m[i];
                } else {
                    if (!strict) {
                        break;
                    }
                    throw new NoSuchMethodException(
                            "More methods than expected(" + expected + ") at " + paramsTypesToString(
                                    argt) + " in " + obj.getClass().getName());
                }
            }
        }
        if (strict && count != expected) {
            throw new NoSuchMethodException(
                    "Less methods(" + count + ") than expected(" + expected + ") at "
                            + paramsTypesToString(argt) + " in " + obj.getClass().getName());
        }
        Arrays.sort(candidates, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return strcmp(o1.getName(), o2.getName());
        });
        candidates[ordinal].setAccessible(true);
        return candidates[ordinal].invoke(obj, argv);
    }

    /**
     * DO NOT USE, it's fragile static methods are counted, both public/private, instance methods
     * are EXCLUDED, count from 0
     *
     * @param clazz
     * @param ordinal                the ordinal of instance method meeting the signature
     * @param expected               how many instance methods are expected there
     * @param argsTypesAndReturnType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Deprecated
    public static Object invokeStaticDeclaredOrdinalModifier(Class clazz, int ordinal, int expected,
                                                             boolean strict, int requiredMask, int excludedMask,
                                                             Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method[] candidates = new Method[expected];
        int count = 0;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = clazz.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if (!Modifier.isStatic(m[i].getModifiers())) {
                    continue;
                }
                if ((m[i].getModifiers() & requiredMask) != requiredMask) {
                    continue;
                }
                if ((m[i].getModifiers() & excludedMask) != 0) {
                    continue;
                }
                if (count < expected) {
                    candidates[count++] = m[i];
                } else {
                    if (!strict) {
                        break;
                    }
                    throw new NoSuchMethodException(
                            "More methods than expected(" + expected + ") at " + paramsTypesToString(
                                    argt) + " in " + clazz.getName());
                }
            }
        }
        if (strict && count != expected) {
            throw new NoSuchMethodException(
                    "Less methods(" + count + ") than expected(" + expected + ") at "
                            + paramsTypesToString(argt) + " in " + clazz.getName());
        }
        Arrays.sort(candidates, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return strcmp(o1.getName(), o2.getName());
        });
        candidates[ordinal].setAccessible(true);
        return candidates[ordinal].invoke(null, argv);
    }

    /**
     * DO NOT USE, it's fragile static methods are counted, both public/private, instance methods
     * are EXCLUDED, count from 0
     *
     * @param clazz
     * @param ordinal                the ordinal of instance method meeting the signature
     * @param expected               how many instance methods are expected there
     * @param argsTypesAndReturnType
     * @return
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    @Deprecated
    public static Object invokeStaticDeclaredOrdinal(Class clazz, int ordinal, int expected,
                                                     boolean strict, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method[] candidates = new Method[expected];
        int count = 0;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        m = clazz.getDeclaredMethods();
        loop:
        for (i = 0; i < m.length; i++) {
            _argt = m[i].getParameterTypes();
            if (_argt.length == argt.length) {
                for (ii = 0; ii < argt.length; ii++) {
                    if (!argt[ii].equals(_argt[ii])) {
                        continue loop;
                    }
                }
                if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                    continue;
                }
                if (!Modifier.isStatic(m[i].getModifiers())) {
                    continue;
                }
                if (count < expected) {
                    candidates[count++] = m[i];
                } else {
                    if (!strict) {
                        break;
                    }
                    throw new NoSuchMethodException(
                            "More methods than expected(" + expected + ") at " + paramsTypesToString(
                                    argt) + " in " + clazz.getName());
                }
            }
        }
        if (strict && count != expected) {
            throw new NoSuchMethodException(
                    "Less methods(" + count + ") than expected(" + expected + ") at "
                            + paramsTypesToString(argt) + " in " + clazz.getName());
        }
        Arrays.sort(candidates, (o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            }
            if (o1 == null) {
                return 1;
            }
            if (o2 == null) {
                return -1;
            }
            return strcmp(o1.getName(), o2.getName());
        });
        candidates[ordinal].setAccessible(true);
        return candidates[ordinal].invoke(null, argv);
    }

    public static Object invokeVirtual(Object obj, String name, Object... argsTypesAndReturnType)
            throws ReflectiveOperationException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) {
                                continue loop;
                            }
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                            continue;
                        }
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(
                    name + paramsTypesToString(argt) + " in " + obj.getClass().getName());
        }
        method.setAccessible(true);
        return method.invoke(obj, argv);
    }

    public static Object invokeVirtualOriginal(Object obj, String name, Object... argsTypesAndReturnType)
            throws ReflectiveOperationException {
        Class clazz = obj.getClass();
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) {
                                continue loop;
                            }
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                            continue;
                        }
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(name + " in " + obj.getClass().getName());
        }
        method.setAccessible(true);
        Object ret;
        boolean needPatch = false;
        try {
            ret = XposedBridge.invokeOriginalMethod(method, obj, argv);
            return ret;
        } catch (IllegalStateException e) {
            //For SandHook-EdXp: Method not hooked.
            needPatch = true;
        } catch (InvocationTargetException | NullPointerException e) {
            //For TaiChi
            Throwable cause;
            if (e instanceof InvocationTargetException) {
                cause = e.getCause();
            } else {
                cause = e;
            }
            if (cause instanceof NullPointerException) {
                String tr = android.util.Log.getStackTraceString(cause);
                if (tr.indexOf("ExposedBridge.invokeOriginalMethod") != 0
                        || Pattern.compile("me\\.[.a-zA-Z]+\\.invokeOriginalMethod").matcher(tr)
                        .find()) {
                    needPatch = true;
                }
            }
            if (!needPatch) {
                throw e;
            }
        }
        //here needPatch is always true
        ret = method.invoke(obj, argv);
        return ret;
    }

    public static Object invokeStatic(Class<?> staticClass, String name, Object... argsTypesAndReturnType)
            throws NoSuchMethodException, InvocationTargetException, IllegalArgumentException {
        Class clazz = staticClass;
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) {
                                continue loop;
                            }
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                            continue;
                        }
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(name + paramsTypesToString(argt));
        }
        method.setAccessible(true);
        try {
            return method.invoke(null, argv);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new AssertionError(e);
        }
    }

    public static Object getInstanceObjectOrNull(Object obj, String name) {
        return getInstanceObjectOrNull(obj, name, null);
    }

    public static <T> T getInstanceObjectOrNull(Object obj, String name, Class<T> type) {
        try {
            Class clazz = obj.getClass();
            Field f = findField(clazz, type, name);
            f.setAccessible(true);
            return (T) f.get(obj);
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T getInstanceObject(Object obj, String name, Class<T> type) throws NoSuchFieldException {
        Class<?> clazz = obj.getClass();
        Field f = findField(clazz, type, name);
        f.setAccessible(true);
        try {
            return (T) f.get(obj);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new AssertionError(e);
        }
    }

    public static void setInstanceObject(@NonNull Object obj, @NonNull String name,
                                         @Nullable Object value) throws NoSuchFieldException {
        setInstanceObject(obj, name, null, value);
    }

    public static void setInstanceObject(@NonNull Object obj, @NonNull String name,
                                         @Nullable Class<?> type, @Nullable Object value) throws NoSuchFieldException {
        Class<?> clazz = obj.getClass();
        Field f = findField(clazz, type, name);
        f.setAccessible(true);
        try {
            f.set(obj, value);
        } catch (IllegalAccessException e) {
            // should not happen
            throw new AssertionError(e);
        }
    }

    public static void setStaticObjectSilently(Class<?> clz, String name, Object value) {
        setStaticObjectSilently(clz, name, null, value);
    }

    public static void setStaticObjectSilently(Class<?> clazz, String name, Class<?> type, Object value) {
        try {
            setStaticObject(clazz, name, type, value);
        } catch (NoSuchFieldException ignored) {
        }
    }

    public static void setStaticObject(Class<?> clz, String name, Object value) throws NoSuchFieldException {
        setStaticObject(clz, name, null, value);
    }

    public static void setStaticObject(Class<?> clazz, String name, Class<?> type, Object value)
            throws NoSuchFieldException {
        Field f = findField(clazz, type, name);
        f.setAccessible(true);
        try {
            f.set(null, value);
        } catch (IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }

    public static Method hasMethod(Object obj, String name, Object... argsTypesAndReturnType)
            throws IllegalArgumentException {
        Class clazz;
        if (obj == null) {
            throw new NullPointerException("obj/clazz == null");
        }
        if (obj instanceof Class) {
            clazz = (Class) obj;
        } else {
            clazz = obj.getClass();
        }
        return hasMethod(clazz, name, argsTypesAndReturnType);
    }

    public static Method hasMethod(Class clazz, String name, Object... argsTypesAndReturnType)
            throws IllegalArgumentException {
        int argc = argsTypesAndReturnType.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        Class returnType = null;
        if (argc * 2 + 1 == argsTypesAndReturnType.length) {
            returnType = (Class) argsTypesAndReturnType[argsTypesAndReturnType.length - 1];
        }
        int i, ii;
        Method[] m;
        Method method = null;
        Class[] _argt;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsTypesAndReturnType[argc + i];
            argv[i] = argsTypesAndReturnType[i];
        }
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (i = 0; i < m.length; i++) {
                if (m[i].getName().equals(name)) {
                    _argt = m[i].getParameterTypes();
                    if (_argt.length == argt.length) {
                        for (ii = 0; ii < argt.length; ii++) {
                            if (!argt[ii].equals(_argt[ii])) {
                                continue loop;
                            }
                        }
                        if (returnType != null && !returnType.equals(m[i].getReturnType())) {
                            continue;
                        }
                        method = m[i];
                        break loop_main;
                    }
                }
            }
        } while (!Object.class.equals(clazz = clazz.getSuperclass()));
        if (method != null) {
            method.setAccessible(true);
        }
        return method;
    }

    public static Object newInstance(Class clazz, Object... argsAndTypes)
            throws ReflectiveOperationException {
        int argc = argsAndTypes.length / 2;
        Class[] argt = new Class[argc];
        Object[] argv = new Object[argc];
        int i;
        Constructor m;
        for (i = 0; i < argc; i++) {
            argt[i] = (Class) argsAndTypes[argc + i];
            argv[i] = argsAndTypes[i];
        }
        m = clazz.getDeclaredConstructor(argt);
        m.setAccessible(true);
        try {
            return m.newInstance(argv);
        } catch (IllegalAccessException e) {
            loge(e);
            //should NOT happen
            throw new RuntimeException(e);
        }
    }

    public static Field findField(Class<?> clazz, Class<?> type, String name) throws NoSuchFieldException {
        if (clazz == null) {
            throw new NullPointerException("clazz == null");
        }
        if (name == null) {
            throw new NullPointerException("name == null");
        }
        Class<?> clz = clazz;
        do {
            for (Field field : clz.getDeclaredFields()) {
                if ((type == null || field.getType().equals(type)) && field.getName().equals(name)) {
                    field.setAccessible(true);
                    return field;
                }
            }
        } while ((clz = clz.getSuperclass()) != null);
        String errMsg = type == null ? ("field '" + name + "' not found in " + clazz.getName())
                : ("field '" + name + "' of type " + type.getName() + " not found in " + clazz.getName());
        throw new NoSuchFieldException(errMsg);
    }

    public static Method findMethodByTypes_1(Class<?> clazz, Class returnType, Class... argt)
            throws NoSuchMethodException {
        Method method = null;
        Method[] m;
        Class[] _argt;
        loop_main:
        do {
            m = clazz.getDeclaredMethods();
            loop:
            for (Method value : m) {
                _argt = value.getParameterTypes();
                if (_argt.length == argt.length) {
                    for (int ii = 0; ii < argt.length; ii++) {
                        if (!argt[ii].equals(_argt[ii])) {
                            continue loop;
                        }
                    }
                    if (returnType != null && !returnType.equals(value.getReturnType())) {
                        continue;
                    }
                    if (method == null) {
                        method = value;
                        //here we go through this class
                    } else {
                        throw new NoSuchMethodException(
                                "Multiple methods found for __attribute__((any))" + paramsTypesToString(
                                        argt) + " in " + clazz.getName());
                    }
                }
            }
        } while (method == null && !Object.class.equals(clazz = clazz.getSuperclass()));
        if (method == null) {
            throw new NoSuchMethodException(
                    "__attribute__((a))" + paramsTypesToString(argt) + " in " + clazz.getName());
        }
        method.setAccessible(true);
        return method;
    }

    public static Field hasField(Object obj, String name) throws ReflectiveOperationException {
        return hasField(obj, name, null);
    }

    public static Field hasField(Object obj, String name, Class<?> type) throws ReflectiveOperationException {
        if (obj == null) {
            throw new NullPointerException("obj/class == null");
        }
        Class<?> clazz;
        if (obj instanceof Class) {
            clazz = (Class<?>) obj;
        } else {
            clazz = obj.getClass();
        }
        return findField(clazz, type, name);
    }

    public static <T> T getFirstByType(Object obj, Class<T> type) throws NoSuchFieldException {
        if (obj == null) {
            throw new NullPointerException("obj == null");
        }
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        Class<?> clz = obj.getClass();
        while (clz != null && !clz.equals(Object.class)) {
            for (Field f : clz.getDeclaredFields()) {
                if (!f.getType().equals(type)) {
                    continue;
                }
                f.setAccessible(true);
                try {
                    return (T) f.get(obj);
                } catch (IllegalAccessException ignored) {
                    // should not happen
                }
            }
            clz = clz.getSuperclass();
        }
        throw new NoSuchFieldException("No field of type " + type.getName() + " found in "
                + obj.getClass().getName() + " and superclasses");
    }

    public static <T> T getFirstByTypeOrNull(Object obj, Class<T> type) {
        try {
            return getFirstByType(obj, type);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    /**
     * NSF: Neither Static nor Final
     *
     * @param obj  thisObj
     * @param type Field type
     * @return the FIRST(as declared seq in dex) field value meeting the type
     */
    //@Deprecated
    public static <T> T getFirstNSFByType(Object obj, Class<T> type) {
        if (obj == null) {
            throw new NullPointerException("obj == null");
        }
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        Class clz = obj.getClass();
        while (clz != null && !clz.equals(Object.class)) {
            for (Field f : clz.getDeclaredFields()) {
                if (!f.getType().equals(type)) {
                    continue;
                }
                int m = f.getModifiers();
                if (Modifier.isStatic(m) || Modifier.isFinal(m)) {
                    continue;
                }
                f.setAccessible(true);
                try {
                    return (T) f.get(obj);
                } catch (IllegalAccessException ignored) {
                    //should not happen
                }
            }
            clz = clz.getSuperclass();
        }
        return null;
    }

    /**
     * NSF: Neither Static nor Final
     *
     * @param clz  Obj class
     * @param type Field type
     * @return the FIRST(as declared seq in dex) field value meeting the type
     */
    //@Deprecated
    public static Field getFirstNSFFieldByType(Class clz, Class type) {
        if (clz == null) {
            throw new NullPointerException("clz == null");
        }
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        while (clz != null && !clz.equals(Object.class)) {
            for (Field f : clz.getDeclaredFields()) {
                if (!f.getType().equals(type)) {
                    continue;
                }
                int m = f.getModifiers();
                if (Modifier.isStatic(m) || Modifier.isFinal(m)) {
                    continue;
                }
                f.setAccessible(true);
                return f;
            }
            clz = clz.getSuperclass();
        }
        return null;
    }

    @NonNull
    public static String getShortClassName(Object obj) {
        String name;
        if (obj == null) {
            return "null";
        }
        if (obj instanceof String) {
            name = ((String) obj).replace("/", ".");
        } else if (obj instanceof Class) {
            name = ((Class<?>) obj).getName();
        } else if (obj instanceof Field) {
            name = ((Field) obj).getType().getName();
        } else {
            name = obj.getClass().getName();
        }
        if (!name.contains(".")) {
            return name;
        }
        int p = name.lastIndexOf('.');
        return name.substring(p + 1);
    }

    public static boolean isCallingFromMethod(@NonNull String methodName, int maxDepth) {
        if (TextUtils.isEmpty(methodName)) {
            throw new IllegalArgumentException("methodName is empty");
        }
        if (maxDepth < 0) {
            throw new IllegalArgumentException("maxDepth must >= 0");
        }
        StackTraceElement[] ste = new Throwable().getStackTrace();
        int i = 1;
        while (i < ste.length && (i <= maxDepth || maxDepth == 0)) {
            if (ste[i].getMethodName().equals(methodName)) {
                return true;
            }
            i++;
        }
        return false;
    }

}
