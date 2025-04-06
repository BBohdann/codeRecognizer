package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CodeDecryptorTest {

    private ImageFormatValidator formatValidator;
    private CodeDecryptor codeDecryptor;

    @BeforeEach
    void setUp() {
        formatValidator = mock(ImageFormatValidator.class);
        codeDecryptor = new CodeDecryptor(formatValidator);
    }

    @Test
    void shouldReturnQRCodeText_WhenValidQRImageProvided() throws Exception {
        String qrText = "Hello QR";
        BufferedImage qrImage = generateQRImage(qrText);
        MultipartFile file = convertImageToMultipartFile(qrImage);

        when(formatValidator.isValidImage(file)).thenReturn(true);

        String result = codeDecryptor.decryptCode(file);

        assertTrue(result.contains("QR Code:"));
        assertTrue(result.contains(qrText));
    }

    @Test
    void shouldThrowInvalidImageFormatException_WhenImageIsInvalid() throws InvalidImageFormatException, EmptyImageException {
        MultipartFile file = mock(MultipartFile.class);
        when(formatValidator.isValidImage(file)).thenReturn(false);

        assertThrows(InvalidImageFormatException.class, () -> codeDecryptor.decryptCode(file));
    }

    @Test
    void shouldThrowRuntimeException_WhenBarcodeNotFound() throws Exception {
        BufferedImage emptyImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        MultipartFile file = convertImageToMultipartFile(emptyImage);

        when(formatValidator.isValidImage(file)).thenReturn(true);

        assertThrows(RuntimeException.class, () -> codeDecryptor.decryptCode(file));
    }

    private BufferedImage generateQRImage(String text) throws Exception {
        int width = 200;
        int height = 200;
        BitMatrix matrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, width, height);
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private MultipartFile convertImageToMultipartFile(BufferedImage image) throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(image, "png", output);
        return new MockMultipartFile("file", "image.png", "image/png", output.toByteArray());
    }
}