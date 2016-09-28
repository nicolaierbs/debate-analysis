package eu.erbs.debates.filter;

import java.util.List;

import eu.erbs.debates.model.TalkEvent;

public interface Filter {
	
	public List<TalkEvent> filter(List<TalkEvent> talkEvents);

}
