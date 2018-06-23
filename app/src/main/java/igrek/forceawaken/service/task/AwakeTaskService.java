package igrek.forceawaken.service.task;

import android.app.Activity;

import java.util.LinkedList;
import java.util.Random;

import igrek.forceawaken.domain.task.AnswerAgainTask;
import igrek.forceawaken.domain.task.AwakeTask;
import igrek.forceawaken.domain.task.LuckyTask;
import igrek.forceawaken.domain.task.MathTask;

public class AwakeTaskService {
	
	private Activity activity;
	private LinkedList<AwakeTask> registeredTasks = new LinkedList<>();
	private Random random = new Random();
	
	public AwakeTaskService(Activity activity) {
		this.activity = activity;
		enableTasks();
	}
	
	private void enableTasks() {
		registeredTasks.add(new LuckyTask());
		registeredTasks.add(new MathTask());
		registeredTasks.add(new AnswerAgainTask());
	}
	
	private double sumTasksProbability() {
		double sum = 0;
		for (AwakeTask registeredTask : registeredTasks) {
			sum += registeredTask.getProbabilityWeight();
		}
		return sum;
	}
	
	public AwakeTask getRandomTask() {
		// not uniform random
		double sumP = sumTasksProbability();
		double offset = random.nextDouble() * sumP;
		double sum = 0;
		for (AwakeTask registeredTask : registeredTasks) {
			sum += registeredTask.getProbabilityWeight();
			if (sum > offset)
				return registeredTask.getInstance();
		}
		return registeredTasks.getLast();
	}
	
}
