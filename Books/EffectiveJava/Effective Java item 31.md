# Effective Java item 31



### 한정적 와일드카드를 사용해 API 유연성을 높이라



아이템 28에서 이야기했듯 매개변수화 타입은 불공변이다. 즉 서로 다른 타입 Type1과 Type2가 있을 때 List<Type1>은 List<Type2>의 하위 타입도 상위 타입도 아니다. 직관적이지 않겠지만 List<String>은 List<Object>의 하위 타입이 아니라는 뜻인데, 곰곰이 따져보면 사실 이쪽은 말이 된다.

List<Object>에는 어떤 객체든 넣을 수 있지만 List<String>에는 문자열만 넣을 수 있다. 즉, List<String>은 List<Object>가 하는 일을 제대로 수행하지 못하니 하위 타입이 될 수 없다.(리스코프 치환 원칙에 어긋난다.)



- 하지만 때론 불공변 방식보다 유연한 무언가가 필요하다. 아이템 29의 Stack 클래스를 떠올려보자. 여기 Stack의 public API를 추려보았다.



```java
public class Stack<E> {
    public Stack();
    public void push(E e);
    public E pop();
    public boolean isEmpty();
}
```

여기에 일련의 원소를 스택에 넣는 메서드를 추가해야 한다고 해보자.

```java
// 와일드카드 타입을 사용하지 않을 pushAll 메서드 - 결함이 있다.
public void pushAll(Iterable<E> src) {
    for (E e : src)
        push(e);
}
```

이 메서드는 깨끗이 컴파일되지만 완벽하진 않다. Iterable src의 원소 타입이 스택의 원소 타입과 일치하면 잘 작동한다. 하지만 Stack<Number>로 선언한 후 pushAll(intVal)을 호출하면 어떻게 될까? 여기서 intVal은 Integer타입이다. Integer는 Number의 하위 타입이니 잘 동작한다. 아니, 논리적으로는 잘 동작해야 할 것 같다.

```java
Stack<Number> numberStack = new Stack();
Iterable<Integer> integers = ...;
numberStack.pushAll(integers);
```

하지만 실제로는 다음의 오류 메시지가 뜬다. 매개변수화 타입이 불공변이기 때문이다.

- 다행히 해결책은 있다. 자바는 이런 상황에 대처할 수 있는 한정적 와일드 카드 타입이라는 특별한 매개변수화 타입을 지원한다. 
- pushAll의 입력 매개변수 타입은 'E의 Iterable'이 아니라 'E의 하위 타입의 Iterable'이어야 하며, 와일드 카드 타입 Iterable<? extends E>가 정확히 이런 뜻이다(사실 extends라는 키워드는 이 상황에 딱 어울리지는 않는다. 하위 타입이란 자기 자신도 포함하지만, 그렇다고 자신을 확장한 것은 아니기 때문이다.)



와일드카드 타입을 사용하도록 pushAll 메서드를 수정해보자.

```java
// E 생산자(producer) 매개변수에 와일드 카드 타입 적용
public void pushAll(Iterable<? extends E> src) {
    for (E e : src)
        push(e);
}
```

이번 수정으로 Stack은 물론 이를 사용하는 클라이언트 코드도 말끔히 컴파일 된다. Stack과 클라이언트 모두 깔끔히 컴파일되었다는 건 모든 것이 타입 안전하다는 뜻이다.



이제 pushAll과 짝을 이루는 popAll 메서드를 작성할 차례다. popAll 메서드는 Stack 안의 모든 원소를 주어진 컬렉션으로 옮겨 담는다. 다음처럼 작성했다고 해보자.

```java
// 와일드카드 타입을 사용하지 않은 popAll 메서드 - 결함이 있다!

public void popAll(Collection<E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

이번에도 주어진 컬렉션의 원소 타입이 스택의 원소 타입과 일치한다면 말끔히 컴파일되고 문제없이 동작한다. 하지만 이번에도 역시나 완벽하진 않다. Stack<Number>의 원소를 Object용 컬렉션으로 옮기려 한다고 해보자.

🍧이 클라이언트 코드를 앞의 popAll 코드와 함께 컴파일 하면 "Collection<Object>는 Collection<Number>의 하위 타입이 아니다"라는, pushAll을 사용했을 때와 비슷한 오류가 발생한다. 이 오류는 와일드 카드 타입으로 해결할 수 있다.

🌱이번에는 popAll의 입력 매개변수의 타입이 'E의 Collection'이 아니라 'E의 상위 타입의 Collection'이어야 한다(모든 타입은 자기 자신의 상위 타입이다.) 와일드 카드 타입을 사용한 Collection<? super E>가 정확히 이런 의미다. 이를 적용해 보자

```java
public void popAll(Collection<? super E> dst) {
    while (!isEmpty())
        dst.add(pop());
}
```

이제 Stack과 클라이언트 코드 모두 말끔히 컴파일된다. 메시지는 분명하다. **유연성을 극대화하려면 원소의 생산자나 소비자용 입력 매개변수에 와일드카드 타입을 사용하라.** 

한편, 입력 매개변수가 생산자와 소비자 역할을 동시에 한다면 와일드카드 타입을 써도 좋을 게 없다. 타입을 정확히 지정해야 하는 상황으로 이때는 와일드카드 타입을 쓰지 말아야 한다.

```java
// 펙스(PECS) : producer-extends, consumer-super
```

**즉, 매개변수화 타입 T가 생산자라면 <? extends T>를 사용하고, 소비자라면 <? super T>를 사용하라. Stack 예에서 pushAll의 src 매개변수는 Stack이 사용할 E 인스턴스를 생산하므로 src 매개변수는 Stack이 사용할 E 인스턴스를 생산하므로 src의 적절한 타입은 Iterable<? extends E>이다.**

- 생산자라는 것은 Stack에 push하는 과정에서는 Stack에 자기자신을 포함한 하위타입을 넣어야 컴파일 오류가 발생하지 않기 때문에 E 타입을 extends 하는 것으로 볼 수 있다.
- 또한 소비자는 stack에 pop하고 Object Collection에 넣는 과정에서 stack에 입장에서는 소비한다고 본 것 같다.  소비자는 Number 타입이고, Object 타입으로 들어간다고 하면 컴파일 오류가 난다. 타입은 불공변이기 때문인데 이것을 한정적 와일드 카드를 사용해 dst가 E 타입의 상위 타입임을 정의하여 컴파일 타임의 오류를 없앨 수 있다.



PECS 공식은 와일드카드 타입을 사용하는 기본 원칙이다. 나프탈린과 와들러는 이를 겟풋원칙으로 부른다.



> 반환타입에는 한정적 와일드 카드 타입을 사용하면 안된다. 유연성을 높여주기는커녕 클라이언트 코드에서도 와일드카드 타입을 써야 하기 때문이다.



:notebook_with_decorative_cover: 제대로만 사용한다면 클래스 사용자는 와일드카드 타입이 쓰였다는 사실조차 의식하지 못할 것이다. 받아들여야 할 매개변수를 받고 거절해야 할 매개변수는 거절하는 작업이 알아서 이뤄진다.

**클래스 사용자가 와일드카드 타입을 신경 써야 한다면 그 API에 무슨 문제가 있을 가능성이 크다.**



앞 코드는 자바 8부터 제대로 컴파일된다. 자바 7까지는 타입 추론 능력이 충분히 강력하지 못해서 문맥에 맞는 반환타입을 명시해야 했다. 

```java
// 자바 7까지는 명시적 타입 인수를 사용해야 한다.
Set<Number> numbers = Union.<Number>union(integers, doubles);
```

> 매개변수와 인수의 차이를 알아보자. 
>
> 매개변수는 메서드 선언에 정의한 변수이고, 인수는 메서드 호출 시 넘기는 '실젯값'이다. 예를 살펴보자
>
> void add(int value) { ... }
>
> add(10)
>
> 이 코드에서 value는 매개변수이고 10은 인수다. 이 정의를 제네릭까지 확장하면 다음과 같다.
>
> class Set<T> { ... }
>
> Set<Integer> = ...;
>
> 여기서 T는 타입 매개변수가 되고, Integer는 타입 인수가 된다. 보통은 이 둘을 명확히 구분하지 않으니 크게 신경 쓸 필요는 없지만, 자바 언어 명세에서는 구분하고 있어서 설명을 덧붙었다.



- 다음 코드를 보자

  ```java
  public static <E extends Comparable<E>> E max(List<E> list)
  ```

  이것을 와일드카드 타입을 사용해 다듬은 모습이다.

  ```java
  public static <E extends Comparable<? super E>> E max(
  		List<? extends E> list)
  ```

  이번에는 PECS 공식을 두 번 적용했다. 둘 중 더 쉬운 입력 매개변수 목록부터 살펴보자.

  🍧 입력 매개변수에서는 E 인스턴스를 생산하므로 원래의 List<E>를 List<? extends E> 로 수정했다.