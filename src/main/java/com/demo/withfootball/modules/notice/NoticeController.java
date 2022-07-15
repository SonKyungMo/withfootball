package com.demo.withfootball.modules.notice;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.CurrentAccount;
import com.demo.withfootball.modules.community.Community;
import com.demo.withfootball.modules.community.CommunityService;
import com.demo.withfootball.modules.event.Event;
import com.demo.withfootball.modules.event.form.EventForm;
import com.demo.withfootball.modules.notice.Form.NoticeForm;
import com.demo.withfootball.modules.notice.validator.NoticeValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community/{path}")

public class NoticeController {

    private final CommunityService communityService;
    private final NoticeRepository noticeRepository;
    private final NoticeService noticeService;
    private final ModelMapper modelMapper;
    private final NoticeValidator noticeValidator;

    @InitBinder("NoticeForm")
    public void NoticeFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(noticeValidator);
    }

    @GetMapping("/notice")
    public String viewCommunityNotice(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunity(path);
        model.addAttribute(account);
        model.addAttribute(community);

        List<Notice> notices = noticeRepository.findNoticeByCommunityOrderByCreatedDateTime(community);
        model.addAttribute("notices", notices);

        return "community/notice";
    }

    @GetMapping("/notice/{id}")
    public String getNotice(@CurrentAccount Account account, @PathVariable String path, @PathVariable Long id,
                            Model model) {
        model.addAttribute(account);
        model.addAttribute(noticeRepository.findById(id).orElseThrow());
        model.addAttribute(communityService.getCommunity(path));
        return "community/notice-view";
    }

    @GetMapping("/new-notice")
    public String newNoticeForm(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        model.addAttribute(account);
        model.addAttribute(community);
        model.addAttribute(new NoticeForm());
        return "community/new-notice";
    }

    @PostMapping("/new-notice")
    public String newEventSubmit(@CurrentAccount Account account, @PathVariable String path,
                                 @Valid NoticeForm noticeForm, Errors errors, Model model) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(community);

            return "community/new-notice";
        }
        Notice notice = noticeService.createNotice(modelMapper.map(noticeForm, Notice.class), community, account);
        return "redirect:/community/" + community.getEncodedPath() + "/notice/" + notice.getId();
    }

    @GetMapping("/notice/{id}/edit")
    public String updateNoticeForm(@CurrentAccount Account account,
                                   @PathVariable String path, @PathVariable("id") Notice notice, Model model) {
        Community community = communityService.getCommunityToUpdate(account, path);
        model.addAttribute(community);
        model.addAttribute(account);
        model.addAttribute(notice);
        model.addAttribute(modelMapper.map(notice, NoticeForm.class));
        return "community/notice-update";
    }

    @PostMapping("/notice/{id}/edit")
    public String updateNoticeSubmit(@CurrentAccount Account account, @PathVariable String path,
                                     @PathVariable("id") Notice notice, @Valid NoticeForm noticeForm, Errors errors,
                                     Model model) {
        Community community = communityService.getCommunityToUpdate(account, path);
        if (errors.hasErrors()) {
            model.addAttribute(account);
            model.addAttribute(community);
            model.addAttribute(notice);

            return "community/notice-update";
        }

        noticeService.updateNotice(notice, noticeForm);
        return "redirect:/community/" + community.getEncodedPath() + "/notice/" + notice.getId();
    }

    @DeleteMapping("/notice/{id}")
    public String cancelEvent(@CurrentAccount Account account, @PathVariable String path, @PathVariable("id") Notice notice) {
        Community community = communityService.getCommunityToUpdateStatus(account, path);
        noticeService.deleteNotice(notice);
        return "redirect:/community/" + community.getEncodedPath() + "/notice";
    }
}
