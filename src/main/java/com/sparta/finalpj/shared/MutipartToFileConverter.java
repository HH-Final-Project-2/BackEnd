package com.sparta.finalpj.shared;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;

@Component
public class MutipartToFileConverter {
  public Optional<File> convert(MultipartFile images) throws IOException {
    File convertFile = new File(
        System.getProperty("user.dir") +"/"+ images.getOriginalFilename()
    );
    if(convertFile.createNewFile()) {
      try (FileOutputStream fos = new FileOutputStream(convertFile)) {
        fos.write(images.getBytes());
      }
      return Optional.of(convertFile);
    }

    return Optional.empty();
  }
}
