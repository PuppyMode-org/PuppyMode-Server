package umc.puppymode.web.dto.CalenderDTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class FeedDTO {
    private String feedingType;
    private String feedImageUrl;

    public FeedDTO(String feedingType, String feedImageUrl){
        this.feedingType = feedingType;
        this.feedImageUrl = feedImageUrl;
    }
}
