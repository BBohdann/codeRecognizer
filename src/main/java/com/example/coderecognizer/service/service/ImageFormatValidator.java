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

/**
 * Service responsible for validating image files for supported formats
 * and checking for non-empty content.
 */
@Service
public class ImageFormatValidator {
    private static final List<String> VALID_FORMATS = List.of("jpeg", "png", "gif");

    /**
     * Validates whether the uploaded image is non-empty and has a supported format.
     *
     * @param file the uploaded image file
     * @return true if the image is valid
     * @throws InvalidImageFormatException if the image format is not supported
     * @throws EmptyImageException if the file is empty or cannot be read as an image
     */
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

    /**
     * Attempts to detect the image format by reading the file contents
     * and checking the file extension.
     *
     * @param file the uploaded image file
     * @return the image format (e.g. "jpeg", "png")
     * @throws EmptyImageException if the image cannot be read or is empty
     */
    private String detectFormat(MultipartFile file) throws EmptyImageException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes())) {
            BufferedImage image = ImageIO.read(bis);
            if (image == null) {
                throw new EmptyImageException();
            }

            String formatName = guessFormatName(file.getOriginalFilename());
            return formatName != null ? formatName : "unknown";

        } catch (IOException e) {
            throw new RuntimeException("Failed to read image", e);
        }
    }

    /**
     * Extracts the file extension from the file name.
     *
     * @param fileName the name of the uploaded file
     * @return the format string (e.g. "png"), or null if not detected
     */
    private String guessFormatName(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return null;
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }
}
