# Effective Java Item 2



### 생성자에 매개변수가 많다면 빌더를 고려하라



정적 팩토리와 생성자에는 똑같은 제약이 하나 있다. 선택적 매개변수가 많을 때 적절히 대응하기 어렵다는 점이다. 

- 점층적 생성자 패턴을 사용
  - 필수 매개변수만 받는 생성자, 필수 매개변수와 선택 매개변수 1개를 받는 생성자....
  - 이런 형태로 선택 매개변수를 전부 다 받는 생성자까지 늘려가는 방식

하지만 점층적 생성자 패턴은 확장을 하기가 어렵고, 코드가 너무 길어진다는 단점이 있다.

또한 생성자로 객체를 생성할 때 사용자가 설정하길 원치 않는 매개변수까지 포함하기 쉬워진다. 

**점층적 생성자 패턴은 쓸 수는 있지만, 매개변수 개수가 많아지면 클라이언트 코드를 작성하거나 읽기 어렵다.**

코드를 읽을 때 각 값의 의미가 무엇인지 헷갈릴 것이고, 매개변수가 연달아 늘어서 있으면 찾기 어려운 버그로 이어질 수 도 있다. 또한 클라이언트가 실수로 매개변수의 순서를 바꿔 건네줘도 컴파일러는 알아채지 못하고, 결국 런타임에 엉뚱한 동작을 하게 된다.



- 자바빈즈 패턴
  - 매개변수가 없는 생성자로 객체를 만든후 세터메서드들을 호출해 원하는 매개변수의 값을 설정하는 방식



```java
public class NutritionFacts {
    // 매개변수들은 (기본값이 있다면) 기본값으로 초기화된다.
    private int servingSize = -1;
    private int servings = -1;
    private int calories = 0;
    private int fat = 0;
    private int sodium = 0;
    private int cabohydrate = 0;
    
    public NutritionFacts() { }
    
    // 세터메서드 설정
    .....
}
```



- 자바빈즈는 심각한 단점을 지니고 있다. 자바빈즈패턴에서는 객체 하나를 만들려면 메서드를 여러 개 호출해야 하고, 객체가 완전히 생성되기 전까지는 일관성이 무너진 상태에 놓이게 된다.
- 점층적 생성자 패턴에서는 매개변수들이 유효한지를 생성자에서만 확인하면 일관성을 유지할 수 있었는데, 그 장치가 완전히 사라진 것이다.
- 이처럼 일관성이 무너지는 문제 때문에 자바빈즈 패턴에서는 클래스를 불변으로 만들 수 없으며 스레드 안전성을 얻으려면 프로그래머가 추가 작업을 해줘야 한다.





#### 빌더패턴

클라이언트는 필요한 객체를 직접 만드는 대신, 필수 매개변수만으로 생성자를 호출해 빌더 객체를 얻는다. 그런 다음 빌더 객체가 제공하는 일종의 세터 메서드들로 원하는 선택 매개변수들을 설정한다. 마지막으로 매개변수가 없는 build 메서드를 호출해 드디어 우리에게 필요한 (보통은 불변인) 객체를 얻는다. 빌더는 생성할 클래스 안에 정적 멤버 클래스로 만들어 두는 게 보통이다.



```java
public class NutritionFacts {
     private int servingSize;
    private int servings;
    private int calories;
    private int fat;
    private int sodium;
    private int cabohydrate;
    
    public static class Builder {
        // 필수 매개변수
        private final int servingSize;
        private final int servings;
        
        // 선택 매개변수 - 기본값으로 초기화한다.
        private int calories = 0;
    	private int fat = 0;
    	private int sodium = 0;
    	private int cabohydrate = 0;
        
        public Builder(int servingSize, int servings) {
            this.servingSize = servingSize;
            this.servings = servings;
        }
        
        public Builder calories(int val) {
            calories = val;
            return this;
        }
        
        public Builder fat(int val) {
            fat = val;
            return this;
        }
        
        public Builder sodium(int val) {
            sodium = val;
            return this;
        }
        
        public Builder carbohydrate(int val) {
            carbohydrate = val;
            return this;
        }
    }
    
    private NutritionFacts(Builder builder) {
        servingSize = builder.servingSize;
        servings = builder.servings;
        calories = builder.calories;
        fat = builder.fat;
        sodium = builder.sodium;
        carbohydrate = builder.cabohydrate;
    }
}
```



클래스는 불변이며, 모든 매개변수의 기본값들을 한곳에 모아뒀다. 빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출 할 수 있다. 이런 방식을 메서드 호출이 흐르듯 연결된다는 뜻으로 플루언트 API 혹은 메서드 연쇄라 한다.

**빌더 패턴은 (파이썬과 스칼라에 있는) 명명된 선택적 매개변수를 흉내 낸 것이다.**

- 잘못된 매개변수를 최대한 일찍 발견하려면 빌더의 생성자와 메서드에서 입력 매개변수를 검사하고, build 메서드가 호출하는 생성자에서 여러 매개변수에 걸친 불변식을 검사하자
- 공격에 대비해 이런 불변식을 보장하려면 빌더로부터 매개변수를 복사한 후 해당 객체 필드들도 검사해야 한다.
- 검사해서 잘못된 점을 발견하면 어떤 매개변수가 잘못되었는지를 자세히 알려주는 메시지를 담아 IllegalArgumentException을 던지면 된다.



> 불변은 어떠한 변경도 허용하지 않는다는 뜻으로, 주로 변경을 허용하ㅡㄴ 가변 객체와 구분하는 용도로 쓰인다. 대표적으로 String 객체는 한번 만들어지면 절대 값을 바꿀 수 없는 불변 객체다.
>
> 불변식은 프로그램이 실행되는 동안, 혹은 정해진 기간 동안 반드시 만족해야 하는 조건을 말한다. 다시 말해 변경을 허용할 수는 있으나 주어진 조건 내에서만 허용한다는 뜻이다. 예컨대 리스트의 크기는 반드시 0 이상이어야 하니, 만약 한순간이라도 음수 값이 된다면 불변식이 깨진 것이다. 
>
> 가변객체에도 불변식은 존재할 수 있으며, 넓게 보면 불변은 불변식의 극단적인 예라 할 수 있다.



빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다. 각 계층의 클래스에 관련 빌더를 멤버로 정의하자.

추상 클래스는 추상 빌더를, 구체 클래스는 구체 빌더를 갖게 한다.

```java
public abstract class Pizza {
    public enum Topping { HAM, MUSHROOM, ONION, PEPPER, SAUSAGE }
    
    final Set<Topping> toppings;
    
    abstract static class Builder<T extends Builder<T>> {
        
        EnumSet<Topping> toppings = EnumSet.noneOf(Topping.class);
        public T addTopping(Topping topping) {
            toppings.add(Objects.requireNonNull(topping));
            return self();
        }
        
        abstract Pizza build();
        
        // 하위 클래스는 이 메서드를 재정의(overriding)하여
        // "this"를 반환하도록 해야 한다.
        protected abstract T self();
    }
    
    Pizza(Builder<?> builder) {
        toppings = builder.toppings.clone(); // 아이템 50 참조
    }
}
```



Pizza.Builder 클래스는 재귀적 타입 한정(아이템 30)을 이용하는 제네릭 타입이다. 여기에 추상 메서드인 self를 더해 하위 클래스에서는 형변환하지 않고도 메서드 연쇄를 지원할 수 있다. self 타입이 없는 자바를 위한 이 우회 방법을 시뮬레이트한 셀프 타입 관용구라 한다.



- 하위 클래스의 메서드가 상위 클래스의 메서드가 정의한 반환 타입이 아닌, 그 하위 타입을 반환하는 기능을 공변반환 타이핑이라 한다. 이 기능을 이용하면 클라이언트가 형변환에 신경 쓰지 않고도 빌더를 사용할 수 있다.
- 생성자로는 누릴 수 없는 사소한 이점으로, 빌더를 이용하면 가변인수 매개변수를 여러 개 사용할 수 있다. 각각을 적절한 메서드로 나눠 선언하면 된다. 아니면 메서드를 여러 번 호출하도록 하고 각 호출 때 넘겨진 매개변수들을 하나의 필드로 모을 수도 있다.
- 빌더 패턴은 상당히 유연하다. 빌더 하나로 여러 객체르 ㄹ순회하면서 만들 수 있고, 빌더에 넘기는 매개변수에 따라 다른객체를 만들 수도 있다. 객체마다 부여되는 일련번호와 같은 특정 필드는 빌더가 알아서 채우도록 할 수도 있다.
- 단점
  - 객체를 만들려면, 그에 앞서 빌더부터 만들어야 한다. 빌더 생성 비용이 크지는 않지만 성능에 민감한 상황에서는 문제가 될 수 있다.
  - 점층적 생성자 패턴보다는 코드가 장황해서 매개변수가 4개 이상은 되어야 값어치를 한다. 하지만 API는 시간이 지날수록 매개변수가 많아지는 경향이 있음을 명심하자



> 핵심 정리
>
> 생성자나 정적 팩토리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다. 매개변수 중 다수가 필수가 아니거나 같은 타입이면 특히 더 그렇다. 빌더는 점층적 생성자보다 클라이언트 코드를 읽고 쓰기가 훨씬 간결하고, 자바빈즈보다 훨씬 안전하다.



