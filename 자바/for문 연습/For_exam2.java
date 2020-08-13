package For_pratice;

import java.util.Scanner;

public class For_exam2 {

    public void Print_Star2(int num){
        if (num == 0){
            System.out.println("출력기능이 없습니다.");
        }
        else if(num <0){
            for (int i = num; i < 0; i++){
                for (int j = i; j < 0; j++ ){
                    System.out.print("*");
                }
                System.out.println();
            }
        }
        else{
            for (int i = 1; i<num+1; i++){
                for (int j = 0; j < i; j++){
                    System.out.print("*");
                }
                System.out.println();
            }
        }
    }

}
