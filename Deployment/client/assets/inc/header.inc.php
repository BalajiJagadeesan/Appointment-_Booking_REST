<!doctype html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="description" content="">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">

    <!--Import Google Icon Font-->
    <link href="http://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <!--Import materialize.css-->
    <link type="text/css" rel="stylesheet" href="<?=URL_CSS?>materialize.min.css"  media="screen,projection"/>
    <link rel="stylesheet" href="<?=URL_CSS?>style.css" type="text/css" />
    <title>Project-3<?=($title)?" | ".($title):""?></title>
</head>
<body>
<header>
<nav class="amber lighten-1">
    <div class="nav-wrapper">
        <a href="" class="brand-logo">&nbsp;Appointment Manager</a>
        <a href="#" data-activates="mobile-demo" class="button-collapse"><i class="material-icons">menu</i></a>
        <ul class="right hide-on-med-and-down">
            <li><a href="<?=URL_BASE?>get.php">GET Info</a></li>
            <li><a href="<?=URL_BASE?>modify.php">Modify Info</a></li>
        </ul>
        <ul class="side-nav" id="mobile-demo">
            <li><a href="<?=URL_HOME?>get.php">GET Info</a></li>
            <li><a href="<?=URL_HOME?>modify.php">Modify Info</a></li>
        </ul>
    </div>
</nav>
</header>
<main>
    <div class="container">

<script src="<?= URL_JS ?>jquery-v2.min.js" type="text/javascript"></script>
<script src="<?= URL_JS ?>materialize.min.js" type="text/javascript"></script>
<script src="<?= URL_JS ?>main.js" type="text/javascript"></script>
<script src="<?= URL_JS ?>materialize.clockpicker.js" type="text/javascript"></script>
