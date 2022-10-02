package nextstep.study.di.stage3.context;

import javassist.tools.reflect.Reflection;
import org.reflections.Reflections;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContext {

    private final Set<Object> beans;
    private final Map<Class<?>, Object> cache;

    private final Reflections reflections = new Reflections("nextstep.study.di.stage3.context");
    public DIContext(final Set<Class<?>> classes) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        this.beans = new HashSet<>();
        this.cache = new HashMap<>();
        for (Class<?> clazz : classes) {
            dfs(clazz);
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        for (Object instance : this.beans) {
            if (instance.getClass() == aClass) {
                return (T) instance;
            }
        }
        return null;
    }

    private Object dfs(Class<?> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        if (cache.containsKey(clazz)) {
            return cache.get(clazz);
        }
        if (Arrays.stream(clazz.getDeclaredConstructors())
                .anyMatch(each -> each.getParameterCount() == 0)) {
            Object instance = clazz.getConstructor().newInstance();
            cache.put(clazz, instance);
            beans.add(instance);
            return instance;
        }
        int idx = 0;
        Constructor<?> declaredConstructors = clazz.getDeclaredConstructors()[0];
        Class<?>[] parameterTypes = declaredConstructors.getParameterTypes();
        int size = parameterTypes.length;
        Object[] instances = new Object[size];
        for (Class<?> each : parameterTypes) {
            if (!each.isInterface()) {
                instances[idx] = dfs(each);
                idx += 1;
                continue;
            }
            Set<Class<?>> subTypesOf = reflections.getSubTypesOf((Class<Object>) each);
            List<Object> subInstances = new ArrayList<>();
            for (Class<?> subClass : subTypesOf) {
                if (!subClass.isInterface()) {
                    subInstances.add(dfs(subClass));
                }
            }
            instances[idx] = subInstances.get(0);
            idx += 1;
        }
        Object instance = declaredConstructors.newInstance(instances);
        cache.put(clazz, instance);
        beans.add(instance);
        return instance;
    }
}
