package click.backend.imageGen.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import click.backend.imageGen.dto.VoteRequest;
import click.backend.imageGen.inter.VoteService;

@RestController
@RequestMapping("/api/vote")
@CrossOrigin(origins = "http://localhost:5173") // 리액트에서의 접근 허용
public class VoteController {

    private final VoteService voteService;

    // 스프링이 VoteService를 자동으로 주입(의존성 주입)해 줍니다.
    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    // 리액트에서 POST 요청으로 투표 데이터를 보내면 여기서 받습니다.
    @PostMapping
    public ResponseEntity<String> submitVote(@RequestBody VoteRequest voteRequest) {
        try {
            // Service에게 투표 기록을 맡깁니다.
            voteService.recordVote(voteRequest.getGeneration(), voteRequest.getSelectedIndex());
            return ResponseEntity.ok("투표가 성공적으로 저장되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("투표 처리 중 오류가 발생했습니다.");
        }
    }
}
