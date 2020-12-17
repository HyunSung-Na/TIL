# Effective Java item 9



### try-finally 보다는 try-with-resources를 사용하라



자바 라이브러리에는 close 메서드를 호출해 직접 닫아줘야 하는 자원이 많다.

InputStream, OutputStream, java.sql.Connection 등이 좋은 예다. 자원 닫기는 클라이언트가 놓치기 쉬워서 예측할 수 없는 성능 문제로 이어지기도 한다. 이런 자원 중 상당수가 안전망으로 finalizer를 활용하고는 있지만 finalizer는 그리 믿을만하지 못하다.



전통적으로 자원이 제대로 닫힘을 보장하는 수단으로 try-finally가 쓰였다. 예외가 발생하거나 메서드에서 반환되는 경우를 포함해서 말이다.

```java
static String firstLineOfFile(String path) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(path));
    try {
        return br.readLine();
    } finally {
        br.close();
    }
}
```

- 하지만 이런 방법은 자원을 하나 이상 사용하게 되면 코드가 너무 지저분해 진다. 코드가 복잡해지기 때문에 에러가 발생하면 어디서 발생했는지 디버깅을 몹시 어렵게 한다.



이러한 문제들은 자바 7이 생성한 try-with-resources 덕에 모두 해결되었다. 이 구조를 사용하려면 해당 자원이 AutoCloseable 인터페이스를 구현해야 한다. 단순히 void를 반환하는 close 메서드 하나만 덩그러니 정의한 인터페이스다. 자바 라이브러리와 서드파티 라이브러리들의 수많은 클래스와 인터페이스가 이미 AutoCloseable을 구현하거나 확장해뒀다. 여러분도 닫아야 하는 자원을 뜻하는 클래스를 작성한다면 AutoCloseable을 반드시 구현하기 바란다.



```java
// try-with-resources 자원을 회수하는 최선책!

static String firstLineOfFile(String path) throws IOException {
    try (BufferedReader br = new BufferedReader(
    		new FileReader(path))) {
        return br.readLine();
    }
}
```



try-with-resources 버전이 짧고 읽기 수월할 뿐 아니라 문제를 진단하기도 훨씬 좋다.

보통의 try-finally에서처럼 try-with-resources에서도 catch 절을 쓸 수 있다. catch 절 덕분에 try 문을 더 중첩하지 않고도 다수의 예외를 처리할 수 있다.

다음 코드에서는 firstLineOfFile 메서드를 살짝 수정하여 파일을 열거나 데이터를 읽지 못했을 때 예외를 던지는 대신 기본값을 반환하도록 해봤다.

```java
static String firstLineOfFile(String path, String defaultVal) {
    try (BufferedReader br = new BufferedReader(
    		new FileReader(path))) {
        return br.readLind();
    } catch (IOException e) {
        return defaultVal;
    }
}
```



> 핵심정리
>
> 꼭 회수해야 하는 자원을 다룰 때는 try-finally 말고, try-with-resources를 사용하자. 예외는 없다. 코드는 더 짧고 분명해지고, 만들어지는 예외 정보도 훨씬 유용하다. try-finally로 작성하면 실용적이지 못할 만큼 코드가 지저분해지는 경우라도, try-with-resources로는 정확하고 쉽게 자원을 회수할 수 있다.