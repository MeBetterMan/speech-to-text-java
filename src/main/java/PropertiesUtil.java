import java.io.*;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Scanner;

public class PropertiesUtil {
    public static String readKeyFromResources(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("config");
        String result = bundle.getString(key).trim(); //

        return result;
    }

    public static String readKeyFromConfig(String key) {
        Properties properties = new Properties();
        // 使用InPutStream流读取properties文件
        BufferedReader bufferedReader = null;
        try {
            String base_dir = System.getProperty("user.dir");
            System.out.println("current path:" + base_dir);
            bufferedReader = new BufferedReader(new FileReader(base_dir + "/config/config.properties"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.load(bufferedReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取key对应的value值
        return properties.getProperty(key);
    }

    public static void main(String[] args) {
        ResourceBundle bundle = ResourceBundle.getBundle("log4j");
        String result = bundle.getString("log4j.rootLogger").trim(); //
        System.out.println(result);
    }
}
