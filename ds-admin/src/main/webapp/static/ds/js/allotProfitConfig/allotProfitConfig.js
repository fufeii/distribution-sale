layui.use(['table', 'form', 'layer', 'easyHttp', 'popup'], function () {
    let table = layui.table;
    let form = layui.form;
    let layer = layui.layer;
    let easyHttp = layui.easyHttp;
    let popup = layui.popup;

    /**
     * 页面实体对象
     */
    let AllotProfitConfig = {
        tableId: 'profitParamTable'
    }

    /**
     * 初始化表列
     */
    AllotProfitConfig.initCols = function () {
        return [
            [
                {
                    title: '序号',
                    type: 'numbers'
                },
                {
                    title: '平台名称',
                    field: 'platformNickname',
                    align: 'center'
                },
                {
                    title: '账户类型',
                    field: 'accountType',
                    align: 'center'
                },
                {
                    title: '分润类型',
                    field: 'profitType',
                    align: 'center'
                },
                {
                    title: '计算方式',
                    field: 'calculateMode',
                    align: 'center'
                },
                {
                    title: '分润等级',
                    field: 'profitLevel',
                    align: 'center'
                },
                {
                    title: '分润比列',
                    field: 'profitRatio',
                    align: 'center'
                },
                {
                    title: '会员身份类型',
                    field: 'memberIdentityType',
                    align: 'center'
                },
                {
                    title: '会员段位类型',
                    field: 'memberRankType',
                    align: 'center'
                },
                {
                    title: '状态',
                    align: 'center',
                    templet: '#stateTpl'
                },
                {
                    title: '操作',
                    toolbar: '#rowBar',
                    align: 'center',
                    width: 130
                }
            ]
        ];
    }


    /**
     * 搜索操作
     */
    AllotProfitConfig.onSearch = function () {
        let query = form.val('profitParamQueryForm');
        Object.keys(query).forEach(function (key) {
            let value = query[key];
            if (value === '') {
                query[key] = null;
            }
        });
        table.reload(AllotProfitConfig.tableId, {
            where: query,
            page: {curr: 1}
        });
    }

    /**
     * 弹出添加对话框
     */
    AllotProfitConfig.openAddDlg = function () {
        layer.open({
            type: 2,
            title: '添加分润配置',
            shade: 0.3,
            area: ['500px', '500px'],
            content: '/view/allot-profit-config/add?tableId=' + AllotProfitConfig.tableId,
        });
    };

    /**
     * 弹出编辑对话框
     */
    AllotProfitConfig.openEditDlg = function (id) {
        layer.open({
            type: 2,
            title: '编辑分润配置',
            shade: 0.3,
            area: ['500px', '500px'],
            content: '/view/allot-profit-config/edit?tableId=' + AllotProfitConfig.tableId + '&id=' + id,
        });
    };

    /**
     * 移除操作
     */
    AllotProfitConfig.onRemove = function (id) {
        layer.confirm('确认删除吗', {icon: 3, title: '提示'}, function (index) {
            easyHttp.execute({url: '/admin/allot-profit-config/remove/' + id, method: 'DELETE'}, function (resp) {
                popup.success('操作成功');
                table.reload(AllotProfitConfig.tableId);
            });
            layer.close(index);
        });
    }

    /**
     * 表格渲染配置
     */
    table.render({
        elem: '#' + AllotProfitConfig.tableId,
        skin: 'line',
        size: 'lg',
        url: '/admin/allot-profit-config/page',
        method: 'POST',
        page: true,
        contentType: 'application/json',
        request: {pageName: 'page', limitName: 'size'},
        response: {countName: 'total'},
        cols: AllotProfitConfig.initCols(),
        toolbar: '#toolbar',
        defaultToolbar: ['filter', 'print', 'exports']
    });

    /**
     * 监听表格上方按钮 toolbar
     */
    table.on('toolbar(' + AllotProfitConfig.tableId + ')', function (obj) {
        if (obj.event === 'add') {
            AllotProfitConfig.openAddDlg();
        }
    });

    /**
     * 监听数据行末尾按钮 rowBar
     */
    table.on('tool(' + AllotProfitConfig.tableId + ')', function (obj) {
        if (obj.event === 'remove') {
            AllotProfitConfig.onRemove(obj.data.id);
        } else if (obj.event === 'edit') {
            AllotProfitConfig.openEditDlg(obj.data.id);
        }
    });


    /**
     * 搜索按钮点击事件
     */
    form.on('submit(profitParamQueryFormSubmit)', function (data) {
        AllotProfitConfig.onSearch(data);
        return false;
    });

    /**
     * 启用禁用点击事件
     */
    form.on('switch(stateBtn)', function (data) {
        let tips = data.elem.checked ? '启用' : '禁用';
        let path = data.elem.checked ? 'enable' : 'disable';
        data.elem.checked = !data.elem.checked;
        form.render('checkbox');
        layer.confirm('确认' + tips, {icon: 3, title: '提示', closeBtn: 0}, function (index) {
            easyHttp.execute({
                url: '/admin/allot-profit-config/' + path + '/' + data.value,
                method: 'PUT'
            }, function (resp) {
                data.elem.checked = !data.elem.checked;
                form.render('checkbox');
                popup.success('操作成功');
                layer.close(index);
            }, function (resp) {
                popup.failure(resp.msg);

            });
        });
    });

});
