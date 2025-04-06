package com.example.coderecognizer.service.service;

import com.example.coderecognizer.service.exeption.EmptyImageException;
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

/**
 * Service responsible for decoding barcode and QR code values from image files.
 * Supports multiple barcode formats such as QR Code, Code 128, EAN-8, and EAN-13.
 */
@Service
@RequiredArgsConstructor
public class CodeDecryptor {

    private final ImageFormatValidator formatValidator;

    /**
     * Decrypts a barcode or QR code from a multipart image file.
     *
     * @param file the uploaded image file
     * @return a formatted string containing the decoded code type and value
     * @throws InvalidImageFormatException if the image format is not supported
     * @throws IOException if an error occurs while reading the image
     * @throws EmptyImageException if the image file is empty
     */
    public String decryptCode(MultipartFile file) throws InvalidImageFormatException, IOException, EmptyImageException {
        validateImage(file);
        BufferedImage image = convertToImage(file);
        BinaryBitmap binaryBitmap = createBinaryBitmap(image);

        try {
            Result result = tryDecode(binaryBitmap);
            return extractCode(result, binaryBitmap, image);
        } catch (NotFoundException e) {
            throw new RuntimeException("Error decoding the code!", e);
        } catch (FormatException e) {
            throw new RuntimeException("Bad image format", e);
        }
    }

    /**
     * Validates whether the uploaded image has a supported format.
     *
     * @param file the uploaded file to validate
     * @throws InvalidImageFormatException if the image format is invalid
     * @throws EmptyImageException if the file is empty
     */
    private void validateImage(MultipartFile file) throws InvalidImageFormatException, EmptyImageException {
        if (!formatValidator.isValidImage(file)) {
            throw new InvalidImageFormatException("Invalid image format");
        }
    }

    /**
     * Converts the uploaded image file to a BufferedImage.
     *
     * @param file the uploaded image file
     * @return a BufferedImage representation of the file
     * @throws IOException if reading the image fails
     */
    private BufferedImage convertToImage(MultipartFile file) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(file.getBytes()));
    }

    /**
     * Creates a binary bitmap from the buffered image for barcode decoding.
     *
     * @param image the buffered image
     * @return a BinaryBitmap used for decoding
     */
    private BinaryBitmap createBinaryBitmap(BufferedImage image) {
        return new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(image)));
    }

    /**
     * Attempts to decode the barcode from the binary bitmap using ZXing hints.
     *
     * @param binaryBitmap the binary bitmap image
     * @return the decoded result
     * @throws NotFoundException if no code is found
     */
    private Result tryDecode(BinaryBitmap binaryBitmap) throws NotFoundException {
        Map<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        hints.put(DecodeHintType.TRY_HARDER, true);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, EnumSet.allOf(BarcodeFormat.class));
        return new MultiFormatReader().decode(binaryBitmap, hints);
    }

    /**
     * Extracts the code text based on the detected format.
     * Re-decodes if needed for certain formats to improve accuracy.
     *
     * @param result the initial decoded result
     * @param binaryBitmap the original binary bitmap
     * @param image the original buffered image
     * @return formatted string containing code type and value
     * @throws NotFoundException or FormatException if decoding fails
     */
    private String extractCode(Result result, BinaryBitmap binaryBitmap, BufferedImage image) throws NotFoundException, FormatException {
        return switch (result.getBarcodeFormat()) {
            case QR_CODE -> "QR Code: " + result.getText();
            case CODE_128 -> "Code 128: " + result.getText();
            case EAN_8 -> "EAN-8: " + new EAN8Reader().decode(binaryBitmap).getText();
            case EAN_13 -> {
                BinaryBitmap refreshedBitmap = createBinaryBitmap(image);
                yield "EAN-13: " + new EAN13Reader().decode(refreshedBitmap).getText();
            }
            default -> throw new RuntimeException("Unsupported barcode format: " + result.getBarcodeFormat());
        };
    }
}