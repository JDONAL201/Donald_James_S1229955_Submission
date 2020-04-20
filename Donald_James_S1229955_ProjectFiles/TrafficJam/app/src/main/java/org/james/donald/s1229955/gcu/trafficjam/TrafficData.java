package org.james.donald.s1229955.gcu.trafficjam;
/*
 * STUDENT NAME: JAMES DONALD
 * METRIC NUMBER : S1229955
 * COURSE: COMPUTER GAMES SOFTWARE DEVELOPMENT (YEAR 4)
 * */
public class TrafficData
{
    private String title,
                    description,
                    link,
                    geoLocation,
                    author,
                    comments,
                    publicationDate,
                    startDate,endDate;

    private  int iconId;
    private int[] startDateArr = new int[3];
    private int[] endDateArr =  new int[3];

    private double latitude,longitude;
    private String[] months = {"January","Febuary","March","April","May","June","July","August","September","October","November","December"};

    public TrafficData()
    {
        title = "";
        description = "";
        link = "";
        geoLocation = "";
        author = "";
        comments = "";
        publicationDate ="";
    }

    public String GetDisplayInfo()
    {
        String displayInfo ="Date: " + publicationDate + "\n" +"\n"+
                            "Description: " + description + "\n" +"\n"+
                            "Link: " + link + "\n" +"\n"+
                            "Location: " + geoLocation + "\n" +"\n"+
                            "Author: " + author +"\n" +"\n"+
                            "Comments: " + comments;

        String displayInfoFinal = displayInfo.replace("<br />" ," ");

        return displayInfoFinal;
    }
    //Getters
    public String GetTitle()
    {
        return  title;
    }
    public String GetDescription()
    {
        String displayInfoFinal = description.replace("<br />" ," ");
        return displayInfoFinal;
    }
    public String GetLink()
    {
        return link;
    }
    public String GetGeoLocation()
    {
        return geoLocation;
    }
    public String GetAuthor()
    {
        return author;
    }
    public String GetComments()
    {
        return comments;
    }
    public void SetDateRange(String sDate,String eDate)
    {
        startDate = sDate;
        endDate = eDate;

        String[] tempArr = startDate.split(" ",3);
        int day = Integer.parseInt(tempArr[0]);
        int month = 0;

        for (int i = 0; i < months.length; i++)
        {
            if(tempArr[1].equalsIgnoreCase(months[i]))
            {
                month = i+1;
            }
        }
        int year = Integer.parseInt(tempArr[2]);

        startDateArr[0] = day;
        startDateArr[1] = month;
        startDateArr [2] = year;

        tempArr = endDate.split(" ",3);
        day = Integer.parseInt(tempArr[0]);
        month = 0;

        for (int i = 0; i < months.length; i++)
        {
            if(tempArr[1].equalsIgnoreCase(months[i]))
            {
                month = i+1;
            }
        }
        year = Integer.parseInt(tempArr[2]);
        endDateArr[0] = day;
        endDateArr[1] =month;
        endDateArr[2] =year;
    }
    public int[] GetStartDate()
    {
        return startDateArr;
    }
    public int[] GetEndDate()
    {
        return endDateArr;
    }
    public int GetIconId()
    {
        return iconId;
    }
    public void SetIconId(int id)
    {
        iconId = id;
    }
    public String GetPublicationDate()
    {
        return publicationDate;
    }
    public double GetLat()
    {
        return latitude;
    }
    public double GetLong()
    {
        return longitude;
    }
        //Setters
    public void SetTitle(String inTitle)
    {
        title = inTitle;
    }
    public void SetDescription(String inDescription)
    {
        description = inDescription;
    }
    public void SetLink(String inLink)
    {
        link = inLink;
    }
    public void SetGeoLocation(String inGeoLocation)
    {
        geoLocation = inGeoLocation;

        String[] splitString = inGeoLocation.trim().split("\\s+");
        double lat = Double.parseDouble(splitString[0]);
        double lng = Double.parseDouble(splitString[1]);

        latitude = lat;
        longitude = lng;
    }
    public void SetAuthor(String inAuthor)
    {
        author = inAuthor;
    }
    public void SetComments(String inComments)
    {
        comments = inComments;
    }
    public void SetPublicationDate(String inPublicationDate)
    {
        publicationDate = inPublicationDate;
    }

}
