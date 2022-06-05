$(function(){
	$("#publishBtn").click(publish);//点击发布按钮的时候就调用这个函数
});

function publish() {
    $("#publishModal").modal("hide");
    //发送AJAX请求之前，将CSRF令牌设置到请求的消息头中。
    // var token = $("meta [name='_csrf']").attr("content");
    // var header = $("meta [name='_csrf_header']").attr("content");
    // $(document).ajaxSend(function (e, xhr, options) {
    //     xhr.setRequestHeader(header, token);
    // });
    //获取标题和内容
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();
    //发送异步请求
    $.post(
        CONTEXT_PATH + "/discuss/add",//请求路径
        {"title": title, "content": content},//输入数据
        function (data) {
            data = $.parseJSON(data);//通过jQuery，将服务端返回的JSON格式的字符串转为js对象
            //在提示框中返回消息
            $("hintBody").text(data.msg);
            // 显示提示框
            $("#hintModal").modal("show");
            //2s后自动隐藏提示框
            setTimeout(function () {
                $("#hintModal").modal("hide");
                //刷新界面
				if(data.code == 0) {
					window.location.reload();
				}
            }, 2000);
        }
    );
}



