package generic;

public class GenericPrinterTest {

    public static void main(String[] args) {
        GenericPrinter<Powder> powderPrinter = new GenericPrinter<Powder>();
        Powder powder = new Powder();
        powderPrinter.setMaterial(powder);
        System.out.println(powderPrinter);

        GenericPrinter<Plastic> plasicPrinter = new GenericPrinter<Plastic>();
        Plastic plastic = new Plastic();
        plasicPrinter.setMaterial(plastic);
        System.out.println(plasicPrinter);
    }
}
