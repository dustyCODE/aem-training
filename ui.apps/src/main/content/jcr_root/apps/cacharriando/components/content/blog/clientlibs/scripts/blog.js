//on ready
$(function(){

    var urlServlet = $("#blog-parent").data("url-servlet");

    $.ajax({
        url: urlServlet,
        method: "GET",
        dataType: "json"
    }).done(function(json){
        if (!json){
            return;
        }
        var $blogContainer = $("#blog-parent");
        var source = $("#sly-blog-template").html();
        var template = Handlebars.compile(source);
        var dataArray = JSON.parse(JSON.stringify(json));
        $blogContainer.html(template({"blogs": dataArray}));
    }).fail(function(xhr, error, msg){
        console.error("Something failed! :" +  msg);
    });
});









