# Effective Java item 71



### 필요 없는 검사 예외 사용은 피하라



검사 예외를 싫어하는 자바 프로그래머가 많지만 제대로 활용하면 API와 프로그램의 질을 높일 수 있다. 결과를 코드로 반환하거나 비검사 예외를 던지는 것과 달리, 검사 예외는 발생한 문제를 프로그래머가 처리하여 안전성을 높이게끔 해준다. 물론 검사 예외를 과하게 사용하면 오히려 쓰기 불편한 API가 된다.



- 어떤 메서드가 검사 예외를 던질 수 있다고 선언됐다면, 이를 호출하는 코드에서는 catch 블록을 두어 그 예외를 붙잡아 처리하거나 더 바깥으로 던져 문제를 전파해야만 한다. 어느 쪽이든 API 사용자에게 부담을 준다. 더구나 검사 예외를 던지는 메서드는 스트림안에서 직접 사용할 수 없기 때문에 자바 8부터는 부담이 더욱 커졌다.
- API를 제대로 사용해도 발생할 수 있는 예외이거나, 프로그래머가 의미 있는 조치를 취할 수 있는 경우라면 이 정도 부담은 받아 들일 수 있을 것이다. 그러나 **둘 중 어디에도 해당하지 않는다면 비검사 예외를 사용하는 게 좋다.**



검사 예외와 비검사 예외 중 어느 것을 선택해야 할지는 프로그래머가 그 예외를 어떻게 다룰지 생각해보면 알 수 있다. 다음과 같이 하는 게 최선인가?



```java
} catch (TheCheckedException e) {
    throw new AssertionError(); // 일어날 수 없다!
}
```

아니면 다음 방식은 어떤가?

```java
} catch (TheCheckedException e) {
    e.printStackTrace(); // 이런, 우리가 졌다.
    System.exit(1);
}
```

더 나은 방법이 없다면 비검사 예외를 선택해야 한다.



- 검사 예외를 회피하는 가장 쉬운 방법은 적절한 결과 타입을 담은 옵셔널을 반환하는 것이다. 검사 예외를 던지는 대신 단순히 빈 옵셔널을 반환하면 된다. 이 방식의 단점이라면 예외가 발생한 이유를 알려주는 부가 정보를 담을 수 없다는 것이다.
- 또 다른 방법으로, 검사 예외를 던지는 메서드를 2개로 쪼개 비검사 예외로 바꿀 수 있다. 이 방식에서 첫 번째 메서드는 예외가 던져질지 여부를 boolean 값으로 반환한다. 다음 예를 보자.

```java
// 검사 예외를 던지는 메서드 - 리펙토링 전

try {
    obj.action(args);
} catch (TheCheckedException e) {
    ... // 예외 상황에 대처한다.
}
```

리펙터링하면 다음처럼 된다.

```java
// 상태 검사 메서드와 비검사 예외를 던지는 메서드 - 리펙터링 후

if (obj.actionPPermitted(args)) {
    obj.action(args);
} else {
    ... // 예외 상황에 대처한다.
}
```

이 리펙토링을 모든 상황에 적용할 수는 없다. 그래도 적용할 수만 있다면 더 쓰기 편한 API를 제공할 수 있다. 리팩터링 후의 API가 딱히 더 아름답진 않지만, 더 유연한 것은 확실하다.



> 핵심 정리
>
> 꼭 필요한 곳에만 사용한다면 검사 예외는 프로그램의 안전성을 높여주지만, 남용하면 쓰기 고통스러운 API를 낳는다. API 호출자가 예외 상황에서 복구할 방법이 없다면 비검사 예외를 던지자. 복구가 가능하고 호출자가 그 처리를 해주길 바란다면, 우선 옵셔널을 반환해도 될지 고민하자. 옵셔널만으로는 상황을 처리하기에 충분한 정보를 제공할 수 없을 때만 검사 예외를 던지자.