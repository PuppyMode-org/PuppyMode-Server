package umc.puppymode.web.dto.UserCollectionDTO;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class UserCollectionResDTO {

    @Getter
    @Builder
    public static class UserCollectionViewDTO {
        private Long userCollectionId;
        private String collectionName;
        private Long puppyItemId;
        private String hangoverName;
        private Integer requiredNum;
        private Integer currentNum;
        private boolean isCompleted;
    }

    @Getter
    @Builder
    public static class UserCollectionListViewDTO {
        private List<UserCollectionViewDTO> userCollectionViewDTOs;
    }
}
