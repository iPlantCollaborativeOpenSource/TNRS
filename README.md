Taxonomic Name Resolution Service (TNRS)
========================================

## INTRODUCTION
This repo includes all of the code that powers iPlant's Taxonomic 
Name Resolution Service.

The TNRS is described in:

[http://dx.doi.org/10.1186/1471-2105-14-16](Boyle, B. et.al. The taxonomic name resolution service: an online tool for automated standardization of plant names. BMC Bioinformatics. 2013, 14:16.)

Parts of this code come from other projects, as noted below in 
LICENSE. The rest was written by iPlant.

More information about iPlant's Open Source policies and current 
status is available at 
http://iplantcollaborative.org/opensource


## LICENSE
All code, except where noted below, is released as described in the 
LICENSE.md file included with this code.

Exceptions are biodiversity (BSD) and taxamatch (Apache 2.0), which
are open source projects that we modified and are releasing here with
our changes. The licenses for these are included in the LICENSE files
in the respective directories.


## SOURCE
Source code is available at: https://github.com/iPlantCollaborativeOpenSource/TNRS


## INSTALLATION
For instructions, see INSTALL.md.


## COMMON ISSUES/BUGS/QUESTIONS
* Java Heap Size
The TNRS uses Java's default mechanism to define the maximum amount of memory accessible to its services. This might be too low for certain appications or dedicated servers, and can be changed in TNRS/scripts/batch_start.sh and TNRS/scripts/services_start.sh.
 
* Use the issue tracker available from the GitHub repository at the link listed under SOURCE above. Click Issues to submit a question or report a problem.


## CONTRIBUTING
iPlant requires all outside collaborators (those not employed by iPlant) to sign and submit a Contributor License Agreement. Details as well as answers to Frequently Asked Questions about this agreement are available at http://www.iplantcollaborative.org/opensource/CLA.

Other steps in the process are still being created. While this message remains, email opensource@iplantcollaborative.org
