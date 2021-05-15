# Effective Java item 40



### @Override 애너테이션을 일관되게 사용하라



- 자바가 기본으로 제공하는 애너테이션 중 보통의 프로그래머에게 가장 중요한 것은 @Override일 것이다. @Override는 메서드 선언에만 달 수 있으며, 이 애너테이션이 달렸다는 것은 상위 타입의 메서드를 재정의했음을 뜻한다. 이 애너테이션을 일관되게 사용하면 여러가지 악명 높은 버그들을 예방해준다. 



다음의 Bigram 프로그램을 살펴보자.

```java
// 영어 알파벳 2개로 구성된 문자열을 표현하는 클래스 - 버그를 찾아보자
public class Bigram {
    private final char first;
    private final char second;
    
    public Bigram(char first, char second) {
        this.first = first;
        this.second = second;
    }
    public boolean equals(Bigram b) {
        return b.first == first && b.second == second;
    }
    public int hashCode() {
        return 31 * first + second;
    }
    
    public static void main(String[] args) {
        Set<Bigram> s = new HashSet<>();
        for (int i = 0; i < 10; i++)
           	for (char ch = 'a'; ch <= 'z'; ch++)
                s.add(new Bigram(ch, ch));
        System.out.println(s.size);
    }
}
```

main 메서드를 보면 똑같은 소문자 2개로 구성된 바이그램 26개를 10번 반복해 집합에 추가한 다음, 그 집합의 크기를 출력한다. Set은 중복을 허용하지 않으니 26이 출력될 거 같지만 실제로는 260이 출력된다.

- 확실히 Bigram 작성자는 equals를 재정의하려 한 것으로 보인다. hashCode도 함께 재정의해야 한다는 사실을 잊지 않았다.
- 하지만 equals를 재정의를 한 것이 아니라 다중정의를 해버렸다. Object의 equals를 재정의하려면 매개변수 타입을 Object로 해야만 하는데, 그렇게 하지 않은 것이다. 그래서 Object에서 상속한 equals와는 별개인 equals를 새로 정의한 꼴이 되었다. Object의 equals는 == 연산자와 똑같이 객체 식별성만을 확인한다. 따라서 같은 소문자를 소유한 바이그램 10개 각각이 서로 다른객체로 인식되고, 결국 260을 출력한 것이다.
- 다행이 이 오류는 컴파일러가 찾아낼 수 있지만 그러려면 Object.equals를 재정의한다는 의도를 명시해야 한다.

```java
@Override
public boolean equals(Bigram b) {
    return b.first == first && b.second == second;
}
```

- 이처럼 @Override 애너테이션을 달고 다시 컴파일하면 오류가 발생한다.
- 잘못된 부분을 명확히 알려주므로 올바르게 수정할 수 있다.



⭐**그러니 상위 클래스의 메서드를 재정의하려는 모든 메서드에 @Override 애너테이션을 달자.** 예외는 한 가지뿐이다. 구체 클래스에서 상위 클래스의 추상메서드를 재정의할 때는 굳이 달지 않아도 된다.



🚩 @Override는 클래스뿐 아니라 인터페이스의 메서드를 재정의할 때도 사용할 수 있다. 디폴트 메서드를 지원하기 시작하면서, 인터페이스 메서드를 구현한 메서드에도 @Override를 다는 습관을 들이면 시그니처가 올바른지 재차 확인할 수 있다.



🚀 추상클래스나 인터페이스에서는 상위 클래스나 상위 인터페이스의 메서드를 재정의하는 모든 메서드에 @Override를 다는 것이 좋다. 상위 클래스가 구체 클래스든 추상 클래스든 마찬가지다. 예컨데 Set 인터페이스는 Collection 인터페이스를 확장했지만 새로 추가한 메서드는 없다. 따라서 모든 메서드 선언에 @Override를 달아 실수로 추가한 메서드가 없음을 보장했다.



> 핵심정리
>
> 재정의한 모든 메서드에 @Override 애너테이션을 의식적으로 달면 여러분이 실수했을때 컴파일러가 바로 알려줄 것이다. 예외는 한 가지 뿐이다. 구체 클래스에서 상위 클래스의 추상 메서드를 재정의한 경우엔 이 애너테이션을 달지 않아도 된다.(단다고 해서 해로울 것도 없다.)

