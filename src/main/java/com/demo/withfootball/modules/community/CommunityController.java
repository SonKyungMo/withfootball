package com.demo.withfootball.modules.community;

import com.demo.withfootball.modules.account.Account;
import com.demo.withfootball.modules.account.CurrentAccount;
import com.demo.withfootball.modules.community.form.CommunityForm;
import com.demo.withfootball.modules.community.validator.CommunityFormValidator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;
    private final ModelMapper modelMapper;
    private final CommunityFormValidator communityFormValidator;
    private final CommunityRepository communityRepository;

    @InitBinder("CommunityForm")
    public void CommunityFormInitBinder(WebDataBinder webDataBinder) {
        webDataBinder.addValidators(communityFormValidator);
    }

    @GetMapping("/new-community")
    public String newCommunityForm(@CurrentAccount Account account, Model model) {
        model.addAttribute(account);
        model.addAttribute(new CommunityForm());
        return "community/form";
    }

    @PostMapping("/new-community")
    public String newCommunitySubmit(@CurrentAccount Account account, @Valid CommunityForm communityForm, Errors errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute(account);
            return "community/form";
        }

        Community newCommunity = communityService.createNewCommunity(modelMapper.map(communityForm, Community.class), account);
        return "redirect:/community/" + URLEncoder.encode(newCommunity.getPath(), StandardCharsets.UTF_8);
    }

    @GetMapping("/community/{path}")
    public String viewCommunity(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunity(path);
        model.addAttribute(account);
        model.addAttribute(community);
        return "community/view";
    }

    @GetMapping("/community/{path}/members")
    public String viewCommunityMembers(@CurrentAccount Account account, @PathVariable String path, Model model) {
        Community community = communityService.getCommunity(path);
        model.addAttribute(account);
        model.addAttribute(community);

        return "community/members";
    }

    @GetMapping("/community/{path}/join")
    public String joinCommunity(@CurrentAccount Account account, @PathVariable String path) {
        Community community = communityRepository.findCommunityWithMembersByPath(path); //findCommunityWithMembersByPath(path);
        communityService.addMember(community, account);
        return "redirect:/community/" + community.getEncodedPath() + "/members";
    }

    @GetMapping("/community/{path}/leave")
    public String leaveCommunity(@CurrentAccount Account account, @PathVariable String path) {
        Community community = communityRepository.findCommunityWithMembersByPath(path); //findCommunityWithMembersByPath(path);
        communityService.removeMember(community, account);
        return "redirect:/community/" + community.getEncodedPath() + "/members";
    }

}
