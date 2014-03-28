<?php
// functions related to parsing and converting coordinates

function decimal_coordinate($deg,$min,$sec,$lat_or_long) {
	// Converts coordinates from
	// degrees, minutes, seconds
	// to decimal format.
	// Will convert either latitude or longitude, depending on
	// value of parameter $lat_or_long.
	// Can handle character format input; will convert if numeric.
	// Can return either the converted value, false (check with ===) or null

	$lat_or_long = strtolower($lat_or_long);
	
	if ($lat_or_long=='lat' ||  $lat_or_long=='latitude') {
		$lat = true;
		$long = false;
	} else if ($lat_or_long=='long' || $lat_or_long=='lon' || $lat_or_long=='longitude') {
		$lat = false;
		$long = true;
	} else {
		return null;
	}

	
	// check degrees numeric and in range
	if ( is_null($deg) || (!(is_numeric($deg))) ) {
		return null;
	} else {
		// check degrees in range
		if ( ($lat && ($deg>90 || $deg<-90)) || ($long && ($deg>180 || $deg<-180)) ) return null;
	}

	// check minutes numeric and in range
	if ( is_null($min) || (!(is_numeric($min))) ) {
		// assume zero if degrees null
		$min=0;
	} elseif ( ($min>=60 || $min<-0) ) {
		return null;
	}

	// check seconds numeric and in range
	if ( is_null($sec) || (!(is_numeric($sec))) ) {
		// assume zero if degrees null
		$sec=0;
	} elseif ( ($sec>=60 || $sec<-0) ) {
		return null;
	}

	// finally, check that minutes + seconds do not total >60
	if ($min + $sec > 60) return null;

	// Good to go; calculate decimal coordinate
	$coord = $deg + $min/60 + $sec/3600;
	return $coord;

}

function state_name_US($state_code) {
	// Translates US two-letter state codes to full state names
	// Returns NULL if no match; be sure to add handler if 
	// need to use some other vaue for nomatch

	$state_name=array(
		'AL'=>'Alabama',
		'AK'=>'Alaska',
		'AZ'=>'Arizona',
		'AR'=>'Arkansas',
		'CA'=>'California',
		'CO'=>'Colorado',
		'CT'=>'Connecticut',
		'DE'=>'Delaware',
		'DC'=>'District of Columbia',
		'FL'=>'Florida',
		'GA'=>'Georgia',
		'HI'=>'Hawaii',
		'ID'=>'Idaho',
		'IL'=>'Illinois',
		'IN'=>'Indiana',
		'IA'=>'Iowa',
		'KS'=>'Kansas',
		'KY'=>'Kentucky',
		'LA'=>'Louisiana',
		'ME'=>'Maine',
		'MD'=>'Maryland',
		'MA'=>'Massachusetts',
		'MI'=>'Michigan',
		'MN'=>'Minnesota',
		'MS'=>'Mississippi',
		'MO'=>'Missouri',
		'MT'=>'Montana',
		'NE'=>'Nebraska',
		'NV'=>'Nevada',
		'NH'=>'New Hampshire',
		'NJ'=>'New Jersey',
		'NM'=>'New Mexico',
		'NY'=>'New York',
		'NC'=>'North Carolina',
		'ND'=>'North Dakota',
		'OH'=>'Ohio',
		'OK'=>'Oklahoma',
		'OR'=>'Oregon',
		'PA'=>'Pennsylvania',
		'RI'=>'Rhode Island',
		'SC'=>'South Carolina',
		'SD'=>'South Dakota',
		'TN'=>'Tennessee',
		'TX'=>'Texas',
		'UT'=>'Utah',
		'VT'=>'Vermont',
		'VA'=>'Virginia',
		'WA'=>'Washington',
		'WV'=>'West Virginia',
		'WI'=>'Wisconsin',
		'WY'=>'Wyoming',
		'AS'=>'American Samoa',
		'GU'=>'Guam',
		'FM'=>'Federated States of Micronesia',
		'MH'=>'Marshall Islands',
		'MP'=>'Northern Mariana Islands',
		'PW'=>'Palau',
		'PR'=>'Puerto Rico',
		'VI'=>'Virgin Islands',
		'AE'=>'Armed Forces Africa \ Canada \ Europe \ Middle East',
		'AA'=>'Armed Forces America (except Canada)',
		'AP'=>'Armed Forces Pacific'
	);
 return strtr($state_code,$state_name);
}

?>
