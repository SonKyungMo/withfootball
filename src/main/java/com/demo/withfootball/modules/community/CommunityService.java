package com.demo.withfootball.modules.community;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.community.event.CommunityCreatedEvent;
import com.demo.withfootball.modules.community.event.CommunityUpdateEvent;
import com.demo.withfootball.modules.community.form.CommunityDescriptionForm;
import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.tag.TagRepository;
import com.demo.withfootball.modules.zone.Zone;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;

@Service
@Transactional
@RequiredArgsConstructor
public class CommunityService {

    private static final String VALID_PATH_PATTERN = "^[ㄱ-ㅎ가-힣a-z0-9_-]{2,20}$";
    private final CommunityRepository repository;
    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Community createNewCommunity(Community community, Account account) {
        Community newCommunity = repository.save(community);
        newCommunity.addManager(account);
        return newCommunity;
    }

    public Community getCommunity(String path) {
        Community community = this.repository.findByPath(path);
        checkIfExistingCommunity(path, community);
        return community;
    }

    public Community getCommunityToUpdate(Account account, String path) {
        Community community = this.getCommunity(path);
        checkIfManager(account, community);
        return community;
    }


    public void updateCommunityDescription(Community community, CommunityDescriptionForm communityDescriptionForm) {
        modelMapper.map(communityDescriptionForm, community);
        eventPublisher.publishEvent(new CommunityUpdateEvent(community, "커뮤니티 소개를 수정했습니다."));

    }

    private void checkIfExistingCommunity(String path, Community community) {
        if (community == null) {
            throw new IllegalArgumentException(path + "에 해당하는 커뮤니티가 없습니다.");
        }
    }

    private void checkIfManager(Account account, Community community) {
        if (!community.isManagedBy(account)) {
            throw new AccessDeniedException("해당 기능을 사용할 수 없습니다.");
        }
    }

    public void updateCommunityImage(Community community, String image) {
        community.setImage(image);
    }

    public void enableCommunityBanner(Community community) {
        community.setUseBanner(true);
    }

    public void disableCommunityBanner(Community community) {
        community.setUseBanner(false);
    }

    public void addTag(Community community, Tag tag) {
        community.getTags().add(tag);
    }

    public void removeTag(Community community, Tag tag) {
        community.getTags().remove(tag);
    }

    public void addZone(Community community, Zone zone) {
        community.getZones().add(zone);
    }

    public void removeZone(Community community, Zone zone) {
        community.getZones().remove(zone);
    }

    public Community getCommunityToUpdateTag(Account account, String path) {
        Community community = repository.findCommunityWithTagsByPath(path);
        checkIfExistingCommunity(path, community);
        checkIfManager(account, community);
        return community;
    }

    public Community getCommunityToUpdateZone(Account account, String path) {
        Community community = repository.findCommunityWithZonesByPath(path);
        checkIfExistingCommunity(path, community);
        checkIfManager(account, community);
        return community;
    }

    public void publish(Community community) {
        community.publish();
        this.eventPublisher.publishEvent(new CommunityCreatedEvent(community));
    }

    public void close(Community community) {
        community.close();
        eventPublisher.publishEvent(new CommunityUpdateEvent(community, "스터디를 종료했습니다."));
    }

    public Community getCommunityToUpdateStatus(Account account, String path) {
        Community community = repository.findCommunityWithManagersByPath(path); //수정
        checkIfExistingCommunity(path, community);
        checkIfManager(account, community);
        return community;
    }

    public void startRecruit(Community community) {
        community.startRecruit();
        eventPublisher.publishEvent(new CommunityUpdateEvent(community, "팀원 모집을 시작합니다."));
    }

    public void stopRecruit(Community community) {
        community.stopRecruit();
        eventPublisher.publishEvent(new CommunityUpdateEvent(community, "팀원 모집을 중단했습니다."));
    }

    public boolean isValidPath(String newPath) {
        if (!newPath.matches(VALID_PATH_PATTERN)) {
            return false;
        }

        return !repository.existsByPath(newPath);
    }

    public void updateCommunityPath(Community community, String newPath) {
        community.setPath(newPath);
    }

    public boolean isValidTitle(String newTitle) {
        return newTitle.length() <= 50;
    }

    public void updateCommunityTitle(Community community, String newTitle) {
        community.setTitle(newTitle);
    }

    public void remove(Community community) {
        if (community.isRemovable()) {
            repository.delete(community);
        } else {
            throw new IllegalArgumentException("커뮤니티를 삭제할 수 없습니다.");
        }
    }

    public void addMember(Community community, Account account) {
        community.addMember(account);
    }

    public void removeMember(Community community, Account account) {
        community.removeMember(account);
    }

    public Community getCommunityToEnroll(String path) {
        Community community = repository.findCommunityOnlyByPath(path);
        checkIfExistingCommunity(path, community);
        return community;
    }

}
