package com.ruoyi.biz.controller;

import com.ruoyi.biz.domain.Note;
import com.ruoyi.biz.domain.NoteLike;
import com.ruoyi.biz.service.INoteLikeService;
import com.ruoyi.biz.service.INoteService;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 点赞Controller
 *
 * @author yuan
 * @date 2021-10-21
 */
@RestController
@RequestMapping("/biz/like")
public class NoteLikeController extends BaseController {
    @Resource
    private INoteLikeService noteLikeService;

    /**
     * 查询点赞列表
     */
    @PreAuthorize("@ss.hasPermi('biz:like:list')")
    @GetMapping("/list")
    @DataScope
    public TableDataInfo list(NoteLike noteLike) {
        startPage();
        noteLike.setCreateBy(SecurityUtils.getUserId().toString());
        noteLike.setNoteId(noteLike.getId());
        noteLike.setId(null);
        List<NoteLike> list = noteLikeService.selectNoteLikeList(noteLike);
        return getDataTable(list);
    }

    /**
     * 导出点赞列表
     */
    @PreAuthorize("@ss.hasPermi('biz:like:export')")
    @Log(title = "点赞", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(NoteLike noteLike) {
        List<NoteLike> list = noteLikeService.selectNoteLikeList(noteLike);
        ExcelUtil<NoteLike> util = new ExcelUtil<NoteLike>(NoteLike.class);
        return util.exportExcel(list, "点赞数据");
    }

    /**
     * 获取点赞详细信息
     */
    @DataScope
    @PreAuthorize("@ss.hasPermi('biz:like:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(noteLikeService.selectNoteLikeById(id));
    }

    /**
     * 新增点赞
     */
    @PreAuthorize("@ss.hasPermi('biz:like:add')")
    @Log(title = "点赞", businessType = BusinessType.INSERT)
    @GetMapping("/add/{id}")
    public AjaxResult add(@PathVariable Long id) {
        return toAjax(noteLikeService.insertNoteLike(id));
    }

    /**
     * 修改点赞
     */
    @PreAuthorize("@ss.hasPermi('biz:like:edit')")
    @Log(title = "点赞", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NoteLike noteLike) {
        return toAjax(noteLikeService.updateNoteLike(noteLike));
    }

    /**
     * 删除点赞
     */
    @PreAuthorize("@ss.hasPermi('biz:like:remove')")
    @Log(title = "点赞", businessType = BusinessType.DELETE)
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable Long id) {

        return toAjax(noteLikeService.deleteNoteLikeById(id));
    }
}
