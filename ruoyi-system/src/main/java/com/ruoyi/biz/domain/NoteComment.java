package com.ruoyi.biz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 评论对象 note_comment
 *
 * @author yuan
 * @date 2021-10-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteComment extends BaseEntity {

    /**
     * 评论编号
     */
    private Long id;

    /**
     * 笔记编号
     */
    @Excel(name = "笔记编号")
    private Long noteId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
