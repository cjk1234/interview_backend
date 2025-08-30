package com.interviewpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interviewpractice.entity.Topic;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TopicMapper extends BaseMapper<Topic> {
}
