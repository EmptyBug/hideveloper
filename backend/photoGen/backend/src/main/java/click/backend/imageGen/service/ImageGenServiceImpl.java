package click.backend.imageGen.service;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import click.backend.imageGen.inter.ImageGenService;

@Service
public class ImageGenServiceImpl implements ImageGenService{
		
	@Value("${project.image.base-dir}")
	private String BASE_DIR;
	
	// 서버 메모리에서 현재 세대를 기억하고 추적합니다.
    private int currentGeneration = 1;
    public int getCurrentGeneration() {
        return currentGeneration;
    }
 // 완전 무작위 노이즈 이미지 3개 생성 (1세대용)
   @Override
   public void generateRandomImages(int gen) {
        File dir = new File(BASE_DIR + gen + "/");
        if (!dir.exists()) dir.mkdirs();
        try {
            for (int i = 1; i <= 3; i++) {
                File file = new File(dir, "image_" + i + ".png");
                if (!file.exists()) createNoiseImage(file, null, 1.0); // 100% 랜덤
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    // 🧬 진화 명령을 받으면 다음 세대를 생성하는 핵심 메서드4
   @Override
   public synchronized void evolveToNextGeneration(int winnerIndex) {
        System.out.println("🛠️ [이미지 공장] " + currentGeneration + "세대의 " + winnerIndex + "번 이미지를 바탕으로 진화 시작!");
        try {
            // 1. 1등 이미지 읽어오기
            File winnerFile = new File(BASE_DIR + currentGeneration + "/image_" + winnerIndex + ".png");
            BufferedImage winnerImage = ImageIO.read(winnerFile);

            // 2. 다음 세대 폴더 준비
            currentGeneration++; 
            File nextGenDir = new File(BASE_DIR + currentGeneration + "/");
            if (!nextGenDir.exists()) nextGenDir.mkdirs();

            // 3. 요구사항에 맞춘 돌연변이 적용 (1등 유전자 기반)
            createNoiseImage(new File(nextGenDir, "image_1.png"), winnerImage, 0.30); // 1번: 변화 많이 (30%)
            createNoiseImage(new File(nextGenDir, "image_2.png"), winnerImage, 0.10); // 2번: 변화 적당히 (10%)
            createNoiseImage(new File(nextGenDir, "image_3.png"), winnerImage, 0.01); // 3번: 변화 거의 없게 (1%)

            System.out.println("✅ [이미지 공장] " + currentGeneration + "세대 생성 완료!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
	 // 특정 세대의 폴더를 만들고 3개의 노이즈 이미지를 생성하는 메서드
   @Override
   public void generateImagesForGeneration(int generation) {
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
   @Override
    public void createNoiseImage(File saveFile, BufferedImage original, double mutationRate) throws Exception {
        int width = 256, height = 256;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Random random = new Random();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p;
                // 돌연변이 확률에 당첨되거나, 원본이 아예 없는 경우 무작위 노이즈
                if (original == null || random.nextDouble() < mutationRate) {
                    int a = 255, r = random.nextInt(256), g = random.nextInt(256), b = random.nextInt(256);
                    p = (a << 24) | (r << 16) | (g << 8) | b;
                } else {
                    // 당첨되지 않으면 부모(1등)의 픽셀을 그대로 물려받음
                    p = original.getRGB(x, y);
                }
                newImage.setRGB(x, y, p);
            }
        }
        ImageIO.write(newImage, "png", saveFile);
    }

    // 실제 노이즈 픽셀을 그리고 파일로 저장하는 로직
    @Override
    public void createAndSaveNoiseImage(File file) throws IOException {
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
    @Override
    public Map<String, Object> getGenerationImages() {
        Map<String, Object> response = new HashMap<>();
        List<String> base64Images = new ArrayList<>();
        
        // ★ 자기가 알고 있는 '현재 세대' 폴더를 정확히 찾아갑니다.
        String genDirPath = BASE_DIR + currentGeneration + "/";

        try {
            for (int i = 1; i <= 3; i++) {
                File imageFile = new File(genDirPath + "image_" + i + ".png");
                
                if (imageFile.exists()) {
                    byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    base64Images.add("data:image/png;base64," + base64);
                } else {
                    System.out.println("❌ 이미지를 찾을 수 없습니다: " + imageFile.getAbsolutePath());
                }
            }
            
            response.put("generation", currentGeneration);
            response.put("images", base64Images); 
            return response;

        } catch (Exception e) {
            System.err.println("이미지 읽기 실패: " + e.getMessage());
            return response;
        }
    }

}
