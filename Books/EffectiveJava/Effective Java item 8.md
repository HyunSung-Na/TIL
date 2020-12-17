# Effective Java item 8



### finalizer 와 cleaner 사용을 피하라



자바는 두 가지 객체 소멸자를 제공한다. 그중 finalizer는 예측할 수 없고, 상황에 따라 위험할 수 있어 일반적으로 불필요하다. 오동작, 낮은 성능, 이식성 문제의 원인이 되기도 한다. finalizer는 나름의 쓰임새가 있긴 하지만 기본적으로 **쓰지 말아야 한다.** 그래서 자바 9에서는 finalizer를 사용 자제 API로 지정하고 cleaner를 그 대안으로 소개했다. (하지만 자바 라이브러리에서도 finalizer를 여전히 사용한다.)

- cleaner는 finalizer보다는 덜 위험하지만, 여전히 예측할 수 없고, 느리고, 일반적으로 불필요하다.



자바의 finalizer와 cleaner는 C++의 파괴자와는 다른개념이다. C++에서의 파괴자는 특정 객체와 관련된 자원을 회수하는 보편적인 방법이다.



#### finalizer와 cleaner를 사용하면 안되는 이유

1. finalizer와 cleaner는 즉시 수행된다는 보장이 없다. 객체에 접근 할 수 없게 된 후 finalizer나 cleaner가 실행되기까지 얼마나 걸릴지 알 수 없다. **즉 finalizer와 cleaner로는 제때 실행되어야 하는 작업은 절대 할 수 없다.**

   예를 들면 파일 닫기를 맡기면 중대한 오류를 일으킬 수 있다. 시스템이 동시에 열 수 있는 파일 개수에 한계가 있기 때문이다. finalizer와 cleaner를 얼마나 신속히 수행할지는 전적으로 가비지 컬렉터 알고리즘에 달렸으며, 이는 가비지 컬렉터 구현마다 천차만별이다. 

2. 자바 언어 명세는 finalizer와 cleaner의 수행 시점뿐 아니라 수행 여부조차 보장하지 않는다. 접근할 수 없는 일부 객체에 딸린 종료 작업을 전혀 수행하지 못한 채 프로그램이 중단될 수도 있다는 얘기다. 따라서 프로그램 생애주기와 상관없는, **상태를 영구적으로 수정하는 작업에서는 절대 finalizer와 cleaner에 의존해서는 안된다**

3. System.gc나 System.runFinalization 메서드에 현혹되지 말자. finalizer와 cleaner가 실행될 가능성을 높여줄 수는 있으나, 보장해주진 않는다. 사실 이를 보장해 주겠다는 System.runFinalizersOnExit와 그 쌍둥이인 Runtime.runFinalizersOnExit다. 하지만 이 두 메서드는 심각한 결함이 있다.

4. finalizer 동작 중 발생한 예외는 무시되며, 처리할 작업이 남았더라도 그 순간 종료된다. 잡지 못한 예외 때문에 해당 객체는 자칫 마무리가 덜 된 상태로 남을 수 있다. 그리고 다른스레드가 이처럼 훼손된 객체를 사용하려 한다면 어떻게 동작할지 예측할 수 없다. 보통의 경우는 잡지 못한 예외가 스레드를 중단시키고 스택 추적 내역을 출력하지만, 같은 일이 finalizer에서 일어난다면 경고조차 출력하지 않는다. 그나마 cleaner를 사용하는 라이브러리는 자신의 스레드를 통제하기 때문에 이러한 문제가 발생하지 않는다.

5. **finalizer와 cleaner는 심각한 성능 문제도 동반한다.** finalizer를 사용한 객체를 생성하고 파괴하니 일반 간단한 객체보다 50배가 느렸다. cleaner도 성능이 비슷한대 안전망 형태로 사용하면 훨씬 빨라진다. 안전망 방식에서는 일반 객체보다 5배 느리다.

6. **finalizer를 사용한 클래스는 finalizer 공격에 노출되어 심각한 보안 문제를 일으킬 수도 있다.** finalizer 공격 원리는 간단하다. 생성자나 직렬화 과정에서 예외가 발생하면, 이 생성되다 만 객체에서 악의적인 하위 클래스의 finalizer가 수행될 수 있게 된다. 객체 생성을 막으려면 생성자에서 예외를 던지는 것만으로 충분하지만, finalizer가 있다면 그렇지도 않다. **final이 아닌 클래스를 finalizer 공격으로부터 방어하려면 아무 일도 하지 않는 finalize 메서드를 만들고 final로 선언하자**

- 파일이나 스레드 등 종료해야 할 자원을 담고 있는 객체의 클래스에서 finalizer나 cleaner를 대신해줄 묘안은 그저 AutoCloseable을 구현해주고, 클라이언트에서 인스턴스를 다 쓰고 나면 close 메서드를 호출하면 된다.



#### finalizer와 cleaner를 대체 어디에?



1. 자원의 소유자가 close 메서드를 호출하지 않는 것에 대비한 안전망 역할. 자바 라이브러리의 일부 클래스는 안전망 역할의 finalizer를 제공한다. FileInputStream, FileOutputStream, ThreadPoolExecutor가 대표적이다.
2. 네이티브 피어와 연결된 객체에서 활용할 수 있다. 네이티브 피어란 일반 자바 객체가 네이티브 메서드를 통해 기능을 위임한 네이티브 객체를 말한다. 네이티브 피어는 자바 객체가 아니니 가비지 컬렉터는 그 존재를 알지 못한다. 그 결과 자바 피어를 회수할 때 네이티브 객체까지 회수하지 못한다. finalizer와 cleaner가 나서서 처리하기 적당한 작업이다.



cleaner는 사용하기에 조금 까다롭다. 다음의 Room 클래스로 이 기능을 설명해보겠다. 방(room) 자원을 수거하기 전에 반드시 청소(clean)해야 한다고 가정해보자. Room 클래스는 AutoCloseable을 구현한다. 사실 자동 청소 안전망이 cleaner를 사용할지 말지는 순전히 내부 구현방식에 관한 문제다. 즉 finalizer와 달리 cleaner는 클래스의 public API에 나타나지 않는다는 이야기다.

```java
// cleaner를 안전망으로 활용하는 AutoCloseable 클래스

public class Room implements AutoCloseable {
    
    private static final Cleaner cleaner = Cleaner.create();
    
    // 청소가 필요한 자원. 절대 Room을 참조해서는 안된다!
    private static class State implements Runnable {
        int numJunkPiles; // 방(Room) 안의 쓰레기 수
        
        State(int numJunkPiles) {
            this.numJunkPiles = numJunkPiles;
        }
        
        // close 메서드나 cleaner가 호출된다.
        @Override public void run() {
            System.out.println("방 청소");
            numJunkPiles = 0;
        }
    }
    
    // 방의 상태, cleanable과 공유한다.
    private final State state;
    
    // cleanable 객체, 수거 대상이 되면 방을 청소한다.
    private final Cleaner.Cleanable cleanable;
    
    public Room(int numJunkPiles) {
        state = new State(numJunkPiles);
        cleanable = cleaner.register(this, state);
    }
    
    @Override public void close() {
        cleanable.clean();
    }
}
```



static으로 선언된 중첩 클래스인 State는 cleaner가 방을 청소할 때 수거할 자원들을 담고 있다. 이 예에서는 단순히 방 안의 쓰레기 수를 뜻하는 numJunkPiles 필드가 수거할 자원에 해당한다. state는 Runnable을 구현하고, 그 안의 run 메서드는 cleanable에 의해 딱 한 번만 호출 될 것이다. 이 cleanable 객체는 Room 생성자에서 cleaner에 Room과 State를 등록할 때 얻는다. 

앞서 이야기한 대로 Room의 cleaner는 단지 안전망으로만 쓰였다. 클라이언트가 모든 Room생성을 try-with-resources 블록으로 감쌌다면 자동 청소는 전혀 필요하지 않다.



```java
// 잘 짜인 클라이언트 코드

public class Adult {
    
    public static void main(String[] args) {
        try (Room myRoom = new Room(7)) {
            System.out.println("안녕~");
        }
    }
}
```

기대한 대로 Adult 프로그램은 "안녕~"을 출력한 후, 이어서 "방 청소"를 출력한다. 이번엔 결코 방 청소를 하지 않는 다음 프로그램을 보자

```java
public class Teenager {
    public static void main(String[] args) {
        new Room(99);
        System.out.println("아무렴");
    }
}
```

"아무렴"에 이어 "방 청소"가 출력되리라 기대했는가? 하지만 내 컴퓨터에서 "방 청소" 는 한 번도 출력되지 않았다. 앞서 '예측할 수 없다' 고 한 상황이다. cleaner의 명세에는 이렇게 쓰여 있다.



> System.exit을 호출할 때의 cleaner 동작은 구현하기 나름이다. 청소가 이뤄질지는 보장하지 않는다.



명세에선 명시하지 않았지만 일반적인 프로그램 종료에서도 마찬가지다. 내 컴퓨터에서는 Teenager의 main 메서드에 System.gc() 를 추가하는 것으로 종료 전에 "방 청소"를 출력할 수 있었지만, 여러분의 컴퓨터에서도 그러리라는 보장은 없다.



> 핵심정리
>
> cleaner(자바 8 까지만 finalizer)는 안전망 역할이나 중요하지 않은 네이티브 자원 회수용으로만 사용하자. 물론 이런 경우라도 불확실성과 성능 저하에 주의해야 한다.



