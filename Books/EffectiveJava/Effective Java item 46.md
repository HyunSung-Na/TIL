# Effective Java item 46



### 스트림에서는 부작용 없는 함수를 사용하라



스트림은 처음 봐서는 이해하기 어려울 수 있다. 원하는 작업을 스트림 파이프 라인으로 표현하는 것 조차 어려울지 모른다.



- 스트림 패러다임의 핵심은 계산을 일련의 변환으로 재구성하는 부분이다. 이때 각 변환 단계는 가능한 한 이전 단계의 결과를 받아 처리하는 순수 함수여야 한다. 순수 함수란 오직 입력만이 결과에 영향을 주는 함수를 말한다. 이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 **Side Effect**가 없어야 한다.



다음은 주위에서 종종 볼 수 있는 스트림 코드로, 텍스트 파일에서 단어별 수를 세어 빈도표로 만드는 일을 한다.

```java
Map<String, Long> freq = new HashMap<>();
try (Stream<String> words = new Scanner(file).tokens()) {
    words.forEach(word -> {
        freq.merge(word.toLowerCase(), 1L, Long::sum);
    });
}
```

무엇이 문제인지 보이는가? 스트림, 람다, 메서드 참조를 사용했고, 결과도 올바르다. 하지만 절대 스트림 코드라 할 수 없다. 스트림 코드를 가장한 반복적 코드다. 스트림 API의 이점을 살리지 못하여 같은 기능의 반복적 코드보다 길고, 읽기 어렵고, 유지보수에도 좋지 않다. 이 코드의 모든 작업이 종단 연산인 forEach에서 일어나는데, 이때 외부 상태를 수정하는 람다를 실행하면서 문제가 생긴다. forEach가 그저 스트림이 수행한 연산 결과를 보여주는 일 이상을 하는 것을 보니 나쁜 냄새가 난다. 이제 올바르게 작성한 모습을 살펴보자.

```java
// 스트림을 제대로 활용해 빈도표를 초기화 한다.
Map<String, Long> freq;
try (Stream<String> words = new Scanner(file).tokens()) {
    freq = words
        .collect(groupingBy(String::toLowerCase, counting()));
}
```

앞서와 같은 일을 하지만, 이번엔 스트림 API를 제대로 사용했다. 그뿐만 아니라 짧고 명확하다. 자바 프로그래머라면 forEach 반복문을 사용할 줄 알 텐데, forEach 종단 연산과 비슷하게 생겼다. 하지만 forEach 연산은 종단 연산 중 기능이 가장 적고 가장 '덜' 스트림답다. 대놓고 반복적이라서 병렬화할 수도 없다. **forEach 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산하는 데는 쓰지말자.** 물론 가끔은 스트림 계산 결과를 기존 컬렉션에 추가하는 등의 다른 용도로도 쓸 수 있다.



- 이 코드는 collector를 사용하는데, 스트림을 사용하려면 꼭 배워야하는 새로운 개념이다. Collectors 클래스는 메서드를 무려 39개나 가지고 있고, 그중에는 타입 매개변수가 5개나 되는 것도 있다. 
- collector를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다. 수집기는 총 세 가지로, toList(), toSet(), toCollection(collectionFactory)가  그 주인공이다. 이들은 차례로 리스트, 집합, 프로그래머가 지정한 컬렉션 타입을 반환한다.



지금까지 배운 지식을 활용해 빈도표에서 가장 흔한 단어 10개르 뽑아내는 스트림 파이프라인을 작성해보자.

```java
// 빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인

List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
```

이 코드에서 어려운 부분은 sorted에 넘긴 비교자, 즉 comparing(freq::get).reversed()뿐이다. comparing 메서드는 키 추출 함수를 받는 비교자 생성 메서드다. 그리고 한정적 메서드 참조이자, 여기서 키 추출 함수로 쓰인 freq::get 은 입력 받은 단어를 빈도표에서 찾아 그 빈도를 반환한다. 그런 다음 가장 흔한 단어가 위로 오도록 역순으로 정렬한다. 여기까지 왔으면 단어 10개를 뽑아 리스트에 담는 일은 식은 죽 먹기다.



- Collectors의 나머지 메서드들도 알아보자. 이 중 대부분은 스트림을 맵으로 취합하는 기능으로, 진짜 컬렉션에 취합하는 것보다 훨씬 복잡하다. 스트림의 각 원소는 키 하나와 값 하나에 연관되어 있다. 그리고 다수의 스트림 원소가 같은 키에 연관될 수 있다.



```java
// toMap 수집기를 사용하여 문자열을 열거 타입 상수에 매핑한다.
private static final Map<String, Operation> stringToEnum =
    Stream.of(values()).collect(
		toMap(Object::toString, e -> e));
```

🚩 이 간단한 toMap 형태는 스트림의 각 원소가 고유한 키에 매핑되어 있을 때 적합하다.



더 복잡한 형태의 toMap이나 groupingBy는 이런 충돌을 다루는 다양한 전략을 제공한다. 예컨대 toMap에 키 매퍼와 값 매퍼는 물론 병합 함수까지 제공할 수 있다.



- 인수 3개를 받는 toMap은 어떤 키와 그 키에 연관된 원소들 중 하나를 골라 연관 짓는 맵을 만들 때 유용하다. 예컨대 다양한 음악가의 앨범들을 담은 스트림을 가지고 음악가와 그 음악가의 베스트 앨범을 연관 짓고 싶다고 해보자.

```java
// 각 키와 해당 키의 특정 원소를 연관 짓는 맵을 생성하는 수집기
Map<Artist, Album> topHits = albums.collect(
	toMap(Album::artist, a -> a, maxBy(compareing(Album::sales))));
```

여기서 비교자로는 BinaryOperator에서 정적 임포트한 maxBy라는 정적 팩터리 메서드를 사용했다. maxBy는 Comparator<T>를 입력받아 BinaryOperator<T>를 돌려준다. 이 경우 비교자 생성 메서드인 comparing이 maxBy에 넘겨줄 비교자를 반환하는데, 자신의 키 추출 함수로는 Album::sales를 받았다. 

말로 풀어보자면 "앨범 스트림을 맵으로 바꾸는데 이 맵은 각 음악가와 그 음악가의 베스트 앨범을 짝지은 것이다"는 이야기다.



- 이번에는 Collectors가 제공하는 또 다른 메서드인 groupingBy를 알아보자. 이 메서드는 입력으로 분류 함수를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다.

```java
// 알파뱃화한 단어를 알파뱃화 결과가 같은 단어들의 리스트로 매핑하는 맵
words.collect(groupingBy(word -> alphabetize(word)))
```

groupingBy가 반환하는 수집기가 리스트 외의 값을 갖는 맵을 생성하게 하려면, 분류 함수와 함께 다운스트림 수집기도 명시해야 한다. 다운 스트림 수집기의 역할을 해당 카테고리의 모든 원소를 담은 스트림으로부터 값을 생성하는 일이다.

```java
// 유연하게 컬렉션을 값으로 갖는 맵을 생성

Map<String, Long> freq = words
    .collect(groupingBy(String::toLowerCase, counting()));
```

groupingBy의 세 번째 버전은 다운 스트림수집기에 더해 맵 팩터리도 지정 할 수 있게 해준다. 참고로 이 메서드는 점층적 인수 목록 패턴에 어긋난다. 즉, mapFactory 매개변수가 downStream 매개변수 보다 앞에 놓인다.



- counting 메서드가 반환하는 수집기는 다운스트림 수집기 전용이다. Stream의 count 메서드를 직접 사용하여 같은 기능을 수행할 수 있으니 collect(counting()) 형태로 사용할 일은 전혀 없다. Collections에는 이런 속성의 메서드가 16개나 더 있다. 그 중 9개는 이름이 summing, averaging, summarizing으로 시작하며, 각각 int, long, double 스트림용으로 하나씩 존재한다.
- 이 외에도 다중정의된 reducing 메서드들, filtering, flatMapping, collectingAndThen 메서드, joining 등이 있다.



> 핵심 정리
>
> 스트림 파이프라인 프로그래밍의 핵심은 부작용 없는 함수 객체에 있다. 스트림뿐 아니라 스트림 관련 객체에 건네지는 모든 함수 객체가 부작용이 없어야 한다. 종단 연산 중 forEach는 스트림이 수행한 계산 결과를 보고할 때만 이용해야 한다. 계산 자체에는 이용하지 말자. 스트림을 올바로 사용하려면 수집기를 잘 알아둬야 한다. 가장 중요한 수집기 팩터리는 toList, toSet, toMap, groupingBy, joining이다.





