package cn.fufeii.ds.admin.controller.view;

import cn.fufeii.ds.admin.config.constant.DsAdminConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 会员中心页 VIEW Controller
 *
 * @author FuFei
 */
@Controller
@RequestMapping(DsAdminConstant.VIEW_PATH_PREFIX + "/member")
public class MemberViewController {

    @GetMapping("/")
    public String index() {
        return "/member/member.html";
    }

    @GetMapping("/account")
    public String account() {
        return "/member/memberAccount.html";
    }

}