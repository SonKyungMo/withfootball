package com.demo.withfootball.modules.notice;

import com.demo.withfootball.modules.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional(readOnly = true)
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findNoticesByCommunity(Community community);
    List<Notice> findNoticeByCommunityOrderByCreatedDateTime(Community community);

}