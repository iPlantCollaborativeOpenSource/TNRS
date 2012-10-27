Ext.namespace('Taxamatch');

Taxamatch.SearchGrid = function(config) {

	var sources = new Ext.data.JsonStore({
			url: '../api/taxamatch.php'
		,	baseParams: { cmd: 'sources' }
		,	root: 'data'
		,	fields: ['name', 'value']
		,	autoLoad: true
	});

	this.form = new Ext.form.FormPanel({
			id: 'searchform2'
		,	url: '../api/taxamatch.php'
		,	title: 'Filters'
		,	split: true
		,	border: false
		,	region: 'west'
		,	autoScroll: true
		,	width: 350
    , labelWidth: 125
		,	bodyStyle: 'padding:5px 5px 0'
		,	defaults: {width: 150}
		,	defaultType: 'textfield'
		,	tbar: [{
					text: 'Clear Results'
				,	xtype: 'button'
				,	handler: this.clearResults
				,	scope: this
			}]
		,	items: [{
					store: sources
				,	itemId: 'source'
				,	xtype: 'combo'
				,	displayField: 'name'
				,	typeAhead: true
				,	mode: 'local'
				,	fieldLabel: 'Data Source'
				, forceSelection: true
				,	readOnly: true
				,	valueField: 'value'
				,	triggerAction: 'all'
				,	allowBlank: false
			}, {
					xtype: 'textarea'
				,	itemId: 'string1'
				,	width: 200
				, height: 200
				,	fieldLabel: 'Scientific Name(s)'
			}, {
					xtype: 'checkbox'
				,	itemId: 'search_mode'
				,	fieldLabel: 'Rapid Search'
				,	checked: true
				,	inputValue: 'rapid'
			}, {
					text: 'Search'
				,	xtype: 'button'
				,	handler: this.doSearch
				,	scope: this
			}]
	});

	this.results = new Taxamatch.FlatGrid({ border: false });
	
	Ext.apply(this, config, {
			layout: 'border'
		,	id: 'tm-search-grid'
		,	hideMode: 'offsets'
		,	iconCls: 'icon-search'
		,	flag: true
		,	items: [
					this.form
				,	this.results	
			]
	});

	Taxamatch.SearchGrid.superclass.constructor.apply(this, arguments);

};

Ext.extend( Taxamatch.SearchGrid, Ext.Panel, {

		clearResults: function() {
			this.results.store.removeAll();
		}
		
	,	doSearch: function(a, b, c) {
			if (this.form.getComponent('search_mode').getValue()) {
				this.results.search_mode = 'rapid';
			}
			this.results.source = this.form.getComponent('source').getValue();
			this.results.str = this.form.getComponent('string1').getValue();
			this.results.store.load();
		}
		
}); // end of extend