# Effective Java item 58



### 전통적인 for 문보다는 for-each 문을 사용하라



아이템 45에서 이야기했듯, 스트림이 제격인 작업이 있고 반복이 제격인 작업이 있다. 기존에 for문을 사용했을 때 몇가지 문제가 있다.

- while 문보다는 낫지만 가장 좋은 방법은 앙니다. 반복자와 인덱스 변수는 모두 코드를 지저분하게 할 뿐 우리에게 진짜 필요한 건 원소들뿐이다. 더구다나 이처럼 쓰이는 요소 종류가 늘어나면 오류가 생길 가능성이 높아진다.
- 혹시라도 잘못된 변수를 사용했을 때 컴파일러가 잡아주리라는 보장도 없다. 마지막으로 컬렉션이나 배열이냐에 따라 코드 형태가 상당히 달라지므로 주의해야 한다.



🚩 이상의 문제는 for-each 문을 사용하면 모두 해결된다. 반복자와 인덱스 변수를 사용하지 않으니 코드가 깔끔해지고 오류가 날 일도 없다. 하나의 관용구로 컬렉션과 배열을 모두 처리할 수 있어서 어떤 컨테이너를 다루는지는 신경 쓰지 않아도 된다.



```java
// 컬렉션과 배열을 순회하는 올바른 관용구

for (Element e : elements) {
    ... // e로 무언가를 한다.
}
```

여기서 콜론은 '안의' 라는 읽으면 된다. 반복 대상이 컬렉션이든 배열이든, for-each 문을 사용해도 속도는 그대로다. for-each 문이 만들어내는 코드는 사람이 손으로 최적한 것과 사실상 같기 때문이다.



- 컬렉션을 중첩해 순회해야 한다면 for-each 문의 이점이 더욱 커진다. 다음 코드를 보자

```java
// 버그를 찾아보자.

enum Suit { CLUB, DIAMOND, HEART, SPADE }
enum Rank { ACE, DEUCE, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, 
           NINE, TEN, JACK, QUEEN, KING }
...

static Collection<Suit> suits = Arrays.asList(Suit.values());
static Collection<Rank> ranks = Arrays.asList(Rank.values());

List<Card> deck = new ArrayList<>();
for (Iterator<Suit> i = suits.iterator(); i.hasNext();)
    for (Iterator<Rank> j = ranks.iterator(); j.hasNext(); )
        deck.add(new Card(i.next(), j.next()));
```

여기서 문제는 바깥 컬렉션의 반복자에서 next 메서드가 너무 많이 불린다는 것이다. 마지막 줄의 i.next()를 주목하자. 이 next()는 '숫자(Suit) 하나당' 한 번씩만 불러야 하는데, 안쪽 반복문에서 호출되는 바람에 '카드(Rank) 하나당' 한 번씩만 불리고 있다. 그래서 숫자가 바닥나면 반복문에서 NoSuchElementException을 던진다.

- 정말 운이 나빠서 바깥 컬렉션의 크기가 안쪽 컬렉션 크기의 배수라면 이 반복문은 예외를 던지지 않고 종료한다.
- for-each 문을 중첩하는 것으로 이 문제는 간단히 해결된다. 코드도 놀랄 만큼 간결해진다.

```java
// 컬렉션이나 배열의 중첩 반복을 위한 권장 관용구

for (Suit suit : suits)
    for (Rank rank : ranks)
        deck.add(new Card(suit, rank));
```

하지만 안타깝게도 for-each 문을 사용할 수 없는 상황이 세 가지 존재한다.

- **파괴적인 필터링** - 컬렉션을 순회하면서 선택된 원소를 제거해야 한다면 반복자의 remove 메서드를 호출해야 한다. 자바 8부터는 Collection의 removeIf 메서드를 사용해 컬렉션을 명시적으로 순회하는 일을 피할 수 있다.
- **변형** - 리스트나 배열을 순회하면서 그 원소의 값 일부 혹은 전체를 교체해야 한다면 리스트의 반복자나 배열의 인덱스를 사용해야 한다.
- **병렬 반복** - 여러 컬렉션을 병렬로 순회해야 한다면 각각의 반복자와 인덱스 변수를 사용해 엄격하고 명시적으로 제어해야 한다.



세 가지 상황 중 하나의 속할 때는 일반적인 for 문을 사용하되 이번 아이템에서 언급한 문제들을 경계하기 바란다.

for-each 문은 컬렉션과 배열은 물론 Iterable 인터페이스를 구현한 객체라면 무엇이든 순회할 수 있다. Iterable 인터페이스 다음과 같이 메서드가 단 하나뿐이다.

```java
public interface Iterable<E> {
    // 이 객체의 원소들을 순회하는 반복자를 반환한다.
    Iterator<E> iterator();
}
```

Iterable을 처음부터 직접 구현하기는 까다롭지만, 원소들의 묶음을 표현하는 타입을 작성해야 한다면 Iterable을 구현하는 쪽으로 고민해보기 바란다.



> 핵심 정리
>
> 전통적인 for 문과 비교했을 때 for-each 문은 명료하고, 유연하고, 버그를 예방해준다. 성능 저하도 없다. 가능한 모든 곳에서 for 문이 아닌 for - each 문을 사용하자.