package TestNG;

import java.io.*;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class Function_Dang_Ky_Hoc_Thu {
    WebDriver driver;
    String excelFilePath = "src\\TestNG\\TestCase\\TestCase_Dang_Ky_Hoc_Thu.xlsx";
    String jsonFilePath = "src\\TestNG\\DataJson\\datatest1.json";
    String tenGiaoVien = "Thầy Nguyễn Công Chính";
    boolean dropdownPrinted = false;

    @BeforeMethod
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless"); // Chạy Chrome không hiển thị UI
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.setUnhandledPromptBehaviour(UnexpectedAlertBehaviour.ACCEPT);
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://tuyensinh247.com/");
        driver.findElement(By.xpath("//*[@id=\"wrapper\"]/div[1]/div[5]/div/ul/li[5]/a")).click();
        
        List<WebElement> giaoVienElements = driver.findElements(By.xpath("//div[@class='tc-info-teacher fl']/ul/li/p"));

        for (WebElement element : giaoVienElements) {
            if (element.getText().trim().equals(tenGiaoVien)) {
                WebElement parent = element.findElement(By.xpath("./ancestor::a"));
                WebElement xemNgayButton = parent.findElement(By.xpath(".//span[contains(text(), 'Xem ngay')]"));
                xemNgayButton.click();

                System.out.println("Đã nhấn vào nút 'Xem ngay' của " + tenGiaoVien);
                break;
            }
        }
    }

    @Test
    public void testDangKyHocThu() throws IOException, ParseException, InterruptedException {
        JSONParser jsonParser = new JSONParser();
        FileReader reader = new FileReader(jsonFilePath);
        JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);
        JSONArray testData = (JSONArray) jsonObject.get("User_data_test");
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
            String phone = (String) testEntry.get("phone");
            String className = (String) testEntry.get("class");
            String expectedMessage = row.getCell(4).getStringCellValue();

            System.out.println("🔹 Đang test với dữ liệu: " + fullname + ", " + phone + ", " + className);

            if (!dropdownPrinted) {
                printDropdownOptions();
                dropdownPrinted = true;
            }

            WebElement nhapten =  driver.findElement(By.id("fullname"));
            nhapten.clear();
            nhapten.sendKeys(fullname);
            
            WebElement nhapsdt = driver.findElement(By.id("phone"));
            nhapsdt.clear();
            nhapsdt.sendKeys(phone);

            WebElement dropdown = driver.findElement(By.id("advisory_problem"));
            Select selectClass = new Select(dropdown);
            selectClass.selectByVisibleText(className);

            WebElement selectedOption = selectClass.getFirstSelectedOption();
            System.out.println("Đã chọn lớp: " + selectedOption.getText());

            driver.findElement(By.xpath("//*[@id=\"bannerForm\"]/div/div[2]/form/div/button")).click();
            Thread.sleep(3000); 

            handleAlert(expectedMessage);

            rowIndex++;
        }

        fis.close();
        workbook.close();
    }

    private void printDropdownOptions() {
        WebElement selectElement = driver.findElement(By.id("advisory_problem"));
        Select select = new Select(selectElement);
        List<WebElement> options = select.getOptions();

        System.out.println("📌 Danh sách các options trong dropdown:");
        for (WebElement option : options) {
            System.out.println("- " + option.getText());
        }
    }

    private void handleAlert(String expectedMessage) {
        try {
            Thread.sleep(2000); // Chờ alert xuất hiện
            Alert alert = driver.switchTo().alert();
            String actualMessage = alert.getText();
            alert.accept();

            if (actualMessage.equals(expectedMessage)) {
                System.out.println("✅ Test Passed: " + actualMessage);
            } else {
                System.out.println("❌ Test Failed! Expected: " + expectedMessage + ", but got: " + actualMessage);
            }
        } catch (NoAlertPresentException e) {
            System.out.println("⚠ Không có thông báo nào xuất hiện.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @AfterMethod
    public void teardown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
