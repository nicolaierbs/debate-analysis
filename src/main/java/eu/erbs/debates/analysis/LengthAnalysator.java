package eu.erbs.debates.analysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

import eu.erbs.debates.model.TalkEvent;

public class LengthAnalysator extends AbstractAnalysator {

	@Override
	public  Map<String, Double> analyse(List<TalkEvent> talkEvents) {
		DescriptiveStatistics statistics = new DescriptiveStatistics();
		for(TalkEvent talkEvent : talkEvents){
			statistics.addValue(talkEvent.getText().length());
		}

		Map<String, Double> results = new HashMap<>();

		results.put("Number", (double) statistics.getN());
		results.put("Mean", (double) statistics.getMean());
		results.put("Max", (double) statistics.getMax());
		results.put("Sum", (double) statistics.getSum());
		results.put("StDev", (double) statistics.getStandardDeviation());

		return results;
	}

}
