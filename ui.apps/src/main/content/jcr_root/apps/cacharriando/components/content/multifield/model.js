"use strict";

use(function () {

	 var isEdit = wcmmode.isEdit();
	 var isDesign = wcmmode.isDesign();
	 var currentMode = null;
	if (isEdit){
		currentMode = "EDITION MODE!";
	}else if (isDesign){
		currentMode = "DESIGN MODE!";
	}
	 
	console.error("You are in the " + currentMode);
	var mode = com.day.cq.wcm.api.WCMMode.fromRequest(request);
	
    console.error("MODE WCM:" + mode);

    var CONST_PROP = {
        links: "links",
        text: "text",
        checkbox: "checkbox"
    };

	var checkboxValue = properties.get(CONST_PROP.checkbox);
    var valueMap = checkboxValue ? properties : currentStyle;
	var listLinks = valueMap.get(CONST_PROP.links);

    var textValue = valueMap.get(CONST_PROP.text);
    if (checkboxValue){
		//edit dialog,
		console.error("EDITION TEXT VALUE: " + textValue);
    }else{
		//design dialog
		console.error("DESIGN TEXT VALUE: " + textValue);
    }

    var myArray = [];

    for( var i=0; i < listLinks.length; i++ ) {
		var objJson = JSON.parse(listLinks[i]);
        console.error("ESOOO object : " + (i+1) + " " + JSON.stringify(objJson));
		myArray.push(objJson);
    }

    var  prop = {checkbox: checkboxValue,
                 text: textValue,
                 linksList: myArray,
                 mode: currentMode
    };

    return prop;
});