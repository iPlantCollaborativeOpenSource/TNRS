<?php

$DOC_ROOT = dirname(__FILE__) . "/../";
require_once($DOC_ROOT . "lib/simpletest/autorun.php");
ini_set('error_reporting', E_ALL);

$group_test = new GroupTest('All tests');

get_tests_from_dir($DOC_ROOT . "tests/", $group_test, true);

$group_test->run(new HtmlReporter());





function get_tests_from_dir($dir, &$group_test, $recursive)
{
    if($handle = opendir($dir))
    {
       while(false !== ($file = readdir($handle)))
       {
           if(!$recursive && preg_match("/^(Test.*)\.php/", $file, $arr))
           {
               $file = $arr[1];
               require_once($dir . $file.".php");
               
               $group_test->addTestCase(new $file());
           }elseif($recursive && is_dir($dir .'/'. $file) && !preg_match("/^\./", $file))
           {
               get_tests_from_dir($dir .'/'. $file .'/', $group_test, false);
           }
       }
       closedir($handle);
    }
}

?>

