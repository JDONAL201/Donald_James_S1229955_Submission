package org.james.donald.s1229955.gcu.trafficjam;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedList;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class TrafficDataParser
{
    private String urlRoadworks = "https://trafficscotland.org/rss/feeds/roadworks.aspx";
    private String urlPlannedWorks = "https://trafficscotland.org/rss/feeds/plannedroadworks.aspx";
    private String urlIncidents = "https://trafficscotland.org/rss/feeds/currentincidents.aspx";

    
    private LinkedList<TrafficData> incidents = new LinkedList<>();
    private LinkedList<TrafficData> roadworks = new LinkedList<>();
    private LinkedList<TrafficData> plannedRoadWorks= new LinkedList<>();

    public  void ParseData()
    {
        startProgress(urlRoadworks,"roadworks");
        startProgress(urlIncidents,"incidents");
        startProgress(urlPlannedWorks,"plannedRW");
    }
    private void startProgress(String urlSource,String inData)
    {
        //run network access on a separate thread
        new Thread(new TrafficDataParser.Task(urlSource, inData)).start();
    }

    private class Task implements Runnable
    {
        private String url;
        private  String result;
        private String dataGroup;

        public Task (String inUrl,String inData)
        {
            url = inUrl;
            dataGroup = inData;
        }
        @Override
        public void run()
        {
            URL aurl;
            URLConnection connection;
            BufferedReader bf = null;
            String inputLine = "";

            Log.e("MyTag","In Run");

            try
            {
                Log.e("MyTag", "In Try block");
                aurl = new URL(url);
                connection = aurl.openConnection();
                bf = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer bufferResult = new StringBuffer();

                while ((inputLine = bf.readLine()) != null)
                {
                    bufferResult.append(inputLine.trim());
                    Log.e("MyTag", inputLine);
                }

                result = bufferResult.toString();
                bf.close();
            }
            catch (IOException ae)
            {
                Log.e("MyTag", "ioexception");
            }

            LinkedList<TrafficData>trafficDataLinkedList = null;
            trafficDataLinkedList = ParseTrafficData(result);

            for(TrafficData td : trafficDataLinkedList)
            {
                if(dataGroup.equalsIgnoreCase("incidents"))
                {
                    td.SetIconId(0);
                    incidents.add(td);
                }
                else if(dataGroup.equalsIgnoreCase("roadworks"))
                {
                    td.SetIconId(1);
                    roadworks.add(td);
                }
                else if(dataGroup.equalsIgnoreCase("plannedRW"))
                {
                    td.SetIconId(2);
                    plannedRoadWorks.add(td);
                }
            }
            Log.e("MyTag", String.valueOf(incidents.size()));
            Log.e("MyTag", String.valueOf(roadworks.size()));
            Log.e("MyTag", String.valueOf(plannedRoadWorks.size()));

        }
    }


    public LinkedList<TrafficData> GetIncidents()
    {
        return incidents;
    }
    public LinkedList<TrafficData>GetRoadWorks()
    {
        return  roadworks;
    }
    public LinkedList<TrafficData>GetPlannedRoadWorks()
    {
        return plannedRoadWorks;
    }
    public LinkedList<TrafficData> ParseTrafficData(String dataToParse) {

        TrafficData tdWidget = null;
        LinkedList<TrafficData> trafficDataList = new LinkedList<TrafficData>();
        boolean begin = false;

        try
        {
            XmlPullParserFactory pFactory = XmlPullParserFactory.newInstance();
            pFactory.setNamespaceAware(false);
            XmlPullParser pullParser = pFactory.newPullParser();
            pullParser.setInput(new StringReader(dataToParse));
            int event = pullParser.getEventType();

            while (event != XmlPullParser.END_DOCUMENT)
            {
                switch (event)
                {
                    case XmlPullParser.START_TAG:
                        // Log.e("MyTag : ", pullParser.getName());

                        if (pullParser.getName().equals("channel"))
                        {
                            Log.e("MyTag : ", "Found channel");

//                            trafficDataList = new LinkedList<TrafficData>();
                        }
                        else if (pullParser.getName().equals("item"))
                        {
                            Log.e("MyTag : ", "ITEM CREATED");
                            tdWidget = new TrafficData();
                            begin = true;
                        }
                        else if (begin)
                        {
                            Log.e("MyTag : ", "NAME: " + pullParser.getName());

                            if (pullParser.getName().equalsIgnoreCase("title"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Title: " + temp);
                                tdWidget.SetTitle(temp);
                            }
                            else if (pullParser.getName().equalsIgnoreCase("description"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Description: " + temp);
                                tdWidget.SetDescription(temp);

                                if(temp.contains("Start Date"))
                                {
//                                    Log.e("MyTagDATE : ", "HAS START DATE");
                                    String extractedStartDate = temp.substring(temp.indexOf(",") + 2, temp.indexOf("-")-1);
                                    Log.e("MyTagDATE : ", "START:" + extractedStartDate);

                                    int startIndex = GetIndexOfOccurance(temp, ",", 2);
                                    int endIndex = GetIndexOfOccurance(temp, "-", 2);
                                    String extractedEndDate = temp.substring(startIndex + 2, endIndex-1);
                                    Log.e("MyTagDATE : ", "END:" + extractedEndDate);

                                    tdWidget.SetDateRange(extractedStartDate,extractedEndDate);
                                }
                            }
                            else if (pullParser.getName().equalsIgnoreCase("link"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Link: " + temp);
                                tdWidget.SetLink(temp);
                            }
                            else if (pullParser.getName().contains("point"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "GeoLocation: " + temp);
                                tdWidget.SetGeoLocation(temp);
                            }
                            else if (pullParser.getName().equalsIgnoreCase("author"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Author: " + temp);
                                tdWidget.SetAuthor(temp);
                            }
                            else if (pullParser.getName().equalsIgnoreCase("comments"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Comments: " + temp);
                                tdWidget.SetComments(temp);
                            }
                            else if (pullParser.getName().equalsIgnoreCase("pubDate"))
                            {
                                String temp = pullParser.nextText();
                                Log.e("MyTag : ", "Publication: " + temp);
                                tdWidget.SetPublicationDate(temp);
                            }
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if(pullParser.getName().equals("item"))
                        {
                            Log.e("MyTag: ", "ADDED TO LIST " + tdWidget.toString());
                            trafficDataList.add(tdWidget);
                        }
                        else if(pullParser.getName().equals("/channel"))
                        {
                            int size = trafficDataList.size();
                            Log.e("MyTag: ", "TRAFFIC_DATA_LIST SIZE: " + size);
                        }
                        break;
                }
                event = pullParser.next();
            }
        }
        catch (XmlPullParserException ae1)
        {
            Log.e("MyTag","Parsing error" + ae1.toString());
        }
        catch (IOException e)
        {
            Log.e("MyTag","IO error during parsing");
        }

        return trafficDataList;
    }

    public static int GetIndexOfOccurance(String str1, String str2, int n) {

        String tempStr = str1;
        int tempIndex = -1;
        int finalIndex = 0;
        for(int occurrence = 0; occurrence < n ; ++occurrence)
        {
            tempIndex = tempStr.indexOf(str2);
            if(tempIndex==-1)
            {
                finalIndex = 0;
                break;
            }
            tempStr = tempStr.substring(++tempIndex);
            finalIndex+=tempIndex;
        }
        return --finalIndex;
    }
}
