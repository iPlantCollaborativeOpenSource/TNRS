Ext.namespace('Taxamatch');

Taxamatch.Search = function(config) {

	var cmds = new Ext.data.SimpleStore({
			fields: ['name', 'value']
		,	data : [
					['Taxamatch', 'taxamatch']
				,	['Near Match', 'near_match']
				,	['Normalize', 'normalize']
				,	['Normalize Author', 'normalize_auth']
				,	['Compare Authors', 'compare_auth']
				,	['Treat Word', 'treat_word']
				,	['nGram', 'ngram']
			]
	});

	var sources = new Ext.data.JsonStore({
			url: '../api/taxamatch.php'
		,	baseParams: { cmd: 'sources' }
		,	root: 'data'
		,	fields: ['name', 'value']
		,	autoLoad: true
	});

	this.form = new Ext.form.FormPanel({
			id: 'searchform'
		,	title: 'Filters'
		,	split: true
		,	border: false
		,	region: 'west'
		,	autoScroll: true
		,	width: 250
    , labelWidth: 75
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
					store: cmds
				,	xtype: 'combo'
				,	id: 'cmd'
				,	displayField: 'name'
				,	typeAhead: true
				,	mode: 'local'
				,	fieldLabel: 'Command'
				, forceSelection: true
				,	readOnly: true
				,	valueField: 'value'
				,	triggerAction: 'all'
				,	listeners: {
						 select: this.changeView
					}
			}, {
					store: sources
				,	xtype: 'combo'
				,	id: 'source'
				,	displayField: 'name'
				,	typeAhead: true
				,	mode: 'local'
				,	fieldLabel: 'Data Source'
				, forceSelection: true
				,	readOnly: true
				,	valueField: 'value'
				,	triggerAction: 'all'
			}, {
					xtype: 'textfield'
				,	id: 'string1'
				,	fieldLabel: 'Value'
			}, {
					xtype: 'textfield'
				,	id: 'string2'
				,	hidden: false
				,	fieldLabel: 'Value 2'
			}, {
					xtype: 'checkbox'
				,	id: 'search_mode'
				,	fieldLabel: 'Rapid Search'
				,	checked: true
			}, {
					xtype: 'checkbox'
				,	id: 'debug'
				,	fieldLabel: 'Debug'
			}, {
					text: 'Search'
				,	xtype: 'button'
				,	handler: this.doSearch
				,	scope: this
			}]
	});

	this.form.findById('cmd').setValue('taxamatch');
	
	this.results = new Ext.Panel({
			id: 'results'
		,	region: 'center'
		,	title: 'Results'
		,	autoScroll: true
		,	border: false
		,	bodyStyle: 'padding: 10px;'
	});
	
	Ext.apply(this, config, {
			layout: 'border'
		,	id: 'tm-search'
		,	hideMode: 'offsets'
		,	iconCls: 'icon-search'
		,	flag: true
		,	items: [
					this.form
				,	this.results	
			]
		,	listeners: {
				show: function() {
					if (this.flag) {

						var task = new Ext.util.DelayedTask(function(){
							Ext.getCmp('string2').container.up('div.x-form-item').hide();
							Ext.getCmp('string2').container.up('div.x-form-item').setStyle('display','none');					
						});
						task.delay(200); 
						this.flag = false;
					}
				}
			}
	});

	Taxamatch.Search.superclass.constructor.apply(this, arguments);

};

Ext.extend( Taxamatch.Search, Ext.Panel, {

		clearResults: function() {
			Ext.getCmp('results').body.update("");
		}
		
	,	doSearch: function() {
			
			var cmd = 'normalize';
			var debug = 0;

			if (Ext.getCmp('debug').getValue()) {
				debug = 1;
			}

			if (Ext.getCmp('search_mode').getValue()) {
					var search_mode = 'rapid';
			}
			var params = {};
			Ext.apply( params, {
					cmd: Ext.getCmp('cmd').getValue()
				,	str: Ext.getCmp('string1').getValue()
				,	str2: Ext.getCmp('string2').getValue()
				,	source: Ext.getCmp('source').getValue()
				,	search_mode: search_mode
				,	debug: debug
			});

			this.process( params );
		}
	
	,	process: function( params ) {

			Ext.Ajax.request({
					url: '../api/taxamatch.php'
				,	scope: this
				,	success: this.success
				,	failure: this.error
				,	params: params
			});

		}

	,	success: function( response, ref ) {

			var obj = Ext.decode(response.responseText);
			
			if (obj.success) {
				switch( ref.params.cmd ) {
						case 'normalize':
						
							var tpl = new Ext.XTemplate(
									'<h1>Normalize</h1>'
								,	'<p>Input: <b>{str1}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, data: obj.data } );
							
							break;

						case 'normalize_auth':
						
							var tpl = new Ext.XTemplate(
									'<h1>Normalize Author</h1>'
								,	'<p>Author Input: <b>{str1}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, data: obj.data } );
							
							break;

						case 'treat_word':
						
							var tpl = new Ext.XTemplate(
									'<h1>Treat Word</h1>'
								,	'<p>Input: <b>{str1}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, data: obj.data } );
							
							break;

						case 'near_match':
						
							var tpl = new Ext.XTemplate(
									'<h1>Near Match</h1>'
								,	'<p>Input: <b>{str1}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, data: obj.data } );
							
							break;
							
						case 'compare_auth':
						
							var tpl = new Ext.XTemplate(
									'<h1>Compare Authors</h1>'
								,	'<p>Author 1: <b>{str1}</b></p>'
								,	'<p>Author 2: <b>{str2}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, str2: ref.params.str2, data: obj.data } );
							
							break;

						case 'treat_word':
						
							var tpl = new Ext.XTemplate(
									'<h1>Treat Word</h1>'
								,	'<p>Source: <b>{str1}</b></p>'
								,	'<p>Target: <b>{str2}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, str2: ref.params.str2, data: obj.data } );
							
							break;

						case 'ngram':
						
							var tpl = new Ext.XTemplate(
									'<h1>nGram</h1>'
								,	'<p>Source: <b>{str1}</b></p>'
								,	'<p>Target: <b>{str2}</b></p>'
								,	'<p class="result">Result: {data}</p>'
								,	'<hr size="1" noshade="noshade" />'
							);
							tpl.insertFirst(Ext.getCmp('results').body, { str1: ref.params.str, str2: ref.params.str2, data: obj.data } );
							
							break;

						case 'taxamatch':
						
							var tpl = new Ext.XTemplate(
									'<h1>Taxamatch</h1>'
								,	'<p>Input: <b>{input}</b></p>'
								,	'<p>Duration: <b>{duration}</b> seconds.</p>'
								
								,	'<tpl if="this.exists(values, \'genus\')">'
									,	'<div class="result">'
									,	'<p><b>Genus Results:</b></p>'
									,	'<tpl for="genus">'
									
										,	'<tpl if="this.exists(values, \'exact\')">'
											,	'<p><b>Exact Match:</b></p>'
											,	'<tpl for="exact">'
												,	'<div class="result exact" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Genus: {genus}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Genus Id: {genus_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'phonetic\')">'
											,	'<p><b>Phonetic Matches:</b></p>'
											,	'<tpl for="phonetic">'
												,	'<div class="result phonetic" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Genus: {genus}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Genus Id: {genus_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'near_1\')">'
											,	'<p><b>Near Matches:</b></p>'
											,	'<tpl for="near_1">'
												,	'<div class="result near_1" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Genus: {genus}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Genus Id: {genus_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'near_2\')">'
											,	'<p><b>Near Matches 2:</b></p>'
											,	'<tpl for="near_2">'
												,	'<div class="result near_2" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Genus: {genus}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Genus Id: {genus_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

									,	'</tpl>'
									,	'</div>'
								,	'</tpl>'
								
								,	'<tpl if="this.exists(values, \'species\')">'
									,	'<div class="result">'
									,	'<p><b>Species Results:</b></p>'
									,	'<tpl for="species">'
									
										,	'<tpl if="this.exists(values, \'exact\')">'
											,	'<p><b>Exact Match:</b></p>'
											,	'<tpl for="exact">'
												,	'<div class="result exact" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Species: {genus_species}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Species Id: {species_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'phonetic\')">'
											,	'<p><b>Phonetic Matches:</b></p>'
											,	'<tpl for="phonetic">'
												,	'<div class="result phonetic" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Species: {genus_species}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Species Id: {species_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'near_1\')">'
											,	'<p><b>Near Matches:</b></p>'
											,	'<tpl for="near_1">'
												,	'<div class="result near_1" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Species: {genus_species}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Species Id: {species_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

										,	'<tpl if="this.exists(values, \'near_2\')">'
											,	'<p><b>Near Matches 2:</b></p>'
											,	'<tpl for="near_2">'
												,	'<div class="result near_2" style="margin-left: 15px; padding-left: 5px;">'
												,	'<p>Species: {genus_species}</p>'
												,	'<p>Authority: {temp_authority}</p>'
												,	'<p>Species Id: {species_id}</p>'
												,	'</div>'
											,	'</tpl>'
										,	'</tpl>'

									,	'</tpl>'
									,	'</div>'
								,	'</tpl>'
								
								,	'<hr size="1" noshade="noshade" />'
							);
							if(window.console && window.console.firebug) {
								console.log('Data: ', obj.data);
							}
							obj.data.duration = obj.duration;
							tpl.insertFirst(Ext.getCmp('results').body, obj.data );
							
							break;

				}
				
				if (ref.params.debug) {
					if(window.console && window.console.firebug) {
						console.log('Debug: ', ref.params, obj.debug );
					}
				}
			} else {
				Ext.getCmp('results').body.update('Error');
			}
			
		}
	
	,	error: function(response, ref) {
			if (window.console && window.console.firebug) {
				console.log('Error: ', response, ref);
			}
		}
		
	,	changeView: function( cb, store, index) {
			
			switch( cb.getValue() ) {
				
					case 'ngram':
					case 'normmalize_auth':
					case 'compare_auth':
						Ext.getCmp('string2').container.up('div.x-form-item').show();
						Ext.getCmp('string2').container.up('div.x-form-item').setStyle('display','');
						break;
						
					case 'taxamatch':
					case 'near_match':
					case 'normalize':
					case 'treat_word':
						Ext.getCmp('string2').container.up('div.x-form-item').hide();
						Ext.getCmp('string2').container.up('div.x-form-item').setStyle('display','none');
						break;
			}
			
		}
		
}); // end of extend