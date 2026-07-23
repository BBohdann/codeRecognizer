package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.dto.DecodedCode;
import com.example.coderecognizer.service.exeption.BarcodeDecodingException;
import com.example.coderecognizer.service.exeption.EmptyImageException;
import com.example.coderecognizer.service.exeption.InvalidImageFormatException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
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

    private final ImageFormatValidator formatValidator;

    public DecodedCode decryptCode(MultipartFile file) throws InvalidImageFormatException, IOException, EmptyImageException {
        validateImage(file);
        BufferedImage image = convertToImage(file);
        if (image == null) {
            throw new EmptyImageException();
        }

        BinaryBitmap binaryBitmap = createBinaryBitmap(image);

        try {
            Result result = decode(binaryBitmap);
            return new DecodedCode(formatLabel(result.getBarcodeFormat()), result.getText());
        } catch (NotFoundException e) {
            throw new BarcodeDecodingException("No barcode or QR code could be found in this image", e);
        }
    }

    private void validateImage(MultipartFile file) throws InvalidImageFormatException, EmptyImageException {
        if (!formatValidator.isValidImage(file)) {
            throw new InvalidImageFormatException("Invalid image format");
        }
    }

    private BufferedImage convertToImage(MultipartFile file) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    }

    private BinaryBitmap createBinaryBitmap(BufferedImage image) {
        return new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
    }

    private Result decode(BinaryBitmap binaryBitmap) throws NotFoundException {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, true);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
        return new MultiFormatReader().decode(binaryBitmap, hints);
    }

    private String formatLabel(BarcodeFormat format) {
        return switch (format) {
            case QR_CODE -> "QR Code";
            case CODE_128 -> "Code 128";
            case EAN_8 -> "EAN-8";
            case EAN_13 -> "EAN-13";
            default -> format.name();
        };
    }
}