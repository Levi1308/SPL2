package bgu.spl.mics;

import bgu.spl.mics.Event;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;


/**
 * The {@link MessageBusImpl class is the implementation of the MessageBus interface.
 * Write your implementation here!
 * Only private fields and methods can be added to this class.
 */
public class MessageBusImpl implements MessageBus {
	private  Map<Class<? extends Event>, List<MicroService>> eventSubscribers;
	private  Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers;
	private Map<MicroService, Queue<Message>> queues;
	private  Map<Event, Future> eventFutures;
	private static MessageBusImpl instance = null;

	private MessageBusImpl() {
		this.eventSubscribers = new HashMap<>();
		this.broadcastSubscribers = new HashMap<>();
		this.queues = new HashMap<>();
		this.eventFutures = new HashMap<>();
	}

	public static synchronized MessageBusImpl getInstance() {
		if (instance == null) {
			instance = new MessageBusImpl();
		}
		return instance;
	}

	@Override
	public synchronized <T> void subscribeEvent(Class<? extends Event<T>> type, MicroService m) {
		eventSubscribers.putIfAbsent(type, new ArrayList<>());
		eventSubscribers.get(type).add(m);
	}

	@Override
	public synchronized void subscribeBroadcast(Class<? extends Broadcast> type, MicroService m) {
		broadcastSubscribers.putIfAbsent(type, new ArrayList<>());
        broadcastSubscribers.get(type).add(m);
	}

	@Override
	public synchronized <T> void complete(Event<T> e, T result) {
		Future<T> future = eventFutures.remove(e);
		if (future != null) {
			future.resolve(result);
		}

	}

	@Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (broadcastSubscribers.containsKey(b.getClass())) {
			for (MicroService m : broadcastSubscribers.get(b.getClass())) {
				queues.get(m).add(b);
			}
		}

	}

	
	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if (eventSubscribers.containsKey(e.getClass())) {
			List<MicroService> subscribers = eventSubscribers.get(e.getClass());
			MicroService m = subscribers.remove(0);
			subscribers.add(m);
			queues.get(m).add(e);

			Future<T> future = new Future<>();
			eventFutures.put(e, future);
			return future;
		}
		return null;
	}

	@Override
	public synchronized void register(MicroService m) {
		queues.putIfAbsent(m, new LinkedList<>());

	}

	@Override
	public synchronized void unregister(MicroService m) {
		queues.remove(m);
		eventSubscribers.values().forEach(list -> list.remove(m));
		broadcastSubscribers.values().forEach(list -> list.remove(m));

	}

	@Override
	public Message awaitMessage(MicroService m) throws InterruptedException {
		synchronized (this){
		while (queues.get(m) == null) {
			this.wait();
		}
		return queues.get(m).poll();
	}
    }
}
