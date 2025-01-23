package umc.puppymode.converter;

import umc.puppymode.domain.Puppy;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

public class MainPuppyConverter {

    public static MainPuppyResDTO.RandomPuppyViewDTO toRandomPuppyViewDTO(String type, String imageUrl) {
        return MainPuppyResDTO.RandomPuppyViewDTO.builder()
                .puppyType(type)
                .puppyImageUrl(imageUrl)
                .build();
    }

    public static MainPuppyResDTO.UserPuppyViewDTO toUserPuppyViewDTO(Puppy puppy) {
        return MainPuppyResDTO.UserPuppyViewDTO.builder()
                .puppyId(puppy.getPuppyId())
                .puppyName(puppy.getPuppyName())
                .level(puppy.getPuppyLevel().getPuppyLevel())
                .levelName(puppy.getPuppyLevel().getLevelName())
                .imageUrl(puppy.getPuppyImageUrl())
                .levelMinExp(puppy.getPuppyLevel().getLevelMinExp())
                .levelMaxExp(puppy.getPuppyLevel().getLevelMaxEXP())
                .puppyExp(puppy.getPuppyExp())
                .build();
    }

    public static MainPuppyResDTO.PlayResDTO toPlayResDTO(Puppy puppy) {
        return MainPuppyResDTO.PlayResDTO.builder()
                .level(puppy.getPuppyLevel().getPuppyLevel())
                .levelMinExp(puppy.getPuppyLevel().getLevelMinExp())
                .levelMaxExp(puppy.getPuppyLevel().getLevelMaxEXP())
                .puppyExp(puppy.getPuppyExp())
                .build();
    }
}
