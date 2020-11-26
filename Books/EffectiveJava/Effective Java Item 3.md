# Effective Java Item 3



### private 생성자나 열거타입으로 싱글턴임을 보증하라



싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말한다. 싱글턴의 전형적인 예로는 함수와 같은 무상태 객체나 설계상 유일해야 하는 시스템 컴포넌트를 들 수 있다. 그런데 **클래스를 싱글턴으로 만들면 이를 사용하는 클라이언트를 테스트하기가 어려워질 수 있다.** 타입을 인터페이스로 정의한 다음 그 인터페이스를 구현해서 만든 싱글턴이 아니라면 싱글턴 인스턴스를 가짜 구현으로 대체할 수 없기 때문이다.



- 싱글턴을 만드는 방식은 보통 둘 중 하나다. 두 방식 모두 생성자는 private으로 감춰두고, 유일한 인스턴스에 접근할 수 있는 수단으로 public static 멤버를 하나 마련해둔다.



```java
//public static final 필드 방식의 싱글턴
public class Elvis {
    public static final Elvis INSTANCE = new Elvis();
    
    private Elvis() { ... }
    
    public void leaveTheBuilding() { ... }
}
```



private 생성자는 public static final 필드인 Elvis.INSTANCE를 초기화할 때 딱 한번만 호출된다.

public 이나 protected 생성자가 없으므로 Elvis 클래스가 초기화될 때 만들어진 인스턴스가 전체 시스템에서 하나뿐임이 보장된다. 클라이언트는 손 쓸 방법이 없다. 예외는 단 한가지, 권한이 있는 클라이언트는 리플렉션 API인 AccessibleObject.setAccessible을 사용해 private 생성자를 호출할 수 있다. 이러한 공격을 방어하려면 생성자를 수정하여 두 번째 객체가 생성되려 할 때 예외를 던지게 하면 된다.



- 싱글턴을 만드는 두 번째 방법에서는 정적 팩토리 메서드를 public static 멤버로 제공한다.



```java
public class Elvis {
    private static final Elvis INSTANCE = new Elvis();
    private Elvis() { ... }
    public static Elvis getInstance() { return INSTANCE; }
    
    public void leaveTheBuilding() { ... }
}
```



Elvis.getInstance 는 항상 같은 객체의 참조를 반환하므로 제2의 Elvis 인스턴스란 결코 만들어지지 않다. (역시 리플렉션을 통한 예외는 똑같이 적용된다.)

1. public 필드 방식의 큰 장점은 해당 클래스가 싱글턴임이 API에 명백히 드러난다는 것이다. public static 필드가 final이니 절대로 다른객체를 참조할 수 없다.
2. 두번째 장점은 바로 간결함이다. 
