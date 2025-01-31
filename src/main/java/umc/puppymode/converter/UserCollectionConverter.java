package umc.puppymode.converter;

import umc.puppymode.domain.mapping.UserCollection;
import umc.puppymode.web.dto.UserCollectionDTO.UserCollectionResDTO;

import java.util.List;

public class UserCollectionConverter {

    public static UserCollectionResDTO.UserCollectionViewDTO toUserCollectionViewDTO(UserCollection userCollection) {
        return UserCollectionResDTO.UserCollectionViewDTO.builder()
                .userCollectionId(userCollection.getUserCollectionId())
                .collectionName(userCollection.getCollection().getCollectionName())
                .puppyItemId(userCollection.getCollection().getPuppyItem().getItemId())
                .hangoverName(userCollection.getCollection().getHangoverItem().getHangoverName())
                .requiredNum(userCollection.getCollection().getRequiredNum())
                .currentNum(userCollection.getCurrentNum())
                .isCompleted(userCollection.isCompleted())
                .build();
    }

    public static UserCollectionResDTO.UserCollectionListViewDTO toUserCollectionListViewDTO(List<UserCollection> userCollections) {

        List<UserCollectionResDTO.UserCollectionViewDTO> userCollectionViewDTOs = userCollections.stream()
                .map(UserCollectionConverter::toUserCollectionViewDTO)
                .toList();

        return UserCollectionResDTO.UserCollectionListViewDTO.builder()
                .userCollectionViewDTOs(userCollectionViewDTOs)
                .build();
    }
}
