/**
 * Ics2txt - Convert .ics file (ICalendar) to readable text 
 *   > javac Ics2txt.java
 *   > java Ics2txt data\DoctorAppointment.ics
 *
 * see also vscode extension iCalendar
 *   https://www.kanzaki.com/docs/ical/uid.html
 * google: .ics file type
 *   https://en.wikipedia.org/wiki/ICalendar
 * google: iCalendar DTSTAMP format
 *   (https://www.kanzaki.com/docs/ical/dtstamp.html)
 *   [4.3.5 Date-Time](https://www.kanzaki.com/docs/ical/dateTime.html)
 *     FORM #1: DATE WITH LOCAL TIME
 *     FORM #2: DATE WITH UTC TIME   N.B. ends in Z
 * 
 * [[Baeldung] Read a File into an ArrayList](https://www.baeldung.com/java-file-to-arraylist)
 * 
 * arg[0] == data/DoctorAppointment.ics
 * arg[0] == c:\1d\JavaPjs\UtilExPjs\ics24txt\data\DoctorAppointment.ics
 * Joe N.B. problem copying example code from web 
 *    Syntax error on token "Invalid Character", delete this token
 *        at Ics2txt.main(Ics2txt.java:33)
 *    RETYPING FIXED PROBLEM
 * 
 * 
 * for
 *   C:\1d\JavaPjs\UtilExPjs\ics24txt>  javac -version 
 *     javac 12.0.2
 *   C:\1d\JavaPjs\UtilExPjs\ics24txt>  java -version 
 *     java version "12.0.2" 2019-07-16
 *     Java(TM) SE Runtime Environment (build 12.0.2+10
 *     Java HotSpot(TM) 64-Bit Server VM (build 12.0.2+10, mixed mode, sharing)
 * 
 *  java snippets already in with Language Support for Java(TM) by Red Hat redhat.java
 *    see https://github.com/redhat-developer/vscode-java/blob/master/snippets/java.json
 * 
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Ics2txt {
  private static String insertHyphensColons(String instr){
    String outstr = "";
    outstr += instr.substring(0,4);
    outstr += "-";
    outstr += instr.substring(4,6);
    outstr += "-";

    outstr += instr.substring(6,11);
    outstr += ":";

    outstr += instr.substring(11,13);
    outstr += ":";

    outstr += instr.substring(13,15);
    outstr += ".000";

    outstr += instr.substring(15);
    return outstr;
  }
  private static String convertZuluToPST(String inputValue){
    // https://stackoverflow.com/a/46011166/601770
    // String inputZuluValue = "2012-08-15T22:56:02.038Z";
    String hyphensColons = insertHyphensColons(inputValue);
    Instant timestamp = Instant.parse(hyphensColons);
    ZonedDateTime losAngelesDateTime = timestamp.atZone(ZoneId.of("America/Los_Angeles"));
    
    // https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
      // The count of pattern letters determines the format.
      // The text style is determined based on the number of pattern letters used. Less than 4 pattern letters will use the short form. Exactly 4 pattern letters will use the full form. Exactly 5 pattern letters will use the narrow form.
    // String formattedDateTime = DateTimeFormatter.ofPattern("dd/MM/yyyy - hh:mm").format(losAngelesDateTime);
    // String formattedDateTime = DateTimeFormatter.ofPattern("E L d, u @ hh:mm a").format(losAngelesDateTime); // Wed 9 25, 2019 @ 08:30 AM
    // String formattedDateTime = DateTimeFormatter.ofPattern("E MMMM d, u @ hh:mm a").format(losAngelesDateTime); // Wed September 25, 2019 @ 08:30 AM
    // String formattedDateTime = DateTimeFormatter.ofPattern("E MMMMM d, u @ hh:mm a").format(losAngelesDateTime); // Wed S 25, 2019 @ 08:30 AM
    String formattedDateTime = DateTimeFormatter.ofPattern("E MMM d, u @ hh:mm a").format(losAngelesDateTime); // Wed Sep 25, 2019 @ 08:30 AM
    return formattedDateTime;
  }

  private static ArrayList<String> parseLines(ArrayList<String> inArrayList) {
    ArrayList<String> outArrayList = new ArrayList<String>();  
    outArrayList.add("begin");
    for (String line : inArrayList) {
      if (line.startsWith("UID:")) {
        outArrayList.add("Event Unique ID: "+line.substring("UID:".length()));
      } else if (line.startsWith("DESCRIPTION;LANGUAGE=en-US:Phone:")) {
        outArrayList.add("Event Phone: "+line.substring("DESCRIPTION;LANGUAGE=en-US:Phone:".length()));         
      } else if (line.startsWith("SUMMARY;LANGUAGE=en-US:")) {
        outArrayList.add("Event Summary: "+line.substring("SUMMARY;LANGUAGE=en-US:".length()));         
      } else if (line.startsWith("LOCATION;LANGUAGE=en-US:")) {
        outArrayList.add("Location: "+line.substring("LOCATION;LANGUAGE=en-US:".length()));         
      } else if (line.startsWith("ORGANIZER;CN=")) {
        outArrayList.add("Organizer Contact Name: "+line.substring("ORGANIZER;CN=".length()));         
      } else if (line.startsWith("DTSTAMP:")) {
        outArrayList.add("Event Create Date Time: "+convertZuluToPST(line.substring("DTSTAMP:".length())));         
       } else if (line.startsWith("DTSTART:")) {
        outArrayList.add("Event Start Date Time: "+convertZuluToPST(line.substring("DTSTART:".length())));         
      } else if (line.startsWith("DTEND:")) {
        outArrayList.add("Event End Date Time: "+convertZuluToPST(line.substring("DTEND:".length())));         
      } else {
        outArrayList.add("no translation: "+line);
      }
      String outStr = outArrayList.get(outArrayList.size()-1);
      if (!outStr.contains("no translation: ")) System.out.println(outStr);
    }
    return outArrayList;
  }

  public static void main(String[] args) {
    ArrayList<String> icsStrA = new ArrayList<String>();
    System.out.println("\n\n");
    String inFilePath = args[0];
    String currentDirectory = System.getProperty("user.dir");

    // System.out.println("Your inFilePath is: "+inFilePath);
    // System.out.println("Your currentDirectory is: "+currentDirectory);

    try (BufferedReader br = new BufferedReader(new FileReader(inFilePath))) {
      while (br.ready()) {
        String inStr = br.readLine();
       
        // compute in here in on inStr

        icsStrA.add(inStr);
        // System.out.println(inStr);
      }
    }catch(IOException e){
      System.out.println("Your IOException is: "+e.toString());
    }
    System.out.println("Your icsStrA.size() is: "+icsStrA.size());

    // String testInputZuluValue = "2012-08-15T22:56:02.038Z";
    // String dtstart = "20190925T153000Z";
    // String dtstartHyphenColon = "2019-09-25T15:30:00.000Z";

    // System.out.println("Your convertZuluToPST(testInputZuluValue) is: "+convertZuluToPST(testInputZuluValue));
    // System.out.println("Your convertZuluToPST(dtstart) is: "+convertZuluToPST(dtstart));
    // System.out.println("Your convertZuluToPST(dtstartHyphenColon) is: "+convertZuluToPST(dtstartHyphenColon));
    
    parseLines(icsStrA);
  }
}

/* Compile & Run Output

> javac Ics2txt.java

> java Ics2txt data\DoctorAppointment.ics



Your icsStrA.size() is: 15
Event Unique ID: mdOBFUSCATEDPnyOBFUSCATED==@mychart.example.com
Organizer Contact Name: Division Health MyChart:mychart.support@xyzmcd.example.com
Event Create Date Time: Fri Sep 20, 2019 @ 09:41 AM
Event Start Date Time: Wed Sep 25, 2019 @ 08:30 AM
Event End Date Time: Wed Sep 25, 2019 @ 09:00 AM
Event Summary: Office Visit with Something Orother\, MD
Location: 1313 Blueview Terrace STE 947\, Oroville CA 95555-5555
Event Phone:  555-555-1212\n


*/