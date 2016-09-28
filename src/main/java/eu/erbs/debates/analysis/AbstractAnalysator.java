package eu.erbs.debates.analysis;

import java.util.List;

import eu.erbs.debates.model.TalkEvent;

public abstract class AbstractAnalysator implements DebateAnalysator {

	private static String NEWLINE = System.getProperty("line.separator"); 

	protected String joinTalkEvents(List<TalkEvent> talkEvents) {
		StringBuffer buffer = new StringBuffer();
		for(TalkEvent talkEvent : talkEvents){
			buffer.append(talkEvent.getText());
			buffer.append(NEWLINE);
		}
		return buffer.toString();
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}

}
