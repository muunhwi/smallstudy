package com.smallstudy.domain.member_entity;

import com.smallstudy.domain.region_entity.InterestRegion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberInterestRegion {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private InterestRegion interestRegion;

    public MemberInterestRegion(Member member, InterestRegion interestRegion) {
        this.member = member;
        this.interestRegion = interestRegion;
    }
}
