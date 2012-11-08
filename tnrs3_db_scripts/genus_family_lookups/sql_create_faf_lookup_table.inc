<?php

$sql_create_faf_lookup_table="
DROP TABLE IF EXISTS `$faf`;
CREATE TABLE `$faf` (
	family VARCHAR(255) NOT NULL,
	acceptedFamily VARCHAR(255) NOT NULL,
	INDEX(family),
	INDEX(acceptedFamily)
) ENGINE=MYISAM DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
";

?>