# Effective Java item 33



### 타입 안전 이종 컨테이너를 고려하라



제네릭은 Set<E>, Map<K, V> 등의 컬렉션과 ThreadLocal<T>, AtomicReference<T> 등의 단일원소 컨테이너에도 흔히 쓰인다. 이런 모든 쓰임에서 매개변수화되는 대상은 컨테이너 자신이다. 따라서 하나의 컨테이너에서 매개변수화할 수 있는 타입의 수가 제한된다.

- 컨테이너의 일반적인 용도에 맞게 설계된 것이니 문제될 건 없다. 예컨대 Set에는 원소의 타입을 뜻하는 단 하나의 타입 매개변수만 있으면 되며, Map에는 키와 값의 타입을 뜻하는 2개만 필요한 식이다.
- 하지만 더 유연한 수단이 필요할 때도 종종 있다.  예컨대 데이터베이스의 행은 임의 개수의 열을 가질 수 있는데, 모두 열을 타입 안전하게 이용할 수 있다면 멋질 것이다. 다행히 쉬운 해법이 있다. 컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하면 된다.



```java
// 타입 안전 이종 컨테이너 패턴 - API
public class Favorites {
    public <T> void putFavorite(Class<T> type, T instance);
    public <T> T getFavorite(Class<T> type);
}
```

그리고 다음은 앞의 Favorites 클래스를 사용하는 예시다. 즐겨 찾는 String, Integer, Class 인스턴스를 저장, 검색, 출력하고 있다.



```java
// 타입 안전 이종 컨테이너 패턴 - 클라이언트
public static void main(String[] args) {
    Favorites f = new Favorites();
    
    f.putFavorite(String.class, "Java");
    f.putFavorite(Integer.class, 0xcafebabe);
    f.putFavorite(Class.class, Favorites.class);
    
    String favoriteString = f.getFavorite(String.class);
    int favoriteInteger = f.getFavorite(Integer.class);
    Class<?> favoriteClass = f.getFavorite(Class.class);
    
    System.out.printf("%s %x %s%n", favoriteString,
                     favoriteInteger, favoriteClass.getName());
}
```

기대한 대로 이 프로그램은 Java cafebabe Favorites를 출력한다.



> 자바의 printf가 C의 printf와 다른점이 하나 있다. 이 코드에서는 만약 C였다면 \n을 썼을 곳에 %n을 썼는데, 이 %n은 플랫폼에 맞는 줄바꿈 문자로 자동으로 대체된다(대부분 플랫폼에서 \n이 되겠지만, 모든 플랫폼이 그렇지는 않다.)



```java
// 타입 안전 이종 컨테이너 패턴 - 구현
public class Favorites {
    private Map<Class<?>, Object> favorites = new HashMap<>();
    
    public <T> void putFavorite(Class<T> type, T instance) {
        favorites.put(Objects.requireNonNull(type), instance);
    }
    
    public <T> T getFavorite(Class<T> type) {
        return type.cast(favorites.get(type));
    }
}
```

getFavorite 구현은 Class의 cast 메서드를 사용해 이 객체 참조를 Class 객체가 가리키는 타입으로 동적 형변환한다.

- cast 메서드는 형변환 연산자의 동적 버전이다. 이 메서드는 단순히 주어진 인수가 Class 객체가 알려주는 타입의 인스턴스인지를 검사한 다음, 맞다면 그 인수를 반환하고, 아니면 ClassCastException을 던진다. 클라이언트 코드가 깔끔히 컴파일 된다면 getFavorite이 호출하는 cast는 ClassCastException을 던지지 않을 것임을 우리는 알고있다. 다시 말해 favorites 맵 안의 값은 해당 키의 타입과 항상 일치함을 알고 있다.
- 그런데 cast 메서드가 단지 인수를 그대로 반환하기만 한다면 굳이 왜 사용하는 것일까? 그 이유는 cast 메서드의 시그니처가 Class 클래스가 제네릭이라는 이점을 완벽히 활용하기 때문이다. 다음 코드에서 보듯 cast의 반환타입은 Class 객체의 타입 매개변수와 같다.

```java
public class Class<T> {
    T cast(Object obj);
}
```

이것이 정확히 getFavorite 메서드에 필요한 기능으로 T로 비검사 형변환하는 손실 없이도 Favorites를 타입 안전하게 만드는 비결이다.



⭐지금의 favorites 클래스에는 알아두어야 할 제약이 두가지 있다.

1. 첫 번째, 악의적인 클라이언트가 Class 객체를 (제네릭이 아닌) 로 타입(아이템 26)으로 넘기면 Favorites 인스턴스의 타입 안전성이 쉽게 깨진다. 하지만 이렇게 짜여진 클라이언트 코드에서는 컴파일할 때 비검사 경고가 뜰 것이다. Favorites가 타입 불변식을 어기는 일이 없도록 보장하려면 putFaorite 메서드에서 인수로 주어진 instance의 타입이 type으로 명시한 타입과 같은지 확인하면 된다. 그 방법은 이미 알고 있듯, 다음 코드와 같이 그냥 동적 형변환을 쓰면 된다.

   ```java
   // 동적 형변환으로 런타임 타입 안전성 확보
   public <T> void putFavorite(Class<T> type, T instance) {
       favorites.put(Objcets.requireNonNull(type), type.cast(instance));
   }
   ```

   java.util.Collections에는 checkedSet, checkedList, checkedMap 같은 메서드가 있는데, 바로 이 방식을 적용한 컬렉션 래퍼들이다. 이 정적 팩터리들은 컬렉션과 함께 1개(혹은 2개)의 Class 객체를 받는다. 이 메서드들은 모두 제네릭이라 Class 객체와 컬렉션의 컴파일타임 타입이 같음을 보장한다.

2. Favorites 클래스의 두 번째 제약은 실체화 불가 타입에는 사용할 수 없다는 것이다. 다시 말해, 즐겨 찾는 String이나 String[]은 저장할 수 있어도 즐겨 찾는 List<String>은 저장할 수 없다. List<String>을 저장하려는 코드는 컴파일 되지 않을 것이다. List<String>용 Class 객체를 얻을 수 없기 때문이다. List<String>.class라고 쓰면 문법 오류가 난다. **이 두 번째 제약에 대한 완벽히 만족스러운 우회로는 없다.**

   > 이 두 번째 제약을 슈퍼 타입 토큰으로 해결하려는 시도도 있다. 슈퍼 타입 토큰은 자바 업계의 거장인 닐 개프터가 고안한 방식으로, 실제로 아주 유용하여 스프링 프레임워크에서는 아예 ParameterizedTypeReference라는 클래스로 미리 구현해 놓았다.
   >
   > Favorites f = new Favorites();
   >
   > List<String> pets = Arrays.asList("개", "고양이", "앵무");
   >
   > f.putFavorite(new TypeRef<List<String>>() {}, pets);
   >
   > List<String> listofStrings = f.getFavorite(new TypeRef<List<String>> () {});
   >
   > 하지만 이 슈퍼토큰 방식도 완벽하지는 않다.

   

   Favorites가 사용하는 타입 토큰은 비한정적이다. 즉 getFavorite과 putFavorite은 어떤 Class 객체든 받아들인다. 때로는 이 메서드들을 허용하는 타입을 제한하고 싶을 수도 있는데, 한정적 타입 토큰을 활용하면 가능하다.

   

   🚩 애너테이션 API는 한정적 타입 토큰을 적극적으로 사용한다.예를 들어 다음은 AnnotatedElement 인터페이스에 선언된 메서드로, 대상 요소에 달려 있는 애너테이션을 런타임에 읽어 오는 기능을 한다. 이 메서드는 리플렉션의 대상이 되는 타입들, 즉 클래스, 메서드, 필드 같이 프로그램 요소를 표현하는 타입들에서 구현한다.

   

   ```java
   public <T extends Annotation>
       T getAnnotation(Class<T> annotationType);
   ```

   여기서 annotationType 인수는 애너테이션 타입을 뜻하는 한정적 타입 토큰이다. 이 메서드는 토큰으로 명시한 타입의 애너테이션이 대상 요소에 달려 있다면 그 애너테이션을 반환하고, 없다면 null을 반환한다. 즉, 애너테이션된 요소는 그 키가 애너테이션 타입인, 타입 안전 이종 컨테이너인 것이다.

   

   Class<?> 타입의 객체가 있고, 이를 한정적 타입 토큰을 받는 메서드에 넘기려면 어떻게 해야 할까?

   운 좋게도 Class 클래스가 이런 형변환을 안전하게 수행해주는 인스턴스 메서드를 제공한다. 바로 asSubclass메서드로, 호출된 인스턴스 자신의 Class 객체를 인수가 명시한 클래스로 형변환한다. 형변환에 성공하면 인수로 받은 클래스 객체를 반환하고, 실패하면 ClassCastException을 던진다.

   

   다음은 컴파일 시점에 타입을 알 수 없는 애너테이션을 asSubclass 메서드를 사용해 런타임에 읽어내는 예다. 이 메서드는 오류나 경고 없이 컴파일 된다.

   ```java
   // asSubclass를 사용해 한정적 타입 토큰을 안전하게 형변환한다.
   static Annotation getAnnotation(AnnotatedElement element,
                                  String annotationTypeName) {
       Class<?> annotationType = null; // 비 한정적 타입 토큰
       try {
           annotationType = Class.forName(annotationTypeName);
       } catch (Exception ex) {
           throw new IllegalArgmentException(ex);
       }
       return element.getAnnotation(
       annotationType.asSubclass(Annotation.class));
   }
   ```



> 핵심정리
>
> 컬렉션 API로 대표되는 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수의 수가 고정되어 있다. 하지만 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 이런 제약이 없는 타입 안전 이종 컨테이너를 만들 수 있다. 타입 안전 이종 컨테이너는 Class를 키로 쓰며, 이런 식으로 쓰이는 Class 객체를 타입 토큰이라 한다. 또한, 직접 구현한 키 타입도 쓸 수 있다. 예컨대 데이터베이스의 행을 표현한 DatabaseRow 타입에는 제네릭 타입인 Column<T>를 키로 사용할 수 있다.