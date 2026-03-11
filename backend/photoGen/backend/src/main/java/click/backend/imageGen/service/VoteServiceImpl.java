package click.backend.imageGen.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import click.backend.imageGen.inter.ImageGenService;
import click.backend.imageGen.inter.VoteService;

@Service
public class VoteServiceImpl implements VoteService{
	
	private final ImageGenService imageGenService;
	// 진화 조건 설정
    private final int TARGET_VOTES = 1; // 목표 투표수
    private final int TIME_LIMIT_SECONDS = 0; // 제한 시간 (테스트를 위해 60초로 설정. 나중에 늘리세요!)
    
    // 현재 세대가 시작된 시간 기록
    private LocalDateTime generationStartTime;

    public VoteServiceImpl(ImageGenService imageGenService) {
        this.imageGenService = imageGenService;
        this.generationStartTime = LocalDateTime.now(); // 서비스 켜질 때 시간 기록
    }
    // 데이터베이스 대신 투표수를 저장할 인메모리 저장소
    // 구조: 세대(Generation) -> (선택된 이미지 번호(Index) -> 투표수(Count))
    // 다수의 유저가 동시에 투표해도 문제없도록 ConcurrentHashMap을 사용합니다.
    private final Map<Integer, Map<Integer, Integer>> voteStorage = new ConcurrentHashMap<>();

    // 투표를 기록하는 메서드
    @Override
 // 1. 투표 접수 메서드
    public synchronized void recordVote(int generation, int selectedIndex) {
        int currentGen = imageGenService.getCurrentGeneration();
        
        // 지난 세대에 대한 뒤늦은 투표는 무시합니다.
        if (generation != currentGen) return;

        voteStorage.putIfAbsent(currentGen, new ConcurrentHashMap<>());
        Map<Integer, Integer> votes = voteStorage.get(currentGen);
        
        votes.put(selectedIndex, votes.getOrDefault(selectedIndex, 0) + 1);
        int totalVotes = votes.values().stream().mapToInt(Integer::intValue).sum();

        System.out.println("🗳️ [투표 접수] " + currentGen + "세대 누적 표: " + totalVotes + " / " + TARGET_VOTES);

        // 조건 1: 목표 투표수에 도달하면 진화 트리거 발동!
        if (totalVotes >= TARGET_VOTES) {
            triggerEvolution(currentGen, votes, "투표수 달성");
        }
    }
 // 2. 타이머 체크 메서드 (매 10초마다 자동으로 실행됨)
    @Scheduled(fixedRate = 100) 
    public synchronized void checkTimeLimit() {
        int currentGen = imageGenService.getCurrentGeneration();
        long secondsElapsed = Duration.between(generationStartTime, LocalDateTime.now()).getSeconds();

        // 조건 2: 제한 시간이 지났다면 진화 트리거 발동!
        if (secondsElapsed >= TIME_LIMIT_SECONDS) {
            Map<Integer, Integer> votes = voteStorage.getOrDefault(currentGen, new ConcurrentHashMap<>());
            triggerEvolution(currentGen, votes, "시간 초과 (" + secondsElapsed + "초)");
        }
    }
 // 3. 진화를 지시하는 공통 메서드
    private void triggerEvolution(int generation, Map<Integer, Integer> votes, String reason) {
        System.out.println("🚀 [진화 발동] 사유: " + reason);

        int winnerIndex = 1; // 기본값

        // 표가 1표라도 있다면 1등을 찾고, 아무도 투표를 안했다면 무작위로 1등을 선정합니다.
        if (!votes.isEmpty()) {
            winnerIndex = Collections.max(votes.entrySet(), Map.Entry.comparingByValue()).getKey();
        } else {
            winnerIndex = new Random().nextInt(3) + 1;
            System.out.println("아무도 투표하지 않아 무작위로 " + winnerIndex + "번이 1등으로 선정되었습니다.");
        }

        // ★ 역할 분담: 내가 그리지 않고 이미지 공장에게 1등 번호를 주며 그려달라고 요청!
        imageGenService.evolveToNextGeneration(winnerIndex);
        
        // 다음 세대를 위해 시작 시간을 초기화합니다.
        generationStartTime = LocalDateTime.now();
    }

    // 특정 세대의 전체 투표 현황을 조회하는 메서드 (추후 통계용)
    @Override
    public Map<Integer, Integer> getVoteStatus(int generation) {
        return voteStorage.getOrDefault(generation, new ConcurrentHashMap<>());
    }
}