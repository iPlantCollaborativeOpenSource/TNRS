<?php
/* class.FastCache.php
* Copyright (C) 1999-2005 Jacob Stetser/icongarden.com
* cacheable() attributed to Troels Arvin (arvin@ead.dk)
*
* This library is free software; and provided under a
* Creative Commons Attribution-ShareAlike license.
*
* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Library General Public License for more details.
*
*/

/**
* FastCache
* @package Utilities
* @author Jacob Stetser <php@icongarden.com>
**/
// 08/26/1999 12:34

class FastCache {
  var $CACHE_PATH    = "cache";    // Set default cache path to
                                    // current directory + /cache
  var $UMASK         = "0111";       // Change this to fit your needs.
  var $CACHE_KEY     = "";
  var $CACHE_SUFFIX  = ".cache";
  var $CTRL_SUFFIX   = ".ctrl";

  function FastCache($cache_path="cache/") {
    if(isset($cache_path)) {
//       $this->CACHE_PATH = '../' . $cache_path;
      $this->CACHE_PATH = $cache_path;
    }
  }

  function make_key($page_path_info) {
    $cache_key = md5($page_path_info);
    $this->CACHE_KEY = $cache_key;
    return $cache_key;
  }

  function update_control() {
    @touch($this->CACHE_PATH.$this->CACHE_KEY.$this->CTRL_SUFFIX);
    return;
  }

  function update($html) {
    flush();
    if(isset($this->UMASK)) {
      umask($this->UMASK);
    }
    $cache_ctrl = $this->CACHE_KEY.$this->CTRL_SUFFIX;
    $cache_file = $this->CACHE_KEY.$this->CACHE_SUFFIX;
/*    if(!file_exists($this->CACHE_PATH.$cache_ctrl)) {
     @touch($this->CACHE_PATH.$cache_ctrl);
    }*/
    if(file_exists($this->CACHE_PATH.$cache_file)) {
      $temp_name = tempnam($this->CACHE_PATH."temp","fastcache-");
      $fp = fopen($temp_name,"w");
      fputs($fp,$html);
//      fputs($fp,"<!-- This file came from the cache -->");
      fclose($fp);
      rename($this->CACHE_PATH.$cache_file,$this->CACHE_PATH.$cache_file."bak");
      copy($temp_name,$this->CACHE_PATH.$cache_file);
      unlink($temp_name);
      unlink($this->CACHE_PATH.$cache_file."bak");
    } else {
      $temp_name = tempnam($this->CACHE_PATH."temp","fastcache-");
      $fp = fopen($temp_name,"w");
      fputs($fp,$html);
//      fputs($fp,"<!-- This file came from the cache -->");
      fclose($fp);
      @copy($temp_name,$this->CACHE_PATH.$cache_file);
      unlink($temp_name);
    }
    return;
  }

  function cache_exists() {
      if(file_exists($this->CACHE_PATH.$this->CACHE_KEY.$this->CACHE_SUFFIX) && file_exists($this->CACHE_PATH.$this->CACHE_KEY.$this->CTRL_SUFFIX)) {
          return 1;
      } else {
          return 0;
      }
  }
  
  function compare_cache() {
      if(filemtime($this->CACHE_PATH.$this->CACHE_KEY.$this->CACHE_SUFFIX) >= filemtime($this->CACHE_PATH.$this->CACHE_KEY.$this->CTRL_SUFFIX)) {
          return 1;
      } else {
          return 0;
      }
  }
      
  function print_cache(){
    readfile($this->CACHE_PATH.$this->CACHE_KEY.$this->CACHE_SUFFIX);
    return;
  }

  function fetch($cache_key = ""){
     $html = join(file($this->CACHE_PATH.$this->CACHE_KEY.$this->CACHE_SUFFIX));
     return $html;
  }

  function cacheable ($days='', $printlastmod='yes') {
    // Normal use: cacheable();

    // Should appear before any normal (non-HTTP-header)
    // output.

    // cacheable('no') will try it's very best to make
    // the page non-cacheable (be putting the
    // Expires header in the path)

    if ($days == 'no') { $days = -1; }

    // Default caching time
    if (($days=='') || ($days=='yes')) { $days = 1; }

    settype($days, 'double');
    setlocale('LC_ALL', 'en_US');
    if ($days < 0) {
      header('Cache-Control: no-cache, no-store, must-validate');
      header('Pragma: no-cache');
      header('Last-Modified: now');
      header('Expires: '.strftime('%a, %d %b %Y %H:%M:%S GMT',(time() - 86400)));
    } else {
        // The Last-Modified header may not always be
        // relevant, but I believe that it makes the
        // pages more cacheable. Default is to put
        // it there.
        if ($printlastmod == 'yes') {
            header('Last-Modified: ' .
            strftime('%a, %d %b %Y %H:%M:%S GMT',
                getlastmod()));
        }

        if ($days > 0) {
              header('Expires: ' .
                  strftime('%a, %d %b %Y %H:%M:%S GMT',
                  (time() + (86400 * $days))));
        }
    }
  }
}
?>