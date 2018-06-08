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

import com.github.pagehelper.PageInfo;
import com.study.shiro.business.enums.ResponseStatus;
import com.study.shiro.business.service.ShiroService;
import com.study.shiro.business.service.SysRoleResourcesService;
import com.study.shiro.business.service.SysRoleService;
import com.study.shiro.business.vo.RoleConditionVO;
import com.study.shiro.framework.object.*;
import com.study.shiro.persistence.beans.SysRole;
import com.study.shiro.util.ResultUtil;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统角色管理
 *
 * @author yadong.zhang (yadong.zhang0415(a)gmail.com)
 * @version 1.0
 * @website https://www.zhyd.me
 * @date 2018/4/24 14:37
 * @since 1.0
 */
@RestController
@RequestMapping("/roles")
public class RestRoleController {
    @Autowired
    private SysRoleService roleService;
    @Autowired
    private SysRoleResourcesService roleResourcesService;
    @Autowired
    private ShiroService shiroService;

    @RequiresPermissions("roles")
    @PostMapping("/list")
    public JsonResult getAll(RoleConditionVO vo) {
        PageInfo<SysRole> pageInfo = roleService.findPageBreakByCondition(vo);
        return ResultUtil.build(pageInfo);
    }

    @RequiresPermissions("user:allotRole")
    @PostMapping("/rolesWithSelected")
    public JsonResult rolesWithSelected(Integer uid) {
        return ResultUtil.build(ResponseStatus.SUCCESS, roleService.queryRoleListWithSelected(uid));
    }

    @RequiresPermissions("role:allotResource")
    @PostMapping("/saveRoleResources")
    public JsonResult saveRoleResources(Long roleId, String resourcesId) {
        if (StringUtils.isEmpty(roleId)) {
            return ResultUtil.build("error");
        }
        roleResourcesService.addRoleResources(roleId, resourcesId);
        // 重新加载所有拥有roleId的用户的权限信息
        shiroService.reloadAuthorizingByRoleId(roleId);
        return ResultUtil.build(ResponseStatus.SUCCESS,"成功");
    }

    @RequiresPermissions("role:add")
    @PostMapping(value = "/add")
    public JsonResult add(SysRole role) {
        roleService.insert(role);
        return ResultUtil.build(ResponseStatus.SUCCESS,"成功");
    }

    @RequiresPermissions(value = {"role:batchDelete", "role:delete"}, logical = Logical.OR)
    @PostMapping(value = "/remove")
    public JsonResult remove(Long[] ids) {
        if (null == ids) {
            return ResultUtil.build(500, "请至少选择一条记录");
        }
        for (Long id : ids) {
            roleService.removeByPrimaryKey(id);
            roleResourcesService.removeByRoleId(id);
        }
        return ResultUtil.build(ResponseStatus.SUCCESS,"成功删除 [" + ids.length + "] 个角色");
    }

    @RequiresPermissions("role:edit")
    @PostMapping("/get/{id}")
    public JsonResult get(@PathVariable Long id) {
        return ResultUtil.build(ResponseStatus.SUCCESS, this.roleService.getByPrimaryKey(id));
    }

    @RequiresPermissions("role:edit")
    @PostMapping("/edit")
    public JsonResult edit(SysRole role) {
        try {
            roleService.updateSelective(role);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtil.build("角色修改失败！");
        }
        return ResultUtil.build(ResponseStatus.SUCCESS);
    }

}
