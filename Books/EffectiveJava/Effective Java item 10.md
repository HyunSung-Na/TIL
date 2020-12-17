# Effective Java item 10



### equals는 일반 규약을 지켜 재정의하라



#### equals 메서드를 재정의 안해도 될때

equals 메서드는 재정의 하기 쉬워 보이지만 곳곳에 함정이 도사리고 있어서 조심해야 한다. 문제를 회피하는 가장 쉬운 길은 아예 재정의하지 않는 것이다. 그냥 두면 그 클래스의 인스턴스는 오직 자기 자신과만 같게 된다. 다음에서 열거한 상황 중 하나에 해당한다면 재정의하지 않는 것이 최선이다.

1. **각 인스턴스가 본질적으로 고유하다.** 값을 표현하는 게 아니라 동작하는 개체를 표현하는 클래스가 여기 해당한다. Thread가 좋은 예로, Object의 equals 메서드는 이러한 클래스에 딱 맞게 구현되었다.
2. **인스턴스의 '논리적 동치성'을 검사할 일이 없다.** 예컨대 java.util.regex.Pattern은 equals를 재정의 해서 두 Patten의 인스턴스가 같은 정규표현식을 나타내는지를 검사하는, 즉 논리적 동치성을 검사하는 방법도 있다. 하지만 설계자는 클라이언트가 이 방식을 원하지 않거나 애초에 필요하지 않다고 판단할 수도 있다. 설계자가 후자로 판단했다면 Object의 기본 equals만으로 해결된다.
3. **상위 클래스에서 재정의한 equals가 하위 클래스에도 딱 들어맞는다.** 예컨대 대부분의 Set 구현체는 AbstractSet이 구현한 equals를 상속받아 쓰고, List 구현체들은 AbstractList로부터, Map 구현체들은 AbstractMap으로부터 상속받아 그대로 쓴다.
4. **클래스가 private이거나 package-private이고 equals 메서드를 호출할 일이 없다.** 여러분이 위험을 철저히 회피하는 스타일이라 equals가 실수로라도 호출되는 걸 막고 싶다면 다음처럼 구현해두자.

```java
@Override
public boolean equals(Object o) {
    throw new AssertionError(); // 호출 금지! equals 호출하면 error 발생!
}
```



#### 그렇다면 equals를 재정의해야 할 때는 언제일까?

- 객체의 식별성이 아니라 논리적 동치성을 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의되지 않았을 때다.
  - 주로 값 클래스들이 여기 해당한다. 두 값 객체를 equals로 비교하는 프로그래머는 객체가 같은지가 아니라 값이 같은지를 알고 싶어 할 것이다.
  - 값 클래스라 해도, 값이 같은 인스턴스가 둘 이상 만들어지지 않음을 보장하는 인스턴스 통제 클래스라면 equals를 재정의하지 않아도 된다. Enum도 여기에 해당한다. 이런 클래스에서는 어차피 논리적으로 같은 인스턴스가 2개 이상 만들어지지 않으니 논리적 동치성과 객체 식별성이 사실상 똑같은 의미가 된다.
- equals 메서드를 재정의 할 때는 반드시 일반 규약을 따라야 한다. 다음은 Object 명세에 적힌 규약이다.



> equals 메서드는 동치관계를 구현하며, 다음을 만족한다.



##### 반사성

- null이 아닌 모든 참조 값 x에 대해, x.equals(x)는 true다.

  - 객체는 자기 자신과 같아야 한다는 뜻이다. 이 요건은 일부러 어기는 경우가 아니라면 만족시키지 못하기가 더 어려워 보인다.




##### 대칭성

- null이 아닌 모든 참조 값 x, y에 대해 x.equals(y) 가 true면 y.equals(x)도 true다.

  - 두 객체는 서로에 대한 동치 여부에 똑같이 답해야 한다는 뜻이다. 대소문자를 구별하지 않는 문자열을 구현한 다음 클래스를 보면 equals에서 대소문자를 무시하는 걸 볼 수 있다.

  ```java
  // 잘못된 코드 - 대칭성 위배
  
  public final class CaseInsensitiveString {
      
      private final String s;
      
      public CaseInsensitiveString(String s) {
          this.s = Objects.requireNonNull(s);
      }
      
      // 대칭성 위배!
      @Override
      public boolean equals(Object o) {
          
          if (o instanceof CaseInsensitiveString)
              return s.equalsIgnoreCase(
          			((CaseInsensitiveString) o).s);
          if (o instanceof String) // 한 방향으로만 작동한다!
              return s.equalsIgnoreCase((String) o);
          return false;
      }
      ... // 나머지 코드 생략
  }
  ```




CaseInsensitiveString의 equals는 순진하게 일반 문자열과도 비교를 시도한다. 다음처럼 CaseInsensitiveString과 일반 String 객체가 하나씩 있다고 해보자.

```java
CaseInsensitiveString cis = new CaseInsensitiveString("Polish");
String s = "polish";
```

다음과 같은 코드는 cis.equals(s) 는 true를 반환한다. 문제는 CaseInsensitiveString의 equals는 일반 String을 알고 있지만 String의 equals는 CaseInsensitiveString의 존재를 모른다는 데 있다. 따라서 s.equals(cis)는 false를 반환하여, 대칭성을 명백히 위반한다.

**equals 규약을 어기면 그 객체를 사용하는 다른객체들이 어떻게 반응할지 알 수 없다.**



##### 추이성

- null이 아닌 모든 참조 값 x, y, z에 대해, x.equals(y)가 true이고, y.equals(z)도 true이면 x.equals(z)도 true다.

  - 첫 번째 객체와 두 번째 객체가 같고, 두 번째 객체와 세 번째 객체가 같다면, 첫 번째 객체와 세 번째 객체도 같아야 한다는 뜻이다.
  - 상위 클래스에는 없는 새로운 필드를 하위 클래스에 추가하는 상황을 생각해보자. equals 비교에 영향을 주는 정보를 추가한 것이다.

  ```java
  public class Point {
      
      private final int x;
      private final int y;
      
      public Point(int x, int y) {
          this.x = x;
          this.y = y;
      }
      
      @Override
      public boolean equals(Object o) {
          if (!(o instanceof Point))
              return false;
          Point p = (Point)o;
          return p.x == x && p.y == y;
      }
      
      ... // 나머지 코드는 생략
  }
  ```

이제 이 클래스를 확장해서 점에 색상을 더해보자.

```java
public class ColorPoint extends Point {
    private final Color color;
    
    public ColorPoint(int x, int y, Color color) {
        super(x, y);
        this.color = color;
    }
    
    ...// 나머지 코드는 생략
}
```

equals 메서드는 어떻게 해야 할까? 그대로 둔다면 Point의 구현이 상속되어 색상 정보는 무시한 채 비교를 수행한다. equals 규약을 어긴 것은 아니지만, 중요한 정보를 놓치게 된다.  일단 각각의 인스턴스를 하나씩 만들어 보자

```java
Point p = new Point(1, 2);
ColorPoint cp = new ColorPoint(1, 2, Color.RED);
```

이제 p.equals(cp)는 true를 cp.equals(p)는 false를 반환한다. ColorPoint.equals가 Point와 비교할 때는 색상을 무시하도록 하면 어떨까?

```java
// 추이성 위배!
@Override
public boolean equals(Object o) {
    if (!(o instanceof Point))
        return false;
    
    // o가 일반 Point면 색상을 무시하고 비교한다.
    if (!(o instanceof ColorPoint))
        return o.equals(this);
    
    // o가 ColorPoint면 색상까지 비교한다.
    return super.equals(o) && ((ColorPoint) o).color == color;
}
```

이 방식은 대칭성은 지켜주지만, 추이성을 깨버린다.



```java
ColorPoint p1 = new ColorPoint(1, 2, Color.RED);
Point p2 = new Point(1, 2);
ColorPoint p3 = new ColorPoint(1, 2, Color.BLUE);
```

- 이제 p1.equals(p2)와 p2.equals(p3)는 true를 반환하는데, p1.equals(p3)가 false를 반환한다. 추이성에 명백히 위배한다. p1과 p2, p2와 p3 비교에서는 색상을 무시했지만 p1과 p3 비교에서는 색상까지 고려했기 때문이다.
- 또한 이 방식은 무한 재귀에 빠질 위험도 있다. Point의 또 다른 하위 클래스로 SmellPoint를 만들고, equals는 같은 방식으로 구현했다고 해보자. 그런 다음 myColorPoint.equals(mySmellPoint)를 호출하면 StackOverflowError를 일으킨다.
- 그렇다면 해법은 무엇일까? 이 현상은 모든 객체 지향 언어의 동치관계에서 나타나는 근본적인 문제다. **구체 클래스를 확장해 새로운 값을 추가하면서 equals 규약을 만족시킬 방법은 존재하지 않는다.** 객체 지향적 추상화의 이점을 포기하지 않는 한은 말이다.
- 이는 얼핏, equals 안의 instanceof 검사를 getClass 검사로 바꾸면 규약도 지키고 값도 추가하면서 구체 클래스를 상속할 수 있다는 뜻으로 들린다.

```java
// 리스코프 치환 원칙 위배!
@Override
public boolean equals(Object o) {
    if (o == null || o.getClass() != getClass())
        return false;
    Point p = (Point) o;
    return p.x == x && p.y == y;
}
```

이번 equals는 같은 구현 클래스의 객체와 비교할 때만 true를 반환한다. 괜찮아 보이지만 실제로 활용할 수는 없다. Point의 하위 클래스는 정의상 여전히 Point이므로 어디서든 Point로써 활용될 수 있어야 한다. 그런데 이 방식에서는 그렇지 못하다. 예를 들어 주어진 점이 단위 원 안에 있는지를 판별하는 메서드가 필요하다고 해보자. 다음은 이를 구현한 코드다

```java
// 단위 원 안의 모든 점을 포함하도록 unitCircle을 초기화 한다.
private static final Set<Point> unitCircle = Set.of(
			new Point(1, 0), new Point(0, 1),
			new Point(-1, 0), new Point(0, -1));

public static boolean onUnitCircle(Point p) {
    return unitCircle.contains(p);
}
```

이 기능을 구현하는 가장 빠른방법은 아니지만, 동작은 한다. 이제 값을 추가하지 않는 방식으로 Point를 확장하겠다. 만들어진 인스턴스의 개수를 보자.

```java
public class CounterPoint extends Point {
    
    private static final AtomicInteger counter = new AtomicInteger();
    
    public CounterPoint(int x, int y) {
        super(x, y);
        counter.IncrementAndGet();
    }
    
    public static int numberCreated() { return counter.get(); }
}
```

리스코프 치환 원칙에 따르면, 어떤 타입에 있어 중요한 속성이라면 그 하위 타입에서도 마찬가지로 중요하다. 따라서 그 타입의 모든 메서드가 하위 타입에서도 마찬가지로 중요하다. 이는 "Point의 하위 클래스는 정의상 여전히 Point이므로 어디서든 Point로써 활용될 수 있어야 한다."를 격식 있게 표현한 말이다.

- 그런데 CounterPoint의 인스턴스를 onUnitCircle 메서드에 넘기면 어떻게 될까? Point 클래스의 equals를 getClass를 사용해 작성했다면 onUnitCircle은 false를 반환할 것이다. CounterPoint 인스턴스의 x, y 값과는 무관하게 말이다. 원인은 컬렉션 구현체에서 주어진 원소에 담고 있는지를 확인하는 방법에 있다.
  - onUnitCircle에서 사용한 Set을 포함하여 대부분의 컬렉션은 이 작업에 equals 메서드를 이용하는데, CounterPoint의 인스턴스는 어떤 Point와도 같을 수 없기 때문이다. 반면, Point의 equals를 instanceof 기반으로 올바로 구현했다면 CounterPoint 인스턴스를 건네줘도 onUnitCircle 메서드가 제대로 동작할 것이다.
- 구체 클래스의 하위 클래스에서 값을 추가할 방법은 없지만 우회방법이 하나 있다. "상속 대신 컴포지션을 사용하라"는 아이템 18의 조언을 따라 Point를 상속하는 대신 Point를 ColorPoint의 private 필드로 두고, ColorPoint와 같은 위치의 일반 Point를 반환하는 뷰 메서드를 public으로 추가하는 식이다.

```java
// equals 규약을 지키면서 값 추가하기

public class ColorPoint {
    
    private final Point point;
    private final Color color;
    
    public ColorPoint(int x, int y, Color color) {
        point = new Point(x, y);
        this.color = Objects.requireNonNull(color);
    }
    
    /**
    * 이 ColorPoint의 Point 뷰를 반환한다.
    */
    public Point asPoint() {
        return point;
    }
    
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ColorPoint))
            return false;
        ColorPoint cp = (ColorPoint) o;
        return cp.point.equals(point) && cp.color.equals(color);
    }
    ... // 나머지 코드 생략
}
```

자바 라이브러리에도 구체 클래스를 확장해 값을 추가한 클래스가 종종 있다.

한 가지 예로 java.sql.Timestamp는 java.util.Date를 확장한 후 nanoseconds 필드를 추가했다. 그 결과로 Timestamp의 equals는 대칭성을 위배하며, Date 객체와 한 컬렉션에 넣거나 서로 섞어 사용하면 엉뚱하게 동작할 수 있다.



> 추상클래스의 하위 클래스에서라면 equals 규약을 지키면서도 값을 추가할 수 있다. "태그 달린 클래스보다는 클래스 계층구조를 활용하라"는 아이템 23의 조언을 따르는 클래스 계층구조에서는 아주 중요한 사실이다. 예컨대 아무런 값을 갖지 않는 추상 클래스인 Shape를 위에 두고, 이를 확장하여 radius 필드를 추가한 Circle 클래스와 length와 width 필드를 추가한 Rectangle 클래스를 만들 수 있다. 상위 클래스를 직접 인스턴스로 만드는 게 불가능하다면 지금까지 이야기한 문제들은 일어나지 않는다.

##### 일관성

- null이 아닌 모든 참조 값 x, y에 대해, x.equals(y)를 반복해서 호출하면 항상 true를 반환하거나 항상 false를 반환한다.
- 두 객체가 같다면 (어느 하나 혹은 두 객체 모두가 수정되지 않는 한) 앞으로도 영원히 같아야 한다는 뜻이다.
- 가변 객체는 비교 시점에 따라 서로 다를 수도 혹은 같을 수도 있는 반면, 불변 객체는 한번 다르면 끝까지 달라야 한다. 클래스를 작성할 때는 불변 클래스로 만드는 게 나을지를 심사숙고하자.
- 클래스가 불변이든 가변이든 **equals의 판단에 신뢰할 수 없는 자원이 끼어들게 해서는 안된다.** 이 제약을 어기면 일관성 조건을 만족시키기가 아주 어렵다.



##### null - 아님

- null이 아닌 모든 참조 값 x에 대해, x.equals(null)은 false다.
- 수많은 클래스가 다음 코드처럼 입력이 null인지를 확인해 자신을 보호한다.

```java
// 명시적 null 검사 - 필요없다!
@Override
public boolean equals(Object o) {
    if (o == null)
        return false;
    ...
}
```

이런 검사는 필요치 않다. 동치성 검사하려면 equals는 건네받은 객체를 적절히 형변환한 후 필수 필드들의 값을 알아내야 한다. 그러려면 형변환에 앞서 instanceof 연산자로 입력 매개변수가 올바른타입인지 검사해야 한다.

```java
// 묵시적 null 검사 - 이쪽이 낫다.
@Override
public boolean equals(Object o) {
    if (!(o instanceof MyType))
        return false;
    MyType mt = (MyType) o;
    ...
}
```

equals가 타입을 확인하지 않으면 잘못된 타입이 인수로 주어졌을 때 ClassCastException을 던져서 일반 규약을 위배하게 된다. 그런데 instanceof는 첫 번째 피연산자가 null이면 false를 반환한다. 따라서 입력이 null이면 타입 확인 단계에서 false를 반환하기 때문에 null 검사를 명시적으로 하지 않아도 된다.



##### Equals 메서드 구현 방법

1.  **== 연산자를 사용해 입력이 자기 자신의 참조인지 확인한다.** 자기 자신이면 true를 반환한다. 이는 단순한 성능 최적화용으로, 비교 작업이 복잡한 상황일 때 값어치를 할 것이다.
2. **instanceof 연산자로 입력이 올바른타입인지 확인한다.** 그렇지 않다면 false를 반환한다. 이때의 올바른타입은 equals가 정의된 클래스인 것이 보통이지만, 가끔은 그 클래스가 구현한 특정 인터페이스가 될 수도 있다. 어떤 인터페이스는 자신을 구현한 클래스끼리도 비교할 수 있도록 equals 규약을 수정하기도 한다. 이런 인터페이스를 구현한 클래스라면 equals에서 해당 인터페이스를 사용해야 한다. Set, List, Map, Map.Entry 등의 컬렉션 인터페이스들이 여기 해당한다.
3. **입력을 올바른타입으로 형변환한다.** 앞서 2번에서 instanceof 검사를 했기 때문에 이 단계는 100% 성공한다.
4. **입력 객체와 자기 자신의 대응되는 '핵심' 필드들이 모두 일치하는지 하나씩 검사한다.** 모든 필드가 일치하면 true를, 하나라도 다르면 false를 반환한다. 2단계에서 인터페이스를 사용했다면 입력의 필드 값을 가져올 때도 그 인터페이스의 메서드를 사용해야 한다. 타입이 클래스라면 (접근 권한에 따라) 해당 필드에 직접 접근할 수도 있다.



- **equals를 다 구현했다면 세 가지만 자문해보자. 대칭적인가? 추이성이 있는가? 일관적인가?**

자문에서 끝내지 말고 단위 테스트를 작성해 돌려보자. 단, equals 메서드를 AutoValue를 이용해 작성했다면 테스트를 생략해도 안심할 수 있다.

다음은 이상의 비법에 따라 작성해본 PhoneNumber 클래스용 equals 메서드다.

```java
// 전형적인 equals 메서드의 예
public final class PhoneNumber {
    private final short areaCode, prefix, lineNum;
    
    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = rangeCheck(areaCode, 999, "지역코드");
        this.prefix = rangeCheck(prefix, 999, "프리픽스");
        this.lineNum = rangeCheck(lineNum, 9999, "가입자 번호");
    }
    
    private static short rangeCheck(int val, int max, String arg) {
        if (val < 0 || val > max)
            throw new IllegalArgumentException(arg + ": " + val);
        return (short) val;
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof PhoneNumber))
            return false;
        PhoneNumber pn = (PhoneNumber)o;
        return pn.lineNum == lineNum && pn.prefix == prefix
            && pn.areaCode == areaCode;
    }
    ...// 나머지 코드는 생략
}
```



#### 마지막 주의사항

- **equals를 재정의할 땐 hashCode도 반드시 재정의하자**

- **너무 복잡하게 해결하려 들지 말자.** 필드들의 동치성만 검사해도 equals 규약을 어렵지 않게 지킬 수 있다. 오히려 너무 공격적으로 파고들다가 문제를 일으키기도 한다. 일반적으로 별칭은 비교하지 ㅇ낳는 게 좋다.
- Object 외의 타입을 매개변수로 받는 equals 메서드는 선언하지 말자. 많은 프로그래머가 equals를 다음과 같이 작성해놓고 문제의 원인을 찾아 헤맨다.



```java
// 잘못된 예 - 입력 타입은 반드시 Objcet여야 한다!
public boolean equals(MyClass o) {
    ...
}
```

이 메서드는 Object.equals를 재정의한게 아니다. 입력 타입이 Object가 아니므로 재정의가 아니라 다중정의한 것이다. 기본 equals를 그대로 둔 채로 추가한 것일지라도, 이처럼 '타입을 구체적으로 명시한' equals는 오히려 해가 된다.



- equals를 작성하고 테스트하는 일은 지루하고 이를 테스트하는 코드도 항상 뻔하다. 다행히 이 작업을 대신해줄 오픈소스가 있으니, 바로 구글이 만든 AutoValue 프로엠워크다. 클래스에 애너테이션 하나만 추가하면 AutoValue가 이 메서드들을 알아서 작성해주며, 여러분이 직접 작성하는 것과 근본적으로 똑같은 코드를 만들어 줄 것이다.



> 핵심정리
>
> 꼭 필요한 경우가 아니면 equals를 재정의하지 말자. 많은 경우에 Object의 equals가 여러분이 원하는 비교를 정확히 수행해준다. 재정의해야 할 때는 그 클래스의 핵심 필드 모두를 빠짐없이, 다섯 가지 규약을 확실히 지켜가며 비교해야 한다.



