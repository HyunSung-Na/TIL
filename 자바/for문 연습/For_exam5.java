package For_pratice;

public class For_exam5 {
    public void for_Diamondstar(){
        for (int i = 1, n = 5; i <= 9; i++){
            for (int j = 0; j <= n+2*(6-n) - 1; j++){
                System.out.print((j <= n)? " ":"*");
            }
            System.out.println();
            if(i < 5){
                n--;
            }
            else{
                n++;
            }
        }
    }
}
