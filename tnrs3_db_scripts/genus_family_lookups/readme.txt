Create table `genus_family_lookup`, consisting of every possible alternative family classification 
for a given genus-family couplet. The table provides an exhaustive list of every family in which a genus has been classified, essentially capturing both current and historical familial classifications of genera. Thus for example, Ceiba (Bombacaceae) can be detect as equivalent to 
Ceiba (Malvaceae).

The original family is included to prevent erroneous transformation of homonyms in different families. For example, although the accepted use of the genus Shorea is for trees in the family Dipterocarpaceae, Shorea has also be used (as an inllegitimate posterior homonym) for a genus of Gentianaceae. Including original family in the couplet Shorea-Gentianaceae will prevent the usage referring to gentians from being transformed into the usage referring to Dipterocarps (Shorea-Dipterocarpaceae). Even though the name Shorea is nomenclaturally incorrect when applied to Gentians, the meaning (a genus of gentians) must be preserved if gentians are the intended meaning. Transormation to the corret name can be performed separately, but only if the intended meaning (gentians) is preserved.

Note that this table does not prevent the erroneous transformation of homonyms in the same family.

The current source for this table is the GRIN taxonomy database (http://www.ars-grin.gov/cgi-bin/npgs/html/index.pl). The general algorithm used involves pairing concatenated families in column OTHFAMILY in the GRIN table GENUS, and loading those families to a new table containing  genus, family, and all the possible alternate families associated with that genus+familypair (including repeating the family twice). Thus, this scripts create a crosswalk table between allhistoric genus-family pairs.

Also creates and populates table `family_acceptedFamily_lookup`, containing fully synonymous family names and their current accepted names (e.g., Compositae-->Asteraceae). For partially synonymous families, use genus_family_lookup, created by separate set of scripts.



