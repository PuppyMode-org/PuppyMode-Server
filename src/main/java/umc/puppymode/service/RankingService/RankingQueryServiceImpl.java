package umc.puppymode.service.RankingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserAuthRepository;
import umc.puppymode.web.dto.RankingDTO;
import umc.puppymode.web.dto.RankingResponseDTO;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingQueryServiceImpl implements RankingQueryService {

    private final UserAuthRepository userAuthProviderRepository;
    private final PuppyRepository puppyRepository;

    /**
     * 친구 랭킹 조회
     */
    @Override
    public RankingResponseDTO getFriendRankings(List<String> authIds, int page, int size, Long currentUserId) {
        // authIds를 기반으로 해당 유저 목록 가져오기
        List<User> kakaoUsers = userAuthProviderRepository.findUsersByAuthProviderAndAuthIdIn("KAKAO", authIds);

        // 해당 유저들의 강아지 정보 조회
        List<Puppy> puppies = puppyRepository.findByUserIn(kakaoUsers);

        // 경험치 기준으로 랭킹 정렬 + 랭킹 부여
        AtomicInteger rankCounter = new AtomicInteger(1);
        List<RankingDTO> rankings = puppies.stream()
                .sorted(Comparator.comparingInt(Puppy::getPuppyExp).reversed()) // 경험치 기준 정렬
                .map(puppy -> RankingDTO.from(puppy, rankCounter.getAndIncrement())) // 랭킹
                .collect(Collectors.toList());

        // 페이징 적용
        List<RankingDTO> pagedRankings = applyPagination(rankings, page, size);

        // 현재 유저의 랭킹 정보 조회
        RankingDTO currentUserRank = getCurrentUserRanking(rankings, currentUserId);

        // 최종 응답 객체 생성
        return RankingResponseDTO.builder()
                .currentUserRank(currentUserRank)
                .rankings(pagedRankings)
                .totalCount(rankings.size())
                .build();
    }

    /**
     * 전체 랭킹 조회
     */
    @Override
    public RankingResponseDTO getGlobalRankings(int page, int size, Long currentUserId) {
        // 모든 유저의 강아지 정보 조회
        List<Puppy> puppies = puppyRepository.findAll();

        // 경험치 기준으로 랭킹 정렬 후 DTO 변환
        AtomicInteger rankCounter = new AtomicInteger(1);
        List<RankingDTO> rankings = puppies.stream()
                .sorted(Comparator.comparingInt(Puppy::getPuppyExp).reversed())
                .map(puppy -> RankingDTO.from(puppy, rankCounter.getAndIncrement()))
                .collect(Collectors.toList());

        // 페이징 적용
        List<RankingDTO> pagedRankings = applyPagination(rankings, page, size);

        // 현재 유저의 랭킹 정보 조회
        RankingDTO currentUserRank = getCurrentUserRanking(rankings, currentUserId);

        // 최종 응답 객체 생성
        return RankingResponseDTO.builder()
                .currentUserRank(currentUserRank)
                .rankings(pagedRankings)
                .totalCount(rankings.size())
                .build();
    }

    /**
     * 현재 유저 랭킹 검색
     */
    private RankingDTO getCurrentUserRanking(List<RankingDTO> rankings, Long currentUserId) {
        return rankings.stream()
                .filter(r -> r.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 페이징 적용
     */
    private List<RankingDTO> applyPagination(List<RankingDTO> rankings, int page, int size) {
        int fromIndex = Math.min(page * size, rankings.size());
        int toIndex = Math.min(fromIndex + size, rankings.size());
        return rankings.subList(fromIndex, toIndex);
    }
}