package com.ruoyi.biz.service.impl;

import com.ruoyi.biz.domain.NoteLike;
import com.ruoyi.biz.mapper.NoteLikeMapper;
import com.ruoyi.biz.service.INoteLikeService;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
     * @param noteLike 点赞
     * @return 结果
     */
    @Override
    public int insertNoteLike(NoteLike noteLike) {
        noteLike.setCreateBy(SecurityUtils.getUserId());
        noteLike.setCreateTime(DateUtils.getNowDate());
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
    public int deleteNoteLikeById(Long id) {
        return noteLikeMapper.deleteNoteLikeById(id);
    }
}
