# Effective Java item 18



### 상속보다는 컴포지션을 사용하라



상속은 코드를 재사용하는 강력한 수단이지만, 항상 최선은 아니다. 잘못 사용하면 오류를 내기 쉬운 소프트웨어를 만들게 된다.



**메서드 호출과 달리 상속은 캡슐화를 깨뜨린다.** 다르게 말하면 상위 클래스가 어떻게 구현되느냐에 따라 하위 클래스의 동작에 이상이 생길 수 있다.

구체적인 예를 들어보자. 우리에게 HashSet을 사용하는 프로그램이 있다. 성능을 높이려면 이 HashSet은 처음 생성된 이후 원소가 몇 개 더 해졌는지 알 수 있어야 한다. 그래서 코드 18-1과 같이 변형된 HashSet을 만들어 추가도니 원소의 수를 저장하는 변수와 접근자 메서드를 추가했다. 그런 다음 HashSet에 원소를 추가하는 메서드인 add와 addAll을 재정의했다.

```java
// 잘못된 예 - 상속을 잘못 사용했다!
public class InstrumentedHashSet<E> extends HashSet<E> {
    // 추가된 원소의 수
    private int addCount = 0;
    
    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }
    
    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    
    public int getAddCount() {
        return addCount;
    }
}
```

이 클래스는 잘 구현된 것처럼 보이지만 제대로 작동하지 않는다. 이 클래스의 인스턴스에 addAll 메서드로 원소 3개를 더했다고 해보자. 다음 코드는 자바 9부터 지원하는 정적 팩토리 메서드인 List.of로 리스트를 생성했다. 그 전 버전을 사용하는 독자는 Arrays.asList를 사용하면 된다.

```java
InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
s.addAll(List.of("틱", "탁탁", "펑"));
```

이제 getAddCount 메서드를 호출하면 3을 반환하리라 기대하겠지만, 실제로는 6을 반환한다. 어디서 잘못된 걸까? 그 원인은 HashSet의 addAll 메서드가 add메서드를 사용해 구현된 데 있다. 이런 내부 구현 방식은 HashSet문서에서는 쓰여 있지 않다. InstrumentedHashSet의 addAll은 addCount은 각 원소를 add 메서드를 호출해 추가하는데, 이때 불리는 add는 InstrumentedHashSet에서 재정의한 메서드다. 따라서 addCount에 값이 중복해서 더해져, 최종값이 6으로 늘어난 것이다. addAll로 추가한 원소 하나당 2씩 늘어났다.



addAll 메서드를 다른식으로 재정의 할 수도 있다. 예컨대 주어진 컬렉션을 순회하며 원소 하나당 add 메서드를 한 번만 호출하는 것이다. 이 방식은 HashSet의 addAll을 더 이상 호출하지 않으니 addAll이 add를 사용하는지와 상관없이 결과가 옳다는 점에서 조금은 나은 해법이다.



- 상위 클래스의 메서드 동작을 다시 구현하는 이 방식은 어렵고, 시간도 더 들고, 자칫 오류를 내거나 성능을 떨어뜨릴 수도 있다. 또한 하위 클래스에서는 접근 할 수 없는 private필드를 써야 하는 상황이라면 이 방식으로는 구현 자체가 불가능 하다.

- 하위 클래스가 깨지기 쉬운 이유는 더 있다. 다음 릴리스에서 상위 클래스에서 새로운 메서드를 추가한다면..

  보안 때문에 컬렉션에 추가된 모든 원소가 특정 조건을 만족해야만 하는 프로그램을 생각해보자. 그 컬렉션을 상속하여 원소를 추가하는 모든 메서드를 재정의해 필요한 조건을 먼저 검사하게끔 하면 될 것 같다. 하지만 이 방식이 통하는 것은 상위 클래스에 또 다른 원소 추가 메서드가 만들어지기 전까지다.

- 이상의 두 문제 모두 메서드 재정의가 원인이었다. 따라서 클래스를 확장하더라도 메서드를 재정의하는 대신 새로운 메서드를 추가하면 괜찮으리라 생각 할 수도 있다. 이 방식이 훨씬 안전한 것은 맞지만, 위험이 전혀 없는 것은 아니다. 다음 릴리스에서 상위 클래스에 새 메서드가 추가됐는데, 운 없게도 하필 여러분이 하위 클래스에 추가한 메서드와 시그니처가 같고 반환 타입은 다르다면 여러분의 클래스는 컴파일조차 되지 않는다.



다행히 이상의 문제를 모두 피해 가는 묘안이 있다 기존 클래스를 확장하는 대신, 새로운 클래스를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하자. 기존 클래스가 새로운 클래스의 구성요소로 쓰인다는 뜻에서 이러한 설계를 컴포지션이라 한다.



```java
// 래퍼 클래스 - 상속 대신 컴포지션을 사용했다.

public class InstrumentedSet<E> extends ForwardingSet<E> {
    
    private int addCount = 0;
    
    public InstrumentedSet(Set<E> s) {
        super(s);
    }
    
    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }
    
    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }
    
    public int getAddCount() {
        return addCount;
    }
}
```



- 상속은 반드시 하위 클래스가 상위 클래스의 '진짜' 하위 타입인 상황에서만 쓰여야 한다. 다르게 말하면, 클래스 B가 클래스 A와 is-a 관계일 때만 클래스 A를 상속해야 한다. 클래스 A를 상속하는 클래스 B를 작성하러 한다면 "B가 정말 A인가?" 라고 자문해보자. "그렇다"고 확신할 수 없다면 B는 A를 상속해서는 안된다.
- 컴포지션을 써야 할 상황에서 상속을 사용하는 건 내부 구현을 불필요하게 노출하는 꼴이다. 그 결과 API가 내부 구현에 묶이고 그 클래스의 성능도 영원히 제한된다. 더 심각ㅎ나 문제는 클라이언트가 노출된 내부에 직접 접근 할 수 있다는 점이다.
- 컴포지션 대신 상속을 사용하기로 결정하기 전에 마지막으로 자문해야 할 질문이 있다. 확장하려는 클래스의 API에 아무런 결함이 없는가? 결함이 있다면, 이 결함이 여러분 클래스의 API에 아무런 결함이 없는가? 컴포지션으로는 이런 결함을 숨기는 새로운 API를 설계할 수 있지만, 상속은 상위 클래스의 API를 '그 결함까지도' 그대로 승계한다.



> 핵심 정리
>
> 상속은 강력하지만 캡슐화를 해친다는 문제가 있다. 상속은 상위 클래스와 하위 클래스가 순수한 is - a 관계일 때만 써야 한다. is-a 관계일 때도 안심할 수만은 없는 게, 하위 클래스의 패키지가 상위 클래스와 다르고, 상위 클래스가 확장을 고려해 설계되지 않았다면 여전히 문제가 될 수 있다. 상속의 취약점을 피하려면 상속 대신 컴포지션과 전달을 사용하자. 특히 래퍼 클래스로 구현할 적당한 인터페이스가 있다면 더욱 그렇다. 래퍼 클래스는 하위 클래스보다 견고하고 강력하다.