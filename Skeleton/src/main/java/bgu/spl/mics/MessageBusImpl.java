package bgu.spl.mics;

import bgu.spl.mics.Event;
import bgu.spl.mics.Broadcast;
import bgu.spl.mics.MicroService;
import bgu.spl.mics.Future;
import bgu.spl.mics.Message;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.objects.LiDarDataBase;

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



    private class MessageBusImplHolder{
		private static MessageBusImpl instance = new MessageBusImpl();
	}
    private Map<Class<? extends Event>, List<MicroService>> eventSubscribers;
    private Map<Class<? extends Broadcast>, List<MicroService>> broadcastSubscribers;
    private Map<MicroService, Queue<Message>> queues;
    private Map<Event, Future> eventFutures;

    private MessageBusImpl() {
        this.eventSubscribers = new HashMap<>();
        this.broadcastSubscribers = new HashMap<>();
        this.queues = new HashMap<>();
        this.eventFutures = new HashMap<>();
    }

    public static synchronized MessageBusImpl getInstance() {
        return MessageBusImplHolder.instance;
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
    public <T> void complete(Event<T> e, T result) {
        Future<T> future = eventFutures.remove(e);
        if (future != null) {
            synchronized (future) {
                future.resolve(result);
            }
        }
    }


    @Override
	public synchronized void sendBroadcast(Broadcast b) {
		if (broadcastSubscribers.containsKey(b.getClass())) {
			for (MicroService m : broadcastSubscribers.get(b.getClass())) {
				Queue<Message> queue = queues.get(m);
				if (queue != null) {
					queue.add(b);
					this.notifyAll();
				}
			}
		}
	}



	@Override
	public synchronized <T> Future<T> sendEvent(Event<T> e) {
		if (eventSubscribers.containsKey(e.getClass())) {
			List<MicroService> subscribers = eventSubscribers.get(e.getClass());
			if (!subscribers.isEmpty()) {
				MicroService m = subscribers.remove(0);
				subscribers.add(m);
				Queue<Message> queue = queues.get(m);
				if (queue != null) {
					queue.add(e);
					this.notifyAll();
				}

				Future<T> future = new Future<>();
				eventFutures.put(e, future);
				return future;
			}
		}
		return null;
	}

	@Override
	public synchronized void register(MicroService m) {
		if (queues.putIfAbsent(m, new LinkedList<>()) != null) {
			System.err.println("Service already registered: " + m.getName());
		} else {
			System.out.println("Service registered successfully: " + m.getName());
		}
	}


	@Override
    public synchronized void unregister(MicroService m) {
        queues.remove(m);
        eventSubscribers.values().forEach(list -> list.remove(m));
        broadcastSubscribers.values().forEach(list -> list.remove(m));

    }

    @Override
    public Message awaitMessage(MicroService m) throws InterruptedException {
        synchronized (this) {
            while (queues.get(m).isEmpty()) {
                this.wait();
            }
            return queues.get(m).poll();
        }
    }

    public  Map<Class<? extends Event>, List<MicroService>> getEventSubscribers() {
        return eventSubscribers;
    }

    public Map<Event, Future> getEventFutures() {
        return eventFutures;
    }

    public Map<Class<? extends Broadcast>, List<MicroService>> getBroadcastSubscribers() {
        return broadcastSubscribers;
    }

    public Map<MicroService, Queue<Message>> getQueues() {
        return queues;
    }
}
