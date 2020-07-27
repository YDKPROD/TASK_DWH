package org.example.taskDwh;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.opencsv.CSVWriter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


public class JsonTask {


    @SuppressWarnings("unchecked")
    public static void main(String[] args)
    {
        //JSON parser object to parse read file
        JSONParser jsonParser = new JSONParser();
        //Retrieve the file from the source
        try (FileReader reader = new FileReader("/home/emmanuel/Downloads/taskDwh/statuses.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            //Retrieve the JSON objects
            JSONObject data = (JSONObject) obj;
            //Retrieve the array "records" only
            JSONArray records = (JSONArray) data.get("records");
            //Create an empty list that will be used to store
            // the array Sorting which will be used to sort with the help of Stream
            List to_sort = new ArrayList();

            for(int i = 0; i<records.size(); i++){
                //Get record one by one
                JSONObject one_record = (JSONObject) records.get(i);
                //Collect the date in string
                String date = (String) one_record.get("kontakt_ts");
                //Create a pattern for the date
                String pattern = "yyyy-MM-dd HH:mm:ss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                //Date from the record
                Date d1 = simpleDateFormat.parse(date);
                //Date to compare to
                Date d2 = simpleDateFormat.parse("2017-06-30 23:59:59");
                //Check the matching rows
                if (d1.compareTo(d2) > 0) {
                    Long kontakt_id = (Long) one_record.get("kontakt_id");
                    Long klient_id = (Long) one_record.get("klient_id");
                    Long pracownik_id = (Long) one_record.get("pracownik_id");
                    String  status = (String) one_record.get("status");
                    //Add the columns that match in the list of items to sort (to_sort)
                    to_sort.add(new Sorting(kontakt_id, klient_id, pracownik_id, status, d1));
                }
                }
            //Compare first with the "klient_id"
            Comparator <Sorting> comparator = Comparator.comparing(sorting -> sorting.klient_id);
            //Then compare with the date (kontakt_ts)
            comparator = comparator.thenComparing(Comparator.comparing(sorting -> sorting.kontakt_ts));
            //Sequence that will be used while sorting items
            Stream <Sorting> sortingStream = to_sort.stream().sorted(comparator);
            //List of the items sorted already
            List<Sorting> sortedOnes = sortingStream.collect(Collectors.toList());

            //Create the CSV file for the output
            CSVWriter writer = new CSVWriter(new FileWriter("/home/emmanuel/Documents/JavaProjects/taskDwh/sorted_file.csv"));
            //Create the header for the CSV file
            String header[] = {"kontakt_id", "klient_id", "pracownik_id", "status", "kontakt_ts"};
            //Append the header to the file
            writer.writeNext(header);

            //Go through the sorted items
            for (int i = 0; i < sortedOnes.size(); i++){
                Long kontakt_id = (Long) sortedOnes.get(i).kontakt_id;
                Long  klient_id = (Long) sortedOnes.get(i).klient_id;
                Long pracownik_id = (Long) sortedOnes.get(i).pracownik_id;
                String status = (String) sortedOnes.get(i).status;
                Date kontakt_ts = (Date) sortedOnes.get(i).kontakt_ts;
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                //Create the row to append
                String data_to_copy [] = {Long.toString(kontakt_id), Long.toString(klient_id), Long.toString(pracownik_id), status, dateFormat.format(kontakt_ts)};
                //Copy the row to the file
                writer.writeNext(data_to_copy);
        }
            //close the file
            writer.close();

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
    //Demo class that will be used to store items before sorting
    public static class Sorting {
        public Long kontakt_id;
        public Long klient_id;
        public Long pracownik_id;
        public String status;
        public Date kontakt_ts;

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
        public Date getKontakt_ts(){
            return kontakt_ts;
        }

        @Override
        public String toString(){
            return kontakt_id + " - " + klient_id + " - " + pracownik_id + " - "+ status + " - "+ kontakt_ts;
        }
    }

}