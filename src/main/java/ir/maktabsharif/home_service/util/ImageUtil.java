package ir.maktabsharif.home_service.util;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
@Component
public class ImageUtil {
    public byte[] getBytesForExpert(String imagePath) {
        byte[] imageBytes;
        try{
            imageBytes = Files.readAllBytes(Paths.get(imagePath));
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return imageBytes;
    }
}
