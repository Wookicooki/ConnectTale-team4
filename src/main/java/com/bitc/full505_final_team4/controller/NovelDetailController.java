package com.bitc.full505_final_team4.controller;

import com.bitc.full505_final_team4.data.dto.NovelDTO;
import com.bitc.full505_final_team4.data.entity.NovelEntity;
import com.bitc.full505_final_team4.data.entity.NovelPlatformEntity;
import com.bitc.full505_final_team4.service.NovelDetailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.system.SystemProperties;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
public class NovelDetailController {
  private final NovelDetailService novelDetailService;

  // ---------------------db에 있는 디테일페이지 데이터 불러오기-------------------------
  @RequestMapping(value = "/novelDetail", method = RequestMethod.GET)
  public Object getNovelDetail(@RequestParam("platformId") String platformId) throws Exception {
    Map<String, NovelPlatformEntity> novelDetail = new HashMap<>();

    // platformId(매개변수) 를 통해 novelIdx 찾고, novelIdx에 해당하는 노벨정보 다가져오기
    List<NovelPlatformEntity> allNovelDetail = novelDetailService.getNovelDetail(platformId);

    // 플랫폼 별로 나눠서 전달하기
    for (NovelPlatformEntity p : allNovelDetail) {
      if (p.getPlatform() == 1) {
        novelDetail.put("kakao", p);
      }
      else if (p.getPlatform() == 2) {
        novelDetail.put("naver", p);
      }
      else if (p.getPlatform() == 3) {
        novelDetail.put("ridi", p);
      }
    }



    return novelDetail;
  }
  // -------------------------------- db에 데이터 저장 ------------------------------------

  // 리디북스 디테일 페이지 정보 db 저장

  @RequestMapping(value = "/novelDetail", method = RequestMethod.POST)
  public void insertRidiDetail(@RequestParam("id") String id, @RequestParam("title") String title, @RequestParam("ne") String ne, NovelPlatformEntity ridiPlatformEntity) throws Exception {

//    System.out.println(title);
//    System.out.println(id);
//    System.out.println(ne);
//    System.out.println(ridiPlatformEntity);

    NovelEntity novelEntity = new NovelEntity();


<<<<<<< HEAD
    // 네이버 디테일페이지 정보 가져와서 platform entity에 저장
    NovelPlatformEntity naverPlatformEntity = novelDetailService.getNaverCrolling(id, title, ne);
    // 카카오 디테일페이지 정보 가져와서 platform entity에 저장
    NovelPlatformEntity kakaoPlatformEntity = novelDetailService.getKakaoCrolling(id, title, ne);
=======
    // 리디북스 디테일 페이지 정보를 NovelPlatformEntity에 저장
    novelPlatformEntity.setNovelIdx(novelEntity); // 복합키인 novel 엔티티 추가
    novelDetailService.insertRidiToPlatform(novelPlatformEntity);
>>>>>>> origin/chanmi

    System.out.println(kakaoPlatformEntity); // 1
    System.out.println(naverPlatformEntity); // 2
    System.out.println(ridiPlatformEntity); // 3

    // 리디북스에 해당 작품이 없을 때
    if (ridiPlatformEntity.getPlatform() == 0) {

      // 네이버, 카카오 모두 해당 작품이 있을 경우,
      if (naverPlatformEntity.getPlatformId() != null && kakaoPlatformEntity.getPlatformId() != null) {
        novelEntity.setNovelTitle(naverPlatformEntity.getNovelTitle());
        novelEntity.setNovelThumbnail(naverPlatformEntity.getNovelThumbnail());
        novelEntity.setNovelAdult(naverPlatformEntity.getNovelAdult());

        novelDetailService.insertNaverToNovel(novelEntity);

        // 네이버 디테일 페이지 정보를 novel entity에 저장
        naverPlatformEntity.setNovelEntity(novelEntity);

        // 네이버 디테일 페이지 정보를 NovelPlatformEntity에 저장
        novelDetailService.insertNaverToPlatform(naverPlatformEntity);

        kakaoPlatformEntity.setNovelEntity(novelEntity);
        // 카카오 디테일 페이지 정보를 NovelPlatformEntity에 저장
        novelDetailService.insertKakaoToPlatform(kakaoPlatformEntity);

      }
      else {
        // 카네 중 네이버만 있을 경우
        if (naverPlatformEntity.getPlatformId() != null) {
          novelEntity.setNovelTitle(naverPlatformEntity.getNovelTitle());
          novelEntity.setNovelThumbnail(naverPlatformEntity.getNovelThumbnail());
          novelEntity.setNovelAdult(naverPlatformEntity.getNovelAdult());

          novelDetailService.insertNaverToNovel(novelEntity);

          // 네이버 디테일 페이지 정보를 novel entity에 저장
          naverPlatformEntity.setNovelEntity(novelEntity);

          // 네이버 디테일 페이지 정보를 NovelPlatformEntity에 저장
          novelDetailService.insertNaverToPlatform(naverPlatformEntity);
        }
        // 카네 중 카카오만 있을 경우
        else if (kakaoPlatformEntity.getPlatformId() != null) {
          novelEntity.setNovelTitle(kakaoPlatformEntity.getNovelTitle());
          novelEntity.setNovelThumbnail(kakaoPlatformEntity.getNovelThumbnail());
          novelEntity.setNovelAdult(kakaoPlatformEntity.getNovelAdult());

          novelDetailService.insertKakaoToNovel(novelEntity);

          // 카카오 디테일 페이지 정보를 novel entity에 저장
          kakaoPlatformEntity.setNovelEntity(novelEntity);

          // 카카오 디테일 페이지 정보를 NovelPlatformEntity에 저장
          novelDetailService.insertKakaoToPlatform(kakaoPlatformEntity);

        }
      }
    }
    else {
      // 리디만 있는 경우
      if (naverPlatformEntity.getPlatformId() == null && kakaoPlatformEntity.getPlatformId() == null) {
        novelEntity.setNovelTitle(ridiPlatformEntity.getNovelTitle());
        novelEntity.setNovelThumbnail(ridiPlatformEntity.getNovelThumbnail());
        novelEntity.setNovelAdult(ridiPlatformEntity.getNovelAdult());

        novelDetailService.insertRidiToNovel(novelEntity);

        // 리디북스 디테일 페이지 정보를 NovelPlatformEntity에 저장
        ridiPlatformEntity.setNovelEntity(novelEntity); // 복합키인 novel 엔티티 추가
        novelDetailService.insertRidiToPlatform(ridiPlatformEntity);
      }
      // 리디, 네이버 만 있는 경우
      else if (naverPlatformEntity.getPlatformId() != null) {
        novelEntity.setNovelTitle(ridiPlatformEntity.getNovelTitle());
        novelEntity.setNovelThumbnail(ridiPlatformEntity.getNovelThumbnail());
        novelEntity.setNovelAdult(ridiPlatformEntity.getNovelAdult());

        novelDetailService.insertRidiToNovel(novelEntity);

        // 리디북스 디테일 페이지 정보를 NovelPlatformEntity에 저장
        ridiPlatformEntity.setNovelEntity(novelEntity); // 복합키인 novel 엔티티 추가
        novelDetailService.insertRidiToPlatform(ridiPlatformEntity);

        naverPlatformEntity.setNovelEntity(novelEntity);
        novelDetailService.insertNaverToPlatform(naverPlatformEntity);

      }
      // 리디, 카카오만 있는 경우
      else if (kakaoPlatformEntity.getPlatformId() != null) {
        novelEntity.setNovelTitle(ridiPlatformEntity.getNovelTitle());
        novelEntity.setNovelThumbnail(ridiPlatformEntity.getNovelThumbnail());
        novelEntity.setNovelAdult(ridiPlatformEntity.getNovelAdult());

        novelDetailService.insertRidiToNovel(novelEntity);

        // 리디북스 디테일 페이지 정보를 NovelPlatformEntity에 저장
        ridiPlatformEntity.setNovelEntity(novelEntity); // 복합키인 novel 엔티티 추가
        novelDetailService.insertRidiToPlatform(ridiPlatformEntity);

        kakaoPlatformEntity.setNovelEntity(novelEntity);
        novelDetailService.insertKakaoToPlatform(kakaoPlatformEntity);
      }
      // 리디, 네이버, 카카오 모두 있는 경우
      else if (naverPlatformEntity.getPlatformId() != null && kakaoPlatformEntity.getPlatformId() != null) {

        novelEntity.setNovelTitle(ridiPlatformEntity.getNovelTitle());
        novelEntity.setNovelThumbnail(ridiPlatformEntity.getNovelThumbnail());
        novelEntity.setNovelAdult(ridiPlatformEntity.getNovelAdult());

        novelDetailService.insertRidiToNovel(novelEntity);

        ridiPlatformEntity.setNovelEntity(novelEntity); // 복합키인 novel 엔티티 추가
        novelDetailService.insertRidiToPlatform(ridiPlatformEntity);

        naverPlatformEntity.setNovelEntity(novelEntity);
        novelDetailService.insertNaverToPlatform(naverPlatformEntity);

        kakaoPlatformEntity.setNovelEntity(novelEntity);
        novelDetailService.insertKakaoToPlatform(kakaoPlatformEntity);
      }
    }
  }
}
