package com.smallstudy.utils;

import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.dto.FileDTO;
import com.smallstudy.error.BadRequestException;
import com.smallstudy.error.IORuntimeException;
import com.smallstudy.error.UnSupportedImageFileExtensionException;
import com.smallstudy.error.UnSupportedImageFileTypeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class FileService {

    @Value("${file.dir}")
    private String fileDir;
    private final String apiBase = "/image/";
    private static final List<String> ALLOWED_CONTENT_TYPE = Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif");
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpeg", "jpg", "png", "gif");

    public String getImageFilePath(Object principal) {
        if(principal instanceof Member member) {
            String imgPath = member.getImgPath();
            String uuid = member.getImgUuid();

            if(Objects.isNull(imgPath) || Objects.isNull(uuid))
                return null;

            return apiBase + imgPath + "/" + uuid;
        }
        return null;
    }

    public FileDTO saveMultiPartFile(MultipartFile file) {

        if(!ALLOWED_CONTENT_TYPE.contains(file.getContentType()))
            throw new UnSupportedImageFileTypeException("   The file type " + file.getContentType() + " is not supported");

        String originalFilename = file.getOriginalFilename();

        if(Objects.isNull(originalFilename)) {
            throw new BadRequestException("filename is empty!");
        }

        int pos = originalFilename.lastIndexOf(".");

        if(pos == -1)
            throw new UnSupportedImageFileExtensionException("UnSupportedImageFileExtensionException");

        String ext = originalFilename.substring(pos + 1);

        if(!ALLOWED_EXTENSIONS.contains(ext))
            throw new UnSupportedImageFileExtensionException("UnSupportedImageFileExtensionException");

        LocalDateTime now = LocalDateTime.now();

        Path basePath = getBasePath();
        Path path = Paths.get(String.valueOf(now.getYear()), String.valueOf(now.getMonthValue()), String.valueOf(now.getDayOfMonth()));
        Path fullPath = basePath.resolve(path);

        createDirectory(fullPath);

        String uuid = UUID.randomUUID() + "." + ext;
        Path fullPathAndFile = fullPath.resolve(uuid);
        try {
            file.transferTo(fullPathAndFile.toFile());
        } catch (IOException e) {
            log.info("saveMultiPartFile : {}, 파일 저장 불가!", fullPathAndFile);
            throw new IORuntimeException(e);
        }

        return new FileDTO(originalFilename, uuid, path.toString().replace("\\", "/"));
    }

    public FileDTO saveQuillImageFile(Map<String, Object> map, List<Map<String, Object>> list) {

        String value = (String) map.get("image");
        String[] split = value.split(",");

        if(split.length < 2) {
            list.add(Map.of("insert",map));
            return null;
        }

        String mimeType = getMimeType(split);
        byte[] bytes = getBytes(split);

        LocalDateTime now = LocalDateTime.now();

        Path basePath = getBasePath();
        Path timePath = Paths.get(String.valueOf(now.getYear()), String.valueOf(now.getMonthValue()), String.valueOf(now.getDayOfMonth()));
        Path fullPath = basePath.resolve(timePath);

        createDirectory(fullPath);

        String ext = getExtension(mimeType);

        if (!ALLOWED_EXTENSIONS.contains(ext))
            throw new UnSupportedImageFileExtensionException("UnSupportedImageFileExtensionException");

        String filename = UUID.randomUUID() + "." + ext;
        String fileFullPath = fullPath.resolve(filename).toString();

        Path imageBase = Paths.get(apiBase);
        String webPath = imageBase.resolve(timePath).resolve(filename).toString();

        try (FileOutputStream fos = new FileOutputStream(fileFullPath)) {
            fos.write(bytes);
            list.add(Map.of("insert", Map.of("image", webPath.replace("\\", "/"))));
            return new FileDTO("", filename, webPath);
        } catch (IOException e) {
            log.error("saveQuillImageFile : {} 파일 생성 불가!", fileFullPath);
            throw new IORuntimeException(e);
        }
    }

    private Path getBasePath() {
        return Paths.get(fileDir);
    }

    private void createDirectory(Path fullPath) {
        if(!Files.exists(fullPath))
        {
            try {
                Files.createDirectories(fullPath);
            } catch (IOException e) {
                log.info("saveMultiPartFile : {} 디렉터리 생성 불가!", fullPath);
                throw new IORuntimeException(e);
            }
        }
    }

    private byte[] getBytes(String[] split) {
        String data = split[1];
        return Base64.getDecoder().decode(data);
    }

    private String getMimeType(String[] split) {
        String metaData = split[0];
        int pos = metaData.indexOf(":");
        return metaData.substring(pos + 1).split(";")[0];
    }

    private String getExtension(String mimeType) {
        return switch (mimeType) {
            case "image/jpg" -> "jpg";
            case "image/jpeg" -> "jpeg";
            case "image/gif" -> "gif";
            case "image/png" -> "png";
            default -> "";
        };
    }

}
