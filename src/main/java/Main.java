import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;

public class Main {

    private static String accountNum="11112222";

    public static void main(String[] args) {
        double amount = randomDouble(12, 400);
        List<String> randomBliks = getRandomBliks(5);
        List<Card> cards = getRandomCards(5);

        System.out.println("amount = "+amount);
        System.out.println("randomBliks = " + randomBliks);
        System.out.println("cards:"); cards.forEach(System.out::println);
        System.out.println("\nDo zapłaty: "+amount+" zł");

        platnosc(amount);
    }

    private static double randomDouble(double min, double max){
        Random r = new Random();
        return Math.round ((min + (max - min) * r.nextDouble()) * 100.0) / 100.0;
    }
    private static int randomInt(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }
    private static String getRandomCode(int num){
        String code = "";
        for(int j=0; j<num; j++) {
            code += randomInt(0, 9);
        }
        return code;
    }
    private static List<String> getRandomBliks(int number){
        List<String> result = new ArrayList<>();
        for(int i=0; i<number; i++){
            result.add(getRandomCode(4));
        }
        return result;
    }
    private static List<Card> getRandomCards(int number){
        List<Card> result = new ArrayList<>();
        for(int i=0; i<number; i++){
            String num = "";
            String pin = getRandomCode(4);
            for(int j=0; j<4; j++){
                num += getRandomCode(4);
                if(j != 3) num += " ";
            }
            result.add(new Card(num, pin));
        }
        return result;
    }
    private static double round(double amount) {
        return Math.round ((amount) * 100.0) / 100.0;
    }

    private static void platnosc(double amount){
        System.out.print("(1) gotówka\n(2) karta\n(3) blik\nPodaj sposób płatności: ");
        Scanner sc = new Scanner(System.in);
        String input = sc.nextLine();
        try {
            int inputInt = Integer.parseInt(input);
            switch (inputInt){
                case 1:
                    gotowka(amount);
                    break;
                case 2:
                    System.out.println("\nKarta (nieobsługiwane)\n");
                    platnosc(amount);
                    break;
                case 3:
                    blikPay(amount);
                    break;
                default:
                    System.out.println("\n---Not valid---\n");
                    platnosc(amount);
            }
        }catch (NumberFormatException e){
            System.out.println("\n---Not valid---\n");
            platnosc(amount);
        }
    }

    private static void gotowka(double amount){
        System.out.print("Podaj banknoty (rozdzielone średnikiem): ");
        Scanner sc = new Scanner(System.in);
        String banknoty = sc.nextLine();
        int sum = sumBanknoty(banknoty);

        gotowka2(amount, sum);

    }

    private static void gotowka2(double amount, int sum){
        if(sum==amount){
            System.out.println("\nTransakcja zakończona.");
        }
        else if(sum>amount){
            System.out.println("\nTransakcja zakończona.\nReszta: "+ round(sum-amount) + "zł");
        }
        else{
            System.out.println("\nZa mało gotowki (brakuje: "+round(amount-sum)+" zł).\nDołóż banknoty lub:");
            System.out.println("X: Anuluj transakcje\nZ: Zmień sposób płatności");
            Scanner sc = new Scanner(System.in);
            String nextStep = sc.nextLine();
            if(nextStep.equals("X")){
                System.out.println("Transakcja zakończona");
            }
            else if(nextStep.equals("Z")){
                platnosc(amount);
            }
            else{
                sum += sumBanknoty(nextStep);
                gotowka2(amount, sum);
            }
        }
    }
    private static int sumBanknoty(String banknoty){
        int sum = 0;
        for(String banknot : banknoty.split(";")){
            int banknotInt = Integer.parseInt(banknot);
            sum += banknotInt;
        }
        return sum;
    }

    private static void blikPay(double amount){
        Scanner sc = new Scanner(System.in);
        System.out.print("Podaj kod blik: ");
        String blik = sc.nextLine();
        if(blik.length() == 6){
            try {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url(String.format(Locale.US, "http://192.168.1.103:8080/blik/pay/"+blik+"/"+amount+"/"+accountNum))
                        .post(new RequestBody() {
                            @Override
                            public void writeTo(@NotNull BufferedSink bufferedSink) throws IOException {

                            }

                            @Nullable
                            @Override
                            public MediaType contentType() {
                                return null;
                            }
                        })
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    System.out.println(response.body().string());

                }

            }catch (NumberFormatException | IOException e){
                System.out.println("Błędny kod. Spróbuj ponownie");
                blikPay(amount);
            }
        }
        else{
            System.out.println("Błędny kod. Spróbuj ponownie");
            blikPay(amount);
        }
    }
}
