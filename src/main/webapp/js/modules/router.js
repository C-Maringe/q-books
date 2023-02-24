(function(){
    var navActionItem           = $(".navActionItem");
    var layoutMainBody          = $("#body");

    navActionItem.on('click', __handleNavigation);

    function __handleNavigation() {
        var pageClicked = $(this).data('page');
        var viewToRender = render(pageClicked,'index',{});
        layoutMainBody.empty().append(viewToRender);

        $("button.navbar-toggler").click();
    }
})();

function render(tmpl_folder, tmpl_name, tmpl_data) {
    if(!render.tmpl_cache )
        render.tmpl_cache = {};

    if (!render.tmpl_cache[tmpl_folder+'/'+tmpl_name]) {
        var tmpl_dir = '/views/'+tmpl_folder;
        var tmpl_url = tmpl_dir + '/' + tmpl_name + '.html';

        var tmpl_string;
        $.ajax({
            url: tmpl_url,
            method: 'GET',
            async: false,
            success: function(data) {
                tmpl_string = data;
                render.tmpl_cache[tmpl_name] = _.template(tmpl_string);
            }
        });
    }

    return render.tmpl_cache[tmpl_name](tmpl_data);
}
