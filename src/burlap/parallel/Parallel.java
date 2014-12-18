package burlap.parallel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class Parallel {
	private static final int numCores = Runtime.getRuntime().availableProcessors();
	private ExecutorService executor;
	public Parallel() {
		this.executor = Executors.newFixedThreadPool(numCores);
	}
	
	public static <T> List<T> For(int start, int end, int increment, ForCallable<T> runnable) {
		Parallel instance = new Parallel();
		List<T> result = instance.parallelFor(start, end, increment, runnable);
		instance.shutdown();
		return result;
	}
	
	public <T> List<T> parallelFor(int start, int end, int increment, ForCallable<T> runnable) {
		double width = end - start;
		int size = (int)(width / increment + 1); 
		List<Callable<T>> callables = new ArrayList<Callable<T>>(size);
		
		for (int i = start; i < end; i += increment) {
			ForCallable<T> copied = runnable.init(start, i, end, increment);
			callables.add(copied);
		}
		List<T> result = new ArrayList<T>(size);
		try {
			List<Future<T>> futures = executor.invokeAll(callables);
			for (Future<T> future : futures) {
				result.add(future.get());
			}
		} catch (InterruptedException | ExecutionException e) {}
		return result;
	}
	
	public static <I, R> List<R> ForEach(Collection<I> collection, ForEachCallable<I, R> runnable) {
		Parallel instance = new Parallel();
		List<R> result = instance.parallelForEach(collection, runnable);
		instance.shutdown();
		return result;
	}
	
	public <I, R> List<R> parallelForEach(Collection<I> collection, ForEachCallable<I, R> runnable) {
		
		List<Callable<R>> callables = new ArrayList<Callable<R>>(collection.size());
		
		for (I item : collection) {
			ForEachCallable<I,R> copied = runnable.init(item);
			callables.add(copied);
		}
		
		List<R> result = new ArrayList<R>(collection.size());
		try {
			List<Future<R>> futures = executor.invokeAll(callables);
			for (Future<R> future : futures) {
				result.add(future.get());
			}
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void shutdown(ExecutorService executor) {
		executor.shutdown();
		try {
			if (!executor.awaitTermination(1L, TimeUnit.SECONDS)) {
				executor.shutdownNow();
			}
		} catch (InterruptedException e) {
			executor.shutdownNow();
		}
	}
	
	public void shutdown() {
		Parallel.shutdown(this.executor);
	}
	
	public static abstract class ForCallable<T> implements Callable<T>{
		public abstract ForCallable<T> init(int start, int current, int end, int increment);
	}
	
	public static abstract class ForEachCallable<I, R> implements Callable<R>{
		public abstract ForEachCallable<I, R> init(I current);
	}
	
	public static void main(String[] args) {
		int numTrials = 1000;
		int loopLength = 1000;
		
		double start = System.nanoTime();
		for (int i = 0; i < numTrials; i++) {
			List<Integer> result = Parallel.For(0, loopLength, 1, (ForCallable<Integer>)(new V()));
		}
		double end = System.nanoTime();
		
		System.out.println("parallel for: " + (end - start)/1000000000);
		
		start = System.nanoTime();
		
		for (int i = 0; i < numTrials; i++) {
			List<Integer> result = new ArrayList<Integer>(loopLength);
			for (int j = 0; j < loopLength; j++) {
				result.add(V.staticCall(j));
			}
		}
		
		end = System.nanoTime();
		System.out.println("for: " + (end - start)/1000000000);
		
		List<Integer> items = new ArrayList<Integer>(loopLength);
		for (int i =0; i < loopLength; i++) {
			items.add(i);
		}
		
		start = System.nanoTime();
		for (int i = 0; i < numTrials; i++) {
			List<Integer> result = Parallel.ForEach(items, (ForEachCallable<Integer, Integer>)(new W()));
		}
		end = System.nanoTime();
		System.out.println("parallel forEach: " + (end - start)/1000000000);
		
		start = System.nanoTime();
		for (int i = 0; i < numTrials; i++) {
			List<Integer> result = new ArrayList<Integer>(items.size());
			for(Integer j : items) {
				 result.add(V.staticCall(j));
			}
		}
		end = System.nanoTime();
		System.out.println("forEach: " + (end - start)/1000000000);
	}
	
	public static class V extends ForCallable<Integer> {
		private int value;
		public V() {}
		public V(int value) {
			this.value = value;
		}
		@Override
		public Integer call() throws Exception {
			return staticCall(value);
		}
		
		public static Integer staticCall(Integer i) {
			int sum = 0;
			for (int j = 0; j < 10000; j++){
				sum += i + j;
			}
			return sum;
		}

		@Override
		public V init(int start, int current, int end, int increment) {
			return new V(current);
		}
		
	};
	
	public static class W extends ForEachCallable<Integer, Integer> {
		private int value;
		public W() {}
		public W(int value) {
			this.value = value;
		}
		@Override
		public Integer call() throws Exception {
			return V.staticCall(value);
		}

		@Override
		public W init(Integer item) {
			return new W(item);
		}
		
	};
}
