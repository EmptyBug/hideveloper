package click.backend.imageGen.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import click.backend.imageGen.inter.ImageGenService;
import jakarta.annotation.PostConstruct;

@Service
public class ImageGenPolygonImpl implements ImageGenService {

    private int currentGeneration = 1;

	@Value("${project.image.base-dir}")
    private String BASE_DIR;
    
    // 설정값: 도화지 크기와 삼각형 개수
    private final int WIDTH = 256;
    private final int HEIGHT = 256;
    private final int SHAPE_COUNT = 50; 

    // ★ 핵심: 이미지 파일이 아니라 '도형들의 정보(DNA)'를 메모리에 기억하는 저장소
    // 이미지 번호(1,2,3) -> 삼각형 50개가 들어있는 리스트
    private Map<Integer, List<MyTriangle>> dnaStorage = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        generateFirstGeneration();
    }

    // 1. 최초 1세대 무작위 도형 생성
    private void generateFirstGeneration() {
        File dir = new File(BASE_DIR + currentGeneration + "/");
        if (!dir.exists()) dir.mkdirs();

        for (int i = 1; i <= 3; i++) {
            List<MyTriangle> dna = new ArrayList<>();
            for (int j = 0; j < SHAPE_COUNT; j++) {
                dna.add(new MyTriangle()); // 완전 랜덤 삼각형 50개 생성
            }
            dnaStorage.put(i, dna); // DNA 저장
            drawAndSaveImage(dna, new File(dir, "image_" + i + ".png")); // 그림으로 렌더링
        }
    }

    @Override
    public int getCurrentGeneration() {	
        return currentGeneration;
    }

    // 2. 🧬 다음 세대로 진화! (도형의 좌표와 색상을 비틂)
    @Override
    public synchronized void evolveToNextGeneration(int winnerIndex) {
        System.out.println("🎨 [도형 진화] " + currentGeneration + "세대의 " + winnerIndex + "번 유전자로 진화 시작!");

        // 1등의 DNA(삼각형 리스트) 꺼내오기
        List<MyTriangle> winnerDna = dnaStorage.get(winnerIndex);
        if (winnerDna == null) return; 

        currentGeneration++;
        File nextGenDir = new File(BASE_DIR + currentGeneration + "/");
        if (!nextGenDir.exists()) nextGenDir.mkdirs();

        Map<Integer, List<MyTriangle>> nextGenDnaStorage = new ConcurrentHashMap<>();

        // 1등의 DNA를 복제해서 3개의 자식 생성
        for (int i = 1; i <= 3; i++) {
            List<MyTriangle> childDna = new ArrayList<>();
            for (MyTriangle t : winnerDna) {
                childDna.add(t.cloneTriangle()); // 유전자 그대로 복사
            }

            // 돌연변이 발생 (1번은 많이 30%, 2번은 적당히 10%, 3번은 조금만 2%)
            double mutationRate = (i == 1) ? 0.30 : (i == 2) ? 0.10 : 0.02;
            mutateDna(childDna, mutationRate);

            // 자식의 DNA 저장 및 렌더링
            nextGenDnaStorage.put(i, childDna);
            drawAndSaveImage(childDna, new File(nextGenDir, "image_" + i + ".png"));
        }

        // DNA 저장소를 새로운 세대로 교체
        this.dnaStorage = nextGenDnaStorage;
        System.out.println("✅ " + currentGeneration + "세대 도형 생성 완료!");
    }

    // 돌연변이 적용 로직
    private void mutateDna(List<MyTriangle> dna, double mutationRate) {
        Random rand = new Random();
        for (MyTriangle t : dna) {
            if (rand.nextDouble() < mutationRate) {
                t.mutate(); // 확률에 당첨된 삼각형 모양이나 색상을 비틂
            }
        }
    }

    // 3. 🖌️ 메모리의 DNA(삼각형 리스트)를 실제 그림으로 그려서 저장하는 화가 로직
    private void drawAndSaveImage(List<MyTriangle> dna, File saveFile) {
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // 도화지를 흰색으로 칠하기
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, WIDTH, HEIGHT);

        // 안티앨리어싱 (도형 테두리를 부드럽게)
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 삼각형 50개를 순서대로 겹쳐서 그리기
        for (MyTriangle t : dna) {
            g2d.setColor(new Color(t.r, t.g, t.b, t.a));
            g2d.fillPolygon(t.x, t.y, 3);
        }

        g2d.dispose(); // 붓 내려놓기

        try {
            ImageIO.write(image, "png", saveFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 4. 프론트엔드로 이미지 보내주는 로직 (기존과 동일)
    @Override
    public Map<String, Object> getGenerationImages() {
        Map<String, Object> response = new HashMap<>();
        List<String> base64Images = new ArrayList<>();
        String genDirPath = BASE_DIR + currentGeneration + "/";

        try {
            for (int i = 1; i <= 3; i++) {
                File imageFile = new File(genDirPath + "image_" + i + ".png");
                if (imageFile.exists()) {
                    byte[] imageBytes = Files.readAllBytes(imageFile.toPath());
                    String base64 = Base64.getEncoder().encodeToString(imageBytes);
                    base64Images.add("data:image/png;base64," + base64);
                }
            }
            response.put("generation", currentGeneration);
            response.put("images", base64Images);
            return response;
        } catch (Exception e) {
            return response;
        }
    }

    // ==========================================
    // 🧬 유전자 역할을 하는 '삼각형' 클래스
    // ==========================================
    public static class MyTriangle {
        int[] x = new int[3];
        int[] y = new int[3];
        int r, g, b, a;

        public MyTriangle() {
            randomize();
        }

        // 초기화: 도화지 안의 무작위 좌표와 무작위 색상
        public void randomize() {
            Random rand = new Random();
            for (int i = 0; i < 3; i++) {
                x[i] = rand.nextInt(256);
                y[i] = rand.nextInt(256);
            }
            r = rand.nextInt(256); g = rand.nextInt(256); b = rand.nextInt(256);
            a = rand.nextInt(100) + 50; // 반투명하게 (50~150)
        }

        // 유전자 복제
        public MyTriangle cloneTriangle() {
            MyTriangle t = new MyTriangle();
            System.arraycopy(this.x, 0, t.x, 0, 3);
            System.arraycopy(this.y, 0, t.y, 0, 3);
            t.r = this.r; t.g = this.g; t.b = this.b; t.a = this.a;
            return t;
        }

        // 👾 돌연변이: 확률적으로 3가지 중 하나의 변화 발생
        public void mutate() {
            Random rand = new Random();
            int type = rand.nextInt(3); 

            if (type == 0) {
                // 꼭짓점 하나를 골라서 좌표를 조금 이동
                int point = rand.nextInt(3);
                x[point] = Math.max(0, Math.min(255, x[point] + (rand.nextInt(61) - 30)));
                y[point] = Math.max(0, Math.min(255, y[point] + (rand.nextInt(61) - 30)));
            } else if (type == 1) {
                // 색상을 조금 변경
                r = Math.max(0, Math.min(255, r + (rand.nextInt(51) - 25)));
                g = Math.max(0, Math.min(255, g + (rand.nextInt(51) - 25)));
                b = Math.max(0, Math.min(255, b + (rand.nextInt(51) - 25)));
            } else {
                // 삼각형 하나를 아예 새로운 랜덤 삼각형으로 교체
                randomize();
            }
        }
    }
    
    @Override
    public void generateImagesForGeneration(int generation) {
        System.out.println("✨ [" + generation + "세대] 무작위 도형 이미지 생성을 시작합니다.");
        
        File dir = new File(BASE_DIR + generation + "/");
        if (!dir.exists()) dir.mkdirs();

        // 3개의 이미지를 완전히 무작위로 새로 그립니다.
        for (int i = 1; i <= 3; i++) {
            List<MyTriangle> dna = new ArrayList<>();
            for (int j = 0; j < SHAPE_COUNT; j++) {
                dna.add(new MyTriangle()); // 무작위 삼각형 50개 
            }
            dnaStorage.put(i, dna); // 메모리에 유전자 저장 (기존 1,2,3번 덮어쓰기)
            drawAndSaveImage(dna, new File(dir, "image_" + i + ".png")); // 도화지에 그리기
        }
        System.out.println("✅ [" + generation + "세대] 무작위 생성 완료!");
    }
}
