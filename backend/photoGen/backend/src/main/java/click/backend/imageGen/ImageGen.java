package click.backend.imageGen;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ImageGen {

    // 현재 세대를 나타내는 변수 (1세대부터 시작)
    private int currentGeneration = 1;
    
    // 요청하신 기본 저장 경로
    private final String BASE_DIR = "./src/main/java/backend/imageGen/savedimage/";

    @PostConstruct
    public void init() {
        // 서버가 켜질 때 현재 세대(1세대)의 이미지가 없으면 3개를 생성합니다.
        generateImagesForGeneration(currentGeneration);
    }

    // 특정 세대의 폴더를 만들고 3개의 노이즈 이미지를 생성하는 메서드
    private void generateImagesForGeneration(int generation) {
        String genDirPath = BASE_DIR + generation + "/";
        File dir = new File(genDirPath);
        
        // 해당 세대의 폴더가 없으면 새로 만듭니다.
        if (!dir.exists()) {
            dir.mkdirs(); 
        }

        try {
            // 이미지 3개 생성 (image_1.png, image_2.png, image_3.png)
            for (int i = 1; i <= 3; i++) {
                File imageFile = new File(genDirPath + "image_" + i + ".png");
                if (!imageFile.exists()) {
                    createAndSaveNoiseImage(imageFile);
                }
            }
            System.out.println("✅ " + generation + "세대 이미지 3개 준비 완료: " + genDirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 실제 노이즈 픽셀을 그리고 파일로 저장하는 로직
    private void createAndSaveNoiseImage(File file) throws IOException {
        int width = 256;
        int height = 256;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Random random = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int a = 255;
                int r = random.nextInt(256);
                int g = random.nextInt(256);
                int b = random.nextInt(256);
                int p = (a << 24) | (r << 16) | (g << 8) | b;
                image.setRGB(x, y, p);
            }
        }
        ImageIO.write(image, "png", file);
    }

    // 프론트엔드로 세대 정보와 3개의 이미지를 모두 보내주는 API
    @GetMapping("/api/image/noise")
    public Map<String, Object> getGenerationImages() {
        Map<String, Object> response = new HashMap<>();
        List<String> base64Images = new ArrayList<>();
        String genDirPath = BASE_DIR + currentGeneration + "/";

        try {
            // 폴더에서 3개의 이미지를 읽어 Base64로 변환 후 리스트에 담습니다.
            for (int i = 1; i <= 3; i++) {
                File imageFile = new File(genDirPath + "image_" + i + ".png");
                if (imageFile.exists()) {
                    byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    base64Images.add("data:image/png;base64," + base64);
                }
            }
            
            // JSON 형태로 { generation: 1, images: ["data...", "data...", "data..."] } 반환
            response.put("generation", currentGeneration);
            response.put("images", base64Images);
            return response;

        } catch (IOException e) {
            throw new RuntimeException("저장된 이미지를 읽어오는데 실패했습니다.", e);
        }
    }
}