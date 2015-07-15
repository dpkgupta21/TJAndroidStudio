package com.traveljar.memories;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

class Test{
    public static void main(String args[]){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String enteredDate;
        Date date;
        int noDays;
        System.out.println("Enter the date (dd/MM/yyyy)");
        Scanner scanner = new Scanner(System.in);
        for(;;){
            enteredDate = scanner.nextLine();
            try{
                date = dateFormat.parse(enteredDate);
                break;
            }catch (ParseException e) {
                System.out.println("please enter a valid date");
                e.printStackTrace();
            }
        }
        System.out.println("Enter no of days to add");
        noDays = scanner.nextInt();

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, noDays); // Adding 5 days
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        System.out.print("day of week " + dayOfWeek);
        if(dayOfWeek == 6 || dayOfWeek == 7){
            System.out.println("Holiday");
        }else if(dayOfWeek == 3){
            System.out.println("Lazy Wednesday");
        }

    }
}
