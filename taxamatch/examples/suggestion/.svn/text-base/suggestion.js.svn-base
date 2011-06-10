Ext.onReady(function() {

	var taxamatch_path = 'http://localhost/www.silverbiology.com/clients/gbif/taxamatch-webservice/api/taxamatch.php';
	var taxamatch_source = 'col2009';

	var resultTemplate = new Ext.Template('<p class="r-title"><a href="{url}">{title}</a></p><p class="r-content">{content}</p><p class="r-url">{visibleUrl}</p>');
	var resultTMTemplate = new Ext.Template('<p><a href="#">{genus_species}</a> <a href="#">{temp_authority}</a></p>');

	var updateTMResults = function(data) {
		Ext.fly('taxamatch').update('');

		// Check for exact species.
		if ( Ext.isObject( data.data.species.exact ) ){
			Ext.fly('taxamatch').update('<p class="rtm-known">This is a known species <b>' + data.data.species.exact[0].genus_species + ' ' + data.data.species.exact[0].temp_authority + '</b>.</p>');
		} else {
			console.log( data.data.species );

			// Check if Species Near 1 or 2
			if ( Ext.isObject( data.data.species.near_1 ) || Ext.isObject( data.data.species.near_2 ) ) {
	
					// Check if there are any near species
					if (Ext.isObject( data.data.species.near_1 )) {
						Ext.fly('taxamatch').update('<b>Possible Species:</b> ');
						Ext.each(data.data.species.near_1, function() {
							console.log( this );
							resultTMTemplate.append('taxamatch', this);
						});
					}
	
					// Check if there are any near species
					if (Ext.isObject( data.data.species.near_2 )) {
console.log('a');						
						Ext.get('taxamatch').update('<p><b>You might mean:</b> ');
						Ext.each(data.data.species.near_2, function() {
							console.log( this, '2' );
							resultTMTemplate.append('taxamatch', this);
						});
//						Ext.fly('taxamatch').update('</p>');
					}
	
			} else {
				if ( Ext.isObject( data.data.genus.near_1 ) ){
					Ext.each(data.data, function() {
						console.log( this );
						resultTMTemplate.append('taxamatch', this);
					});
				} else {
					if ( Ext.isObject( data.data.genus.exact ) ){
						Ext.fly('taxamatch').update('This is a known genus.');
					}
				}
			}
		}
	}

	var updateResults = function(data) {
		Ext.fly('results').update('');
		Ext.each(data.responseData.results, function() {
			resultTemplate.append('results', this);
		});
	}

	Ext.get('submit').on('click', function() {

		Ext.ux.JSONP.request('http://ajax.googleapis.com/ajax/services/search/web', {
				callbackKey: 'callback'
			,	params: {
						v: '1.0'
					, q: Ext.get('searchtext').getValue()
					, rsz: 'large'
				}
			,	callback: updateResults
		});

		Ext.ux.JSONP.request( taxamatch_path, {
				callbackKey: 'callback'
			,	params: {
						cmd: 'taxamatch'
					, debug: 0
					,	search_mode: 'rapid'
					,	source: taxamatch_source
					,	str: Ext.get('searchtext').getValue()
				}
			,	callback: updateTMResults
		});

	});
	
});