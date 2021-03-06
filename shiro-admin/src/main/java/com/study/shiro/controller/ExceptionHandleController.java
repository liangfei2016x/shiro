/**
 * MIT License
 * Copyright (c) 2018 yadong.zhang
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.study.shiro.controller;

import com.study.shiro.business.consts.CommonConst;
import com.study.shiro.business.enums.ResponseStatus;
import com.study.shiro.framework.exception.StudyException;
import com.study.shiro.framework.object.JsonResult;
import com.study.shiro.framework.object.ResponseVO;
import com.study.shiro.util.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * 统一异常处理类<br>
 * 捕获程序所有异常，针对不同异常，采取不同的处理方式
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/24 14:37
 * @since 1.0
 */
@ControllerAdvice
public class ExceptionHandleController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandleController.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResult handle(Throwable e) {
        if (e instanceof StudyException) {
            return ResultUtil.build(e.getMessage());
        }
        if (e instanceof UndeclaredThrowableException) {
            e = ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        ResponseStatus responseStatus = ResponseStatus.getResponseStatus(e.getMessage());
        if (responseStatus != null) {
            LOGGER.error(responseStatus.getMessage());
            return ResultUtil.build(responseStatus.getCode(), responseStatus.getMessage());
        }
        e.printStackTrace(); // 打印异常栈
        return ResultUtil.build(CommonConst.DEFAULT_ERROR_CODE, ResponseStatus.ERROR.getMessage());
    }

}
