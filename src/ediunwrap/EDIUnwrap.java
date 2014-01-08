/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ediunwrap;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 *
 * @author laddu
 */
public class EDIUnwrap {
    private static int ISA_DELIM_POINT = 105;
    
    private static ArrayList<String> parse(String line)
    {
        ArrayList<String> segments = new ArrayList<String>();
        
        int ll = line.length();
        boolean bISA = false;
        boolean bSegmentStarted = false;
        char cDelim = '\0';
        char cDelimPastOne = '\0';
        char cDelimPastTwo = '\0';
        int index = 0;
        
        try
        {
            while (index < ll)
            {
                if (!bSegmentStarted) {
                    String block = line.substring(index, index + 3);
                    if (block.startsWith("ISA")) {
                        bISA = true;
                        
                        cDelim = line.charAt(index + ISA_DELIM_POINT);
                        if (index + ISA_DELIM_POINT + 1 < ll ) {
                            cDelimPastOne = line.charAt(index + ISA_DELIM_POINT + 1);
                        }
                        
                        String s = line.substring(index, index + ISA_DELIM_POINT);
                        index += ISA_DELIM_POINT;

                        ++index;
                        if (cDelimPastOne == '\r')
                        {
                            ++index;
                        }
                        
                        bSegmentStarted = true;
                        segments.add(s);
                        continue;
                    }
                    else if (block.startsWith("UNB")) {
                        bISA = false;
                        
                        cDelim = '\'';
                        
                        int delimLocation = line.indexOf(cDelim, index);
                        
                        if (delimLocation + 1 < ll ) {
                            cDelimPastOne = line.charAt(delimLocation + 1);
                        }
                        
                        String s = line.substring(index, delimLocation);
                        index = delimLocation;

                        ++index;
                        if (cDelimPastOne == '\r')
                        {
                            ++index;
                        }
                        bSegmentStarted = true;
                        segments.add(s);
                        continue;
                    }
                }
                else
                {
                    String part = line.substring(index, index + 3);
                    if (part.equals("ISA"))
                    {
                        bSegmentStarted = false;
                        continue;
                    }
                    else if (part.equals("UNB"))
                    {
                        bSegmentStarted = false;
                        continue;
                    }
                    else
                    {
                        int d = line.indexOf(cDelim, index);
                        if (d != -1)
                        {
                            part = line.substring(index, d);
                            segments.add(part);
                            index = d + 1;
                        }
                        else
                        {
                            segments.add(line.substring(index, line.length()));
                            index = line.length();
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println("Exception: " + e.getMessage());
        }
        
        return segments;
    }
    
    private static void displayFile(String filepath)
            throws java.io.IOException, java.io.UnsupportedEncodingException
    {
        try
        {
            // Open File
            File file = new File(filepath);

            // Open streams.
            FileInputStream fis = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));

            PrintStream outputStream = new PrintStream(System.out);

            outputStream.println("<html><head></head><body>");
            String header = new String("<b>" + filepath + "</b><br><br>");
            outputStream.println(header);

            String line = null;
            while ((line = br.readLine()) != null) {
                ArrayList<String> segments = parse(line);
                for (String s : segments)
                {
                    outputStream.println(s);
                }
            }
            br.close();
            fis.close();
            br = null;
            fis = null;

            outputStream.println("</body></html>");
            outputStream.flush();
        } catch (Exception e)
        {
            ; // 404.
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try  {
            displayFile("/Users/pvub/Development/2845908_c.txt");
        } catch (Exception e)
        {}
    }
}
