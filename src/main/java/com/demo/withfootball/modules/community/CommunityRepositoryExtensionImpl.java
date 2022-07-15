package com.demo.withfootball.modules.community;
import com.demo.withfootball.modules.tag.QTag;
import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.zone.QZone;
import com.demo.withfootball.modules.zone.Zone;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;
import java.util.Set;

public class CommunityRepositoryExtensionImpl extends QuerydslRepositorySupport implements CommunityRepositoryExtension {
    public CommunityRepositoryExtensionImpl() {
        super(Community.class);
    }

    @Override
    public Page<Community> findByKeyword(String keyword, Pageable pageable) {
        QCommunity community = QCommunity.community;
        JPQLQuery<Community> query = from(community).where(community.published.isTrue()
                .and(community.title.containsIgnoreCase(keyword))
                .or(community.tags.any().title.containsIgnoreCase(keyword))
                .or(community.zones.any().localNameOfCity.containsIgnoreCase(keyword)))
                .leftJoin(community.tags, QTag.tag).fetchJoin()
                .leftJoin(community.zones, QZone.zone).fetchJoin()
                .distinct();
        JPQLQuery<Community> pageableQuery = getQuerydsl().applyPagination(pageable, query);
        QueryResults<Community> fetchResults = pageableQuery.fetchResults();
        return new PageImpl<>(fetchResults.getResults(), pageable, fetchResults.getTotal());
    }

    @Override
    public List<Community> findByAccount(Set<Tag> tags, Set<Zone> zones) {
        QCommunity community = QCommunity.community;
        JPQLQuery<Community> query = from(community).where(community.published.isTrue()
                .and(community.closed.isFalse())
                .and(community.tags.any().in(tags))
                .and(community.zones.any().in(zones)))
                .leftJoin(community.tags, QTag.tag).fetchJoin()
                .leftJoin(community.zones, QZone.zone).fetchJoin()
                .orderBy(community.publishedDateTime.desc())
                .distinct()
                .limit(9);
        return query.fetch();
    }

}
