package com.kcz.configure;

import com.alibaba.fastjson.JSONObject;

import com.kcz.common.Result;
import com.kcz.common.ResultCode;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
/**
 * 处理方法不进入404时候重写spring-boot方法
 */
public class NotFoundController implements ErrorController {
    @Override
    public String getErrorPath() {
        return "/erro";
    }
    @RequestMapping(value = "/error")
    public Object error(HttpServletResponse response, HttpServletRequest request) {
        Result jsonResult = new Result();
        jsonResult.setCode(ResultCode.NOT_FOUND);
        jsonResult.setMessage("interface [" + request.getRequestURI() + "] not exists");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Content-type", "application/json;charset=UTF-8");
        response.setStatus(200);
        try {
            response.getWriter().write(JSONObject.toJSONString(jsonResult));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ModelAndView();

    }
}
