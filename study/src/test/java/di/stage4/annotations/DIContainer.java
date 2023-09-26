package di.stage4.annotations;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = createBeans(classes);
        this.beans.forEach(this::setFields);
    }

    public static DIContainer createContainerForPackage(final String rootPackageName) {
        final Set<Class<?>> classes = ClassPathScanner.getAllClassesInPackage(rootPackageName);
        return new DIContainer(classes);
    }

    private Set<Object> createBeans(final Set<Class<?>> classes) {
        Set<Object> o = new HashSet<>();
        try{
            for (Class<?> aClass : classes) {
                final Constructor<?> constructor = aClass.getDeclaredConstructor();
                constructor.setAccessible(true);
                final Object instance = constructor.newInstance();
                o.add(instance);
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("객체 생성 실패");
        }
        return o;
    }

    private void setFields(final Object bean) {
        try{
            final Field[] fields = bean.getClass().getDeclaredFields();
            final List<Field> injectedFields = Arrays.stream(fields)
                    .filter(field -> field.isAnnotationPresent(Inject.class))
                    .collect(Collectors.toList());
            System.out.println("injectedFields = " + injectedFields.size());
            for (Field field : injectedFields) {
                final Object fieldInstance = beans.stream()
                        .filter(each -> field.getType().isInstance(each))
                        .findFirst()
                        .orElseThrow();
                field.setAccessible(true);
                field.set(bean, fieldInstance);
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("필드 대입 실패");
        }
    }

    public <T> T getBean(final Class<T> aClass) {
        return beans.stream()
                .filter(aClass::isInstance)
                .map(bean -> (T) bean)
                .findFirst()
                .orElse(null);
    }
}
