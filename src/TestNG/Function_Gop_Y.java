 package TestNG;

 import java.io.*;
 import java.util.Iterator;
 import org.apache.poi.ss.usermodel.*;
 import org.apache.poi.xssf.usermodel.XSSFWorkbook;
 import org.json.simple.JSONArray;
 import org.json.simple.JSONObject;
 import org.json.simple.parser.JSONParser;
 import org.json.simple.parser.ParseException;
 import org.openqa.selenium.Alert;
 import org.openqa.selenium.By;
 import org.openqa.selenium.WebDriver;
 import org.openqa.selenium.chrome.ChromeDriver;
 import org.openqa.selenium.chrome.ChromeOptions;
 import org.testng.annotations.AfterMethod;
 import org.testng.annotations.BeforeMethod;
 import org.testng.annotations.Test;
 import io.github.bonigarcia.wdm.WebDriverManager;

public class Function_Gop_Y {
	WebDriver driver;
    String excelFilePath = "src\\TestNG\\TestCase\\TestCase cho chuc nang goi y.xlsx";
    String jsonFilePath = "src\\TestNG\\DataJson\\datatest.json";

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Chạy Chrome không hiển thị UI
        options.addArguments("--disable-gpu"); // Tắt GPU tăng hiệu suất
        options.addArguments("--window-size=1920,1080"); // Giữ kích thước chuẩn
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://tuyensinh247.com/");
    }

    @Test
    public void testGopY() throws IOException, InterruptedException, ParseException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(jsonFilePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONArray testData = (JSONArray) jsonObject.get("Address_Data_Testing");
        reader.close();
        
        FileInputStream fis = new FileInputStream(new File(excelFilePath));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheet("Sheet1");

        Iterator<JSONObject> iterator = testData.iterator();
        int rowIndex = 1;

        while (iterator.hasNext() && rowIndex <= sheet.getLastRowNum()) {
            JSONObject testEntry = iterator.next();
            Row row = sheet.getRow(rowIndex);
            if (row == null) continue;
            
            String fullname = (String) testEntry.get("fullname");
            String email = (String) testEntry.get("email");
            String mobile = (String) testEntry.get("mobile");
            String description = (String) testEntry.get("description");
            String expectedMessage = row.getCell(5).getStringCellValue();
            
            System.out.println("🔹 Đang test với dữ liệu: " + fullname + ", " + email + ", " + mobile + ", " + description);
            
            driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[1]/div[1]/div/div[2]/ul/li[4]/a")).click();
            Thread.sleep(2000);
            driver.findElement(By.id("fullname")).sendKeys(fullname);
            driver.findElement(By.id("email")).sendKeys(email);
            driver.findElement(By.id("mobile")).sendKeys(mobile);
            driver.findElement(By.id("description")).sendKeys(description);
            
            driver.findElement(By.xpath("//*[@id=\"cboxLoadedContent\"]/div/div/p[2]/button/span/strong")).click();
            Thread.sleep(2000);
            
            Alert alert = driver.switchTo().alert();
            String actualMessage = alert.getText();
            alert.accept();
            
            System.out.println("Expected: " + expectedMessage + " || " + "Actual Result: " + actualMessage);
            
            String result = expectedMessage.equals(actualMessage) ? "PASS" : "FAIL";
            System.out.println("Test Result: " + result);
           
            
            rowIndex++;
        }
        
        FileOutputStream fos = new FileOutputStream(new File(excelFilePath));
        workbook.write(fos);
        fos.close();
        fis.close();
        workbook.close();
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
