# Effective Java item 34



### int 상수 대신 열거 타입을 사용하라



열거 타입은 일정 개수의 상수 값을 정의한 다음, 그 외의 값은 허용하지 않는 타입이다. 사계절, 태양계의 행성, 카드게임의 카드 종류등이 좋은 예다. 자바에서는 열거타입을 지원하기 전에는 정수 상수를 한 묶음으로 선언해서 사용하곤 했다.



- 정수 열거 패턴 기법에는 단점이 많다. 타입 안전을 보장할 방법이 없으며 표현력도 좋지 않다.
- 정수 열거 패턴을 사용한 프로그램은 깾기 쉽다. 평범한 상수를 나열한 것뿐이라 컴파일하면 그 값이 클라이언트 파일에 그대로 새겨진다. 따라서 상수의 값이 바뀌면 클라이언트도 반드시 다시 컴파일해야 한다. 다시 컴파일하지 않은 클라이언트는 실행이 되더라도 엉뚱하게 동작할 것이다.
- 정수 대신 문자열 상수를 사용하는 변형패턴도 있다. 문자열 열거 패턴이라 하는 이 변형은 더 나쁘다. 상수의 의미를 출력할 수 있다는 점은 좋지만, 경험이 부족한 프로그래머가 문자열 상수의 이름 대신 문자열 값을 그대로 하드코딩하게 만들기 때문이다.



🚩 다행히 자바는 열거 패턴의 단점을 말끔히 씻어주는 동시에 여러 장점을 안겨주는 대안을 제시했다. 바로 열거타입이다. 다음은 열거타입의 가장 단순한 형태다.

```java
public enum Apple { FUJI, PIPPIN, GRANNY_SMITH }
public enum Orange { NAVEL, TEMPLE, BLOOD }
```

겉보기엔 다른언어의 열거 타입과 비슷하지만, 보이는 것이 다가 아니다. 자바 열거 타입은 완전한 형태의 클래스라서 다른언어의 열거 타입보다 훨씬 강력하다.



- 자바 열거 타입을 뒷받침하는 아이디어는 단순하다. 열거 타입 자체는 클래스이며, 상수 하나당 자신의 인스턴스를 하나씩 만들어 public static final 필드로 공개한다. 열거 타입은 밖에서 접근할 수 있는 생성자를 제공하지 않으므로 사실상 final이다. 따라서 클라이언트가 인스턴스를 직접 생성하거나 확장할 수 없으니 열거 타입 선언으로 만들어진 인스턴스들은 딱 하나씩만 존재함이 보장된다. 다시 말해 열거 타입은 인스턴스 통제된다. 싱글턴은 원소가 하나뿐인 열거 타입이라 할 수 있고, 거꾸로 열거 타입은 싱글턴을 일반화한 형태라고 볼 수 있다.
- 열거 타입은 컴파일 타임 타입 안전성을 제공한다. Apple 열거 타입을 매개변수로 받는 메서드를 선언했다면, 건네받은 참조는 Apple의 세가지 값 중 하나임이 확실하다. 다른 타입의 값을 넘기려하면 컴파일 오류가 난다.
- 열거 타입에는 각자의 이름공간이 있어서 이름이 같은 상수도 평화롭게 공존한다. 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다. 공개되는 것이 오직 필드의 이름뿐이라, 정수 열거 패턴과 달리 상수 값이 클라이언트로 컴파일되어 각인되지 않기 때문이다. 마지막으로 열거 타입의 toString 메서드는 출력하기에 적합한 문자열을 내어준다.
- 이처럼 열거 타입은 정수 열거 패턴의 단점들을 해소해준다. 여기서 끝이 아니다 열거 타입에는 임의의 메서드나 필드를 추가할 수 있고 임의의 인터페이스를 구현하게 할 수도 있다. Object 메서드들을 높은 품질로 구현해놨고, Comparable과 Serializable을 구현했으며, 그 직렬화 형태도 웬만큼 변형을 가해도 문제없이 동작하게끔 구현해놨다.



🍧 근데 열거 타입에 메서드나 필드를 추가한다니, 어떨 때 필요한 기능일까?

각 상수와 연관된 데이터를 해당 상수 자체에 내재시키고 싶다고 해보자. Apple과 Orange를 예로 들면, 과일의 색을 알려주거나 과일 이미지를 반환하는 메서드를 추가하고 싶을 수 있다. 열거 타입에는 어떤 메서드도 추가할 수 있다. 가장 단순하게는 그저 상수 모음일 뿐인 열거타입이지만, (실제로는 클래스이므로) 고차원의 추상 개념 하나를 완벽히 표현해낼 수도 있는 것이다.



**열거 타입 상수 각각을 특정 데이터와 연결지으려면 생성자에서 데이터를 받아 인스턴스 필드에 저장하면 된다.**

열거 타입은 근본적으로 불변이라 모든 필드는 final이어야한다. 필드를 public으로 선언해도 되지만 private으로 두고 별도의 public 접근자 메서드를 두는 게 낫다.

- 열거 타입은 자신안에 정의된 상수들의 값을 배열에 담아 반환하는 정적 메서드인 values를 제공한다. 값들은 선언된 순서로 저장된다.
- 각 열거 타입의 값의 toString 메서드는 상수 이름을 문자열로 반환하므로 println과 printf로 출력하기에 안성맞춤이다.



🍓 일반 클래스와 마찬가지로 그 기능을 클라이언트에 노출해야 할 합당한 이유가 없다면 private 혹은 package-private으로 선언하라



🚀 널리 쓰이는 열거 타입은 톱레벨 클래스로 만들고, 특정 톱레벨 클래스에서만 쓰인다면 해당 클래스의 멤버 클래스로 만든다.

- 예를 들어 소수 자릿수의 반올림 모드를 뜻하는 열거 타입인 java.math.RoundingMode는 BigDecimal이 사용한다. 그런데 반올림모드는 BigDecimal과 관련 없는 영역에서도 유용한 개념이라 자바 라이브러리 설계자는 RoundingMode를 톱레벨로 올렸다. 이 개념을 많은 곳에서 사용하여 다양한 API가 더 일관된 모습을 갖출 수 있도록 장려한 것이다.



⭐ 상수가 더 다양한 기능을 제공해줬으면 할 때도 있다. 상수마다 동작이 달라져야 하는 상황도 있기 때문이다. 예컨대 사칙연산 계산기의 연산 종류를 열거 타입으로 선언하고, 실제 연산까지 열거 타입 상수가 직접 수행했으면 한다고 해보자. 먼저 switch 문을 이용해 상수의 값에 따라 분기하는 방법을 시도해보자.

```java
// 값에 따라 분기하는 열거 타입 - 이대로 만족하는가?
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;
    
    // 상수가 뜻하는 연산을 수행한다.
    public double apply(double x, double y) {
        switch(this) {
            case PLUS: return x + y;
            case MINUS: return x - y;
            case TIMES: return x * y;
            case DIVIDE: return x / y;
        }
        throw new AssertionError("알 수 없는 연산: " + this);
    }
}
```

동작은 하지만 그리 예쁘지는 않다. 마지막 throw 문은 실제로는 도달할 일이 없지만 기술적으로는 도달할 수 있기 때문에 생략하면 컴파일조차 되지 않는다. 더 나쁜 점은 깨지기 쉬운 코드라는 사실이다.

- 다행히 열거 타입은 상수별로 다르게 동작하는 코드를 구현하는 더 나은 수단을 제공한다. 열거 타입에 apply라는 추상 메서드를 선언하고 각 상수별 클래스 몸체, 즉 각 상수에서 자신에 맞게 재정의하는 방법이다. 이를 상수별 메서드 구현이라 한다.

```java
// 상수별 메서드 구현을 활용한 열거타입
public enum Operation {
    PLUS {public double apply(double x, double y) {return x + y;}},
    MINUS {public double apply(double x, double y) {return x - y;}},
    TIMES {public double apply(double x, double y) {return x * y;}},
    DIVIDE{public double apply(double x, double y) {return x / y;}};
    
    public abstract double apply(double x, double y);
}
```



- 열거 타입에는 상수 이름을 입력받아 그 이름에 해당하는 상수를 반환해주는 valueOf(String) 메서드가 자동 생성된다.
- 한편 열거 타입의 toString 메서드를 재정의하려거든, toString이 반환하는 문자열을 해당 열거 타입 상수로 변환해주는 fromString 메서드도 함께 제공하는 걸 고려해보자. 다음 코드는 모든 열거 타입에서 사용할 수 있도록 구현한 fromString이다.(단 타입 이름을 적절히 바꿔야 하고 모든 상수의 문자열 표현이 고유해야 한다.)

```java
// 열거 타입용 fromString 메서드 구현하기
private static final Map<String, Operation> stringToEnum = 
    Stream.of(values()).collect(
		toMap(Object::toString, e -> e));

// 지정한 문자열에 해당하는 Operation을 (존재한다면) 반환한다.
public static Optional<Operation> fromString(String symbol) {
    return Optional.ofNullable(stringToEnum.get(symbol));
}
```

- Operation 상수가 stirngToEnum 맵에 추가되는 시점은 열거 타입 상수 생성후 정적 필드가 초기화될 때다. 앞의 코드는 values 메서드가 반환하는 배열 대신 스트림을 사용했다. 자바 8 이전에는 빈 해시맵을 만든 다음 values가 반환한 배열을 순회하며 [문자열, 열거 타입 상수] 쌍을 맵에 추가했을 것이다. 물론 지금도 이렇게 구현해도 된다. 하지만 열거 타입 상수는 생성자에서 자신의 인스턴스를 맵에 추가할 수 없다.
- 이렇게 하려면 컴파일 오류가 나는데 만약 이 방식이 허용되었다면 런타임에 NullPointerException이 발생했을 것이다. 열거 타입의 정적 필드 중 열거 타입의 생성자에서 접근할 수 있는 것은 상수 변수뿐이다. 열거 타입 생성자가 실행되는 시점에는 정적 필드들이 아직 초기화되기 전이라, 자기자신을 추가하지 못하게 하는 제약이 꼭 필요하다. 이 제약의 특수한 예로 열거 타입 생성자에서 같은 열거 타입의 다른상수에도 접근할 수 없다.
- fromString이 Optional<Opertion>을 반환하는 점도 주의하자. 이는 주어진 문자열이 가리키는 연산이 존재하지 않을 수 있음을 클라이언트에 알리고, 그 상황을 클라이언트에서 대처하도록 한 것이다.



:notebook_with_decorative_cover: 한편 상수별 메서드 구현에는 열거 타입 상수끼리 코드를 공유하기 어렵다는 단점이 있다. 급여명세서에서 쓸 요일을 표현하는 열거 타입을 예로 생각해보자. 이 열거 타입은 직원의 기본 임금과 그날 일한 시간이 주어지면 일당을 계산해주는 메서드를 갖고 있다. 주중에 오버타임이 발생하면 잔업수당이 주어지고, 주말에는 무조건 잔업수당이 주어진다. switch문을 이용하면 case문을 날짜별로 두어 이 계산을 쉽게 수행할 수 있다.

- 하지만 switch문을 사용하는건 관리 관점에서는 위험한 코드다. 휴가와 같은 새로운 값을 열거 타입에 추가하려면 그 값을 처리하는 case문을 잊지 말고 쌍으로 넣어줘야 하는 것이다. 자칫 깜빡하는 날에는 휴가 기간에 열심히 일해도 평일과 똑같은 임금을 받게 된다.

- 상수별 메서드 구현으로 급여를 정확히 계산하는 방법은 두가지다.

  1.  잔업수당을 계산하는 코드를 모든 상수에 중복해서 넣으면 된다.
  2.  계산 코드를 평일용과 주말용으로 나눠 각각을 도우미 메서드로 작성한 다음 각 상수가 자신에게 필요한 메서드를 적절히 호출하면 된다.

  😅 하지만 두 방식 모두 코드가 장황해져 가독성이 크게 떨어지고 오류 발생 가능성이 높아진다.



**가장 깔끔한 방법은 새로운 상수를 추가할 때 잔업수당 '전략'을 선택하도록 하는 것이다.**

- 잔업수당 계산을 private 중첩 열거 타입(다음 코드의 PayType)으로 옮기고 PayrollDay 열거 타입의 생성자에서 이중 적당한 것을 선택한다. 그러면 PayrollDay 열거 타입은 잔업수당 계산을 그 전략 열거 타입에 위임하여, switch 문이나 상수별 메서드 구현이 필요 없게 된다. 이 패턴은 switch 문보다 복잡하지만 더 안전하고 유연하다.



```java
// 전략 열거 타입 패턴
enum PayrollDay {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY,
    SATURDAY(PayType.WEEKEND), SUNDAY(PayType.WEEKEND);
    
    private final PayType payType;
    
    PayrollDay(PayType payType) { this.payType = payType; }
    
    int pay(int minutesWorked, int payRate) {
        return payType.pay(minutesWorked, payRate);
    }
    
    // 전략 열거 타입
    enum PayType {
        WEEKDAY {
            int overtimePay(int minutesWorked, int payRate) {
                return minutesWorked <= MINS_PER_SHIFT ? 0 :
                (minutesWorked - MINS_PER_SHIFT) * payRate / 2;
            }
        },
        WEEKEND {
            int obertimePay(int minutesWorked, int payRate) {
                return minutesWorked * payRate / 2;
            }
        };
        
        abstract int overtimePay(int mins, int payRate);
        private static final int MINS_PER_SHIFT = 8 * 60;
        
        int pay(int minsWorked, int payRate) {
            int basePay = minsWorked * payRate;
            return basePay +overtimePay(minsWorked, payRate);
        }
    }
}
```

보다시피 switch 문은 열거 타입의 상수별 동작을 구현하는 데 적합하지 않다. 하지만 **기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 switch문이 좋은 선택이 될 수 있다.** 예컨대 Operation 열거 타입이 있는데 각 연산의 반대 연산을 반환하는 메서드가 필요하다고 해보자. 다음은 이러한 효과를 내주는 정적 메서드다.

```java
// switch 문을 이용해 원래 열거 타입에 없는 기능을 수행한다.
public static Operation inverse(Operation op) {
    switch(op) {
        case PLUS: return Operation.MINUS;
        case MINUS: return Operation.PLUS;
        case TIMES: return Operation.DIVIDE;
        case DIVIDE: return Operation.TIMES;
        
        default: throw new AssertionError("알 수 없는 연산: " + op);   
    }
}
```

추가하려는 메서드가 의미상 열거 타입에 속하지 않는다면 직접 만든 열거 타입이라도 이 방식을 적용하는 게 좋다. 종종 쓰이지만 열거 타입 안에 포함할 만큼 유용하지는 않은 경우도 마찬가지다.

- 대부분의 경우 열거 타입의 성능은 정수 상수와 별반 다르지 않다. 열거 타입을 메모리에 올리는 공간과 초기화하는 시간이 들긴 하지만 체감될 정도는 아니다.



🚀 그래서 열거 타입을 과연 언제 쓰란 말인가?

**필요한 원소를 컴파일타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자.**

**열거 타입에 정의된 상수 개수가 영원히 고정 불변일 필요는 없다.** 열거 타입은 나중에 상수가 추가돼도 바이너리 수준에서 호환되도록 설계되었다.



> 핵심정리
>
> 열거 타입은 확실히 정수 상수보다 뛰어나다. 더 읽기 쉽고 안전하고 강력하다. 대다수 열거 타입이 명시적 생성자나 메서드 없이 쓰이지만, 각 상수를 특정 데이터와 연결짓거나 상수마다 다르게 동작하게 할 때는 필요하다. 드물게는 하나의 메서드가 상수별로 다르게 동작해야 할 때도 있다. 이런 열거 타입에서는 switch 문 대신 상수별 메서드 구현을 사용하자. 열거 타입 상수 일부가 같은 동작을 공유한다면 전략 열거 타입 패턴을 사용하자.



 