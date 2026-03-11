package click.backend.imageGen.dto;

import lombok.Data;

@Data
public class VoteRequest {
    private int generation;
    private int selectedIndex; // 1, 2, 3 중 하나

}