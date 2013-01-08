<?php

$sql_create_gf_lookup_table="
DROP TABLE IF EXISTS `$fg`;
CREATE TABLE `$fg` (
	genus VARCHAR(255) NOT NULL,
	family VARCHAR(255) NOT NULL,
	altFamily VARCHAR(255) NOT NULL,
	isPrimary INTEGER(1) UNSIGNED DEFAULT NULL,
	INDEX(genus),
	INDEX(family),
	INDEX(altFamily),
	INDEX(isPrimary)
) ENGINE=MYISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
";

?>

