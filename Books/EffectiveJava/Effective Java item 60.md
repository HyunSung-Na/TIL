# Effective Java item 60



### 정확한 답이 필요하다면 float와 double은 피하라



float와 double 타입은 과학과 공학 계산용으로 설계되었다. 넓은 방위의 수를 빠르게 정밀한 '근사치'로 계산하도록 세심하게 설계되었다. 따라서 정확한 결과가 필요할 때는 사용하면 안된다. **float 와 double 타입은 특히 금융 관련 계산과는 맞지 않는다.**



결과값을 출력하기 전에 반올림하면 해결되리라 생각할지 모르지만, 반올림을 해도 틀린 답이 나올 수 있다.

- 예를 들어 주머니에는 1달러가 있고, 선반에 10센트, 20센트, 30센트, ... 1달러짜리의 맛있는 사탕이 놓여 있다고 해보자. 10센트짜리부터 하나씩, 살 수 있을 때까지 사보자. 사탕을 몇 개나 살 수 있고, 잔돈을 얼마가 남을까? 다음은 이 문제의 답을 구하는 '어설픈 코드'다.



```java
// 오류 발생! 금융 계산에 부동소수 타입을 사용했다.

public static void main(String[] args) {
    double funds = 1.00;
    int itemsBought = 0;
    for (double price = 0.10; funds >= price; price += 0.10) {
        funds -= price;
        itemsBought++;
    }
    
    System.out.println(itemsBought + "개 구입");
    System.out.rpintln("잔돈(달러)" + funds);
}
```

프로그램을 실행해보면 사탕 3개를 구입한 후 잔돈은 0.3999999999999달러가 남았음을 알게 된다. 물론 잘못된 결과다! 이 문제를 올바르게 해결하려면 어떻게 해야 될까? **금융계산에는 BigDecimal, int, long을 사용해야 한다.**  위 코드에서 double 타입을 BigDecimal로 교체하면 사탕 4개를 구입한 후 잔돈은 드디어 0달러가 나온다.



- 하지만 BigDecimal에는 단점이 두 가지 있다. 기본 타입보다 쓰기가 훨씬 불편하고, 훨씬 느리다. 단발성 계산이라면 느리다는 문제는 무시할 수 있지만, 쓰기 불편하다는 점은 못내 아쉬울 것이다.



🚀 BigDecimal의 대안으로 int, long을 쓸 수도 있다. 그럴 경우 다룰 수 있는 값의 크기가 제한되고, 소수점을 직접 관리해야 한다. 이번 예에서는 모든 계산을 달러 대신 센트로 수행하면 이 문제가 해결된다. 다음은 이 방식으로 구현해본 코드다.

```java
// 정수 타입을 사용한 해법

public static void main(String[] args) {
    int itemsBought = 0;
    int funds = 100;
    for (int price = 10; funds >= price; price += 10) {
        funds -= price;
        itemsBought++;
    }
    System.out.println(itemsBought + "개 구입");
    System.out.println("잔돈(센트): " + funds);
}
```



> 핵심 정리
>
> 정확한 답이 필요한 계산에는 float나 double을 피하라. 소수점추적은 시스템에 맡기고, 코딩 시의 불편함이나 성능 저하를 신경 쓰지 않겠다면 BigDecimal을 사용하라. BigDecimal이 제공하는 여덟 가지 반올림모드를 이용하여 반올림을 완벽히 제어할 수 있다. 법으로 정해진 반올림을 수행해야 하는 비지니스 계산에서 아주 편리한 기능이다. 반면 성능이 중요하고 소수점을 직접 추적할 수 있고 숫자가 너무 크지 않다면 int나 long을 사용하라. 숫자를 아홉 자리 십진수로 표현할 수 있다면 int를 사용하고, 열여덟 자리 십진수로 표현할 수 있다면 long을 사용하라. 열 여덟 자리를 넘어가면 BigDecimal을 사용해야 한다.