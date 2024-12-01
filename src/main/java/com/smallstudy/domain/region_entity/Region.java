package com.smallstudy.domain.region_entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;

@Embeddable
@Getter
public class Region {

    private String city;
    private String gu;
    private String dong;

    public Region() {
    }

    public Region(String city, String gu, String dong) {
        this.city = city;
        this.gu = gu;
        this.dong = dong;
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", city, gu, dong);
    }
}
