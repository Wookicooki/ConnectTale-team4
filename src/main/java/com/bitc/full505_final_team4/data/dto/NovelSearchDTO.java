package com.bitc.full505_final_team4.data.dto;

import lombok.Data;

@Data
public class NovelSearchDTO {
  private int platform;
  private String platformId;
  private String novelTitle;
  private String novelThumbnail;
  private String novelIntro;
  private String novelIntroImg;
  private String novelAuthor;
  private String novelPubli;
  private int novelCount;
  private int novelPrice;
  private double novelStarRate;
  private String novelRelease;
  private String novelUpdateDate;
  private String novelRecentUpdate;
  private String cateList;

}
