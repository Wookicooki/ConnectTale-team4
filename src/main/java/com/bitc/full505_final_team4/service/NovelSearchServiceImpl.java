package com.bitc.full505_final_team4.service;

import com.bitc.full505_final_team4.data.repository.NovelCateRepository;
import com.bitc.full505_final_team4.data.repository.NovelPlatformRepository;
import com.bitc.full505_final_team4.data.repository.NovelRepository;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.*;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.stereotype.Service;

import javax.swing.text.Element;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.awt.*;
import java.awt.datatransfer.*;

/*@EnableJpaRepositories(basePackages = {"com.bitc.full505_final_team4.data.entity"})*/
@Service
@RequiredArgsConstructor
public class NovelSearchServiceImpl implements NovelSearchService {
  private final NovelRepository novelRepository;
  private final NovelPlatformRepository novelPlatformRepository;
  private final NovelCateRepository novelCateRepository;

  // 셀레니움을 통한 크롤링(카카오페이지)
  public static String WEB_DRIVER_ID = "webdriver.chrome.driver";
//  public static String WEB_DRIVER_PATH = "C:\\chromedriver\\chromedriver.exe";

  // 배포용 크롬드라이버 경로
  public static String WEB_DRIVER_PATH = "/home/ec2-user/chromedriver-linux64/chromedriver";
// --------------------- 카카오페이지 작품 id 가져오기 ------------------------------
  // 셀레니움을 통해 검색 결과에 따른 카카오페이지 작품 id 리스트 가져오기
  @Override
  public List<String> getKakaoSearchIdList(String searchWord) throws Exception {
    WebDriver driver;

    String searchUrl = "https://page.kakao.com/search/result?keyword=" + searchWord + "&categoryUid=11";

    List<String> kakaoSearchIdList = new ArrayList<>();

    System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

    // webDriver 옵션 설정
    ChromeOptions options = new ChromeOptions();
//    options.addArguments("--disable-popup-blocking"); // 팝업 안띄움
    options.addArguments("--headless"); // 브라우저 창 숨기고 실행
//    options.addArguments("--enable-automation");
//    options.addArguments("--window-position=-100000,-100000");
//    options.addArguments("--window-size=0,0");
//    options.addArguments("--lang=ko");
    options.addArguments("--disable-gpu");            //gpu 비활성화
//    options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음

    driver = new ChromeDriver(options);
    driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

    try {

      driver.get(searchUrl);

      // 페이지 끝까지 스크롤 내리는 로직
      while (true) {

        long lastHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.getElementById('__next').scrollHeight");


        // Scroll down to bottom
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.getElementById('__next').scrollHeight);");

        // Wait for new content to load
        Thread.sleep(150);

        long newHeight = (long) ((JavascriptExecutor) driver).executeScript("return document.getElementById('__next').scrollHeight");

        // 새로운 콘텐츠가 로드되지 않을 때, 스크롤이 끝났다고 판단하여 반복문 종료
        if (newHeight == lastHeight) {
          break;
        }
      }


      // css선택자를 활용하여 해당 태그찾기(같은 클래스값을 가진 태그가 반복되는 형태라 list<WebElement>타입으로 설정함
      List<WebElement> elements = driver.findElements(By.cssSelector(".flex-1.cursor-pointer"));

      if (elements.isEmpty()) {
        System.out.println("작품 아이디가 적힌 a태그를 못찾음");
      }
      else {
        for (WebElement element : elements) {
          // 태그 요소들 각각의 작품id값만 String으로 받아오기
          String kakaoSearchHref = element.getAttribute("href");

          int kakaoSearchIdIndex = kakaoSearchHref.lastIndexOf("/");
          String kakaoSearchId = kakaoSearchHref.substring(kakaoSearchIdIndex + 1);
          kakaoSearchIdList.add(kakaoSearchId);
        }
      }

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      driver.quit();
    }
    return kakaoSearchIdList;
  }


  // ---------------------------- 네이버 검색결과 가져오기--------------------------------
  @Override
  public Map<String, Object> getNaverSearchList(String searchWord) throws Exception {
    WebDriver driver;
    Map<String, Object> naverSearchObj = new HashMap<>();

    System.setProperty("java.awt.headless", "false");
    System.setProperty(WEB_DRIVER_ID, WEB_DRIVER_PATH);

    ChromeOptions options = new ChromeOptions();
//    options.addArguments("--disable-popup-blocking"); // 팝업 안띄움
//    options.addArguments("--headless"); // 브라우저 창 숨기고 실행
//    options.addArguments("--start-minimized");
//    options.addArguments("--enable-automation");
//    options.addArguments("--window-position=1980,1050");
//    Dimension windowSize = new Dimension(800, 600); // 원하는 크기로 설정
//    options.addArguments("--window-size=" + windowSize.width + "," + windowSize.height);
//    options.addArguments("--lang=ko");
    options.addArguments("--disable-gpu");            //gpu 비활성화
//    options.addArguments("--blink-settings=imagesEnabled=false"); //이미지 다운 안받음

    driver = new ChromeDriver();
    driver.manage().timeouts().implicitlyWait(300, TimeUnit.MILLISECONDS);

    String naverLoginUrl = "https://nid.naver.com/nidlogin.login?mode=form&url=https://www.naver.com/";

    String naverSearchUrl = "https://series.naver.com/search/search.series?t=novel&fs=default&q=" + searchWord + "&page=1";

    String naverId = "bitcteam4";
    String naverPw = "qntks505!";

    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    StringSelection id = new StringSelection(naverId);
    StringSelection pw = new StringSelection(naverPw);

      // -------------------------------- selenium (로그인) -------------------------------------

    try {
      // 네이버 로그인 페이지 접속하기
      driver.get(naverLoginUrl);

      // 아이디 입력
      WebElement idInput = driver.findElement(By.name("id"));
      clipboard.setContents(id, null);
      idInput.click();
      idInput.sendKeys(Keys.CONTROL + "v");

      Thread.sleep(300);

      // 비밀번호 입력
      WebElement pwInput = driver.findElement(By.name("pw"));
      clipboard.setContents(pw, null);
      pwInput.click();
      pwInput.sendKeys(Keys.CONTROL + "v");

      Thread.sleep(300);

      // 로그인 버튼 클릭
      WebElement btnLogin = driver.findElement(By.cssSelector(".btn_login"));
      btnLogin.click();


      // 검색 작품 개수 찾을 사이트 접속하기
      driver.get(naverSearchUrl);

      try {
        WebElement findAllCount = driver.findElement(By.xpath("//*[@id=\"content\"]/div[2]/div[2]/h3/em"));

        int si = findAllCount.getText().indexOf("(");
        int ei = findAllCount.getText().indexOf(")");
        int searchCount = Integer.parseInt(findAllCount.getText().substring(si + 1, ei));

        // 검색 작품 개수가 25개 이하일때
        if (searchCount <= 25) {
          List<String> platformIdList = new ArrayList<>();
          List<String> titleList = new ArrayList<>();
          List<String> thumbnailList = new ArrayList<>();
          List<Double> starRateList = new ArrayList<>();
          List<String> authorList = new ArrayList<>();
          List<Integer> countList = new ArrayList<>();
          List<String> completeYnList = new ArrayList<>();
          List<String> descriptionList = new ArrayList<>();
          List<String> ageGradeList = new ArrayList<>();

          // 작품 id가 적힌 a태그들 찾기
          List<WebElement> aLinks = driver.findElements(By.className("N=a:nov.img"));

          for (WebElement platformIdEl : aLinks) {
            // a태그 href 속성값 중 platformId값 추출하기
            int platformIdIndex = platformIdEl.getAttribute("href").indexOf("=");
            String platformId = platformIdEl.getAttribute("href").substring(platformIdIndex + 1);
            platformIdList.add(platformId);

            // 썸네일 주소 가져오기
            String thumbnail = platformIdEl.findElement(By.cssSelector("img")).getAttribute("src");
            thumbnailList.add(thumbnail);
          }


          List<WebElement> titles = driver.findElements(By.className("N=a:nov.title"));
          for (WebElement titleEl : titles) {
            // 제목 가져오기
            int titleIndex = titleEl.getText().indexOf("(");
            String title = titleEl.getText().substring(0, titleIndex - 1);
            titleList.add(title);

            // count 가져오기
            int countStartIndex = titleEl.getText().indexOf("(총");
            int countEndIndex = titleEl.getText().indexOf("/");
            int count = Integer.parseInt(titleEl.getText().substring(countStartIndex + 3, countEndIndex - 1));
            countList.add(count);

          }

          // 작가 정보 가져오기
          List<WebElement> infos = driver.findElement(By.cssSelector(".lst_list")).findElements(By.className("info"));
          for (WebElement e : infos) {
            String p = e.getText();
            String[] parts = p.split("\\s*\\|\\s*");

            String author = parts[1];
            authorList.add(author);

            // 완결 유무 가져오기
            String f = parts[3];
            int completeYnStartIndex = f.indexOf("/");

            String completeYn = f.substring(completeYnStartIndex + 1);
            completeYnList.add(completeYn);
          }

          // 별점 정보 가져오기
          List<WebElement> starRates = driver.findElements(By.className("score_num"));
          for (WebElement starRateEl : starRates) {
            double starRate = Double.parseDouble(starRateEl.getText());
            starRateList.add(starRate);
          }

          // 작품 소개글 가져오기
          List<WebElement> descriptions = driver.findElements(By.className("dsc"));
          for (WebElement descriptionEl : descriptions) {
            String description = descriptionEl.getText();
            descriptionList.add(description);
          }

          // 성인 작품 여부 가져오기
          List<WebElement> conts = driver.findElements(By.cssSelector("div.cont"));
          for (WebElement contEl : conts) {
            String h3 = contEl.findElement(By.cssSelector("h3")).getText();
            String ageGrade = h3.contains("19금") ? "Y" : "N";

            ageGradeList.add(ageGrade);
          }

          // 객체에 속성명으로 해당 리스트(배열) 형태로 저장
          naverSearchObj.put("platformId", platformIdList);
          naverSearchObj.put("title", titleList);
          naverSearchObj.put("thumbnail", thumbnailList);
          naverSearchObj.put("starRate", starRateList);
          naverSearchObj.put("author", authorList);
          naverSearchObj.put("count", countList);
          naverSearchObj.put("completeYn", completeYnList);
          naverSearchObj.put("dsc", descriptionList);
          naverSearchObj.put("ageGrade", ageGradeList);
        }

        // 결과가 25개 초과인 경우(2페이지 이상 있는 경우)
        else {
          List<String> platformIdList = new ArrayList<>();  // o
          List<String> titleList = new ArrayList<>(); // o
          List<String> thumbnailList = new ArrayList<>(); // o
          List<Double> starRateList = new ArrayList<>(); // o
          List<String> authorList = new ArrayList<>(); // o
          List<Integer> countList = new ArrayList<>(); // o
          List<String> completeYnList = new ArrayList<>(); //
          List<String> descriptionList = new ArrayList<>();
          List<String> ageGradeList = new ArrayList<>();

          for (int i = 1; i <= 2; i++) {
            // 1~10페이지의 페이지에 접속하기
            String searchDetailUrl = "https://series.naver.com/search/search.series?t=novel&q=" + searchWord + "&page=" + i;

            driver.get(searchDetailUrl);

            // 작품 id가 적힌 a태그들 찾기
            List<WebElement> aLinks = driver.findElements(By.className("N=a:nov.img"));

            for (WebElement platformIdEl : aLinks) {
              // a태그 href 속성값 중 platformId값 추출하기
              int platformIdIndex = platformIdEl.getAttribute("href").indexOf("=");
              String platformId = platformIdEl.getAttribute("href").substring(platformIdIndex + 1);
              platformIdList.add(platformId);

              // 썸네일 주소 가져오기
              String thumbnail = platformIdEl.findElement(By.cssSelector("img")).getAttribute("src");
              thumbnailList.add(thumbnail);
            }

            List<WebElement> titles = driver.findElements(By.className("N=a:nov.title"));

            for (WebElement titleEl : titles) {
              // 제목 가져오기
              int titleIndex = titleEl.getText().indexOf("(");
              String title = titleEl.getText().substring(0, titleIndex - 1);
              titleList.add(title);

              // count 가져오기
              int countStartIndex = titleEl.getText().indexOf("(총");
              int countEndIndex = titleEl.getText().indexOf("/");
              int count = Integer.parseInt(titleEl.getText().substring(countStartIndex + 3, countEndIndex - 1));
              countList.add(count);

            }

            // 작가 정보 가져오기
            List<WebElement> infos = driver.findElement(By.cssSelector(".lst_list")).findElements(By.className("info"));
            for (WebElement e : infos) {
              String p = e.getText();
              String[] parts = p.split("\\s*\\|\\s*");

              String author = parts[1];
              authorList.add(author);

              // 완결 유무 가져오기
              String f = parts[3];
              int completeYnStartIndex = f.indexOf("/");

              String completeYn = f.substring(completeYnStartIndex + 1);
              completeYnList.add(completeYn);
            }

            // 별점 정보 가져오기
            List<WebElement> starRates = driver.findElements(By.className("score_num"));
            for (WebElement starRateEl : starRates) {
              double starRate = Double.parseDouble(starRateEl.getText());
              starRateList.add(starRate);
            }

            // 작품소개글 가져오기
            List<WebElement> descriptions = driver.findElements(By.className("dsc"));
            for (WebElement descriptionEl : descriptions) {
              String description = descriptionEl.getText();
              descriptionList.add(description);
            }

            // 성인 작품 여부 가져오기
            List<WebElement> conts = driver.findElements(By.cssSelector("div.cont"));
            for (WebElement contEl : conts) {
              String h3 = contEl.findElement(By.cssSelector("h3")).getText();
              String ageGrade = h3.contains("19금") ? "Y" : "N";

              ageGradeList.add(ageGrade);
            }


          }
          // 객체에 속성명으로 해당 리스트(배열) 형태로 저장
          naverSearchObj.put("platformId", platformIdList);
          naverSearchObj.put("title", titleList);
          naverSearchObj.put("thumbnail", thumbnailList);
          naverSearchObj.put("starRate", starRateList);
          naverSearchObj.put("author", authorList);
          naverSearchObj.put("count", countList);
          naverSearchObj.put("completeYn", completeYnList);
          naverSearchObj.put("dsc", descriptionList);
          naverSearchObj.put("ageGrade", ageGradeList);
        }
      }
      catch (NoSuchElementException e) {
        System.out.println("검색결과가 결과가 존재하지 않습니다.");
      }
      finally {
        driver.quit();
      }

      // 검색 작품 개수 찾기

    }
    catch (Exception e) {
      e.printStackTrace();
    }
    finally {
      driver.quit();
    }


    return naverSearchObj;
  }
}


























