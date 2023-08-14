package com.bitc.full505_final_team4.service;

import com.bitc.full505_final_team4.common.JsonUtils;
import com.bitc.full505_final_team4.data.dto.NovelMainDto;
import com.bitc.full505_final_team4.data.entity.NovelEntity;
import com.bitc.full505_final_team4.data.entity.NovelPlatformEntity;
import com.bitc.full505_final_team4.data.entity.NovelRankEntity;
import com.bitc.full505_final_team4.data.repository.*;
import com.bitc.full505_final_team4.service.NovelRidiService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;

// 리디북스 관련 메소드 모음 service
@Service
@RequiredArgsConstructor
public class NovelRidiServiceImpl implements NovelRidiService {

  private final NovelRankRepository novelRankRepository;
  private final NovelMainRepository novelMainRepository;
  private final PlatformMainRepository platformMainRepository;


  // 리디북스 일간 순위 페이지의 특정 카테고리 데이터를 저장한다.
  @Override
  public boolean storeRidiCategoryRankList(int category, int startNum) throws Exception {

    boolean result = false;
    int cateRankNum = ridiCategoryRankNum(category);

    List<NovelRankEntity> novelRankList = new ArrayList<>();

    String url = "https://api.ridibooks.com/v2/bestsellers?category_includes=";
    url += category + "&offset=";
    // startNum : 첫 순위 번호
    url += (startNum - 1) + "&limit=50&period=DAILY";


    try {
      // 페이지 json 객체로 변환
      JSONObject novelResult = (JSONObject) JsonUtils.jsonUrlParser(url).get("data");
      ArrayList<JSONObject> novelList = (ArrayList<JSONObject>) novelResult.get("items"); // 배열 변환

      if (!novelList.isEmpty()) {
        for (JSONObject novelItem : novelList) {
          // 각각 item에 들어있는 book Object
          HashMap<String, Object> book = (HashMap<String, Object>) novelItem.get("book");

          // 값을 저장할 entity
//          NovelRankEntity novel = new NovelRankEntity();

          // platform : 리디북스는 3
          int platform = 3;

          // 소설 순위
          int rankNum = novelList.indexOf(novelItem) + 1 + cateRankNum; // novelItem의 인덱스 번호

          // 플랫폼 제공 아이디
          String platformId = book.get("book_id").toString();

          // 소설 제목 얻어오기
          JSONObject serial = (JSONObject) book.get("serial");
          String title = serial.get("title").toString();

          // 작가 이름 얻어오기
          ArrayList authorsList = (ArrayList) book.get("authors");
          HashMap<String, Object> authors = (HashMap<String, Object>) authorsList.get(0);
          String author = authors.get("name").toString();

          // 소설 썸네일
          JSONObject cover = (JSONObject) book.get("cover");
          String thumbnail = cover.get("large").toString();

          // 소설 카테고리
          /*JSONArray categories = (JSONArray) book.get("categories");
          JSONObject categoryFirst = (JSONObject) categories.get(0);
          novel.setCategory(categoryFirst.get("name").toString());*/


          // 소설 별점
          /*JSONArray ratings = (JSONArray) book.get("ratings");
          novel.setNovelStarRate(getStarRate(ratings)); // 하위에 구현한 함수 사용*/

          int adultsOnly = 0;

          // 성인 여부
          if ((Boolean) book.get("adults_only")) {
            adultsOnly = 1; // 성인 작품 맞음
          } else {
            adultsOnly = 0; // 성인 작품 아님
          }

          novelRankList.add(new NovelRankEntity(platform, rankNum, title, author, thumbnail, platformId, LocalDate.now(), category, adultsOnly)); // entity 리스트에 저장
        }

        novelRankRepository.saveAll(novelRankList);
        result = true;
      }
    } catch (Exception e){
      e.printStackTrace();
    }


    return result;
  }

  /* 리디북스 카테고리 별 순위 리스트 불러오기
   * 시작번호로부터 20개 출력
   * 총 100위까지 출력되도록 함 */
  @Override
  @Transactional
  public List<NovelMainDto> getRidiRankList(String category, int startNum) throws Exception {

    List<NovelMainDto> novelDtoList = new ArrayList<>();

    String url = "https://api.ridibooks.com/v2/bestsellers?category_includes=";
    url += category + "&offset=";
    url += (startNum - 1) + "&limit=20&period=DAILY";

    // 페이지 json 객체로 변환
    JSONObject novelResult = (JSONObject) JsonUtils.jsonUrlParser(url).get("data");
    ArrayList<JSONObject> novelList = (ArrayList<JSONObject>) novelResult.get("items"); // 배열 변환

    if (!novelList.isEmpty()){
      for(JSONObject novelItem : novelList){
        // 각각 item에 들어있는 book Object
        HashMap<String, Object > book = (HashMap<String, Object>) novelItem.get("book");

        // 값을 저장할 dto
        NovelMainDto novel = new NovelMainDto();

        // 소설 순위
        novel.setNovelIndexNum(novelList.indexOf(novelItem) + 1); // novelItem의 인덱스 번호

        // 플랫폼 제공 아이디
        novel.setPlatformId(book.get("book_id").toString());

        // 소설 제목 얻어오기
        JSONObject serial = (JSONObject) book.get("serial");
        novel.setNovelTitle(serial.get("title").toString());

        // 작가 이름 얻어오기
        ArrayList authorsList = (ArrayList) book.get("authors");
        HashMap<String, Object> authors = (HashMap<String, Object>) authorsList.get(0);
        novel.setNovelAuthor(authors.get("name").toString());

        // 소설 썸네일
        JSONObject cover = (JSONObject) book.get("cover");
        novel.setNovelThumbnail(cover.get("large").toString());

        // 소설 카테고리
        JSONArray categories = (JSONArray) book.get("categories");
        JSONObject categoryFirst = (JSONObject) categories.get(0);
        novel.setCateList(categoryFirst.get("name").toString());

        // 소설 별점
        JSONArray ratings = (JSONArray) book.get("ratings");
        novel.setNovelStarRate(getStarRate(ratings)); // 하위에 구현한 함수 사용

        // 성인 여부
        novel.setAdultsOnly((Boolean) book.get("adults_only"));



        novelDtoList.add(novel);
      }

      return novelDtoList;
    }

    return null;
  }


  // 리디북스 최신작 리스트 불러온 후 테이블에 없는 작품 저장하기 (카테고리 번호 별)
  @Transactional
  @Override
  public boolean starRidiRecentNovel(int category) throws Exception {

    boolean result = false;

    String url = "https://api.ridibooks.com/v2/category/books?category_id=" + category;
    url += "&tab=new-releases&offset=0&limit=30&platform=web&order_by=recent";

    JSONObject novelResult = (JSONObject) JsonUtils.jsonUrlParser(url).get("data");
    ArrayList<JSONObject> novelList = (ArrayList<JSONObject>) novelResult.get("items"); // 배열 변환

    try {

      if(!ObjectUtils.isEmpty(novelList)){

        // novel table 에 들어갈 데이터 리스트
        List<NovelEntity> novelEntityList = new ArrayList<>();

        // platform table 에 들어갈 데이터 리스트
        List<NovelPlatformEntity> novelPlatformEntityList = new ArrayList<>();


        for(int i = 0; i < novelList.size(); i++){

          HashMap<String, Object > book = (HashMap<String, Object>) novelList.get(i).get("book");

          // 소설 제목 불러오기
          JSONObject serial = (JSONObject) book.get("serial");
          String title = serial.get("title").toString();

          // 소설 썸네일
          JSONObject cover = (JSONObject) book.get("cover");
          String thumbnail = cover.get("large").toString();

          boolean adultsOnly = (Boolean) book.get("adults_only");

          try {

            // novel table 에 일치하는 제목이 있는지 확인, 없을 경우 exeption 반환
            NovelEntity entity = novelMainRepository.findByNovelTitle(title);
            int novelIdx = entity.getNovelIdx();

            // novel table 에 해당 제목이 있다면 platform db에 일치하는 데이터가 있는지 확인(플랫폼 번호, pk idx, 웹소설 여부)
//            platformMainRepository
//              .findByNovelTitleAndPlatformAndNovelOrEbook(title,3,  "novel"); // 데이터가 없으면 예외로 넘어감

            i++; // 데이터가 이미 있으므로 다음 번호로 넘어간다, 업데이트 된 경우에 대해서 나중에 추가 로직 필요

          }
          catch (NoSuchElementException e){
            // 못 찾았을 경우 먼저 novel db에 데이터를 등록해야함
            /* 필요한 정보 : 소설 제목, 썸네일 주소, 성인 여부 */
            NovelEntity novel = getCateNovelEntityFromJson(novelList.get(i));
            NovelPlatformEntity novelPlatformEntity = getCatePlatformEntityFromJson(novel, novelList.get(i));

            // novel entity 객체 리스트에 더하기
            novelEntityList.add(novel);
            // platform entity 객체 리스트에 더하기
            novelPlatformEntityList.add(novelPlatformEntity);

          } catch (Exception e){

            NovelEntity novel = novelMainRepository.findByNovelTitle(title);
            // novel table 에는 있지만 platform 테이블에는 없는 경우
            NovelPlatformEntity novelPlatformEntity = getCatePlatformEntityFromJson(novel, novelList.get(i));
            // platform entity 객체 리스트에 더하기
            novelPlatformEntityList.add(novelPlatformEntity);

          }

        }

        // add 한 데이터 목록들 table 에 저장
        novelMainRepository.saveAll(novelEntityList);
        platformMainRepository.saveAll(novelPlatformEntityList);

        result = true;
      }

    }catch (Exception e){

      e.printStackTrace();
      result = false;

    }



    return result;
  }


  /**************** 단위로 자른 기능 모음 ****************/

  // json object 에 있는 novel table 관련 데이터 entity 형태로 들고오기 (arrayList 반복문용)
  @Override
  @Transactional
  public NovelEntity getCateNovelEntityFromJson(JSONObject novel) throws Exception {

    // 데이터를 담을 entity 객체 생성
    NovelEntity novelEntity = new NovelEntity();

    HashMap<String, Object > book = (HashMap<String, Object>) novel.get("book");

    // 소설 제목 불러오기
    JSONObject serial = (JSONObject) book.get("serial");
    String title = serial.get("title").toString();

    // 소설 썸네일
    JSONObject cover = (JSONObject) book.get("cover");
    String thumbnail = cover.get("large").toString();

    boolean adultsOnly = (Boolean) book.get("adults_only");

    String novelAdult = "";

    if(adultsOnly){
      novelAdult = "Y";
    }else {
      novelAdult = "N";
    }

    novelEntity.setNovelTitle(title);
    novelEntity.setNovelThumbnail(thumbnail);
    novelEntity.setNovelAdult(novelAdult);

    return novelEntity;

  }

  // json object 에 있는 platform table 관련 데이터 entity 형태로 들고오기 (최신 작품, arrayList 반복문용)
  /*
  * 플랫폼 번호, 외래키 idx, 플랫폼 전용 아이디, 제목, 작가이름, 썸네일 주소, 별점, 성인작품 여부, 연재일, 소설 설명(intro), 출시일, 총 화수, 출판사, 가격, 완결 여부, 장르, novel or ebook */
  @Override
  @Transactional
  public NovelPlatformEntity getCatePlatformEntityFromJson(NovelEntity novelEntity, JSONObject novelData) throws Exception {
    NovelPlatformEntity novelPlatformEntity = new NovelPlatformEntity();

    HashMap<String, Object > book = (HashMap<String, Object>) novelData.get("book");

    novelPlatformEntity.setPlatform(3); // 리디북스 플랫폼 번호
    novelPlatformEntity.setNovelOrEbook("novel"); // 웹소설 여부

    // 플랫폼 제공 아이디
    String platformId = book.get("book_id").toString();
    novelPlatformEntity.setPlatformId(platformId);

    // 소설 설명 (novelIntro)
    novelPlatformEntity.setNovelIntro(getNovelIntro(platformId));

    // 소설 연재일
    novelPlatformEntity.setNovelUpdateDate(getNovelUpdateDate(platformId));

    JSONObject serial = (JSONObject) book.get("serial");
    novelPlatformEntity.setNovelTitle(serial.get("title").toString()); // 제목
    novelPlatformEntity.setNovelCount((Integer) serial.get("total")); // 총 화수

    String complete = "";
    if((boolean) serial.get("completion")){
      complete = "Y";
    } else { complete = "N"; }

    novelPlatformEntity.setNovelCompleteYn(complete); // 완결 여부


    // 작가 이름 얻어오기
    ArrayList authorsList = (ArrayList) book.get("authors");
    HashMap<String, Object> authors = (HashMap<String, Object>) authorsList.get(0);
    novelPlatformEntity.setNovelAuthor(authors.get("name").toString());

    // 소설 썸네일
    JSONObject cover = (JSONObject) book.get("cover");
    novelPlatformEntity.setNovelThumbnail(cover.get("large").toString());

    // 소설 별점
    JSONArray ratings = (JSONArray) book.get("ratings");
    novelPlatformEntity.setNovelStarRate(Double.parseDouble(getStarRate(ratings))); // 하위에 구현한 함수 사용

    // 성인 여부
    boolean adultsOnly = (Boolean) book.get("adults_only");

    String novelAdult = "";

    if(adultsOnly){
      novelAdult = "Y";
    }else {
      novelAdult = "N";
    }

    novelPlatformEntity.setNovelAdult(novelAdult);

    // 출판사
    JSONObject publi = (JSONObject) book.get("publisher");
    String publiName = publi.get("name").toString();

    novelPlatformEntity.setNovelPubli(publiName);

    // 가격
    JSONObject purchase = (JSONObject) book.get("purchase");
    int price = (Integer) purchase.get("maxPrice");

    novelPlatformEntity.setNovelPrice(price);

    // 장르
    JSONArray categories = (JSONArray) book.get("categories");
    JSONObject cate = (JSONObject) categories.get(0);

    String cateName = cate.get("name").toString();

    novelPlatformEntity.setCateList(cateName);

    novelPlatformEntity.setNovelEntity(novelEntity); // 외래키 idx 값
    return novelPlatformEntity;
  }

  // 작품 아이디에 해당하는 연재일 들고오기
  // 다른 출판사와 연계 되어 있을 경우 미완결이어도 데이터가 없을 수 있음, 없을 경우 빈 값 반환
  public String getNovelUpdateDate(String platformId) throws Exception{

    String url = "https://book-api.ridibooks.com/books/" + platformId + "/notices";

    JSONArray dateObj = (JSONArray) JsonUtils.jsonUrlParser(url).get("notices");

    JSONObject notices = (JSONObject) dateObj.get(0);

    String updateDate = notices.get("title").toString();

    if (ObjectUtils.isEmpty(updateDate)){
      updateDate = ""; // 값을 못 찾으면 빈 값을 반환한다.
    }

    return updateDate;
  }

  // 작품 아이디에 해당하는 작품 설명 들고오기
  public String getNovelIntro(String platformId) throws Exception {

    String url = "https://book-api.ridibooks.com/books/" + platformId + "/descriptions";

    JSONObject descObj = (JSONObject) JsonUtils.jsonUrlParser(url).get("descriptions");
    String intro = descObj.get("intro").toString();

    return intro;
  }

  /* Ridi Json 에서 들고온 ratings 별점으로 변환하기 (10점 만점 기준) */
  @Override
  public String getStarRate(JSONArray ratings) throws Exception {

    /* 계산식
     * ((1점 * 1점 count) + ... + (5점 * 5점 count)) / totalCount
     * */

    String starRate = "";

    double multiRating = 0;
    double totalCount = 0;

    /* count * rating 총합 구하기 */

    for (int i = 0; i < ratings.size(); i++) {

      JSONObject rateObject = (JSONObject) ratings.get(i);
      int rating = Integer.parseInt(rateObject.get("rating").toString()) ;
      int count = Integer.parseInt(rateObject.get("count").toString()) ;

      int multiRate = rating * count;

      multiRating += multiRate;
      totalCount += count;

    }

    double total = (multiRating / totalCount) * 2;
    // 왜 한자리수 올림이 안되는 건지...
//    total = (double) Math.ceil((total * 100) / 100.0);

    // 소수점 한자리까지 보여주는 별점 반환
    starRate = String.format("%.1f", total);

    return starRate;
  }

  // 리디 카테고리별 pk 중복 방지를 위한 카테고리별 pk 생성 메소드
  // 리디북스 카테고리 1750 : 판타지(0) / 1650 : 로맨스(50) / 6050 : 로판(100) / 4150: BL(150)
  @Override
  public int ridiCategoryRankNum(int category) throws Exception {
    int cateRankNum = 0;
    switch (category){
      case 1750 : // 판타지
        cateRankNum = 0;
        break;

      case 1650 : // 로맨스
        cateRankNum = 50;
        break;

      case 6050 : // 로판
        cateRankNum = 100;
        break;

      case 4150 : // BL
        cateRankNum = 150;
        break;
    }

    return cateRankNum;
  }

  @Override
  public String ridiCategoryNameConverter(int category) throws Exception{

    String cateName = "";

    switch (category){
      case 1750 :
        cateName = "판타지";
        break;

      case 1650 :
        cateName = "로맨스";
        break;

      case 6050 :
        cateName = "로판";
        break;

      case 4150 :
        cateName = "BL";
        break;
    }

    return cateName;
  }

}
