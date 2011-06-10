Ext.namespace('Taxamatch');

Taxamatch.FlatGrid = function(config) {

	this.store = new Ext.data.JsonStore({
			url:  Taxamatch.APP_PATH + 'api/taxamatch.php'
		,	baseParams: {
					cmd: 'taxamatch'
				,	debug: 0
				,	layout: 'flat'
			}
		,	root: 'data'
		,	idProperty: 'name'
		,	fields: ['rank', 'match_type', 'genus', 'genus_species', 'temp_authority', 'genus_ed', 'species_ed', 'input']
		,	listeners: {
					beforeload: {
							scope: this
						,	fn: function(store, params) {
								
/*								
								if (this.form.getComponent('search_mode').getValue()) {
										search_mode = 'rapid';
								}
								this.source = this.form.getComponent('source').getValue();
								this.str = this.form.getComponent('string1').getValue();
*/
								
								Ext.apply(store.baseParams, {
										source: this.source
									,	search_mode: this.search_mode
									,	str: this.str
								});
							}
					}
			}
	});
	
	Ext.apply(this, config, {
			store: this.store
		,	title: 'Results'
		,	region: 'center'
		,	loadMask: true
		,	search_mode: ''
		,	sm: new Ext.grid.RowSelectionModel({singleSelect:true})
		,	viewConfig: {
				emptyText: 'No Results Found'
			}
		,	columns: [
					{id:'input',header: 'Input', width: 125, sortable: true, dataIndex: 'input'}
				,	{id:'rank',header: 'Rank', width: 75, sortable: true, dataIndex: 'rank'}
				,	{header: 'Type', width: 75, sortable: true, dataIndex: 'match_type', renderer: this.renderMatchType }
				,	{header: 'Genus', width: 75, sortable: true, dataIndex: 'genus'}
				,	{header: 'Species', width: 150, sortable: true, dataIndex: 'genus_species'}
				,	{header: 'Author', width: 125, sortable: true, dataIndex: 'temp_authority'}
				,	{header: 'Genus ED', width: 75, sortable: true, dataIndex: 'genus_ed'}
				,	{header: 'Species ED', width: 75, sortable: true, dataIndex: 'species_ed'}
			]
	});

	Taxamatch.FlatGrid.superclass.constructor.apply(this, arguments);

};

Ext.extend( Taxamatch.FlatGrid, Ext.grid.GridPanel, {

		clearResults: function() {
			this.store.removeAll();
		}
		
	,	doSearch: function() {
			this.store.load();
		}
		
	,	renderMatchType: function( value, a ) {

			switch( value ) {
				case 'exact':
					return("Exact");
					
				case 'phonetic':
					return("Phonetic");

				case 'near_1':
					return("Near Match");

				case 'near_2':
					return("Possible Match");

				default:
					return("Unknown");
					break;
			}
		}
}); // end of extend