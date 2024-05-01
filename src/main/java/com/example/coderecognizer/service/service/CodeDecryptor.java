package com.example.coderecognizer.service.service;


import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.oned.EAN13Reader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

@Service
public class CodeDecryptor {

    public String decryptCode(MultipartFile file) {
        try {
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image == null) {
                return "Invalid image format";
            }

            // Визначення типу коду
            Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));

            MultiFormatReader reader = new MultiFormatReader();
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
            Result result = reader.decode(binaryBitmap, hints);
            BarcodeFormat barcodeFormat = result.getBarcodeFormat();

            // Розшифрування коду
            switch (barcodeFormat) {
                case QR_CODE:
                    return "QR Code: " + result.getText();
                case EAN_13:
                    EAN13Reader ean13Reader = new EAN13Reader();
                    result = ean13Reader.decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image))));
                    return "EAN-13: " + result.getText();
                case CODE_128:
                    // Розшифрування Code 128 ви можете реалізувати за необхідністю
                    return "Code 128: " + result.getText();
                default:
                    return "Unsupported code format";
            }
        } catch (IOException | NotFoundException e) {
            e.printStackTrace();
            return "Error decoding code";
        } catch (FormatException e) {
            throw new RuntimeException(e);
        }
    }
}
