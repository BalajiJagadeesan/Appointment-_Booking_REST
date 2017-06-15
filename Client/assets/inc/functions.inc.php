<?php

/**
 * To load necessary class files
 * @param $classname - name of the class
 */

function __autoload($classname){
    require_once PATH_CLASS.$classname.".class.php";
}

?>