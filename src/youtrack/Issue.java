package youtrack;
import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import youtrack.commands.*;
import youtrack.exceptions.CommandExecutionException;
import youtrack.exceptions.NoSuchIssueFieldException;
import youtrack.exceptions.SetIssueFieldException;
import youtrack.issue.fields.BaseIssueField;
import youtrack.issue.fields.SingleField;
import youtrack.issue.fields.values.BaseIssueFieldValue;
import youtrack.issue.fields.values.IssueFieldValue;
import youtrack.issue.fields.values.MultiUserFieldValue;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * Created by Egor.Malyshev on 19.12.13.
 * Provides access to a single issue and its fields.
 */
@XmlRootElement(name = "issue")
@XmlAccessorType(XmlAccessType.FIELD)
public class Issue extends BaseItem<YouTrack> {
    @XmlTransient
    public final CommandBasedList<Issue, IssueComment> comments;
    @XmlTransient
    public final CommandBasedList<Issue, IssueAttachment> attachments;
    @XmlTransient
    public final CommandBasedList<Issue, IssueLink> links;
    @XmlTransient
    public final CommandBasedList<Issue, IssueTag> tags;
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @XmlElement(name = "field")
    private List<BaseIssueField> fieldArray;
    @XmlTransient
    private HashMap<String, BaseIssueField> fields;
    @XmlTransient
    private boolean wikify;
    Issue() {
    	
        final Issue thiz = this;
        comments = new CommandBasedList<Issue, IssueComment>(this, new AddComment(this), new RemoveComment(this), new GetIssueComments(this), null, null);
        attachments = new CommandBasedList<Issue, IssueAttachment>(this, new AddAttachment(this),
                new RemoveAttachment(this), new GetIssueAttachments(this), null, null);
        links = new CommandBasedList<Issue, IssueLink>(this, new AddIssueLink(this), new RemoveIssueLink(this), new GetIssueLinks(this), null, null);
        tags = new CommandBasedList<Issue, IssueTag>(this, new AddIssueTag(this), new RemoveIssueTag(this), new GetIssueTags(this), null, null);
    }
    private Issue(Project project, String summary, String description) {
        wrapper = true;
        fieldArray = new ArrayList<BaseIssueField>();
        fieldArray.add(SingleField.createField("project", IssueFieldValue.createValue(project.getId())));
        fieldArray.add(SingleField.createField("summary", IssueFieldValue.createValue(summary)));
        fieldArray.add(SingleField.createField("description", IssueFieldValue.createValue(description)));
        this.afterUnmarshal(null, null);
        tags = null;
        links = null;
        comments = null;
        attachments = null;
    }
    private Issue(Map<String, BaseIssueField> fields) {
        wrapper = true;
        tags = null;
        links = null;
        comments = null;
        attachments = null;
        this.fields = new HashMap<String, BaseIssueField>();
        this.fields.putAll(fields);
    }
    public static Issue createIssue(Project project, String summary, String description) {
        return new Issue(project, summary, description);
    }
    public HashMap<String, BaseIssueField> getFields() {
        return new HashMap<String, BaseIssueField>(fields);
    }
    public Issue createSnapshot() {
        return new Issue(fields);
    }
    void setFieldByName(@NotNull String fieldName, @Nullable String value) throws SetIssueFieldException, IOException, NoSuchIssueFieldException, CommandExecutionException {
        if(fields.containsKey(fieldName)) {
            final ModifyIssueField modifyCommand = new ModifyIssueField(this);
            modifyCommand.addParameter("field", fieldName);
            modifyCommand.addParameter("value", value);
            final CommandResultData<String> result = youTrack.execute(modifyCommand);
            if(!result.success()) {
                throw new SetIssueFieldException(this, fields.get(fieldName), value);
            }
            updateSelf();
        } else throw new NoSuchIssueFieldException(this, fieldName);
    }
    @SuppressWarnings("unchecked")
    @Nullable
    <V extends BaseIssueFieldValue> V getFieldByName(@NotNull String fieldName) throws IOException, CommandExecutionException {
        if(fields.containsKey(fieldName)) {
            if(!wrapper) updateSelf();
            return (V) fields.get(fieldName).getValue();
        } else return null;
    }
    @SuppressWarnings("UnusedParameters")
    void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        fields = new HashMap<String, BaseIssueField>();
        for(BaseIssueField issueField : fieldArray) {
            fields.put(issueField.getName(), issueField);
        }
    }
    @Override
    public String toString() {
        return "Issue{" +
                "id='" + getId() + '\'' +
                '}';
    }
    public boolean isResolved() {
        return fields.containsKey("resolved");
    }
    public String getState() throws IOException, CommandExecutionException {
        final BaseIssueFieldValue state = getFieldByName("State");
        return state == null ? null : state.getValue();
    }
    public void setState(String state) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
        setFieldByName("State", state);
    }
    public boolean isWikify() {
        return wikify;
    }
    public void setWikify(boolean wikify) {
        this.wikify = wikify;
    }
    public String getDescription() throws IOException, CommandExecutionException {
        final BaseIssueFieldValue description = getFieldByName("description");
        return description == null ? null : description.getValue();
    }
    public void setDescription(String description) throws IOException, SetIssueFieldException, CommandExecutionException {
        final ModifyIssue command = new ModifyIssue(this);
        command.addParameter("description", description);
        final CommandResultData<String> result = youTrack.execute(command);
        if(result.success()) {
            updateSelf();
        } else throw new SetIssueFieldException(this, fields.get("summary"), description);
    }
    public String getSummary() throws IOException, CommandExecutionException {
        BaseIssueFieldValue summary = getFieldByName("summary");
        return summary == null ? null : summary.getValue();
    }
    public void setSummary(String summary) throws IOException, SetIssueFieldException, CommandExecutionException {
        final ModifyIssue command = new ModifyIssue(this);
        command.addParameter("summary", summary);
        final CommandResultData<String> result = youTrack.execute(command);
        if(result.success()) {
            updateSelf();
        } else throw new SetIssueFieldException(this, fields.get("summary"), summary);
    }
    public int getVotes() {
        try {
            return Integer.parseInt(getFieldByName("votes").getValue());
        } catch(Exception e) {
            return 0;
        }
    }
    public String getType() throws IOException, CommandExecutionException {
        BaseIssueFieldValue type = getFieldByName("Type");
        return type == null ? null : type.getValue();
    }
    public void setType(String type) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
        setFieldByName("Type", type);
    }
    public String getPriority() throws IOException, CommandExecutionException {
        BaseIssueFieldValue priority = getFieldByName("Priority");
        return priority == null ? null : priority.getValue();
    }
    public void setPriority(String priority) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
        setFieldByName("Priority", priority);
    }
    public MultiUserFieldValue getAssignee() throws IOException, CommandExecutionException {
        return (MultiUserFieldValue) getFieldByName("Assignee");
    }
    public void setAssignee(String assignee) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
    	String fieldName = "Assignee";
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, assignee);
        }
        setFieldByName(fieldName, assignee);
    }
    public String getReporter() throws IOException, CommandExecutionException {
        BaseIssueFieldValue reporterName = getFieldByName("reporterName");
        return reporterName == null ? null : reporterName.getValue();
    }
    public void vote() throws CommandExecutionException {
        youTrack.execute(new ChangeIssueVotes(this, true));
    }
    public void unVote() throws CommandExecutionException {
        youTrack.execute(new ChangeIssueVotes(this, false));
    }
    private void updateSelf() throws CommandExecutionException {
        final GetIssue command = new GetIssue(owner);
        if(wikify) command.addParameter("wikifyDescription", String.valueOf(true));
        command.setItemId(getId());
        final Issue issue = youTrack.execute(command).getResult();
        if(issue != null) {
            this.fields.clear();
            this.fields.putAll(issue.fields);
        }
    }
    public String getProjectId() throws IOException, CommandExecutionException {
    	if(getId()==null || getId().isEmpty()){
    		return getFieldByName("project").getValue();
    	}
        return getId().substring(0, getId().indexOf("-"));
    }
    
    public String getFixVersions() throws IOException, CommandExecutionException {
        BaseIssueFieldValue version = getFieldByName("Fix versions");
        return version == null ? null : version.getValue();
    }
    public void setFixVersions(String version) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
    	String fieldName = "Fix versions";
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, version);
        }
    	setFieldByName(fieldName, version);
    }
    public String getAffectedVersions() throws IOException, CommandExecutionException {
        BaseIssueFieldValue version = getFieldByName("Affected versions");
        return version == null ? null : version.getValue();
    }
    public void setAffectedVersions(String version) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
    	String fieldName = "Affected versions";
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, version);
        }
    	setFieldByName(fieldName, version);
    }
    /**
     * Estimation en heures indiqu�es dans Youtrack
     * @return estimation en nombre d'heures
     * @throws IOException
     * @throws CommandExecutionException
     */
    public Integer getEstimation() throws IOException, CommandExecutionException {
        BaseIssueFieldValue estimation= getFieldByName("Estimation");
        if(estimation == null || estimation.getValue().isEmpty()) return null;
        return Integer.valueOf(estimation.getValue())/60;
    }
    
    public void setEstimation(int heures) throws IOException, SetIssueFieldException, NoSuchIssueFieldException, CommandExecutionException {
    	String fieldName = "Estimation";
    	String estimation = String.valueOf(heures);
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, estimation);
        }
    	setFieldByName(fieldName, estimation);
    }
	private BaseIssueField createField(String fieldName, String value) {
		return fields.put(fieldName, SingleField.createField(fieldName, IssueFieldValue.createValue(value)));
	}
	public void setPcmNumber(String number) throws SetIssueFieldException, IOException, NoSuchIssueFieldException, CommandExecutionException {
		String fieldName = "PCM";
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, number);
        }
    	setFieldByName(fieldName, number);
	}
	public String getPcmNumber() throws IOException, CommandExecutionException {
		BaseIssueFieldValue number = getFieldByName("PCM");
        return number == null ? null : number.getValue();
	}
	public void setPcmDeliver(boolean deliver) throws SetIssueFieldException, IOException, NoSuchIssueFieldException, CommandExecutionException {
		String fieldName = "PcmDeliver";
		String value = deliver?"Oui":"Non";
		if(!fields.containsKey(fieldName )) {
        	createField(fieldName, value);
        }
    	setFieldByName(fieldName, value);
	}
	public Boolean getPcmDeliver() throws IOException, CommandExecutionException {
		BaseIssueFieldValue value = getFieldByName("PcmDeliver");
        return value == null ? false : value.getValue().equals("Oui");
	}
}