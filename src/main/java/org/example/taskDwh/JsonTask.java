package org.example.taskDwh;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.javatuples.Quartet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/*
public class Tuple<Kt_id, Kl_id, Pr_id, St, Kt_ts>{
    private Kt_id kontakt_id;
    private Kl_id klient_id;
    private Pr_id pracownik_id;
    private St status;
    private Kt_ts kontakt_ts;
}

public Tuple (Kt_id kt_id, Kl_id kl_id, Pr_id pr_id, St st, Kt_ts kt_ts){
    this.kontakt_id = kt_id;
    this.klient_id = kl_id;
    this.pracownik_id = pr_id;
    this.status = st;
    this.kontakt_ts = kt_ts;
}
*/

public class JsonTask {
    private static class Sorting extends ArrayList<Sorting> {
        private Long kontakt_id;
        private Long klient_id;
        private Long pracownik_id;
        private String status;
        private Date kontakt_ts;

        public Sorting(Long kontakt_id, Long klient_id, Long pracownik_id, String status, Date kontakt_ts) {
            this.kontakt_id = kontakt_id;
            this.klient_id = klient_id;
            this.pracownik_id = pracownik_id;
            this.status = status;
            this.kontakt_ts = kontakt_ts;

        }

        public Long getKontakt_id() {
            return kontakt_id;
        }
        public Long getKlient_id(){
            return klient_id;
        }
        public Long getPracownik_id(){
            return  pracownik_id;
        }
        public String getStatus(){
            return  status;
        }
        private Date getKontakt_ts(){
            return kontakt_ts;
        }
    }


    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader("/home/emmanuel/Downloads/taskDwh/statuses.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONObject data = (JSONObject) obj;
            JSONArray data2 = (JSONArray) data.get("records");
            //Integer i = 0;
            //Empty list to append matched items
            ArrayList found = new ArrayList();
            List sorted = new ArrayList();
            List<Sorting> list = new ArrayList<>();



            for(int i = 0; i<data2.size(); i++){
                //Get record one by one
                JSONObject one_record = (JSONObject) data2.get(i);
                //Collect the date in string
                String date = (String) one_record.get("kontakt_ts");
                //Create a pattern for the date
                String pattern = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                //Date from the record
                Date d1 = simpleDateFormat.parse(date);
                //Date to compare to
                Date d2 = simpleDateFormat.parse("2017-06-30 23:59:59");
                if (d1.compareTo(d2) > 0) {
                    Long kontakt_id = (Long) one_record.get("kontakt_id");
                    Long klient_id = (Long) one_record.get("klient_id");
                    Long pracownik_id = (Long) one_record.get("klient_id");
                    String  status = (String) one_record.get("status");
                    String kontakt_ts = (String) one_record.get("kontakt_ts");
                    System.out.println(kontakt_ts);
                    List <Sorting>new_out = createElement(kontakt_id, klient_id, pracownik_id, status, d1);
                    System.out.println(Arrays.asList(new Sorting(kontakt_id, klient_id, pracownik_id, status, d1)));
                    System.out.println(createElement(kontakt_id, klient_id, pracownik_id,status, d1));
                    System.out.println(new_out);

                    //found.add(one_record);
                }
                }


            //System.out.println(sorted);


            //ArrayList result = found.stream().sorted(Comparator.comparing(Sorting::"Klient_id").thenComparing(Sorting::"kontakt_ts")).collect(Collectors.toList());
            //data2.forEach((JSONObject) data2.get(i));

            //System.out.println(data2.size());


            //System.out.println(data2);
             //Iterate over the object
            //data.forEach( record -> parseRecordArray ((JSONArray) record));

            //Iterate over employee array
            //employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
    }
    private static List<Sorting> createElement(Long klient_id, Long b, Long c, String d, Date e) {
        return Arrays.asList(new Sorting(klient_id, b, c, d, e));
    }
    /*
    private static void parseRecordArray (JSONArray records){
        JSONObject data2 = (JSONObject) records.get(1);
    }

     */
    /*

    private static void parseEmployeeObject(JSONObject employee)
    {
        //Get employee object within list
        JSONObject employeeObject = (JSONObject) employee.get("records");

        //Get employee first name
        String firstName = (String) employeeObject.get("firstName");
        System.out.println(firstName);

        //Get employee last name
        String lastName = (String) employeeObject.get("lastName");
        System.out.println(lastName);

        //Get employee website name
        String website = (String) employeeObject.get("website");
        System.out.println(website);
    }
    */

}