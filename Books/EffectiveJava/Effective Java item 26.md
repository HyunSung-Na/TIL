# Effective Java item 26



### 로 타입은 사용하지 말라



클래스와 인터페이스 선언에 타입 매개변수가 쓰이면, 이를 **제네릭 클래스** 혹은 **제네릭 인터페이스**라 한다.

예컨대 List 인터페이스는 원소의 타입을 나타내는 타입 매개변수 E를 받는다. 그래서 이 인터페이스의 완전한 이름은 List<E>지만, 짧게 그냥 List라고도 자주 쓴다. 제네릭 클래스와 제네릭 인터페이스를 통틀어 **제네릭 타입**이라 한다.



- 각각의 제네릭 타입은 일련의 **매개변수화 타입**을 정의한다. 먼저 클래스 이름이 나오고, 이어서 괄호 안에 실제 타입 변수들을 나열한다.
- 제네릭 타입을 하나 정의하면 그에 딸린 **로 타입**도 함께 정의된다. 로 타입이란 제네릭 타입에서 타입 매개변수를 전혀 사용하지 않을 때를 말한다. 예컨대 List<E>의 로 타입은 List다. 로 타입은 타입 선언에서 제네릭 타입 정보가 전부 지워진 것처럼 동작하는데, 제네릭이 도래하기 전 코드와 호환되도록 하기 위한 궁여지책이라 할 수 있다.



:notebook_with_decorative_cover: 제네릭을 지원하기 전에는 컬렉션을 다음과 같이 선언했다. 자바 9에서도 여전히 동작하지만 좋은 예라고 볼 순 	없다.

```java
// Stamp 인스턴스만 취급한다.
private final Collection stamps = ...;
```

이 코드를 사용하면 실수로 도장 대신 동전을 넣어도 아무 오류없이 컴파일되고 실행된다(컴파일러가 모호한 경고 메시지를 보여주긴 할 것이다.)



```java
// 실수로 동전을 넣는다.
stamps.add(new Coin(...)); // "unchecked call" 경고를 내뱉는다.
```

컬렉션에서 이 동전을 다시 꺼내기 전에는 오류를 알아채지 못한다.



```java
// 반복자의 로 타입 - 따라 하지 말 것!
for (Iterator i = stamps.iterator(); i.hasNext();) {
    Stamp stamp = (Stamp) i.next(); // ClassCastException을 던진다.
    stamp.cancel();
}
```



- 제네릭을 활용하면 이 정보가 주석이 아닌 타입 선언 자체에 녹아든다.

```java
// 매개변수화된 컬렉션 타입 - 타입 안정성 확보!
private final Collection<Stamp> stamps = ...;
```

컴파일러는 컬렉션에서 원소를 꺼내는 모든 곳에 보이지 않는 형변환을 추가하여 절대 실패하지 않음을 보장한다.



:notebook_with_decorative_cover: 로 타입을 쓰는 걸 언어 차원에서 막아 놓지는 않았지만 절대로 써서는 안 된다. **로 타입을 쓰면 제네릭이 안	겨주는 안전성과 표현력을 모두 잃게 된다.**

- 그렇다면 절대 써서는 안 되는 로 타입을 애초에 왜 만들어놓은 걸까? 바로 호환성 때문이다. 기존 코드를 수용하면서 제네릭을 사용하는 새로운 코드와도 맞물려 돌아가게 해야만 했다.



**List 같은 로 타입을 사용하면 타입 안정성을 잃게 된다.**



```java
// 런타임에 실패한다 - unsafeAdd 메서드가 로 타입을 사용
public static void main(String[] args) {
    List<String> strings = new ArrayList<>();
    unsafeAdd(strings, Integer.valueOf(42));
    String s = strings.get(0); // 컴파일러가 자동으로 형변환 코드를 넣어준다.
}

private static void unsafeAdd(List list, Object o) {
    list.add(o);
}
```

이 코드는 컴파일은 되지만 로 타입인 List를 사용하여 다음과 같은 경고가 발생한다.

```java
Test.java:10: warning: [unchecked] unchecked call to add(E) as a
member of the raw type List
    list.add(0);
```

이 프로그램을 이대로 실행하면 strings.get(0)의 결과를 형변환하려 할 때 ClassCastException을 던진다. Integer를 String으로 변환하려 시도한 것이다.



- 로 타입을 사용하지 말고 비한정적 와일드카드 타입을 대신 사용하는 게 좋다. 제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경 쓰고 싶지 않다면 물음표(?)를 사용하자. 예컨대 제네릭 타입인 Set<E>의 비한정적 와일드카드 타입은 Set<?>다. 이것이 어떤 타입이라도 담을 수 있는 가장 범용적인 매개변수화 Set타입이다.

```java
// 비한정적 와일드카드 타입을 사용하라. -타입 안전하며 유연하다.

static int numElementsInCommon(Set<?> s1, Set<?> s2) { ,,, }
```

- 로 타입 컬렉션에는 아무 원소나 넣을 수 있으니 타입 불변식을 훼손하기 쉽다. 반면, **Collection<?>에는 (null 외에는) 어떤 원소도 넣을 수 없다.** 다른원소를 넣으려 하면 컴파일할 때 다음의 오류 메시지를 보게 될 것이다.



:notebook_with_decorative_cover: 로 타입을 쓰지 말라는 규칙에도 소소한 예외가 몇 개 있다. **class 리터럴에는 로 타입을 써야 한다.** 자바 명세는 	class 리터럴에 매개변수화 타입을 사용하지 못하게 했다(배열과 기본 타입은 허용한다.)

- 예를 들어 List.class, String[].class, int.class는 허용하고 List<String>.class와 List<?>.class는 허용하지 않는다.



:notebook_with_decorative_cover: 두 번째 예외는 instanceof 연산자와 관련이 있다. 런타임에는 제네릭 타입 정보가 지워지므로 instanceof 연산	자는 비한정적 와일드카드 타입 이외의 매개변수화 타입에는 적용할 수 없다. 그리고 로 타입이든 비한정적 와일	드카드타입이든 instanceof는 완전히 똑같이 동작한다. 비한정적 와일드카드 타입의 꺾쇠괄호와 물음표는 아무	런 역할 없이 코드만 지저분하게 만드므로, 차라리 로 타입을 쓰는 편이 깔끔하다. **다음은 제네릭 타입에       	instanceof를 사용하는 올바른 예다.**

```java
// 로 타입을 써도 좋은 예 - instanceof 연산자

if (o instanceof Set) {  // 로 타입
    Set<?> s = (Set<?>) o; // 와일드카드 타입
    ...
}
```

- o의 타입이 Set임을 확인한 다음 와일드카드 타입인 Set<?>로 형변환해야 한다(로 타입인 Set이 아니다). 이는 검사 형변환이므로 컴파일러 경고가 뜨지 않는다.



> 핵심 정리
>
> 로 타입을 사용하면 런타임에 예외가 일어날 수 있으니 사용하면 안 된다. 로 타입은 제네릭이 도입되기 이전 코드와의 호환성을 위해 제공될 뿐이다. 빠르게 훑어보자면, Set<Object>는 어떤 타입의 객체도 저장할 수 있는 매개변수화 타입이고, Set<?>는 모종의 타입 객체만 저장할 수 있는 와일드카드 타입이다. 그리고 이들의 로 타입은 Set은 제네릭 타입 시스템에 속하지 않는다. Set<Object>와 Set<?>는 안전하지만, 로 타입인 Set은 안전하지 않다.
