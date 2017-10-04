package com.vyoms.whatsapp.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ValidateMobileNumber
{
        PATTERN_1("[+]?[0-9]{10}"), PATTERN_2("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$"), PATTERN_3("[0-9]{10}")
        ,PATTERN_4("[+]?[0-9]{2}[-][0-9]{10}"),PATTERN_5("[+]?[0-9]{12}");

        String  pattern = "";

        ValidateMobileNumber(String pattern)
        {
                this.pattern = pattern;
        }

        /**
         * @return the pattern
         */
        public String getPattern()
        {
                return pattern;
        }

        public static boolean isValidMsisdn(String msisdn)
        {
                for (ValidateMobileNumber regex : values())
                {
                    Pattern pattern = Pattern.compile(regex.getPattern());
                    Matcher matcher = pattern.matcher(msisdn);
                    if(matcher.matches())
                           return true;
                }
                return false;
        }
        
       /* public static void main(String[] args)
        {
                String[] msisdnArray = new String[]{"1234567890", "911234567890", "123-456-7890", "+911234567890", "+91-1234567890", "+91-123-4567890"};
                for(String msisdn:msisdnArray)
                System.out.println("Msisdn  "+ msisdn +" is "+(ValidateMobileNumber.isValidMsisdn(msisdn)? "Valid": "InValid"));
        }*/
}