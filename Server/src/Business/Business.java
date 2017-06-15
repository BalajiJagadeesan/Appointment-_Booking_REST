package Business;

import DBTest.DBSingleton;
import com.jamesmurty.utils.XMLBuilder;
import components.data.*;

import javax.xml.parsers.ParserConfigurationException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Balaji Jagadeesan on 05/04/2017.
 */
public class Business {
    private DBSingleton dbSingleton;
    private InfoCheckHelper helper;
    private boolean errorFlag = false;
    private int errorNumber = 0;
    public Business(){
        dbSingleton = DBSingleton.getInstance();
        helper = new InfoCheckHelper();
    }

    /**
     * To generate the url for the wadl
     * @param url the base url of the application
     * @return  XML document with the url of the wadl document
     */
    public String wadlDoc(String url) {
        XMLBuilder x;
        try {
            x = XMLBuilder.create("AppointmentList").e("intro").t("Welcome to the LAMS Appointment Service").up().e("wadl").t(url);
            String doc = x.asString();
            return doc;
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed.</error></AppointmentList>";
        }
    }

    /**
     * To list all the appointments that is registered in the system
     * @param url the endpoint which called this function
     * @return  XML document with all appointments
     */
    public String getAllAppointments(String url) {

        List<Object> objs;
        try{
            XMLBuilder root = XMLBuilder.create("AppointmentList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            objs = dbSingleton.db.getData("Appointment", "");
            for (Object obj : objs){
                Appointment appointmentObject = (Appointment) obj;
                root = root.importXMLBuilder(this.xmlAppointmentHelper(appointmentObject,url));
            }
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed</error></AppointmentList>";
        }
    }

    /**
     * Get one appointment based on the appointment ID
     * @param url the endpoint from which it is called from
     * @param id the id of the application
     * @return a XML document of the appointment or an error message
     */
    public String getOneAppointment(String url,String id) {
        try{
            XMLBuilder root = XMLBuilder.create("AppointmentList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            if(id.isEmpty() | id == null | id == ""){
                this.errorFlag = true;
                this.errorNumber=3;
                return root.e("error").t("Application ID cannot be empty").up().up().asString();
            }
            Object objs = dbSingleton.db.getData("Appointment", "id='"+id+"'");
            List<Appointment> appointmentObject= (ArrayList<Appointment>) objs;
            if(appointmentObject.isEmpty()){
                this.errorFlag = true;
                this.errorNumber=4;
                return root.e("error").t("No Appointment can be found for the given ID").up().up().asString();
            }
            root = root.importXMLBuilder(this.xmlAppointmentHelper(appointmentObject.get(0), url));
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed</error></AppointmentList>";
        }
    }

    /**
     * Helper function for creating XML document for appointment
     * @param appointmentObject the object from which data is extracted to create the XML doc
     * @param url the endpoint from which it is called from
     * @return the XML subdoc with root as appointment
     * @throws Exception
     */
    private XMLBuilder xmlAppointmentHelper(Appointment appointmentObject, String url) throws Exception {
        XMLBuilder elements;
        Patient patient = appointmentObject.getPatientid();
        Phlebotomist phlebotomist =appointmentObject.getPhlebid();
        PSC psc = appointmentObject.getPscid();
        List<AppointmentLabTest> labTest = appointmentObject.getAppointmentLabTestCollection();
//        if(Character.isDigit(url.charAt(url.length()-1))){
//            url = url.substring(0,url.length()-3);
//        }
        elements = XMLBuilder.create("appointment")
                .a("date",appointmentObject.getApptdate().toString())
                .a("id",appointmentObject.getId())
                .a("time",appointmentObject.getAppttime().toString())
                .e("uri").t(url+"AppointmentsAPI/Appointments/"+appointmentObject.getId()+"/").up()
                .e("patient").a("id",patient.getId())
                .e("uri").t(url+"AppointmentsAPI/Patients/"+patient.getId()+"/").up()
                .e("name").t(patient.getName()).up()
                .e("address").t(patient.getAddress()).up()
                .e("insurance").t(Character.toString( patient.getInsurance())).up()
                .e("dob").t(patient.getDateofbirth().toString()).up().up()
                .e("phlebotomist").a("id",phlebotomist.getId())
                .e("uri").up()
                .e("name").t(phlebotomist.getName()).up().up()
                .e("psc").a("id",psc.getId())
                .e("uri").up()
                .e("name").t(psc.getName()).up().up()
                .e("allLabTest");
        XMLBuilder doc=elements;
        for(AppointmentLabTest test : labTest){
            AppointmentLabTestPK pk = test.getAppointmentLabTestPK();
            doc = elements.e("appointmentLabTest")
                    .a("appointmentId",pk.getApptid())
                    .a("dxcode",pk.getDxcode())
                    .a("labTestId",pk.getLabtestid()).e("uri").up().up();
        }
        return doc;
    }

    /**
     * To add a new appointment to the database
     * @param xml The input xml doc which contains appointment details
     * @param url the url that needs of the webservice
     * @return xml document
     */
    public String addAppointment(String xml, String url) {
        try {
            xml = java.net.URLDecoder.decode(xml, "UTF-8");
            xml = xml.replace( "xml=","");
            System.out.println(xml);
            XMLBuilder root = XMLBuilder.create("AppointmentList");
            if (helper.validateXML(xml,helper.getAppointmentXSD())) {
                helper.parseXML(xml);
                if (helper.checkRules()) {
                    Appointment appointment = new Appointment(Integer.toString(helper.getAppId()), java.sql.Date.valueOf(helper.getDate()), java.sql.Time.valueOf(LocalTime.parse(helper.getApptTime())));
                    appointment.setPatientid(helper.getPatientObject());
                    appointment.setPhlebid(helper.getPhlebotomistObject());
                    appointment.setPscid(helper.getPscObject());
                    appointment.setAppointmentLabTestCollection(helper.getAppointmentLabTestArrayList());
                    System.out.println("\n\n\n\n" + appointment + "\n\n\n\n");
                    if(dbSingleton.db.addData(appointment)) {
                        root = root.e("uri").t(url+"AppointmentsAPI/Appointments/"+helper.getAppId());
                        return root.asString();
                    }
                }
            }
            this.errorFlag = true;
            this.errorNumber = 5;
            for(String s : helper.getErrorMsg()){
                root = root.e("error").t(s).up();
            }
            return root.asString();
        }catch (Exception e) {
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed</error></AppointmentList>";
        }
    }

    /**
     * To update an appointment
     * @param xml xml doc from client
     * @param url the base url of the server
     * @param id id of the appointment to be changed
     * @return xml doc as string
     */
    public String updateAppointment(String xml,String url,String id){
        try{
            xml = java.net.URLDecoder.decode(xml, "UTF-8");
            xml = xml.replace( "xml=","");
            System.out.println(xml);
            XMLBuilder root = XMLBuilder.create("AppointmentList");
            if (helper.validateXML(xml,helper.getUpdateAppointmentXSD())) {
                helper.parseXML(xml);
                System.out.println("Good");
                if(helper.updateAppointmentHelper(id)) {
                    if(dbSingleton.db.updateData(helper.getAppointmentObject())) {
                        root = root.e("uri").t(url+"AppointmentsAPI/Appointments/"+id);
                        return root.asString();
                    }
                }
            }
            this.errorFlag = true;
            this.errorNumber = 5;
            for(String s : helper.getErrorMsg()){
                root = root.e("error").t(s).up();
            }
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed</error></AppointmentList>";
        }
    }

    /**
     * To get details of all the patients
     * @param url the endpoint from which it is called from
     * @return XML doc of list of patients
     */
    public String getAllPatients(String url){
        try {
            XMLBuilder root = XMLBuilder.create("PatientList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            Object objs = dbSingleton.db.getData("Patient", "");
            List<Patient> p= (ArrayList<Patient>) objs;
            for(Patient patient:p) {
                root = root.importXMLBuilder(this.xmlPatientBuilder(patient,url));
            }
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<AppointmentList><error>Some internal error.Request cannot be processed</error></AppointmentList>";
        }
    }

    /**
     * Get one patient detail
     * @param url the endpoint from which it is called from
     * @param id the id of the patient
     * @return the XML doc of patient details
     */
    public String getOnePatient(String url,String id){
        try {
            XMLBuilder root = XMLBuilder.create("PatientList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            if(id.isEmpty() | id == null | id == ""){
                this.errorFlag = true;
                this.errorNumber=3;
                return root.e("error").t("Application ID cannot be empty").up().up().asString();
            }
            Object objs = dbSingleton.db.getData("Patient", "id='"+id+"'");
            List<Patient> p= (ArrayList<Patient>) objs;
            if(p.isEmpty()){
                errorFlag = true;
                return root.e("error").t("No Patient with the ID").up().up().asString();
            }
            root=root.importXMLBuilder(this.xmlPatientBuilder(p.get(0),url));
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<PatientList><error>Some internal error.Request cannot be processed</error></PatientList>";
        }
    }

    /**
     * Helper function to create XML document for each patient
     * @param patient the patient object that contains patient details
     * @param url the endpoint from which it is called from
     * @return the XML sub doc for each patient
     * @throws Exception
     */
    private XMLBuilder xmlPatientBuilder(Patient patient, String url) throws Exception {
        Physician physician = patient.getPhysician();
        XMLBuilder elements = XMLBuilder.create("patient").a("id",patient.getId())
                .e("uri").t(url+"AppointmentsAPI/Patients/"+patient.getId()+"/").up()
                .e("name").t(patient.getName()).up()
                .e("address").t(patient.getAddress()).up()
                .e("insurance").t(Character.toString( patient.getInsurance())).up()
                .e("dob").t(patient.getDateofbirth().toString()).up().up()
                .e("physician").a("id",physician.getId())
                .e("name").t(physician.getName()).up().up();
        return elements;
    }

    /**
     * To get all labtest available
     * @param url  base url of the server
     * @return xml doc as string
     */
    public String getAllLabTests(String url) {
        List<Object> objs;
        try{
            XMLBuilder root = XMLBuilder.create("LabTestList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            objs = dbSingleton.db.getData("LabTest", "");
            for (Object obj : objs){
                LabTest labTestObject = (LabTest) obj;
                root = root.importXMLBuilder(this.xmlLabTestHelper(labTestObject,url));
            }
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<LabTestList><error>Some internal error.Request cannot be processed</error></LabTestList>";
        }
    }

    /**
     * Get one labtest
     * @param url base url of the server
     * @param id id of the testcode
     * @return xml as string
     */
    public String getOneLabTest(String url,String id){
        try {
            XMLBuilder root = XMLBuilder.create("LabTestList");
            if(url=="" | url==null){
                this.errorFlag=true;
                this.errorNumber=2;
                return root.e("error")
                        .t("The context is not not available.Make sure the web application is running on a server")
                        .asString();
            }
            if(id.isEmpty() | id == null | id == ""){
                this.errorFlag = true;
                this.errorNumber=3;
                return root.e("error").t("LabTest ID cannot be empty").up().up().asString();
            }
            Object objs = dbSingleton.db.getData("LabTest", "id='"+id+"'");
            List<LabTest> p= (ArrayList<LabTest>) objs;
            if(p.isEmpty()){
                errorFlag = true;
                return root.e("error").t("No LabTest with the given ID").up().up().asString();
            }
            root=root.importXMLBuilder(this.xmlLabTestHelper(p.get(0),url));
            return root.asString();
        }catch (Exception e){
            e.printStackTrace();
            this.errorFlag=true;
            this.errorNumber = 1;
            return "<LabTestList><error>Some internal error.Request cannot be processed</error></LabTestList>";
        }
    }

    /**
     * Helper method to construct the xml doc for labtest
     * @param labTest the labtest object
     * @param url the base url
     * @return xml doc
     * @throws Exception
     */
    private XMLBuilder xmlLabTestHelper(LabTest labTest,String url) throws Exception {
        XMLBuilder x = XMLBuilder.create("labtest").a("id",labTest.getId())
                .e("uri").t(url+"AppointmentsAPI/LabTests/"+labTest.getId()).up()
                .e("name").t(labTest.getName()).up()
                .e("cost").t(Double.toString(labTest.getCost())).up();
        return x;
    }

    /**
     * Getter method for errorflag
     * @return boolean
     */
    public boolean isErrorFlag() {
        return errorFlag;
    }

    /**
     * Getter method for errorNum
     * @return errornumber
     */
    public int getErrorNumber() {
        return errorNumber;
    }

}
