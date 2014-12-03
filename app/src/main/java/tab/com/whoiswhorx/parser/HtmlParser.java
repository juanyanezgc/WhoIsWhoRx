package tab.com.whoiswhorx.parser;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import tab.com.whoiswhorx.model.TeamMember;
import tab.com.whoiswhorx.utils.Debug;

/**
 * Parses html document
 */
public class HtmlParser {

    public static final String TEAM_MEMBERS_SECTION_KEY = "users";
    public static final String TEAM_MEMBER_KEY = "col";
    public static final String TEAM_MEMBER_IMAGE_URI_KEY = "src";

    /**
     * Parses a given html document
     *
     * @param htmlDocument Document to parse
     * @return List with all parsed team members
     */
    public static List<TeamMember> parseTeamMembers(Document htmlDocument) {
        Debug.logInfo("[Thread: " + Thread.currentThread().getName() + "] " +"Parsing team member html");

        List<TeamMember> teamMembers = new ArrayList<>();
        Element teamMembersSection = htmlDocument.body().getElementById(TEAM_MEMBERS_SECTION_KEY);
        Elements teamMembersHtml = teamMembersSection.getElementsByClass(TEAM_MEMBER_KEY);

        int id = 0;

        for (Element teamMemberHtml : teamMembersHtml) {

            Elements teamMemberElements = teamMemberHtml.getAllElements();

            String imageURI = teamMemberElements.get(2).attr(TEAM_MEMBER_IMAGE_URI_KEY);
            String name = teamMemberElements.get(3).text();
            String jobTitle = teamMemberElements.get(4).text();
            String biography = teamMemberElements.get(5).text();

            TeamMember teamMember = new TeamMember(id, name, jobTitle, biography, imageURI);
            teamMembers.add(teamMember);

            id++;
        }


        return teamMembers;
    }

}
