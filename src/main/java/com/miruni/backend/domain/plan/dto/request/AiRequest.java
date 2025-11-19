package com.miruni.backend.domain.plan.dto.request;

import java.util.List;

public record AiRequest(List<Content> contents) {
    public static AiRequest fromPrompt(String prompt) {
        Part part = new Part(prompt);
        Content content = new Content(List.of(part));
        return new AiRequest(List.of(content));
    }

    public record Content(List<Part> parts) {}
    public record Part(String text) {}
}
