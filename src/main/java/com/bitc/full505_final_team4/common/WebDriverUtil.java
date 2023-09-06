package com.bitc.full505_final_team4.common;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class WebDriverUtil {
  public static String WEB_DRIVER_ID  = "webdriver.chrome.driver";
//  private static String WEB_DRIVER_PATH = "C:\\chromedriver\\chromedriver.exe"; // WebDriver 경로

  public static String WEB_DRIVER_PATH = "/home/ec2-user/chromedriver-linux64/chromedriver"; // WebDriver 경로


  public static WebDriver getChromeDriver(){
    System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

    //webDriver 옵션 설정
    ChromeOptions chromeOptions = new ChromeOptions();
    chromeOptions.setHeadless(true);
    chromeOptions.addArguments("headless"); // 설정 안하면 실제 크롬창이 생성되고, 진행 순서를 보게 된다.
    chromeOptions.addArguments("--lang=ko");
    chromeOptions.addArguments("--no-sandbox");
    chromeOptions.addArguments("--disable-dev-shm-usage");
    chromeOptions.addArguments("--disable-gpu");
    chromeOptions.setCapability("ignoreProtectedModeSettings", true);

    // webDriver 생성
    WebDriver driver = new ChromeDriver(chromeOptions);
    driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);

    return driver;

  }

  // 크롤링 원하는 url을 입력
  /*public void useDriver(String url){

    // WebDriver를 해당 url 로 이동시킨다.
    driver.get(url);

    // 브라우저 이동시 생기는 로드시간을 기다린다.
    // HTTP 응답 속도보다 자바의 컴파일 속도가 더 빠르기 때문에 임의적으로 1초를 대기한다.
    driver.manage().timeouts().implicitlyWait(500, TimeUnit.MICROSECONDS); // 페이지 불러오는 여유시간
    System.out.println("=========== selenium : " + driver.getTitle());

    // 종료 메소드 별도로 불러와야함
  }*/

  public static void quit(WebDriver driver){
    if(!ObjectUtils.isEmpty(driver)){
      driver.quit();
    }
  }

  public static void close(WebDriver driver){
    if(!ObjectUtils.isEmpty(driver)){
      driver.close();
    }
  }

}
