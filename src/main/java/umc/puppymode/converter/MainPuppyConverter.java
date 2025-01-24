package umc.puppymode.converter;

import umc.puppymode.domain.Puppy;
import umc.puppymode.domain.PuppyLevel;
import umc.puppymode.domain.User;
import umc.puppymode.web.dto.MainPuppyDTO.MainPuppyResDTO;

public class MainPuppyConverter {

    public static MainPuppyResDTO.RandomPuppyViewDTO toRandomPuppyViewDTO(Puppy puppy) {
        return MainPuppyResDTO.RandomPuppyViewDTO.builder()
                .userId(puppy.getUser().getUserId())
                .puppyType(puppy.getPuppyLevel().getPuppyType().getType())
                .puppyImageUrl(puppy.getPuppyLevel().getLevelImageUrl())
                .build();
    }

    public static MainPuppyResDTO.UserPuppyViewDTO toUserPuppyViewDTO(Puppy puppy) {
        return MainPuppyResDTO.UserPuppyViewDTO.builder()
                .puppyId(puppy.getPuppyId())
                .puppyName(puppy.getPuppyName())
                .level(puppy.getPuppyLevel().getPuppyLevel())
                .levelName(puppy.getPuppyLevel().getLevelName())
                .imageUrl(puppy.getPuppyLevel().getLevelImageUrl())
                .levelMinExp(puppy.getPuppyLevel().getLevelMinExp())
                .levelMaxExp(puppy.getPuppyLevel().getLevelMaxExp())
                .puppyExp(puppy.getPuppyExp())
                .build();
    }

    public static MainPuppyResDTO.PlayResDTO toPlayResDTO(Puppy puppy) {
        return MainPuppyResDTO.PlayResDTO.builder()
                .level(puppy.getPuppyLevel().getPuppyLevel())
                .levelMinExp(puppy.getPuppyLevel().getLevelMinExp())
                .levelMaxExp(puppy.getPuppyLevel().getLevelMaxExp())
                .puppyExp(puppy.getPuppyExp())
                .build();
    }

    public static Puppy toPuppy(PuppyLevel puppyLevel, User user) {
        return Puppy.builder()
                .user(user)
                .puppyLevel(puppyLevel)
                .puppyExp(0)
                .build();
    }
}
