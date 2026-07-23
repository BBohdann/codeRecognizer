package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

@Service
public class ImageFormatValidator {
    private static final List<String> VALID_FORMATS = List.of("jpeg", "png", "gif");

    public boolean isValidImage(MultipartFile file) throws InvalidImageFormatException, EmptyImageException {
        if (file.isEmpty()) {
            throw new EmptyImageException();
        }

        String format = detectFormat(file);
        if (!VALID_FORMATS.contains(format.toLowerCase())) {
            throw new InvalidImageFormatException(format);
        }

        return true;
    }

    private String detectFormat(MultipartFile file) throws EmptyImageException {
        try (ImageInputStream iis = ImageIO.createImageInputStream(file.getInputStream())) {
            if (iis == null) {
                throw new EmptyImageException();
            }

            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (!readers.hasNext()) {
                return "unknown";
            }
            return readers.next().getFormatName();
        } catch (IOException e) {
            throw new EmptyImageException();
        }
    }
}