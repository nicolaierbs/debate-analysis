package eu.erbs.debates.analysis;

import java.util.List;
import java.util.Map;

import org.apache.uima.UIMAException;

import eu.erbs.debates.model.TalkEvent;
import eu.erbs.debates.utils.DKProUtils;

public class DiversityAnalysator extends AbstractAnalysator {
	
	@Override
	public  Map<String, Double> analyse(List<TalkEvent> talkEvents) throws UIMAException {
		
		return DKProUtils.getLengthStatistics(joinTalkEvents(talkEvents));
	}

}