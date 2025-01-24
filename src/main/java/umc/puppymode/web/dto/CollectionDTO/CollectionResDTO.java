package umc.puppymode.web.dto.CollectionDTO;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class CollectionResDTO {

    @Getter
    @Builder
    public static class CollectionViewDTO {
        private Long collectionId;
        private String collectionName;
        private Long puppyItemId;
        private String hangoverName;
        private Integer requiredNum;
        private Integer currentNum;
        private boolean isCompleted;
    }

    @Getter
    @Builder
    public static class CollectionListViewDTO {
        private List<CollectionViewDTO> collectionViewDTOs;
    }
}
