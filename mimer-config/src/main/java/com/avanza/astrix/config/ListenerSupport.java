/*
 * Copyright 2020 Avanza Bank AB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avanza.astrix.config;

import static java.util.Objects.requireNonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
final class ListenerSupport<T> {
	
	private static final Logger log = LoggerFactory.getLogger(ListenerSupport.class);
	
	private final Queue<SubscribedListener> listeners = new ConcurrentLinkedQueue<>();
	
	void addListener(T l) {
		listeners.add(new SubscribedListener(l));
	}
	
	void dispatchEvent(Consumer<T> eventNotification) {
		for (SubscribedListener subscribedListener : listeners) {
			try {
				eventNotification.accept(subscribedListener.listener);
			} catch (Exception e) {
				log.warn("Error when notifying listener {}", subscribedListener, e);
			}
		}
	}
	
	void removeListener(T l) {
		listeners.remove(new SubscribedListener(l));
	}
	
	private class SubscribedListener {
		private final T listener;

		public SubscribedListener(T l) {
			this.listener = requireNonNull(l);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ListenerSupport.SubscribedListener)) {
				return false;
			}
			return listener == SubscribedListener.class.cast(obj).listener;
		}
		
		@Override
		public int hashCode() {
			return System.identityHashCode(listener);
		}
		
		@Override
		public String toString() {
			return "DynamicConfigListener(" + this.listener.toString() + ")";
		}
	}

}
