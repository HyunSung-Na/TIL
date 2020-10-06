# Collection Framework

### - Collection Framework란?

\- 배열의 단점을 보완한 데이터 군을 저장하는 클래스들을 표준화한 설계이다.
\- 다수의 데이터를 쉽게 처리할 수 있는 방법을 제공하는 클래스들로 구성된다.
\- 컬렉션(collection) : 다수의 데이터, 데이터 그룹을 의미한다.
\- 프레임워크(framework) : 표준화, 정형화된 체게적인 프로그래밍 방식이다.
\- 컬렉션 클래스(collection class) : 다수의 데이터를 저장할 수 있는 클래스이다.



![Collection Framework 계층도](https://3.bp.blogspot.com/-YatAN_wi-Kw/VsZujw_7XuI/AAAAAAAAAIk/5tZPcgA8T7w/s1600/%25EC%25BB%25AC%25EB%25A0%2589%25EC%2585%25981.jpg)

**인터페이스의 특징**

- **List**
  1. 순서가 있는 데이터의 집합, 데이터의 중복을 허용한다.
  2. 데이터를 add를 하면 앞에서 부터 순차적으로 데이터가 들어간다.(Stack)
  3. 각각의 저장되어 있는 공간들은 고유한 index를 갖는다
  4. 구현클래스 : ArrayList, LinkedList, Stack, Vector 등
- **Set**
  1. 순서를 유지하지 않는 데이터의 집합(순서가 없음), 데이터의 중복을 허용하지 않는다.
  2. 집합과 같은 구조(데이터를 추가할 때 순서와 상관없이 add한다. )
  3. 구현클래스 : HashSet, TreeSet
- **Map**
  1. key , value 값의 쌍으로 이뤄진 데이터의 집합
  2. 순서는 유지되지 않는다.
  3. key는 중복이 허용되지 않으나 value는 중복허용
  4. 구현클래스 : HashMap, TreeMap, Hashtable, Properties등

------

### ArrayList

- **Array**
  1. PHP  : Array가 Size를 가지지 않고 데이터가 추가될 때 마다 자동으로 Size가 늘어남
  2. Java  : Array에 Size를 정해줘야함 - > Java Library - > ArrayList (데이터 추가하면 배열이 자동으로 추가 )

- **ArrayList**

- 특징

  	- 배열기반의 데이터구조
  	- 저장 순서가 유지되고 중복을 허용

  1. 장점 : 배열은 구조가 간단하고 데이터를 읽는 시간이 짧다
  2. 단점 : 크기를 변경할 수 없다.
      - 크기를 변경해야되는 경우 새로운 배열을 생성 후 데이터를 복사해야 함
      - 크기 변경을 피하기 위해 충분히 큰 배열을 생성하면 메모리의 낭비
  3. 비 순차적인 데이터의 추가, 삭제 시간이 많이 걸린다.
     	- 데이터를 추가하거나 삭제하기 위해 다른 데이터를 옮겨야 함.
          	- 순차적인 데이터추가는 빠르다 (데이터의 끝에서 추가, 끝부터 삭제)

- Doubling

  - 생성한 ArrayList가 가득 차고 새로운 데이터를 넣어야 할때 Doubling 이 일어난다.
  - Doubling 이란 기존 ArrayList를 복사해서 ArrayList 두배의 크기로 생성하고 복사한 ArrayList를 붙여넣어 생성하는 것이다. 
  - 수정) 자바 컬렉션프레임워크 도입되기 전 Vector 클래스에서는 2배의 크기로 증가시켰으나 현재 ArrayList는 사이즈 증가시 50%증가
    - ArrayList grow(minCapacity : int)  - > int newCapacity = oldCapacity + (oldCapacity >> 1) 오른쪽 시프트연산
    - 오른쪽 시프트연산은 그 값을 /2 로 나눈 것과 같으므로 
    - 새로운용량 = 이전용량 + 이전용량 / 2  (전 용량보다 1.5배 증가)

  <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbQ5ZNv%2FbtqFdt2FFoU%2FnNOQqEHT5cjQaySItSpekk%2Fimg.jpg" width = "400px" style = "height:300px" />

  

  **ArrayList 구현**

  ```java
  class ArrayList {
  	private Object[] data; // ArrayList는 객체만 사용할 수 있기 때문에 Object객체 생성
  	private int size;
  	private int index;
  	
  	public ArrayList(){
  		this.size = 1;
  		this.data = new Object[this.size]; // size 1로 배열 객체생성
  		this.index = 0; // index 0번째 부터 데이터 삽입
  	}
      
      public void add(Object obj){ //배열의 요소를 추가해주는 함수
          if(this.index == this.size-1){  // 배열이 꽉찬 경우 doubling을 실행
              doubling();
          }
          data[this.index] = obj; //현재 index에 object를 삽입
          this.index++; // 새로운 배열 추가를 위해 빈 배열 index로 이동
      }
      
      private void doubling(){ //배열의 크기를 늘려주는 함수
          this.size = this.size * 2;
          Object[] newData = new Object[this.size]; // 기존 배열의 2배 크기의 new배열 생성
          for(int i = 0; i < data.length; i++){
              newData[i] = data[i]; // 기존 배열의 값을 new배열로 복사
          }
          this.data = newData; //기존 배열의 참조변수를 현재 newData로 변경
      }
      
      public Object get(int i) throws Exception{ // index 번호로 ArrayList값에 접근하는함수
          if (i > this.index-1){ // index 값이 배열의 크기보다 크면 Exception을 발생시킨다
              throw new Exception("ArrayIndexOutOfBound");
          }else if(i < 0){ // index의 값이 0보다 작으면 Exception 발생
              throw new Exception("Negative Value");
          }
          return this.data[i];
      }
      
      public void remove(int i) throws Exception{
          if (i > this.index-1){
              throw new Exception("ArrayIndexOutOfBound");
          }else if(i < 0){
              throw new Exception("Negative Value");
          }
          for (int x = i; x < this.data.length -1; x++){
              data[x] = data[x + 1]; //삭제할 데이터를 기준으로 앞으로 덮어쓴다.
          }
          this.index--; // index를 하나 줄여준다
      }
  }
  
  public class Test{
  	public static void main(String[] args) throws Exception{
  		ArrayList al = new ArrayList();
          al.add("0");
          al.add("1");
          al.add("2");
          al.add("3");
          al.add("4");
          al.add("5");
          al.add("6");
          al.add("7");
          al.add("8");
          al.add("9");
          System.out.println(al.get(5));
          al.remove(5);
          System.out.println(al.get(5));
  	}
  }
  ```

  **(1) 객체 생성**

  ArrayList<Integer> numbers = new Arraylist<>();

  

  **(2) 데이터 추가시 add 메소드 사용**

  numbers.add(10)

  numbers.add(1,50) : index 1번에 50이라는 숫자 넣기

  

  **(3)데이터 삭제시 remove 메소드 사용**

  numbers.remove(2) : index 2번째 값 삭제, 빈공간은 메움

  but 데이터의 끝부터 삭제시 덮어쓰지 않고 데이터만 삭제

  

  **(4) 데이터 가져올 때 get 메소드 사용**

  numbers.get(2) : index 2번째 값 가져오기

  

  **(5) 몇개 데이터 있는 지 확인 : size 메소드 사용**

  numbers.size(); 

  

  **(6) 반복(Iteration)**

  Iterator it = numbers.iterator();

  iterator 메소드 호출하여 Iterator 데이터 타입에 넣음

  

  ------

  

  ### Linked List

  - Linked List 란

  - 연결리스트는 노드 하나에 하나의 데이터를 보관하고 노드 내의 링크에 의해 순서 정보(다음 노드의 위치 정보, 이전 노드의 위치 정보)를 기억하는 자료구조입니다.

    <img src = "http://ehpub.co.kr/wp-content/uploads/2016/12/%EA%B7%B8%EB%A6%BC-3.3-%EB%B0%B0%EC%97%B4%EA%B3%BC-%EC%97%B0%EA%B2%B0%EB%A6%AC%EC%8A%A4%ED%8A%B8.png" />

  - 단방향 Linked List

    ```java
    class Node{
        int data;
        Node next = null; // 다음노드의 주소값 처음엔 null 로 설정
        
        Node(int d){
            this.data = d; // 데이터 생성자
        }
        
        void append(int d){
            Node end = new Node(d); //새로운 노드 생성
            Node n = this;
            while (n.next != null){ // 다음 노드가 있을때 까지 
                n = n.next;
            }
            n.next = end; // n.next 값이 null 일때 end를 넣어준다.
        }
        
        void delete(int d){ // 노드를 삭제하는 함수
            Node n = this;
            while (n.next != null) {
                if (n.next.data == d){
                    n.next = n.next.next;
                }else{
                    n = n.next;
                }
            }
        }
        
        void retrieve(){ // 모든 노드를 출력하는 함수
            Node n = this;
            while (n.next != null){
                System.out.print(n.data + " -> ");
                n = n.next;
            }
            System.out.println(n.data);
        }
    }
    
    public class SinglyLinkedList {
        public static void main(String[] args){
            Node head = new Node(1);
            head.append(2);
            head.append(3);
            head.retrieve();
        }
    }
    ```

    <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F9919F9395AD8A21E05" />

    

  - #### **양방향 LinkedList**

    

    **장점**

    '이중 연결 리스트'의 큰 장점은 양방향으로 연결되어 있기 때문에,

    노드를 탐색하는 방향이 양쪽으로 가능하다는 것이다.

     

    단일연결리스트의 경우는 맨끝의 데이터를 가져올때,

    순차적으로 처음노드(head)부터 탐색을 해야한다.

    (많은 연산을 필요로 한다.)

     

    이러한 단점을 보완할 수 있는 것이 '이중연결리스트'이다.

    이중연결리스트는 처음 부터 탐색 할 필요 없이 뒤(tail)에서 부터 탐색을 시작하면 된다.

    

    **단점**

    이전 노드를 지정하기 위한 변수를 하나 더 사용해야 한다. (메모리를 더 많이 사용함)

    구현이 '단일연결리스트'에 비해 복잡하다.

     

    하지만, 장점이 크기 때문에 현실에서는 대부분 이중 연결 리스트를 많이 사용한다.

  - 양방향 LinkedList 의 데이터의 삽입, 삭제

    - 데이터의 삽입

    <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F9965A3405AD8A21E23" />

    <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F99B55F375AD8A21E0F" />

    - 데이터의 삭제

      

      <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F99038A385AD8A21E09" />

      

      

      <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Ft1.daumcdn.net%2Fcfile%2Ftistory%2F994314375AD8A21E03" />



#### **ArrayList 와 LinkedList의 비교**



- **검색**

데이터 검색 시에는 ArrayList는 LinkedList에 비해 굉장히 빠르다. ArrayList는 인덱스 기반의 자료 구조이며 `get(int index)` 를 통해 O(1) 의 시간 복잡도를 가진다. 그에 비해 LinkedList는 검색 시 모든 요소를 탐색해야 하기 때문에 최악의 경우에는 O(N)의 시간 복잡도를 가진다.

- **삽입, 삭제**

LinkedList에서의 데이터의 삽입, 삭제 시에는 ArrayList와 비교해 굉장히 빠른데, LinkedList는 이전 노드와 다음 노드를 참조하는 상태만 변경하면 되기 때문이다. 삽입, 삭제가 일어날 때 O(1)의 시작 복잡도를 가진다. 반면 ArrayList의 경우 삽입, 삭제 이후 다른 데이터를 복사해야 하기 때문에 최악의 경우 O(N) 의 성능을 내게 된다.

지금까지의 내용을 정리하면 아래와 같다.

| 리스트     | 설명                                                         |
| ---------- | ------------------------------------------------------------ |
| Array      | 정적인 길이를 제공하는 배열                                  |
| Vector     | Java 1.0 에서 추가. 동기화 기능이 제공되는 가변이 가능한 자료구조 |
| ArrayList  | Java 1.2 에서 추가. 동기화가 제공되지 않음. 데이터의 검색에 유리하며 추가, 삭제에는 성능을 고려해야 한다. |
| LinkedList | Java 1.2 에서 추가. ArrayList 에 비해 데이터의 추가, 삭제에 유리하며 데이터 검색 시에는 성능을 고려해야 한다. |

같은 타입의 많은 데이터를 관리하기 위해 상황에 맞게 적절하게 사용하기 위한 방법을 알아 보았는데 데이터 관리를 위한 성능 이슈를 위해 고려해야 요소들이 더 많음을 상기해야 한다.



#### 스택( Stack )

스택(stack)은 데이터를 일시적으로 저장하기 위해 사용하는 자료구조로, 데이터의 입력과 출력 순서는 후입선출(LIFO, Last In First Out)입니다(가장 나중에 넣은 데이터를 가장 먼저 꺼냅니다). 스택에 데이터를 넣는 작업을 푸시(push)라 하고, 스택에서 데이터를 꺼내는 작업을 팝(pop)이라고 합니다. 그림 4-1에 데이터를 스택에 푸시하고 팝하는 과정을 나타냈습니다. 테이블에 겹겹이 쌓은 접시처럼 데이터를 넣는 작업도 꺼내는 작업도 위쪽부터 수행합니다. 이렇게 푸시와 팝을 하는 위치를 꼭대기(top)라 하고, 스택의 가장 아랫부분을 바닥(bottom)이라고 합
니다.
stack은 ‘마른 풀을 쌓은 더미’, ‘겹겹이 쌓음’을 뜻합니다. 그래서 푸시를 ‘쌓기’라고도 합니다.

<img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FcXnbhv%2FbtqFfYPYN5H%2Ftd7S4VgvIB3K591Wa1R0x0%2Fimg.png" />

다음과 같이 push 와 pop이 이뤄지는 구조를 스택이라고 합니다. 제일 나중에 들어간 자료가 제일 먼저 나오는 후입선출의 구조입니다.

- **스택의 구현**

  ```java
  public class IntStack {
      private int max; // 스택용량
      private int ptx; // 스택 포인터
      private int[] stk; // 스택 본체
      
      //실행시 예외 : 스택이 비어있음
      public class EmptyIntStackException extends RuntimeException {
          public EmptyIntStackException(){}
      }
      // 스택이 가득참
      public class OverflowIntStackException extends RuntimeException {
          public OverflowIntStackException(){}
      }
      
      //생성자
      public IntStack(int capacity){
          ptr = 0;
          max = capacity;
          try{
              stk = new int[max]; // 스택 본체용 배열을 생성
          }catch (OutofMemoryError e){ // 생성할 수 없음
              max = 0;
          }
      }
      
      // 스택에 x를 푸시
  	public int push(int x) throws OverflowIntStackException {
  		if (ptr >= max) // 스택이 가득 참
  			throw new OverflowIntStackException();
  		return stk[ptr++] = x;
  	}
      
      // 스택에서 데이터를 팝(정상에 있는 데이터를 꺼냄)
  	public int pop() throws EmptyIntStackException {
  		if (ptr <= 0) // 스택이 비어 있음
  			throw new EmptyIntStackException();
  		return stk[--ptr];
  	}
  }
  ```

- **스택의 Push**

  <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fchgc51%2FbtqFfXXM2fY%2F1oKbRlmTAUuOyiDubbKpo0%2Fimg.png" />

- **스택의 pop**

  <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FUN1OO%2FbtqFeDlI6Cy%2FXxx5AbAkxYnF7ckTcbdMm1%2Fimg.png" />



#### 큐( Queue )

큐(queue)는 스택과 마찬가지로 데이터를 일시적으로 쌓아 두기 위한 자료구조입니다. 그림 4-7에서 볼 수 있듯이 가장 먼저 넣은 데이터를 가장 먼저 꺼내는 선입선출 구조로 되어 있습니다. 생활에서 볼 수 있는 큐의 예는 은행 창구에서 차례를 기다리는 대기열이나 마트에서 계산을 기다리는 대기열을 들 수 있습니다. 만약 이렇게 기다리는 대기열이 ‘스택’이라면 가장 먼저 줄을 선 사람이 가장 늦게까지 기다리게 됩니다. 큐에 데이터를 넣는 작업을 인큐(enqueue)라 하고, 데이터를 꺼내는 작업을 디큐(dequeue)라고 합니다. 또 데이터를 꺼내는 쪽을 프런트(front)라 하고, 데이터를 넣는 쪽을 리어(rear)라고 합니다.

<img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FRsctj%2FbtqFfXQ5dDf%2Fh2zMPML2eUv4JXNwkOzni1%2Fimg.png" />

- **Queue의 구현**

  <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbgvbZX%2FbtqFfZuwqQJ%2FZg9pVkKEnKg2z8wZb3YCS0%2Fimg.png" />

```java
public class IntQueue {
    
    private int max; 	// 큐의 용량
    private int front;  // 첫번째 요소
    private int rear;   // 마지막 요소
    private int num;  	// 현재 데이터 수
    private int[] que; 	// 큐 본체
    
    // 실행 시 예외：큐가 비어 있음
	public class EmptyIntQueueException extends RuntimeException {
		public EmptyIntQueueException() { }
	}
    
	// 실행 시 예외：큐가 가득 참
	public class OverflowIntQueueException extends RuntimeException {
		public OverflowIntQueueException() { }
	}
	// 생성자
	public IntQueue(int capacity) {
		num = front = rear = 0;
		max = capacity;
		try {
				que = new int[max]; // 큐 본체용 배열을 생성
		} catch (OutOfMemoryError e) { // 생성할 수 없음
		max = 0;
	}
        
    // 큐에 데이터를 인큐
	public int enque(int x) throws OverflowIntQueueException {
		if (num >= max)
			throw new OverflowIntQueueException(); // 큐가 가득 참
		que[rear++] = x;
		num++;
		if (rear == max)
			rear = 0;
		return x;
	}
        
    // 큐에서 데이터를 디큐
	public int deque() throws EmptyIntQueueException {
		if (num <= 0)
			throw new EmptyIntQueueException(); // 큐가 비어 있음
		int x = que[front++];
		num--;
		if (front == max)
			front = 0;
		return x;
	}
}
```



#### Iterator, ListIterator, Enumeration

- 컬렉션에 저장된 데이터에 접근하는데 사용되는 인터페이스 (List, Set에 사용가능)
- Enumeration은 Iterator의 구버전 ( 지금은 거의 사용하지 않음 ) 
- ListIterator는 Iterator의 접근성을 향상시킨 것 (단방향 → 양방향) (List에만 사용가능)

<img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2FbKupHM%2FbtqFhmbjBZb%2FKh0lQT99bG0Ed9GkrK1LP1%2Fimg.png" />

- **Iterator**

1. 컬렉션에 저장된 요소들을 읽어오는 방법을 표준화 한 것 

2. 컬렉션에 정의된 iterator() 를 호출해서 Iterator를 구현한 객체를 얻어서 사용

   ```java
   Collection c = new ArraryList(); // Set인터페이스로 만들어도 그대로 사용가능(재사용성)
   ...
   Iterator it = c.iterator();
   
   while (it.hasnext()){
       System.out.println(it.next());
   }
   ```

   - Iterator 메서드는 Collection 인터페이스에 선언되어 있기 때문에 Collection 인터페이스 자손인 Set 과 List를 모두 구현할 수 있다. 

   ```java
   public interface Collection {
       ...
       public Iterator iterator();
       ...
   }
   ```

   - Map 인터페이스는 Collection의 자손이 아니기 때문에 Iterator를 사용할 수 없다. 다음과 같이 사용할 수 있다.

     ```java
     Map map = new HashMap();
     	...
     Iterator list = map.entrySet().iterator(); // entrySet은 map을 Set으로 변환
     // map을 변환해서 Iterator를 사용 해야 된다.(keySet메서드도 가능)
     ```

   - iterator로 데이터를 읽어 오는 도중에 데이터의 변경이 있으면 안된다.

     ```java
     import java.util.ArraryList;
     import java.util.Iterator;
     
     public class IteratorEx1{
         
         public static void main(String[] args){
            
             ArraryList list = new ArraryList();
             list.add("1");
             list.add("2");
             list.add("3");
             list.add("4");
             list.add("5");
             
             Iterator it = list.iterator();
             System.out.println(it.next());
             System.out.println(it.next());
             list.add("5"); // 예외발생!! ConcurrentModificationException
             System.out.println(it.next());
             System.out.println(it.next());
             System.out.println(it.next());
             // list의 개수보다 iterator 호출 개수가 많으면 에러발생!
             
             while (it.hasNext())
                 System.out.println(it.next());
             //모든 요소를 읽으면 iterator 소멸
             
             while (it.hasNext()) // iterator는 일회용이라서 두번 호출하면 한번만 나온다.
                 System.out.println(it.next());
             
             //람다식으로 while문을 이렇게 간결하게 작성할 수 있다.
             it.forEachRemaining(System.out::println); 
         }
     }
     ```



- **ListIterator**

  - Iterator의 접근성을 향상 시킨 것이 ListIterator이다. (단방향 -> 양방향)

  ```java
  public interface ListIterator extends Iterator {
      ...// Iterator를 상속 받아서 기능을 확장시켰다.
  }
  ```

  ```java
  public interface List extends Collection { // 자주사용되지는 않음
      ...
      ListIterator listIterator();
      ...
  }// Set에서는 사용할 수 없다.(Set은 순서가 없기 때문) list 타입만 가능
  ```

  <img src = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fk.kakaocdn.net%2Fdn%2Fcflbyi%2FbtqFeDF4YSW%2FskD1ZHFYZNA9xOKKGUOzRk%2Fimg.png" />

#### Arrarys

배열을 다루기 편리한 메서드(static) 제공 (끝에 s가 붙은 것은 static 메서드 제공 object, collections)

1. 배열의 출력 - toString()

   ```java
   public class ArraysTest{
       
       public static void main(String[] args){
           
           int[] arr = {3, 1, 5, 2, 9};
           System.out.println(Arrays.toString(arr));
       }
   }
   ```

2. 다차원 배열의 비교와 출력 - deepToString() , equals(), deepEquals()

   ```java
   int[] arr = {0, 1, 2, 3, 4}; // 1차원 배열
   int[][] arr2D = {{11, 12}, {21, 22}}; // 2차원 배열
   
   System.out.println(Arrays.toString(arr)); // {0, 1, 2, 3, 4}
   System.out.println(Arrays.deepToString(arr2D)); // {{11, 12}, {21, 22}};
   ```

   ```java
   String[][] str2D = new String{{"aaa", "bbb"}, {"AAA", "BBB"}};
   String[][] str2D2 = new String{{"aaa", "bbb"}, {"AAA", "BBB"}};
   
   System.out.println(Arrays.equals(str2D, str2D2)); // false
   System.out.println(Arrays.deepEquals(str2D, str2D2)); // true
   ```

   ```java
   public static boolean equals(int[] a, int[] a2) {
       if (a==a2)
           return true;
       if (a==null || a2==null)
           return false;
   
       int length = a.length;
       if (a2.length != length)
           return false;
   
       for (int i=0; i<length; i++)
           if (a[i] != a2[i])
               return false;
   
       return true;
   }
   ```

   **Arrays.eqauls() 는 2차원 배열 두개를 파라미터로 넘기면 2차원 배열의 원소들(1차원 배열들)의 번지를 비교합니다. 이 때 원소들이 다시 배열일 것을 감안하지 않습니다.**

   **하지만 Arrays.deepEquals() 는 2차원 배열을 넘기더라도 다시 내부에서 원소들이 배열일 것을 감안하여 비교합니다. (배열이면 계속해서 deepEquals 를 재귀합니다.)**

   

3. 배열의 복사 - copyOf() , copyOfRange()

   - copyOf() 는 배열의 처음부터 끝까지 복사( 인덱스를 지정한다면 마지막 인덱스를 제외하고 복사)
   - copyOfRange() 는 배열의 첫 시작부터 끝을 지정해서 범위로 복사 할 수 있다.
   - 배열을 복사하면서 새로 생성해 주기 때문에 따로 생성을 안해줘도 되서 편리하다

```java
int[] arr = {0, 1, 2, 3, 4};
int[] arr2 = Arrays.copyOf(arr, arr.length); // arr2 = {0, 1, 2, 3, 4}
int[] arr3 = Arrays.copyOf(arr, 3); // 0 ~ 2 까지 arr3 = {0, 1, 2}

int[] arr5 =Arrays.copyOfRange(arr, 2, 4); // arr4 = {2, 3}
int[] arr6 = Arrays.copyOfRange(arr, 0, 7); // arr4 = {0, 1, 2, 3, 4, 0, 0}

```

- 배열 채우기 - fill(), setAll()

```java
int[] arr = new int[5];
Arrays.fill(arr, 9); // arr = {9, 9, 9, 9, 9}
Arrays.setAll(arr, () -> (int)(Math.random() * 5) + 1); // arr = {1, 5, 2, 1, 1}
```

- 배열을 List로 변환 - asList(Object... a)

  읽기 전용으로 List 컬렉션을 반환

  ```java
  List list = Arrays.asList(new Integer[]{1, 2, 3, 4, 5}); // list = [1, 2, 3, 4, 5]
  List list = Arrays.asList(1, 2, 3, 4, 5);
  list.add(6); // UnsupportedOperationException 예외 발생. list의 크기를 변경할 수 없음.
  ```

  ```java
  List list = new ArrayList(Arrays.asList(1, 2, 3, 4, 5)); //변경가능한 ArrayList생성
  list. add(6); // Ok
  ```

- 배열의 정렬과 검색 - sort(), binarySearch()

  ```java
  int[] arr = {3, 2, 0, 1, 4}; // 정렬되지 않은 배열
  int idx = Arrays.binarySearch(arr, 2); // idx = -5 잘못된 결과일때 음수를 반환
  
  // binaryserach는 정렬을 하고 나서 사용해야 됨
  Arrays.sort(arr); // 배열 arr을 정렬한다.
  System.out.println(Arrays.toString(arr)); // {0, 1, 2, 3, 4}
  int idx = Arrays.binarysearch(arr, 2); // idx = 2 올바른 결과
  ```



#### Comparator 와 Comparable

배열은 Arrays.sort() , 컬렉션은 Collections.sort() 로 정렬할 수 있다. 객체들을 정렬할 때는 sort 메서드 만으로는 부족하다.

객체의 정렬 기준을 정해주어야 한다.(오름차순인지 내림차순인지) 

- 객체를 정렬하는데 필요한 메서드를 정의한 인터페이스

  ```
  Comparable 기본 정렬기준을 구현하는데 사용
  Comparator 기본 정렬기준 외에 다른기준으로 정렬하고자할 때 사용
  ```

  ```java
  public static void main(String[] args){
      Integer[] arr = {30, 50, 10, 40, 20};
      
      Arrays.sort(arr); // 기본 정렬기준(Comparable)으로 정렬, 오름차순
      System.out.println(Arrays.toString(arr)); // {10, 20, 30, 40, 50}
      
      // sort(Object[] objArr, Comparator c)
      Arrays.sort(arr, new DescComp()); // DescComp에 구현된 정렬기준으로 정렬, 내림차순정렬
      System.out.println(Arrays.toString(arr)); // {50, 40, 30, 20, 10}
  }
  ```

  ```java
  public final class Integer extends Number implements Comparable{
      ...
      public int compareTo(Integer anotherInteger) {
          int v1 = this.value;
          int v2 = anoterInteger.value;
          // 같으면 0, 오른쪽 값이 크면 -1, 작으면 1을 반환
          return (v1 <  v2 ? -1 : (v1 ==v2 ? 0: 1));
      }
      ...
  }
  ```

  ```java
  class DescComp implements Comparator {
      public int compare(Object o1, Object o2) {
          if(!(o1 instanceof Integer)) return -1;
          if(!(o2 instanceof Integer)) return -1;
          
          Integer i = (Integer)o1;
          Integer i2 = (Integer)o2;
          return i.compareTo(i2) * -1; //기본 정렬방식의 반대 , 내림차순
          // OR i2.compareTo(i)
      }
  }
  ```

- 정렬 - 두 값을 비교해서 자리 바꿈을 반복하는 것

- 정렬 방법 - 버블정렬, 선택정렬, 삽입정렬, 퀵정렬, 쉘정렬

  ```java
  static void sort(Object[] objArr) {
      for (int i = 0; i < objArr.length-1; i++) {
          for(int j = 0; j < objArr.length-1-i; j++) {
              Comparable c = (Comparable)objArr[j];
              Compatable c2 = (Comparable)objArr[j+1];
              
              if(c.compareTo(c2) > 0) {
                  Object tmp = objArr[j];
                  objArr[j] = objArr[j+1];
                  objArr[j+1] = tmp;
              }
          }
      }
  } // 버블정렬
  ```

  

