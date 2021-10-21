package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.NoteComment;

import java.util.List;

/**
 * 评论Service接口
 *
 * @author yuan
 * @date 2021-10-21
 */
public interface INoteCommentService {
    /**
     * 查询评论
     *
     * @param id 评论主键
     * @return 评论
     */
    public NoteComment selectNoteCommentById(Long id);

    /**
     * 查询评论列表
     *
     * @param noteComment 评论
     * @return 评论集合
     */
    public List<NoteComment> selectNoteCommentList(NoteComment noteComment);

    /**
     * 新增评论
     *
     * @param noteComment 评论
     * @return 结果
     */
    public int insertNoteComment(NoteComment noteComment);

    /**
     * 修改评论
     *
     * @param noteComment 评论
     * @return 结果
     */
    public int updateNoteComment(NoteComment noteComment);

    /**
     * 批量删除评论
     *
     * @param ids 需要删除的评论主键集合
     * @return 结果
     */
    public int deleteNoteCommentByIds(Long[] ids);

    /**
     * 删除评论信息
     *
     * @param id 评论主键
     * @return 结果
     */
    public int deleteNoteCommentById(Long id);
}
