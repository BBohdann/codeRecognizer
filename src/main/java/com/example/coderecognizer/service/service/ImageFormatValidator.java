package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class ImageFormatValidator {
    private final List<String> validFormats = Arrays.asList("JPEG", "PNG", "GIF");

    private String recognizeFormat(MultipartFile file) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
            BufferedImage image = ImageIO.read(bis);
            return getImageFormat(image);
        } catch (IOException | EmptyImageException | InvalidImageFormatException e) {
            e.printStackTrace();
        }
        return "unknown image";
    }

    private String getImageFormat(BufferedImage image) throws EmptyImageException, InvalidImageFormatException {
        if (image == null) {
            throw new EmptyImageException();
        }
        String formatName = ImageIO.getImageReadersByMIMEType("image/jpeg").next().getOriginatingProvider().getFormatNames()[0];

        if (!formatName.equals("JPEG") && !formatName.equals("PNG") && !formatName.equals("GIF")) {
            throw new InvalidImageFormatException(formatName);
        }
        return formatName;
    }

    public boolean isValidImage(MultipartFile file) {
        return validFormats.contains(recognizeFormat(file).toUpperCase());
    }
}


