import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Scanner;

public class CSVProductsData implements ProductsData{
    private final String dataPath;
    public CSVProductsData() throws FileNotFoundException {
        this.dataPath = "Products.txt";
    }

    public CSVProductsData(String dataPath) throws FileNotFoundException {
        this.dataPath = dataPath;
    }

    public Product getProductByCode(String code){
        Scanner productsScanner = null;
        try {
            productsScanner = new Scanner(new File(dataPath));
            while (productsScanner.hasNextLine()){
                String[] data = productsScanner.nextLine().split(";");
                Product product = new Product(data[0], data[1], BigDecimal.valueOf(Double.parseDouble(data[2])));
                if(product.getCode().equals(code)){
                    productsScanner.close();
                    return product;
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Nie znaleziono bazy produktow");
        }
        return null;
    }
}
