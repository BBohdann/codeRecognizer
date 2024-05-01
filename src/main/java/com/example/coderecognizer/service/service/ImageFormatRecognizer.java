package com.example.coderecognizer.service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageFormatRecognizer {
    private final List<String> validFormats = Arrays.asList("JPEG", "PNG", "GIF");

    private String recognizeFormat(MultipartFile file) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            BufferedImage image = ImageIO.read(bis);
            return getImageFormat(image);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "unknown image";
    }

    private String getImageFormat(BufferedImage image) {
        if (image == null) {
            return "empty image!";
            //todo custom exeption on empty image
        }
        String formatName = "unknown format";
        try {
            formatName = ImageIO.getImageReadersByMIMEType("image/jpeg").next().getOriginatingProvider().getFormatNames()[0];
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formatName;
    }

    public boolean isValidImage(MultipartFile file) {
        String format = recognizeFormat(file);
        return validFormats.contains(format.toUpperCase());
    }
}
