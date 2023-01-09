import okhttp3.*;
import okio.BufferedSink;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    private static String accountNum="11112222";
    private static String ip = "localhost";

    public static void main(String[] args) {
        double amount = randomDouble(12, 400);
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
        System.out.println("\nDo zapłaty: "+amount+" zł");
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
                OkHttpClient client = new OkHttpClient.Builder()
                        .readTimeout(40, TimeUnit.SECONDS)
                        .build();

                Request request = new Request.Builder()
                        .url(String.format(Locale.US, "http://"+ip+":8080/blik/pay/"+blik+"/"+amount+"/"+accountNum))
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

                System.out.println("Oczekiwanie...");
                try (Response response = client.newCall(request).execute()) {
                    BlikTransactionStatus blikTransactionStatus = BlikTransactionStatus.valueOf(response.body().string().replace("\"", ""));
                    if(blikTransactionStatus == BlikTransactionStatus.CONFIRMED){
                        System.out.println("Transakcja potwierdzona");
                        System.exit(0);
                    }
                    else if(blikTransactionStatus == BlikTransactionStatus.BAD_BLIK){
                        System.out.println("Zły kod BLIK");
                    }
                    else if(blikTransactionStatus == BlikTransactionStatus.NO_CONFIRMATION){
                        System.out.println("Brak potwierdzenia. Anulowanie transakcji\n");
                    }
                    else if(blikTransactionStatus == BlikTransactionStatus.BAD_TARGET_ACCOUNT){
                        System.out.println("Złe konto odbiorcy");
                    }
                    else if(blikTransactionStatus == BlikTransactionStatus.NOT_ENOUGHT_MONEY){
                        System.out.println("Niewystarczające środki na koncie");
                    }
                    platnosc(amount);
                }

            }catch (NumberFormatException | IOException e){
                System.out.println("Błędny kod. Spróbuj ponownie");
                blikPay(amount);
            }catch (IllegalArgumentException e){
                System.out.println("Błąd banku");
                e.printStackTrace();
            }
        }
        else{
            System.out.println("Błędny kod. Spróbuj ponownie");
            blikPay(amount);
        }
    }
}
