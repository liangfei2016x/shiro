$(function () {
    showMenu();
    function showMenu(){
        var userId=$("#menu").attr("value");
        var url = "allResource/"+userId;
        var html ="<li><a href=\"/index\"><i class=\"fa fa-home\"></i>首页</a></li>";
        $.ajax({
            type:"GET",
            url:url,
            dataType:"json",
            success:function (data) {
                var result = data.data;
                console.log(result);
                for (var i = 0; i < result.length; i++) {
                    html+="<li>";
                    html+="<a><i class='"+result[i].icon+"'></i>"+result[i].name+"<span class=\"fa fa-chevron-down\"></span></a>";
                    var child = result[i].nodes;
                    html+="<ul class=\"nav child_menu\">";
                    for (var j = 0; j <child.length ; j++) {
                        html+="<li><a href="+child[j].url+"><i></i>"+child[j].name+"</a></li>";
                    }
                    html+="</ul>";
                    html+="</li>";
                }
                $("#menu").html();
                $("#menu").html(html);
            }
        })
    }
})