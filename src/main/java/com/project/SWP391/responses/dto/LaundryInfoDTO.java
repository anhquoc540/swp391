package com.project.SWP391.responses.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LaundryInfoDTO {
    private Long id;
    private String name;

    private String imageBanner;
    private String description;
    private List<FeedbackDTO> feedbacks;

}