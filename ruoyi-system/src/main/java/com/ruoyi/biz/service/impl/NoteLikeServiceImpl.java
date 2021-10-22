package com.ruoyi.biz.service.impl;

import cn.hutool.core.util.IdUtil;
import com.ruoyi.biz.domain.Note;
import com.ruoyi.biz.domain.NoteLike;
import com.ruoyi.biz.mapper.NoteLikeMapper;
import com.ruoyi.biz.service.INoteLikeService;
import com.ruoyi.biz.service.INoteService;
import com.ruoyi.common.annotation.DataScope;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 点赞Service业务层处理
 *
 * @author yuan
 * @date 2021-10-21
 */
@Service
public class NoteLikeServiceImpl implements INoteLikeService {
    @Resource
    private NoteLikeMapper noteLikeMapper;

    @Resource
    private INoteService noteService;

    /**
     * 查询点赞
     *
     * @param id 点赞主键
     * @return 点赞
     */
    @Override
    public NoteLike selectNoteLikeById(Long id) {
        return noteLikeMapper.selectNoteLikeById(id);
    }

    /**
     * 查询点赞列表
     *
     * @param noteLike 点赞
     * @return 点赞
     */
    @Override
    public List<NoteLike> selectNoteLikeList(NoteLike noteLike) {
        return noteLikeMapper.selectNoteLikeList(noteLike);
    }

    /**
     * 新增点赞
     *
     * @param id 笔记编号
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int insertNoteLike(Long id) {
        NoteLike noteLike = new NoteLike();
        noteLike.setCreateBy(SecurityUtils.getUserId().toString());
        noteLike.setCreateTime(DateUtils.getNowDate());
        noteLike.setNoteId(id);
        noteLike.setId(IdUtil.getSnowflake(1,1).nextId());

        noteService.updateNoteLikeIncrement(id);
        return noteLikeMapper.insertNoteLike(noteLike);
    }

    /**
     * 修改点赞
     *
     * @param noteLike 点赞
     * @return 结果
     */
    @Override
    public int updateNoteLike(NoteLike noteLike) {
        return noteLikeMapper.updateNoteLike(noteLike);
    }

    /**
     * 批量删除点赞
     *
     * @param ids 需要删除的点赞主键
     * @return 结果
     */
    @Override
    public int deleteNoteLikeByIds(Long[] ids) {
        return noteLikeMapper.deleteNoteLikeByIds(ids);
    }

    /**
     * 删除点赞信息
     *
     * @param id 点赞主键
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = {Exception.class})
    public int deleteNoteLikeById(Long id) {
        NoteLike noteLike = new NoteLike();
        noteLike.setCreateBy(SecurityUtils.getUserId().toString());
        noteLike.setNoteId(id);
        List<NoteLike> noteLikes = noteLikeMapper.selectNoteLikeList(noteLike);
        noteService.updateNoteLikeDecrement(id);
        return noteLikeMapper.deleteNoteLikeById(noteLikes.get(0).getId());
    }
}
