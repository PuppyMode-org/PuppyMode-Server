package umc.puppymode.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import umc.puppymode.domain.Puppy;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RankingDTO {
    private int rank;
    private String username;
    private String puppyName;
    private int level;
    private String levelName;
    private String imageUrl;

    @JsonIgnore
    private Long userId;

    public static RankingDTO from(Puppy puppy, int rank) {
        return new RankingDTO(
                rank,
                puppy.getUser().getUsername(),
                puppy.getPuppyName(),
                puppy.getPuppyLevel().getPuppyLevel(),
                puppy.getPuppyLevel().getLevelName(),
                puppy.getImageUrl(),
                puppy.getUser().getUserId()
        );
    }
}
