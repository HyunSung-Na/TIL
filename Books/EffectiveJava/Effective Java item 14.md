# Effective Java item 14



### Comparable을 구현할지 고려하라



이번에는 Comparable 인터페이스의 유일무이한 메서드인 compareTo를 알아보자

compareTo는 단순 동치성 비교에 더해 순서까지 비교할 수 있으며, 제네릭하다. Compareable을 구현했다는 것은 그 클래스의 인스턴스들에는 자연적인 순서가 있음을 뜻한다. 그래서 Compareable을 구현한 객체들의 배열은 다음처럼 손쉽게 정렬할 수 있다.

```java
Arrays.sort(a);
```



검색, 극단값 계산, 자동 정렬되는 컬렉션 관리도 역시 쉽게 할 수 있다. 예컨대 다음 프로그램은 명령줄 인수들을 알파벳순으로 출력한다. String이 Comparable을 구현한 덕분이다.

```java
public class WordList {
    
    public static void main(String[] args) {
        Set<String> s = new TreeSet<>();
        Collections.addAll(s, args);
        System.out.println(s);
    }
}
```



여러분도 Comparable을 구현하여 이 인터페이스를 활용하는 수많은 제네릭 알고리즘과 컬렉션의 힘을 누릴 수 있다. 사실상 자바 플랫폼 라이브러리의 모든 값 클래스와 열거타입이 Comparable을 구현했다. 알파벳, 숫자, 연대 같이 순서가 명확한 값 클래스를 작성한다면 반드시 Comparable 인터페이스를 구현하자.

```java
public interface Comparable<T> {
    int compareTo(T t);
}
```



compareTo 메서드의 일반 규약은 equals의 규약과 비슷하다.

> 이 객체와 주어진 객체의 순서를 비교한다. 이 객체가 주어진 객체보다 작으면 음의 정수를 같으면 0을, 크면 양의 정수를 반환한다. 이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.
>
> 다음 설명에서 sgn(표현식)표기는 수학에서 말하는 부호 함수를 뜻하며, 표현식의 값이 음수, 0 양수일 때 -1, 0, 1을 반환하도록 정의했다.
>
> - Comparable을 구현한 클래스는 모든 x, y에 대해 sgn(x.compareTo(y)) == -sng(y.compareTo(x)) 여야 한다. 
> - Comparable을 구현한 클래스는 추이성을 보장해야 한다.
> - Comparable을 구현한 클래스는 모든 z에 대해 x.compareTo(y) == 0 이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z)) 다.
> - 이번 권고가 필수는 아니지만 꼭 지키는 게 좋다. (x.compareTo(y) == 0) == (x.equals(y)) 여야 한다. Comparable을 구현하고 이 권고를 지키지 않는 모든 클래스는 그 사실을 명시해야 한다. 다음과 같이 명시하면 적당할 것이다. "주의 : 이 클래스의 순서는 equals 메서드와 일관되지 않다."



모든 객체에 대해 전역 동치관계를 부여하는 equals 메서드와 달리, compareTo는 타입이 다른 객체를 신경 쓰지 않아도 된다. 타입이 다른 객체가 주어지면 간단히 ClassCastException을 던져도 되며, 대부분 그렇게 한다.



- compareTo와 equals가 일관되지 않는 BigDecimal 클래스를 예로 생각해보자. 빈 HashSet 인스턴스를 생성한 다음 new BigDecimal("1.0")과 new BigDecimal("1.00") 을 차례로 추가한다. 이 두 BigDecimal은 equals 메서드로 비교하면 서로 다르기 때문에 HashSet은 원소를 2개 갖게 된다. compareTo 메서드로 비교하면 두 BigDecimal 인스턴스가 똑같기 때문이다.
- compareTo 메서드 작성 요령은 equals와 비슷하다. 몇 가지 차이점만 주의하면 된다. Comparable은 타입을 인수로 받는 제네릭 인터페이스이므로 compareTo 메서드의 인수 타입은 컴파일 타임에 정해진다. 인수의 타입이 잘못됐다면 컴파일 자체가 되지 않는다. 또한 null을 인수로 넣어 호출하면 NullPointerException을 던져야 한다.
- comparaTo 메서드는 각 필드가 동치인지를 비교하는 게 아니라 그 순서를 비교한다. 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다. Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 한다면 비교자(Comparator)를 대신 사용한다.

```java
// 객체 참조 필드가 하나뿐인 비교자
public final class CaseInsensitiveString
    implements Comparable<CastInsensitiveString> {
    
    public int compareTo(CastInsensitiveString cis) {
        return String.CASE_INSENSITIVE_ORDER.compare(s, cis.s);
    }
    ... // 나머지 코드 생략
}
```

CaseInsensitiveString이 Comparable<CastInsensitiveString>을 구현한 것에 주목하자.



**compareTo 메서드에서 관계 연산자 <와 >를 사용하는 이전 방식은 거추장스럽고 오류를 유발하니, 이제는 추천하지 않는다.**

클래스에 핵심 필드가 여러 개라면 어느 것을 먼저 비교하느냐가 중요해진다. 가장 핵심적인 필드부터 비교해나가자. 비교 결과가 0이 아니라면, 즉 순서가 결정되면 거기서 끝이다. 그 결과를 곧장 반환하자.

다음은 아이템 10의 PhoneNumber 클래스용 compareTo 메서드를 이 방식으로 구현한 모습이다.

```java
public int compareTo(PhoneNumber pn) {
    
    int result = Short.compare(areaCode, pn.areaCode); // 가장 중요한 필드
    
    if (result == 0) {
        result = Short.compare(prefix, pn.prefix); // 두 번째로 중요한 필드
        if (result == 0)
            result = Short.compare(lineNum, pn.lineNum); // 세 번째로 중요한 필드
    }
    return result;
}
```

자바 8에서는 Comparator 인터페이스가 일련의 비교자 생성 메서드와 팀을 꾸려 메서드 연쇄 방식으로 비교자를 생성할 수 있게 되었다. 그리고 이 비교자들을 Comparable 인터페이스가 원하는 compareTo 메서드를 구현하는 데 멋지게 활용할 수 있다. 하지만 이 방식은 약간의 성능 저하가 뒤따른다. (10% 정도)

- 자바의 정적 임포트 기능을 이용하면 정적 비교자 생성 메서드들을 그 이름만으로 사용할 수 있어 코드가 훨씬 깔끔해진다.

```java
// 비교자 생성 메서드를 활용한 비교자

private static final Comparator<PhoneNumber> COMPARATOR =
    comparingInt((PhoneNumber pn) -> pn.areaCode)
    	.thenComparingInt(pn -> pn.prefix)
    	.thenComparingInt(pn -> pn.lineNum);

public int compareTo(PhoneNumber pn) {
    return COMPARATOR.compare(this, pn);
}
```

이 코드는 클래스를 초기화할 때 비교자 생성 메서드 2개를 이용해 비교자를 생성한다.

1. comparingInt는 객체 참조를 int 타입 키에 매피아는 키 추출 함수를 인수로 받아, 그 키를 기준으로 순서를 정하는 비교자를 반환하는 정적 메서드다. 앞의 예에서 comparingInt는 람다를 인수로 받으며, 이 람다는 PhoneNumber에서 추출한 지역 코드를 기준으로 전화번호의 순서를 정하는 Comparator<PhoneNumber>를 반환한다. 
2. 두 전화번호의 지역 코드가 같을 수 있으니 비교 방식을 더 다듬어야 한다. 이 일은 두 번째 비교자 생성 메서드인 thenComparingInt가 수행한다. thenCompareingInt는 Comparator의 인스턴스 메서드로 int 키 추출자 함수를 입력 받아 다시 비교자를 반환한다. thenComparingInt는 원하는 만큼 연달아 호출할 수 있다.
3. 앞의 예에서는 2개를 연달아 호출했으며, 그중 첫 번째의 키로는 프리픽스를, 두 번째의 키로는 가입자 번호를 사용했다. 이번에는 ComparingInt를 호출할 때 타입을 명시하지 않았다.



- Comparator는 수많은 보조 생성 메서드들로 중무장하고 있다. long과 double용으로는 comparingInt와 thenComparingInt의 변형 메서드를 준비했다.
- 객체 참조용 비교자 생성 메서드도 준비되어 있다. 우선, comparing이라는 정적 메서드 2개가 다중정의되어 있다. 첫 번째는 키 추출자를 받아서 그 키의 자연적 순서를 사용한다. 두 번째는 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다. 
- 또한 thenComparing이란 인스턴스 메서드가 3개 다중정의되어 있다. 첫 번째는 비교자 하나만 인수로 받아 그 비교자로 부차순서를 정한다. 두 번째는 키 추출자를 인수로 받아 그 키의 자연적 순서로 보조 순서를 정한다. 마지막 세 번째는 키 추출자 하나와 추출된 키를 비교할 비교자까지 총 2개의 인수를 받는다.



이따금 '값의 차'를 기준으로 첫 번째 값이 두 번째 값보다 작으면 음수를, 두 값이 같으면 0을, 첫 번째 값이 크면 양수를 반환하는 compareTo나 compare 메서드와 마주할 것이다.

```java
// 해시코드 값의 차를 기준으로 하는 비교자 - 추이성을 위배한다.

static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCode();
    }
};
```

이 방식은 사용하면 안 된다. 이 방식은 정수 오버플로를 일으키거나 부동소수점계산 방식에 따른 오류를 낼 수 있다. 그 대신 다음의 두 방식 중 하나를 사용하자.



```java
// 정적 compare 메서드를 활용한 비교자

static Comparator<Object> hashCodeOrder = new Comparator<>() {
    public int compare(Object o1, Object o2) {
        return Integer.compare(o1.hashCode(), o2.hashCode());
    }
};
```



```java
// 비교자 생성 메서드를 활용한 비교자

static Comparator<Object> hashCodeOrder = 
    Comparator.comparingInt(o -> o.hashCode());
```



> 핵심 정리
>
> 순서를 고려해야 하는 값 클래스를 작성한다면 꼭 Comparable 인터페이스를 구현하여, 그 인스턴스들을 쉽게 정렬하고, 검색하고, 비교 기능을 제공하는 컬렉션과 어우러지도록 해야 한다. compareTo 메서드에서 필드의 값을 비교할 때 < 와 > 연산자는 쓰지 말아야 한다. 그 대신 박싱된 기본 타입 클래스가 제공하는 정적 compare 메서드나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 사용하자.