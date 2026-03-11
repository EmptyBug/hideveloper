package click.backend.imageGen.inter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ImageGenService {
	// 1. 이미지 달라고 할 때
    Map<String, Object> getGenerationImages();
    
    // 2. 진화하라고 명령할 때
    void evolveToNextGeneration(int winnerIndex);
    
    // 3. 현재 몇 세대인지 물어볼 때
    int getCurrentGeneration();
    
    void generateImagesForGeneration(int gen);
}
