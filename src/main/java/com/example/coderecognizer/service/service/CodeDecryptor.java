package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;
import com.google.zxing.oned.EAN8Reader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CodeDecryptor {
    private final ImageFormatValidator formatRecognizer;

    public String decryptCode(MultipartFile file) throws InvalidImageFormatException {
        try {
            if(!formatRecognizer.isValidImage(file))
                throw new InvalidImageFormatException("InvalidImageFormat");
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(file.getBytes()));
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
            MultiFormatReader reader = new MultiFormatReader();
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = reader.decode(binaryBitmap, hints);
            BarcodeFormat barcodeFormat = result.getBarcodeFormat();

            switch (barcodeFormat) {
                case QR_CODE:
                    return "QR Code: " + result.getText();
                case EAN_8:
                    EAN8Reader ean8Reader = new EAN8Reader();
                    result = ean8Reader.decode(binaryBitmap);
                    return "EAN-8: " + result.getText();
                case EAN_13:
                    EAN13Reader ean13Reader = new EAN13Reader();
                    result = ean13Reader.decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image))));
                    return "EAN-13: " + result.getText();
                case CODE_128:
                    return "Code 128: " + result.getText();
                default:
                    throw new RuntimeException("Unsupported Code Format");
            }
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("Erorr decoding this code!");
        } catch (FormatException e) {
            throw new RuntimeException("Bad image format");
        }
    }
}
