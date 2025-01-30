package umc.puppymode.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import umc.puppymode.domain.enums.AnimationType;

import java.util.List;

@Getter
@AllArgsConstructor
public class AnimationFramesResponseDTO{
    private AnimationType animationType;
    private int frameCount;
    private List<String> imageUrls;
}