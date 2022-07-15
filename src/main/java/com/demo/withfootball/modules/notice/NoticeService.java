package com.demo.withfootball.modules.notice;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.community.event.CommunityNoticeUpdateEvent;
import com.demo.withfootball.modules.notice.Form.NoticeForm;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Transactional
@Service
@RequiredArgsConstructor
public class NoticeService {

    private final ModelMapper modelMapper;
    private final ApplicationEventPublisher eventPublisher;
    private final NoticeRepository noticeRepository;

    public Notice createNotice(Notice notice, Community community, Account account) {
        notice.setCreatedDateTime(LocalDateTime.now());
        notice.setCommunity(community);
        eventPublisher.publishEvent(new CommunityNoticeUpdateEvent(notice.getCommunity(),
                "'" + notice.getTitle() + "' 공지사항을 작성했습니다."));

        return noticeRepository.save(notice);
    }

    public void updateNotice(Notice notice, NoticeForm noticeForm) {
        modelMapper.map(noticeForm, notice);
        eventPublisher.publishEvent(new CommunityNoticeUpdateEvent(notice.getCommunity(),
                "'" + notice.getTitle() + "' 공지사항을 수정했으니 확인하세요."));
    }

    public void deleteNotice(Notice notice) {
        noticeRepository.delete(notice);

    }
}
