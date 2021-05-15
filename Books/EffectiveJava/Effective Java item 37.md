# Effective Java item 37



### ordinal 인덱싱 대신 EnumMap을 사용하라



이따금 배열이나 리스트에서 원소를 꺼낼 때 ordinal 메서드로 인텍스를 얻는 코드가 있다. 식물을 간단히 나타낸 다음 클래스를 예로 살펴보자.



```java
class Plant {
    enum LifeCycle { ANNUAL, PERENNIAL, BIENNIAL }
    
    final String name;
    final LifeCycle lifeCycle;
    
    Plant(String name, LifeCycle lifeCycle) {
        this.name = name;
        this.lifeCycle = lifeCycle;
    }
    
    @Override public String toString() {
        return name;
    }
}
```

이제 정원에 심은 식물들을 배열 하나로 관리하고, 이들을 생애주기(한해살이, 여러해살이, 두해살이)별로 묶어보자. 생애주기별로 총 3개의 집합을 만들고 정원을 한 바퀴 돌며 각 식물을 해당 집합에 넣는다. 이때 어떤 프로그래머는 집합들을 배열 하나에 넣고 생애주기의 ordinal 값을 그 배열의 인덱스로 사용하려 할 것이다.

🥺 하지만 이 방법은 좋지 못한 방법이다. 정수는 열거 타입과 달리 타입 안전하지 않기 때문이다.



- 훨씬 멋진 해결책이 있으니 걱정마시라. 여기서 배열은 실질적으로 열거 타입 상수를 값으로 매핑하는 일을 한다. 그러니 Map을 사용할 수도 있을 것이다. 사실 열거 타입을 키로 사용하도록 설계한 아주 빠른 Map 구현체가 존재하는데, 바로 EnumMap이 그 주인공이다. 다음 코드를 보자

```java
// EnumMap을 사용해 데이터와 열거 타입을 매핑한다.
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle =
    new EnumMap<>(Plant.LifeCycle.class);
for (Plant.LifeCycle lc : Plant.LifeCycle.values())
    plantsByLifeCycle.put(lc, new HashSet<>());
for (Plant p : garden)
    plantsByLifeCycle.get(p.lifeCycle).add(p);
System.out.println(plantsByLifeCycle);
```

더 짧고 명료하고 안전하고 성능도 원래 버전과 비등하다. 안전하지 않은 형변환은 쓰지 않고, 맵의 키인 열거 타입이 그 자체로 출력용 문자열을 제공하니 출력 결과에 직접 레이블을 달 일도 없다. 나아가 배열 인덱스를 계산하는 과정에서 오류가 날 가능성도 원천봉쇄된다. 

**EnumMap의 성능이 ordinal을 쓴 배열에 비견되는 이유는 그 내부에서 배열을 사용하기 때문이다.** 내부 구현 방식을 안으로 숨겨서 Map의 타입 안전성과 배열의 성능을 모두 얻어낸 것이다.여기서 EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로 런타임 제네릭 타입 정보를 제공한다.



- 스트림을 사용해 맵을 관리하면 코드를 더 줄일 수 있다. 다음은 위 코드를 스트림으로 바꾼 코드다.

```java
// 스트림을 사용한 코드 1 - EnumMap을 사용하지 않는다!

System.out.println(Arrays.stream(garden)
                  .collect(groupingBy(p -> p.lifeCycle)));
```

이 코드는 EnumMap이 아닌 고유한 맵 구현체를 사용했기 때문에 EnumMap을 써서 얻은 공간과 성능 이점이 사라진다는 문제가 있다. 이 문제를 좀 더 구체적으로 살펴보자. 매개변수 3개짜리 Collectors.groupingBy 메서드는 mapFactory 매개변수에 원하는 맵 구현체를 명시해 호출할 수 있다.

```java
// 스트림을 사용한 코드 2 - EnumMap을 이용해 데이터와 열거 타입을 매핑했다.
System.out.println(Arrays.stream(garden)
                  .collect(groupingBy(p -> p.lifeCycle,
                                     () -> new EnumMap<>(LifeCycle.class), toSet)));
```

이 예처럼 단순한 프로그램에서는 최적화가 굳이 필요 없지만, 맵을 빈번히 사용하는 프로그램에서는 꼭 필요할 것이다.



- 스트림을 사용하면 EnumMap만 사용했을 때와는 살짝 다르게 동작한다. EnumMap 버전은 언제나 식물의 생애주기당 하나씩의 중첩 맵을 만들지만, 스트림 버전에서는 해당 생애주기에 속하는 식물이 있을 때만 만든다.
- 두 열거 타입 값들을 매핑하느라 ordinal을 쓴 배열들의 배열을 본 적이 있을 것이다. 다음은 이 방식을 적용해 두 가지 상태를 전이와 매핑하도록 구현한 프로그램이다. 

```java
// 배열들의 배열의 인덱스에 ordinal()을 사용 - 따라 하지 말 것!

public enum Phase {
    SOLID, LIQUID, GAS;
    
    public enum Transition {
        MELT, FREEZE, BOIL, CONDENSE, DEPOSIT;
        
        // 행은 from의 ordinal을, 열은 to의 ordinal을 인덱스로 쓴다.
        private static final Transition[][] TRANSITIONS = {
            { null, MELT, SUBLIME },
            { FREEZE, null, BOIL },
            { DEPOSIT, CONDENSE, null }
        };
        
        // 한 상태에서 다른상태로의 전이를 반환한다.
        public static Transition from(Phase from, Phase to) {
            return TRANSITIONS[from.ordinal()][to.ordinal()];
        }
    }
}
```

멋져 보이지만 겉모습에 속으면 안 된다. 앞서 보여준 간단한 정원 예제와 마찬가지로 컴파일러는 ordinal과 배열 인덱스의 관계를 알 도리가 없다. 즉, Phase나 Phase, Transition 열거 타입을 수정하면서 상전이 표 TRANSITIONS를 함께 수정하지 않거나 실수로 잘못 수정하면 런타임 오류가 날 것이다.



**다시 이야기하지만 EnumMap을 사용하는 편이 훨씬 낫다.** 전이 하나를 얻으려면 이전 상태와 이후 상태가 필요하니, 맵 2개를 중첩하면 쉽게 해결 할 수 있다. 안쪽 맵은 이전 상태와 전이를 연결하고 바깥 맵은 이후 상태와 안쪽 맵을 연결한다. 전이 전후의 두 상태를 전이 열거 타입 Transition의 입력으로 받아, 이 Transition 상수들로 중첩된 EnumMap을 초기화 하면 된다.



```java
// 중첩 EnumMap으로 데이터와 열거 타입 쌍을 연결했다.

public enum Phase {
    SOLID, LIQUID, GAS;
    
    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID);
    }
    
    private final Phase from;
    private final Phase to;
    
    Transition(Phase from, Phase to) {
        this.from = from;
        this.to = to;
    }
    
    // 상전이 맵을 초기화 한다.
    private static final Map<Phase, Map<Phase, Transition>>
        m = stream.of(values()).collect(groupingBy(t -> t.from,
                                                  () -> new EnumMap<>(Phase.class)));
    
    public static Transition from(Phase from, Phase to) {
        return m.get(from).get(to);
    }
}
```

상전이 맵을 초기화하는 코드는 제법 복잡하다. 이 맵의 타입인 Map<Phase, Map<Phase, Transition>>은 "이전 상태에서 '이후 상태에서 전이로의 맵'에 대응시키는 맵" 이라는 뜻이다. 이러한 맵의 맵을 초기화하기 위해 수집기 2개를 차례로 사용했다.





```java
// EnumMap 버전에 새로운 상태 추가하기

public enum Phase {
    SOLID, LIQUID, GAS, PLASMA;
    
    public enum Transition {
        MELT(SOLID, LIQUID), FREEZE(LIQUID, SOLID),
        BOIL(LIQUID, GAS), CONDENSE(GAS, LIQUID),
        SUBLIME(SOLID, GAS), DEPOSIT(GAS, SOLID),
        IONIZE(GAS, PLASMA), DEIONIZE(PLASMA, GAS);
        
        ... // 나머지 코드는 그대로다.
    }
}
```

나머지는 기존 로직에서 잘 처리해주어 잘못 수정할 가능성이 극히 작다. 실제 내부에서는 맵들의 맵이 배열들의 배열로 구현되니 낭비되는 공간과 시간도 거의 없이 명확하고 안전하고 유지보수하기 좋다.



> 핵심 정리
>
> 배열의 인덱스를 얻기 위해 ordinal을 쓰는 것은 일반적으로 좋지 않으니, 대신 EnumMap을 사용하라. 다차원 관계는 EnumMap<..., EnumMap<...>>으로 표현하라. "애플리케이션 프로그래머는 Enum.ordinal을 사용하지 말아야 한다(아이템 35)"는 일반 원칙의 특수한 사례다.



