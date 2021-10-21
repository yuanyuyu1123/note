package com.ruoyi.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.biz.domain.Note;
import com.ruoyi.biz.mapper.NoteMapper;
import com.ruoyi.biz.service.INoteService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 笔记Service业务层处理
 *
 * @author yuan
 * @date 2021-10-21
 */
@Service
@Slf4j
public class NoteServiceImpl implements INoteService {
    @Resource
    private NoteMapper noteMapper;

    /**
     * 查询笔记
     *
     * @param id 笔记主键
     * @return 笔记
     */
    @Override
    public Note selectNoteById(Long id) {
        return noteMapper.selectNoteById(id);
    }

    /**
     * 查询笔记列表
     *
     * @param note 笔记
     * @return 笔记
     */
    @Override
    public List<Note> selectNoteList(Note note) {
        List<Note> noteList = noteMapper.selectNoteList(note);
        log.debug("noteList={}",noteList);
        return noteList;
    }

    /**
     * 新增笔记
     *
     * @param note 笔记
     * @return 结果
     */
    @Override
    public int insertNote(Note note) {
        note.setId(IdUtil.getSnowflake(1,1).nextId());
        note.setCreateBy(SecurityUtils.getUserId());
        note.setCreateTime(DateUtils.getNowDate());
        return noteMapper.insertNote(note);
    }

    /**
     * 修改笔记
     *
     * @param note 笔记
     * @return 结果
     */
    @Override
    public int updateNote(Note note) {
        note.setUpdateTime(DateUtils.getNowDate());
        return noteMapper.updateNote(note);
    }

    /**
     * 批量删除笔记
     *
     * @param ids 需要删除的笔记主键
     * @return 结果
     */
    @Override
    public int deleteNoteByIds(Long[] ids) {
        return noteMapper.deleteNoteByIds(ids);
    }

    /**
     * 删除笔记信息
     *
     * @param id 笔记主键
     * @return 结果
     */
    @Override
    public int deleteNoteById(Long id) {
        return noteMapper.deleteNoteById(id);
    }
}
