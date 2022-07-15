package com.demo.withfootball.modules.community;

import com.demo.withfootball.modules.tag.Tag;
import com.demo.withfootball.modules.zone.Zone;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
public interface CommunityRepositoryExtension {

    Page<Community> findByKeyword(String keyword, Pageable pageable);

    List<Community> findByAccount(Set<Tag> tags, Set<Zone> zones);

}
