package TestNG;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Function_DangKy {
    WebDriver driver;
    String jsonFilePath = "src\\TestNG\\DataJson\\datatest2.json";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Chạy không hiển thị UI
        options.addArguments("--disable-gpu"); // Tắt GPU để tăng hiệu suất
        options.addArguments("--window-size=1920,1080"); // Giữ kích thước chuẩn
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://www.flipkart.com/");
        driver.findElement(By.xpath("//*[@id=\"container\"]/div/div[1]/div/div/div/div/div[1]/div/div/div/div[1]/div[1]/header/div[2]/div[4]/div/a[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div/div[2]/div/div/div[2]/button[1]")).click();
        driver.findElement(By.xpath("//*[@id=\"app\"]/div[1]/div/section/section/div/div[2]/div[2]/button")).click();
    }


    @Test
    public void testDangKy() throws IOException, ParseException, InterruptedException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(jsonFilePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONArray testData = (JSONArray) jsonObject.get("User_data_test");
        reader.close();

        Iterator<JSONObject> iterator = testData.iterator();

        while (iterator.hasNext()) {
            JSONObject testEntry = iterator.next();
            String mobileNumber = (String) testEntry.get("mobileNumber");
            String email = (String) testEntry.get("email");
            String gst = (String) testEntry.get("gst");

            System.out.println("🔹 Đang test với dữ liệu: " + mobileNumber + ", " + email + ", " + gst);

            fillAndSubmitForm(mobileNumber, email, gst);
            readErrorMessages();
            Thread.sleep(2000);
        }
    }

    private void fillAndSubmitForm(String mobileNumber, String email, String gst) throws InterruptedException {
    	Thread.sleep(2000);
    	driver.findElement(By.name("mobileNumber")).sendKeys("");
        driver.findElement(By.name("mobileNumber")).sendKeys(mobileNumber);
        Thread.sleep(1000);
        driver.findElement(By.name("email")).sendKeys("");
        driver.findElement(By.name("email")).sendKeys(email);
        Thread.sleep(1000);
        driver.findElement(By.name("gst")).sendKeys("");
        driver.findElement(By.name("gst")).sendKeys(gst);
        Thread.sleep(1000);
        driver.findElement(By.xpath("//*[@id=\"app-container\"]/div/div[1]/div/form/footer/button")).click();
        Thread.sleep(3000);

    }

    private void readErrorMessages() {
        List<WebElement> errorMessages = driver.findElements(By.xpath("//p[contains(@class, 'ErrorInfo')]"));
        if (errorMessages.isEmpty()) {
            System.out.println("✅ Không có lỗi nào hiển thị dưới các ô nhập liệu.");
        } else {
            for (WebElement error : errorMessages) {
                System.out.println("⚠ Lỗi nhập liệu: " + error.getText());
            }
        }
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
