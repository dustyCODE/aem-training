var Utils = {};
Utils.VideoEmbed = Utils.VideoEmbed || {};
Utils.VideoEmbed.functions = (function() {
	/**
	 * Receives the field refers to the selection widget and the changed value of that field
	 */
	var onChangeType = function(field, value) {
		var dialog = field.findParentByType("dialog");
		var keyTxtField = dialog.getField("./key-video");
		if (!value) {
			keyTxtField.reset();
			keyTxtField.hide();
		} else {
			keyTxtField.show();
		}
	};
	
	return {
		change : onChangeType
	}
})();