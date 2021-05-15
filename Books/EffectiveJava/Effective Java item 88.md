# Effective Java item 88



### readObject 메서드는 방어적으로 작성하라



아이템 50에서는 불변인 날짜 범위 클래스를 만드는 데 가변인 Date 필드를 이용했다. 그래서 불변식을 지키고 불변을 유지하기 위해 생성자와 접근자에서 Date 객체를 방어적으로 복사하느라 코드가 상당히 길어졌다. 다음이 바로 그 클래스다.



```java
// 방어적 복사를 사용하는 불변 클래스

public final class Period {
    private final Date start;
    private final Date end;
    
    /**
    * @param start 시작 시각
    * @param end  종료 시각; 시작 시간보다 뒤여야 한다.
    * @throws IllegalArgumentException 시작 시간이 종료 시각보다 늦을 때 발생한다.
    * @throws NullPointerException start나 end가 null이면 발행한다.
    */
    
    public Period(Date start, Date end) {
        this.start = new Date(start.getTime());
        this.end = new Date(end.getTime());
        if (this.start.compareTo(this.end) > 0)
            throw new IllegalArgumentException(start + " after" + end);
    }
    
    public Date start() { return new Date(start.getTime()); }
    public Date end() { return new Date(end.getTime()); }
    public String toString() { return start + " - " + end; }
    
    ... // 나머지 코드는 생략
}
```

이 클래스를 직렬활하기로 결정했다고 해보자. Period 객체의 물리적 표현이 논리적 표현과 부합하므로 기본 직렬화 형태를 사용해도 나쁘지 않다. 그러나 이 클래스 선언에 implements Serializable을 추가하는 것으로 모든 일을 끝낼 수 있을 것 같다. 하지만 이렇게 해서는 이 클래스의 주요한 불변식을 더는 보장하지 못하게 된다.



- 문제는 readObject 메서드가 실질적으로 또 다른 public 생성자이기 때문이다. 따라서 다른생성자와 똑같은 수준으로 주의를 기울여야 한다.
- 보통의 생성자처럼 readObject 메서드에서도 인수가 유효한지 검사해야 하고 필요하다면 매개변수를 방어적으로 복사해야 한다. 이 작업을 제대로 수행하지 못하면 공격자는 아주 손쉽게 해당 클래스의 불변식을 깨뜨릴 수 있다.
- 쉽게 말해 readObject는 매개변수로 바이트 스트림을 받는 생성자라 할 수 있다. 보통의 경우 바이트 스트림은 정상적으로 생성된 인스턴스를 직렬화해 만들어진다. 하지만 불변식을 깨뜨릴 의도로 임의 생성한 바이트 스트림을 건네면 문제가 생긴다. 정상적인 생성자로는 만들어 낼 수 없는 객체를 생성해낼 수 있기 때문이다.



```java
// 유효성 검사를 수행하는 readObject 메서드 - 아직 부족하다.

private void readObject(ObjectInputStream s)
    throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    
    // 불변식을 만족하는지 검사한다.
    if (start.compareTo(end) > 0)
        throw new InvalidObjectException(start + " after " + end);
}
```

- 이상의 작업으로 공격자가 허용되지 않는 Period 인스턴스를 생성하는 일을 막을 수 있지만, 아직도 미묘한 문제 하나가 숨어 있다. 
- 정상 Period 인스턴스에서 시작된 바이트 스트림끝에 private Date 필드로의 참조를 추가하면 가변 Period 인스턴스를 만들어 낼 수 있다. 
- 공격자는 ObjectInputStream에서 Period 인스턴스를 읽은 후 스트림끝에 추가된 이 '악의적인 객체 참조'를 읽어 Period 객체의 내부 정보를 얻을 수 있다. 이제 이 참조로 얻은 Date 인스턴스들을 수정할 수 있으니, Period 인스턴스는 더는 불변이 아니게 되는 것이다.



다음은 이 공격이 어떻게 이뤄지는지 보여주는 예다

```java
// 가변 공격의 예

public class MutablePeriod {
    // Period 인스턴스
    public final Period period;
    
    // 시작 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date start;
    
    // 종료 시각 필드 - 외부에서 접근할 수 없어야 한다.
    public final Date end;
    
    public MutablePeriod() {
        try {
            ByteArrayOutputStream bos = 
                new ByteArrayOutputStream();
            ObjectOutputStream out =
                new ObjectOutputStream(bos);
            
            // 유효한 Period 인스턴스를 직렬화 한다.
            out.writeObject(new Period(new Date(), new Date()));
            
     /**
    * 악의적인 '이전 객체 참조', 즉 내부 Date 필드로의 참조를 추가한다.
    * 상세 내용은 자바 객체 직렬화 명세의 6.4절을 참고하자.
    */
            byte[] ref = { 0x71, 0, 0x7e, 0, 5 }; // 참조 #5
            bos.write(ref); // 시작 필드
            ref[4] = 4; // 참조 #4
            bos.write(ref); // 종료 필드
            
            // Period 역직렬화 후 Date 참조를 '훔친다'.
            ObjectInputStream in = new ObjectInputStream(
            	new ByteArrayInputStream(bos.toByteArray()));
            period = (Period) in.readObject();
     		start = (Date) in.readObject();
            end = (Date) in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }
}
```

다음 코드를 실행하면 이 공격이 실제로 이뤄지는 모습을 확인할 수 있다.



```java
public static void main(String[] args) {
    MutablePeriod mp = new MutablePeriod();
    Period p = mp.period;
    Date pEnd = mp.period;
    
    // 시간을 되돌리자!
    pEnd.setYear(69);
    System.out.println(p);
}

// 다음의 결과를 출력한다.
Wed Nov 22 00:21:29 PST 2017 - Wed Nov 22 00:21:29 PST 1978
Wed Nov 22 00:21:29 PST 2017 - Wed Nov 22 00:21:29 PST 1969
```

이 예에서 Period 인스턴스는 불변식을 유지한 채 생성되었지만, 의도적으로 내부의 값을 수정할 수 있었다. 

- 이 문제의 근원은 Period의 readObject 메서드가 방어적 복사를 충분히 하지 않은 데 있다. **객체를 역직렬화할 때는 클라이언트가 소유해서는 안 되는 객체 참조를 갖는 필드를 모두 반드시 방어적으로 복사해야 한다.**
- 따라서 readObject에서는 불변 클래스 안의 모든 private 가변 요소를 방어적으로 복사해야 한다. 다음의 readObject 메서드라면 Period의 불변식과 불변 성질을 지켜내기에 충분하다.

```java
// 방어적 복사와 유효성 검사를 수행하는 readObject 메서드

private void readObject(ObjectInputStream s)
    throws IOException, ClassNotFoundException {
    s.defaultReadObject();
    
    // 가변 요소들을 방어적으로 복사하낟.
    start = new Date(start.getTime());
    end = new Date(end.getTime());
    
    // 불변식을 만족하는지 검사한다.
    if (start.compareTo(end) > 0)
        throw new InvalidObjectException(start + " after " + end);
}
```



기본 readObject 메서드를 써도 좋을지를 판단하는 간단한 방법을 소개하겠다. transient 필드를 제외한 모든 필드의 값을 매개변수로 받아 유효성 검사 없이 필드에 대입하는 public 생성자를 추가해도 괜찮은가?

- 답이 "아니오"라면 커스텀 readObject 메서드를 만들어 (생성자에서 수행했어야 함) 모든 유효성 검사와 방어적 복사를 수행해야 한다. 혹은 직렬화 프록시 패턴을 사용하는 방법도 있다. 이 패턴은 안전하게 만드는 데 필요한 노력을 상당히 경감해주므로 적극 권장하는 바다.
- final이 아닌 직렬화 가능 클래스라면 readObject와 생성자의 공통점이 하나 더 있다. 마치 생성자처럼 readObject 메서드도 재정의 가능 메서드를 호출해서는 안된다. 이 규칙을 어겼는데 해당 메서드가 재정의되면, 하위 클래스의 상태가 완전히 역직렬화되기 전에 하위 클래스에서 재정의된 메서드가 실행된다. 결국 프로그램 오작동으로 이어질 것이다.



> 핵심 정리
>
> readObject 메서드를 작성할 때는 언제나 public 생성자를 작성하는 자세로 임해야 한다. readObject는 어떤 바이트 스트림이 넘어오더라도 유효한 인스턴스를 만들어내야 한다. 바이트 스트림이 진짜 직렬화된 인스턴스라고 가정해서는 안된다. 이번 아이템에서는 기본 직렬화 형태를 사용한 클래스를 예로 들었지만 커스텀 직렬화를 사용하더라도 모든 문제가 그대로 발생할 수 있다.