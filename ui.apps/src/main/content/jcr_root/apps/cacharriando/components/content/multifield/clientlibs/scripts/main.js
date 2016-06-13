'use strict';

var toggleMultifield = function(checkbox){

	console.log("hola! entro al check!");
    var panel = checkbox.findParentByType('panel');
    var dlgFieldSet = panel.findByType('dialogfieldset')[0];
    var show =  checkbox.getValue()[0];
    if (show) {
        console.log("show!");
        dlgFieldSet.show();
        panel.doLayout();
    } else {
        console.log("hide!");
        dlgFieldSet.hide();
    }
};
