package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController // 声明该类为 REST 风格控制器：方法的返回值会自动序列化为 JSON 写入响应体
@RequestMapping("/admin/employee") // 将本类所有接口的 URL 统一映射到 /admin/employee 前缀下
@Slf4j // Lombok 注解：自动生成名为 log 的日志对象，便于打印日志
public class EmployeeController {

    @Autowired // Spring 自动从容器中注入 EmployeeService 接口的实现类对象（EmployeeServiceImpl）
    private EmployeeService employeeService;
    @Autowired // Spring 自动注入 JwtProperties 配置对象，用于读取 application.yml 中 sky.jwt 下的配置
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login") // 将 HTTP POST 请求映射到本方法，完整路径为 /admin/employee/login
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) { // @RequestBody：将请求体中的 JSON 反序列化为 EmployeeLoginDTO 对象
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout") // 将 HTTP POST 请求映射到本方法，完整路径为 /admin/employee/logout
    public Result<String> logout() {
        return Result.success();
    }

}
