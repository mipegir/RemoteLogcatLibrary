package micsoftware.es.remotelogcat;

import android.support.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by miguelangel.perez on 21/10/2017.
 */

public class HighLighting {

    //constants
    private static final int MAX_LINES_JSON_BUFFER_HEURISTICS = 10;

    //Retrofit tags
    private static final String TAG_END_REQUEST_RETROFIT = ": --> END";
    private static final String TAG_START_REQUEST_RETROFIT = ": --> ";
    private static final String TAG_END_RESPONSE_RETROFIT = ": <-- END";
    private static final String TAG_START_RESPONSE_RETROFIT = ": <-- ";
    private static final String TAG_REMOTE_LOGCAT_BOLD = "#RemoteLogcatBold#";
    private static final String TAG_JSON_CONTENT_VALUE = "#JSON_CONTENT_VALUE#";
    private static final String TAG_LINE_NUMBER = "#LINE_NUMBER#";
    private static final String TAG_LINE_LABEL = "#LINE_LABEL#";


    private static String tagBuffer = "";
    private static String jsonBuffer = "";
    private static int jsonBufferLines = 0;

    public static String applyHighLighting(String inputStr, int lineNumber) {

        restoreStateBetweenRequests(lineNumber);

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
            //user request to higtlighting bold
            else if (inputStr.contains(TAG_REMOTE_LOGCAT_BOLD)){
                return boldHighLighting(inputStr.replace(TAG_REMOTE_LOGCAT_BOLD, ""));
            }
            //JSON HightLighting
            else if(containsJSONValue(inputStr)){
                return formatJSONLine(inputStr, lineNumber);

            }else if (tryHeuristicJSONMessageOutbounds(inputStr)){
                if (jsonBuffer.isEmpty()){
                    tagBuffer = extractLineLabel(inputStr);
                    jsonBuffer=inputStr.replace(tagBuffer, "");
                }else{
                    jsonBuffer+=inputStr.replace(tagBuffer, "");
                }

                //Stop contion: json completed
                if(countMatches(jsonBuffer, "\\{")==countMatches(jsonBuffer, "\\}")){
                    String jsonComplete = tagBuffer + jsonBuffer;
                    jsonBuffer="";
                    tagBuffer="";
                    jsonBufferLines=0;
                    return formatJSONLine(jsonComplete, lineNumber);
                }

                //Stop condition: avoid infinite buffer
                jsonBufferLines++;
                if (jsonBufferLines >= MAX_LINES_JSON_BUFFER_HEURISTICS){
                    jsonBuffer="";
                    tagBuffer="";
                    jsonBufferLines=0;
                    return jsonBuffer;
                }
                return "";
            }
        }
        return inputStr;
    }

    //region standard json extraction
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
        if (inputStr!=null && !inputStr.isEmpty() && inputStr.contains("{") && inputStr.endsWith("}") && countMatches(inputStr, "\\{")==countMatches(inputStr, "\\}")){
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

    private static int countMatches(String inputStr, String strRegExToMatch){
        return (inputStr!=null && inputStr.length()>0 && strRegExToMatch!=null && strRegExToMatch.length()>0)
                ? inputStr.length() - inputStr.replaceAll(strRegExToMatch, "").length()
                : 0;
    }

    //endregion


    //region heuristic json extraction: json use several lines

    /**
     * Heuristics: Sometimes, a large response exceeds max weigth for the Android Logcat line. This method check if mixing several lines, is possiblo to create a valid JSON response
     * @param inputStr current line
     * @return it decides if this line can mix with others to create a unique valid response.
     */
    private static boolean tryHeuristicJSONMessageOutbounds(String inputStr) {
        if (inputStr!=null && !inputStr.isEmpty() && inputStr.contains("{") &&inputStr.contains("}")
                && (countMatches(inputStr, "\\{")>2 || countMatches(inputStr, "\\}")>2)  //hight chance that several lines have been used (multiline json)
                && countMatches(inputStr, "\\{") != countMatches(inputStr, "\\}")) {
            return true;
        }
        return false;
    }

    /*Avoid mixing heuristics state between several requests reseting static vars*/
    private static void restoreStateBetweenRequests(int lineNumber) {
        if (lineNumber == 0){
            tagBuffer = "";
            jsonBuffer = "";
            jsonBufferLines = 0;
        }
    }
    //end region


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
