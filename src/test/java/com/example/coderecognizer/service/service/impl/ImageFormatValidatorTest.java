package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.ImageFormatValidator;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ImageFormatValidatorTest {

    private final ImageFormatValidator validator = new ImageFormatValidator();

    @Test
    void isValidImage_EmptyFile_ThrowsEmptyImageException() {
        MockMultipartFile emptyFile = new MockMultipartFile("file", "empty.png", "image/png", new byte[0]);

        assertThatThrownBy(() -> validator.isValidImage(emptyFile))
                .isInstanceOf(EmptyImageException.class)
                .hasMessage("This image is empty");
    }

    @Test
    void isValidImage_ValidPng_ReturnsTrue() throws Exception {
        byte[] pngBytes = generateImageBytes("png");
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", pngBytes);

        assertThat(validator.isValidImage(file)).isTrue();
    }

    @Test
    void isValidImage_ValidJpeg_ReturnsTrue() throws Exception {
        byte[] jpegBytes = generateImageBytes("jpg");
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", jpegBytes);

        assertThat(validator.isValidImage(file)).isTrue();
    }

    @Test
    void isValidImage_ValidGif_ReturnsTrue() throws Exception {
        byte[] gifBytes = generateImageBytes("gif");
        MockMultipartFile file = new MockMultipartFile("file", "test.gif", "image/gif", gifBytes);

        assertThat(validator.isValidImage(file)).isTrue();
    }

    @Test
    void isValidImage_UnsupportedFormat_ThrowsInvalidImageFormatException() throws Exception {
        byte[] bmpBytes = generateImageBytes("bmp");
        MockMultipartFile file = new MockMultipartFile("file", "test.bmp", "image/bmp", bmpBytes);

        assertThatThrownBy(() -> validator.isValidImage(file))
                .isInstanceOf(InvalidImageFormatException.class);
    }

    @Test
    void isValidImage_UnrecognizableContent_ThrowsInvalidImageFormatException() {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain",
                "this is not an image".getBytes());

        assertThatThrownBy(() -> validator.isValidImage(file))
                .isInstanceOf(InvalidImageFormatException.class);
    }

    @Test
    void isValidImage_IOExceptionOnRead_ThrowsEmptyImageException() throws IOException {
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("stream failure"));

        assertThatThrownBy(() -> validator.isValidImage(file))
                .isInstanceOf(EmptyImageException.class);
    }

    private byte[] generateImageBytes(String format) throws IOException {
        BufferedImage image = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.BLUE);
        graphics.fillRect(0, 0, 50, 50);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, format, outputStream);
        return outputStream.toByteArray();
    }
}