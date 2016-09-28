package eu.erbs.debates.analysis;

import java.util.List;
import java.util.Map;

import eu.erbs.debates.model.TalkEvent;

public interface DebateAnalysator {
	
	public Map<String, Double> analyse(List<TalkEvent> talkEvents) throws Exception;
	
	public String getName();

}
