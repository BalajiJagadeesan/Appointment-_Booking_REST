<?php
/**
 * Created by PhpStorm.
 * User: bajji
 * Date: 05/08/2017
 * Time: 4:18 PM
 */
require_once "./assets/inc/page_start.inc.php";
$title = "Modify Content";
require_once PATH_INC."header.inc.php";

?>
<?php if(!empty($_POST)){
//    echo "<pre>";
//    print_r($_POST);
//    echo "</pre>";
    $msg = "<appointment>";
    if(!empty($_POST['date'])) {
        $datefmt = date("Y-m-d", strtotime(str_replace(",", "", $_POST['date'])));
        $msg .="<date>".$datefmt."</date>";
    }
    if(!empty($_POST['time'])) {
        $timestamp = $_POST['time'];
        $msg .= "<time>".$timestamp."</time>";
    }
    if(!empty($_POST['patientid'])) {
        $patientId = $_POST['patientid'];
        $msg .= "<patientId>".$patientId."</patientId>";
    }
    if(!empty($_POST['physicianid'])) {
        $physicianId = $_POST['physicianid'];
        $msg .= "<physicianId>".$physicianId."</physicianId>";
    }
    if(!empty($_POST['pscid'])) {
        $pscId = $_POST['pscid'];
        $msg .= "<pscId>".$pscId."</pscId>";
    }
    if(!empty($_POST['phlebotomistid'])) {
        $phlebotomistId = $_POST['phlebotomistid'];
        $msg .= "<phlebotomistId>".$phlebotomistId."</phlebotomistId>";
    }
    $msg .= "<labTests>";
    if(!empty($_POST['phlebotomistid'])) {
        foreach ($_POST['labtest'] as $key => $item) {
            $data = explode("/", $_POST['labtest'][0]);
            $msg .= "<test id='" . $data[0] . "' dxcode='" . $data[1] . "' />";
        }
    }
    $msg .= "</labTests></appointment>";

    if($_POST['method']==2) {
        $url = SERVER_PATH."Appointments/".$_POST['id'];
    }else{
        $url = SERVER_PATH."Appointments/";
    }

//    echo $url;
    $post_data = array(
        "xml" => $msg
    );

    $stream_options = array(
        'http' => array(
            'method'  => ($_POST['method']==1)?"POST":"PUT",
            'header'  => "Content-type: application/xml\r\n",
            'content' => http_build_query($post_data),
            'ignore_errors' => true
        ),
    );
//    echo "<pre>";
//    print_r($stream_options);
//    echo "</pre>";
    $context  = stream_context_create($stream_options);
    $response="";
    try {
        $response = file_get_contents($url, null, $context);
    }catch (Exception $e){
        echo "<p>Some error occurred in parsing the data</p>";
    }

    if($_POST['method']=="1"){
        echo "<div><p>Successfully added the appointment to the database.<a href='./get.php?content=1&key=".explode("/",$response)[6]."'>Your ID is ".explode("/",$response)[6]."</a></p></div>";
    }
    else{
        echo "<div><p>Successfully modified the content.</p><a href='./get.php?content=1&key=".($_POST['id'])."'>Your ID is ".$_POST['id']."</a></div>";

    }
}else { ?>
    <h5>Modify Content</h5>
    <div class="row">
        <form class="col s12" action="<?= $_SERVER['PHP_SELF'] ?>" method="<?= (empty($_GET) ? "GET" : "POST") ?>">
            <div class="row">
                <div class="input-field col s4 ">
                    <select class="" id="content" name="content">
                        <option value="" <?= (empty($_GET['content'])) ? 'selected' : '' ?>>Choose your option</option>
                        <option value="1" <?= (!empty($_GET['content']) && $_GET['content'] == 1) ? 'selected' : '' ?>>
                            Appointment Details
                        </option>
                    </select>
                    <label for="content">Info needed to be fetched</label>
                </div>
                <div class="input-field col s4 ">
                    <select class="" id="method" name="method">
                        <option value="" <?= (empty($_GET['method'])) ? 'selected' : '' ?>>Choose your option</option>
                        <option value="1" <?= (!empty($_GET['method']) && $_GET['method'] == 1) ? 'selected' : '' ?>>
                            POST
                        </option>
                        <option value="2" <?= (!empty($_GET['method']) && $_GET['method'] == 2) ? 'selected' : '' ?>>
                            PUT
                        </option>
                    </select>
                    <label for="method">Operation</label>
                </div>
            </div>
            <?php if (!empty($_GET['content']) && $_GET['content'] == 1) {
                if($_GET['method']==2) {
                    ?>
                    <div class="row">
                        <div class="input-field col s4 ">
                            <input id="id" name="id" type="text"
                                   value="<?= !empty($_POST['id']) ? ($_POST['id']) : "" ?>">
                            <label for="id">Appointment ID</label>
                        </div>
                    </div>
                    <?php
                }
                ?>
                <div class="row">
                    <div class="input-field col s4 ">
                        <input id="date" name="date" type="date" class="datepicker"
                               value="<?= !empty($_POST['date']) ? ($_POST['date']) : "" ?>">
                        <label for="date">Pick a date</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4 ">
                        <input id="time" name="time" type="time" class="timepicker"
                               value="<?= !empty($_POST['time']) ? ($_POST['time']) : "" ?>">
                        <label for="time">Pick a time</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4 ">
                        <input id="patientid" name="patientid" type="text" class="validate"
                               value="<?= !empty($_POST['patientid']) ? ($_POST['patientid']) : "" ?>">
                        <label for="patientid">Patient ID</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4 ">
                        <input id="physicianid" name="physicianid" type="text" class="validate"
                               value="<?= !empty($_POST['physicianid']) ? ($_POST['physicianid']) : "" ?>">
                        <label for="physicianid">Physician ID</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4 ">
                        <input id="pscid" name="pscid" type="text" class="validate"
                               value="<?= !empty($_POST['pscid']) ? ($_POST['pscid']) : "" ?>">
                        <label for="pscid">PSC ID</label>
                    </div>
                </div>
                <div class="row">

                    <div class="input-field col s4 ">
                        <input id="phlebotomistid" name="phlebotomistid" type="text" class="validate"
                               value="<?= !empty($_POST['phlebotomistid']) ? ($_POST['phlebotomistid']) : "" ?>">
                        <label for="phlebotomistid">Phlebotomist ID</label>
                    </div>
                </div>
                <div class="row">
                    <div class="input-field col s4 ">
                        <div id="addinputs">
                            <input id="labtest" name="labtest[]" type="text" class="validate"
                                   placeholder="testcode/dxcode"
                                   value="<?= !empty($_POST['labtest']) ? ($_POST['labtest']) : "" ?>">
                            <label for="labtest"></label>
                        </div>
                        <button id="addfield" class="btn-floating waves-effect waves-light red" type="button"><i
                                    class="material-icons">add</i></button>
                    </div>
                </div>
                <?php
            }
            ?>
            <div class="row">
                <button class="btn waves-effect waves-light green" type="submit">Submit
                    <i class="material-icons right">send</i>
                </button>
            </div>
        </form>
    </div>
    <?php
}
require_once PATH_INC."footer.inc.php";

?>