# You must have function strSplit() available to your database to run TNRS scripts
# Use this script to create it.
CREATE FUNCTION `strSplit`(str varchar(255), delim varchar(12), tokenNo int)  RETURNS varchar(255) CHARSET utf8
COMMENT 'Splits the string (str) at all delimiters (delim) and returns token number tokenNo'
RETURN replace(SUBSTRING(SUBSTRING_INDEX(str, delim, tokenNo), LENGTH(SUBSTRING_INDEX(str, delim, tokenNo - 1)) + 1), delim, '');
