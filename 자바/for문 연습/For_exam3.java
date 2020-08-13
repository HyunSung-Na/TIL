package For_pratice;

import java.util.ArrayList;
import java.util.Scanner;
// 상품과 겨격과 상품개수를 변수에 저장
public class For_exam3 {
    public void select_menu(){
        String bul = "불고기버거";
        int bul_num = 0;
        int bul_sum = 3500;
        String chi = "치킨버거";
        int chi_num = 0;
        int chi_sum = 3200;
        String gam = "감자튀김";
        int gam_num = 0;
        int gam_sum = 1000;
        String chee = "치즈스틱";
        int chee_num = 0;
        int chee_sum = 400;
        String salad = "샐러드";
        int salad_num = 0;
        int salad_sum = 2000;
        String cok = "콜라";
        int cok_num = 0;
        int cok_sum = 700;
        String aid = "에이드";
        int aid_num = 0;
        int aid_sum = 1200;
        String coffee = "커피";
        int coffee_num = 0;
        int coffee_sum = 1000;
        int ex = 0;
        String end = "n";
        ArrayList<String> menu = new ArrayList<>();
        ArrayList<Integer> sum = new ArrayList<>();
        ArrayList<Integer> total = new ArrayList<>();
        int total_sum = 0;
        Scanner sc = new Scanner(System.in);

        do {
            System.out.println("*** 메뉴를 선택하세요 ***");
            System.out.println();
            System.out.println("햄버거 ***************");
            System.out.printf("1. %s\t3500원\n",bul);
            System.out.printf("2. %s\t3200원\n",chi);
            System.out.println("추가 ****************");
            System.out.printf("3. %s\t1000원\n",gam);
            System.out.printf("4. %s\t400원\n",chee);
            System.out.printf("5. %s\t2000원\n",salad);
            System.out.println("음료수 ***************");
            System.out.printf("6. %s\t700원\n",cok);
            System.out.printf("7. %s\t1200원\n",aid);
            System.out.printf("8. %s\t1000원\n",coffee);
            System.out.println("********************");
            System.out.print("메뉴선택 : ");
            int num = sc.nextInt();
            switch (num){
                case 1:
                    System.out.printf("%s를 선택하셨습니다.\n",bul);
                    System.out.print("수량은? ");
                    bul_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",bul_num);
                    // ArrayList에 각각의 결과를 삽입 total은 메뉴의 가격과 개수를 곱한 값으로 저장
                    menu.add(bul);
                    sum.add(bul_num);
                    total.add(bul_num*bul_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end = sc.next();
                    // end = "n" 으로 변수지정 해놓고 equals로 내용 비교 후 같은 문자열일 경우 ex를 1로 만들어줘서 while문 탈출
                    if (ex_end.equals(end))
                        ex = 1;
                    break;
                case 2:
                    System.out.printf("%s를 선택하셨습니다.\n",chi);
                    System.out.print("수량은? ");
                    chi_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",chi_num);
                    menu.add(chi);
                    sum.add(chi_num);
                    total.add(chi_num*chi_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end1 = sc.next();
                    if (ex_end1.equals(end))
                        ex = 1;
                    break;
                case 3:
                    System.out.printf("%s를 선택하셨습니다.\n",gam);
                    System.out.print("수량은? ");
                    gam_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",gam_num);
                    menu.add(gam);
                    sum.add(gam_num);
                    total.add(gam_num*gam_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end2 = sc.next();
                    if (ex_end2.equals(end))
                        ex = 1;
                    break;
                case 4:
                    System.out.printf("%s를 선택하셨습니다.\n",chee);
                    System.out.print("수량은? ");
                    chee_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",chee_num);
                    menu.add(chee);
                    sum.add(chee_num);
                    total.add(chee_num*chee_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end3 = sc.next();
                    if (ex_end3.equals(end))
                        ex = 1;
                    break;
                case 5:
                    System.out.printf("%s를 선택하셨습니다.\n",salad);
                    System.out.print("수량은? ");
                    salad_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",salad_num);
                    menu.add(salad);
                    sum.add(salad_num);
                    total.add(salad_num*salad_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end4 = sc.next();
                    if (ex_end4.equals(end))
                        ex = 1;
                    break;
                case 6:
                    System.out.printf("%s를 선택하셨습니다.\n",cok);
                    System.out.print("수량은? ");
                    cok_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",cok_num);
                    menu.add(cok);
                    sum.add(cok_num);
                    total.add(cok_num*cok_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end5 = sc.next();
                    if (ex_end5.equals(end))
                        ex = 1;
                    break;
                case 7:
                    System.out.printf("%s를 선택하셨습니다.\n",aid);
                    System.out.print("수량은? ");
                    aid_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",aid_num);
                    menu.add(aid);
                    sum.add(aid_num);
                    total.add(aid_num*aid_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end6 = sc.next();
                    if (ex_end6.equals(end))
                        ex = 1;
                    break;
                case 8:
                    System.out.printf("%s를 선택하셨습니다.\n",coffee);
                    System.out.print("수량은? ");
                    coffee_num = sc.nextInt();
                    System.out.printf("%d개 주문하셨습니다.\n",coffee_num);
                    menu.add(coffee);
                    sum.add(coffee_num);
                    total.add(coffee_num*coffee_sum);
                    System.out.println();
                    System.out.println("추가 주문하시겠습니까?(y/n)");
                    String ex_end7 = sc.next();
                    if (ex_end7.equals(end))
                        ex = 1;
                    break;
            }


        }while (ex != 1);
        System.out.println("* 주문 하신 정보는 다음과 같습니다. *");
        System.out.println("-----------------------------------------------");
        for (int i = 0; i<menu.size(); i++){
            System.out.printf("%s : %d개 - %d원\n",menu.get(i),sum.get(i),total.get(i));
            total_sum += total.get(i);
        }
        System.out.println("-----------------------------------------------");
        System.out.printf("총가격: %d원",total_sum);
    }
}
