<?php
/**
 * Created by PhpStorm.
 * User: bajji
 * Date: 05/08/2017
 * Time: 4:18 PM
 */
require_once "./assets/inc/page_start.inc.php";

$title = "GET Info";
require_once PATH_INC."header.inc.php";

?>
<h5>GET Content</h5>
    <div class="row">
        <form class="col s12" action="<?=$_SERVER['PHP_SELF']?>" method="GET">
            <div class="row">
                <div class="input-field col s4 ">
                    <select class="" id="content" name="content">
                        <option value="" <?= (empty($_GET['content']))?'selected':''?>>Choose your option</option>
                        <option value="1" <?=(!empty($_GET['content']) && $_GET['content'] == 1)?'selected':''?>>Appointment Details</option>
                        <option value="2" <?=(!empty($_GET['content']) && $_GET['content'] == 2)?'selected':''?>>Patient Details</option>
                        <option value="3" <?=(!empty($_GET['content']) && $_GET['content'] == 3)?'selected':''?>>LabTest-Prices Details</option>
                    </select>
                    <label for="content">Info needed to be fetched</label>
                </div>
                <div class="input-field col s4 ">
                    <input id="id" name="key" type="text" class="validate" value="<?= !empty($_GET['key'])?($_GET['key']):""?>">
                    <label for="id">ID field</label>
                </div>
                <button class="btn waves-effect waves-light green" type="submit">Submit
                    <i class="material-icons right">send</i>
                </button>
            </div>
        </form>
    </div>
<?php
if(isset($_GET)){
    if(empty($_GET["content"])){
       echo "Please choose a info you need to get";
    }
    $url ="";
    if(!empty($_GET['content'])){
        switch ($_GET['content']){
            case 1:
                $url = SERVER_PATH."Appointments/";
                break;
            case 2:
                $url = SERVER_PATH."Patients/";
                break;
            case 3:
                $url = SERVER_PATH."LabTests/";
                break;
        }
        if(!empty($_GET['key'])){
            $url .= $_GET['key']."/";
        }
    }

    if(!empty($url)){
        $response = MyCurl::getRemoteFile($url);
//        echo htmlentities($response);
        if(empty($response)){
            echo "<p>Server is offline</p>";
        }else {
            $xml = simplexml_load_string($response);
//            echo "<pre>";
//            print_r($xml);
//            echo "</pre>";
            if($xml->appointment){
                echo "<div class='row'>";
                echo "<h5>Appointment List</h5>";
                foreach ($xml->appointment as $item) {
                    echo "<div>";
                    echo "<p>Appointment ID : ".$item['id']."<br>";
                    echo "Appointment Date : ".$item['date']."<br>";
                    echo "Appointment Time : ".$item['time']."<br>";
                    echo "Patient Name : ".$item->patient->name."<br>";
                    echo "Phlebotomist Name : ".$item->phlebotomist->name."<br>";
                    echo "PSC Location : ".$item->psc->name."<br></p>";
                    echo "<p>Patient API Endpoint : <a href='".$item->uri."'>Patient Endpoint</a></p>";
                    echo "<p>Appointment API Endpoint : <a href='".$item->uri."'>Appointment Endpoint</a></p>";
//                    echo "Patient"
                    echo "</div><hr>";
                }
                echo "</div>";
            }else if($xml->patient){
                echo "<div class='row'>";
                echo "<h5>Patient List</h5>";
                foreach ($xml->patient as $item) {
                    echo "<div>";
                    echo "<p>Patient API Endpoint : <a href='".$item->uri."'>Link</a></p>";
                    echo "<p>Patient ID : ".$item['id']."<br>";
                    echo "Patient Name : ".$item->name."<br>";
                    echo "Patient's Physician : ".$item->physician->name."<br>";
//                    echo "Patient"
                    echo "</p></div><hr>";
                }
            }else if($xml->labtest){

                echo "<div class='row'>";
                echo "<h5>LabTest List</h5>";
                foreach ($xml->labtest as $item) {
                    echo "<div>";
                    echo "<p>Lab Test : <a href='" . $item->uri . "'>Link</a></p>";
                    echo "<p>LabTest ID : " . $item['id'] . "<br>";
                    echo "LabTest Name : " . $item->name . "<br>";
                    echo "Cost : " . $item->cost . "<br>";
//                    echo "Patient"
                    echo "</p></div><hr>";
                }
            }else if($xml->error){
                echo $xml->error;
            }
        }
    }
}
require_once PATH_INC."footer.inc.php";

?>