package com.smallstudy.domain.region_entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class InterestRegion {

    @Id @GeneratedValue
    private Long id;

    @Embedded
    @Column(nullable = false)
    private Region region;

    public InterestRegion(Region region) {
        this.region = region;
    }


}
