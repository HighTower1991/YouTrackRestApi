package youtrack.commands;
import com.sun.istack.internal.NotNull;

import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PutMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;

import youtrack.BaseItemList;
import youtrack.Error;
import youtrack.Issue;
import youtrack.YouTrack;
import youtrack.commands.base.AddCommand;
import youtrack.exceptions.AuthenticationErrorException;
import youtrack.exceptions.CommandExecutionException;
import youtrack.util.Service;
/**
 * Created by egor.malyshev on 01.04.2014.
 */
public class AddIssue extends AddCommand<YouTrack, Issue> {
    public AddIssue(@NotNull YouTrack owner) {
        super(owner);
    }
    @Override
    public Issue getResult() throws CommandExecutionException, AuthenticationErrorException{
    	try {
            if(method.getStatusCode() != 201) {
                String responseBodyAsString = method.getResponseBodyAsString();
				final Object receivedData = objectFromXml(responseBodyAsString);
                if(receivedData instanceof Issue){
                	return (Issue) receivedData;
                }
                final Error e = new Error();
                e.setMessage("\n"+method.getStatusText() + ".\n" +responseBodyAsString);
                e.setCode(method.getStatusCode());
                throw new CommandExecutionException(this, e);
            } else {
    			final String[] locations = method.getResponseHeader("Location").getValue().split("/");
    	        final String issueId = locations[locations.length - 1];
    	        return owner.issues.item(issueId);
            }
        } catch(CommandExecutionException e) {
            throw e;
        } catch(Exception e) {
            throw new CommandExecutionException(this, e);
        }
    }
    @Override
    public void createCommandMethod() throws Exception {
        PutMethod putMethod = new PutMethod(owner.getYouTrack().getHostAddress() + "issue");
        HttpMethodParams params = new HttpMethodParams();
        params.setParameter("project", item.getProjectId());
        params.setParameter("summary", getItem().getSummary());
        params.setParameter("description", getItem().getDescription());
        //putMethod.setParams(params);
        putMethod.setQueryString(new NameValuePair[]{
        		new NameValuePair("project", item.getProjectId()),
                new NameValuePair("summary", getItem().getSummary()),
                new NameValuePair("description", getItem().getDescription())
        });
        method = putMethod;
        //method.setParams(params);
    }
}