package org.kablink.teaming.util.stringcheck;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.util.stringcheck.HtmlSanitizerCheck;
import org.kablink.teaming.util.stringcheck.XSSCheck;
import org.kablink.util.PropsUtil;

/**
 * Created by david on 4/28/16.
 */
public class HtmlSanitizerCheckTest  {

    static {
//        PropsUtil.getProperties().setProperty("xss.check.enable", "true");
//        PropsUtil.getProperties().setProperty("xss.check.mode.default", "trusted.strip");
//        PropsUtil.getProperties().setProperty("xss.check.mode.file", "trusted.disallow");
    }

    private HtmlSanitizerCheck sanitizer;
    private XSSCheck xssCheck;

    @Before
    public void setUp() throws Exception {
        sanitizer = new HtmlSanitizerCheck();
//        xssCheck = new XSSCheck();
    }

    public void testBasic() {
        String sanitized = sanitizer.check("<a href=\"javascript:alert(1)\" target=\"_blank\">clickme</a>");
        sanitized = sanitizer.check("<div class=\"fak_header\" style=\"font-size: 100%; height: 33px; font-family: verdana; background-image: url(http://vignette4.wikia.nocookie.net/despicableme/images/c/ca/Bob-from-the-minions-movie.jpg/revision/latest?cb=20151224154354); color: #fff; margin-top: 5px; padding-left: 10px; margin-left: 0px; line-height: 33px; padding-right: 0px; margin-right: 0px;\">Meddelelser fra holdlederen og l�rerne</div> <br>");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"http://localhost:8080/ssf/s/readFile/folderEntry/16/-/1461957717981/last/gedit-logo.png\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"{{attachmentUrl: somename.png}}\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"cid:{{attachmentUrl: somename.png}}\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/png;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/jpeg;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/gif;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:image/svg;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<img class=\" ss_addimage_att \" src=\"data:text/html;abcdef\" alt=\" \" />");
        sanitized = sanitizer.check("<p> {{youtubeUrl: url=https://www.youtube.com/watch?v=HRqZhJcae3M width=425 height=344}}</p>");
        sanitized = sanitizer.check("<img src='>' onerror='alert(1)'>");
        sanitized = sanitizer.check("<p><a href=\"\tjavascript:alert(1)\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"http://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"https://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"mailto://www.novell.com\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"cid:{{fsakjfldsa}}\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"data:text/html;abcde\">clickme</a></p>");
        sanitized = sanitizer.check("<p><a href=\"data:image/png;abcde\">clickme</a></p>");


        Assert.assertEquals("<a >clickme</a>", xssCheck.check("<a href=\"\tjavascript:alert(1)\">clickme</a>"));
    }

    @Test
    public void testMap() {
        String input = "<p>" +
            "<img style=\"width:1900px;z-index: -10;\" src=\"http://localhost:8080/ssf/s/readFile/workspace/213/09c1c01958d597eb0158d59a326e0012/1481052402000/last/IU%20strructure_new%207.jpg\" alt=\"IU-org-chart\" usemap=\"#IU-org-chart\" data-mce-src=\"https://workspace.csir.co.za/ssf/s/readFile/workspace/76374/-/1480017327269/last/IU strructure_new 7.jpg\" data-mce-style=\"width: 1900px; z-index: -10;\"> " +
                "<map id=\"IU-org-chart\" name=\"IU-org-chart\"> " +
                "<!-- ROW 01 --> " +
                "<area id=\"IU\" title=\"Implementation Unit\" shape=\"rect\" coords=\"815,38,1093,147\" href=\"https://workspace.csir.co.za/ssf/a/do?p_name=ss_forum&amp;p_action=1&amp;binderId=2333&amp;entityType=workspace&amp;action=view_permalink&amp;novl_url=1#1449038173627\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/do?p_name=ss_forum&amp;p_action=1&amp;binderId=2333&amp;entityType=workspace&amp;action=view_permalink&amp;novl_url=1#1449038173627\"> " +
                "<!-- Column 01 --> " +
                "<area id=\"Init\" title=\"CSIR Initiatives\" shape=\"rect\" coords=\"41,243,274,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76379/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76379/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Init_MI\" title=\"Mining Initiatives\" shape=\"rect\" coords=\"49,377,266,473\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76380/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76380/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Init_MI_ADL\" title=\"Air and Dust Labs\" shape=\"rect\" coords=\"49,506,266,601\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76381/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76381/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Init_MI_CL\" title=\"Cottesloe\" shape=\"rect\" coords=\"50,618,266,739\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76382/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76382/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Init_MI_HF\" title=\"Human Factors\" shape=\"rect\" coords=\"50,757,266,850\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76383/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76383/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Init_MI_KB\" title=\"Kloppersbos\" shape=\"rect\" coords=\"50,868,266,963\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 02 --> " +
                "<area id=\"Labs\" title=\"Laboratories\" shape=\"rect\" coords=\"298,243,532,353\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Labs_FB\" title=\"F&amp;B Laboratories\" shape=\"rect\" coords=\"312,424,518,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Labs_EL\" title=\"Environmental Labs\" shape=\"rect\" coords=\"312,539,518,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76406/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76406/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Labs_EL_PTA\" title=\"Environmental Labs - Pretoria Region\" shape=\"rect\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76407/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76407/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Labs_EL_SB\" title=\"Environmental Labs - Stellenbosh\" shape=\"rect\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76408/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76408/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"Labs_EL_DBN\" title=\"Environmental Labs - Durban Region\" shape=\"rect\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76409/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76409/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 03 --> " +
                "<area id=\"ECD\" title=\"Enterprise Creation for Development\" shape=\"rect\" coords=\"567,243,801,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76375/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76375/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"ED\" title=\"Enterprise Development\" shape=\"rect\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76376/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76376/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"ED_Pretoria\" title=\"Enterprise Development - Pretoria Region\" shape=\"rect\" coords=\"609,424,759,519\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76378/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76378/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"ED_Coastal\" title=\"Enterprise Development - Coastal Region\" shape=\"rect\" coords=\"609,539,759,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76377/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76377/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 04 --> " +
                "<area id=\"SS\" title=\"Specialist Services\" shape=\"rect\" coords=\"836,243,1070,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76391/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76391/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SS_EMS\" title=\"Environmental Management Services\" shape=\"rect\" coords=\"848,425,1057,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76392/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76392/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SS_JIOC\" title=\"Joint Interim Operations Centre (Project)\" shape=\"rect\" coords=\"849,537,1057,632\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76393/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76393/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SS_MSPS\" title=\"Municipal Strategic Programme Support\" shape=\"rect\" coords=\"850,654,1055,769\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76394/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76394/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SS_SIM\" title=\"Strategic Infrastructure Management\" shape=\"rect\" coords=\"849,786,1057,880\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76395/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76395/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 05 --> " +
                "<area id=\"SII\" title=\"Strategic Initiatives Implementation\" shape=\"rect\" coords=\"1090,243,1324,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76397/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76397/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_AISI\" title=\"Aviation Initiative Strategic Implementation\" shape=\"rect\" coords=\"1115,424,1340,519\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76398/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76398/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_COMP\" title=\"Composites\" shape=\"rect\" coords=\"1115,528,1339,582\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76399/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76399/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_NCPC\" title=\"National Cleaner Production Centre\" shape=\"rect\" coords=\"1115,593,1340,687\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76400/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76400/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_NFTN\" title=\"National Foundry Technology Network\" shape=\"rect\" coords=\"1115,697,1340,792\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76401/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76401/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_TLIU\" title=\"Technology Localisation Implementation Unit\" shape=\"rect\" coords=\"1115,802,1340,923\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76402/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76402/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_WASTE\" title=\"Waste\" shape=\"rect\" coords=\"1115,933,1340,1019\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76404/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76404/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SII_TCCBI\" title=\"TCCBI\" shape=\"rect\" coords=\"1115,1030,1340,1144\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76403/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76403/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 06 --> " +
                "<area id=\"OUM\" title=\"OU Management\" shape=\"rect\" coords=\"1360,243,1594,353\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"OUM_DIR\" title=\"OU Directorate\" shape=\"rect\" coords=\"1392,424,1562,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"OUM_OPS\" title=\"Operations Management\" shape=\"rect\" coords=\"1392,539,1561,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<!-- Column 07 --> " +
                "<area id=\"SSV\" title=\"Shared Services\" shape=\"rect\" coords=\"1624,243,1858,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76385/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76385/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SSV_COMMS\" title=\"SS - Communications\" shape=\"rect\" coords=\"1647,424,1836,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77891/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77891/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SSV_FIN\" title=\"SS - Finances\" shape=\"rect\" coords=\"1647,539,1836,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77496/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77496/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SSV_HR\" title=\"SS - Human Resources\" shape=\"rect\" coords=\"1647,674,1836,768\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77730/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77730/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SSV_LS\" title=\"Legal Services\" shape=\"rect\" coords=\"1647,786,1836,880\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77428/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77428/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "<area id=\"SSV_MS\" title=\"Managed Services\" shape=\"rect\" coords=\"1647,902,1836,996\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76389/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76389/entityType/workspace/action/view_permalink/novl_url/1\"> " +
                "</map></p>";

        String actual = sanitizer.check(input);
        String expect = "<p>" +
                "<img style=\"width:1900px;z-index:-10\" src=\"http://localhost:8080/ssf/s/readFile/workspace/213/09c1c01958d597eb0158d59a326e0012/1481052402000/last/IU%20strructure_new%207.jpg\" alt=\"IU-org-chart\" usemap=\"#IU-org-chart\" data-mce-src=\"https://workspace.csir.co.za/ssf/s/readFile/workspace/76374/-/1480017327269/last/IU strructure_new 7.jpg\" data-mce-style=\"width: 1900px; z-index: -10;\" /> " +
                "<map id=\"IU-org-chart\" name=\"IU-org-chart\">  " +
                "<area id=\"IU\" title=\"Implementation Unit\" coords=\"815,38,1093,147\" href=\"https://workspace.csir.co.za/ssf/a/do?p_name&#61;ss_forum&amp;p_action&#61;1&amp;binderId&#61;2333&amp;entityType&#61;workspace&amp;action&#61;view_permalink&amp;novl_url&#61;1#1449038173627\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/do?p_name&#61;ss_forum&amp;p_action&#61;1&amp;binderId&#61;2333&amp;entityType&#61;workspace&amp;action&#61;view_permalink&amp;novl_url&#61;1#1449038173627\" />  " +
                "<area id=\"Init\" title=\"CSIR Initiatives\" coords=\"41,243,274,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76379/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76379/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Init_MI\" title=\"Mining Initiatives\" coords=\"49,377,266,473\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76380/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76380/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Init_MI_ADL\" title=\"Air and Dust Labs\" coords=\"49,506,266,601\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76381/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76381/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Init_MI_CL\" title=\"Cottesloe\" coords=\"50,618,266,739\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76382/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76382/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Init_MI_HF\" title=\"Human Factors\" coords=\"50,757,266,850\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76383/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76383/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Init_MI_KB\" title=\"Kloppersbos\" coords=\"50,868,266,963\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"Labs\" title=\"Laboratories\" coords=\"298,243,532,353\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Labs_FB\" title=\"F&amp;B Laboratories\" coords=\"312,424,518,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76384/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Labs_EL\" title=\"Environmental Labs\" coords=\"312,539,518,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76406/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76406/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Labs_EL_PTA\" title=\"Environmental Labs - Pretoria Region\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76407/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76407/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Labs_EL_SB\" title=\"Environmental Labs - Stellenbosh\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76408/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76408/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"Labs_EL_DBN\" title=\"Environmental Labs - Durban Region\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76409/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76409/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"ECD\" title=\"Enterprise Creation for Development\" coords=\"567,243,801,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76375/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76375/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"ED\" title=\"Enterprise Development\" coords=\"0,0,0,0\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76376/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76376/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"ED_Pretoria\" title=\"Enterprise Development - Pretoria Region\" coords=\"609,424,759,519\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76378/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76378/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"ED_Coastal\" title=\"Enterprise Development - Coastal Region\" coords=\"609,539,759,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76377/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76377/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"SS\" title=\"Specialist Services\" coords=\"836,243,1070,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76391/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76391/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SS_EMS\" title=\"Environmental Management Services\" coords=\"848,425,1057,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76392/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76392/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SS_JIOC\" title=\"Joint Interim Operations Centre (Project)\" coords=\"849,537,1057,632\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76393/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76393/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SS_MSPS\" title=\"Municipal Strategic Programme Support\" coords=\"850,654,1055,769\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76394/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76394/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SS_SIM\" title=\"Strategic Infrastructure Management\" coords=\"849,786,1057,880\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76395/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76395/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"SII\" title=\"Strategic Initiatives Implementation\" coords=\"1090,243,1324,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76397/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76397/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_AISI\" title=\"Aviation Initiative Strategic Implementation\" coords=\"1115,424,1340,519\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76398/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76398/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_COMP\" title=\"Composites\" coords=\"1115,528,1339,582\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76399/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76399/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_NCPC\" title=\"National Cleaner Production Centre\" coords=\"1115,593,1340,687\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76400/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76400/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_NFTN\" title=\"National Foundry Technology Network\" coords=\"1115,697,1340,792\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76401/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76401/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_TLIU\" title=\"Technology Localisation Implementation Unit\" coords=\"1115,802,1340,923\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76402/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76402/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_WASTE\" title=\"Waste\" coords=\"1115,933,1340,1019\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76404/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76404/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SII_TCCBI\" title=\"TCCBI\" coords=\"1115,1030,1340,1144\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76403/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76403/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"OUM\" title=\"OU Management\" coords=\"1360,243,1594,353\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"OUM_DIR\" title=\"OU Directorate\" coords=\"1392,424,1562,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"OUM_OPS\" title=\"Operations Management\" coords=\"1392,539,1561,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77569/entityType/workspace/action/view_permalink/novl_url/1\" />  " +
                "<area id=\"SSV\" title=\"Shared Services\" coords=\"1624,243,1858,354\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76385/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76385/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SSV_COMMS\" title=\"SS - Communications\" coords=\"1647,424,1836,518\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77891/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77891/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SSV_FIN\" title=\"SS - Finances\" coords=\"1647,539,1836,633\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77496/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77496/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SSV_HR\" title=\"SS - Human Resources\" coords=\"1647,674,1836,768\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77730/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77730/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SSV_LS\" title=\"Legal Services\" coords=\"1647,786,1836,880\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77428/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/77428/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "<area id=\"SSV_MS\" title=\"Managed Services\" coords=\"1647,902,1836,996\" href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76389/entityType/workspace/action/view_permalink/novl_url/1\" data-mce-href=\"https://workspace.csir.co.za/ssf/a/c/p_name/ss_forum/p_action/1/binderId/76389/entityType/workspace/action/view_permalink/novl_url/1\" /> " +
                "</map></p>";
        Assert.assertEquals(expect, actual);
    }
}
