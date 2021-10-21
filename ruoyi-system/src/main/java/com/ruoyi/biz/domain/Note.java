package com.ruoyi.biz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 笔记对象 note
 *
 * @author yuan
 * @date 2021-10-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Note implements Serializable {

    /**
     * 笔记编号
     */
    private Long id;

    /**
     * 标题
     */
    @Excel(name = "标题")
    private String title;

    /**
     * 笔记内容
     */
    private String content;

    /**
     * 点赞数
     */
    @Excel(name = "点赞数")
    private Integer likeNumber;

    /**
     * 评论数
     */
    @Excel(name = "评论数")
    private Integer commentNumber;

    /**
     * 标签
     */
    @Excel(name = "标签")
    private String tag;

    /**
     * 创建者
     */
    private Long createBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
