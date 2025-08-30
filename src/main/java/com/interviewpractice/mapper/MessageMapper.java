package com.interviewpractice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.interviewpractice.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {
}
