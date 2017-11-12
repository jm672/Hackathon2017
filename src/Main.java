import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.dstu2.resource.DiagnosticReport;


import ca.uhn.fhir.model.dstu2.resource.Patient;
import ca.uhn.fhir.parser.IParser;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.exceptions.FHIRException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;


public class Main {


    public static void main(String[] args) throws FHIRException {

        //Create ctx and parsers
        FhirContext ctx = FhirContext.forDstu2();
        FhirContext ctx2 = FhirContext.forDstu3();
        IParser parser = ctx.newJsonParser();
        IParser parser2 = ctx2.newJsonParser();

        //Calls the diagnostic Report
        String dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/DiagnosticReport/DiagnosticReport-1598088/_history/1?_format=json&_pretty=true");
        DiagnosticReport diagnostic = parser.parseResource(DiagnosticReport.class, dataString);

        //Extracting info from the diagnostic report to grab from observations
        String heightLOC = diagnostic.getResult().get(0).getReference().getValue();
        String weightLOC = diagnostic.getResult().get(1).getReference().getValue();
        String BMILOC = diagnostic.getResult().get(2).getReference().getValue();
        String TemperatureLOC = diagnostic.getResult().get(3).getReference().getValue();
        String SystolicBPLOC = diagnostic.getResult().get(4).getReference().getValue();
        String DiastolicBPLOC = diagnostic.getResult().get(5).getReference().getValue();

        //Call separate observation reports and grab relevant data

        //height (cm)
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + heightLOC + "/_history/1?_format=json&_pretty=true");
        Observation obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal height = obs.getValueQuantity().getValue();

        //weight
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + weightLOC + "/_history/1?_format=json&_pretty=true");
        obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal weight = obs.getValueQuantity().getValue();

        //BMI
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + BMILOC + "/_history/1?_format=json&_pretty=true");
        obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal BMI = obs.getValueQuantity().getValue();

        //Temperature
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + TemperatureLOC + "/_history/1?_format=json&_pretty=true");
        obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal temperature = obs.getValueQuantity().getValue();

        //Systolic Blood Pressure
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + SystolicBPLOC + "/_history/1?_format=json&_pretty=true");
        obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal SystolicBP = obs.getValueQuantity().getValue();

        //Diastolic Blood Pressure
        dataString = callURL("http://52.200.214.141:8080/fhir/baseDstu3/" + DiastolicBPLOC + "/_history/1?_format=json&_pretty=true");
        obs = parser2.parseResource(Observation.class, dataString);
        BigDecimal DiastolicBP = obs.getValueQuantity().getValue();



    }

    public static String callURL(String myURL) {
        System.out.println("Requested URL:" + myURL);
        StringBuilder sb = new StringBuilder();
        URLConnection urlConn = null;
        InputStreamReader in = null;
        try {
            URL url = new URL(myURL);
            urlConn = url.openConnection();
            if (urlConn != null)
                urlConn.setReadTimeout(60 * 1000);
            if (urlConn != null && urlConn.getInputStream() != null) {
                in = new InputStreamReader(urlConn.getInputStream(),
                        Charset.defaultCharset());
                BufferedReader bufferedReader = new BufferedReader(in);
                if (bufferedReader != null) {
                    int cp;
                    while ((cp = bufferedReader.read()) != -1) {
                        sb.append((char) cp);
                    }
                    bufferedReader.close();
                }
            }
            in.close();
        } catch (Exception e) {
            throw new RuntimeException("Exception while calling URL:"+ myURL, e);
        }

        return sb.toString();
    }
}
