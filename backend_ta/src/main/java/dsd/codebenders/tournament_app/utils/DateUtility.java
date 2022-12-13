package dsd.codebenders.tournament_app.utils;

import java.util.Calendar;
import java.util.Date;

public class DateUtility {

    public static Date addSeconds(Date date, int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime();
    }

}
