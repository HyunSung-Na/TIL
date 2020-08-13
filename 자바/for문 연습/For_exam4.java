package For_pratice;

import java.util.Scanner;

public class For_exam4 {
    public void continputCharacter(){
        Scanner sc = new Scanner(System.in);
        System.out.print("문자열 입력 :");
        String exam = sc.nextLine();
        boolean a = true; //영문자인지 판단하기 위한 변수
        char chinput; // 문자열을 분리해서 1글자씩 담을 변수
        while (a) {
            for (int i = 0; i < exam.length(); i++) {
                chinput = exam.charAt(i);
                // 16진수로 0x41 = A, 0x7A = z 사이에 값이 들어오면 a = false 로 만들어주고 while문 종료
                if (chinput >= 0x41 && chinput <= 0x7A) {
                    a = false;
                }
                else {
                    System.out.println("영문자가 아닙니다. 다시 입력해주세요");
                    System.out.print("문자열 입력 :");
                    exam = sc.nextLine();
                }
            }

        }
        System.out.print("문자입력 :");
        String input = sc.next();
        char input_exam = input.charAt(0);
        int sum = 0;
        for (int i = 0; i < exam.length(); i++) {
            chinput = exam.charAt(i);
            if (input_exam == chinput){
                sum += 1;
            }

        }
        System.out.printf("포함된 갯수 : %d",sum);

    }

}
