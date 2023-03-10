package com.company.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.company.project.common.utils.DataResult;
import com.company.project.common.utils.NumberUtils;
import com.company.project.entity.SysDictDetailEntity;
import com.company.project.service.SysDictDetailService;
import com.company.project.service.SysDictService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


/**
 * 字典明细管理
 *
 * @author wenbin
 * @version V1.0
 * @date 2020年3月18日
 */
@Api(tags = "字典明细管理")
@RestController
@RequestMapping("/sysDictDetail")
public class SysDictDetailController {
    @Resource
    private SysDictDetailService sysDictDetailService;

    @Resource
    private SysDictService sysDictService;

    @ApiOperation(value = "新增")
    @PostMapping("/add")
    @RequiresPermissions("sysDict:add")
    public DataResult add(@RequestBody SysDictDetailEntity sysDictDetail) {
        if (StringUtils.isEmpty(sysDictDetail.getValue())) {
            return DataResult.fail("字典值不能为空");
        }
        LambdaQueryWrapper<SysDictDetailEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictDetailEntity::getValue, sysDictDetail.getValue());
        queryWrapper.eq(SysDictDetailEntity::getDictId, sysDictDetail.getDictId());
        SysDictDetailEntity q = sysDictDetailService.getOne(queryWrapper);
        if (q != null) {
            return DataResult.fail("字典名称-字典值已存在");
        }
        sysDictDetailService.save(sysDictDetail);
        return DataResult.success();
    }




    @ApiOperation(value = "新增")
    @PostMapping("/addByGradePage")
    @RequiresPermissions("sysDict:add")
    public DataResult addByGradePage(@RequestBody SysDictDetailEntity sysDictDetail) {
        if (StringUtils.isEmpty(sysDictDetail.getDictId())) {
            return DataResult.fail("系统错误，请稍后再试");
        }

        LambdaQueryWrapper<SysDictDetailEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictDetailEntity::getDictId, sysDictDetail.getDictId());
        List<SysDictDetailEntity> list = sysDictDetailService.list(queryWrapper);
        Integer max_value = null;
        Integer max_sort = 0;
        for(SysDictDetailEntity bean: list){
            if(max_value == null){
                if(NumberUtils.isInteger(bean.getValue()))
                    max_value = Integer.parseInt(bean.getValue());
                else
                    max_value = 0;
                max_sort = bean.getSort();
            }else{
                if(NumberUtils.isInteger(bean.getValue())&& Integer.parseInt(bean.getValue()) > max_value){
                    max_value = Integer.parseInt(bean.getValue());
                }
                if(max_sort < bean.getSort())
                    max_sort += 1;
            }
        }

        if(max_value == 0){
            sysDictDetail.setValue(NumberUtils.getUUID());
        }else{
            sysDictDetail.setValue(max_value + 1 + "");
        }
        sysDictDetail.setSort(max_sort + 1);

        sysDictDetailService.save(sysDictDetail);
        return DataResult.success(sysDictService.getType("class_type"));
    }



    @ApiOperation(value = "删除")
    @DeleteMapping("/delete")
    @RequiresPermissions("sysDict:delete")
    public DataResult delete(@RequestBody @ApiParam(value = "id集合") List<String> ids) {
        sysDictDetailService.removeByIds(ids);
        return DataResult.success();
    }

    @ApiOperation(value = "更新")
    @PutMapping("/update")
    @RequiresPermissions("sysDict:update")
    public DataResult update(@RequestBody SysDictDetailEntity sysDictDetail) {
        if (StringUtils.isEmpty(sysDictDetail.getValue())) {
            return DataResult.fail("字典值不能为空");
        }
        LambdaQueryWrapper<SysDictDetailEntity> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(SysDictDetailEntity::getValue, sysDictDetail.getValue());
        queryWrapper.eq(SysDictDetailEntity::getDictId, sysDictDetail.getDictId());
        SysDictDetailEntity q = sysDictDetailService.getOne(queryWrapper);
        if (q != null && !q.getId().equals(sysDictDetail.getId())) {
            return DataResult.fail("字典名称-字典值已存在");
        }

        sysDictDetailService.updateById(sysDictDetail);
        return DataResult.success();
    }


    @ApiOperation(value = "查询列表数据")
    @PostMapping("/listByPage")
    @RequiresPermissions("sysDict:list")
    public DataResult findListByPage(@RequestBody SysDictDetailEntity sysDictDetail) {
        Page page = new Page(sysDictDetail.getPage(), sysDictDetail.getLimit());
        if (StringUtils.isEmpty(sysDictDetail.getDictId())) {
            return DataResult.success();
        }
        IPage<SysDictDetailEntity> iPage = sysDictDetailService.listByPage(page, sysDictDetail.getDictId());
        return DataResult.success(iPage);
    }

}