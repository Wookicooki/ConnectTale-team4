package com.bitc.full505_final_team4.data.dto;

import com.bitc.full505_final_team4.data.entity.NovelReplyEntity;
import com.bitc.full505_final_team4.data.entity.ReportEntity;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@NoArgsConstructor
public class ReportDto2 {
  private int reportIdx;
  private int replyIdx;
  private String replyContent;
  private String novelTitle;
  private String novelAdult;
  private String ebookCheck;
  private String reportContent;
  private String reporter;
  private String suspect;
  private String reportDt;


  @Builder
  ReportDto2(int reportIdx, int replyIdx, String replyContent, String novelTitle, String novelAdult, String ebookCheck, String reportContent, String reporter, String suspect, LocalDateTime reportDt) {
    this.reportIdx = reportIdx;
    this.replyIdx = replyIdx;
    this.replyContent = replyContent;
    this.novelTitle = novelTitle;
    this.novelAdult = novelAdult;
    this.ebookCheck = ebookCheck;
    this.reportContent = reportContent;
    this.reporter = reporter;
    this.suspect = suspect;
    this.reportDt = reportDt.format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
  }

  public static ReportDto2 toDto(ReportEntity entity) {
    return ReportDto2.builder()
      .reportIdx(entity.getReportIdx())
      .replyIdx(entity.getReplyIdx().getReplyIdx())
      .replyContent(entity.getReplyIdx().getReplyContent())
      .novelTitle(entity.getReplyIdx().getNovelIdx().getNovelTitle())
      .novelAdult(entity.getReplyIdx().getNovelIdx().getNovelAdult())
      .ebookCheck(entity.getReplyIdx().getNovelIdx().getEbookCheck())
      .reportContent(entity.getReportContent())
      .reporter(entity.getReporter())
      .suspect(entity.getSuspect())
      .reportDt(entity.getReportDt())
      .build();
  }
}
