package com.miruni.backend.domain.plan.dto.response;

import java.util.List;
import java.util.Optional;

public record AiResponse(List<Candidate> candidates) {
    public Optional<String> getFirstCandidateText(){
        if (candidates != null && !candidates.isEmpty()){
            Candidate firstCandidate = candidates.get(0);
            if (firstCandidate.content() != null && firstCandidate.content().parts() != null && !firstCandidate.content().parts().isEmpty()){
                String rawText = firstCandidate.content().parts().get(0).text();
                return Optional.of(rawText.replace("```json", "").replace("```", "").trim());            }
        }
        return Optional.empty();
    }
    public record Candidate(Content content) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}
