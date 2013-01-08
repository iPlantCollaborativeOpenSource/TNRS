
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

	var Home = new Ext.Panel({
			id: 'home'
		,	border: false
		,	autoScroll: true
		,	autoLoad: 'docs/home.html'
	});

	var Help = new Ext.Panel({
			id: 'help'				
		,	border: false
		,	autoScroll: true
		,	autoLoad: 'docs/help.html'
	});

	var Search = new Taxamatch.Search();
	var SearchGrid = new Taxamatch.SearchGrid();

	var CSV = new Taxamatch.CSV();

	var contentPanel = new Ext.Panel({
			id: 'content-panel'
		,	region: 'center'
		,	layout: 'card'
		,	hideMode: 'offsets'
		,	activeItem: 'home'
		,	border: false
		,	layoutConfig: {
				deferredRender: true // Important when not specifying an items array
			}
		,	items: [
				Home, Search, SearchGrid, CSV, Help
			]
	});

	var tb = new Ext.Toolbar({
			items: [{
					text: 'Home'
				,	iconCls: 'icon_home'
				,	scope: contentPanel
				,	handler: function() {
						this.getLayout().setActiveItem('home');
					}
			}, {
					text: 'Search'
				,	iconCls: 'icon_search'
				,	scope: contentPanel
				,	handler: function() {
						this.getLayout().setActiveItem('tm-search');
					}
			}, {
					text: 'Search (Grid View)'
				,	iconCls: 'icon_search'
				,	scope: contentPanel
				,	handler: function() {
						this.getLayout().setActiveItem('tm-search-grid');
					}
			}, {
					text: 'Process CSV'
				,	iconCls: 'icon_upload'
				,	scope: contentPanel
				,	handler: function() {
						this.getLayout().setActiveItem('tm-csv');
					}
			}, {
					text: 'Help'
				,	iconCls: 'icon_help'
				,	scope: contentPanel
				,	handler: function() {
						this.getLayout().setActiveItem('help');
					}
			}]		
	});

	var viewport = new Ext.Viewport({
			layout:'border'
		,	items:[{
					region: 'north'
				,	id: 'north'
				,	border: false
				, bodyStyle: 'padding: 20px; background-color: Lavender; font-family: tahoma,verdana,helvetica;'
				,	html: '<h1>Taxamatch Interactive Web Service</h1>'
				, bbar: tb
			}
			, contentPanel
			]
	});
});