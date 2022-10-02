package reflection;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.stream.Collectors;

class Junit4TestRunner {

    @Test
    void run() throws Exception {
        Class<Junit4Test> clazz = Junit4Test.class;

        // TODO Junit4Test에서 @MyTest 애노테이션이 있는 메소드 실행
        Arrays.stream(clazz.getMethods())
                .filter(a -> a.isAnnotationPresent(MyTest.class))
                .forEach(a -> {
                    try {
                        System.out.println(a.getDeclaringClass());
                        a.invoke(new Junit4Test());
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
