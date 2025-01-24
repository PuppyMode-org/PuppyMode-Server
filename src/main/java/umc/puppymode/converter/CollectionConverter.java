package umc.puppymode.converter;

import umc.puppymode.domain.Collection;
import umc.puppymode.web.dto.CollectionDTO.CollectionResDTO;

import java.util.List;

public class CollectionConverter {

    public static CollectionResDTO.CollectionViewDTO toCollectionViewDTO(Collection collection) {
        return CollectionResDTO.CollectionViewDTO.builder()
                .collectionId(collection.getCollectionId())
                .collectionName(collection.getCollectionName())
                .puppyItemId(collection.getPuppyItem().getItemId())
                .hangoverName(collection.getHangoverItem().getHangoverName())
                .requiredNum(collection.getRequiredNum())
                .currentNum(collection.getCurrentNum())
                .isCompleted(collection.isCompleted())
                .build();
    }

    public static CollectionResDTO.CollectionListViewDTO toCollectionListViewDTO(List<Collection> collections) {

        List<CollectionResDTO.CollectionViewDTO> collectionViewDTOs = collections.stream()
                .map(CollectionConverter::toCollectionViewDTO)
                .toList();

        return CollectionResDTO.CollectionListViewDTO.builder()
                .collectionViewDTOs(collectionViewDTOs)
                .build();
    }
}
