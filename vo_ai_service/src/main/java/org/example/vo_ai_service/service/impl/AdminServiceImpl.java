package org.example.vo_ai_service.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.vo_ai_service.entity.Admin;
import org.example.vo_ai_service.mapper.AdminMapper;
import org.example.vo_ai_service.service.AdminService;
import org.springframework.stereotype.Service;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

}
