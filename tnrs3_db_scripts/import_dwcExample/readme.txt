Imports template for modified Darwin Core format taxonomic data

Use this template to import a taxonomic source in a modified Darwin Core (DwC) format. 
Most data elements and their names are identical to DwC Taxon terms, as listed at 
http://rs.tdwg.org/dwc/terms/index.htm. A few important terms not included in the current
version of DwC have been added; these are indicated by an asterisk (*). This format supports
most types of taxonomic names with the exception of quadrinomials (for example,
Ptelea trifoliata ssp. pallida var. cognata). 

Taxonomic file format

To use this import template, you taxonomic source data must match the format of the 
example file "dwcExample.csv" in the directory data/. In particular, it must have the
following columns in the order shown:

taxonID				Required. Unique identifier for each taxon (name). Can be character 
					or numeric. MUST be unique for each record. 
parentNameUsageID	Link to identifier (taxonID) of most proximate parent taxon. Required
					for any taxon that has a parent. Taxa without a parent will be linked
					to the root. Orphan parentNameUsageIDs (linking to nonexistent taxonIDs)
					will be linked to the root. WARNING: circular parentNameUsageID-taxonID
					links will cause database loading to fail.
taxonRank			Required. Differs slight from DwC specification in that this is the 
					fully spelled (not abbreviated) English name of the rank of the taxon. 
					You MUST use the following controlled vocabulary (similar to 
					http://code.google.com/p/darwincore/wiki/Taxon#taxonRank, under 
					column "English", except "forma" instead of "form", more
					extensive vocabulary): kingdom, subkingdom, 
					superdivision, division, subdivision, superclass, class, subclass, 
					order, suborder, family, subfamily, tribe, subtribe, section, 
					subsection, series, subseries, genus, subgenus, species, subspecies,
					variety, subvariety, forma, subforma, cultivar, unranked
family				Optional but recommended. 
scientificName		Required. The fully formed scientific name, without the authority.
scientificNameAuthorship	Optional but recommended. This is the terminal authority of the
					taxon. Please do not include any internal authorities (for example, if 
					the name is a subspecies, this should be the authority for the subspecies,
					not the species. Required if your list contains homonyms (duplicate names 
					disambiguated only by authority).
genusHybridMarker	*Optional but recommended. Leave blank if not a hybrid name. The TNRS 
					loading scripts will detect most hybrids.
genus				Required if taxon at rank of genus or lower.
speciesHybridMarker	*Optional but recommended. Leave blank if not a hybrid name. The TNRS 
					loading scripts will detect most hybrids.
specificEpithet		Required if taxon at rank of species or lower. Just the epithet; do not
					include the genus portion of a species name. Leave blank for hybrid
					formula names (e.g., Adiantum latifolium x petiolatum).
infraspecificRank	*Required if name is an infraspecific trinomial (e.g., 
					"Poa annua var. supina"). This is the abbreviated rank indicator 
					included in names of subspecies, varieties, etc. Not directly 
					equivalent to any Darwin Core term. Controlled vocabulary as per 
					abbreviations as used in the ICBN Vienna code 
					(http://ibot.sav.sk/icbn/main.htm; see esp. Section 5): 
					"subsp.", "var.", "subvar.", "fo.", "subfo.", "cv.".
					The TNRS will attempt to parse this value from the scientificName if 
					column infraspecificRank is left blank. The TNRS has an extensive 
					library of infraspecific rank indicators and their variants and will 
					attempt to standardize any variant abbreviations. Leave blank if taxon
					is at rank of species or higher. 
infraspecificEpithet	Required if taxon is subspecies, variety, forma, etc.  Tribes and 
					subgeneric taxa such as section and series 
					(e.g., Psychotria sect. Notopleura) can be entered by leaving 
					specificEpithet NULL, entering the rank indicator in infraspecificRank
					and epithet infraspecificEpithet.
infraspecificRank2	*Required if name is an infraspecific quadrinomial (e.g., 
					"Silene laciniata ssp. major var. angustifolia"). These will be taxa of
					ranks variety, forma, subforma, etc. Not directly 
					equivalent to any Darwin Core term. Controlled vocabulary as per 
					abbreviations as used in the ICBN Vienna code 
					(http://ibot.sav.sk/icbn/main.htm; see esp. Section 5): 
					"var.", "subvar.", "fo.", "subfo.", "cv.". 
infraspecificEpithet2	*Required if taxon is an infraspecific quadrinomial (see 
					infraspecificRank2, above). Not directly equivalent to any Darwin Core
					term. If used, infraspecificRank2 as well as all higher taxonomic name 
					component fields must be populated.
taxonomicStatus		Indications of simple taxonomic status. If name is a synonym, more detailed
					reason for (nomenclaturally) invalid or illegitimate names can be used. 
					If left NULL, TNRS will treat name as "No opinion". Current controlled 
					vocabulary supported: 
					"Accepted", "Synonym", "Invalid", "Illegitimate", NULL.
acceptedNameUsageID	Optional but recommended if taxonomicStatus="Synonym", "Invalid", or 
					"Illegitimate". Link to taxonID of accepted name. 
taxonUri			Optional. Hyperlink to name in original database.
lsid				Optional. LSID, if known. If you don't know what this is, don't worry
					about it.

If your taxonomic source list contains only concatenated names, use the TNRS in parse-only 
mode to break the names into their atomic components (http://tnrs.iplantcollaborative.org/TNRSapp.html).

To prepare your file for import:

1. Choose a unique short name for this data source. For example, "mySource". No spaces.
2. Copy this directory and its contents to a new directory, import_[short source name]. 
	For example, "import_mySource".
3. Format your raw taxonomic data file to match the example file dwcExample.csv in the 
	directory data/. (For details, see "Taxonomic file format", above).
4. Place the file in the data directory.
5. Edit params.inc, making the following mandatory changes:
	(a) $sourceName="[short source name]". For example, $sourceName="mySource";
	(b) $is_higher_classification="false" [set to true only if you wish the families
		of this taxonomic source to be used as a family classification for all other
		sources in the database.]
	(c) $filepath="import_[short source name]/data/". For example $filepath="import_dwcExample/data/";
	(d) $namesfile = "[taxonomic source file name]"; Use the name and extension of the taxonomic
		data file you placed in folder data/.
	(e) Make sure the "MySQL LOAD DATA INFILE parameters" are appropriate for the format of 
		your file (for more information, see 
6. If you are import other taxonomic source in darwin core format, prepare a separate import
	directory for each one.	
	
To build the database:

1. Edit global_params.inc, making the following changes:
	(a) Add your source name(s) to the array $src_array. For example,

		$src_array=array(
			"mySource"
		);
		
	(b) Set any other parameters as needed, in particular $replace_db and all parameters
		in section "Db connection info".
		
2. Run the script "load_tnrs.php"

Note: the source names in array $src_array (in global_params.inc) MUST match the values
of $sourceName in params.inc for each source AND the suffix of each import directory.
		


