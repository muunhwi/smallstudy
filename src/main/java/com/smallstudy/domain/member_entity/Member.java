package com.smallstudy.domain.member_entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Setter
    private LocalDateTime emailTokenReceivedAt;
    @Setter
    private String emailToken;

    private String imgPath;
    private String imgName;
    private String imgUuid;

    private String message;

    private boolean isEmailValid = false;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    List<MemberInterestRegion> memberInterestRegions = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "member", cascade = CascadeType.ALL)
    List<MemberCategoryItem> memberCategoryItems = new ArrayList<>();


    public void setEmailValid() { isEmailValid = true; }

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Member(String email, String nickname, String password) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
    }

    public Member(String email, String nickname, String password, String token) {
        this.email = email;
        this.nickname = nickname;
        this.password = password;
        this.emailToken = token;
    }

    public void copy(Member member) {
        this.id = member.id;
        this.email = member.email;
        this.nickname = member.nickname;
        this.password = member.password;
        this.emailToken = member.emailToken;
        this.isEmailValid = member.isEmailValid;
        this.imgName = member.imgName;
        this.imgPath = member.imgPath;
        this.imgUuid = member.imgUuid;
        this.message = member.message;
    }

    public void copyWithoutId(Member member) {
        this.email = member.email;
        this.nickname = member.nickname;
        this.password = member.password;
        this.emailToken = member.emailToken;
        this.isEmailValid = member.isEmailValid;
        this.imgName = member.imgName;
        this.imgPath = member.imgPath;
        this.imgUuid = member.imgUuid;
        this.message = member.message;
    }

    public void profileUpdate(String nickname, String message) {
        this.message = message;
        this.nickname = nickname;
    }

    public void profileUpdateImage(String originName, String uuid, String path) {
        this.imgName = originName;
        this.imgUuid = uuid;
        this.imgPath = path;
    }

    public void addMemberCategoryItem(MemberCategoryItem memberCategoryItem) {
        this.memberCategoryItems.add(memberCategoryItem);
        memberCategoryItem.setMember(this);
    }

    public void addMemberRegion(MemberInterestRegion memberInterestRegion) {
        this.memberInterestRegions.add(memberInterestRegion);
        memberInterestRegion.setMember(this);
    }

}

