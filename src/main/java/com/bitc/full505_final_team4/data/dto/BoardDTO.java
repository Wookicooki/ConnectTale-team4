package com.bitc.full505_final_team4.data.dto;

import com.bitc.full505_final_team4.data.entity.BoardEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardDTO {

    private int boardIdx;
    private String boardTitle;
    private String boardContents;
    private String createId;
    private LocalDateTime createDt;
    private String reqCate;
    private int boardCate;

    @Builder
    public BoardDTO(int boardIdx, String boardTitle, String boardContents, String createId, LocalDateTime createDt, String reqCate) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContents = boardContents;
        this.createId = createId;
        this.createDt = createDt;
        this.reqCate = reqCate;
    }

    public static BoardDTO toDTO(BoardEntity entity) {
        String reqName = "";
        if (entity.getReqCate() != null)
            reqName = entity.getReqCate().getReqName();
        return BoardDTO.builder()
                .boardIdx(entity.getBoardIdx())
                .boardTitle(entity.getBoardTitle())
                .boardContents(entity.getBoardContents())
                .createId(entity.getCreateId().getNickname())
                .createDt(entity.getCreateDt())
                .reqCate(reqName)
                .build();
    }

}
