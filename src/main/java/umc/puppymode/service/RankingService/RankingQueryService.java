package umc.puppymode.service.RankingService;

import umc.puppymode.web.dto.RankingResponseDTO;

public interface RankingQueryService {
    // 친구 랭킹 조회하기
    RankingResponseDTO getFriendRankings(String accessToken, int page, int size, Long currentUserId);

    // 전체 랭킹 조회하기
    RankingResponseDTO getGlobalRankings(int page, int size, Long currentUserId);
}
