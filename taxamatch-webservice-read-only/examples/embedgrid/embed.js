Ext.namespace('Taxamatch');

Ext.override(Ext.XTemplate, {
	exists: function(o, name) {
		return o.hasOwnProperty(name);
	}
});

Ext.onReady(function() {

	Taxamatch.APP_PATH = "/taxamatch-webservice/";
	
	Ext.QuickTips.init();

	// Disable browser right click
	Ext.fly(document.body).on('contextmenu', function(e, target) {
		e.preventDefault();
	});	

	Ext.BLANK_IMAGE_URL = "http://extjs.cachefly.net/ext-2.2.1/resources/images/default/s.gif";	

	var SearchBox = new Ext.form.TextArea({
			id: 'searchBox'
		,	width: 300
		, height: 75
		,	fieldLabel: 'Scientific Name(s)'
		,	renderTo: 'searchBox'		
	});
	
	var SearchBtn = new Ext.Button({
			text: 'Search'
		,	renderTo: 'searchBtn'
		,	handler: function() {
				var g = Ext.getCmp('searchGrid');
				g.str = Ext.getCmp('searchBox').getValue();
				if (g.str != '') {
					g.doSearch();
				}
			}
	});
			
	var SearchGrid = new Taxamatch.FlatGrid({ 
			width: 600
		,	height: 400
		,	search_mode: 'rapid' // <empty> for not rapid
		,	source: 'col2009' // Set this to your source
		,	id: 'searchGrid'
		,	renderTo: 'taxamatchgrid'
/*		
		,	columns: [
					{header: 'Type', width: 75, sortable: true, dataIndex: 'match_type'}
				,	{header: 'Genus', width: 75, sortable: true, dataIndex: 'genus'}
				,	{header: 'Species', width: 150, sortable: true, dataIndex: 'genus_species'}
				,	{header: 'Author', width: 155, sortable: true, dataIndex: 'temp_authority'}
			]
*/		
		,	listeners: {
				rowdblclick: function( grid, rowIndex ) {
					var row = grid.store.getAt(rowIndex);
					window.open( '' + row.id,'_blank');
				}
			}
	});

});