package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankingResponseDTO {
    private RankingDTO currentUserRank;
    private List<RankingDTO> rankings;
    private int totalCount;
}
