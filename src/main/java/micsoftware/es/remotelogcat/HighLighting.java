package micsoftware.es.remotelogcat;

/**
 * Created by miguelangel.perez on 21/10/2017.
 */

public class HighLighting {
    //Retrofit tags
    private static String TAG_END_REQUEST_RETROFIT = ": --> END";
    private static String TAG_START_REQUEST_RETROFIT = ": --> ";
    private static String TAG_END_RESPONSE_RETROFIT = ": <-- END";
    private static String TAG_START_RESPONSE_RETROFIT = ": <-- ";


    public static String applyHighLighting(String inputStr) {

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
        }
        return inputStr;
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
