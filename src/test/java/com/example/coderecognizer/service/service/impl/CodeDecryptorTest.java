package com.example.coderecognizer.service.service.impl;

import com.example.coderecognizer.service.dto.DecodedCode;
import com.example.coderecognizer.service.exeption.BarcodeDecodingException;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.example.coderecognizer.service.service.CodeDecryptor;
import com.example.coderecognizer.service.service.ImageFormatValidator;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeDecryptorTest {

    @Mock
    private ImageFormatValidator formatValidator;

    @InjectMocks
    private CodeDecryptor decryptor;

    @Test
    void decryptCode_ValidQrImage_ReturnsDecodedCode() throws Exception {
        when(formatValidator.isValidImage(any())).thenReturn(true);
        MockMultipartFile file = new MockMultipartFile("file", "qr.png", "image/png", generateQrCode("Hello World"));

        DecodedCode result = decryptor.decryptCode(file);

        assertThat(result.codeType()).isEqualTo("QR Code");
        assertThat(result.codeValue()).isEqualTo("Hello World");
    }

    @Test
    void decryptCode_ImageWithoutBarcode_ThrowsBarcodeDecodingException() throws Exception {
        when(formatValidator.isValidImage(any())).thenReturn(true);
        MockMultipartFile file = new MockMultipartFile("file", "blank.png", "image/png", generateBlankImage());

        assertThatThrownBy(() -> decryptor.decryptCode(file))
                .isInstanceOf(BarcodeDecodingException.class)
                .hasMessageContaining("No barcode or QR code could be found");
    }

    @Test
    void decryptCode_InvalidFormat_PropagatesValidatorException() throws Exception {
        when(formatValidator.isValidImage(any())).thenThrow(new InvalidImageFormatException("bmp"));
        MockMultipartFile file = new MockMultipartFile("file", "test.bmp", "image/bmp", new byte[]{1, 2, 3});

        assertThatThrownBy(() -> decryptor.decryptCode(file))
                .isInstanceOf(InvalidImageFormatException.class);
    }

    @Test
    void decryptCode_UnreadableContent_ThrowsEmptyImageException() throws Exception {
        when(formatValidator.isValidImage(any())).thenReturn(true);
        MockMultipartFile file = new MockMultipartFile("file", "garbage.png", "image/png",
                "not actually image bytes".getBytes());

        assertThatThrownBy(() -> decryptor.decryptCode(file))
                .isInstanceOf(EmptyImageException.class);
    }

    private byte[] generateQrCode(String content) throws WriterException, IOException {
        BitMatrix matrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 250, 250);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }

    private byte[] generateBlankImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, 100, 100);
        graphics.dispose();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", outputStream);
        return outputStream.toByteArray();
    }
}