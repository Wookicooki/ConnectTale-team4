package com.bitc.full505_final_team4.common;

import com.bitc.full505_final_team4.data.dto.MemberDto;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

// input 태그를 통해 입력된 file형태의 데이터를
@Component
public class ProfileUtils {

  public MemberDto parseProfileInfo(String id, MultipartHttpServletRequest uploadFiles) throws Exception {
    if (ObjectUtils.isEmpty(uploadFiles)) {
      return null;
    }


    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
    String date = String.valueOf(System.nanoTime());
    String current_date = date + id;

    String path = "/home/ec2-user/bmn_img";

    File file = new File(path);

    if (!file.exists()) {
      file.mkdirs();
    }

    System.out.println("폴더 존재?");
    Iterator<String> iterator = uploadFiles.getFileNames();

    String newFileName;
    String originalFileExtension;
    String contentType;
    MemberDto profile = new MemberDto();

    while (iterator.hasNext()) {
      List<MultipartFile> fileLists = uploadFiles.getFiles(iterator.next());
      for (MultipartFile multipartFile : fileLists) {
        if (!multipartFile.isEmpty()) {
          contentType = multipartFile.getContentType();
          if (ObjectUtils.isEmpty(contentType)) {
            break;
          } else {
            if (contentType.contains("image/jpeg")) {
              originalFileExtension = ".jpg";
            } else if (contentType.contains("image/png")) {
              originalFileExtension = ".png";
            } else if (contentType.contains("image/gif")) {
              originalFileExtension = ".gif";
            } else {
              break;
            }
          }

          newFileName = current_date + originalFileExtension;
          profile.setSFile(newFileName);

          file = new File(path + "/" + newFileName);
          multipartFile.transferTo(file);
        }
      }
    }
    return profile;
  }

}
