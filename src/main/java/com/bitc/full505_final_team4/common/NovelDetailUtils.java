package com.bitc.full505_final_team4.common;

import com.bitc.full505_final_team4.data.entity.NovelPlatformEntity;
import com.bitc.full505_final_team4.service.NovelCommonEditService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
@RequiredArgsConstructor
public class NovelDetailUtils {
  public static NovelPlatformEntity naverDetailCrolling(WebDriver driver, String novelTitle, String ebookCheck, WebElement titleEl)  {
    NovelPlatformEntity naverCrollingData = new NovelPlatformEntity();
    // 검색결과 페이지에서 가져올 정보
    int platformIdIndex = titleEl.getAttribute("href").indexOf("=");

    // platform 구분하기
    naverCrollingData.setPlatform(2);

    // platformId 가져오기
    naverCrollingData.setPlatformId(titleEl.getAttribute("href").substring(platformIdIndex + 1));

    // novelIntro 가져오기
    WebElement titleParentEl = titleEl.findElement(By.xpath("..")).findElement(By.xpath(".."));
    naverCrollingData.setNovelIntro(titleParentEl.findElement(By.className("dsc")).getText());
    // -------------------------------


    // 작품 디테일 페이지 접속하기
    driver.get(titleEl.getAttribute("href"));

    // novelTitle 설정하기
    naverCrollingData.setNovelTitle(novelTitle);

    // novelThumbnail 가져오기
    WebElement leftSide = driver.findElement(By.xpath("//*[@id=\"container\"]/div[1]"));
    WebElement thumbnailEl = leftSide.findElement(By.tagName("img"));

    naverCrollingData.setNovelThumbnail(thumbnailEl.getAttribute("src"));


    // novelAuthor 가져오기
    naverCrollingData.setNovelAuthor(driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[3]/a")).getText());

    // novelPubli 가져오기
    naverCrollingData.setNovelPubli(driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[4]/a")).getText());

    // novelCount 가져오기
    naverCrollingData.setNovelCount(Integer.parseInt(driver.findElement(By.xpath("//*[@id=\"content\"]/h5/strong")).getText()));

    // novelCompleteYn 가져오기
    if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[1]/span")).getText().equals("완결")) {
      naverCrollingData.setNovelCompleteYn("Y");
    } else {
      naverCrollingData.setNovelCompleteYn("N");
    }

    // novelPrice 가져오기
    int novelPrice = Integer.parseInt(driver.findElement(By.cssSelector(".area_ebook_price_information")).findElement(By.tagName("div")).findElement(By.cssSelector(".point_color")).getText());
    naverCrollingData.setNovelPrice(novelPrice);

    // novelStarRate 가져오기
    naverCrollingData.setNovelStarRate(Double.parseDouble(driver.findElement(By.xpath("//*[@id=\"content\"]/div[1]/div[1]/em")).getText()));

    // novelRelease 가져오기
    int novelReleaseStartIndex = driver.findElement(By.id("volumeList")).findElement(By.tagName("em")).getText().indexOf("(");
    int novelReleaseEndIndex = driver.findElement(By.id("volumeList")).findElement(By.tagName("em")).getText().indexOf(".)");

    naverCrollingData.setNovelRelease(driver.findElement(By.id("volumeList")).findElement(By.tagName("em")).getText().substring(novelReleaseStartIndex + 1, novelReleaseEndIndex));

    // cateList 가져오기
    if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("판타지")) {
      naverCrollingData.setCateList("1");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("현판")) {
      naverCrollingData.setCateList("2");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("로맨스")) {
      naverCrollingData.setCateList("3");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("로판")) {
      naverCrollingData.setCateList("4");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("무협")) {
      naverCrollingData.setCateList("5");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("드라마")) {
      naverCrollingData.setCateList("6");
    } else if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[2]/span/a")).getText().equals("BL")) {
      naverCrollingData.setCateList("7");
    }
    else {
      naverCrollingData.setCateList("8");
    }

    // ebookCheck 가져오기 : 이거는 상세페이지로 이동할 때 이미 정해져있음
    naverCrollingData.setEbookCheck(ebookCheck);

    // novelAdult 가져오기
    if (driver.findElement(By.xpath("//*[@id=\"content\"]/ul[1]/li/ul/li[5]")).getText().equals("청소년 이용불가")) {
      naverCrollingData.setNovelAdult("Y");
    } else {
      naverCrollingData.setNovelAdult("N");
    }

    return naverCrollingData;
  }

  public static NovelPlatformEntity kakaoDetailCrolling(WebDriver driver, String novelTitle, String ebookCheck, WebElement aEl) {
    NovelPlatformEntity kakaoCrollingData = new NovelPlatformEntity();
    // 플랫폼 설정하기
    kakaoCrollingData.setPlatform(1);

    // platformId 가져오기
    int platformIdIndex = aEl.getAttribute("href").lastIndexOf("/");
    kakaoCrollingData.setPlatformId(aEl.getAttribute("href").substring(platformIdIndex + 1));

    // 작품 디테일 페이지로 접속하기
    driver.get(aEl.getAttribute("href"));

    // novelTitle 저장하기(페이지 내에 있는 작품이기에 그냥 검색한 키워드가 곧 작품 제목이 됨)
    kakaoCrollingData.setNovelTitle(novelTitle);

    // novelThumbnail 가져오기
    kakaoCrollingData.setNovelThumbnail(driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[1]/div/div/img")).getAttribute("src"));

    // novelAuthor 가져오기
    kakaoCrollingData.setNovelAuthor(driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/div/span")).getText());

    // novelCount 가져오기
    kakaoCrollingData.setNovelCount(Integer.parseInt(driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[2]/div[2]/div[1]/div[1]/div[1]/span")).getText().substring(3).replace(",", "")));

    // starRate 가져오기
    WebElement starRateDivEl = driver.findElement(By.cssSelector(".justify-center.mt-16pxr"));
    if (starRateDivEl.findElements(By.tagName("img")).get(2).getAttribute("alt").equals("별점")) {
      kakaoCrollingData.setNovelStarRate(Double.parseDouble(starRateDivEl.findElements(By.tagName("span")).get(1).getText()));
    }
    else {
      kakaoCrollingData.setNovelStarRate(0);
    }

    // novelCompleteYn 가져오기
    if (driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/span")).getText().contains("완결")) {
      kakaoCrollingData.setNovelCompleteYn("Y");
    }
    else {
      kakaoCrollingData.setNovelCompleteYn("N");
    }

    // cateList 가져오기
    String category = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[1]")).getText();
    if (category.contains("판타지")) {
      kakaoCrollingData.setCateList("1");
    }
    else if (category.contains("현판")) {
      kakaoCrollingData.setCateList("2");
    }
    else if (category.contains("로맨스")) {
      kakaoCrollingData.setCateList("3");
    }
    else if (category.contains("로판")) {
      kakaoCrollingData.setCateList("4");
    }
    else if (category.contains("무협")) {
      kakaoCrollingData.setCateList("5");
    }
    else if (category.contains("드라마")) {
      kakaoCrollingData.setCateList("6");
    }
    else if (category.contains("BL")) {
      kakaoCrollingData.setCateList("7");
    }
    else {
      kakaoCrollingData.setCateList("8");
    }

    // novelIntro 가져오기
    driver.get("https://page.kakao.com/content/" + kakaoCrollingData.getPlatformId() + "?tab_type=about");
    kakaoCrollingData.setNovelIntro(driver.findElement(By.cssSelector(".whitespace-pre-wrap.break-words")).getText());

    // novelPubli 가져오기
    kakaoCrollingData.setNovelPubli(driver.findElements(By.cssSelector(".font-small1.mb-8pxr")).get(1).findElement(By.tagName("div")).getText());

    // novelAdult 가져오기
    if (driver.findElements(By.cssSelector(".font-small1.mb-8pxr")).get(2).findElement(By.tagName("div")).getText().equals("19세이용가")) {
      kakaoCrollingData.setNovelAdult("Y");
    }
    else {
      kakaoCrollingData.setNovelAdult("N");
    }

    // novelUpdateDate 가져오기
    if (driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/span")).getText().contains("연재") && driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/span")).getText().indexOf("연재") != 0) {
      int novelUpdateDateIndex = driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/span")).getText().indexOf("연재");
      kakaoCrollingData.setNovelUpdateDate(driver.findElement(By.xpath("//*[@id=\"__next\"]/div/div[2]/div[1]/div[1]/div[1]/div[1]/div/div[3]/div[2]/div[1]/div[2]/span")).getText().substring(0, novelUpdateDateIndex - 1));
    }

    // novelPrice 가져오기
    if (driver.findElements(By.cssSelector(".font-small1.mb-8pxr")).get(3).findElement(By.tagName("div")).getText().indexOf("원") == -1) {
      kakaoCrollingData.setNovelPrice(0);
    }
    else {
      int novelPriceIndex = driver.findElements(By.cssSelector(".font-small1.mb-8pxr")).get(3).findElement(By.tagName("div")).getText().indexOf("원");
      kakaoCrollingData.setNovelPrice(Integer.parseInt(driver.findElements(By.cssSelector(".font-small1.mb-8pxr")).get(3).findElement(By.tagName("div")).getText().substring(0, novelPriceIndex)));
    }

    // ebookCheck 가져오기
    kakaoCrollingData.setEbookCheck(ebookCheck);


    return kakaoCrollingData;
  }

  public static NovelPlatformEntity ridiDetailCrolling(JSONObject books, String novelTitle, String ebookCheck, String novelAdult) throws Exception {
    NovelPlatformEntity ridiPlatformEntity = new NovelPlatformEntity();

    // 플랫폼 설정
    ridiPlatformEntity.setPlatform(3);

    String platformId = books.get("b_id").toString();
    ridiPlatformEntity.setPlatformId(platformId);

    // novelTitle 설정
    ridiPlatformEntity.setNovelTitle(novelTitle);

    // novelThumbnail 설정
    String novelThumbnail = "https://img.ridicdn.net/cover/" + platformId +"/xxlarge";
    ridiPlatformEntity.setNovelThumbnail(novelThumbnail);

    // novelIntro 설정
    JSONObject introDesc = (JSONObject) JsonUtils.jsonUrlParser("https://book-api.ridibooks.com/books/" + platformId + "/descriptions").get("descriptions");
    String novelIntro = introDesc.get("intro").toString();
    ridiPlatformEntity.setNovelIntro(novelIntro);

    // novelAuthor 설정
    ArrayList authorsList = (ArrayList) books.get("authors_info");
    HashMap<String, Object> authors = (HashMap<String, Object>) authorsList.get(0);
    String author = authors.get("name").toString();
    ridiPlatformEntity.setNovelAuthor(author);

    // novelPubli 설정
    String novelPubli = books.get("publisher").toString();
    ridiPlatformEntity.setNovelPubli(novelPubli);

    // novelCount
    int novelCount = Integer.parseInt(books.get("book_count").toString());
    ridiPlatformEntity.setNovelCount(novelCount);

    // novelPrice
    ArrayList priceList = (ArrayList) books.get("series_prices_info");
    if (!priceList.isEmpty()) {
      HashMap<String, Object> prices = (HashMap<String, Object>) priceList.get(0);
      int novelPrice = Integer.parseInt(prices.get("max_price").toString());
      ridiPlatformEntity.setNovelPrice(novelPrice);
    }
    else {
      int novelPrice = Integer.parseInt(books.get("price").toString());
      ridiPlatformEntity.setNovelPrice(novelPrice);
    }

    // starRate
    double novelStarRate = Double.parseDouble(books.get("buyer_rating_score").toString());
    ridiPlatformEntity.setNovelStarRate(novelStarRate);

    // completeYn
    String completeYn = Boolean.parseBoolean(books.get("is_series_complete").toString()) ? "Y" : "N";
    ridiPlatformEntity.setNovelCompleteYn(completeYn);

    // novelAdult
    ridiPlatformEntity.setNovelAdult(novelAdult);

    // novelRelease => 애매해서 생략

    // updateDate
    ArrayList updateDateList = (ArrayList) JsonUtils.jsonUrlParser("https://book-api.ridibooks.com/books/" + platformId + "/notices").get("notices");
    if (!updateDateList.isEmpty()) {
      HashMap<String, Object> updateDates = (HashMap<String, Object>) updateDateList.get(0);
      String updateDate = updateDates.get("title").toString();
      ridiPlatformEntity.setNovelUpdateDate(updateDate);
    }

    // cateList
    String cateItem = books.get("parent_category_name").toString();
    if (cateItem.contains("BL")) {
      ridiPlatformEntity.setCateList("7");
    }
    else if (cateItem.contains("로맨스")) {
      ridiPlatformEntity.setCateList("3");
    }
    else if (cateItem.contains("로판")) {
      ridiPlatformEntity.setCateList("4");
    }
    else if (cateItem.contains("판타지")) {
      ridiPlatformEntity.setCateList("1");
    }
    else {
      ridiPlatformEntity.setCateList("8");
    }

    // ebookCheck
    ridiPlatformEntity.setEbookCheck(ebookCheck);

    return ridiPlatformEntity;
  }

  List<Integer> answer = new ArrayList<>();
}



