package com.base.domain.apartment.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class ApartmentMedia {

    private String id;
    private String apartmentId;
    private String mediaType;
    private String url;
    private String thumbnailUrl;
    private Boolean primary;
    private Integer displayOrder;
    private LocalDateTime createdAt;
}
