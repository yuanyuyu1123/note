package com.ruoyi.biz.controller;

import com.ruoyi.biz.domain.Note;
import com.ruoyi.biz.service.INoteService;
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
 * 笔记Controller
 *
 * @author yuan
 * @date 2021-10-21
 */
@RestController
@RequestMapping("/biz/note")
public class NoteController extends BaseController {
    @Resource
    private INoteService noteService;

    /**
     * 查询笔记列表
     */
    @PreAuthorize("@ss.hasPermi('biz:note:list')")
    @GetMapping("/list")
    public TableDataInfo list(Note note) {
        startPage();
        List<Note> list = noteService.selectNoteList(note);
        return getDataTable(list);
    }

    /**
     * 查询自己的笔记列表
     */
    @PreAuthorize("@ss.hasPermi('biz:note:list')")
    @GetMapping("/listSelfNote")
    public TableDataInfo listSelfNote() {
        Note note = new Note();
        note.setCreateBy(SecurityUtils.getUserId());
        startPage();
        List<Note> list = noteService.selectNoteList(note);
        return getDataTable(list);
    }

    /**
     * 导出笔记列表
     */
    @PreAuthorize("@ss.hasPermi('biz:note:export')")
    @Log(title = "笔记", businessType = BusinessType.EXPORT)
    @GetMapping("/export")
    public AjaxResult export(Note note) {
        List<Note> list = noteService.selectNoteList(note);
        ExcelUtil<Note> util = new ExcelUtil<Note>(Note.class);
        return util.exportExcel(list, "笔记数据");
    }

    /**
     * 获取笔记详细信息
     */
    @PreAuthorize("@ss.hasPermi('biz:note:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return AjaxResult.success(noteService.selectNoteById(id));
    }

    /**
     * 新增笔记
     */
    @PreAuthorize("@ss.hasPermi('biz:note:add')")
    @Log(title = "笔记", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Note note) {
        return toAjax(noteService.insertNote(note));
    }

    /**
     * 修改笔记
     */
    @PreAuthorize("@ss.hasPermi('biz:note:edit')")
    @Log(title = "笔记", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Note note) {
        return toAjax(noteService.updateNote(note));
    }

    /**
     * 删除笔记
     */
    @PreAuthorize("@ss.hasPermi('biz:note:remove')")
    @Log(title = "笔记", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(noteService.deleteNoteByIds(ids));
    }
}
