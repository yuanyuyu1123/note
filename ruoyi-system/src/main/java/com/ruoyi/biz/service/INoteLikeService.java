package com.ruoyi.biz.service;

import com.ruoyi.biz.domain.NoteLike;

import java.util.List;

/**
 * 点赞Service接口
 *
 * @author yuan
 * @date 2021-10-21
 */
public interface INoteLikeService {
    /**
     * 查询点赞
     *
     * @param id 点赞主键
     * @return 点赞
     */
    public NoteLike selectNoteLikeById(Long id);

    /**
     * 查询点赞列表
     *
     * @param noteLike 点赞
     * @return 点赞集合
     */
    public List<NoteLike> selectNoteLikeList(NoteLike noteLike);

    /**
     * 新增点赞
     *
     * @param noteLike 点赞
     * @return 结果
     */
    public int insertNoteLike(NoteLike noteLike);

    /**
     * 修改点赞
     *
     * @param noteLike 点赞
     * @return 结果
     */
    public int updateNoteLike(NoteLike noteLike);

    /**
     * 批量删除点赞
     *
     * @param ids 需要删除的点赞主键集合
     * @return 结果
     */
    public int deleteNoteLikeByIds(Long[] ids);

    /**
     * 删除点赞信息
     *
     * @param id 点赞主键
     * @return 结果
     */
    public int deleteNoteLikeById(Long id);
}
