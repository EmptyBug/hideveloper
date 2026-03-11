package click.backend.imageGen.inter;

import java.util.Map;

public interface VoteService {
	
	/**
	 * 투표시 사용
	 * @param generation
	 * @param selectedIndex
	 */
	public void recordVote(int generation, int selectedIndex);
	
	/**
	 * 투표 통계용
	 * @param generation
	 * @return
	 */
	public Map<Integer, Integer> getVoteStatus(int generation);
	
}
