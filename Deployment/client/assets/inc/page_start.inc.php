<?php
//initialise everything we need for the site

//define constants
define("SERVER_PATH","http://localhost:8080/Project3/AppointmentsAPI/");

//PHP uses path
define("PATH_BASE","./");
define("PATH_INC", PATH_BASE. "assets/inc/");
define("PATH_JS",PATH_BASE."assets/js/");
define("PATH_LIB",PATH_BASE."assets/lib/");
define("PATH_CLASS",PATH_BASE."assets/classes/");
define("PATH_PRODUCT_IMG",PATH_BASE."assets/img/product/");

//HTML uses url
//define("URL_BASE","http://kelvin.ist.rit.edu/~bxj9142/appointment/");
define("URL_BASE","http://localhost/project3/");
define("URL_HOME","index.php");
define("URL_JS",URL_BASE."assets/js/");
define("URL_CSS",URL_BASE."assets/css/");

//establish DB connections

//include function libraries

require_once PATH_CLASS."MyCurl.class.php";
?>