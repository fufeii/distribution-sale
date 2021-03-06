layui.use(['form', 'easyHttp', 'popup'], function () {
    let easyHttp = layui.easyHttp;
    let form = layui.form;
    let popup = layui.popup;

    //表单提交事件
    form.on('submit(btnSubmit)', function (data) {
        console.log(data.field)
        easyHttp.execute({
            url: '/admin/platform/create',
            method: 'POST',
            data: JSON.stringify(data.field)
        }, function (resp) {
            popup.success('操作成功', function () {
                parent.layer.close(parent.layer.getFrameIndex(window.name));
                parent.layui.table.reload(easyHttp.getQueryVariable('tableId'));
            });
        });
        return false;
    });

});