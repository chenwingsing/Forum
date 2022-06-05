$(function(){
	$("#sendBtn").click(send_letter);
	$(".closemessage").click(delete_msg);//前端的标签是ml-2 mb-1 close，这个应该前端知识,表示这个类名有三个，原题目
});

function send_letter() {
	$("#sendModal").modal("hide");
	var toName = $("#recipient-name").val();
	var content = $("#message-text").val();
	$.post(
		CONTEXT_PATH + "/letter/send",
		{"toName":toName,"content":content},
		function (data) {
			data = $.parseJSON(data);
			if (data.code == 0) {//发送成功的code
                $("#hintBody").text("发送成功");
			} else {
                $("#hintBody").text(data.msg);
			}
            $("#hintModal").modal("show");//展示提示窗口信息
            setTimeout(function(){
                $("#hintModal").modal("hide");
                location.reload();//无论成功还是失败，都要重载页面
            }, 2000);
        }
	);

}

//function delete_msg() {
	//
	//$(this).parents(".media").remove();
//}
function delete_msg() {
    var btn = this;//控制台打印出<button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Close"></button>
    var id = $(btn).prev().val();
    console.log(btn);
    $.post(
        CONTEXT_PATH + "/letter/delete",
        {"id":id},
        function(data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                $(btn).parents(".media").remove();
            } else {
                alert(data.msg);
            }
        }
    );
}