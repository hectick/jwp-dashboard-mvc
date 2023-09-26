package di.stage3.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * 스프링의 BeanFactory, ApplicationContext에 해당되는 클래스
 */
class DIContainer {

    private final Set<Object> beans;

    public DIContainer(final Set<Class<?>> classes) {
        this.beans = createBeans(classes);
        this.beans.forEach(this::setFields);
    }

    // 기본 생성자로 빈을 생성한다.
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
            for (Field field : fields) {
                final Object fieldInstance = beans.stream()
                        .filter(each -> field.getType().isInstance(each))
                        .findFirst()
                        .orElse(null);
                if(fieldInstance != null) {
                    field.setAccessible(true);
                    field.set(bean, fieldInstance);
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
            throw new IllegalStateException("필드 대입 실패");
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getBean(final Class<T> aClass) {
        return beans.stream()
                .filter(aClass::isInstance)
                .map(bean -> (T) bean)
                .findFirst()
                .orElse(null);
    }
}
