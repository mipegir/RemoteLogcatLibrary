package micsoftware.es.remotelogcat;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguelangel.perez on 21/10/2017.
 */

public class HighLighting {
    //Retrofit tags
    private static String TAG_END_REQUEST_RETROFIT = ": --> END";
    private static String TAG_START_REQUEST_RETROFIT = ": --> ";
    private static String TAG_END_RESPONSE_RETROFIT = ": <-- END";
    private static String TAG_START_RESPONSE_RETROFIT = ": <-- ";
    private static String TAG_JSON_CONTENT_VALUE = "#JSON_CONTENT_VALUE#";
    private static String TAG_LINE_NUMBER = "#LINE_NUMBER#";
    private static String TAG_LINE_LABEL = "#LINE_LABEL#";


    public static String applyHighLighting(String inputStr, int lineNumber) {

        if (inputStr!=null && !inputStr.isEmpty()) {
            //Requests
            if (inputStr.contains(TAG_END_REQUEST_RETROFIT)
                    ) {
                return boldHighLighting(inputStr);
            }else if(inputStr.contains(TAG_START_REQUEST_RETROFIT)) {
                return previousJumpHighLighting(boldHighLighting(inputStr));
            }
            //Responses
            else if (inputStr.contains(TAG_END_RESPONSE_RETROFIT)) {
                return boldHighLighting(inputStr);
            }else if (inputStr.contains(TAG_START_RESPONSE_RETROFIT)) {
                return previousJumpHighLighting(boldHighLighting(inputStr));
            }
            //JSON HightLighting
            else if(containsJSONValue(inputStr)){
                return formatJSONLine(inputStr, lineNumber);
            }
        }
        return inputStr;
    }

    private static String formatJSONLine(String inputStr, int lineNumber) {
        String jsonContentFormat = "<div class=\"jsonzone\"><span class=\"jsonlabel\" onclick=\"document.getElementById('idLineTag#LINE_NUMBER#').innerHTML=syntaxHighlight(JSON.stringify(JSON.parse(document.getElementById('idLineTag#LINE_NUMBER#').innerHTML),undefined,  4));onInputSearchClick();\">#LINE_LABEL#</span><pre style=\"background-color:white\"><code id=\"idLineTag#LINE_NUMBER#\">#JSON_CONTENT_VALUE#</code></pre></div>";

        if (containsJSONValue(inputStr)) {
            return jsonContentFormat
                .replace(TAG_LINE_NUMBER, String.valueOf(lineNumber))
                .replace(TAG_LINE_LABEL, extractLineLabel(inputStr))
                .replace(TAG_JSON_CONTENT_VALUE, extractJSONValue(inputStr));
        }else{
            return inputStr;
        }
    }

    @NonNull
    private static String extractLineLabel(String inputStr) {
        return inputStr.substring(0, inputStr.indexOf("{"));
    }

    private static String extractJSONValue(String inputString){
        return inputString.substring(inputString.indexOf("{"), inputString.length());
    }

    private static boolean containsJSONValue(String inputStr) {
        if (inputStr!=null && !inputStr.isEmpty() && inputStr.contains("{") && inputStr.endsWith("}")){
            return isJSONValid(extractJSONValue(inputStr));  //parse & validate object
        }
        return false;
    }

    private static boolean isJSONValid(String test) {
        try {
            JSONObject object = new JSONObject(test);
            if (object.length()>0) return true;
            return false;
        } catch (JSONException ex) {
            try {
                new JSONArray(test);
            } catch (JSONException ex1) {
                return false;
            }
        }
        return true;
    }

    public static String boldHighLighting(String inputStr){
        return "<b>" + inputStr + "</b>";
    }

    public static String previousJumpHighLighting(String inputStr){
        return "<br/>" + inputStr;
    }

    public static String postJumpHighLighting(String inputStr){
        return inputStr + "<br/>";
    }
}
