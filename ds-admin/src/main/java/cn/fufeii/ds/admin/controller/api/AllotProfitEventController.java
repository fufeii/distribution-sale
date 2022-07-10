package cn.fufeii.ds.admin.controller.api;

import cn.fufeii.ds.admin.config.constant.DsAdminConstant;
import cn.fufeii.ds.admin.model.vo.request.AllotProfitEventQueryRequest;
import cn.fufeii.ds.admin.model.vo.response.AllotProfitEventResponse;
import cn.fufeii.ds.admin.model.vo.response.ProfitIncomeRecordResponse;
import cn.fufeii.ds.admin.service.AllotProfitEventService;
import cn.fufeii.ds.common.model.CommonResult;
import cn.fufeii.ds.common.model.PageResult;
import cn.fufeii.ds.repository.entity.AllotProfitEvent;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分润事件 API Controller
 *
 * @author FuFei
 */
@Api(tags = "分润事件")
@RestController
@RequestMapping(DsAdminConstant.API_PATH_PREFIX + "/allot-profit-event")
public class AllotProfitEventController {

    @Autowired
    private AllotProfitEventService allotProfitEventService;

    @ApiOperation("分页查询")
    @PostMapping("/page")
    public PageResult<AllotProfitEventResponse> page(@RequestBody AllotProfitEventQueryRequest pageParam) {
        IPage<AllotProfitEventResponse> pageResult = allotProfitEventService.page(pageParam, new Page<AllotProfitEvent>(pageParam.getPage(), pageParam.getSize()).addOrder(OrderItem.desc("id")));
        return PageResult.success(pageResult.getTotal(), pageResult.getRecords());
    }

    @ApiOperation("查询事件详情")
    @GetMapping("/info/{id}")
    public CommonResult<AllotProfitEventResponse> info(@PathVariable Long id) {
        return CommonResult.success(allotProfitEventService.info(id));
    }

    @ApiOperation("查询分润收入记录列表")
    @GetMapping("/profit-income-record/list/{id}")
    public CommonResult<List<ProfitIncomeRecordResponse>> profitIncomeRecordList(@PathVariable Long id) {
        return CommonResult.success(allotProfitEventService.profitIncomeRecordList(id));
    }

}