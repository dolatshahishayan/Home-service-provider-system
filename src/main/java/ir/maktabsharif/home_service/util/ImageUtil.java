package ir.maktabsharif.home_service.util;

import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
@Component
public class ImageUtil {
    public byte[] getBytesForExpert(String imagePath) {
        String normalizedPath = imagePath.replace("\\", File.separator);
        byte[] imageBytes;
        try{
            imageBytes = Files.readAllBytes(Paths.get(normalizedPath));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return imageBytes;
    }
}
