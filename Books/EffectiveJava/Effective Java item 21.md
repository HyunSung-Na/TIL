# Effective Java item 21



### 인터페이스는 구현하는 쪽을 생각해 설계하라



자바 8 전에는 기존 구현체를 깨뜨리지 않고는 인터페이스에 메서드를 추가할 방법이 없었다. 인터페이스에 메서드를 추가하면 보통은 컴파일 오류가 나는데, 추가된 메서드가 우연히 기존 구현체에 이미 존재할 가능성은 아주 낮기 때문이다. 잡자 8에 와서 기존 인터페이스에 메서드를 추가할 수 있도록 디폴트 메서드를 소개했지만 위험이 완전히 사라진 것은 아니다.



:notebook_with_decorative_cover: 디폴트 메서드를 선언하면, 그 인터페이스를 구현한 후 디폴트 메서드를 재정의하지 않은 모든 클래스에서 디	 폴트 구현이 쓰이게 된다. 이처럼 자바에도 기존 이너페이스에 메서드를 추가하는 길이 열렸지만 모든 기존 구	현체들과 매끄럽게 연동되리라는 보장은 없다.

:notebook_with_decorative_cover: 자바 8에서는 핵심 컬렉션 인터페이스들에 다수의 디폴트 메서드가 추가되었다. 주로 람다를 활용하기 위해서	다. 자바 라이브러리의 디폴트 메서드는 코드 품질이 높고 범용적이라 대부분 상황에서 잘 작동한다.



- **생각할 수 있는 모든 상황에서 불변식을 해치지 않는 디폴트 메서드를 작성하기란 어려운 법이다.**



```java
// 자바 8의 Collection 인터페이스에 추가된 디폴트 메서드

default boolean removeIf(Predicate<? super E> filter) {
    
    Objects.requireNonNull(filter);
    
    boolean result = false;
    
    for (Iterator<E> it = iterator(); it.hasNext();) {
        if (filter.test(it.next())) {
        it.remove();
        result = true;
        }
    }
    return result;
}
```

이 코드보다 더 범용적으로 구현하기도 어렵겠지만, 그렇다고 해서 현존하는 모든 Collection 구현체와 잘 어우러지는 것은 아니다.