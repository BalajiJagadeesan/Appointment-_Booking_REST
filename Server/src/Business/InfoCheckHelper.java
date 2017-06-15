package Business;

import DBTest.DBSingleton;
import components.data.*;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.message.internal.XmlJaxbElementProvider;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.InputStreamReader;
import java.time.LocalTime;
import java.util.*;

/**
 * Created by bajji on 05/05/2017.
 */
public class InfoCheckHelper {

    private DBSingleton dbSingleton;
    private boolean errorFlag = false;
    private ArrayList<String> errorMsg = new ArrayList<>();
    private String appointmentXSD = "<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"appointment\"><xs:complexType><xs:sequence><xs:element type=\"xs:date\" name=\"date\" minOccurs=\"1\"/><xs:element type=\"xs:string\" name=\"time\" minOccurs=\"1\" /><xs:element type=\"xs:short\" name=\"patientId\" minOccurs=\"1\"/><xs:element type=\"xs:byte\" name=\"physicianId\" minOccurs=\"1\"/><xs:element type=\"xs:short\" name=\"pscId\" minOccurs=\"1\"/><xs:element type=\"xs:byte\" name=\"phlebotomistId\" minOccurs=\"1\"/><xs:element name=\"labTests\" minOccurs=\"1\"><xs:complexType><xs:sequence><xs:element name=\"test\" maxOccurs=\"unbounded\" minOccurs=\"1\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:int\" name=\"id\" use=\"required\"/><xs:attribute type=\"xs:float\" name=\"dxcode\" use=\"required\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>";
    private String updateAppointmentXSD ="<xs:schema attributeFormDefault=\"unqualified\" elementFormDefault=\"qualified\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"><xs:element name=\"appointment\"><xs:complexType><xs:sequence><xs:element type=\"xs:date\" name=\"date\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element type=\"xs:string\" name=\"time\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element type=\"xs:short\" name=\"patientId\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element type=\"xs:byte\" name=\"physicianId\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element type=\"xs:short\" name=\"pscId\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element type=\"xs:byte\" name=\"phlebotomistId\" minOccurs=\"0\" maxOccurs=\"1\"/><xs:element name=\"labTests\" minOccurs=\"0\" maxOccurs=\"1\"><xs:complexType><xs:sequence><xs:element name=\"test\" maxOccurs=\"unbounded\" minOccurs=\"0\"><xs:complexType><xs:simpleContent><xs:extension base=\"xs:string\"><xs:attribute type=\"xs:int\" name=\"id\" use=\"required\"/><xs:attribute type=\"xs:float\" name=\"dxcode\" use=\"required\"/></xs:extension></xs:simpleContent></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:sequence></xs:complexType></xs:element></xs:schema>";
    //Appointment ID
    private int appId ;

    //Used for data access
    List<Object> objs;

    //XML input data variables
    private String date;
    private String apptTime;
    private String patientId;
    private String physicianId;
    private String pscId;
    private String phlebotomistId;
    private HashMap<String,String> testData;
    LocalTime t;

    //Object created through the checking of input variables against the database
    private Appointment appointmentObject;
    private Patient patientObject;
    private Physician physicianObject;
    private PSC pscObject;
    private Phlebotomist phlebotomistObject;
    private ArrayList<AppointmentLabTest> appointmentLabTestArrayList;
    private LabTest labTest;
    private Diagnosis diagnosis;


    /**
     * Constructor to initialize the variables
     */
    public InfoCheckHelper(){
        dbSingleton = DBSingleton.getInstance();
        objs = new ArrayList<>();
        testData =new HashMap<>();
        appointmentLabTestArrayList = new ArrayList<>();
    }

    /**
     * To validate the xml with xsd
     * @param xml xml doc as string from client
     * @param xsd xml doc as string (Stored as private var)
     * @return boolean
     */
    public boolean validateXML(String xml,String xsd){
        try {
            System.out.println(xml);
            SchemaFactory factory =
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new InputStreamReader(IOUtils.toInputStream(xsd,"UTF-8"))));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new InputStreamReader(IOUtils.toInputStream(xml,"UTF-8"))));
        } catch (Exception e) {
            e.printStackTrace();
//            System.out.println(e);
            this.errorFlag = true;
            this.errorMsg.add("XML document validation failed.Malformed XML");
            return false;
        }
        return true;
    }

    /**
     * Getter fn for errormessages
     * @return arraylist of errormessage
     */
    public ArrayList<String> getErrorMsg(){
        return this.errorMsg;
    }

    /**
     * Parse the xml to get the attributes
     * @param xml xml doc from client
     */
    public void parseXML(String xml) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new InputStreamReader(IOUtils.toInputStream(xml,"UTF-8"))));
            NodeList nList = doc.getElementsByTagName("appointment");
            NodeList nChildNodes = nList.item(0).getChildNodes();
            for(int i=0;i<nChildNodes.getLength();i++) {
                if (nChildNodes.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nChildNodes.item(i);
                    if (eElement.getTagName() == "date") {
                        date = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "time") {
                        apptTime = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "patientId") {
                        patientId = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "physicianId") {
                        physicianId = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "pscId") {
                        pscId = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "phlebotomistId") {
                        phlebotomistId = eElement.getTextContent();
                    }
                    if (eElement.getTagName() == "labTests") {
                        for (int j = 0; j < eElement.getElementsByTagName("test").getLength(); j++) {
                            String id = eElement.getElementsByTagName("test").item(j).getAttributes().getNamedItem("id").getNodeValue();
                            String dxcode = eElement.getElementsByTagName("test").item(j).getAttributes().getNamedItem("dxcode").getNodeValue();
                            testData.put(id, dxcode);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.errorFlag=true;
            this.errorMsg.add("Error in XML parsing "+ e);
        }
    }

    /**
     * To check other appointments
     * @return
     */
    public boolean checkOtherAppointments(){
        if(!checkTime(apptTime) || !checkTime(t.minusMinutes(15).toString()) || !checkTime(t.plusMinutes(15).toString())){
            this.errorFlag=true;
            this.errorMsg.add("Appointment can be fixed only from 8am to 5pm");
            return false;
        }
        List<Object> objs;
        objs = dbSingleton.db.getData("Appointment",
                "apptdate='"+date+"' AND pscid='"+pscId+"' AND phlebid='"+phlebotomistId+"' AND appttime between '"+t.minusMinutes(15).toString()+"' AND '"+t.plusMinutes(15).toString()+"' ");
//        System.out.println(objs);
        if(objs.isEmpty() | objs == null){
            return true;
        }else{
            this.errorFlag=true;
            this.errorMsg.add("Appointment is not available at the requested time");
            return false;
        }
    }

    /**
     * Check business rules
     * @return  boolean value
     */
    public boolean checkRules(){
        if(this.checkValidPatient() && this.checkValidPhysician() && this.checkValidPhlebotomist()
                && this.checkValidPSC()){
            if(this.checkOtherAppointments()){
                getlastAppointmentId();
                if(this.checkCollectionOfTests(Integer.toString(this.appId))) {
                    return true;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * Update appointment in table
     * @param id the appointment to be updated
     * @return boolean value
     */
    public boolean updateAppointmentHelper(String id){
        if(checkValidAppointment(id)) {
            if(this.date!=null){
                appointmentObject.setApptdate(java.sql.Date.valueOf(date));
            }
            if (this.apptTime != null) {
                if(checkTime(apptTime)){
                    appointmentObject.setAppttime(java.sql.Time.valueOf(LocalTime.parse(this.apptTime)));
                }
            }
            if (this.patientId != null) {
                if(checkValidPatient()){
                    appointmentObject.setPatientid(this.patientObject);
                }
            }
            if (this.physicianId != null ) {
                checkValidPhysician();
            }

            if (this.phlebotomistId != null ) {
                if(checkValidPhlebotomist()){
                    appointmentObject.setPhlebid(this.phlebotomistObject);
                }
            }

            if (this.pscId != null) {
                if(checkValidPSC()){
                    appointmentObject.setPscid(this.pscObject);
                }
            }

            if (this.testData.size() > 0) {
                HashMap<String,String> oldTestDiagMaps = new HashMap<>();
                List<AppointmentLabTest> oldLabTest = appointmentObject.getAppointmentLabTestCollection();
                for(AppointmentLabTest oneLabTest : oldLabTest){
                    oldTestDiagMaps.put(oneLabTest.getLabTest().getId(),oneLabTest.getDiagnosis().getCode());
                }
                if(checkCollectionOfTests(id,oldTestDiagMaps)){
                    appointmentObject.setAppointmentLabTestCollection(appointmentLabTestArrayList);
                }
            }
            if(this.errorFlag==true) {
                return false;
            }
            return true;
        }
        return false;
    }

    /**
     * Check if appointment is valid
     * @param id the id of the appointment
     * @return
     */
    private boolean checkValidAppointment(String id){
        if(id == null | id.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Appointment ID is Empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("Appointment","id='"+id+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Appointment ID is invalid");
            return false;
        }else{
            this.appointmentObject = (Appointment) objs.get(0);
//            System.out.println(this.patientObject);
        }
        return true;
    }

    /**
     * Check if patient id is valid
     * @return boolean
     */
    private boolean checkValidPatient(){
        if(this.patientId == null | this.patientId.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Patient Id is empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("Patient","id='"+this.patientId+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Patient ID is invalid");
            return false;
        }else{
            this.patientObject = (Patient) objs.get(0);
//            System.out.println(this.patientObject);
        }
        return true;
    }


    /**
     * Check if Physician id is valid
     * @return boolean
     */
    private boolean checkValidPhysician(){
        if(this.physicianId == null | this.physicianId.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Physician ID is empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("Physician","id='"+this.physicianId+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Physician ID is invalid");
            return false;
        }else {
            this.physicianObject = (Physician) objs.get(0);
//            System.out.println(this.physicianObject);
        }
        return true;
    }


    /**
     * Check if PSC id is valid
     * @return boolean
     */
    private boolean checkValidPSC(){
        if(this.pscId == null | this.pscId.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("PSC ID is empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("PSC","id='"+this.pscId+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("PSC ID is invalid");
            return false;
        }else {
            this.pscObject = (PSC) objs.get(0);
//            System.out.println(this.pscObject);
        }
        return true;
    }


    /**
     * Check if Phlebotomist id is valid
     * @return boolean
     */
    private boolean checkValidPhlebotomist(){
        if(this.phlebotomistId == null | this.phlebotomistId.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Phlebotomist ID is empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("Phlebotomist","id='"+this.phlebotomistId+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Phlebotomist ID is invalid");
            return false;
        }else{
            this.phlebotomistObject = (Phlebotomist) objs.get(0);
//            System.out.println(this.phlebotomistObject);
        }
        return true;
    }

    /**
     * Check if test code is valid
     * @param id id of the test code
     * @return boolean
     */
    private boolean checkTestId(String id){
        if(id == null | id.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Test ID is Empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("LabTest","id='"+id+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Test ID is invalid");
            return false;
        }else{
            this.labTest = (LabTest) objs.get(0);
//            System.out.println(this.labTest);
        }
        return true;
    }


    /**
     * Check if diagnosis code is valid
     * @param id code of diagnosis
     * @return boolean
     */
    private  boolean checkDiagnosisCode(String id){
        if(id == null | id.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Diagnosis Code is Empty");
            return false;
        }
        objs.clear();
        objs = dbSingleton.db.getData("Diagnosis","code='"+id+"'");
        if(objs == null | objs.isEmpty()){
            this.errorFlag=true;
            this.errorMsg.add("Diagnosis Code is invalid");
            return false;
        }else{
            this.diagnosis = (Diagnosis) objs.get(0);
//            System.out.println(this.diagnosis);
        }
        return true;
    }

    /**
     * Get last appointment ID
     */
    private void getlastAppointmentId(){
        objs.clear();
        objs = dbSingleton.db.getData("Appointment"," id = (SELECT MAX(id) FROM components.data.Appointment)");
        if(objs==null | objs.isEmpty()){
            this.errorFlag = true;
            this.errorMsg.add("Appointment ID cannot be retrieved");
        }
//        System.out.println(objs);
        this.appId = Integer.parseInt(((Appointment) objs.get(0)).getId())+1;
//        System.out.println(appId);
    }

    /**
     * Check the testcode and dxcode in table
     * @param id appointment id
     * @return
     */
    private  boolean checkCollectionOfTests(String id){
        Iterator it = this.testData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(checkTestId(pair.getKey().toString())){
                if(checkDiagnosisCode(pair.getValue().toString())){
                    AppointmentLabTest appointmentLabTest = new
                            AppointmentLabTest(id,pair.getKey().toString(),pair.getValue().toString());
                    appointmentLabTest.setDiagnosis(this.diagnosis);
                    appointmentLabTest.setLabTest(this.labTest);
                    appointmentLabTestArrayList.add(appointmentLabTest);
                    continue;
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
//        System.out.println(appointmentLabTestArrayList.toString());
        return true;
    }

    /**
     * New test code and diagnosis code
     * @param id appointment id
     * @param oldTestMaps old database testocde and diagnosis
     * @return
     */
    private boolean checkCollectionOfTests(String id,HashMap<String,String> oldTestMaps){
        Iterator it = this.testData.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(checkTestId(pair.getKey().toString())){
                if(checkDiagnosisCode(pair.getValue().toString())) {
//                    if(oldTestMaps.containsKey(pair.getKey())){
                    oldTestMaps.put(pair.getKey().toString(), pair.getValue().toString());
                    continue;
//                }
                }else{
                    return false;
                }
            }else{
                return false;
            }
        }
        Iterator it1 = oldTestMaps.entrySet().iterator();
        while(it1.hasNext()){
            Map.Entry pair = (Map.Entry)it1.next();
            AppointmentLabTest appointmentLabTest = new
                    AppointmentLabTest(id,pair.getKey().toString(),pair.getValue().toString());
            appointmentLabTest.setDiagnosis(this.diagnosis);
            appointmentLabTest.setLabTest(this.labTest);
            appointmentLabTestArrayList.add(appointmentLabTest);
            continue;
        }
        return true;
    }

    /**
     * Check if service is alive
     * @param apptTime
     * @return
     */
    private boolean checkTime(String apptTime){
        String tokens[] = apptTime.split(":");
        t = LocalTime.of(Integer.parseInt(tokens[0]),Integer.parseInt(tokens[1]));
//        t = LocalTime.parse(apptTime);
        LocalTime startTime = LocalTime.of(8,00);
        LocalTime endTime = LocalTime.of(16,59);
        if(t.isAfter(startTime) && t.isBefore(endTime)){
            return true;
        }
        this.errorFlag=true;
        this.errorMsg.add("Appointment time is outside working hours");
        return false;
    }
    public String getDate() {
        return date;
    }

    public String getApptTime() {
        return apptTime;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getPhysicianId() {
        return physicianId;
    }

    public String getPscId() {
        return pscId;
    }

    public String getPhlebotomistId() {
        return phlebotomistId;
    }

    public HashMap<String,String> getTestData(){
        return this.testData;
    }


    public Patient getPatientObject() {
        return patientObject;
    }


    public Physician getPhysicianObject() {
        return physicianObject;
    }


    public PSC getPscObject() {
        return pscObject;
    }

    public Phlebotomist getPhlebotomistObject() {
        return phlebotomistObject;
    }


    public ArrayList<AppointmentLabTest> getAppointmentLabTestArrayList() {
        return appointmentLabTestArrayList;
    }


    public int getAppId() {
        return appId;
    }

    public void setAppId(int appId) {
        this.appId = appId;
    }


    public String getAppointmentXSD() {
        return appointmentXSD;
    }

    public String getUpdateAppointmentXSD() {
        return updateAppointmentXSD;
    }

    public Appointment getAppointmentObject() {
        return appointmentObject;
    }
}
