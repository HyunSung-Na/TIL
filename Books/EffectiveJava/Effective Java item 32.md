# Effective Java item 32



### 제네릭과 가변인수를 함께 쓸 때는 신중하라



가변인수 메서드와 제네릭은 자바 5때 함께 추가되었으나 서로 잘 어우러지리라 기대하겠지만, 슬프게도 그렇지 않다. 가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해주는데, 구현 방식에 허점이 있다. 

- **가변인수 메서드를 호출하면 가변인수를 담기 위한 배열이 자동으로 하나 만들어진다.**  그런데 내부로 감춰야 했을 이 배열을 그만 클라이언트에 노출하는 문제가 생겼다. 그 결과 varargs 매개변수에 제네릭이나 매개변수화 타입이 포함되면 알기 어려운 컴파일 경고가 발생한다.

- 아이템 28에서 실체화 불가 타입은 런타임에는 컴파일타임보다 타입 관련 정보를 적게 담고 있음을 배웠다. 그리고 거의 모든 제네릭과 매개변수화 타입은 실체화되지 않는다. 메서드를 선언할 때 실체화 불가 타입으로 varargs 매개변수를 선언하면 컴파일러가 경고를 보낸다.

  

```java
warning: [unchecked] Possible heap pollution from
    parameterized vararg type List<String>
```



매개변수화 타입의 변수가 타입이 다른 객체를 참조하면 힙 오염이 발생한다. 이렇게 다른 타입 객체를 참조하는 상황에서는 컴파일러가 자동 생성한 형변환이 실패할 수 있으니, 제네릭 타입 시스템이 약속한 타입 안정성의 근간이 흔들려버린다.

🚩 가변인수 메서드인데 제네릭으로 선언하면 가변이 아니게 된다.

```java
// 제네릭과 varargs를 혼용하면 타입 안정성이 깨진다!

static void dangerous(List<String>... stringLists) {
    List<Integer> intList = List.of(42);
    Object[] objects = stringLists;
    objects[0] = intList;  // 힙 오염 발생
    String s = stringLists[0].get(0); // ClassCastException
}
```

이 메세드에서는 형변환하는 곳이 보이지 않는데도 인수를 건네 호출하면 ClassCastException을 던진다. 마지막 줄에 컴파일러가 생성한 형변환이 숨어 있기 때문이다.

이처럼 타입 안전성이 깨지니 **제네릭 varargs 배열 매개변수에 값을 저장하는 것은 안전하지 않다.**



- 자바 7 전에는 제네릭 가변인수 메서드의 작성자가 호출자 쪽에서 발생하는 경고에 대해서 해줄 수 있는 일이 없었다. 따라서 이런 메서드는 사용하기에 좀 꺼림칙했다. 사용자는 이 경고들을 그냥 두거나 호출하는 곳마다 @SuppressWarnings("unchecked") 애너테이션을 달아 경고를 숨겨야 했다. 지루한 작업이고, 가독성을 떨어뜨리고, 때로는 진짜 문제를 알려주는 경고마저 숨기는 안 좋은 결과로 이어졌다.
- 자바 7에서는 @SafeVarargs 애너테이션이 추가되어 제네릭 가변인수 메서드 작성자가 클라이언트 측에서 발생하는 경고를 숨길 수 있게 되었다. **@SafeVarargs 애너테이션은 메서드 작성자가 그 메서드가 타입 안전함을 보장하는 장치다.**

😅메서드가 안전한 게 확실하지 않다면 절대 @SafeVarargs 애너테이션을 달아서는 안된다.

그렇다면 메서드가 안전한지는 어떻게 확신할 수 있을까?

1. 가변인수 메서드를 호출할 때 varargs 매개변수를 담는 제네릭 배열이 만들어진다는 사실을 기억하자.
2. 메서드가 이 배열에 아무것도 저장하지 않고(그 매개변수들을 덮어쓰지 않고) 그 배열의 참조가 밖으로 노출되지 않는다면 (신뢰할 수 없는 코드가 배열에 접근할 수 없다면) 타입 안전하다.
3. 이 varargs 매개변수 배열이 호출자로부터 그 메서드로 순수하게 인수들을 전달하는 일만 한다면 (varargs의 목적대로만 쓰인다면) 그 메서드는 안전하다.

⚡ 이때, varargs 매개변수 배열에 아무것도 저장하지 않고도 타입 안전성을 깰수도 있으니 주의해야 한다.

```java
// 자신의 제네릭 매개변수 배열의 참조를 노출한다. - 안전하지 않다.

static <T> T[] toArray(T... args) {
    return args;
}
```

이 메서드가 반환하는 배열의 타입은 이 메서드에 인수를 넘기는 컴파일타임에 결정되는데, 그 시점에는 컴파일러에게 충분한 정보가 주어지지 않아 타입을 잘못 판단할 수 있다. 따라서 자신의 varargs 매개변수 배열을 그대로 반환하면 힙 오염을 이 메서드를 호출한 쪽의 콜스택으로까지 전이하는 결과를 낳을 수 있다.



- 구체적인 예를 보자. 다음 메서드는 T 타입 인수 3개를 받아 그중 2개를 무작위로 골라 담은 배열을 반환한다.

```java
static <T> T[] pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return toArray(a, b);
        case 1: return toArray(a, c);
        case 2: return toArray(b, c);    
    }
    throw new AssertionError(); // 도달할 수 없다.
}
```

이 메서드는 제네릭 가변인수를 받는 toArray 메서드를 호출한다는 점만 빼면 위험하지 않고 경고도 내지 않을 것이다. 이 메서드를 본 컴파일러는 toArray에 넘길 T 인스턴스 2개를 담을 varargs 매개변수 배열을 만드는 코드를 생성한다. 이 코드가 만드는 배열의 타입은 Object[] 인데, pickTwo에 어떤 타입의 객체를 넘기더라도 담을 수 있는 가장 구체적인 타입이기 때문이다. 그리고 toArray 메서드가 돌려준 이 배열이 그대로 pickTwo를 호출한 클라이언트까지 전달된다. 즉, pickTwo는 항상 Object[] 타입 배열을 반환한다.



이제 pickTwo를 사용하는 main메서드를 볼 차례다.

```java
public static void main(String[] args) {
    String[] attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

아무런 문제가 없는 메서드이니 별다른경고 없이 컴파일 된다. 하지만 실행하려 들면 ClassCastException을 던진다. 형변환하는 곳이 보이지 않는데도 말이다.

- 무엇을 놓친 것일까? 바로 pickTwo의 반환값을 attributes에 저장하기 위해 String[]로 형변환하는 코드를 컴파일러가 자동 생성한다는 점을 놓쳤다. Object[]는 String[]의 하위타입이 아니므로 이 형변환은 실패한다.
- 이 예는 **제네릭 varargs 매개변수 배열에 다른메서드가 접근하도록 허용하면 안전하지 않다**는 점을 다시 한번 상기시킨다. 단 예외가 두가지 있다.
  - 첫 번째, @SafeVarargs로 제대로 애노테이트된 또 다른 varargs 메서드에 넘기는 것은 안전하다.
  - 두 번째, 그저 이 배열 내용의 일부 함수를 호출만 하는 (varargs) 일반 메서드에 넘기는 것도 안전하다.



```java
// 제네릭 varargs 매개변수를 안전하게 사용하는 메서드
@SafeVarargs
static <T> List<T> flatten(List<? extends T>... lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
        result.addAll(list);
    return result;
}
```



🚩 @SafeVarargs 애너테이션을 사용해야 할 때를 정하는 규칙은 간단하다. **제네릭이나 매개변수화 타입의 varargs 매개변수를 받는 모든 메서드에 @SafeVarargs를 달라.** 그래야 사용자를 헷갈리게 하는 컴파일러 경고를 없앨 수 있다. 이 말은 안전하지 않은 varargs 메서드는 절대 작성해서는 안 된다는 뜻이기도 하다.



⭐여러분이 통제할 수 있는 메서드 중 제네릭 varargs 매개변수를 사용하며 힙 오염 경고가 뜨는 메서드가 있다면, 그 메서드가 진짜 안전한지 점검하라. 정리하자면, 다음 두 조건을 모두 만족하는 제네릭 varargs 메서드는 안전하다. 둘 줄 하나라도 어겼다면 수정하라!

1. varargs 매개변수 배열에 아무것도 저장하지 않는다.
2. 그 배열을 신뢰할 수 없는 코드에 노출하지 않는다.



> @SafeVarargs 애너테이션은 재정의할 수 없는 메서드에만 달아야 한다. 재정의한 메서드도 안전할지는 보장할 수 없기 때문이다. 자바 8에서 이 애너테이션은 오직 정적 메서드와 final 인스턴스 메서드에만 붙일 수 있고, 자바 9 부터는 private 인스턴스 메서드에도 허용된다.



@SafeVarargs 애너테이션이 유일한 정답은 아니다. varargs 매개변수를 List 매개변수로 바꿀 수도 있다. 이 방식을 앞서의 flatten 메서드에 적용하면 다음처럼 된다. 매개변수 선언만 수정했음에 주목하자.



```java
// 제네릭 varargs 매개변수를 List로 대체한 예 - 타입 안전하다.

static <T> List<T> flatten(List<List<? extends T>> lists) {
    List<T> result = new ArrayList<>();
    for (List<? extends T> list : lists)
        result.addAll(list);
    return result;
}
```

정적 팩터리 메서드인 List.of를 활용하면 다음 코드와 같이 이 메서드에 임의개수의 인수를 넘길 수 있다. 이렇게 사용하는 게 가능한 이유는 List.of에도 @SafeVarargs 애너테이션이 달려 있기 때문이다.

```java
audience = flatten(List.of(friends, romans, countrymen));
```

- 이 방식의 장점은 컴파일러가 이 메서드의 타입 안전성을 검증할 수 있다는 데 있다. @SafeVarargs 애너테이션을 우리가 직접 달지 않아도 되며, 실수로 안전하다고 판단할 걱정도 없다. 단점이라면 클라이언트 코드가 살짝 지저분해지고 속도가 조금 느려질 수 있다는 정도다.
- 또한 이 방식은 toArray처럼 varargs 메서드를 안전하게 작성하는 게 불가능한 상황에서도 쓸 수 있다. 이 toArray의 List 버전이 바로 List.of로, 자바 라이브러리 차원에서 제공하니 우리가 직접 작성할 필요도 없다. 이방식을 pickTwo에 적용하면 다음처럼 된다.



```java
static <T> List<T> pickTwo(T a, T b, T c) {
    switch(ThreadLocalRandom.current().nextInt(3)) {
        case 0: return List.of(a, b);
        case 1: return List.of(a, c);
        case 2: return List.of(b, c);   
    }
    throw new AssertionError();
}
```

그리고 main 메서드는 다음처럼 변한다.

```java
public static void main(String[] args) {
    List<String> attributes = pickTwo("좋은", "빠른", "저렴한");
}
```

결과 코드는 배열 없이 제네릭만 사용하므로 타입 안전하다.



> 핵심 정리
>
> 가변인수와 제네릭은 궁합이 좋지 않다. 가변인수 기능은 배열을 노출하여 추상화가 완벽하지 못하고, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다. 제네릭 varargs 매개변수는 타입 안전하지는 않지만, 허용된다. 메서드에 제네릭 (혹은 매개변수화된) varags 매개변수를 사용하고자 한다면, 먼저 그 메서드가 타입 안전한지 확인한 다음 @SafeVarargs 애너테이션을 달아 사용하는 데 불편함이 없게끔 하자.

