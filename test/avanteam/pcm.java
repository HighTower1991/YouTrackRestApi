package avanteam;

import java.io.IOException;
import java.util.List;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import youtrack.BaseItem;
import youtrack.Hotfix;
import youtrack.Issue;
import youtrack.IssueTag;
import youtrack.Project;
import youtrack.State;
import youtrack.YouTrack;
import youtrack.exceptions.CommandExecutionException;

public class pcm{
	String url ="http://youtrack.corp.avanteam.fr:8020/rest/";
	YouTrack youTrack;
	
	@Before
	public void setUp() throws Exception {
		
		youTrack = YouTrack.getInstance(url);
		youTrack.login("apiuser", "@vanteam78");
		
	}
	
	@Test
	public void ShowProject(){
		try {
			List<Project> projects = youTrack.projects.list();
			assertTrue("Au moins un projet", projects.size()>0);
			Project project = youTrack.projects.item("APS");
			assertNotNull("APS trouvé", project);
			System.out.println("Project id = " + project.getId() );
		} catch (CommandExecutionException e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void ShowASP404() throws IOException{
		try {
			List<Issue> issues = youTrack.issues.query("#APS-404");
			assertFalse("ASP found", issues ==null);
			assertEquals("Only one ASP found", issues.size(),1);
			Issue issue = issues.get(0);
			assertEquals("Issue project id = APS", issue.getProjectId(), "APS");
			assertEquals("Issue id = APS-404", issue.getId(), "APS-404");
		} catch (CommandExecutionException e) {
			fail(e.getMessage());
		}
	}
	@Test
	public void AddIssue() throws Exception{
		Project project = youTrack.projects.item("APS");
		Issue issue = Issue.createIssue(project, "TEST CREATE ISSUE FROM DOMINO", "Test description");
		assertEquals("Project id on new issue is APS", issue.getProjectId(), "APS");
		issue = youTrack.issues.add(issue).getResult();
		assertEquals("Issue project id = APS",issue.getProjectId(), "APS");
		
		String issueId = issue.getId();
		System.out.println("Issue created : " + issueId);
		assertNotNull("Issue id non null", issueId);
		assertTrue("Issue id non vide = " + issueId, !issueId.isEmpty());

		issue.setState(State.Open);
		assertEquals("state open", issue.getState(), State.Open);
		issue.setFixVersions("2016");
		assertEquals("Fix version 2016", issue.getFixVersions(), "2016");
		
		issue.setAffectedVersions("2014 SP3 2014 SP2");
		assertEquals("Fix version 2014 SP3", issue.getAffectedVersions(), "2014 SP3");
		
		issue.setAssignee("pchaumeil");
		assertEquals("Assignee pchaumeil", issue.getAssignee().getValue(), "pchaumeil");
		assertEquals("Assignee Patrick CHAUMEIL", issue.getAssignee().getFullName(), "Patrick CHAUMEIL");

		issue.setEstimation(60);
		assertEquals("Estimation 60h", issue.getEstimation(), 60);
		
		String idTag = "pour permanence";
		IssueTag item = IssueTag.createTag(idTag);
		issue.tags.add(item );
		List<IssueTag> tags = issue.tags.list();
		int indexOf=-1;
		for(int i=0; i<tags.size(); i++){
			if(tags.get(i).getTag().equals(idTag)){
				indexOf = i;
				break;
			}
		}
		assertTrue("Tag pour permanence", indexOf>-1);
		
		issue.setPcmNumber("PCM-2020-0001");
		assertEquals("Numéro de PCM = PCM-2020-0001", issue.getPcmNumber(),"PCM-2020-0001");
		
		issue.setHotfix(Hotfix.Deliver);
		assertEquals("PCM à livrer = Oui", issue.getHotfix().name(), Hotfix.Deliver.name());
		
		System.out.println("Remove issue id = " + issue.getId());
		youTrack.issues.remove(issue);
		try {
			issue = youTrack.issues.item(issueId);						
		} catch (CommandExecutionException e) {
			assertEquals("Issue id = " + issueId + " removed", e.getError().getCode(), 404);
		}
		
	}
	
}
