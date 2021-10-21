package com.ruoyi.biz.controller;

import com.ruoyi.biz.domain.NoteComment;
import com.ruoyi.biz.service.INoteCommentService;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论Controller
 *
 * @author yuan
 * @date 2021-10-21
 */
@RestController
@RequestMapping("/biz/comment")
public class NoteCommentController extends BaseController {
    @Autowired
    private INoteCommentService noteCommentService;

    /**
     * 查询评论列表
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:list')")
    @GetMapping("/list")
    public TableDataInfo list(NoteComment noteComment) {
        startPage();
        List<NoteComment> list = noteCommentService.selectNoteCommentList(noteComment);
        return getDataTable(list);
    }

    /**
     * 导出评论列表
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:export')")
    @Log(title = "评论", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(NoteComment noteComment) {
        List<NoteComment> list = noteCommentService.selectNoteCommentList(noteComment);
        ExcelUtil<NoteComment> util = new ExcelUtil<NoteComment>(NoteComment.class);
        return util.exportExcel(list, "评论数据");
    }

    /**
     * 获取评论详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(noteCommentService.selectNoteCommentById(id));
    }

    /**
     * 新增评论
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:add')")
    @Log(title = "评论", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NoteComment noteComment) {
        return toAjax(noteCommentService.insertNoteComment(noteComment));
    }

    /**
     * 修改评论
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:edit')")
    @Log(title = "评论", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NoteComment noteComment) {
        return toAjax(noteCommentService.updateNoteComment(noteComment));
    }

    /**
     * 删除评论
     */
    @PreAuthorize("@ss.hasPermi('biz:comment:remove')")
    @Log(title = "评论", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(noteCommentService.deleteNoteCommentByIds(ids));
    }
}
