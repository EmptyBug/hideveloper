package click.backend.imageGen.inter;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface ImageGenService {
	
	/**
	 * 이미지 생성
	 * @param generation
	 */
	void generateImagesForGeneration(int generation);
	/**
	 * 노이즈 생성및 저장
	 * @param file
	 */
	void createAndSaveNoiseImage(File file)  throws IOException;
	Map<String, Object> getGenerationImages();
	public int getCurrentGeneration();
	void generateRandomImages(int gen);
	public void evolveToNextGeneration(int winnerIndex);
	void createNoiseImage(File saveFile, BufferedImage original, double mutationRate) throws Exception;
}
