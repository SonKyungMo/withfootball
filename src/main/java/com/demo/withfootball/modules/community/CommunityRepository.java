package com.demo.withfootball.modules.community;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.zone.Zone;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface CommunityRepository extends JpaRepository<Community, Long>, CommunityRepositoryExtension {

    boolean existsByPath(String path);

    @EntityGraph(attributePaths = {"tags", "zones", "managers", "members"}, type = EntityGraph.EntityGraphType.LOAD)
    Community findByPath(String path);

    @EntityGraph(attributePaths = {"tags", "managers"})
    Community findCommunityWithTagsByPath(String path);

    @EntityGraph(attributePaths = {"zones", "managers"})
    Community findCommunityWithZonesByPath(String path);

    @EntityGraph(attributePaths = "managers")
    Community findCommunityWithManagersByPath(String path);

    @EntityGraph(attributePaths = "members")
    Community findCommunityWithMembersByPath(String path);

    Community findCommunityOnlyByPath(String path);

    @EntityGraph(attributePaths = {"zones", "tags"})
    Community findCommunityWithTagsAndZonesById(Long id);

    @EntityGraph(attributePaths = {"members", "managers"})
    Community findCommunityWithManagersAndMemebersById(Long id);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Community> findFirst9ByPublishedAndClosedOrderByPublishedDateTimeDesc(boolean published, boolean closed);

    @EntityGraph(attributePaths = {"zones", "tags"})
    List<Community> findFirst9ByPublishedAndClosedOrderByMemberCountDesc(boolean published, boolean closed);

    List<Community> findFirst5ByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Community> findFirst5ByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Community> findByManagersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);

    List<Community> findByMembersContainingAndClosedOrderByPublishedDateTimeDesc(Account account, boolean closed);
}
