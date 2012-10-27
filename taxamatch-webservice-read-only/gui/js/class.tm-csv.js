Ext.namespace('Taxamatch');

Taxamatch.CSV = function(config) {
	
	Ext.apply(this, config, {
			fileUpload: false
		,	id: 'tm-csv'
		,	border: false
		,	title: 'Taxanomic CSV File Upload Form'
		,	autoHeight: true
		,	bodyStyle: 'padding: 10px 10px 0 10px;'
		,	labelWidth: 70
		,	defaults: {
//					anchor: '95%'
					allowBlank: false
				,	msgTarget: 'side'
			}
		,	items: [{
					xtype: 'box'
				,	autoEl: {
						html: '<div style="padding-bottom:10px;">This tool is used to accept a CSV file where the first rows contains Darwin Core headers and analyzed the scientific names and returns a new CSV file that has results appended.  For more information please see the Help section.</div>'
					}
			}, {
					xtype: 'fileuploadfield'
				,	id: 'form-file'
				,	emptyText: 'Select a file'
				,	fieldLabel: 'CSV File'
				,	name: 'csv-file'
				,	width: 200
				,	buttonCfg: {
							text: ''
						,	iconCls: 'icon_upload'
					}
        }]
			,	buttonAlign: 'left'
			,	buttons: [{
						text: 'Upload'
					,	scope: this
					,	handler: function(){
							if( this.getForm().isValid()) {
								this.getForm().submit({
										url: '../api/taxamatch_csv.php'
									,	params: {
												cmd: 'download'
										}
									,	waitMsg: 'Uploading your CSV data file...'
									,	success: function(fp, o){
												msg('Success', 'Processed file "' + o.result.file + '" on the server');
										}
								});
							}
            }
        },{
						text: 'Reset'
					,	scope: this
					,	handler: function(){
							this.getForm().reset();
						}
			}]
	});

	Taxamatch.CSV.superclass.constructor.apply(this, arguments);

};

Ext.extend( Taxamatch.CSV, Ext.FormPanel, {
}); // end of extend