package cn.fufeii.ds.server.controller;

import cn.fufeii.ds.common.annotation.DataValid;
import cn.fufeii.ds.common.result.CommonResult;
import cn.fufeii.ds.server.model.api.request.MemberCreateRequest;
import cn.fufeii.ds.server.model.api.request.MemberTeamRequest;
import cn.fufeii.ds.server.model.api.response.MemberCreateResponse;
import cn.fufeii.ds.server.model.api.response.MemberTeamResponse;
import cn.fufeii.ds.server.service.MemberService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 会员 API controller
 *
 * @author FuFei
 * @date 2022/3/19
 */
@Api("会员API")
@DataValid
@RestController
@RequestMapping("/api/member")
public class MemberApiController {

    @Autowired
    private MemberService memberService;

    @PostMapping("/create")
    public CommonResult<MemberCreateResponse> create(@RequestBody MemberCreateRequest request) {
        return CommonResult.success(memberService.create(request));
    }

    @PostMapping("/info")
    public CommonResult<MemberCreateResponse> info(@RequestBody MemberCreateRequest request) {
        return CommonResult.success();
    }

    @PostMapping("/team")
    public CommonResult<MemberTeamResponse> team(@RequestBody MemberTeamRequest request) {
        return CommonResult.success();
    }

}