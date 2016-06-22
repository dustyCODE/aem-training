"use strict";

use(function () {

var CONST = {
PROP_TITLE: "jcr:title"
}

var title = {};
title.text = granite.resource.properties[CONST.PROP_TITLE] || wcm.currentPage.name;
return title;
});