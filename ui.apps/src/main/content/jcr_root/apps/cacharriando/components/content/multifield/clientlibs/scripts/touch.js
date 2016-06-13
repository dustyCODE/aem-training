(function (document, $) {

    //Granite.$ -> granite jquery
  "use strict";

  // listen for dialog injection
  $(document).on("foundation-contentloaded", function (e) {
    $(".showhide").each(function () {
      showHide($(this));
    });
  });

  // listen for toggle change
  $(document).on("change", ".showhide", function (e) {
    showHide($(this));
  });

  // show/hide our target depending on toggle state
  function showHide(el) {
    var target = el.data("showhideTarget"),
      value = el.prop("checked") ? el.val() : "";

    // hide all targets by default
    $(target).not(".hide").addClass("hide");

    // show any targets with a matching target value
    $(target).filter("[data-showhide-target-value=\"" + value + "\"]").removeClass("hide");
  }

})(document, Granite.$);