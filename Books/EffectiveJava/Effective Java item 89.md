# Effective Java item 89



### 인스턴스 수를 통제해야 한다면 readResolve보다는 열거 타입을 사용하라



아이템 3에서 싱글턴 패턴을 설명하며 다음 예를 보여주었다. 이 클래스는 바깥에서 생성자를 호출하지 못하게 막는 방식으로 인스턴스가 오직 하나만 만들어짐을 보장했다.



```java
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    
    public void leaveTheBuilding() { ,,, }
}
```

아이템 3에서 이야기 했듯 이 클래스는 그 선언에 implements Serializable을 추가하는 순간 더 이상 싱글턴이 아니게 된다. 기본 직렬화를 쓰지 않더라도, 명시적인 readObject를 제공하더라도 소용없다. 이 클래스가 초기화 될때 만들어진 인스턴스와는 별개인 인스턴스를 반환하게 된다.



- readResolve 기능을 이용하면 readObject가 만들어낸 인스턴스를 다른것으로 대체할 수 있다. 역직렬화한 객체의 클래스가 readResolve 메서드를 적절히 정의해뒀다면, 역직렬화 후 새로 생성된 객체를 인수로 이 메서드가 호출되고, 이 메서드가 반환한 객체 참조가 새로 생성된 객체를 대신해 반환된다. 대부분의 경우 이때 새로 생성된 객체의 참조는 유지하지 않으므로 바로 가비지 컬렉션 대상이 된다.



앞의 Elvis 클래스가 Serializable을 구현한다면 다음의 readResolve 메서드를 추가해 싱글턴이라는 속성을 유지할 수 있다.

```java
// 인스턴스 통제를 위한 readResolve - 개선의 여지가 있다.
private Object readResolve() {
    // 진짜 Elvis를 반환하고, 가짜 Elvis는 가비지 컬렉터에 맡긴다.
    return INSTANCE;
}
```

이 메서드는 역직렬화한 객체는 무시하고 클래스 초기화 때 만들어진 Elvis 인스턴스를 반환한다. 따라서 Elvis 인스턴스의 직렬화 형태는 아무런 실 데이터를 가질 이유가 없으니 모든 인스턴스 필드를 transient로 선언해야 한다.



- **사실, readResolve를 인스턴스 통제 목적으로 사용한다면 객체 참조 타입 인스턴스 필드는 모두 transient로 선언해야 한다.** 그렇지 않으면 MutablePeriod 공격과 비슷한 방식으로 readResolve 메서드가 수행되기 전에 역직렬화된 객체의 참조를 공격할 여지가 남는다.



다음의 잘못된 싱글턴을 통해 구체적으로 살펴보자.

```java
// 잘못된 싱글턴 - transient가 아닌 참조 필드를 가지고 있다!

public class Elvis implements Serializable {
    public static final Elvis INSTANCE = new Elvis();
    private Elvis() {  }
    
    private String[] favoriteSongs =
    	{"Hound Dog", "Heartbreak Hotel" };
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
    
    private Object readResolve() {
        return INSTANCE;
    }
}
```

다음은 도둑클래스로 readResolve 메서드가 수행되기 전에 도둑의 인스턴스 필드에 역직렬화 도중인 싱글턴의 참조가 담겨지게 되어 이 참조를 이용해 공격할 여지가 남게 된다.

```java
// 도둑 클래스

public class Elvis implements Serializable {
    static Elvis impersonator;
    private Elvis payload;
    
    private Object readResolve() {
        // resolve 되기 전의 Elvis 인스턴스의 참조를 저장한다.
        impersonator = payload;
        
        // favoriteSongs 필드에 맞는 타입의 객체를 반환한다.
        return new String[] { "A Fool Such as I"};
    }
    
    private static final long serialVersionUID = 0;
}
```

- readResolve 메서드를 사용해 '순간적으로' 만들어진 역직렬화된 인스턴스에 접근하지 못하게 하는 방법은 깨지기 쉽고 신경을 많이 써야 하는 작업이다.
- 직렬화 가능한 인스턴스 통제 클래스를 열거 타입을 이용해 구현하면 선언한 상수 외의 다른객체는 존재하지 않음을 자바가 보장해 준다. 물론 공격자가 AccessibleObject.setAccessible 같은 특권 메서드를 악용한다면 이야기는 달라진다. 임의의 네이티브 코드를 수행할 수 있는 특권을 가로챈 공격자에게는 모든 방어가 무력화된다. 



다음은 Elvis 예를 열거 타입응로 구현한 모습이다.

```java
public enum Elvis {
    INSTANCE;
    private String[] favoriteSongs = 
    	{ "Hound Dog", "Heartbreak Hotel" };
    
    public void printFavorites() {
        System.out.println(Arrays.toString(favoriteSongs));
    }
}
```

인스턴스 통제를 위해 readResolve를 사용하는 방식이 완전히 쓸모없는 것은 아니다. 직렬화 가능 인스턴스 통제 클래스를 작성해야 하는데, 컴파일 타임에는 어떤 인스턴스들이 있는지 알 수 없는 상황이라면 열거 타입으리ㅗ 표현하는 것이 불가능하기 때문이다.



- **readResolve 메서드의 접근성은 매우 중요하다.** final 클래스에서라면 readResolve 메서드는 private이어야 한다. final이 아닌 클래스에서는 다음 몇가지를 주의해서 고려해야 한다.

1. private로 선언하면 하위 클래스에서 사용할 수 없다.
2. package-private로 선언하면 같은 패키지에 속한 하위 클래스에서만 사용할 수 있다.
3. protected나 public으로 선언하면 이를 재정의하지 않은 모든 하위 클래스에서 사용할 수 있다.
4. protected나 public이면서 하위 클래스에서 재정의하지 않았다면, 하위 클래스의 인스턴스를 역직렬화하면 상위 클래스의 인스턴스를 생성하여 ClassCastException을 일으킬 수 있다.



> 핵심 정리
>
> 불변식을 지키기 위해 인스턴스를 통제해야 한다면 가능한 한 열거 타입을 사용하자. 여의치 않은 상황에서 직렬화와 인스턴스 통제가 모두 필요하다면 readResolve 메서드를 작성해 넣어야 하고, 그 클래스에서 모든 참조 타입 인스턴스 필드를 transient로 선언 해야 한다.