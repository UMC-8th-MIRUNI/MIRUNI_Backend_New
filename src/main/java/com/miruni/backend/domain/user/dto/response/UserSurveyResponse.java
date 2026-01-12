package com.miruni.backend.domain.user.dto.response;

import com.miruni.backend.domain.user.entity.DelayLevel;
import com.miruni.backend.domain.user.entity.DelayReason;
import com.miruni.backend.domain.user.entity.DelaySituation;
import com.miruni.backend.domain.user.entity.Survey;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 설문조사 결과 응답")
public record UserSurveyResponse(

        @Schema(description = "미루는 상황들")
        List<String> situationDescriptions,

        @Schema(description = "미루는 정도 설명")
        String levelDescription,

        @Schema(description = "미루는 이유들")
        List<String> reasonDescriptions

) {

    public static UserSurveyResponse fromSurvey(Survey survey) {
        if (survey == null) {
            return new UserSurveyResponse(List.of(), null, List.of());
        }

        List<String> situations = DelaySituation.fromMask(survey.getDelaySituationMask()).stream()
                .map(DelaySituation::getDescription)
                .toList();

        List<String> reasons = DelayReason.fromMask(survey.getDelayReasonMask()).stream()
                .map(DelayReason::getDescription)
                .toList();

        DelayLevel level = survey.getDelayLevel();
        String levelDescription = (level != null) ? level.getDescription() : null;

        return new UserSurveyResponse(situations, levelDescription, reasons);
    }
}


