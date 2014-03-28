Custom fixes for taxonomic source Tropicos

Created by: Brad Boyle (bboyle@email.arizona.edu; ojalaquellueva@gmail.com)
Date created: 2013-04-12
Last modified: 2014-03-27

These scripts are NOT part of the TNRS pipeline. They are meant to be run separately, after building the TNRS database. 

These fixes specific to taxonomic source Tropicos. They fix two issues resulting from errors in the Tropicos ComputedAcceptance algorithm: (1) names with missing link to an accepted name, (2) species (and infraspecific taxa) belonging to an invalid or otherwise non-accepted genus but erroneously labeled as "Accepted". Ultimately, these fixes can be omitted if and when ComputedAcceptance is fixed

Fix (1) adds additional acceptedNameID links to some names reported as "No opinion" by ComputedAcceptance. This fix required a download of additional accepted nameIDs; this download can only be obtained by querying Tropicos from within the Tropicos firewall (in general only available to MBG curators). The user submits a list of tropicos IDs (nameIDs) of names for which the Tropicos API ComputedAcceptance call (wrongly) returns "No opinion". Tropicos then returns the same list of nameIDs, along with accepted nameIDs, if available. The GUI has a limit on the number of names that can be submitted at a time, so the file of name IDs needs to be cut into chunks before submission, and the results re-concatenated into a single text file. If you do not have this download file, you can skip this step by commenting out the relevant include commands in the main scripts (tropicos_fixes.php).

Fix (2) is an algorithmic fix for the failure of ComputedAcceptance to transfer the status of non-accepted genera to its child taxa. For example, although ComputedAcceptance correctly reports that the genus Hyeronima is a synonym (actually, an orthographic variant) of Hieronyma, some species of Hyeronima are reported as accepted. Fix (2) addresses this problem by (a) ensuring that child taxa inherit the taxonomic status of an invalid or illegitimate genus, and (b) populating the link to the accepted name with the correctly spelled genus, when this can be safely inferred.

Main script: tropicos_fixes.php (calls all others)

Parameters: Ensure that all parameters in params.inc are correct. This script does NOT use global_params.inc. All parameters must be specified in the local params.inc.