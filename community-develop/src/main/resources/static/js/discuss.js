$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});
//上面这种写法表示页面加载完后调用

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post(
        CONTEXT_PATH + "/like",
        {"entityType":entityType,"entityId":entityId, "entityUserId":entityUserId, "postId":postId},
        function (data) {
            data = $.parseJSON(data);
            //console.log(data);
            if(data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus==1?"已赞":"赞");
            } else {
                alert(data.msg);
            }

        }
    );

}

//置顶
function setTop() {
    $.post(
        CONTEXT_PATH + "/discuss/top/"+$("#topBtn").val(),
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.reload();//$("#topBtn").attr("disabled","disabled");//你点击了之后把disabled属性设置为disabled，不可用
            } else {
                alert(data.msg);
            }
        }
    );
}

//加精
function setWonderful() {
    $.post(
        CONTEXT_PATH + "/discuss/wonderful/"+$("#wonderfulBtn").val(),
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
                location.reload();//$("#wonderfulBtn").attr("disabled","disabled");//你点击了之后把disabled属性设置为disabled，不可用
            } else {
                alert(data.msg);
            }
        }
    );
}

//删除
function setDelete() {
    $.post(
        CONTEXT_PATH + "/discuss/delete",
        {"id":$("#postId").val()},
        function (data) {
            data = $.parseJSON(data);
            if(data.code == 0) {
               location.href = CONTEXT_PATH + "/index";//删除之后直接跳转到index
            } else {
                alert(data.msg);
            }
        }
    );
}