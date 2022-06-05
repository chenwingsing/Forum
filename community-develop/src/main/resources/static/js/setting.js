$(function(){
    $("form").submit(check_data);
    $("input").focus(clear_error);
});

function check_data() {
    var pwd1 = $("#new-password").val();
    var pwd2 = $("#confirm-password").val();
    if(pwd1 != pwd2) {
        $("#confirm-password").addClass("is-invalid");
        return false;
    }
    return true;
}

function clear_error() {
    $(this).removeClass("is-invalid");
}

$(function () {
    $("#uploadForm").submit(upload);
});
function upload() {
    $.ajax({
        url: "http://upload-z1.qiniup.com",
        method: "post",
        processData: false,//不要把表单转成字符串
        contentType: false,//不让jquery设置上传类型
        data: new FormData($("#uploadForm")[0]),//取第0个值就是dom？
        success: function (data) {//七牛云返回json
            if (data && data.code == 0){
                //更新访问路径
                $.post(
                    CONTEXT_PATH + "/user/header/url",
                    {"fileName":$("input[name='key']").val()},
                    function (data) {
                        data = $.parseJSON(data);//而我们服务器返回的是字符串，但是格式是json，所以转成json对象
                        if (data.code == 0) {
                            window.location.reload();
                        } else {
                            alert(data.msg);
                        }
                    }
                );
            } else {
                alert("上传失败")
            }
        }
    });
    return false;//因为我们没写action那些，所以返回false
}