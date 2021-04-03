# Effective Java item 80



### 스레드보다는 실행자, 태스크, 스트림을 애용하라



Java.util.concurrent 패키지가 등장했다. 이 패키지는 실행자 프레임워크라고 하는 인터페이스 기반의 유연한 태스크 실행 기능을 담고 있다. 그래서 모든 면에서 뛰어난 작업 큐를 단 한 줄로 생성할 수 있게 되었다.



```java
ExecutorService exec = Executors.newSingleThreadExecutor();
```



다음은 이 실행자에 실행할 태스크를 넘기는 방법이다.

```java
exec.execute(runnable);
```



그리고 다음은 실행자를 우아하게 종료시키는 방법이다.(이 작업이 실패하면 VM자체가 종료되지 않을 것이다.)

```java
exec.shutdown();
```



실행자 서비스의 기능은 이 외에도 많다. 다음은 실행자 서비스의 주요 기능들이다.

- 특정 태스크가 완료되기를 기다린다.
- 태스크 모음 중 아무것 하나(invokeAny 메서드) 혹은 모든 태스크(invokeAll 메서드)가 완료되기를 기다린다.
- 실행자 서비스가 종료하기를 기다린다. (awaitTremination 메서드).
- 완료된 태스크들의 결과를 차례로 받는다.(ExecutorCompletionService 이용).
- 태스크를 특정 시간에 혹은 주기적으로 실행하게 한다.(ScheduledThreadPoolExecutor 이용).



큐를 둘 이상의 스레드가 처리하게 하고 싶다면 간단히 다른정적 팩터리를 이용하여 다른 종류의 실행자 서비스(스레드 풀)를 생성하면 된다. 스레드 풀의 스레드 개수는 고정할 수도 있고, 필요에 따라 늘어나거나 줄어들게 설정할 수 도 있다. 여러분에게 필요한 실행자 대부분은 java.util.concurrent.Executors의 정적 팩터리들을 이용해 생성할 수 있을 것이다. 평범하지 않은 실행자를 원한다면 ThreadPoolExecutor 클래스를 직접 사용해도 된다.



⭐ 가벼운 서버라면 Executors.newCachedThreadPool이 일반적으로 좋지만 무거운 프로덕션 서버에는 좋지 못하다. 무거운 서버라면 스레드 개수를 고정한 Executors.newFixedThreadPool을 선택하거나 완전히 통제할 수 있는 ThreadPoolExecutor를 직접 사용하는 편이 훨씬 낫다.



- 작업 큐나 스레드를 직접 다루는 것은 일반적으로 삼가야 한다. 실행자 프레임워크를 이용해 작업 단위와 실행 매커니즘을 분리하는 것이 좋다.



### CompletableFuture

https://brunch.co.kr/@springboot/267

자바 병렬 프로그래밍을 읽어보자.