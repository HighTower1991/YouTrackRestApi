package youtrack.commands;
import com.sun.istack.internal.NotNull;
import org.apache.commons.httpclient.methods.DeleteMethod;

import youtrack.Error;
import youtrack.Issue;
import youtrack.YouTrack;
import youtrack.commands.base.RemoveCommand;
import youtrack.exceptions.CommandExecutionException;
import youtrack.exceptions.NoSuchIssueFieldException;

import java.io.IOException;
/**
 * Created by egor.malyshev on 08.04.2014.
 */
public class RemoveIssue extends RemoveCommand<YouTrack, Issue> {
    public RemoveIssue(@NotNull YouTrack owner) {
        super(owner);
    }
    
    @Override
    public Issue getResult() throws CommandExecutionException {
    	if(method.getStatusCode()!=200){
    		String responseBodyAsString;
			try {
				responseBodyAsString = method.getResponseBodyAsString();
				final Object receivedData = objectFromXml(responseBodyAsString);
				final Error e = new Error();
                e.setMessage("\n"+method.getStatusText() + ".\n" +responseBodyAsString);
                e.setCode(method.getStatusCode());
                throw new CommandExecutionException(this, e);
			} catch (IOException e) {
				throw new CommandExecutionException(this, e);
			} catch (Exception e) {
				throw new CommandExecutionException(this, e);
			}
			
    	}
    	return null;
    };
    @Override
    public void createCommandMethod() throws IOException, NoSuchIssueFieldException {
        method = new DeleteMethod(owner.getYouTrack().getHostAddress() + "issue/" + item.getId());
    }
}
