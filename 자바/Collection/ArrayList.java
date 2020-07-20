package Collection;

public class ArrayList {
    private Object[] data; // ArrayList는 객체만 사용할 수 있기 때문에 Object객체 생성
    private int size;
    private int index;

    public ArrayList(){
        this.size = 1;
        this.data = new Object[this.size]; // size 1로 배열 객체생성
        this.index = 0; // index 0번째 부터 데이터 삽입
    }

    public void add(Object obj){ //배열의 요소를 추가해주는 함수
        System.out.println("index: "+this.index + ", size: "+this.size + ", datasize: "+this.data.length);
        if(this.index == this.size-1){  // 배열이 꽉찬 경우 doubling을 실행
            doubling();
        }
        data[this.index] = obj; //현재 index에 object를 삽입
        this.index++; // 새로운 배열 추가를 위해 빈 배열 index로 이동
    }

    private void doubling(){ //배열의 크기를 늘려주는 함수
        this.size = this.size * 2 ;
        Object[] newData = new Object[this.size]; // 기존 배열의 2배 크기의 new배열 생성
        for(int i = 0; i < data.length; i++){
            newData[i] = data[i]; // 기존 배열의 값을 new배열로 복사
        }
        this.data = newData; //기존 배열의 참조변수를 현재 newData로 변경
        System.out.println("***index: "+this.index + ", size: "+this.size + ", datasize: "+this.data.length);
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
        System.out.println("data removed: " + this.data[i]);
        for (int x = i; x < this.data.length -1; x++) {
            data[x] = data[x + 1]; //삭제할 데이터를 기준으로 앞으로 덮어쓴다.
        }
        this.index--; // index를 하나 줄여준다
    }
}

