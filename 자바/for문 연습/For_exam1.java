package For_pratice;

import java.util.Scanner;

public class For_exam1 {


    public void Printstar1(int num){
        if (num <0){
            System.out.println("양수가 아닙니다.");
        }
        else{
            for(int i = 1; i < num+1; i++){
                for (int j = 0; j<i-1; j++){
                    System.out.print("*");
                }
                System.out.printf("%d",i);
                System.out.println();
            }
        }
    }


}


