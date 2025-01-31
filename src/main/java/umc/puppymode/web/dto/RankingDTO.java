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
    private String levelName;
    private int level;
    private int progressPercentage;

    @JsonIgnore
    private Long userId;

    public static RankingDTO from(Puppy puppy, int rank) {
        return new RankingDTO(
                rank,
                puppy.getUser().getUsername(),
                puppy.getPuppyLevel().getLevelName(),
                puppy.getPuppyLevel().getPuppyLevel(),
                calculateProgress(puppy),
                puppy.getUser().getUserId()
        );
    }

    private static int calculateProgress(Puppy puppy) {
        int minExp = puppy.getPuppyLevel().getLevelMinExp();
        int maxExp = puppy.getPuppyLevel().getLevelMaxExp();
        int currentExp = puppy.getPuppyExp();

        return (int) (((double) (currentExp - minExp) / (maxExp - minExp)) * 100);
    }
}
