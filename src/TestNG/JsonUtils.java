package TestNG;

import java.io.FileReader;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonUtils {
    // Hàm đọc file JSON và trả về danh sách dữ liệu
    public static JSONArray readJsonData(String filePath) {
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader(filePath);
            JSONObject jsonObject = (JSONObject) parser.parse(reader);
            return (JSONArray) jsonObject.get("User_data_test"); // Đọc danh sách dữ liệu trong file JSON
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
