package TestNG;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import io.github.bonigarcia.wdm.WebDriverManager;

public class ZoomIn_ZoomOut_Function {
    WebDriver driver;

    @Test
    public void zoomTest() throws InterruptedException {
        driver.get("https://tuyensinh247.com/"); // Thay bằng URL cần kiểm thử
        JavascriptExecutor js = (JavascriptExecutor) driver;

        int[] zoomLevels = {50, 100, 150, 200}; // Mức zoom cần kiểm tra

        for (int expectedZoom : zoomLevels) {
            System.out.println("\n🔹 Testing Zoom Level: " + expectedZoom + "%");

            // Cách 1: Sử dụng zoom nếu trình duyệt hỗ trợ
            js.executeScript("document.body.style.zoom = '" + expectedZoom + "%'");
            
            // Cách 2: Dự phòng nếu zoom không hoạt động, dùng transform
            js.executeScript("document.body.style.transform = 'scale(" + (expectedZoom / 100.0) + ")';");
            js.executeScript("document.body.style.transformOrigin = '0 0';");
            
            // Chờ trình duyệt cập nhật mức zoom
            Thread.sleep(1000);

            // Kiểm tra mức zoom thực tế
            String actualZoom = (String) js.executeScript("return document.body.style.zoom;");
            actualZoom = actualZoom.isEmpty() ? "100%" : actualZoom; // Nếu rỗng, mặc định là 100%
            
            System.out.println("📌 Expected Zoom Level: " + expectedZoom + "%");
            System.out.println("📌 Actual Zoom Level: " + actualZoom);

            // So sánh Expected vs Actual Zoom Level
            if (actualZoom.equals(expectedZoom + "%")) {
                System.out.println("✅ Test Passed: Zoom level is correct!");
            } else {
                System.out.println("❌ Test Failed: Expected " + expectedZoom + "% but got " + actualZoom);
            }

            // Reset zoom trước khi chuyển sang mức tiếp theo
            js.executeScript("document.body.style.zoom = '100%'");
            js.executeScript("document.body.style.transform = 'scale(1)';");
        }
    }

    @BeforeMethod
    public void beforeMethod() {
        System.out.println("Bắt Đầu Test Chức Năng Zoom in và Zoom out");
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @AfterMethod
    public void afterMethod() {
        if (driver != null) {
            driver.quit();
        }
    }
}
