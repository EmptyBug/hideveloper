package click.backend.imageGen.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import click.backend.imageGen.inter.ImageGenService;
import jakarta.annotation.PostConstruct;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class ImageGenController {

	private static ImageGenService imageGenService;
	
	@Autowired
	public ImageGenController(ImageGenService imageGenService) {
		this.imageGenService = imageGenService;
	}
    // 현재 세대를 나타내는 변수 (1세대부터 시작)
    private int currentGeneration = 1;
    
    // 요청하신 기본 저장 경로
    private final String BASE_DIR = "./src/main/java/backend/imageGen/savedimage/";

    @PostConstruct
    public void init() {
        // 서버가 켜질 때 현재 세대(1세대)의 이미지가 없으면 3개를 생성합니다.
    	imageGenService.generateImagesForGeneration(currentGeneration);
    }


    // 프론트엔드로 세대 정보와 3개의 이미지를 모두 보내주는 API
    @GetMapping("/api/image/noise")
    public Map<String, Object> getGenerationImages() {
        // ★ 파일 찾는 로직을 지우고, 서비스한테 달라고 요청만 합니다.
        return imageGenService.getGenerationImages(); 
    }
    
}