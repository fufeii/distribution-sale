layui.use(['table', 'form', 'jquery', 'common'], function () {
    let table = layui.table;
    let form = layui.form;
    let $ = layui.jquery;
    let common = layui.common;

    let MODULE_PATH = "operate/";

    let cols = [
        [{
            type: 'checkbox'
        },
            {
                title: '账号',
                field: 'username',
                align: 'center',
                width: 100
            },
            {
                title: '姓名',
                field: 'realName',
                align: 'center'
            },
            {
                title: '性别',
                field: 'sex',
                align: 'center',
                width: 80,
                templet: '#user-sex'
            },
            {
                title: '电话',
                field: 'phone',
                align: 'center'
            },
            {
                title: '启用',
                field: 'enable',
                align: 'center',
                templet: '#user-enable'
            },
            {
                title: '登录',
                field: 'login',
                align: 'center',
                templet: '#user-login'
            },
            {
                title: '注册',
                field: 'createTime',
                align: 'center',
                templet: '#user-createTime'
            },
            {
                title: '操作',
                toolbar: '#user-bar',
                align: 'center',
                width: 130
            }
        ]
    ]

    table.render({
        elem: '#user-table',
        url: '/ds/data/user.json',
        page: true,
        cols: cols,
        skin: 'line',
        toolbar: '#user-toolbar',
        defaultToolbar: [{
            title: '刷新',
            layEvent: 'refresh',
            icon: 'layui-icon-refresh',
        }, 'filter', 'print', 'exports']
    });

    table.on('tool(user-table)', function (obj) {
        if (obj.event === 'remove') {
            window.remove(obj);
        } else if (obj.event === 'edit') {
            window.edit(obj);
        }
    });

    table.on('toolbar(user-table)', function (obj) {
        if (obj.event === 'add') {
            window.add();
        } else if (obj.event === 'refresh') {
            window.refresh();
        } else if (obj.event === 'batchRemove') {
            window.batchRemove(obj);
        }
    });

    form.on('submit(user-query)', function (data) {
        table.reload('user-table', {
            where: data.field
        })
        return false;
    });

    form.on('switch(user-enable)', function (obj) {
        layer.tips(this.value + ' ' + this.name + '：' + obj.elem.checked, obj.othis);
    });

    window.add = function () {
        layer.open({
            type: 2,
            title: '新增',
            shade: 0.1,
            area: [common.isModile() ? '100%' : '500px', common.isModile() ? '100%' : '400px'],
            content: MODULE_PATH + 'add.html'
        });
    }

    window.edit = function (obj) {
        layer.open({
            type: 2,
            title: '修改',
            shade: 0.1,
            area: ['500px', '400px'],
            content: MODULE_PATH + 'edit.html'
        });
    }

    window.remove = function (obj) {
        layer.confirm('确定要删除该用户', {
            icon: 3,
            title: '提示'
        }, function (index) {
            layer.close(index);
            let loading = layer.load();
            $.ajax({
                url: MODULE_PATH + "remove/" + obj.data['userId'],
                dataType: 'json',
                type: 'delete',
                success: function (result) {
                    layer.close(loading);
                    if (result.success) {
                        layer.msg(result.msg, {
                            icon: 1,
                            time: 1000
                        }, function () {
                            obj.del();
                        });
                    } else {
                        layer.msg(result.msg, {
                            icon: 2,
                            time: 1000
                        });
                    }
                }
            })
        });
    }

    window.batchRemove = function (obj) {

        var checkIds = common.checkField(obj, 'userId');

        if (checkIds === "") {
            layer.msg("未选中数据", {
                icon: 3,
                time: 1000
            });
            return false;
        }

        layer.confirm('确定要删除这些用户', {
            icon: 3,
            title: '提示'
        }, function (index) {
            layer.close(index);
            let loading = layer.load();
            $.ajax({
                url: MODULE_PATH + "batchRemove/" + ids,
                dataType: 'json',
                type: 'delete',
                success: function (result) {
                    layer.close(loading);
                    if (result.success) {
                        layer.msg(result.msg, {
                            icon: 1,
                            time: 1000
                        }, function () {
                            table.reload('user-table');
                        });
                    } else {
                        layer.msg(result.msg, {
                            icon: 2,
                            time: 1000
                        });
                    }
                }
            })
        });
    }

    window.refresh = function (param) {
        table.reload('user-table');
    }
})
