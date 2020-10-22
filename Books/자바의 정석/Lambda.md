# Lambda



#### 람다의 시작

- 람다는 자바 8부터 추가된 기능

- 람다를 사용하면 자연어에 더 가깝게 간단한 방식으로 코드를 구현할 수 있다.

  ```java
  // 람다를 사용하지 않은 코드
  Collections.sort(inventory, new Comparator<Apple>(){
      public int compare(Apple a1, Apple a2){
          return a1.getWeight(), compareTo(a2.getWeight());
      }
  });
  ```

  ```java
  inventory.sort(comparing(Apple::getWeight));
  ```

  위 코드는 사과의 무게를 비교해서 목록에서 정렬한다.

- CPU가 싱글코어에서 듀얼 혹은 쿼드 코어로 진화하면서 자바도 바뀌어야 할 필요성을 느꼈다.

  1. 스레드를 사용하면 되지만 스레드는 관리하기 어렵고 많은 문제를 발생할 수 있다는 단점
  2. 이런 병렬 실행 환경을 쉽게 관리하고 에러가 덜 발생하는 방향으로 진화하려고 노력
  3. 스레드를 숙련된 전문가가 아닌 개발자가 사용하기는 쉽지 않음



**그래서 탄생한 것이 자바 8! --> 나중에 Rxjava로 이어진다**



-  자바 프로그램에서 조작할 수 있는 값은 int, double 등의 기본값이 있고, 객체(엄밀히 따지면 객체의 참조값)도 값에 해당한다.  다양한 구조체들은 값의 구조를 표현하는데 도움이 된다. 하지만 프로그램이 실행하는 동안에 이런 구조체(메서드, 클래스 같은)를 자유롭게 전달 할 수 없다.(클래스를 통째로 런타임 도중 참조하라고 던져 줄 수 없다) 이런 점에 착안해서 자바 8 개발자들은 이런 기능을 추가했다(구조체를 통째로 값으로 넘길 수 있는...)
-  람다란 메서드로 전달할 수 있는 익명 함수를 단순화한 것이라고 할 수 있다. 람다는 이름은 없지만 파라미터 리스트, 바디, 반환 형식, 가능한 예외 리스트를 포함 한다.

- 익명 클래스는 인스턴스를 생성해야 하지만, 함수는 평가될 때마다 새로 생성되지 않습니다. 함수를 위한 메모리 할당은 자바 힙의 Perm 영역에 한 번 저장됩니다.

  **1. 자바 VM은 힙을 세개의 영역으로 나누어 사용한다.**

   1) New/Young 영역 : 새로 생성된 객체를 저장
   2) Old 영역 : 만들어진지 오래된 객체를 저장
   3) Permanent 영역 : JVM클래스와 메서드 객체를 저장

   Perm 영역은 보통 Class Meta 정보나 Method의 메타 정보, static 변수와 상수 정보들이 저장되는 공간으로 흔히 메타데이터 저장 영역이라고 한다. 이 영역은 JAVA8 부터 Native Memory 영역으로 이동하였다. (기존의 Perm영역에 존재하는 static object는 Heap 영역으로 옮겨졌다.)

  https://swiftymind.tistory.com/112 (자바 메모리 구조)

- 객체는 데이터와 밀접하게 연관해서 동작하지만, 함수는 데이터와 분리되어 있습니다. 상태를 보존하지 않기 때문에 연산을 여러 번 적용해도 결과가 달라지지 않습니다(멱등성).

- 클래스의 스태틱 메소드가 함수의 개념과 가장 유사합니다.



#### 람다식 작성하기

```java
int max (int a, int b){
    return a > b ? a : b;
} // 메서드의 이름과 반환타입을 제거하고 -> 을 블록 {} 앞에 추가한다.
```

```java
(int a, int b) -> a > b ? a : b // 반환값이 있을 경우 식이나 값만 적고 return 생략
```

```java
(a, b) -> a > b ? a : b  // 매개 변수의 타입이 추론 가능하면 생략가능
```

1.  매개 변수가 하나인 경우 괄호 생략 가능( 타입이 없을 때만 )
2. 블록 안에 문장이 하나뿐일 때, 괄호 생략 가능(끝에 ; 를 안 붙임) 단 하나뿐 인 문장이 return 문이면 괄호 생략 불가



```java
// 람다 문법
1. () -> {}
2. () -> "Raoul"
3. () -> {return "Mario";}
4. (Integer i) -> return "Alan" + i;
5. (String s) -> {"Iron Man";}
```

```java
// 1. 파라미터가 없으며 void를 반환하는 람다 표현식, 바디가 없는 메서드
// 2. 파라미터가 없으며 문자열을 반환하는 표현식이다.
// 3. 파라미터가 없으며 (명시적으로 return 문을 이용해서) 문자열을 반환하는 표현식이다.
// 4. return은 흐름 제어문이다. (Integer i) -> {return "Alan" + i;} 처럼 되어야 올바르다.
// 5. "Iron man"은 구문이 아니라 표현식이다. (String s) -> "Iron man" or (String s) -> {return "Iron man";} 처럼 바꿔야 한다.
```



### 문법 요약

```java
// 인자 -> 바디
(int x, int y) -> { return x + y; }

// 인자 타입 생략 - 컴파일러가 추론
(x, y) -> { return x + y; }

// return 및 중괄호 생략
(x, y) -> x + y

// 인자가 하나인 경우 인자 괄호 생략
x-> x * 2
    
// 인자가 없으면 빈 괄호로 표시
() -> System.out.println("Hey there!")

// 메소드 참조 Method reference
// (value -> System.out.println(value)) 의 축약형
System.out::println
```



**함수형 인터페이스**

기존 방식의 코드는 다음과 같습니다.

```java
// Thread
new Thread(new Runnable(){
    @Override
    public void run(){
        System.out.println("Hello World");
    }
}).start();
```

쓰레드를 돌리기 위해 Runnable 인터페이스를 새롭게 작성해서 매개변수로 넣었습니다.

다음을 람다식으로 바꾸면

```java
// Lambda
new Thread(() -> {System.out.println("Hello World");
                 }).start();
```



객체지향 언어인 자바에서 값이나 객체가 아닌 하나의 함수(Funtion)을 변수에 담아둔다는 것은 이해가 되지 않을 것입니다. 하지만 자바 8에서 람다식이 추가 되고 나서는 하나의 변수에 하나의 함수를 매핑할 수 있습니다.

실제로 다음과 같은 구문을 실행시키고자 한다면 어떻게 해야할까요?

```java
Func add = (int a, int b) -> a + b;
```

분명히 int형 매개 변수 a,b를 받아 그것을 합치는 것을 람다식으로 표현한것입니다. 그러면 Func는 무엇이어야 할까요?

답은 interface입니다. 위와 같은 람다식을 구현하려면 Func 인터페이스를 아래처럼 작성합니다.

```java
interface Func {
	public int calc(int a, int b);
}
```



이 인터페이스에서는 하나의 추상 메소드를 가지고 있습니다. 바로 calc라는 메소드입니다. 이 메소드는 int형 매개 변수 2개를 받아 하나의 int형 변수를 반환합니다. 아직 내부 구현은 어떻게 할지 정해지지 않았죠.



이 내부 구현을 람다식으로 만든것이 처음에 보셨던 코드입니다. 아래의 코드죠.

```java
Func add = (int a, int b) -> a + b;
```

여기까지는 진행에 무리가 없어보입니다. 그러면 혹시 Func 인터페이스에 메소드를 추가하게 되면 어떻게 될까요?

람다식으로 구현했던 add 함수 코드에서 오류가 납니다. **기본적으로 람다식을 위한 인터페이스에서 추상 메소드는 단 하나여야 합니다.** 하지만 이러한 사실을 알고 있다 하더라도 람다식으로 사용하는 인터페이스나 그냥 메소드가 하나뿐인 인터페이스나 구별을 하기 힘들뿐더러 혹시라도 누군가 람다식으로 사용하는 인터페이스에 메소드를 추가하더라도 해당 인터페이스에서는 오류가 나지 않습니다.

따라서 이 인터페이스는 람다식을 위한 것이다라는 표현을 위해 어노테이션 @FunctionalInterface을 사용합니다. 실제로 저 어노테이션을 선언하면 해당 인터페이스에 메소드를 두 개 이상 선언하면 유효하지 않다는 오류를 냅니다. 즉, 컴파일러 수준에서 오류를 확인 할 수 있습니다.

다음처럼 Func 인터페이스의 코드가 변경됩니다.

```java
@FunctionalInterface
interface Func {
	public int calc(int a, int b);
}
```



#### 예외

`@FunctionalInterface` 에는 하나의 메소드만 작성할 수 있다고 했는데, 여기에는 예외가 있습니다.

- `Object` 클래스의 메소드를 오버라이드하는 경우
- [디폴트 메소드](https://docs.oracle.com/javase/tutorial/java/IandI/defaultmethods.html)
- 스태틱 메소드

예를 들어 [Comparator](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html) 의 경우 `@FunctionalInterface` 인데 메소드가 많이 있습니다. 살펴보면 디폴트 메소드, 스태틱 메소드, Object 오버라이드한 메소드가 있고 추상 메소드의 경우는 [compare 메소드](https://docs.oracle.com/javase/8/docs/api/java/util/Comparator.html#compare-T-T-) 하나 뿐입니다



다음은 Comparator 인터페이스를 구현한 객체를 정렬 기준으로 사용하여 람다를 표현하겠습니다.

```java
public class CardComp implements Comparator<Card> {
    @Override
    public int compare(Card c1, Card c2){
        // 문자열 compareTo() - 사전식 비교
        return c1.getCardval().compareTo(c2.getCardVal());
    }
}
cards.sort(new CardComp());
```

1.  Comparator 인터페이스를 구현하면서 객체를 생성한다. new Comparator<Card>() {compare(Card c1, Card c2)) { }}. "new 인터페이스(){ 메서드 구현 ( ) }" 과 같이 인터페이스의 메서드를 구현하면서 생성하는 것은 가능하다.

```java
Comparator<Card> cmp = new Comparator<Card>() {
    @Override
    public int compare (Card c1, Card c2) {
        return c1.getCardVal().compareTo(c2.getCardVal());
    }
};
cards.sort(cmp);
```

2.  new Comparator<Card>() {compare(Card c1, Card c2) { }} 를 레퍼런스 대신 대입한다. 레퍼런스 없는 익명 클래스를 만든다.

```java
cards.sort(new Comparator<Card>(){
    @Override
    public int compare(Card c1, Card c2){
        return c1.getCardVal().compareTo(c2.getCardVal());
    }
});
```

3.  해당 클래스가 메서드를 한개만 갖고 있다면 함수 형식( 클래스와 메서드, 식별자 없이 사용) 을 이용하여 어떤 객체의 어떤 메서드가 호출되는지 알 수 있다. 이 방법을 이용하는 것이 람다 표현식이다. 클래스와 메서드를 삭제하고 메서드의 () 이후 부분을 남기면 ( ) { } 이 된다. ( )와 { } 사이에 클래스, 메서드가 삭제된 것을 표시하기 위해서 -> 을 넣으면 ( ) -> { } 가 완성된다.

```java
cards.sort( 
    (Card c1, Card c2) -> {return c1.getCardVal().compareTo(c2.getCardVal());}
);
```

4. 람다 표현에서 타입이 없어도 알 수 있으므로 타입을 제거할 수 있다.

```java
cards.sort((c1, c2) -> {return c1.getCardVal().compareTo(c2.getCardVal());});
```



#### java.util.function 패키지

자바의 함수형 인터페이스는 하나의 추상 메서드를 지정한 인터페이스다.

몇가지만 살펴보면

```java
public interface Predicate<T> {
    boolean test (T t);
} // 조건식을 표현하는데 사용, 매개변수는 하나, 반환타입은 boolean
```

```java
public interface Comparator<T> {
    int compare(T o1, T o2);
} // java.util.Comparator
```

```java
public interface Runnable {
    void run();
} // java.lang.Runnable
```

```java
public interface ActionListener extends EventListener {
    void actionPerformed(ActionEvent e);
} // java.awt.event.ActionListener
```

```java
public interface Consumer<T> {
    void accept(T t);
} // 
```

![image-20200725224908469](C:\Users\ocean\AppData\Roaming\Typora\typora-user-images\image-20200725224908469.png)

![image-20200725224946509](C:\Users\ocean\AppData\Roaming\Typora\typora-user-images\image-20200725224946509.png)





1.  **Predicate**

java.util.function.Predicate<T> 인터페이스는 test라는 추상메서드를 정의하며 test는 제네릭 형식 T의 객체를 인수로 받아 boolean을 반환한다. 우리가 만들었던 인터페이스와 같은 형태인데 따로 정의할 필요 없이 바로 사용 할 수 있다는 점이 특징이다. T 형식의 객체를 사용하는 boolean 표현식이 필요한 상황에서 Predicate 인터페이스를 사용할 수 있다. 다음 예제처럼 String 객체를 인수로 받는 람다를 정의 할 수 있다.

```java
@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);
}

public <T> List<T> filter(List<T> list, Predicate<T> p){
    List<T> results = new ArrayList<>();
    for (T t: list){
        if (p.test(t)){
            results.add(t);
        }
    }
    return results;
} 
Predicate<String> nonEmptyStringPredicate = (String s) -> !s.isEmpty();
List<String> nonEmpty = filter(listOfStrings, nonEmptyStringPredicate);
```

Predicate 인터페이스의 자바독 명세를 보면 and나 or같은 메서드도 있음을 알 수 있다.



2. **Consumer**

java.util.function.Consumer<T> 인터페이스는 제네릭 형식 T 객체를 받아서 void를 반환하는 accept라는 추상메서드를 정의한다. T 형식의 객체를 인수로 받아서 어떤 동작을 수행하고 싶을 때 Consumer 인터페이스를 사용할 수 있다. 예를 들면 Integer 리스트를 인수로 받아서 각 항목에 어떤 동작을 수행하는 forEach 메서드를 정의할 때 Consumer를 활용할 수 있다. 다음은 forEach와 람다를 이용해서 리스트의 모든 항목을 출력하는 예제이다.

```java
@FunctionalInterface
public interface Consumer<T>{
    void accept(T t);
}

public <T> void forEach(List<T> list, Consumer<T> c){
    for(T t: list) {
        c.accept(t);
    }
}
forEach(
    Arrays.asList(1, 2, 3, 4, 5),
    (Integer i) -> System.out.println(i)
); // Consumer 의 accept 메서드를 구현하는 람다
```



3. **Function**

java.util.function.Function<T, R> 인터페이스는 제네릭 형식 T를 인수로 받아서 제네릭 형식 R 객체를 반환하는 추상 메서드 apply를 정의한다. 입력을 출력으로 매핑하는 람다를 정의할 때 Function 인터페이스를 활용 할 수 있다. (예를 들면 사과의 무게 정보를 추출하거나 문자열을 길이와 매핑), 다음은 String 리스트를 인수로 받아 각 String의 길이를 포함하는 Integer 리스트로 변환하는 map 메서드를 정의하는 예제다.

```java
@FunctionalInterface
public interface Function<T, R>{
    R apply(T t);
}

public <T, R> List<R> map(List<T> list, Function<T, R> f) {
    List<R> result = new ArrayList<>();
    for(T t: list) {
        result.add(f.apply(t));
    }
    return result;
}

// [7, 2, 6]
List<Integer> l = map(
	Array.asList("lambdas", "in", "action"),
    (String s) -> s.length() // Functhon의 apply 메서드를 구현하는 람다
)
```



- 자바의 모든 형식은 참조형(ex Byte, Integer, Object) 아니면 기본형( int double, byte, char)에 해당한다. 하지만 제네릭 파라미터( 예를 들면 Consumer<T>)는 참조형만 사용할 수 있다. 자바에서는 기본형을 참조형으로 변환하는 기능을 제공한다. 이 기능을 박싱이라고 한다. 참조형을 기본형으로 변환하는 반대 동작을 언박싱이라고 한다.

- 하지만 이런 변환 과정은 비용이 소모된다. 자바 8 에서는 기본형을 입출력으로 사용하는 상황에서 오토박싱 동작을 피할 수 있도록 특별한 버전의 함수형 인터페이스를 제공한다. 예를 들어 아래 예제에서 IntPredicate는 1000이라는 값을 박싱하지 않지만, Predicate<Integer> 는 1000이라는 값을 Integer 객체로 박싱한다.

```java
public interface IntPredicate {
    boolean test(int t);
}

IntPredicate evenNumbers = (int i) -> i % 2 == 0;
evenNumbers.test(1000); // 참(박싱 없음)

Predicate<Integer> oddnumbers = (Integer i) -> i % 2 != 0;
oddNumbers.test(1000); // 거짓(박싱)
```

일반적으로 특정 형식을 입력으로 받는 함수형 인터페이스의 이름앞에는 DoublePredicate, IntConsumer처럼 형식명이 붙는다. 



<img src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fyewkl%2FbtqFZ4QCXVL%2Fu9zSr3UGUjPHgkxnSK6tAk%2Fimg.jpg" />

#### 메서드 참조



메서드 참조를 이용하면 기존의 메서드 정의를 재활용해서 람다처럼 전달할 수 있다. 때로는 람다 표현식보다 메서드 참조를 사용하는 것이 더 가독성이 좋으며 자연스러울 수 있다..

- 메서드 참조는 특정 메서드만을 호출하는 람다의 축약형이라고 생각할 수 있다. 예를 들어 람다가 '이 메서드를 직접 호출해' 라고 명령한다면 메서드를 어떻게 호출해야 하는지 설명을 참조하기 보다는 메서드명을 직접 참조하는 것이 편리하다. 실제로 메서드 참조를 이용하면 기존 메서드 구현으로 람다 표현식을 만들 수 있다. 이따 명시적으로 메서드명을 참조함으로써 가독성을 높일 수 있다. 

- 메서드 참조는 메서드명 앞에 구분자( :: )를 붙이는 방식으로 메서드 참조를 활용할 수 있다. 예를 들어 

  ```java
  Apple::getWeight
  ```

  위 코드는 Apple 클래스에 정의된 getWeight의 메서드 참조이다. 결과적으로 메서드 참조는 람다 표현식 (Apple a) -> a.getWeight()를 축약한 것이다.

  