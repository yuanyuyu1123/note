package com.ruoyi.biz.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.common.core.domain.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 点赞对象 note_like
 *
 * @author yuan
 * @date 2021-10-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoteLike extends BaseEntity {

    /**
     * 点赞编号
     */
    private Long id;

    /**
     * 笔记编号
     */
    private Long noteId;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}
