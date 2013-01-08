<?php

/*
Copyright (c) 2005, Costin Bereveanu (costin@bluewallllc.com)
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions 
are met:
* Redistributions of source code must retain the above copyright notice, this 
list of conditions and the following disclaimer. 
* Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution. 
* Neither the name of the Black Art Software nor the names of its contributors
may be used to endorse or promote products derived from this software without 
specific prior written permission. 

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE 
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, 
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE 
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE
*/

/**
* MysqliDatabase, MysqliResult
* @package Utilities
**/

define('MYSQL_TYPE_DECIMAL',		0);
define('MYSQL_TYPE_TINY',			1);
define('MYSQL_TYPE_SHORT',			2);
define('MYSQL_TYPE_LONG',			3);
define('MYSQL_TYPE_FLOAT',			4);
define('MYSQL_TYPE_DOUBLE',			5);
define('MYSQL_TYPE_NULL',			6);
define('MYSQL_TYPE_TIMESTAMP',		7);
define('MYSQL_TYPE_LONGLONG',		8);
define('MYSQL_TYPE_INT24',			9);
define('MYSQL_TYPE_DATE',			10);
define('MYSQL_TYPE_TIME',			11);
define('MYSQL_TYPE_DATETIME',		12);
define('MYSQL_TYPE_YEAR',			13);
define('MYSQL_TYPE_NEWDATE',		14);
define('MYSQL_TYPE_ENUM',			247);
define('MYSQL_TYPE_SET',			248);
define('MYSQL_TYPE_TINY_BLOB',		249);
define('MYSQL_TYPE_MEDIUM_BLOB',	250);
define('MYSQL_TYPE_LONG_BLOB',		251);
define('MYSQL_TYPE_BLOB',			252);
define('MYSQL_TYPE_VAR_STRING',		253);
define('MYSQL_TYPE_STRING',			254);
define('MYSQL_TYPE_GEOMETRY',		255);

$MYSQL2PHP_TYPE_INT = array(MYSQL_TYPE_TINY, MYSQL_TYPE_SHORT, MYSQL_TYPE_LONG, MYSQL_TYPE_LONGLONG, MYSQL_TYPE_INT24, MYSQL_TYPE_YEAR);
$MYSQL2PHP_TYPE_FLOAT = array(MYSQL_TYPE_DECIMAL, MYSQL_TYPE_FLOAT, MYSQL_TYPE_DOUBLE);
$MYSQL2PHP_TYPE_STRING = array(MYSQL_TYPE_TINY_BLOB, MYSQL_TYPE_MEDIUM_BLOB, MYSQL_TYPE_LONG_BLOB, MYSQL_TYPE_BLOB, MYSQL_TYPE_VAR_STRING, MYSQL_TYPE_STRING, MYSQL_TYPE_GEOMETRY, MYSQL_TYPE_SET, MYSQL_TYPE_ENUM, MYSQL_TYPE_DATE, MYSQL_TYPE_TIME, MYSQL_TYPE_DATETIME, MYSQL_TYPE_NEWDATE);
$MYSQL2PHP_TYPE_NULL = array(MYSQL_TYPE_NULL);

// error codes
define('MYSQL_ER_HASHCHK', 1000);
define('MYSQL_ER_NISAMCHK', 1001);
define('MYSQL_ER_NO', 1002);
define('MYSQL_ER_YES', 1003);
define('MYSQL_ER_CANT_CREATE_FILE', 1004);
define('MYSQL_ER_CANT_CREATE_TABLE', 1005);
define('MYSQL_ER_CANT_CREATE_DB', 1006);
define('MYSQL_ER_DB_CREATE_EXISTS', 1007);
define('MYSQL_ER_DB_DROP_EXISTS', 1008);
define('MYSQL_ER_DB_DROP_DELETE', 1009);
define('MYSQL_ER_DB_DROP_RMDIR', 1010);
define('MYSQL_ER_CANT_DELETE_FILE', 1011);
define('MYSQL_ER_CANT_FIND_SYSTEM_REC', 1012);
define('MYSQL_ER_CANT_GET_STAT', 1013);
define('MYSQL_ER_CANT_GET_WD', 1014);
define('MYSQL_ER_CANT_LOCK', 1015);
define('MYSQL_ER_CANT_OPEN_FILE', 1016);
define('MYSQL_ER_FILE_NOT_FOUND', 1017);
define('MYSQL_ER_CANT_READ_DIR', 1018);
define('MYSQL_ER_CANT_SET_WD', 1019);
define('MYSQL_ER_CHECKREAD', 1020);
define('MYSQL_ER_DISK_FULL', 1021);
define('MYSQL_ER_DUP_KEY', 1022);
define('MYSQL_ER_ERROR_ON_CLOSE', 1023);
define('MYSQL_ER_ERROR_ON_READ', 1024);
define('MYSQL_ER_ERROR_ON_RENAME', 1025);
define('MYSQL_ER_ERROR_ON_WRITE', 1026);
define('MYSQL_ER_FILE_USED', 1027);
define('MYSQL_ER_FILSORT_ABORT', 1028);
define('MYSQL_ER_FORM_NOT_FOUND', 1029);
define('MYSQL_ER_GET_ERRNO', 1030);
define('MYSQL_ER_ILLEGAL_HA', 1031);
define('MYSQL_ER_KEY_NOT_FOUND', 1032);
define('MYSQL_ER_NOT_FORM_FILE', 1033);
define('MYSQL_ER_NOT_KEYFILE', 1034);
define('MYSQL_ER_OLD_KEYFILE', 1035);
define('MYSQL_ER_OPEN_AS_READONLY', 1036);
define('MYSQL_ER_OUTOFMEMORY', 1037);
define('MYSQL_ER_OUT_OF_SORTMEMORY', 1038);
define('MYSQL_ER_UNEXPECTED_EOF', 1039);
define('MYSQL_ER_CON_COUNT_ERROR', 1040);
define('MYSQL_ER_OUT_OF_RESOURCES', 1041);
define('MYSQL_ER_BAD_HOST_ERROR', 1042);
define('MYSQL_ER_HANDSHAKE_ERROR', 1043);
define('MYSQL_ER_DBACCESS_DENIED_ERROR', 1044);
define('MYSQL_ER_ACCESS_DENIED_ERROR', 1045);
define('MYSQL_ER_NO_DB_ERROR', 1046);
define('MYSQL_ER_UNKNOWN_COM_ERROR', 1047);
define('MYSQL_ER_BAD_NULL_ERROR', 1048);
define('MYSQL_ER_BAD_DB_ERROR', 1049);
define('MYSQL_ER_TABLE_EXISTS_ERROR', 1050);
define('MYSQL_ER_BAD_TABLE_ERROR', 1051);
define('MYSQL_ER_NON_UNIQ_ERROR', 1052);
define('MYSQL_ER_SERVER_SHUTDOWN', 1053);
define('MYSQL_ER_BAD_FIELD_ERROR', 1054);
define('MYSQL_ER_WRONG_FIELD_WITH_GROUP', 1055);
define('MYSQL_ER_WRONG_GROUP_FIELD', 1056);
define('MYSQL_ER_WRONG_SUM_SELECT', 1057);
define('MYSQL_ER_WRONG_VALUE_COUNT', 1058);
define('MYSQL_ER_TOO_LONG_IDENT', 1059);
define('MYSQL_ER_DUP_FIELDNAME', 1060);
define('MYSQL_ER_DUP_KEYNAME', 1061);
define('MYSQL_ER_DUP_ENTRY', 1062);
define('MYSQL_ER_WRONG_FIELD_SPEC', 1063);
define('MYSQL_ER_PARSE_ERROR', 1064);
define('MYSQL_ER_EMPTY_QUERY', 1065);
define('MYSQL_ER_NONUNIQ_TABLE', 1066);
define('MYSQL_ER_INVALID_DEFAULT', 1067);
define('MYSQL_ER_MULTIPLE_PRI_KEY', 1068);
define('MYSQL_ER_TOO_MANY_KEYS', 1069);
define('MYSQL_ER_TOO_MANY_KEY_PARTS', 1070);
define('MYSQL_ER_TOO_LONG_KEY', 1071);
define('MYSQL_ER_KEY_COLUMN_DOES_NOT_EXITS', 1072);
define('MYSQL_ER_BLOB_USED_AS_KEY', 1073);
define('MYSQL_ER_TOO_BIG_FIELDLENGTH', 1074);
define('MYSQL_ER_WRONG_AUTO_KEY', 1075);
define('MYSQL_ER_READY', 1076);
define('MYSQL_ER_NORMAL_SHUTDOWN', 1077);
define('MYSQL_ER_GOT_SIGNAL', 1078);
define('MYSQL_ER_SHUTDOWN_COMPLETE', 1079);
define('MYSQL_ER_FORCING_CLOSE', 1080);
define('MYSQL_ER_IPSOCK_ERROR', 1081);
define('MYSQL_ER_NO_SUCH_INDEX', 1082);
define('MYSQL_ER_WRONG_FIELD_TERMINATORS', 1083);
define('MYSQL_ER_BLOBS_AND_NO_TERMINATED', 1084);
define('MYSQL_ER_TEXTFILE_NOT_READABLE', 1085);
define('MYSQL_ER_FILE_EXISTS_ERROR', 1086);
define('MYSQL_ER_LOAD_INFO', 1087);
define('MYSQL_ER_ALTER_INFO', 1088);
define('MYSQL_ER_WRONG_SUB_KEY', 1089);
define('MYSQL_ER_CANT_REMOVE_ALL_FIELDS', 1090);
define('MYSQL_ER_CANT_DROP_FIELD_OR_KEY', 1091);
define('MYSQL_ER_INSERT_INFO', 1092);
define('MYSQL_ER_UPDATE_TABLE_USED', 1093);
define('MYSQL_ER_NO_SUCH_THREAD', 1094);
define('MYSQL_ER_KILL_DENIED_ERROR', 1095);
define('MYSQL_ER_NO_TABLES_USED', 1096);
define('MYSQL_ER_TOO_BIG_SET', 1097);
define('MYSQL_ER_NO_UNIQUE_LOGFILE', 1098);
define('MYSQL_ER_TABLE_NOT_LOCKED_FOR_WRITE', 1099);
define('MYSQL_ER_TABLE_NOT_LOCKED', 1100);
define('MYSQL_ER_BLOB_CANT_HAVE_DEFAULT', 1101);
define('MYSQL_ER_WRONG_DB_NAME', 1102);
define('MYSQL_ER_WRONG_TABLE_NAME', 1103);
define('MYSQL_ER_TOO_BIG_SELECT', 1104);
define('MYSQL_ER_UNKNOWN_ERROR', 1105);
define('MYSQL_ER_UNKNOWN_PROCEDURE', 1106);
define('MYSQL_ER_WRONG_PARAMCOUNT_TO_PROCEDURE', 1107);
define('MYSQL_ER_WRONG_PARAMETERS_TO_PROCEDURE', 1108);
define('MYSQL_ER_UNKNOWN_TABLE', 1109);
define('MYSQL_ER_FIELD_SPECIFIED_TWICE', 1110);
define('MYSQL_ER_INVALID_GROUP_FUNC_USE', 1111);
define('MYSQL_ER_UNSUPPORTED_EXTENSION', 1112);
define('MYSQL_ER_TABLE_MUST_HAVE_COLUMNS', 1113);
define('MYSQL_ER_RECORD_FILE_FULL', 1114);
define('MYSQL_ER_UNKNOWN_CHARACTER_SET', 1115);
define('MYSQL_ER_TOO_MANY_TABLES', 1116);
define('MYSQL_ER_TOO_MANY_FIELDS', 1117);
define('MYSQL_ER_TOO_BIG_ROWSIZE', 1118);
define('MYSQL_ER_STACK_OVERRUN', 1119);
define('MYSQL_ER_WRONG_OUTER_JOIN', 1120);
define('MYSQL_ER_NULL_COLUMN_IN_INDEX', 1121);
define('MYSQL_ER_CANT_FIND_UDF', 1122);
define('MYSQL_ER_CANT_INITIALIZE_UDF', 1123);
define('MYSQL_ER_UDF_NO_PATHS', 1124);
define('MYSQL_ER_UDF_EXISTS', 1125);
define('MYSQL_ER_CANT_OPEN_LIBRARY', 1126);
define('MYSQL_ER_CANT_FIND_DL_ENTRY', 1127);
define('MYSQL_ER_FUNCTION_NOT_DEFINED', 1128);
define('MYSQL_ER_HOST_IS_BLOCKED', 1129);
define('MYSQL_ER_HOST_NOT_PRIVILEGED', 1130);
define('MYSQL_ER_PASSWORD_ANONYMOUS_USER', 1131);
define('MYSQL_ER_PASSWORD_NOT_ALLOWED', 1132);
define('MYSQL_ER_PASSWORD_NO_MATCH', 1133);
define('MYSQL_ER_UPDATE_INFO', 1134);
define('MYSQL_ER_CANT_CREATE_THREAD', 1135);
define('MYSQL_ER_WRONG_VALUE_COUNT_ON_ROW', 1136);
define('MYSQL_ER_CANT_REOPEN_TABLE', 1137);
define('MYSQL_ER_INVALID_USE_OF_NULL', 1138);
define('MYSQL_ER_REGEXP_ERROR', 1139);
define('MYSQL_ER_MIX_OF_GROUP_FUNC_AND_FIELDS', 1140);
define('MYSQL_ER_NONEXISTING_GRANT', 1141);
define('MYSQL_ER_TABLEACCESS_DENIED_ERROR', 1142);
define('MYSQL_ER_COLUMNACCESS_DENIED_ERROR', 1143);
define('MYSQL_ER_ILLEGAL_GRANT_FOR_TABLE', 1144);
define('MYSQL_ER_GRANT_WRONG_HOST_OR_USER', 1145);
define('MYSQL_ER_NO_SUCH_TABLE', 1146);
define('MYSQL_ER_NONEXISTING_TABLE_GRANT', 1147);
define('MYSQL_ER_NOT_ALLOWED_COMMAND', 1148);
define('MYSQL_ER_SYNTAX_ERROR', 1149);
define('MYSQL_ER_DELAYED_CANT_CHANGE_LOCK', 1150);
define('MYSQL_ER_TOO_MANY_DELAYED_THREADS', 1151);
define('MYSQL_ER_ABORTING_CONNECTION', 1152);
define('MYSQL_ER_NET_PACKET_TOO_LARGE', 1153);
define('MYSQL_ER_NET_READ_ERROR_FROM_PIPE', 1154);
define('MYSQL_ER_NET_FCNTL_ERROR', 1155);
define('MYSQL_ER_NET_PACKETS_OUT_OF_ORDER', 1156);
define('MYSQL_ER_NET_UNCOMPRESS_ERROR', 1157);
define('MYSQL_ER_NET_READ_ERROR', 1158);
define('MYSQL_ER_NET_READ_INTERRUPTED', 1159);
define('MYSQL_ER_NET_ERROR_ON_WRITE', 1160);
define('MYSQL_ER_NET_WRITE_INTERRUPTED', 1161);
define('MYSQL_ER_TOO_LONG_STRING', 1162);
define('MYSQL_ER_TABLE_CANT_HANDLE_BLOB', 1163);
define('MYSQL_ER_TABLE_CANT_HANDLE_AUTO_INCREMENT', 1164);
define('MYSQL_ER_DELAYED_INSERT_TABLE_LOCKED', 1165);
define('MYSQL_ER_WRONG_COLUMN_NAME', 1166);
define('MYSQL_ER_WRONG_KEY_COLUMN', 1167);
define('MYSQL_ER_WRONG_MRG_TABLE', 1168);
define('MYSQL_ER_DUP_UNIQUE', 1169);
define('MYSQL_ER_BLOB_KEY_WITHOUT_LENGTH', 1170);
define('MYSQL_ER_PRIMARY_CANT_HAVE_NULL', 1171);
define('MYSQL_ER_TOO_MANY_ROWS', 1172);
define('MYSQL_ER_REQUIRES_PRIMARY_KEY', 1173);
define('MYSQL_ER_NO_RAID_COMPILED', 1174);
define('MYSQL_ER_UPDATE_WITHOUT_KEY_IN_SAFE_MODE', 1175);
define('MYSQL_ER_KEY_DOES_NOT_EXITS', 1176);
define('MYSQL_ER_CHECK_NO_SUCH_TABLE', 1177);
define('MYSQL_ER_CHECK_NOT_IMPLEMENTED', 1178);
define('MYSQL_ER_CANT_DO_THIS_DURING_AN_TRANSACTION', 1179);
define('MYSQL_ER_ERROR_DURING_COMMIT', 1180);
define('MYSQL_ER_ERROR_DURING_ROLLBACK', 1181);
define('MYSQL_ER_ERROR_DURING_FLUSH_LOGS', 1182);
define('MYSQL_ER_ERROR_DURING_CHECKPOINT', 1183);
define('MYSQL_ER_NEW_ABORTING_CONNECTION', 1184);
define('MYSQL_ER_DUMP_NOT_IMPLEMENTED', 1185);
define('MYSQL_ER_FLUSH_MASTER_BINLOG_CLOSED', 1186);
define('MYSQL_ER_INDEX_REBUILD', 1187);
define('MYSQL_ER_MASTER', 1188);
define('MYSQL_ER_MASTER_NET_READ', 1189);
define('MYSQL_ER_MASTER_NET_WRITE', 1190);
define('MYSQL_ER_FT_MATCHING_KEY_NOT_FOUND', 1191);
define('MYSQL_ER_LOCK_OR_ACTIVE_TRANSACTION', 1192);
define('MYSQL_ER_UNKNOWN_SYSTEM_VARIABLE', 1193);
define('MYSQL_ER_CRASHED_ON_USAGE', 1194);
define('MYSQL_ER_CRASHED_ON_REPAIR', 1195);
define('MYSQL_ER_WARNING_NOT_COMPLETE_ROLLBACK', 1196);
define('MYSQL_ER_TRANS_CACHE_FULL', 1197);
define('MYSQL_ER_SLAVE_MUST_STOP', 1198);
define('MYSQL_ER_SLAVE_NOT_RUNNING', 1199);
define('MYSQL_ER_BAD_SLAVE', 1200);
define('MYSQL_ER_MASTER_INFO', 1201);
define('MYSQL_ER_SLAVE_THREAD', 1202);
define('MYSQL_ER_TOO_MANY_USER_CONNECTIONS', 1203);
define('MYSQL_ER_SET_CONSTANTS_ONLY', 1204);
define('MYSQL_ER_LOCK_WAIT_TIMEOUT', 1205);
define('MYSQL_ER_LOCK_TABLE_FULL', 1206);
define('MYSQL_ER_READ_ONLY_TRANSACTION', 1207);
define('MYSQL_ER_DROP_DB_WITH_READ_LOCK', 1208);
define('MYSQL_ER_CREATE_DB_WITH_READ_LOCK', 1209);
define('MYSQL_ER_WRONG_ARGUMENTS', 1210);
define('MYSQL_ER_NO_PERMISSION_TO_CREATE_USER', 1211);
define('MYSQL_ER_UNION_TABLES_IN_DIFFERENT_DIR', 1212);
define('MYSQL_ER_LOCK_DEADLOCK', 1213);
define('MYSQL_ER_TABLE_CANT_HANDLE_FT', 1214);
define('MYSQL_ER_CANNOT_ADD_FOREIGN', 1215);
define('MYSQL_ER_NO_REFERENCED_ROW', 1216);
define('MYSQL_ER_ROW_IS_REFERENCED', 1217);
define('MYSQL_ER_CONNECT_TO_MASTER', 1218);
define('MYSQL_ER_QUERY_ON_MASTER', 1219);
define('MYSQL_ER_ERROR_WHEN_EXECUTING_COMMAND', 1220);
define('MYSQL_ER_WRONG_USAGE', 1221);
define('MYSQL_ER_WRONG_NUMBER_OF_COLUMNS_IN_SELECT', 1222);
define('MYSQL_ER_CANT_UPDATE_WITH_READLOCK', 1223);
define('MYSQL_ER_MIXING_NOT_ALLOWED', 1224);
define('MYSQL_ER_DUP_ARGUMENT', 1225);
define('MYSQL_ER_USER_LIMIT_REACHED', 1226);
define('MYSQL_ER_SPECIFIC_ACCESS_DENIED_ERROR', 1227);
define('MYSQL_ER_LOCAL_VARIABLE', 1228);
define('MYSQL_ER_GLOBAL_VARIABLE', 1229);
define('MYSQL_ER_NO_DEFAULT', 1230);
define('MYSQL_ER_WRONG_VALUE_FOR_VAR', 1231);
define('MYSQL_ER_WRONG_TYPE_FOR_VAR', 1232);
define('MYSQL_ER_VAR_CANT_BE_READ', 1233);
define('MYSQL_ER_CANT_USE_OPTION_HERE', 1234);
define('MYSQL_ER_NOT_SUPPORTED_YET', 1235);
define('MYSQL_ER_MASTER_FATAL_ERROR_READING_BINLOG', 1236);
define('MYSQL_ER_SLAVE_IGNORED_TABLE', 1237);
define('MYSQL_ER_INCORRECT_GLOBAL_LOCAL_VAR', 1238);
define('MYSQL_ER_WRONG_FK_DEF', 1239);
define('MYSQL_ER_KEY_REF_DO_NOT_MATCH_TABLE_REF', 1240);
define('MYSQL_ER_OPERAND_COLUMNS', 1241);
define('MYSQL_ER_SUBQUERY_NO_1_ROW', 1242);
define('MYSQL_ER_UNKNOWN_STMT_HANDLER', 1243);
define('MYSQL_ER_CORRUPT_HELP_DB', 1244);
define('MYSQL_ER_CYCLIC_REFERENCE', 1245);
define('MYSQL_ER_AUTO_CONVERT', 1246);
define('MYSQL_ER_ILLEGAL_REFERENCE', 1247);
define('MYSQL_ER_DERIVED_MUST_HAVE_ALIAS', 1248);
define('MYSQL_ER_SELECT_REDUCED', 1249);
define('MYSQL_ER_TABLENAME_NOT_ALLOWED_HERE', 1250);
define('MYSQL_ER_NOT_SUPPORTED_AUTH_MODE', 1251);
define('MYSQL_ER_SPATIAL_CANT_HAVE_NULL', 1252);
define('MYSQL_ER_COLLATION_CHARSET_MISMATCH', 1253);
define('MYSQL_ER_SLAVE_WAS_RUNNING', 1254);
define('MYSQL_ER_SLAVE_WAS_NOT_RUNNING', 1255);
define('MYSQL_ER_TOO_BIG_FOR_UNCOMPRESS', 1256);
define('MYSQL_ER_ZLIB_Z_MEM_ERROR', 1257);
define('MYSQL_ER_ZLIB_Z_BUF_ERROR', 1258);
define('MYSQL_ER_ZLIB_Z_DATA_ERROR', 1259);
define('MYSQL_ER_CUT_VALUE_GROUP_CONCAT', 1260);
define('MYSQL_ER_WARN_TOO_FEW_RECORDS', 1261);
define('MYSQL_ER_WARN_TOO_MANY_RECORDS', 1262);
define('MYSQL_ER_WARN_NULL_TO_NOTNULL', 1263);
define('MYSQL_ER_WARN_DATA_OUT_OF_RANGE', 1264);
define('MYSQL_ER_WARN_DATA_TRUNCATED', 1265);
define('MYSQL_ER_WARN_USING_OTHER_HANDLER', 1266);
define('MYSQL_ER_CANT_AGGREGATE_2COLLATIONS', 1267);
define('MYSQL_ER_DROP_USER', 1268);
define('MYSQL_ER_REVOKE_GRANTS', 1269);
define('MYSQL_ER_CANT_AGGREGATE_3COLLATIONS', 1270);
define('MYSQL_ER_CANT_AGGREGATE_NCOLLATIONS', 1271);
define('MYSQL_ER_VARIABLE_IS_NOT_STRUCT', 1272);
define('MYSQL_ER_UNKNOWN_COLLATION', 1273);
define('MYSQL_ER_SLAVE_IGNORED_SSL_PARAMS', 1274);
define('MYSQL_ER_SERVER_IS_IN_SECURE_AUTH_MODE', 1275);
define('MYSQL_ER_WARN_FIELD_RESOLVED', 1276);
define('MYSQL_ER_BAD_SLAVE_UNTIL_COND', 1277);
define('MYSQL_ER_MISSING_SKIP_SLAVE', 1278);
define('MYSQL_ER_UNTIL_COND_IGNORED', 1279);
define('MYSQL_ER_WRONG_NAME_FOR_INDEX', 1280);
define('MYSQL_ER_WRONG_NAME_FOR_CATALOG', 1281);
define('MYSQL_ER_WARN_QC_RESIZE', 1282);
define('MYSQL_ER_BAD_FT_COLUMN', 1283);
define('MYSQL_ER_UNKNOWN_KEY_CACHE', 1284);
define('MYSQL_ER_WARN_HOSTNAME_WONT_WORK', 1285);
define('MYSQL_ER_UNKNOWN_STORAGE_ENGINE', 1286);
define('MYSQL_ER_WARN_DEPRECATED_SYNTAX', 1287);
define('MYSQL_ER_NON_UPDATABLE_TABLE', 1288);
define('MYSQL_ER_FEATURE_DISABLED', 1289);
define('MYSQL_ER_OPTION_PREVENTS_STATEMENT', 1290);
define('MYSQL_ER_DUPLICATED_VALUE_IN_TYPE', 1291);
define('MYSQL_ER_TRUNCATED_WRONG_VALUE', 1292);
define('MYSQL_ER_TOO_MUCH_AUTO_TIMESTAMP_COLS', 1293);
define('MYSQL_ER_INVALID_ON_UPDATE', 1294);
define('MYSQL_ER_UNSUPPORTED_PS', 1295);
define('MYSQL_ER_GET_ERRMSG', 1296);
define('MYSQL_ER_GET_TEMPORARY_ERRMSG', 1297);
define('MYSQL_ER_UNKNOWN_TIME_ZONE', 1298);
define('MYSQL_ER_WARN_INVALID_TIMESTAMP', 1299);
define('MYSQL_ER_INVALID_CHARACTER_STRING', 1300);
define('MYSQL_ER_WARN_ALLOWED_PACKET_OVERFLOWED', 1301);
define('MYSQL_ER_CONFLICTING_DECLARATIONS', 1302);
define('MYSQL_ER_ERROR_MESSAGES', 1303); //or 303

//Custom errors
define('MYSQL_ER_NOT_CONNECTED', 1);
define('MYSQL_ERSTR_NOT_CONNECTED', 'Not connected to server');

/**
* A mysqli wrapper class. To be used by the db abstraction layer
* @version 0.0.1
* @author Costin Bereveanu
* @package Utilities
*/
class MysqliDatabase extends mysqli
{
    public $throw_exceptions = true;
    
	/**
	* Modified constructor that accepts a connection string as its input
	* Format: "server=...; database=...; username=...; password=...; port=...; socket=..."
	* Each parameter is optional
	* Please note the existance of the connect_errno and connect_error mysqli member variables, not declared in the PHP documentation. They will hold the last connect error
	* @param string $ConnectionString
	* @param bool $ThrowExceptions
	*/
	public function __construct($ConnectionString = '', $ThrowExceptions = true)
	{
		if ($ConnectionString == '') {
			global $mysql_host,$mysql_name,$mysql_user,$mysql_pass,$per_page;
			$CS['server'] = $mysql_host;
			$CS['database'] = $mysql_name;
			$CS['username'] = $mysql_user;
			$CS['password'] = $mysql_pass;
		} else {		
			$CS = explode(';', $ConnectionString);
		}
		
		foreach ($CS as $Key => $Element)
		{
			$Element = explode('=', $Element);
			if (isset($Element[1]))
			{
				$CS[trim($Element[0])] = trim($Element[1]);
				unset($CS[$Key]);
			}
		}
		
		@$this->connect(empty($CS['server']) ? 'localhost' : $CS['server'],
							 empty($CS['username']) ? 'root' : $CS['username'],
							 empty($CS['password']) ? '' : $CS['password'],
							 empty($CS['database']) ? 'test' : $CS['database'],
							 empty($CS['port']) ? 3306 : $CS['port'],
							 empty($CS['socket']) ? null : $CS['socket']);
		if (@$this->connect_errno)
		{
		    if ($ThrowExceptions) throw new Exception(mysqli_connect_error(), mysqli_connect_errno());
		}
	}
	
    /**
    * Sends a query to the database
    * Adds exception throwing
    * @param string $Query
    * @return mixed
    */
	public function query($Query)
	{
		if ($Query == '') return;
		
		if ($this->connect_errno)
		{
				if ($this->throw_exceptions) throw new Exception(MYSQL_ERSTR_NOT_CONNECTED, MYSQL_ER_NOT_CONNECTED);
				return null;
		}

		$Ret = parent::query($Query);
		
		$allow_err = array(1062);
		
		if ($this->errno && !(in_array( $this->errno, $allow_err )) )
		{
			if ($this->throw_exceptions) throw new Exception($this->error, $this->errno);
		}
		
		if (is_object($Ret))
		{
			$Ret =& new MysqliResult($Ret);
		}
		return $Ret;
	}
	
	/**
    * Send a query and returns the first result row
    * @param string $Query
    * @return mixed NULL if there are no rows, object otherwise
    */
	public function query_one($Query)
	{
	    $Ret = $this->query($Query);
	    if (is_object($Ret)) return $Ret->fetch_object();
	    return null;
	}
	
	/**
    * Send out a query and return all the rows
    * @param string $Query
    * @return mixed NULL if there are no rows, array of objects otherwise
    */
	public function query_all($Query)
	{
		$Ret = $this->query($Query);
		if (!is_object($Ret)) return null;
		
		$Rows = array();

		while ($Row = $Ret->fetch_object())
		{
			$Rows[] = $Row;
		}
		return empty($Rows) ? null : $Rows;
	}
	
	/**
    * Send out a query and return the first field of the first result row, cast to the given type
    * @param string $Query
    * @param string $Type
    * @return mixed NULL if there are no rows, $Type otherwise
    */
	public function query_value($Query, $Type)
	{
		$Ret = $this->query_one($Query);
		if (null !== $Ret)
		{
			foreach ($Ret as $Field)
			{
				settype($Field, $Type);
				return $Field;
			}
		}
		return null;
	}
	
	/**
    * Send out a query and return the number of affected rows
    * @param string $Query
    * @return int The number of affected rows
    */
	public function query_affected($Query)
	{
		$this->query($Query);
		return $this->affected_rows;
	}
	
	/**
    * Returns true if the last error code is equal to that given or false otherwise. You may use the constants given
    * @param int $ErrorCode
    * @return bool
    */
	public function last_error_was($ErrorCode)
	{
		return $this->errno == $ErrorCode;
	}
	
	/**
	* Runs a paginated query - returns the specified page of results
	* @param string $Query
	* @param int $RecordsPerPage
	* @param int $Page
	* @return mixed
	*/
	public function query_page($Query, $RecordsPerPage, $Page)
	{
	    $Ret = false;
	    if (stripos($Query, 'SELECT') === 0)
	    {
	        $Ret = $this->query(substr_replace($Query, 'SELECT SQL_CALC_FOUND_ROWS', 0, 6) . ' LIMIT ' . ($RecordsPerPage * $Page) . ', ' . $RecordsPerPage);
	    }
	    return $Ret;
	}
	
	/**
	* Runs a paginated query and fetches all result rows
	* @param string $Query
	* @param int $RecordsPerPage
	* @param int $Page
	* @return mixed array or NULL if error
	*/
	public function query_page_all($Query, $RecordsPerPage, $Page)
	{
	    $Ret = $this->query_page($Query, $RecordsPerPage, $Page);
		if (!is_object($Ret)) return null;
		
		$Rows = array();
		while ($Row = $Ret->fetch_object())
		{
			$Rows[] = $Row;
		}
		return empty($Rows) ? null : $Rows;
	}
	
	/**
	* Runs a paginated query and fetches only the first result row
	* @param string $Query
	* @param int $RecordsPerPage
	* @param int $Page
	* @return mixed object or NULL if error
	*/
	public function query_page_one($Query, $RecordsPerPage, $Page)
	{
	    $Ret = $this->query_page($Query, $RecordsPerPage, $Page);
	    if (is_object($Ret)) return $Ret->fetch_object();
	    return null;
	}
	
	/**
	* Returns the total number of rows in the last paginated query. DO NOT run unless a paginated query has been ran previously. It will just return 0
	* @return int
	*/
	public function query_total()
	{
	    return $this->query_value("SELECT FOUND_ROWS()", 'int');
	}
}

/**
* A mysqli_result wrapper class. To be used by MysqliDatabase
* @version 0.0.1
* @author Costin Bereveanu
* @package Utilities
*/
class MysqliResult
{
    private $Result = null;
    private $FieldsInfo = array();
    
    /**
    * Constructs a MysqliResult object based on a mysqli_result one
    * @param mysqli_result $Result
    */
    public function __construct(mysqli_result $Result)
    {
        $this->Result = $Result;
        $this->FieldsInfo = $this->Result->fetch_fields();
    }
    
    /**
    * Fetches the next row of data and casts the destination variables to types compatible to those in the database
    * @return mixed NULL if there are no more rows, object otherwise
    */
    public function fetch_object()
    {
        $Row = $this->Result->fetch_object();
        if (is_null($Row)) return null;
				
		$k = 0;
		foreach ($Row as $Key => $Field)
		{

		  if (in_array($this->FieldsInfo[$k]->type, $GLOBALS['MYSQL2PHP_TYPE_NULL']))
			{
//				$Row->$Key = null;
				$Row->$Key = $Field;
			}
			elseif (in_array($this->FieldsInfo[$k]->type, $GLOBALS['MYSQL2PHP_TYPE_INT']))
			{
				$Row->$Key = intval($Field);
			}
			elseif (in_array($this->FieldsInfo[$k]->type, $GLOBALS['MYSQL2PHP_TYPE_FLOAT']))
			{
				$Row->$Key = floatval($Field);
			}
			elseif (in_array($this->FieldsInfo[$k]->type, $GLOBALS['MYSQL2PHP_TYPE_STRING']))
			{
				$Row->$Key = strval($Field);
			}
			$k++;
		}
		return $Row;
    }
    
    public function __get($Attribute)
    {
    	return $this->Result->$Attribute;
    }
    
    public function __set($Attribute, $Value)
    {
    	$this->Result->$Attribute = $Value;
    }
    
    public function __call($Method, $Arguments)
    {
    	return call_user_func_array(array($this->Result, $Method), $Arguments);
    }

}

?>
