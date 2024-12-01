package com.smallstudy.service;

import com.smallstudy.domain.region_entity.InterestRegion;
import com.smallstudy.domain.member_entity.Member;
import com.smallstudy.domain.member_entity.MemberInterestRegion;

import com.smallstudy.dto.profile_dto.RegionDTO;
import com.smallstudy.repo.InterestRegionRepository;
import com.smallstudy.repo.member_repo.MemberInterestRegionRepository;
import com.smallstudy.repo.member_repo.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InterestRegionService {

    private final MemberRepository memberRepository;
    private final InterestRegionRepository interestRegionRepository;
    private final MemberInterestRegionRepository memberInterestRegionRepository;

    public static List<RegionDTO> filteredRegionDTOs(List<RegionDTO> regions, List<RegionDTO> sub) {
        return regions.stream()
                .filter(region -> sub.stream()
                        .noneMatch(selected -> selected.getCode().equals(region.getCode())))
                .sorted(Comparator.comparingLong(RegionDTO::getCode))
                .toList();
    }


    public static RegionDTO toRegionDTO(InterestRegion region) {
        return new RegionDTO(region.getId(), region.getRegion());
    }


    public List<RegionDTO> getRegionDTOs() {
        return  interestRegionRepository
                .findAll()
                .stream()
                .map(InterestRegionService::toRegionDTO)
                .toList();
    }

    public List<RegionDTO> getMemberSelectedRegions(Long memberId) {
        return memberInterestRegionRepository.findInterestRegionsByMemberId(memberId);
    }

    @Transactional
    public List<RegionDTO> saveInterestRegion(Long memberId, Long regionCode) {
        List<MemberInterestRegion> memberInterestRegions = memberInterestRegionRepository.findInterestRegionsWithMemberByMemberId(memberId);

        for (MemberInterestRegion memberInterestRegion : memberInterestRegions) {
            if (regionCode.equals(memberInterestRegion.getInterestRegion().getId())) {
                return memberInterestRegions.stream()
                        .map(mir -> toRegionDTO(mir.getInterestRegion()))
                        .sorted(Comparator.comparing(RegionDTO::getCode))
                        .toList();
            }
        }

        Member member = memberInterestRegions.isEmpty()
                ? memberRepository.findById(memberId).orElseThrow(() -> new EntityNotFoundException("Member not found"))
                : memberInterestRegions.get(0).getMember();

        InterestRegion region = interestRegionRepository.findById(regionCode)
                .orElseThrow(() -> new EntityNotFoundException("Interest region not found"));

        MemberInterestRegion newMemberInterestRegion = new MemberInterestRegion(member, region);
        memberInterestRegionRepository.save(newMemberInterestRegion);

        return List.of();
    }

    @Transactional
    public void deleteInterestRegion(Long memberId, Long regionCode) {
        memberInterestRegionRepository.deleteByMemberIdAndRegionId(memberId, regionCode);
    }



}
