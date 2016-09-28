package eu.erbs.debates.filter;

import java.util.ArrayList;
import java.util.List;

import eu.erbs.debates.model.TalkEvent;

public class SpeakerFilter implements Filter {
	
	private String speaker;
	
	public SpeakerFilter(String speaker){
		this.speaker = speaker;
	}

	@Override
	public List<TalkEvent> filter(List<TalkEvent> talkEvents) {
		List<TalkEvent> filteredTalkEvents = new ArrayList<>();
		
		for(TalkEvent talkEvent : talkEvents){
			if(talkEvent.getSpeaker().equals(speaker)){
				filteredTalkEvents.add(talkEvent);
			}
		}
		
		return filteredTalkEvents;
	}

}
