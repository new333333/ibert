/**
 * $RCSfile: editor_plugin_src.js,v $
 * $Revision: 1.30 $
 * $Date: 2006/02/10 16:29:37 $
 *
 * @author Moxiecode
 * @copyright Copyright � 2004-2006, Moxiecode Systems AB, All rights reserved.
 */

(function() {
	// Load plugin specific language pack
	tinymce.PluginManager.requireLangPack('ss_addimage');

	tinymce.create('tinymce.plugins.SS_AddImagePlugin', {
		init : function(ed, url) {
			// Register commands
			ed.addCommand('mce_ssAddImage', function() {
				if (!ss_diskQuotaExceeded) {
					ed.windowManager.open({
						file : url + '/image_.htm',
						width : 550 + parseInt(ed.getLang('advimage.delta_width', 0)),
						height : 460 + parseInt(ed.getLang('advimage.delta_height', 0)),
						inline : 1
					}, {
						plugin_url : url
					});
				}
				if (ss_diskQuotaExceeded) {
					ed.windowManager.open({
						file : url + '/image_no.htm',
						width : 550 + parseInt(ed.getLang('advimage.delta_width', 0)),
						height : 460 + parseInt(ed.getLang('advimage.delta_height', 0)),
						inline : 1
					}, {
						plugin_url : url
					});
				}
			});

			// Register buttons
			if (!ss_diskQuotaExceeded) {
			    ed.addButton('ss_addimage', {
				    title : 'advimage.image_desc',
				    cmd : 'mce_ssAddImage',
				    image : url + '/images/ss_image.gif'
			    });
			}
			if (ss_diskQuotaExceeded) {
			    ed.addButton('ss_addimage', {
				    title : 'ss_addimage.desc_no',
				    cmd : 'mce_ssAddImage',
				    image : url + '/images/ss_image_no.gif'
			    });
			}

			// Add a node change handler, selects the button in the UI when a image is selected
			ed.onNodeChange.add(function(ed, cm, n) {
				cm.setActive('ss_addimage', n.nodeName == 'IMG' && n.className == 'ss_addimage');
			});
		},

		getInfo : function() {
			return {
				longname : 'SS Add image',
				author : 'Moxiecode Systems AB',
				authorurl : 'http://tinymce.moxiecode.com',
				infourl : 'http://wiki.moxiecode.com/index.php/TinyMCE:Plugins/ss_addimage',
				version : tinymce.majorVersion + "." + tinymce.minorVersion
			};
		}
	});

	// Register plugin
	tinymce.PluginManager.add('ss_addimage', tinymce.plugins.SS_AddImagePlugin);
})();
