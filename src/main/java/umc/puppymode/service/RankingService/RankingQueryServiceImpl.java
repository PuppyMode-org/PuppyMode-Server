package umc.puppymode.service.RankingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.User;
import umc.puppymode.domain.enums.AuthProvider;
import umc.puppymode.repository.PuppyRepository;
import umc.puppymode.repository.UserAuthRepository;
import umc.puppymode.web.dto.RankingDTO;
import umc.puppymode.web.dto.RankingResponseDTO;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RankingQueryServiceImpl implements RankingQueryService {

    private final UserAuthRepository userAuthProviderRepository;
    private final PuppyRepository puppyRepository;

    /**
     * 친구 목록(authIds)중, 가입된 회원들의 강아지 정보를 가져와 랭킹을 조회합니다.
     */
    @Override
    public RankingResponseDTO getFriendRankings(List<String> authIds, int page, int size, Long currentUserId) {
        String currentUserAuthId = userAuthProviderRepository.findAuthIdByUserId(currentUserId).orElse(null);
        authIds.add(currentUserAuthId);
        List<User> kakaoUsers = userAuthProviderRepository.findUsersByAuthProviderAndAuthIdIn(AuthProvider.KAKAO, authIds);
        List<Puppy> puppies = puppyRepository.findByUserIn(kakaoUsers);

        List<RankingDTO> rankings = calculateRankings(puppies);
        List<RankingDTO> pagedRankings = applyPagination(rankings, page, size);
        RankingDTO currentUserRank = getCurrentUserRanking(rankings, currentUserId);

        return RankingResponseDTO.builder()
                .currentUserRank(currentUserRank)
                .rankings(pagedRankings)
                .totalCount(rankings.size())
                .build();
    }

    /**
     * 전체 회원의 강아지 정보를 가져와 랭킹을 조회합니다.
     */
    @Override
    public RankingResponseDTO getGlobalRankings(int page, int size, Long currentUserId) {
        List<Puppy> puppies = puppyRepository.findAll();

        List<RankingDTO> rankings = calculateRankings(puppies);
        List<RankingDTO> pagedRankings = applyPagination(rankings, page, size);
        RankingDTO currentUserRank = getCurrentUserRanking(rankings, currentUserId);

        return RankingResponseDTO.builder()
                .currentUserRank(currentUserRank)
                .rankings(pagedRankings)
                .totalCount(rankings.size())
                .build();
    }

    /**
     * puppy list를 puppyExp 기준으로 정렬하고, 랭킹을 계산합니다.
     * 동일 경험치인 경우 공동 순위 처리합니다.
     *
     * @param puppies 랭킹 계산할 강아지 리스트
     * @return 경험치 기준으로 정렬된 강아지 랭킹 리스트
     */
    private List<RankingDTO> calculateRankings(List<Puppy> puppies) {
        AtomicInteger rankCounter = new AtomicInteger(1);
        AtomicReference<Integer> previousExp = new AtomicReference<>(null);
        AtomicReference<Integer> previousRank = new AtomicReference<>(0);

        return puppies.stream()
                .sorted(Comparator.comparingInt(Puppy::getPuppyExp).reversed())
                .map(puppy -> {
                    if (previousExp.get() != null && puppy.getPuppyExp().equals(previousExp.get())) {
                        return RankingDTO.from(puppy, previousRank.get());
                    } else {
                        previousExp.set(puppy.getPuppyExp());
                        previousRank.set(rankCounter.getAndIncrement());
                        return RankingDTO.from(puppy, previousRank.get());
                    }
                })
                .collect(Collectors.toList());
    }

    /**
     * 현재 사용자의 랭킹을 검색합니다.
     */
    private RankingDTO getCurrentUserRanking(List<RankingDTO> rankings, Long currentUserId) {
        return rankings.stream()
                .filter(r -> r.getUserId().equals(currentUserId))
                .findFirst()
                .orElse(null);
    }

    /**
     * 페이지네이션을 적용합니다.
     */
    private List<RankingDTO> applyPagination(List<RankingDTO> rankings, int page, int size) {
        int fromIndex = Math.min(page * size, rankings.size());
        int toIndex = Math.min(fromIndex + size, rankings.size());
        return rankings.subList(fromIndex, toIndex);
    }
}