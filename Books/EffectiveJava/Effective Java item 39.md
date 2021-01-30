# Effective Java item 39



### 명명 패턴보다 애너테이션을 사용하라



전통적으로 도구나 프레임워크가 특별히 다뤄야 할 프로그램 요소에는 딱 구분되는 명명 패턴을 적용해왔다.

명명 패턴의 단점은 명확하다.

1. 오타가 나면 안된다.
2. 올바른프로그램 요소에서만 사용되리라 보증할 방법이 없다.
3. 프로그램 요소를 매개변수로 전달할 마땅한 방법이 없다는 점이다.



⭐애너테이션은 이 모든 문제를 해결해 주는 멋진 개념으로, JUnit도 버전 4부터 전면 도입되었다.

```java
// 마커 애너테이션 타입 선언
// 매개 변수 없는 정적 메서드 전용
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {    
}
```

보다시피 @Test 애너테이션 타입 선언 자체에도 두 가지의 다른 애너테이션이 달려있다.

바로 @Retention과 @Target이다. 이처럼 애너테이션 선언에 다는 애너테이션을 메타애너테이션이라 합니다.

@Retention 매타애너테이션은 @Test가 런타임에도 유지되어야 한다는 표시다. 만약 이 메타애너테이션을 생략하면 테스트 도구는 @Test를 인식할 수 없다.

한편 @Target(ElementType.METHOD) 메타애너테이션은 @Test가 반드시 메서드 선언에서만 사용돼야 한다고 알려준다. 따라서 클래스 선언, 필드 선언 등 다른프로그램 요소에는 달 수 없다.

:notebook:  앞선 코드에서는 '매개변수 없는 정적 메서드 전용이다'라고 쓰여있다. 이 제약을 컴파일러가 강제할 수 있으면 좋겠지만, 그렇게 하려면 적절한 애너테이션 처리기를 직접 구현해야 한다. 적절한 애너테이션 처리기 없이 인스턴스 메서드나 매개변수가 있는 메서드에 달면 어떻게 될까? 컴파일은 잘 되겠지만, 테스트 도구를 실행할 때 문제가 된다.



- 다음 코드는 @Test 애너테이션을 실제 적용한 모습이다. 이와 같은 애너테이션을 "아무 매개변수 없이 단순히 대상에 마킹한다"는 뜻에서 마커 애너테이션이라 한다.

```java
// 마커 애너테이션을 사용한 프로그램 예

public class Sample {
    
    @Test public static void m1() { } // 성공해야 한다.
    public static void m2() { }
    @Test public static void m3() { // 실패해야 한다.
        throw new RuntimeException("실패");
    }
    public static void m4() { }
    @Test public void m5() { } // 잘못 사용한 예: 정적 메서드가 아니다.
    public static void m6() { }
    @Test public static void m7() {
        throw new RuntimeException("실패");
    }
    public static void m8() { }
}
```

Sample 클래스에는 정적 메서드가 7개고, 그중 4개에 @Test를 달았다. m3와 m7메서드는 예외를 던지고 m1과 m5는 그렇지 않다. 그리고 m5는 인스턴스 메서드이므로 @Test를 잘못 사용한 경우다. 요약하면 총 4개의 테스트 메서드 중 1개는 성공, 2개는 시패, 1개는 잘못 사용했다. 그리고 @Test를 붙이지 않은 나머지 4개의 메서드는 테스트 도구가 무시할 것이다.



- @Test 애너테이션이 Sample 클래스의 의미에 직접적인 영향을 주지는 않는다. 그저 이 애너테이션에 관심 있는 프로그램에 추가 정보를 제공할 뿐이다. 더 넓게 이야기하면, 대상 코드의 의미는 그대로 둔 채 그 애너테이션에 관심 있는 도구에서 특별한 처리를 할 기회를 준다.
- 만약 @Test 애너테이션이 매개변수가 있는 메서드, 호출할 수 없는 메서드, 인스턴스 메서드 등에 달았다면 예외가 발생할 것이다.



:notebook_with_decorative_cover: 이제 특정 예외를 던져야만 성공하는 테스트를 지원하도록 해보자. 그러려면 새로운 애너테이션 타입이 필요하다.

```java
// 매개변수 하나를 받는 애너테이션 타입
// 명시한 예외를 던져야만 성공하는 테스트 메서드용 애너테이션

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExcetionTest {
    Class<? extends Throwable> value();
}
```

이 애너테이션의 매개변수 타입은 Class<? extends Throwable>이다. 여기서의 와일드카드 타입은 많은 의미를 담고 있다. "Throwable을 확장한 클래스의 Class 객체"라는 뜻이며, 따라서 모든 예외 타입을 다 수용한다.

다음은 이 애너테이션을 실제 활용하는 모습이다. class 리터럴은 애너테이션 매개변수의 값으로 사용됐다.

```java
// 매개변수 하나짜리 애너테이션을 사용한 프로그램

public class Sample2 {
    @ExceptionTest(ArithmeticException.class)
    public static void m1() {
        // 성공해야 한다.
        int i = 0;
        i = i / i;
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m2() { // 실패해야 한다. (다른예외 발생)
    	int[] a = new int[0];
        int i = a[1];
    }
    @ExceptionTest(ArithmeticException.class)
    public static void m3() { } // 실패해야한다. (예외가 발생하지 않음)
}
```



- 여기서 더 나아가 예외를 여러 개 명시하고 그중 하나가 발생하면 성공하게 만들 수도 있다. 애너테이션 매커니즘에는 이런 쓰임에 아주 유용한 기능이 기본으로 들어 있다. @ExceptionTest 애너테이션의 매개변수 타입을 Class 객체의 배열로 수정해보자.

```java
@Rention(RetentionPolicy.RUNTIME)
@Target(ElementsType.METHOD)
public @interface ExceptionTest {
    Class<? extends Throwable>[] value();
}
```

배열 매개변수를 받는 애너테이션용 문접은 아주 유연하다. 단일 원소 배열에 최적화했지만, 앞서의 @ExceptionTest들도 모두 수정없이 수용한다. 원소가 여럿인 배열을 지정할 때는 다음과 같이 원소들을 중괄호로 감싸고 쉼표로 구분해주기만 하면 된다.

```java
// 배열 매개변수를 받는 애너테이션을 사용하는 코드
@ExceptionTest({ IndexOutOfBoundsException.class,
               	 NullPointerException.class})
public static void doublyBad() {
    List<String> list = new ArrayList<>();
    
    // 자바 API 명세에 따르면 다음 메서드는 IndexOutOfBoundsException이나
    // NullPointerException을 던질 수 있다.
    list.addAll(5, null);
}
```



⚡ 자바 8에서는 여러개의 값을 받는 애너테이션을 다른 방식으로도 만들 수 있다. 배열 매개변수를 사용하는 대신 애너테이션에 @Repeatable 메타애너테이션을 다는 방식이다. @Repeatable을 단 애너테이션은 하나의 프로그램 요소에 여러 번 달 수 있다. 단 주의할 점이 있다.

1. @Repeatable을 단 애너테이션을 반환하는 '컨테이너 애너테이션'을 하나 더 정의하고, @Repeatable에 이 컨테이너 애너테이션의 class 객체를 매개변수로 전달해야 한다.
2. 컨테이너 애너테이션은 내부 애너테이션 타입의 배열을 반환하는 value 메서드를 정의해야 한다.
3. 컨테이너 애너테이션 타입에는 적절한 보존 정책과 적용 대상을 명시해야 한다. 그렇지 않으면 컴파일되지 않을 것이다.



```java
// 반복 가능한 애너테이션 타입
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Repeatable(ExceptionTestContainer.class)
public @interface ExceptionTest {
    Class<? extends Throwable> value();
}

// 컨테이너 애너테이션
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExceptionTestContainer {
    ExceptionTest[] value();
}
```

이제 앞서의 배열 방식 대신 반복 가능 애너테이션을 적용해보자.

```java
// 반복 가능 애너테이션을 두번 단 코드
@ExceptionTest(IndexOutOfBoundsException.class)
@ExceptionTest(NullPointerException.class)
public static void doublyBad() { ... }
```

반복 가능 애너테이션은 처리할 때도 주의를 요한다. 반복 가능 애너테이션을 여러 개 달면 하나만 달았을 때와 구분하기 위해 해당 '컨테이너' 애너테이션 타입이 적용된다.

- getAnnotationsByType 메서드는 이 둘을 구분하지 않아서 반복 가능 애너테이션과 그 컨테이너 애너테이션을 모두 가져오지만 isAnnotationPresent 메서드는 둘을 명확히 구분한다. 따라서 반복 가능 애너테이션을 여러 번 단 다음 isAnnotationPresent로 반복 가능 애너테이션이 달렸는지 검사한다면 "그렇지 않다."라고 알려준다.
- isAnnotationPresent로 컨테이너 애너테이션이 달렸는지 검사한다면 반복 가능 애너테이션을 한 번만 단 메서드를 무시하고 지나친다. 그래서 달려 있는 수와 상관없이 모두 검사하려면 둘을 따로따로 확인해야 한다.



⭐ 애너테이션으로 할 수 있는 일을 명명패턴으로 처리할 이유는 없다. 도구 제작자를 제외하고는 일반 프로그래머가 애너테이션 타입을 직접 정의할 일은 거의 없다. 하지만 **자바 프로그래머라면 예외 없이 자바가 제공하는 애너테이션 타입들은 사용해야 한다.**

