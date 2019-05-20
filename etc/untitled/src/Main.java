public class Main {

    public static void main(String[] args) {
            int a = 3, b = 4, c = 0, hap = 0;
            int temp = 0, temp2 = 0, temp3 = 0;
            do{
                c++;
                hap += a++ - --b %c;
            } while (c <4);
            System.out.printf("%d\n", hap);

    }
}
