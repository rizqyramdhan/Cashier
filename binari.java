import java.util.Scanner;

public class binari {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        System.out.print("Enter a decimal integer: ");
        int angka = input.nextInt();

        String binaryResult = decimalToBinary(angka);
        
        System.out.println("The binary representation is " + binaryResult);
    }

    public static String decimalToBinary(int decimal) {
        if (decimal == 0) {
            return "0";
        }

        String binary = "";
        int n = decimal;
        while (n > 0) {
            int sisa = n % 2;
            binary = sisa + binary; 
            n /= 2;
        }
        return binary;
    }
}